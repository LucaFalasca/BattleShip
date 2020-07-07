package it.qzeroq.battleship.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Match.class, version = 3, exportSchema = false)
public abstract class MatchRoom extends RoomDatabase {

    public abstract MatchDao matchDao();

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
