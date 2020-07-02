package it.qzeroq.battleship.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.bluetooth.BluetoothService;
import it.qzeroq.battleship.bluetooth.BluetoothGameActivity;


public class MainActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "btsample";

    private static final int CREATE_MATCH = 1;
    private static final int CONNECT_MATCH = 2;


    //WaitingDialog waitingDialog = new WaitingDialog(MainActivity.this);

    //Handler handler = new Handler();
    //BluetoothService bs = new BluetoothService(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "MainActivity: ON CREATE");

        new Holder();
        requestGPS();   //-------------meglio metterla quando si ricercano dispositivi---------------
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
                Intent i = new Intent(MainActivity.this, BluetoothGameActivity.class);
                i.putExtra("match", CONNECT_MATCH);
                startActivity(i);
            }
            else if(v.getId() == R.id.btnCreate){
                Log.d(TAG, "MainActivity: click on btnCreate");
                Intent i = new Intent(MainActivity.this, BluetoothGameActivity.class);
                i.putExtra("match", CREATE_MATCH);
                startActivity(i);
                /*
                waitingDialog.startWaitingDialog();
                //Toast.makeText(getApplicationContext(), "Trying to connect", Toast.LENGTH_SHORT).show();
                bs.start();
                while(true){
                    if(bs.getState() == BluetoothService.STATE_CONNECTED){
                        waitingDialog.dismissWaitingDialog();
                        Intent Int = new Intent(MainActivity.this, PositionShipActivity.class);
                        startActivity(Int);
                        break;
                    }
                }*/
            }
            else if(v.getId() == R.id.btnSetting){
                Log.d(TAG, "MainActivity: click on btnSetting");
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        }
    }
}
