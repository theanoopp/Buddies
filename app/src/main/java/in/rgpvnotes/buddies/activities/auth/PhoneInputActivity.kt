package `in`.rgpvnotes.buddies.activities.auth

import `in`.rgpvnotes.buddies.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import kotlinx.android.synthetic.main.activity_phone_input.*
import android.view.inputmethod.BaseInputConnection
import android.view.KeyEvent.KEYCODE_DEL
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.numeric_keypad.*


class PhoneInputActivity : AppCompatActivity(), View.OnClickListener {

    private val textFieldInputConnection by lazy { BaseInputConnection(phoneInput, true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_input)

        phoneInput.requestFocus()
        phoneInput.showSoftInputOnFocus = false

        email_login.setOnClickListener {

            FirebaseAuth.getInstance().signInWithEmailAndPassword("theanoopp@gmail.com","123456").addOnSuccessListener {




            }

        }

        phoneInput.customSelectionActionModeCallback = object : ActionMode.Callback {

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

        phoneInput.addTextChangedListener(object :TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                buttonNext.isEnabled = s!!.length == 10
            }


        })

        buttonNext.setOnClickListener(this)
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
            phoneInput.text.clear()
            true
        }


    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            buttonNext.id -> {
                val intent = Intent(this@PhoneInputActivity,VerifyPhoneActivity::class.java)
                val number:String = phoneInput.text.toString()
                intent.putExtra("number",number)
                startActivity(intent)
            }

            button0.id -> phoneInput.append("0")

            button1.id -> phoneInput.append("1")

            button2.id -> phoneInput.append("2")

            button3.id -> phoneInput.append("3")

            button4.id -> phoneInput.append("4")

            button5.id -> phoneInput.append("5")

            button6.id -> phoneInput.append("6")

            button7.id -> phoneInput.append("7")

            button8.id -> phoneInput.append("8")

            button9.id -> phoneInput.append("9")

            imageButton.id -> textFieldInputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KEYCODE_DEL))


        }


    }
}
