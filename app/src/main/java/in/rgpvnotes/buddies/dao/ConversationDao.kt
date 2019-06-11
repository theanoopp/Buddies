package `in`.rgpvnotes.buddies.dao

import androidx.room.Dao
import `in`.rgpvnotes.buddies.model.Conversation
import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query


@Dao
interface ConversationDao {

    // set return type to know the result
    @Insert(onConflict = REPLACE)
    fun insert(conversation: Conversation)

    @Delete
    fun deleteConversation(conversation: Conversation)

    //this return a single message
    @Query("SELECT * FROM Conversation ORDER BY lastTime DESC")
    fun getConversationList(): LiveData<List<Conversation>>


    @Query("DELETE FROM conversation WHERE conversationId = :conversationId")
    fun deleteConversationWithId(conversationId: String)

}