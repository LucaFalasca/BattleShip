package it.qzeroq.battleship.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao

public interface MatchDao {

    @Insert
    void insert(Match partita);

    @Query("SELECT * FROM `Matches`")
    LiveData<List<Match>> getAllMatch();
}
