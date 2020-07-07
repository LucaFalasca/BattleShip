package it.qzeroq.battleship.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.bluetooth.ChooseActivity;
import it.qzeroq.battleship.bluetooth.WaitActivity;
import it.qzeroq.battleship.database.GameHistoryActivity;


public class MainActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "btsample";

    private static final int CREATE_MATCH = 1;
    private static final int CONNECT_MATCH = 2;
    private static final int REQUEST_ENABLE_BT = 1;
    boolean q = false;
    Holder holder;
    BluetoothAdapter mBluetoothAdapter;

    //WaitingDialog waitingDialog = new WaitingDialog(MainActivity.this);

    //Handler handler = new Handler();
    //BluetoothService bs = new BluetoothService(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "MainActivity: ON CREATE");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        holder = new Holder();
        requestGPS();   //-------------meglio metterla quando si ricercano dispositivi---------------
        checkBluetooth();

    }

    private void requestGPS(){
        int MY_PERMISSION_LOCATION = 0;
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults)
    {
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, this.getResources().getText(R.string.no_permission), Toast.LENGTH_LONG).show();
        }
    }

    class Holder implements View.OnClickListener {
        Button btnStart;
        Button btnSetting;
        Button btnWait;


        Holder(){
            btnStart = findViewById(R.id.btnCreate);
            btnSetting = findViewById(R.id.btnSetting);
            btnWait = findViewById(R.id.btnConnect);

            btnStart.setOnClickListener(this);
            btnSetting.setOnClickListener(this);
            btnWait.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Handler handler = new Handler();
            //BluetoothService bs = new BluetoothService(handler);


            if(v.getId() == R.id.btnConnect) {
                Log.d(TAG, "MainActivity: click on btnConnect");
                Intent i = new Intent(MainActivity.this, ChooseActivity.class);
                startActivity(i);
            }
            else if(v.getId() == R.id.btnCreate){
                Log.d(TAG, "MainActivity: click on btnCreate");
                Intent i = new Intent(MainActivity.this, WaitActivity.class);
                startActivity(i);
            }
            else if(v.getId() == R.id.btnSetting){
                Log.d(TAG, "MainActivity: click on btnSetting");
                Intent intent = new Intent(MainActivity.this, GameHistoryActivity.class);
                startActivity(intent);
            }
        }


    }

    @Override
    public void onBackPressed() {

    }

    private void checkBluetooth() {
        Log.d(TAG, "checkBluetooth(): start");

        if (mBluetoothAdapter == null) {
            //Bluetooth not supported by the device
            Toast.makeText(MainActivity.this, "This device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
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
//                q = true;
            } else {
                Log.d(TAG, "BluetoothChat onActivityResult: REQUEST_ENABLE_BT CANCELED");
                Toast.makeText(this, "Bluetooth enabling cancelled. Leaving the chat", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
