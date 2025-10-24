package com.example.spend.data.room.category

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultCategoryRepository @Inject constructor(
    private val dao: CategoryDao
): CategoryRepository {
    override suspend fun insert(category: Category): Long = dao.insert(category)

    override suspend fun update(category: Category) = dao.update(category)

    override suspend fun delete(category: Category) = dao.delete(category)

    override fun getAllCategories(): Flow<List<Category>> = dao.getAllCategories()

    override fun getAllIncomeCategories(): Flow<List<Category>> = dao.getAllIncomeCategories()

    override fun getAllExpenseCategories(): Flow<List<Category>> = dao.getAllExpenseCategories()

    override fun findCategoryById(id: Long): Flow<Category> = dao.findCategoryById(id)

    override fun findCategoryByNameAndId(name: String, isExpense: Boolean): Flow<Category> = dao.findCategoryByNameAndType(name, if (isExpense) 1 else 0)
}