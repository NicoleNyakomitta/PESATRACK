
package com.example.pesatrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pesatrack.viewmodel.TransactionViewModel

private val ReportsNavy = Color(0xFF0B1F49)
private val ReportsGold = Color(0xFFE5B85C)
private val ReportsBackground = Color(0xFFF7F8FC)
private val ReportsGreen = Color(0xFF23845B)
private val ReportsRed = Color(0xFFCA4545)

@Composable
fun ReportsScreen(
    viewModel: TransactionViewModel,
    onBack: () -> Unit
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

    val netBalance = totalIncome - totalExpenses

    val incomeCount = transactions.count {
        it.type.equals("Income", ignoreCase = true)
    }

    val expenseCount = transactions.count {
        it.type.equals("Expense", ignoreCase = true)
    }

    val spendingByCategory = transactions
        .filter { it.type.equals("Expense", ignoreCase = true) }
        .groupBy { it.category }
        .mapValues { entry ->
            entry.value.sumOf { it.amount }
        }
        .toList()
        .sortedByDescending { it.second }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ReportsBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ReportsNavy
            )
        ) {
            Text("← Back Home")
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Financial Reports",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ReportsNavy
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Understand your money and see where it goes.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Financial Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ReportsNavy
        )

        Spacer(modifier = Modifier.height(12.dp))

        ReportCard(
            title = "Total Income",
            amount = totalIncome,
            amountColor = ReportsGreen
        )

        ReportCard(
            title = "Total Expenses",
            amount = totalExpenses,
            amountColor = ReportsRed
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = ReportsNavy
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Net Balance",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "KSh %,.2f".format(netBalance),
                    color = if (netBalance >= 0) ReportsGold else Color(0xFFFF8585),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (netBalance >= 0)
                        "You have a positive balance."
                    else
                        "Your expenses are higher than your income.",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Transaction Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ReportsNavy
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                ReportStatistic(
                    label = "Total Transactions",
                    value = transactions.size.toString()
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFEEEEEE)
                )

                ReportStatistic(
                    label = "Income Transactions",
                    value = incomeCount.toString(),
                    valueColor = ReportsGreen
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFEEEEEE)
                )

                ReportStatistic(
                    label = "Expense Transactions",
                    value = expenseCount.toString(),
                    valueColor = ReportsRed
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Spending by Category",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ReportsNavy
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (spendingByCategory.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "No Expense Records Yet",
                        color = ReportsNavy,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Your spending breakdown will appear here once you add expenses.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            spendingByCategory.forEach { (category, amount) ->

                val progress = if (totalExpenses > 0) {
                    (amount / totalExpenses).toFloat().coerceIn(0f, 1f)
                } else {
                    0f
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ReportsNavy
                            )

                            Text(
                                text = "KSh %,.2f".format(amount),
                                color = ReportsNavy,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(9.dp),
                            color = ReportsGreen,
                            trackColor = Color(0xFFE8ECF2)
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = "%.1f%% of total expenses".format(progress * 100),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ReportCard(
    title: String,
    amount: Double,
    amountColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "KSh %,.2f".format(amount),
                style = MaterialTheme.typography.headlineSmall,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ReportStatistic(
    label: String,
    value: String,
    valueColor: Color = ReportsNavy
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
