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

    Ship ship;
    int lenghtShip;
    Context context;

    public ShipView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ShipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ShipView);
        lenghtShip = attributes.getInt(R.styleable.ShipView_lenght, 4);
        attributes.recycle();
        init();
    }

    public ShipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ShipView);
        lenghtShip = attributes.getInt(R.styleable.ShipView_lenght, 4);
        attributes.recycle();
        init();
    }

    public ShipView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
}
