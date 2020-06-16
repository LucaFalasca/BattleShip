package it.qzeroq.battleship;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import it.qzeroq.battleship.enums.LengthShip;
import it.qzeroq.battleship.enums.Rotation;

public class Ship {

    private Drawable[] sprites;


    public Ship(Context context, int lengthShip){
        switch(lengthShip){
            case 2:
                sprites = new Drawable[]{
                        ContextCompat.getDrawable(context, R.drawable.ic_prova),
                        ContextCompat.getDrawable(context, R.drawable.ic_prova)
                };
                break;
            case 3:
                sprites = new Drawable[]{
                        ContextCompat.getDrawable(context, R.drawable.ic_prova),
                        ContextCompat.getDrawable(context, R.drawable.ic_prova),
                        ContextCompat.getDrawable(context, R.drawable.ic_prova)
                };
                break;
            case 4:
                sprites = new Drawable[]{
                        ContextCompat.getDrawable(context, R.drawable.ic_prova),
                        ContextCompat.getDrawable(context, R.drawable.ic_prova),
                        ContextCompat.getDrawable(context, R.drawable.ic_prova),
                        ContextCompat.getDrawable(context, R.drawable.ic_prova)
                };
                break;
        }

    }

    public Drawable[] getSprites() {
        return sprites;
    }

    public void applyRotation(Rotation rotation) {
    }
}
