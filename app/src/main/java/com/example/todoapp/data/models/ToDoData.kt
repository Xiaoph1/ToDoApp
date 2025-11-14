package com.example.todoapp.data.models
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class ToDoData(
    @PrimaryKey(autoGenerate = true)
    var id:Int,  //primary key autogenerate
    var title:String,
    var priority: Priority,
    var description:String
)