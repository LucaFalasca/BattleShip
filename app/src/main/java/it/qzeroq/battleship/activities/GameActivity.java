package it.qzeroq.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;

import it.qzeroq.battleship.views.BattleGridView;
import it.qzeroq.battleship.R;
import it.qzeroq.battleship.Ship;
import it.qzeroq.battleship.views.ShipView;
import it.qzeroq.battleship.enums.Rotation;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        new Holder(this);
    }

    class Holder implements View.OnLongClickListener, View.OnDragListener{

        Context context;
        BattleGridView battleGridView;
        ShipView shipView;

        Holder(Context context){
            this.context = context;

            shipView = findViewById(R.id.shipView);
            shipView.setOnLongClickListener(this);

            battleGridView = findViewById(R.id.battleGridView);
            battleGridView.setOnDragListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            v.startDragAndDrop(null, new View.DragShadowBuilder(v), v, 0);
            return true;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            boolean isInTheGrid = false;
            int lenghtShip;
            BattleGridView battleGrid;
            int x, y;
            ShipView ship;
            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:

                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    ship = (ShipView) event.getLocalState();

                    lenghtShip = ship.getLenghtShip();

                    battleGrid = (BattleGridView) v;

                    battleGrid.removeSelection();

                    x = calculateIndex(event.getX(), battleGrid.getX(), battleGrid.getSide(), ship.getWidth());
                    y = calculateIndex(event.getY(), battleGrid.getY(), battleGrid.getSide(), ship.getHeight());
                    if(x < 0 || y < 0)
                        return false;
                    else
                        battleGrid.showSelection(new Ship(context, lenghtShip), x, y, Rotation.ROTATION_0);


                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;

                case DragEvent.ACTION_DROP:

                    ship = (ShipView) event.getLocalState();

                    lenghtShip = ship.getLenghtShip();

                    battleGrid = (BattleGridView) v;

                    x = calculateIndex(event.getX(), battleGrid.getX(), battleGrid.getSide(), ship.getWidth());
                    y = calculateIndex(event.getY(), battleGrid.getY(), battleGrid.getSide(), ship.getHeight());
                    if(x < 0 || y < 0)
                        return false;
                    else
                        battleGrid.placeShip(new Ship(context, lenghtShip), x, y, Rotation.ROTATION_0);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    return true;
            }
            return false;
        }

        private int calculateIndex(float absolutePosition, float battleGridPosition, int sideCell, float sizeShip) {

            float trueGridPosition = battleGridPosition + sideCell;
            float trueAbsolutePosition = absolutePosition - sizeShip / 2;
            float relativePosition = trueAbsolutePosition - trueGridPosition;

            int sideGrid = sideCell * 9;

            float min = trueGridPosition - sizeShip / 2;
            float max = trueGridPosition + sideGrid + sizeShip / 2;

            if(trueAbsolutePosition < min){
                return -1;
            }
            else if(trueAbsolutePosition > max){
                return -1;
            }
            else {
                int index = Math.round(relativePosition / sideCell);
                if(index > 9){
                    System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°  " + index);
                }
                return index;
            }
        }


    }
}
