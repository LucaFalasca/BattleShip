package it.qzeroq.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import it.qzeroq.battleship.ShadowBuilderRotation;
import it.qzeroq.battleship.views.BattleGridView;
import it.qzeroq.battleship.R;
import it.qzeroq.battleship.Ship;
import it.qzeroq.battleship.views.ShipView;

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
        Map<Integer, ShipView> shipViews;
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

            shipViews= new HashMap<>();
            shipViews.put(2, swTwo);
            shipViews.put(3, swThree);
            shipViews.put(4, swFour);

            o = new View.DragShadowBuilder(swFour);

            battleGridView = findViewById(R.id.battleGridView);
            battleGridView.setOnDragListener(this);
            battleGridView.setOnTouchListener(this);
            battleGridView.setOnLongClickListener(this);
            battleGridView.setTag("grid");
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            xClick = event.getX();
            yClick = event.getY();

            if(v.getTag() != "grid")
                v.startDragAndDrop(null, new View.DragShadowBuilder(v), ((ShipView)v).getShip(), 0);

            switch(event.getAction()){
                case MotionEvent.ACTION_UP:

                        int xIndex = calculateIndex(xClick, 16, battleGridView.getSide(), 0);
                        int yIndex = calculateIndex(yClick, 16, battleGridView.getSide(), 0);
                        battleGridView.rotateShipAt(xIndex, yIndex);
                        return true;
            }
            return false;
        }

        @Override
        public boolean onLongClick(View v) {
            int xIndex = calculateIndex(xClick, 16, battleGridView.getSide(), 0);
            int yIndex = calculateIndex(yClick, 16, battleGridView.getSide(), 0);

            if(battleGridView.thereIsAShipAt(xIndex, yIndex)){
                Ship ship = battleGridView.getShipAt(xIndex, yIndex);
                boolean q = true;
                switch(ship.getLength()){
                    case 2:
                        q = swTwo.startDragAndDrop(null, new ShadowBuilderRotation(swTwo, ship.getRotation()), ship, 0);
                        break;
                    case 3:
                        q = swThree.startDragAndDrop(null, new ShadowBuilderRotation(swThree, ship.getRotation()), ship, 0);
                        break;
                    case 4:
                        q = swFour.startDragAndDrop(null, new ShadowBuilderRotation(swFour, ship.getRotation()), ship, 0);
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

            Ship ship;
            int lenghtShip;
            BattleGridView battleGrid;
            int x, y;

            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    ship = (Ship) event.getLocalState();
                    lenghtShip = ship.getLength();

                    battleGrid = (BattleGridView) v;

                    /*x = calculateIndex(event.getX(), 16, battleGrid.getSide(), ship.getWidth());
                    y = calculateIndex(event.getY(), 16, battleGrid.getSide(), ship.getHeight());*/
                    int[] c = calculateIndexs(event.getX(), event.getY(), battleGrid, ship);
                    System.out.println(c[0] + " " + c[1]);
                    x = c[0];
                    y = c[1];

                    battleGrid.removeSelection();

                    if(x < 0 || y < 0) return false;

                    battleGrid.showSelection(new Ship(context, lenghtShip), x, y);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    return true;

                case DragEvent.ACTION_DROP:

                    ship = (Ship) event.getLocalState();
                    lenghtShip = ship.getLength();

                    battleGrid = (BattleGridView) v;

                    /*
                    x = calculateIndex(event.getX(), 16, battleGrid.getSide(), ship.getWidth());
                    y = calculateIndex(event.getY(), 16, battleGrid.getSide(), ship.getHeight());*/
                    c = calculateIndexs(event.getX(), event.getY(), battleGrid, ship);
                    System.out.println(c[0] + " " + c[1]);
                    x = c[0];
                    y = c[1];

                    if(x < 0 || y < 0)
                        return false;

                    if(!battleGrid.placeShip(new Ship(context, lenghtShip), x, y))
                        return false;

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

        private int[] calculateIndexs(float xPosition, float yPosition, BattleGridView grid, Ship ship){
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) grid.getLayoutParams();
            int marginTop = params.topMargin / 3;
            int marginLeft = params.leftMargin / 3;

            int sideCell = grid.getSide();

            float xGrid = marginTop + sideCell;
            float yGrid = marginLeft + sideCell;

            int angleRotation = ship.getAngleRotation();
            float cos = (int) Math.cos(Math.toRadians(angleRotation));
            float sin = (int) Math.sin(Math.toRadians(angleRotation));

            ShipView shipView = shipViews.get(ship.getLength());

            float xShip = xPosition - (shipView.getWidth() * cos + shipView.getHeight() * sin) / 2;
            float yShip = yPosition - (shipView.getWidth() * sin + shipView.getHeight() * cos) / 2;

            float relativeX = xShip - xGrid;
            float relativeY = yShip - yGrid;

            int xIndex = Math.round(relativeX / sideCell);
            int yIndex = Math.round(relativeY / sideCell);

            int sideGrid = sideCell * battleGridView.GRID_SIZE;

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
