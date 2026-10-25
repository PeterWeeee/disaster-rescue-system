/**
 * CỨUNẠN 24/7 - JAVASCRIPT HELPERS & GEOLOCATION
 */

document.addEventListener("DOMContentLoaded", function () {
    console.log(">>> CứuNạn 24/7 JS Engine initialized successfully.");

    // 1. Quét GPS tự động ở Hero Card (Trang chủ)
    const btnDetectGps = document.getElementById("btn-detect-gps");
    const quickLatLng = document.getElementById("quick-latlng");
    const quickGeoStatus = document.getElementById("quick-geo-status");
    const btnGotoSos = document.getElementById("btn-goto-sos");

    if (btnDetectGps && quickLatLng) {
        btnDetectGps.addEventListener("click", function () {
            console.log(">>> Clicked btn-detect-gps");
            if (!navigator.geolocation) {
                if (quickGeoStatus) {
                    quickGeoStatus.className = "alert alert-warning py-2 small mb-3";
                    quickGeoStatus.innerHTML = '<i class="fa-solid fa-triangle-exclamation me-1"></i> Trình duyệt của bạn không hỗ trợ định vị GPS.';
                }
                return;
            }

            btnDetectGps.disabled = true;
            btnDetectGps.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-1"></i> Đang quét...';
            if (quickGeoStatus) {
                quickGeoStatus.className = "alert alert-info py-2 small mb-3";
                quickGeoStatus.innerHTML = '<i class="fa-solid fa-satellite-dish fa-spin me-1"></i> Đang truy vấn vệ tinh GPS xác định tọa độ...';
            }

            function onQuickSuccess(position) {
                const lat = position.coords.latitude.toFixed(6);
                const lng = position.coords.longitude.toFixed(6);
                const acc = Math.round(position.coords.accuracy || 15);

                quickLatLng.value = `${lat}, ${lng}`;
                btnDetectGps.disabled = false;
                btnDetectGps.innerHTML = '<i class="fa-solid fa-crosshairs me-1"></i> Quét lại';

                if (quickGeoStatus) {
                    quickGeoStatus.className = "alert alert-success py-2 small mb-3 fw-semibold";
                    quickGeoStatus.innerHTML = `<i class="fa-solid fa-circle-check text-success me-1"></i> Đã định vị thành công: <strong>${lat}, ${lng}</strong> (Sai số: ±${acc}m)`;
                }

                if (btnGotoSos) {
                    btnGotoSos.href = `/sos?lat=${lat}&lng=${lng}`;
                }

                sessionStorage.setItem("user_last_lat", lat);
                sessionStorage.setItem("user_last_lng", lng);
            }

            function onQuickFail(err) {
                // Nếu timeout hoặc không có chip GPS (máy tính bàn), fallback sang định vị Wi-Fi/IP
                if (err.code === err.TIMEOUT || err.code === err.POSITION_UNAVAILABLE) {
                    if (quickGeoStatus) {
                        quickGeoStatus.innerHTML = '<i class="fa-solid fa-network-wired fa-spin me-1"></i> Vệ tinh bận, đang chuyển sang định vị Wi-Fi/IP...';
                    }
                    navigator.geolocation.getCurrentPosition(
                        onQuickSuccess,
                        function (err2) {
                            finalizeFail(err2);
                        },
                        { enableHighAccuracy: false, timeout: 12000, maximumAge: 60000 }
                    );
                    return;
                }
                finalizeFail(err);
            }

            function finalizeFail(err) {
                btnDetectGps.disabled = false;
                btnDetectGps.innerHTML = '<i class="fa-solid fa-crosshairs me-1"></i> Quét GPS';

                let msg = "Không thể lấy vị trí hiện tại.";
                if (err.code === err.PERMISSION_DENIED) {
                    msg = "Trình duyệt chưa được cấp quyền vị trí. Vui lòng bấm <b>Cho phép (Allow)</b> trên thanh địa chỉ.";
                } else {
                    msg = "Thiết bị không bắt được tín hiệu vệ tinh. Bạn có thể mở form cứu nạn và <b>click trực tiếp lên bản đồ</b>.";
                }

                if (quickGeoStatus) {
                    quickGeoStatus.className = "alert alert-warning py-2 small mb-3";
                    quickGeoStatus.innerHTML = `<i class="fa-solid fa-triangle-exclamation text-warning me-1"></i> ${msg}`;
                }
            }

            navigator.geolocation.getCurrentPosition(
                onQuickSuccess,
                onQuickFail,
                { enableHighAccuracy: true, timeout: 6000, maximumAge: 0 }
            );
        });
    }

    // 2. Tự động điền tọa độ nếu đã quét ở trang chủ trước đó (khi vào trang /sos)
    const inputLat = document.getElementById("input-lat");
    const inputLng = document.getElementById("input-lng");
    const geoAlert = document.getElementById("geo-alert");

    if (inputLat && inputLng && !inputLat.value) {
        const savedLat = sessionStorage.getItem("user_last_lat");
        const savedLng = sessionStorage.getItem("user_last_lng");
        if (savedLat && savedLng) {
            inputLat.value = savedLat;
            inputLng.value = savedLng;
            if (geoAlert) {
                geoAlert.className = "alert alert-success py-2 small mb-3";
                geoAlert.innerHTML = `<i class="fa-solid fa-circle-check me-1"></i> Đã đồng bộ tọa độ từ trang chủ: <strong>${savedLat}, ${savedLng}</strong>`;
            }
        }
    }
});
