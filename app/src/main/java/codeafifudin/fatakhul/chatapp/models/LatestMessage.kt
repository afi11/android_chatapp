package codeafifudin.fatakhul.chatapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class LatestMessage(val iduser : String, val name : String, val photo : String ,val message : String, val waktu : String, val num_unread_message : String) :
    Parcelable {
    constructor() : this("","","","","","")
}