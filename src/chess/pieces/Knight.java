package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.enums.Color;

public class Knight extends ChessPiece {

	public Knight(Board board, Color color) {
		super(board, color);
	}
	
	@Override
	public String toString() {
		return "k";
	}
	
	/*  Instantiates a matrix of booleans using as arguments:
	 *  getBoard: method from abstract class Piece
	 	getRows/Columns: method from class Board, returning number of rows and columns of the already created board */ 
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		return mat;
	}
	
}
