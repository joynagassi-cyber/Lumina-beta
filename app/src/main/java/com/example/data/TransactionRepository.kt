package com.example.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao,
    private val cashRegisterDao: CashRegisterDao
) {
    // Transactions
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getTransactionsBetween(start: Long, end: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsBetween(start, end)
    }

    suspend fun insert(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun update(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun delete(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteById(id: Long) {
        transactionDao.deleteById(id)
    }

    // Catégories
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>> {
        return categoryDao.getCategoriesByType(type)
    }

    suspend fun insertCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }

    suspend fun deleteCategoryById(id: Long) {
        categoryDao.deleteById(id)
    }

    // Budgets
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    fun getBudgetsForPeriod(monthYearKey: String): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsForPeriod(monthYearKey)
    }

    suspend fun saveBudget(categoryName: String, amountCents: Long, monthYearKey: String) {
        val existing = budgetDao.getBudgetForCategoryAndPeriod(categoryName, monthYearKey)
        if (existing != null) {
            budgetDao.updateBudget(existing.copy(amountCents = amountCents))
        } else {
            budgetDao.insertOrUpdateBudget(
                BudgetEntity(categoryName = categoryName, amountCents = amountCents, monthYearKey = monthYearKey)
            )
        }
    }

    suspend fun deleteBudget(id: Long) {
        budgetDao.deleteById(id)
    }

    suspend fun deleteBudgetByCategory(categoryName: String) {
        budgetDao.deleteByCategory(categoryName)
    }

    // Caisses
    val allCashRegisters: Flow<List<CashRegisterEntity>> = cashRegisterDao.getAllCashRegisters()

    suspend fun insertCashRegister(cashRegister: CashRegisterEntity): Long {
        return cashRegisterDao.insertCashRegister(cashRegister)
    }

    suspend fun updateCashRegister(cashRegister: CashRegisterEntity) {
        cashRegisterDao.updateCashRegister(cashRegister)
    }

    suspend fun deleteCashRegister(cashRegister: CashRegisterEntity) {
        cashRegisterDao.deleteCashRegister(cashRegister)
    }

    suspend fun deleteCashRegisterById(id: Long) {
        cashRegisterDao.deleteById(id)
    }

    suspend fun getCashRegisterById(id: Long): CashRegisterEntity? {
        return cashRegisterDao.getById(id)
    }
}
