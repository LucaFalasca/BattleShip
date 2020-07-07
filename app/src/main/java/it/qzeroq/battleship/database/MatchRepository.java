package it.qzeroq.battleship.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MatchRepository {

    private MatchDao matchDao;
    private LiveData<List<Match>> allMatches;
    private MatchRoom partiteDB;

    public MatchRepository(Application application){
        partiteDB = MatchRoom.getInstance(application);
        matchDao = partiteDB.partiteDao();
        allMatches = matchDao.getAllMatch();
    }

    public void insert(Match partita){
        new InsertAsynTask(matchDao).execute(partita);
    }

    public LiveData<List<Match>> getAllMatches(){
        return allMatches;
    }

    private static class InsertAsynTask extends AsyncTask<Match, Void, Void> {
        private MatchDao matchDao;
        public InsertAsynTask(MatchDao matchDao) {
            this.matchDao = matchDao;
        }

        @Override
        protected Void doInBackground(Match... matches) {
            matchDao.insert(matches[0]);
            return null;
        }
    }
}
