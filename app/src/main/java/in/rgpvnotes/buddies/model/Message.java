package in.rgpvnotes.buddies.model;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@Entity
public class Message {

    public static class MessageType{

        public final static String TEXT = "TEXT";
        public final static String IMAGE = "IMAGE";

    }

    @PrimaryKey @NonNull
    private String messageId;

    private String messageText;

    private boolean seen;

    private Date seenAt;

    private String type;

    private String senderId;

    private String senderNum;

    private String receiverId;

    private String conversationId;

    @ServerTimestamp
    private Date serverTimestamp;

    private String extraUrl;

    @Ignore
    public Message() {

    }

    public Message(String messageId, String messageText, boolean seen, Date seenAt, String type, String senderId,String senderNum, String receiverId, String conversationId, Date serverTimestamp, String extraUrl) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.seen = seen;
        this.seenAt = seenAt;
        this.type = type;
        this.senderId = senderId;
        this.senderNum = senderNum;
        this.receiverId = receiverId;
        this.conversationId = conversationId;
        this.serverTimestamp = serverTimestamp;
        this.extraUrl = extraUrl;
    }

    @Ignore
    public Message(String messageId, String messageText, boolean seen, Date seenAt, String type, String senderId,String senderNum, String receiverId, String conversationId, String extraUrl) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.seen = seen;
        this.seenAt = seenAt;
        this.type = type;
        this.senderId = senderId;
        this.senderNum = senderNum;
        this.receiverId = receiverId;
        this.conversationId = conversationId;
        this.extraUrl = extraUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Date getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(Date seenAt) {
        this.seenAt = seenAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Date getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(Date serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    public String getExtraUrl() {
        return extraUrl;
    }

    public void setExtraUrl(String extraUrl) {
        this.extraUrl = extraUrl;
    }

    public String getSenderNum() {
        return senderNum;
    }

    public void setSenderNum(String senderNum) {
        this.senderNum = senderNum;
    }
}

