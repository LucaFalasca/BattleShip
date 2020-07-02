package it.qzeroq.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public void onBackPressed() {

    }

    class Holder implements View.OnLongClickListener, View.OnTouchListener, View.OnDragListener, View.OnClickListener{

        Context context;
        BattleGridView battleGridView;
        Map<Integer, ShipView> shipViews;
        Map<Integer, TextView> tvCounts;
        Button btnConfirm;
        float xClick, yClick;
        boolean q = false;

        @SuppressLint("ClickableViewAccessibility")
        Holder(Context context){
            this.context = context;

            tvCounts = new HashMap<>();
            tvCounts.put(2, (TextView) findViewById(R.id.tvCountTwo));
            tvCounts.put(3, (TextView) findViewById(R.id.tvCountThree));
            tvCounts.put(4, (TextView) findViewById(R.id.tvCountFour));

            shipViews = new HashMap<>();
            shipViews.put(2, (ShipView) findViewById(R.id.swTwo));
            shipViews.put(3, (ShipView) findViewById(R.id.swThree));
            shipViews.put(4, (ShipView) findViewById(R.id.swFour));

            Objects.requireNonNull(shipViews.get(2)).setOnTouchListener(this);
            Objects.requireNonNull(shipViews.get(3)).setOnTouchListener(this);
            Objects.requireNonNull(shipViews.get(4)).setOnTouchListener(this);

            battleGridView = findViewById(R.id.battleGridView);
            battleGridView.setOnDragListener(this);
            battleGridView.setOnTouchListener(this);
            battleGridView.setOnClickListener(this);
            battleGridView.setOnLongClickListener(this);
            battleGridView.setTag("grid");

            btnConfirm = findViewById(R.id.btnConfirm);
            btnConfirm.setOnClickListener(this);
        }

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(v.getTag() == "grid") {
                xClick = event.getX();
                yClick = event.getY();
            }
            else {
                Ship newShip = ((ShipView) v).getShip();
                String c = Objects.requireNonNull(tvCounts.get(newShip.getLength())).getText().toString();
                if(!c.endsWith("0")) {
                    v.startDragAndDrop(null, new View.DragShadowBuilder(v), new Ship(context, newShip.getLength(), newShip.getRotation()), 0);
                    removeOneToCount(Objects.requireNonNull(tvCounts.get(newShip.getLength())));
                }
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnConfirm) {

            }
            else {
                int[] coords = calculateIndexs(xClick, yClick, battleGridView);

                int xIndex = coords[0];
                int yIndex = coords[1];
                if (xIndex >= 0 && xIndex < 10 && yIndex >= 0 && yIndex < 10)
                    battleGridView.rotateShipAt(xIndex, yIndex);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int[] coords = calculateIndexs(xClick, yClick, battleGridView);

            int xIndex = coords[0];
            int yIndex = coords[1];

            if(battleGridView.thereIsAShipAt(xIndex, yIndex)){
                Ship ship = battleGridView.getShipAt(xIndex, yIndex);
                ShipView sv = shipViews.get(ship.getLength());

                q = true;

                assert sv != null;
                sv.startDragAndDrop(null, new ShadowBuilderRotation(sv, ship.getRotation()), ship, 0);
                battleGridView.removeShipAt(xIndex, yIndex);
            }
            return false;
        }

        @SuppressLint("SetTextI18n")
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

                    if(x < 0 || y < 0 || y > 9 || x > 9) return false;

                    battleGrid.showSelection(ship, x, y);
                    return true;

                case DragEvent.ACTION_DROP:

                    ship = (Ship) event.getLocalState();

                    battleGrid = (BattleGridView) v;

                    c = calculateIndexs(event.getX(), event.getY(), battleGrid, ship);
                    System.out.println(c[0] + " " + c[1]);
                    x = c[0];
                    y = c[1];

                    if(x < 0 || y < 0 || y > 9 || x > 9 || !battleGrid.placeShip(ship, x, y)) {
                        addOneToCount(Objects.requireNonNull(tvCounts.get(ship.getLength())));
                        return false;
                    }

                    return true;

                case DragEvent.ACTION_DRAG_STARTED:

                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    if(!event.getResult()){
                        if(q){
                            /*c = calculateIndexs(startX, startY, battleGridView);
                            x = c[0];
                            y = c[1];*/
                            ship = (Ship) event.getLocalState();
                            battleGridView.placeShip(ship, 1, 1);
                        }else {
                            ship = (Ship) event.getLocalState();
                            addOneToCount(Objects.requireNonNull(tvCounts.get(ship.getLength())));
                        }
                    }
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

            assert shipView != null;
            float xShip = xPosition - (shipView.getWidth() * cos + shipView.getHeight() * sin) / 2.6f;
            float yShip = yPosition - (shipView.getWidth() * sin + shipView.getHeight() * cos) / 2.6f;

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

        @SuppressLint("SetTextI18n")
        private void addOneToCount(TextView tvCount){
            String count = tvCount.getText().toString();
            String lastLetter = count.substring(count.length() - 1);
            int number = Integer.parseInt(lastLetter);

            tvCount.setText("x " + (number + 1));
        }

        @SuppressLint("SetTextI18n")
        private void removeOneToCount(TextView tvCount){
            String count = tvCount.getText().toString();
            String lastLetter = count.substring(count.length() - 1);
            int number = Integer.parseInt(lastLetter);

            tvCount.setText("x " + (number - 1));
        }
    }
}
