package it.qzeroq.battleship.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.qzeroq.battleship.R;

public class GameHistoryAdapter extends RecyclerView.Adapter<GameHistoryAdapter.Holder>{
    private List<Match> matchList = new ArrayList<>();

    @NonNull
    @Override
    public GameHistoryAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout cl = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_game_history_activity,parent,false);
        return new Holder(cl);

    }

    @Override
    public void onBindViewHolder(@NonNull GameHistoryAdapter.Holder holder, int position) {
        holder.fill(matchList.get(getItemCount() - 1 - position));

    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public void setMatchList(List<Match> matchList){
        this.matchList = matchList;
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tvGameResult;
        TextView tvData;
        TextView tvNShipLost;
        TextView tvNShipHit;
        Button btnCancel;

        Holder(@NonNull View ItemView){
            super(ItemView);
            tvGameResult = itemView.findViewById(R.id.tvGameResult);
            tvData = itemView.findViewById(R.id.tvData);
            tvNShipHit = itemView.findViewById(R.id.tvNShipHit);
            tvNShipLost = itemView.findViewById(R.id.tvNShipLost);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }

        private void fill(Match match){
            tvData.setText(match.getDate());
            tvNShipLost.setText(match.getNShipLost());
            tvNShipHit.setText(match.getNShipHit());
            tvGameResult.setText(match.getMatchResult());


        }


    }

}
