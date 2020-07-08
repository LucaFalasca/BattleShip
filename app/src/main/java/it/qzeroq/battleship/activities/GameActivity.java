package it.qzeroq.battleship.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.Ship;
import it.qzeroq.battleship.bluetooth.BluetoothService;
import it.qzeroq.battleship.database.Match;
import it.qzeroq.battleship.database.MatchViewModel;
import it.qzeroq.battleship.views.BattleGridView;

import static it.qzeroq.battleship.bluetooth.ChooseActivity.MESSAGE_READ;
import static it.qzeroq.battleship.bluetooth.ChooseActivity.MESSAGE_WRITE;

public class GameActivity extends AppCompatActivity {

    BluetoothService bluetoothService;

    String message;

    String result;

    String writeMessage;
    String readMessage;

    Ship[][] ships;

    int[] coord;

    private StringBuffer mOutStringBuffer;

    private MediaPlayer mediaPlayer;

    Intent i;

    Holder holder;
    boolean itsMyTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mediaPlayer = MediaPlayer.create(this,R.raw.startgame);
        mediaPlayer.start();

        holder = new Holder(this);

        i = getIntent();
        itsMyTurn = i.getBooleanExtra("itsMyTurn", false);

        //tocca vedere se funziona
        ArrayList<Ship> ships = i.getParcelableArrayListExtra("ships");
        ArrayList<Integer> x = i.getIntegerArrayListExtra("x");
        ArrayList<Integer> y = i.getIntegerArrayListExtra("y");

        assert ships != null;
        for(int k = 0; k < ships.size(); k++){
            assert x != null;
            assert y != null;
            holder.bgMine.placeShip(new Ship(this, ships.get(k).getLength(), ships.get(k).getRotation()), x.get(k), y.get(k));
        }

        bluetoothService = BluetoothService.getInstance();
        bluetoothService.setHandler(handler);

