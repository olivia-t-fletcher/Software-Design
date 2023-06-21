package nz.ac.vuw.ecs.swen225.gp24.app;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import nz.ac.vuw.ecs.swen225.gp24.domain.Cell;
import nz.ac.vuw.ecs.swen225.gp24.domain.Game;
import nz.ac.vuw.ecs.swen225.gp24.persistancy.Persistancy;
import nz.ac.vuw.ecs.swen225.gp24.recorder.Recorder;
import nz.ac.vuw.ecs.swen225.gp24.renderer.Music;
import nz.ac.vuw.ecs.swen225.gp24.renderer.Renderer;

/**
 * App is in charge of the window and orchestrating of game functionality.
 * Extends JFrame for window functionality.
 * Implements KeyListener for key events (for hotkeys and controlling character).
 *
 * @author George McLachlan
 */
public class App extends JFrame implements KeyListener {
	// Window related
	/**
	 * Preferred width of the window.
	 */
	private final int windowWidth = 1000;

	/**
	 * Preferred height of the window.
	 */
	private final int windowHeight = 500;

	/**
	 * Content pane reference.
	 */
	private Container window; // is actually the content inside the window

	// module instances
	/**
	 * renderer instance.
	 *
	 * renderer is used to display the game state and replay.
	 */
	private Renderer renderer;

	/**
	 * Recorder instance.
	 *
	 * For recording a play-through of a level.
	 */
	private Recorder recorder;

	// Simulation
	/**
	 * Is responsible for the simulation of the game.
	 * Holds all game relevant information.
	 */
	private Game gameState;

	/**
	 * Dictates whether the game or replay should continue simulating.
	 */
	private boolean paused = false; // + replay

	/**
	 * Used for delta time calculation.
	 */
	private Long lastTimeStamp;

	/**
	 * Holds current delta time (duh).
	 */
	private float currentDeltaTime = 0.0f; // keeps track of delta time in seconds

	// Set default level file names
	/**
	 * Level 1 filename constant.
	 */
	private final String LEVELONE = "level1.xml";

	/**
	 * Level 2 filename constant.
	 */
	private final String LEVELTWO = "level2.xml";

	// Game input
	/**
	 * Holds the moves that haven't been executed yet
	 *
	 * I though this was a good way of keeping the controls simple and preventing a player 'moving diagonally'
	 */
	private LinkedList<Game.Direction> movementInputBuffer = new LinkedList<Game.Direction>();

	/**
	 * Not exactly sure is this is useful here.
	 */
	private static final long serialVersionUID = 1L;

	// for switching level/menus
	/**
	 * Holds the logic that should be run to clean up no longer needed elements of the window.
	 */
	private Runnable closeLevel = () -> { };

	// for replays
	/**
	 * Holds the 'frames' that make up the replay.
	 */
	private ArrayList<Cell[][]> replayBoards;

	/**
	 * Holds the current position of the replay player.
	 */
	private int currentFrameIndex = 0;

	/**
	 * Used to determine the playback speed when watching a replay.
	 */
	private int replaySpeed = 400;

