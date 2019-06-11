package in.rgpvnotes.buddies.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Conversation {

    @PrimaryKey @NonNull
    private String conversationId;

    private Date lastTime;

    @Ignore
    public Conversation() {
    }

    public Conversation(@NonNull String conversationId, Date lastTime) {
        this.conversationId = conversationId;
        this.lastTime = lastTime;
    }

    @NonNull
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(@NonNull String conversationId) {
        this.conversationId = conversationId;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
