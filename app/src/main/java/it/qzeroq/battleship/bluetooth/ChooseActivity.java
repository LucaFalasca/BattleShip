package it.qzeroq.battleship.bluetooth;


import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.WaitingDialog;
import it.qzeroq.battleship.activities.MainActivity;
import it.qzeroq.battleship.activities.PositionShipActivity;


/**
 * This is the main Activity that displays the current chat session.
 */
public class ChooseActivity extends AppCompatActivity {
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
//    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PAIR_BT_DEVICE = 2;

    private static final int CREATE_MATCH = 1;
    private static final int CONNECT_MATCH = 2;

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
    private int match = 0;


    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<BluetoothDevice> discoveredDeviceList = new ArrayList<>();
    ArrayList<String> discoveredDeviceName = new ArrayList<>();
    ArrayAdapter<String> discoveredArrayAdapter;
    Set<BluetoothDevice> prevDevices = btAdapter.getBondedDevices();
    ArrayList<BluetoothDevice> prevDeviceList;
    ArrayList<String> prevDeviceName = new ArrayList<>();
    ArrayAdapter<String> prevArrayAdapter;

    //WaitingDialog waitingDialog = new WaitingDialog(ChooseActivity.this);

    IntentFilter filter = new IntentFilter();

    Holder holder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosedevice);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        holder = new Holder();
