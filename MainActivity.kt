package com.example.smcictsignalpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF0B1020)
private val Panel = Color(0xFF151D31)
private val Muted = Color(0xFFB8C4DC)
private val Green = Color(0xFF35D39A)
private val Red = Color(0xFFFF6577)
private val Gold = Color(0xFFFFC857)

private val markets = listOf(
    "ALL",
    "INDIA",
    "CRYPTO",
    "FOREX",
    "METALS"
)

private val timeframes = listOf(
    "All",
    "1m",
    "5m",
    "15m",
    "1h",
    "4h",
    "1D"
)

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
    val direction: String,
    val timeframe: String,
    val reason: String,
    val status: String = "DEMO DATA"
)

@Composable
fun SignalProApp() {

    var selectedMarket by remember {
        mutableStateOf("ALL")
    }

    var selectedTimeframe by remember {
        mutableStateOf("All")
    }

    val signals = remember {
        listOf(
            MarketSignal(
                "NIFTY 50", "INDIA", "WAIT",
                "15m", "Live feed not connected"
            ),
            MarketSignal(
                "BANKNIFTY", "INDIA", "WAIT",
                "5m", "Live feed not connected"
            ),
            MarketSignal(
                "SENSEX", "INDIA", "WAIT",
                "15m", "Live feed not connected"
            ),
            MarketSignal(
                "FINNIFTY", "INDIA", "WAIT",
                "15m", "Live feed not connected"
            ),
            MarketSignal(
                "MIDCPNIFTY", "INDIA", "WAIT",
                "15m", "Live feed not connected"
            ),

            MarketSignal(
                "BTC/USDT", "CRYPTO", "WAIT",
                "5m", "Live analysis not implemented"
            ),
            MarketSignal(
                "ETH/USDT", "CRYPTO", "WAIT",
                "15m", "Live analysis not implemented"
            ),
            MarketSignal(
                "BNB/USDT", "CRYPTO", "WAIT",
                "15m", "Live analysis not implemented"
            ),
            MarketSignal(
                "SOL/USDT", "CRYPTO", "WAIT",
                "15m", "Live analysis not implemented"
            ),
            MarketSignal(
                "XRP/USDT", "CRYPTO", "WAIT",
                "15m", "Live analysis not implemented"
            ),
            MarketSignal(
                "DOGE/USDT", "CRYPTO", "WAIT",
                "15m", "Live analysis not implemented"
            ),

            MarketSignal(
                "EUR/USD", "FOREX", "WAIT",
                "15m", "Live feed not connected"
            ),
            MarketSignal(
                "GBP/USD", "FOREX", "WAIT",
                "1h", "Live feed not connected"
            ),
            MarketSignal(
                "USD/JPY", "FOREX", "WAIT",
                "15m", "Live feed not connected"
            ),

            MarketSignal(
                "GOLD (XAU/USD)", "METALS", "WAIT",
                "15m", "Live feed not connected"
            ),
            MarketSignal(
                "SILVER (XAG/USD)", "METALS", "WAIT",
                "15m", "Live feed not connected"
            )
        )
    }

    val visibleSignals = signals.filter { signal ->
        (selectedMarket == "ALL" ||
            signal.market == selectedMarket) &&
        (selectedTimeframe == "All" ||
            signal.timeframe == selectedTimeframe)
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Background,
            surface = Panel,
            primary = Green,
            secondary = Gold,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "SMC ICT",
                        color = Color.White,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "SIGNAL PRO",
                        color = Green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                }

                Surface(
                    color = Color(0xFF263149),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "DEMO",
                        color = Gold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 9.dp
                        )
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                text = "SELECT MARKET",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                markets.chunked(3).forEach { rowMarkets ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        rowMarkets.forEach { market ->

                            FilterChip(
                                selected =
                                    selectedMarket == market,
                                onClick = {
                                    selectedMarket = market
                                },
                                label = {
                                    Text(
                                        text = market,
                                        fontSize = 11.sp
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        repeat(3 - rowMarkets.size) {
                            Spacer(
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "SELECT TIMEFRAME",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {
                timeframes.forEach { timeframe ->

                    val selected =
                        selectedTimeframe == timeframe

                    Surface(
                        onClick = {
                            selectedTimeframe = timeframe
                        },
                        color = if (selected) {
                            Green
                        } else {
                            Panel
                        },
                        contentColor = if (selected) {
                            Background
                        } else {
                            Color.White
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.padding(
                                vertical = 11.dp,
                                horizontal = 2.dp
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = timeframe,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Panel
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = "SMC + ICT ANALYSIS",
                        color = Green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Market Structure • BOS • CHoCH • " +
                            "Liquidity Sweep • Order Blocks • FVG",
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "DEMO MODE: Real-time analysis is " +
                            "not yet available for all markets.",
                        color = Gold,
                        fontSize = 11.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MARKET SIGNALS",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${visibleSignals.size} markets",
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            if (visibleSignals.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No markets for this timeframe.",
                        color = Muted,
                        fontSize = 14.sp
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    items(visibleSignals) { signal ->
                        SignalCard(signal)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Educational prototype • No orders placed",
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Panel
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = signal.symbol,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "${signal.market} • ${signal.timeframe}",
                        color = Muted,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    color = accent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = signal.direction,
                        color = accent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 8.dp
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = signal.reason,
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {
                InfoTag("Entry: N/A")
                InfoTag("SL: N/A")
                InfoTag("TP: N/A")
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = signal.status,
                color = Gold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun InfoTag(text: String) {

    Surface(
        color = Color(0xFF202B43),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 7.dp
            )
        )
    }
}
