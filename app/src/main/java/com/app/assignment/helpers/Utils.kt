package com.app.assignment.helpers

import android.content.*
import android.widget.Toast
//import androidx.viewbinding.BuildConfig
import com.app.assignment.BuildConfig

object Utils {
    //return true;
    val isDebug: Boolean
        get() =//return true;
            BuildConfig.DEBUG

    fun showToast(context: Context?, msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}