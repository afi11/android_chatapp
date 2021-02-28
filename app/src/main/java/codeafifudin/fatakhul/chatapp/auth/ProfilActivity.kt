package codeafifudin.fatakhul.chatapp.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import codeafifudin.fatakhul.chatapp.App
import codeafifudin.fatakhul.chatapp.R
import codeafifudin.fatakhul.chatapp.endpoints.EndPoints
import codeafifudin.fatakhul.chatapp.utils.Auth
import codeafifudin.fatakhul.chatapp.utils.media.MediaGalery
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profil.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.Throws

class ProfilActivity : AppCompatActivity() {

    lateinit var mediaGalery: MediaGalery
    var imgSTr = ""
    var oldImage = ""
    val auth = Auth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        initView()
    }

    private fun initView() {
        supportActionBar!!.title = "Edit Profil"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        try{
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        }catch (e: Exception){
            e.printStackTrace()
        }
        mediaGalery = MediaGalery(this)

        getProfilData()

        btnUploadProfilImg.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,mediaGalery.getRcGallery())
        }

        button_update_profil.setOnClickListener {
            updateData()
        }
    }

    private fun getProfilData(){
        val stringRequest = object : StringRequest(Request.Method.GET, EndPoints.URL_GET_USER+"/"+auth.getIdUser(this),
            Response.Listener<String> {response ->
                try {
                    val obj = JSONObject(response).getJSONObject("data")
                    val namalengkap = obj.getString("name")
                    val email = obj.getString("email")
                    imgSTr = obj.getString("photo")
                    oldImage = obj.getString("photo")
                    val url_image = EndPoints.URL_WEB+"/profil/"+obj.getString("photo")

                    editTextNama_profil.setText(namalengkap)
                    editTextEmail_profil.setText(email)
                    Picasso.get().load(url_image).into(imageViewProfil)

                }catch (e:JSONException){
                    e.printStackTrace()
                }
            }, object : Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Toast.makeText(applicationContext, error?.message, Toast.LENGTH_LONG).show()
            }
        }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params : MutableMap<String, String> = HashMap()
                params["Authorization"] = auth.getToken(this@ProfilActivity)
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(stringRequest)
    }

    private fun updateData(){
        val jsonBody = JSONObject()
        val namalengkap = editTextNama_profil.text.toString()
        val email = editTextEmail_profil.text.toString()
        val password = editTextPassword_profil.text.toString()

        jsonBody.put("name",namalengkap)
        jsonBody.put("email",email)
        jsonBody.put("password",password)
        jsonBody.put("photo",imgSTr)
        jsonBody.put("old_photo",oldImage)

        val jsonRequest : JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, EndPoints.URL_UPDATE_PROFIL,jsonBody, Response.Listener { response ->
                Toast.makeText(this,"Successfull to update profil",Toast.LENGTH_LONG).show()
            },Response.ErrorListener { error ->
            Toast.makeText(this@ProfilActivity, error.toString(), Toast.LENGTH_SHORT).show()
            error.printStackTrace()
        }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth.getToken(this@ProfilActivity)
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(jsonRequest)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == mediaGalery.getRcGallery()){
                imgSTr = mediaGalery.getBitmapToString(data!!.data,imageViewProfil)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}