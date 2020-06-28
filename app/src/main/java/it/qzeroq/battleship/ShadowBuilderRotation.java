package it.qzeroq.battleship;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

import it.qzeroq.battleship.enums.Rotation;

public class ShadowBuilderRotation extends View.DragShadowBuilder {

    private View view;
    private Rotation rotation;

    public ShadowBuilderRotation(View view, Rotation rotation){
        super(view);
        this.view = view;
        this.rotation = rotation;
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        if(rotation == Rotation.ROTATION_90) {
            outShadowSize.set(view.getHeight(), view.getWidth());
            outShadowTouchPoint.set(outShadowSize.x / 2 + view.getHeight() / 2, outShadowSize.y / 2);
        }
        else{
            super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
        }
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        if(rotation == Rotation.ROTATION_90) {
            canvas.rotate(90, 0, 0);
            canvas.translate(0, -view.getHeight());
        }
        super.onDrawShadow(canvas);
    }
}
