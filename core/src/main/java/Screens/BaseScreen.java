package Screens;

import static com.badlogic.gdx.Gdx.input;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;
import com.elkinedwin.LogicaUsuario.AudioBus;
import com.elkinedwin.LogicaUsuario.AudioX;

public abstract class BaseScreen implements Screen {

    protected final Color BACKGROUND = new Color(34 / 255f, 32 / 255f, 52 / 255f, 1f);
    protected Stage stage;

    protected Music bgMusic;

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        input.setInputProcessor(stage);
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

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BACKGROUND);
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
        stage.dispose();
    }
}
