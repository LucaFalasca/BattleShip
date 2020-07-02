package it.qzeroq.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.views.BattleGridView;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        new Holder();
    }

    class Holder implements View.OnTouchListener{
        BattleGridView bgMine;
        BattleGridView bgOpponent;
        boolean itsMyTurn;

        Holder(){
            bgMine = findViewById(R.id.bgMine);
            bgOpponent = findViewById(R.id.bgEnemy);
            bgOpponent.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if (itsMyTurn) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    int[] coord = calculateIndexs(x, y, bgOpponent);


                    String result = "";

                    if (result == "si") {
                        bgOpponent.markCellHit();
                    } else {
                        bgOpponent.markCellMissed();
                    }

                    //passa il turno tramite bluetooth
                    itsMyTurn = false;
                }
            }
            return false;
        }

        private int[] calculateIndexs(float xPosition, float yPosition, BattleGridView grid){
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) grid.getLayoutParams();
            int marginTop = params.topMargin / 3;
            int marginLeft = params.leftMargin / 3;

            int sideCell = grid.getSide();

            float xGrid = marginTop + sideCell;
            float yGrid = marginLeft + sideCell;

            float relativeX = xPosition - xGrid;
            float relativeY = yPosition - yGrid;

            int xIndex = (int)(relativeX / sideCell);
            int yIndex = (int)(relativeY / sideCell);

            int sideGrid = sideCell * grid.GRID_SIZE;

            float xMax = xGrid + sideGrid;
            float yMax = yGrid + sideGrid;

            if(relativeX < 0 || relativeX > xMax)
                xIndex = -1;
            if(relativeY < 0 || relativeY > yMax)
                yIndex = -1;

            return new int[]{xIndex, yIndex};
        }

    }
}
