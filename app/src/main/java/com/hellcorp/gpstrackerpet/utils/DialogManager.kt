package com.hellcorp.gpstrackerpet.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast

object DialogManager {
    fun showLocationEnableDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("GPS sensor is currently desabled, you should to switch it on to use thr app.")
        dialog.setMessage("Enable GPS?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
            Toast.makeText(context, "YES", Toast.LENGTH_SHORT).show()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ ->
            Toast.makeText(context, "No", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }
}