package it.qzeroq.battleship.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
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


public class MainActivity extends AppCompatActivity {

    // debugging
    private static final String TAG = "btsample";

    // layout holder
    Holder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        holder = new Holder();

        // location request to find nearby Bluetooth devices
        requestGPS();
    }

    /**
     * Every time the app is launched it checks that it has permission
     * to access the location and, if not, asks the user if he wants to grant it.
     */
    private void requestGPS(){
        int MY_PERMISSION_LOCATION = 0;
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults)
    {
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    this.getResources().getText(R.string.no_permission),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    class Holder implements View.OnClickListener {
        Button btnStart;
        Button btnSetting;
        Button btnWait;

        Holder() {
            btnStart = findViewById(R.id.btnCreate);
            btnSetting = findViewById(R.id.btnSetting);
            btnWait = findViewById(R.id.btnConnect);

            btnStart.setOnClickListener(this);
            btnSetting.setOnClickListener(this);
            btnWait.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnConnect) {
                Intent i = new Intent(MainActivity.this, ChooseActivity.class);
                startActivity(i);
            }
            else if(v.getId() == R.id.btnCreate){
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
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
