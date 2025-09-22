package Screens;

import static com.badlogic.gdx.Gdx.input;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Texture;
import java.util.concurrent.ArrayBlockingQueue;

import GameLogic.GameConfig;
import GameLogic.MovementThread;
import GameLogic.TileMap;
import GameLogic.Lang;

import com.elkinedwin.LogicaUsuario.AudioX;

public final class GameScreen extends BasePlayScreen {

    private Texture boxTexture, boxTexturePlaced, targetTexture;

    private int kUp, kDown, kLeft, kRight, kReset;

    public GameScreen(Game app, int level) {
        super(app, level);
    }

    @Override
    protected void onShowExtra() {
        prevPushes = game.getPlayer().getPushCount();

        boxTexture = load("textures/box.png");
        boxTexturePlaced = load("textures/boxPlaced.png");
        targetTexture = load("textures/target.png");

        boxPlacedSound = AudioX.newSound("audios/box_placed.wav");

        kUp = getCfgKey("MoverArriba", Input.Keys.UP);
        kDown = getCfgKey("MoverAbajo", Input.Keys.DOWN);
        kLeft = getCfgKey("MoverIzq", Input.Keys.LEFT);
        kRight = getCfgKey("MoverDer", Input.Keys.RIGHT);
        kReset = getCfgKey("Reiniciar", Input.Keys.R);

        sUp = keyLabel(kUp);
        sDown = keyLabel(kDown);
        sLeft = keyLabel(kLeft);
        sRight = keyLabel(kRight);
        sReset = keyLabel(kReset);
    }

    @Override
    protected void onUpdate(float delta) {
        super.onUpdate(delta);
        if (paused) {
            return;
        }
        if (input.isKeyJustPressed(kReset)) {
            resetLevel();
        }
    }

    private void resetLevel() {
        AudioX.play(resetLevelSound, 1.0f);
        notifyRestart();
        try {
            if (movementThreadLogic != null) {
                movementThreadLogic.stop();
            }
            if (movementThread != null) {
                movementThread.interrupt();
            }
        } catch (Exception ignored) {
        }

        game.startLevel(level);
        setPlayer(game.getPlayer());

        timeChronometer = 0f;
        tweenActive = false;
        moveRequested = false;

        prevX = game.getPlayer().getX();
        prevY = game.getPlayer().getY();
        prevPushes = game.getPlayer().getPushCount();

        directionQueue = new ArrayBlockingQueue<>(1);
        movementThreadLogic = new MovementThread(directionQueue, game.getMap(), game.getPlayer());
        movementThread = new Thread(movementThreadLogic);
        movementThread.setDaemon(true);
        movementThread.start();
    }

    @Override
    protected void onDrawMap() {
        TileMap map = game.getMap();
        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                int px = col * GameConfig.TILE_SIZE;
                int py = row * GameConfig.TILE_SIZE;

                batch.draw(floorTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);

                char ch = map.getTile(col, row);
                switch (ch) {
                    case TileMap.WALL:
                        batch.draw(wallTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        break;
                    case TileMap.TARGET:
                        batch.draw(targetTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        break;
                    case TileMap.BOX:
                        batch.draw(boxTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        break;
                    case TileMap.BOX_ON_TARGET:
                        batch.draw(targetTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        batch.draw(boxTexturePlaced, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onDrawHUD() {
        int moves = game.getPlayer().getMoveCount();
        int pushes = game.getPlayer().getPushCount();

        int totalSeconds = (int) timeChronometer;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);

        BitmapFont f = font;
        f.draw(batch,
                Lang.hudLevel() + " " + level
                + "  " + Lang.hudSteps() + ": " + moves
                + "  " + Lang.hudPushes() + ": " + pushes
                + "  " + Lang.hudTime() + ": " + timeStr,
                6, GameConfig.PX_HEIGHT - 6);

        if (level == 0) {
            float x = 6f, y = 36f;
            font.draw(batch, Lang.hudControls() + ":", x, y + 24);
            font.draw(batch, Lang.cfgUp() + ": " + sUp + "   " + Lang.cfgDown() + ": " + sDown, x, y + 12);
            font.draw(batch, Lang.cfgLeft() + ": " + sLeft + "   " + Lang.cfgRight() + ": " + sRight
                    + "   " + Lang.cfgRestart() + ": " + sReset, x, y);
        }
    }

    @Override
    protected void onDisposeExtra() {
        boxTexture.dispose();
        boxTexturePlaced.dispose();
        targetTexture.dispose();
        boxPlacedSound.dispose();
    }

    private String keyLabel(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                return "Flecha arriba";
            case Input.Keys.DOWN:
                return "Flecha abajo";
            case Input.Keys.LEFT:
                return "Flecha izquierda";
            case Input.Keys.RIGHT:
                return "Flecha derecha";
            default: {
                String s = Input.Keys.toString(keycode);
                return (s != null && !s.isEmpty()) ? s : ("[" + keycode + "]");
            }
        }
    }
}
