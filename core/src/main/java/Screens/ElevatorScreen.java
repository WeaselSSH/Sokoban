package Screens;

import GameLogic.GameConfig;
import GameLogic.TileMap;
import GameLogic.Lang;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Gdx.input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;

public final class ElevatorScreen extends BasePlayScreen {

    private Texture elevatorFloor, elevatorButtons, elevatorWall, blackTexture;
    private Pixmap pixmap;

    private boolean nearButtons = false;
    private boolean selecting = false;
    private boolean ignoreEnter = false;
    private int selectedIndex = 0;
    private final int levelList[] = {1, 2, 3, 4, 5, 6, 7};

    // transición
    private boolean transitioning = false;
    private float transitionTime = 0f;
    private int pendingLevel = -1;
    private Sound sMoving, sFinished;
    private boolean movingPlayed = false;

    // Iconos estado
    private Texture texOk, texLock;

    // Colores
    private static final Color COLOR_TITLE = new Color(0.95f, 0.95f, 1f, 1f);
    private static final Color COLOR_UNLOCKED = new Color(0.94f, 0.94f, 1f, 1f);
    private static final Color COLOR_LOCKED = new Color(1f, 1f, 1f, 0.80f);
    private static final Color SHADOW_GREEN = new Color(0.30f, 1f, 0.30f, 0.45f);
    private static final Color COLOR_SELECTED = new Color(1f, 0.85f, 0.20f, 1f);

    private final GlyphLayout glyph = new GlyphLayout();

    private float animTime = 0f;

    // Layout
    private static final float PANEL_X = 80f;
    private static final float TITLE_Y = GameConfig.PX_HEIGHT - 24f;
    private static final float START_Y = TITLE_Y - 28f;
    private static final float STEP_Y = 26f;
    private static final float ROW_H = STEP_Y;
    private static final float PANEL_PADDING_H = 12f;
    private static final float PANEL_PADDING_V = 10f;

    // Alineación de iconos
    private static final float ICON_GAP_X = 8f;
    private static final float LOCK_SIZE = 18f;
    private static final float OK_BASE = 20f;
    private static final float OK_PULSE = 2f;

    public ElevatorScreen(Game app) {
        super(app, 9);
    }

    @Override
    protected void onShowExtra() {
        prevPushes = game.getPlayer().getPushCount();
        elevatorFloor = load("textures/elevator_floor.png");
        elevatorWall = load("textures/elevator_wall.png");
        elevatorButtons = load("textures/elevator_buttons.png");

        pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1);
        pixmap.fill();
        blackTexture = new Texture(pixmap);

        sMoving = com.elkinedwin.LogicaUsuario.AudioX.newSound("audios/elevator_moving.wav");
        sFinished = com.elkinedwin.LogicaUsuario.AudioX.newSound("audios/elevator_finished.wav");

