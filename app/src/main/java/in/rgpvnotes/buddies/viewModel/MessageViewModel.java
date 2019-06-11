package in.rgpvnotes.buddies.viewModel;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;
import in.rgpvnotes.buddies.database.AppDatabase;
import in.rgpvnotes.buddies.model.Message;

import java.util.List;

/**
 * Created by Anoop on 07-02-2018.
 */

public class MessageViewModel extends AndroidViewModel {

    private LiveData<List<Message>> messageList;

    private final AppDatabase appDatabase;

    public MessageViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.Companion.getDatabase(this.getApplication());

    }

    public LiveData<List<Message>> getMessageList(String user_id) {

        messageList = appDatabase.messageDao().getConversationMessages(user_id);
        return messageList;

    }


    public LiveData<List<Message>> getUnreadCount(String conversationId, String user_id) {

        messageList = appDatabase.messageDao().getUnreadCount(conversationId,user_id);
        return messageList;

    }

    public LiveData<List<Message>> getLastMessage(String conversationId) {

        messageList = appDatabase.messageDao().getLastMessage(conversationId);
        return messageList;

    }


    //-------delete message

    public void deleteMessageById(String msg_id){
        new deleteAsyncTask(appDatabase).execute(msg_id);
    }

    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final String... msg_id) {
            db.messageDao().deleteMessageWithId(msg_id[0]);
            return null;
        }

    }

    ///add new messages

    public void addMessage(final Message message) {
        new addAsyncTask(appDatabase).execute(message);
    }

    private static class addAsyncTask extends AsyncTask<Message, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Message... params) {

            db.messageDao().insertMessage(params[0]);
            return null;
        }

    }





}
