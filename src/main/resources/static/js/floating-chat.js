/**
 * FLOATING REAL-TIME EMERGENCY CHAT DOCK (FACEBOOK MESSENGER STYLE)
 * Hệ thống Hỗ Trợ Cứu Nạn 24/7
 * Tự động nổi trên mọi trang dành cho Người dân (Citizen) và Đội cứu hộ (Rescuer).
 * Hỗ trợ đồng thời N ca cứu nạn (N khung chat độc lập đặt cạnh nhau).
 */
(function () {
    'use strict';

    // Âm thanh thông báo tin nhắn (chime Web Audio API mềm mại)
    function playChimeSound() {
        try {
            const AudioContext = window.AudioContext || window.webkitAudioContext;
            if (!AudioContext) return;
            const ctx = new AudioContext();
            const now = ctx.currentTime;

            // Tone 1: E5 (659.25Hz) -> Tone 2: A5 (880Hz)
            const osc = ctx.createOscillator();
            const gain = ctx.createGain();

            osc.type = 'sine';
            osc.frequency.setValueAtTime(659.25, now);
            osc.frequency.exponentialRampToValueAtTime(880, now + 0.12);

            gain.gain.setValueAtTime(0.12, now);
            gain.gain.exponentialRampToValueAtTime(0.01, now + 0.28);

            osc.connect(gain);
            gain.connect(ctx.destination);

            osc.start(now);
            osc.stop(now + 0.3);
            osc.onended = () => ctx.close();
        } catch (_) {
            // Trình duyệt có thể chặn audio khi chưa có tương tác người dùng
        }
    }

    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    /**
     * Đại diện cho một Cửa sổ Chat của một Ca cứu nạn (SOS).
     */
    class FloatingChatWindow {
        constructor(session, manager) {
            this.session = session;
            this.sosId = Number(session.sosId);
            this.manager = manager;
            this.renderedMessageIds = new Set();
            this.unreadCount = 0;
            this.isOpen = false;
            this.stompSubscription = null;

            this.initDom();
            this.bindEvents();
            this.populateQuickChips();
            this.loadHistory(true);
        }

        initDom() {
            const template = this.manager.templateEl;
            if (!template) return;

            const clone = template.content.cloneNode(true);
            this.itemEl = clone.querySelector('.floating-chat-item');
            this.itemEl.setAttribute('data-sos-id', this.sosId);

            // Launcher elements
            this.launcherEl = this.itemEl.querySelector('.fc-launcher');
            this.launcherSosTagEl = this.itemEl.querySelector('.fc-bubble-sos-tag');
            this.unreadBadgeEl = this.itemEl.querySelector('.fc-unread-badge');
            this.launcherTitleEl = this.itemEl.querySelector('.fc-launcher-title');

            // Window elements
            this.windowEl = this.itemEl.querySelector('.fc-window');
            this.headerTitleArea = this.itemEl.querySelector('.fc-header-title-area');
            this.partnerInitialEl = this.itemEl.querySelector('.fc-partner-initial');
            this.partnerNameEl = this.itemEl.querySelector('.fc-partner-name');
            this.sosBadgeEl = this.itemEl.querySelector('.fc-sos-badge');
            this.partnerSubtitleEl = this.itemEl.querySelector('.fc-partner-subtitle');

            this.callBtnEl = this.itemEl.querySelector('.fc-call-btn');
            this.trackLinkEl = this.itemEl.querySelector('.fc-track-link');
            this.minimizeBtnEl = this.itemEl.querySelector('.fc-minimize-btn');
            this.closeBtnEl = this.itemEl.querySelector('.fc-close-btn');

            this.sosAddressEl = this.itemEl.querySelector('.fc-sos-address');
            this.statusPillEl = this.itemEl.querySelector('.fc-status-pill');
            this.messagesBodyEl = this.itemEl.querySelector('.fc-messages-body');
            this.loadingEl = this.itemEl.querySelector('.fc-loading');
            this.quickChipsEl = this.itemEl.querySelector('.fc-quick-chips');

            this.formEl = this.itemEl.querySelector('.fc-form');
            this.inputEl = this.itemEl.querySelector('.fc-input');
            this.sendBtnEl = this.itemEl.querySelector('.fc-send-btn');

            this.applySessionData();
        }

        applySessionData() {
            const s = this.session;
            const partnerName = s.partnerName || 'Đội Cứu Hộ';
            const partnerInit = partnerName.trim().length > 0 ? partnerName.trim().charAt(0).toUpperCase() : 'C';

            // Launcher
            if (this.launcherSosTagEl) this.launcherSosTagEl.textContent = `SOS-${this.sosId}`;
            if (this.launcherTitleEl) this.launcherTitleEl.textContent = `#SOS-${this.sosId}: ${partnerName}`;

            // Header
            if (this.partnerInitialEl) this.partnerInitialEl.textContent = partnerInit;
            if (this.partnerNameEl) this.partnerNameEl.textContent = partnerName;
            if (this.sosBadgeEl) this.sosBadgeEl.textContent = `SOS-${this.sosId}`;
            if (this.partnerSubtitleEl) this.partnerSubtitleEl.textContent = s.partnerSubtitle || `Ca cứu nạn #SOS-${this.sosId}`;

            // Address strip
            if (this.sosAddressEl) this.sosAddressEl.textContent = s.sosAddress || 'Vị trí hiện trường';
            if (this.statusPillEl) this.statusPillEl.textContent = s.statusText || 'Đang tiếp cận';

            // Phone call button
            if (this.callBtnEl) {
                if (s.partnerPhone && s.partnerPhone.length > 3) {
                    this.callBtnEl.href = `tel:${s.partnerPhone}`;
                    this.callBtnEl.style.display = 'inline-flex';
                } else {
                    this.callBtnEl.style.display = 'none';
                }
            }

            // Track link button
            if (this.trackLinkEl) {
                this.trackLinkEl.href = (s.userRole === 'RESCUER')
                    ? (s.assignmentId ? `/rescuer/routes?assignmentId=${s.assignmentId}` : '/rescuer/routes')
                    : `/sos/track/${this.sosId}`;
            }
        }

        updateSessionData(newSession) {
            this.session = newSession;
            this.applySessionData();
        }

        bindEvents() {
            if (this.launcherEl) {
                this.launcherEl.addEventListener('click', () => this.openWindow());
            }
            if (this.minimizeBtnEl) {
                this.minimizeBtnEl.addEventListener('click', (e) => {
                    e.stopPropagation();
                    this.minimizeWindow();
                });
            }
            if (this.closeBtnEl) {
                this.closeBtnEl.addEventListener('click', (e) => {
                    e.stopPropagation();
                    this.minimizeWindow();
                });
            }
            if (this.headerTitleArea) {
                this.headerTitleArea.addEventListener('click', () => {
                    if (this.isOpen) this.minimizeWindow();
                    else this.openWindow();
                });
            }
            if (this.formEl && this.inputEl) {
                this.formEl.addEventListener('submit', (e) => {
                    e.preventDefault();
                    const text = this.inputEl.value;
                    if (text && text.trim()) {
                        this.sendChatMessage(text, 'TEXT');
                        this.inputEl.value = '';
                        this.inputEl.focus();
                    }
                });
            }
        }

        openWindow() {
            if (!this.windowEl || !this.launcherEl) return;
            this.manager.onWindowOpening(this);

            this.isOpen = true;
            sessionStorage.setItem('floating_chat_state_' + this.sosId, 'open');

            this.launcherEl.style.display = 'none';
            this.windowEl.style.display = 'flex';

            // Xóa thông báo chưa đọc
            this.unreadCount = 0;
            sessionStorage.setItem('floating_chat_unread_' + this.sosId, '0');
            if (this.unreadBadgeEl) {
                this.unreadBadgeEl.textContent = '0';
                this.unreadBadgeEl.style.display = 'none';
            }

            setTimeout(() => {
                this.scrollToBottom();
                if (this.inputEl) this.inputEl.focus();
            }, 100);
        }

        minimizeWindow() {
            if (!this.windowEl || !this.launcherEl) return;
            this.isOpen = false;
            sessionStorage.setItem('floating_chat_state_' + this.sosId, 'minimized');

            this.windowEl.style.display = 'none';
            this.launcherEl.style.display = 'flex';
        }

        scrollToBottom() {
            if (this.messagesBodyEl) {
                this.messagesBodyEl.scrollTop = this.messagesBodyEl.scrollHeight;
            }
        }

        isMessageFromMe(msg) {
            if (!this.session || !msg) return false;
            const myRole = (this.session.mySenderType || this.session.userRole || '').toUpperCase();
            const msgSender = (msg.senderType || '').toUpperCase();

            // Nếu tôi là Đội Cứu Hộ:
            if (myRole === 'RESCUER') {
                return msgSender === 'RESCUER';
            }
            // Nếu tôi là Người Dân:
            if (myRole === 'CITIZEN') {
                return msgSender === 'CITIZEN';
            }
            // Khớp theo tên nếu có
            if (this.session.mySenderName && msg.senderName) {
                if (this.session.mySenderName.trim().toLowerCase() === msg.senderName.trim().toLowerCase()) {
                    return true;
                }
            }
            return msg.isMine === true;
        }

        appendMessage(msg, isInitial = false) {
            if (!msg || !this.messagesBodyEl) return;
            if (msg.id && this.renderedMessageIds.has(msg.id)) return;
            if (msg.id) this.renderedMessageIds.add(msg.id);

            // Xóa loading placeholder nếu có
            if (this.loadingEl && this.loadingEl.parentNode) {
                this.loadingEl.remove();
            }

            const row = document.createElement('div');
            const isMine = this.isMessageFromMe(msg);
            const isQuickAlert = msg.messageType === 'QUICK_ALERT';
            row.className = `chat-msg-row ${isMine ? 'outgoing' : 'incoming'} ${isQuickAlert ? 'quick-alert' : ''}`;

            let avatarHtml = '';
            if (!isMine) {
                const initial = (msg.senderName && msg.senderName.trim().length > 0)
                    ? msg.senderName.trim().charAt(0).toUpperCase()
                    : 'U';
                avatarHtml = `
                    <div class="rounded-circle bg-secondary text-white fw-bold d-flex align-items-center justify-content-center shadow-xs flex-shrink-0"
                         style="width: 28px; height: 28px; font-size: 0.75rem;">
                        ${initial}
                    </div>`;
            }

            const timeStr = msg.formattedTime || (msg.createdAt ? new Date(msg.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '');

            const bubbleHtml = `
                <div class="chat-bubble-content">
                    ${!isMine ? `<div class="fw-bold small mb-0.5 opacity-75" style="font-size: 0.72rem;">${escapeHtml(msg.senderName || 'Người gửi')}</div>` : ''}
                    <div class="message-text">${escapeHtml(msg.content)}</div>
                    <span class="chat-msg-time">${timeStr}</span>
                </div>`;

            if (isMine) {
                row.innerHTML = bubbleHtml;
            } else {
                row.innerHTML = avatarHtml + bubbleHtml;
            }

            this.messagesBodyEl.appendChild(row);
            this.scrollToBottom();

            // Nếu là tin nhắn từ đối phương thời gian thực
            if (!isMine && !isInitial) {
                if (!this.isOpen) {
                    this.unreadCount++;
                    sessionStorage.setItem('floating_chat_unread_' + this.sosId, this.unreadCount.toString());
                    if (this.unreadBadgeEl) {
                        this.unreadBadgeEl.textContent = this.unreadCount > 9 ? '9+' : this.unreadCount;
                        this.unreadBadgeEl.style.display = 'block';
                    }
                    playChimeSound();
                }
            }
        }

        loadHistory(isInitial = false) {
            fetch(`/api/chat/sos/${this.sosId}/messages`)
                .then(res => res.json())
                .then(messages => {
                    if (Array.isArray(messages)) {
                        if (this.loadingEl && this.loadingEl.parentNode) {
                            this.loadingEl.remove();
                        }
                        messages.forEach(msg => this.appendMessage(msg, isInitial));
                        this.scrollToBottom();
                    }
                })
                .catch(err => console.warn(`Không thể tải lịch sử chat nổi ca #${this.sosId}:`, err));
        }

        populateQuickChips() {
            if (!this.quickChipsEl) return;
            this.quickChipsEl.innerHTML = '';

            const citizenChips = [
                '🚨 Nước dâng cao gấp!',
                '📍 Tôi đang ở trên mái / tầng 2',
                '🔋 Điện thoại sắp cạn pin',
                '👥 Có người già và trẻ nhỏ',
                '✅ Đã thấy cờ / đèn cứu hộ'
            ];

            const rescuerChips = [
                '⏱ Đội đang cách bạn 500m',
                '🚨 Giữ nguyên vị trí an toàn',
                '📢 Phát tín hiệu đèn / còi',
                '🤝 Đội đã tới điểm tiếp cận',
                '🔋 Tiết kiệm pin, đội đang vào'
            ];

            const role = (this.session.userRole || '').toUpperCase();
            const chips = (role === 'RESCUER') ? rescuerChips : citizenChips;

            chips.forEach(text => {
                const btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'quick-chip-item border-0';
                btn.textContent = text;
                btn.addEventListener('click', () => {
                    this.sendChatMessage(text, 'QUICK_ALERT');
                });
                this.quickChipsEl.appendChild(btn);
            });
        }

        sendChatMessage(content, messageType = 'TEXT') {
            if (!content || !content.trim()) return;

            const payload = {
                content: content.trim(),
                senderType: this.session.mySenderType || (this.session.userRole === 'RESCUER' ? 'RESCUER' : 'CITIZEN'),
                senderName: this.session.mySenderName || 'Tôi',
                senderPhone: this.session.mySenderPhone || '',
                messageType: messageType,
                assignmentId: this.session.assignmentId || null
            };

            const stomp = this.manager.stompClient;
            if (stomp && stomp.connected) {
                try {
                    stomp.send(`/app/chat/${this.sosId}/send`, {}, JSON.stringify(payload));
                    return;
                } catch (err) {
                    console.warn('Lỗi gửi qua WebSocket, fallback REST:', err);
                }
            }

            // Dự phòng REST API POST
            fetch(`/api/chat/sos/${this.sosId}/messages`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
                .then(res => res.json())
                .then(dto => {
                    this.appendMessage(dto);
                })
                .catch(err => console.error('Không thể gửi tin nhắn qua REST:', err));
        }

        destroy() {
            if (this.stompSubscription) {
                try { this.stompSubscription.unsubscribe(); } catch (_) {}
            }
            if (this.itemEl && this.itemEl.parentNode) {
                this.itemEl.remove();
            }
        }
    }

    /**
     * Quản lý tập trung toàn bộ các phiên Chat nổi (1 hoặc N ca đồng thời).
     */
    class FloatingChatManager {
        constructor() {
            this.windows = new Map(); // sosId -> FloatingChatWindow
            this.stompClient = null;
            this.mapUpdateSubscription = null;
            this.pollInterval = null;

            this.dockEl = document.getElementById('floatingChatDock');
            this.templateEl = document.getElementById('floatingChatItemTemplate');
        }

        init() {
            if (!this.dockEl || !this.templateEl) return false;
            this.connectWebSocket();
            this.checkActiveSessions(false);
            return true;
        }

        connectWebSocket() {
            if (!window.SockJS || !window.Stomp) {
                setTimeout(() => this.connectWebSocket(), 2500);
                return;
            }

            try {
                const socket = new SockJS('/ws');
                this.stompClient = Stomp.over(socket);
                this.stompClient.debug = null; // Tắt log console ồn ào

                this.stompClient.connect({}, () => {
                    // Subscribe chat room cho từng ca đang mở
                    this.windows.forEach(win => this.subscribeWindow(win));

                    // Subscribe map-updates để phát hiện khi Đội cứu hộ CHẤP NHẬN hoặc HOÀN THÀNH ca
                    if (!this.mapUpdateSubscription) {
                        this.mapUpdateSubscription = this.stompClient.subscribe('/topic/map-updates', frame => {
                            try {
                                const data = JSON.parse(frame.body);
                                if (data.type === 'CHAT_ACTIVATED' || data.type === 'MISSION_START' || data.type === 'STATUS_CHANGE' || data.type === 'MISSION_COMPLETE') {
                                    this.checkActiveSessions(true);
                                }
                            } catch (e) {
                                console.error('Invalid map update', e);
                            }
                        });
                    }
                }, (err) => {
                    console.warn('WebSocket chat mất kết nối, thử lại sau 5s...', err);
                    setTimeout(() => this.connectWebSocket(), 5000);
                });
            } catch (e) {
                console.error('Không thể kết nối SockJS chat:', e);
                setTimeout(() => this.connectWebSocket(), 5000);
            }
        }

        subscribeWindow(chatWindow) {
            if (!this.stompClient || !this.stompClient.connected) return;
            if (chatWindow.stompSubscription) {
                try { chatWindow.stompSubscription.unsubscribe(); } catch (_) {}
            }

            chatWindow.stompSubscription = this.stompClient.subscribe(`/topic/chat/${chatWindow.sosId}`, frame => {
                try {
                    const messageDto = JSON.parse(frame.body);
                    chatWindow.appendMessage(messageDto);
                } catch (err) {
                    console.error('Lỗi phân tích tin nhắn WebSocket:', err);
                }
            });
        }

        onWindowOpening(openingWindow) {
            // Trên màn hình di động (< 768px), chỉ mở 1 cửa sổ cùng lúc để tránh che khuất
            const isMobile = window.innerWidth < 768;
            if (isMobile) {
                this.windows.forEach(win => {
                    if (win !== openingWindow && win.isOpen) {
                        win.minimizeWindow();
                    }
                });
                return;
            }

            // Trên desktop: tính toán số lượng cửa sổ tối đa vừa với chiều rộng màn hình
            const windowWidth = 360 + 16;
            const maxWindows = Math.max(1, Math.floor((window.innerWidth - 60) / windowWidth));
            const openWindows = Array.from(this.windows.values()).filter(w => w.isOpen && w !== openingWindow);

            if (openWindows.length >= maxWindows) {
                // Thu nhỏ cửa sổ cũ nhất để nhường chỗ
                openWindows[0].minimizeWindow();
            }
        }

        checkActiveSessions(forceOpenOnNewActivation = false) {
            let urlSosId = null;
            const pathMatch = window.location.pathname.match(/\/sos\/track\/(\d+)/);
            if (pathMatch) {
                urlSosId = pathMatch[1];
            } else {
                const searchParams = new URLSearchParams(window.location.search);
                urlSosId = searchParams.get('sosId');
            }
            const url = urlSosId ? `/api/chat/active-sessions?sosId=${encodeURIComponent(urlSosId)}` : '/api/chat/active-sessions';

            fetch(url)
                .then(res => res.json())
                .then(sessions => {
                    if (!Array.isArray(sessions)) {
                        sessions = [];
                    }

                    if (sessions.length === 0) {
                        // Không có ca cứu nạn đang tiếp cận
                        this.windows.forEach(win => win.destroy());
                        this.windows.clear();
                        if (this.dockEl) this.dockEl.style.display = 'none';
                        if (this.pollInterval) {
                            clearInterval(this.pollInterval);
                            this.pollInterval = null;
                        }
                        return;
                    }

                    // Có ca đang hoạt động
                    if (this.dockEl) this.dockEl.style.display = 'flex';

                    const serverSosIds = new Set(sessions.map(s => Number(s.sosId)));

                    // 1. Xóa các ca đã kết thúc/hủy
                    this.windows.forEach((win, sosId) => {
                        if (!serverSosIds.has(sosId)) {
                            win.destroy();
                            this.windows.delete(sosId);
                        }
                    });

                    // 2. Thêm hoặc cập nhật từng ca cứu nạn
                    sessions.forEach((sess, index) => {
                        const sosId = Number(sess.sosId);
                        if (this.windows.has(sosId)) {
                            this.windows.get(sosId).updateSessionData(sess);
                        } else {
                            const chatWindow = new FloatingChatWindow(sess, this);
                            this.windows.set(sosId, chatWindow);
                            this.dockEl.appendChild(chatWindow.itemEl);

                            if (this.stompClient && this.stompClient.connected) {
                                this.subscribeWindow(chatWindow);
                            }

                            // Xác định trạng thái mở hay thu nhỏ ban đầu
                            const savedState = sessionStorage.getItem('floating_chat_state_' + sosId);
                            const isMobile = window.innerWidth < 768;

                            if (savedState === 'open') {
                                chatWindow.openWindow();
                            } else if (savedState === 'minimized') {
                                chatWindow.minimizeWindow();
                            } else {
                                // Lần đầu tải: Trên desktop, mở tất cả các ca (hoặc ca đầu nếu mobile)
                                if (isMobile) {
                                    if (index === 0) chatWindow.openWindow();
                                    else chatWindow.minimizeWindow();
                                } else {
                                    // Desktop: mở tất cả n ca cạnh nhau
                                    chatWindow.openWindow();
                                }

                                if (forceOpenOnNewActivation) {
                                    playChimeSound();
                                }
                            }
                        }
                    });

                    // Polling dự phòng mỗi 12 giây nếu WebSocket mất kết nối
                    if (!this.pollInterval) {
                        this.pollInterval = setInterval(() => {
                            if (!this.stompClient || !this.stompClient.connected) {
                                this.windows.forEach(win => win.loadHistory());
                            }
                        }, 12000);
                    }
                })
                .catch(err => {
                    console.warn('Lỗi kiểm tra danh sách active chat sessions:', err);
                });
        }

        openChat(sosId) {
            if (sosId && this.windows.has(Number(sosId))) {
                this.windows.get(Number(sosId)).openWindow();
            } else if (this.windows.size > 0) {
                this.windows.values().next().value.openWindow();
            }
        }

        refresh() {
            this.checkActiveSessions(false);
        }
    }

    // Khởi chạy khi DOM sẵn sàng
    document.addEventListener('DOMContentLoaded', () => {
        const manager = new FloatingChatManager();
        if (manager.init()) {
            window.floatingChatManager = manager;
            window.openFloatingChat = (sosId) => manager.openChat(sosId);
            window.refreshFloatingChats = () => manager.refresh();
        }
    });

})();
