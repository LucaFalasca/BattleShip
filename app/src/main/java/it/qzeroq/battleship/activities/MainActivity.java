package it.qzeroq.battleship.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.bluetooth.BluetoothChat;
import it.qzeroq.battleship.bluetooth.BluetoothService;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Holder();
        requestGPS();
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
            btnStart = findViewById(R.id.btnStart);
            btnSetting = findViewById(R.id.btnSetting);
            btnWait = findViewById(R.id.btnWait);

            btnStart.setOnClickListener(this);
            btnSetting.setOnClickListener(this);
            btnWait.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Handler handler = new Handler();
            BluetoothService bs = new BluetoothService(handler);
            if(v.getId() == R.id.btnStart) {
                Intent i = new Intent(MainActivity.this, BluetoothChat.class);
                startActivity(i);
            }
            if(v.getId() == R.id.btnWait){
                Toast.makeText(getApplicationContext(), "Trying to connect", Toast.LENGTH_SHORT).show();
                bs.start();
                while(true){
                    if(bs.getState() == bs.STATE_CONNECTED){
                        Intent Int = new Intent(MainActivity.this, PositionShipActivity.class);
                        startActivity(Int);
                        break;
                    }
                }
            }
            if(v.getId() == R.id.btnSetting){
                Intent intent = new Intent(MainActivity.this, PositionShipActivity.class);
                startActivity(intent);
            }
        }
    }
}
