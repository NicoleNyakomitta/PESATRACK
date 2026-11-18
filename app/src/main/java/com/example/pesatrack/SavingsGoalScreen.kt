package com.example.pesatrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pesatrack.data.model.SavingsGoal
import com.example.pesatrack.viewmodel.SavingsGoalViewModel

private val SavingsNavy = Color(0xFF0B1F49)
private val SavingsGold = Color(0xFFE5B85C)
private val SavingsBackground = Color(0xFFF7F8FC)
private val SavingsGreen = Color(0xFF23845B)

@Composable
fun SavingsGoalScreen(viewModel: SavingsGoalViewModel, onBack: () -> Unit) {
    val goals by viewModel.allGoals.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var goalToDelete by remember { mutableStateOf<SavingsGoal?>(null) }
    var goalToAddAmount by remember { mutableStateOf<SavingsGoal?>(null) }

    val totalTarget = goals.sumOf { it.targetAmount }
    val totalSaved = goals.sumOf { it.savedAmount }

    Column(
        modifier = Modifier.fillMaxSize().background(SavingsBackground).padding(20.dp)
    ) {
        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SavingsNavy)
        ) { Text("← Back Home") }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Savings Goals", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = SavingsNavy)
        Spacer(modifier = Modifier.height(5.dp))
        Text("Set targets, save consistently and watch your progress.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SavingsNavy)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Your Total Savings", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.titleMedium)
                Text("KSh %,.2f".format(totalSaved), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Target: KSh %,.2f".format(totalTarget), color = SavingsGold, style = MaterialTheme.typography.bodyMedium)
                val overallProgress = if (totalTarget > 0) (totalSaved / totalTarget).toFloat().coerceIn(0f, 1f) else 0f
                LinearProgressIndicator(
                    progress = { overallProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = SavingsGold,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                Text("${(overallProgress * 100).toInt()}% of your combined target", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SavingsNavy)
        ) { Text("+ Create Savings Goal", fontWeight = FontWeight.Bold) }

        Spacer(modifier = Modifier.height(20.dp))
        Text("My Goals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SavingsNavy)
        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage != null || successMessage != null) {
            Text(
                text = errorMessage ?: successMessage.orEmpty(),
                color = if (errorMessage != null) MaterialTheme.colorScheme.error else SavingsGreen,
                style = MaterialTheme.typography.bodySmall
            )
            LaunchedEffect(errorMessage, successMessage) { viewModel.clearMessages() }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = SavingsGold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (goals.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Start Your Savings Journey", color = SavingsNavy, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Create your first goal and start working towards something you want.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(items = goals, key = { it.id }) { goal ->
                    val progress = if (goal.targetAmount > 0) (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(goal.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SavingsNavy)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Saved", color = Color.Gray)
                                Text("KSh %,.2f".format(goal.savedAmount), color = SavingsGreen, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Target", color = Color.Gray)
                                Text("KSh %,.2f".format(goal.targetAmount), color = SavingsNavy, fontWeight = FontWeight.SemiBold)
                            }
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(9.dp),
                                color = SavingsGreen,
                                trackColor = Color(0xFFE8ECF2)
                            )
                            Text("%.1f%% completed".format(progress * 100), color = SavingsGreen, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { goalToAddAmount = goal },
                                    enabled = !isLoading,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SavingsNavy)
                                ) { Text("+ Add Savings") }
                                OutlinedButton(
                                    onClick = { goalToDelete = goal },
                                    enabled = !isLoading,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCA4545))
                                ) { Text("Delete") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddSavingsGoalDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, target ->
                viewModel.insertGoal(SavingsGoal(name = name, targetAmount = target))
                showAddDialog = false
            }
        )
    }

    goalToAddAmount?.let { goal ->
        AddSavingsAmountDialog(
            goal = goal,
            onDismiss = { goalToAddAmount = null },
            onConfirm = { amountToAdd ->
                viewModel.updateGoal(goal.copy(savedAmount = goal.savedAmount + amountToAdd))
                goalToAddAmount = null
            }
        )
    }

    goalToDelete?.let { goal ->
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Delete Savings Goal?") },
            text = { Text("Are you sure you want to delete ${goal.name}?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteGoal(goal); goalToDelete = null }) {
                    Text("Delete", color = Color(0xFFCA4545))
                }
            },
            dismissButton = { TextButton(onClick = { goalToDelete = null }) { Text("Cancel") } }
        )
    }
}
@Composable
fun AddSavingsAmountDialog(goal: SavingsGoal, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var amountText by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to ${goal.name}", color = SavingsNavy, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Current saved: KSh %,.2f / KSh %,.2f".format(goal.savedAmount, goal.targetAmount), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; amountError = "" },
                    label = { Text("Amount to Add (KSh)") },
                    placeholder = { Text("e.g. 1500") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (amountError.isNotEmpty()) Text(amountError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = amountText.trim().toDoubleOrNull()
                    if (parsed == null || parsed <= 0.0) amountError = "Enter a valid amount greater than 0" else onConfirm(parsed)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SavingsNavy)
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
@Composable
fun AddSavingsGoalDialog(onDismiss: () -> Unit, onSave: (String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Savings Goal", color = SavingsNavy, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = "" },
                    label = { Text("Goal Name") },
                    placeholder = { Text("e.g. New Laptop") },
                    singleLine = true,
                    isError = nameError.isNotEmpty()
                )
                if (nameError.isNotEmpty()) Text(nameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it; amountError = "" },
                    label = { Text("Target Amount (KSh)") },
                    placeholder = { Text("Enter target amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError.isNotEmpty()
                )
                if (amountError.isNotEmpty()) Text(amountError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedTarget = targetAmount.trim().toDoubleOrNull()
                    when {
                        name.isBlank() -> nameError = "Please enter a goal name"
                        parsedTarget == null || parsedTarget <= 0.0 -> amountError = "Enter a valid target amount"
                        else -> onSave(name.trim(), parsedTarget)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SavingsNavy)
            ) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

