/* Gameboard.java */

package player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 *  An implementation of the game board.  
 *  Keeps track of the current state of the game board.
 */
public class Gameboard {

	private Chip[][] board;

	public Gameboard () {
		board = new Chip[8][8];
		for (int i = 0; i < 8; i++) {
			Arrays.fill(board[i], Chip.EMPTY);
//			System.out.println(Arrays.deepToString(board[i]));
		}
	}

	public void setMove(Move m, Chip c) {
		if (m.moveKind == Move.ADD) {
			board[m.x1][m.y1] = c;
		}
		else if (m.moveKind == Move.STEP) {
			if (board[m.x2][m.y2] == c) {
				board[m.x1][m.y1] = c;
				board[m.x2][m.y2] = Chip.EMPTY;
			}
		}
		else { // QUIT MOVE
			return;
		}
	}
	
	public Gameboard clone() {
		Gameboard gb = new Gameboard();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				gb.board[i][j] = board[i][j];
			}
		}

		return gb;
	}
	
	public Chip getBoard(int i, int j) {
		return board[i][j];
	}
}