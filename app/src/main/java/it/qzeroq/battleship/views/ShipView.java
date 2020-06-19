package it.qzeroq.battleship.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.Ship;

public class ShipView extends LinearLayout {

    private Ship ship;
    private int lenghtShip;
    private Context context;

    public ShipView(Context context) {
        this(context, null);
    }

    public ShipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.style.ShipDefault);
        this.context = context;

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ShipView, 0, R.style.ShipDefault);
        lenghtShip = attributes.getInt(R.styleable.ShipView_lenght, 4);
        attributes.recycle();

        init();
    }

    private void init(){
        this.setOrientation(HORIZONTAL);

        ship = new Ship(context, lenghtShip);
        Drawable[] sprites = ship.getSprites();
        for(int i = 0; i < sprites.length; i++) {
            ImageView img = new ImageView(context);
            img.setImageDrawable(sprites[i]);
            this.addView(img);
        }
    }

    public void setLenghtShip(int lenghtShip) {
        this.lenghtShip = lenghtShip;
        init();
    }

    public int getLenghtShip() {
        return lenghtShip;
    }
}
