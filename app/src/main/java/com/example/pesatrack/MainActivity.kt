
package com.example.pesatrack

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pesatrack.data.local.AppDatabase
import com.example.pesatrack.data.repository.TransactionRepository
import com.example.pesatrack.data.repository.SavingsGoalRepository
import com.example.pesatrack.data.repository.UserRepository
import com.example.pesatrack.viewmodel.UserViewModel
import com.example.pesatrack.viewmodel.UserViewModelFactory
import com.example.pesatrack.ui.theme.PesaTrackTheme
import com.example.pesatrack.viewmodel.TransactionViewModel
import com.example.pesatrack.viewmodel.TransactionViewModelFactory
import com.example.pesatrack.viewmodel.SavingsGoalViewModel
import com.example.pesatrack.viewmodel.SavingsGoalViewModelFactory

private val Navy = Color(0xFF0B1F49)
private val PageBackground = Color(0xFFF7F8FC)
private val Gold = Color(0xFFE5B85C)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission on Android 13 and above
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        val database = AppDatabase.getDatabase(applicationContext)

        val repository = TransactionRepository(database.transactionDao())
        val factory = TransactionViewModelFactory(repository)

        val transactionViewModel = ViewModelProvider(
            this,
            factory
        )[TransactionViewModel::class.java]

        val savingsGoalRepository = SavingsGoalRepository(
            database.savingsGoalDao()
        )

        val savingsGoalFactory = SavingsGoalViewModelFactory(
            savingsGoalRepository
        )

        val savingsGoalViewModel = ViewModelProvider(
            this,
            savingsGoalFactory
        )[SavingsGoalViewModel::class.java]

        val userRepository = UserRepository(database.userDao())
        val userFactory = UserViewModelFactory(userRepository)

        val userViewModel = ViewModelProvider(
            this,
            userFactory
        )[UserViewModel::class.java]

        setContent {
            PesaTrackTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {

                    // LOGIN SCREEN
                    composable("login") {
                        LoginScreen(
                            viewModel = userViewModel,
                            onSignIn = { userAccount ->
                                navController.navigate("dashboard") {
                                    popUpTo("login") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    // DASHBOARD
                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = transactionViewModel,
                            onAddTransaction = {
                                navController.navigate("add_transaction")
                            },
                            onViewHistory = {
                                navController.navigate("transaction_history")
                            },
                            onViewSavingsGoals = {
                                navController.navigate("savings_goals")
                            },
                            onViewReports = {
                                navController.navigate("reports")
                            }
                        )
                    }

                    // ADD TRANSACTION
                    composable("add_transaction") {
                        AddTransactionScreen(
                            viewModel = transactionViewModel,
                            onTransactionSaved = {
                                navController.navigate("dashboard") {
                                    popUpTo("dashboard") {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    // TRANSACTION HISTORY
                    composable("transaction_history") {
                        TransactionHistoryScreen(
                            viewModel = transactionViewModel,
                            onBackHome = {
                                navController.navigate("dashboard") {
                                    popUpTo("dashboard") {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    // SAVINGS GOALS
                    composable("savings_goals") {
                        SavingsGoalScreen(
                            viewModel = savingsGoalViewModel,
                            onBack = {
                                navController.navigate("dashboard") {
                                    popUpTo("dashboard") {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    // FINANCIAL REPORTS
                    composable("reports") {
                        ReportsScreen(
                            viewModel = transactionViewModel,
                            onBack = {
                                navController.navigate("dashboard") {
                                    popUpTo("dashboard") {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    viewModel: TransactionViewModel,
    onAddTransaction: () -> Unit,
    onViewHistory: () -> Unit,
    onViewSavingsGoals: () -> Unit,
    onViewReports: () -> Unit
) {
    val transactions by viewModel.allTransactions.collectAsState(
        initial = emptyList()
    )

    val totalIncome = transactions
        .filter { it.type.equals("Income", ignoreCase = true) }
        .sumOf { it.amount }

    val totalExpenses = transactions
        .filter { it.type.equals("Expense", ignoreCase = true) }
        .sumOf { it.amount }

    val balance = totalIncome - totalExpenses

    val spendingByCategory = transactions
        .filter { it.type.equals("Expense", ignoreCase = true) }
        .groupBy { it.category }
        .mapValues { entry ->
            entry.value.sumOf { it.amount }
        }
        .toList()
        .sortedByDescending { it.second }

    val transactionCount = transactions.size

    Scaffold(
        containerColor = PageBackground,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("⌂") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onAddTransaction,
                    icon = { Text("+") },
                    label = { Text("Add") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onViewHistory,
                    icon = { Text("▤") },
                    label = { Text("History") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onViewSavingsGoals,
                    icon = { Text("◎") },
                    label = { Text("Goals") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onViewReports,
                    icon = { Text("▥") },
                    label = { Text("Reports") }
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PageBackground)
                .verticalScroll(rememberScrollState())
        ) {

            // HEADER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy)
                    .padding(horizontal = 22.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "PesaTrack",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily(
                            Font(
                                resId = R.font.playfair_display,
                                weight = FontWeight.Bold
                            )
                        ),
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Track • Save • Grow",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {

                // AVAILABLE BALANCE
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Navy
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {
                        Text(
                            text = "Available Balance",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "KSh %,.2f".format(balance),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$transactionCount transactions recorded",
                            color = Gold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // INCOME AND EXPENSES
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Income",
                        amount = totalIncome,
                        amountColor = Color(0xFF23845B),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "Expenses",
                        amount = totalExpenses,
                        amountColor = Color(0xFFD14B4B),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // SPENDING BREAKDOWN
                Text(
                    text = "Spending Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    if (spendingByCategory.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No expenses recorded yet.")
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SpendingDonutChart(
                                spendingByCategory = spendingByCategory,
                                modifier = Modifier.size(145.dp)
                            )

                            Spacer(modifier = Modifier.width(18.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                spendingByCategory
                                    .take(5)
                                    .forEachIndexed { index, item ->

                                        val percentage =
                                            if (totalExpenses > 0) {
                                                item.second / totalExpenses * 100
                                            } else {
                                                0.0
                                            }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(9.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(
                                                        chartColors[index % chartColors.size]
                                                    )
                                            )

                                            Spacer(
                                                modifier = Modifier.width(7.dp)
                                            )

                                            Column {
                                                Text(
                                                    text = item.first,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium
                                                )

                                                Text(
                                                    text = "%.1f%%".format(percentage),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // FEATURE CARDS
                Text(
                    text = "Manage Your Money",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardFeatureCard(
                        title = "Add Transaction",
                        imageRes = R.drawable.wallet_transaction,
                        modifier = Modifier.weight(1f),
                        onClick = onAddTransaction
                    )

                    DashboardFeatureCard(
                        title = "Transaction History",
                        imageRes = R.drawable.receipt_history,
                        modifier = Modifier.weight(1f),
                        onClick = onViewHistory
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardFeatureCard(
                        title = "Savings Goals",
                        imageRes = R.drawable.piggy_bank_savings,
                        modifier = Modifier.weight(1f),
                        onClick = onViewSavingsGoals
                    )

                    DashboardFeatureCard(
                        title = "Financial Reports",
                        imageRes = R.drawable.laptop_reports,
                        modifier = Modifier.weight(1f),
                        onClick = onViewReports
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // RECENT TRANSACTIONS
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (transactions.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transactions yet. Start tracking your spending!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        transactions.take(5).forEach { transaction ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(15.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = transaction.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Text(
                                            text = transaction.category,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }

                                    Text(
                                        text = "${
                                            if (transaction.type.equals("Income", true)) "+"
                                            else "-"
                                        } KSh %,.2f".format(transaction.amount),
                                        color = if (
                                            transaction.type.equals("Income", true)
                                        ) {
                                            Color(0xFF23845B)
                                        } else {
                                            Color(0xFFD14B4B)
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "KSh %,.2f".format(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Composable
fun DashboardFeatureCard(
    title: String,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Navy
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(Navy)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private val chartColors = listOf(
    Color(0xFF0B1F49),
    Color(0xFF6C63A8),
    Color(0xFFB49AD6),
    Color(0xFFE5B85C),
    Color(0xFF6C9A91)
)

@Composable
fun SpendingDonutChart(
    spendingByCategory: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val total = spendingByCategory.sumOf { it.second }
        val strokeWidth = 25.dp.toPx()

        if (total <= 0.0) {
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
        } else {
            var startAngle = -90f

            spendingByCategory.forEachIndexed { index, item ->
                val sweepAngle = (item.second / total * 360).toFloat()

                drawArc(
                    color = chartColors[index % chartColors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Butt
                    )
                )

                startAngle += sweepAngle
            }
        }
    }
}
