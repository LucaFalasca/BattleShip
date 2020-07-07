package it.qzeroq.battleship;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.core.content.ContextCompat;

import java.io.Serializable;

import it.qzeroq.battleship.enums.Rotation;

public class Ship implements Parcelable {

    private Drawable[] sprites;
    private int length;
    private Rotation rotation;
    private Context context;

    public Ship(Context context, int length){
        switch(length){
            case 1:
                sprites = new Drawable[]{
                        ContextCompat.getDrawable(context, R.drawable.ic_prova)
                };
                break;
            case 2:
                sprites = new Drawable[]{
                        ContextCompat.getDrawable(context, R.drawable.ic_prova),
                        ContextCompat.getDrawable(context, R.drawable.ic_prova),
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
        this.context = context;
    }

    public Ship(Context context, int lengthShip, Rotation rotation){
        this(context, lengthShip);
        this.rotation = rotation;
    }

    protected Ship(Parcel in) {
        length = in.readInt();
        switch(in.readInt()){
            case 0:
                rotation = Rotation.ROTATION_0;
                break;
            case 90:
                rotation = Rotation.ROTATION_90;
                break;
        }
    }


    public static final Creator<Ship> CREATOR = new Creator<Ship>() {
        @Override
        public Ship createFromParcel(Parcel in) {
            return new Ship(in);
        }

        @Override
        public Ship[] newArray(int size) {
            return new Ship[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(length);
        dest.writeInt(getAngleRotation());
    }
}
