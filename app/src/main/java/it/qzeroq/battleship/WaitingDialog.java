package it.qzeroq.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import it.qzeroq.battleship.R;

public class WaitingDialog {

    private Activity activity;
    private AlertDialog waitDialog;
    private ImageButton btnCancel;

    public WaitingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startWaitingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater =  activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_wait, null);

        btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissWaitingDialog();
                activity.onBackPressed();   //-------VEDERE SE FUNZIONA---------
            }
        });

        builder.setView(view);

        waitDialog = builder.create();
        waitDialog.show();
    }

    public void dismissWaitingDialog() {
        waitDialog.dismiss();
    }

}
