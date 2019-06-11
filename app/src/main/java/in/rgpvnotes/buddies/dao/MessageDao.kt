package `in`.rgpvnotes.buddies.dao

import `in`.rgpvnotes.buddies.model.Message
import androidx.room.Dao
import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query


@Dao
interface MessageDao {

    @Insert(onConflict = REPLACE)
    fun insertMessage(messages: Message)


    @Query("DELETE FROM message WHERE messageId = :message_id")
    fun deleteMessageWithId(message_id: String)

    //---------select query----------//

    //get last message
    @Query("SELECT * FROM Message ORDER BY serverTimestamp DESC LIMIT 1")
    fun getMessage(): Message


    //get last message
    @Query("SELECT * FROM message WHERE conversationId = :conversationId ORDER BY serverTimestamp DESC LIMIT 1")
    fun getLastMessage(conversationId: String): LiveData<List<Message>>


    //get unread count
    @Query("SELECT * FROM message WHERE conversationId = :conversationId AND receiverId =:user_id AND seen is 0")
    fun getUnreadCount(conversationId: String, user_id: String): LiveData<List<Message>>

    //get unread count
    @Query("SELECT * FROM message WHERE conversationId = :conversationId AND receiverId =:user_id AND seen is 0")
    fun getUnreadMessages(conversationId: String, user_id: String): Array<Message>

    //this method returns messages by conversation id
    @Query("SELECT * FROM message WHERE conversationId = :conversationId ORDER BY CASE WHEN serverTimestamp IS NULL THEN 0 ELSE 1 END,serverTimestamp DESC ")
    fun getConversationMessages(conversationId: String): LiveData<List<Message>>


}