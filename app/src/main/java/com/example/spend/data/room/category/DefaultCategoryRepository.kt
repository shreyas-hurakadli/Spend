package com.example.spend.data.room.category

import javax.inject.Inject

class DefaultCategoryRepository @Inject constructor(
    private val dao: CategoryDao
): CategoryRepository {
    override suspend fun insert(category: Category): Long = dao.insert(category)

    override suspend fun update(category: Category) = dao.update(category)

    override suspend fun delete(category: Category) = dao.delete(category)
}