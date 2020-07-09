package it.qzeroq.battleship.bluetooth;


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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.activities.MainActivity;
import it.qzeroq.battleship.activities.PositionShipActivity;

/**
 * This is the activity by which the "client" user
 * chooses which "server" user to connect to.
 */

public class ChooseActivity extends AppCompatActivity {

    // debugging
    private static final String TAG = "btsample";

    // message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // request code for Bluetooth enabling
    private static final int REQUEST_ENABLE_BT = 1;

    // Device
    private BluetoothDevice targetDevice;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService bluetoothService = null;

    ArrayList<BluetoothDevice> discoveredDeviceList;
    ArrayList<String> discoveredDeviceName = new ArrayList<>();
    ArrayAdapter<String> discoveredArrayAdapter;
    Set<BluetoothDevice> prevDevices;
    ArrayList<BluetoothDevice> prevDeviceList;
    ArrayList<String> prevDeviceName = new ArrayList<>();
    ArrayAdapter<String> prevArrayAdapter;

    IntentFilter filter = new IntentFilter();


    Holder holder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosedevice);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(checkBluetooth()){
            setup();
        }

    }

    private boolean checkBluetooth() {

        if (mBluetoothAdapter == null) {
            //Bluetooth not supported by the device
            Toast.makeText(ChooseActivity.this, "This device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
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

    private void setup() {
        discoveredDeviceList = new ArrayList<>();
        discoveredDeviceName = new ArrayList<>();
        prevDevices = mBluetoothAdapter.getBondedDevices();

        holder = new Holder();

        filter = new IntentFilter();

        mBluetoothAdapter.startDiscovery();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        bluetoothService = BluetoothService.getInstance();
        bluetoothService.setHandler(mHandler);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Toast.makeText(getApplicationContext(), "Paired device.", Toast.LENGTH_LONG).show();
                    bluetoothService.connect(targetDevice);

                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(getApplicationContext(), "Pairing device.", Toast.LENGTH_LONG).show();
                }
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;

                if(!(discoveredDeviceList.contains(device)) && !(prevDeviceList.contains(device))) {
                    if(device.getName() != null) {
                        discoveredDeviceName.add(device.getName());
                        discoveredDeviceList.add(device);

                        discoveredArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private void pairDevice(BluetoothDevice targetDevice){
        if (targetDevice == null){
            Toast.makeText(getApplicationContext(), "Can't paired with that device.", Toast.LENGTH_LONG).show();
        }
        else {
            try {
                if (targetDevice.createBond()) {
                    filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    registerReceiver(receiver, filter);

                } else if (mBluetoothAdapter.getBondedDevices().contains(targetDevice)) {
                    Toast.makeText(getApplicationContext(), "Device already paired.", Toast.LENGTH_LONG).show();

                    bluetoothService.connect(targetDevice);

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to pair device.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, String.valueOf(e));
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
        if (bluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (BluetoothService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                bluetoothService.start();
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop the Bluetooth chat services
        if (bluetoothService != null)
            bluetoothService.stop();

        unregisterReceiver(receiver);
    }


    @Override
    public void onBackPressed() {
        if (bluetoothService != null)
            bluetoothService.stop();

        unregisterReceiver(receiver);
        Intent i = new Intent(ChooseActivity.this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            bluetoothService.write(send);

        }
    }




    // The Handler that gets information back from the BluetoothChatService
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                    case BluetoothService.STATE_CONNECTED:
                        ChooseActivity.this.sendMessage("connected");
                        break;
                    case BluetoothService.STATE_CONNECTING:
                    case BluetoothService.STATE_LISTEN:
                    case BluetoothService.STATE_NONE:
                        break;
                }
                break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if ("connected".equals(readMessage)) {
                        Intent i = new Intent(ChooseActivity.this, PositionShipActivity.class);
                        i.putExtra("itsMyTurn", false);
                        startActivity(i);
                    }
                    break;
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
                prevArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.row_list_view, prevDeviceName);
                lvPrevDevices.setAdapter(prevArrayAdapter);
                lvPrevDevices.setOnItemClickListener(this);
            }
            else {
                tvNoPrevDevices.setText(getResources().getString(R.string.no_prev));
            }

            //setting the adapter for the ListView
            discoveredArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.row_list_view, discoveredDeviceName);
            lvDiscoveredDevices.setAdapter(discoveredArrayAdapter);
            lvDiscoveredDevices.setOnItemClickListener(this);

        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mBluetoothAdapter.cancelDiscovery();

            //putting the information of the chosen device into the Intent
            if (parent.getId() == R.id.lvPrevDevices) {
                targetDevice = prevDeviceList.get(position);
            }
            else if (parent.getId() == R.id.lvDiscoveredDevices) {
                targetDevice = discoveredDeviceList.get(position);
            }
            pairDevice(targetDevice);
        }

    }



}