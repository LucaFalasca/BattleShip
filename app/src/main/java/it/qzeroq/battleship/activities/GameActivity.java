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

            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    return true;

                case DragEvent.ACTION_DROP:

                    LinearLayout shipLayout = (LinearLayout) event.getLocalState();

                    BattleGridView battleGrid = (BattleGridView) v;

                    int x = calculateIndex(event.getX(), battleGrid.getX(), battleGrid.getSide());
                    int y = calculateIndex(event.getY(), battleGrid.getY(), battleGrid.getSide());

                    battleGrid.placeShip(new Ship(context, 4), x, y, Rotation.ROTATION_0);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    return true;
            }
            return false;
        }

        private int calculateIndex(float absolutePosition, float battleGridPosition, int sideCell) {
            /*
            float relativePosition = absolutePosition - (battleGridPosition + sideCell);

            int index = Math.round(relativePosition / sideCell) - 1;
            return index;
             */
            return 0;
        }


    }
}
