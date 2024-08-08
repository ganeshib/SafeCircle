package com.example.safecircle

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ContactModel::class], version = 1, exportSchema = false)
public abstract class safeCircleDatabase:RoomDatabase() {
    abstract fun contactDao():ContactDao

    companion object{
        @Volatile
        private var INSTANCE:safeCircleDatabase?=null
        fun getDatabase(context: Context):safeCircleDatabase{
            if(INSTANCE!=null){
                return INSTANCE as safeCircleDatabase
            }

            return synchronized(safeCircleDatabase::class.java){
                val instance= Room.databaseBuilder(context.applicationContext,safeCircleDatabase::class.java,"safeCircle database").build()
                INSTANCE=instance
                instance
            }
        }
    }
}