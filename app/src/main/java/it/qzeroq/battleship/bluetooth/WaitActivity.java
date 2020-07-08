package it.qzeroq.battleship.bluetooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.WaitingDialog;
import it.qzeroq.battleship.activities.MainActivity;
import it.qzeroq.battleship.activities.PositionShipActivity;

public class WaitActivity extends AppCompatActivity {

    private static final String TAG = "btsample";

    BluetoothAdapter mBluetoothAdapter;
    BluetoothService mChatService;

    WaitingDialog waitingDialog;

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBluetooth();

        while (!mBluetoothAdapter.isEnabled()) {
        }

        mChatService = BluetoothService.getInstance();
        mChatService.start();

        Intent dIntent =  new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        dIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(dIntent);

        waitingDialog = new WaitingDialog(WaitActivity.this);
        waitingDialog.startWaitingDialog();

        new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    if(BluetoothService.getState() == BluetoothService.STATE_CONNECTED){
                        waitingDialog.dismissWaitingDialog();
                        Intent i = new Intent(WaitActivity.this, PositionShipActivity.class);
                        i.putExtra("itsMyTurn", true);
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

    private void checkBluetooth() {
        Log.d(TAG, "checkBluetooth(): start");

        if (mBluetoothAdapter == null) {
            //Bluetooth not supported by the device
            Toast.makeText(WaitActivity.this, "This device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
            Log.d(TAG, "checkBluetooth(): btAdapter == null");
            finish();
        }
        else {
            //request to enable Bluetooth if it isn't on
            if (!mBluetoothAdapter.isEnabled()) {
                Log.d(TAG, "checkBluetooth(): start request to enable BT");
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, REQUEST_ENABLE_BT);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result of request if BT is enabled or not
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "BluetoothChat onActivityResult: REQUEST_ENABLE_BT OK");
                Toast.makeText(this, "Bluetooth is enable", Toast.LENGTH_SHORT).show();
                //setupChat();
            } else {
                Log.d(TAG, "BluetoothChat onActivityResult: REQUEST_ENABLE_BT CANCELED");
                Toast.makeText(this, "Bluetooth enabling cancelled. Leaving the game", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}