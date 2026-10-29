/**
 * FLOATING REAL-TIME EMERGENCY CHAT DOCK (FACEBOOK MESSENGER STYLE)
 * Hệ thống Hỗ Trợ Cứu Nạn 24/7
 * Tự động nổi trên mọi trang dành cho Người dân (Citizen) và Đội cứu hộ (Rescuer)
 * khi ca cứu nạn được tiếp nhận / đang thực hiện.
 */
(function () {
    'use strict';

    let currentSession = null;
    let stompClient = null;
    let chatSubscription = null;
    let mapUpdateSubscription = null;
    let renderedMessageIds = new Set();
    let unreadCount = 0;
    let isWindowOpen = false;
    let pollInterval = null;

    // DOM Elements
    let dockEl, launcherEl, windowEl, unreadBadgeEl;
    let messagesBodyEl, chatFormEl, chatInputEl;
    let partnerNameEl, partnerSubtitleEl, partnerInitialEl, sosBadgeEl;
    let callBtnEl, trackLinkEl, minimizeBtnEl, closeBtnEl;
    let sosAddressEl, statusPillEl, quickChipsEl, toastEl;

    function initDomElements() {
        dockEl = document.getElementById('floatingChatDock');
        if (!dockEl) return false;

        launcherEl = document.getElementById('floatingChatLauncher');
        windowEl = document.getElementById('floatingChatWindow');
        unreadBadgeEl = document.getElementById('floatingChatUnreadBadge');
        messagesBodyEl = document.getElementById('floatingMessagesBody');
        chatFormEl = document.getElementById('floatingChatForm');
        chatInputEl = document.getElementById('floatingChatInput');

        partnerNameEl = document.getElementById('floatingPartnerName');
        partnerSubtitleEl = document.getElementById('floatingPartnerSubtitle');
        partnerInitialEl = document.getElementById('floatingPartnerInitial');
        sosBadgeEl = document.getElementById('floatingSosBadge');

        callBtnEl = document.getElementById('floatingCallBtn');
        trackLinkEl = document.getElementById('floatingTrackLink');
        minimizeBtnEl = document.getElementById('floatingMinimizeBtn');
        closeBtnEl = document.getElementById('floatingCloseBtn');

        sosAddressEl = document.getElementById('floatingSosAddress');
        statusPillEl = document.getElementById('floatingStatusPill');
        quickChipsEl = document.getElementById('floatingQuickChips');
        toastEl = document.getElementById('floatingChatToast');

        return true;
    }

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
            // Trình duyệt có thể chặn audio khi chưa có tương tác
        }
    }

    // Bật cửa sổ chat nổi
    function openChatWindow() {
        if (!windowEl || !launcherEl) return;
        isWindowOpen = true;
        sessionStorage.setItem('floating_chat_state', 'open');

        launcherEl.style.display = 'none';
        windowEl.style.display = 'flex';
        if (toastEl) toastEl.style.display = 'none';

        // Xóa thông báo chưa đọc
        unreadCount = 0;
        sessionStorage.setItem('floating_chat_unread', '0');
        if (unreadBadgeEl) {
            unreadBadgeEl.textContent = '0';
            unreadBadgeEl.style.display = 'none';
        }

        // Tự động focus ô nhập và scroll xuống cuối
        setTimeout(() => {
            scrollToBottom();
            if (chatInputEl) chatInputEl.focus();
        }, 120);
    }

    // Thu nhỏ về bong bóng nổi
    function minimizeChatWindow() {
        if (!windowEl || !launcherEl) return;
        isWindowOpen = false;
        sessionStorage.setItem('floating_chat_state', 'minimized');

        windowEl.style.display = 'none';
        launcherEl.style.display = 'flex';
    }

    // Cuộn danh sách tin nhắn xuống cuối
    function scrollToBottom() {
        if (messagesBodyEl) {
            messagesBodyEl.scrollTop = messagesBodyEl.scrollHeight;
        }
    }

    // Xác định tin nhắn là của TÔI (nằm BÊN PHẢI) hay của ĐỐI PHƯƠNG (nằm BÊN TRÁI)
    function isMessageFromMe(msg) {
        if (!currentSession || !msg) return false;

        const myRole = (currentSession.mySenderType || currentSession.userRole || '').toUpperCase();
        const msgSender = (msg.senderType || '').toUpperCase();

        // 1. Nếu tôi là Đội Cứu Hộ:
        if (myRole === 'RESCUER') {
            // Tin nhắn từ RESCUER là của tôi (BÊN PHẢI)
            // Tin nhắn từ CITIZEN chắc chắn là của Nạn nhân (BÊN TRÁI)
            return msgSender === 'RESCUER';
        }

        // 2. Nếu tôi là Người Dân:
        if (myRole === 'CITIZEN') {
            // Tin nhắn từ CITIZEN là của tôi (BÊN PHẢI)
            // Tin nhắn từ RESCUER chắc chắn là của Đội cứu hộ (BÊN TRÁI)
            return msgSender === 'CITIZEN';
        }

        // 3. Khớp theo tên người gửi nếu có
        if (currentSession.mySenderName && msg.senderName) {
            if (currentSession.mySenderName.trim().toLowerCase() === msg.senderName.trim().toLowerCase()) {
                return true;
            }
        }

        return msg.isMine === true;
    }

    // Thêm tin nhắn vào giao diện
    function appendMessage(msg) {
        if (!msg || !messagesBodyEl) return;
        if (msg.id && renderedMessageIds.has(msg.id)) return;
        if (msg.id) renderedMessageIds.add(msg.id);

        // Xóa loading placeholder nếu có
        const loading = document.getElementById('floatingChatLoading');
        if (loading) loading.remove();

        const row = document.createElement('div');
        const isMine = isMessageFromMe(msg);

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

        messagesBodyEl.appendChild(row);
        scrollToBottom();

        // Xử lý thông báo khi nhận tin nhắn từ người khác
        if (!isMine) {
            if (!isWindowOpen) {
                unreadCount++;
                sessionStorage.setItem('floating_chat_unread', unreadCount.toString());
                if (unreadBadgeEl) {
                    unreadBadgeEl.textContent = unreadCount > 9 ? '9+' : unreadCount;
                    unreadBadgeEl.style.display = 'block';
                }
                playChimeSound();
                showToastNotification(msg.senderName || 'Tin nhắn mới', msg.content);
            }
        }
    }

    // Hiển thị toast nổi phía trên bong bóng chat
    function showToastNotification(sender, content) {
        if (!toastEl || isWindowOpen || (windowEl && windowEl.style.display !== 'none')) return;
        const senderEl = document.getElementById('floatingToastSender');
        const contentEl = document.getElementById('floatingToastContent');
        const initialEl = document.getElementById('floatingToastInitial');

        if (senderEl) senderEl.textContent = sender;
        if (contentEl) contentEl.textContent = content;
        if (initialEl) initialEl.textContent = sender.charAt(0).toUpperCase();

        toastEl.style.display = 'flex';
        clearTimeout(toastEl._timer);
        toastEl._timer = setTimeout(() => {
            toastEl.style.display = 'none';
        }, 6000);
    }

    function escapeHtml(str) {
        if (!str) return '';
        return str
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    // Tải lịch sử tin nhắn
    function loadHistory() {
        if (!currentSession || !currentSession.sosId) return;
        fetch(`/api/chat/sos/${currentSession.sosId}/messages`)
            .then(res => res.json())
            .then(messages => {
                if (Array.isArray(messages)) {
                    const loading = document.getElementById('floatingChatLoading');
                    if (loading) loading.remove();
                    messages.forEach(msg => appendMessage(msg));
                    scrollToBottom();
                }
            })
            .catch(err => console.warn('Không thể tải lịch sử chat nổi:', err));
    }

    // Điền các nút nhắn nhanh khẩn cấp 1 chạm
    function populateQuickChips(role) {
        if (!quickChipsEl) return;
        quickChipsEl.innerHTML = '';

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

        const chips = (role === 'RESCUER') ? rescuerChips : citizenChips;

        chips.forEach(text => {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'quick-chip-item border-0';
            btn.textContent = text;
            btn.addEventListener('click', () => {
                sendChatMessage(text, 'QUICK_ALERT');
            });
            quickChipsEl.appendChild(btn);
        });
    }

    // Gửi tin nhắn
    function sendChatMessage(content, messageType = 'TEXT') {
        if (!content || !content.trim() || !currentSession || !currentSession.sosId) return;

        const payload = {
            content: content.trim(),
            senderType: currentSession.mySenderType || 'CITIZEN',
            senderName: currentSession.mySenderName || 'Tôi',
            senderPhone: currentSession.mySenderPhone || '',
            messageType: messageType,
            assignmentId: currentSession.assignmentId || null
        };

        // Ưu tiên gửi qua STOMP WebSocket
        if (stompClient && stompClient.connected) {
            try {
                stompClient.send(`/app/chat/${currentSession.sosId}/send`, {}, JSON.stringify(payload));
                return;
            } catch (err) {
                console.warn('Lỗi gửi qua WebSocket, chuyển sang REST:', err);
            }
        }

        // Dự phòng REST API POST
        fetch(`/api/chat/sos/${currentSession.sosId}/messages`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
            .then(res => res.json())
            .then(dto => {
                appendMessage(dto);
            })
            .catch(err => console.error('Không thể gửi tin nhắn qua REST:', err));
    }

    // Kết nối WebSocket STOMP
    function connectWebSocket() {
        if (!window.SockJS || !window.Stomp) {
            setTimeout(connectWebSocket, 3000);
            return;
        }

        try {
            const socket = new SockJS('/ws');
            stompClient = Stomp.over(socket);
            stompClient.debug = null; // Tắt log console ồn ào

            stompClient.connect({}, () => {
                // 1. Subscribe topic tin nhắn phòng chat ca SOS hiện tại
                if (currentSession && currentSession.sosId) {
                    subscribeChatRoom(currentSession.sosId);
                }

                // 2. Subscribe map-updates để phát hiện khi Đội cứu hộ CHẤP NHẬN nhiệm vụ (CHAT_ACTIVATED)
                if (!mapUpdateSubscription) {
                    mapUpdateSubscription = stompClient.subscribe('/topic/map-updates', frame => {
                        try {
                            const data = JSON.parse(frame.body);
                            if (data.type === 'CHAT_ACTIVATED' || data.type === 'MISSION_START' || data.type === 'STATUS_CHANGE') {
                                // Tự động kiểm tra lại session nếu chưa có chat hoặc cập nhật trạng thái
                                checkActiveSession(true);
                            }
                        } catch (e) {
                            console.error('Invalid map update', e);
                        }
                    });
                }
            }, (err) => {
                console.warn('WebSocket chat mất kết nối, thử lại sau 5s...', err);
                setTimeout(connectWebSocket, 5000);
            });
        } catch (e) {
            console.error('Không thể kết nối SockJS chat:', e);
            setTimeout(connectWebSocket, 5000);
        }
    }

    function subscribeChatRoom(sosId) {
        if (!stompClient || !stompClient.connected) return;
        if (chatSubscription) {
            try { chatSubscription.unsubscribe(); } catch (_) {}
        }
        chatSubscription = stompClient.subscribe(`/topic/chat/${sosId}`, frame => {
            try {
                const messageDto = JSON.parse(frame.body);
                appendMessage(messageDto);
            } catch (err) {
                console.error('Lỗi phân tích tin nhắn WebSocket:', err);
            }
        });
    }

    // Kiểm tra xem người dùng hiện tại có ca cứu nạn đang hoạt động không
    function checkActiveSession(forceOpenOnNewActivation = false) {
        fetch('/api/chat/active-session')
            .then(res => res.json())
            .then(data => {
                if (data && data.hasActiveChat === true) {
                    const isNewActivation = !currentSession || (currentSession.sosId !== data.sosId);
                    currentSession = data;

                    // Hiển thị khung chat nổi
                    if (dockEl) dockEl.style.display = 'block';

                    // Cập nhật thông tin đối tác chat
                    if (partnerNameEl) partnerNameEl.textContent = data.partnerName || 'Đội Cứu Hộ';
                    if (partnerSubtitleEl) partnerSubtitleEl.textContent = data.partnerSubtitle || 'Đang trực tuyến 2 chiều';
                    if (partnerInitialEl) {
                        const initChar = (data.partnerName && data.partnerName.length > 0) ? data.partnerName.charAt(0).toUpperCase() : 'C';
                        partnerInitialEl.textContent = initChar;
                    }
                    if (sosBadgeEl) sosBadgeEl.textContent = `SOS-${data.sosId}`;
                    if (sosAddressEl) sosAddressEl.textContent = data.sosAddress || 'Vị trí hiện trường';
                    if (statusPillEl) statusPillEl.textContent = data.statusText || 'Đang tiếp cận';

                    // Cập nhật nút gọi điện
                    if (callBtnEl) {
                        if (data.partnerPhone && data.partnerPhone.length > 3) {
                            callBtnEl.href = `tel:${data.partnerPhone}`;
                            callBtnEl.style.display = 'inline-flex';
                        } else {
                            callBtnEl.style.display = 'none';
                        }
                    }

                    // Cập nhật link theo dõi GPS
                    if (trackLinkEl) {
                        trackLinkEl.href = (data.userRole === 'RESCUER')
                            ? `/rescuer/routes`
                            : `/sos/track/${data.sosId}`;
                    }

                    // Điền quick action chips
                    populateQuickChips(data.userRole);

                    // Subscribe WebSocket
                    if (stompClient && stompClient.connected) {
                        subscribeChatRoom(data.sosId);
                    }

                    // Tải lịch sử tin nhắn
                    loadHistory();

                    // Xác định trạng thái mở hay thu nhỏ
                    const savedState = sessionStorage.getItem('floating_chat_state');
                    if (forceOpenOnNewActivation || savedState === 'open') {
                        openChatWindow();
                        if (forceOpenOnNewActivation && isNewActivation) {
                            playChimeSound();
                        }
                    } else {
                        minimizeChatWindow();
                    }

                    // Bắt đầu polling dự phòng mỗi 12 giây
                    if (!pollInterval) {
                        pollInterval = setInterval(() => {
                            if (!stompClient || !stompClient.connected) {
                                loadHistory();
                            }
                        }, 12000);
                    }
                } else {
                    // Không có ca cứu nạn đang tiếp cận
                    currentSession = null;
                    if (dockEl) dockEl.style.display = 'none';
                    if (pollInterval) {
                        clearInterval(pollInterval);
                        pollInterval = null;
                    }
                }
            })
            .catch(err => {
                console.warn('Lỗi kiểm tra active chat session:', err);
            });
    }

    // Khởi tạo các sự kiện giao diện
    function setupEventListeners() {
        if (launcherEl) {
            launcherEl.addEventListener('click', openChatWindow);
        }
        if (minimizeBtnEl) {
            minimizeBtnEl.addEventListener('click', minimizeChatWindow);
        }
        if (closeBtnEl) {
            closeBtnEl.addEventListener('click', minimizeChatWindow);
        }
        const headerTitleArea = document.getElementById('floatingHeaderTitleArea');
        if (headerTitleArea) {
            headerTitleArea.addEventListener('click', () => {
                if (isWindowOpen) minimizeChatWindow();
                else openChatWindow();
            });
        }
        if (toastEl) {
            toastEl.addEventListener('click', (e) => {
                if (e.target && e.target.id === 'floatingToastClose') {
                    toastEl.style.display = 'none';
                    return;
                }
                openChatWindow();
            });
        }
        const toastClose = document.getElementById('floatingToastClose');
        if (toastClose) {
            toastClose.addEventListener('click', (e) => {
                e.stopPropagation();
                toastEl.style.display = 'none';
            });
        }
        if (chatFormEl && chatInputEl) {
            chatFormEl.addEventListener('submit', (e) => {
                e.preventDefault();
                const text = chatInputEl.value;
                if (text && text.trim()) {
                    sendChatMessage(text, 'TEXT');
                    chatInputEl.value = '';
                    chatInputEl.focus();
                }
            });
        }
    }

    // Chạy khi trang web được tải
    document.addEventListener('DOMContentLoaded', () => {
        if (!initDomElements()) return;
        setupEventListeners();
        connectWebSocket();
        checkActiveSession(false);
    });

})();
