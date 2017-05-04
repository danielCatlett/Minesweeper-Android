package com.daniel_catlett.minesweeperandroid;

/**
 * Created by daniel on 10/29/2016.
 * Added to this project on 5/3/2017.
 */
public class Tile
{
    //0 is untouched, 1 is flagged, 2 is question marked, 3 is exposed
    int state;

    //is or isn't a mine
    boolean mine;

    //number of mines touching tile
    int num;

    public Tile(int status, boolean isMine)
    {
        state = status;
        mine = isMine;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int newState)
    {
        state = newState;
    }

    public boolean getMine()
    {
        return mine;
    }

    public int getNum()
    {
        return num;
    }

    public void setNum(int x) { num = x; }

    public void makeMine()
    {
        mine = true;
    }
}
