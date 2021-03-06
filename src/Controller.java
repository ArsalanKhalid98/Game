import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Controls for main game screen.
 *
 * @author s325919, s325894
 */

public class Controller {

    private Stage stage;
    private AnimationTimer timer;
    private List<Bullet> bullets = new ArrayList<>();
    private List<Bullet> bullets2 = new ArrayList<>();
    private List<Level> maps = new ArrayList<>();
    private Player player;
    private Player enemy;
    private ImageView boom[] = new ImageView[5];
    private boolean music = true;
    private boolean effect = true;

    //interne klokker
    private int load = 10; //skudd per antall frames
    private int loadCount = load;
    private int load2 = 6;
    private int loadCount2 = load2;

    private int currentLevel;
    private String saveName;
    private BitSet keyboardBitSet = new BitSet();
    private double scenewidth = 1280;
    private double sceneheigth = 720;
    private static Music lobby = new Music("/res/backgroundMusic.wav");
    private static Music victory = new Music("/res/Victory.wav");
    private static Music hit = new Music("/res/sound.wav");
    private static Music die = new Music("/res/fatality.wav");
    private static Music firing = new Music("/res/shoot.wav");

    //fxml koblinger
    /**
     * Label errorLabel shows error on screen.
     * Label victoryLabelWinner shows the winner.
     * Label VictoryLabelScore shows the winner's score.
     * Label currentScore shows the current score.
     */

    public Label errorLabel, victoryLabelWinner, victoryLabelScore, currentScore;

    /**
     * Pane gameP the pane which the game is loaded into.
     */
    public Pane gameP;

    /**
     * AnchorPane main, the main menu pane.
     *      * AnchorPane SaveP, the save pane.
     *      * AnchorPane errorP, the error pane.
     *      * AnchorPane LoadP, the load pane.
     *      * AnchorPane mainPane, the pane that every other pane is connected to.
     *      * AnchorPane gamePaused, the pane that shows up when the game is paused.
     *      * AnchorPane victoryP, the pane that shows up when you win.
     *      * AnchorPane gameRoot, the pane that the game is played on.
     *      * AnchorPane helpP, the settings pane.
     *      * AnchorPane settingsP, the keybinds pane.
     */
    public AnchorPane main, saveP, errorP, loadP, mainPane, gamePaused, victoryP, gameRoot, helpP, settingsP;

    /**
     * ImageView background, the background logo for the game.
     */
    public ImageView background;

    /**
     * ProgressBar playerHP, shows the player HP.
     * ProgressBar enemyHP, shows the enemy HP
     */
    public ProgressBar playerHp, enemyHp;

    /**
     * Button loadfile1, chooses savefile name to load.
     * Button loadfile2, chooses savefile name to load.
     * Button loadfile3, chooses savefile name to load.
     * Button savefile1, chooses savefile name to save.
     * Button savefile2, chooses savefile name to save.
     * Button savefile3, chooses savefile name to save.
     */
    public Button loadfile1,loadfile2,loadfile3, savefile1, savefile2,savefile3;

    /**
     * Loads in the player, enemy and level.
     * Adds the controls in and loads pane in.
     * Starts the timer and clears the keyboardBitSet
     * Stops the lobby music.
     */
    public void startGame() {
        createContent(0,0,0);
        addInputControls(mainPane.getScene());
        switchPane(main, gameRoot);
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();
        keyboardBitSet.clear();
        lobby.stop();

    }

    /**
     * Switches to the help pane.
     */
    public void help() {
        switchPane(main,helpP);
    }

    /**
     * Sets the correct things to be shown on screen
     * when going from paused to unpaused.
     * Loads in the correct background image.
     * starts the timer and clears the keyboardBitSets
     */
    public void resumeGame() {
            gamePaused.setVisible(false);
            gamePaused.setDisable(true);
            gameRoot.setDisable(false);
            background.setImage(new Image(maps.get(currentLevel).getMapBg()));
            timer.start();
            keyboardBitSet.clear();
    }

