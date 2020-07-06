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

    // Constructors

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

    // Public methods

    /**
     * Place a ship in the Grid
     * @param ship Ship that must be placed
     * @param startX starting x in the grid
     * @param startY starting y in the grid
     */
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

    /**
     * Show where the ship  will be positioned
     * @param ship ship from where teh method takes the size
     * @param startX starting x in the grid
     * @param startY starting y in the grid
     */
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

    /**
     * Remove all the selection active
     */
    public void removeSelection(){
        for(int i = 0; i < GRID_SIZE; i++){
            for(int j = 0; j < GRID_SIZE; j++) {
                setForegroundCell(frameCell, i, j);
            }
        }
    }

    /**
     * Remove a ship from specific point of the grid
     */
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

    /**
     * Add a mark that represent that this cell was a sea cell
     */
    public void markCellMissed(){

    }

    /**
     * Add a mark that represent that this cell was a ship cell
     */
    public void markCellHit(){

    }

    /**
     * Remove a mark from cell
     */
    public void removeMarkCell(){

    }

    /**
     * @param x x position
     * @param y y position
     * @return true if there is a Ship in this position
     */
    public boolean thereIsAShipAt(int x, int y) {
        if(cells[x][y].getDrawable() != null){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean rotateShipAt(int x, int y){
        if(thereIsAShipAt(x, y)){
            Ship ship = getShipAt(x, y);
            int xShip = getXShip(ship);
            int yShip = getYShip(ship);
            removeShipAt(x, y);
            ship.changeRotation();
            if(!placeShip(ship, xShip, yShip)){
                ship.changeRotation();
                placeShip(ship, xShip, yShip);
            }
            return true;
        }
        return false;
    }

    public void replaceAllShip(Ship[][] ships){
        for(int i = 0; i < GRID_SIZE; i++){
            for(int j = 0; j < GRID_SIZE; j++){
                Ship ship = ships[i][j];
                if(ship != null){
                    placeShip(ship, i, j);
                }
            }
        }
    }

    // Implementation

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getMeasuredWidth() < getMeasuredHeight()){
            side = getMeasuredHeight() / (GRID_SIZE + 1);
        }
        else{
            side = getMeasuredWidth() / (GRID_SIZE + 1);
        }
        setSizeCells(side);

    }

    /**
     * This method initialize the GridView adding images that represent
     * the cells and the Text views that represent the numbers and letters
     * in a battle ship grid
     */
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

    // Getters and Setters

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

    public int getSide() {
        return side;
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

    public Ship[][] getShips() {
        return ships;
    }
}
