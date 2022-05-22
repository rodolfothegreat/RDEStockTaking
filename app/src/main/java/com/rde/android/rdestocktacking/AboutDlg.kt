package com.rde.android.rdestocktacking

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.about_dlg.view.*


class AboutDlg: DialogFragment() {
    init {
        retainInstance = true
    }

    private var btnAboutOk: Button? = null;
    private var tvAbout: TextView? = null;

    override fun onResume() {
        super.onResume()
        var params = dialog?.window?.attributes
        params?.width  = ConstraintLayout.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }
    fun VersionName(context: Context): String? {
        return try {
            context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.about_dlg, container);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var atext = arguments?.getString(AboutDlg.ID_TEXT, "");
        val avernumber = VersionName(view.context);

        val aboutText = SpannableString(
            "Version "  +avernumber + "\n"
                    + view.context.resources.getString(R.string.about_text))
        val anotherText = Html.fromHtml("Version "  +avernumber + "<br>"
                + "Please see <a href=\"http://www.example.com\">http://www.example.com</a>.<br>" +
                "Email support at <a href=\"mailto:rodolfothegreat@gmail.com\">rodolfothegreat@gmail.com</a>")



        btnAboutOk = view.btnAboutOk;
        tvAbout = view.tvAbout;

        btnAboutOk?.setOnClickListener { dismiss() };
        tvAbout?.text = anotherText;
        tvAbout?.setMovementMethod(LinkMovementMethod.getInstance());

    }

    companion object {
        const val ID_TEXT = "id_barcode";
        fun newInstance(atext: String): AboutDlg {
            val dlg = AboutDlg()
            val abundle = Bundle()
            abundle.putString(ID_TEXT, atext)
            dlg.arguments = abundle
            return dlg
        }


    }

}