package com.daniel_catlett.minesweeperandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    //visual container for the image buttons
    GridLayout boardContainer;
    //programming container for the image buttons
    ArrayList<ArrayList<ImageButton>> buttonBoard;
    //container for handling game data
    Board theBoard;
    boolean boardSetUp;

    //buttons for selecting selection type
    ImageButton mineButton;
    ImageButton flagButton;
    ImageButton questionButton;
    //keeps track if player is selecting flags, question marks, or clearing
    //clearing is 0, placing flags is 1, placing question marks is 2
    int activeSelectionType;

    TextView mineCounter;
    int minesLeft;

    boolean gameIsOver;

    Button newGameButton;

    HeyListen listener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        loadActivity();
    }

    private void loadActivity()
    {
        setContentView(R.layout.activity_main);

        theBoard = new Board(8, 8, 10);
        buttonBoard = new ArrayList<ArrayList<ImageButton>>();

        boardSetUp = false;
        boardContainer = (GridLayout)findViewById(R.id.gridForBoard);
        activeSelectionType = 0;

        mineButton = (ImageButton)findViewById(R.id.mineButton);
        mineButton.setOnClickListener(bMine);
        flagButton = (ImageButton)findViewById(R.id.flagButton);
        flagButton.setOnClickListener(bFlag);
        questionButton = (ImageButton)findViewById(R.id.questionButton);
        questionButton.setOnClickListener(bQuestion);

        //set up mineCounter
        mineCounter = (TextView)findViewById(R.id.mineCounter);
        minesLeft = 10;
        String displayThis = getString(R.string.minesLeft) + " " + Integer.toString(minesLeft);
        mineCounter.setText(displayThis);

        gameIsOver = false;

        newGameButton = (Button)findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(bNewGame);

        //Create buttons, and add to the GridLayout and the Arraylist
        for(int y = 0; y < 8; y++)
        {
            ArrayList<ImageButton> row = new ArrayList<ImageButton>();
            buttonBoard.add(row);
            for(int x = 0; x < 8; x++)
            {
                ImageButton tile = new ImageButton(this);
                tile.setImageResource(R.drawable.untouched43);
                tile.setBackground(getResources().getDrawable(R.drawable.tilebackground));
                listener = new HeyListen(x, y);
                tile.setOnClickListener(listener);
                row.add(tile);
                boardContainer.addView(tile);
            }
        }
    }

    ImageButton.OnClickListener bMine = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(activeSelectionType != 0)
            {
                activeSelectionType = 0;
                mineButton.setImageResource(R.drawable.minebuttonselected);
                flagButton.setImageResource(R.drawable.flagbutton);
                questionButton.setImageResource(R.drawable.questionbutton);
            }
        }
    };

    ImageButton.OnClickListener bFlag = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(activeSelectionType != 1 && boardSetUp)
            {
                activeSelectionType = 1;
                mineButton.setImageResource(R.drawable.minebutton);
                flagButton.setImageResource(R.drawable.flagbuttonselected);
                questionButton.setImageResource(R.drawable.questionbutton);
            }
        }
    };

    ImageButton.OnClickListener bQuestion = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(activeSelectionType != 2 && boardSetUp)
            {
                activeSelectionType = 2;
                mineButton.setImageResource(R.drawable.minebutton);
                flagButton.setImageResource(R.drawable.flagbutton);
                questionButton.setImageResource(R.drawable.questionbuttonselected);
            }
        }
    };

    Button.OnClickListener bNewGame = new Button.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            loadActivity();
        }
    };

    public class HeyListen implements View.OnClickListener
    {

        int x;
        int y;
        public HeyListen(int xVar, int yVar)
        {
            x = xVar;
            y = yVar;
        }

        @Override
        public void onClick(View v)
        {
            if(!boardSetUp)
            {
                //Setting up the board
                theBoard.selectMineLocations(8, 8, y, x, 10);
                theBoard.setBoardNums();
                theBoard.setState(x, y, 3);
                updateButton(x, y);
                clearSurroundingTiles(x, y);
                boardSetUp = true;
            }
            else if(!gameIsOver)
            {
                //respond to player interaction
                updateGameState(x, y, theBoard.getState(x, y));
            }
        }
    };

    public void updateGameState(int x, int y, int currentState)
    {
        switch(currentState)
        {
            case 0: //if tile is untouched
                tileIsUntouched(x, y);
                break;
            case 1: //if tile is flagged
                if(activeSelectionType == 1) //if player is flagging tiles
                {
                    //restore tile to untouched
                    theBoard.setState(x, y, 0);
                    updateButton(x, y);

                    //add to minesLeft
                    minesLeft++;
                    String displayThis = getString(R.string.minesLeft) + " " + Integer.toString(minesLeft);
                    mineCounter.setText(displayThis);
                }
                break;
            case 2: //if tile is question marked
                if(activeSelectionType == 2) //if player is question marking tiles
                {
                    theBoard.setState(x, y, 0); //restore tile to untouched
                    updateButton(x, y);
                }
                break;
            default:
        }
    }

    private void tileIsUntouched(int x, int y)
    {
        switch(activeSelectionType)
        {
            case 0: //if player is clearing tiles
                clearingUntouchedTile(x, y);
                break;
            case 1: //if the player is flagging tiles
                theBoard.setState(x, y, 1); //flag tile
                updateButton(x, y);
                //decrease minesLeft
                minesLeft--;
                String displayThis = getString(R.string.minesLeft) + " " + Integer.toString(minesLeft);
                mineCounter.setText(displayThis);
                break;
            default: //if the player is question marking tiles
                theBoard.setState(x, y, 2); //question mark the tile
                updateButton(x, y);
        }
    }

    private void clearingUntouchedTile(int x, int y)
    {
        if(!theBoard.checkIfMine(x, y)) //if tile is not a mine
        {
            theBoard.setState(x, y, 3); //clear it

            if(!theBoard.playerWon()) //if the player didn't win
            {
                updateButton(x, y);

                //if the tile is a 0, clear all tiles around it
                if(theBoard.getNum(x, y) == 0)
                    clearSurroundingTiles(x, y);
            }
            else
            {
                updateButton(x, y);
                mineCounter.setText(getString(R.string.youWin));
                gameIsOver = true;
            }
        }
        else
            gameOver();
    }

    private void gameOver()
    {
        for(int y = 0; y < 8; y++)
        {
            for(int x = 0; x < 8; x++)
            {
                if(theBoard.checkIfMine(x, y))
                    buttonBoard.get(y).get(x).setImageResource(R.drawable.mine43);
                else
                    setCorrectNumForButton(x, y, theBoard.getNum(x, y));
            }
        }
        mineCounter.setText(getString(R.string.gameOver));
        gameIsOver = true;
    }

    /*
     * When a tile on theBoard gets a new state, this method is called.
     * It updates the corresponding button to the correct image
     */
    public void updateButton(int x, int y)
    {
        int state = theBoard.getState(x, y); //the state the tile was set to

        switch(state)
        {
            case 0:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.untouched43);
                break;
            case 1:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.flagged43);
                break;
            case 2:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.question43);
                break;
            default:
                setCorrectNumForButton(x, y, theBoard.getNum(x, y));
        }
    }

    //if a button needs a num, its done here
    private void setCorrectNumForButton(int x, int y, int num)
    {
        switch(num)
        {
            case 0:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.clear43);
                break;
            case 1:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.one43);
                break;
            case 2:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.two43);
                break;
            case 3:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.three43);
                break;
            case 4:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.four43);
                break;
            case 5:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.five43);
                break;
            case 6:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.six43);
                break;
            default:
                buttonBoard.get(y).get(x).setImageResource(R.drawable.seven43);
        }
    }

    private void clearSurroundingTiles(int x, int y)
    {
        if(y > 0 && x > 0)
        {
            handleTileClearing(x - 1, y - 1);
        }
        if(y > 0)
        {
            handleTileClearing(x, y - 1);
        }
        if(x < 7 && y > 0)
        {
            handleTileClearing(x + 1, y - 1);
        }
        if(x > 0)
        {
            handleTileClearing(x - 1, y);
        }
        if(x < 7)
        {
            handleTileClearing(x + 1, y);
        }
        if(x > 0 && y < 7)
        {
            handleTileClearing(x - 1, y + 1);
        }
        if(y < 7)
        {
            handleTileClearing(x, y + 1);
        }
        if(x < 7 && y < 7)
        {
            handleTileClearing(x + 1, y + 1);
        }
    }

    private void handleTileClearing(int x, int y)
    {
        //if not a mine, and tile is untouched
        if(theBoard.getState(x, y) == 0 && !theBoard.checkIfMine(x, y))
        {
            theBoard.setState(x, y, 3);
            updateButton(x, y);
            //if the newly cleared tile is a zero, recursively call
            if(theBoard.getNum(x, y) == 0)
                clearSurroundingTiles(x, y);
        }
    }
}
