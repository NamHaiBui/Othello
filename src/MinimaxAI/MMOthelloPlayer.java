package MinimaxAI;
import java.util.AbstractSet;
import java.util.Date;

import Othello.*;


public class MMOthelloPlayer extends OthelloPlayer implements MiniMax{
	// This field set our maximum depth limit.
	private int depthLimit;
	// initialize the number of generated nodes : integer.
	private int generatedNodes = 1;
	// initialize the number of explored nodes : integer.
	private int exploredNodes = 1;
	// initialize the number of Non Terminal nodes: integer.
	private int numNonTerminal = 1;
	// initialize the number of static Evaluations has been computed : integer.
	private int staticEvaluationsComputed = 0;
	// initialize the number of AveBranchingFactor : double.
	private double AveBranchingFactor = 1;
	// initialize the number of effective branchingFactor : double.
	private double branchingFactor = 1;
	

	// Constructor for MniMax Player. Initial depth limit is set to a constant.
	public MMOthelloPlayer(String name) {
		super(name);
		depthLimit = 5;
	}
	// Constructor for MiniMax Player with given depth limit.
	public MMOthelloPlayer(String name, int _depthLimit) {
		super(name);
		depthLimit = _depthLimit;
	}

	 /**
     * The getMove function for this player.  This function
     * will return the best move.
     * 
     * @param currentState the current state of the board.
	 * @param deadline the left time to play.
     * @return the Square whose best move is calculated.
     */

	@Override
	public Square getMove(GameState currentState, Date deadline) {
        Square moves[] = currentState.getValidMoves().toArray(new Square[0]);
		
		// This store the best move value of the current board.
		int bestValue = Integer.MIN_VALUE;
		// This store the next move
        Square nextMove = null;

        for(Square _move : moves) {
			generatedNodes++;

			// We want the role of MAX Player, so we want to find all the MIN child -> isMAX is false.
        	int moveValue = miniMax(depthLimit, currentState.applyMove(_move),false);
    		
			if(moveValue > bestValue) {
    			bestValue = moveValue;
    			nextMove = _move;
    		};
        }
		return nextMove;
	}


	//**************************    MiniMax Search algo ************************* 
	/**
     * The getMove function for this player.  This function
     * will return the best move.
     * 
     * @param depth the current depth of the node.
	 * @param currentState the current state of the board.
	 * @param isMax if this is the Max Player then true.
     * @return the int value whose best move is calculated.
     */

	public int miniMax(int depth, GameState currentState,boolean isMax) {
		Square possibleMoves[] = currentState.getValidMoves().toArray(new Square[0]);
		
		// if depth = 0 or node is a terminal node.
		if(depth == 0 || possibleMoves.length == 0) {
			return staticEvaluator(currentState);
		} else { 
			numNonTerminal += 1; // this node is a non Terminal node.
			generatedNodes += possibleMoves.length; // the number of child is generated.
		}

		if (isMax) {
			int v = Integer.MIN_VALUE;
			for(Square _move: possibleMoves) {	
				exploredNodes++;
				//Recursively search the new game state		
				v = Integer.min(v, miniMax(depth - 1, currentState.applyMove(_move), false));
			}
			return v;
		} else {
			int v = Integer.MAX_VALUE;		
			for(Square _move: possibleMoves) {
				exploredNodes++;
				v = Integer.max(v, miniMax(depth - 1, currentState.applyMove(_move), true));
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
     * Get the number of nodes that were
     * generated during the search.
     * 
     * @return the number of generatedNodes.
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
		// formula: mean b = # non of root nodes / # of non terminal nodes
		AveBranchingFactor = (generatedNodes - 1) / numNonTerminal;  // Minus 1 is minus the root
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
		// formula: b = # explored nodes / # of non terminal nodes
		branchingFactor = (exploredNodes - 1) /numNonTerminal; // Minus 1 is minus the root
		return branchingFactor;
	}
	
	
	
}
