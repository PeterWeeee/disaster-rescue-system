# ==============================================================================
# SCRIPT COMMIT NHANH THEO THANH VIEN (HO TRO GHI NHAN COMMIT TREN GITHUB)
# ==============================================================================
# Cu phap:
#   .\commit-as.ps1 <1|2|3> "thong diep commit" [-Push]
#
# Vi du:
#   .\commit-as.ps1 2 "feat(auth): implement spring security 6 and jwt filter"
#   .\commit-as.ps1 3 "feat(dispatch): implement haversine algorithm and coordinator dashboard" -Push
# ==============================================================================

[CmdletBinding()]
param (
    [Parameter(Mandatory = $true, Position = 0)]
    [ValidateSet("1", "2", "3")]
    [string]$Member,

    [Parameter(Mandatory = $true, Position = 1)]
    [string]$Message,

    [switch]$Push
)

$authors = @{
    "1" = @{
        Name  = "PeterWeeee"
        Email = "NguyenTT162.4@gmail.com"
        Label = "Tri Thai / PeterWeeee"
    };
    "2" = @{
        Name  = "Vo Van Thinh"
        Email = "vovanthinh2006@gmail.com"
        Label = "Vo Van Thinh"
    };
    "3" = @{
        Name  = "Student 24110329"
        Email = "24110329@student.hcmute.edu.vn"
        Label = "Sinh vien 24110329"
    }
}

$chosen = $authors[$Member]
$authorString = "$($chosen.Name) <$($chosen.Email)>"

Write-Host "--------------------------------------------------------" -ForegroundColor Cyan
Write-Host "TIEN HANH COMMIT CHO THANH VIEN: $($chosen.Label)" -ForegroundColor Yellow
Write-Host "Tac gia commit: $authorString" -ForegroundColor White
Write-Host "Thong diep: $Message" -ForegroundColor White
Write-Host "--------------------------------------------------------" -ForegroundColor Cyan

# 1. Thiet lap bien moi truong Git tam thoi cho phien lam viec nay
$env:GIT_AUTHOR_NAME = $chosen.Name
$env:GIT_AUTHOR_EMAIL = $chosen.Email
$env:GIT_COMMITTER_NAME = $chosen.Name
$env:GIT_COMMITTER_EMAIL = $chosen.Email

# 2. Dong thoi thu cap nhat git config local neu co the ghi
try {
    git config --local user.name "$($chosen.Name)" 2>$null
    git config --local user.email "$($chosen.Email)" 2>$null
} catch {}

# 3. Chay git add va git commit
Write-Host "[1/3] Adding changes (git add .)..." -ForegroundColor Gray
git add .

Write-Host "[2/3] Committing with specified author..." -ForegroundColor Gray
git commit --author="$authorString" -m "$Message"

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n>>> COMMIT THANH CONG! <<<" -ForegroundColor Green
    git log -1 --pretty=format:"Hash: %h%nAuthor: %an <%ae>%nDate: %ad%nMessage: %s%n"
    
    if ($Push) {
        Write-Host "`n[3/3] Pushing to origin develop..." -ForegroundColor Magenta
        git push origin develop
        Write-Host ">>> PUSH THANH CONG! <<<" -ForegroundColor Green
    } else {
        Write-Host "`nDe push len GitHub, hay chay lenh: git push origin develop" -ForegroundColor Cyan
    }
} else {
    Write-Host "`nKhong co thay doi nao de commit hoac da xay ra loi!" -ForegroundColor Yellow
}
