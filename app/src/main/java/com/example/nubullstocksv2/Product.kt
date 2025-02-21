package com.example.nubullstocksv2

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val imageURL: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeInt(stock)
        parcel.writeString(imageURL)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
