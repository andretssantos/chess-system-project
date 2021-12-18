package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.enums.Color;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check; // initiates as false by default, no need to include it in constructor
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;

	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}

	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE; // if ? then : else;
	}

	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream()
				.filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());

		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}

	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}

	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);

		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You cannot put yourself in check. Please inform a different move");
		}

		ChessPiece movedPiece = (ChessPiece) board.piece(target);

		check = (testCheck(opponent(currentPlayer))) ? true : false;

		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}

		if (movedPiece instanceof Pawn
				&& (source.getRow() + 2 == target.getRow() || source.getRow() - 2 == target.getRow())) {
			enPassantVulnerable = movedPiece;
		}

		return (ChessPiece) capturedPiece;
	}

	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		// Castling
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sCRookSource = new Position(source.getRow(), source.getColumn() + 3);
			Position sCRookTarget = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece shortCastRook = (ChessPiece) board.removePiece(sCRookSource);
			shortCastRook.increaseMoveCount();
			board.placePiece(shortCastRook, sCRookTarget);
		}
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position lCRookSource = new Position(source.getRow(), source.getColumn() - 4);
			Position lCRookTarget = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece longCastRook = (ChessPiece) board.removePiece(lCRookSource);
			longCastRook.increaseMoveCount();
			board.placePiece(longCastRook, lCRookTarget);
		}
		// En Passant
		if (p instanceof Pawn && source.getRow() != target.getColumn() && capturedPiece == null) {
			Position enPassantCaptured;
			if (p.getColor() == Color.BLACK) {
				enPassantCaptured = new Position(target.getRow() - 1, target.getColumn());
			} else {
				enPassantCaptured = new Position(target.getRow() + 1, target.getColumn());
			}
			capturedPiece = board.removePiece(enPassantCaptured);
			capturedPieces.add(capturedPiece);
			piecesOnTheBoard.remove(capturedPiece);
		}
		return capturedPiece;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		// Castling
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sCRookSource = new Position(source.getRow(), source.getColumn() + 3);
			Position sCRookTarget = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece shortCastRook = (ChessPiece) board.removePiece(sCRookTarget);
			shortCastRook.decreaseMoveCount();
			board.placePiece(shortCastRook, sCRookSource);
		}
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position lCRookSource = new Position(source.getRow(), source.getColumn() - 4);
			Position lCRookTarget = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece longCastRook = (ChessPiece) board.removePiece(lCRookTarget);
			longCastRook.decreaseMoveCount();
			board.placePiece(longCastRook, lCRookSource);
		}
		// En Passant
		if (p instanceof Pawn && source.getRow() != target.getColumn() && capturedPiece == enPassantVulnerable) {
			ChessPiece pawn = (ChessPiece)board.removePiece(target);
			Position enPassantCaptured;
			if (p.getColor() == Color.BLACK) {
				enPassantCaptured = new Position(4, target.getColumn());
			} else {
				enPassantCaptured = new Position(3, target.getColumn());
			}
			board.placePiece(pawn, enPassantCaptured);
		}
	}

	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException(
					"There is no piece on the informed source position. Plese inform a valid position");
		}
		if (getCurrentPlayer() != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece belongs to the adversary. Please choose another piece");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There are no possible moves for this piece. Please choose another piece");
		}
	}

	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("Target position cannot be reached by this piece. Please inform a valid position");
		}
	}

	public void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	public void initialSetup() {
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));

		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));

		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));

		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));

		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
	}

}
