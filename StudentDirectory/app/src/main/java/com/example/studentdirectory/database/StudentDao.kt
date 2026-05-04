package com.example.studentdirectory.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<StudentEntity>)

    @Query("SELECT * FROM students ORDER BY name ASC")
    suspend fun getAllStudents(): List<StudentEntity>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Int): StudentEntity?

    @Query("SELECT * FROM students WHERE name LIKE '%' || :keyword || '%' OR nim LIKE '%' || :keyword || '%' ORDER BY name ASC")
    suspend fun searchStudents(keyword: String): List<StudentEntity>

    @Update
    suspend fun update(student: StudentEntity): Int

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int
}
