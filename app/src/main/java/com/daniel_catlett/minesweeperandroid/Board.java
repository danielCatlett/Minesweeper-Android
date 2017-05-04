package com.daniel_catlett.minesweeperandroid;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by daniel on 10/30/2016.
 */
public class Board
{

    //The board is an ArrayList of ArrayLists.
    //Each list within the list is a row.
    ArrayList<ArrayList<Tile>> board = new ArrayList<ArrayList<Tile>>();

    public Board(int rows, int cols, int mines)
    {

        //Add each row
        for(int y = 0; y < rows; y++)
        {
            addRow();

            //Add each tile in each row
            for(int x = 0; x < cols; x++)
            {
                addTile(y);
            }
        }


        //System.out.println(board.size() + " " + board.get(0).size());

        //If there are supposed to be more than 8 rows or cols in the board
        if(rows > 8)
        {
            for(int i = (rows-8); i <= rows; i++)
                addRow();
        }

        if(cols > 8)
        {
            for(int i = (cols-8); i <= cols; i++)
                addCol();
        }

        initializeTiles();
    }



    /*
        ArrayLists default to a size of 10.
        I need them to default to a size of 8.
        Not sure how to control the size of the
        ArrayLists within the ArrayList, so doing it manually.

    private void reduceColumns()
    {
        //removes the last to tiles in each nestled list
        for(int i = 0; i < 8; i++)
        {
            System.out.println("hgfiojrio");

            System.out.println(board.get(i).size());
            board.get(i).remove(8);
            board.get(i).remove(9);
        }
    }
    */

    public void addRow()
    {
        //Create new list of size 8, and add it to the board
        ArrayList<Tile> row = new ArrayList<>(8);
        board.add(row);
    }

    public void addCol()
    {
        for(int i = 0; i <= board.size(); i++)
        {
            addTile(i);
        }
    }

    public void addTile(int i)
    {
        Tile x = new Tile(0, false); //new untouched tile that isn't a mine
        board.get(i).add(x); //add the tile to the row it was told to be added to
    }


    public Tile createTile()
    {
        Tile x = new Tile(0, false);
        return x;
    }


    private void initializeTiles()
    {
        int rows = board.size();
        int cols = board.get(0).size() - 1;
        for(int y = 0; y < rows; y++)
        {
            for(int x = 0; x < cols; x++)
            {
                board.get(y).set(x, createTile());
            }
        }
    }

    public void selectMineLocations(int maxY, int maxX, int startY, int startX, int numMines)
    {
        Random r = new Random();
        maxX--;
        maxY--;

        //Arrays storing the coordinates of the starting tile and each mine
        //Checks this to make sure a mine isn't placed where it isn't supposed to be

        ArrayList<Integer> unusableY = new ArrayList<Integer>();
        ArrayList<Integer> unusableX = new ArrayList<Integer>();

        //Initial tile is added
        unusableY.add(startY);
        unusableX.add(startX);

        //All tiles around initial tile are added
        if(startY > 0 && startX > 0)
        {
            unusableX.add(startX - 1);
            unusableY.add(startY - 1);
        }
        if(startY > 0)
        {
            unusableX.add(startX);
            unusableY.add(startY - 1);
        }
        if(startX < maxX && startY > 0)
        {
            unusableX.add(startX + 1);
            unusableY.add(startY - 1);
        }
        if(startX > 0)
        {
            unusableX.add(startX - 1);
            unusableY.add(startY);
        }
        if(startX < maxX)
        {
            unusableX.add(startX + 1);
            unusableY.add(startY);
        }
        if(startX > 0 && startY < maxY)
        {
            unusableX.add(startX - 1);
            unusableY.add(startY + 1);
        }
        if(startY < maxY)
        {
            unusableX.add(startX);
            unusableY.add(startY + 1);
        }
        if(startX < maxX && startY < maxY)
        {
            unusableX.add(startX + 1);
            unusableY.add(startY + 1);
        }

        for(int i = 0; i < numMines; i++)
        {
            int x = r.nextInt(maxX);
            int y = r.nextInt(maxY);

            while(notUsable(unusableY, unusableX, x, y) || board.get(y).get(x).getMine())
            {
                x = r.nextInt(maxX);
                y = r.nextInt(maxY);
            }

            board.get(y).get(x).makeMine();

            //System.out.println(x + " " + y);
            //System.out.println(i);

        }
    }

