package codeafifudin.fatakhul.chatapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val iduser : String, val name : String, val photo : String) : Parcelable {
    constructor() : this("","","")
}