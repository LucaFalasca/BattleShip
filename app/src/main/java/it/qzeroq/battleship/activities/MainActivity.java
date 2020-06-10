package it.qzeroq.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.qzeroq.battleship.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Holder holder = new Holder();
    }

    class Holder implements View.OnClickListener {
        Button btnStart;
        Button btnSetting;

        Holder(){
            btnStart = findViewById(R.id.btnStart);
            btnSetting = findViewById(R.id.btnSetting);

            btnStart.setOnClickListener(this);
            btnSetting.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnStart){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        }
    }
}
