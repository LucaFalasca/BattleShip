package it.qzeroq.battleship.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.Ship;
import it.qzeroq.battleship.bluetooth.BluetoothService;
import it.qzeroq.battleship.database.Match;
import it.qzeroq.battleship.database.MatchViewModel;
import it.qzeroq.battleship.views.BattleGridView;

import static it.qzeroq.battleship.bluetooth.ChooseActivity.MESSAGE_READ;

public class GameActivity extends AppCompatActivity {

    private BluetoothService bluetoothService;
    private String message;
    private int[] coord;
    private MediaPlayer mediaPlayer;
    private Holder holder;
    private boolean itsMyTurn;
    private int enemySunkenShip;
    private boolean iWin = false;
    private boolean surrender = false;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_READ) {
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);


                switch (readMessage) {
                    case "your turn":
                        setTurn(true);
                        break;
                    case "HIT":
                        hit(coord);
                        break;
                    case "MISS":
                        miss(coord);
                        break;
                    case "YOU WIN":
                        iWin = true;
                        GameActivity.this.sendMessage("SHIP LOST " + holder.bgMine.getNumberOfSunkenShip());
                        mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.victory);
                        mediaPlayer.start();
                        Toast.makeText(getApplicationContext(), "You win!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        if(readMessage.startsWith("SHIP LOST")){
                            enemySunkenShip = Integer.parseInt(readMessage.substring(readMessage.length() - 1));
                            if(iWin){
                                finishGame(getResources().getString(R.string.win));
                            }
                            else if(surrender){
                                GameActivity.this.sendMessage("SHIP LOST " + holder.bgMine.getNumberOfSunkenShip());
                                finishGame(getResources().getString(R.string.surrender));
                            }
                            else{
                                GameActivity.this.sendMessage("SHIP LOST " + holder.bgMine.getNumberOfSunkenShip());
                                finishGame(getResources().getString(R.string.lose));
                            }

                        }
                        else {
                            String[] coordString = readMessage.split(" ");
                            coord = new int[]{Integer.parseInt(coordString[0]), Integer.parseInt(coordString[1])};

                            int x = coord[0];
                            int y = coord[1];

                            checkShip(x, y);
                        }
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mediaPlayer = MediaPlayer.create(this,R.raw.startgame);
        mediaPlayer.start();

        holder = new Holder(this);

        Intent i = getIntent();
        itsMyTurn = i.getBooleanExtra("itsMyTurn", false);

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

        setTurn(itsMyTurn);
    }

    private void checkShip(int x, int y) {
        if (holder.bgMine.thereIsAShipAt(x, y)) {
            holder.bgMine.markCellHit(x, y);
            if(!checkVictory())
                sendMessage("HIT");
        }
        else {
            holder.bgMine.markCellMissed(x, y);
            sendMessage("MISS");
        }
    }

    private void hit(int[] coord) {
        mediaPlayer = MediaPlayer.create(this,R.raw.hittingship);
        mediaPlayer.start();
        holder.bgOpponent.markCellHit(coord[0], coord[1]);

        setTurn(false);
    }

    private void miss(int[] coord) {
        mediaPlayer = MediaPlayer.create(this,R.raw.missing);
        mediaPlayer.start();
        holder.bgOpponent.markCellMissed(coord[0], coord[1]);

        setTurn(false);
    }

    private void setTurn(boolean turn){
        itsMyTurn = turn;
        holder.btnTurn.setChecked(turn);

        if (turn) {
            Toast.makeText(getApplicationContext(), "Your turn", Toast.LENGTH_SHORT).show();
        } else {
            sendMessage("your turn");
        }
    }

    private boolean checkVictory() {
        int sunkenShip = holder.bgMine.getNumberOfSunkenShip();
        System.out.println("Affondate: " + sunkenShip);
        if(sunkenShip == 7){
            sendMessage("YOU WIN");
            Toast.makeText(getApplicationContext(), "You Lose!", Toast.LENGTH_SHORT).show();
            mediaPlayer = MediaPlayer.create(this, R.raw.lose);
            mediaPlayer.start();
            return true;
        }
        return false;
    }

    private void finishGame(String result){

        MatchViewModel matchViewModel = new ViewModelProvider(this).get(MatchViewModel.class);
        Calendar calendar = Calendar.getInstance();
        String data = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        String nOfShipLost = String.valueOf(holder.bgMine.getNumberOfSunkenShip());
        String nOfShipHit = String.valueOf(enemySunkenShip);
        Match match = new Match(data, nOfShipHit, nOfShipLost, result);
        matchViewModel.insert(match);
        //DA TESTARE
        if(result.equals(getResources().getString(R.string.win))){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.match_over)
                    .setMessage(getResources().getString(R.string.game_win))
                    .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle(R.string.match_over)
                    .setMessage(getResources().getString(R.string.game_lose))
                    .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
        stopConnection();
    }

    private void stopConnection() {
        bluetoothService.stop();
    }

    private void sendMessage(String message) {

        // Check that we're actually connected before trying anything
        if (BluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            bluetoothService.write(send);
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

    class Holder implements View.OnTouchListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener{
        Context context;
        BattleGridView bgMine, bgOpponent;
        Button btnFire,  btnSurrender;
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
                return true;
            }

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if (itsMyTurn) {
                    coord = calculateIndexes(x, y, bgOpponent);
                    String xString = String.valueOf(coord[0]);
                    String yString = String.valueOf(coord[1]);

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
                sendMessage("YOU WIN");
                mediaPlayer = MediaPlayer.create(GameActivity.this,R.raw.lose);
                mediaPlayer.start();
                surrender = true;
            }
            else if(v.getId() == R.id.btnFire){
                if(itsMyTurn) {
                    if (etCoords.getText().toString().matches("[A-J]+([1-9]|10)")) {
                        String coordinate = etCoords.getText().toString();
                        String y = String.valueOf(((int) coordinate.charAt(0)) -  65);
                        String x = String.valueOf(Integer.parseInt(coordinate.substring(1)) - 1);

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
}
