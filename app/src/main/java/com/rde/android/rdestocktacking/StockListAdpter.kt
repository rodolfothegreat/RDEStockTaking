package com.rde.android.rdestocktacking

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.barcode_row.view.*

class StockListAdpter (val items: ArrayList<StockLine>, val context: Context) : RecyclerView.Adapter<StockListAdpter.MyViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.barcode_row, parent, false)
        )
    }

    private var idListItemEdit : IdListItemEdit? = null
    public fun setIdListItemEdit(_idListItemEdit : IdListItemEdit)
    {
        idListItemEdit = _idListItemEdit
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        // val tvAnimalType = view.tv_animal_type
        val tvRowBarcode = view.tvBRBarcode
        val tvRowQty  = view.tvBRQty
        val tvRowLocation = view.tvBRLocation
        val btnBRRowEdit = view.btnBRRowEdit


    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(position >= itemCount)
        {
            return;
        }
        val rowData = items[position]
        holder.tvRowBarcode.text = rowData.barcode;
        holder.tvRowLocation.text = rowData.location
        holder.tvRowQty.text = rowData.qty.toString()

        holder.btnBRRowEdit.setOnClickListener(View.OnClickListener {
            val popupMenu = PopupMenu(context, holder.btnBRRowEdit)
            popupMenu.inflate(R.menu.edit_menu)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item!!.itemId) {
                        R.id.miEdit -> {
                            idListItemEdit?.itemEdit(position)
                        }
                        R.id. miDelete -> {
                           idListItemEdit?.itemDelete(position)
                        }
                    }
                    return false
                }

            })

            popupMenu.show()
        })
    }



    override fun getItemCount(): Int {
        return items.size
    }

    interface IdListItemEdit {
        fun itemEdit(index: Int)
        fun itemDelete(index: Int)

    }


}
