
package com.example.pesatrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pesatrack.data.model.Transaction
import com.example.pesatrack.viewmodel.TransactionViewModel
import java.util.Date

private val Navy = Color(0xFF101D3A)
private val Gold = Color(0xFFD6A84F)
private val PageBackground = Color(0xFFF4F6FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onTransactionSaved: () -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf("") }
    var typeError by remember { mutableStateOf("") }
    var categoryError by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
            onTransactionSaved()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    val categories = listOf(
        "Food", "Transport", "Shopping", "Education",
        "Entertainment", "Bills", "Salary", "Allowance", "Other"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        OutlinedButton(
            onClick = onTransactionSaved,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Navy
            )
        ) {
            Text("← Back Home")
        }

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = "Add Transaction",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Navy
            )

            Text(
                text = "Keep track of where your money goes.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp)
            ) {

                Text(
                    text = "Transaction Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = ""
                    },
                    label = { Text("Transaction Title") },
                    placeholder = { Text("e.g. Lunch, Rent, Salary") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    isError = titleError.isNotEmpty()
                )

                if (titleError.isNotEmpty()) {
                    Text(
                        text = titleError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        amountError = ""
                    },
                    label = { Text("Amount (KSh)") },
                    placeholder = { Text("Enter amount") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    isError = amountError.isNotEmpty()
                )

                if (amountError.isNotEmpty()) {
                    Text(
                        text = amountError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "Transaction Type",
                    fontWeight = FontWeight.SemiBold,
                    color = Navy
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = type == "Income",
                        onClick = {
                            type = "Income"
                            typeError = ""
                        },
                        label = { Text("Income") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE1F3E8),
                            selectedLabelColor = Color(0xFF237A46)
                        )
                    )

                    FilterChip(
                        selected = type == "Expense",
                        onClick = {
                            type = "Expense"
                            typeError = ""
                        },
                        label = { Text("Expense") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFCE8E8),
                            selectedLabelColor = Color(0xFFB33A3A)
                        )
                    )
                }

                if (typeError.isNotEmpty()) {
                    Text(
                        text = typeError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "Category",
                    fontWeight = FontWeight.SemiBold,
                    color = Navy
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Category") },
                        placeholder = { Text("Choose a category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        isError = categoryError.isNotEmpty()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        categories.forEach { selectedCategory ->
                            DropdownMenuItem(
                                text = { Text(selectedCategory) },
                                onClick = {
                                    category = selectedCategory
                                    categoryError = ""
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (categoryError.isNotEmpty()) {
                    Text(
                        text = categoryError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Description (Optional)")
                    },
                    placeholder = {
                        Text("Add any extra details...")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 3
                )

                Button(
                    onClick = {
                        titleError = ""
                        amountError = ""
                        typeError = ""
                        categoryError = ""

                        val amountValue = amount.trim().toDoubleOrNull()

                        if (title.isBlank()) {
                            titleError = "Please enter a transaction title."
                        }

                        if (amount.isBlank()) {
                            amountError = "Please enter an amount."
                        } else if (
                            amountValue == null ||
                            !amountValue.isFinite() ||
                            amountValue <= 0
                        ) {
                            amountError =
                                "Enter a valid amount greater than zero."
                        }

                        if (type.isBlank()) {
                            typeError = "Please select Income or Expense."
                        }

                        if (category.isBlank()) {
                            categoryError = "Please select a category."
                        }

                        if (
                            titleError.isEmpty() &&
                            amountError.isEmpty() &&
                            typeError.isEmpty() &&
                            categoryError.isEmpty()
                        ) {
                            val transaction = Transaction(
                                title = title.trim(),
                                amount = amountValue!!,
                                category = category,
                                type = type,
                                date = Date().time,
                                description = description.trim()
                            )

                            viewModel.insertTransaction(
                                transaction,
                                context
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Navy
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Transaction",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState
                )
            }
        }
    }
}
