package it.qzeroq.battleship.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

class MatchRepository {

    private MatchDao matchDao;
    private LiveData<List<Match>> allMatches;

    MatchRepository(Application application){
        MatchRoom matchDB = MatchRoom.getInstance(application);
        matchDao = matchDB.matchDao();
        allMatches = matchDao.getAllMatch();
    }

    void insert(Match partita){
        new InsertAsynTask(matchDao).execute(partita);
    }

    LiveData<List<Match>> getAllMatches(){
        return allMatches;
    }

    private static class InsertAsynTask extends AsyncTask<Match, Void, Void> {
        private MatchDao matchDao;
        InsertAsynTask(MatchDao matchDao) {
            this.matchDao = matchDao;
        }

        @Override
        protected Void doInBackground(Match... matches) {
            matchDao.insert(matches[0]);
            return null;
        }
    }
}
