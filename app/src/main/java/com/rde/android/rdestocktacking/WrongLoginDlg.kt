package com.rde.android.rdestocktacking

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


class WrongLoginDlg : DialogFragment {
    private var mTitle: String? = null

    constructor() {}
    constructor(_title: String?) {
        mTitle = _title
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //    String title = getArguments().getString("title");
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this.context!! )
        alertDialogBuilder.setTitle(context?.getString(R.string.app_name))
        alertDialogBuilder.setMessage(mTitle)
        alertDialogBuilder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which ->
                // on success
            })
        return alertDialogBuilder.create()
    }

}