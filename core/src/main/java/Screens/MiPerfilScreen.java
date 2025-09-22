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

    public MiPerfilScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        fontGenerator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));
        // Fuentes con contorno (borde negro)
        titleFont = generateFontWithOutline(88, "E6DFC9", 3, "000000");
        subtitleFont = generateFontWithOutline(48, "E6DFC9", 2, "000000");
        bodyFont = generateFontWithOutline(34, "E6DFC9", 2, "000000");
        smallFont = generateFontWithOutline(26, "BFC4D0", 1, "000000");
        largeFont = generateFontWithOutline(60, "FFFFFF", 3, "000000");

        // Estilos de texto
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        Label.LabelStyle subtitleStyle = new Label.LabelStyle(subtitleFont, subtitleFont.getColor());
        Label.LabelStyle keyStyle = new Label.LabelStyle(bodyFont, new Color(1, 1, 1, 0.85f));
        Label.LabelStyle valueStyle = new Label.LabelStyle(bodyFont, Color.WHITE);
        Label.LabelStyle cellStyle = new Label.LabelStyle(bodyFont, Color.WHITE);
        Label.LabelStyle tableHeadStyle = new Label.LabelStyle(bodyFont, new Color(1, 1, 1, 0.95f));
        Label.LabelStyle hintStyle = new Label.LabelStyle(smallFont, smallFont.getColor());
        Label.LabelStyle largeValueStyle = new Label.LabelStyle(largeFont, largeFont.getColor());

        // Paneles y divisores
        panelTexture = makeColorTexture(255, 255, 255, 22);
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

        TextureRegionDrawable panelBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(panelTexture));
        TextureRegionDrawable dividerBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(dividerTexture));

        // Root
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Botón volver (transparente)
        TextButton.TextButtonStyle backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle.font = bodyFont;
        backButtonStyle.fontColor = Color.WHITE;
        backButtonStyle.overFontColor = Color.WHITE;
        backButtonStyle.downFontColor = Color.WHITE;
        backButtonStyle.checkedFontColor = Color.WHITE;
        backButtonStyle.disabledFontColor = Color.WHITE;
        Texture transparentTexture = makeColorTexture(0, 0, 0, 0);
        TextureRegionDrawable transparentDrawable = new TextureRegionDrawable(new TextureRegion(transparentTexture));
        backButtonStyle.up = transparentDrawable;
        backButtonStyle.over = transparentDrawable;
        backButtonStyle.down = transparentDrawable;
        backButtonStyle.checked = transparentDrawable;
        backButtonStyle.disabled = transparentDrawable;

        TextButton backButton = new TextButton(Lang.back(), backButtonStyle);
        backButton.getLabel().setColor(Color.WHITE);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table topbar = new Table();
        topbar.add(backButton).left().pad(12f);
        root.add(topbar).expandX().fillX().row();

        // Contenido
        Table content = new Table();
        content.top().pad(24f);
        content.defaults().pad(10f);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFadeScrollBars(false);
        root.add(scrollPane).expand().fill().pad(0, 16f, 16f, 16f).row();

        Usuario currentUser = ManejoUsuarios.UsuarioActivo;

        // Avatar
        avatarImage = new Image();
        avatarImage.setScaling(Scaling.fit);
        reloadAvatar();

        ImageButton editAvatarButton = new ImageButton(editButtonStyle);
        editAvatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onChangeAvatar();
            }
        });

        Table avatarRow = new Table();
        avatarRow.add(avatarImage).size(220f, 220f).center().padRight(12f);
        avatarRow.add().width(8f);
        avatarRow.add(editAvatarButton).size(48f, 48f).top().right();
        content.add(avatarRow).center().row();

        String displayName = (currentUser != null && currentUser.getNombre() != null && !currentUser.getNombre().isEmpty())
                ? currentUser.getNombre() : (currentUser != null ? currentUser.getUsuario() : Lang.guest());
        content.add(new Label(displayName, titleStyle)).padTop(2f).center().row();

        // Card: datos de cuenta
        content.add(cardHeader(Lang.cfgControls(), subtitleStyle)).expandX().fillX().row(); // (solo encabezado visual; puedes cambiar el texto)

        Table accountCard = new Table();
        accountCard.setBackground(panelBackgroundDrawable);
        accountCard.pad(20f);
        accountCard.defaults().left().pad(10f);

        labelNameValue = new Label(textOrDash(currentUser != null ? currentUser.getNombre() : null), valueStyle);
        Table nameRow = keyValueRowWithLabel("Nombre completo", labelNameValue, keyStyle);
        accountCard.add(nameRow).expandX().fillX().row();
        addDivider(accountCard, dividerBackgroundDrawable);

        labelUserValue = new Label(textOrDash(currentUser != null ? currentUser.getUsuario() : null), valueStyle);
        Table userRow = keyValueRowWithLabel("Usuario", labelUserValue, keyStyle);
        ImageButton editUserButton = new ImageButton(editButtonStyle);
        editUserButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                onEditUsuario();
            }
        });
        userRow.add(editUserButton).size(32f, 32f).padLeft(8f);
        accountCard.add(userRow).expandX().fillX().row();
        addDivider(accountCard, dividerBackgroundDrawable);

        labelPasswordValue = new Label(mask(currentUser != null ? currentUser.getContrasena() : null), valueStyle);
        Table passwordRow = keyValueRowWithLabel("Contrasena", labelPasswordValue, keyStyle);
        ImageButton editPasswordButton = new ImageButton(editButtonStyle);
        editPasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                onEditPass();
            }
        });
        passwordRow.add(editPasswordButton).size(32f, 32f).padLeft(8f);
        accountCard.add(passwordRow).expandX().fillX().row();
        addDivider(accountCard, dividerBackgroundDrawable);

        int totalSecondsPlayed = (currentUser != null) ? currentUser.getTiempoJugadoTotal() : 0;
        String totalTimeFormatted = formatSecondsHuman(totalSecondsPlayed) + "   (" + (totalSecondsPlayed / 60) + " min)";
        accountCard.add(keyValueRow("Tiempo total de juego", totalTimeFormatted, keyStyle, valueStyle)).expandX().fillX().row();
        addDivider(accountCard, dividerBackgroundDrawable);

        int totalMatches = 0;
        if (currentUser != null && currentUser.historial != null) {
            totalMatches = currentUser.historial.size();
        }
        accountCard.add(keyValueRow("Total partidas jugadas", String.valueOf(totalMatches), keyStyle, valueStyle)).expandX().fillX().row();

        content.add(accountCard).expandX().fillX().row();

        // Card: estadísticas
        content.add(cardHeader("Mis Estadisticas", subtitleStyle)).expandX().fillX().padTop(12f).row();

        Table statsCard = new Table();
        statsCard.setBackground(panelBackgroundDrawable);
        statsCard.pad(14f);
        statsCard.defaults().left().pad(6f);

        Table statsHeader = new Table();
        statsHeader.defaults().left().pad(6f);

        statsHeader.add(new Label("Nivel", tableHeadStyle)).width(120f);
        statsHeader.add(new Label("Estado", tableHeadStyle)).width(200f);
        statsHeader.add(new Label("Partidas", tableHeadStyle)).width(120f);
        statsHeader.add(new Label("Tiempo promedio", tableHeadStyle)).width(200f);
        statsHeader.add(new Label("Victoria record", largeValueStyle)).colspan(3).center().padBottom(4f).row();

        statsHeader.add().width(120f);
        statsHeader.add().width(200f);
        statsHeader.add().width(120f);
        statsHeader.add().width(200f);
        statsHeader.add(new Label("Pasos", tableHeadStyle)).width(220f);
        statsHeader.add(new Label("Empujes", tableHeadStyle)).width(140f);
        statsHeader.add(new Label("Tiempo", tableHeadStyle)).width(180f).row();

        statsCard.add(statsHeader).expandX().fillX().row();

        for (int level = 1; level <= 7; level++) {
            boolean completed = (currentUser != null) && currentUser.getNivelCompletado(level);
            int averageSeconds = 0;
            int bestSteps = 0;
            int bestPushes = 0;
            int bestTimeOfBestAttempt = 0;
            int matchesOnLevel = 0;
            if (currentUser != null) {
                try {
                    averageSeconds = currentUser.getTiempoPromedioNivel(level);
                } catch (Throwable ignored) {
                }
                try {
                    bestSteps = currentUser.getMayorPuntuacion(level);
                } catch (Throwable ignored) {
                }
                try {
                    bestPushes = currentUser.getEmpujesNivel(level);
                } catch (Throwable ignored) {
                }
                try {
                    bestTimeOfBestAttempt = currentUser.getTiempoMejorIntento(level);
                } catch (Throwable ignored) {
                }
                try {
                    matchesOnLevel = currentUser.getPartidasPorNivel(level);
                } catch (Throwable ignored) {
                }
            }

            String matchesText = (matchesOnLevel > 0) ? String.valueOf(matchesOnLevel) : "-";
            String averageText = (averageSeconds > 0) ? formatSecondsHuman(averageSeconds) : "-";
            String bestStepsText = (bestSteps > 0) ? (bestSteps + " pasos") : "-";
            String bestPushesText = (bestPushes > 0) ? String.valueOf(bestPushes) : "-";
            String bestTimeText = (bestTimeOfBestAttempt > 0) ? formatSecondsHuman(bestTimeOfBestAttempt) : "-";

            Table statsRow = new Table();
            statsRow.defaults().left().pad(6f);
            statsRow.add(new Label("Nivel " + level, cellStyle)).width(120f);
            statsRow.add(new Label(completed ? "Completado" : "Sin completar", cellStyle)).width(200f);
            statsRow.add(new Label(matchesText, cellStyle)).width(120f);
            statsRow.add(new Label(averageText, cellStyle)).width(200f);
            statsRow.add(new Label(bestStepsText, cellStyle)).width(220f);
            statsRow.add(new Label(bestPushesText, cellStyle)).width(140f);
            statsRow.add(new Label(bestTimeText, cellStyle)).width(180f).row();

            statsCard.add(statsRow).expandX().fillX().row();

            if (level < 7) {
                Image divider = new Image(dividerBackgroundDrawable);
                statsCard.add(divider).height(1f).expandX().fillX().padTop(2f).padBottom(2f).row();
            }
        }

        Label statsHint = new Label("", hintStyle);
        statsHint.setColor(new Color(1, 1, 1, 0.55f));
        statsCard.add(statsHint).left().padTop(8f).row();

        content.add(statsCard).expandX().fillX().row();

        // Recursos de diálogo/inputs
        dialogBackgroundTexture = makeColorTexture(0, 0, 0, 200);
        textfieldBackgroundTexture = makeColorTexture(0, 0, 0, 150);
        textfieldSelectionTexture = makeColorTexture(255, 255, 255, 80);
        textfieldCursorTexture = makeColorTexture(255, 255, 255, 255);
        buttonClearTexture = makeColorTexture(0, 0, 0, 0);

        dialogBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(dialogBackgroundTexture));
        textfieldBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(textfieldBackgroundTexture));
        textfieldSelectionDrawable = new TextureRegionDrawable(new TextureRegion(textfieldSelectionTexture));
        textfieldCursorDrawable = new TextureRegionDrawable(new TextureRegion(textfieldCursorTexture));
        buttonClearDrawable = new TextureRegionDrawable(new TextureRegion(buttonClearTexture));

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

    private void onChangeAvatar() {
        final String[] selectedPath = new String[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Seleccionar imagen de avatar");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagenes (png, jpg, jpeg, gif)", "png", "jpg", "jpeg", "gif");
                fileChooser.setFileFilter(filter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedPath[0] = fileChooser.getSelectedFile().getAbsolutePath();
                }
            });
        } catch (Exception ignored) {
        }
        if (selectedPath[0] == null || selectedPath[0].trim().isEmpty()) {
            return;
        }
        Usuario user = ManejoUsuarios.UsuarioActivo;
        if (user != null) {
            user.setAvatar(selectedPath[0]);
            reloadAvatar();
        }
    }

    private void reloadAvatar() {
        if (avatarTexture != null) {
            avatarTexture.dispose();
            avatarTexture = null;
        }
        String defaultAvatarPath = "ui/default_avatar.png";
        Usuario user = ManejoUsuarios.UsuarioActivo;
        if (user != null && user.avatar != null && !user.avatar.trim().isEmpty()) {
            if (files.internal(user.avatar).exists()) {
                avatarTexture = new Texture(files.internal(user.avatar));
            } else if (files.absolute(user.avatar).exists()) {
                avatarTexture = new Texture(files.absolute(user.avatar));
            }
        }
        if (avatarTexture == null) {
            avatarTexture = new Texture(files.internal(defaultAvatarPath));
        }
        if (avatarImage != null) {
            avatarImage.setDrawable(new TextureRegionDrawable(new TextureRegion(avatarTexture)));
            avatarImage.invalidateHierarchy();
        }
    }

    private boolean isAlnum(String s) {
        return s != null && s.matches("[A-Za-z0-9]+");
    }

    private void onEditUsuario() {
        final Dialog dialog = new Dialog("Editar usuario", cachedWindowStyle);
        Table content = dialog.getContentTable();
        content.pad(16f);
        content.defaults().pad(6f).fillX();

        Label prompt = new Label("Nuevo usuario :", new Label.LabelStyle(bodyFont, Color.WHITE));
        final TextField input = new TextField("", cachedTextFieldStyle);
        final Label error = new Label("", new Label.LabelStyle(smallFont, Color.SALMON));

        content.add(prompt).left().row();
        content.add(input).width(380f).row();
        content.add(error).left().row();

        TextButton cancel = new TextButton(Lang.dlgCancel(), cachedTextButtonStyle);
        TextButton ok = new TextButton(Lang.saveChanges(), cachedTextButtonStyle);

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                dialog.hide();
            }
        });
        ok.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                Usuario user = ManejoUsuarios.UsuarioActivo;
                String current = (user != null) ? user.getUsuario() : "";
                String value = input.getText().trim();

                if (!isAlnum(value)) {
                    error.setText(Lang.errOnlyAlnum());
                    return;
                }
                if (value.equalsIgnoreCase(current)) {
                    error.setText(Lang.errUserExists());
                    return;
                }
                String existing = ManejoArchivos.buscarUsuario(value);
                if (existing != null) {
                    error.setText(Lang.errUserExists());
                    return;
                }

                if (user != null) {
                    user.setUsuario(value);
                    labelUserValue.setText(value);
                }
                dialog.hide();
            }
        });

        dialog.getButtonTable().pad(0, 16f, 16f, 16f).defaults().width(140f).pad(6f);
        dialog.getButtonTable().add(cancel);
        dialog.getButtonTable().add(ok);

        showDialog(dialog, input);
    }

    private void onEditPass() {
        final Dialog dialog = new Dialog("Editar contrasena", cachedWindowStyle);
        Table content = dialog.getContentTable();
        content.pad(16f);
        content.defaults().pad(6f).fillX();

        Label prompt1 = new Label("Nueva contrasena :", new Label.LabelStyle(bodyFont, Color.WHITE));
        Label prompt2 = new Label("Confirmar:", new Label.LabelStyle(bodyFont, Color.WHITE));
        final TextField input1 = new TextField("", cachedTextFieldStyle);
        final TextField input2 = new TextField("", cachedTextFieldStyle);
        input1.setPasswordMode(true);
        input1.setPasswordCharacter('*');
        input2.setPasswordMode(true);
        input2.setPasswordCharacter('*');
        final Label error = new Label("", new Label.LabelStyle(smallFont, Color.SALMON));

        content.add(prompt1).left().row();
        content.add(input1).width(380f).row();
        content.add(prompt2).left().row();
        content.add(input2).width(380f).row();
        content.add(error).left().row();

        TextButton cancel = new TextButton(Lang.dlgCancel(), cachedTextButtonStyle);
        TextButton ok = new TextButton(Lang.saveChanges(), cachedTextButtonStyle);

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                dialog.hide();
            }
        });
        ok.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                String p1 = input1.getText().trim();
                String p2 = input2.getText().trim();
                if (!isAlnum(p1) || !isAlnum(p2)) {
                    error.setText(Lang.errOnlyAlnum());
                    return;
                }
                if (!p1.equals(p2)) {
                    error.setText(Lang.errPasswordMismatch());
                    return;
                }
                Usuario user = ManejoUsuarios.UsuarioActivo;
                if (user != null) {
                    user.setContrasena(p1);
                    labelPasswordValue.setText(mask(p1));
                }
                dialog.hide();
            }
        });

        dialog.getButtonTable().pad(0, 16f, 16f, 16f).defaults().width(140f).pad(6f);
        dialog.getButtonTable().add(cancel);
        dialog.getButtonTable().add(ok);

        showDialog(dialog, input1);
    }

    private void showDialog(Dialog dialog, Actor focus) {
        dialog.setColor(Color.WHITE);
        dialog.show(stage);
        dialog.toFront();
        dialog.invalidateHierarchy();
        dialog.pack();
        if (focus != null) {
            stage.setKeyboardFocus(focus);
        }
        stage.setScrollFocus(null);
        forceWhiteText(dialog);
    }

    private void forceWhiteText(Actor actor) {
        if (actor instanceof Label) {
            ((Label) actor).setColor(Color.WHITE);
        }
        if (actor instanceof TextButton) {
            ((TextButton) actor).getLabel().setColor(Color.WHITE);
        }
        if (actor instanceof Group) {
            for (Actor child : ((Group) actor).getChildren()) {
                forceWhiteText(child);
            }
        }
    }

    private BitmapFont generateFontWithOutline(int size, String fillHex, int borderWidth, String borderHex) {
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = size;
        params.color = Color.valueOf(fillHex);
        params.borderWidth = borderWidth;
        params.borderColor = Color.valueOf(borderHex);
        return fontGenerator.generateFont(params);
    }

    private Texture makeColorTexture(int r, int g, int b, int a) {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(r / 255f, g / 255f, b / 255f, a / 255f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Table cardHeader(String text, Label.LabelStyle style) {
        Table table = new Table();
        table.defaults().left();
        table.add(new Label(text, style)).left();
        return table;
    }

    private void addDivider(Table table, TextureRegionDrawable dividerBg) {
        Image divider = new Image(dividerBg);
        table.add(divider).height(1f).expandX().fillX().padTop(2f).padBottom(2f).row();
    }

    private Table keyValueRow(String key, String value, Label.LabelStyle keyStyle, Label.LabelStyle valueStyle) {
        Table row = new Table();
        row.defaults().left().pad(6f);
        row.add(new Label(key + ":", keyStyle)).width(300f).left().padRight(24f);
        Label valueLabel = new Label(value == null || value.isEmpty() ? "-" : value, valueStyle);
        row.add(valueLabel).left().expandX().fillX();
        return row;
    }

    private Table keyValueRowWithLabel(String key, Label valueLabel, Label.LabelStyle keyStyle) {
        Table row = new Table();
        row.defaults().left().pad(6f);
        row.add(new Label(key + ":", keyStyle)).width(300f).left().padRight(24f);
        row.add(valueLabel).left().expandX().fillX();
        return row;
    }

    private String textOrDash(String s) {
        return (s == null || s.isEmpty()) ? "-" : s;
    }

    private String formatMillis(Long millis) {
        if (millis == null || millis <= 0) {
            return "-";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(millis));
        // (no se usa actualmente, lo dejo por si lo reutilizas)
    }

    private String formatSecondsHuman(int sec) {
        if (sec <= 0) {
            return "0 s";
        }
        int h = sec / 3600;
        int m = (sec % 3600) / 60;
        int s = sec % 60;
        if (h > 0) {
            return String.format("%dh %02dmin %02ds", h, m, s);
        }
        if (m > 0) {
            return String.format("%dmin %02ds", m, s);
        }
        return String.format("%ds", s);
    }

    private String mask(String s) {
        if (s == null || s.isEmpty()) {
            return "-";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            out.append('*');
        }
        return out.toString();
    }

    @Override
    public void hide() {
        if (avatarTexture != null) {
            avatarTexture.dispose();
            avatarTexture = null;
        }
        if (panelTexture != null) {
            panelTexture.dispose();
            panelTexture = null;
        }
        if (dividerTexture != null) {
            dividerTexture.dispose();
            dividerTexture = null;
        }
        if (editIconTexture != null) {
            editIconTexture.dispose();
            editIconTexture = null;
        }
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fontGenerator != null) {
            fontGenerator.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (subtitleFont != null) {
            subtitleFont.dispose();
        }
        if (bodyFont != null) {
            bodyFont.dispose();
        }
        if (smallFont != null) {
            smallFont.dispose();
        }
        if (largeFont != null) {
            largeFont.dispose();
        }
        if (avatarTexture != null) {
            avatarTexture.dispose();
            avatarTexture = null;
        }
        if (panelTexture != null) {
            panelTexture.dispose();
            panelTexture = null;
        }
        if (dividerTexture != null) {
            dividerTexture.dispose();
            dividerTexture = null;
        }
        if (editIconTexture != null) {
            editIconTexture.dispose();
            editIconTexture = null;
        }
        if (dialogBackgroundTexture != null) {
            dialogBackgroundTexture.dispose();
        }
        if (textfieldBackgroundTexture != null) {
            textfieldBackgroundTexture.dispose();
        }
        if (textfieldSelectionTexture != null) {
            textfieldSelectionTexture.dispose();
        }
        if (textfieldCursorTexture != null) {
            textfieldCursorTexture.dispose();
        }
        if (buttonClearTexture != null) {
            buttonClearTexture.dispose();
        }
    }
}
