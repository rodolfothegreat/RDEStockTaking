package com.rde.android.rdestocktacking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.URLConnection

class MainActivity : AppCompatActivity() {

    private val lstBarcode = ArrayList<StockLine>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMainList.layoutManager = LinearLayoutManager(this)
        rvMainList.adapter = StockTakingAdapter(lstBarcode, this);
        rvMainList.adapter?.notifyDataSetChanged()

        btnAddSku.setOnClickListener(View.OnClickListener { addSku() })
        btnClear.setOnClickListener(View.OnClickListener {
            clearList();
        })

        btnShare.setOnClickListener {
            shareData();
        }

        btnSave.setOnClickListener {
            saveData()
        }

        checkAndRequestPermissions();

        cbDefault1.setOnClickListener(View.OnClickListener { setEditBoxes() })
        cbHardEnter.setOnClickListener(View.OnClickListener { setEditBoxes() })
        btnView.setOnClickListener {
            val intent  = Intent(this@MainActivity, ListActivity::class.java)
                intent.putExtra(FILE_NAME_ITEM, FILE_NAME)
                startActivity(intent);

        }

        edtBarcode.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            editorActionListener(
                v,
                actionId,
                event
            )
        })

    }

    private fun editorActionListener(v: TextView, actionId: Int, event: KeyEvent) : Boolean
    {
        if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER) {
            if(cbDefault1.isChecked && !v.text.toString().equals(""))
            {
                btnAddSku.performClick()

            }
            return true;
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    fun shareData()
    {
        saveAll();
        shareFile(FILE_NAME);
    }

    private fun shareFile(filename: String) {

        val root = getExternalFilesDir(null)
        val rootPath = root!!.absolutePath

        val afilename: String = rootPath + File.separator + filename


        val file = File(afilename)
        val intentShareFile = Intent(Intent.ACTION_SEND)

        val apkURI: Uri =
            FileProvider.getUriForFile(applicationContext, "$packageName.provider", file)
        intentShareFile.type = URLConnection.guessContentTypeFromName(file.getName())
        intentShareFile.putExtra(Intent.EXTRA_STREAM, apkURI)

        startActivity(Intent.createChooser(intentShareFile, "Share File"))
    }


    fun saveData()
    {
        val docs = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS
            ), "rde_stock_taking"
        )

        try {
            docs.mkdirs()
            saveAll()
            val root = getExternalFilesDir(null)
            val rootPath = root!!.absolutePath

            val afilsourcename = rootPath + File.separator + FILE_NAME
            //String afilename = csvFileName;
            val afile = File(afilsourcename)


            val afilename: String = docs.absolutePath + File.separator + FILE_NAME

            val afileDest = File(afilename)
            if(afileDest.exists())
            {
                afileDest.delete()
            }
            afile.copyTo(afileDest)
            Snackbar.make(
                rootLayout,
                "File copied to " + docs.absolutePath + File.separator + FILE_NAME,
                Snackbar.LENGTH_LONG
            ).show()
        } catch (E: Exception)
        {
            Snackbar.make(rootLayout, "Could not export file. " + E.message, Snackbar.LENGTH_LONG).show()
        }


    }

    fun clearList()
    {
        val fragmentManager = getSupportFragmentManager();
        val dlg = ConfirmationDlg.newInstance("Are you sure you want to delete the whole list?", 1);
        dlg.show(fragmentManager, "iDDConfirmationlDlg")
        dlg.idConfirmationListener = object : ConfirmationDlg.IdConfirmDlgListener {
            override fun onConfirm(itemIndex: Int) {
                this@MainActivity.lstBarcode.clear();
                this@MainActivity.rvMainList.adapter?.notifyDataSetChanged();


            }

        }

    }

    fun addSku()
    {
        val abarcode = edtBarcode.text.toString().trim();
        val alocation = edtLocation.text.toString().trim();
        val sqty = editTextNumber.text.toString().trim();
        var qty : Int = 0;
        try {
            qty = sqty.toInt()
        }
        catch (exe: Exception){}

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
        var theStockLine : StockLine? = null;
        for( i  in 0..lstBarcode.size - 1   )
        {
            val stockLine = lstBarcode[i];
            if(stockLine.barcode.equals(abarcode) && stockLine.location.equals(alocation))
            {
                theStockLine = stockLine;
                //theStockLine.qty += qty;
                //saveAll();
                //rvMainList.adapter?.notifyItemChanged(i)
                //return;
                break;
            }
        }
        if(theStockLine == null)
        {
            theStockLine = StockLine(abarcode, alocation, 0);
            lstBarcode.add(theStockLine);
        }
        theStockLine.qty += qty;

        rvMainList.adapter?.notifyDataSetChanged();

        edtBarcode.setText("")
        if(!cbDefault1.isChecked)
        {
            editTextNumber.setText("");
        }
        if (!cbLockLoc.isChecked)
        {
            edtLocation.setText("")
        } else {
            edtBarcode.requestFocus();
        }



    }

    private fun saveAll()
    {
        saveFile(FILE_NAME);
    }

    private fun getAll()
    {
        getDataFromFile_3(FILE_NAME)
    }
    override fun onResume() {
        super.onResume()
        getAll()
        getPref()
        setEditBoxes()
        //hideKeyboard()
    }

    override fun onPause() {
        super.onPause()
        saveAll()
        savePref()
    }

    private fun getDataFromFile_3(csvFileName: String)
    {
        if ( csvFileName.equals("")) return
        val root = getExternalFilesDir(null)
        val rootPath = root!!.absolutePath
        val afilename = rootPath + File.separator + csvFileName
        lstBarcode.clear()
        try {
            val lines: List<String> = File(afilename).readLines()
            lines.forEach{
                val theFields = it.split(",".toRegex()).toTypedArray()
                if ( theFields.size < 3) {
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
            rvMainList.adapter?.notifyDataSetChanged();

        }
        catch (ex: Exception)
        {
            Log.e(TAG, " Error reading file " + afilename + " " + ex.message)
        }
    }

    private fun saveFile(csvFileName: String): Boolean {
        val root = getExternalFilesDir(null)
        val rootPath = root!!.absolutePath
        val afilename = rootPath + File.separator + csvFileName
        //String afilename = csvFileName;
        if (lstBarcode.size == 0) {
            val afile = File(afilename)
            if (afile.exists()) {
                afile.delete()
            }
            return false
        }
        var fileWriter: FileWriter? = null
        var br: BufferedWriter? = null
        Log.d(TAG, "filename: $csvFileName")
        Log.d(TAG, "filename: " + afilename)
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

    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    private fun checkAndRequestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storage =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val storageRead =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val loc =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val loc2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        // val serperm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
        }
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        //if (serperm != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        // }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (storageRead != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    fun savePref()
    {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit().clear()
        editor.putBoolean(PREF_HARD_ENTER, this.cbHardEnter.isChecked())
        editor.putBoolean(PREF_DEFAULT_1, this.cbDefault1.isChecked())
        editor.putBoolean(PREF_LOCK_LOC, cbLockLoc.isChecked())
        editor.commit()

    }

    fun getPref()
    {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        this.cbHardEnter.isChecked = sharedPreferences.getBoolean(PREF_HARD_ENTER, true)
        this.cbDefault1.isChecked = sharedPreferences.getBoolean(PREF_DEFAULT_1, false)
        this.cbLockLoc.isChecked = sharedPreferences.getBoolean(PREF_LOCK_LOC, false)
    }

    fun setEditBoxes()
    {
        editTextNumber.imeOptions =  EditorInfo.IME_ACTION_DONE or EditorInfo.IME_ACTION_NEXT
        edtBarcode.imeOptions =  EditorInfo.IME_ACTION_DONE or EditorInfo.IME_ACTION_NEXT
        edtLocation.imeOptions =  EditorInfo.IME_ACTION_DONE or EditorInfo.IME_ACTION_NEXT

        editTextNumber.setCursorVisible(true); //Set the cursor in the input box to be invisible
        editTextNumber.setFocusable(true); //no focus
        editTextNumber.setFocusableInTouchMode(true); //Under focus when touched

        if(!cbHardEnter.isChecked)
        {
            editTextNumber.imeOptions =  EditorInfo.IME_ACTION_NEXT
            edtBarcode.imeOptions =  EditorInfo.IME_ACTION_NEXT
            edtLocation.imeOptions =  EditorInfo.IME_ACTION_NEXT
        }

        if (cbDefault1.isChecked)
        {
            editTextNumber.setText("1")
            editTextNumber.imeOptions = editTextNumber.imeOptions or EditorInfo.IME_FLAG_NO_ENTER_ACTION

            editTextNumber.setCursorVisible(false); //Set the cursor in the input box to be invisible
            editTextNumber.setFocusable(false); //no focus
            editTextNumber.setFocusableInTouchMode(false); //Under focus when touched


        }


    }

    companion object
    {
        const val FILE_NAME = "barcodes.csv"
        const val FILE_NAME_ITEM = "file_name_item"
        const val EXPORT_FILE = "download.txt"
        const val TAG = "MainActivity"

        const val PREF_HARD_ENTER = "PrefHardEnter"
        const val PREF_DEFAULT_1 = "PrefDeafault1"
        const val PREF_LOCK_LOC = "PrefLockLoc"
    }


}