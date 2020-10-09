/* Evaluator.java */

package player;

/**
 *  An implementation of the game board.  
 *  Keeps track of the current state of the game board.
 */
public interface Evaluator {

	double evaluateMove(Gameboard gb, Move m, Chip c);
	boolean completeNetwork(Gameboard gb, Chip c);
}