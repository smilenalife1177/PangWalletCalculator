# 胖錢包計算機 Widget™ v1.0

這不是「點進 App 才能用」的計算機，而是 Android 手機桌面 Widget。

## 已完成
- 桌面直接按數字、加減乘除、等號、清除
- 可拖曳到桌面任何位置
- 可拉大 / 縮小；尺寸變小時自動切換 compact 版
- 名胖圖片已內建，並處理成透明背景 PNG
- 每一個 Widget 各自保存計算狀態
- 關螢幕、重開手機後，最後數字仍保留
- 第一次開 App 有「＋ 加到手機桌面」按鈕

## 用 Android Studio 開啟
1. File → Open → 選整個 `PangWalletCalculator` 資料夾。
2. 等 Gradle Sync 完成。
3. 接 Android 手機，開啟 USB 偵錯。
4. Run ▶ 安裝到手機。
5. 第一次打開「胖錢包計算機」，按「＋ 加到手機桌面」。
6. 之後直接在桌面使用，不必再打開 App。

也可長按桌面 → 小工具 / Widgets → 胖錢包計算機。

## 重要檔案
- `WalletCalculatorWidget.kt`：計算邏輯、按鍵、保存狀態、縮放切版
- `widget_wallet_full.xml`：大版桌面計算機
- `widget_wallet_compact.xml`：縮小版
- `mingpang.png`：名胖透明背景圖

## v1.0 計算鍵
7 8 9 ÷
4 5 6 ×
1 2 3 −
0 . + =
C 清除

## v1.1 奶油杏 × 牛仔藍改色版

這版只改視覺，不動計算邏輯與 package name。

- 計算機機身：奶油杏 `#F3DFB9`
- 螢幕：淡奶油 `#FFF8E8`
- 數字鍵：暖白 `#FFF4D9`
- 運算鍵：牛仔藍 `#456D91`
- 等號：珊瑚粉 `#E96F78`
- 文字／外框：咖啡棕

顏色已集中到：
`app/src/main/res/values/colors.xml`

之後想換色，通常只需要改 `colors.xml`，不用再到各個版面找色碼。

`applicationId` 維持 `tw.smilenalife.pangwallet`，版本升級為 `1.1 (versionCode 2)`，方便以相同簽章直接覆蓋更新舊版。
