/* EvaluatorImpl.java */

package player;

import java.util.HashSet;
import java.util.Set;

/**
 *  An implementation of the game board.  
 *  Keeps track of the current state of the game board.
 */
public class EvaluatorImpl implements Evaluator {

	public double evaluateMove(Gameboard gb, Move m, Chip c) {
		Chip e = Chip.EMPTY;
		if (c == Chip.BLACK) {
			e = Chip.WHITE;
		}
		else if (c == Chip.WHITE) {
			e = Chip.BLACK;
		}
		
		Gameboard gb2 = gb.clone();
		gb2.setMove(m, c);
		if (completeNetwork(gb2, e)) {
			return -10000;
		}
		if (completeNetwork(gb2, c)) {
			return 10000;
		}
		
		
		int cntPrevC = countPairs(gb, c);
		int cntCurrC = countPairs(gb2, c);
		int cntPrevE = countPairs(gb, e);
		int cntCurrE = countPairs(gb2, e);
//		System.out.println(m.toString()+":"+cntPrevC+","+cntCurrC+","+cntPrevE+","+cntCurrE);
//		System.out.println(m.toString()+"|"+cntPrevC+","+cntCurrC+","+cntPrevE+","+cntCurrE);
		
		double score = (double)(cntCurrC - cntPrevC) * 50.0 + (double)(cntPrevE - cntCurrE) * 70.0;
//		double score1 = (double)countPairs(gb2, c) * 10.0;
//		double score2 = (double)countPairs(gb2, e) * 5.0;
		return score;
	}

