
import java.util.Date;


public class XXXOthelloPlayer extends OthelloPlayer implements MiniMax{
	private int depthLimit;
	private int generatedNodes = 1;
	private int branchingFactor = 1;
	private int AveBranchingFactor = 1;
	
	private int staticEvaluationsComputed = 0;

	
	public XXXOthelloPlayer(String name) {
		super(name);
		depthLimit = 5;
	}
	public XXXOthelloPlayer(String name, int _depthLimit) {
		super(name);
		depthLimit = _depthLimit;
	}

	@Override
	public Square getMove(GameState currentState, Date deadline) {
        Square moves[] = currentState.getValidMoves().toArray(new Square[0]);
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
    	AveBranchingFactor+= 1;
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
                branchingFactor++;
            }
            return v;

		} else {
			int v = Integer.MAX_VALUE;
            for (Square _move: possibleMoves) {
            	generatedNodes++;
                v = Integer.min(v, alphaBeta(depth - 1, currentState.applyMove(_move), alpha, beta, true));
                if ( v <= alpha) break;
                beta = Integer.min(beta, v);
                branchingFactor++;
            }
            return v;      
        }
}

  
	@Override
	public int staticEvaluator(GameState state) {
		staticEvaluationsComputed++;
		int myScore = state.getScore(state.getCurrentPlayer());
		int opponentScore = state.getScore(state.getOpponent(state.getCurrentPlayer()));
		int h_score = 50*(myScore - opponentScore)/(myScore + opponentScore);
		
		//mobility
		int myPossibleMoves = state.getValidMoves(state.getCurrentPlayer()).size();
		int opponentPossibleMoves = state.getValidMoves(state.getOpponent(state.getCurrentPlayer())).size();
		if((myPossibleMoves + opponentPossibleMoves) != 0 ) {
			h_score += 10*((myPossibleMoves - opponentPossibleMoves)/(myPossibleMoves + opponentPossibleMoves));
		} else {
			h_score += 0;
		}
		// Corner Capture + Flank Risk
		int[][] pqBoard =
			 	{{500, -100, 100, 50, 50, 100, -100, 500},
				 {-100, -200, -30 ,-30, -30 ,-30, -200, -100},
				 {100,  -30, 80, 40, 40, 80, -30, 100},
				 {50,  -30, 40, 0, 0, 40, -30, 50},
				 {50,  -30, 40, 0, 0, 40, -30, 50},
				 {100,  -30, 80, 40, 40, 80, -30, 100},
				 {-100, -200, -30, -30, -30, -30, -200, -100},
				 {500, -100, 100, 50, 50, 80, -100, 500}};

		int valueOfMove = pqBoard[state.getPreviousMove().getRow()][state.getPreviousMove().getCol()];
		h_score += valueOfMove;
		return h_score;
	}
	 /**
     * Get the number of static evaluations that were
     * performed during the search.
     * 
     * @return the number of static evaluations performed.
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
		return generatedNodes/AveBranchingFactor;
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
		return generatedNodes/branchingFactor;
	}
	
	
	
}
