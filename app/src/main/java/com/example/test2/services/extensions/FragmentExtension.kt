package com.example.test2.services.extensions

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

fun Fragment.startAnimation(progressBar: ProgressBar) {
    try {
        val activity: Activity = activity as Activity
        //activity.blockBackButton = true
        activity.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility = View.VISIBLE
    }
    catch(e: Exception) { }
}

fun Fragment.stopAnimation(progressBar: ProgressBar) {
    try {
        val activity: Activity = activity as Activity
        //activity.blockBackButton = false
        activity.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility = View.INVISIBLE
    }
    catch(e: Exception) { }
}
