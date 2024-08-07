package com.example.transletapi

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.transletapi.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var textToSpeech: TextToSpeech
    private val SPEECH_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textToSpeech = TextToSpeech(this, this)

        // Retrieve text from intent
        val textToTranslate = intent.getStringExtra("textToTranslate")
        if (!textToTranslate.isNullOrEmpty()) {
            binding.tvSourceText.text =
                Editable.Factory.getInstance().newEditable(textToTranslate)
            translateText("en", "gu", textToTranslate) // Automatically translate the text if needed
        }

        // Set click listener for the Translate button
        binding.btnTranslate.setOnClickListener {
            val text = binding.tvSourceText.text.toString()
            if (text.isNotEmpty()) {
                translateText("en", "gu", text)
            } else {
                Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for the Speaker button
        binding.ivSourceAudio.setOnClickListener {
            val text = binding.tvTranslatedText.text.toString()
            if (text.isNotEmpty()) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Toast.makeText(this, "No translated text to speak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now...")
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Speech input is not supported on this device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (result != null && result.isNotEmpty()) {
                    binding.tvSourceText.text =
                        Editable.Factory.getInstance().newEditable(result[0])
                }
            }
        }
    }

    fun translateText(source: String, target: String, text: String) {
        val transInterface =
            TransletClient.getTransletClient()?.create(TransletInterface::class.java)
        transInterface?.translateLanguage(source = source, target = target, text = text)
            ?.enqueue(object : Callback<TransletApiModel> {
                override fun onResponse(
                    call: Call<TransletApiModel>,
                    response: Response<TransletApiModel>
                ) {
                    if (response.isSuccessful) {
                        val translatedText = response.body()?.data?.translatedText
                        if (!TextUtils.isEmpty(translatedText)) {
                            binding.tvTranslatedText.text =
                                Editable.Factory.getInstance().newEditable(translatedText)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Translation failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Translation failed: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<TransletApiModel>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.US
        } else {
            Toast.makeText(this, "Text to Speech initialization failed", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
