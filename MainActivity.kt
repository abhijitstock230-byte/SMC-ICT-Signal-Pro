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
private val Muted = Color(0xFF9BA8C2)
private val Green = Color(0xFF35D39A)
private val Red = Color(0xFFFF6577)
private val Gold = Color(0xFFFFC857)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SignalProApp() }
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
    val signals = remember {
        listOf(
            MarketSignal("NIFTY 50", "INDIA", "WAIT", "15m", "Waiting for sweep + structure confirmation"),
            MarketSignal("BANKNIFTY", "INDIA", "WAIT", "5m", "No confirmed entry setup"),
            MarketSignal("BTC/USDT", "CRYPTO", "BUY", "5m", "Example only: sweep + bullish shift + FVG"),
            MarketSignal("ETH/USDT", "CRYPTO", "WAIT", "15m", "Waiting for price to return to zone"),
            MarketSignal("EUR/USD", "FOREX", "SELL", "15m", "Example only: buy-side sweep + bearish shift"),
            MarketSignal("GBP/USD", "FOREX", "WAIT", "1h", "Higher-timeframe alignment not confirmed")
        )
    }
    val visible = signals.filter { selectedMarket == "ALL" || it.market == selectedMarket }

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
            modifier = Modifier.fillMaxSize().background(Background).padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("SMC ICT", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("SIGNAL PRO", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Green, letterSpacing = 3.sp)
                }
                Surface(color = Color(0xFF263149), shape = RoundedCornerShape(12.dp)) {
                    Text("DEMO MODE", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("MARKET SCANNER", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("ALL", "INDIA", "CRYPTO", "FOREX").forEach { market ->
                    FilterChip(
                        selected = selectedMarket == market,
                        onClick = { selectedMarket = market },
                        label = { Text(market, fontSize = 11.sp) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Timeframe:", color = Muted, fontSize = 12.sp)
                listOf("All", "1m", "5m", "15m", "1h", "4h", "1D").forEach { tf ->
                    if (selectedTimeframe == tf) {
                        AssistChip(onClick = { selectedTimeframe = tf }, label = { Text(tf) })
                    } else {
                        TextButton(onClick = { selectedTimeframe = tf }, contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)) {
                            Text(tf, color = Muted, fontSize = 11.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text("STRICT SMC + ICT", color = Green, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Higher-timeframe bias • Liquidity sweep • BOS/CHoCH • Order Block/FVG • Entry confirmation",
                        color = Color.White, fontSize = 12.sp, lineHeight = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Live feeds are not connected yet. Signal cards below are illustrative demo examples, not live trading advice.",
                        color = Gold, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("SIGNALS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Text("${visible.size} instruments", color = Muted, fontSize = 12.sp)
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                items(visible.filter { selectedTimeframe == "All" || it.timeframe == selectedTimeframe }) { signal ->
                    SignalCard(signal)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text("Educational prototype • No live prices • No orders are placed", color = Muted, fontSize = 10.sp)
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
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(signal.symbol, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${signal.market}  •  ${signal.timeframe}", color = Muted, fontSize = 11.sp)
                }
                Surface(color = accent.copy(alpha = 0.14f), shape = RoundedCornerShape(10.dp)) {
                    Text(signal.direction, color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(signal.reason, color = Color(0xFFD5DCEE), fontSize = 12.sp, lineHeight = 17.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MiniTag("Entry: pending")
                MiniTag("SL: pending")
                MiniTag("TP: pending")
            }
            Spacer(Modifier.height(8.dp))
            Text(signal.status, color = Gold, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun MiniTag(text: String) {
    Surface(color = Color(0xFF202B43), shape = RoundedCornerShape(8.dp)) {
        Text(text, color = Muted, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
    }
}
