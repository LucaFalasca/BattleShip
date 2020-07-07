package it.qzeroq.battleship.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Matches")
public class Match {

    @PrimaryKey(autoGenerate = true)
    private int idMatch;

    private String date;

    private String opponentDevice;

    private String nShipHit;

    private String nShipLost;

    private String matchResult;

    public Match(String date,  String opponentDevice, String nShipHit, String nShipLost, String matchResult){
        this.date = date;
        this.matchResult = matchResult;
        this.nShipHit = nShipHit;
        this.nShipLost = nShipLost;
        this.opponentDevice = opponentDevice;
    }

    public void setIdMatch(int idMatch) {
        this.idMatch = idMatch;
    }


    public int getIdMatch() {
        return idMatch;
    }

    @NonNull
    public String getMatchResult() {
        return matchResult;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    @NonNull
    public String getOpponentDevice() {
        return opponentDevice;
    }

    @NonNull
    public String getNShipHit() {
        return nShipHit;
    }

    @NonNull
    public String getNShipLost() {
        return nShipLost;
    }

    /*public void setNShipLost(@NonNull String nShipLost) {
        this.nShipLost = nShipLost;
    }

    public void setMatchResult(@NonNull String matchResult) {
        this.matchResult = matchResult;
    }*/












}
