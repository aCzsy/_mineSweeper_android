package com.example.minesweeper

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import kotlin.random.Random


//class representing game mode
enum class GameMode(val label:String){
    MARKING_MODE("Marking Mode"),
    UNCOVER_MODE("Uncover Mode");

    fun next() = when (this) {
        MARKING_MODE -> UNCOVER_MODE
        UNCOVER_MODE -> MARKING_MODE
    }
}

class MineSweeperView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    //width of a view
    private var size = width

    //10x10 grid
    private var numberOfRows = 10
    private var numberOfColumns = 10

    //counter that will be used to limit function calls
    private var count = 0

    //main 2d array board
    private var board = Array(numberOfColumns) {Array(numberOfRows) {Cell(0,0,0,0,0,0)} }

    //variable to limit generated mines on a field
    private var minesToBeGenerated = 0;

    //turns true when user clicks on a mine
    var gameOver:Boolean = false

    //turns true when user clicks on a mine
    private var revealAllMines = false

    //current game mode
    var gameMode:GameMode = GameMode.UNCOVER_MODE

    //opposite to current mode is displayed as text of a switch button
    //because it represents a state that will be set when button is clicked
    var gameModeBtnText = GameMode.MARKING_MODE

    //number of marked cells
    private var markedCells = 0

    override fun onDraw(canvas: Canvas) {
        //incrementing the counter
        count++
        super.onDraw(canvas)
        //only calling initializedGrid() once as we don't want to reinitialize the board every time onDraw is called
        //to prevent removal of different cell states that will be set during further milestones
        if(count == 1){
            //initializing the 10x10 grid with cells only once
            initializeGrid()
            //only placing mines once, at the start of the game
            placeMines(canvas)
            calculateNeighbours()
        }
        //Instead of reinitializing the grid, only dimensions of every cell are changed to adjust to the view size change
        rescale()

        //drawing all cells depending on their states
        drawGrid(canvas)

        //when user clicks on a first mine, all mines get revealed
        if(revealAllMines){
            showRevealedMines(canvas)
        }
    }

    //creating the main grid of 10x10 cells
    private fun initializeGrid(){
        var cellWidth = size / numberOfColumns; //width of each cell based on the custom view width
        //initializing each item as a Cell with its own position and dimensions
        for(i:Int in 0 until board.size) {
            for(j : Int in 0 until board[i].size) {
                var pos_x = i*cellWidth
                var pos_y = j*cellWidth
                board[i][j] = Cell(i,j,pos_x,pos_y,pos_x+cellWidth,pos_y+cellWidth)
            }
        }
    }
    //function that changes cells dimensions based on the width of a view
    //as the view changes based on the device screen dimensions and screen orientation
    private fun rescale(){
        var cellWidth = size / numberOfColumns; //width of each cell based on the custom view width
        //only changing dimensions of each cell using its index in the grid
        for(i:Int in 0 until board.size) {
            for(j : Int in 0 until board[i].size) {
                var pos_x = board[i][j].i*cellWidth
                var pos_y = board[i][j].j*cellWidth
                //Rect object(dimensionss of a cell) of each cell gets set with recalculated values
                board[i][j].rect = Rect(pos_x,pos_y,pos_x+cellWidth,pos_y+cellWidth)
            }
        }
    }

    //drawing the grid using the method show() which gets called by each Cell
    private fun drawGrid(canvas: Canvas){
        for (i in 0 until board.size) {
            for (j in 0 until board[i].size) {
                //Log.i("TAG", "the value at " + i.toString() + "-" + j.toString() + " is: " + board[i][j].Coordinates())
                board[i][j].show(canvas)
            }
        }
    }

    //function that places 20 mines randomly on the minefield
    private fun placeMines(canvas: Canvas){
        for (i in 0 until board.size) {
            for (j in 0 until board[i].size) {
                //random numbers
                var random_i = Random.nextInt(numberOfColumns)
                var random_j = Random.nextInt(numberOfRows)

                //if number of mines generated is less than 20
                if(minesToBeGenerated <= 19){
                    if(!(board[random_i][random_j]).hasMine()){
                        //keep looping and generating mines until mines count turns 20
                        minesToBeGenerated++;
                        //setting flag to true of cells that has mine
                        board[random_i][random_j].setHasMine(true)
                    }
                }
                setValueOfTotalMinesTextView()
            }
        }
    }

    //function that calculates neighbours of each cell in a grid
    private fun calculateNeighbours(){
        for (i in 0 until board.size) {
            for (j in 0 until board[i].size) {
                board[i][j].setNeighbours(calculateCellNeighbours(board,i,j,numberOfColumns,numberOfRows))
            }
        }
    }


    //function calculates neighbours of a cell
    private fun calculateCellNeighbours(arr: Array<Array<Cell>>,i:Int,j:Int,width:Int,height:Int):Int{
        if(arr[i][j].hasMine()){
            return -1
        }
        var total = 0;
        for (offset_x in -1..1) {
            for (offset_y in -1..1) {
                var k = i + offset_x
                var l = j + offset_y
                //if indexes are within the boundaries of a grid
                if(k > -1 && l > -1 && k < width && l < height){
                    var neighbour = arr[k][l]
                    if(neighbour.hasMine()){
                        total++
                    }
                }
            }
        }
        return total
    }

    //recursive method that will check neighbours of a cell passed into this method
    //if neighbour of a cell that's been passed doesn't have neighbours, call this method again on the neighbour, and so on
    private fun revealOthers(arr: Array<Array<Cell>>, i:Int, j:Int, width:Int, height:Int){
        for (offset_x in -1..1) {
            for (offset_y in -1..1) {
                //getting neighbour indexes
                var k = i + offset_x
                var l = j + offset_y
                //if indexes are within the boundaries of a grid
                if(k > -1 && l > -1 && k < width && l < height){
                    var neighbour = arr[k][l]
                    if(!neighbour.hasMine() && !neighbour.isRevealed()){
                        neighbour.setRevealed(true)
                        //if neighbour has no neighbours, call method again
                        if(neighbour.getNeighbours() == 0){
                            revealOthers(arr,k,l,width,height)
                        }
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        //x,y coordinates of a clicked point
        val x: Float = event.x
        val y: Float = event.y

        if (!gameOver){
            for (i in 0 until board.size) {
                for (j in 0 until board[i].size) {
                    //if a clicked point on the screen is withing the boundaries of a cell on the grid
                    //width/10 = width of a cell, (width/10)/2 = distance from center of a cell to its wall
                    var centerToWall = (width/10)/2
                    if(x > board[i][j].rectX()-centerToWall && x < board[i][j].rectX()+centerToWall && y > board[i][j].rectY()-centerToWall && y < board[i][j].rectY()+centerToWall){
                        //if user places finger on the screen
                        if(event.actionMasked == MotionEvent.ACTION_DOWN){
                            //Log.wtf("GAME MODE",gameMode.label)
                            //in Uncover mode, if cell isn't marked, it gets revealed
                            //unless it's not a mine, in this case game is over and remaining mines get revealed
                            if(gameMode == GameMode.UNCOVER_MODE && !board[i][j].getIsMarked()){
                                if(board[i][j].hasMine()){
                                    board[i][j].setFirstClickedMine(true)
                                    revealAllMines = true
                                    gameOver()
                                }
                                board[i][j].setRevealed(true)
                                //if this cell has no neighbours and its not a mine, run recursive method on its neighbours and reveal based on the results
                                if(board[i][j].getNeighbours() == 0 && !board[i][j].hasMine()){
                                    revealOthers(board,i,j,numberOfColumns,numberOfRows)
                                }
                            }
                            //In Marking mode
                            else if(gameMode == GameMode.MARKING_MODE){
                                //If cell isn't revealed, isn't marked and max available number of marked cell isn't reached
                                if(!board[i][j].isRevealed()){
                                    if(markedCells < 20 && !board[i][j].getIsMarked()){
                                        //also if its click count is < 2
                                        if(board[i][j].getClicksOnMarkedCell() < 2){
                                            //mark it
                                            board[i][j].setMarked(true)
                                        }
                                        //counter of marked mines increases by 1
                                        markedCells++;
                                        //updates a textview
                                        updateMarkedMinesTextView()
                                    }
                                    //if num of marked cells is less than or reached max, and current cell is marked
                                    if(markedCells <= 20 && board[i][j].getIsMarked()){
                                        //cells' click count increases by 1
                                        board[i][j].setClicksOnMarkedCell(board[i][j].getClicksOnMarkedCell()+1);
                                        //Log.w("CLICKS ON MARKED CELL",board[i][j].getClicksOnMarkedCell().toString())
                                        //if marked cell is clicked twice, its click count rolls back to 0 and it becomes uncovered
                                        //which represents that this cell is no longer marked and is not revealed yet
                                        //If the cell is marked(is yellow), mode switches to UNCOVER, and it's clicked again,nothing happens
                                        if(board[i][j].getClicksOnMarkedCell() == 2){
                                            board[i][j].setClicksOnMarkedCell(0)
                                            board[i][j].setMarked(false)
                                            //decreasing number of marked cells
                                            markedCells--
                                            //Log.wtf("MARKED CELLS",markedCells.toString())
                                            updateMarkedMinesTextView()
                                        }
                                    }
                                }
                            }
                            //Log.i("IS REVEALED","CELL " + i.toString() + "-" + j.toString() + " IS REVEALED : " + board[i][j].isRevealed())
                            //redrawing so that the changes appear on the screen
                            invalidate()
                        }
                    }
                }
            }
        }
        return true
    }

    //function stat sets a gameOver flag to true that will affect the game state, also toast gets displayed
    private fun gameOver(){
        gameOver = true
        showGameOverText()
    }

    //function that gets fired when Reset button is clicked
    fun gameStart(){
        //resetting all the values in order to start a new game
        count = 0
        gameOver = false
        minesToBeGenerated = 0
        revealAllMines = false
        board = Array(numberOfColumns) {Array(numberOfRows) {Cell(0,0,0,0,0,0)} }
        markedCells = 0
        gameMode = GameMode.UNCOVER_MODE
        gameModeBtnText = GameMode.MARKING_MODE
        hideGameOverText()
        updateMarkedMinesTextView()
        setGameModeLabelsText()
        setCurrentModeLabelsText()
        //redrawing the canvas
        invalidate()
    }

    //when user clicks on a first mine, this function will reveal all remaining mines on the field
    private fun showRevealedMines(canvas: Canvas){
        for (i in 0 until board.size) {
            for (j in 0 until board[i].size) {
                if(board[i][j].hasMine() && !board[i][j].getFirstClickedMine()){
                    board[i][j].showRemainingMine(canvas)
                }
            }
        }
    }

    //appending text and variable's value to a text view
    private fun setValueOfTotalMinesTextView(){
        val txtView = (context as Activity).findViewById<View>(R.id.total_mines) as TextView
        txtView.setText("Total mines on the field: " + minesToBeGenerated.toString())
    }

    //appending text and variable's value to a text view
    private fun updateMarkedMinesTextView(){
        var txtView = (context as Activity).findViewById<View>(R.id.marked_mines) as TextView
        txtView.setText("Marked mines: " + markedCells.toString())
    }

    //sets text of a mode switch button and current mode textview
    fun setGameModeLabelsText(){
        var btn = (context as Activity).findViewById<Button>(R.id.mode_switch_btn) as Button
        btn.setText(gameModeBtnText.label)
        setCurrentModeLabelsText()
    }

    //function will update text in a textview
    private fun setCurrentModeLabelsText(){
        var txtView = (context as Activity).findViewById<TextView>(R.id.currentGameMode) as TextView
        txtView.setText("Current mode: " + gameMode.label)
    }

    //when game is over, this function will set textview's visitbility to visible
    private fun showGameOverText(){
        var gameOverTextView = (context as Activity).findViewById<TextView>(R.id.game_over_text) as TextView
        if(gameOver){
            gameOverTextView.visibility = View.VISIBLE
        }
    }

    //sets textview's visibility to invisible if game is not over
    private fun hideGameOverText(){
        var gameOverTextView = (context as Activity).findViewById<TextView>(R.id.game_over_text) as TextView
        gameOverTextView.visibility = View.INVISIBLE
    }

    //function that makes the view responsive and fit its parent
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = Math.min(measuredWidth,measuredHeight)
        setMeasuredDimension(size,size)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //updating textviews,button texts
        setValueOfTotalMinesTextView()
        updateMarkedMinesTextView()
        setGameModeLabelsText()
        showGameOverText()
    }


    //saving states of the data structures and relevant variables which is needed for a case when screen orientation changes
    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putInt("count",count)
        bundle.putSerializable("board",board)
        bundle.putInt("minesToBeGenerated",minesToBeGenerated)
        bundle.putBoolean("gameOver",gameOver)
        bundle.putBoolean("revealAllMines",revealAllMines)
        bundle.putSerializable("gameMode",gameMode)
        bundle.putInt("markedCells",markedCells)
        bundle.putSerializable("gameModeBtnText",gameModeBtnText)
        bundle.putParcelable("superState", super.onSaveInstanceState())
        return bundle
    }

    //restored the saved state
    override fun onRestoreInstanceState(state: Parcelable?) {
        var viewState = state
        if (viewState is Bundle) {
            count = viewState.getInt("count")
            board = viewState.getSerializable("board") as Array<Array<Cell>>
            minesToBeGenerated = viewState.getInt("minesToBeGenerated")
            gameOver = viewState.getBoolean("gameOver")
            revealAllMines = viewState.getBoolean("revealAllMines")
            gameMode = viewState.getSerializable("gameMode") as GameMode
            markedCells = viewState.getInt("markedCells")
            gameModeBtnText = viewState.getSerializable("gameModeBtnText") as GameMode
            viewState = viewState.getParcelable("superState")
        }
        super.onRestoreInstanceState(viewState)
    }
}