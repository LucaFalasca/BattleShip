package it.qzeroq.battleship.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import it.qzeroq.battleship.R;
import it.qzeroq.battleship.Ship;
import it.qzeroq.battleship.enums.Rotation;

public class BattleGridView extends GridLayout {

    private final int CELL_SIDE = 50;
    private final int GRID_SIZE = 10;
    private Drawable backgroundCell;
    private Drawable frameCell;
    private Drawable selectionCell;
    private Drawable selectionCellWrong;
    private Context context;
    private ImageView[][] cells = new ImageView[GRID_SIZE][GRID_SIZE];
    private Ship[][] ships = new Ship[GRID_SIZE][GRID_SIZE];
    private TextView[] columns = new TextView[GRID_SIZE];
    private TextView[] rows = new TextView[GRID_SIZE];
    private TextView uselessCell;
    private int side;

    public BattleGridView(Context context) {
        super(context, null, R.style.BattleGridDefault);
        this.context = context;
        init();
    }

    public BattleGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.style.BattleGridDefault);
        this.context = context;

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BattleGridView, 0, R.style.BattleGridDefault);
        backgroundCell = attributes.getDrawable(R.styleable.BattleGridView_background_cell);
        frameCell = attributes.getDrawable(R.styleable.BattleGridView_frame_cell);
        selectionCell = attributes.getDrawable(R.styleable.BattleGridView_selection_cell);
        selectionCellWrong = attributes.getDrawable(R.styleable.BattleGridView_selection_cell_wrong);
        attributes.recycle();

        init();
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
                        columns[k].setGravity(Gravity.CENTER);
                        this.addView(columns[k]);
                    }
                }
                if(j == 0){
                    rows[i] = new TextView(context);
                    rows[i].setText(Character.toString((char) (65 + i)));
                    rows[i].setGravity(Gravity.CENTER);
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
            setSideView(side, columns[i]);
            setSideView(side, rows[i]);
            for (int j = 0; j < 10; j++) {
                setSideView(side, cells[i][j]);
            }
        }
    }

    private void setSideView(int side, View view){
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = side ;
        param.width = side;
        view.setLayoutParams(param);
    }

    public boolean placeShip(Ship ship, int x, int y, Rotation rotation){
        removeSelection();
        ship.applyRotation(rotation);
        Drawable[] sprites = ship.getSprites();
        if(x + ship.getLenghtShip() <= GRID_SIZE ) {
            for(int i = 0; i < ship.getLenghtShip(); i++){
                if(thereIsAShipAt(x + i, y)){
                    return false;
                }
            }
            ships[x][y] = ship;
            for (int i = 0; i < sprites.length; i++) {
                cells[x + i][y].setImageDrawable(sprites[i]);

            }
        }
        else{
            int q = GRID_SIZE - ship.getLenghtShip();
            for(int i = 0; i < ship.getLenghtShip(); i++){
                if(thereIsAShipAt(q + i, y)){
                    return false;
                }
            }
            for (int i = 0; i < ship.getLenghtShip(); i++) {
                ships[q][y] = ship;
                cells[q + i][y].setImageDrawable(sprites[i]);
            }
        }
        return true;
    }

    public void showSelection(Ship ship, int x, int y, Rotation rotation){
        ship.applyRotation(rotation);
        if(x + ship.getLenghtShip() <= GRID_SIZE ) {
            for (int i = 0; i < ship.getLenghtShip(); i++) {
                if(thereIsAShipAt(x + i, y)){
                    cells[x + i][y].setForeground(selectionCellWrong);
                }
                else {
                    cells[x + i][y].setForeground(selectionCell);
                }
            }
        }
        else{
            for (int i = 0; i < ship.getLenghtShip(); i++) {
                int q = GRID_SIZE - ship.getLenghtShip();
                if(thereIsAShipAt(q + i, y)){
                    cells[q + i][y].setForeground(selectionCellWrong);
                }
                else {
                    cells[q + i][y].setForeground(selectionCell);
                }

            }
        }
    }

    public void removeSelection(){
        for(int i = 0; i < GRID_SIZE; i++){
            for(int j = 0; j < GRID_SIZE; j++) {
                cells[i][j].setForeground(frameCell);
            }
        }
    }

    public void removeShipAt(int x, int y){
        for(int k = 0; k < GRID_SIZE; k++){
            if(ships[x][k] != null){
                if(Math.abs(y - k) < ships[x][k].getLenghtShip()){
                    for(int i = 0; i < ships[x][k].getLenghtShip(); i++){
                        cells[x + i][k].setImageDrawable(null);
                    }
                    ships[x][k] = null;
                }
            }
            if(ships[k][y] != null){
                if(Math.abs(x - k) < ships[k][y].getLenghtShip()){
                    for(int i = 0; i < ships[k][y].getLenghtShip(); i++){
                        cells[k + i][y].setImageDrawable(null);
                    }
                    ships[k][y] = null;
                }
            }
        }
    }

    public void markCellMissed(){

    }

    public void markCellHit(){

    }

    public boolean thereIsAShipAt(int x, int y) {
        if(cells[x][y].getDrawable() != null){
            return true;
        }
        else{
            return false;
        }
    }

    public Ship getShipAt(int x, int y) {
        for(int k = 0; k < GRID_SIZE; k++){
            if(ships[x][k] != null){
                if(Math.abs(y - k) < ships[x][k].getLenghtShip()){
                    return ships[x][k];
                }
            }
            if(ships[k][y] != null){
                if(Math.abs(x - k) < ships[k][y].getLenghtShip()){
                    return ships[k][y];
                }
            }
        }
        return null;
    }
}
