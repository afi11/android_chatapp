package codeafifudin.fatakhul.chatapp

import android.app.Application
import android.util.Log
import codeafifudin.fatakhul.chatapp.endpoints.EndPoints
import codeafifudin.fatakhul.chatapp.utils.Auth
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class App : Application(), LifeCycleDelegate {

    val auth = Auth()

    override fun onCreate() {
        super.onCreate()
        instance = this
        val lifecycleHandler = AppLifecycleHandler(this)
        registerLifecycleHandler(lifecycleHandler)
    }

    val requestQueue: RequestQueue? = null
        get() {
            if (field == null) {
                return Volley.newRequestQueue(applicationContext)
            }
            return field
        }

    fun <T> addToRequestQueue(request: Request<T>) {
        request.tag = TAG
        requestQueue?.add(request)
    }

    companion object {
        private val TAG = App::class.java.simpleName
        @get:Synchronized var instance: App? = null
            private set
    }

    private fun updateOnline(state: String) {
        val jsonBody = JSONObject()
        jsonBody.put("iduser", auth.getIdUser(this))

        var url =""
        if(state.equals("online")){
            url = EndPoints.URL_UPDATE_ONLINE
        }else{
            url = EndPoints.URL_UPDATE_OFFLINE
        }

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
                url, jsonBody, Response.Listener { response ->

        }, Response.ErrorListener { error -> }){
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth.getToken(this@App)
                params["content-type"] = "application/json"
                return params
            }
        }
        addToRequestQueue(jsonRequest)
    }

    override fun onAppBackgrounded() {
        Log.d("Awwww","App in background")
        updateOnline("offline")
    }

    override fun onAppForegrounded() {
        Log.d("Yeey","App in foreground")
        updateOnline("online")
    }

    private fun registerLifecycleHandler(lifecycleHandler: AppLifecycleHandler){
        registerActivityLifecycleCallbacks(lifecycleHandler)
        registerComponentCallbacks(lifecycleHandler)
    }

}