package com.jetpack.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userLoginCredentials")
data class UserLoginInfoClass(
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    var email:String,
    val password:String
)