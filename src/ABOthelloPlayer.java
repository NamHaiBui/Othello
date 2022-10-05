import java.util.AbstractSet;
import java.util.Date;


public class ABOthelloPlayer extends OthelloPlayer implements MiniMax{
	private int depthLimit;
	private int AveBranchingFactor = 1;
	private int branchingFactor = 1;
	private int generatedNodes = 1;
	private int staticEvaluationsComputed = 0;
	
	public ABOthelloPlayer(String name) {
		super(name);
		depthLimit = 5;
	}
	public ABOthelloPlayer(String name, int _depthLimit) {
		super(name);
		depthLimit = _depthLimit;
	}

	@Override
	public Square getMove(GameState currentState, Date deadline) {
        Square moves[] = currentState.getValidMoves().toArray(new Square[0]);
        generatedNodes += moves.length;
        int nextMoveScore = Integer.MIN_VALUE;
        Square nextMove = null;
        for (Square _move: moves) {
            int moveValue = alphaBeta(depthLimit, currentState.applyMove(_move), Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            if (moveValue > nextMoveScore) {
            	nextMoveScore = moveValue;
                nextMove = _move;
            }

        }
		return nextMove;
	}


    public int alphaBeta (int depth, GameState currentState, int alpha, int beta, boolean isMax) {
    	Square possibleMoves[] = currentState.getValidMoves().toArray(new Square[0]);
    	AveBranchingFactor += 1;
        if (depth == 0 || currentState.getStatus() ==  GameState.GameStatus.PLAYING) {
            return staticEvaluator(currentState);
        }
        if (isMax) {
			int v = Integer.MIN_VALUE;
            for(Square _move: possibleMoves) {
            	generatedNodes++;
                v = Integer.max(v, alphaBeta(depth - 1, currentState.applyMove(_move), alpha, beta, false));
                if ( v >= beta) break;
                alpha = Integer.max(alpha, v);
            }
            return v;

		} else {
			int v = Integer.MAX_VALUE;
            for (Square _move: possibleMoves) {
            	generatedNodes++;
                v = Integer.min(v, alphaBeta(depth - 1, currentState.applyMove(_move), alpha, beta, true));
                
                if ( v <= alpha) break;
                beta = Integer.min(beta, v);
            }
            return v;      
        }
}

  
	@Override
	public int staticEvaluator(GameState state) {
//		staticEvaluationsComputed++;
//		int myScore = state.getScore(state.getCurrentPlayer());
//		int opponentScore = state.getScore(state.getOpponent(state.getCurrentPlayer()));
//		int h_score = 100*(myScore - opponentScore)/(myScore + opponentScore);
//		
//		//mobility
//		int myPossibleMoves = state.getValidMoves(state.getCurrentPlayer()).size();
//		int opponentPossibleMoves = state.getValidMoves(state.getOpponent(state.getCurrentPlayer())).size();
//		if((myPossibleMoves + opponentPossibleMoves) != 0 ) {
//			h_score += ((myPossibleMoves - opponentPossibleMoves)/(myPossibleMoves + opponentPossibleMoves))*100;
//		} else {
//			h_score += 0;
//		}
		// Corner Capture + Flank
//		int[][] pqBoard =
//			 	{{500, -100, 60, 50, 50, 60, -100, 500},
//				 {-100, -250, -20 ,-20, -20 ,-20, -250, -100},
//				 {60,  -20, 100, 40, 40, 100, -20, 60},
//				 {30,  -20, 40, 40, 40, 40, -20, 30},
//				 {30,  -20, 40, 40, 40, 40, -20, 30},
//				 {60,  -20, 100, 40, 40, 100, -20, 60},
//				 {-100, -250, -20, -20, -20, -20, -250, -100},
//				 {500, -100, 60, 30, 30, 60, -100,  500}};
//
//		int valueOfMove = pqBoard[state.getPreviousMove().getRow()][state.getPreviousMove().getCol()];
//		h_score += valueOfMove;
		return state.getScore(state.getCurrentPlayer());
//		return h_score;
	}
	/**
     * Get the number of nodes that were generated
     * during the search.
     * 
     * @return the number of nodes generated.
     */
	@Override
	public int getNodesGenerated() {
		return generatedNodes;
	}
	 /**
     * Get the number of static evaluations that were
     * performed during the search.
     * 
     * @return the number of static evaluations performed.
     */
	@Override
	public int getStaticEvaluations() {
		return staticEvaluationsComputed;
	}
	 /**
     * Get the average branching factor of the nodes that
     * were expanded during the search.  This is to be computed
     * based upon the actual number of children for each node.
     * 
     * @return the average branching factor.
     */
	@Override
	public double getAveBranchingFactor() {
		double res = generatedNodes/AveBranchingFactor;
		return res;
	}
	 /**
     * Get the effective branching factor of the nodes that were
     * expanded during the search.  This is to be computed based
     * upon the number of children that are explored in the search.
     * Without alpha/beta pruning this number will be equal to the 
     * average branching factor. With alpha/beta pruning it may be
     * significantly smaller.
     * 
     * @return the effective branching factor.
     */
	@Override
	public double getEffectiveBranchingFactor() {
		double res = generatedNodes/branchingFactor;
		return res;
	}
	
	
	
}
