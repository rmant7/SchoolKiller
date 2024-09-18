package com.schoolkiller.data_Layer.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Picture(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pictureFilePath: String,
    val pictureTitle: String? = null,
    val pictureDescription: String? = null,
    val pictureTimestamp: Long = System.currentTimeMillis()
)
