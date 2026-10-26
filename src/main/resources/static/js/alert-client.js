(function () {
    let client;
    let reconnectTimer;
    const modalElement = document.getElementById('emergencyBroadcastModal');
    const modal = modalElement && window.bootstrap ? bootstrap.Modal.getOrCreateInstance(modalElement, { backdrop: 'static', keyboard: false }) : null;

    function publish(name, payload) {
        document.dispatchEvent(new CustomEvent(name, { detail: payload }));
    }

    function showBroadcast(alert) {
        if (!modal) return;
        const severityStyles = {
            DANGER_RED: ['bg-danger', 'btn-danger', 'bg-danger-subtle text-danger'],
            WARNING_ORANGE: ['bg-warning', 'btn-warning', 'bg-warning-subtle text-dark'],
            NOTICE_YELLOW: ['bg-info', 'btn-info', 'bg-info-subtle text-info-emphasis']
        };
        const styles = severityStyles[alert.severity] || severityStyles.DANGER_RED;
        const header = document.getElementById('broadcastModalHeader');
        const button = document.getElementById('acknowledgeBroadcast');
        const region = document.getElementById('broadcastAlertRegion');
        header.className = `modal-header text-white ${styles[0]}`;
        button.className = `btn fw-bold px-4 ${styles[1]}`;
        region.className = `badge mb-2 ${styles[2]}`;
        region.textContent = alert.affectedRegion || 'Cảnh báo toàn khu vực';
        document.getElementById('broadcastAlertTitle').textContent = alert.title || 'Cảnh báo khẩn cấp';
        document.getElementById('broadcastAlertContent').textContent = alert.content || '';
        modal.show();
        try {
            const audioContext = new (window.AudioContext || window.webkitAudioContext)();
            const oscillator = audioContext.createOscillator();
            const gain = audioContext.createGain();
            oscillator.frequency.value = 880;
            gain.gain.value = 0.12;
            oscillator.connect(gain);
            gain.connect(audioContext.destination);
            oscillator.start();
            oscillator.stop(audioContext.currentTime + 0.45);
            oscillator.onended = () => audioContext.close();
        } catch (_) { /* Browser may block sound until user interaction. */ }
    }

    function showSosToast(sos) {
        const toast = document.createElement('div');
        toast.className = 'alert alert-danger shadow position-fixed top-0 end-0 m-3';
        toast.style.zIndex = '1100';
        toast.setAttribute('role', 'alert');
        const title = document.createElement('strong');
        title.textContent = `SOS mới #${sos.id || ''}`;
        const detail = document.createElement('div');
        detail.textContent = `Mức độ ${sos.urgencyLevel || 'khẩn cấp'} — Điều phối viên đang được thông báo.`;
        toast.append(title, detail);
        document.body.appendChild(toast);
        window.setTimeout(() => toast.remove(), 8000);
        try {
            const audioContext = new (window.AudioContext || window.webkitAudioContext)();
            const oscillator = audioContext.createOscillator();
            oscillator.frequency.value = 740;
            oscillator.connect(audioContext.destination);
            oscillator.start();
            oscillator.stop(audioContext.currentTime + 0.25);
            oscillator.onended = () => audioContext.close();
        } catch (_) { /* Sound may require prior user interaction. */ }
    }

    if (modalElement) {
        document.getElementById('acknowledgeBroadcast').addEventListener('click', () => modal.hide());
    }

    function connect() {
        if (!window.SockJS || !window.Stomp) {
            reconnectTimer = window.setTimeout(connect, 4000);
            return;
        }
        try {
            client = Stomp.over(new SockJS('/ws'));
            client.debug = null;
            client.connect({}, () => {
                client.subscribe('/topic/broadcast-alerts', frame => {
                    try { showBroadcast(JSON.parse(frame.body)); } catch (error) { console.error('Invalid broadcast alert', error); }
                });
                client.subscribe('/topic/map-updates', frame => {
                    try { publish('rescue:map-update', JSON.parse(frame.body)); } catch (error) { console.error('Invalid map update', error); }
                });
                client.subscribe('/user/queue/assignments', frame => {
                    try { publish('rescue:assignment', JSON.parse(frame.body)); } catch (error) { console.error('Invalid assignment update', error); }
                });
                client.subscribe('/topic/sos-alerts', frame => {
                    try {
                        const payload = JSON.parse(frame.body);
                        showSosToast(payload);
                        publish('rescue:new-sos', payload);
                    } catch (error) { console.error('Invalid SOS alert', error); }
                });
            }, () => {
                reconnectTimer = window.setTimeout(connect, 4000);
            });
        } catch (error) {
            console.error('WebSocket connection failed', error);
            reconnectTimer = window.setTimeout(connect, 4000);
        }
    }

    connect();
    window.addEventListener('beforeunload', () => {
        if (reconnectTimer) window.clearTimeout(reconnectTimer);
        if (client && client.connected) client.disconnect(() => {});
    });
})();
