package com.example.navigator.data.repository

import com.example.navigator.data.data_source.Database
import com.example.navigator.data.model.Record
import com.example.navigator.domain.repository.RecordsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecordsImpl @Inject constructor(
    private val database: Database
): RecordsRepository {

    private val dao = database.recordsDao

    override suspend fun insertRecord(record: Record) {
        dao.insertRecord(record)
    }

    override fun getRecords(time: Long, limit: Int): Flow<List<Record>> {
        return dao.getRecords(time, limit)
    }

    override fun getRecords(): Flow<List<Record>> {
        return dao.getRecords()
    }
}