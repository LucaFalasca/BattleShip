package it.qzeroq.battleship.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import static it.qzeroq.battleship.activities.BluetoothService.STATE_CONNECTED;
import static it.qzeroq.battleship.activities.BluetoothService.getState;

public class ChooseDevice extends AppCompatActivity {

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
    Holder holder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosedevice);

        Log.d(TAG, "ChooseDevice onCreate(): starting");

        btAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);


        holder = new Holder();
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

            Intent data = new Intent();

            //putting the information of the chosen device into the Intent
            if (parent.getId() == R.id.lvPrevDevices) {
                Log.d(TAG, "ChooseDevice onItemClick(): device = " + prevDeviceList.get(position).getName());
                data.putExtra("device", prevDeviceList.get(position));
                data.putExtra("layout", R.layout.activity_choosedevice);
            }
            else if (parent.getId() == R.id.lvDiscoveredDevices) {
                Log.d(TAG, "ChooseDevice onItemClick(): device = " + discoveredDeviceList.get(position).getName());
                data.putExtra("device", discoveredDeviceList.get(position));
                data.putExtra("layout", R.layout.activity_choosedevice);
            }

            // Set result and finish this Activity
            Log.d(TAG, "ChooseDevice onItemClick(): setResult()");
            setResult(Activity.RESULT_OK, data);
            finish();

        }


    }
}
