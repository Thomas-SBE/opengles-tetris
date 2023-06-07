package net.tsbe.tetrominoes.models;

import net.tsbe.tetrominoes.exceptions.GridHeightOverflowException;
import net.tsbe.tetrominoes.gles.GLESRenderer;
import net.tsbe.tetrominoes.gles.GameGridGraphics;

public class GameGrid {

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    int width;
    int height;
    char[] grid;

    int amountOfFilledCells = 0;

    GameGridGraphics graphics;
    GLESRenderer mRenderer;

    public void setRenderer(GLESRenderer rendere){ mRenderer = rendere; }

    public GameGrid(int width, int height) {
        this.width = width;
        this.height = height;

        grid = new char[width*height];
        for(int i = 0; i < width*height; i++) grid[i] = ' ';

        graphics = new GameGridGraphics(this);
    }

    public GameGridGraphics getGraphics(){ return graphics; }

    public char[] getGrid(){ return grid; }
    public int getAmountOfFilledCells(){ return amountOfFilledCells; }

    public void placeTetrominoe(Tetrominoe tetrominoe, Position position) {;
        int dropHeight = 0;
        mRenderer.surface.placedATetrominoe();
        while(canTetrominoeFall(tetrominoe, position)){
            dropHeight++;
            position.y = dropHeight;
        }
        drawTetrominoeInGrid(tetrominoe, position);
    }

    public boolean canTetrominoeMoveRight(Tetrominoe tetrominoe, int columnIndex){
        return columnIndex + tetrominoe.getWidth() < tetrominoe.getWidth();
    }

    public void drawTetrominoeInGrid(Tetrominoe tetrominoe, Position position){
        for(int i = 0; i < tetrominoe.getHeight() * tetrominoe.getWidth(); i++){
            int line = i/tetrominoe.getWidth();
            int offset = i% tetrominoe.getWidth();
            if(tetrominoe.getShape()[i] != ' '){
                grid[(position.y+line)*width+position.x+offset] = tetrominoe.getShape()[i];
                amountOfFilledCells++;
            }
        }
    }

    public void removeTetrisLines(){
        for(int y = width*(height-1); y >= width; y = y - width){
            if(doLineFormTetris(y)){
                mRenderer.surface.incrementScore(true);
                for(int i = y+width-1; i >= width; i--) grid[i] = grid[i-width];
                for(int i = 0; i < width; i++) grid[i] = ' ';
            }
        }
    }

    public boolean doLineFormTetris(int y){
        for(int i = 0; i < width; i++)
            if(grid[i+y] == ' ' || grid[i+y] == '?') return false;
        System.out.println("TETRIS: " + (y/width));
        return true;
    }

    public boolean canTetrominoeFall(Tetrominoe tetrominoe, Position position){
        for(int x = 0; x < tetrominoe.getWidth(); x++){
            for(int y = 0; y < tetrominoe.getHeight(); y++){
                if(tetrominoe.getShape()[y*tetrominoe.getWidth()+x] == ' ') continue;
                if(y+1 < tetrominoe.getHeight() && tetrominoe.getShape()[(y+1)*tetrominoe.getWidth()+x] != ' ') continue;
                int cellUnder = (position.y+y+1)*width+position.x+x;
                if(cellUnder >= width*height) return false;
                if(grid[cellUnder] != ' ') return false;
            }
        }
        return true;
    }

    public boolean doTetromonoeOverlap(Tetrominoe tetrominoe, Position position){
        for(int x = 0; x < tetrominoe.getWidth(); x++){
            for(int y = 0; y < tetrominoe.getHeight(); y++) {
                if(tetrominoe.getShape()[y*tetrominoe.getWidth()+x] == ' ') continue;
                if(grid[position.x + ((position.y+y)*width)+ x] != ' ') return true;
            }
        }
        return false;
    }

    public void debugShowGrid(){
        StringBuilder r = new StringBuilder();
        for(int i = 0; i < height*width; i++){
            r.append(grid[i]);
            if((i+1)%width==0) r.append('\n');
        }
        System.out.println(r.toString());
    }

}
