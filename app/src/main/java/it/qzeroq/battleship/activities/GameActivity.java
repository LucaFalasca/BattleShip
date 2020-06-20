package it.qzeroq.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

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

    class Holder implements View.OnLongClickListener, View.OnTouchListener, View.OnDragListener{

        Context context;
        BattleGridView battleGridView;
        ShipView swFour, swThree, swTwo;
        float xClick, yClick;

        View.DragShadowBuilder o;

        Holder(Context context){
            this.context = context;

            swFour = findViewById(R.id.swFour);
            swFour.setOnTouchListener(this);
            swThree = findViewById(R.id.swThree);
            swThree.setOnTouchListener(this);
            swTwo = findViewById(R.id.swTwo);
            swTwo.setOnTouchListener(this);

            o = new View.DragShadowBuilder(swFour);

            battleGridView = findViewById(R.id.battleGridView);
            battleGridView.setOnDragListener(this);
            battleGridView.setOnTouchListener(this);
            battleGridView.setOnLongClickListener(this);
            battleGridView.setTag("grid");
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(v.getTag() != "grid") {
                v.startDragAndDrop(null, new View.DragShadowBuilder(v), v, 0);
            }else{
                xClick = event.getX();
                yClick = event.getY();
            }
            return false;
        }

        @Override
        public boolean onLongClick(View v) {
            int xIndex = calculateIndex(xClick, 16, battleGridView.getSide(), 0);
            int yIndex = calculateIndex(yClick, 16, battleGridView.getSide(), 0);
            System.out.println("WHEIUHEIUHWEIUHWIUEHIUWHEIUHWEU:  " + battleGridView.thereIsAShipAt(xIndex, yIndex));
            if(battleGridView.thereIsAShipAt(xIndex, yIndex)){
                Ship ship = battleGridView.getShipAt(xIndex, yIndex);
                boolean q = false;
                switch(ship.getLenghtShip()){
                    case 2:
                        q = swTwo.startDragAndDrop(null, new View.DragShadowBuilder(swTwo), swTwo, 0);
                        break;
                    case 3:
                        q = swThree.startDragAndDrop(null, new View.DragShadowBuilder(swThree), swThree, 0);
                        break;
                    case 4:
                        q = swFour.startDragAndDrop(null, new View.DragShadowBuilder(swFour), swFour, 0);
                        break;
                }
                battleGridView.removeShipAt(xIndex, yIndex);

                if(!q){

                }


            }
            return false;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();

            ShipView ship;
            int lenghtShip;
            BattleGridView battleGrid;
            int x, y;

            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    ship = (ShipView) event.getLocalState();
                    lenghtShip = ship.getLenghtShip();

                    battleGrid = (BattleGridView) v;

                    x = calculateIndex(event.getX(), 16, battleGrid.getSide(), ship.getWidth());
                    y = calculateIndex(event.getY(), 16, battleGrid.getSide(), ship.getHeight());

                    battleGrid.removeSelection();

                    if(x < 0 || y < 0) return false;

                    battleGrid.showSelection(new Ship(context, lenghtShip), x, y, Rotation.ROTATION_0);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    return true;

                case DragEvent.ACTION_DROP:

                    ship = (ShipView) event.getLocalState();
                    lenghtShip = ship.getLenghtShip();

                    battleGrid = (BattleGridView) v;

                    x = calculateIndex(event.getX(), 16, battleGrid.getSide(), ship.getWidth());
                    y = calculateIndex(event.getY(), 16, battleGrid.getSide(), ship.getHeight());

                    if(x < 0 || y < 0)
                        return false;

                    battleGrid.placeShip(new Ship(context, lenghtShip), x, y, Rotation.ROTATION_0);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
            }
            return false;
        }

        private int calculateIndex(float positionOfFinger, float battleGridMargin, int sideCell, float sizeShip) {

            float startPositionOfGrid = battleGridMargin + sideCell;
            float positionOfShip = positionOfFinger - sizeShip / 2;
            float relativePosition = positionOfShip - startPositionOfGrid;

            int sideGrid = sideCell * 9;

            float min = startPositionOfGrid - sizeShip / 2;
            float max = startPositionOfGrid + sideGrid + sizeShip / 2;

            if(positionOfShip < min){
                return -1;
            }
            else if(positionOfShip > max){
                return -1;
            }
            else {
                int index = Math.round(relativePosition / sideCell);
                return index;
            }
        }


    }
}
