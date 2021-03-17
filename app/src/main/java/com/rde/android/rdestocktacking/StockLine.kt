package com.rde.android.rdestocktacking

class StockLine() {
    public var barcode : String = "";
    public var location : String = "";
    public var qty : Int = 0;

    constructor (_barcode : String, _location : String, _qty : Int) : this()
    {
        barcode = _barcode
        location = _location
        qty = _qty
    }

    fun toCsvString() : String
    {
        return location.replace(",", ";")  + barcode.replace(",", ";")  + "," + qty.toString()
    }



}