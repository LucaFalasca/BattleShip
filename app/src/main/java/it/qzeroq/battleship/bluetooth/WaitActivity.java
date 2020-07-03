package it.qzeroq.battleship.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.activities.MainActivity;
import it.qzeroq.battleship.activities.PositionShipActivity;

public class WaitActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothService mChatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mChatService = new BluetoothService(mHandler);
        mChatService.start();

        Intent dIntent =  new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        dIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(dIntent);

        new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    if(mChatService.getState() == BluetoothService.STATE_CONNECTED){
                        Intent i = new Intent(WaitActivity.this, PositionShipActivity.class);
                        startActivity(i);
                        break;
                    }
                }
            }
        }.start();



    }

    @Override
    public void onBackPressed() {
        mChatService.stop();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    // The Handler that gets information back from the BluetoothChatService
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };


}