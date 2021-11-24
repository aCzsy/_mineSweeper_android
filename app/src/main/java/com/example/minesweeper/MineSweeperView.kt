package com.example.minesweeper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class MineSweeperView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    //width of a view
    var size = width

    //10x10 grid
    private var numberOfRows = 10
    private var numberOfColumns = 10

    //counter that will be used to limit function calls
    var count = 0

    //main 2d array board
    var board = Array(numberOfColumns) {Array(numberOfRows) {Cell(0,0,0,0,0,0)} }

    override fun onDraw(canvas: Canvas) {
        //incrementing the counter
        count++
        super.onDraw(canvas)
        //only calling initializedGrid() once as we don't want to reinitialize the board every time onDraw is called
        //to prevent removal of different cell states that will be set during further milestones
        if(count == 1){
            //initializing the 10x10 grid with cells only once
            initializeGrid()
        }
        //Instead of reinitializing the grid, only dimensions of every cell are changed to adjust to the view size change
        rescale()

        //drawing all cells depending on their states
        drawGrid(canvas)
    }

    //creating the main grid of 10x10 cells
    fun initializeGrid(){
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
    fun rescale(){
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        //x,y coordinates of a clicked point
        val x: Float = event.x
        val y: Float = event.y

        for (i in 0 until board.size) {
            for (j in 0 until board[i].size) {
                //if a clicked point on the screen is withing the boundaries of a cell on the grid
                //width/10 = width of a cell, (width/10)/2 = distance from center of a cell to its wall
                var centerToWall = (width/10)/2
                if(x > board[i][j].rectX()-centerToWall && x < board[i][j].rectX()+centerToWall && y > board[i][j].rectY()-centerToWall && y < board[i][j].rectY()+centerToWall){
                    //if user places finger on the screen
                    if(event.actionMasked == MotionEvent.ACTION_DOWN){
                        //setting flag to true when cell is clicked so that it can be revealed(drawn)
                        board[i][j].setRevealed(true)
                        //Log.i("IS REVEALED","CELL " + i.toString() + "-" + j.toString() + " IS REVEALED : " + board[i][j].isRevealed())
                        //redrawing so that the changes appear on the screen
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    //function that makes the view responsive and fit its parent
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = Math.min(measuredWidth,measuredHeight)
        setMeasuredDimension(size,size)
    }

    //saving states of the data structures and relevant variables which is needed for a case when screen orientation changes
    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putInt("count",count)
        bundle.putSerializable("board",board)
        bundle.putParcelable("superState", super.onSaveInstanceState())
        return bundle
    }

    //restored the saved state
    override fun onRestoreInstanceState(state: Parcelable?) {
        var viewState = state
        if (viewState is Bundle) {
            count = viewState.getInt("count")
            board = viewState.getSerializable("board") as Array<Array<Cell>>
            viewState = viewState.getParcelable("superState")
        }
        super.onRestoreInstanceState(viewState)
    }
}