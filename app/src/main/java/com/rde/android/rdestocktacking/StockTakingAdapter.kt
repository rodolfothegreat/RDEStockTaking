package com.rde.android.rdestocktacking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_row.view.*

class StockTakingAdapter (val items: ArrayList<StockLine>, val context: Context) : RecyclerView.Adapter<StockTakingAdapter.MyViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return StockTakingAdapter.MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_row, parent, false)
        )
    }



    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        // val tvAnimalType = view.tv_animal_type
        val tvRowBarcode = view.tvRowBarcode
        val tvRowQty  = view.tvRowQty
        val tvRowLocation = view.tvRowLocation

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(position >= itemCount)
        {
            return;
        }
        val rowData = items[position]
        holder.tvRowBarcode.text = rowData.barcode;
        holder.tvRowLocation.text = rowData.location;
        holder.tvRowQty.text = rowData.qty.toString()

    }

    override fun getItemCount(): Int {
        return items.size
    }



}

