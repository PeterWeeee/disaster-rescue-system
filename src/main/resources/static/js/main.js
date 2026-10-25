/**
 * CỨUNẠN 24/7 - JAVASCRIPT HELPERS & GEOLOCATION
 */

document.addEventListener("DOMContentLoaded", function () {
    // 1. Quét GPS tự động ở Hero Card
    const btnDetectGps = document.getElementById("btn-detect-gps");
    const quickLatLng = document.getElementById("quick-latlng");
    const quickGeoStatus = document.getElementById("quick-geo-status");

    if (btnDetectGps && quickLatLng) {
        btnDetectGps.addEventListener("click", function () {
            if (!navigator.geolocation) {
                quickGeoStatus.className = "alert alert-warning py-2 small mb-3";
                quickGeoStatus.innerHTML = '<i class="fa-solid fa-triangle-exclamation me-1"></i> Trình duyệt của bạn không hỗ trợ định vị GPS.';
                return;
            }

            quickGeoStatus.className = "alert alert-info py-2 small mb-3";
            quickGeoStatus.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-1"></i> Đang truy vấn vệ tinh GPS xác định tọa độ của bạn...';

            navigator.geolocation.getCurrentPosition(
                function (position) {
                    const lat = position.coords.latitude.toFixed(6);
                    const lng = position.coords.longitude.toFixed(6);
                    quickLatLng.value = `${lat}, ${lng}`;
                    quickGeoStatus.className = "alert alert-success py-2 small mb-3";
                    quickGeoStatus.innerHTML = `<i class="fa-solid fa-circle-check me-1"></i> Đã bắt được tọa độ: <strong>${lat}, ${lng}</strong> (Độ chính xác: ±${Math.round(position.coords.accuracy)}m)`;
                    sessionStorage.setItem("user_last_lat", lat);
                    sessionStorage.setItem("user_last_lng", lng);
                },
                function (error) {
                    quickGeoStatus.className = "alert alert-danger py-2 small mb-3";
                    quickGeoStatus.innerHTML = `<i class="fa-solid fa-circle-xmark me-1"></i> Không thể lấy vị trí: ${error.message}. Vui lòng cấp quyền truy cập vị trí.`;
                },
                { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
            );
        });
    }

    // 2. Quét GPS tự động ở Form SOS (/sos) - chỉ dùng fallback khi không có bản đồ Leaflet
    const sosMapPresent = document.getElementById("sosMap");
    const btnGetLocation = document.getElementById("btn-get-location");
    const inputLat = document.getElementById("input-lat");
    const inputLng = document.getElementById("input-lng");
    const geoAlert = document.getElementById("geo-alert");

    // Tự động điền nếu đã quét trước đó ở trang chủ (chỉ fallback nếu không có sosMap)
    if (!sosMapPresent && inputLat && inputLng) {
        const savedLat = sessionStorage.getItem("user_last_lat");
        const savedLng = sessionStorage.getItem("user_last_lng");
        if (savedLat && savedLng && !inputLat.value) {
            inputLat.value = savedLat;
            inputLng.value = savedLng;
            if (geoAlert) {
                geoAlert.className = "small mt-2 text-success";
                geoAlert.innerHTML = `<i class="fa-solid fa-check me-1"></i> Đã nạp vị trí từ phiên trước: ${savedLat}, ${savedLng}`;
            }
        }
    }

    if (!sosMapPresent && btnGetLocation && inputLat && inputLng) {
        btnGetLocation.addEventListener("click", function () {
            if (!navigator.geolocation) {
                if (geoAlert) {
                    geoAlert.className = "small mt-2 text-danger";
                    geoAlert.innerText = "Trình duyệt không hỗ trợ GPS.";
                }
                return;
            }

            if (geoAlert) {
                geoAlert.className = "small mt-2 text-primary";
                geoAlert.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-1"></i> Đang lấy tọa độ vệ tinh độ chính xác cao...';
            }

            navigator.geolocation.getCurrentPosition(
                function (pos) {
                    const lat = pos.coords.latitude.toFixed(6);
                    const lng = pos.coords.longitude.toFixed(6);
                    inputLat.value = lat;
                    inputLng.value = lng;
                    sessionStorage.setItem("user_last_lat", lat);
                    sessionStorage.setItem("user_last_lng", lng);

                    if (geoAlert) {
                        geoAlert.className = "small mt-2 text-success fw-semibold";
                        geoAlert.innerHTML = `<i class="fa-solid fa-circle-check me-1"></i> Đã định vị chính xác: ${lat}, ${lng} (Sai số ±${Math.round(pos.coords.accuracy)}m)`;
                    }
                },
                function (err) {
                    if (geoAlert) {
                        geoAlert.className = "small mt-2 text-danger";
                        geoAlert.innerHTML = `<i class="fa-solid fa-triangle-exclamation me-1"></i> Lỗi lấy vị trí: ${err.message}.`;
                    }
                },
                { enableHighAccuracy: true, timeout: 12000, maximumAge: 0 }
            );
        });
    }

    // 3. Preview ảnh tải lên hiện trường
    const photoInput = document.getElementById("photoInput");
    const imagePreview = document.getElementById("imagePreview");
    const previewImg = document.getElementById("previewImg");

    if (photoInput && imagePreview && previewImg) {
        photoInput.addEventListener("change", function () {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewImg.src = e.target.result;
                    imagePreview.classList.remove("d-none");
                };
                reader.readAsDataURL(file);
            } else {
                imagePreview.classList.add("d-none");
            }
        });
    }
});
