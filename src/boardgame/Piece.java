package boardgame;


//
public abstract class Piece {

	protected Position position;;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		position = null;
	}
	
	protected Board getBoard() {
		return board;
	}
	
	//Based on arguments from each chess piece's own concrete method, returns a matrix of booleans indicating positions with legal or illegal moves
	public abstract boolean[][] possibleMoves();
	
	//Based on a target position passed as argument, tests if the move is legal or not
	public boolean possibleMove(Position position) {
		return possibleMoves()[position.getRow()][position.getColumn()];
	}
	
	//Based on abstract method possibleMoves, returns whether or not there is at least one legal move for a chess piece
	public boolean isThereAnyPossibleMove() {
		boolean[][] mat = possibleMoves();
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				if (mat[i][j]) {
					return true;
				}
			}
		}
		return false;
	}
	
}
