package it.qzeroq.battleship;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

public class WaitingDialog {

    private Activity activity;
    private AlertDialog waitDialog;

    public WaitingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startWaitingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater =  activity.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_wait, null);

        ImageButton btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissWaitingDialog();
                activity.onBackPressed();
            }
        });

        builder.setView(view);

        waitDialog = builder.create();
        waitDialog.show();
    }

    private void dismissWaitingDialog() {
        waitDialog.dismiss();
    }

}
