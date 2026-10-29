package com.example.smcictsignalpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.math.abs

private val Background = Color(0xFF0B1020)
private val Panel = Color(0xFF151D31)
private val Muted = Color(0xFFB8C4DC)
private val Green = Color(0xFF35D39A)
private val Red = Color(0xFFFF6577)
private val Gold = Color(0xFFFFC857)
private val White = Color(0xFFFFFFFF)
private val DarkText = Color(0xFF0B1020)
private val ChipBackground = Color(0xFF263149)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignalProApp()
        }
    }
}

data class MarketSignal(
    val symbol: String,
    val market: String,
    val direction: String = "WAIT",
    val timeframe: String = "15m",
    val reason: String = "Select GET SIGNAL to analyse",
    val status: String = "NOT ANALYSED",
    val entry: Double? = null,
    val stopLoss: Double? = null,
    val target1: Double? = null,
    val target2: Double? = null,
    val target3: Double? = null,
    val live: Boolean = false
)

private data class Candle(
    val high: Double,
    val low: Double,
    val close: Double
)

private fun price(value: Double?): String {
    if (value == null || !value.isFinite()) return "N/A"

    return String.format(
        Locale.US,
        "%.6f",
        value
    ).trimEnd('0').trimEnd('.')
}

private fun average(
    values: List<Double>,
    period: Int
): Double {
    if (values.isEmpty()) return 0.0

    val selected = values.takeLast(period)

    return selected.average()
}

private fun intervalFor(timeframe: String): String {
    return when (timeframe) {
        "1m" -> "1m"
        "5m" -> "5m"
        "15m" -> "15m"
        "1h" -> "1h"
        "4h" -> "4h"
        "1D" -> "1d"
        else -> "15m"
    }
}

private fun binanceSymbol(symbol: String): String? {
    return when (symbol) {
        "BTC/USDT" -> "BTCUSDT"
        "ETH/USDT" -> "ETHUSDT"
        "BNB/USDT" -> "BNBUSDT"
        "SOL/USDT" -> "SOLUSDT"
        "XRP/USDT" -> "XRPUSDT"
        "DOGE/USDT" -> "DOGEUSDT"
        else -> null
    }
}

private fun fetchCandles(
    symbol: String,
    timeframe: String
): List<Candle> {

    val interval = intervalFor(timeframe)

    val endpoint =
        "https://api.binance.com/api/v3/klines" +
        "?symbol=$symbol" +
        "&interval=$interval" +
        "&limit=100"

    val connection =
        URL(endpoint).openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        val responseCode = connection.responseCode

        if (responseCode !in 200..299) {
            throw Exception("Binance HTTP error: $responseCode")
        }

        val response = connection.inputStream
            .bufferedReader()
            .use { it.readText() }

        val json = JSONArray(response)
        val candles = mutableListOf<Candle>()

        for (i in 0 until json.length()) {
            val row = json.getJSONArray(i)

            candles.add(
                Candle(
                    high = row.getString(2).toDouble(),
                    low = row.getString(3).toDouble(),
                    close = row.getString(4).toDouble()
                )
            )
        }

        if (candles.size < 25) {
            throw Exception("Not enough candle data")
        }

        return candles

    } finally {
        connection.disconnect()
    }
}

