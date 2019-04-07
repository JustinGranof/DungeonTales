/*
DungeonTales By Justin, Alex, and Jerry
1/19/2017
 */
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class DungeonTales extends JFrame {

    // [Boolean variables]
    static boolean doGravity = true;
    static boolean atInstructions = false;
    static boolean getName = false;
    // [-----------------]

    // [Timer variables]
    static Timer movement;
    static Timer jump;
    // [---------------]

    // [Image variables]
    static Image door;
    static Image knight;
    static Image knight2;
    static Image knight3;
    static Image pause;
    static Image menuBack;
    static Image spikeImage;
    static Image platformImage;
    // [---------------]

    // [Other variables]
    static Player p;
    static PausePanel pausePanel;
    static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    static Clip clip;
    static MainMenu menu;
    static DungeonTales tales;
    // Create an array of pressed keys.
    static int[] pressed = new int[1];
    // [---------------]

    // [Integer variables]
    static int count = 0;
    final static int GROUND_WIDTH = 152;
    final static int SCREEN_HEIGHT = (int) dim.getHeight();
    final static int SCREEN_WIDTH = (int) dim.getWidth();
    static final int PLAYER_WIDTH = 150;
    static final int PLAYER_HEIGHT = 125;

    // [-----------------]

    // Player object class.
    static class Player {
        // A set of variables that belong to the player.
        private int x;
        private int y;
        private String name;
        private Level currentLevel;
        private boolean isVisible;
        private boolean isPaused;
        private boolean canPause;
        private boolean canMove = true;
        private boolean isMoving = false;
        private boolean isJumping;
        private boolean isFalling;
        private boolean onPlat = false;

        // Constructor for player.
        public Player(String name) {
            // Initialize the variables.
            x = 0;
            y = 0;
            this.name = name;
            this.isVisible = false;
            this.isPaused = false;
            this.canPause = false;
            this.isJumping = false;
            this.isFalling = false;
        }

        // Method to return the player name.
        public String getName() {
            return name;
        }

        // Method to return the players X location.
        public int getX() {
            return this.x;
        }

        // Method to return the players Y location.
        public int getY() {
            return this.y;
        }

        // Method to return if the player can move.
        public boolean canMove() {
            return this.canMove;
        }

        // Method to check if the game is paused.
        public boolean isPaused() {
            return this.isPaused;
        }

        // Method to set player name
        public void setName(String name) {
            this.name = name;
        }

        // Method to get the current level.
        public Level getCurrentLevel() {
            return this.currentLevel;
        }

        // Method to set the X location of the player.
        public void setX(int x) {
            this.x = x;
        }

        // Method to set if the player can move.
        public void setCanMove(boolean canMove) {
            this.canMove = canMove;
        }

        // Method to check if the player is falling.
        public boolean isFalling() {
            return this.isFalling;
        }

        // Method to set if the player is falling.
        public void setFalling(boolean yes) {
            this.isFalling = yes;
        }

        // Method to set the Y location of the player.
        public void setY(int y) {
            this.y = y;
        }

        // Method to set the current level of the player.
        public void setCurrentLevel(Level currentLevel) {
            this.currentLevel = currentLevel;
        }

        // Method to check if the player is visible.
        public boolean isVisible() {
            return this.isVisible;
        }

        // Method to set the player visible or not.
        public void setVisible(boolean visible) {
            this.isVisible = visible;
        }

        // Method to pause or unpause the game.
        public void setPaused(boolean pause) {
            this.isPaused = pause;
        }

    }

    // Class for the moving platforms
    static class Platform {
        // Moving platform variables
        private int startX = 0;
        private int startY = 0;
        private int endX;
        private int endY;
        private int x;
        private int y;
        private int width;
        private int height;
        private Level level;
        private int id;
        private int speed;
        private boolean back;
        private boolean hasPlayer = false;

        // Constructor for moving platforms
        public Platform(int startX, int startY, int endX, int endY, int width,
                        int height, Level level, int id, int speed) {
            // Initialize all the variables
            this.startX = startX;
            this.startY = startY;
            this.speed = speed;
            this.x = startX;
            this.y = startY;
            this.endX = endX;
            this.endY = endY;
            this.width = width;
            this.height = height;
            this.level = level;
            this.back = false;
            this.level.addPlatform(id, this);
        }

        // Method to set the X location of the platform
        public void setX(int x) {
            this.x = x;
        }

        // Method to set the Y location of the platform
        public void setY(int y) {
            this.y = y;
        }

        // Method to return the X location of the platform
        public int getX() {
            return this.x;
        }

        // Method to return the Y location of the platform
        public int getY() {
            return this.y;
        }

        // Method to return the speed of the platform
        public int getSpeed() {
            return this.speed;
        }

        // Method to check if the platform has the player on it
        public boolean hasPlayer() {
            return this.hasPlayer;
        }

        // Method to set if the player is on the platform
        public void hasPlayer(boolean yes) {
            this.hasPlayer = yes;
        }

        // Method to check if the platform is on it's way back
        public boolean getBack() {
            return this.back;
        }

        // Method to set the platform to it's way back
        public void setBack(boolean back) {
            this.back = back;
        }

    }

    // Game timer class
    static class GameTimer {
        // Game timer variables
        private Timer timer;
        private int time;
        private ActionListener al;
        private Level level;

        // Constructor for game timer
        public GameTimer(Level level) {
            this.level = level;
            this.time = 0;
            // Create an action listener to increment the time variable
            al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    time++;
                }
            };
            // Create a timer to run every second
            this.timer = new Timer(1000, al);
        }

        // Method to pause the timer
        public void pauseTime() {
            this.timer.stop();
        }

        // Method to resume/start the timer
        public void resumeTime() {
            this.timer.start();
        }

        // Method to return the time
        public int getTime() {
            return this.time;
        }

        // Method to set the time
        public void setTime(int time) {
            this.time = time;
        }

        // Method to reset the time
        public void resetTime() {
            this.time = 0;
        }

    }

    // Level class
    static class Level extends JPanel {
        // Level object variables
        private int level;
        private int spawnX, spawnY, endX, endY;
        private Player p;
        private Rectangle[] platforms;
        private Platform[] movingPlats;
        private Rectangle[] spikes;
        private GameTimer gt;
        private JLabel time;
        private String[] tops = new String[3];

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw the background image
            g.drawImage(menuBack, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
            // Draw the door at the end position of the level
            g.drawImage(door, endX, endY, 187, 187, null);

            // Loop through each platform in the level (non-moving)
            for (Rectangle r : getPlatforms()) {

                // Make sure the platform isn't null
                if (r == null) {
                    return;
                }

                // Draw the platform image
                g.drawImage(platformImage, (int) r.getBounds().getMinX(),
                        (int) r.getBounds().getMinY(), (int) r.getWidth(),
                        (int) r.getHeight(), null);
            }

            // Loop through all the spikes in the level
            for (Rectangle s : getSpikes()) {
                // Draw the spike image
                g.drawImage(spikeImage, (int) s.getBounds().getMinX(), (int) s
                        .getBounds().getMinY(), (int) s.getWidth(), (int) s
                        .getHeight(), null);
            }

            // Loop through all the moving platforms in the level
            for (Platform p : getMovingPlats()) {

                // Make sure it's not null
                if (p == null) {
                    return;
                }

                // Draw the moving platform image
                g.drawImage(platformImage, (int) p.getX(), (int) p.getY(),
                        p.width, p.height, null);
            }

            // Check to see if the player is visible
            if (p.isVisible()) {
                // If the player is, then draw the player at their X & Y
                g.drawImage(knight, p.getX(), p.getY(), 150, 125, null);
            }

        }

        // Constructor for the level object
        public Level(int level, int spawnX, int spawnY, int endX, int endY,
                     final Player p, Rectangle[] platforms, int movingPlats,
                     Rectangle[] spikes) {
            // Declare all the variables
            this.level = level;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.endX = endX;
            this.endY = endY;
            this.p = p;
            this.spikes = spikes;
            // Create a new instance of a game timer for this level
            gt = new GameTimer(this);

            // Set the players X & Y to those of the levels spawn location
            p.setX(spawnX);
            p.setY(spawnY);
            // Set the player visible
            p.setVisible(true);
            this.platforms = platforms;
            this.movingPlats = new Platform[movingPlats];
            // Add the level to an array
            LevelManager.levels[level - 1] = this;

            // Set the layout to null for formatting
            setLayout(null);

            // Create a new JLabel to display the timer on the screen
            time = new JLabel("<html><b>Time: " + this.getGameTimer().getTime()
                    + "s</b></html>");
            // Set the location and bounds of the timer
            time.setBounds(SCREEN_WIDTH - 200, 0, 200, 100);

            // Add the time JLabel to the level
            add(time);

            // If the level is the tutorial
            if (level == 4) {
                // Create the tutorial messages
                // Create the moving tip.
                JLabel moveTip = new JLabel(
                        "<html><b>TIP:</b><br>Use the arrow keys<br>to navigate the level!</html>");
                // Set the location
                moveTip.setBounds(getSpawnX() + 20, getSpawnY() - 160, 300, 100);
                // Set the font colour
                moveTip.setForeground(Color.white);
                // Set the font size
                moveTip.setFont(new Font(moveTip.getFont().getName(),
                        Font.ITALIC, 20));
                // Add it to the panel
                add(moveTip);

                // Create the door tip
                JLabel doorTip = new JLabel(
                        "<html><b>TIP:</b><br>Reach these doors<br>to complete the level!</html>");
                // Set the location
                doorTip.setBounds(getEndX() + 20, getSpawnY() - 180, 300, 100);
                // Set the font colour
                doorTip.setForeground(Color.white);
                // Set the font size
                doorTip.setFont(new Font(doorTip.getFont().getName(),
                        Font.ITALIC, 20));
                // Add it to the panel
                add(doorTip);

                // Create the platform tip
                JLabel platTip = new JLabel(
                        "<html><b>TIP:</b><br>Jump on these platforms<br>to reach higher parts!</html>");
                // Set the location
                platTip.setBounds(SCREEN_WIDTH / 2 - 570, getSpawnY() - 200,
                        300, 100);
                // Set the font colour
                platTip.setForeground(Color.white);
                // Set the font size
                platTip.setFont(new Font(platTip.getFont().getName(),
                        Font.ITALIC, 20));
                // Add it to the panel
                add(platTip);

                // Create the spike tip
                JLabel spikeTip = new JLabel(
                        "<html><b>TIP:</b><br>Avoid touching these<br>spikes or you'll respawn!</html>");
                // Set the location
                spikeTip.setBounds(SCREEN_WIDTH / 2 - 270, getSpawnY() - 400,
                        300, 100);
                // Set the font colour
                spikeTip.setForeground(Color.white);
                // Set the font size
                spikeTip.setFont(new Font(spikeTip.getFont().getName(),
                        Font.ITALIC, 20));
                // Add it to the panel
                add(spikeTip);

                // Create the moving platform tip
                JLabel movingPlatTip = new JLabel(
                        "<html><b>TIP:</b><br>These platforms move, side to<br>side, and up and down.<br>use them to reach platforms!</html>");
                // Set the location
                movingPlatTip.setBounds(SCREEN_WIDTH / 2, getSpawnY() - 300,
                        300, 100);
                // Set the font colour
                movingPlatTip.setForeground(Color.white);
                // Set the size
                movingPlatTip.setFont(new Font(movingPlatTip.getFont()
                        .getName(), Font.ITALIC, 20));
                // Add it to the panel
                add(movingPlatTip);
            }

            // Create an action listener to repaint the updated time and
            // platforms
            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    // Set the text of the JLabel
                    time.setText("<html><b>Time: " + getGameTimer().getTime()
                            + "s</b></html>");
                    // Set the font colour
                    time.setForeground(Color.WHITE);
                    // Set the font size
                    time.setFont(new Font(time.getFont().getName(), Font.PLAIN,
                            27));

                    // Repaint the screen with updated time
                    repaint();
                    // Check to see if the player is paused
                    if (p.isPaused()) {
                        return;
                    }

                    // Loop through each moving platform in the level
                    for (Platform p : getMovingPlats()) {

                        // Make sure the platform is not null
                        if (p == null) {
                            return;
                        }

                        // Create an instance of the player
                        Player pl = DungeonTales.p;

                        // Check to see if the platform has a horizontal
                        // animation/movement
                        if (p.startX != p.endX) {
                            // Check to see if the current X location is equal
                            // to the end location,
                            // or if the platform is going back
                            if (p.getX() >= p.endX - p.width || p.getBack()) {
                                // Move the platform to the left
                                p.setX(p.getX() - p.getSpeed());
                                // Set the direction of the platform
                                p.setBack(true);
                                // Check to see if the platform is back at it's
                                // start location
                                if (p.getX() == p.startX) {
                                    // Set the direction again
                                    p.setBack(false);
                                }
                            } else {
                                // Otherwise move it in the right direction
                                p.setX(p.getX() + p.getSpeed());
                            }
                        }

                        // Check to see if there is animation/movement on the Y
                        // axis
                        if (p.startY != p.endY) {
                            // Check to see if the platform is at the end of
                            // it's path
                            if (p.getY() >= p.endY - p.width || p.getBack()) {
                                // Move the platform up the screen
                                p.setY(p.getY() - p.getSpeed());
                                // Set the platforms direction
                                p.setBack(true);
                                // Check if the platform has reached it's
                                // starting position
                                if (p.getY() == p.startY) {
                                    // Set the direction of the platform
                                    p.setBack(false);
                                    // Check if the platform has the player on
                                    // it
                                    if (p.hasPlayer()) {
                                        // Move the player up the screen a small
                                        // amount.
                                        // This is to prevent the player from
                                        // gradually falling
                                        // through the platform
                                        pl.setY(pl.getY() - 5);
                                    }
                                }
                            } else {
                                // Else move the platform down the screen
                                p.setY(p.getY() + p.getSpeed());
                            }
                        }

                        // Check to see if the player is on top of that platform
                        if (p.hasPlayer()) {
                            // Check if the platform has horizontal animation
                            if (p.startX != p.endX) {
                                // Check the direction of the platform
                                if (p.getBack()) {
                                    // Move the player with the platform
                                    pl.setX(pl.getX() - p.getSpeed());
                                } else {
                                    // Move the player with the platform (other
                                    // direction)
                                    pl.setX(pl.getX() + p.getSpeed());
                                }
                            }
                            // Check if the platform has verticle animation
                            if (p.startY != p.endY) {
                                // Check the direction of the platform
                                if (p.getBack()) {
                                    // Move the player with the platform
                                    pl.setY(pl.getY() - p.getSpeed());
                                } else {
                                    // Move the player with the platform (other
                                    // direction)
                                    pl.setY(pl.getY() + p.getSpeed());
                                }
                            }
                        }
                    }
                }
            };

            // Create a timer to loop the action listener, and update the
            // platforms etc
            Timer timer = new Timer(10, al);
            // Start the timer.
            timer.start();
        }

        /*
        Method to return the 3 top scores for a level
        @returns a string array of the top scores, and names.
         */
        public String[] getTopScores() throws IOException {
            // Get the leaderboards file and make sure it exists.
            File board = new File("Leaderboards.txt");
            if (!board.exists()) {
                return null;
            }

            // Create a scanner of the file.
            Scanner file = new Scanner(board);

            // Loop through the values in the file.
            while (file.hasNext()) {
                // Continue to another line
                String line = file.nextLine();
                // Check if the data is for the level we're looking for
                if (line.equalsIgnoreCase("Level " + getLevel() + " Scores")) {
                    // Add the top 3 scores to the array
                    tops[0] = file.nextLine();
                    tops[1] = file.nextLine();
                    tops[2] = file.nextLine();
                    // Close the scanner.
                    file.close();
                    // Return the array of top scores.
                    return this.tops;
                }
            }
            // Close the file in case no top scores were found
            file.close();
            // Return null meaning no scores were found.
            return null;
        }

        /*
        Method to update scores in the leaderboards file
         */
        public void updateScores() throws IOException {
            // Get the leaderboard file, and make sure it exists.
            File board = new File("Leaderboards.txt");
            if (!board.exists()) {
                return;
            }

            // Create a scanner of the file.
            Scanner file = new Scanner(board);

            // Create an array to hold the previous file, and it's values.
            String[] textFile = new String[16];
            // Loop through the files values
            while (file.hasNext()) {
                // Add the line to the array (16 times), once for each line.
                for (int i = 0; i < 16; i++) {
                    textFile[i] = file.nextLine();
                }
            }
            // Close the scanner.
            file.close();
            // Delete the previous leaderboards file
            board.delete();

            // Create a new file with the updated scores.
            File update = new File("Leaderboards.txt");
            update.createNewFile();
            // Create a print writer to print values to the file.
            PrintWriter out = new PrintWriter(update);

            // Create an index variable to locate the new scores for the level.
            int index = -1;

            // Loop through the array of the previous file
            for (int i = 0; i < 16; i++) {
                // Get the line from that file.
                String line = textFile[i];
                // Make sure it does not print for the three lines following the header
                if (i < index || i > (index + 3) || index == -1) {
                    // Check to see if the line is for this level.
                    if (line.equalsIgnoreCase("Level " + getLevel() + " Scores")) {
                        // Save the location of the line
                        index = i;
                        // Print out the header (LEVEL (LVL) SCORES
                        out.println(line);
                        // Loop through all the top scores
                        for (String score : tops) {
                            // Print out the top three scores.
                            out.println(score);
                        }
                    } else {
                        // Print out any lines we aren't looking for.
                        out.println(line);
                    }
                }
            }

            // Close the output file.
            out.close();

        }

        // Method to get the spawn X of the level
        public int getSpawnX() {
            return this.spawnX;
        }

        // Method to get the spawn Y of the level
        public int getSpawnY() {
            return this.spawnY;
        }

        // Method to get the ending X of the level
        public int getEndX() {
            return this.endX;
        }

        // Method to get the ending Y of the level
        public int getEndY() {
            return this.endY;
        }

        // Method to return the level number
        public int getLevel() {
            return this.level;
        }

        // Method to check if the level has been completed (from file)
        public boolean isCompleted() {
            // Get the file from the folder
            File save = new File("save.txt");

            // Make sure the file exists
            if (!(save.exists())) {
                System.out.println("[ERROR} File not found!");
                return false;
            }

            // Create a scanner for the file.
            Scanner input = null;
            try {
                // Get the file as a scanner
                input = new Scanner(save);
            } catch (IOException e) {
                System.out.println("[ERROR] Unable to access save.txt");
            }

            // Make sure the input got initialized
            if (input == null) {
                return false;
            }

            // Loop through each file value.
            while (input.hasNext()) {
                // Get the line in the file.
                String line = input.nextLine();
                // Get the saved value from the file
                // Check if the line is equal to the data we're looking for
                if (line.equalsIgnoreCase(Integer.toString(getLevel())
                        + "Completed:true")) {
                    // Get the value
                    String completed = getFileValue(line);
                    // Check if it's been completed
                    if (completed.equalsIgnoreCase("true")) {
                        // Close the input, and return.
                        input.close();
                        return true;
                    }
                }
            }
            // Close the input.
            input.close();
            // Return false since no data was found.
            return false;
        }

        // Method to set the level as completed
        public void setCompleted(boolean completed) {

            // Make sure the level isn't already clear
            if (isCompleted()) {
                return;
            }

            // Write to the file the data
            try {
                writeToFile(getLevel() + "Completed:true");
            } catch (IOException e) {
            }
        }

        // Method to return all the moving platforms
        public Platform[] getMovingPlats() {
            return this.movingPlats;
        }

        // Method to return all the platforms in the level
        public Rectangle[] getPlatforms() {
            return this.platforms;
        }

        // Method to return all the spikes
        public Rectangle[] getSpikes() {
            return this.spikes;
        }

        // Method to add a moving platform to the level
        public void addPlatform(int id, Platform form) {
            this.movingPlats[id - 1] = form;
        }

        // Method to return the game timer
        public GameTimer getGameTimer() {
            return this.gt;
        }

    }

    // Level manager class
    static class LevelManager {
        // Create an array of all the levels we have
        public static Level[] levels = new Level[4];

        /*
         * Method to return the level object
         *
         * @param The level you would like to search for
         *
         * @returns The level object
         */
        public static Level getLevel(int level) {
            // Loop through each level stored
            for (Level l : levels) {
                // Make sure the level isn't null
                if (l == null) {
                    continue;
                }
                // If the number of the level is equal to the parameter
                if (l.getLevel() == level) {
                    // Return the level
                    return l;
                }
            }
            // Return null, level was not found
            return null;
        }
    }

    // Constructor for our main class
    public DungeonTales() {
        // Set the size of the window to be full screen
        setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        // Disable resizing of the screen
        setResizable(false);
        // Set the title of the window
        setTitle("| Dungeon Tales |");

        try {
            // Play the menu music
            playMusicFile("MenuMusic.wav", true);
            // Load all our images & resources
            door = ImageIO.read(DungeonTales.class.getResource("Door.png"));
            knight = ImageIO.read(DungeonTales.class.getResource("Knight.png"));
            knight2 = ImageIO.read(DungeonTales.class.getResource("Knight2.png"));
            knight3 = ImageIO.read(DungeonTales.class.getResource("Knight.png"));
            menuBack = ImageIO.read(DungeonTales.class.getResource("menuBack.jpg"));
            spikeImage = ImageIO.read(DungeonTales.class.getResource("spikes.png"));
            platformImage = ImageIO.read(DungeonTales.class.getResource("platformWall.jpg"));
        } catch (IOException e) {
        } catch (LineUnavailableException e) {
        } catch (UnsupportedAudioFileException e) {
        }

        // Set the menu pane.
        menu = new MainMenu();
        // Create a pause panel instance
        pausePanel = new PausePanel();
        // Add the menu to the window
        add(menu);

        // Add the key listener to the window
        addKeyListener(kl);
        // Request focus to allow listeners to work
        this.requestFocusInWindow();
        // Set this window to be the focused one
        this.setFocusable(true);

        // Set the default close operation to avoid multiple windows
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set the window to visible
        setVisible(true);

        // Timer to control movement
        movement = new Timer(8, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // If the player can not move, disable movement.
                if (!(p.canMove())) {
                    return;
                }

                // If the player has paused the game, disable movement.
                if (p.isPaused()) {
                    return;
                }

                // If no keys are being pressed, do not move the player.
                if (pressed[0] == 0) {
                    return;
                }

                // Get the key that is currently being pressed
                int key = pressed[0];

                // Check to see if the player is attempting to move
                if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_LEFT) {
                    // If the player is trying to go off the screen, move them
                    // back.
                    if (p.getX() > SCREEN_WIDTH - 140) {
                        p.setX(p.getX() - 4);
                    }
                    // Player tries to go off the left of the screen, move
                    // player back.
                    if (p.getX() < 10) {
                        p.setX(p.getX() + 4);
                    }
                }

                // Make sure the current level is not null
                if (p.getCurrentLevel() == null) {
                    return;
                }

                // Door win collision

                // Create a rectangle around the door
                Rectangle door = new Rectangle(
                        p.getCurrentLevel().getEndX() + 90, p.getCurrentLevel()
                        .getEndY(), 187 - 100, 187);
                // Create a rectangle on the middle of the player
                Rectangle middlePlayer = new Rectangle(p.getX() + PLAYER_WIDTH
                        / 2, p.getY(), 1, 1);

                // Check if the players rectangle is intersecting the doors
                if (middlePlayer.intersects(door)) {
                    // Pause the current game timer
                    p.getCurrentLevel().getGameTimer().pauseTime();
                    // Put name in leaderboard
                    // Create an array to hold the top players from the current level.
                    String[] tops = null;
                    try {
                        // Get the top scores.
                        tops = p.getCurrentLevel().getTopScores();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    // Make sure that the top scores aren't null.
                    if (tops == null) {
                        return;
                    }

                    // Get the time it took for the player to beat the level.
                    int time = p.getCurrentLevel().getGameTimer().getTime();
                    // Create a variable to control searching
                    boolean found = false;
                    // Loop through each value of the scores
                    for (int i = 0; i < tops.length; i++) {
                        // Check to make sure that the spot has not been found yet.
                        if (!found) {
                            // Get the string with the score
                            String line = tops[i];

                            // If the line is equal to empty, fill the spot.
                            if (line.equalsIgnoreCase("empty")) {
                                // Set that spot to be the players score.
                                tops[i] = p.getName() + ":" + time;
                                // Found is true now that we've put the player in
                                found = true;
                            } else {
                                // Get the time value from the string
                                String timeString = getFileValue(line);
                                // Convert the time string to an integer
                                int topTime = Integer
                                        .parseInt(timeString);

                                // Check if the player is already on the board
                                String name = line.substring(0, line.indexOf(":"));
                                if (name.equalsIgnoreCase(p.getName())) {
                                    // Player is on the board already
                                    // If the players time is better than their previous
                                    if (time < topTime) {
                                        // Put the player into that position with new time
                                        tops[i] = p.getName() + ":" + time;
                                        // found position
                                        found = true;
                                    } else {
                                        // Break from the loop
                                        i = tops.length;
                                    }
                                }

                                // Make sure that the players name hasn't been repeated
                                if (!found) {
                                    // Compare the finishing time with those of the leaderboard
                                    if (time < topTime) {
                                        // If the position is not the last one, move other values down.
                                        if (i != 2) {
                                            // If the position is the first slot
                                            if (i == 0) {
                                                // Move the 2 other values down in the array.
                                                String num1 = tops[i];
                                                String num2 = tops[i + 1];
                                                tops[i + 1] = num1;
                                                tops[i + 2] = num2;
                                            } else {
                                                // Else if the position is the second slot, move the 1 other value down.
                                                String num2 = tops[i];
                                                tops[i + 1] = num2;
                                            }
                                        }
                                        // Put the player and their time into the index.
                                        tops[i] = p.getName() + ":" + time;
                                        // Set found to true
                                        found = true;
                                    }
                                }
                            }
                        }
                    }

                    try {
                        // Update the file with the new scores.
                        p.getCurrentLevel().updateScores();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    // If it's the tutorial, display the tutorial completion
                    // text.
                    if (p.getCurrentLevel().getLevel() == 4) {
                        JOptionPane.showMessageDialog(p.getCurrentLevel(),
                                "You've completed the tutorial in "
                                        + p.getCurrentLevel().getGameTimer()
                                        .getTime() + " seconds!");
                    } else {
                        // Else show the normal completion text
                        JOptionPane.showMessageDialog(p.getCurrentLevel(),
                                "You've completed level "
                                        + p.getCurrentLevel().getLevel()
                                        + " in "
                                        + p.getCurrentLevel().getGameTimer()
                                        .getTime() + " seconds!");
                    }
                    // Disable player movement
                    p.setCanMove(false);
                    // Remove the current level panel
                    tales.remove(p.getCurrentLevel());
                    // Reset the game timer for that level
                    p.getCurrentLevel().getGameTimer().resetTime();
                    // Set the level to completed
                    p.getCurrentLevel().setCompleted(true);
                    // Set the window to display main menu
                    tales.setContentPane(new MainMenu());
                    // Disable pausing
                    p.canPause = false;
                    // Refresh the window
                    tales.validate();
                    // Remove any pressed keys (stopping any current movement)
                    pressed[0] = 0;
                    try {
                        // Stop the previous music, and play the menu music.
                        stopMusicFile();
                        playMusicFile("MenuMusic.wav", true);
                    } catch (IOException ee) {
                    } catch (LineUnavailableException ee) {
                    } catch (UnsupportedAudioFileException ee) {
                    }
                }

                // Check if the player is pressing left arrow key
                if (pressed[0] == KeyEvent.VK_LEFT) {
                    // Move the character to the left
                    p.setX(p.getX() - 4);
                } else if (pressed[0] == KeyEvent.VK_RIGHT) {
                    // Else if the player is pressing right arrow key
                    // Move player to the right
                    p.setX(p.getX() + 4);
                }

            }
        });

        // Timer to catch any collision
        Timer collide = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a rectangle at the player. (for the right)
                Rectangle rightPlayer = new Rectangle(p.getX() + 90 / 2, p
                        .getY(), PLAYER_WIDTH - 80, PLAYER_HEIGHT - 20);

                // Create a rectangle at the player. (for the left)
                Rectangle leftPlayer = new Rectangle(p.getX() + 40, p.getY(),
                        PLAYER_WIDTH / 2 - 30, PLAYER_HEIGHT - 20);

                // Create a rectangle at the player.
                Rectangle player = new Rectangle(p.getX() + PLAYER_WIDTH / 2
                        - 20, p.getY() + PLAYER_HEIGHT - 5,
                        PLAYER_WIDTH / 2 - 30, 1);

                // Make sure the current level isn't null
                if (p.getCurrentLevel() == null) {
                    return;
                }

                // Loop through each platform on the current level
                for (Rectangle r : p.getCurrentLevel().getPlatforms()) {
                    // Check if the player is colliding on the right
                    if (r.intersects(rightPlayer)) {
                        // If so, move the player to the left to counter act the
                        // movement.
                        p.setX(p.getX() - 4);
                        return;
                    } else if (r.intersects(leftPlayer)) {
                        // Move the player to the right due to collision
                        // detecting on the left.
                        p.setX(p.getX() + 4);
                        return;
                    }
                }

                // Loop through all spikes on the level
                for (Rectangle spike : p.getCurrentLevel().getSpikes()) {
                    // Create a more centered rectangle around each spike
                    Rectangle newSpike = new Rectangle((int) spike.getX() + 50,
                            (int) spike.getY(), spike.width - 95, spike.height);

                    // Check if the spike is colliding with any part of the
                    // player
                    if (newSpike.intersects(player)
                            || newSpike.intersects(leftPlayer)
                            || newSpike.intersects(rightPlayer)) {
                        // Respawn the player to spawn of the level
                        p.setX(p.getCurrentLevel().getSpawnX());
                        p.setY(p.getCurrentLevel().getSpawnY());
                    }
                }
            }
        });
        // Begin the timer
        collide.start();

        // Create a new loop to run gravity.
        Timer gravity = new Timer(5, new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // If the boolean is true, allow gravity to be applied.
                if (doGravity) {

                    // Only apply gravity when the player is within a level
                    // which has loaded properly.
                    if (p.getCurrentLevel() == null) {
                        return;
                    }

                    if (p.isPaused) {
                        return;
                    }

                    // Create a rectangle at the player.
                    Rectangle player = new Rectangle(p.getX() + PLAYER_WIDTH
                            / 2 - 40, p.getY() + PLAYER_HEIGHT - 5,
                            PLAYER_WIDTH / 2 - 30, 1);

                    // Test code to draw the location of rectangle.
                    // p.getCurrentLevel().getGraphics().setColor(Color.red);

					/*
                     * p.getCurrentLevel().getGraphics().fillRect(p.getX() +
					 * PLAYER_WIDTH/2 - 40, p.getY() + PLAYER_HEIGHT - 5,
					 * PLAYER_WIDTH/2 - 30, 1);
					 */

                    // Loop through all the platforms on the level, and compare
                    // their locations with the player.
                    for (Rectangle plat : p.getCurrentLevel().getPlatforms()) {
                        Rectangle newPlat = new Rectangle((int) plat.getX(),
                                (int) plat.getY(), plat.width, 8);
                        if (newPlat.intersects(player)) {

                            // Make sure the player is falling
                            if (!p.isFalling) {
                                return;
                            }

                            // Player is ontop of a platform, so gravity is not
                            // applied.
                            p.setFalling(false);
                            return;
                        }
                    }

                    // Moving platform collision
                    for (Platform plat : p.getCurrentLevel().getMovingPlats()) {

                        // Make sure that the platform is not null.
                        if (plat == null) {
                            return;
                        }

                        // Create a rectangle out of the platform object.
                        Rectangle r = new Rectangle(plat.getX(), plat.getY(),
                                plat.width, 8);

                        // Check if the players rectangle is intersecting with a
                        // platform.
                        if (r.intersects(player)) {
                            // If they are intersecting, then add the player to
                            // that platform.
                            // This is done for moving platforms to allow the
                            // player to stay on the platform as it moves.
                            plat.hasPlayer(true);
                            // Return to stop gravity from applying.
                            p.setFalling(false);
                            return;
                        } else {
                            // Else the player is no longer on a platform.
                            // Check to see if the platform that we were
                            // checking had the player on top of it.
                            if (plat.hasPlayer()) {
                                // The player was on this platform, so remove
                                // the player from that platform.
                                plat.hasPlayer(false);
                            }
                        }
                    }

                    // Main gravity control. Sends player to the lowest possible
                    // point on the level (Assuming no platform is found)
                    if (p.getY() < SCREEN_HEIGHT - GROUND_WIDTH) {
                        // Make the player fall.
                        p.setFalling(true);
                        p.setY(p.getY() + 6);
                    } else {
                        p.setFalling(false);
                    }
                }
            }
        });
        // Start the gravity timer.
        gravity.start();
    }

    /*
     * Method to play a music file.
     *
     * @param The file of music you would like to play.
     *
     * @param Boolean for whether or not you would like to loop it.
     *
     * @returns Nothing, a void method.
     */
    public static void playMusicFile(String file, boolean loop)
            throws IOException, LineUnavailableException,
            UnsupportedAudioFileException {
        // Create a new file using the filename parameter.
        URL sound = DungeonTales.class.getResource(file);

        // Make sure the file exists.
        if (sound == null) {
            System.out.println("[ERROR] Sound file not found.");
            return;
        }

        // Play the audio file.
        AudioInputStream stream = AudioSystem.getAudioInputStream(sound);
        clip = AudioSystem.getClip();
        // Open the audio stream.
        clip.open(stream);
        // If loop boolean is true, then loop the sound file.
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.loop(1);
        }

    }

    /*
     * Method to stop any music file currently playing.
     */
    public static void stopMusicFile() throws LineUnavailableException {
        // If no music is being played, return.
        if (clip == null) {
            return;
        }
        // Stop the music, and close the clip.
        clip.stop();
        clip.close();
    }

    /*
     * Method used to easily obtain stored values from the save file.
     *
     * @param The string you would like to get the value of.
     *
     * @returns The value as a string.
     */
    public static String getFileValue(String path) {
        // Get the location of the value separator ":".
        int index = path.indexOf(":");

        String value = "";

        // If there isn't a separator, return null.
        if (index == -1) {
            // Hint is not found.
            System.out.println("[ERROR] No category found!");
            return null;
        }

        // Get the value using substring from the separator point.
        value = path.substring(index + 1);

        // Trim any excess spaces.
        value.trim();

        // Return the value.
        return value;
    }

    // Create a new key listener to listen for key events.
    static KeyListener kl = new KeyListener() {

        public void keyTyped(KeyEvent arg0) {

        }

        public void keyReleased(KeyEvent e) {
            // Get the key that was released.
            int key = e.getKeyCode();

            // if the player hasn't even moved, then return.
            if (movement == null) {
                return;
            }

            // If the key that is released was the left arrow remove it from the
            // array.
            if (key == KeyEvent.VK_LEFT) {
                pressed[0] = 0;
                // Stop the movement timer.
                movement.stop();
            } else if (key == KeyEvent.VK_RIGHT) {
                // Reset the array.
                pressed[0] = 0;
                // Stop the movement timer.
                movement.stop();
            }
        }

        public void keyPressed(KeyEvent e) {
            // Get the key that was pressed.
            int key = e.getKeyCode();

            // If the player pressed escape, pause the game.
            if (key == KeyEvent.VK_ESCAPE) {
                if (atInstructions) {
                    atInstructions = false;
                    tales.setContentPane(menu);
                    tales.validate();

                    return;
                }
                // Make sure that the player is on a level, so they cannot pause
                // at the menu screen.
                if (!p.canPause) {
                    return;
                }

                // Check to see if the game is paused already.
                if (p.isPaused()) {
                    // The game is already paused, so remove the paused panel.
                    tales.remove(pausePanel);
                    // Set the main panel to the current level.
                    tales.setContentPane(p.getCurrentLevel());
                    // Reset the screen.
                    tales.validate();
                    // Set paused to false - resume all game functions (movement
                    // etc.)
                    p.setPaused(false);
                    // Resume the game timer
                    p.getCurrentLevel().getGameTimer().resumeTime();
                    // if the level is the tutorial level, reappear all the
                    // JLabels.
                    for (Component c : p.getCurrentLevel().getComponents()) {
                        c.setVisible(true);
                    }
                    // Set the tutorial layout to absolute for JLabel
                    // positioning.
                    p.getCurrentLevel().setLayout(null);
                    // Restart playing the in game music file.
                    try {
                        stopMusicFile();
                        playMusicFile("NonBoss.wav", true);
                    } catch (IOException ee) {
                    } catch (LineUnavailableException ee) {
                    } catch (UnsupportedAudioFileException ee) {
                    }
                    return;
                }
                // The game is not paused yet, so stop the music file.
                try {
                    stopMusicFile();
                } catch (LineUnavailableException ee) {
                }
                // Check if the level being paused is the tutorial.
                // Create a level variable.
                Level tutorial = p.getCurrentLevel();

                // Hide all the components on the level (JLabels).
                for (Component c : tutorial.getComponents()) {
                    c.setVisible(false);
                }
                // Add a layout to the screen for the pause menu.
                tutorial.setLayout(new FlowLayout());
                // Set paused to true.
                p.setPaused(true);
                // Pause the timer
                p.getCurrentLevel().getGameTimer().pauseTime();
                // Add the pause panel.
                tales.add(pausePanel);
                // Repaint the current level to apply the new changes.
                p.getCurrentLevel().repaint();
                // Refresh the pane.
                tales.validate();
            }

            // If the game is paused, then block all movement.
            if (p.isPaused()) {
                return;
            }

            // If the player is attempting to leave the screen send them back.
            if (p.getX() > SCREEN_WIDTH - 140) {
                p.setX(p.getX() - 7);
            }
            if (p.getX() < 10) {
                p.setX(p.getX() + 7);
            }

            // If the movement is null there was a problem so return.
            if (movement == null) {
                return;
            }

            // Check to see if no keys are being pressed.
            if (pressed[0] == 0) {
                // If the right arrow was pressed, add the key to the array.
                if (key == KeyEvent.VK_RIGHT) {
                    pressed[0] = KeyEvent.VK_RIGHT;
                    // Start the movement timer.
                    movement.start();
                    // Change the picture to move right.
                    knight = knight3;
                }
                // If the left key was pressed, add it to the array.
                if (key == KeyEvent.VK_LEFT) {
                    pressed[0] = KeyEvent.VK_LEFT;
                    // Start the movement timer.
                    movement.start();
                    // Change the image to the left.
                    knight = knight2;
                }
            }

            if (key == KeyEvent.VK_SPACE) {
                // Player wishes to jump

                if (p.isJumping || p.isFalling) {
                    return;
                }

                // Timer for creating the jump animation.
                jump = new Timer(5, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        if (p.isPaused) {
                            return;
                        }

                        // Create a rectangle at the player. (for the right)
                        Rectangle rightPlayer = new Rectangle(
                                p.getX() + 90 / 2, p.getY(), PLAYER_WIDTH - 80,
                                PLAYER_HEIGHT - 20);

                        // Create a rectangle at the player. (for the left)
                        Rectangle leftPlayer = new Rectangle(p.getX() + 40, p
                                .getY(), PLAYER_WIDTH / 2, PLAYER_HEIGHT - 20);

                        for (Rectangle r : p.getCurrentLevel().getPlatforms()) {
                            if (r.intersects(rightPlayer)) {
                                p.setX(p.getX() - 2);
                                count = 100;
                            } else if (r.intersects(leftPlayer)) {
                                p.setX(p.getX() + 2);
                                count = 100;
                            }
                        }

                        for (Platform plat : p.getCurrentLevel()
                                .getMovingPlats()) {
                            plat.hasPlayer(false);
                        }

                        // Make the player move up by 4 pixels each time the
                        // timer runs.
                        p.setY(p.getY() - 4);
                        // Set the player to be jumping
                        p.isJumping = true;
                        // Disable gravity to allow the player to move upwards.
                        doGravity = false;
                        // Add to the counter, eventually causing the jump to
                        // stop.
                        count += 2;
                        // Once the counter has reached 100, stop the jumping,
                        // and begin falling.
                        if (count >= 100) {
                            // Make sure the timer isn't null.
                            if (jump != null) {
                                // The player is no longer jumping
                                p.isJumping = false;
                                // Enable gravity
                                doGravity = true;
                                // Reset the counter to allow more jumps.
                                count = 0;
                                // Stop the current timer.
                                jump.stop();
                            }
                        }
                    }
                });
                // Start the jump timer.
                jump.start();
            }

        }
    };

    // Instructions panel class.
    class Instructions extends JPanel {

        // Method to paint components on the panel.
        protected void paintComponent(Graphics g) {
            super.paintComponents(g);
            // Paint the background image on the panel.
            g.drawImage(menuBack, 0, 0, null);
        }

        // Constructor for instructions class.
        public Instructions() {
            // Create a layout for the pause panel.
            GridBagLayout layout = new GridBagLayout();
            // Set the layout of the panel to previously created layout.
            setLayout(layout);
            // Get the grid bag constraints from the layout.
            GridBagConstraints gc = layout.getConstraints(this);
            // Set the size of the panel to cover the whole screen.
            setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
            // Repaint the screen
            repaint();

            addKeyListener(kl);

            // Create instruction JLabels.
            // Header label for instructions panel.
            JLabel header = new JLabel("<html>INSTRUCTIONS</html>");
            // Set the font to white.
            header.setForeground(Color.WHITE);
            // Set the font to bold, and size to 30.
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 30));
            // Position the header.
            gc.gridy = 0;
            gc.insets = new Insets(0, 0, 15, 0);
            // Add the header to the panel.
            add(header, gc);

            // Jlabel for instructions.
            JLabel movement = new JLabel(
                    "<html>1. Use the <i>arrow keys</i> to navigate the level."
                            + "<br>2. Use the <i>space bar</i> to jump.<br>3."
                            + " Hitting <i>spikes</i> will result in restarting the level.<br>4. Reach "
                            + "the door to complete the level.<br>5. Some platforms <i>move</i>. They can "
                            + "carry you past obstacles.<br>6. Press <i>escape</i> to pause the game at any time<br><br><center>Press <i>ESC</i> to resume the game.</center></html>");
            // Set the font size to 25.
            movement.setFont(new Font(header.getFont().getName(), Font.PLAIN,
                    25));
            // Set the colour to white.
            movement.setForeground(Color.white);
            // Position the label under the header.
            gc.gridy = 1;
            // Add the label to the panel.
            add(movement, gc);

        }

    }

    // Pause panel class.
    class PausePanel extends JPanel {

        // Create an action listener to listen for buttons.
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Make sure the event is being fired due to a JButton being
                // pressed.
                if (!(e.getSource() instanceof JButton)) {
                    return;
                }

                // Get the source of the event, cast it to a JButton.
                JButton button = (JButton) e.getSource();

                // Reset the colour of the button for the next pause panel.
                button.setForeground(Color.white);

                // TODO implement instructions.

                // If the player presses resume game button
                if (button.getText().equalsIgnoreCase("Resume Game")) {
                    // Remove the pause panel.
                    tales.remove(pausePanel);
                    // Reset the screen to display the current level.
                    tales.setContentPane(p.getCurrentLevel());
                    // Refresh the JFrame.
                    tales.validate();
                    // Set the game to be no longer paused.
                    p.setPaused(false);
                    // Resume the game timer.
                    p.getCurrentLevel().getGameTimer().resumeTime();
                    // Make all components on the level visible again.
                    for (Component c : p.getCurrentLevel().getComponents()) {
                        c.setVisible(true);
                    }
                    // Set the layout to absolute for component positioning.
                    p.getCurrentLevel().setLayout(null);
                    // Begin the music once again.
                    try {
                        stopMusicFile();
                        playMusicFile("NonBoss.wav", true);
                    } catch (IOException ee) {
                    } catch (LineUnavailableException ee) {
                    } catch (UnsupportedAudioFileException ee) {
                    }
                    return;
                    // Else if the player presses the quit game button.
                } else if (button.getText().equalsIgnoreCase("Quit Game")) {
                    quitGame();
                    // If the user presses the menu button
                } else if (button.getText().equalsIgnoreCase("Return To Menu")) {
                    // Remove the pause panel.
                    tales.remove(pausePanel);
                    // Stop other music, and play menu music.
                    try {
                        stopMusicFile();
                        playMusicFile("MenuMusic.wav", true);
                    } catch (IOException ee) {
                    } catch (LineUnavailableException ee) {
                    } catch (UnsupportedAudioFileException ee) {
                    }
                    // Set the game to be no longer paused.
                    p.setPaused(false);
                    // Disable pausing
                    p.canPause = false;
                    // Reset the time of the current level they were on.
                    p.getCurrentLevel().getGameTimer().resetTime();
                    // Set the JFrame to display the main menu.
                    tales.setContentPane(new MainMenu());
                    // Remove the player from any platforms
                    for (Platform plat : p.getCurrentLevel().getMovingPlats()) {
                        if (plat.hasPlayer()) {
                            plat.hasPlayer = false;
                        }
                    }
                    // Set the current level to be null.
                    p.setCurrentLevel(null);
                    // Set menuscreen visible
                    menu.setVisible(true);
                    // Refresh the JFrame.
                    tales.validate();
                } else if (button.getText().equalsIgnoreCase("Instructions")) {
                    // User wants to view the instructions.
                    tales.remove(pausePanel);
                    tales.setContentPane(new Instructions());
                    tales.validate();
                }

            }
        };

        // Constructor for the pause panel class.
        public PausePanel() {
            // Create a layout for the pause panel.
            GridBagLayout layout = new GridBagLayout();
            // Set the layout of the panel to previously created layout.
            setLayout(layout);
            // Get the grid bag constraints from the layout.
            GridBagConstraints gc = layout.getConstraints(this);
            // Set the background colour to be a transparent dark colour.
            setBackground(new Color(0, 0, 0, 195));
            // Set the size of the panel to cover the whole screen.
            setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

            // Get the main logo image.
            ImageIcon logo = new ImageIcon("Logo.png");
            // Create a JLabel to hold the logo.
            JLabel label3 = new JLabel(logo);
            // Set the size of the JLabel.
            label3.setPreferredSize(new Dimension(1000, 800));
            // Set the location of the image.
            label3.setLocation(SCREEN_WIDTH / 2 - 1000, SCREEN_HEIGHT / 2);
            // Position the JLabel.
            gc.gridx = 0;
            gc.gridy = 0;
            // Give room below the JLabel.
            gc.insets = new Insets(0, 0, 20, 0);
            // Add the JLabel to the pause panel.
            add(label3, gc);

            // Create the JLabel for the Paused Game text.
            JLabel title = new JLabel("Paused Game");
            // Set the font size, and make the text bold.
            title.setFont(new Font(title.getFont().getName(), Font.BOLD, 42));
            // Make the text colour white.
            title.setForeground(Color.white);
            // Position the JLabel.
            gc.gridx = 0;
            gc.gridy = 1;
            // Add the JLabel to pause panel.
            add(title, gc);

            // Create a JButton for resume game.
            JButton resume = new JButton("Resume Game");
            // Set the font size.
            resume.setFont(new Font(resume.getFont().getName(), Font.PLAIN,
                    title.getFont().getSize() - 18));
            // Format the button to make it look nicer.
            resume.setBorderPainted(false);
            resume.setFocusable(false);
            resume.setContentAreaFilled(false);
            // Set the text colour to white.
            resume.setForeground(Color.white);
            // Position the JButton.
            gc.gridy = 2;
            // Add the mouse and action listener.
            resume.addActionListener(listener);
            resume.addMouseListener(ml);
            // Add the JButton to the panel.
            add(resume, gc);

            // Create a JButton for the instructions button.
            JButton instructions = new JButton("Instructions");
            // Set the font size of the button.
            instructions.setFont(new Font(instructions.getFont().getName(),
                    Font.PLAIN, title.getFont().getSize() - 18));
            // Format the button.
            instructions.setBorderPainted(false);
            instructions.setFocusable(false);
            instructions.setContentAreaFilled(false);
            // Set the text colour to white.
            instructions.setForeground(Color.white);
            // Position the button.
            gc.gridy = 3;
            // Add listeners to the button
            instructions.addActionListener(listener);
            instructions.addMouseListener(ml);
            // Add the button to the panel.
            add(instructions, gc);

            // Create return to menu button.
            JButton menu = new JButton("Return To Menu");
            // Set the font of the menu button.
            menu.setFont(new Font(menu.getFont().getName(), Font.PLAIN, title
                    .getFont().getSize() - 18));
            // Format the button.
            menu.setBorderPainted(false);
            menu.setFocusable(false);
            menu.setContentAreaFilled(false);
            // Set the text to be white.
            menu.setForeground(Color.white);
            // Add the listeners, and position the button.
            menu.addActionListener(listener);
            menu.addMouseListener(ml);
            gc.gridy = 4;
            // Add the button to the pause panel.
            add(menu, gc);

            // Create JButton to quit the game.
            JButton quit = new JButton("Quit Game");
            // Set the font size of the button.
            quit.setFont(new Font(quit.getFont().getName(), Font.PLAIN, title
                    .getFont().getSize() - 18));
            // Format the button.
            quit.setBorderPainted(false);
            quit.setFocusable(false);
            quit.setContentAreaFilled(false);
            // Set the text to white.
            quit.setForeground(Color.white);
            // Add the listeners, and position the button.
            quit.addActionListener(listener);
            quit.addMouseListener(ml);
            gc.gridy = 5;
            // Add the button to the JPanel.
            add(quit, gc);

        }

    }

    // Create a mouse listener to catch the hover event.
    static MouseListener ml = new MouseListener() {

        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof JButton) {
                JButton button = (JButton) e.getSource();
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        // When the mouse enters the JButton.
        public void mouseEntered(MouseEvent e) {
            // Make sure they are hovering over a button.
            if (!(e.getSource() instanceof JButton)) {
                return;
            }

            // Cast the source to a Jbutton.
            JButton button = (JButton) e.getSource();

            // If the button is the quit button, make it red on hover.
            if (button.getText().equalsIgnoreCase("Quit")) {
                button.setForeground(Color.red);
                return;
            } else if (button.getText().indexOf("Level") != -1) {
                // Get all the levels into variables.
                Level tutorial = LevelManager.getLevel(4);
                Level one = LevelManager.getLevel(1);
                Level two = LevelManager.getLevel(2);

                // Check to see if the level has been unlocked
                // If not, then return to prevent colour changing.
                if (button.getText().indexOf("1") != -1) {
                    if (!tutorial.isCompleted()) {
                        return;
                    }
                } else if (button.getText().indexOf("2") != -1) {
                    if (!one.isCompleted()) {
                        return;
                    }
                } else if (button.getText().indexOf("3") != -1) {
                    if (!two.isCompleted()) {
                        return;
                    }
                }

            }

            // Set the colour of the button to green.
            button.setForeground(Color.green);

        }

        // When the mouse no longer hovers over the button.
        public void mouseExited(MouseEvent e) {
            // Make sure they are leaving a button.
            if (!(e.getSource() instanceof JButton)) {
                return;
            }

            // Cast the source to a Jbutton.
            JButton button = (JButton) e.getSource();

            // Check to see if they are exiting a level or tutorial button.
            if (button.getText().indexOf("Level") != -1
                    || button.getText().equalsIgnoreCase("Tutorial")) {
                // Get all the levels into variables
                Level tutorial = LevelManager.getLevel(4);
                Level one = LevelManager.getLevel(1);
                Level two = LevelManager.getLevel(2);
                Level three = LevelManager.getLevel(3);

                // Check if it's level 1.
                if (button.getText().indexOf("1") != -1) {
                    // Set the colour of the button based on if it's unlocked/completed.
                    if (!tutorial.isCompleted()) {
                        button.setForeground(Color.red);
                        return;
                    } else if (one.isCompleted()) {
                        button.setForeground(Color.green);
                    } else {
                        button.setForeground(Color.white);
                    }
                } else if (button.getText().indexOf("2") != -1) {
                    // Set the colour of the level two button based on if it's unlocked/completed
                    if (!one.isCompleted()) {
                        button.setForeground(Color.red);
                        return;
                    } else if (two.isCompleted()) {
                        button.setForeground(Color.green);
                    } else {
                        button.setForeground(Color.white);
                    }
                } else if (button.getText().indexOf("3") != -1) {
                    // Set the colour of the level 3 button based on if it's unlocked/completed
                    if (!two.isCompleted()) {
                        button.setForeground(Color.red);
                        return;
                    } else if (three.isCompleted()) {
                        button.setForeground(Color.green);
                    } else {
                        button.setForeground(Color.white);
                    }
                }

                // Check if the button is the tutorial button.
                if (button.getText().equalsIgnoreCase("Tutorial")) {
                    // If the tutorial is not completed, set it to white since it is always unlocked.
                    if (!tutorial.isCompleted()) {
                        button.setForeground(Color.white);
                    }
                }
            } else {
                // Set the colour of the button to white.
                button.setForeground(Color.white);
            }

        }
    };

    // Leaderboards panel
    class Leaderboards extends JPanel {

        // Create a paint component to draw the background image.
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw background image
            g.drawImage(menuBack, 0, 0, null);
        }

        // Create an action listener to catch button presses.
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Make sure the source is from a button.
                if (e.getSource() instanceof JButton) {
                    // Set the window to display the main menu.
                    tales.setContentPane(new MainMenu());
                    // Refresh the window
                    tales.validate();
                }
            }
        };

        // Constructor for leaderboards
        public Leaderboards() {
            // Paint the background image
            repaint();
            // Create a layout for the panel
            GridBagLayout layout = new GridBagLayout();
            // Get the constraints for the layout
            GridBagConstraints gc = layout.getConstraints(this);
            // Set the panel layout
            setLayout(layout);

            // Get the level
            Level one = LevelManager.getLevel(1);
            // Make sure it's not null
            if (one == null) {
                return;
            }
            // Get the top scores
            String[] scores = null;
            try {
                scores = one.getTopScores();
            } catch (IOException e) {
            }
            // Make sure scores aren't null
            if (scores == null) {
                return;
            }
            // Print out the top 3 scores of that level.
            String text1 = "<html><center><b>LEVEL 1 TOP SCORES</b><br>";
            if (scores[0].indexOf(":") != -1) {
                text1 += scores[0].substring(0, scores[0].indexOf(":")).toUpperCase() + " " + scores[0].substring(scores[0].indexOf(":") + 1) + "s" + "<br>";
            } else {
                text1 += "EMPTY<br>";
            }
            if (scores[1].indexOf(":") != -1) {
                text1 += scores[1].substring(0, scores[1].indexOf(":")).toUpperCase() + " " + scores[1].substring(scores[1].indexOf(":") + 1) + "s" + "<br>";
            } else {
                text1 += "EMPTY<br>";
            }
            if (scores[2].indexOf(":") != -1) {
                text1 += scores[2].substring(0, scores[2].indexOf(":")).toUpperCase() + " " + scores[2].substring(scores[2].indexOf(":") + 1) + "s" + "<br>";
            } else {
                text1 += "EMPTY<br>";
            }
            text1 += "</center></html>";

            // Create a JLabel to hold the text
            JLabel lvl1 = new JLabel(text1);
            // Set the font colour
            lvl1.setForeground(Color.white);
            // Set the font size
            lvl1.setFont(new Font(lvl1.getFont().getName(), Font.PLAIN, 25));
            // Position the label
            gc.gridx = 0;
            gc.gridy = 1;
            // Provide insets to space components out.
            gc.insets = new Insets(0, 0, 20, 0);
            // Add it to the panel
            add(lvl1, gc);

            // Get the level 2
            Level two = LevelManager.getLevel(2);
            // Make sure it's not null
            if (two == null) {
                return;
            }
            // Get the top scores from file
            String[] scores2 = null;
            try {
                scores2 = two.getTopScores();
            } catch (IOException e) {
            }
            // Make sure scores are not null
            if (scores2 == null) {
                return;
            }
            // Generate the top 3 scores from the file, and put them into one string.
            String text2 = "<html><center><b>LEVEL 2 TOP SCORES</b><br>";
            if (scores2[0].indexOf(":") != -1) {
                text2 += scores2[0].substring(0, scores2[0].indexOf(":")).toUpperCase() + " " + scores2[0].substring(scores2[0].indexOf(":") + 1) + "s" + "<br>";
            } else {
                text2 += "EMPTY<br>";
            }
            if (scores2[1].indexOf(":") != -1) {
                text2 += scores2[1].substring(0, scores2[1].indexOf(":")).toUpperCase() + " " + scores2[1].substring(scores2[1].indexOf(":") + 1) + "s" + "<br>";
            } else {
                text2 += "EMPTY<br>";
            }
            if (scores2[2].indexOf(":") != -1) {
                text2 += scores2[2].substring(0, scores2[2].indexOf(":")).toUpperCase() + " " + scores2[2].substring(scores2[2].indexOf(":") + 1) + "s" + "<br>";
            } else {
                text2 += "EMPTY<br>";
            }
            text2 += "</center></html>";

            // Create a JLabel to hold lvl 2 scores
            JLabel lvl2 = new JLabel(text2);
            // Set the font colour
            lvl2.setForeground(Color.white);
            // Set the font size
            lvl2.setFont(new Font(lvl2.getFont().getName(), Font.PLAIN, 25));
            // Position the component
            gc.gridx = 0;
            gc.gridy = 2;
            // Add it to the panel
            add(lvl2, gc);

            // Get level 3
            Level three = LevelManager.getLevel(3);
            // Make sure it's not null
            if (three == null) {
                return;
            }
            // Get the scores for the level
            String[] scores3 = null;
            try {
                scores3 = three.getTopScores();
            } catch (IOException e) {
            }
            // Make sure the scores aren't null
            if (scores3 == null) {
                return;
            }
            // Generate the top 3 scores into one string
            String text3 = "<html><center><b>LEVEL 3 TOP SCORES</b><br>";
            if (scores3[0].indexOf(":") != -1) {
                text3 += scores3[0].substring(0, scores3[0].indexOf(":")).toUpperCase() + " " + scores3[0].substring(scores3[0].indexOf(":") + 1) + "s" + "<br>";
            } else {
                text3 += "EMPTY<br>";
            }
            if (scores3[1].indexOf(":") != -1) {
                text3 += scores3[1].substring(0, scores3[1].indexOf(":")).toUpperCase() + " " + scores3[1].substring(scores3[1].indexOf(":") + 1) + "s" + "<br>";
            } else {
                text3 += "EMPTY<br>";
            }
            if (scores3[2].indexOf(":") != -1) {
                text3 += scores3[2].substring(0, scores3[2].indexOf(":")).toUpperCase() + " " + scores3[2].substring(scores3[2].indexOf(":") + 1) + "s" + "<br>";
            } else {
                text3 += "EMPTY<br>";
            }
            text3 += "</center></html>";

            // Create a JLabel to hold the string
            JLabel lvl3 = new JLabel(text3);
            // Set the font colour
            lvl3.setForeground(Color.white);
            // set the font size
            lvl3.setFont(new Font(lvl2.getFont().getName(), Font.PLAIN, 25));
            // Position the JLabel
            gc.gridx = 0;
            gc.gridy = 3;
            // Add it to the panel
            add(lvl3, gc);

            // Create a button for the main menu
            JButton back = new JButton("Main Menu");
            // Disable the background of the button
            back.setContentAreaFilled(false);
            // Set the text colour on the button
            back.setForeground(Color.white);
            // Disable highlighting of the button
            back.setFocusable(false);
            // Set the font size of the button
            back.setFont(new Font(back.getFont().getName(),
                    Font.PLAIN, 30));
            // Position the button
            gc.gridx = 0;
            gc.gridy = 5;
            // Add it to the panel
            add(back, gc);
            // Add the action and mouse listener to the button.
            back.addActionListener(al);
            back.addMouseListener(ml);

        }

    }

    // Main menu panel
    class MainMenu extends JPanel {

        JButton buttonT = new JButton("Tutorial");
        JButton button2 = new JButton("Level 1");
        JButton button3 = new JButton("Level 2");
        JButton button4 = new JButton("Level 3");
        JButton credits = new JButton("Credits");
        JButton instructions = new JButton("Instructions");
        JButton leaderboard = new JButton("Leaderboards");
        JButton change = new JButton("Change Player");
        JButton quit = new JButton("Quit");
        JPanel panel1 = new JPanel(), panel2 = new JPanel(),
                panel3 = new JPanel(), panel4 = new JPanel(),
                panel5 = new JPanel();

        JPanel panel = this;

        protected void paintComponent(Graphics g) {

            g.drawImage(menuBack, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);

        }

        public MainMenu() {
            //Set constant variables to get the levels for tutorial and the three different levels
            final Level tutorial = LevelManager.getLevel(4);
            final Level one = LevelManager.getLevel(1);
            final Level two = LevelManager.getLevel(2);
            final Level three = LevelManager.getLevel(3);

            // If the name is not stored, get the name from player.
            if (getName) {
                // Create a name variable
                String name;
                // Loop until the user enters a name
                do {
                    // Use a input dialog to retrieve the name
                    name = JOptionPane.showInputDialog(this, "Enter your name",
                            "Welcome to Dungeon Tales",
                            JOptionPane.INFORMATION_MESSAGE);
                    //Custom error checking for main menu and its names
                    if(name != null) {
                        if (name.indexOf(" ") != -1) {
                            JOptionPane.showMessageDialog(this, "No spaces are allowed.", "Warning", JOptionPane.ERROR_MESSAGE);
                        } else if (name.length() > 16) {
                            JOptionPane.showMessageDialog(this, "Maximum of 16 characters allowed.", "Warning", JOptionPane.ERROR_MESSAGE);
                        }else if(name.length() <= 0){
                            JOptionPane.showMessageDialog(this, "Please enter a name.", "Warning", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } while (name == null || name.indexOf(" ") != -1 || name.length() > 16 || name.length() <= 0);
                // Set the players name to the entered one
                p.setName(name);
                try {
                    // Save the new name to the file
                    writeToFile("PlayerName:" + name);
                    getName = false;
                } catch (IOException e) {

                }
            }
            //Key listener to for the credits screen
            addKeyListener(kl);

            panel5.setLayout(null);

            panel5.setBackground(Color.lightGray);

            //The fonts and other specifics used to create the credit screen
            JLabel creditsTitle = new JLabel("CREDITS");
            creditsTitle.setFont(new Font("TimesRoman",
                    Font.BOLD + Font.ITALIC, 80));
            creditsTitle.setBounds(SCREEN_WIDTH / 2 - 500, 150,
                    SCREEN_WIDTH - 500, 100);

            //Add the credits buttona and its action listener
            panel5.add(creditsTitle);

            //Credits formatted with HTML code
            JLabel creditsInfo = new JLabel(
                    "<html>Sound Credits:<br>https://www.youtube.com/user/gamingsoundfx<br><br>"
                            + "Knight Image Credits: <br> https://www.google.ca/search?q=knight+image+cartoon&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjXrZOAuL...<br><br>"
                            + "Door Image Credits: <br> http://cliparts.co/cartoon-door <br><br> Dungeon Background Image Credits: <br> "
                            + "http://steamtradingcards.wikia.com/wiki/File:Darkest_Dungeon_Background_The_Ruins.jpg<br><br>"
                            + "Spikes Image Credits: <br> http://sonic.wikia.com/wiki/Spikes_(obstacle)<br><br>Game created by Justin, Alex, and Jerry<html>");

            creditsInfo.setFont(new Font("TimesRoman", Font.BOLD, 18));
            creditsInfo.setBounds(SCREEN_WIDTH / 2 - 500, 0,
                    SCREEN_WIDTH - 500, SCREEN_HEIGHT);
            panel5.add(creditsInfo);

            //Back button to go back into the main menu
            JButton back = new JButton("Main Menu");

            //Set the panel contents to false to go back to main menu
            back.setContentAreaFilled(false);
            back.setBorderPainted(false);
            back.setFocusable(false);
            //Change the font for the new panel
            back.setFont(new Font(back.getFont().getName(), Font.PLAIN, 25));
            back.setBounds(SCREEN_WIDTH / 2 - 500, 0, SCREEN_WIDTH - 900,
                    SCREEN_HEIGHT + 700);

            //Add the panel back
            panel5.add(back);

            //Action listener for main menu
            ActionListener al = new ActionListener() {

                //Repaint when a button is clicked
                public void actionPerformed(ActionEvent event) {
                    repaint();

                    if (!(event.getSource() instanceof JButton)) {
                        return;
                    }

                    //Jbutton for the source of event
                    JButton button = (JButton) event.getSource();

                    if (button.getText().indexOf("Level") != -1) {
                        Level tutorial = LevelManager.getLevel(4);
                        Level one = LevelManager.getLevel(1);
                        Level two = LevelManager.getLevel(2);

                        //If previous levels have index -1 which means they have not been completed then you display the message (incomplete) for level 1
                        if (button.getText().indexOf("1") != -1) {
                            //Message displays if tutorial was not completed
                            if (!tutorial.isCompleted()) {
                                JOptionPane
                                        .showMessageDialog(
                                                menu,
                                                "You have not unlocked this level yet!",
                                                "Uh oh!",
                                                JOptionPane.INFORMATION_MESSAGE);
                                return;
                            }

                            //If previous levels have index -1 which means they have not been completed then you display the message (incomplete) for level 2
                        } else if (button.getText().indexOf("2") != -1) {
                            //Message displays if level one was not completed
                            if (!one.isCompleted()) {
                                JOptionPane
                                        .showMessageDialog(
                                                menu,
                                                "You have not unlocked this level yet!",
                                                "Uh oh!",
                                                JOptionPane.INFORMATION_MESSAGE);
                                return;
                            }

                            //If previous levels have index -1 which means they have not been completed then you display the message (incomplete) for level 3
                        } else if (button.getText().indexOf("3") != -1) {
                            //Message displays if level two was not completed
                            if (!two.isCompleted()) {
                                JOptionPane
                                        .showMessageDialog(
                                                menu,
                                                "You have not unlocked this level yet!",
                                                "Uh oh!",
                                                JOptionPane.INFORMATION_MESSAGE);
                                return;
                            }
                        }

                    }
                    //Display the main menu screen
                    if (button.getText().equalsIgnoreCase("Main Menu")) {
                        menu.setVisible(true);
                        tales.setContentPane(new MainMenu());
                        tales.validate();
                        return;
                    }
                    //If tutorial cannot be loaded then display the message
                    if (button == buttonT) {
                        if (tutorial == null) {
                            System.out.println("[ERROR] Unable to load level.");
                            return;
                        }
                        //Set the current panel to false and display tutorial level and its related features
                        //if the tutorial can be loaded
                        panel.setVisible(false);
                        menu.setVisible(false);
                        p.setPaused(false);
                        p.setCurrentLevel(tutorial);
                        tales.setContentPane(tutorial);
                        tales.validate();
                        //Quit the game
                    } else if (button == quit) {
                        System.exit(0);
                        //Instructions screen
                    } else if (button == instructions) {
                        button.setForeground(Color.white);
                        atInstructions = true;
                        tales.setContentPane(new Instructions());
                        tales.validate();
                    } else if (button == change) {
                        // Player wishes to reset game/change player

                        // Create variables to catch the result of the confirmation
                        // dialog.
                        int result = 0;
                        // Display a confirmation dialog confirmed the player would
                        // like to quit the game.
                        int dialog = JOptionPane.showConfirmDialog(null,
                                "Changing players will reset all progress, are you sure?", "Warning", result);
                        // If the user does want to quit the game
                        if (dialog == JOptionPane.YES_OPTION) {
                            // Clear data
                            File save = new File("save.txt");
                            if (save.exists()) {
                                save.delete();
                                try {
                                    // Create the new save file.
                                    save.createNewFile();
                                    // Make a writer for the file.
                                    PrintWriter out = new PrintWriter(save);
                                    // Print the header in the file.
                                    out.println("- DUNGEON TALES SAVE FILE -");
                                    // Close the writer.
                                    out.close();
                                    getName = true;
                                    tales.setContentPane(new MainMenu());
                                    tales.validate();
                                } catch (IOException e) {

                                }
                            }

                        }
                    } else if (button == credits) {
                        panel.setVisible(false);
                        tales.setContentPane(panel5);
                        tales.validate();
                    } else if (button == button2) {
                        if (one == null) {
                            JOptionPane
                                    .showMessageDialog(
                                            panel,
                                            "Unable to load level!\nHas it been created?",
                                            "Error", 0);
                            System.out.println("[ERROR] Unable to load level.");
                            return;
                        }
                        p.setCurrentLevel(one);
                        panel.setVisible(false);
                        one.addKeyListener(kl);
                        menu.setVisible(false);
                        // tales.remove(menu);
                        tales.setContentPane(one);
                        tales.validate();
                    } else if (button == leaderboard) {
                        tales.setContentPane(new Leaderboards());
                        tales.validate();
                    } else if (button == button3) {
                        if (two == null) {
                            JOptionPane
                                    .showMessageDialog(
                                            panel,
                                            "Unable to load level!\nHas it been created?",
                                            "Error", 0);
                            System.out.println("[ERROR] Unable to load level.");
                            return;
                        }
                        p.setCurrentLevel(two);
                        panel.setVisible(false);
                        two.addKeyListener(kl);
                        tales.setContentPane(two);
                        tales.validate();
                    } else {
                        if (three == null) {
                            JOptionPane
                                    .showMessageDialog(
                                            panel,
                                            "Unable to load level!\nHas it been created?",
                                            "Error", 0);
                            System.out.println("[ERROR] Unable to load level.");
                            return;
                        }
                        p.setCurrentLevel(three);
                        panel.setVisible(false);
                        three.addKeyListener(kl);
                        tales.setContentPane(three);
                        tales.validate();
                    }

                    if (p.getCurrentLevel() != null) {
                        for (Component c : p.getCurrentLevel().getComponents()) {
                            c.setVisible(true);
                        }
                        LevelManager.getLevel(4).setLayout(null);
                        p.setX(p.getCurrentLevel().getSpawnX());
                        p.setY(p.getCurrentLevel().getSpawnY());
                        p.getCurrentLevel().getGameTimer().resumeTime();
                        p.canPause = true;
                        p.setCanMove(true);
                        p.getCurrentLevel().setLayout(null);
                        try {
                            stopMusicFile();
                            playMusicFile("NonBoss.wav", true);
                        } catch (LineUnavailableException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (UnsupportedAudioFileException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            };

            back.addActionListener(al);

            FlowLayout flow = new FlowLayout();
            this.setLayout(flow);

            Dimension dimB = new Dimension(200, 60);

            p.canPause = false;

            add(quit);
            // panel3.add(back);
            // panel4.add(back);
            // panel5.add(back);
            quit.addActionListener(al);

            buttonT.setContentAreaFilled(false);
            buttonT.setBorderPainted(false);
            buttonT.setFocusable(false);
            if (!tutorial.isCompleted()) {
                buttonT.setForeground(Color.white);
            } else {
                buttonT.setForeground(Color.green);
            }
            buttonT.setFont(new Font(buttonT.getFont().getName(), Font.PLAIN,
                    30));
            add(buttonT);
            buttonT.addActionListener(al);
            buttonT.addMouseListener(ml);

            button2.setContentAreaFilled(false);
            button2.setBorderPainted(false);
            button2.setFocusable(false);
            button2.setForeground(Color.white);
            button2.setFont(new Font(button2.getFont().getName(), Font.PLAIN,
                    30));
            if (!tutorial.isCompleted()) {
                button2.setForeground(Color.red);
            } else {
                if (!one.isCompleted()) {
                    button2.setForeground(Color.white);
                } else {
                    button2.setForeground(Color.green);
                }
            }
            add(button2);
            button2.addActionListener(al);
            button2.addMouseListener(ml);

            button3.setContentAreaFilled(false);
            button3.setBorderPainted(false);
            button3.setForeground(Color.white);
            button3.setFocusable(false);
            button3.setFont(new Font(button3.getFont().getName(), Font.PLAIN,
                    30));
            if (!one.isCompleted()) {
                button3.setForeground(Color.red);
            } else {
                if (!two.isCompleted()) {
                    button3.setForeground(Color.white);
                } else {
                    button3.setForeground(Color.green);
                }
            }
            add(button3);
            button3.addActionListener(al);
            button3.addMouseListener(ml);

            button4.setContentAreaFilled(false);
            button4.setBorderPainted(false);
            button4.setForeground(Color.white);
            button4.setFocusable(false);
            button4.setFont(new Font(button4.getFont().getName(), Font.PLAIN,
                    30));
            if (!two.isCompleted()) {
                button4.setForeground(Color.red);
            } else {
                if (!three.isCompleted()) {
                    button4.setForeground(Color.white);
                } else {
                    button4.setForeground(Color.green);
                }
            }
            add(button4);
            button4.addActionListener(al);
            button4.addMouseListener(ml);

            instructions.setContentAreaFilled(false);
            instructions.setBorderPainted(false);
            instructions.setForeground(Color.white);
            instructions.setFocusable(false);
            instructions.setFont(new Font(instructions.getFont().getName(),
                    Font.PLAIN, 30));
            add(instructions);
            instructions.addActionListener(al);
            instructions.addMouseListener(ml);

            leaderboard.setContentAreaFilled(false);
            leaderboard.setBorderPainted(false);
            leaderboard.setForeground(Color.white);
            leaderboard.setFocusable(false);
            leaderboard.setFont(new Font(leaderboard.getFont().getName(),
                    Font.PLAIN, 30));
            add(leaderboard);
            leaderboard.addActionListener(al);
            leaderboard.addMouseListener(ml);

            quit.setContentAreaFilled(false);
            quit.setBorderPainted(false);
            quit.setForeground(Color.white);
            quit.setFocusable(false);
            quit.setFont(new Font(quit.getFont().getName(), Font.PLAIN, 30));
            add(quit);
            quit.addActionListener(al);
            quit.addMouseListener(ml);

            change.setContentAreaFilled(false);
            change.setBorderPainted(false);
            change.setForeground(Color.white);
            change.setFocusable(false);
            change.setFont(new Font(change.getFont().getName(), Font.PLAIN, 30));
            add(change);
            change.addActionListener(al);
            change.addMouseListener(ml);

            credits.setContentAreaFilled(false);
            credits.setBorderPainted(false);
            credits.setForeground(Color.white);
            credits.setFocusable(false);
            credits.setFont(new Font(credits.getFont().getName(), Font.PLAIN,
                    30));
            add(credits);
            credits.addActionListener(al);
            credits.addMouseListener(ml);

            JLabel name = new JLabel("Player: " + p.getName());
            name.setForeground(Color.GREEN);
            name.setFont(new Font(name.getFont().getName(), Font.PLAIN, 25));
            add(name);

            panel1.setBackground(new Color(100, 100, 100));

            ImageIcon logo = new ImageIcon("Logo.png");
            JLabel label3 = new JLabel(logo);
            label3.setPreferredSize(new Dimension(1000, 800));
            label3.setLocation(SCREEN_WIDTH / 2 - 1000, SCREEN_HEIGHT / 2);
            add(label3);
        }

    }

    public static void registerLevels() throws IOException {

        // Tutorial

        // Create the spikes for the tutorial
        Rectangle[] spikesTut = {new Rectangle(725, SCREEN_HEIGHT - 50, 120,
                25)};
        // Create the platforms for the tutorial
        Rectangle[] tutorialPlats = {
                new Rectangle(400, SCREEN_HEIGHT - 150, 180, 20),
                new Rectangle(1420, SCREEN_HEIGHT - 550, 20, 900),
                new Rectangle(680, SCREEN_HEIGHT - GROUND_WIDTH - 130, 40,
                        130 + GROUND_WIDTH),
                new Rectangle(855, SCREEN_HEIGHT - GROUND_WIDTH - 180, 40,
                        180 + GROUND_WIDTH)};
        // Create a level instance for the tutorial.
        Level tutorial = new Level(4, 10, SCREEN_HEIGHT - GROUND_WIDTH,
                SCREEN_WIDTH - 400, SCREEN_HEIGHT - GROUND_WIDTH - 50, p,
                tutorialPlats, 1, spikesTut);
        // Add the key listener to the panel
        tutorial.addKeyListener(kl);
        // Create the moving platform for the tutorial
        Platform tPlat1 = new Platform(1250, 650, 1250, 1030, 90, 30, tutorial,
                1, 2);

        // Level 1

        // Create spikes for level 1
        Rectangle[] spikesOne = {new Rectangle(400, 275, 120, 25),
                new Rectangle(510, 275, 120, 25),
                new Rectangle(620, 275, 120, 25),
                new Rectangle(730, 275, 120, 25),
                new Rectangle(SCREEN_WIDTH - 120, 670, 120, 30),
                new Rectangle(700, 730, 120, 30),
                new Rectangle(810, 730, 120, 30),
                new Rectangle(920, 730, 120, 30),
                new Rectangle(1030, 730, 120, 30),
                new Rectangle(1140, 730, 150, 30),
                new Rectangle(SCREEN_WIDTH - 620, 670, 120, 30)};
        // Create platforms for level 1
        Rectangle[] onePlats = {new Rectangle(0, 300, 850, 30),
                new Rectangle(1250, 300, 500, 30),
                new Rectangle(200, 700, 500, 30),
                new Rectangle(1300, 700, 750, 30),
                new Rectangle(700, 760, 600, 30)};
        // Create the level instance for level 1
        Level one = new Level(1, 20, 20, SCREEN_WIDTH - 300, SCREEN_HEIGHT
                - GROUND_WIDTH - 50, p, onePlats, 4, spikesOne);
        // Create the moving platforms for level 1
        Platform lPlat1 = new Platform(SCREEN_WIDTH - 120, 100,
                SCREEN_WIDTH - 120, 600, 90, 30, one, 1, 3);
        Platform lPlat2 = new Platform(350, 175, 1150, 175, 90, 30, one, 2, 3);
        Platform lPlat3 = new Platform(700, 600, 950, 600, 60, 30, one, 3, 4);
        Platform lPlat4 = new Platform(1050, 600, 1600, 600, 90, 30, one, 4, 6);

        // Level 2

        // Create the spikes for level 2
        Rectangle[] spikesTwo = {new Rectangle(370, 270, 120, 30),
                new Rectangle(480, 270, 120, 30),
                new Rectangle(590, 270, 120, 30),
                new Rectangle(700, 270, 120, 30),
                new Rectangle(810, 270, 120, 30),
                new Rectangle(920, 270, 120, 30),
                new Rectangle(1200, 270, 120, 30),
                new Rectangle(1286, 270, 120, 30),
                new Rectangle(1396, 270, 120, 30),
                new Rectangle(1506, 270, 120, 30),
                new Rectangle(1506, 810, 120, 30),
                new Rectangle(1396, 810, 120, 30),
                new Rectangle(1286, 810, 120, 30),
                new Rectangle(1176, 810, 120, 30),
                new Rectangle(1066, 810, 120, 30),
                new Rectangle(956, 810, 120, 30),
                new Rectangle(846, 810, 120, 30),
                new Rectangle(736, 810, 120, 30),
                new Rectangle(626, 810, 120, 30),
                new Rectangle(516, 810, 120, 30),
                new Rectangle(406, 810, 120, 30),
                new Rectangle(296, 810, 120, 30),
                new Rectangle(1066, 810, 120, 30),
                new Rectangle(1800, 1020, 120, 30),
                new Rectangle(1690, 1020, 120, 30),
                new Rectangle(1580, 1020, 120, 30),
                new Rectangle(1470, 1020, 120, 30),
                new Rectangle(1360, 1020, 120, 30),
                new Rectangle(1250, 1020, 120, 30),
                new Rectangle(1140, 1020, 120, 30),
                new Rectangle(830, 1020, 120, 30),
                new Rectangle(720, 1020, 120, 30),
                new Rectangle(610, 1020, 120, 30),
                new Rectangle(500, 1020, 120, 30),
                new Rectangle(390, 1020, 120, 30),
                new Rectangle(390, 1020, 120, 30),
                new Rectangle(280, 1020, 120, 30),
                new Rectangle(170, 1020, 120, 30),
                new Rectangle(SCREEN_WIDTH - 270, 320, 120, 30)};
        // Create the platforms for level 2
        Rectangle[] twoPlats = {new Rectangle(0, 300, SCREEN_WIDTH - 300, 30),
                new Rectangle(0, 600, SCREEN_WIDTH - 1200, 30),
                new Rectangle(200, 840, 1420, 30),
                new Rectangle(SCREEN_WIDTH - 300, 300, 30, 570),
                new Rectangle(SCREEN_WIDTH - 270, 350, 120, 30)};
        // Create the level instance for level 2
        Level two = new Level(2, 40, 150, 100, 425, p, twoPlats, 10, spikesTwo);

        // First platform in level that moves left to right
        Platform Plat1 = new Platform(350, 175, 1050, 175, 90, 30, two, 1, 3);

        // Platform on right that moves up and down
        Platform Plat2 = new Platform(SCREEN_WIDTH - 120, 100,
                SCREEN_WIDTH - 120, 600, 90, 30, two, 2, 2);

        // Platform in center of screen that
        Platform Plat3 = new Platform(800, 600, 1300, 600, 300, 30, two, 3, 2);

        // First platform in level that moves left to right
        Platform Plat4 = new Platform(1250, 175, 1650, 175, 90, 30, two, 4, 3);

        // Platform on right that moves up and down
        Platform Plat5 = new Platform(SCREEN_WIDTH - 240, 620,
                SCREEN_WIDTH - 240, 950, 90, 30, two, 5, 2);

        // Platform in center of screen that
        Platform Plat6 = new Platform(1130, 995, 1880, 995, 300, 15, two, 6, 2);

        // Platform in center of screen that
        Platform Plat7 = new Platform(160, 995, 930, 995, 300, 15, two, 7, 2);

        // Platform in center of screen that
        Platform Plat8 = new Platform(30, 785, 30, 1115, 100, 15, two, 8, 2);

        // Platform in center of screen that
        Platform Plat9 = new Platform(290, 785, 1580, 785, 100, 15, two, 9, 2);

        // Platform in center of screen that
        Platform Plat10 = new Platform(935, 705, 1580, 705, 100, 15, two, 10, 2);

        // Level 3

        // Create the spikes for level 3
        Rectangle[] spikesThree = {new Rectangle(330, 570, 120, 30),
                new Rectangle(0, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(110, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(220, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(330, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(440, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(700, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(780, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(900, 170, 120, 30),
                new Rectangle(1280, 170, 120, 30),
                new Rectangle(930, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(1030, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(1140, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(1250, SCREEN_HEIGHT - 60, 120, 30),
                new Rectangle(1360, SCREEN_HEIGHT - 60, 120, 30),
        };

        // Create the platforms for level 3
        Rectangle[] threePlats = {
                new Rectangle(0, 300, 300, 30),
                new Rectangle(450, 0, 30, SCREEN_HEIGHT - 300),
                new Rectangle(150, 600, 300, 30),
                new Rectangle(900, 200, 30, SCREEN_HEIGHT),
                new Rectangle(1400, 0, 30, SCREEN_HEIGHT - 250),
                new Rectangle(930, 200, 180, 30),
                new Rectangle(1240, 200, 160, 30),
                new Rectangle(930, 400, 180, 30),
                new Rectangle(1240, 400, 160, 30),
                new Rectangle(930, 600, 180, 30),
                new Rectangle(1240, 600, 160, 30),
                new Rectangle(930, 800, 180, 30),
                new Rectangle(1240, 800, 160, 30),
        };

        // Create the level instance for level 3
        Level three = new Level(3, 40, 150, SCREEN_WIDTH - 350, 200, p, threePlats, 6,
                spikesThree);

        // Create the moving platforms for level 3
        Platform threePlat1 = new Platform(0, SCREEN_HEIGHT - 175, 500, SCREEN_HEIGHT - 175, 90, 30, three, 1, 4);
        Platform threePlat2 = new Platform(SCREEN_WIDTH - 430, SCREEN_HEIGHT - 700, SCREEN_WIDTH - 430, SCREEN_HEIGHT - 90, 90, 30, three, 2, 2);
        Platform threePlat3 = new Platform(800, SCREEN_HEIGHT - 400, 800, SCREEN_HEIGHT - 100, 90, 30, three, 3, 2);
        Platform threePlat4 = new Platform(500, SCREEN_HEIGHT - 700, 500, SCREEN_HEIGHT - 320, 90, 30, three, 4, 2);
        Platform threePlat5 = new Platform(800, SCREEN_HEIGHT - 960, 800, SCREEN_HEIGHT - 680, 90, 30, three, 5, 2);
        Platform threePlat6 = new Platform(930, SCREEN_HEIGHT - 100, 1450, SCREEN_HEIGHT - 100, 90, 30, three, 6, 5);
    }

    /*
    Method to quit the game
     */
    public static void quitGame() {
        // Create variables to catch the result of the confirmation
        // dialog.
        int result = 0;
        // Display a confirmation dialog confirmed the player would
        // like to quit the game.
        int dialog = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to quit?", "Warning", result);
        // If the user does want to quit the game
        if (dialog == JOptionPane.YES_OPTION) {
            // Close the game.
            System.exit(0);
        }
    }

    /*
    Method to write to the save file
    @param The text you would like to write
     */
    public static void writeToFile(String text) throws IOException {
        // Get the file, make sure it exists.
        File save = new File("save.txt");
        if (!save.exists()) {
            return;
        }

        // Write to the file
        PrintWriter out = new PrintWriter(new FileWriter(save, true));
        out.println(text);
        // Close the writer.
        out.close();
    }

    public static void main(String[] args) throws IOException {

        // Create the leaderboard save file
        File board = new File("Leaderboards.txt");

        if (!board.exists()) {
            // Create the leaderboards.txt file
            board.createNewFile();
            PrintWriter out = new PrintWriter(board);
            // Print the defaults in the file.
            out.println("LEVEL 1 SCORES");
            out.println("empty");
            out.println("empty");
            out.println("empty");
            out.println("LEVEL 2 SCORES");
            out.println("empty");
            out.println("empty");
            out.println("empty");
            out.println("LEVEL 3 SCORES");
            out.println("empty");
            out.println("empty");
            out.println("empty");
            out.println("LEVEL 4 SCORES");
            out.println("empty");
            out.println("empty");
            out.println("empty");
            // Close the writer.
            out.close();
        }

        // Create a save file if it does not already exist.
        File save = new File("save.txt");
        if (!save.exists()) {
            // Create the new save file.
            save.createNewFile();
            // Make a writer for the file.
            PrintWriter out = new PrintWriter(save);
            // Print the header in the file.
            out.println("- DUNGEON TALES SAVE FILE -");
            // Close the writer.
            out.close();
            // Create a new instance of a player.
            p = new Player("John");
            getName = true;
        } else {
            // Get the player from the file.
            // Create a scanner for the file.
            Scanner input = null;
            try {
                input = new Scanner(save);
            } catch (IOException e) {
                System.out.println("[ERROR] Unable to access save.txt");
            }

            if (input == null) {
                return;
            }

            // Loop through each file value.
            while (input.hasNext()) {
                // Get the line in the file.
                String line = input.nextLine();
                // Get the saved player name from the file.
                if (line.indexOf("PlayerName") != -1) {
                    // Get the value
                    String name = getFileValue(line);
                    p = new Player(name);
                }
            }
            // Close the input.
            input.close();

            if (p == null) {
                getName = true;
                p = new Player("John");
            }

        }

        // Register levels
        registerLevels();

        // Activate the constructor, and create the frame.
        tales = new DungeonTales();

    }
}
