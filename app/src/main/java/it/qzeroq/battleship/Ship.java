package it.qzeroq.battleship;

import android.graphics.drawable.Drawable;

import it.qzeroq.battleship.enums.LengthShip;
import it.qzeroq.battleship.enums.Rotation;

public class Ship {

    private Drawable[] sprites;

    public Ship(LengthShip lengthShip){
        switch(lengthShip){
            case X_TWO:
                sprites = new Drawable[]{

                };
                break;
            case X_THREE:
                sprites = new Drawable[]{

                };
                break;
            case X_FOUR:
                sprites = new Drawable[]{

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
