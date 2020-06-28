package it.qzeroq.battleship;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import it.qzeroq.battleship.enums.Rotation;

public class Ship {

    private Drawable[] sprites;
    private int length;
    private Rotation rotation;

    public Ship(Context context, int length){
        switch(length){
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
        this.length = length;
        rotation = Rotation.ROTATION_0;
    }

    public Ship(Context context, int lengthShip, Rotation rotation){
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
        this.length = lengthShip;
        this.rotation = rotation;
    }

    public Drawable[] getSprites() {
        return sprites;
    }

    public int getLength() {
        return length;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public Rotation getRotation(){
        return rotation;
    }

    public void changeRotation(){
        if(rotation == Rotation.ROTATION_0){
            rotation = Rotation.ROTATION_90;
        }
        else{
            rotation = Rotation.ROTATION_0;
        }
    }

    public int getAngleRotation(){
        switch (rotation){
            case ROTATION_0:
                return 0;
            case ROTATION_90:
                return 90;
        }
        return 0;
    }
}
