package `in`.rgpvnotes.buddies.activities.auth

import `in`.rgpvnotes.buddies.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_fail.*

class FailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fail)

        supportActionBar!!.title = "Unable to verify number"

        val number: String = intent.getStringExtra("number")

        change_button.setOnClickListener {

            val intent = Intent(this@FailActivity,PhoneInputActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }

        resent_button.setOnClickListener {

            val intent = Intent(this@FailActivity,VerifyPhoneActivity::class.java)
            intent.putExtra("number",number)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
    }
}
