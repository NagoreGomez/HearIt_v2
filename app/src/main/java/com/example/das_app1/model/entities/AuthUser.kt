package com.example.das_app1.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/*************************************************************************
 ****                               User                              ****
 *************************************************************************/


@Serializable
data class AuthUser(
    val username: String,
    val password: String = "",
)
