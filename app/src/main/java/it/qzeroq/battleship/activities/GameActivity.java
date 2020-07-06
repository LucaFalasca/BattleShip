package it.qzeroq.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.bluetooth.BluetoothService;
import it.qzeroq.battleship.views.BattleGridView;

import static it.qzeroq.battleship.bluetooth.ChooseActivity.MESSAGE_READ;
import static it.qzeroq.battleship.bluetooth.ChooseActivity.MESSAGE_STATE_CHANGE;
import static it.qzeroq.battleship.bluetooth.ChooseActivity.MESSAGE_WRITE;

public class GameActivity extends AppCompatActivity {

    BluetoothService bluetoothService;

    String message;

    String writeMessage;
    String readMessage;

    Boolean hit;

    Intent i;

    boolean itsMyTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        i = getIntent();
        //Ship (?) ships = i.getExtra("ships");
        // ---------- POI PASSARE LE NAVI ALLA GRID ---------

        bluetoothService = BluetoothService.getInstance();
        bluetoothService.setHandler(handler);

        new Holder();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    /*
                    writeMessage = new String(writeBuf);
                    if (writeMessage.equals(readMessage)) {
                        //-------NON SO COSA DEBBA FARE-------
                    }
                    */

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    readMessage = new String(readBuf, 0, msg.arg1);

                    if (message.equals("your turn")) {
                        itsMyTurn = true;
                        Toast.makeText(getApplicationContext(), "Your turn", Toast.LENGTH_LONG).show();
                    }
                    else if (message.equals("HIT")) {
                        hit = true;
                        //--------- MARCARE CELLA HIT ------------
                        itsMyTurn = false;
                        //--------- INVIO MESSAGGIO "YOUR TURN" ---------
                    }
                    else if (message.equals("MISS")) {
                        hit = false;
                        //--------- MARCARE CELLA MISS -----------
                        itsMyTurn = false;
                        //--------- INVIO MESSAGGIO "YOUR TURN" ---------
                    }
                    else {
                        String[] coordString = readMessage.split(" ");
                        int[] coordInt = {Integer.parseInt(coordString[0]), Integer.parseInt(coordString[1])};

                        //-------- INSERIRE CONTROLLO CHE coordInt SIA UN HIT O UN MISS NELLA PROPRIA GRIDVIEW -------
                        //-------- E INVIO MESSAGGIO HIT O MISS ---------------
                    }
                    break;
            }
        }
    };

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (bluetoothService.getState() != bluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            bluetoothService.write(send);

            // Reset out string buffer to zero
            //mOutStringBuffer.setLength(0);
        }
    }

    class Holder implements View.OnTouchListener{
        BattleGridView bgMine;
        BattleGridView bgOpponent;


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

                    int[] coord = calculateIndexes(x, y, bgOpponent);

                    String xString = String.valueOf(x);
                    String yString = String.valueOf(y);

                    message = xString + " " + yString;
                    sendMessage(message);


                    /*
                    String result = "";

                    if (result.equals("hit")) {
                        bgOpponent.markCellHit();
                    } else {
                        bgOpponent.markCellMissed();
                    }*/

                    //passa il turno tramite bluetooth
                    //itsMyTurn = false;


                }
            }
            return false;
        }

        private int[] calculateIndexes(float xPosition, float yPosition, BattleGridView grid){
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

    @Override
    public void onBackPressed() {

    }
}