        if (itsMyTurn) {
            Toast.makeText(getApplicationContext(), "tour turn", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "enemy's turn", Toast.LENGTH_SHORT).show();
        holder.btnTurn.setChecked(itsMyTurn);
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

                    switch (readMessage) {
                        case "your turn":
                            itsMyTurn = true;
                            holder.btnTurn.setChecked(itsMyTurn);
                            Toast.makeText(getApplicationContext(), "Your turn", Toast.LENGTH_SHORT).show();
                            break;
                        case "HIT":
                            hit(coord);
                            break;
                        case "MISS":
                            miss(coord);
                            break;
                        case "YOU WIN":
                            mediaPlayer = MediaPlayer.create(GameActivity.this,R.raw.victory);
                            mediaPlayer.start();
                            Toast.makeText(getApplicationContext(), "You win!", Toast.LENGTH_SHORT).show();
                            finishGame(getResources().getString(R.string.win));
                            break;
                        default:
                            String[] coordString = readMessage.split(" ");
                            coord = new int[]{Integer.parseInt(coordString[0]), Integer.parseInt(coordString[1])};

                            int x = coord[0];
                            int y = coord[1];

                            checkShip(x, y);
                            checkVictory();
                            break;
                    }
                    break;
            }
        }
    };


    private void checkVictory() {
        int sunkenShip = holder.bgMine.getNumberOfSunkenShip();
        System.out.println("Affondate: " + sunkenShip);
        if(sunkenShip == 7){
            sendMessage("YOU WIN");
            Toast.makeText(getApplicationContext(), "You Lose!", Toast.LENGTH_SHORT).show();
            mediaPlayer = MediaPlayer.create(this,R.raw.lose);
            mediaPlayer.start();
            finishGame(getResources().getString(R.string.win));
        }
    }

    private void finishGame(String result){
        MatchViewModel matchViewModel = new ViewModelProvider(this).get(MatchViewModel.class);
        Date date;
        date = Calendar.getInstance().getTime();
        String data = date.toString();
        String nOfShipLost = String.valueOf(holder.bgMine.getNumberOfSunkenShip());
        String nOfShipHit = String.valueOf(holder.bgOpponent.getNumberOfSunkenShip());
        Match match = new Match(data, nOfShipHit, nOfShipLost, result);
        matchViewModel.insert(match);
        stopConnection();
    }

    private void stopConnection() {
        bluetoothService.stop();
    }

    private void hit(int[] coord) {
        mediaPlayer = MediaPlayer.create(this,R.raw.hittingship);
        mediaPlayer.start();
        holder.bgOpponent.markCellHit(coord[0], coord[1]);

        itsMyTurn = false;
        holder.btnTurn.setChecked(itsMyTurn);

        sendMessage("your turn");
    }

    private void miss(int[] coord) {
        mediaPlayer = MediaPlayer.create(this,R.raw.missingship);
        mediaPlayer.start();
        holder.bgOpponent.markCellMissed(coord[0], coord[1]);

        itsMyTurn = false;
        holder.btnTurn.setChecked(itsMyTurn);

        sendMessage("your turn");
    }

    private void checkShip(int x, int y) {
        if (holder.bgMine.thereIsAShipAt(x, y)) {
            holder.bgMine.markCellHit(x, y);
            sendMessage("HIT");
        }
        else {
            holder.bgMine.markCellMissed(x, y);
            sendMessage("MISS");
        }
    }

    private void sendMessage(String message) {
        Log.d("btsample", "send message");

        // Check that we're actually connected before trying anything
        if (BluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            Log.d("btsample", "---------bytes " + send);
            bluetoothService.write(send);

            // Reset out string buffer to zero
            //mOutStringBuffer.setLength(0);
        }
    }

    class Holder implements View.OnTouchListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener{
        Context context;
        BattleGridView bgMine;
        BattleGridView bgOpponent;
        Button btnFire;
        Button btnSurrender;
        EditText etCoords;
        ToggleButton btnTurn;

        @SuppressLint("ClickableViewAccessibility")
        Holder(Context context) {
            this.context = context;
            bgMine = findViewById(R.id.bgMine);
            bgOpponent = findViewById(R.id.bgEnemy);
            btnSurrender = findViewById(R.id.btnSurrender);
            btnFire = findViewById(R.id.btnFire);
            etCoords = findViewById(R.id.etCoords);
            btnTurn = findViewById(R.id.btnTurn);

            btnSurrender.setOnClickListener(this);
            btnFire.setOnClickListener(this);
            bgOpponent.setOnTouchListener(this);
            btnTurn.setOnCheckedChangeListener(this);

            if (itsMyTurn)
                Toast.makeText(getApplicationContext(), "Your turn", Toast.LENGTH_LONG).show();
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d("btsample", "------------------tocco1-------------");
                return true;
            }

            if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d("btsample", "------------------tocco3-------------");
                if (itsMyTurn) {
                    //int x = (int) event.getX();
                    //int y = (int) event.getY();

                    coord = calculateIndexes(x, y, bgOpponent);

                    String xString = String.valueOf(coord[0]);
                    String yString = String.valueOf(coord[1]);

                    Log.d("btsample", "x-y = " + coord[0] + " " + coord[1]);

                    message = xString + " " + yString;
                    sendMessage(message);

                }
                else {
                    Toast.makeText(getApplicationContext(), "Turn of the enemy", Toast.LENGTH_LONG).show();
                }
                return true;
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

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnSurrender){
                Toast.makeText(GameActivity.this, getResources().getString(R.string.game_over),Toast.LENGTH_LONG).show();
                //mediaPlayer = MediaPlayer.create(GameActivity.this);
                //mediaPlayer.start();
                sendMessage("YOU WIN");
                mediaPlayer = MediaPlayer.create(GameActivity.this,R.raw.lose);
                mediaPlayer.start();
                finishGame(getResources().getString(R.string.surrender));

            }
            else if(v.getId() == R.id.btnFire){
                if(itsMyTurn) {
                    if (etCoords.getText().toString().matches("[A-J]+[1-10]")) {
                        String coordinate = etCoords.getText().toString();
                        String y = String.valueOf(((int) coordinate.charAt(0)) -  65);
                        String x = String.valueOf(Integer.parseInt(coordinate.substring(1, 2)) - 1);

                        coord = new int[]{Integer.parseInt(x), Integer.parseInt(y)};

                        message = x + " " + y;
                        sendMessage(message);
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong Coordinates", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Turn of the enemy", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                buttonView.setBackgroundResource(R.color.color_my_turn);
                buttonView.setTextColor(ContextCompat.getColor(context, R.color.text_color_my_turn));
            }
            else {
                buttonView.setBackgroundResource(R.color.color_enemy_turn);
                buttonView.setTextColor(ContextCompat.getColor(context, R.color.text_color_enemy_turn));
            }
        }
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit match")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
