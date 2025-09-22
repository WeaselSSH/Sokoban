package Screens;

import static com.badlogic.gdx.Gdx.input;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.elkinedwin.LogicaUsuario.AudioBus;
import com.elkinedwin.LogicaUsuario.AudioX;

public abstract class BaseScreen implements Screen {

    protected Stage stage;

    protected Music bgMusic;
    private Texture backgroundTexture;

    private FreeTypeFontGenerator sharedFontGenerator;

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        input.setInputProcessor(stage);

        if (backgroundTexture == null) {
            backgroundTexture = new Texture("ui/bg_general.png");
            Image bg = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
            bg.setFillParent(true);
            stage.addActor(bg);
        }

        if (sharedFontGenerator == null) {
            FileHandle ttf = Gdx.files.internal("fonts/pokemon_fire_red.ttf");
            if (ttf.exists()) {
                sharedFontGenerator = new FreeTypeFontGenerator(ttf);
            }
        }

        startBgmIfNeeded();
        onShow();
    }

    protected abstract void onShow();

    private void startBgmIfNeeded() {
        if (bgMusic != null) {
            return;
        }
        bgMusic = AudioX.newMusic("audios/menu_bg_song.mp3");
        bgMusic.setLooping(true);
        bgMusic.play();
    }

    public void setBgmVolume(float volume) {
        if (bgMusic != null) {
            bgMusic.setVolume(volume);
        }
    }

    protected Texture makeSolidTexture(int r, int g, int b, int a) {
        Pixmap pm = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pm.setColor(r / 255f, g / 255f, b / 255f, a / 255f);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    protected BitmapFont createOutlinedFont(int sizePx, Color fillColor, int borderWidthPx, Color borderColor) {
        if (sharedFontGenerator == null) {
            // Fallback sin TTF
            BitmapFont fallback = new BitmapFont();
            fallback.setColor(fillColor);
            return fallback;
        }
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = sizePx;
        p.color = fillColor;
        p.borderWidth = borderWidthPx;
        p.borderColor = borderColor;
        return sharedFontGenerator.generateFont(p);
    }

    protected BitmapFont createOutlinedFont(int sizePx, String fillHex, int borderWidthPx, String borderHex) {
        return createOutlinedFont(sizePx, Color.valueOf(fillHex), borderWidthPx, Color.valueOf(borderHex));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        if (bgMusic != null) {
            bgMusic.stop();
            AudioBus.unregisterMusic(bgMusic);
            bgMusic.dispose();
            bgMusic = null;
        }
    }

    @Override
    public void dispose() {
        if (bgMusic != null) {
            bgMusic.stop();
            AudioBus.unregisterMusic(bgMusic);
            bgMusic.dispose();
            bgMusic = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        if (sharedFontGenerator != null) {
            sharedFontGenerator.dispose();
            sharedFontGenerator = null;
        }
        stage.dispose();
    }
}
