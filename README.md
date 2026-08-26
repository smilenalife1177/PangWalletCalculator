# 胖錢包計算機 App™ v1.0｜26合1生活計算 × 桌面 Widget版

這一版把兩條線正式合在一起：

1. **App 內：26 合 1 生活計算**
   - 直接載入微笑娜生活萬用計算機 v2.8 公開版
   - 保留搜尋、分類、收藏、最近使用與 26 種生活計算
   - 匯率等線上資料可直接使用網站最新邏輯

2. **手機桌面：胖錢包 Widget**
   - 不開 App 也能直接四則運算
   - 名胖角色保留
   - 支援拖曳與尺寸調整
   - 每顆 Widget 各自保存計算狀態

## v1.0 架構

目前 App 內的 26 合 1採「原生 Android App 外殼 + 安全 WebView」方式載入現行 v2.8 網頁。
好處是：網站邏輯更新後，App 不必重新發布就能同步收到 26 合 1的新修正。
桌面 Widget 則是原生 Android 功能，可離線四則運算。

## GitHub 自動編譯 APK

把解壓後的整個專案內容上傳到 GitHub 倉庫根目錄。
`.github/workflows/build-apk.yml` 會自動建立可安裝 APK。

到：Actions → Build 胖錢包計算機 App v1.0 → 最新綠色勾勾 → Artifacts
下載：`胖錢包計算機-App-v1.0-APK`

## Google Play 下一階段

目前先用 APK 做手機實測。
Google Play 正式上架會另外建立「Release AAB + Upload Key 簽章」流程，避免用 debug 簽章誤上架。
