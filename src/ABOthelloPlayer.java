import java.util.AbstractSet;
import java.util.Date;


public class ABOthelloPlayer extends OthelloPlayer implements MiniMax{
	private int depthLimit;
	private int generatedNodes = 0;
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
        AbstractSet<Square> possibleMoves = currentState.getValidMoves();

        Square moves[] = currentState.getValidMoves().toArray(new Square[0]);
        int randomNext = currentState.getRandom().nextInt(moves.length);

        int bestMoveValue = -1;
        Square nextMove = moves[randomNext];

        for (Square _move: possibleMoves) {
            int moveValue = alphaBeta(depthLimit, currentState, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

            if (moveValue >= bestMoveValue) {
                bestMoveValue = moveValue;
                nextMove = _move;
            }

        }
		return nextMove;
	}


    public int alphaBeta (int depth, GameState currentState, int alpha, int beta, boolean isMax) {
        AbstractSet<Square> possibleMoves = currentState.getValidMoves();
        if (depth == 0 ) {
            return staticEvaluator(currentState);
        }
        if (isMax) {
			int v = Integer.MIN_VALUE;
            for(Square _move: possibleMoves) {
                v = Integer.max(v, alphaBeta(depth - 1, currentState.applyMove(_move), alpha, beta, false));
                if ( v >= beta) break;
                alpha = Integer.max(alpha, v);
            }
            return v;

		} else {
			int v = Integer.MAX_VALUE;
            for (Square _move: possibleMoves) {
                v = Integer.min(v, alphaBeta(depth - 1, currentState.applyMove(_move), alpha, beta, true));
                if ( v <= alpha) break;
                beta = Integer.min(beta, v);
            }
            return v;      
        }
}

  
	@Override
	public int staticEvaluator(GameState state) {
		staticEvaluationsComputed++;
		// check current state of the board
		// Apply all moves and calculate scores
		// Assign scores to possible nodes
		
		//Study strategy to play Othello:
		/*p 
		 * Using actual game score for minimax is bad
		 * Avoid the space next to the corners and the side! - Mark a low score for those spaces 
		 * Avoid at all cost B1, G1, B7, G7
		 * Mark a high score for the spaces in the corners and on the side - Especially corners: Mark it for 99? 
		 * Corner: Using Static Values
		 * A  B  C  D  E  F  G  H
		 * 100  50 50 50 50 50 50  100
		 * 0 -99 20 20 20 20 -99 50				
		 * 50  20 60 40 40 40 20  0
		 * 50  20 40 PS PS 40 20  50
		 * 50  20 40 PS PS 40 20  50
		 * 50  20 40 40 40 40 20  50
		 * 0 -99 20 20 20 20 -100 0
		 * 100  0 50 50 50 50 0  100
		 * result
		 * Mobility: 
		 * The more moves you have the better the reverse is applied for the opponent:
		 * result += (youValidMoves - opponentValidMoves)*20
		 * 
		 */
		int h_score = state.getScore(state.getCurrentPlayer()) * 5;
		int[][] pqBoard =
			 	{{400, -100, 60, 50, 50, 60, -100, 400},
				 {-100, -150, 20 ,20, 20 ,20, -150, -100},
				 {60,  20, 40, 40, 40, 40, 20, 60},
				 {50,  20, 40, 40, 40, 40, 20, 50},
				 {50,  20, 40, 40, 40, 40, 20, 50},
				 {60,  20, 40, 40, 40, 40, 20, 60},
				 {-100, -150, 20, 20, 20, 20, -150, -100},
				 {400, -100, 50, 50, 50, 50, -100,  400}};
		int score_of_move = pqBoard[state.getPreviousMove().getRow()][state.getPreviousMove().getCol()];
		h_score += score_of_move; 
		
		AbstractSet<Square> myPossibleMoves = state.getValidMoves(state.getCurrentPlayer());
		AbstractSet<Square> opponentPossibleMoves = state.getValidMoves(state.getOpponent(state.getCurrentPlayer()));
		h_score += (myPossibleMoves.size() - opponentPossibleMoves.size())*15;
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
		// TODO Auto-generated method stub
		return 0;
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
		return 0;
	}
	
	
	
}
