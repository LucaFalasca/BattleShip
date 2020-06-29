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
    public final int GRID_SIZE = 10;
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

    public boolean placeShip(Ship ship, int startX, int startY){
        removeSelection();

        int length = ship.getLength();

        int[][] coords = calculateCoordsOfShip(ship, startX, startY);

        int[] xCoords = coords[0];
        int[] yCoords = coords[1];

        Drawable[] sprites = ship.getSprites();

        for(int i = 0; i < length; i++){
            int x = xCoords[i];
            int y = yCoords[i];

            if(thereIsAShipAt(x, y))
                return false;
        }

        for(int i = 0; i < ship.getLength(); i++){
            int x = xCoords[i];
            int y = yCoords[i];

            setImageCell(sprites[i], x, y);
        }

        ships[xCoords[0]][yCoords[0]] = ship;

        return true;
    }

    public void showSelection(Ship ship, int startX, int startY){
        int length = ship.getLength();

        int[][] coords = calculateCoordsOfShip(ship, startX, startY);

        int[] xCoords = coords[0];
        int[] yCoords = coords[1];

        for(int i = 0; i < length; i++){
            int x = xCoords[i];
            int y = yCoords[i];
            if(thereIsAShipAt(x, y)){
                setForegroundCell(selectionCellWrong, x, y);
            }
            else{
                setForegroundCell(selectionCell, x, y);
            }
        }
    }

    private int adaptCoord(int k, int length) {
        int max = k + length;

        if(max > GRID_SIZE)
            return k - (max - GRID_SIZE);
        else{
            return k;
        }
    }

    private void setImageCell(Drawable fgImage, int x, int y){
        cells[x][y].setImageDrawable(fgImage);
    }

    private void setForegroundCell(Drawable fgImage, int x, int y){
        cells[x][y].setForeground(fgImage);
    }

    private int[][] calculateCoordsOfShip(Ship ship, int startX, int startY){
        int angleRotation = ship.getAngleRotation();
        int length = ship.getLength();

        int cos = (int) Math.cos(Math.toRadians(angleRotation));
        int sin = (int) Math.sin(Math.toRadians(angleRotation));

        if(cos == 1)
             startX = adaptCoord(startX, length);
        else if(sin == 1)
             startY = adaptCoord(startY, length);

        int[] x = new int[length];
        int[] y = new int[length];

        for(int i = 0; i < length; i++){
            x[i] = startX + i * cos;
            y[i] = startY + i * sin;
        }

        return new int[][]{x, y};
    }

    public void removeSelection(){
        for(int i = 0; i < GRID_SIZE; i++){
            for(int j = 0; j < GRID_SIZE; j++) {
                setForegroundCell(frameCell, i, j);
            }
        }
    }

    public void removeShipAt(int xPoint, int yPoint){
        Ship ship = getShipAt(xPoint, yPoint);

        int startingX = getXShip(ship);
        int startingY = getYShip(ship);

        int[][] coords = calculateCoordsOfShip(ship, startingX, startingY);

        int[] xCoords = coords[0];
        int[] yCoords = coords[1];

        for(int i = 0; i < ship.getLength(); i++){
            int x = xCoords[i];
            int y = yCoords[i];

            setImageCell(null, x, y);
        }

        ships[startingX][startingY] = null;
    }

    public void markCellMissed(){

    }

    public void markCellHit(){

    }

    public void removeMarkCell(){

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
                if(Math.abs(y - k) < ships[x][k].getLength()){
                    return ships[x][k];
                }
            }
            if(ships[k][y] != null){
                if(Math.abs(x - k) < ships[k][y].getLength()){
                    return ships[k][y];
                }
            }
        }
        return null;
    }

    private int getXShip(Ship ship){
        for(int i = 0; i < GRID_SIZE; i++){
            for(int j = 0; j < GRID_SIZE; j++){
                if(ships[i][j] == ship){
                    return i;
                }
            }
        }
        return -1;
    }

    private int getYShip(Ship ship){
        for(int i = 0; i < GRID_SIZE; i++){
            for(int j = 0; j < GRID_SIZE; j++){
                if(ships[i][j] == ship){
                    return j;
                }
            }
        }
        return -1;
    }

    public boolean rotateShipAt(int x, int y){
        if(thereIsAShipAt(x, y)){
            Ship ship = getShipAt(x, y);
            int xShip = getXShip(ship);
            int yShip = getYShip(ship);
            removeShipAt(x, y);
            ship.changeRotation();
            placeShip(ship, xShip, yShip);
            return true;
        }
        return false;
    }
}
