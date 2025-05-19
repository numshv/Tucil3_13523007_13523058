package solver;

import obj.Board;

abstract public class Heuristic {
    abstract public int evaluate(Board b);
}
