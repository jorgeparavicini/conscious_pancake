package ch.ffhs.conscious_pancake.ui.views.draughts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.ui.views.animation.PieceAnimator
import ch.ffhs.conscious_pancake.utils.resolveColorAttr
import com.jorgeparavicini.draughts.model.core.Draughts
import com.jorgeparavicini.draughts.model.core.Piece
import com.jorgeparavicini.draughts.model.core.Vector2
import com.jorgeparavicini.draughts.model.enums.Player
import kotlin.math.min

typealias CellClickedHandler = (Vector2) -> Unit

class DraughtsView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var _draughts: Draughts? = null

    var draughts: Draughts
        get() = _draughts!!
        set(value) {
            _draughts = value
            animators =
                (value.field.getPieces(Player.BLACK) + value.field.getPieces(Player.WHITE)).map {
                    PieceAnimator(it).apply { setDrawUpdateRequestHandler { postInvalidateOnAnimation() } }
                }
            invalidate()
        }

    private var animators: List<PieceAnimator> = emptyList()

    private var cellClickedHandler: CellClickedHandler? = null

    private val indentX = 1f

    private val gameWidth
        get() = width - 2 * indentX

    private val cellWidth: Float
        get() = gameWidth / draughts.field.fieldSize

    private val indentY = 1f

    private val gameHeight
        get() = height - 2 * indentY

    private val cellHeight: Float
        get() = gameHeight / draughts.field.fieldSize

    private val pieceRadius
        get() = cellWidth / 2 - 10

    private val gridColor = context.resolveColorAttr(R.attr.gridColor)

    private val gridPaint = Paint().apply {
        color = gridColor
        strokeWidth = 2f
    }

    private val whitePieceFillColor = context.resolveColorAttr(R.attr.whitePieceFillColor)

    private val whitePieceStrokeColor = context.resolveColorAttr(R.attr.whitePieceStrokeColor)

    private val whiteDraughtsColor = context.resolveColorAttr(R.attr.whiteDraughtsColor)

    private val whitePieceFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = whitePieceFillColor
    }

    private val whitePieceStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = whitePieceStrokeColor
    }

    private val whiteDraughtsStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = whiteDraughtsColor
    }

    private val whiteDraughtsCrownPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = whiteDraughtsColor
    }


    private val blackPieceFillColor = context.resolveColorAttr(R.attr.blackPieceFillColor)

    private val blackPieceStrokeColor = context.resolveColorAttr(R.attr.blackPieceStrokeColor)

    private val blackDraughtsColor = context.resolveColorAttr(R.attr.blackDraughtsColor)

    private val blackPieceFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = blackPieceFillColor
    }

    private val blackPieceStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = blackPieceStrokeColor
    }

    private val blackDraughtsStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = blackDraughtsColor
    }

    private val blackDraughtsCrownPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = blackDraughtsColor
    }

    private val possibleMoveCellColor = context.resolveColorAttr(R.attr.possibleMoveCellColor)

    private val possibleMoveCellPaint = Paint().apply {
        style = Paint.Style.FILL
        color = possibleMoveCellColor
    }

    fun setOnCellClickedListener(handler: CellClickedHandler) {
        cellClickedHandler = handler
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
        for (i in 0..draughts.field.fieldSize) {
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
        animators.forEach {
            if (it.isVisible) {
                drawPiece(it)

                if (it.piece.isDraught) {
                    drawCrown(it)
                    drawDraughtBorder(it)
                }

                if (it.selectedPieces != null) {
                    drawSelectedPieces(it)
                }
            }
        }
    }

    private fun Canvas.drawPiece(animator: PieceAnimator) {
        val center = getScreenCoordinates(animator.coordinates)
        val fillPaint =
            if (animator.piece.player == Player.BLACK) blackPieceFillPaint else whitePieceFillPaint
        val strokePaint =
            if (animator.piece.player == Player.BLACK) blackPieceStrokePaint else whitePieceStrokePaint

        drawCircle(center, animator.size * pieceRadius, fillPaint, strokePaint)
    }

    private fun Canvas.drawDraughtBorder(animator: PieceAnimator) {
        val center = getScreenCoordinates(animator.coordinates)
        val rect =
            RectF(
                center.x - animator.size * pieceRadius,
                center.y - animator.size * pieceRadius,
                center.x + animator.size * pieceRadius,
                center.y + animator.size * pieceRadius
            )
        val borderPaint =
            if (animator.piece.player == Player.BLACK) blackDraughtsCrownPaint else whiteDraughtsCrownPaint

        drawArc(
            rect,
            -animator.promotionProgress * 360f / 2f,
            animator.promotionProgress * 360f,
            false,
            borderPaint
        )

    }

    private fun Canvas.drawCrown(animator: PieceAnimator) {
        val center = getScreenCoordinates(animator.coordinates)

        val crownPath = Path().apply {
            moveTo(
                center.x - animator.size * pieceRadius * 0.5f,
                center.y + animator.size * pieceRadius * 0.5f
            )
            lineTo(
                center.x - animator.size * pieceRadius * 0.5f,
                center.y - animator.size * pieceRadius * 0.5f
            )
            lineTo(center.x, center.y - animator.size * pieceRadius * 0.1f)
            lineTo(
                center.x + animator.size * pieceRadius * 0.5f,
                center.y - animator.size * pieceRadius * 0.5f
            )
            lineTo(
                center.x + animator.size * pieceRadius * 0.5f,
                center.y + animator.size * pieceRadius * 0.5f
            )
        }

        val crownPaint =
            if (animator.piece.player == Player.BLACK) blackDraughtsCrownPaint else whiteDraughtsCrownPaint

        val length = PathMeasure(crownPath, false).length
        val x = length * animator.promotionProgress
        drawPath(crownPath, crownPaint.apply {
            pathEffect = DashPathEffect(floatArrayOf(x, length - x), 0f)
        })
    }

    private fun Canvas.drawSelectedPieces(animator: PieceAnimator) {
        animator.selectedPieces!!.forEach { pos ->
            val coordinates = getScreenCoordinates(Coordinates.from(pos))
            val cellRect = RectF(
                coordinates.x - cellWidth / 2 + 1,
                coordinates.y - cellHeight / 2 + 1,
                coordinates.x + cellWidth / 2 - 1,
                coordinates.y + cellHeight / 2 - 1
            )
            drawRect(cellRect, possibleMoveCellPaint.apply {
                alpha = (animator.selectionOpacity * 255f).toInt()
            })
        }
    }

    private fun Canvas.drawCircle(
        center: Coordinates,
        radius: Float,
        fillPaint: Paint,
        strokePaint: Paint
    ) {
        drawCircle(center.x, center.y, radius, fillPaint)
        drawCircle(center.x, center.y, radius, strokePaint)
    }

    // Try to make the view square whenever possible
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        val size = resolveSize(min(desiredWidth, desiredHeight), MeasureSpec.EXACTLY)
        setMeasuredDimension(size, size)
    }

    @SuppressLint("ClickableViewAccessibility")
    // There is no way to determine which piece would get clicked.
    // Hence we disable the view accessibility
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when (event.action and MotionEvent.ACTION_MASK) {
            +
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                val cell = getCellFromCoordinates(event.x, event.y)
                cell?.let { cellClickedHandler?.invoke(it) }
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

    private fun getScreenCoordinates(gameCoordinates: Coordinates): Coordinates {
        val x = gameCoordinates.x / draughts.field.fieldSize * gameWidth + indentX + cellWidth / 2
        val y = gameCoordinates.y / draughts.field.fieldSize * gameHeight + indentY + cellHeight / 2
        return Coordinates(x, y)
    }

    fun onReset() {
        resetAnimators()
    }

    fun onPieceSelectionChanged(piece: Piece, selected: Boolean) {
        val animator = animators.find { it.piece == piece }!!
        animator.selected = selected
        if (selected) {
            animator.selectedPieces =
                draughts.field.getPossibleMoves(piece.player)
                        .filter { move -> move.piece == piece }
                        .map { move -> move.destination }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        resetAnimators()
    }

    private fun resetAnimators() {
        animators.forEach { it.reset() }
    }
}