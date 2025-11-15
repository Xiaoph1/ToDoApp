package com.example.todoapp.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
// 1. 修正：导入 Android 系统的 Context（关键！）
import android.content.Context
import androidx.room.TypeConverters
// 导入你的实体类和 Dao 接口（确保路径正确）
import com.example.todoapp.data.models.ToDoData

@Database(entities = [ToDoData::class],version = 1,exportSchema = false)
@TypeConverters(Converter::class)
abstract class ToDoDatabase:RoomDatabase() {

    abstract fun toDoDao():ToDoDao

    //伴生对象的作用是「确保数据库只有一个实例」（单例模式）
    companion object {
        @Volatile  //保证多线程环境下的「可见性」。即当一个线程修改了 INSTANCE 的值（如创建了实例），其他线程能立即看到最新值，避免多个线程重复创建实例。
        private var INSTANCE:ToDoDatabase? = null

        fun getDatabase(context: Context):ToDoDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}