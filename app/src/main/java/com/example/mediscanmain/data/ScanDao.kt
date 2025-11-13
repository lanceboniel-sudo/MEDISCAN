package com.example.mediscanmain.data

import androidx.room.*
import com.example.mediscanmain.model.ScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scan: ScanEntity)

    @Query("SELECT * FROM scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanEntity>>

    @Delete
    suspend fun delete(scan: ScanEntity)
}
