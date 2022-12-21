package ch.epfl.cs107.play.game.icrogue;


import ch.epfl.cs107.play.game.Sound;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.Screens.GameOverScreen;
import ch.epfl.cs107.play.game.icrogue.Screens.PauseScreen;
import ch.epfl.cs107.play.game.icrogue.Screens.WinScreen;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.area.Level;
import ch.epfl.cs107.play.game.icrogue.area.level0.Level0;
import ch.epfl.cs107.play.game.icrogue.area.level1.Level1;
import ch.epfl.cs107.play.game.icrogue.hud.HUD;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

public class ICRogue extends AreaGame {
    public final static float CAMERA_SCALE_FACTOR = 13.f;

    private Area currentArea;

    public static Sound sound = new Sound();
    private ICRoguePlayer player;
    private Level level;
    private HUD hud;
    private boolean isPaused = false;
    private boolean winScreen = false;
    private boolean gameOver = false;
    private int levelToLoad = 1;

    private void initLevel(){
       playMusic(0);
        winScreen = false;
        gameOver = false;
        level = loadLevel();
        currentArea = setCurrentArea(level.getFirstRoomTitle(), true);
        player = new ICRoguePlayer(currentArea, Orientation.UP, new DiscreteCoordinates(2,2), "zelda/player");
        player.enterArea(currentArea, new DiscreteCoordinates(2,2));
        hud = new HUD();
        currentArea.registerActor(hud);
    }

    private Level loadLevel(){
        switch (levelToLoad){
            case 0 -> {return new Level0(this);}
            default -> {return new Level1(this);}
        }
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            initLevel();
            addArea(new PauseScreen());
            addArea(new WinScreen());
            addArea(new GameOverScreen());
            return true;
        }
        return false;
    }

   /*
    private void initArea(String areaKey) {

        Tuto2Area area = (Tuto2Area)setCurrentArea(areaKey, true);
        DiscreteCoordinates coords = area.getPlayerSpawnPosition();
        player = new GhostPlayer(area, Orientation.DOWN, coords,"ghost.1");
        player.enterArea(area, coords);
        player.centerCamera();

    }
    */
    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getWindow().getKeyboard();
        hud.updateHUD((int) player.getHp(), player.hasKey());

        if(player.isChangingRoom()){
            switchRoom(player.getDestinationRoom(), player.getArrivalCoordinate());
        }
        if(keyboard.get(Keyboard.ESCAPE).isPressed()){
            if(!isPaused){
                setCurrentArea("Pause", true);
                isPaused = true;
            }
            else {
                setCurrentArea(currentArea.getTitle(), false);
                isPaused = false;
            }
        }

        if(level.isOn() && !winScreen){
            winScreen = true;
            setCurrentArea("Win", true);
            levelToLoad += 1;
        }


        if(player.getHp()<=0 && !gameOver){
            gameOver = true;
            setCurrentArea("GameOver", true);
            levelToLoad = 0;
        }

        if((winScreen || gameOver) && keyboard.get(Keyboard.ENTER).isPressed()){
            initLevel();
        }
        super.update(deltaTime);

    }

    @Override
    public void end() {
    }

    @Override
    public String getTitle() {
        return "ICRogue";
    }
    protected void switchRoom(String destinationRoom, DiscreteCoordinates playerArrivalCoordinates){
        currentArea.unregisterActor(hud);
        player.leaveArea();
        currentArea = setCurrentArea(destinationRoom, false);
        currentArea.registerActor(hud);
        player.enterArea(currentArea, playerArrivalCoordinates);
    }
    public void playMusic (int i){
        sound.setFile(i);
        sound.play();
        sound.loop();
    }
    public static void stopMusic()
     {
        sound.stop();
    }
    public static void playSE(int i) {
        sound.setFile(i);
        sound.play();
    }
    public static void closeSE(int i) {
        sound.setFile(i);
        sound.close();
    }
}
