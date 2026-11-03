package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.CashRegisterSummary
import com.example.ui.FinancialUiState
import com.example.util.CurrencyFormatter

@Composable
fun CaissesScreen(
    uiState: FinancialUiState,
    onSelectCaisseFilter: (Long?) -> Unit,
    onAddCaisse: (name: String, description: String, initialBalance: Long, iconType: String) -> Unit,
    onUpdateCaisse: (id: Long, name: String, description: String, initialBalance: Long, iconType: String, isDefault: Boolean) -> Unit,
    onDeleteCaisse: (Long) -> Unit,
    onTransferBetweenCaisses: (sourceId: Long, targetId: Long, amountCents: Long, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddCaisseDialog by remember { mutableStateOf(false) }
    var caisseToEdit by remember { mutableStateOf<CashRegisterSummary?>(null) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var caisseToDelete by remember { mutableStateOf<CashRegisterSummary?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("caisses_screen")
    ) {
        // En-tête
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Système de Caisses",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Traçabilité intégrale de chaque flux par caisse",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Bouton Virement
                IconButton(
                    onClick = { showTransferDialog = true },
                    modifier = Modifier.testTag("transfer_caisse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Virement inter-caisses",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Bouton Nouvelle Caisse
                IconButton(
                    onClick = {
                        caisseToEdit = null
                        showAddCaisseDialog = true
                    },
                    modifier = Modifier.testTag("new_caisse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nouvelle Caisse",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Carte Solde Total Consolidé (Toutes les Caisses)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                .testTag("total_cash_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRÉSORERIE GLOBALE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${uiState.cashRegisterSummaries.size} caisses actives",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = CurrencyFormatter.format(uiState.totalCashBalanceCents, uiState.amountDisplayMode),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filtre actif de caisse
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Filtre actuel : ${uiState.selectedCashRegisterName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (uiState.selectedCashRegisterId != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(Voir tout)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onSelectCaisseFilter(null) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liste des Caisses individuelles
        Text(
            text = "Détail des Caisses & Relevés",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.cashRegisterSummaries, key = { it.id }) { caisse ->
                val isSelected = uiState.selectedCashRegisterId == caisse.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .testTag("caisse_item_${caisse.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (caisse.iconType) {
                                            "mobile" -> Icons.Default.PhoneAndroid
                                            "bank" -> Icons.Default.AccountBalance
                                            "vault" -> Icons.Default.Savings
                                            else -> Icons.Default.Payments
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = caisse.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (caisse.isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = "Par défaut",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    if (caisse.description.isNotBlank()) {
                                        Text(
                                            text = caisse.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        caisseToEdit = caisse
                                        showAddCaisseDialog = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (!caisse.isDefault) {
                                    IconButton(
                                        onClick = { caisseToDelete = caisse },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Solde de la caisse
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Solde disponible :",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyFormatter.format(caisse.currentBalanceCents, uiState.amountDisplayMode),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Button(
                                onClick = {
                                    if (isSelected) onSelectCaisseFilter(null) else onSelectCaisseFilter(caisse.id)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(if (isSelected) "Filtré ✓" else "Isoler", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Récapitulatif Entrées / Sorties relevées de cette caisse
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Entrées : +${CurrencyFormatter.format(caisse.totalIncomeCents, uiState.amountDisplayMode)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Sorties : -${CurrencyFormatter.format(caisse.totalExpenseCents, uiState.amountDisplayMode)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${caisse.transactionCount} op.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Dialogue Créer / Modifier Caisse
    if (showAddCaisseDialog) {
        AddEditCaisseDialog(
            initialCaisse = caisseToEdit,
            onDismissRequest = {
                showAddCaisseDialog = false
                caisseToEdit = null
            },
            onSave = { name, desc, initialBal, icon ->
                if (caisseToEdit != null) {
                    onUpdateCaisse(caisseToEdit!!.id, name, desc, initialBal, icon, caisseToEdit!!.isDefault)
                } else {
                    onAddCaisse(name, desc, initialBal, icon)
                }
            }
        )
    }

    // Dialogue Virement Inter-Caisses
    if (showTransferDialog) {
        TransferCaisseDialog(
            caisses = uiState.cashRegisterSummaries,
            onDismissRequest = { showTransferDialog = false },
            onTransfer = { sourceId, targetId, amount, note ->
                onTransferBetweenCaisses(sourceId, targetId, amount, note)
            }
        )
    }

    // Confirmation de suppression
    if (caisseToDelete != null) {
        val reg = caisseToDelete!!
        AlertDialog(
            onDismissRequest = { caisseToDelete = null },
            title = { Text("Supprimer la caisse ?") },
            text = { Text("Voulez-vous supprimer la caisse \"${reg.name}\" (${CurrencyFormatter.format(reg.currentBalanceCents)}) ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCaisse(reg.id)
                        caisseToDelete = null
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { caisseToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun AddEditCaisseDialog(
    initialCaisse: CashRegisterSummary?,
    onDismissRequest: () -> Unit,
    onSave: (name: String, description: String, initialBalance: Long, iconType: String) -> Unit
) {
    var name by remember { mutableStateOf(initialCaisse?.name ?: "") }
    var description by remember { mutableStateOf(initialCaisse?.description ?: "") }
    var initialBalanceText by remember {
        mutableStateOf(if (initialCaisse != null && initialCaisse.initialBalanceCents > 0) initialCaisse.initialBalanceCents.toString() else "")
    }
    var selectedIcon by remember { mutableStateOf(initialCaisse?.iconType ?: "cash") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (initialCaisse != null) "Modifier la Caisse" else "Nouvelle Caisse",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
                    label = { Text("Nom de la caisse") },
                    placeholder = { Text("ex: Caisse Espèces, Wave, BOA...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("caisse_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = initialBalanceText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            initialBalanceText = input
                            error = null
                        }
                    },
                    label = { Text("Solde initial (FCFA)") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("caisse_initial_balance_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Usage") },
                    placeholder = { Text("ex: Pour dépenses quotidiennes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Icône :",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("cash" to "Espèces", "mobile" to "Mobile", "bank" to "Banque", "vault" to "Coffre").forEach { (type, label) ->
                        FilterChip(
                            selected = selectedIcon == type,
                            onClick = { selectedIcon = type },
                            label = { Text(label) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) { Text("Annuler") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                error = "Veuillez renseigner un nom pour la caisse"
                                return@Button
                            }
                            val initialBal = initialBalanceText.toLongOrNull() ?: 0L
                            onSave(name, description, initialBal, selectedIcon)
                            onDismissRequest()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_caisse_button")
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferCaisseDialog(
    caisses: List<CashRegisterSummary>,
    onDismissRequest: () -> Unit,
    onTransfer: (sourceId: Long, targetId: Long, amountCents: Long, note: String) -> Unit
) {
    var sourceId by remember { mutableStateOf(caisses.firstOrNull()?.id ?: 1L) }
    var targetId by remember { mutableStateOf(caisses.getOrNull(1)?.id ?: caisses.firstOrNull()?.id ?: 1L) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Virement Inter-Caisses",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Transfert transparent et déterministe entre deux caisses",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Caisse Source (Débit) :", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    caisses.forEach { caisse ->
                        FilterChip(
                            selected = sourceId == caisse.id,
                            onClick = { sourceId = caisse.id },
                            label = { Text(caisse.name, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Caisse Destinataire (Crédit) :", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    caisses.forEach { caisse ->
                        FilterChip(
                            selected = targetId == caisse.id,
                            onClick = { targetId = caisse.id },
                            label = { Text(caisse.name, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            amountText = input
                            error = null
                        }
                    },
                    label = { Text("Montant à transférer (FCFA)") },
                    placeholder = { Text("ex: 50000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transfer_amount_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Motif / Note du virement") },
                    placeholder = { Text("ex: Approvisionnement espèces, Dépôt Wave...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) { Text("Annuler") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (sourceId == targetId) {
                                error = "La caisse source et la caisse cible doivent être différentes"
                                return@Button
                            }
                            val amount = amountText.toLongOrNull()
                            if (amount == null || amount <= 0L) {
                                error = "Veuillez entrer un montant valide supérieur à 0"
                                return@Button
                            }
                            onTransfer(sourceId, targetId, amount, note)
                            onDismissRequest()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("confirm_transfer_button")
                    ) {
                        Text("Transférer")
                    }
                }
            }
        }
    }
}
