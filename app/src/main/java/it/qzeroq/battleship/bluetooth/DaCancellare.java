package it.qzeroq.battleship.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

import it.qzeroq.battleship.R;

//import static it.qzeroq.battleship.activities.bluetooth.BluetoothService.STATE_CONNECTED;
//import static it.qzeroq.battleship.activities.BluetoothService.getState;

public class DaCancellare extends AppCompatActivity {

    // Debugging
    private static final String TAG = "btsample";


    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<BluetoothDevice> discoveredDeviceList = new ArrayList<>();
    ArrayList<String> discoveredDeviceName = new ArrayList<>();
    ArrayAdapter<String> discoveredArrayAdapter;
    Set<BluetoothDevice> prevDevices = btAdapter.getBondedDevices();
    ArrayList<BluetoothDevice> prevDeviceList;
    ArrayList<String> prevDeviceName = new ArrayList<>();
    ArrayAdapter<String> prevArrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosedevice);

        Log.d(TAG, "ChooseDevice onCreate(): starting");

        btAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);


    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                Log.d(TAG, "ChooseDevice BroadcastReceiver: device found" + device.getName()+" "+ device.getAddress() );
                discoveredDeviceName.add(device.getName());
                discoveredDeviceList.add(device);
                discoveredArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ChooseDevice onDestroy()");

        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Set result CANCELED in case the user backs out
        if (btAdapter != null) {
            btAdapter.cancelDiscovery();
        }
        unregisterReceiver(receiver);
        setResult(Activity.RESULT_CANCELED);
    }


}
