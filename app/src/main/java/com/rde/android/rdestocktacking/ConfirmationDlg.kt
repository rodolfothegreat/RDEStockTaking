package com.rde.android.rdestocktacking

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.confirmation_dlg.view.*

class ConfirmationDlg: DialogFragment() {
    init {
        setRetainInstance(true)
    }
    private var btnCCancel: Button? = null
    private var btnCOk: Button? = null
    private var tvCMessage: TextView? = null


    public var idConfirmationListener: IdConfirmDlgListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        val _dialog = dialog
        if (_dialog != null && getRetainInstance()) {
            _dialog.setDismissMessage(null);
        }
        dialog?.ownerActivity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.confirmation_dlg, container);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCCancel = view.btnCCancel
        btnCOk = view.btnCOk
        tvCMessage = view.tvCMessage
        val itemIndex = getArguments()?.getInt(ID_INDEX, -1);
        tvCMessage?.text = arguments?.getString(ID_MESSAGE, "")

        btnCCancel?.setOnClickListener {
            dialog?.ownerActivity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
            dismiss() }

        btnCOk?.setOnClickListener {
            if (itemIndex != null) {
                idConfirmationListener?.onConfirm(itemIndex)
            }
            dialog?.ownerActivity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)
        dialog.getWindow()?.requestFeature(Window.FEATURE_NO_TITLE);
        return dialog
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        dialog?.window?.attributes = params
    }




    interface IdConfirmDlgListener {
        fun onConfirm(itemIndex: Int)
    }

    companion object {
        const val COMPANION_OBJECT_NUMBER = 40
        const val ID_MESSAGE = "id_message";
        const val ID_INDEX = "id_index"
        fun newInstance(acaption: String, anIndex: Int) : ConfirmationDlg
        {
            val dlg = ConfirmationDlg()
            val abundle = Bundle()
            abundle.putString(ID_MESSAGE, acaption)
            abundle.putInt(ID_INDEX, anIndex)
            dlg.setArguments(abundle)
            return dlg
        }
    }


}