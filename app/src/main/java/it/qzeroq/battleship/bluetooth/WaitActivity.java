package it.qzeroq.battleship.bluetooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
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

import static it.qzeroq.battleship.bluetooth.ChooseActivity.MESSAGE_READ;
import static it.qzeroq.battleship.bluetooth.ChooseActivity.MESSAGE_DEVICE_NAME;

public class WaitActivity extends AppCompatActivity {

    private static final String TAG = "btsample";

    String enemyDevice;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothService mChatService;

    WaitingDialog waitingDialog;
    BluetoothService bluetoothService;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(checkBluetooth()){
            setup();
        }

    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent i = new Intent(WaitActivity.this, PositionShipActivity.class);
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    if ("connected".equals(readMessage)) {
                        WaitActivity.this.sendMessage("connected");
                        i.putExtra("itsMyTurn", true);
                        startActivity(i);
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    enemyDevice = msg.getData().getString("device_name","null");
                    i.putExtra("enemyDevice", enemyDevice);
                    Toast.makeText(getApplicationContext(), "Connected to " + enemyDevice, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (BluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            onBackPressed();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

        }
    }

    private void setup(){
        mChatService = BluetoothService.getInstance();
        mChatService.start();

        Intent dIntent =  new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        dIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(dIntent);

        waitingDialog = new WaitingDialog(WaitActivity.this);
        waitingDialog.startWaitingDialog();
        bluetoothService = BluetoothService.getInstance();
        bluetoothService.setHandler(handler);
    }
    @Override
    public void onBackPressed() {
        mChatService.stop();
        mBluetoothAdapter.cancelDiscovery();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private boolean checkBluetooth() {
        Log.d(TAG, "checkBluetooth(): start");

        if (mBluetoothAdapter == null) {
            //Bluetooth not supported by the device
            Toast.makeText(WaitActivity.this, "This device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        else {
            //request to enable Bluetooth if it isn't on
            if (!mBluetoothAdapter.isEnabled()) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, REQUEST_ENABLE_BT);
                return false;
            }
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result of request if BT is enabled or not
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth is enable", Toast.LENGTH_SHORT).show();
                setup();
            } else {
                Toast.makeText(this, "Bluetooth enabling cancelled. Leaving the game", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}