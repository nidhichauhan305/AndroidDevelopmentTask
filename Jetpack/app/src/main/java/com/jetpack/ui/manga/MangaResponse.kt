package com.jetpack.ui.manga

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class MangaResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("data")
    val data: List<Data> = listOf()
) {
    @Entity(tableName = "manga_table")
    data class Data(
        @PrimaryKey(autoGenerate = true)
        @SerializedName("roomId")
        val roomId:Int = 0,
        @SerializedName("authors")
        val authors: List<String> = listOf(),
        @SerializedName("create_at")
        val createAt: Long = 0,
        @SerializedName("genres")
        val genres: List<String> = listOf(),
        @SerializedName("id")
        val id: String = "",
        @SerializedName("nsfw")
        val nsfw: Boolean = false,
        @SerializedName("status")
        val status: String = "",
        @SerializedName("sub_title")
        val subTitle: String = "",
        @SerializedName("summary")
        val summary: String = "",
        @SerializedName("thumb")
        val thumb: String = "",
        @SerializedName("title")
        val title: String = "",
        @SerializedName("total_chapter")
        val totalChapter: Int = 0,
        @SerializedName("type")
        val type: String = "",
        @SerializedName("update_at")
        val updateAt: Long = 0
    )
}