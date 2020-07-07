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

    public void setIdMatch(int idMatch) {
        this.idMatch = idMatch;
    }

    public int getIdMatch() {
        return idMatch;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setOpponentDevice(@NonNull String opponentDevice) {
        this.opponentDevice = opponentDevice;
    }

    @NonNull
    public String getOpponentDevice() {
        return opponentDevice;
    }

    public void setnShipHit(@NonNull String nShipHit) {
        this.nShipHit = nShipHit;
    }

    @NonNull
    public String getnShipHit() {
        return nShipHit;
    }

    public void setnShipLost(@NonNull String nShipLost) {
        this.nShipLost = nShipLost;
    }

    @NonNull
    public String getnShipLost() {
        return nShipLost;
    }

    public void setMatchResult(@NonNull String matchResult) {
        this.matchResult = matchResult;
    }

    @NonNull
    public String getMatchResult() {
        return matchResult;
    }










}
