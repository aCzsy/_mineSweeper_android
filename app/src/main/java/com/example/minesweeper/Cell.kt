package com.example.minesweeper

import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Cell(var i:Int, var j:Int,var l:Int, var r: Int, var t:Int, var b:Int): Parcelable, Serializable{
    //initializing variables
    private var _i:Int = i
    private var _j:Int = j
    private var _l:Int = l
    private var _r:Int = r
    private var _t:Int = t
    private var _b:Int = b

    private var cellCoveredColor = Color.BLACK
    private var cellOutlineColor = Color.WHITE
    private var cellUncoveredColor = Color.GRAY
    private var _strokeWidth = 4.0f

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
    private val mine_text_paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 45.0f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create( "", Typeface.BOLD)
        color = Color.BLACK
    }

    //flag that sets cell's revealed state
    private var revealed:Boolean = false
    private var hasMine:Boolean = false


    //cell to be drawn
    @Transient
    var rect = Rect(_l,_r,_t,_b)

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
        revealed = parcel.readByte() != 0.toByte()
        hasMine = parcel.readByte() != 0.toByte()
    }


    //function that draws a cell on the main board depending on its state
    fun show(__canvas: Canvas){
        if(this.revealed && !this.hasMine){
            this.showUncovered(__canvas)
        } else if(this.revealed && this.hasMine){
            this.showMine(__canvas)
        } else{
            showCovered(__canvas)
        }
    }

    //function that draws covered cell on the main board
    fun showCovered(__canvas: Canvas){
        drawCellRect(__canvas,covered_cell_paint,stroke_paint,rect)
    }

    //function that draws uncovered cell on the main board
    fun showUncovered(__canvas: Canvas){
        drawCellRect(__canvas,uncovered_cell_paint,stroke_paint,rect)
    }

    fun showMine(__canvas: Canvas){
        drawCellRect(__canvas,mined_cells_paint,stroke_paint,rect)
        drawMineText(__canvas)
    }

    fun drawMineText(__canvas: Canvas){
        __canvas.drawText("M",this.rect.exactCenterX().toFloat(),(this.rect.exactCenterY()+20).toFloat(),mine_text_paint)
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
        parcel.writeByte(if (revealed) 1 else 0)
        parcel.writeByte(if (hasMine) 1 else 0)
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
