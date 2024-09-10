package com.schoolkiller.data_Layer.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.schoolkiller.data_Layer.entities.Picture
import com.schoolkiller.utils.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {

    @Query("SELECT * FROM ${Constants.PICTURE_TABLE_NAME}")
    fun getAllPictures(): Flow<List<Picture>>

    @Insert
    suspend fun insertPicture(picture: Picture)

    @Delete
    suspend fun deletePicture(picture: Picture)

}