        texOk = new Texture("../Imagenes/OK.png");
        texLock = new Texture("../Imagenes/Lock.png");
    }

    private boolean isUnlocked(int lvl) {
        if (lvl <= 1) {
            return true;
        }
        try {
            return ManejoUsuarios.UsuarioActivo != null
                    && ManejoUsuarios.UsuarioActivo.getNivelCompletado(lvl - 1);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    protected void onUpdate(float delta) {
        animTime += delta;

        if (transitioning) {
            if (!movingPlayed) {
                movingPlayed = true;
                sMoving.play(1f);
                if (bgMusic != null) {
                    bgMusic.pause();
                }
                directionQueue.clear();
                heldDirection = null;
                moveRequested = false;
                tweenActive = false;
            }
            transitionTime += delta;
            if (transitioning && pendingLevel >= 0) {
                transitionTime += delta;

                if (transitionTime >= 5.0f && transitionTime < 5.1f) {
                    sMoving.stop();
                    sFinished.play(1f);
                }
                if (transitionTime >= 9f) {
                    app.setScreen(new GameScreen(app, pendingLevel));
                    return;
                }
                return;
            }
            return;
        }

        if (selecting && input.isKeyJustPressed(Keys.ESCAPE)) {
            selecting = false;
            return;
        }

        super.onUpdate(delta);
        if (paused) {
            return;
        }

        // detectar si estamos frente a los botones del elevador
        nearButtons = false;
        TileMap map = game.getMap();
        int px = game.getPlayer().getX();
        int py = game.getPlayer().getY();

        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                if (map.getTile(col, row) == TileMap.ELEVATOR_BUTTONS) {
                    if (px == col && py == row - 1) {
                        nearButtons = true;
                        break;
                    }
                }
            }
            if (nearButtons) {
                break;
            }
        }

        // abrir selección
        if (!selecting && nearButtons && Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            selecting = true;
            selectedIndex = 0;
            ignoreEnter = true;
        }

        if (selecting) {
            if (ignoreEnter) {
                if (!input.isKeyPressed(Keys.ENTER)) {
                    ignoreEnter = false;
                }
                return;
            }
            if (input.isKeyJustPressed(Keys.UP)) {
                selectedIndex = (selectedIndex - 1 + levelList.length) % levelList.length;
            } else if (input.isKeyJustPressed(Keys.DOWN)) {
                selectedIndex = (selectedIndex + 1) % levelList.length;
            } else if (input.isKeyJustPressed(Keys.ENTER)) {
                int levelId = levelList[selectedIndex];
                if (!isUnlocked(levelId)) {
                    return;
                }
                pendingLevel = levelId;
                selecting = false;
                transitioning = true;
                transitionTime = 0f;
                movingPlayed = false;
                return;
            }
        }
    }

    @Override
    protected GameLogic.Directions readHeldDirection() {
        if (selecting || transitioning) {
            return null;
        }
        return super.readHeldDirection();
    }

    @Override
    protected void onDrawMap() {
        TileMap map = game.getMap();

        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                int dx = col * GameConfig.TILE_SIZE;
                int dy = row * GameConfig.TILE_SIZE;
                batch.draw(elevatorFloor, dx, dy, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                char ch = map.getTile(col, row);
                if (ch == TileMap.WALL) {
                    batch.draw(wallTexture, dx, dy, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                } else if (ch == TileMap.BLACK) {
                    batch.draw(blackTexture, dx, dy, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                }
            }
        }

        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                int dx = col * GameConfig.TILE_SIZE;
                int dy = row * GameConfig.TILE_SIZE;
                char ch = map.getTile(col, row);
                if (ch == TileMap.ELEVATOR_BUTTONS) {
                    float drawW = 16f, drawH = 49f;
                    float seamX = dx + GameConfig.TILE_SIZE;
                    float drawX = seamX - drawW;
                    float drawY = dy;
                    batch.draw(elevatorButtons, drawX, drawY, drawW, drawH);
                } else if (ch == TileMap.ELEVATOR_WALL) {
                    float drawW = 65f, drawH = 49f;
                    batch.draw(elevatorWall, dx, dy, drawW, drawH);
                }
            }
        }
    }

    private void drawShadowText(String text, float x, float y, Color shadow, Color main) {
        Color old = font.getColor();
        font.setColor(shadow);
        font.draw(batch, text, x + 1f, y);
        font.draw(batch, text, x - 1f, y);
        font.draw(batch, text, x, y + 1f);
        font.draw(batch, text, x, y - 1f);
        font.setColor(main);
        font.draw(batch, text, x, y);
        font.setColor(old);
    }

    @Override
    protected void onDrawHUD() {
        if (transitioning) {
            float halfW = GameConfig.PX_WIDTH / 2f;
            float prog = Math.min(1f, transitionTime / 2f);
            float doorOffset = halfW * prog;

            // fondo negro
            batch.setColor(Color.BLACK);
            batch.draw(blackTexture, 0, 0, GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT);

            // puertas
            batch.setColor(Color.DARK_GRAY);
            batch.draw(blackTexture, 0, 0, halfW - doorOffset, GameConfig.PX_HEIGHT);
            batch.draw(blackTexture, halfW + doorOffset, 0, halfW - doorOffset, GameConfig.PX_HEIGHT);
            batch.setColor(Color.WHITE);

            // Texto central: "Entrando al Nivel N"
            String msg = Lang.elevatorEnteringLevel() + " " + pendingLevel;
            float prevScaleX = font.getData().scaleX;
            float prevScaleY = font.getData().scaleY;

            font.getData().setScale(2.0f);
            glyph.setText(font, msg);

            float cx = (GameConfig.PX_WIDTH - glyph.width) * 0.5f;
            float cy = (GameConfig.PX_HEIGHT + glyph.height) * 0.5f;

            font.setColor(new Color(0, 0, 0, 0.55f));
            font.draw(batch, msg, cx + 2f, cy - 2f);

            font.setColor(Color.WHITE);
            font.draw(batch, msg, cx, cy);

            font.getData().setScale(prevScaleX, prevScaleY);
            font.setColor(Color.WHITE);
            return;
        }

        if (!selecting) {
            if (nearButtons) {
                font.setColor(Color.WHITE);
                font.draw(batch, Lang.elevatorHintOpen(), 10f, 52f);
                font.setColor(Color.WHITE);
            }
            return;
        }

        // panel
        float listHeight = levelList.length * ROW_H;
        float panelYTop = TITLE_Y + PANEL_PADDING_V;
        float panelYBottom = START_Y - listHeight - PANEL_PADDING_V + 10f;
        float panelH = panelYTop - panelYBottom;
        float panelW = 220f;

        batch.setColor(0f, 0f, 0f, 0.55f);
        batch.draw(blackTexture, PANEL_X - PANEL_PADDING_H, panelYBottom,
                panelW + PANEL_PADDING_H * 2f, panelH);
        batch.setColor(Color.WHITE);

        // Título
        Color old = font.getColor();
        font.setColor(COLOR_TITLE);
        font.draw(batch, Lang.elevatorSelectTitle(), PANEL_X, TITLE_Y);
        font.setColor(old);

        // Lista
        float y = START_Y;
        float cap = font.getCapHeight();
        float centerOffset = cap * 0.55f;

        for (int i = 0; i < levelList.length; i++) {
            int lvl = levelList[i];
            boolean unlocked = isUnlocked(lvl);

            String prefix = (i == selectedIndex) ? ">" : " ";
            String label = prefix + " " + Lang.elevatorLevel() + " " + lvl;
            glyph.setText(font, label);

            float iconX = PANEL_X + glyph.width + ICON_GAP_X;
            float centerY = y - centerOffset;

            if (unlocked) {
                float pulse = 0.4f + 0.4f * MathUtils.sin(animTime * 2f);
                Color glow = new Color(0.3f, 1f, 0.3f, pulse);
                drawShadowText(label, PANEL_X, y, glow, (i == selectedIndex) ? COLOR_SELECTED : COLOR_UNLOCKED);

                float size = OK_BASE + OK_PULSE * MathUtils.sin(animTime * 3f);
                batch.draw(texOk, iconX, centerY - size / 2f, size, size);
            } else {
                font.setColor(COLOR_LOCKED);
                font.draw(batch, label, PANEL_X, y);
                font.setColor(old);

                float alpha = 0.65f + 0.35f * MathUtils.sin(animTime * 2f);
                batch.setColor(1f, 1f, 1f, alpha);
                batch.draw(texLock, iconX, centerY - LOCK_SIZE / 2f, LOCK_SIZE, LOCK_SIZE);
                batch.setColor(Color.WHITE);
            }

            y -= STEP_Y;
        }
    }

    @Override
    protected void onDisposeExtra() {
        elevatorFloor.dispose();
        elevatorWall.dispose();
        elevatorButtons.dispose();
        blackTexture.dispose();
        pixmap.dispose();
        if (sMoving != null) {
            sMoving.dispose();
        }
        if (sFinished != null) {
            sFinished.dispose();
        }
        if (texOk != null) {
            texOk.dispose();
        }
        if (texLock != null) {
            texLock.dispose();
        }
    }
}
