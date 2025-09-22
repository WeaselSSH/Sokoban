package Screens;

import static com.badlogic.gdx.Gdx.files;

import GameLogic.Lang;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.elkinedwin.LogicaUsuario.ArchivoGuardar;
import com.elkinedwin.LogicaUsuario.AudioBus;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import com.elkinedwin.LogicaUsuario.Usuario;

import java.io.IOException;

public class MenuScreen extends BaseScreen {

    private final Game game;

    private Label titleLabel;
    private TextButton playButton, settingsButton, historyButton, rankingButton, logoutButton;

    private BitmapFont titleFont, buttonFont;

    private Label userLabel;
    private Texture avatarTexture;
    private Image avatarImage;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        int language = (ManejoUsuarios.UsuarioActivo != null)
                ? ManejoUsuarios.UsuarioActivo.getIdioma()
                : 1;
        Lang.init(language);

        titleFont = createOutlinedFont(136, Color.valueOf("E6DFC9"), 2, Color.BLACK);
        buttonFont = createOutlinedFont(72, Color.valueOf("E6DFC9"), 2, Color.BLACK);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = buttonFont.getColor();

        titleLabel = new Label(Lang.gameTitle(), titleStyle);
        playButton = new TextButton(Lang.play(), buttonStyle);
        settingsButton = new TextButton(Lang.settings(), buttonStyle);
        historyButton = new TextButton(Lang.history(), buttonStyle);
        rankingButton = new TextButton("Ranking", buttonStyle);
        logoutButton = new TextButton(Lang.logout(), buttonStyle);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                boolean tutorialDone = false;
                try {
                    if (ManejoUsuarios.UsuarioActivo != null) {
                        tutorialDone = ManejoUsuarios.UsuarioActivo.getTutocomplete();
                    }
                } catch (Exception ignored) {
                }
                if (!tutorialDone) {
                    game.setScreen(new TutorialScreen(game));
                } else {
                    game.setScreen(new StageScreen(game));
                }
            }
        });
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new ConfigScreen(game));
            }
        });
        historyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new HistorialScreen(game));
            }
        });
        rankingButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new RankingScreen(game));
            }
        });
        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                try {
                    ArchivoGuardar.guardarTodoCerrarSesion();
                } catch (IOException ignored) {
                } finally {
                    ManejoUsuarios.UsuarioActivo = null;
                    game.setScreen(new LoginScreen(game));
                }
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.top().padTop(40f);
        root.add(titleLabel).center().padBottom(60f).row();
        root.defaults().padTop(18f).padBottom(18f).center();
        root.add(playButton).row();
        root.add(settingsButton).row();
        root.add(historyButton).row();
        root.add(rankingButton).row();
        root.add(logoutButton).padTop(36f).row();

        int volumeConfig = 70;
        try {
            if (ManejoUsuarios.UsuarioActivo != null && ManejoUsuarios.UsuarioActivo.configuracion != null) {
                Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get("Volumen");
                if (v != null) {
                    volumeConfig = v;
                }
            }
        } catch (Exception ignored) {
        }
        AudioBus.setMasterVolume(volumeConfig / 100f);

        Usuario user = ManejoUsuarios.UsuarioActivo;
        String username = (user != null && user.getUsuario() != null) ? user.getUsuario() : Lang.guest();

        String avatarPath = "ui/default_avatar.png";
        if (user != null && user.avatar != null && !user.avatar.trim().isEmpty() && files.internal(user.avatar).exists()) {
            avatarPath = user.avatar;
        }

        Label.LabelStyle userStyle = new Label.LabelStyle(buttonFont, Color.WHITE);
        userLabel = new Label(username, userStyle);
        userLabel.setFontScale(1.1f);

        avatarTexture = new Texture(avatarPath);
        avatarImage = new Image(avatarTexture);
        avatarImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new MiPerfilScreen(game));
            }
        });

        Table topRight = new Table();
        topRight.setFillParent(true);
        topRight.top().right().padTop(14f).padRight(16f);
        topRight.add(userLabel).padRight(10f).center();
        topRight.add(avatarImage).size(112f, 112f).center();
        stage.addActor(topRight);
    }

    @Override
    public void hide() {
        if (avatarTexture != null) {
            avatarTexture.dispose();
            avatarTexture = null;
        }
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (buttonFont != null) {
            buttonFont.dispose();
        }
    }
}
