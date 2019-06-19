package org.kinecosystem.common.base

import android.content.Context
import android.content.SharedPreferences

class LocalStore(context: Context, name:String){

    private val sharedPreferences:SharedPreferences = context.applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)

    fun updateString(key:String, value:String){
        sharedPreferences.edit().apply{
            putString(key, value)
            apply()
        }
    }

    fun getString(key:String, defaultValue:String): String = sharedPreferences.getString(key, defaultValue)

    fun hasString(key:String): Boolean = sharedPreferences.contains(key)

    fun updateInt(key:String, value:Int){
        sharedPreferences.edit().apply{
            putInt(key, value)
            apply()
        }
    }

    fun getInt(key:String, defaultValue:Int): Int = sharedPreferences.getInt(key, defaultValue)

    fun clearAll(){
        sharedPreferences.edit().apply {
            clear()
            apply()
        }
    }

}