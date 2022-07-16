package kr.rabbito.speechrecognizertest

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kr.rabbito.speechrecognizertest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    private val PERM_REQ_CODE = 100

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognitionListener: RecognitionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 안드로이드 6.0 이상인 경우 권한 확인
        checkPermission(PERM_REQ_CODE)

        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")   // 언어 설정

        // RecognitionListener 설정
        setListener()
        // SpeechRecognizer 설정
        setRecognizer()

        // 음성인식 시작
        binding.mainClTopBtnRecord.setOnClickListener { startListening(intent) }
    }

    private fun checkPermission(requestCode: Int) {
        if (Build.VERSION.SDK_INT >= 23)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO),
                requestCode
            )
    }

    private fun setListener() {
        recognitionListener = object : RecognitionListener {

            // 말하기 시작할 준비가되면(SpeechRecognizer의 startListening() 호출 시) 호출
            override fun onReadyForSpeech(params: Bundle?) {
                Toast.makeText(applicationContext, "음성인식 시작", Toast.LENGTH_SHORT).show()
            }

            // 말하기 시작했을 때 호출
            override fun onBeginningOfSpeech() {}

            // 소리 크기가 변경되었을 때 호출
            override fun onRmsChanged(rmsdB: Float) {}

            // 새 소리가 들어왔을 때 호출 - 말을 시작하고 인식된 단어를 buffer에 담음
            override fun onBufferReceived(buffer: ByteArray?) {}

            // 말하기가 끝났을 때 호출
            override fun onEndOfSpeech() {}

            // 오류가 발생했을 때 호출
            override fun onError(error: Int) {
                val message: String

                when (error) {
                    SpeechRecognizer.ERROR_AUDIO ->
                        message = "AUDIO ERROR"
                    SpeechRecognizer.ERROR_CLIENT ->
                        message = "CLIENT ERROR"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                        message = "PERMISSION INSUFFICIENT ERROR"
                    SpeechRecognizer.ERROR_NETWORK ->
                        message = "NETWORK ERROR"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                        message = "NETWORK TIMEOUT"
                    SpeechRecognizer.ERROR_NO_MATCH ->
                        message = "NO MATCH ERROR"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                        message = "RECOGNIZER BUSY ERROR"
                    SpeechRecognizer.ERROR_SERVER ->
                        message = "SERVER ERROR"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                        message = "SPEECH TIMEOUT ERROR"
                    else ->
                        message = "UNKNOWN ERROR"
                }
                Toast.makeText(applicationContext, "Error: $message", Toast.LENGTH_SHORT).show()
            }

            // 인식 결과가 준비되면 호출
            override fun onResults(results: Bundle?) {
                // 음성 -> ArrayList -> TextView
                var matches: ArrayList<String> =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
                for (i in 0 until matches.size) {
                    binding.mainClTopTvResult.text = matches[i]
                }
            }

            // 부분 인식 결과를 사용할 수 있을 때 호출
            override fun onPartialResults(partialResults: Bundle?) {}

            // 향후 이벤트를 추가하기 위해 예약
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun setRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(recognitionListener)
    }

    private fun startListening(intent: Intent) {
        speechRecognizer.startListening(intent)
    }
}