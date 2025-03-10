package com.example.navigator.data.data_source

import androidx.room.*
import com.example.navigator.data.model.Record
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordsDao {

    @Query("SELECT * FROM record")
    fun getRecords(): Flow<List<Record>>

    @Query("SELECT * FROM record " +
            "WHERE time < :time  " +
            "GROUP BY `end`" +
            "ORDER BY time DESC " +
            "LIMIT :limit")
    fun getRecords(time: Long, limit: Int = 5): Flow<List<Record>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: Record)

}