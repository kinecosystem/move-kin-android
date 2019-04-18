package org.kinecosystem.appsdiscovery.base

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

    fun getString(key:String, defaultValue:String?=null): String? = sharedPreferences.getString(key, defaultValue)

    fun clearAll(){
        sharedPreferences.edit().apply {
            clear()
            apply()
        }
    }

}