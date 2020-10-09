/* LegalMove.java  */

package player;

import java.util.ArrayList;
import java.util.List;

/**
 *  An interface of LegalMove include generate all legal moves for the current gameboard
 *  and judge if a move is a legal move.
 */
public class LegalMoveImpl implements LegalMove {

  private int[][] offset = {{-1, 0, 1, 1, 1, 0, -1, -1}, {-1, -1, -1, 0, 1, 1, 1, 0}};

  public List<Move> generateLegalMove(Gameboard gb, Chip c, boolean add) {
    List<Move> moves = new ArrayList<Move>();
//    System.out.println("generate legal move");
    if (add) {
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          Move m = new Move(i, j);
          m.toString();
          if (isLegalMove(gb, m, c)) {
            moves.add(m);
          }
        }
      }
    }
    else {
      for (int i1 = 0; i1 < 8; i1++) {
        for (int j1 = 0; j1 < 8; j1++) {
          if (gb.getBoard(i1, j1) != Chip.EMPTY) {
            continue;
          }
          for (int i2 = 0; i2 < 8; i2++) {
            for (int j2 = 0; j2 < 8; j2++) {
              if (gb.getBoard(i2, j2) != c) {
                continue;
              }
              Move m = new Move(i1, j1, i2, j2);
              
              if (isLegalMove(gb, m, c)) {
//            	  System.out.println(m.toString());
                moves.add(m);
              }
            }
          }
        }
      }
    }

    return moves;
  }

  public boolean isLegalMove(Gameboard gb, Move m, Chip c) {
//	  System.out.println("check is legal move");
    if (m.moveKind == Move.STEP) {
      if (gb.getBoard(m.x2, m.y2) != c) {
        return false;
      }
      if (m.x1 == m.x2 && m.y1 == m.y2) {
        return false;
      }
    }

    // No chip may be placed in any of the four corners.
    if ((m.x1 == 0 || m.x1 == 7) && (m.y1 == 0 || m.y1 == 7)) {
//    	System.out.println("placed at corner");
      return false;
    }

    // No chip may be placed in a goal of the opposite color
    if (c == Chip.WHITE) {
      if (m.y1 == 0 || m.y1 == 7) {
//    	  System.out.println("placed at opposite goal area");
        return false;
      }
    }
    else if (c == Chip.BLACK) {
      if (m.x1 == 0 || m.x1 == 7) {
//    	  System.out.println("placed at opposite goal area");
        return false;
      }
    }

    // No chip may be placed in a square that is already occupied
    if (gb.getBoard(m.x1, m.y1) != Chip.EMPTY) {
//    	System.out.println("placed at occupied place");
      return false;
    }

    // A player may not have more than two chips in a connected group
    Gameboard gb2 = gb.clone();
    gb2.setMove(m, c);
    
    if (formCluster(gb2, m.x1, m.y1)) {
      return false;
    }
    for (int k = 0; k < 8; k++) {
      int x = m.x1 + offset[0][k];
      int y = m.y1 + offset[1][k];
      if (x < 0 || x >= 8 || y < 0 || y >= 8) {
        continue;
      }
      if (gb2.getBoard(x, y) == c && formCluster(gb2, x, y)) {
        return false;
      }
    }

    return true;
  }

  private boolean formCluster(Gameboard gb, int x1, int y1) {
//	  System.out.println("--------------------------");
//	  System.out.println(x1 + ","+y1);
    int cnt = 0;
    for (int k = 0; k < 8; k++) {
      int x = x1 + offset[0][k];
      int y = y1 + offset[1][k];
      if (x < 0 || x >= 8 || y < 0 || y >= 8) {
        continue;
      }
//      System.out.println(x+","+y+":"+gb.getBoard(x, y));
      if (gb.getBoard(x, y) == gb.getBoard(x1, y1)) {
        cnt++;
      }
    }
    if (cnt >= 2) {
//    	System.out.println(x1+","+y1+" form a cluster");
//    	System.out.println("--------------------------");
    }
    return cnt >= 2;
  }
}
