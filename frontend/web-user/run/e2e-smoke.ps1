$ErrorActionPreference = 'Stop'

$base = 'http://localhost:3100'
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$ts = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$email = "e2e-user-$ts@example.com"

$signupBody = @{ 
  name = 'E2E User'
  email = $email
  password = 'Password123!'
} | ConvertTo-Json -Compress

$signup = Invoke-RestMethod -Uri "$base/api/v1/auth/signup" -Method Post -ContentType 'application/json' -Body $signupBody -WebSession $session
if (-not $signup.success) {
  throw "signup failed: $($signup | ConvertTo-Json -Depth 8 -Compress)"
}

$homeHtml = (Invoke-WebRequest -Uri "$base/" -WebSession $session).Content
if ($homeHtml -notlike '*카테고리별 바로 주문*') {
  throw 'home missing category section'
}

$stores = Invoke-RestMethod -Uri "$base/api/v1/stores" -WebSession $session
if (-not $stores.success -or -not $stores.data -or $stores.data.Count -lt 1) {
  throw "stores empty: $($stores | ConvertTo-Json -Depth 8 -Compress)"
}

$storeId = [string]$stores.data[0].id
$storeName = [string]$stores.data[0].name
if (-not $stores.data[0].category) {
  throw 'store category missing'
}

$detailHtml = (Invoke-WebRequest -Uri "$base/stores/$storeId" -WebSession $session).Content
if ($detailHtml -notlike "*$storeName*") {
  throw 'store detail missing store name'
}

$menus = Invoke-RestMethod -Uri "$base/api/v1/stores/$storeId/menus" -WebSession $session
if (-not $menus.success -or -not $menus.data -or $menus.data.Count -lt 1) {
  throw "menus empty: $($menus | ConvertTo-Json -Depth 8 -Compress)"
}

$menuId = [string]$menus.data[0].id
$menuName = [string]$menus.data[0].name
$menuPrice = [int]$menus.data[0].price

$cartBody = @{
  storeId = $storeId
  storeName = $storeName
  menuId = $menuId
  menuName = $menuName
  quantity = 1
  price = $menuPrice
} | ConvertTo-Json -Compress

$addCart = Invoke-RestMethod -Uri "$base/api/v1/users/me/cart" -Method Post -ContentType 'application/json' -Body $cartBody -WebSession $session
if (-not $addCart.success) {
  throw "add cart failed: $($addCart | ConvertTo-Json -Depth 8 -Compress)"
}

$checkoutHtml = (Invoke-WebRequest -Uri "$base/checkout" -WebSession $session).Content
if ($checkoutHtml -notlike "*$menuName*") {
  throw 'checkout missing menu name'
}

$cart = Invoke-RestMethod -Uri "$base/api/v1/users/me/cart" -WebSession $session
$cartCount = if ($cart.data) { $cart.data.Count } else { 0 }
if ($cartCount -lt 1) {
  throw 'cart empty after add'
}

Write-Output "E2E_OK email=$email storeId=$storeId storeName=$storeName menuName=$menuName cartCount=$cartCount"
