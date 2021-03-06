package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.enums.Color;

public class Pawn extends ChessPiece {

	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public String toString() {
		return "P";
	}

	/*
	 * Instantiates a matrix of booleans using as arguments: getBoard: method from
	 * abstract class Piece getRows/Columns: method from class Board, returning
	 * number of rows and columns of the already created board
	 */
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);

		// Black
		if (getColor() == Color.BLACK) {
			p.setValues(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 2, position.getColumn());
			Position p2 = new Position(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2)
					&& !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
		}

		// En Passant(Black)
		if (position.getRow() == 4) {
			// Left
			p.setValues(position.getRow(), position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)
					&& getBoard().piece(p) == chessMatch.getEnPassantVulnerable()) {
				mat[p.getRow() + 1][p.getColumn()] = true;
			}
			// Right
			p.setValues(position.getRow(), position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)
					&& getBoard().piece(p) == chessMatch.getEnPassantVulnerable()) {
				mat[p.getRow() + 1][p.getColumn()] = true;
			}
		}

		// White
		if (getColor() == Color.WHITE) {
			p.setValues(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 2, position.getColumn());
			Position p2 = new Position(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2)
					&& !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
		}
		// En Passant(White)
		if (position.getRow() == 3) {
			// Left
			p.setValues(position.getRow(), position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)
					&& getBoard().piece(p) == chessMatch.getEnPassantVulnerable()) {
				mat[p.getRow() - 1][p.getColumn()] = true;
			}
			// Right
			p.setValues(position.getRow(), position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)
					&& getBoard().piece(p) == chessMatch.getEnPassantVulnerable()) {
				mat[p.getRow() - 1][p.getColumn()] = true;
			}
		}

		return mat;
	}

}
