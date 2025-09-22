package Screens;

import GameLogic.Lang;
import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import com.elkinedwin.LogicaUsuario.Usuario;
import com.elkinedwin.LogicaUsuario.ManejoArchivos;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MiPerfilScreen extends BaseScreen {

    private final Game game;

    // Fuentes
    private FreeTypeFontGenerator fontGenerator;
    private BitmapFont titleFont, subtitleFont, bodyFont, smallFont, largeFont;

    // Avatar
    private Texture avatarTexture;
    private Image avatarImage;

    // Texturas UI
    private Texture panelTexture, dividerTexture, editIconTexture;

    // Labels dinámicos
    private Label labelNameValue;
    private Label labelUserValue;
    private Label labelPasswordValue;

    // Recursos de diálogos/inputs
    private Texture dialogBackgroundTexture, textfieldBackgroundTexture, textfieldSelectionTexture, textfieldCursorTexture, buttonClearTexture;
    private TextureRegionDrawable dialogBackgroundDrawable, textfieldBackgroundDrawable, textfieldSelectionDrawable, textfieldCursorDrawable, buttonClearDrawable;
    private Window.WindowStyle cachedWindowStyle;
    private TextField.TextFieldStyle cachedTextFieldStyle;
    private TextButton.TextButtonStyle cachedTextButtonStyle;

    public MiPerfilScreen(Game game) { this.game = game; }

    @Override
    protected void onShow() {
        fontGenerator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));
        titleFont   = generateFontWithOutline(88, "E6DFC9", 3, "000000");
        subtitleFont= generateFontWithOutline(48, "E6DFC9", 2, "000000");
        bodyFont    = generateFontWithOutline(34, "E6DFC9", 2, "000000");
        smallFont   = generateFontWithOutline(26, "BFC4D0", 1, "000000");
        largeFont   = generateFontWithOutline(60, "FFFFFF", 3, "000000");

        // Estilos
        Label.LabelStyle titleStyle      = new Label.LabelStyle(titleFont, titleFont.getColor());
        Label.LabelStyle subtitleStyle   = new Label.LabelStyle(subtitleFont, subtitleFont.getColor());
        Label.LabelStyle keyStyle        = new Label.LabelStyle(bodyFont,  new Color(1,1,1,0.85f));
        Label.LabelStyle valueStyle      = new Label.LabelStyle(bodyFont,  Color.WHITE);
        Label.LabelStyle cellStyle       = new Label.LabelStyle(bodyFont,  Color.WHITE);
        Label.LabelStyle tableHeadStyle  = new Label.LabelStyle(bodyFont,  new Color(1,1,1,0.95f));
        Label.LabelStyle hintStyle       = new Label.LabelStyle(smallFont, smallFont.getColor());
        Label.LabelStyle largeValueStyle = new Label.LabelStyle(largeFont, largeFont.getColor());

        // Paneles y divisores
        panelTexture   = makeColorTexture(255, 255, 255, 22);
        dividerTexture = makeColorTexture(255, 255, 255, 38);

        // Icono editar
        String editIconPath = "../Imagenes/Editar.png";
        if (files.internal(editIconPath).exists()) {
            editIconTexture = new Texture(files.internal(editIconPath));
        } else if (files.internal("ui/edit.png").exists()) {
            editIconTexture = new Texture(files.internal("ui/edit.png"));
        } else {
            editIconTexture = makeColorTexture(255, 255, 255, 180);
        }
        ImageButton.ImageButtonStyle editButtonStyle = new ImageButton.ImageButtonStyle();
        editButtonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(editIconTexture));

        TextureRegionDrawable panelBg   = new TextureRegionDrawable(new TextureRegion(panelTexture));
        TextureRegionDrawable dividerBg = new TextureRegionDrawable(new TextureRegion(dividerTexture));

        // Root
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Botón volver (transparente)
        TextButton.TextButtonStyle backStyle = new TextButton.TextButtonStyle();
        backStyle.font = bodyFont;
        backStyle.fontColor = Color.WHITE;
        backStyle.overFontColor = Color.WHITE;
        backStyle.downFontColor = Color.WHITE;
        backStyle.checkedFontColor = Color.WHITE;
        backStyle.disabledFontColor = Color.WHITE;
        Texture transparent = makeColorTexture(0,0,0,0);
        TextureRegionDrawable trd = new TextureRegionDrawable(new TextureRegion(transparent));
        backStyle.up = trd; backStyle.over = trd; backStyle.down = trd; backStyle.checked = trd; backStyle.disabled = trd;

        TextButton backButton = new TextButton(Lang.back(), backStyle);
        backButton.getLabel().setColor(Color.WHITE);
        backButton.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { game.setScreen(new MenuScreen(game)); }});

        Table topbar = new Table();
        topbar.add(backButton).left().pad(12f);
        root.add(topbar).expandX().fillX().row();

        // Contenido
        Table content = new Table();
        content.top().pad(24f);
        content.defaults().pad(10f);

        ScrollPane sp = new ScrollPane(content);
        sp.setFadeScrollBars(false);
        root.add(sp).expand().fill().pad(0, 16f, 16f, 16f).row();

        Usuario u = ManejoUsuarios.UsuarioActivo;

        // Avatar
        avatarImage = new Image();
        avatarImage.setScaling(Scaling.fit);
        reloadAvatar();

        ImageButton editAvatarButton = new ImageButton(editButtonStyle);
        editAvatarButton.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { onChangeAvatar(); }});

        Table avatarRow = new Table();
        avatarRow.add(avatarImage).size(220f, 220f).center().padRight(12f);
        avatarRow.add().width(8f);
        avatarRow.add(editAvatarButton).size(48f, 48f).top().right();
        content.add(avatarRow).center().row();

        String displayName = (u != null && u.getNombre()!=null && !u.getNombre().isEmpty())
                ? u.getNombre() : (u != null ? u.getUsuario() : Lang.guest());
        content.add(new Label(displayName, titleStyle)).padTop(2f).center().row();

        // Card: datos de cuenta
        content.add(cardHeader("Datos de la cuenta", subtitleStyle)).expandX().fillX().row();

        Table cardInfo = new Table();
        cardInfo.setBackground(panelBg);
        cardInfo.pad(20f);
        cardInfo.defaults().left().pad(10f);

        labelNameValue  = new Label(textOrDash(u != null ? u.getNombre() : null),  valueStyle);
        Table rNombre = keyValueRowWithLabel("Nombre completo", labelNameValue, keyStyle);
        cardInfo.add(rNombre).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        labelUserValue = new Label(textOrDash(u != null ? u.getUsuario() : null), valueStyle);
        Table rUsuario = keyValueRowWithLabel("Usuario", labelUserValue, keyStyle);
        ImageButton btnEditUsuario = new ImageButton(editButtonStyle);
        btnEditUsuario.addListener(new ClickListener() { @Override public void clicked(InputEvent e,float x,float y){ onEditUsuario(); }});
        rUsuario.add(btnEditUsuario).size(32f, 32f).padLeft(8f);
        cardInfo.add(rUsuario).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        labelPasswordValue = new Label(mask(u != null ? u.getContrasena() : null), valueStyle);
        Table rPass = keyValueRowWithLabel("Contrasena", labelPasswordValue, keyStyle);
        ImageButton btnEditPass = new ImageButton(editButtonStyle);
        btnEditPass.addListener(new ClickListener() { @Override public void clicked(InputEvent e,float x,float y){ onEditPass(); }});
        rPass.add(btnEditPass).size(32f, 32f).padLeft(8f);
        cardInfo.add(rPass).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        int totalSeg = (u != null) ? u.getTiempoJugadoTotal() : 0;
        String totalFmt = formatSecondsHuman(totalSeg) + "   (" + (totalSeg/60) + " min)";
        cardInfo.add(keyValueRow("Tiempo total de juego", totalFmt, keyStyle, valueStyle)).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        int partidasJugadas = (u != null && u.historial != null) ? u.historial.size() : 0;
        cardInfo.add(keyValueRow("Total partidas jugadas", String.valueOf(partidasJugadas), keyStyle, valueStyle)).expandX().fillX().row();

        content.add(cardInfo).expandX().fillX().row();

        // Card: estadísticas
        content.add(cardHeader("Mis Estadisticas", subtitleStyle)).expandX().fillX().padTop(12f).row();

        Table cardProg = new Table();
        cardProg.setBackground(panelBg);
        cardProg.pad(14f);
        cardProg.defaults().left().pad(6f);

        Table header = new Table();
        header.defaults().left().pad(6f);

        header.add(new Label("Nivel", tableHeadStyle)).width(120f);
        header.add(new Label("Estado", tableHeadStyle)).width(200f);
        header.add(new Label("Partidas", tableHeadStyle)).width(120f);
        header.add(new Label("Tiempo promedio", tableHeadStyle)).width(200f);
        header.add(new Label("Victoria record", largeValueStyle)).colspan(3).center().padBottom(4f).row();

        header.add().width(120f);
        header.add().width(200f);
        header.add().width(120f);
        header.add().width(200f);
        header.add(new Label("Pasos", tableHeadStyle)).width(220f);
        header.add(new Label("Empujes", tableHeadStyle)).width(140f);
        header.add(new Label("Tiempo", tableHeadStyle)).width(180f).row();

        cardProg.add(header).expandX().fillX().row();

        for (int n = 1; n <= 7; n++) {
            boolean completo = (u != null) && u.getNivelCompletado(n);
            int promedio = 0, bestSteps = 0, bestEmpujes = 0, bestTimeOfBestAttempt = 0, partidasNivel = 0;
            if (u != null) {
                try { promedio = u.getTiempoPromedioNivel(n); } catch (Throwable ignored) {}
                try { bestSteps = u.getMayorPuntuacion(n); }  catch (Throwable ignored) {}
                try { bestEmpujes = u.getEmpujesNivel(n); }   catch (Throwable ignored) {}
                try { bestTimeOfBestAttempt = u.getTiempoMejorIntento(n); } catch (Throwable ignored) {}
                try { partidasNivel = u.getPartidasPorNivel(n); } catch (Throwable ignored) {}
            }

            String partidasTxt  = (partidasNivel > 0) ? String.valueOf(partidasNivel) : "-";
            String promedioTxt  = (promedio > 0) ? formatSecondsHuman(promedio) : "-";
            String bestStepsTxt = (bestSteps > 0) ? (bestSteps + " pasos") : "-";
            String bestPushTxt  = (bestEmpujes > 0) ? String.valueOf(bestEmpujes) : "-";
            String bestTimeTxt  = (bestTimeOfBestAttempt > 0) ? formatSecondsHuman(bestTimeOfBestAttempt) : "-";

            Table row = new Table();
            row.defaults().left().pad(6f);
            row.add(new Label("Nivel " + n, cellStyle)).width(120f);
            row.add(new Label(completo ? "Completado" : "Sin completar", cellStyle)).width(200f);
            row.add(new Label(partidasTxt, cellStyle)).width(120f);
            row.add(new Label(promedioTxt, cellStyle)).width(200f);
            row.add(new Label(bestStepsTxt, cellStyle)).width(220f);
            row.add(new Label(bestPushTxt, cellStyle)).width(140f);
            row.add(new Label(bestTimeTxt, cellStyle)).width(180f).row();

            cardProg.add(row).expandX().fillX().row();

            if (n < 7) {
                Image div = new Image(dividerBg);
                cardProg.add(div).height(1f).expandX().fillX().padTop(2f).padBottom(2f).row();
            }
        }

        Label hint = new Label("", hintStyle);
        hint.setColor(new Color(1,1,1,0.55f));
        cardProg.add(hint).left().padTop(8f).row();

        content.add(cardProg).expandX().fillX().row();

        // Recursos de diálogo/inputs
        dialogBackgroundTexture   = makeColorTexture(0, 0, 0, 200);
        textfieldBackgroundTexture= makeColorTexture(0, 0, 0, 150);
        textfieldSelectionTexture = makeColorTexture(255,255,255,80);
        textfieldCursorTexture    = makeColorTexture(255,255,255,255);
        buttonClearTexture        = makeColorTexture(0,0,0,0);

        dialogBackgroundDrawable  = new TextureRegionDrawable(new TextureRegion(dialogBackgroundTexture));
        textfieldBackgroundDrawable= new TextureRegionDrawable(new TextureRegion(textfieldBackgroundTexture));
        textfieldSelectionDrawable = new TextureRegionDrawable(new TextureRegion(textfieldSelectionTexture));
        textfieldCursorDrawable    = new TextureRegionDrawable(new TextureRegion(textfieldCursorTexture));
        buttonClearDrawable        = new TextureRegionDrawable(new TextureRegion(buttonClearTexture));

        cachedWindowStyle = new Window.WindowStyle();
        cachedWindowStyle.titleFont = bodyFont;
        cachedWindowStyle.titleFontColor = Color.WHITE;
        cachedWindowStyle.background = dialogBackgroundDrawable;

        cachedTextFieldStyle = new TextField.TextFieldStyle();
        cachedTextFieldStyle.font = bodyFont;
        cachedTextFieldStyle.fontColor = Color.WHITE;
        cachedTextFieldStyle.background = textfieldBackgroundDrawable;
        cachedTextFieldStyle.selection = textfieldSelectionDrawable;
        cachedTextFieldStyle.cursor = textfieldCursorDrawable;

        cachedTextButtonStyle = new TextButton.TextButtonStyle();
        cachedTextButtonStyle.font = bodyFont;
        cachedTextButtonStyle.fontColor = Color.WHITE;
        cachedTextButtonStyle.overFontColor = Color.WHITE;
        cachedTextButtonStyle.downFontColor = Color.WHITE;
        cachedTextButtonStyle.checkedFontColor = Color.WHITE;
        cachedTextButtonStyle.disabledFontColor = Color.WHITE;
        cachedTextButtonStyle.up = buttonClearDrawable;
        cachedTextButtonStyle.down = buttonClearDrawable;
        cachedTextButtonStyle.over = buttonClearDrawable;
        cachedTextButtonStyle.checked = buttonClearDrawable;
        cachedTextButtonStyle.disabled = buttonClearDrawable;
    }

    // ======== Editar Avatar ========
    private void onChangeAvatar() {
        final String[] selectedPath = new String[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Seleccionar imagen de avatar");
                fc.setFileFilter(new FileNameExtensionFilter("Imagenes (png, jpg, jpeg, gif)", "png","jpg","jpeg","gif"));
                fc.setAcceptAllFileFilterUsed(false);
                int res = fc.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) selectedPath[0] = fc.getSelectedFile().getAbsolutePath();
            });
        } catch (Exception ignored) {}
        if (selectedPath[0] == null || selectedPath[0].trim().isEmpty()) return;
        Usuario u = ManejoUsuarios.UsuarioActivo;
        if (u != null) { u.setAvatar(selectedPath[0]); reloadAvatar(); }
    }

    private void reloadAvatar() {
        if (avatarTexture != null) { avatarTexture.dispose(); avatarTexture = null; }
        String defaultPath = "ui/default_avatar.png";
        Usuario u = ManejoUsuarios.UsuarioActivo;
        if (u != null && u.avatar != null && !u.avatar.trim().isEmpty()) {
            if (files.internal(u.avatar).exists()) avatarTexture = new Texture(files.internal(u.avatar));
            else if (files.absolute(u.avatar).exists()) avatarTexture = new Texture(files.absolute(u.avatar));
        }
        if (avatarTexture == null) avatarTexture = new Texture(files.internal(defaultPath));
        if (avatarImage != null) {
            avatarImage.setDrawable(new TextureRegionDrawable(new TextureRegion(avatarTexture)));
            avatarImage.invalidateHierarchy();
        }
    }

    // ======== Editar Usuario (sin comas) ========
    private void onEditUsuario() {
        final Dialog dlg = new Dialog("Editar usuario", cachedWindowStyle);
        Table c = dlg.getContentTable(); c.pad(16f); c.defaults().pad(6f).fillX();

        Label l = new Label("Nuevo usuario :", new Label.LabelStyle(bodyFont, Color.WHITE));
        final TextField tf = new TextField("", cachedTextFieldStyle);
        final Label err  = new Label("", new Label.LabelStyle(smallFont, Color.SALMON));

        c.add(l).left().row();
        c.add(tf).width(380f).row();
        c.add(err).left().row();

        TextButton cancel = new TextButton(Lang.dlgCancel(), cachedTextButtonStyle);
        TextButton ok     = new TextButton(Lang.saveChanges(),  cachedTextButtonStyle);

        cancel.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ dlg.hide(); }});
        ok.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e,float x,float y){
                Usuario u = ManejoUsuarios.UsuarioActivo;
                String actual = (u != null) ? u.getUsuario() : "";
                String v = tf.getText().trim();

                if (v.isEmpty()) { err.setText("Usuario vacío"); return; }
                if (v.indexOf(',') >= 0) { err.setText("No se permite la coma (,)"); return; }
                if (v.equalsIgnoreCase(actual)) { err.setText(Lang.errUserExists()); return; }
                String existente = ManejoArchivos.buscarUsuario(v);
                if (existente != null) { err.setText(Lang.errUserExists()); return; }

                if (u != null) { u.setUsuario(v); labelUserValue.setText(v); }
                dlg.hide();
            }
        });

        dlg.getButtonTable().pad(0,16f,16f,16f).defaults().width(140f).pad(6f);
        dlg.getButtonTable().add(cancel);
        dlg.getButtonTable().add(ok);

        showDialog(dlg, tf);
    }

   
    private void onEditPass() {
        final Dialog dlg = new Dialog("Editar contrasena", cachedWindowStyle);
        Table c = dlg.getContentTable(); c.pad(16f); c.defaults().pad(6f).fillX();

        Label l1 = new Label("Nueva contrasena :", new Label.LabelStyle(bodyFont, Color.WHITE));
        Label l2 = new Label("Confirmar:",      new Label.LabelStyle(bodyFont, Color.WHITE));
        final TextField tf1 = new TextField("", cachedTextFieldStyle);
        final TextField tf2 = new TextField("", cachedTextFieldStyle);
        tf1.setPasswordMode(true); tf1.setPasswordCharacter('*');
        tf2.setPasswordMode(true); tf2.setPasswordCharacter('*');
        final Label err  = new Label("", new Label.LabelStyle(smallFont, Color.SALMON));

        c.add(l1).left().row();
        c.add(tf1).width(380f).row();
        c.add(l2).left().row();
        c.add(tf2).width(380f).row();
        c.add(err).left().row();

        TextButton cancel = new TextButton(Lang.dlgCancel(), cachedTextButtonStyle);
        TextButton ok     = new TextButton(Lang.saveChanges(),  cachedTextButtonStyle);

        cancel.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ dlg.hide(); }});
        ok.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e,float x,float y){
                String p1 = tf1.getText(); if (p1 == null) p1 = "";
                String p2 = tf2.getText(); if (p2 == null) p2 = "";

                if (!p1.equals(p2)) { err.setText(Lang.errPasswordMismatch()); return; }
                if (p1.indexOf(',') >= 0) { err.setText("No se permite la coma (,)"); return; }

                Usuario u = ManejoUsuarios.UsuarioActivo;
                if (u == null) { err.setText(Lang.errFail()); return; }
                
                if (!u.passValida(p1)) { err.setText("La contraseña no cumple los requisitos."); return; }

                u.setContrasena(p1);
                labelPasswordValue.setText(mask(p1));
                dlg.hide();
            }
        });

        dlg.getButtonTable().pad(0,16f,16f,16f).defaults().width(140f).pad(6f);
        dlg.getButtonTable().add(cancel);
        dlg.getButtonTable().add(ok);

        showDialog(dlg, tf1);
    }

    
    private void showDialog(Dialog dialog, Actor focus) {
        dialog.setColor(Color.WHITE);
        dialog.show(stage);
        dialog.toFront();
        dialog.invalidateHierarchy();
        dialog.pack();
        if (focus != null) stage.setKeyboardFocus(focus);
        stage.setScrollFocus(null);
        forceWhiteText(dialog);
    }

    private void forceWhiteText(Actor actor) {
        if (actor instanceof Label) ((Label) actor).setColor(Color.WHITE);
        if (actor instanceof TextButton) ((TextButton) actor).getLabel().setColor(Color.WHITE);
        if (actor instanceof Group) for (Actor ch : ((Group) actor).getChildren()) forceWhiteText(ch);
    }

    private BitmapFont generateFontWithOutline(int size, String fillHex, int borderWidth, String borderHex) {
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = size; p.color = Color.valueOf(fillHex);
        p.borderWidth = borderWidth; p.borderColor = Color.valueOf(borderHex);
        return fontGenerator.generateFont(p);
    }

    private Texture makeColorTexture(int r, int g, int b, int a) {
        Pixmap pm = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pm.setColor(r/255f, g/255f, b/255f, a/255f);
        pm.fill();
        Texture t = new Texture(pm); pm.dispose();
        return t;
    }

    private Table cardHeader(String text, Label.LabelStyle style) {
        Table t = new Table(); t.defaults().left(); t.add(new Label(text, style)).left(); return t;
    }

    private void addDivider(Table table, TextureRegionDrawable dividerBg) {
        Image div = new Image(dividerBg);
        table.add(div).height(1f).expandX().fillX().padTop(2f).padBottom(2f).row();
    }

    private Table keyValueRow(String key, String value, Label.LabelStyle keyStyle, Label.LabelStyle valStyle) {
        Table row = new Table();
        row.defaults().left().pad(6f);
        row.add(new Label(key + ":", keyStyle)).width(300f).left().padRight(24f);
        Label v = new Label(value == null || value.isEmpty() ? "-" : value, valStyle);
        row.add(v).left().expandX().fillX();
        return row;
    }

    private Table keyValueRowWithLabel(String key, Label valueLabel, Label.LabelStyle keyStyle) {
        Table row = new Table();
        row.defaults().left().pad(6f);
        row.add(new Label(key + ":", keyStyle)).width(300f).left().padRight(24f);
        row.add(valueLabel).left().expandX().fillX();
        return row;
    }

    private String textOrDash(String s){ return (s == null || s.isEmpty()) ? "-" : s; }

    private String formatSecondsHuman(int sec){
        if (sec <= 0) return "0 s";
        int h = sec / 3600;
        int m = (sec % 3600) / 60;
        int s = sec % 60;
        if (h > 0) return String.format("%dh %02dmin %02ds", h, m, s);
        if (m > 0) return String.format("%dmin %02ds", m, s);
        return String.format("%ds", s);
    }

    private String mask(String s){
        if (s == null || s.isEmpty()) return "-";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) b.append('*');
        return b.toString();
    }

    @Override
    public void hide() {
        if (avatarTexture != null) { avatarTexture.dispose(); avatarTexture = null; }
        if (panelTexture != null)  { panelTexture.dispose(); panelTexture = null; }
        if (dividerTexture != null){ dividerTexture.dispose(); dividerTexture = null; }
        if (editIconTexture != null){ editIconTexture.dispose(); editIconTexture = null; }
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fontGenerator != null) fontGenerator.dispose();
        if (titleFont != null) titleFont.dispose();
        if (subtitleFont != null) subtitleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
        if (smallFont != null) smallFont.dispose();
        if (largeFont != null) largeFont.dispose();
        if (avatarTexture != null) { avatarTexture.dispose(); avatarTexture = null; }
        if (panelTexture != null)  { panelTexture.dispose(); panelTexture = null; }
        if (dividerTexture != null){ dividerTexture.dispose(); dividerTexture = null; }
        if (editIconTexture != null){ editIconTexture.dispose(); editIconTexture = null; }
        if (dialogBackgroundTexture != null)   dialogBackgroundTexture.dispose();
        if (textfieldBackgroundTexture != null) textfieldBackgroundTexture.dispose();
        if (textfieldSelectionTexture != null)  textfieldSelectionTexture.dispose();
        if (textfieldCursorTexture != null)     textfieldCursorTexture.dispose();
        if (buttonClearTexture != null)         buttonClearTexture.dispose();
    }
}
