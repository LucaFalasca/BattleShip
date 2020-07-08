package it.qzeroq.battleship;

import android.content.Context;
import android.content.res.Resources;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
        this(context, length, Rotation.ROTATION_0);
    }

    public Ship(Context context, int length, Rotation rotation){
        this.context = context;
        this.length = length;
        this.rotation = rotation;
        setSprites(length, rotation);
    }

    private void setSprites(int length, Rotation rotation) {
        if (rotation == Rotation.ROTATION_0) {
            switch (length) {
                case 1:
                    sprites = new Drawable[]{
                            ContextCompat.getDrawable(context, R.drawable.ic_crop_16_9_black_24dp)
                    };
                    break;
                case 2:
                    sprites = new Drawable[]{
                            ContextCompat.getDrawable(context, R.drawable.ship_l2_horizontal_s1_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l2_horizontal_s2_72),
                    };
                    break;
                case 3:
                    sprites = new Drawable[]{
                            ContextCompat.getDrawable(context, R.drawable.ship_l3_horizontal_s1_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l3_horizontal_s2_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l3_horizontal_s3_72)
                    };
                    break;
                case 4:
                    sprites = new Drawable[]{
                            ContextCompat.getDrawable(context, R.drawable.ship_l4_horizontal_s1_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l4_horizontal_s2_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l4_horizontal_s3_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l4_horizontal_s4_72)
                    };
                    break;
            }
        }
        else if(rotation == Rotation.ROTATION_90){
            switch (length) {
                case 1:
                    sprites = new Drawable[]{
                            ContextCompat.getDrawable(context, R.drawable.ic_crop_16_9_black_24dp)
                    };
                    break;
                case 2:
                    sprites = new Drawable[]{
                            ContextCompat.getDrawable(context, R.drawable.ship_l2_vertical_s1_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l2_vertical_s2_72),
                    };
                    break;
                case 3:
                    sprites = new Drawable[]{
                            ContextCompat.getDrawable(context, R.drawable.ship_l3_vertical_s1_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l3_vertical_s2_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l3_vertical_s3_72)
                    };
                    break;
                case 4:
                    sprites = new Drawable[]{
                            ContextCompat.getDrawable(context, R.drawable.ship_l4_vertical_s1_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l4_vertical_s2_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l4_vertical_s3_72),
                            ContextCompat.getDrawable(context, R.drawable.ship_l4_vertical_s4_72)
                    };
                    break;
            }
        }
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
        setSprites(length, rotation);
    }

    private Drawable rotate(Drawable sprite, int angleRotation) {
        Matrix m = new Matrix();
        m.postRotate(angleRotation);

        BitmapDrawable bd = (BitmapDrawable) sprite;
        Bitmap bitmap = bd.getBitmap();

        Bitmap rotatedBitap = Bitmap.createBitmap(bitmap, 0, 0, sprite.getIntrinsicWidth(), sprite.getIntrinsicHeight(), m, true);
        return new BitmapDrawable(Resources.getSystem(), rotatedBitap);
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
