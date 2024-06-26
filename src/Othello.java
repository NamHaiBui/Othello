// package edu.drexel.cs.ai.othello;

// import java.util.HashSet;
// import java.util.Iterator;
// import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;

/**
 * A class for playing the game Othello.
 * 
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A.
 *         Sultanik</a>
 */
public class Othello {
    private OthelloPlayer player1;
    private OthelloPlayer player2;
    private long p1timeUsed;
    private long p2timeUsed;
    private GameState state;
    private UserInterface ui;
    private int turnDuration;
    private boolean verbose;

    
    private String ip = "localhost";
	private int port = 22222;
	private Scanner scanner = new Scanner(System.in);
	private int errors = 0;

	private Socket socket;
	private DataOutputStream dos;
	private BufferedReader dis;

	private ServerSocket serverSocket;

	private boolean accepted = false;
	

/**
 * 
 */
	private void listenForServerRequest() {
		Socket socket = null;
		try {
			socket = serverSocket.accept();
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			state.updateConnectionStatus(true);
			accepted = true;
			ui.handleStateUpdate(state);
			System.out.println("CLIENT HAS REQUESTED TO JOIN, AND WE HAVE ACCEPTED");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean connect() {
		try {
			socket = new Socket(ip, port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			accepted = true;
		} catch (IOException e) {
			System.out.println("Unable to connect to the address: " + ip + ":" + port + " | Starting a server");
			return false;
		}
		System.out.println("Successfully connected to the server.");
		return true;
	}

	private void initializeServer() {
		try {
			serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * The release version of this code.
     */
    public static final String VERSION = "1.5";
    /**
     * The release date of this code.
     */
    public static final String REV_DATE = "2024-22-04";

    /**
     * Constructs a new othello game with a specific seed to the random number
     * generator.
     */
    public Othello(OthelloPlayer player1, OthelloPlayer player2, UserInterface ui, long seed) {
        init(player1, player2, ui, seed, true);
    }

    /**
     * Constructs a new othello game with the random number generator seeded
     * randomly.
     */
    public Othello(OthelloPlayer player1, OthelloPlayer player2, UserInterface ui) {
        init(player1, player2, ui, 0, false);
    }

    private void init(OthelloPlayer player1, OthelloPlayer player2, UserInterface ui, long seed,
            boolean useSeed) {
        
    	boolean isMultiplayed = (player1 instanceof HumanP2PPlayer && player2 instanceof HumanP2PPlayer);
        if ((player1 instanceof HumanP2PPlayer && !( player2 instanceof HumanP2PPlayer))) {
    		player1 = new HumanOthelloPlayer(player1.getName());
    	}else if((!(player1 instanceof HumanP2PPlayer) && ( player2 instanceof HumanP2PPlayer))) {
    		player2 = new HumanOthelloPlayer(player2.getName());
    	}
        this.player1 = player1;
        this.player2 = player2;

        if (isMultiplayed) {
	    	System.out.println("Please input the IP: ");
			ip = scanner.nextLine();
			System.out.println("Please input the port: ");
			port = scanner.nextInt();
			while (port < 1 || port > 65535) {
				System.out.println("The port you entered was invalid, please input another port: ");
				port = scanner.nextInt();
			}
			boolean isConnected = connect();
			if (!isConnected) {
				initializeServer();    
			}
			if (useSeed)
	            this.state = new GameState(seed, isMultiplayed, isConnected);
	        else
	            this.state = new GameState(isMultiplayed, isConnected);
	    }
    	else {
    		if (useSeed)
                this.state = new GameState(seed);
            else
                this.state = new GameState();
    	}
        p1timeUsed = 0;
        p2timeUsed = 0;
        turnDuration = 5;
        this.ui = ui;
        verbose = true;
    }

    public void setVerbosity(boolean verbosity) {
        verbose = verbosity;
    }

    /**
     * Attempts to instantiate a new {@link OthelloPlayer} with the given
     * <code>playerName</code> from the given class.
     * 
     * @throws ClassNotFoundException if the class named <code>className</code>
     *             was not found in the current classpath.
     * @throws NoSuchMethodException if the given class does not have a
     *             constructor that takes a single {@link java.lang.String}
     *             argument.
     * @throws InstantiationException if <code>className</code> represents an
     *             abstract class.
     * @throws IllegalAccessException if the constructor of
     *             <code>className</code> enforces Java language access control
     *             and the underlying constructor is inaccessible.
     * @throws IllegalArgumentException if an unwrapping conversion for
     *             primitive arguments of the class' constructor fails.
     * @throws InvocationTargetException if the constructor of
     *             <code>className</code> throws an exception.
     * @throws ClassCastException if <code>className</code> does not extend
     *             {@link OthelloPlayer}.
     */
    public static OthelloPlayer instantiatePlayer(String className, String playerName)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, ClassNotFoundException,
            ClassCastException {
        Class<?> c = Class.forName(className);
        Constructor<?> constr = c.getDeclaredConstructor(String.class);
        Object o = constr.newInstance(playerName);
        return (OthelloPlayer) o;
    }

    private class PlayerTimerThread implements Runnable {
        Thread thread;
        OthelloPlayer player;
        Date deadline;
        Square move;
        GameState state;
        Date startTime;
        Date endTime;

        public PlayerTimerThread(OthelloPlayer player, GameState state) {
            this.player = player;
            this.state = state;
            move = null;
            thread = new Thread(this);
            startTime = null;
            endTime = null;
        }

        public Square getMove(int timeLimitSeconds) throws TimeoutException {
            long sleepInterval = ((long) timeLimitSeconds * 1000) / 60;
            startTime = new Date();
            deadline = new Date(startTime.getTime() + (long) timeLimitSeconds * 1000);
            thread.start();
            while (move == null && (new Date()).before(deadline)) {
                try {
                    Thread.yield();
                    Thread.sleep(sleepInterval);
                }
                catch (Exception e) {
                }
                ui.updateTimeRemaining(player, (new Long((deadline.getTime() - (new Date())
                        .getTime()) / 1000)).intValue());
            }
            thread = null;
            if (move == null)
                throw new TimeoutException(player.getName() + " took to long to move!");
            if (endTime == null) {
                Date currTime = new Date();
                if (currTime.getTime() - startTime.getTime() > (long) timeLimitSeconds * 1000)
                    endTime = new Date(startTime.getTime() + (long) timeLimitSeconds * 1000);
                else
                    endTime = currTime;
            }
            return move;
        }

        public long getElapsedMillis() {
            if (endTime != null && startTime != null)
                return endTime.getTime() - startTime.getTime();
            else if (startTime != null)
                return (new Date()).getTime() - startTime.getTime();
            else
                return 0;
        }

        public void run() {
            move = player.getMoveInternal(state, deadline);
            if (endTime == null)
                endTime = new Date();
            thread = null;
        }
    }

    /**
     * Causes this othello game instance to play until completion, returning the
     * winner. <code>null</code> is returned if the game resulted in a tie.
     */
    public OthelloPlayer play() {
        while (state.getStatus() == GameState.GameStatus.PLAYING) {
            if (state.getPreviousState() != null
                    && state.getPreviousState().getCurrentPlayer() == state.getCurrentPlayer())
                log((state.getCurrentPlayer() == GameState.Player.PLAYER1 ? player1.getName()
                        : player2.getName())
                        + " gets to go again!");
            ui.handleStateUpdate(state);
            OthelloPlayer player = (state.getCurrentPlayer() == GameState.Player.PLAYER1 ? player1
                    : player2);
            boolean validMove;
            if (state.getIsMultiplayed() && state.getCurrentPlayer() == GameState.Player.PLAYER1 && !accepted) {
        		listenForServerRequest();
        	}
            do {
                validMove = true;
                Square move = null;
                if (!state.getIsMultiplayed() && (player instanceof HumanOthelloPlayer || player instanceof HumanP2PPlayer)) {
                    ui.updateTimeRemaining(player, -1);
                    Date start = new Date();
                    move = player.getMoveInternal(state, null);
                    Date end = new Date();
                    ui.updateTimeRemaining(player, -1);
                    /*
                     * there is no limit for
                     * humans
                     */
                    if (state.getCurrentPlayer() == GameState.Player.PLAYER1) {
	                        p1timeUsed += end.getTime() - start.getTime();
	                        ui.updateTimeUsed(player, p1timeUsed);
	                    }
                    else {
	                        p2timeUsed += end.getTime() - start.getTime();
	                        ui.updateTimeUsed(player, p2timeUsed);
	                    }
            		}
                else if (state.getIsMultiplayed() &&( player instanceof HumanP2PPlayer)) {
                	if(state.yourTurn()) {
                	ui.updateTimeRemaining(player, -1);
                    Date start = new Date();
                    
                    move = player.getMoveInternal(state, null);
//                    Random mover
//                    Square moves[] = state.getValidMoves().toArray(new Square[0]);
//                    int next = state.getRandom().nextInt(moves.length);
//                    log("Randomly moving " + player.getName() + " to " + moves[next].toString()
//                            + "...");
//                    move = moves[next];
//                    
                    Date end = new Date();
                    ui.updateTimeRemaining(player, -1);
                    if (state.getCurrentPlayer() == GameState.Player.PLAYER1) {
	                        p1timeUsed += end.getTime() - start.getTime();
	                        ui.updateTimeUsed(player, p1timeUsed);
	                    }
                    else {
	                        p2timeUsed += end.getTime() - start.getTime();
	                        ui.updateTimeUsed(player, p2timeUsed);
	                    }
            		}}
                else {
                    PlayerTimerThread ptt = new PlayerTimerThread(player, state);
                    try {
                        move = ptt.getMove(turnDuration);
                    }
                    catch (TimeoutException te) {
                        log(te);
                        Square moves[] = state.getValidMoves().toArray(new Square[0]);
                        int next = state.getRandom().nextInt(moves.length);
                        log("Randomly moving " + player.getName() + " to " + moves[next].toString()
                                + "...");
                        move = moves[next];
                    }
                    if (state.getCurrentPlayer() == GameState.Player.PLAYER1) {
                        p1timeUsed += ptt.getElapsedMillis();
                        ui.updateTimeUsed(player, p1timeUsed);
                    }
                    else {
                        p2timeUsed += ptt.getElapsedMillis();
                        ui.updateTimeUsed(player, p2timeUsed);
                    }
                }
                
                try {
                	Square nextMove = null;
                	String nextString = "";
                	if (state.getIsMultiplayed()) {
                	
                	while(nextMove == null) {
                	if(!state.yourTurn() && errors <= 10) {
                		while (dis.ready()) {
                			nextString = dis.readLine();
                			nextMove = new Square(nextString);
                		}
                	}
                	else if (errors > 10){
                		log("Failed to connect");
                	}
                	else {
                		nextMove = move;

                		dos.writeBytes(nextMove + "\n");
                		
//                		System.out.println("DATA WAS SENT");
                		dos.flush();
                	}
                    }
                	}else {
                		nextMove = move;
                	}
                	state = state.applyMove(nextMove);
                }
                catch (InvalidMoveException ime) {
                    log(ime);
                    ui.handleStateUpdate(state);
                    validMove = false;
                }
                catch(IOException e) {
                	e.printStackTrace();
                	errors++;
                }
                // Check for victory
            } while (!validMove);
        }
        
        ui.handleStateUpdate(state);
//        System.out.print(state.getStatus());
        switch (state.getStatus()) {
        case PLAYER1WON:
            return player1;
        case PLAYER2WON:
            return player2;
        default:
            return null;
        }
    }

    /**
     * Logs a message to the user interface.
     */
    public void log(Object message) {
//    	System.out.println(message.toString());
        if (verbose) {
            if (message instanceof Exception && ui instanceof Logger)
                ((Logger) ui).log(message.toString(), message);
            else
                log(message.toString());
        }
    }

    /**
     * Logs a message to the user interface.
     */
    public void log(String message) {
//    	System.out.println(message);
        if (verbose) {
            if (ui instanceof Logger)
                ((Logger) ui).log(message, this);
            else
                System.err.println(message);
        }
    }

    static String getSimplifiedClassName(String className) {
        int lastPeriod = className.lastIndexOf(".");
        if (lastPeriod < 0)
            return className;
        else
            return className.substring(lastPeriod + 1);
    }

    /**
     * Get the current state of the game.
     * @return the game's state.
     */
    public GameState getState() {
        return state;
    }

    /**
     * Instantiates the players, loads the user interface, and plays the game
     * until completion.
     */
    public static void main(String[] args) {
        UserInterface ui = null;
        String[] sarg = new String[4];
        int sargs = 0;
        boolean printUse = false;
        long seed = 0;
        boolean seedSet = false;
        int turnDuration = -1;

        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("-")) {
                /**
                 * This is the class name of an agent
                 */
                if (sargs < 4)
                    sarg[sargs++] = args[i];
                else {
                    System.err.println("Warning: unexpected argument \"" + args[i] + "\"!");
                    printUse = true;
                }
            }
            else if (args[i].equals("-s")) {
                /**
                 * Set the seed to the random number generator
                 */
                if (i == args.length - 1) {
                    System.err
                            .println("Error: -s requires an argument (the number with which to seed the random number generator)");
                    printUse = true;
                }
                else {
                    seed = Long.parseLong(args[++i]);
                    seedSet = true;
                }
            }
            else if (args[i].equals("-d")) {
                /**
                 * Set the maximum turn duration
                 */
                if (i == args.length - 1) {
                    System.err
                            .println("Error: -d requires an argument (the maximum turn duration in seconds)");
                    printUse = true;
                }
                else {
                    turnDuration = Integer.parseInt(args[++i]);
                }
            }
            else if (args[i].equals("-nw")) {
                ui = new ConsoleUserInterface();
            }
            else {
                System.err.println("Warning: unexpected argument \"" + args[i] + "\"!");
                printUse = true;
            }
        }

        if (ui == null)
            ui = new GraphicalUserInterface();

        OthelloPlayer players[];
        
        if (sargs < 2) {
            players = ui.getPlayers();
        }
        else {
            players = new OthelloPlayer[2];
            String player1class = sarg[0];
            String player1name = (sargs > 2 ? sarg[1] : getSimplifiedClassName(player1class));
            if (player1name.equals(""))
                player1name = "Player 1";
            String player2class = (sargs > 2 ? sarg[2] : sarg[1]);
            String player2name = (sargs > 3 ? sarg[3] : getSimplifiedClassName(player2class));
            if (player2name.equals(player1name))
                player2name = player2name + "2";
            else if (player2name.equals(""))
                player2name = "Player 2";
            
            try {
                players[0] = instantiatePlayer(player1class, player1name);
            }
            catch (NoSuchMethodException nsme1) {
                System.err
                        .println("Error Instantiating Agent: Make sure the agent class for player 1 ("
                                + player1class
                                + ")\nhas a constructor that accepts a single string as an argument!");
                printUse = true;
            }
            catch (Exception e1) {
                System.err.println("Error Instantiating Agent: " + e1.toString());
                printUse = true;
            }
            try {
                players[1] = instantiatePlayer(player2class, player2name);
            }
            catch (NoSuchMethodException nsme2) {
                System.err
                        .println("Error Instantiating Agent: Make sure the agent class for player 2 ("
                                + player2class
                                + ")\nhas a constructor that accepts a single string as an argument!");
                printUse = true;
            }
            catch (Exception e2) {
                System.err.println("Error Instantiating Agent: " + e2.toString());
                printUse = true;
            }
        }

        if (printUse) {
            printUsage();
            System.exit(1);
        }
        
        ui.setPlayers(players[0], players[1]);
        if (ui instanceof Logger) {
            players[0].setLogger((Logger) ui);
            players[1].setLogger((Logger) ui);
        }
        
        Othello othello;
        
        if (seedSet)
            othello = new Othello(players[0], players[1], ui, seed);
        else
            othello = new Othello(players[0], players[1], ui);
        
//        othello.turnDuration = turnDuration;
        if (ui instanceof Logger)
            ((Logger) ui).log(getVersionInfo(), null);
        else
            System.out.println(getVersionInfo());
        
        OthelloPlayer winner = othello.play();
        if (winner == null)
            othello.log("It was a tie!");
        else
            othello.log("The winner was " + winner + "!");

        for (OthelloPlayer op : players) {
            if (op instanceof MiniMax) {
                MiniMax mm = (MiniMax) op;
                othello.log(op.getName() + " Stats:");
                othello.log("          Nodes: " + mm.getNodesGenerated());
                othello.log("    Evaluations: " + mm.getStaticEvaluations());
                othello.log("  Ave Branching: " + mm.getAveBranchingFactor());
                othello.log("  Eff Branching: " + mm.getEffectiveBranchingFactor());
            }
        }
    }

    static String getVersionInfo() {
        return "Othello Version " + VERSION + " " + REV_DATE + "\n"
                + "Copyright 2024, Nam Bui" + "\n" + "Coders' mental health matters"
                + "\n" + "\n";
    }

    /**
     * Prints command line usage information.
     */
    public static void printUsage() {
        System.err.println(getVersionInfo());
        System.err
                .println("Usage: othello [options] [player1class [player1name] player2class [player2name]]");
        System.err.println();
        System.err.println("  player1class      Class name of the agent for player1");
        System.err
                .println("                    (i.e. \"org.drexel.edu.cs.ai.othello.RandomOthelloPlayer\")");
        System.err.println("  player1name       The name for player1 (i.e. \"Evan's Agent\")");
        System.err.println("  player2class      Class name of the agent for player2");
        System.err.println("  player2name       The name for player2");
        System.err.println();
        System.err.println("OPTIONS:");
        System.err.println("         -s  number Seed for the simulator's random number generator.");
        System.err.println("                    If omitted, time since the epoch is used.");
        System.err.println("         -nw        Run in console mode (a GUI is used by default)");
        System.err
                .println("         -d  number Sets the amount of time (in seconds) an agent has to make");
        System.err.println("                    its decision each turn (i.e. the deadline).");
        System.err
                .println("                    A value <= 0 will result in an infinite deadline (this is");
        System.err.println("                    the default).");
    }
}
