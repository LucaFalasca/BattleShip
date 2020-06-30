package it.qzeroq.battleship.activities;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.qzeroq.battleship.R;


/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChat extends AppCompatActivity {
    // Debugging
    private static final String TAG = "btsample";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PAIR_BT_DEVICE = 2;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    private Button btn_searchDevice;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Device
    private BluetoothDevice targetDevice;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService mChatService = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosedevice);

        Log.e(TAG, "BluetoothChat ON CREATE");

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "BluetoothChat ON START");

        checkBluetooth();

        if (mChatService == null)
            setupConnection();


    }


    private void checkBluetooth() {
        Log.d(TAG, "checkBluetooth(): start");

        if (mBluetoothAdapter == null) {
            //Bluetooth not supported by the device
            Toast.makeText(this, "This device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
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



    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.e(TAG, "BluetoothChat ON RESUME");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }


    private void setupConnection() {
        Log.d(TAG, "setupChat()");


        Intent i = new Intent(getApplicationContext(), ChooseDevice.class);
        i.putExtra("layout", R.layout.activity_choosedevice);
        startActivityForResult(i, PAIR_BT_DEVICE);


        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothService(mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();
        Log.e(TAG, "--- ON DESTROY ---");
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(BluetoothChat.this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            onBackPressed();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    /*private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = view.getText().toString();
                        sendMessage(message);
                    }
                    Log.d(TAG, "END onEditorAction");
                    return true;
                }
            };*/

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BluetoothChat BroadcastReceiver paired device");
                    Toast.makeText(getApplicationContext(), "Paired device.", Toast.LENGTH_LONG).show();
                    targetDevice = device;
                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BluetoothChat BroadcastReceiver pairing device");
                    Toast.makeText(getApplicationContext(), "Pairing device.", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    // The Handler that gets information back from the BluetoothChatService
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                        case BluetoothService.STATE_CONNECTING:
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    Log.d(TAG, "BluetoothChat WriteBuf");
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    Log.d(TAG, "BluetoothChat readBuf");
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result of request if BT is enabled or not
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "BluetoothChat onActivityResult: REQUEST_ENABLE_BT OK");
                Toast.makeText(this, "Bluetooth is enable", Toast.LENGTH_SHORT).show();
                //setupChat();
            }
            else {
                Log.d(TAG, "BluetoothChat onActivityResult: REQUEST_ENABLE_BT CANCELED");
                Toast.makeText(this, "Bluetooth enabling cancelled. Leaving the chat", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        //result of the choice for the player2 BT device
        else if (requestCode == PAIR_BT_DEVICE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "BluetoothChat onActivityResult: PAIR_BT_DEVICE OK");
                assert data != null;
                // get the BluetoothDevice object
                targetDevice = data.getParcelableExtra("device");
                if (targetDevice == null)
                    Log.d(TAG, "BluetoothChat onActivityResult: target device is null");
                else {
                    try {
                        Intent i = new Intent(this, GameActivity.class);
                        if (targetDevice.createBond()) {
                            Log.d(TAG, "BluetoothChat onActivityResult: createBond()");
                            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                            registerReceiver(receiver, filter);

                            mChatService.connect(targetDevice);

                            startActivity(i);

                        } else if (mBluetoothAdapter.getBondedDevices().contains(targetDevice)) {
                            Toast.makeText(getApplicationContext(), "Device already paired.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "BluetoothChat onActivityResult: device already paired");

                            mChatService.connect(targetDevice);
                            startActivity(i);

                        } else {
                            Log.d(TAG, "BluetoothChat onActivityResult: unable to pair device");
                            Toast.makeText(getApplicationContext(), "Unable to pair device.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, String.valueOf(e));
                    }
                }
            }
        }
    }
}