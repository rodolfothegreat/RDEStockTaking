package com.rde.android.rdestocktacking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.shawnlin.numberpicker.NumberPicker
import kotlinx.android.synthetic.main.record_dlg.view.*

class RecordDlg : DialogFragment() {
    init {
        retainInstance = true
    }

    private var btnRecOk: Button? = null
    private var btnRecCancel: Button? = null
    private var tvDlgBarcode: TextView? = null
    private var number_picker: NumberPicker? = null
    private var tvDlgLocation: TextView? = null

    var idSaveDlgListener: IdSaveDlgListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        val _dialog = dialog
        if (_dialog != null && getRetainInstance()) {
            _dialog.setDismissMessage(null);
        }
        idSaveDlgListener?.oncancel()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnRecCancel = view.btnRecCancel
        btnRecCancel?.setOnClickListener {
            idSaveDlgListener?.oncancel()
            dismiss()
        }

        tvDlgBarcode = view.tvDlgBarcode
        tvDlgBarcode?.text = arguments?.getString(ID_BARCODE, "")

        tvDlgLocation = view.tvDlgLocation
        tvDlgLocation?.text = arguments?.getString(ID_LOCATION, "")


        number_picker = view.number_picker
        number_picker?.value = requireArguments().getInt(ID_QTY, 0)

        btnRecOk = view.btnRecOk
        btnRecOk?.setOnClickListener {
            val newQty = number_picker?.value
            if(newQty != null)
                idSaveDlgListener?.onsave(newQty)
            dismiss()
        }



    }

    override fun onResume() {
        super.onResume()
        var params = dialog?.window?.attributes
        params?.width  = ConstraintLayout.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.record_dlg, container);
    }


    interface IdSaveDlgListener {
        fun onsave(qty: Int)
        fun oncancel()
    }

    companion object {
        const val ID_BARCODE = "id_barcode";
        const val ID_QTY = "id_qty"
        const val ID_LOCATION = "id_location"
        fun newInstance(alocation: String, abarcode: String, aqty: Int): RecordDlg {
            val dlg = RecordDlg()
            val abundle = Bundle()
            abundle.putString(ID_BARCODE, abarcode)
            abundle.putString(ID_LOCATION, alocation)
            abundle.putInt(ID_QTY, aqty)
            dlg.arguments = abundle
            return dlg
        }


    }
}