    /**
     * Switches to saveGame pane and clear the inputfield.
     */
    public void saveGame() {
        saveName=null;
        loadfile3.setStyle("-fx-background-color: whitesmoke;");
        savefile3.setStyle("-fx-background-color: whitesmoke;");
        loadfile2.setStyle("-fx-background-color: whitesmoke;");
        savefile2.setStyle("-fx-background-color: whitesmoke;");
        loadfile1.setStyle("-fx-background-color: whitesmoke;");
        savefile1.setStyle("-fx-background-color: whitesmoke;");
        switchPane(main,saveP);
    }

    /**
     * Method that saves the game and
     * prints out an error if it cant
     * save the file or no name is
     * entered.
     */
    public void saveSave(){
        if(saveName != null){
            System.out.println(saveName);
            Save save = new Save(player.getScore(),enemy.getScore(),currentLevel);
            try {
                resourceManager.save(save,saveName);
                System.out.println("Saving game ...");
            } catch (Exception ex){
                System.out.println("FUNKER IKKE Å LAGRE " + ex.getMessage());
            }
            switchPane(saveP,gamePaused);
        } else {
            error("No save picked \n");
            System.out.println("Skriv inn et navn");
        }
    }

    /**
     * Switches to the loadGame pane.
     */
    public void loadGame() {
        loadfile3.setStyle("-fx-background-color: whitesmoke;");
        savefile3.setStyle("-fx-background-color: whitesmoke;");
        loadfile2.setStyle("-fx-background-color: whitesmoke;");
        savefile2.setStyle("-fx-background-color: whitesmoke;");
        loadfile1.setStyle("-fx-background-color: whitesmoke;");
        savefile1.setStyle("-fx-background-color: whitesmoke;");
        saveName = null;
        switchPane(main,loadP);
    }

    /**
     * Lets the user choose savefile 1,
     * and changes the color.
     */
    public void loader(){
        System.out.println("getting file 1");
        saveName = "save1.save";
        loadfile1.setStyle("-fx-background-color: lightgray;");
        savefile1.setStyle("-fx-background-color: lightgray;");
        loadfile2.setStyle("-fx-background-color: whitesmoke;");
        savefile2.setStyle("-fx-background-color: whitesmoke;");
        loadfile3.setStyle("-fx-background-color: whitesmoke;");
        savefile3.setStyle("-fx-background-color: whitesmoke;");
    }

    /**
     * Lets the user choose savefile 2,
     * and changes the color.
     */
    public void loader2(){
        System.out.println("getting file 2");
        saveName = "save2.save";
        loadfile2.setStyle("-fx-background-color: lightgray;");
        savefile2.setStyle("-fx-background-color: lightgray;");
        loadfile1.setStyle("-fx-background-color: whitesmoke;");
        savefile1.setStyle("-fx-background-color: whitesmoke;");
        loadfile3.setStyle("-fx-background-color: whitesmoke;");
        savefile3.setStyle("-fx-background-color: whitesmoke;");
    }

    /**
     * Lets the user choose savefile 3,
     * and changes the color.
     */
    public void loader3(){
        System.out.println("getting file 3");
        saveName = "save3.save";
        loadfile3.setStyle("-fx-background-color: lightgray;");
        savefile3.setStyle("-fx-background-color: lightgray;");
        loadfile2.setStyle("-fx-background-color: whitesmoke;");
        savefile2.setStyle("-fx-background-color: whitesmoke;");
        loadfile1.setStyle("-fx-background-color: whitesmoke;");
        savefile1.setStyle("-fx-background-color: whitesmoke;");
    }


