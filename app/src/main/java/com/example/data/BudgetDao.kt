package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets ORDER BY id DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE monthYearKey = :monthYearKey")
    fun getBudgetsForPeriod(monthYearKey: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM budgets WHERE categoryName = :categoryName")
    suspend fun deleteByCategory(categoryName: String)

    @Query("SELECT * FROM budgets WHERE categoryName = :categoryName AND monthYearKey = :monthYearKey LIMIT 1")
    suspend fun getBudgetForCategoryAndPeriod(categoryName: String, monthYearKey: String): BudgetEntity?
}
