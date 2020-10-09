/* MachinePlayer.java */

package player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

  private int color;
  private Chip chip;
  private int searchDepth;
  // stores the current gameboard state
  private Gameboard gameboard;
  private int leftChips;
  // interface
  private LegalMove legalMove;
  private Evaluator evaluator;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    this.color = color;
    this.chip = setChip(color);
    this.searchDepth = 3;
    this.gameboard = new Gameboard();
    this.leftChips = 10;
    this.legalMove = new LegalMoveImpl();
    this.evaluator = new EvaluatorImpl();
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
    this.color = color;
    this.chip = setChip(color);
    this.searchDepth = searchDepth;
    this.gameboard = new Gameboard();
    this.leftChips = 10;
    this.legalMove = new LegalMoveImpl();
    this.evaluator = new EvaluatorImpl();
  }

  private Chip setChip(int color) {
    if (color == 0) {
      return Chip.BLACK;
    }
    else {
      return Chip.WHITE;
    }
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    // generate all legal moves
    List<Move> legalMoves = new ArrayList<Move>();
//    System.out.println("chooseMove");
    if (leftChips == 0) {
//    	System.out.println("choose STEP Move");
      legalMoves = legalMove.generateLegalMove(gameboard, chip, false);
    }
    else {
//    	System.out.println("choose ADD Move");
      legalMoves = legalMove.generateLegalMove(gameboard, chip, true);
    }
    
    if (legalMoves.size() == 0) {
    	return new Move();
    }

//    System.out.println("find best move");
    // choose best move
    Move bestMove = legalMoves.get(0);
    double bestScore = -10000;
    double score = 0;
    double alpha = -10000;
    double beta = 10000;
    for (Move m: legalMoves) {
      if (searchDepth == 1) {
        score = evaluator.evaluateMove(gameboard, m, chip);
      }
      else {
        Gameboard gb = gameboard.clone();
        gb.setMove(m, chip);
        if (evaluator.completeNetwork(gb, setChip(1 - this.color))) {
        	score = -10000;
        }
        else if (evaluator.completeNetwork(gb, chip)) {
        	score = 10000;
        }
        else {
        	score = chooseNextMove(1 - color, searchDepth - 1, gb, alpha, beta);
        }
      }
      alpha = Math.max(alpha, score);
//      System.out.println(m.toString()+":"+score);
      if (score >= bestScore) {
        bestScore = score;
        bestMove = m;
        if (bestScore == 10000) {
        	break;
        }
      }
      
      if (alpha >= beta) {
      	break;
      }
    }
//    System.out.println(bestScore);
    // record the move
    System.out.println(bestScore);
    gameboard.setMove(bestMove, chip);
    leftChips = Math.max(leftChips - 1, 0);
    
    return bestMove;
  }

  private double chooseNextMove(int color, int depth, Gameboard gb, double alpha, double beta) {
    Chip c = setChip(color);

    // generate all legal moves
    List<Move> legalMoves = new ArrayList<Move>();
    if (leftChips == 0) {
      legalMoves = legalMove.generateLegalMove(gb, c, false);
    }
    else {
      legalMoves = legalMove.generateLegalMove(gb, c, true);
    }

    // could not find any legal move
    if (legalMoves.size() == 0) {
    	System.out.println("generate 0 legal move");
      if (color == this.color) {
        return -10000;
      }
      return 10000;
    }

    // BestMove bestMove = new BestMove();
    double bestScore = color == this.color ? -10000 : 10000;
    double score = 0;
    for (Move m: legalMoves) {
      if (depth == 1) {
        if (color == this.color) {
          score = evaluator.evaluateMove(gb, m, c);
        }
        else {
          score = -evaluator.evaluateMove(gb, m, c);
        }
      }
      else {
        Gameboard gb2 = gb.clone();
        gb2.setMove(m, c);
        if (evaluator.completeNetwork(gb2, setChip(1 - color))) {
        	score = color == this.color ? -10000 : 10000;
        }
        else if (evaluator.completeNetwork(gb2, c)) {
        	score = color == this.color ? 10000 : -10000;
        }
        else {
        	score = chooseNextMove(1 - color, depth - 1, gb2, alpha, beta);
        }
      }
      
      if (color == this.color) {
        bestScore = Math.max(bestScore, score);
        alpha = Math.max(alpha, bestScore);
        if (bestScore == 10000) {
        	break;
        }
      }
      else {
        bestScore = Math.min(bestScore, score);
        beta = Math.min(beta, bestScore);
        if (bestScore == -10000) {
        	break;
        }
      }
      
      if (alpha >= beta) {
      	break;
      }
    }
//    System.out.println(depth+":"+bestScore);
    return bestScore;
  }

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
    Chip e = setChip(1 - this.color);
//    System.out.println("opponent move");
    if (legalMove.isLegalMove(gameboard, m, e)) {
//    	System.out.println("opponent make a legal move");
      gameboard.setMove(m, e);
      return true;
    }
//    System.out.println("opponent make a illegal move");
    return false;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    if (legalMove.isLegalMove(gameboard, m, chip)) {
//    	System.out.println("force a legal move");
      gameboard.setMove(m, chip);
      return true;
    }
//    System.out.println("force a illegal move");
    return false;
  }



}