private fun analyseMarket(
    signal: MarketSignal,
    timeframe: String
): MarketSignal {

    val apiSymbol = binanceSymbol(signal.symbol)

    if (apiSymbol == null) {
        return signal.copy(
            direction = "WAIT",
            timeframe = timeframe,
            reason = "Live data provider is not connected for this market.",
            status = "LIVE DATA UNAVAILABLE",
            entry = null,
            stopLoss = null,
            target1 = null,
            target2 = null,
            target3 = null,
            live = false
        )
    }

    return try {
        val candles = fetchCandles(apiSymbol, timeframe)

        val closes = candles.map { it.close }

        val last = candles.last()
        val previous = candles[candles.lastIndex - 1]

        val emaFast = average(closes, 9)
        val emaSlow = average(closes, 21)

        val recent = candles.takeLast(10)

        val recentHigh = recent.maxOf { it.high }
        val recentLow = recent.minOf { it.low }

        val direction = when {
            emaFast > emaSlow && last.close > previous.close -> "BUY"
            emaFast < emaSlow && last.close < previous.close -> "SELL"
            else -> "WAIT"
        }

        val entry = last.close

        val sl: Double?
        val t1: Double?
        val t2: Double?
        val t3: Double?

        if (direction == "BUY") {

            sl = recentLow

            val risk = entry - sl

            if (risk <= 0.0) {
                return signal.copy(
                    direction = "WAIT",
                    timeframe = timeframe,
                    reason = "Invalid risk distance; no trade setup.",
                    status = "LIVE DATA • NO TRADE",
                    entry = entry,
                    live = true
                )
            }

            t1 = entry + risk
            t2 = entry + risk * 2
            t3 = entry + risk * 3

        } else if (direction == "SELL") {

            sl = recentHigh

            val risk = sl - entry

            if (risk <= 0.0) {
                return signal.copy(
                    direction = "WAIT",
                    timeframe = timeframe,
                    reason = "Invalid risk distance; no trade setup.",
                    status = "LIVE DATA • NO TRADE",
                    entry = entry,
                    live = true
                )
            }

            t1 = entry - risk
            t2 = entry - risk * 2
            t3 = entry - risk * 3

        } else {

            sl = null
            t1 = null
            t2 = null
            t3 = null
        }

        val reason = when (direction) {
            "BUY" ->
                "Fast trend average is above slow average and the latest close is rising. Basic trend signal; not full SMC/ICT confirmation."

            "SELL" ->
                "Fast trend average is below slow average and the latest close is falling. Basic trend signal; not full SMC/ICT confirmation."

            else ->
                "Trend confirmation is mixed. Wait for a clearer setup."
        }

        signal.copy(
            direction = direction,
            timeframe = timeframe,
            reason = reason,
            status = "LIVE BINANCE DATA • BASIC TREND",
            entry = entry,
            stopLoss = sl,
            target1 = t1,
            target2 = t2,
            target3 = t3,
            live = true
        )

    } catch (e: Exception) {

        signal.copy(
            direction = "WAIT",
            timeframe = timeframe,
            reason = "Unable to load live candles. Check internet or retry.",
            status = "LIVE DATA ERROR",
            entry = null,
            stopLoss = null,
            target1 = null,
            target2 = null,
            target3 = null,
            live = false
        )
    }
}

