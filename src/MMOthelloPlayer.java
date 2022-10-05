import java.util.AbstractSet;
import java.util.Date;




public class MMOthelloPlayer extends OthelloPlayer implements MiniMax{
	private int depthLimit;
	private int generatedNodes = 1;
	private int AveBranchingFactor = 1;
	private int branchingFactor = 1;
	private int staticEvaluationsComputed = 0;
	
	public MMOthelloPlayer(String name) {
		super(name);
		depthLimit = 1;
	}
	public MMOthelloPlayer(String name, int _depthLimit) {
		super(name);
		depthLimit = _depthLimit;
	}

	@Override
	public Square getMove(GameState currentState, Date deadline) {
        Square moves[] = currentState.getValidMoves().toArray(new Square[0]);
        int nextMoveScore = Integer.MIN_VALUE;
        Square nextMove = null;
        for(Square _move : moves) {
        	generatedNodes++;
        	int _moveScore = miniMax(depthLimit, currentState.applyMove(_move),false);
    		if(_moveScore > nextMoveScore) {
    			nextMoveScore = _moveScore;
    			nextMove = _move;
    		}
        }
		return nextMove;
	}
	public int miniMax(int depth, GameState currentState,boolean isMax) {
		Square possibleMoves[] = currentState.getValidMoves().toArray(new Square[0]);
		if(depth == 0 || currentState.getStatus() ==  GameState.GameStatus.PLAYING) {
			return staticEvaluator(currentState);
		}
		if (isMax) {
			int v = Integer.MIN_VALUE;
			for(Square _move: possibleMoves) {
				generatedNodes++;
				v = Integer.min(v, miniMax(depth - 1, currentState.applyMove(_move), false));
			}
			return v;
		}else {
			int v = Integer.MAX_VALUE;
			
			for(Square _move: possibleMoves) {
				generatedNodes++;
				v = Integer.max(v, miniMax(depth - 1, currentState.applyMove(_move), false));
			}
			return v;
        }
	}
	
	
	
	 /**
     * The static evaluation function for your search.  This function
     * must be used by your MiniMax and AlphaBeta algorithms for all
     * static evaluations.  It is separated out so that it can be easily
     * altered for grading purposes.
     * 
     * @param state the state to be evaluated.
     * @return an integer score for the value of the state to the max player.
     */
	@Override
	public int staticEvaluator(GameState state) {
		staticEvaluationsComputed++;
		return state.getScore(state.getCurrentPlayer());
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
		// TODO Auto-generated method stub
		return AveBranchingFactor;
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
		// TODO Auto-generated method stub
		return branchingFactor;
	}
	
	
	
}
