package ch.ffhs.conscious_pancake.ui.views.animation

import android.view.animation.AccelerateDecelerateInterpolator
import ch.ffhs.conscious_pancake.ui.views.draughts.Coordinates
import com.jorgeparavicini.draughts.model.core.Piece
import com.jorgeparavicini.draughts.model.core.Vector2
import timber.log.Timber
import kotlin.math.max

typealias DrawUpdateRequestHandler = () -> Unit

private const val moveDuration = 1500L
private const val eatDuration = 400L
private const val promotionDuration = 1000L
private const val selectionDuration = 200L
private const val selectionSizeIncreasePercentage = 0.3f

class PieceAnimator(val piece: Piece) {

    private var drawUpdateRequestHandler: DrawUpdateRequestHandler? = null

    private var translationAnimations = mutableListOf<Animation<Coordinates>>()

    private var sizeAnimation: Animation<Float>? = null

    private var draughtsBorderAnimation: Animation<Float>? = null

    private var selectionOpacityAnimation: Animation<Float>? = null

    var size: Float = 1f
        private set

    var coordinates: Coordinates = Coordinates.from(piece.position)
        private set

    var promotionProgress: Float = if (piece.isDraught) 1f else 0f
        private set

    var selectionOpacity: Float = 0f
        private set

    var isVisible: Boolean = true
        private set

    var selectedPieces: List<Vector2>? = null

    var selected: Boolean = false
        set(value) {
            field = value
            if (piece.eaten) {
                Timber.w("Eaten piece was selected")
                return
            }
            val target = if (selected) 1f else 0f
            val durationLeft =
                max(0f, if (selected) 1f - selectionOpacity else selectionOpacity)
            selectionOpacityAnimation =
                FloatAnimation(
                    selectionOpacity,
                    target,
                    (durationLeft * selectionDuration).toLong(),
                    AccelerateDecelerateInterpolator()
                ).apply {
                    setOnUpdateListener {
                        selectionOpacity += delta
                        size += delta * selectionSizeIncreasePercentage
                        drawUpdateRequestHandler?.invoke()
                    }
                    setOnAnimationOverListener {
                        selectionOpacityAnimation = null
                        if (!selected) {
                            selectedPieces = null
                        }
                    }
                    animator.start()
                }
        }

    init {
        piece.setOnPieceMovedHandler { from, destination -> onPieceMovedHandler(from, destination) }
        piece.setOnPieceEatenHandler { onPieceEatenHandler() }
        piece.setOnPiecePromotedHandler { onPiecePromotedHandler() }
    }

    fun setDrawUpdateRequestHandler(handler: DrawUpdateRequestHandler) {
        drawUpdateRequestHandler = handler
    }

    private fun onPieceMovedHandler(from: Vector2, destination: Vector2) {
        val translationAnimation =
            CoordinatesAnimation(
                Coordinates.from(from),
                Coordinates.from(destination),
                moveDuration,
                AccelerateDecelerateInterpolator()
            ).apply {
                setOnUpdateListener {
                    coordinates += delta
                    drawUpdateRequestHandler?.invoke()
                }
                setOnAnimationOverListener {
                    translationAnimations.remove(this)
                }
                animator.start()
            }
        translationAnimations.add(translationAnimation)
    }

    private fun onPieceEatenHandler() {
        if (sizeAnimation != null) {
            Timber.e("There is already a size animation going on")
            return
        }
        Timber.v("Playing eat animation")
        sizeAnimation =
            FloatAnimation(size, 0f, eatDuration, AccelerateDecelerateInterpolator()).apply {
                setOnUpdateListener {
                    size += delta
                    drawUpdateRequestHandler?.invoke()
                }
                setOnAnimationOverListener {
                    sizeAnimation = null
                    isVisible = false
                }
                animator.start()
            }
    }

    private fun onPiecePromotedHandler() {
        if (draughtsBorderAnimation != null) {
            Timber.e("There is already a promotion animation going on")
            return
        }
        Timber.v("Playing promotion animation")
        draughtsBorderAnimation =
            FloatAnimation(0f, 1f, promotionDuration, AccelerateDecelerateInterpolator()).apply {
                animator.startDelay = moveDuration
                setOnUpdateListener {
                    promotionProgress += delta
                    drawUpdateRequestHandler?.invoke()
                }
                setOnAnimationOverListener {
                    draughtsBorderAnimation = null
                }
                animator.start()
            }
    }

    fun reset() {
        size = 1f
        coordinates = Coordinates.from(piece.position)
        promotionProgress = if (piece.isDraught) 1f else 0f
        selectionOpacity = 0f
        isVisible = true
        selectedPieces = null
        selected = false
        translationAnimations.clear()
        sizeAnimation = null
        draughtsBorderAnimation = null
        selectionOpacityAnimation = null
    }
}