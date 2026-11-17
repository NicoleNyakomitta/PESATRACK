
package com.example.pesatrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pesatrack.data.model.Transaction
import com.example.pesatrack.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HistoryNavy = Color(0xFF101D3A)
private val HistoryBackground = Color(0xFFF4F6FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    viewModel: TransactionViewModel,
    onBackHome: () -> Unit
) {
    val transactions by viewModel.allTransactions.collectAsState(
        initial = emptyList()
    )

    var selectedCategory by remember { mutableStateOf("All") }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }

    val categories = listOf(
        "All", "Food", "Transport", "Shopping",
        "Education", "Entertainment", "Bills",
        "Salary", "Allowance", "Other"
    )

    val filteredTransactions = if (selectedCategory == "All") {
        transactions
    } else {
        transactions.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HistoryBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedButton(
            onClick = onBackHome,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = HistoryNavy
            )
        ) {
            Text("← Back Home")
        }

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                "Transaction History",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = HistoryNavy
            )

            Text(
                "View and manage all your transactions.",
                color = Color.Gray
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = HistoryNavy
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Total Transactions",
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    "${transactions.size}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Text(
            "Filter by Category",
            fontWeight = FontWeight.Bold,
            color = HistoryNavy
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var filterExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = filterExpanded,
                onExpandedChange = { filterExpanded = !filterExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = filterExpanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                ExposedDropdownMenu(
                    expanded = filterExpanded,
                    onDismissRequest = { filterExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                filterExpanded = false
                            }
                        )
                    }
                }
            }
        }

        if (filteredTransactions.isEmpty()) {
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
                        .padding(28.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        "No transactions found",
                        fontWeight = FontWeight.Bold,
                        color = HistoryNavy
                    )
                    Text(
                        "Your transactions will appear here.",
                        color = Color.Gray
                    )
                }
            }
        } else {
            filteredTransactions.forEach { transaction ->
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
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    transaction.title,
                                    fontWeight = FontWeight.Bold,
                                    color = HistoryNavy
                                )
                                Text(
                                    transaction.category,
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Text(
                                text = (if (transaction.type == "Income") "+" else "-") +
                                        " KSh ${"%,.2f".format(Locale.getDefault(), transaction.amount)}",
                                color = if (transaction.type == "Income")
                                    Color(0xFF23834A)
                                else
                                    Color(0xFFCA4545),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            SimpleDateFormat(
                                "dd MMM yyyy",
                                Locale.getDefault()
                            ).format(Date(transaction.date)),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )

                        if (transaction.description.isNotBlank()) {
                            Text(
                                transaction.description,
                                color = Color.DarkGray
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { transactionToEdit = transaction }
                            ) {
                                Text("Edit", color = HistoryNavy)
                            }

                            TextButton(
                                onClick = { transactionToDelete = transaction }
                            ) {
                                Text("Delete", color = Color(0xFFCA4545))
                            }
                        }
                    }
                }
            }
        }
    }

    transactionToDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Transaction?") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(transaction)
                        transactionToDelete = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFCA4545))
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    transactionToEdit?.let { transaction ->
        var editTitle by remember(transaction.id) {
            mutableStateOf(transaction.title)
        }
        var editAmount by remember(transaction.id) {
            mutableStateOf(transaction.amount.toString())
        }
        var editDescription by remember(transaction.id) {
            mutableStateOf(transaction.description)
        }

        AlertDialog(
            onDismissRequest = { transactionToEdit = null },
            title = { Text("Edit Transaction") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") }
                    )
                    OutlinedTextField(
                        value = editAmount,
                        onValueChange = { editAmount = it },
                        label = { Text("Amount") }
                    )
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amountValue = editAmount.toDoubleOrNull()
                        if (editTitle.isNotBlank() &&
                            amountValue != null &&
                            amountValue > 0
                        ) {
                            viewModel.updateTransaction(
                                transaction.copy(
                                    title = editTitle.trim(),
                                    amount = amountValue,
                                    description = editDescription.trim()
                                )
                            )
                            transactionToEdit = null
                        }
                    }
                ) {
                    Text("Save", color = HistoryNavy)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