//        else if (match == CREATE_MATCH) {
//            setContentView(R.layout.activity_main); // così sotto rimane mostrato il layout della mainactivity
//            waitingDialog.startWaitingDialog();
//        }

        // Get local Bluetooth adapter
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //checkBluetooth();

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "BluetoothChat ON START");

        if (mChatService == null)
            setupConnection();

    }

    /*private void checkBluetooth() {
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
    }*/

    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.e(TAG, "BluetoothChat ON RESUME");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        /*if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }*/

        /*while(true){
            if(mChatService.getState() == BluetoothService.STATE_CONNECTED){
                Intent Int = new Intent(ChooseActivity.this, PositionShipActivity.class);
                startActivity(Int);
                break;
            }
        }*/
    }


    private void setupConnection() {
        Log.d(TAG, "setupChat()");

        findDevice();
        /*Intent i = new Intent(getApplicationContext(), ChooseDeviceActivity.class);
        startActivityForResult(i, PAIR_BT_DEVICE);*/


        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothService(mHandler);

        // Initialize the buffer for outgoing messages
        //mOutStringBuffer = new StringBuffer();
    }

    private void findDevice() {
        btAdapter.startDiscovery();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();
        Log.e(TAG, "--- ON DESTROY ---");

        unregisterReceiver(receiver);
    }


    @Override
    public void onBackPressed() {
        if (mChatService != null)
            mChatService.stop();
        Log.e(TAG, "--- ONBACKPRESSED ---");

        unregisterReceiver(receiver);
        Intent i = new Intent(ChooseActivity.this, MainActivity.class);
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
                    mChatService.connect(targetDevice);

                    while(true){
                        if(mChatService.getState() == BluetoothService.STATE_CONNECTED){
                            Intent i = new Intent(ChooseActivity.this, PositionShipActivity.class);
                            startActivity(i);
                            break;
                        }
                    }

                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BluetoothChat BroadcastReceiver pairing device");
                    Toast.makeText(getApplicationContext(), "Pairing device.", Toast.LENGTH_LONG).show();

                }
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                Log.d(TAG, "ChooseDevice BroadcastReceiver: device found " + device.getName()+" "+ device.getAddress() );
                if(!(discoveredDeviceName.contains(device)) && !(prevDeviceList.contains(device))) {
                    discoveredDeviceName.add(device.getName());
                    discoveredDeviceList.add(device);

                    discoveredArrayAdapter.notifyDataSetChanged();
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
                /*case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;*/
            }
        }
    };

    class Holder implements AdapterView.OnItemClickListener {
        TextView tvPrevBtDevices;
        TextView tvNoPrevDevices;
        TextView tvDiscoveredBTDevices;
        ListView lvPrevDevices;
        ListView lvDiscoveredDevices;

        Holder() {
            tvPrevBtDevices = findViewById(R.id.tvPrevBtDevices);
            tvNoPrevDevices = findViewById(R.id.tvNoPrevDevices);
            tvDiscoveredBTDevices = findViewById(R.id.tvDiscoveredBTDevices);
            lvPrevDevices = findViewById(R.id.lvPrevDevices);
            lvDiscoveredDevices = findViewById(R.id.lvDiscoveredDevices);


            //setting previously connected BT devices
            if (prevDevices.size() > 0) {
                prevDeviceList = new ArrayList<>(prevDevices);
                for (BluetoothDevice device : prevDeviceList) {
                    prevDeviceName.add(device.getName());
                }
                prevArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, prevDeviceName);
                lvPrevDevices.setAdapter(prevArrayAdapter);
                lvPrevDevices.setOnItemClickListener(this);
            }
            else {
                tvNoPrevDevices.setText("No previously connected devices.");
            }

            //setting the adapter for the ListView
            discoveredArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, discoveredDeviceName);
            lvDiscoveredDevices.setAdapter(discoveredArrayAdapter);
            lvDiscoveredDevices.setOnItemClickListener(this);

        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            btAdapter.cancelDiscovery();

            //putting the information of the chosen device into the Intent
            if (parent.getId() == R.id.lvPrevDevices) {
                Log.d(TAG, "ChooseDevice onItemClick(): device = " + prevDeviceList.get(position).getName());
                targetDevice = prevDeviceList.get(position);
            }
            else if (parent.getId() == R.id.lvDiscoveredDevices) {
                Log.d(TAG, "ChooseDevice onItemClick(): device = " + discoveredDeviceList.get(position).getName());
                targetDevice = discoveredDeviceList.get(position);
            }
            pairDevice(targetDevice);
        }



    }


    private void pairDevice(BluetoothDevice targetDevice){
        if (targetDevice == null)
            Log.d(TAG, "BluetoothChat onActivityResult: target device is null");
        else {
            try {
                if (targetDevice.createBond()) {
                    Log.d(TAG, "BluetoothChat onActivityResult: createBond()");
                    filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    registerReceiver(receiver, filter);

//                    mChatService.connect(targetDevice);

                } else if (mBluetoothAdapter.getBondedDevices().contains(targetDevice)) {
                    Toast.makeText(getApplicationContext(), "Device already paired.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "BluetoothChat onActivityResult: device already paired");

                    mChatService.connect(targetDevice);
                    while(true){
                        if(mChatService.getState() == BluetoothService.STATE_CONNECTED){
                            Intent i = new Intent(ChooseActivity.this, PositionShipActivity.class);
                            startActivity(i);
                            break;
                        }
                    }

                } else {
                    Log.d(TAG, "BluetoothChat onActivityResult: unable to pair device");
                    Toast.makeText(getApplicationContext(), "Unable to pair device.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, String.valueOf(e));
            }
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result of request if BT is enabled or not
       *//* if (requestCode == REQUEST_ENABLE_BT) {
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
        }*//*
        //result of the choice for the player2 BT device
        if (requestCode == PAIR_BT_DEVICE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "BluetoothChat onActivityResult: PAIR_BT_DEVICE OK");
                assert data != null;
                // get the BluetoothDevice object
                targetDevice = data.getParcelableExtra("device");
                if (targetDevice == null)
                    Log.d(TAG, "BluetoothChat onActivityResult: target device is null");
                else {
                    try {
                        if (targetDevice.createBond()) {
                            Log.d(TAG, "BluetoothChat onActivityResult: createBond()");
                            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                            registerReceiver(receiver, filter);

                            mChatService.connect(targetDevice);

                        } else if (mBluetoothAdapter.getBondedDevices().contains(targetDevice)) {
                            Toast.makeText(getApplicationContext(), "Device already paired.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "BluetoothChat onActivityResult: device already paired");

                            mChatService.connect(targetDevice);

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
    }*/
}