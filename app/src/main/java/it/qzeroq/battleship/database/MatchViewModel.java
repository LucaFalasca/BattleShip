package it.qzeroq.battleship.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MatchViewModel extends AndroidViewModel {
    private MatchRepository matchRepository;
    private LiveData<List<Match>> allMatches;
    public MatchViewModel(@NonNull Application application) {
        super(application);
        matchRepository = new MatchRepository(application);
        allMatches = matchRepository.getAllMatches();
    }

    public void insert(Match match){
        matchRepository.insert(match);
    }

    public LiveData<List<Match>> getAllMatches(){
        return allMatches;
    }



}
