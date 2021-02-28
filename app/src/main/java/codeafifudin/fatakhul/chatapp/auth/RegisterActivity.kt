package codeafifudin.fatakhul.chatapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import codeafifudin.fatakhul.chatapp.R
import codeafifudin.fatakhul.chatapp.App
import codeafifudin.fatakhul.chatapp.endpoints.EndPoints
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.Throws

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initView()

    }

    private fun initView() {
        back_to_previous.setOnClickListener {
            onBackPressed()
        }

        button_register_register.setOnClickListener {
            performRegister()
        }
    }

    private fun performRegister() {
        val password = editText_password_register.text.toString()
        val repeat_password = editText_repeat_password_register.text.toString()
        if(password.equals(repeat_password)){
            val name = editText_name_register.text.toString()
            val email = editText_email_register.text.toString()

            val stringRequest = object : StringRequest(Request.Method.POST, EndPoints.URL_REGISTER,
                Response.Listener<String> { response ->
                    try {
                        val obj = JSONObject(response)
                        val code = obj.getString("code").toInt()
                        if(code == 200){
                            val intent = Intent(this,LoginActivity::class.java)
                            intent.putExtra("alreadyRegister",true)
                            startActivityForResult(intent,100)
                        }else{
                            Toast.makeText(this,"Account already registered!",Toast.LENGTH_LONG).show()
                        }
                    }catch (e: JSONException){
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
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params.put("name",name)
                    params.put("email",email)
                    params.put("password",password)
                    params.put("password_confirmation",repeat_password)
                    return params
                }
            }

            App.instance?.addToRequestQueue(stringRequest)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}