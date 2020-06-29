package it.qzeroq.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

public class PositionShipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_ship);
        new Holder(this);
    }

    class Holder implements View.OnLongClickListener, View.OnTouchListener, View.OnDragListener, View.OnClickListener{

        Context context;
        BattleGridView battleGridView;
        Map<Integer, ShipView> shipViews;
        ShipView swFour, swThree, swTwo;
        float xClick, yClick;

        View.DragShadowBuilder o;

        @SuppressLint("ClickableViewAccessibility")
        Holder(Context context){
            this.context = context;

            swFour = findViewById(R.id.swFour);
            swFour.setOnTouchListener(this);
            swThree = findViewById(R.id.swThree);
            swThree.setOnTouchListener(this);
            swTwo = findViewById(R.id.swTwo);
            swTwo.setOnTouchListener(this);

            shipViews = new HashMap<>();
            shipViews.put(2, swTwo);
            shipViews.put(3, swThree);
            shipViews.put(4, swFour);

            o = new View.DragShadowBuilder(swFour);

            battleGridView = findViewById(R.id.battleGridView);
            battleGridView.setOnDragListener(this);
            battleGridView.setOnTouchListener(this);
            battleGridView.setOnClickListener(this);
            battleGridView.setOnLongClickListener(this);
            battleGridView.setTag("grid");
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            xClick = event.getX();
            yClick = event.getY();

            if(v.getTag() != "grid") {
                Ship newShip = ((ShipView) v).getShip();
                v.startDragAndDrop(null, new View.DragShadowBuilder(v), new Ship(context, newShip.getLength(), newShip.getRotation()), 0);
            }
            return false;
        }


        @Override
        public void onClick(View v) {
            int[] coords = calculateIndexs(xClick, yClick, battleGridView);

            int xIndex = coords[0];
            int yIndex = coords[1];
            battleGridView.rotateShipAt(xIndex, yIndex);

        }

        @Override
        public boolean onLongClick(View v) {
            int[] coords = calculateIndexs(xClick, yClick, battleGridView);

            int xIndex = coords[0];
            int yIndex = coords[1];

            if(battleGridView.thereIsAShipAt(xIndex, yIndex)){
                Ship ship = battleGridView.getShipAt(xIndex, yIndex);
                ShipView sv = shipViews.get(ship.getLength());

                assert sv != null;
                sv.startDragAndDrop(null, new ShadowBuilderRotation(sv, ship.getRotation()), ship, 0);
                battleGridView.removeShipAt(xIndex, yIndex);
            }
            return false;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();

            Ship ship;
            BattleGridView battleGrid;
            int x, y;

            switch(action) {
                case DragEvent.ACTION_DRAG_LOCATION:

                    ship = (Ship) event.getLocalState();

                    battleGrid = (BattleGridView) v;

                    int[] c = calculateIndexs(event.getX(), event.getY(), battleGrid, ship);
                    System.out.println(c[0] + " " + c[1]);
                    x = c[0];
                    y = c[1];

                    battleGrid.removeSelection();

                    if(x < 0 || y < 0) return false;

                    battleGrid.showSelection(ship, x, y);
                    return true;

                case DragEvent.ACTION_DROP:

                    ship = (Ship) event.getLocalState();

                    battleGrid = (BattleGridView) v;

                    c = calculateIndexs(event.getX(), event.getY(), battleGrid, ship);
                    System.out.println(c[0] + " " + c[1]);
                    x = c[0];
                    y = c[1];

                    if(x < 0 || y < 0) return false;

                    if(!battleGrid.placeShip(ship, x, y))
                        return false;

                    return true;

                case DragEvent.ACTION_DRAG_STARTED:
                case DragEvent.ACTION_DRAG_ENTERED:
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;
            }
            return false;
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
