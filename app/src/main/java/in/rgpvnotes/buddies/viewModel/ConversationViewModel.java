package in.rgpvnotes.buddies.viewModel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import in.rgpvnotes.buddies.database.AppDatabase;
import in.rgpvnotes.buddies.model.Conversation;

import java.util.List;

public class ConversationViewModel extends AndroidViewModel {

    private LiveData<List<Conversation>> list;

    private final AppDatabase appDatabase;

    public ConversationViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.Companion.getDatabase(this.getApplication());
    }

    public LiveData<List<Conversation>> getConversationList() {

        list = appDatabase.conversationDao().getConversationList();
        return list;

    }


    ///delete new conversation

    public void deleteConversationId(String conversationId){
        new deleteAsyncTask(appDatabase).execute(conversationId);
    }

    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final String... conversationId) {
            db.conversationDao().deleteConversationWithId(conversationId[0]);
            return null;
        }

    }

    ///add new conversation

    public void addConversation(final Conversation conversation) {
        new addAsyncTask(appDatabase).execute(conversation);
    }

    private static class addAsyncTask extends AsyncTask<Conversation, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Conversation... params) {
            db.conversationDao().insert(params[0]);
            return null;
        }

    }


}
