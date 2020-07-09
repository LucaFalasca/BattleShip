package it.qzeroq.battleship.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Matches")
public class Match {

    @PrimaryKey(autoGenerate = true)
    private int idMatch;

    private String date;

    private String nShipHit;

    private String nShipLost;

    private String matchResult;

    public Match(String date, String nShipHit, String nShipLost, String matchResult){
        this.date = date;
        this.matchResult = matchResult;
        this.nShipHit = nShipHit;
        this.nShipLost = nShipLost;
    }

    void setIdMatch(int idMatch) {
        this.idMatch = idMatch;
    }


    int getIdMatch() {
        return idMatch;
    }

    @NonNull
    String getMatchResult() {
        return matchResult;
    }

    @NonNull
    String getDate() {
        return date;
    }

    @NonNull
    String getNShipHit() {
        return nShipHit;
    }

    @NonNull
    String getNShipLost() {
        return nShipLost;
    }


}
