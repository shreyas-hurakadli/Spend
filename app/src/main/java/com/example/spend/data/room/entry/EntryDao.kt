package com.example.spend.data.room.entry

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.spend.data.dto.CategoryAmount
import com.example.spend.data.dto.EntryCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert
    suspend fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    @Query("SELECT * FROM entries ORDER BY epochSeconds DESC")
    fun getAllEntries(): Flow<List<Entry>>

    @Query("SELECT * FROM entries ORDER BY id DESC LIMIT 4")
    fun getRecentEntries(): Flow<List<Entry>>

    @Query("DELETE FROM entries")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = :tableName")
    suspend fun resetAutoIncrement(tableName: String)

    @Query("SELECT SUM(amount) FROM entries WHERE is_expense = 1 AND epochSeconds >= :from")
    fun getExpense(from: Long): Flow<Double>

    @Query("SELECT SUM(amount) FROM entries WHERE is_expense = 0 AND epochSeconds >= :from")
    fun getIncome(from: Long): Flow<Double>

    @Query(
        value = """
        SELECT e.*, c.name, c.icon, c.color
        FROM entries e, categories c
        WHERE e.account_id = :id AND e.category_id = c.id
        ORDER BY epochSeconds DESC
        """
    )
    fun getEntriesByAccountId(id: Long): Flow<List<EntryCategory>>

    @Query("SELECT c.name AS name, SUM(e.amount) AS totalAmount, c.color AS color FROM categories c, entries e WHERE e.is_expense = 1 AND e.category_id = c.id AND e.epochSeconds >= :from AND e.epochSeconds <= :to GROUP BY c.name")
    fun getExpenseByCategory(
        from: Long,
        to: Long
    ): Flow<List<CategoryAmount>>

    @Query("SELECT c.name AS name, SUM(e.amount) AS totalAmount, c.color AS color FROM categories c, entries e WHERE e.is_expense = 0 AND  e.category_id = c.id AND e.epochSeconds >= :from AND e.epochSeconds <= :to GROUP BY c.name")
    fun getIncomeByCategory(
        from: Long,
        to: Long
    ): Flow<List<CategoryAmount>>

    @Query("SELECT EXISTS (SELECT 1 FROM entries WHERE id IS NOT NULL)")
    fun areEntriesPresent(): Flow<Boolean>

    @Query("SELECT amount FROM entries WHERE is_expense = 1")
    fun getAllExpenseAmount(): Flow<List<Double>>

    @Query("SELECT amount FROM entries WHERE is_expense = 0")
    fun getAllIncomeAmount(): Flow<List<Double>>

    @Query("SELECT epochSeconds as timeStamp, amount FROM entries WHERE is_expense = 1 AND epochSeconds >= :from AND epochSeconds <= :to")
    fun getExpenseByTime(
        from: Long,
        to: Long
    ): Flow<Map<@MapColumn("timeStamp") Long, @MapColumn("amount") Double>>

    @Query("SELECT epochSeconds as timeStamp, amount FROM entries WHERE is_expense = 0 AND epochSeconds >= :from AND epochSeconds <= :to")
    fun getIncomeByTime(
        from: Long,
        to: Long
    ): Flow<Map<@MapColumn("timeStamp") Long, @MapColumn("amount") Double>>

    @Transaction
    @Query(
        """
        SELECT e.*, c.name, c.icon, c.color
        FROM entries e
        INNER JOIN categories c ON e.category_id = c.id
        ORDER BY epochSeconds DESC
        LIMIT :limit
    """
    )
    fun getEntryIconAndColor(limit: Long = Long.MAX_VALUE): Flow<List<EntryCategory>>

    @Query("SELECT SUM(amount) FROM entries WHERE is_expense = 1 AND account_id = :accountId AND epochSeconds >= :startTime AND epochSeconds <= :endTime")
    fun getExpenseByBudgetConstraintsUsingAccount(
        accountId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<Double>

    @Query("SELECT SUM(amount) FROM entries WHERE is_expense = 1 AND category_id = :categoryId AND epochSeconds >= :startTime AND epochSeconds <= :endTime")
    fun getExpenseByBudgetConstraintsUsingCategory(
        categoryId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<Double>

    @Query("SELECT SUM(amount) FROM entries WHERE is_expense = 1 AND epochSeconds >= :startTime AND epochSeconds <= :endTime")
    fun getExpenseByBudgetConstraintsUsingOnlyTime(
        startTime: Long,
        endTime: Long
    ): Flow<Double>

    @Query("SELECT SUM(amount) FROM entries WHERE is_expense = 1 AND account_id = :accountId AND category_id = :categoryId AND epochSeconds >= :startTime AND epochSeconds <= :endTime")
    fun getExpenseByBudgetConstraints(
        accountId: Long,
        categoryId: Long,
        startTime: Long,
        endTime: Long,
    ): Flow<Double>

    @Query(
        "SELECT e.*, c.name, c.icon, c.color " +
                "FROM entries e " +
                "INNER JOIN categories c ON e.category_id = c.id " +
                "WHERE e.is_expense = 1 AND e.account_id = :accountId AND e.epochSeconds >= :startTime AND e.epochSeconds <= :endTime"
    )
    fun getEntriesByBudgetConstraintsUsingAccount(
        accountId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<EntryCategory>>

    @Query(
        "SELECT e.*, c.name, c.icon, c.color " +
                "FROM entries e " +
                "INNER JOIN categories c ON e.category_id = c.id " +
                "WHERE e.is_expense = 1 AND e.category_id = :categoryId AND e.epochSeconds >= :startTime AND e.epochSeconds <= :endTime"
    )
    fun getEntriesByBudgetConstraintsUsingCategory(
        categoryId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<EntryCategory>>

    @Query(
        "SELECT e.*, c.name, c.icon, c.color " +
                "FROM entries e " +
                "INNER JOIN categories c ON e.category_id = c.id " +
                "WHERE e.is_expense = 1 AND e.epochSeconds >= :startTime AND e.epochSeconds <= :endTime"
    )
    fun getEntriesByBudgetConstraintsUsingOnlyTime(
        startTime: Long,
        endTime: Long
    ): Flow<List<EntryCategory>>

    @Query(
        "SELECT e.*, c.name, c.icon, c.color " +
                "FROM entries e " +
                "INNER JOIN categories c ON e.category_id = c.id " +
                "WHERE e.is_expense = 1 AND e.account_id = :accountId AND e.category_id = :categoryId AND e.epochSeconds >= :startTime AND e.epochSeconds <= :endTime"
    )
    fun getEntriesByBudgetConstraints(
        accountId: Long,
        categoryId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<EntryCategory>>
}