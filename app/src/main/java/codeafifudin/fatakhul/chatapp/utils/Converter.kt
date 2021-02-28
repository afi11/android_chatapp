package codeafifudin.fatakhul.chatapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Converter {
    @RequiresApi(Build.VERSION_CODES.O)
    fun forMessage(tgl : String) : String{
        val parsedDate = LocalDateTime.parse(removeChar(tgl,".000000Z"), DateTimeFormatter.ISO_DATE_TIME)
        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        return formattedDate
    }

    fun removeChar(string : String, char : String) : String {
        val char_removeSpasi = string.replace("\\s".toRegex(),"")
        val characther = char_removeSpasi.replace(char,"")
        return characther
    }

}