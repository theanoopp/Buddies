package `in`.rgpvnotes.buddies.utils

import `in`.rgpvnotes.buddies.model.Contact
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import java.lang.Exception

object ContactUtil {

    fun getContactName(context: Context, phoneNumber: String): String {

        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val cursor =
            context.contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        var contactName = ""
        if (cursor != null && cursor.count > 0) {

            cursor.use {
                if (cursor.moveToFirst()) {
                    contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                }
            }
        }

        return if (contactName == "") {
            phoneNumber
        } else {
            contactName
        }
    }


    fun fetchContacts(context: Context): ArrayList<Contact> {
        val mContactList = arrayListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        if (cursor != null && cursor.count > 0) {

            cursor.use {
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (cursor.moveToNext()) {

                    val name = cursor.getString(nameIndex)
                    var number = cursor.getString(numberIndex)

                    number = number.replace("""[ ,-]""".toRegex(), "")

                    val contact = Contact(name)

                    when {
                        number.length == 10 -> number = "+91$number"
                        number[0] == '0' -> {
                            if (number.length > 5) {
                                val srt = number.substring(1)
                                number = "+91$srt"
                            }
                        }
                    }

                    contact.number = number
                    mContactList.add(contact)


                }
            }
        }

        return mContactList
    }

}