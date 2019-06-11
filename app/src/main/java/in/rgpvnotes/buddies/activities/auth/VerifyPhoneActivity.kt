package `in`.rgpvnotes.buddies.activities.auth

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.activities.AccountSetupActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.BaseInputConnection
import android.widget.Toast
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_verify_phone.*
import kotlinx.android.synthetic.main.numeric_keypad.*
import java.util.concurrent.TimeUnit

class VerifyPhoneActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mVerificationId: String

    private val textFieldInputConnection by lazy { BaseInputConnection(otp_input, true) }

    private lateinit var timer:CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)

        val number: String = intent.getStringExtra("number")

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportActionBar!!.title = "Verify phone number"

        otp_text.text = "Enter OTP sent to +91 $number"

        otp_input.requestFocus()
        otp_input.showSoftInputOnFocus = false

        otp_input.customSelectionActionModeCallback = object : ActionMode.Callback {

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {}

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return false
            }
        }


        button_setup.setOnClickListener(this)
        button0.setOnClickListener(this)
        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)
        button4.setOnClickListener(this)
        button5.setOnClickListener(this)
        button6.setOnClickListener(this)
        button7.setOnClickListener(this)
        button8.setOnClickListener(this)
        button9.setOnClickListener(this)
        imageButton.setOnClickListener(this)

        imageButton.setOnLongClickListener {
            otp_input.text.clear()
            true
        }

        otp_input.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                button_setup.isEnabled = s!!.length == 6
            }


        })

        sendVerificationCode(number)

        timer = object: CountDownTimer(100000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                time_view.text = ""+millisUntilFinished / 1000
            }

            override fun onFinish() {

                val intent = Intent(this@VerifyPhoneActivity,FailActivity::class.java)
                intent.putExtra("number",number)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            }
        }
        timer.start()

    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            button_setup.id -> {

                val code = otp_input.text.toString()
                verifyCode(code)
            }

            button0.id -> otp_input.append("0")

            button1.id -> otp_input.append("1")

            button2.id -> otp_input.append("2")

            button3.id -> otp_input.append("3")

            button4.id -> otp_input.append("4")

            button5.id -> otp_input.append("5")

            button6.id -> otp_input.append("6")

            button7.id -> otp_input.append("7")

            button8.id -> otp_input.append("8")

            button9.id -> otp_input.append("9")

            imageButton.id -> textFieldInputConnection.sendKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_DEL
                )
            )


        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timer.cancel()
    }

    private fun sendVerificationCode(number: String) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91$number",
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallbacks
        )

    }

    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(verificationId: String?, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(verificationId, forceResendingToken)
            mVerificationId = verificationId!!
        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            val code = phoneAuthCredential.smsCode

            signInWithPhoneAuthCredential(phoneAuthCredential)

            if (code != null) {
                otp_input.setText(code)
                verifyCode(code)
            }


        }

        override fun onVerificationFailed(e: FirebaseException) {


            when (e) {
                is FirebaseAuthInvalidCredentialsException -> errorText.text = e.message
                is FirebaseAuthInvalidUserException -> errorText.text = "Invalid mobile number"
                else -> {
                    errorText.text = e.message
                    Log.d("error", e.localizedMessage)
                }
            }

            errorText.visibility = View.VISIBLE

        }


    }

    private fun verifyCode(code: String) {

        val phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, code)

        signInWithPhoneAuthCredential(phoneAuthCredential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        val auth = FirebaseAuth.getInstance()

        auth.signInWithCredential(credential).addOnSuccessListener(this) {

            timer.cancel()

            val intent = Intent(this@VerifyPhoneActivity, AccountSetupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)


        }.addOnFailureListener {

            if (it is FirebaseAuthInvalidCredentialsException) {

//                codeProgress.visibility = View.INVISIBLE

                errorText.text = it.message

                errorText.visibility = View.VISIBLE

            }
        }
    }


}