    //Checks to see if tile is one of the tiles that is not allowed to be declared a mine
    //Changed this while porting code to Swift. Commented out stuff is the old code. Too lazy to
    //Test and verify it works, but there is no reason it shouldn't
    //Left the old stuff here anyways, just in case
    private boolean notUsable(ArrayList<Integer> unusableY, ArrayList<Integer> unusableX, int x, int y)
    {

        //boolean xValid = true;
        //boolean yValid = true;

        for(int i = 0; i < unusableX.size(); i++)
        {
            if(unusableX.get(i) == x && unusableY.get(i) == y)
                return true;
        }

        /*
        for(int i = 0; i < unusableY.size(); i++)
        {
            if(unusableY.get(i) == y)
                yValid = false;
        }
        if(xValid || yValid)
            return false;
        else
            return true;
        */
        return false;
    }

    public boolean playerWon()
    {
        boolean theAnswer = true;

        //Loop through all the tiles
        for(int y = 0; y < 8; y++)
        {
            for(int x = 0; x < 8; x++)
            {
                //If there is a non mine that isn't cleared
                if(!board.get(y).get(x).getMine() && getState(x, y) != 3)
                    theAnswer = false;
            }
        }

        return theAnswer;
    }

    public void setBoardNums()
    {
        /*
            Nums for each tile with 8 tiles surrounding it
            loops through each tile not on an edge, outsources the counting of the
            surrounding mines, and then sets the tile number to the outsourced number
        */
        for(int y = 1; y < (board.size() - 1); y++) //board.size() - 1 takes us to the corner, so we stop the square before that using the <
        {
            for(int x = 1; x < (board.get(0).size() - 1); x++)
            {
                int a = calculateNumsInner(y, x);
                board.get(y).get(x).setNum(a);
            }
        }

        //Nums for upper row(not corners)
        for(int x = 1; x < (board.get(0).size() - 1); x++)
        {
            int a = calculateNumsTop(x);
            board.get(0).get(x).setNum(a);
        }

        //Nums for lower row(not corners)
        for(int x = 1; x < (board.get(0).size() - 1); x++)
        {
            int a = calculateNumsBottom(x);
            board.get(board.size() - 1).get(x).setNum(a);
        }

        //Nums for left side(not corners)
        for(int y = 1; y < (board.size() - 1); y++)
        {
            int a = calculateNumsLeft(y);
            board.get(y).get(0).setNum(a);
        }

        //Nums for right side(not corners)
        for(int y = 1; y < (board.size() - 1); y++)
        {
            int a = calculateNumsRight(y);
            board.get(y).get(board.get(0).size() - 1).setNum(a);
        }

        //Nums for corners
        setNumsForCorners();
    }

    private void setNumsForCorners()
    {
        int upperLeft = 0;
        int upperRight = 0;
        int lowerLeft = 0;
        int lowerRight = 0;

        //checks each tile bordering the upper left corner tile,
        //adds one if it is a mine
        if(board.get(0).get(1).getMine())
            upperLeft++;
        if(board.get(1).get(0).getMine())
            upperLeft++;
        if(board.get(1).get(1).getMine())
            upperLeft++;
        board.get(0).get(0).setNum(upperLeft);

        //upper right
        if(board.get(0).get(board.get(0).size() - 2).getMine())
            upperRight++;
        if(board.get(1).get(board.get(0).size() - 2).getMine())
            upperRight++;
        if(board.get(1).get(board.get(0).size() - 1).getMine())
            upperRight++;
        board.get(0).get(board.get(0).size() - 1).setNum(upperRight);

        //lower left
        if(board.get(board.size() - 2).get(0).getMine())
            lowerLeft++;
        if(board.get(board.size() - 2).get(1).getMine())
            lowerLeft++;
        if(board.get(board.size() - 1).get(1).getMine())
            lowerLeft++;
        board.get(board.size() - 1).get(0).setNum(lowerLeft);

        //lower right
        if(board.get(board.size() - 2).get(board.get(0).size() - 2).getMine())
            lowerRight++;
        if(board.get(board.size() - 2).get(board.get(0).size() - 1).getMine())
            lowerRight++;
        if(board.get(board.size() - 1).get(board.get(0).size() - 2).getMine())
            lowerRight++;
        board.get(board.size() - 1).get(board.get(0).size() - 1).setNum(lowerRight);
    }

