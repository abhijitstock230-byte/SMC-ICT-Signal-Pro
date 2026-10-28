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

private val Background = Color(0xFF0B1020)
private val Panel = Color(0xFF151D31)
private val Muted = Color(0xFFB8C4DC)
private val Green = Color(0xFF35D39A)
private val Red = Color(0xFFFF6577)
private val Gold = Color(0xFFFFC857)
private val White = Color(0xFFFFFFFF)
private val DarkText = Color(0xFF0B1020)

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

    var selectedMarket by remember { mutableStateOf("ALL") }
    var selectedTimeframe by remember { mutableStateOf("All") }

    val markets = listOf(
        "ALL",
        "INDIA",
        "CRYPTO",
        "FOREX",
        "METALS"
    )

    val timeframes = listOf(
        "All", "1m", "5m", "15m", "1h", "4h", "1D"
    )

    val signals = remember {
        listOf(
            MarketSignal(
                "NIFTY 50", "INDIA", "WAIT", "15m",
                "Waiting for sweep and structure confirmation"
            ),
            MarketSignal(
                "BANKNIFTY", "INDIA", "WAIT", "5m",
                "No confirmed entry setup"
            ),
            MarketSignal(
                "SENSEX", "INDIA", "WAIT", "15m",
                "Waiting for market structure confirmation"
            ),
            MarketSignal(
                "FINNIFTY", "INDIA", "WAIT", "1h",
                "Higher timeframe confirmation pending"
            ),
            MarketSignal(
                "MIDCPNIFTY", "INDIA", "WAIT", "5m",
                "Demo analysis only"
            ),
            MarketSignal(
                "BTC/USDT", "CRYPTO", "WAIT", "5m",
                "Live feed not connected"
            ),
            MarketSignal(
                "ETH/USDT", "CRYPTO", "WAIT", "15m",
                "Live analysis not implemented"
            ),
            MarketSignal(
                "BNB/USDT", "CRYPTO", "WAIT", "1h",
                "Demo data only"
            ),
            MarketSignal(
                "SOL/USDT", "CRYPTO", "WAIT", "5m",
                "Waiting for structure confirmation"
            ),
            MarketSignal(
                "XRP/USDT", "CRYPTO", "WAIT", "15m",
                "Demo data only"
            ),
            MarketSignal(
                "DOGE/USDT", "CRYPTO", "WAIT", "1h",
                "Live feed not connected"
            ),
            MarketSignal(
                "EUR/USD", "FOREX", "WAIT", "15m",
                "Live feed not connected"
            ),
            MarketSignal(
                "GBP/USD", "FOREX", "WAIT", "1h",
                "Live analysis not implemented"
            ),
            MarketSignal(
                "USD/JPY", "FOREX", "WAIT", "4h",
                "Demo data only"
            ),
            MarketSignal(
                "GOLD (XAU/USD)", "METALS", "WAIT", "15m",
                "Live feed not connected"
            ),
            MarketSignal(
                "SILVER (XAG/USD)", "METALS", "WAIT", "1h",
                "Demo data only"
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
            primary = Green,
            onPrimary = whiteText,
            secondary = Gold,
            onSecondary = whiteText,
            background = Background,
            onBackground = White,
            surface = Panel,
            onSurface = White,
            surfaceVariant = Color(0xFF202B43),
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
                    color = Color(0xFF263149),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "DEMO MODE",
                        color = Gold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 8.dp
                        )
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
                                color = if (selected) {
                                    DarkText
                                } else {
                                    White
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green,
                            selectedLabelColor = WhiteText,
                            containerColor = Panel,
                            labelColor = White,
                            selectedLeadingIconColor = DarkText,
                            leadingIconColor = White,
                            disabledContainerColor = Panel,
                            disabledLabelColor = White
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
                                color = if (selected) {
                                    DarkText
                                } else {
                                    White
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green,
                            selectedLabelColor = WhiteText,
                            containerColor = Panel,
                            labelColor = White,
                            disabledContainerColor = Panel,
                            disabledLabelColor = White
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
                        text = "STRICT SMC + ICT",
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
                        text = "Live market feeds and actual SMC/ICT signal analysis are not connected yet. The cards below show demo status only.",
                        color = Gold,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

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
                items(visibleSignals) { signal ->
                    SignalCard(signal)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Educational prototype • Demo data • No orders placed",
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
                    color = accent.copy(alpha = 0.14f),
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

            Spacer(Modifier.height(10.dp))

            Text(
                text = signal.reason,
                color = White,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniTag("Entry: pending")
                MiniTag("SL: pending")
                MiniTag("TP: pending")
            }

            Spacer(Modifier.height(8.dp))

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
fun MiniTag(text: String) {
    Surface(
        color = Color(0xFF202B43),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = White,
            fontSize = 10.sp,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 6.dp
            )
        )
    }
}
