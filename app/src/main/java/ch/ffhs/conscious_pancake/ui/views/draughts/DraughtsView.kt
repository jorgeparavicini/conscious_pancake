package ch.ffhs.conscious_pancake.ui.views.draughts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.draughts.model.core.Draughts
import ch.ffhs.conscious_pancake.draughts.model.core.Move
import ch.ffhs.conscious_pancake.draughts.model.core.Vector2
import ch.ffhs.conscious_pancake.draughts.model.enums.PieceType
import ch.ffhs.conscious_pancake.utils.resolveColorAttr
import kotlin.math.min

typealias OnCellClickedListener = (Vector2) -> Unit

class DraughtsView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var _draughts: Draughts? = null

    private var onCellClickedListener: OnCellClickedListener? = null

    var draughts: Draughts
        get() = _draughts!!
        set(value) {
            _draughts = value
            invalidate()
        }

    private val indentX = 1f

    private val gameWidth
        get() = width - 2 * indentX

    private val cellWidth: Float
        get() = gameWidth / draughts.size.toFloat()

    private val indentY = 1f

    private val gameHeight
        get() = height - 2 * indentY

    private val cellHeight: Float
        get() = gameHeight / draughts.size.toFloat()

    private val pieceRadius
        get() = cellWidth / 2 - 10

    private val gridColor = context.resolveColorAttr(R.attr.gridColor)

    private val gridPaint = Paint().apply {
        color = gridColor
        strokeWidth = 2f
    }

    private val whitePieceFillColor = context.resolveColorAttr(R.attr.whitePieceFillColor)

    private val whitePieceStrokeColor = context.resolveColorAttr(R.attr.whitePieceStrokeColor)

    private val whitePieceFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = whitePieceFillColor
    }

    private val whitePieceStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = whitePieceStrokeColor
    }

    private val blackPieceFillColor = context.resolveColorAttr(R.attr.blackPieceFillColor)

    private val blackPieceStrokeColor = context.resolveColorAttr(R.attr.blackPieceStrokeColor)

    private val blackPieceFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = blackPieceFillColor
    }

    private val blackPieceStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = blackPieceStrokeColor
    }

    fun setOnCellClickedListener(listener: OnCellClickedListener) {
        onCellClickedListener = listener
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (_draughts == null) return

        canvas.apply {
            drawGrid()
            drawPieces()
        }
    }

    private fun Canvas.drawGrid() {
        for (i in 0..draughts.size) {
            drawLine(
                (cellWidth * i) + indentX,
                indentY,
                (cellWidth * i) + indentX,
                gameHeight + indentY,
                gridPaint
            )
            drawLine(
                indentX,
                (cellHeight * i) + indentY,
                gameWidth + indentX,
                (cellHeight * i) + indentY,
                gridPaint
            )
        }
    }

    private fun Canvas.drawPieces() {
        for (pos in draughts.blackPieces) {
            val centerX = pos.x * cellWidth + (cellWidth / 2) + indentX
            val centerY = pos.y * cellHeight + (cellHeight / 2) + indentY
            drawCircle(centerX, centerY, pieceRadius, blackPieceFillPaint)
            drawCircle(centerX, centerY, pieceRadius, blackPieceStrokePaint)
        }

        for (pos in draughts.whitePieces) {
            val centerX = pos.x * cellWidth + (cellWidth / 2) + indentX
            val centerY = pos.y * cellHeight + (cellHeight / 2) + indentY
            drawCircle(centerX, centerY, pieceRadius, whitePieceFillPaint)
            drawCircle(centerX, centerY, pieceRadius, whitePieceStrokePaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        val size = resolveSize(min(desiredWidth, desiredHeight), MeasureSpec.EXACTLY)
        setMeasuredDimension(size, size)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                val cell = getCellFromCoordinates(event.x, event.y)
                cell?.let { onCellClickedListener?.invoke(it) }
                return true
            }
        }

        return false
    }

    private fun getCellFromCoordinates(x: Float, y: Float): Vector2? {
        if (x < indentX || x > gameWidth) return null
        if (y < indentY || y > gameHeight) return null

        val posX = ((x - indentX) / cellWidth).toInt()
        val posY = ((y - indentY) / cellHeight).toInt()
        return Vector2(posX, posY)
    }

    fun onPieceMoved(move: Move) {
        invalidate()
    }

    fun onPieceEaten(pos: Vector2) {

    }

    fun onPiecePromoted(pos: Vector2) {

    }

    fun onGameOver(winner: PieceType) {

    }

    fun onReset() {

    }

    fun onCellSelectionChanged(pos: Vector2, selected: Boolean) {

    }

    override fun onDetachedFromWindow() {
        // Remove animations
        super.onDetachedFromWindow()
    }
}