    private int calculateNumsRight(int y)
    {
        int numMines = 0;

        if(board.get(y - 1).get(board.get(0).size() - 2).getMine())
            numMines++;
        if(board.get(y - 1).get(board.get(0).size() - 1).getMine())
            numMines++;
        if(board.get(y).get(board.get(0).size() - 2).getMine())
            numMines++;
        if(board.get(y + 1).get(board.get(0).size() - 2).getMine())
            numMines++;
        if(board.get(y + 1).get(board.get(0).size() - 1).getMine())
            numMines++;

        return numMines;
    }

    private int calculateNumsLeft(int y)
    {
        int numMines = 0;

        if(board.get(y - 1).get(0).getMine())
            numMines++;
        if(board.get(y - 1).get(1).getMine())
            numMines++;
        if(board.get(y).get(1).getMine())
            numMines++;
        if(board.get(y + 1).get(0).getMine())
            numMines++;
        if(board.get(y + 1).get(1).getMine())
            numMines++;

        return numMines;
    }

    private int calculateNumsBottom(int x)
    {
        int numMines = 0;
        int y = board.size() - 1;

        if(board.get(y - 1).get(x - 1).getMine())
            numMines++;
        if(board.get(y - 1).get(x).getMine())
            numMines++;
        if(board.get(y - 1).get(x + 1).getMine())
            numMines++;
        if(board.get(y).get(x - 1).getMine())
            numMines++;
        if(board.get(y).get(x + 1).getMine())
            numMines++;

        return numMines;
    }

    private int calculateNumsTop(int x)
    {
        int numMines = 0;

        if(board.get(0).get(x - 1).getMine())
            numMines++;
        if(board.get(0).get(x + 1).getMine())
            numMines++;
        if(board.get(1).get(x - 1).getMine())
            numMines++;
        if(board.get(1).get(x).getMine())
            numMines++;
        if(board.get(1).get(x + 1).getMine())
            numMines++;

        return numMines;
    }

    private int calculateNumsInner(int y, int x)
    {
        int numMines = 0;

        if(board.get(y - 1).get(x - 1).getMine())
            numMines++;
        if(board.get(y - 1).get(x).getMine())
            numMines++;
        if(board.get(y - 1).get(x + 1).getMine())
            numMines++;
        if(board.get(y).get(x - 1).getMine())
            numMines++;
        if(board.get(y).get(x + 1).getMine())
            numMines++;
        if(board.get(y + 1).get(x - 1).getMine())
            numMines++;
        if(board.get(y + 1).get(x).getMine())
            numMines++;
        if(board.get(y + 1).get(x + 1).getMine())
            numMines++;


        return numMines;
    }

    public boolean checkIfMine(int x, int y)
    {
        if(board.get(y).get(x).getMine())
            return true;
        else
            return false;
    }

    public void setState(int x, int y, int state)
    {
        board.get(y).get(x).setState(state);
    }

    public int getState(int x, int y)
    {
        return board.get(y).get(x).getState();
    }

    public int getNum(int x, int y)
    {
        return board.get(y).get(x).getNum();
    }

    //public int getSize(int i) { return board.get(i).size(); }
}