	public boolean completeNetwork(Gameboard gb, Chip c) {
		boolean flag = false;
		if (c == Chip.WHITE) {
			Set<Integer> set = new HashSet<Integer>();
			for (int i = 0; i < 7; i++) {
				if (gb.getBoard(0, i) == Chip.WHITE) {
					set.add(i);
					flag = searchNetwork(gb, c, 0, i, set, 1, Direction.UP);
					set.remove(i);
					if (flag) {
						return true;
					}
				}
			}
		}
		else if (c == Chip.BLACK) {
			Set<Integer> set = new HashSet<>();
			for (int i = 0; i < 7; i++) {
				if (gb.getBoard(i, 0) == Chip.BLACK) {
					set.add(i * 10);
					flag = searchNetwork(gb, c, i, 0, set, 1, Direction.LEFT);
					set.remove(i * 10);
					if (flag) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean searchNetwork(Gameboard gb, Chip c, int x, int y, Set<Integer> set, int cnt, Direction dire) {
		// search for black
		Chip e = Chip.EMPTY;
		if (c == Chip.BLACK) {
			e = Chip.WHITE;
			if (y == 7) {
				return cnt >= 6;
			}
		}
		if (c == Chip.WHITE) {
			e = Chip.BLACK;
			if (x == 7) {
				return cnt >= 6;
			}
		}

		boolean flag = false;
		// up: 1
		if (dire != Direction.UP && dire != Direction.DOWN) {
			for (int i = y - 1; i >= 1; i--) {
				int num = x * 10 + i;
				if (gb.getBoard(x, i) == e) {
					break;
				}
				else if (gb.getBoard(x, i) == c) {
					if (!set.contains(num)) {
						set.add(num);
						flag = searchNetwork(gb, c, x, i, set, cnt + 1, Direction.UP);
						set.remove(num);
						if (flag) {
							return true;
						}
					}
					break;
				}
			}
		}

		// down: 2
		if (dire != Direction.DOWN && dire != Direction.UP) {
			for (int i = y + 1; i < 8; i++) {
				int num = x * 10 + i;
				if (gb.getBoard(x, i) == e) {
					break;
				}
				else if (gb.getBoard(x, i) == c) {
					if (!set.contains(num)) {
						set.add(num);
						flag = searchNetwork(gb, c, x, i, set, cnt + 1, Direction.DOWN);
						set.remove(num);
						if (flag) {
							return true;
						}
					}
					break;
				}
			}
		}

		// left: 3
		if (dire != Direction.LEFT && dire != Direction.RIGHT) {
			for (int i = x - 1; i >= 1; i--) {
				int num = i * 10 + y;
				if (gb.getBoard(i, y) == e) {
					break;
				}
				else if (gb.getBoard(i, y) == c) {
					if (!set.contains(num)) {
						set.add(num);
						flag = searchNetwork(gb, c, i, y, set, cnt + 1, Direction.LEFT);
						set.remove(num);
						if (flag) {
							return true;
						}
					}
					break;
				}
			}
		}

		// right: 4
		if (dire != Direction.RIGHT && dire != Direction.LEFT) {
			for (int i = x + 1; i < 8; i++) {
				int num = i * 10 + y;
				if (gb.getBoard(i, y) == e) {
					break;
				}
				else if (gb.getBoard(i, y) == c) {
					if (!set.contains(num)) {
						set.add(num);
						flag = searchNetwork(gb, c, i, y, set, cnt + 1, Direction.RIGHT);
						set.remove(num);
						if (flag) {
							return true;
						}
					}
					break;
				}
			}
		}

		// left-up: 5
		if (dire != Direction.UPPERLEFT && dire != Direction.LOWERRIGHT) {
			for (int i = x - 1, j = y - 1; i >= 1 && j >= 1; i--, j--) {
				int num = i * 10 + j;
				if (gb.getBoard(i, j) == e) {
					break;
				}
				else if (gb.getBoard(i, j) == c) {
					if (!set.contains(num)) {
						set.add(num);
						flag = searchNetwork(gb, c, i, j, set, cnt + 1, Direction.UPPERLEFT);
						set.remove(num);
						if (flag) {
							return true;
						}
					}
					break;
				}
			}
		}

		// right-up: 6
		if (dire != Direction.UPPERRIGHT && dire != Direction.LOWERLEFT) {
			for (int i = x + 1, j = y - 1; i < 8 && j >= 1; i++, j--) {
				int num = i * 10 + j;
				if (gb.getBoard(i, j) == e) {
					break;
				}
				else if (gb.getBoard(i, j) == c) {
					if (!set.contains(num)) {
						set.add(num);
						flag = searchNetwork(gb, c, i, j, set, cnt + 1, Direction.UPPERRIGHT);
						set.remove(num);
						if (flag) {
							return true;
						}
					}
					break;
				}
			}
		}

		// left-down: 7
		if (dire != Direction.LOWERLEFT && dire != Direction.UPPERRIGHT) {
			for (int i = x - 1, j = y + 1; i >= 1 && j < 8; i--, j++) {
				int num = i * 10 + j;
				if (gb.getBoard(i, j) == e) {
					break;
				}
				else if (gb.getBoard(i, j) == c) {
					if (!set.contains(num)) {
						set.add(num);
						flag = searchNetwork(gb, c, i, j, set, cnt + 1, Direction.LOWERLEFT);
						set.remove(num);
						if (flag) {
							return true;
						}
					}
					break;
				}
			}
		}

		// right-down: 8
		if (dire != Direction.LOWERRIGHT && dire != Direction.UPPERLEFT) {
			for (int i = x + 1, j = y + 1; i < 8 && j < 8; i++, j++) {
				int num = i * 10 + j;
				if (gb.getBoard(i, j) == e) {
					break;
				}
				else if (gb.getBoard(i, j) == c) {
					if (!set.contains(num)) {
						set.add(num);
						flag = searchNetwork(gb, c, i, j, set, cnt + 1, Direction.LOWERRIGHT);
						set.remove(num);
						if (flag) {
							return true;
						}
					}
					break;
				}
			}
		}

		return false;
	}

	private int countPairs(Gameboard gb, Chip c) {

		Set<String> set = new HashSet<>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (gb.getBoard(i, j) == c) {
					searchPair(gb, c, i, j, set);
				}
			}
		}

		return set.size();
	}

	private void searchPair(Gameboard gb, Chip c, int x, int y, Set<String> set) {
		int base = x * 10 + y;

			// up
			for (int i = y - 1; i >= 0; i--) {
				int num = x * 10 + i;
				if (gb.getBoard(x, i) == Chip.EMPTY) {
					continue;
				}
				if (gb.getBoard(x, i) == c) {
					set.add(num + "-" + base);
				}
				break;
			}

			// down
			for (int i = y + 1; i < 8; i++) {
				int num = x * 10 + i;
				if (gb.getBoard(x, i) == Chip.EMPTY) {
					continue;
				}
				if (gb.getBoard(x, i) == c) {
					set.add(base + "-" + num);
				}
				break;
			}

			// left
			for (int i = x - 1; i >= 0; i--) {
				int num = i * 10 + y;
				if (gb.getBoard(i, y) == Chip.EMPTY) {
					continue;
				}
				if (gb.getBoard(i, y) == c) {
					set.add(num + "-" + base);
				}
				break;
			}

			// right
			for (int i = x + 1; i < 8; i++) {
				int num = i * 10 + y;
				if (gb.getBoard(i, y) == Chip.EMPTY) {
					continue;
				}
				if (gb.getBoard(i, y) == c) {
					set.add(base + "-" + num);
				}
				break;
			}

			// left-up
			for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--) {
				int num = i * 10 + j;
				if (gb.getBoard(i, j) == Chip.EMPTY) {
					continue;
				}
				if (gb.getBoard(i, j) == c) {
					set.add(num + "-" + base);
				}
				break;
			}

			// right-up
			for (int i = x + 1, j = y - 1; i < 8 && j >= 0; i++, j--) {
				int num = i * 10 + j;
				if (gb.getBoard(i, j) == Chip.EMPTY) {
					continue;
				}
				if (gb.getBoard(i, j) == c) {
					set.add(num + "-" + base);
				}
				break;
			}

			// left-down
			for (int i = x - 1, j = y + 1; i >= 0 && j < 8; i--, j++) {
				int num = i * 10 + j;
				if (gb.getBoard(i, j) == Chip.EMPTY) {
					continue;
				}
				if (gb.getBoard(i, j) == c) {
					set.add(base + "-" + num);
				}
				break;
			}

			// right-down
			for (int i = x + 1, j = y + 1; i < 8 && j < 8; i++, j++) {
				int num = i * 10 + j;
				if (gb.getBoard(i, j) == Chip.EMPTY) {
					continue;
				}
				if (gb.getBoard(i, j) == c) {
					set.add(base + "-" + num);
				}
				break;
			}
	}
}