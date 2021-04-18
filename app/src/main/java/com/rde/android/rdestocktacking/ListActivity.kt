package com.rde.android.rdestocktacking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException


class ListActivity : AppCompatActivity() {

    private var fileName : String? = null
    private val lstBarcode = ArrayList<StockLine>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        fileName = intent.getStringExtra(MainActivity.FILE_NAME_ITEM)

        rvLList.layoutManager = LinearLayoutManager(this)
        var anAdapter = StockListAdpter(lstBarcode, this);
        rvLList.adapter = anAdapter
        rvLList.adapter?.notifyDataSetChanged()
        updateEB()

        val listemery = object : StockListAdpter.IdListItemEdit{
            override fun itemEdit(index: Int) {
                if(index < 0 || index >= lstBarcode.size)
                    return;

                val rowData = lstBarcode[index]
                val fragmentManager = getSupportFragmentManager();
                val dlg = RecordDlg.newInstance(rowData.location, rowData.barcode, rowData.qty);
                dlg.show(fragmentManager, "iDDConfirmationlDlg")
                dlg.idSaveDlgListener = object : RecordDlg.IdSaveDlgListener{
                    override fun onsave(qty: Int) {
                        rowData.qty = qty
                        this@ListActivity.rvLList.adapter?.notifyItemChanged(index)
                        updateEB()
                    }

                }
            }


            override fun itemDelete(index: Int) {
                if(index < 0 || index >= lstBarcode.size)
                    return;

                val rowData = lstBarcode[index]
                val fragmentManager = getSupportFragmentManager();
                val dlg = ConfirmationDlg.newInstance(
                    "Are you sure you want to delete the barcode " + rowData.barcode + "?",
                    1
                );
                dlg.show(fragmentManager, "iDDConfirmationlDlg")
                dlg.idConfirmationListener = object : ConfirmationDlg.IdConfirmDlgListener {
                    override fun onConfirm(itemIndex: Int) {
                        lstBarcode.removeAt(index)
                        this@ListActivity.rvLList.adapter?.notifyDataSetChanged();
                        updateEB()
                    }

                }


            }

        }

        anAdapter.setIdListItemEdit( listemery)


    }

    private fun updateEB()
    {
        tvTotalRow.text = "Total Rows: " + lstBarcode.count().toString();
        var totQty : Int = 0;
        lstBarcode.forEach {totQty = totQty + it.qty }
        tvTotalQty.text = "Total Quantity: " + totQty.toString();
    }

    private fun getAll()
    {
        if(fileName == null) {
            val sharedPreferences = getPreferences(MODE_PRIVATE)
            fileName = sharedPreferences.getString(PREF_FILE_NAME, "")
        }
        if(fileName.equals(""))
        {
            return;
        }
        getDataFromFile(fileName!!)
    }

    private fun getDataFromFile(csvFileName : String)
    {
        if (csvFileName == null || csvFileName.equals("")) return
        val root = getExternalFilesDir(null)
        val rootPath = root!!.absolutePath
        val afilename = rootPath + File.separator + csvFileName
        lstBarcode.clear()
        try {
            val lines: List<String> = File(afilename).readLines()
            lines.forEach{
                val theFields = it.split(",".toRegex()).toTypedArray()
                if (theFields == null || theFields.size < 3) {
                    return
                }
                val anObj = StockLine()
                lstBarcode.add(anObj)
                anObj.location = theFields[0]
                anObj.barcode = theFields[1]
                var aqty = 0
                try {
                    aqty = theFields[2].toInt()
                } catch (ee: java.lang.Exception) {
                }
                anObj.qty = aqty
            }
            rvLList.adapter?.notifyDataSetChanged();

        }
        catch (ex : Exception)
        {
            Log.e(ListActivity.TAG, " Error reading file " + afilename + " " + ex.message)
        }
    }

    private fun saveFile(csvFileName: String): Boolean {
        val root = getExternalFilesDir(null)
        val rootPath = root!!.absolutePath
        val afilename = rootPath + File.separator + csvFileName
        //String afilename = csvFileName;
        if (lstBarcode.size === 0) {
            val afile = File(afilename)
            if (afile.exists()) {
                afile.delete()
            }
            return false
        }
        val afile = File(afilename)
        var fileWriter: FileWriter? = null
        var br: BufferedWriter? = null
        Log.d(MainActivity.TAG, "filename: $csvFileName")
        Log.d(MainActivity.TAG, "filename: " + afilename)
        return try {
            fileWriter = FileWriter(afilename, false) //overwrites file
            br = BufferedWriter(fileWriter)
            for (i in 0 until lstBarcode.size) {
                val anObj = lstBarcode.get(i)
                br.write(anObj.toCsvString().toString() + System.getProperty("line.separator"))
            }
            true
        } catch (e: IOException) {
            val newFragment =
                WrongLoginDlg("Could not create write file " + afilename + " " + e.message)
            newFragment.show(supportFragmentManager, "loginDlg")
            false
        } finally {
            try {
                br?.close()
                fileWriter?.close()
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun saveAll()
    {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit().clear()
        editor.putString(PREF_FILE_NAME, fileName)
        editor.commit()
        if(fileName != null && !fileName.equals("")) {
            saveFile(fileName!!)
        }
    }

    override fun onPause() {
        super.onPause()
        saveAll()
    }

    override fun onResume() {
        super.onResume()
        getAll()
        updateEB()
    }

    companion object {
        const val PREF_FILE_NAME = "prefFileName"
        const val TAG = "ListActivity"

    }

}