package com.example.transletapi

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.transletapi.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btn.setOnClickListener {
            var text = binding.edtext.text.toString()
            transletapicall("en", "hi", text)
        }
    }
}

fun transletapicall(source: String, target: String, text: String){

    var transletClient = TransletClient.getTransletClient()?.create(TransletInterface::class.java)

    transletClient?.translateLanguage(source = source, target =target,text = text)!!.enqueue(object : Callback<TransletApiModel> {
        override fun onResponse(
            call: Call<TransletApiModel>,
            response: Response<TransletApiModel>
        ) {
            if (response.isSuccessful)
            {
                val model = response.body()?.data!!.translatedText
            }
           else
           {
                Log.e("Error", "onResponse: is failed")
            }
        }

        override fun onFailure(call: Call<TransletApiModel>, t: Throwable) {

            Log.e("TAG", "onFailure:=${t.message} ")
        }

    })
    }
