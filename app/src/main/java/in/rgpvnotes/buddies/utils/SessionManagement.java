package in.rgpvnotes.buddies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import in.rgpvnotes.buddies.model.AppUser;
import in.rgpvnotes.buddies.model.Message;

import static android.content.Context.MODE_PRIVATE;

public class SessionManagement {

    public static boolean saveLastMessage(Context context, Message lastDoc){

        String PREF_NAME = "user";

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(lastDoc);
        prefsEditor.putString("lastDoc", json);

        return prefsEditor.commit();

    }

    public static Message getLastMessage(Context context){

        String PREF_NAME = "user";

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        Gson gson = new Gson();

        String json = pref.getString("lastDoc", "");

        return gson.fromJson(json, Message.class);

    }

    public static boolean saveStudent(Context context, AppUser user){

        String PREF_NAME = "user";

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("MyObject", json);
        return prefsEditor.commit();

    }

    public static AppUser getUser(Context context){

        String PREF_NAME = "user";

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = pref.getString("MyObject", "");
        return gson.fromJson(json, AppUser.class);

    }
}
