package com.app.assignment.helpers

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefManager  @Inject constructor(@ApplicationContext val context: Context) {

    private val APP_NAME = "Assignment"
    private val APP_NAME1 = "Assignment1"

    private val editor: SharedPreferences.Editor
    private val editorNR: SharedPreferences.Editor

    private val LOGIN = "login"
    private val _userId = "User_Id"

    init {
        val prefs = context.getSharedPreferences(APP_NAME, 0)
        editor = prefs.edit()

        val prefsNR = context.getSharedPreferences(APP_NAME1, 0)
        editorNR = prefsNR.edit()
    }

    //------------------------------------------------------------
    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
    }

    //set data to preference manager through a key
    private fun setPreferencesData(key: String, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }


    private fun applyPreferencesData(key: String, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    //get data from preference manager using a key
    private fun getPreferenceData(key: String, defaultValue: String?): String? {
        return getPrefs().getString(key, defaultValue)
    }

    private fun setPreferencesDataList(key: String, value: ArrayList<String>?) {
        val gson = Gson()
        val json = gson.toJson(value)
        editor.putString(key, json)
        editor.commit()
    }

    private fun getPreferenceDataList(key: String, defaultValue: ArrayList<String>?): ArrayList<String> {
        val gson = Gson()
        val json = getPrefs().getString(key, null)
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }



    //set boolean preferences
    private fun setBoolean(key: String, value: Boolean?) {
        editor.putBoolean(key, value!!)
        editor.commit()
    }

    //get boolean preferences
    private fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return getPrefs().getBoolean(key, defaultValue)
    }
    //------------------------------------------------------------

    // this data not delete
    private fun getPrefsNR(): SharedPreferences {
        return context.getSharedPreferences(APP_NAME1, Context.MODE_PRIVATE)
    }

    //set data to preference manager through a key
    private fun setPreferencesDataNR(key: String, value: String?) {
        editorNR.putString(key, value)
        editorNR.commit()
    }

    private fun applyPreferencesDataNR(key: String, value: String?) {
        editorNR.putString(key, value)
        editorNR.apply()
    }

    //get data from preference manager using a key
    private fun getPreferenceDataNR(key: String, defaultValue: String?): String? {
        return getPrefsNR().getString(key, defaultValue)
    }

    //set boolean preferences
    private fun setBooleanNR(key: String, value: Boolean?) {
        editorNR.putBoolean(key, value!!)
        editorNR.commit()
    }

    //get boolean preferences
    private fun getBooleanNR(key: String?, defaultValue: Boolean): Boolean {
        return getPrefsNR().getBoolean(key, defaultValue)
    }
    //==============================================================================================

    fun setLogin(isLogin: Boolean) {
        return setBoolean(LOGIN, isLogin)
    }

    fun isLogin(): Boolean {
        return getBoolean(LOGIN, false)
    }

    fun setUserId(userId: String?) {
        setPreferencesData(_userId, userId)
    }

    fun getUserId(): String {
        return getPreferenceData(_userId, "")?: ""
    }

    fun clear() {
        editor.clear()
        editor.apply()
    }


}