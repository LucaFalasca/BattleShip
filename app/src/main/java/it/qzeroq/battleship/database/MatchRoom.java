package it.qzeroq.battleship.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Match.class, version = 3)
public abstract class MatchRoom extends RoomDatabase {

    public abstract MatchDao partiteDao();

    private static MatchRoom matchRoom;

    public static synchronized MatchRoom getInstance(Context context){
        if(matchRoom == null){
            matchRoom = Room.databaseBuilder(context.getApplicationContext(),
                    MatchRoom.class,"Matches").fallbackToDestructiveMigration()
                    .build();
        }
        return matchRoom;
    }


}
