package codeafifudin.fatakhul.chatapp.endpoints

object EndPoints {
    private val URL_ROOT = "http://192.168.10.60/api/"
    val URL_LOGIN = URL_ROOT+"login"
    val URL_REGISTER = URL_ROOT+"register"
    val URL_LET_PERSON_MESSAGE = URL_ROOT+"letmessage"
    val URL_LATEST_PERSON_MESSAGE = URL_ROOT+"latestmessage"
    val URL_LATEST_MESSAGE = URL_ROOT+"getlatetsmessage"
    val URL_COUNT_UNREADMESSAGE = URL_ROOT+"countunreadmessage"

    val URL_UPDATE_ONLINE = URL_ROOT+"updateuseronline"
    val URL_UPDATE_OFFLINE = URL_ROOT+"updateuserclose"
    val URL_GET_USER = URL_ROOT+"getuserbyid"

    val URL_UPDATE_PROFIL = URL_ROOT+"update_profil"
    val URL_LOGOUT = URL_ROOT+"logout"

    val URL_GET_CHAT = URL_ROOT+"getchat"
    val URL_SEND_CHAT = URL_ROOT+"sendchat"
    val URL_READ_MESSAGE = URL_ROOT+"readmessage"

    val URL_TEST = URL_ROOT+"testapi"

    val URL_WEB = "http://192.168.43.140/"
}