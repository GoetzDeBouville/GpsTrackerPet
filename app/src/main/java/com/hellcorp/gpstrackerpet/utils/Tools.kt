package com.hellcorp.gpstrackerpet.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hellcorp.gpstrackerpet.R

fun Fragment.openFragment(f: Fragment) {
    (activity as AppCompatActivity).supportFragmentManager
        .beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolder, f).commit()
}

fun AppCompatActivity.openFragment(f: Fragment) {
    supportFragmentManager
        .beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolder, f).commit()
}

fun showSnackbar(
    view: View,
    message: String,
    context: Context
) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
    val snackTextColor = ContextCompat.getColor(context, R.color.white)
    val backgroundColor = ContextCompat.getColor(context, R.color.text_color)

    val textView =
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
    textView.textSize = 16f
    textView.setTextColor(snackTextColor)
    snackbar.view.setBackgroundColor(backgroundColor)
    snackbar.show()
}