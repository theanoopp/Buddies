package `in`.rgpvnotes.buddies.activities

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.activities.auth.PhoneInputActivity
import `in`.rgpvnotes.buddies.model.AppUser
import `in`.rgpvnotes.buddies.utils.SessionManagement
import android.Manifest.permission.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_splash.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import android.app.Activity
import android.content.ComponentName


class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()
        if(isGooglePlayServicesAvailable(this)){
//            checkOS()
            checkPermissions()
        }

    }

    private fun checkOS(){

        val manufacturer = "xiaomi"
        val my = android.os.Build.MANUFACTURER
        Log.d("MYT", " hye there $my")
        if(manufacturer.equals(android.os.Build.MANUFACTURER,ignoreCase = true)) {
            //this will open auto start screen where user can enable permission for your app
            val intent = Intent()
            intent.component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            startActivity(intent)
        }

    }

    private fun checkPermissions() {


        val listener = object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                when {
                    report.areAllPermissionsGranted() -> startApp()
                    report.isAnyPermissionPermanentlyDenied -> showSnackbar(splash_view, "Permission are needed for Buddies","settings")
                    else -> showSnackbar(splash_view, "Permission are needed for Buddies","allow")
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>,
                token: PermissionToken
            ) {

                token.continuePermissionRequest()
            }
        }


        Dexter.withActivity(this)
            .withPermissions(
                CAMERA,
                READ_CONTACTS,
                RECORD_AUDIO,
                WRITE_EXTERNAL_STORAGE
            ).withListener(listener)
            .check()


    }

    private fun isGooglePlayServicesAvailable(activity: Activity): Boolean {


        val googleApiAvailability = GoogleApiAvailability.getInstance()


        val status = googleApiAvailability.isGooglePlayServicesAvailable(activity)
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show()
            }
            return false
        }
        return true
    }

    private fun showSnackbar(view: View, text: String, callback: String) {

        val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
        if(callback == "settings"){
            snackbar.setAction("Settings") {

                val context = view.context
                val myAppSettings = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + context.packageName)
                )
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(myAppSettings)

            }
        }
        if(callback == "allow"){
            snackbar.setAction("Allow") {
                checkPermissions()
            }
        }

        snackbar.show()
    }

    private fun startApp(){

        val mAuth = FirebaseAuth.getInstance()

        val user = mAuth.currentUser

        if (user != null) {

            val myUserId = user.uid
            checkUserExist(myUserId)

        }else{
            val intent = Intent(this@SplashActivity, PhoneInputActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun checkUserExist(myUserId:String) {

        val database = FirebaseFirestore.getInstance()

        val query = database.collection("users").document(myUserId)

        query.get().addOnSuccessListener {

            val isExists = it.exists()

            if (isExists) {
                val user = it.toObject(AppUser::class.java)
                SessionManagement.saveStudent(this@SplashActivity, user)
                val intent = Intent(this@SplashActivity, ConversationsActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@SplashActivity, AccountSetupActivity::class.java)
                startActivity(intent)
                finish()
            }


        }.addOnFailureListener {

            Log.d("MYT", it.localizedMessage)
            Toast.makeText(this@SplashActivity, "Something bad happened", Toast.LENGTH_SHORT).show()

        }

    }
}
