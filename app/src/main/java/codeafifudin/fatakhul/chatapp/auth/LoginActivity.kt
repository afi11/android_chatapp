package codeafifudin.fatakhul.chatapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import codeafifudin.fatakhul.chatapp.MainActivity
import codeafifudin.fatakhul.chatapp.R
import codeafifudin.fatakhul.chatapp.App
import codeafifudin.fatakhul.chatapp.endpoints.EndPoints
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
    }

    private fun initView() {

        val alreadyRegister = intent.getStringExtra("alreadyRegister").toBoolean()
        if(alreadyRegister){
            Toast.makeText(this,"Already Register please login with your account",Toast.LENGTH_LONG).show()
        }

        textView_goto_register.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

        button_login_login.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = editText_email_login.text.toString()
        val password = editText_password_login.text.toString()

        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_LOGIN,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    val iduser = obj.getString("id_user")
                    val token =  obj.getJSONObject("token").getJSONObject("original").getString("token")
                        .toString()
                    addTokenIduser(iduser,token)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
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
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("email", email)
                params.put("password", password)
                return params
            }
        }

        App.instance?.addToRequestQueue(stringRequest)
    }

    private fun addTokenIduser(iduser: String, token: String) {
        val sharedPref1 = getSharedPreferences("userid", 0)
        val sharedPref2 = getSharedPreferences("token", 0)
        val editor1 = sharedPref1.edit()
        val editor2 = sharedPref2.edit()
        editor1.putString("userid",iduser)
        editor1.commit()
        editor2.putString("token","Bearer $token")
        editor2.commit()
    }

}