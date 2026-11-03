package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CategoryBudgetProgress
import com.example.ui.FinancialUiState
import com.example.ui.components.CategoryManagerDialog
import com.example.ui.components.SetBudgetDialog
import com.example.ui.components.parseHexColor
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.LuminaGold
import java.util.Locale

@Composable
fun BudgetScreen(
    uiState: FinancialUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetCurrentMonth: () -> Unit,
    onSaveBudget: (categoryName: String, amountCents: Long) -> Unit,
    onDeleteBudget: (Long) -> Unit,
    onAddCategory: (name: String, type: com.example.data.TransactionType, colorHex: String) -> Unit,
    onUpdateCategory: (id: Long, name: String, type: com.example.data.TransactionType, colorHex: String, isDefault: Boolean) -> Unit,
    onDeleteCategory: (com.example.data.CategoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSetBudgetDialog by remember { mutableStateOf(false) }
    var budgetToEdit by remember { mutableStateOf<CategoryBudgetProgress?>(null) }
    var showCategoryManager by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("budget_screen")
    ) {
        // En-tête
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Budgets & Suivi",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Contrôle déterministe de vos plafonds",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Bouton gestion des catégories
            OutlinedButton(
                onClick = { showCategoryManager = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("open_category_manager_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Catégories", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sélecteur de Mois
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPreviousMonth,
                    modifier = Modifier.testTag("prev_month_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Mois précédent")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.budgetMonthLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onNextMonth,
                    modifier = Modifier.testTag("next_month_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Mois suivant")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Synthèse globale du budget
        val global = uiState.globalBudgetSummary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("global_budget_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BUDGET MENSUEL GLOBAL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = when {
                            global.isExceeded -> MaterialTheme.colorScheme.errorContainer
                            global.percentage >= 0.8f -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (global.isExceeded) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = when {
                                    global.isExceeded -> MaterialTheme.colorScheme.onErrorContainer
                                    global.percentage >= 0.8f -> MaterialTheme.colorScheme.onSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when {
                                    global.totalBudgetCeilingCents == 0L -> "Non défini"
                                    global.isExceeded -> "Dépassement !"
                                    global.percentage >= 0.8f -> "Attention"
                                    else -> "Équilibré"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    global.isExceeded -> MaterialTheme.colorScheme.onErrorContainer
                                    global.percentage >= 0.8f -> MaterialTheme.colorScheme.onSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Dépensé : " + com.example.util.CurrencyFormatter.format(global.totalSpentCents, uiState.amountDisplayMode),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Plafond : " + com.example.util.CurrencyFormatter.format(global.totalBudgetCeilingCents, uiState.amountDisplayMode),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        val isExceeded = global.totalRemainingCents < 0
                        Text(
                            text = if (isExceeded) "Dépassement" else "Reste à dépenser",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = com.example.util.CurrencyFormatter.format(
                                amount = global.totalRemainingCents,
                                mode = uiState.amountDisplayMode,
                                withSign = true,
                                isPositive = !isExceeded
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isExceeded) ExpenseRed else IncomeGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Jauge globale
                val progressValue = (global.percentage).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        global.isExceeded -> ExpenseRed
                        global.percentage >= 0.8f -> LuminaGold
                        else -> IncomeGreen
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format(Locale.FRANCE, "%.1f %% consommé", (global.percentage * 100f).coerceAtLeast(0f)),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Titre de section & bouton d'ajout de budget
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progression par Catégorie",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = {
                    budgetToEdit = null
                    showSetBudgetDialog = true
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("add_budget_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Définir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (uiState.categoryBudgetProgress.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aucun budget défini pour ${uiState.budgetMonthLabel}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cliquez sur 'Définir' pour fixer vos plafonds de dépenses mensuels par catégorie.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.categoryBudgetProgress, key = { it.categoryName }) { item ->
                    CategoryBudgetCard(
                        item = item,
                        displayMode = uiState.amountDisplayMode,
                        onEditClick = {
                            budgetToEdit = item
                            showSetBudgetDialog = true
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Dialogue Définir / Modifier Budget
    if (showSetBudgetDialog) {
        SetBudgetDialog(
            monthLabel = uiState.budgetMonthLabel,
            categories = uiState.categories,
            initialCategory = budgetToEdit?.categoryName,
            initialAmountCents = budgetToEdit?.budgetAmountCents,
            existingBudgetId = budgetToEdit?.budgetId,
            onDismissRequest = {
                showSetBudgetDialog = false
                budgetToEdit = null
            },
            onSaveBudget = { cat, amount ->
                onSaveBudget(cat, amount)
            },
            onDeleteBudget = if (budgetToEdit?.budgetId != null) {
                { id -> onDeleteBudget(id) }
            } else null
        )
    }

    // Dialogue Gestion Catégories
    if (showCategoryManager) {
        CategoryManagerDialog(
            categories = uiState.categories,
            onDismissRequest = { showCategoryManager = false },
            onAddCategory = onAddCategory,
            onUpdateCategory = onUpdateCategory,
            onDeleteCategory = onDeleteCategory
        )
    }
}

@Composable
fun CategoryBudgetCard(
    item: CategoryBudgetProgress,
    displayMode: com.example.util.AmountDisplayMode = com.example.util.AmountDisplayMode.STANDARD,
    onEditClick: () -> Unit
) {
    val categoryColor = parseHexColor(item.categoryColorHex)
    val spentFormatted = com.example.util.CurrencyFormatter.format(item.spentAmountCents, displayMode)
    val budgetFormatted = com.example.util.CurrencyFormatter.format(item.budgetAmountCents, displayMode)
    val remainingFormatted = com.example.util.CurrencyFormatter.format(Math.abs(item.remainingCents), displayMode)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("budget_item_${item.categoryName}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // En-tête de la carte
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(categoryColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Badge d'alerte / statut
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            item.budgetAmountCents == 0L -> MaterialTheme.colorScheme.surfaceVariant
                            item.isExceeded -> MaterialTheme.colorScheme.errorContainer
                            item.isNearLimit -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ) {
                        Text(
                            text = when {
                                item.budgetAmountCents == 0L -> "Sans plafond"
                                item.isExceeded -> "Dépassement !"
                                item.isNearLimit -> "Alerte 80%"
                                else -> "Normal"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                item.budgetAmountCents == 0L -> MaterialTheme.colorScheme.onSurfaceVariant
                                item.isExceeded -> MaterialTheme.colorScheme.onErrorContainer
                                item.isNearLimit -> MaterialTheme.colorScheme.onSecondaryContainer
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("edit_budget_${item.categoryName}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier ce budget",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chiffres dépensé vs plafond
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "$spentFormatted / $budgetFormatted",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (item.isExceeded) {
                        "+$remainingFormatted en trop"
                    } else if (item.budgetAmountCents > 0L) {
                        "Reste $remainingFormatted"
                    } else {
                        "Pas de plafond"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.isExceeded) ExpenseRed else IncomeGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barre de progression
            val progressVal = if (item.budgetAmountCents > 0L) item.percentage.coerceIn(0f, 1f) else 1f
            LinearProgressIndicator(
                progress = { progressVal },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when {
                    item.isExceeded -> ExpenseRed
                    item.isNearLimit -> LuminaGold
                    else -> categoryColor
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (item.budgetAmountCents > 0L) {
                        String.format(Locale.FRANCE, "%.1f %% utilisé", item.percentage * 100f)
                    } else {
                        "Dépenses non plafonnées"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