@Composable
fun SignalProApp() {

    var selectedMarket by remember {
        mutableStateOf("ALL")
    }

    var selectedTimeframe by remember {
        mutableStateOf("15m")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("Select a market and tap GET SIGNAL.")
    }

    val markets = listOf(
        "ALL",
        "INDIA",
        "CRYPTO",
        "FOREX",
        "METALS"
    )

    val timeframes = listOf(
        "All",
        "1m",
        "5m",
        "15m",
        "1h",
        "4h",
        "1D"
    )

    val initialSignals = listOf(
        MarketSignal("NIFTY 50", "INDIA"),
        MarketSignal("BANKNIFTY", "INDIA"),
        MarketSignal("SENSEX", "INDIA"),
        MarketSignal("FINNIFTY", "INDIA"),
        MarketSignal("MIDCPNIFTY", "INDIA"),
        MarketSignal("BTC/USDT", "CRYPTO"),
        MarketSignal("ETH/USDT", "CRYPTO"),
        MarketSignal("BNB/USDT", "CRYPTO"),
        MarketSignal("SOL/USDT", "CRYPTO"),
        MarketSignal("XRP/USDT", "CRYPTO"),
        MarketSignal("DOGE/USDT", "CRYPTO"),
        MarketSignal("EUR/USD", "FOREX"),
        MarketSignal("GBP/USD", "FOREX"),
        MarketSignal("USD/JPY", "FOREX"),
        MarketSignal("GOLD (XAU/USD)", "METALS"),
        MarketSignal("SILVER (XAG/USD)", "METALS")
    )

    var signals by remember {
        mutableStateOf(initialSignals)
    }

    val visibleSignals = signals.filter { signal ->
        (selectedMarket == "ALL" ||
            signal.market == selectedMarket) &&
        (selectedTimeframe == "All" ||
            signal.timeframe == selectedTimeframe)
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Green,
            onPrimary = DarkText,
            secondary = Gold,
            onSecondary = DarkText,
            background = Background,
            onBackground = White,
            surface = Panel,
            onSurface = White,
            surfaceVariant = ChipBackground,
            onSurfaceVariant = White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "SMC ICT",
                        color = White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "SIGNAL PRO",
                        color = Green,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                }

                Surface(
                    color = ChipBackground,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (loading) "SCANNING" else "SIGNAL SCANNER",
                        color = Gold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "MARKET SCANNER",
                color = White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                markets.forEach { market ->

                    val selected = selectedMarket == market

                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedMarket = market
                        },
                        label = {
                            Text(
                                text = market,
                                color = White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green,
                            selectedLabelColor = White,
                            containerColor = Panel,
                            labelColor = White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = Muted,
                            selectedBorderColor = Green
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "TIMEFRAME",
                color = White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                timeframes.forEach { timeframe ->

                    val selected = selectedTimeframe == timeframe

                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedTimeframe = timeframe
                        },
                        label = {
                            Text(
                                text = timeframe,
                                color = White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green,
                            selectedLabelColor = White,
                            containerColor = Panel,
                            labelColor = White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = Muted,
                            selectedBorderColor = Green
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Panel
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = "SMC + ICT SIGNAL SCANNER",
                        color = Green,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Liquidity Sweep • BOS/CHoCH • Order Block • FVG",
                        color = White,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Crypto uses Binance candle data. Other markets require their own live data providers. Signals use a basic trend rule, not complete SMC/ICT analysis.",
                        color = Gold,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (!loading) {
                        loading = true
                        message = "Fetching market data..."

                        val targetMarket = selectedMarket
                        val targetTimeframe = selectedTimeframe

                        Thread {
                            val selected = initialSignals.filter {
                                targetMarket == "ALL" ||
                                    it.market == targetMarket
                            }

                            val results = selected.map { signal ->
                                analyseMarket(signal, targetTimeframe)
                            }

                            runOnUiThread {
                                signals = signals.map { old ->
                                    results.find {
                                        it.symbol == old.symbol
                                    } ?: old
                                }

                                loading = false
                                message =
                                    "Scan complete. Check each card's data status."
                            }
                        }.start()
                    }
                },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    contentColor = DarkText,
                    disabledContainerColor = Muted
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (loading) "SCANNING..." else "GET SIGNAL",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = message,
                color = Muted,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MARKET SIGNALS",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${visibleSignals.size} instruments",
                    color = White,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visibleSignals, key = { it.symbol }) { signal ->
                    SignalCard(signal)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Educational tool • Verify signals before trading • No orders placed",
                color = Muted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun SignalCard(signal: MarketSignal) {

    val accent = when (signal.direction) {
        "BUY" -> Green
        "SELL" -> Red
        else -> Gold
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Panel
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = signal.symbol,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "${signal.market} • ${signal.timeframe}",
                        color = Muted,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    color = accent.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = signal.direction,
                        color = accent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            PriceLine("Entry Price", signal.entry, White)
            PriceLine("Stop Loss", signal.stopLoss, Red)
            PriceLine("Target 1", signal.target1, Green)
            PriceLine("Target 2", signal.target2, Green)
            PriceLine("Target 3", signal.target3, Green)

            Spacer(Modifier.height(12.dp))

            Text(
                text = signal.reason,
                color = White,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(10.dp))

            Surface(
                color = if (signal.live) {
                    Green.copy(alpha = 0.12f)
                } else {
                    Gold.copy(alpha = 0.12f)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = signal.status,
                    color = if (signal.live) Green else Gold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 7.dp
                    )
                )
            }
        }
    }
}

@Composable
fun PriceLine(
    label: String,
    value: Double?,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = price(value),
            color = valueColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
