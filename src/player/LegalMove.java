/* LegalMoveImpl.java  */

package player;

import java.util.List;

/**
 *  An interface of LegalMove include generate all legal moves for the current gameboard
 *  and judge if a move is a legal move.
 */
public interface LegalMove {

	List<Move> generateLegalMove(Gameboard gb, Chip c, boolean add);
	boolean isLegalMove(Gameboard gb, Move m, Chip c);
}