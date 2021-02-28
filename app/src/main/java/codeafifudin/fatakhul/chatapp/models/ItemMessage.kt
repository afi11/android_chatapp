package codeafifudin.fatakhul.chatapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ItemMessage(val idmessage : String, val sender : String, val receiver : String, val photo_user : String,val messages : String, val audio : String, val image : String,
    val date_message : String ,val is_read : String) : Parcelable {
    constructor() : this("","","","","","","","","")
}