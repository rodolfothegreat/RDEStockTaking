package com.rde.android.rdestocktacking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    private val lstBarcode = ArrayList<StockLine>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lstBarcode.add(StockLine("111111111", "11111", 12));
        lstBarcode.add(StockLine("222222222", "222222", 10));
        lstBarcode.add(StockLine("333333333", "33333", 9))
        rvMainList.layoutManager = LinearLayoutManager(this)
        rvMainList.adapter = StockTakingAdapter(lstBarcode, this);
        rvMainList.adapter?.notifyDataSetChanged()

        btnAddSku.setOnClickListener(View.OnClickListener {v: View? -> addSku(v) })
    }

    fun addSku(v : View?)
    {
        val abarcode = edtBarcode.text.toString().trim();
        val alocation = edtLocation.text.toString().trim();
        val sqty = editTextNumber.text.toString().trim();
        var qty : Int = 0;
        try {
            qty = sqty.toInt()
        }
        catch (exe : Exception){}

        if(abarcode.equals(""))
        {
            val newFragment =
                WrongLoginDlg("Barcode cannot be blank");
            newFragment.show(supportFragmentManager, "loginDlg")
            return;
        }

        if(alocation.equals(""))
        {
            val newFragment =
                WrongLoginDlg("Location cannot be blank");
            newFragment.show(supportFragmentManager, "loginDlg")
            return;
        }
        if(qty == 0)
        {
            val newFragment =
                WrongLoginDlg("Qty cannot be zero or blank");
            newFragment.show(supportFragmentManager, "loginDlg")
            return;
        }


    }

    private fun saveAll()
    {
        saveFile(FILE_NAME);
    }

    private fun getAll()
    {
        getDataFromFile_1(FILE_NAME)
    }
    override fun onResume() {
        super.onResume()
        getAll()
        //hideKeyboard()
    }

    override fun onPause() {
        super.onPause()
        saveAll()
    }

    private fun getDataFromFile_1(csvFileName : String) {
        if (csvFileName == null || csvFileName.equals("")) return
        val root = getExternalFilesDir(null)
        val rootPath = root!!.absolutePath
        val afilename = rootPath + File.separator + csvFileName
        //String afilename = csvFileName;
        val afile = File(afilename)
        if (!afile.exists()) return
        var aline = ""
        var acount = 0
        var bufferReader: BufferedReader? = null
        try {
            bufferReader = BufferedReader(FileReader(afilename))
            lstBarcode.clear()
            while (bufferReader.readLine().also { aline = it } != null) {
                acount++
                //if(acount == 1)
                //    continue;
                val theFields = aline.split(",".toRegex()).toTypedArray()
                if (theFields.size < 3) {
                    continue
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
            rvMainList.adapter?.notifyDataSetChanged();
        } catch (ex: Exception) {
            Log.e(TAG, "Error reading file " + afilename + " " + ex.message)
        } finally {
            try {
                bufferReader?.close()
            } catch (exy: IOException) {
                Log.e(TAG, " " + exy.message)
            }
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
        Log.d(TAG, "filename: $csvFileName")
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


    companion object
    {
        const val FILE_NAME = "barcodes.csv"
        const val EXPORT_FILE = "download.txt"
        const val TAG = "MainActivity"
    }


}