package codeafifudin.fatakhul.chatapp.utils

import android.content.Context

class Auth {

    fun getIdUser(context: Context) : String {
        val sharedPref = context.getSharedPreferences("userid", 0)
        var userid = sharedPref.getString("userid", "")
        return userid.toString()
    }

    fun getToken(context: Context) : String {
        val sharedPref = context.getSharedPreferences("token", 0)
        var token = sharedPref.getString("token", "")
        return token.toString()
    }

}