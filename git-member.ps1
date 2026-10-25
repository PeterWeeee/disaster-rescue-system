# ==============================================================================
# SCRIPT CHUYỂN ĐỔI THÔNG TIN COMMIT GIỮA CÁC THÀNH VIÊN NHÓM 04
# Dự án: Hệ Thống Cứu Nạn Thiên Tai (CứuNạn 24/7)
# ==============================================================================
# Cách sử dụng:
#   .\git-member.ps1            -> Hiển thị menu chọn thành viên
#   .\git-member.ps1 1          -> Đổi sang PeterWeeee (Trưởng nhóm / Backend Core)
#   .\git-member.ps1 2          -> Đổi sang Võ Văn Thịnh (Security / Phân hệ Dân)
#   .\git-member.ps1 3          -> Đổi sang SV 24110329 (Điều phối / Cứu hộ / Admin)
#   .\git-member.ps1 -Check     -> Xem ai đang là người commit hiện tại
# ==============================================================================

[CmdletBinding()]
param (
    [Parameter(Position = 0)]
    [string]$MemberChoice,

    [Parameter()]
    [string]$CommitMessage,

    [switch]$Check
)

$members = @{
    "1" = @{
        Name  = "PeterWeeee"
        Email = "NguyenTT162.4@gmail.com"
        Role  = "Lead / Kien truc & Realtime (Session 01, 04, 05)"
    };
    "2" = @{
        Name  = "Vo Van Thinh"
        Email = "vovanthinh2006@gmail.com"
        Role  = "Bao mat & Phan he Dan SOS (Session 02, 03, 09-UI)"
    };
    "3" = @{
        Name  = "Student 24110329"
        Email = "24110329@student.hcmute.edu.vn"
        Role  = "Dieu phoi & Cuu ho & Admin (Session 06, 07, 08)"
    }
}

function Show-Current-Author {
    $currentName = git config --local user.name
    $currentEmail = git config --local user.email
    $scope = "(Local Repo)"
    if (-not $currentName) {
        $currentName = git config --global user.name
        $currentEmail = git config --global user.email
        $scope = "(Global)"
    }
    Write-Host ""
    Write-Host "==================================================" -ForegroundColor Cyan
    Write-Host "TAC GIA COMMIT HIEN TAI $scope" -ForegroundColor Yellow
    Write-Host "   Ten   : $currentName" -ForegroundColor White
    Write-Host "   Email : $currentEmail" -ForegroundColor White
    Write-Host "==================================================" -ForegroundColor Cyan
    Write-Host ""
}

function Set-Member-Author([string]$id) {
    if (-not $members.ContainsKey($id)) {
        Write-Host "Lua chon khong hop le! Vui long chon 1, 2 hoac 3." -ForegroundColor Red
        return $false
    }
    $m = $members[$id]
    git config --local user.name "$($m.Name)"
    git config --local user.email "$($m.Email)"
    Write-Host "DA CHUYEN DANH TINH COMMIT SANG:" -ForegroundColor Green
    Write-Host "   Ten  : $($m.Name)" -ForegroundColor Yellow
    Write-Host "   Email: $($m.Email)" -ForegroundColor Yellow
    Write-Host "   Vai tro: $($m.Role)" -ForegroundColor Gray
    Write-Host "Cac commit tiep theo se duoc tinh cho tai khoan nay tren GitHub!`n" -ForegroundColor Cyan
    return $true
}

if ($Check) {
    Show-Current-Author
    exit 0
}

if ($MemberChoice -and $members.ContainsKey($MemberChoice)) {
    $ok = Set-Member-Author $MemberChoice
    if ($CommitMessage -and $ok) {
        Write-Host "Dang tien hanh commit voi thong diep: '$CommitMessage'..." -ForegroundColor Magenta
        git add .
        git commit -m "$CommitMessage"
        Write-Host "Commit hoan tat! Ban co the chay 'git push origin develop'." -ForegroundColor Green
    }
    exit 0
}

Show-Current-Author

Write-Host "CHON THANH VIEN DE DOI THONG TIN COMMIT:" -ForegroundColor Yellow
Write-Host "  [1] PeterWeeee (NguyenTT162.4@gmail.com) - Core & Realtime" -ForegroundColor White
Write-Host "  [2] Vo Van Thinh (vovanthinh2006@gmail.com) - Security & SOS Leaflet" -ForegroundColor White
Write-Host "  [3] Student 24110329 (24110329@student.hcmute.edu.vn) - Coordinator & Rescuer & Admin" -ForegroundColor White
Write-Host "  [Q] Thoat" -ForegroundColor Gray
Write-Host ""

$choice = Read-Host "Nhap so (1/2/3) hoac bam Enter de giu nguyen"
if ($choice -match '^[123]$') {
    Set-Member-Author $choice
} else {
    Write-Host "Giu nguyen cau hinh hien tai." -ForegroundColor Gray
}
