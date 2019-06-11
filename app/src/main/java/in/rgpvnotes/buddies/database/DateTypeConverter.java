package in.rgpvnotes.buddies.database;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Created by Anoop on 06-02-2018.
 */

public class DateTypeConverter {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }

}
