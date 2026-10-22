# SMC ICT Signal Pro — Android starter project

This is a Kotlin + Jetpack Compose Android Studio starter app for the requested SMC/ICT signal dashboard.

## Current prototype includes
- Android dashboard UI
- Market filters: India, Crypto, Forex
- Timeframe filter
- Example signal cards for BUY / SELL / WAIT
- Explicit DEMO MODE labeling

## Important limitations
- No live market data is connected.
- Signal cards are illustrative examples, not generated from live candles.
- Entry, Stop Loss and Take Profit are intentionally pending.
- No broker connection or order execution is implemented.
- Do not use this prototype to make real trading decisions.

## Open in Android Studio
1. Extract the ZIP.
2. Open the `SMC_ICT_Signal_Pro` folder in Android Studio.
3. Allow Gradle sync to complete.
4. Use JDK 17 and Android SDK 35.
5. Run the `app` configuration on an emulator or Android device.

## Next implementation steps
1. Select data providers and obtain API keys for each market.
2. Implement normalized OHLC candle models and WebSocket/REST data adapters.
3. Implement and document exact SMC/ICT rules (BOS, CHoCH, sweep, OB, FVG, multi-timeframe filters).
4. Add historical backtesting and paper-trading validation.
5. Add push notifications and persist alert preferences.
6. Review exchange data licenses and applicable Indian regulations before release.

## Suggested architecture for production
- Android client: Kotlin + Jetpack Compose
- Backend: Python + FastAPI
- Signal worker: candle processing and strategy rules
- Storage: PostgreSQL
- Notifications: Firebase Cloud Messaging
- Market feeds: exchange/provider-specific adapters

Signals are probabilistic and cannot guarantee profits. Validate strategies with realistic fees, slippage, and out-of-sample data before any real-money use.


## Phone-only cloud build (GitHub Actions)
This project includes `.github/workflows/build-apk.yml`. Upload the project files to a GitHub repository, then open **Actions → Build SMC ICT Signal Pro APK → Run workflow**. When it finishes, open the workflow run and download the `SMC-ICT-Signal-Pro-debug-apk` artifact. This creates a test/debug APK, not a production release.
