package com.example.minesweeper

import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.core.graphics.toColor
import java.io.Serializable

class Cell(var i:Int, var j:Int,var l:Int, var r: Int, var t:Int, var b:Int): Parcelable, Serializable{
    //initializing variables
    private var _i:Int = i
    private var _j:Int = j
    private var _l:Int = l
    private var _r:Int = r
    private var _t:Int = t
    private var _b:Int = b

    //colours
    private var cellCoveredColor = Color.BLACK
    private var cellOutlineColor = Color.WHITE
    private var cellUncoveredColor = Color.GRAY
    private var _strokeWidth = 4.0f

    private var number_one_colour = Color.BLUE
    private var number_two_colour = Color.rgb(34,139,34)
    private var number_three_colour = Color.DKGRAY
    private var number_four_colour = Color.rgb(75,0,130)
    private var number_five_colour = Color.rgb(149,69,53)
    private var number_six_colour = Color.rgb(95,158,160)
    private var number_seven_colour = Color.BLACK
    private var number_eight_colour = Color.rgb(172,172,172)

    //paint objects for each state of a cell
    @Transient
    private val covered_cell_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = cellCoveredColor
        style = Paint.Style.FILL
    }
    @Transient
    private val stroke_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = cellOutlineColor
        style = Paint.Style.STROKE
        strokeWidth = _strokeWidth
    }
    @Transient
    private val uncovered_cell_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = cellUncoveredColor
        style = Paint.Style.FILL
    }
    @Transient
    private val mined_cells_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
        textSize = 45.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }
    @Transient
    private val remaining_mined_cells_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.MAGENTA
        style = Paint.Style.FILL
        textSize = 45.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }
    @Transient
    private val mine_text_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 45.0f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create( "", Typeface.BOLD)
        color = Color.BLACK
    }
    @Transient
    private val marked_mine_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 45.0f
        typeface = Typeface.create( "", Typeface.BOLD)
        color = Color.YELLOW
    }
    @Transient
    private val neighbour_count_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 45.0f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    //flag that sets cell's revealed state
    private var revealed:Boolean = false
    //flag that sets cell's hasMine state
    private var hasMine:Boolean = false
    //flag that is set to true if user clicks on a first mine
    private var isFirstClickedMine:Boolean = false
    //flag that is set to true cell is clicked in marking mode
    private var isMarked:Boolean = false
    //counter to keep track of number of clicks on a marked cell in a marking mode
    private var clicksOnMarkedCell = 0
    //number of neighbours of a cell
    private var totalNeighbours = 0

    //cell to be drawn
    @Transient
    var rect = Rect(_l,_r,_t,_b)

    //Needed for Parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
        _i = parcel.readInt()
        _j = parcel.readInt()
        _l = parcel.readInt()
        _r = parcel.readInt()
        _t = parcel.readInt()
        _b = parcel.readInt()
        cellCoveredColor = parcel.readInt()
        cellOutlineColor = parcel.readInt()
        cellUncoveredColor = parcel.readInt()
        _strokeWidth = parcel.readFloat()
        number_one_colour = parcel.readInt()
        number_two_colour = parcel.readInt()
        number_three_colour = parcel.readInt()
        number_four_colour = parcel.readInt()
        number_five_colour = parcel.readInt()
        number_six_colour = parcel.readInt()
        number_seven_colour = parcel.readInt()
        number_eight_colour = parcel.readInt()
        revealed = parcel.readByte() != 0.toByte()
        hasMine = parcel.readByte() != 0.toByte()
        isFirstClickedMine = parcel.readByte() != 0.toByte()
        isMarked = parcel.readByte() != 0.toByte()
        clicksOnMarkedCell = parcel.readInt()
        totalNeighbours = parcel.readInt()
    }

    //function that draws a cell on the main board depending on its state
    fun show(__canvas: Canvas){
        if(this.revealed && !this.hasMine){
            this.showUncovered(__canvas)
            setTextColour()
            if(this.totalNeighbours > 0){
                this.drawNeighbourCountText(__canvas)
            }
        } else if(this.revealed && this.hasMine){
            this.showMine(__canvas)
        } else if(this.isMarked){
            this.showMarkedCell(__canvas)
        } else{
            showCovered(__canvas)
        }
    }

    private fun setTextColour(){
        when(this.totalNeighbours){
            1 -> neighbour_count_paint.setColor(number_one_colour)
            2 -> neighbour_count_paint.setColor(number_two_colour)
            3 -> neighbour_count_paint.setColor(number_three_colour)
            4 -> neighbour_count_paint.setColor(number_four_colour)
            5 -> neighbour_count_paint.setColor(number_five_colour)
            6 -> neighbour_count_paint.setColor(number_six_colour)
            7 -> neighbour_count_paint.setColor(number_seven_colour)
            8 -> neighbour_count_paint.setColor(number_eight_colour)
            else -> neighbour_count_paint.setColor(Color.BLACK)
        }
    }

    //function that draws a cell with parameters of a covered cell
    private fun showCovered(__canvas: Canvas){
        drawCellRect(__canvas,covered_cell_paint,stroke_paint,rect)
    }

    //function that draws a cell with parameters of an uncovered cell
    private fun showUncovered(__canvas: Canvas){
        drawCellRect(__canvas,uncovered_cell_paint,stroke_paint,rect)
    }

    //function that draws a cell with parameters of a cell containing a mine
    private fun showMine(__canvas: Canvas){
        drawCellRect(__canvas,mined_cells_paint,stroke_paint,rect)
        drawMineText(__canvas)
    }

    //function that draws a cell with parameters of a cell containing a remaining mine when game is over
    fun showRemainingMine(__canvas: Canvas){
        drawCellRect(__canvas,remaining_mined_cells_paint,stroke_paint,rect)
        drawMineText(__canvas)
    }

    private fun showMarkedCell(__canvas: Canvas){
        drawCellRect(__canvas,marked_mine_paint,stroke_paint,rect)
    }

    //M letter is drawn within a cell
    private fun drawMineText(__canvas: Canvas){
        __canvas.drawText("M",this.rect.exactCenterX().toFloat(),(this.rect.exactCenterY()+20).toFloat(),mine_text_paint)
    }

    private fun drawNeighbourCountText(__canvas: Canvas){
        __canvas.drawText(this.totalNeighbours.toString(),this.rect.exactCenterX().toFloat(),(this.rect.exactCenterY()+20).toFloat(),neighbour_count_paint)
    }

    //function draw draws a rectangle based on the parameters provided
    private fun drawCellRect(__canvas: Canvas,cellPaint: Paint,strokePaint:Paint,cellRect: Rect){
        __canvas.save()
        __canvas.drawRect(cellRect,cellPaint)
        __canvas.restore()
        __canvas.save()
        __canvas.drawRect(cellRect,strokePaint)
        __canvas.restore()
    }

    //getters and setters
    fun Coordinates(): String {
        return "L: " + _l + " R: " + _r + " T: " + _t + " B: " + _b ;
    }

    fun left():Int{
        return this._l
    }

    fun right():Int{
        return this._r
    }

    fun top():Int{
        return _t;
    }

    fun bottom():Int{
        return _b;
    }

    fun rectX(): Int {
        return rect.centerX();
    }

    fun rectY():Int{
        return rect.centerY();
    }

    fun setRevealed(isRevealed:Boolean){
        this.revealed = isRevealed;
    }

    fun isRevealed():Boolean{
        return this.revealed;
    }

    fun setHasMine(mine:Boolean){
        this.hasMine = mine;
    }

    fun hasMine():Boolean{
        return this.hasMine;
    }

    fun setFirstClickedMine(mine: Boolean){
        this.isFirstClickedMine = mine
    }

    fun getFirstClickedMine():Boolean{
        return this.isFirstClickedMine
    }

    fun setMarked(mark:Boolean){
        this.isMarked = mark
    }

    fun getIsMarked():Boolean{
        return this.isMarked
    }

    fun setClicksOnMarkedCell(clicks:Int){
        this.clicksOnMarkedCell = clicks
    }

    fun getClicksOnMarkedCell():Int{
        return this.clicksOnMarkedCell
    }

    fun setNeighbours(count:Int){
        this.totalNeighbours = count
    }

    fun getNeighbours():Int{
        return this.totalNeighbours
    }

    //Needed for Parcelable implementation
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(i)
        parcel.writeInt(j)
        parcel.writeInt(l)
        parcel.writeInt(r)
        parcel.writeInt(t)
        parcel.writeInt(b)
        parcel.writeInt(_i)
        parcel.writeInt(_j)
        parcel.writeInt(_l)
        parcel.writeInt(_r)
        parcel.writeInt(_t)
        parcel.writeInt(_b)
        parcel.writeInt(cellCoveredColor)
        parcel.writeInt(cellOutlineColor)
        parcel.writeInt(cellUncoveredColor)
        parcel.writeFloat(_strokeWidth)
        parcel.writeInt(number_one_colour)
        parcel.writeInt(number_two_colour)
        parcel.writeInt(number_three_colour)
        parcel.writeInt(number_four_colour)
        parcel.writeInt(number_five_colour)
        parcel.writeInt(number_six_colour)
        parcel.writeInt(number_seven_colour)
        parcel.writeInt(number_eight_colour)
        parcel.writeByte(if (revealed) 1 else 0)
        parcel.writeByte(if (hasMine) 1 else 0)
        parcel.writeByte(if (isFirstClickedMine) 1 else 0)
        parcel.writeByte(if (isMarked) 1 else 0)
        parcel.writeInt(clicksOnMarkedCell)
        parcel.writeInt(totalNeighbours)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cell> {
        override fun createFromParcel(parcel: Parcel): Cell {
            return Cell(parcel)
        }

        override fun newArray(size: Int): Array<Cell?> {
            return arrayOfNulls(size)
        }
    }


}
