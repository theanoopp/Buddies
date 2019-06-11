package `in`.rgpvnotes.buddies.activities

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.dialogs.MyProgressDialog
import `in`.rgpvnotes.buddies.model.AppUser
import `in`.rgpvnotes.buddies.utils.SessionManagement
import `in`.rgpvnotes.buddies.utils.TokenUtils
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_account_setup.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class AccountSetupActivity : AppCompatActivity() {

    private val mStorageRef by lazy { FirebaseStorage.getInstance().reference }

    private val userId by lazy { FirebaseAuth.getInstance().uid!! }

    private val database by lazy { FirebaseFirestore.getInstance() }

    private val galleryRequest = 1

    private var uri: Uri? = null

    private var profileUri = "default"
    private var thumbUri = "default"
    private lateinit var thumbByte: ByteArray

    private lateinit var dialog: MyProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setup)

        supportActionBar!!.title = "Setup your account"

        dialog = MyProgressDialog(this@AccountSetupActivity)


        dialog.setTitle("Loading")
        dialog.setMessage("Please wait while we load user's information")

        dialog.show()


        database.collection("users").document(userId).get().addOnSuccessListener {

            if (it.exists()) {
                FirebaseInstanceId.getInstance()
                    .instanceId.addOnSuccessListener(this@AccountSetupActivity) { instanceIdResult ->
                    val newToken = instanceIdResult.token
                    TokenUtils.addTokenToFirestore(newToken)
                }

                val user = it.toObject(AppUser::class.java)!!
                nameInput.editText!!.setText(user.userName)
                frag_statusInput.editText!!.setText(user.status)
            }
            dialog.dismiss()


        }.addOnFailureListener {
            dialog.dismiss()
        }


        profile_image.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_PICK
            galleryIntent.type = "image/*"
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), galleryRequest)
        }

        setupButton.setOnClickListener {
            if (checkInputs()) {

                uploadImages()

            }
        }


    }

    private fun uploadImages() {


        setupButton.isEnabled = false
        nameInput.isEnabled = false
        frag_statusInput.isEnabled = false
        profile_image.isEnabled = false

        setupButton.text = "Uploading Images"


        if (uri != null) {

            val thumbFile = File(uri!!.path)

            try {
                val thumbBitmap = Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(50)
                    .compressToBitmap(thumbFile)

                val byteArray = ByteArrayOutputStream()

                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArray)
                thumbByte = byteArray.toByteArray()

            } catch (e: IOException) {
                e.printStackTrace()
            }


            val picRef = mStorageRef.child("images/profileImages/$userId.jpg")
            val thumbRef = mStorageRef.child("images/profileImages/thumbs/$userId.jpg")

            picRef.putFile(uri!!).addOnCompleteListener { pic ->
                if (pic.isSuccessful) {
                    picRef.downloadUrl.addOnCompleteListener { picD ->
                        if (picD.isSuccessful) {
                            profileUri = picD.result.toString()
                            thumbRef.putBytes(thumbByte).addOnCompleteListener { thumb ->
                                if (thumb.isSuccessful) {
                                    thumbRef.downloadUrl.addOnCompleteListener { thumbD ->
                                        if (thumbD.isSuccessful) {
                                            thumbUri = thumbD.result.toString()
                                        }
                                        updateUser()
                                    }

                                } else {
                                    updateUser()
                                }


                            }
                        } else {
                            updateUser()
                        }

                    }
                } else {
                    updateUser()
                }

            }.addOnProgressListener { taskSnapshot ->
//                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            }
        } else {

            updateUser()

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == galleryRequest && resultCode == RESULT_OK) {

            val imageUri = data!!.data

            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(1, 1).start(this)


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {
                uri = result.uri
                profile_image.setImageURI(uri)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this@AccountSetupActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUser() {

        setupButton.text = "Few seconds more..."

        var status = frag_statusInput.editText!!.text.toString().trim()

        val name = nameInput.editText!!.text.toString().trim()

        if (status == "") {
            status = "hey there..."
        }

        val phone = FirebaseAuth.getInstance().currentUser!!.phoneNumber

        val userObject = AppUser(userId, phone, name, status, profileUri, thumbUri, true)

        database.collection("users")
            .document(userId)
            .set(userObject)
            .addOnSuccessListener {
                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {

                    val registrationToken = it.token
                    TokenUtils.addTokenToFirestore(registrationToken)

                }

                SessionManagement.saveStudent(this@AccountSetupActivity, userObject)

                val intent = Intent(this@AccountSetupActivity, ConversationsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)


            }.addOnFailureListener { e ->
                //                headingText.text = e.localizedMessage
//                progressInfo.visibility = View.GONE
            }


    }

    private fun checkInputs(): Boolean {

        nameInput.error = null

        val name = nameInput.editText!!.text.toString().trim()

        return if (name.length < 3) {

            nameInput.error = "Enter valid name"
            false

        } else {

            true

        }
    }
}
