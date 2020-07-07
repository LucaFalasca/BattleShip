package it.qzeroq.battleship.database;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

import it.qzeroq.battleship.R;

public class GameHistoryActivity extends AppCompatActivity {

    GameHistoryAdapter adapter = new GameHistoryAdapter();

    MatchViewModel matchViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv_game_history_activity);
        matchViewModel = new ViewModelProvider(this).get(MatchViewModel.class);
        new Holder();
    }

    @Override
    protected void onStart() {
        super.onStart();
        matchViewModel.getAllMatches().observe(this, new Observer<List<Match>>() {
            @Override
            public void onChanged(@Nullable List<Match> matchList) {
                adapter.setMatchList(matchList);
            }
        });
    }

    class Holder{
        RecyclerView rvSeeDatabase;
        Holder(){
            rvSeeDatabase = findViewById(R.id.rvDatabse);
            RecyclerView.LayoutManager lManager = new GridLayoutManager(GameHistoryActivity.this, 3);
            rvSeeDatabase.setLayoutManager(lManager);
            rvSeeDatabase.setAdapter(adapter);
        }
    }
}