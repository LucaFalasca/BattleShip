package it.qzeroq.battleship.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.Ship;
import it.qzeroq.battleship.enums.Rotation;

public class BattleGridView extends GridLayout {

    final int CELL_SIDE = 50;
    final int GRID_SIZE = 10;
    Drawable backgroundCell;
    Drawable frameCell;
    Drawable selectionCell;
    Context context;
    ImageView[][] cells = new ImageView[GRID_SIZE][GRID_SIZE];
    TextView[] columns = new TextView[GRID_SIZE];
    TextView[] rows = new TextView[GRID_SIZE];
    TextView uselessCell;
    int side;

    public BattleGridView(Context context) {
        super(context);
        this.context = context;
        backgroundCell = ContextCompat.getDrawable(context, R.drawable.background_cell_sea);
        frameCell = ContextCompat.getDrawable(context, R.drawable.frame_cell);
        selectionCell = ContextCompat.getDrawable(context, R.drawable.selection_cell);
        init();
    }

    public BattleGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BattleGridView);
        backgroundCell = attributes.getDrawable(R.styleable.BattleGridView_background_cell);
        frameCell = attributes.getDrawable(R.styleable.BattleGridView_frame_cell);
        selectionCell = ContextCompat.getDrawable(context, R.drawable.selection_cell);
        attributes.recycle();
        init();
    }

    public BattleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
    }

    public Drawable getBackgroundCell() {
        return backgroundCell;
    }

    public void setBackgroundCell(Drawable backgroundCell) {
        this.backgroundCell = backgroundCell;
    }

    public Drawable getFrameCell() {
        return frameCell;
    }

    public void setFrameCell(Drawable frameCell) {
        this.frameCell = frameCell;
    }

    public int getSide() {
        return side;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getMeasuredWidth() > getMeasuredHeight()){
            side = getMeasuredHeight() / (GRID_SIZE + 1);
        }
        else{
            side = getMeasuredWidth() / (GRID_SIZE + 1);
        }
        setSizeCells(side);

    }

    private void init(){

        int realSize = GRID_SIZE + 1;
        this.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        this.setColumnCount(realSize);
        this.setRowCount(realSize);


        for(int i = 0; i < GRID_SIZE; i++) {
            for(int j = 0; j < GRID_SIZE; j++) {
                if (i == 0 && j == 0) {
                    uselessCell = new TextView(context);
                    this.addView(uselessCell);
                    for(int k = 0; k < GRID_SIZE; k++) {
                        columns[k] = new TextView(context);
                        columns[k].setText(String.valueOf(k + 1));
                        columns[k].setGravity(View.TEXT_ALIGNMENT_GRAVITY);
                        this.addView(columns[k]);
                    }
                }
                if(j == 0){
                    rows[i] = new TextView(context);
                    rows[i].setText(Character.toString((char) (65 + i)));
                    rows[i].setGravity(View.TEXT_ALIGNMENT_GRAVITY);
                    this.addView(rows[i]);
                }
                cells[j][i] = new ImageView(context);
                cells[j][i].setBackground(backgroundCell);
                cells[j][i].setForeground(frameCell);
                this.addView(cells[j][i]);
            }
        }
        setSizeCells(CELL_SIDE);
    }

    private void setSizeCells(int side) {

        for(int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = side ;
                param.width = side;
                cells[i][j].setLayoutParams(param);
            }
        }
        for(int i = 0; i < GRID_SIZE; i++){
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = side;
            param.width = side;
            columns[i].setLayoutParams(param);
        }

        for(int i = 0; i < GRID_SIZE; i++){
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = side ;
            param.width = side;
            rows[i].setLayoutParams(param);
        }
    }

    public void placeShip(Ship ship, int x, int y, Rotation rotation){
        removeSelection();
        ship.applyRotation(rotation);
        Drawable[] sprites = ship.getSprites();
        for(int i = 0; i < sprites.length; i++){
            cells[x + i][y].setImageDrawable(sprites[i]);
        }
    }

    public void showSelection(Ship ship, int x, int y, Rotation rotation){
        ship.applyRotation(rotation);
        for(int i = 0; i < ship.getLenghtShip(); i++){
            cells[x + i][y].setForeground(selectionCell);
        }
    }

    public void removeSelection(){
        for(int i = 0; i < GRID_SIZE; i++){
            for(int j = 0; j < GRID_SIZE; j++) {
                cells[i][j].setForeground(frameCell);
            }
        }
    }

    public void removeShip(){

    }

    public void markCellMissed(){

    }

    public void markCellHit(){

    }

}
