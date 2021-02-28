package codeafifudin.fatakhul.chatapp.messages

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import codeafifudin.fatakhul.chatapp.R
import codeafifudin.fatakhul.chatapp.App
import codeafifudin.fatakhul.chatapp.adapter.ItemMessageFromUser
import codeafifudin.fatakhul.chatapp.adapter.ItemMessageToUser
import codeafifudin.fatakhul.chatapp.endpoints.EndPoints
import codeafifudin.fatakhul.chatapp.models.ItemMessage
import codeafifudin.fatakhul.chatapp.models.LatestMessage
import codeafifudin.fatakhul.chatapp.utils.Auth
import codeafifudin.fatakhul.chatapp.utils.Converter
import codeafifudin.fatakhul.chatapp.utils.media.MediaCamera
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.json.JSONException
import org.json.JSONObject


class ChatLogActivity : AppCompatActivity() {

    val auth = Auth()
    val converter = Converter()
    lateinit var mediaCamera: MediaCamera
    var fileUri = Uri.parse("")
    var imgSTr = ""
    val url_message_image = EndPoints.URL_WEB+"messages/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        FirebaseMessaging.getInstance().subscribeToTopic("FirebaseMessaging")

        initView()

        getChat()

        backgroundStatusUser()
        backgroundToReadMessage()
    }

    override fun onResume() {
        super.onResume()
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener (
            OnCompleteListener { task ->
                if(!task.isSuccessful) return@OnCompleteListener
            }
        )
    }

    private fun backgroundToReadMessage(){
        val handler = Handler()
        val user = intent.getParcelableExtra<LatestMessage>("detUser")
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                readMessage(user!!.iduser)
                handler.postDelayed(this, 5000)
            }
        }
        handler.post(runnableCode)
    }

    private fun backgroundStatusUser(){
        val handler = Handler()
        val user = intent.getParcelableExtra<LatestMessage>("detUser")
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                getStatusUser(user!!.iduser)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnableCode)
    }

    private fun initView() {

        try{
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        }catch (e: Exception){
            e.printStackTrace()
        }
        mediaCamera = MediaCamera()

        // Appbar

        val user = intent.getParcelableExtra<LatestMessage>("detUser")
        textView_Username_chatlog.setText(user!!.name)
        Picasso.get().load(user!!.photo).into(imageView_person)

        relativeLayout_chat_log.visibility = View.GONE

        back_to_previous_chatlog.setOnClickListener {
            onBackPressed()
        }

        button_close_preview_img.setOnClickListener {
            imgSTr = ""
            relativeLayout_chat_log.visibility = View.GONE
        }

        button_send_message.setOnClickListener {
            sendMessage()
        }

        button_take_picture.setOnClickListener {
            requestPermission()
        }
    }

    private fun getStatusUser(receiver: String){
        val stringRequest = object : StringRequest(Request.Method.GET,EndPoints.URL_GET_USER+"/"+receiver,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    var status = obj.getJSONObject("data").getString("login_at").toString()
                    if(status.equals("null")){
                        textView_StatusUsr_chatlog.setText("")
                    }else{
                        textView_StatusUsr_chatlog.setText("Online")
                    }
                }catch (e:JSONException){
                    e.printStackTrace()
                }
            },object : Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Toast.makeText(applicationContext, error?.message, Toast.LENGTH_LONG).show()
            }
        }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth.getToken(this@ChatLogActivity)
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(stringRequest)
    }

    private fun readMessage(receiver: String){
        val stringRequest = object : StringRequest(Request.Method.GET,
            EndPoints.URL_READ_MESSAGE + "/" + receiver,
            Response.Listener { s ->
                try {
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("data")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Toast.makeText(applicationContext, error?.message, Toast.LENGTH_LONG).show()
                }
            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth.getToken(this@ChatLogActivity)
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(stringRequest)
    }

    private fun getChat(){
        val adapter = GroupAdapter<GroupieViewHolder>()
        val user = intent.getParcelableExtra<LatestMessage>("detUser")
        var image_message = ""
        var image = ""
        val stringRequest = @RequiresApi(Build.VERSION_CODES.O)
        object : StringRequest(
            Request.Method.GET, EndPoints.URL_GET_CHAT + "/" + user!!.iduser,
            Response.Listener { s ->
                try {
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("data")
                    for (i in 0..array.length() - 1) {
                        val objectMessage = array.getJSONObject(i)
                        image = objectMessage.getString("image")
                        if (image != "null") {
                            image_message = url_message_image + image
                        } else {
                            image_message = ""
                        }
                        val itemMessage = ItemMessage(
                            objectMessage.getString("chatId"),
                            objectMessage.getString(
                                "sender"
                            ),
                            objectMessage.getString("receiver"),
                            "",
                            objectMessage.getString(
                                "messages"
                            ),
                            "",
                            image_message,
                            converter.forMessage(objectMessage.getString("created_at")),
                            objectMessage.getString(
                                "is_read"
                            )
                        )
                        if (objectMessage.getString("sender").equals(auth.getIdUser(this))) {
                            adapter.add(ItemMessageToUser(itemMessage))
                        } else {
                            adapter.add(ItemMessageFromUser(itemMessage))
                        }
                        recyclerview_dataLogChat.adapter = adapter
                    }
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
                params["Authorization"] = auth.getToken(this@ChatLogActivity)
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(stringRequest)
    }

    private fun sendMessage(){
        val jsonBody = JSONObject()
        val messages = editText_Input_Message_LogMessage.text.toString()
        val user = intent.getParcelableExtra<LatestMessage>("detUser")
        jsonBody.put("messages", messages)
        jsonBody.put("image", imgSTr)
        jsonBody.put("receiver", user!!.iduser)

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            EndPoints.URL_SEND_CHAT,
            jsonBody,
            Response.Listener { response -> //now handle the response
                getChat()
                sendNotification(messages)
                editText_Input_Message_LogMessage.setText("")
            },
            Response.ErrorListener { error -> //handle the error
                Toast.makeText(this@ChatLogActivity, error.toString(), Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }) {
            //this is the part, that adds the header to the request
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth.getToken(this@ChatLogActivity)
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(jsonRequest)
    }

    private fun sendNotification(message : String){
        var form_message = ""
        if(message != "") form_message = message
        else form_message = ""
        val user = intent.getParcelableExtra<LatestMessage>("detUser")
        val jsonBody = JSONObject()
        val jsonDataMessage = JSONObject()
        jsonDataMessage.put("iduser",user!!.iduser)
        jsonDataMessage.put("name",user!!.name)
        jsonDataMessage.put("photo",user!!.photo)
        jsonDataMessage.put("message",message)
        jsonDataMessage.put("waktu","2020-11-10 19:10:10")
        jsonDataMessage.put("num_unread_message","0")

        jsonBody.put("to", "/topics/FirebaseMessaging")
        jsonBody.put("data", jsonDataMessage)

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            "https://fcm.googleapis.com/fcm/send",
            jsonBody,
            Response.Listener { response -> //now handle the response
            },
            Response.ErrorListener { error -> //handle the error
                Toast.makeText(this@ChatLogActivity, error.toString(), Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }) {
            //this is the part, that adds the header to the request
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "key=AAAAhpXbw_0:APA91bF9WqY2tVymc1NG4TWPr6pQSlVU_FGaR9NM8wIwbC-rA7rB0dO58AtTnDDcB6Nh6m9yPl-yrgJD-DLNaKDyxIOGvB6WcaMV9MtIIfGVlZf0f5xS5LcAghennywmAQpQSUGd1HlR"
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(jsonRequest)
    }

    fun requestPermission() = runWithPermissions(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    ){
        fileUri = mediaCamera.getOutputMediaFileUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(intent, mediaCamera.getRcCamera())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK)
            if(requestCode == mediaCamera.getRcCamera()){
                imgSTr = mediaCamera.getBitmapToSting(imageView_message, fileUri)
                textView_imageName.setText(mediaCamera.getMyFileName())
                relativeLayout_chat_log.visibility = View.VISIBLE
            }
    }


}