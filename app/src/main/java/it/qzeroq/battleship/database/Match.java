package it.qzeroq.battleship.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Matches")
public class Match {

    public Match(String date, String opponentDevice, String nShipHit,String nShipLost, String matchResult){
        this.date = date;
        this.matchResult = matchResult;
        this.nShipHit = nShipHit;
        this.nShipLost = nShipLost;
        this.opponentDevice = opponentDevice;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int idMatch;

    @NonNull
    private String date;

    @NonNull
    private String opponentDevice;

    @NonNull
    private String nShipHit;

    @NonNull
    private String nShipLost;

    @NonNull
    private String matchResult;

    @NonNull
    public void setIdMatch(int idMatch) {
        this.idMatch = idMatch;
    }

    @NonNull
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

    public void setNShipLost(@NonNull String nShipLost) {
        this.nShipLost = nShipLost;
    }

    public void setMatchResult(@NonNull String matchResult) {
        this.matchResult = matchResult;
    }












}
