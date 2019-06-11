package `in`.rgpvnotes.buddies.database

import `in`.rgpvnotes.buddies.dao.ConversationDao
import `in`.rgpvnotes.buddies.dao.MessageDao
import `in`.rgpvnotes.buddies.model.Conversation
import `in`.rgpvnotes.buddies.model.Message
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Message::class, Conversation::class], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {

                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "messages"
                )
                    .addCallback(CALLBACK)
                    .build()
            }

            return INSTANCE!!
        }

        private val CALLBACK = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
//                db.execSQL("CREATE TRIGGER IF NOT EXISTS conver AFTER INSERT ON Message BEGIN INSERT INTO Conversation(conversationId,lastTime) VALUES(new.conversationId,new.serverTimestamp); END;")
            }
        }
    }


    fun destroyInstance() {
        INSTANCE = null
    }

    abstract fun messageDao(): MessageDao

    abstract fun conversationDao(): ConversationDao
}