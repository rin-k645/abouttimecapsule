package com.aboutcapsule.android.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context:Context) {
    private val preferences : SharedPreferences = context.getSharedPreferences("pre_name",Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String):String{
        return preferences.getString(key,defValue).toString()

    }

    fun getInt(key:String,defValue:Int) : Int{
        var tmp = defValue.toString()
        return preferences.getString(key,tmp)!!.toInt()
    }

    fun setString(key: String, defValue: String){
        preferences.edit().putString(key, defValue).apply()
    }

    fun setInt(key: String, defValue: Int){
        var tmp = defValue.toString()
        preferences.edit().putString(key, tmp).apply()
    }

}