package ch.ffhs.conscious_pancake.draughts.model.core

import ch.ffhs.conscious_pancake.draughts.model.enums.MoveType
import ch.ffhs.conscious_pancake.draughts.model.enums.PieceType
import ch.ffhs.conscious_pancake.draughts.model.exceptions.IllegalMoveException
import timber.log.Timber

typealias OnPieceMovedListener = (Move) -> Unit
typealias OnPieceEatenListener = (Vector2) -> Unit
typealias OnPiecePromotedListener = (Vector2) -> Unit
typealias OnGameOverListener = (PieceType) -> Unit
typealias OnGameResetListener = () -> Unit

class Field(val size: Int = 8) {
    var winner: PieceType? = null
        private set

    val isGameOver: Boolean
        get() = winner != null

    private val cells: List<List<Cell>> = List(size) { row ->
        List(size) { column ->
            Cell(Vector2(column, row))
        }
    }

    var onPieceMovedListener: OnPieceMovedListener? = null

    var onPieceEatenListener: OnPieceEatenListener? = null

    var onPiecePromotedListener: OnPiecePromotedListener? = null

    var onGameOverListener: OnGameOverListener? = null

    var onGameResetListener: OnGameResetListener? = null

    init {
        reset()
    }

    fun reset() {
        clearField()
        resetWhitePieces()
        resetBlackPieces()
        onGameResetListener?.invoke()
        Timber.v("Field reset")
    }

    private fun clearField() {
        cells.forEach { row -> row.forEach { it.clear() } }
    }

    private fun resetWhitePieces() {
        for (y in 0 until 3) {
            cells[y].withIndex().forEach { (x, cell) ->
                if ((x + y) % 2 == 1) {
                    cell.setPiece(Piece(PieceType.WHITE))
                }
            }
        }
    }

    private fun resetBlackPieces() {
        for (y in size - 3 until size) {
            cells[y].withIndex().forEach { (x, cell) ->
                if ((x + y) % 2 == 1) {
                    cell.setPiece(Piece(PieceType.BLACK))
                }
            }
        }
    }

    fun executeMove(move: Move): Boolean {
        if (move !in getPossibleMoves(move.player)) throw IllegalMoveException("Move is not allowed")
        val pointsBetween = (move.from..move.to).drop(1).dropLast(1)
        val eaten = pointsBetween.any {
            val cell = getCell(it)
            if (cell?.piece != null && cell.piece?.type != move.player) {
                eatPiece(it)
                return@any true
            }
            false
        }

        val piece = getCell(move.from)!!.piece!!
        getCell(move.from)!!.clear()
        getCell(move.to)!!.setPiece(piece)

        Timber.v("Moved $piece from ${move.from} to ${move.to}")
        updateDraught(getCell(move.to)!!)
        onPieceMovedListener?.invoke(move)
        updateGameOver()
        return eaten
    }

    private fun updateDraught(cell: Cell) {
        if (cell.piece == null) return
        if (cell.piece!!.isDraught) return

        if (cell.piece!!.type == PieceType.BLACK && cell.position.y == 0 ||
                cell.piece!!.type == PieceType.WHITE && cell.position.y == size - 1
        ) {
            cell.piece!!.isDraught = true
            onPiecePromotedListener?.invoke(cell.position)
            Timber.v("Piece at ${cell.position} promoted")
        }
    }

    private fun updateGameOver() {
        if (getCellsWithPieces(PieceType.BLACK).count() == 0) {
            winner = PieceType.BLACK
            onGameOverListener?.invoke(winner!!)
            Timber.v("Black won")
        } else if (getCellsWithPieces(PieceType.WHITE).count() == 0) {
            winner = PieceType.WHITE
            onGameOverListener?.invoke(winner!!)
            Timber.v("White won")
        }
    }

    private fun eatPiece(position: Vector2) {
        getCell(position)?.clear()
        onPieceEatenListener?.invoke(position)
        Timber.v("Piece eaten at $position")
    }

    fun getPossibleMoves(pieceType: PieceType): List<Move> {
        val cells = getCellsWithPieces(pieceType)
        var moves = cells.map { getPossibleMoves(it) }.flatten()

        if (moves.any { move -> move.second == MoveType.VALID_EAT }) {
            moves = moves.filter { move -> move.second == MoveType.VALID_EAT }
        }
        return moves.map { move -> move.first }
    }

    private fun getPossibleMoves(cell: Cell): List<Pair<Move, MoveType>> {
        if (isGameOver) throw IllegalStateException("Can not get moves when game is already over.")
        if (cell.piece == null) throw IllegalArgumentException("There is no piece on the given cell.")
        val diagonalPositions = cell.position.getDiagonalPositions(size)

        return diagonalPositions.map { pos ->
            val move = Move(cell.position, pos, cell.piece!!.type)
            return@map Pair(move, getMoveType(move))
        }.filter { it.second != MoveType.INVALID }
    }

    private fun getMoveType(move: Move): MoveType {
        val displacement = move.to - move.from
        if (!displacement.isDiagonal) return MoveType.INVALID
        if (move.to == move.from) return MoveType.INVALID
        val cell = getCell(move.from) ?: return MoveType.INVALID
        val targetCell = getCell(move.to) ?: return MoveType.INVALID
        if (cell.piece?.type != move.player) return MoveType.INVALID

        val magnitude = displacement.diagonalMagnitude
        val isDraught = cell.piece!!.isDraught
        val pointsBetween = (move.from..move.to).toList().drop(1).dropLast(1)

        // Can't move backwards
        if (!isDraught &&
                (move.player == PieceType.BLACK && (move.to - move.from).y >= 0 ||
                        move.player == PieceType.WHITE && (move.to - move.from).y <= 0)
        ) return MoveType.INVALID

        // TODO: Check if is draughts??
        if (targetCell.isOccupied) return MoveType.INVALID
        if (magnitude > 2) return MoveType.INVALID
        if (magnitude == 2) {
            val pieceBetween = getCell(pointsBetween.first())!!
            if (!pieceBetween.isOccupied) return MoveType.INVALID
            if (pieceBetween.piece!!.type == move.player) return MoveType.INVALID
            return MoveType.VALID_EAT
        }
        return MoveType.VALID
    }

    private fun getCell(position: Vector2): Cell? {
        if (position.x >= size || position.y >= size) return null
        return cells[position.y][position.x]
    }

    fun getCellsWithPieces(pieceType: PieceType): List<Cell> {
        return cells.flatten().filter { it.piece?.type == pieceType }
    }

    override fun toString(): String {
        var rowCount = 0
        return "   " + (0 until size).joinToString(separator = "  ") { it.toString() } + "\n" +
                cells.joinToString(separator = "") { row ->
                    val result =
                        rowCount.toString() + "  " + row.joinToString(separator = "  ") + "\n"
                    rowCount += 1
                    result
                }
    }
}