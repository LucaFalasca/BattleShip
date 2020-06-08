package it.qzeroq.battleship;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.w3c.dom.Attr;

public class BattleGridView extends LinearLayout {

    int side;
    Context context;

    public BattleGridView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BattleGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getMeasuredWidth() > getMeasuredHeight()){
            side = getMeasuredHeight() / 11;
        }
    }

    private void init(){
        this.setOrientation(VERTICAL);
        ImageView[][] img = new ImageView[10][10];
        for(int i = 0; i < 10; i++) {
            LinearLayout t = new LinearLayout(context);

            for(int j = 0; j < 10; j++) {
                img[i][j] = new ImageView(context);
                //img[i][j].setText(i + " " + j + "|");
                /*
                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.default_drink);
                Matrix matrix = new Matrix();
                matrix.postScale(0.5f, 0.5f);
                Bitmap b2 = Bitmap.createBitmap(b, 100, 100, 100, 100, matrix, true);
                img[i][j].setImageBitmap(b2);
                 */
                img[i][j].setImageResource(R.drawable.ic_prova);
                t.addView(img[i][j]);

                //img[i][j].setLayoutParams(layoutParams);
            }
            this.addView(t);
        }

    }

}