	/**
	 *
	 * Sets up the window and displays the main menu by default.
	 */
	App() {
		assert SwingUtilities.isEventDispatchThread();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		window = getContentPane();
		mainMenu();
		setVisible(true);

		// Init event listener
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				closeLevel.run();
			}
		});

		// window icon
		try {
			setIconImage(ImageIO.read(new File("img/player-moveRight.png")));
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		// Window title
		setTitle("Chips Challenge");

		setPreferredSize(new Dimension(windowWidth, windowHeight));
	}

	/**
	 * Main menu of the game.
	 *
	 * Has a background and buttons for necessary actions
	 */
	private void mainMenu() {
		// No manu bar on main menu
		if (getJMenuBar() != null) {
			removeMenuBar();
		}

		// Combine all JComponents into one container for easy deletion
		Container windowContents = new Container();
		windowContents.setLayout(null); // null layout so we can set the layout EXACTLY how we want it.
		window.add(windowContents);

		// Menu options
		int buttonWidth = 150;
		int buttonHeight = 40;

		// Start new game
		JButton buttonStartNew = new JButton("START NEW GAME");
		buttonStartNew.setBounds(50, 100, buttonWidth, buttonHeight);
		windowContents.add(buttonStartNew);

		// Load game from file
		JButton buttonLoadGame = new JButton("LOAD GAME");
		buttonLoadGame.setBounds(50, 150, buttonWidth, buttonHeight);
		windowContents.add(buttonLoadGame);

		// Load game from file
		JButton buttonLoadReplay = new JButton("LOAD REPLAY");
		buttonLoadReplay.setBounds(50, 200, buttonWidth, buttonHeight);
		windowContents.add(buttonLoadReplay);

		// How to play
		JButton buttonHowToPlay = new JButton("HOW TO PLAY");
		buttonHowToPlay.setBounds(50, 250, buttonWidth, buttonHeight);
		windowContents.add(buttonHowToPlay);

		// controls
		JButton buttonControls = new JButton("CONTROLS");
		buttonControls.setBounds(50, 300, buttonWidth, buttonHeight);
		windowContents.add(buttonControls);

		// Quit
		JButton buttonQuit = new JButton("QUIT");
		buttonQuit.setBounds(50, 350, buttonWidth, buttonHeight);
		windowContents.add(buttonQuit);

		// Set background image (Must be added last!)
		File img = new File("img/start-menu.png");
		JLabel background = new JLabel(new ImageIcon(img.getPath()));
		background.setBounds(0, 0, windowWidth, windowHeight);
		windowContents.add(background);


		// Remove previous windows contents and queue up the removal this ones
		closeLevel.run();
		closeLevel = () -> {
			remove(windowContents);
			Music.stopAllMusic();
		};

		// Set action listeners
		buttonStartNew.addActionListener(e -> {
			Music.playButtonClick();
			level(1); // use level 1
		});
		buttonLoadGame.addActionListener(e -> {
			Music.playButtonClick();
			level(0); // use file chooser
		});
		buttonLoadReplay.addActionListener(e -> {
			Music.playButtonClick();
			try {
				loadReplay();
			} catch (FileNotFoundException ex) { }
		});
		buttonHowToPlay.addActionListener(e -> {
			Music.playButtonClick();
			showHowToPlay();
		});
		buttonControls.addActionListener(e -> {
			Music.playButtonClick();
			showControls();
		});
		buttonQuit.addActionListener(e -> {
			Music.playButtonClick();
			quit();
		});

		Music.playMenuMusic();

		this.setPreferredSize(new Dimension(windowWidth, windowHeight));
		this.pack();
	}

	/**
	 * The main event of the game all runs with this.
	 * Handles the GUI, Game loop, and communication with runtime relevant modules
	 * @param desiredLevel The requested level to play, 0 is load game from file (saved game or custom level)
	 */
	public void level(final int desiredLevel) {

		// Attempt to setup level, sends you back to the menu on fail
		try {
			setupLevel(desiredLevel);
		} catch (Exception e) {
			mainMenu();
			return;
		}

		// Show the menu bar during play
		addMenuBar();

		// Combine all JComponents into one container for easy deletion
		Container windowContents = new Container();
		window.add(windowContents);

		// Game rendering on left, Numbers on right
		windowContents.setLayout(new GridLayout(1, 2));

		// Game renderer panel
		renderer = new Renderer(false);
		renderer.setGame(gameState);
		windowContents.add(renderer);

		// Game info panel
		JPanel panelGameInfo = new JPanel();
		panelGameInfo.setLayout(null); // Stacked like original game

		// Now we add the during game information on the right
		int labelWidth = 300;
		int labelHeight = 40;

		// Level number
		JLabel levelNumberTextField = new JLabel();
		levelNumberTextField.setBounds(50, 100, labelWidth, labelHeight);
		levelNumberTextField.setFont(new Font("Serif", Font.PLAIN, 25));
		levelNumberTextField.setHorizontalAlignment(JLabel.LEFT);
		panelGameInfo.add(levelNumberTextField);

		JLabel timeLeftTextField = new JLabel();
		timeLeftTextField.setBounds(50, 150, labelWidth, labelHeight);
		timeLeftTextField.setFont(new Font("Serif", Font.PLAIN, 25));
		timeLeftTextField.setHorizontalAlignment(JLabel.LEFT);
		panelGameInfo.add(timeLeftTextField);

		JLabel chipsLeftTextField = new JLabel();
		chipsLeftTextField.setBounds(50, 200, labelWidth, labelHeight);
		chipsLeftTextField.setFont(new Font("Serif", Font.PLAIN, 25));
		chipsLeftTextField.setHorizontalAlignment(JLabel.LEFT);
		panelGameInfo.add(chipsLeftTextField);

		windowContents.add(panelGameInfo);

		// Init game info panel
		panelGameInfo.repaint();

		// Set recorder and give initial state of the game
		recorder = new Recorder();
		recorder.recordHistory(gameState.getBoardStrings());

		Timer timer = new Timer(33, unused -> {
			assert SwingUtilities.isEventDispatchThread();

			// Update deltatime
			calculateDeltaTime();

			if (!paused) {
				// Checks for win or loss
				runGameEventCheck();

				// Decrement time by real time (not by tick rate)
				gameState.decrementTime(currentDeltaTime);

				// Only need to update everything except timer if a input has been made or enemy needs moving (level 2)
				if (!movementInputBuffer.isEmpty()) {
					// Give move to Game
					try {
						gameState.movePlayer(movementInputBuffer.removeFirst());

						// Give game state to Recorder
						recorder.recordHistory(gameState.getBoardStrings());

					} catch (IllegalArgumentException e) {
						System.out.println("Invalid move ignored");
					}
				}
			}

			// Update game information panel
			levelNumberTextField.setText("Level: " + gameState.getLevel());
			timeLeftTextField.setText(String.format("Time Left: %.2f", gameState.getTime()));
			chipsLeftTextField.setText("Stars Left: " + gameState.getChipsLeft());

			// Play sounds if event occurred this tick
			if (gameState.wasKeyPickedUp()) {
				Music.playPickUpKeySound();
			}
			if (gameState.wasTreasurePickedUp()) {
				Music.playTreasureSound();
			}

			// Update window. Calls subcomponents (info panel and renderer)
			repaint();
		});

		// Remove previous windows contents and queue up the removal this ones
		closeLevel.run();
		closeLevel = () -> {
			timer.stop();
			remove(windowContents);
			Music.stopAllMusic();
		};

		setPreferredSize(new Dimension(windowWidth, windowHeight));
		pack();

		// Set up key listener for game inputs
		renderer.requestFocus();
		renderer.addKeyListener(this);

		Music.startLevelMusic(gameState.getLevel());

		// Set the initial timestamp to avoid errors calculating delta time
		this.lastTimeStamp = System.currentTimeMillis();

		// Start game simulation
		timer.start();
	}

	/**
	 * Screen showing on winning the game.
	 */
	void winScreen() {
		removeMenuBar();

		// Combine all JComponents into one container for easy deletion
		Container windowContents = new Container();
		window.add(windowContents);
		windowContents.setLayout(null);

		JLabel winText = new JLabel("YOU WON");
		winText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		winText.setHorizontalTextPosition(SwingConstants.CENTER);
		winText.setBounds(50, 110, 200, 40);
		windowContents.add(winText);

		JButton replay = new JButton("REPLAY");
		replay.setHorizontalAlignment(JLabel.CENTER);
		replay.setBounds(50, 200, 150, 40);
		windowContents.add(replay);

		JButton saveReplay = new JButton("SAVE REPLAY");
		saveReplay.setBounds(50, 250, 150, 40);
		windowContents.add(saveReplay);

		JButton menu = new JButton("MAIN MENU");
		menu.setHorizontalAlignment(JLabel.CENTER);
		menu.setBounds(50, 300, 150, 40);
		windowContents.add(menu);

		// Set background image (Must be added last!)
		File img = new File("img/start-menu.png");
		JLabel background = new JLabel(new ImageIcon(img.getPath()));
		background.setBounds(0, 0, windowWidth, windowHeight);
		windowContents.add(background);

		// Remove previous windows contents and queue up the removal this ones
		closeLevel.run();
		closeLevel = () -> remove(windowContents);

		replay.addActionListener(e -> {
			Music.playButtonClick();
			level(1);
		});
		saveReplay.addActionListener(e -> {
			Music.playButtonClick();
			saveReplay();
			mainMenu();
		});
		menu.addActionListener(e -> {
			Music.playButtonClick();
			mainMenu();
		});

		this.setPreferredSize(new Dimension(windowWidth, windowHeight));
		this.pack();
	}

	/**
	 * Screen showing on losing level.
	 */
	private void loseScreen() {
		removeMenuBar();

		// Combine all JComponents into one container for easy deletion
		Container windowContents = new Container();
		window.add(windowContents);
		windowContents.setLayout(null);

		JLabel loseText = new JLabel("GAME OVER");
		loseText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		loseText.setHorizontalTextPosition(SwingConstants.CENTER);
		loseText.setBounds(50, 110, 200, 40);
		windowContents.add(loseText);

		JButton replay = new JButton("TRY AGAIN");
		replay.setBounds(50, 200, 150, 40);
		windowContents.add(replay);

		JButton saveReplay = new JButton("SAVE REPLAY");
		saveReplay.setBounds(50, 250, 150, 40);
		windowContents.add(saveReplay);

		JButton menu = new JButton("MAIN MENU");
		menu.setBounds(50, 300, 150, 40);
		windowContents.add(menu);

		// Set background image (Must be added last!)
		File img = new File("img/start-menu.png");
		JLabel background = new JLabel(new ImageIcon(img.getPath()));
		background.setBounds(0, 0, windowWidth, windowHeight);
		windowContents.add(background);

		// Remove previous windows contents and queue up the removal this ones
		closeLevel.run();
		closeLevel = () -> remove(windowContents);

		replay.addActionListener(e -> {
			Music.playButtonClick();
			level(gameState.getLevel());
		});
		saveReplay.addActionListener(e -> {
			Music.playButtonClick();
			saveReplay();
			mainMenu();
		});
		menu.addActionListener(e -> {
			Music.playButtonClick();
			mainMenu();
		});

		setPreferredSize(new Dimension(windowWidth, windowHeight));
		pack();
	}

	/**
	 * Tells the recorder module to save the replay of the current game (level)
	 * recorder is responsible for deciding filename and path (for now at least)
	 *
	 * Currently, only saves from the start of the current level
	 */
	public void saveReplay() {
		recorder.saveFile();
	}

	/**
	 * Loads a replay file and calls player if successful
	 *
	 * @throws FileNotFoundException on file chooser cancelled/closed
	 */
	public void loadReplay() throws FileNotFoundException {
		String sep = System.getProperty("file.separator");
		File saveDir = new File(System.getProperty("user.dir")+sep+"saves");

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(saveDir);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Game files", "xml");
		fc.setFileFilter(filter);

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File replayFile = fc.getSelectedFile();
			player(Recorder.loadFile(replayFile));
		} else {
			throw new FileNotFoundException("File choosing closed");
		}
	}

	/**
	 * Calls Persistancy load method which saves the current game state to resume later
	 */
	public void saveGame() {
		try {
			LocalDateTime fileDate = LocalDateTime.now();
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");

			String desiredFileName = fileDate.format(format) + ".xml";
			Persistancy.save(gameState, desiredFileName);
		}
		catch (IOException e) {
			System.out.println("Error saving game");
		}
	}


	/**
	 * Useful method for setting up initial state of level
	 *
	 * @param levelNumber requested level to play, [0, 2] range as of now.
	 *                    0 = load from file explorer.
	 *
	 * @throws IOException On file read error
	 * @throws FileNotFoundException file chooser was closed
	 */
	private void setupLevel(int levelNumber) throws IOException {
		int level = levelNumber;

		// If undefined level is requested
		if (level < 0 || level > 2) {
			System.out.println("Level not found, defaulting to level 1");
			level = 1;
		}

		try {
			File gameFile = new File(LEVELONE);
			String sep = System.getProperty("file.separator");

			File saveDir = new File(System.getProperty("user.dir") + sep + "src" + sep + "nz" + sep +
					"ac" + sep + "vuw" + sep + "ecs" + sep + "swen225" + sep + "gp24" + sep + "persistancy");

			File levelDir = new File(System.getProperty("user.dir") + sep + "src" + sep + "nz" + sep + "ac" +
					sep + "vuw" + sep + "ecs" + sep + "swen225" + sep + "gp24" + sep + "persistancy" + sep + "levels");


			// Use predefined level 1
			if (levelNumber == 1) {
				gameFile = new File(LEVELONE);
				gameState = Persistancy.load(levelDir.getPath() + sep + gameFile.getName());

			} else if (levelNumber == 2) {
				gameFile = new File(LEVELTWO);
				gameState = Persistancy.load(levelDir.getPath() + sep + gameFile.getName());

			} else if (levelNumber == 0){
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(saveDir);

				FileNameExtensionFilter filter = new FileNameExtensionFilter("Game files", "xml");
				fc.setFileFilter(filter);

				if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
					gameFile = fc.getSelectedFile();
					gameState = Persistancy.load(gameFile.getPath());

				} else {
					throw new FileNotFoundException("File choosing closed");
				}
			}
		} catch(IOException e) {
			System.out.println(e.toString());
		}

		movementInputBuffer.clear();
		setPause(false);

		// Double-check gameState was set correctly
		if (gameState == null) {
			// Don't event try to simulate if game couldn't be loaded
			throw new FileNotFoundException("Error creating initial game state");
		}
	}

	/**
	 * Checks win/loss conditions and calls relevant methods when met
	 */
	private void runGameEventCheck() {
		if (gameState.getTime() < 0 || gameState.isGameOver()) {
			movementInputBuffer.clear();
			loseScreen();
		}
		if (gameState.getChipsLeft() <= 0 && gameState.isStandingOnExit()) {
			movementInputBuffer.clear();
			if (gameState.getLevel() == 2) {
				winScreen();
			} else {
				level(gameState.getLevel() + 1);
			}
		}
	}

	/**
	 * closes the window
	 * TODO: Remember the last level played of certain quit scenarios
	 */
	private void quit() {
		dispose();
	}

	/**
	 * Shows key bindings for player in a little popup window
	 */
	private void showControls() {
		String title = "Controls";
		String contentText = "AP ARROW : Move up \n" +
				"DOWN ARROW : Move down \n" +
				"LEFT ARROW : Move left \n" +
				"RIGHT ARROW : Move right \n" +
				"SPACE : pause game \n" +
				"ESC : resume game \n" +
				"CTRL + 1 : start new game from level 1 \n" +
				"CTRL + 2 : start new game from level 2 \n" +
				"CTRL + X : quit game without saving \n" +
				"CTRL + S : save game and quit \n" +
				"CTRL + R : load a saved game from file \n";

		JOptionPane.showConfirmDialog(this, contentText, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Tells the player what to do, in a little popup window
	 */
	private void showHowToPlay() {
		String title = "How To Play";
		String contentText = "Collect all of the chips and make it to the exit before time runs out \n" +
				"Keys open doors of the same color \n" +
				"Enemies will kill you, watch out! \n";

		JOptionPane.showConfirmDialog(this, contentText, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}


	/**
	 * Delta time is useful for making simulation adjustments with unpredictable frame times
	 */
	private void calculateDeltaTime() {
		long deltaTime = System.currentTimeMillis() - this.lastTimeStamp; // Delta time in ms

		this.currentDeltaTime = deltaTime / 1000.0f; // Delta time in seconds converted to float

		this.lastTimeStamp = System.currentTimeMillis(); // Reset the last time stamp
	}

	/**
	 * Self-explanatory
	 * @param pause true = paused, false = not paused
	 */
	public void setPause(boolean pause) {
		if(this.paused == pause)
			return;

		this.paused = pause;

		// Make sure music reflects this pause
		if (this.paused)
			Music.pauseLevelMusic(gameState.getLevel());
		else
			Music.playLevelMusic(gameState.getLevel());
	}

	/**
	 * the actual replay viewing method.
	 * Has buttons for rewinding, play, pause, etc.
	 *
	 * @param replay has the list of board states in order, used like frames.
	 */
	public void player(ArrayList<String[][]> replay) {
		currentFrameIndex = 0;
		replayBoards = new ArrayList<Cell[][]>();
		for (String[][] strings : replay) {
			replayBoards.add(Persistancy.encodeStrings(strings));
		}

		addMenuBar();

		// Combine all JComponents into one container for easy deletion
		Container windowContents = new Container();
		window.add(windowContents);

		// Game rendering on left, Controls on right
		windowContents.setLayout(new GridLayout(1, 2));

		Renderer mediaRenderer = new Renderer(true);
		// Convert String[][] to Cell[][] and give to renderer
		mediaRenderer.setBounds(0, 0, this.getContentPane().getWidth(), this.getContentPane().getHeight());
		mediaRenderer.setCells(replayBoards.get(currentFrameIndex));
		windowContents.add(mediaRenderer);

		// replay info panel
		JPanel PanelControlReplay = new JPanel();
		PanelControlReplay.setLayout(null); // Stacked like original game
		windowContents.add(PanelControlReplay);

		// Standardise button sizes and alignment
		int buttonWidth = 150;
		int buttonHeight = 40;
		int xAlignment = 250 - (buttonWidth / 2);

		// Control panel label (also makes it obvious we are watching a replay and now playing)
		JLabel labelReplay = new JLabel("Watching Replay");
		labelReplay.setBounds(250 - 100, 20, 400, buttonHeight);
		labelReplay.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
		labelReplay.setHorizontalTextPosition(SwingConstants.CENTER);
		PanelControlReplay.add(labelReplay);

		// Go to start
		JButton gotoStartButton = new JButton("\u23EE");
		gotoStartButton.setBounds(xAlignment, 100, buttonWidth, buttonHeight);
		gotoStartButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
		PanelControlReplay.add(gotoStartButton);

		// Go back frame
		JButton rewindButton = new JButton("\u23EA");
		rewindButton.setBounds(xAlignment, 150, buttonWidth, buttonHeight);
		rewindButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
		PanelControlReplay.add(rewindButton);

		// Play
		JButton playButton = new JButton("\u23EF");
		playButton.setBounds(xAlignment, 200, buttonWidth, buttonHeight);
		playButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
		PanelControlReplay.add(playButton);

		// Go forward frame
		JButton skipFrame = new JButton("\u23E9");
		skipFrame.setBounds(xAlignment, 250, buttonWidth, buttonHeight);
		skipFrame.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
		PanelControlReplay.add(skipFrame);

		// Go to end
		JButton gotoEndButton = new JButton("\u23ED");
		gotoEndButton.setBounds(xAlignment, 300, buttonWidth, buttonHeight);
		gotoEndButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
		PanelControlReplay.add(gotoEndButton);

		// replay speed buttons
		JButton halfSpeed = new JButton("x0.5");
		halfSpeed.setBounds(xAlignment, 350, (buttonWidth / 3) - 10, buttonHeight);
		PanelControlReplay.add(halfSpeed);

		JButton normalSpeed = new JButton("x1");
		normalSpeed.setBounds(xAlignment + 50, 350, (buttonWidth / 3) - 10, buttonHeight);
		PanelControlReplay.add(normalSpeed);

		JButton doubleSpeed = new JButton("x1.5");
		doubleSpeed.setBounds(xAlignment + 100, 350, (buttonWidth / 3) - 10, buttonHeight);
		PanelControlReplay.add(doubleSpeed);

		// Init game info panel
		repaint();

		Timer replayTimer = new Timer(replaySpeed, unused -> {
			assert SwingUtilities.isEventDispatchThread();

			// paused also acts as a way of stepping through frames without having to fight against play speed
			if (paused) {
				mediaRenderer.setCells(replayBoards.get(currentFrameIndex));

				repaint();
			}
			// If not paused, just iterate through the frames at 'replaySpeed'
			else {
				// End of replay
				if (currentFrameIndex >= replayBoards.size())
					return;

				setReplayIndex(currentFrameIndex + 1);

				mediaRenderer.setCells(replayBoards.get(currentFrameIndex));

				// Update window. Should call all subcomponents (info panel and renderer)
				repaint();
			}
		});

		// Remove previous windows contents and queue up the removal this ones
		closeLevel.run();
		closeLevel = () -> {
			replayTimer.stop();
			remove(windowContents);
			replayBoards.clear();
			Music.stopAllMusic();
		};

		// Setup button actions
		gotoStartButton.addActionListener(e -> {
			setReplayIndex(0);
			paused = true;

			repaint();
		});
		rewindButton.addActionListener(e -> {
			setReplayIndex(currentFrameIndex - 1);
			paused = true;

			repaint();
		});
		playButton.addActionListener(e -> {
			if (paused) paused = false;
			else paused = true;

			repaint();
		});
		skipFrame.addActionListener(e -> {
			setReplayIndex(currentFrameIndex + 1);
			paused = true;

			repaint();
		});
		gotoEndButton.addActionListener(e -> {
			setReplayIndex(replayBoards.size() - 1);
			paused = true;

			repaint();
		});
		// Make delay higher for slower playback
		halfSpeed.addActionListener(e -> replayTimer.setDelay((int) Math.round(replaySpeed * 1.5)) );
		halfSpeed.addActionListener(e -> replayTimer.setDelay(replaySpeed) );
		doubleSpeed.addActionListener(e -> replayTimer.setDelay((int) Math.round(replaySpeed * 0.5)) );

		setPreferredSize(new Dimension(windowWidth, windowHeight));
		pack();
		repaint();

		// Start replaying
		replayTimer.start();
	}

	/**
	 * Sets the current frame index (which frame to display in the player)
	 * Good method for making sure frame index doesn't go out of bounds
	 *
	 * @param i desired frame index
	 */
	private void setReplayIndex(int i) {
		if (i < 0 || i >= replayBoards.size())
			return;

		currentFrameIndex = i;
	}

	/**
	 * removes the menu bar from the window when we don't want it
	 */
	private void removeMenuBar() {
		setJMenuBar(null);
	}

	/**
	 * Constructs and adds a menu bar to the window, alternative to hotkeys
	 */
	private void addMenuBar() {
		JMenuBar mb = new JMenuBar();

		// Game Manu
		JMenu MenuGame = new JMenu("Game");

		JMenuItem menuItemPause = new JMenuItem("Toggle Pause");
		menuItemPause.getAccessibleContext().setAccessibleDescription("Hotkey: space"); // MouseOver hint
		menuItemPause.addActionListener(e -> {
			setPause(!paused);
		});
		MenuGame.add(menuItemPause);

		JMenuItem menuItemRestart = new JMenuItem("Restart Level");
		menuItemRestart.addActionListener(e -> level(gameState.getLevel()));
		MenuGame.add(menuItemRestart);

		JMenuItem menuItemMainMenu = new JMenuItem("Quit To Main Menu");
		menuItemMainMenu.getAccessibleContext().setAccessibleDescription("Return to main manu"); // MouseOver hint
		menuItemMainMenu.addActionListener(e -> mainMenu());
		MenuGame.add(menuItemMainMenu);

		JMenuItem menuItemQuit = new JMenuItem("Quit Game");
		menuItemQuit.getAccessibleContext().setAccessibleDescription("Hotkey: ctrl + x"); // MouseOver hint
		menuItemQuit.addActionListener(e -> quit());
		MenuGame.add(menuItemQuit);

		mb.add(MenuGame);

		// Level Menu
		JMenu MenuLevel = new JMenu("Level");

		JMenuItem menuItemSaveGame = new JMenuItem("Save Game");
		menuItemSaveGame.getAccessibleContext().setAccessibleDescription("Hotkey: ctrl + s"); // MouseOver hint
		menuItemSaveGame.addActionListener(e -> saveGame());
		MenuLevel.add(menuItemSaveGame);


		JMenuItem menuItemLoadGame = new JMenuItem("Load Game");
		menuItemLoadGame.getAccessibleContext().setAccessibleDescription("Hotkey: ctrl + r"); // MouseOver hint
		menuItemLoadGame.addActionListener(e -> {
			closeLevel.run();
			level(0);
		});
		MenuLevel.add(menuItemLoadGame);

		JMenuItem menuItemSaveReplay = new JMenuItem("Save Level Replay");
		menuItemSaveReplay.getAccessibleContext().setAccessibleDescription("Save a replay of this level"); // MouseOver hint
		menuItemSaveReplay.addActionListener(e -> saveReplay());
		MenuLevel.add(menuItemSaveReplay);

		mb.add(MenuLevel);

		// Help Menu
		JMenu MenuHelp = new JMenu("Help");

		JMenuItem menuItemKeys = new JMenuItem("Controls");
		menuItemKeys.getAccessibleContext().setAccessibleDescription("View Controls"); // MouseOver hint
		menuItemKeys.addActionListener(e -> showControls());
		MenuHelp.add(menuItemKeys);

		JMenuItem menuItemHowToPlay = new JMenuItem("How To Play");
		menuItemHowToPlay.getAccessibleContext().setAccessibleDescription("View Controls"); // MouseOver hint
		menuItemHowToPlay.addActionListener(e -> showHowToPlay());
		MenuHelp.add(menuItemHowToPlay);

		mb.add(MenuHelp);

		setJMenuBar(mb);
	}

	// Inputs (should probably use a controller but couldn't get it to work)

	/**
	 * Adds a movement input to a queue where the main game loop can read and give to the Game object
	 *
	 * @param dir which direction we want the player to move
	 */
	public void queueMove(Game.Direction dir) {
		if (!movementInputBuffer.contains(dir)) movementInputBuffer.addLast(dir);
	}

	/**
	 * Key bindings for non-modified keys
	 */
	private java.util.List<Integer> unModifiedBoundKeys = java.util.List.of(
			KeyEvent.VK_UP,
			KeyEvent.VK_DOWN,
			KeyEvent.VK_LEFT,
			KeyEvent.VK_RIGHT,
			KeyEvent.VK_SPACE,
			KeyEvent.VK_ESCAPE
	);

	/**
	 * Key bindings for modified keys
	 */
	private java.util.List<Integer> modifiedBoundKeys = java.util.List.of(
			KeyEvent.VK_R,
			KeyEvent.VK_S,
			KeyEvent.VK_X,
			KeyEvent.VK_1,
			KeyEvent.VK_2
	);

	/**
	 * Handles key events
	 *
	 * @param e the event to be processed
	 */
	@Override
	public void keyPressed(KeyEvent e){
		assert SwingUtilities.isEventDispatchThread();

		if(modifiedBoundKeys.contains(e.getKeyCode())) {
			// Only fire if control is pressed as well
			if (e.isControlDown()) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_1: level(1);	break;
					case KeyEvent.VK_2: level(2);	break;
					case KeyEvent.VK_R: level(0);	break;
					case KeyEvent.VK_X: quit();					break;
					case KeyEvent.VK_S: saveGame(); quit();		break;
					default: break;
				}
			}
		}
		else if (unModifiedBoundKeys.contains(e.getKeyCode())){
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:      queueMove(Game.Direction.UP);      break;
				case KeyEvent.VK_DOWN:    queueMove(Game.Direction.DOWN);    break;
				case KeyEvent.VK_LEFT:	  queueMove(Game.Direction.LEFT);    break;
				case KeyEvent.VK_RIGHT:   queueMove(Game.Direction.RIGHT);   break;
				case KeyEvent.VK_SPACE:   setPause(true);                    break;
				case KeyEvent.VK_ESCAPE:  setPause(false);                   break;
				default: break;
			}

		}
	}

	// Unused method
	@Override
	public void keyReleased(KeyEvent e) {
	}

	// Unused method
	@Override
	public void keyTyped(KeyEvent e) {
	}
}
