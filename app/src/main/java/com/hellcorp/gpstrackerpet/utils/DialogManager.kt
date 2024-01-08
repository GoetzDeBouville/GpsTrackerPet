package com.hellcorp.gpstrackerpet.utils

import android.app.AlertDialog
import android.content.Context

object DialogManager {
    fun showLocationEnableDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("GPS sensor is currently desabled, you should to switch it on to use thr app.")
        dialog.setMessage("Enable GPS?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
            listener.onClick()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ -> }
        dialog.show()
    }

    interface Listener {
        fun onClick()
    }
}