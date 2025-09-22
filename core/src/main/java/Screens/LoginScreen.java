package Screens;

import GameLogic.Lang;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.Gdx.app;
import static com.badlogic.gdx.Gdx.files;

import com.elkinedwin.LogicaUsuario.ArchivoGuardar;
import com.elkinedwin.LogicaUsuario.LeerArchivo;
import com.elkinedwin.LogicaUsuario.ManejoArchivos;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import com.elkinedwin.LogicaUsuario.Usuario;

public class LoginScreen extends BaseScreen {

    private final Game game;

    private Skin skin;
    private Label lblTitle;
    private TextButton btnLogin, btnCrear, btnSalir;

    private Texture background;
    private SpriteBatch batch;

    public LoginScreen(Game game) { this.game = game; }

    @Override
    protected void onShow() {
        ManejoArchivos.iniciarCpadre();

        if (files.internal("fonts/pokemon_fire_red.ttf").exists()) {
            skin = buildSkin("fonts/pokemon_fire_red.ttf");
        } else {
            skin = buildMinimalSkin();
        }

        lblTitle = new Label(Lang.loginScreenTitle(), skin,
                skin.has("lblTitle", Label.LabelStyle.class) ? "lblTitle" : "default");
        btnLogin = new TextButton(Lang.loginButton(), skin);
        btnCrear = new TextButton(Lang.createPlayerButton(), skin);
        btnSalir = new TextButton(Lang.exitButton(), skin);

        btnLogin.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { loginDialog(); }});
        btnCrear.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { createPlayerDialog(); }});
        btnSalir.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { app.exit(); }});

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.top().padTop(40f);
        root.add(lblTitle).center().padBottom(70f).row();

        root.defaults().padTop(14f).padBottom(14f);
        root.add(btnLogin).center().row();
        root.add(btnCrear).center().row();
        root.add(btnSalir).center().padTop(28f).row();

        background = new Texture("ui/bg_general.png");
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(background, 0, 0, stage.getViewport().getScreenWidth(), stage.getViewport().getScreenHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (skin != null) skin.dispose();
        if (background != null) background.dispose();
        if (batch != null) batch.dispose();
    }

    // ======= filtros/validaciones simples (lógica común) =======
    private TextField.TextFieldFilter noCommaFilter() {
        return (tf, c) -> c != ',';  // permite todo menos coma
    }
    private boolean hasComma(String s){ return s != null && s.indexOf(',') >= 0; }
    private boolean notEmpty(String s){ return s != null && !s.trim().isEmpty(); }
    // ===========================================================

    private void loginDialog() {
        final Dialog dialog = new Dialog(Lang.dlgLoginTitle(), skin);
        Table content = dialog.getContentTable();
        content.pad(16f);
        content.defaults().pad(6f).fillX();

        final TextField user = new TextField("", skin, skin.has("tfSmall", TextField.TextFieldStyle.class) ? "tfSmall" : "default");
        user.setMessageText(Lang.fieldUser());
        user.setTextFieldFilter(noCommaFilter()); // permite símbolos/números, excepto coma

        final TextField password = new TextField("", skin, skin.has("tfSmall", TextField.TextFieldStyle.class) ? "tfSmall" : "default");
        password.setMessageText(Lang.fieldPassword());
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');
        password.setTextFieldFilter(noCommaFilter()); // permite símbolos/números, excepto coma

        final Label error = new Label("", skin, skin.has("error", Label.LabelStyle.class) ? "error" : "default");
        if (!skin.has("error", Label.LabelStyle.class)) error.setColor(Color.SALMON);

        content.add(new Label(Lang.fieldUser(), skin)).left().row();
        content.add(user).width(340f).row();
        content.add(new Label(Lang.fieldPassword(), skin)).left().row();
        content.add(password).width(340f).row();
        content.add(error).left().row();

        TextButton cancelBtn = new TextButton(Lang.dlgCancel(), skin);
        TextButton okBtn = new TextButton(Lang.dlgEnter(), skin);

        cancelBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { dialog.hide(); }});
        okBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                String u = user.getText().trim();
                String p = password.getText().trim();

                if (!notEmpty(u) || !notEmpty(p)) { error.setText(Lang.errFail()); return; }
                if (hasComma(u) || hasComma(p)) { error.setText("No se permite la coma (,)"); return; }

                try {
                    String path = ManejoArchivos.buscarUsuario(u);
                    if (path == null) { error.setText(Lang.errUserNotFound()); return; }

                    ManejoArchivos.setArchivo(u);

                    long ahora = System.currentTimeMillis();
                    ManejoUsuarios.UsuarioActivo = new Usuario(u, "", "", ahora);

                    LeerArchivo.cargarUsuario();
                    ManejoUsuarios.UsuarioActivo.recalcularTiempoPromedio();

                    String passArchivo = ManejoUsuarios.UsuarioActivo.getContrasena();
                    if (passArchivo == null || !passArchivo.equals(p)) {
                        ManejoUsuarios.UsuarioActivo = null;
                        error.setText(Lang.errWrongPassword());
                        return;
                    }

                    Long anterior = ManejoUsuarios.UsuarioActivo.getUltimaSesion();
                    if (anterior == null || anterior == 0L) {
                        ManejoUsuarios.UsuarioActivo.sesionAnterior = ahora;
                        ManejoUsuarios.UsuarioActivo.setUltimaSesion(ahora);
                    } else {
                        ManejoUsuarios.UsuarioActivo.sesionAnterior = anterior;
                    }
                    ManejoUsuarios.UsuarioActivo.sesionActual = ahora;

                    ArchivoGuardar.guardarFechas();
                    Lang.init(ManejoUsuarios.UsuarioActivo.getIdioma());

                    dialog.hide();
                    game.setScreen(new MenuScreen(game));

                } catch (Exception ex) {
                    error.setText(Lang.errFail());
                }
            }
        });

        Table bt = dialog.getButtonTable();
        bt.pad(0, 16f, 16f, 16f);
        bt.defaults().width(140f).height(54f).padLeft(8f).padRight(8f);
        bt.add(cancelBtn).padRight(35f);
        bt.add(okBtn);

        dialog.show(stage);
        stage.setKeyboardFocus(user);
    }

    private void createPlayerDialog() {
        final Dialog dialog = new Dialog(Lang.dlgCreateTitle(), skin);
        Table content = dialog.getContentTable();
        content.pad(16f);
        content.defaults().pad(6f).fillX();

        final TextField user = new TextField("", skin, skin.has("tfSmall", TextField.TextFieldStyle.class) ? "tfSmall" : "default");
        user.setMessageText(Lang.fieldUser());
        user.setTextFieldFilter(noCommaFilter()); // símbolos y números ok, excepto coma

        final TextField name = new TextField("", skin, skin.has("tfSmall", TextField.TextFieldStyle.class) ? "tfSmall" : "default");
        name.setMessageText(Lang.fieldName());
        // sin filtro -> permite espacios

        final TextField password = new TextField("", skin, skin.has("tfSmall", TextField.TextFieldStyle.class) ? "tfSmall" : "default");
        password.setMessageText(Lang.fieldPassword());
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');
        password.setTextFieldFilter(noCommaFilter()); // símbolos y números ok, excepto coma

        final Label error = new Label("", skin, skin.has("error", Label.LabelStyle.class) ? "error" : "default");
        if (!skin.has("error", Label.LabelStyle.class)) error.setColor(Color.SALMON);

        content.add(new Label(Lang.fieldUser(), skin)).left().row();
        content.add(user).width(340f).row();
        content.add(new Label(Lang.fieldName(), skin)).left().row();
        content.add(name).width(340f).row();
        content.add(new Label(Lang.fieldPassword(), skin)).left().row();
        content.add(password).width(340f).row();
        content.add(error).left().row();

        TextButton cancelBtn = new TextButton(Lang.dlgCancel(), skin);
        TextButton createBtn = new TextButton(Lang.dlgCreate(), skin);

        cancelBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { dialog.hide(); }});
        createBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                String u = user.getText().trim();
                String n = name.getText(); // nombre puede tener espacios
                String p = password.getText();

                if (!notEmpty(u) || !notEmpty(n) || !notEmpty(p)) { error.setText(Lang.errFail()); return; }
                if (hasComma(u) || hasComma(p)) { error.setText("No se permite la coma (,)"); return; }

                // Validación de fuerza “vaga”
                Usuario aux = new Usuario(u, n, p, System.currentTimeMillis());
                if (!aux.passValida(p)) {
                    error.setText("No cumple los requisitos");
                    return;
                }

                try {
                    String existe = ManejoArchivos.buscarUsuario(u);
                    if (existe != null) {
                        error.setText(Lang.errUserExists());
                        return;
                    }

                    ManejoArchivos.crearUsuario(n, u, p);

                    dialog.hide();
                    new Dialog(Lang.createdOkTitle(), skin)
                            .text(Lang.createdOkBody())
                            .button("OK", true)
                            .show(stage);

                } catch (Exception ex) {
                    error.setText(Lang.errFail());
                }
            }
        });

        Table bt = dialog.getButtonTable();
        bt.pad(0, 16f, 16f, 16f);
        bt.defaults().width(140f).height(54f).padLeft(8f).padRight(8f);
        bt.add(cancelBtn);
        bt.add(createBtn);

        dialog.show(stage);
        stage.setKeyboardFocus(user);
    }

    // ================= SKINS =================

    private static Skin buildSkin(String ttfPath) {
        Skin skin = new Skin();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(files.internal(ttfPath));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

        p.size = 66;
        p.color = Color.valueOf("E6DFC9");
        p.borderWidth = 2;
        p.borderColor = Color.BLACK;
        BitmapFont ui = generator.generateFont(p);

        p.size = 38;
        BitmapFont uiSmall = generator.generateFont(p);

        p.size = 136;
        BitmapFont lblTitle = generator.generateFont(p);
        generator.dispose();

        skin.add("ui", ui, BitmapFont.class);
        skin.add("uiSmall", uiSmall, BitmapFont.class);
        skin.add("lblTitlefont", lblTitle, BitmapFont.class);

        Pixmap pm = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        Texture white = new Texture(pm);
        pm.dispose();
        TextureRegionDrawable whiteDraw = new TextureRegionDrawable(new TextureRegion(white));
        skin.add("white", white);

        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = uiSmall;
        ls.fontColor = ui.getColor();
        skin.add("default", ls);

        Label.LabelStyle lst = new Label.LabelStyle();
        lst.font = lblTitle;
        lst.fontColor = lblTitle.getColor();
        skin.add("lblTitle", lst);

        Label.LabelStyle lse = new Label.LabelStyle();
        lse.font = uiSmall;
        lse.fontColor = Color.SALMON;
        skin.add("error", lse);

        TextButton.TextButtonStyle bs = new TextButton.TextButtonStyle();
        bs.font = ui;
        bs.fontColor = ui.getColor();
        skin.add("default", bs);

        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = ui;
        tfs.fontColor = Color.WHITE;
        tfs.cursor = whiteDraw.tint(Color.WHITE);
        tfs.selection = whiteDraw.tint(new Color(1, 1, 1, 0.25f));
        tfs.background = whiteDraw.tint(new Color(0f, 0f, 0f, 0.6f));
        skin.add("default", tfs);

        TextField.TextFieldStyle tfsSmall = new TextField.TextFieldStyle(tfs);
        tfsSmall.font = uiSmall;
        skin.add("tfSmall", tfsSmall);

        Window.WindowStyle ws = new Window.WindowStyle();
        ws.titleFont = uiSmall;
        ws.titleFontColor = Color.WHITE;
        ws.background = whiteDraw.tint(new Color(0, 0, 0, 1));
        skin.add("default", ws);

        return skin;
    }

    private Skin buildMinimalSkin() {
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.25f);
        skin.add("defaultfont", font, BitmapFont.class);

        Pixmap pixMap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        pixMap.setColor(Color.WHITE);
        pixMap.fill();
        Texture white = new Texture(pixMap);
        pixMap.dispose();
        skin.add("white", white);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = font;
        tfs.fontColor = Color.WHITE;
        tfs.cursor = skin.newDrawable("white", Color.WHITE);
        tfs.selection = skin.newDrawable("white", new Color(1, 1, 1, 0.25f));
        tfs.background = skin.newDrawable("white", new Color(0f, 0f, 0f, 1f));
        skin.add("default", tfs);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = skin.newDrawable("white", new Color(1f, 1f, 1f, 0.85f));
        buttonStyle.over = skin.newDrawable("white", new Color(1f, 1f, 1f, 0.95f));
        buttonStyle.down = skin.newDrawable("white", new Color(0.90f, 0.90f, 0.90f, 1f));
        buttonStyle.fontColor = Color.BLACK;
        skin.add("default", buttonStyle);

        Window.WindowStyle ws = new Window.WindowStyle();
        ws.titleFont = font;
        ws.titleFontColor = Color.WHITE;
        ws.background = skin.newDrawable("white", new Color(0f, 0f, 0f, 1f));
        skin.add("default", ws);

        Label.LabelStyle lse = new Label.LabelStyle();
        lse.font = font;
        lse.fontColor = Color.SALMON;
        skin.add("error", lse);

        return skin;
    }
}
