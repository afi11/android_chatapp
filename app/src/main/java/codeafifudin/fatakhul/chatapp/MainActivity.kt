package codeafifudin.fatakhul.chatapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import codeafifudin.fatakhul.chatapp.adapter.UserLatestMessage
import codeafifudin.fatakhul.chatapp.auth.LoginActivity
import codeafifudin.fatakhul.chatapp.auth.ProfilActivity
import codeafifudin.fatakhul.chatapp.endpoints.EndPoints
import codeafifudin.fatakhul.chatapp.messages.ChatLogActivity
import codeafifudin.fatakhul.chatapp.models.LatestMessage
import codeafifudin.fatakhul.chatapp.utils.Auth
import codeafifudin.fatakhul.chatapp.utils.Converter
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val converter = Converter()
    val auth = Auth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cekUserIsLoggin()
    }

    interface VolleyCallback {
        fun onSuccess(result: String?)
    }

    override fun onStart() {
        super.onStart()
        getPersonLatestMessage()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        menuInflater.inflate(R.menu.menu_options, menu)
        val searchItem = menu?.findItem(R.id.app_bar_search)
        if (searchItem != null) {
            val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
            searchView.setOnCloseListener(object : SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    val adapter = GroupAdapter<GroupieViewHolder>()
                    adapter.clear()
                    getPersonLatestMessage()
                    return false
                }
            })

            val searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
            searchPlate.hint = "Cari Orang...."
            val searchPlateView: View = searchView.findViewById(androidx.appcompat.R.id.search_plate)
            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val adapter = GroupAdapter<GroupieViewHolder>()
                    adapter.clear()
                    recylerviewDataHome.adapter = adapter
                    if (newText!!.isNotEmpty()) {
                        val search = newText.toLowerCase()
                        getPersontoMessage(search)
                    }
                    return true
                }

            })

            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_profil -> {
                val intent = Intent(this, ProfilActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_logout -> {
                logout()
                removeTokenIduser()
                recreate()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout(){
        val jsonBody = JSONObject()
        val token = converter.removeChar(auth.getToken(this),"Bearer")
        jsonBody.put("token",token)

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
            EndPoints.URL_LOGOUT, jsonBody, Response.Listener { response ->
            try {
                val array = response.getJSONArray("data")
            }catch (e:JSONException){
                e.printStackTrace()
            }
        },Response.ErrorListener { error -> //handle the error
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            error.printStackTrace()
        }){
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = getToken()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(jsonRequest)
    }

    private fun getPersontoMessage(search : String){
        val jsonBody = JSONObject()
        jsonBody.put("search_name", search)
        val adapter = GroupAdapter<GroupieViewHolder>()

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
                EndPoints.URL_LET_PERSON_MESSAGE, jsonBody, Response.Listener { response -> //now handle the response
            try {
                val array = response.getJSONArray("data")
                for (i in 0..array.length() - 1) {
                    val objectMessage = array.getJSONObject(i)

                    val latestMessage = LatestMessage(
                            objectMessage.getString("id"), objectMessage.getString("name"),
                            "http://192.168.43.140/profil/" + objectMessage.getString("photo"),"",
                            "", "0")
                    adapter.add(UserLatestMessage(latestMessage))
                    adapter.setOnItemClickListener { item, view ->
                        val userItem = item as UserLatestMessage
                        val intent = Intent(this,ChatLogActivity::class.java)
                        intent.putExtra("detUser",userItem.latestMessage)
                        startActivity(intent)
                    }
                    recylerviewDataHome.adapter = adapter
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error -> //handle the error
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            error.printStackTrace()
        }) {
            //this is the part, that adds the header to the request
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = getToken()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(jsonRequest)
    }

    private fun getPersonLatestMessage(){
        val adapter = GroupAdapter<GroupieViewHolder>()
        val stringRequest =  @RequiresApi(Build.VERSION_CODES.O) object : StringRequest(
                Request.Method.GET, EndPoints.URL_LATEST_PERSON_MESSAGE,
                Response.Listener<String> { s ->
                    try {
                        val obj = JSONObject(s)
                        val array = obj.getJSONArray("data")
                        for (i in 0..array.length() - 1) {
                            val objectMessage = array.getJSONObject(i)

                            getLatestMessage(objectMessage.getString("id"),object : VolleyCallback{
                                override fun onSuccess(message: String?) {
                                    getCountUnreadMessage(objectMessage.getString("id"),object : VolleyCallback{
                                        override fun onSuccess(unreadmessage: String?) {
                                            val latestMessage = LatestMessage(
                                                    objectMessage.getString("id"), objectMessage.getString("name"),
                                                     "http://192.168.10.60/profil/" + objectMessage.getString("photo"),message.toString(),
                                                    objectMessage.getString("last_chat_at"), unreadmessage.toString()
                                            )
                                            adapter.add(UserLatestMessage(latestMessage))
                                        }
                                    })
                                }
                            })
                            adapter.setOnItemClickListener { item, view ->
                                val userItem = item as UserLatestMessage
                                val intent = Intent(this,ChatLogActivity::class.java)
                                intent.putExtra("detUser",userItem.latestMessage)
                                startActivity(intent)
                            }
                            recylerviewDataHome.adapter = adapter
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        //Toast.makeText(applicationContext, error?.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = getToken()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(stringRequest)
    }

    private fun getLatestMessage(sender: String, callback : VolleyCallback){
        val receiver = getIdUser()
        val stringRequest = object : StringRequest(
                Request.Method.GET, EndPoints.URL_LATEST_MESSAGE + "/$sender/$receiver",
                Response.Listener<String> { s ->
                    try {
                        val obj = JSONObject(s)
                        val objMessage = obj.getJSONObject("data").getString("messages")
                        callback.onSuccess(objMessage)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        Toast.makeText(applicationContext, error?.message, Toast.LENGTH_LONG).show()
                    }
                }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = getToken()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(stringRequest)
    }

    private fun getCountUnreadMessage(sender: String, callback : VolleyCallback){
        val receiver = getIdUser()
        val stringRequest = object : StringRequest(
                Request.Method.GET, EndPoints.URL_COUNT_UNREADMESSAGE + "/$sender/$receiver",
                Response.Listener<String> { s ->
                    try {
                        val obj = JSONObject(s)
                        val objCountUnreadMessage = obj.getString("data")
                        callback.onSuccess(objCountUnreadMessage)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        Toast.makeText(applicationContext, error?.message, Toast.LENGTH_LONG).show()
                    }
                }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = getToken()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(stringRequest)
    }

    private fun getIdUser() : String {
        val sharedPref = getSharedPreferences("userid", 0)
        var userid = sharedPref.getString("userid", "")
        return userid.toString()
    }

    private fun getToken() : String {
        val sharedPref = getSharedPreferences("token", 0)
        var token = sharedPref.getString("token", "")
        return token.toString()
    }

    private fun removeTokenIduser() {
        val sharedPref1 = getSharedPreferences("userid", 0)
        val sharedPref2 = getSharedPreferences("token", 0)
        val editor1 = sharedPref1.edit()
        val editor2 = sharedPref2.edit()
        editor1.putString("userid","")
        editor1.commit()
        editor2.putString("token","")
        editor2.commit()
    }

    private fun cekUserIsLoggin(){
        val sharedPref = getSharedPreferences("token", 0)
        var token = sharedPref.getString("token", "")
        if(token.equals("")){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}