    /**
     * Loads in the chosen savefile and
     * start up the game. Catches exception
     * and prints out error message if it cant
     * load correctly or no file is chosen.
     */
    public void loadLoad(){
        if(saveName != null) {
            try {
                Save save = (Save) resourceManager.load(saveName);
                System.out.println("Loading game ...");
                int P = save.getScoreP();
                int E = save.getScoreE();
                int c = save.getCurrentMap();
                createContent(P,E,c);
                addInputControls(mainPane.getScene());
                switchPane(loadP, gameRoot);
                timer = new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        onUpdate();
                    }
                };
                timer.start();
                keyboardBitSet.clear();
                if(music)
                //game.loop();
                lobby.stop();
            } catch (Exception ex) {
                if (ex.getMessage() != null) {
                    System.out.println("KAN IKKE LOADE!: " + ex.getMessage());
                    error("cant load file \n" + ex.getMessage());
                }
            }
        } else {
            error("cant load file, no save chosen");
        }
    }

    /**
     * Closes the game window.
     */
    public void exitGame() {
        stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    /**
     * Switches back to Main Pane or
     * back to the pane you got
     * an error.
     */
    public void goBack() {
        if(errorP.isVisible() && main.isVisible()){
            switchPane(errorP,main);
        } else if(errorP.isVisible() && saveP.isVisible()){
            switchPane(errorP,saveP);
        } else if(errorP.isVisible() && loadP.isVisible()){
            switchPane(errorP,loadP);
        } else if(errorP.isVisible() && gameRoot.isVisible()){
            switchPane(errorP, gameRoot);
        } else {
            errorP.setDisable(true);
            errorP.setVisible(false);
            loadP.setDisable(true);
            loadP.setVisible(false);
            saveP.setDisable(true);
            saveP.setVisible(false);
            gameRoot.setDisable(true);
            gameRoot.setVisible(false);
            helpP.setDisable(true);
            helpP.setVisible(false);
            main.setDisable(false);
            main.setVisible(true);
        }
    }

    /**
     * Switches the pane back to the main
     * pause menu.
     */
    public void goBackGame(){
        switchPane(saveP,gamePaused);
    }

    /**
     * Switches the pane back to settings pane.
     */
    public void goBackHelp() {
        switchPane(settingsP,helpP);
    }

    /**
     * Switches the pane from help to settings.
     */
    public void goSettings() {
        switchPane(helpP,settingsP);
    }

    /**
     * Turns the background music off.
     */
    public void mOFF() {
        if(music){
            music = false;
            lobby.stop();
        } else {
            music = true;
            lobby.loop();
        }
    }

    /**
     * turns the sound effects off.
     */
    public void eOFF() {
        effect = !effect;

    }

    /**
     * Switches back to the main menu,
     * plays the main menu music and
     * loads in the logo image.
     */
    public void toMain() {
        goBack();
        if(music)
        lobby.loop();
        gamePaused.setVisible(false);
        gamePaused.setDisable(true);
        victoryP.setVisible(false);
        victoryP.setDisable(true);
        background.setImage(new Image("/res/navn.png"));
    }

    /**
     * Constructor initializes the ImageView
     * array and starts the music.
     */
    public Controller(){
        boom[0]= new ImageView(new Image("/res/explosion1.png"));
        boom[1]= new ImageView(new Image("/res/explosion2.png"));
        boom[2]= new ImageView(new Image("/res/explosion3.png"));
        boom[3]= new ImageView(new Image("/res/explosion4.png"));
        boom[4]= new ImageView(new Image("/res/explosion5.png"));
        lobby.loop();
    }

    /**
     *
     * Method that creates all the objects and
     * maps and places the relevant photo and
     * text on screen.
     *
     * @param P int for player's score.
     * @param E int for enemy's score.
     * @param L int for the current level.
     */
    private void createContent(int P, int E, int L) {

        stage = (Stage) mainPane.getScene().getWindow();

        gameP.getChildren().clear();
        bullets2.clear();
        bullets.clear();
        maps.clear();

        player = new Player("/res/tankBlue.png", 10,  gameP);
        player.setVelocity(0,0);
        player.setSpeedMultiplier(3);

        enemy = new Player("/res/tankRed.png", 10,  gameP);
        enemy.setVelocity(0,0);
        enemy.getView().setRotate(180);
        enemy.setSpeedMultiplier(3);

        player.setScore(P);
        enemy.setScore(E);
        currentLevel = L;

        String spriteWall = "/res/wall2.png";
        String spriteWall2 = "/res/wall1.png";
        String spriteWall3 = "/res/wall1.png";
        String spriteBg1 = "/res/spillbg1.png";
        String spriteBg2 = "/res/spillbg2.png";
        String spriteBg3 = "/res/spillbg3.png";


        // bane1
        maps.add(new Level(50,50,1210,650, spriteBg1));
        //oppe til venstre
        maps.get(0).addWalls(new Wall(spriteWall,150,100, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,150,150, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,150,200, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,200,100, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,250,100, gameP));
        // oppe til høyre
        maps.get(0).addWalls(new Wall(spriteWall,1100,100, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,1050,100, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,1000,100, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,1100,150, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,1100,200, gameP));
        // nede til venstre
        maps.get(0).addWalls(new Wall(spriteWall,150,550, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,150,500, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,150,450, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,200,550, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,250,550, gameP));
        //nede til høyre
        maps.get(0).addWalls(new Wall(spriteWall,1100,550, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,1100,500, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,1100,450, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,1050,550, gameP));
        maps.get(0).addWalls(new Wall(spriteWall,1000,550, gameP));
        //dritten i midten
        maps.get(0).addWalls(new Wall(spriteWall,scenewidth/2 - 25/2,sceneheigth/2 - 25/2, gameP)); //midten

        //BANE 2
        maps.add(new Level(50,50,1210,650,spriteBg2));
        maps.get(1).addWalls(new Wall(spriteWall2,scenewidth/2 - 25/2,sceneheigth/2 - 25/2, gameP));
        //opp til venstre
        maps.get(1).addWalls(new Wall(spriteWall2,300,45, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,300,95, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,300,145, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,300,195, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,300,245, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,250,245, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,200,245, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,150,245, gameP));
        //oppe til høyre
        maps.get(1).addWalls(new Wall(spriteWall2,900,45, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,900,95, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,900,145, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,900,195, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,900,245, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,950,245, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,1000,245, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,1050,245, gameP));
        //nede til venstre
        maps.get(1).addWalls(new Wall(spriteWall2,350,670, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,350,620, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,350,570, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,350,520, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,350,470, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,300,470, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,250,470, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,200,470, gameP));
        //nede til høyre
        maps.get(1).addWalls(new Wall(spriteWall2,950,670, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,950,620, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,950,570, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,950,520, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,950,470, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,1000,470, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,1050,470, gameP));
        maps.get(1).addWalls(new Wall(spriteWall2,1100,470, gameP));

        //bane3
        maps.add(new Level(50 ,50,1210,650,spriteBg3));
        //nede til høyre'
        maps.get(2).addWalls(new Wall(spriteWall3,1230,400, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,1180,400, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,1130,400, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,1080,400, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,1030,450, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,930,570, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,880,620, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,880,670, gameP));
        //oppe til høyre
        maps.get(2).addWalls(new Wall(spriteWall3,1130,40, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,1130,90, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,1130,140, gameP));
        //sidelengs T til venstre
        maps.get(2).addWalls(new Wall(spriteWall3,0,300, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,50,300, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,100,300, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,150,300, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,150,250, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,150,350, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,150,400, gameP));
        //
        maps.get(2).addWalls(new Wall(spriteWall3,400,670, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,400,620, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,400,570, gameP));
        //krysset
        maps.get(2).addWalls(new Wall(spriteWall3,450,400, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,500,400, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,550,400, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,600,400, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,650,400, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,700,400, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,600,450, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,600,500, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,600,350, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,600,300, gameP));
        maps.get(2).addWalls(new Wall(spriteWall3,600,550, gameP));

        //bane4
        maps.add(new Level(50,50,1210,650,spriteBg1));
        for(Wall i : maps.get(0).getWalls()){
            maps.get(3).addWalls(i);
        }

        //bane5
        maps.add(new Level(50,650,1210,50,spriteBg2));
        for(Wall i : maps.get(1).getWalls()){
            maps.get(4).addWalls(i);
        }

        for(Wall i : maps.get(L).getWalls()){
            i.addPane();
        }
        player.getView().setTranslateX(maps.get(L).getSpawnPX());
        player.getView().setTranslateY(maps.get(L).getSpawnPY());
        enemy.getView().setTranslateX(maps.get(L).getSpawnEX());
        enemy.getView().setTranslateY(maps.get(L).getSpawnEY());


        background.setImage(new Image(maps.get(currentLevel).getMapBg()));

        currentScore.setText(player.getScore() + " : " + enemy.getScore());
    }

    /**
     *
     * Method that switches from one pane
     * to another.
     *
     * @param from The pane you are on.
     * @param to The pane you want to change to.
     */
    public void switchPane(Pane from,Pane to){
        from.setDisable(true);
        from.setVisible(false);
        to.setDisable(false);
        to.setVisible(true);
    }

    /**
     *
     * Sets the keyboardBitSet if
     * key pressed but not released
     *
     * @param scene current scene.
     */
    public void addInputControls(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            keyboardBitSet.set(e.getCode().ordinal(), true);
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            keyboardBitSet.set(e.getCode().ordinal(), false);
        });
    }

    /**
     *
     * Shows an error message.
     *
     * @param error What the error says.
     */
    public void error(String error){
        errorLabel.setText("Error!!! \n" + error);
        errorP.setDisable(false);
        errorP.setVisible(true);
        main.setDisable(true);
        saveP.setDisable(true);
        loadP.setDisable(true);
    }

    /**
     *
     * Stops the player from going out of bounds.
     *
     * @param player takes the Player object as param
     */
    public void boundsPlayer(Player player){
        double maxX = scenewidth - (player.getWidth() / 2);
        double minX = 0 - (player.getWidth() / 2);
        double maxY = sceneheigth - (player.getWidth() / 2);
        double minY = 45;
        if(player.getX() >= maxX) {
            player.getView().setTranslateX(maxX);
        } else if (player.getX() <= minX) {
            player.getView().setTranslateX(minX);
        }
        if(player.getY() >= maxY) {
            player.getView().setTranslateY(maxY);
        } else if (player.getY() <= minY) {
            player.getView().setTranslateY(minY);
        }
    }

    /**
     *
     * Method that moves the objects forward
     * or backwards.
     *
     * @param forward boolean about forward movement.
     * @param back boolean about backwards movement.
     * @param player Which Player object.
     */
    public void movePlayer(boolean forward, boolean back, Player player){
        if(forward && !back){
            player.setVelocity(Math.cos(Math.toRadians(player.getRotate()))*player.getSpeedMultiplier(), Math.sin(Math.toRadians(player.getRotate()))*player.getSpeedMultiplier());
        } else if(back && !forward){
            player.setVelocity(-Math.cos(Math.toRadians(player.getView().getRotate()))*player.getSpeedMultiplier(), -Math.sin(Math.toRadians(player.getView().getRotate()))*player.getSpeedMultiplier());
        }else{
            player.setVelocity(0,0);
        }
    }

    /**
     *
     * Method that rotates the objects the right way.
     *
     * @param right boolean about right-movement.
     * @param left boolean about left-movement.
     * @param player Which Player object.
     */
    public void rotatePlayer(boolean right, boolean left, Player player){
        if (right && !left) {
            player.rotateLeft();
        } else if ( !right && left) {
            player.rotateRight();
        }
    }

    /**
     *
     * Adds bullets to the game
     * and plays sound.
     *
     * @param shoot boolean, if bullet is shot or not.
     * @param player Which Player it is.
     * @param isPistolLadet boolean, if the pistol is loaded or not.
     * @param bullets Takes the ArrayList as param.
     * @param sprite Sprite/Image for the bullet.
     */
    public void shootPlayer(boolean shoot, Player player, boolean isPistolLadet, List<Bullet> bullets, String sprite){
        if (shoot && isPistolLadet) {
            //Adder bulleten til gameworld og posisjonen er da samme som player
            bullets.add(new Bullet(sprite,player.getX()+(player.getHeigth()/2),player.getY()+(player.getWidth()/2), gameP, player, player.getRotate()));
            //resetter pistolklokka
            loadCount = 0;
            if(effect)
                firing.playonce();
        }
    }

    /**
     *
     * Goes through the explosion sprites.
     *
     * @param play the players.
     */
    public void bulletExplosion(Player play){
        for(int i = 0; i < 5; i++){
            boom[i].relocate(play.getX(),play.getY());
        }
        if(loadCount2!=0){
            gameP.getChildren().remove(boom[loadCount2-1]);
        }
        if(loadCount2!=5){
            gameP.getChildren().add(boom[loadCount2]);
        } else {
            play.setExploded(false);
        }
    }

    /**
     *
     * Method for updating lifepoints,
     * playing the die and hit sound,
     * updating the score and showing
     * the victory screen.
     *
     * @param play the players.
     * @param name name of the player.
     * @param pointer the shooter.
     */
    public void lifeUpdate(Player play, String name, Player pointer){
        if (play.getHp() != 1) {
            play.setHp(play.getHp() - 1);
            if(effect)
                hit.playonce();
        } else if(pointer.getScore() < 2){
            if(effect)
                die.play();
            pointer.setScore(pointer.getScore()+1);
            play.setHp(10);
            loadCount2=0;
            newRound();
        } else {
            if(effect)
                die.play();
            pointer.setScore(pointer.getScore()+1);
            play.setHp(play.getHp() - 1);
            timer.stop();
            //game.stop();
            if(effect)
                victory.play();
            victoryLabelScore.setText(player.getScore() + " : " + enemy.getScore());
            victoryLabelWinner.setText(name + " WON!");
            victoryP.setVisible(true);
            victoryP.setDisable(false);
        }
    }

    /**
     *
     * Method that controls what happends when
     * a bullet hits another player, a wall or
     * when it hits the end of the map.
     *
     * @param bullets the ArrayList of bullets.
     * @param play the player.
     * @param name name of the player.
     * @param pointer the one that is shooting.
     */
    public void bulletPhysics(List<Bullet> bullets, Player play, String name, Player pointer){
        for (int i = 0; i < bullets.size(); i++){
            if(bullets.get(i).isColliding(play)) {
                bullets.get(i).RemoveBullet(gameP);
                bullets.remove(i);
                lifeUpdate(play, name, pointer);
                loadCount2=0;
                play.setExploded(true);
            } else if (bullets.get(i).getView().getTranslateY() <= 0  || bullets.get(i).getView().getTranslateY() >= sceneheigth+25) {
                bullets.get(i).RemoveBullet(gameP);
                bullets.remove(i);
            } else if (bullets.get(i).getView().getTranslateX() <= -30  || bullets.get(i).getView().getTranslateX() >= scenewidth+25) {
                bullets.get(i).RemoveBullet(gameP);
                bullets.remove(i);
            } else {
                for(Wall j : maps.get(currentLevel).getWalls()) {
                    if(bullets.size() != 0) {
                        if (i < bullets.size() && bullets.get(i).isColliding(j)) {
                            bullets.get(i).RemoveBullet(gameP);
                            bullets.remove(i);
                        }
                    }
                }
            }
        }
        if(loadCount2 < load2 && play.getExploded()) {
            bulletExplosion(play);
        }
    }

    /**
     *
     * Method that controls what happends when a
     * player hits a wall.
     *
     * @param player Which player it is.
     */
    public void collisionWalls(Player player){
        for(Wall i : maps.get(currentLevel).getWalls()) {
            //spiller kommer fra venstre
            if (player.getX() >= i.getMinX() - player.getWidth() &&
                    player.getX() <= i.getMinX() - player.getWidth() + 5 &&
                    player.getY() >= i.getMinY() - player.getHeigth() &&
                    player.getY() <= i.getMaxY()) {
                player.getView().setTranslateX(i.getMinX() - player.getWidth());
            }
            //spiller kommer fra høyre
            else if(player.getX() >= i.getMaxX() - 5 &&
                    player.getX() <= i.getMaxX() &&
                    player.getY() >= i.getMinY() - player.getHeigth() &&
                    player.getY() <= i.getMaxY()) {
                player.getView().setTranslateX(i.getMaxX());
            }
            //spiller kommer fra toppen

            else if(player.getX() >= i.getMinX() - player.getWidth() &&
                    player.getX() <= i.getMaxX() &&
                    player.getY() >= i.getMinY() - player.getHeigth() &&
                    player.getY() <= i.getMinY() - player.getHeigth() + 5) {
                player.getView().setTranslateY(i.getMinY() - player.getHeigth());
            }
            //spiller kommer fra bunnen
            else if(player.getX() >= i.getMinX() - player.getWidth() &&
                    player.getX() <= i.getMaxX() &&
                    player.getY() >= i.getMaxY() -5 &&
                    player.getY() <= i.getMaxY() ) {
                player.getView().setTranslateY(i.getMaxY());
            }
        }
    }

    /**
     *
     * Method that sets the player to the
     * correct spawn coordinates, sets HP
     * back to 10, removes bullets and sets
     * the correct background image.
     */
    public void newRound(){
        player.getView().setRotate(0);
        enemy.getView().setRotate(180);
        for (Bullet b : bullets){
            b.RemoveBullet(gameP);
        }
        for (Bullet b : bullets2){
            b.RemoveBullet(gameP);
        }
        bullets.clear();
        bullets2.clear();
        enemy.setHp(10);
        player.setHp(10);
        if(currentLevel+1<maps.size()) {
            for (Wall i : maps.get(currentLevel).getWalls()) {
                i.removePane();
            }
            currentLevel = currentLevel + 1;
            for (Wall i : maps.get(currentLevel).getWalls()) {
                 i.addPane();
            }
        }

        background.setImage(new Image(maps.get(currentLevel).getMapBg()));
        player.getView().setTranslateX(maps.get(currentLevel).getSpawnPX());
        player.getView().setTranslateY(maps.get(currentLevel).getSpawnPY());
        enemy.getView().setTranslateX(maps.get(currentLevel).getSpawnEX());
        enemy.getView().setTranslateY(maps.get(currentLevel).getSpawnEY());
    }

    /**
     * Method that updates the game.
     */
    public void onUpdate() {
        boolean isWPressed = keyboardBitSet.get(KeyCode.W.ordinal());
        boolean isAPressed = keyboardBitSet.get(KeyCode.A.ordinal());
        boolean isSPressed = keyboardBitSet.get(KeyCode.S.ordinal());
        boolean isDPressed = keyboardBitSet.get(KeyCode.D.ordinal());
        boolean isVPressed = keyboardBitSet.get(KeyCode.V.ordinal());
        boolean isUpPressed = keyboardBitSet.get(KeyCode.UP.ordinal());
        boolean isDownPressed = keyboardBitSet.get(KeyCode.DOWN.ordinal());
        boolean isLeftPressed = keyboardBitSet.get(KeyCode.LEFT.ordinal());
        boolean isRightPressed = keyboardBitSet.get(KeyCode.RIGHT.ordinal());
        boolean isMPressed = keyboardBitSet.get(KeyCode.M.ordinal());
        boolean isSpacePressed = keyboardBitSet.get(KeyCode.SPACE.ordinal());
        boolean isEscPressed = keyboardBitSet.get(KeyCode.ESCAPE.ordinal());

        int countD = 1;
        loadCount += countD;
        if( loadCount > load) {
            loadCount = load;
        }
        boolean isLoaded = loadCount >= load;
        loadCount2 += countD;
        if(loadCount2 > load2) {
            loadCount2 = load2;
        }
        //Pause
        if(isSpacePressed || isEscPressed){
            timer.stop();
            gamePaused.setDisable(false);
            gamePaused.setVisible(true);
            gameRoot.setDisable(true);
            //game.stop();
        }

        //behandler kulekollisjon med person og utkant
        bulletPhysics(bullets, enemy, "BLUE PLAYER", player);
        bulletPhysics(bullets2, player, "RED PLAYER ", enemy);
        //skyte spiller
        shootPlayer(isVPressed,player,isLoaded,bullets,"/res/bulletBlue.png");
        shootPlayer(isMPressed,enemy,isLoaded,bullets2,"/res/bulletRed.png");
        //rotere spiller
        rotatePlayer(isLeftPressed,isRightPressed,enemy);
        rotatePlayer(isAPressed,isDPressed,player);
        //flytte spiller
        movePlayer(isWPressed, isSPressed, player);
        movePlayer(isUpPressed, isDownPressed,enemy);
        //kollisjon med kanten av banen
        boundsPlayer(player);
        boundsPlayer(enemy);
        //kollisjon med veggene spiller 1
        collisionWalls(player);
        collisionWalls(enemy);

        playerHp.setProgress((double)player.getHp()/10);
        enemyHp.setProgress((double)enemy.getHp()/10);
        currentScore.setText(player.getScore() + " : " + enemy.getScore());

        //oppdaterer posisjon
        bullets.forEach(Bullet::update);
        bullets2.forEach(Bullet::update);
        player.update();
        enemy.update();
    }
}
