package Screens;

import GameLogic.Lang;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.elkinedwin.LogicaUsuario.ArchivoGuardar;
import com.elkinedwin.LogicaUsuario.AudioBus;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;

public class ConfigScreen extends BaseScreen {

    private final Game game;
    private Skin skin;

    private KeyBinder keyBinderUp, keyBinderDown, keyBinderLeft, keyBinderRight, keyBinderRestart;
    private Slider sliderVolume;
    private Label labelVolumeValue;
    private SelectBox<String> selectLanguage;

    private TextButton buttonSave, buttonBack;

    private int originalVolume;
    private int originalLanguage;

    private Texture gradientTexture;

    private static final float CONTENT_MAX_WIDTH = 900f;
    private static final float TOP_OFFSET = 16f;

    public ConfigScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        int language = (ManejoUsuarios.UsuarioActivo != null) ? ManejoUsuarios.UsuarioActivo.getIdioma() : 1;
        Lang.init(language);

        skin = buildSkin();

        BitmapFont baseFont = skin.getFont("base-font");
        baseFont.getData().setScale(1.15f);

        int cfgUp = getCfg("MoverArriba", Input.Keys.UP);
        int cfgDown = getCfg("MoverAbajo", Input.Keys.DOWN);
        int cfgLeft = getCfg("MoverIzq", Input.Keys.LEFT);
        int cfgRight = getCfg("MoverDer", Input.Keys.RIGHT);
        int cfgRestart = getCfg("Reiniciar", Input.Keys.R);

        Integer savedVolume = cfgAny("volumen", "Volumen");
        originalVolume = savedVolume != null ? savedVolume : 70;

        Integer savedLanguage = cfgAny("idioma", "Idioma");
        originalLanguage = savedLanguage != null ? savedLanguage : 1;

        gradientTexture = makeVerticalGradient(16, 400,
                new Color(0.08f, 0.10f, 0.13f, 1f),
                new Color(0.12f, 0.14f, 0.18f, 1f));
        Image gradientBackground = new Image(new TextureRegionDrawable(new TextureRegion(gradientTexture)));
        gradientBackground.setFillParent(true);
        gradientBackground.setScaling(Scaling.fill);
        stage.addActor(gradientBackground);

        keyBinderUp = new KeyBinder(Lang.cfgUp(), "MoverArriba", cfgUp);
        keyBinderDown = new KeyBinder(Lang.cfgDown(), "MoverAbajo", cfgDown);
        keyBinderLeft = new KeyBinder(Lang.cfgLeft(), "MoverIzq", cfgLeft);
        keyBinderRight = new KeyBinder(Lang.cfgRight(), "MoverDer", cfgRight);
        keyBinderRestart = new KeyBinder(Lang.cfgRestart(), "Reiniciar", cfgRestart);

        Label labelVolumeTitle = new Label(Lang.cfgVolume(), skin, "section");
        labelVolumeTitle.setFontScale(1.1f);
        sliderVolume = new Slider(0, 100, 1, false, skin, "thick");
        sliderVolume.setValue(originalVolume);
        labelVolumeValue = new Label(originalVolume + "%", skin, "mono");
        labelVolumeValue.setFontScale(1.05f);

        sliderVolume.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onVolumeChanged();
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                onVolumeChanged();
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int b) {
                onVolumeChanged();
            }
        });

        Label labelLanguageTitle = new Label(Lang.cfgLanguage(), skin, "section");
        labelLanguageTitle.setFontScale(1.1f);
        selectLanguage = new SelectBox<>(skin);
        selectLanguage.setItems(Lang.langSpanish(), Lang.langEnglish());
        selectLanguage.setSelected(originalLanguage == 2 ? Lang.langEnglish() : Lang.langSpanish());
        selectLanguage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateSaveEnabledState();
            }
        });

        buttonSave = new TextButton(Lang.saveChanges(), skin, "cta");
        buttonSave.getLabel().setFontScale(1.05f);
        buttonSave.setDisabled(true);
        buttonSave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonSave.isDisabled()) {
                    return;
                }

                putCfg("MoverArriba", keyBinderUp.getCurrentKeycode());
                putCfg("MoverAbajo", keyBinderDown.getCurrentKeycode());
                putCfg("MoverIzq", keyBinderLeft.getCurrentKeycode());
                putCfg("MoverDer", keyBinderRight.getCurrentKeycode());
                putCfg("Reiniciar", keyBinderRestart.getCurrentKeycode());

                int newVolume = Math.round(sliderVolume.getValue());
                putCfgAny(newVolume, "volumen", "Volumen");

                int newLanguage = selectLanguage.getSelected().equals(Lang.langEnglish()) ? 2 : 1;
                putCfgAny(newLanguage, "idioma", "Idioma");
                try {
                    ArchivoGuardar.guardarConfiguracion();
                } catch (Exception ignored) {
                }

                if (newLanguage != originalLanguage) {
                    if (ManejoUsuarios.UsuarioActivo != null) {
                        ManejoUsuarios.UsuarioActivo.setIdioma(newLanguage);
                    }
                    Lang.init(newLanguage);
                }

                keyBinderUp.confirm();
                keyBinderDown.confirm();
                keyBinderLeft.confirm();
                keyBinderRight.confirm();
                keyBinderRestart.confirm();
                originalVolume = newVolume;
                originalLanguage = newLanguage;
                updateSaveEnabledState();

                Dialog confirmation = new Dialog("", skin);
                confirmation.setModal(true);
                confirmation.getContentTable().pad(12f);
                confirmation.getContentTable().add(new Label(Lang.savedCheck(), skin, "dialog")).left();
                confirmation.button("OK", true);
                confirmation.show(stage);

                if (newLanguage != language) {
                    game.setScreen(new ConfigScreen(game));
                }
            }
        });

        buttonBack = new TextButton(Lang.back(), skin, "ghost");
        buttonBack.getLabel().setFontScale(1.05f);
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table content = new Table();
        content.pad(20f, 24f, 20f, 24f);

        // Card: CONTROLS
        Table cardControls = makeCard();
        Label labelControlsTitle = new Label(Lang.cfgControls(), skin, "section");
        labelControlsTitle.setFontScale(1.12f);
        cardControls.add(labelControlsTitle).left().padBottom(8f).row();
        Label labelHint = new Label(Lang.cfgHintPressKey(), skin, "hint");
        cardControls.add(labelHint).left().padBottom(6f).row();

        Table controlsGrid = new Table();
        controlsGrid.defaults().pad(8f);
        controlsGrid.add(keyBinderUp.labelName).left().padRight(12f).width(150f);
        controlsGrid.add(keyBinderUp.buttonKey).width(300f).height(40f).row();
        controlsGrid.add(keyBinderDown.labelName).left().padRight(12f).width(150f);
        controlsGrid.add(keyBinderDown.buttonKey).width(300f).height(40f).row();
        controlsGrid.add(keyBinderLeft.labelName).left().padRight(12f).width(150f);
        controlsGrid.add(keyBinderLeft.buttonKey).width(300f).height(40f).row();
        controlsGrid.add(keyBinderRight.labelName).left().padRight(12f).width(150f);
        controlsGrid.add(keyBinderRight.buttonKey).width(300f).height(40f).row();
        controlsGrid.add(keyBinderRestart.labelName).left().padRight(12f).width(150f);
        controlsGrid.add(keyBinderRestart.buttonKey).width(300f).height(40f).row();
        cardControls.add(controlsGrid).growX();
        content.add(cardControls).growX().padBottom(14f).row();

        // Card: AUDIO
        Table cardAudio = makeCard();
        Label labelAudioTitle = new Label(Lang.cfgAudio(), skin, "section");
        labelAudioTitle.setFontScale(1.12f);
        cardAudio.add(labelAudioTitle).left().padBottom(8f).row();
        Table audioRow = new Table();
        audioRow.defaults().pad(8f);
        audioRow.add(labelVolumeTitle).left().padRight(12f).width(150f);
        audioRow.add(sliderVolume).growX().padRight(12f).minWidth(420f).height(26f);
        audioRow.add(labelVolumeValue).width(80f).left();
        cardAudio.add(audioRow).growX();
        content.add(cardAudio).growX().padBottom(14f).row();

        // Card: LANGUAGE
        Table cardLanguage = makeCard();
        cardLanguage.add(labelLanguageTitle).left().padBottom(8f).row();
        Table languageRow = new Table();
        languageRow.defaults().pad(8f);
        languageRow.add(new Label(Lang.cfgSelect(), skin)).left().padRight(12f).width(150f);
        languageRow.add(selectLanguage).width(300f).left().padLeft(4f); // margen pequeño adicional
        cardLanguage.add(languageRow).growX();
        content.add(cardLanguage).growX().padBottom(10f).row();

        // Footer buttons
        Table footerButtons = new Table();
        footerButtons.defaults().pad(10f).width(240f).height(52f);
        footerButtons.add(buttonBack);
        footerButtons.add(buttonSave);
        content.add(footerButtons).right();

        ScrollPane scroller = new ScrollPane(content, skin);
        scroller.setFadeScrollBars(false);
        scroller.setScrollingDisabled(true, false);

        Table frame = new Table();
        frame.setFillParent(true);
        frame.top().padTop(TOP_OFFSET).padBottom(16f);
        frame.add(scroller).width(CONTENT_MAX_WIDTH).growY().top();
        stage.addActor(frame);

        // Key capture
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                boolean handled = false;
                handled |= keyBinderUp.tryCapture(keycode);
                handled |= keyBinderDown.tryCapture(keycode);
                handled |= keyBinderLeft.tryCapture(keycode);
                handled |= keyBinderRight.tryCapture(keycode);
                handled |= keyBinderRestart.tryCapture(keycode);
                if (handled) {
                    updateSaveEnabledState();
                }
                return handled;
            }
        });
    }

    private void onVolumeChanged() {
        int v = Math.round(sliderVolume.getValue());
        labelVolumeValue.setText(v + "%");
        AudioBus.setMasterVolume(v / 100f);
        updateSaveEnabledState();
    }

    private void updateSaveEnabledState() {
        int languageNow = selectLanguage.getSelected().equals(Lang.langEnglish()) ? 2 : 1;
        boolean validChange
                = keyBinderUp.hasChanged()
                || keyBinderDown.hasChanged()
                || keyBinderLeft.hasChanged()
                || keyBinderRight.hasChanged()
                || keyBinderRestart.hasChanged()
                || Math.round(sliderVolume.getValue()) != originalVolume
                || languageNow != originalLanguage;
        buttonSave.setDisabled(!validChange);
    }

    private int getCfg(String key, int def) {
        try {
            Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get(key);
            return (v != null) ? v : def;
        } catch (Exception e) {
            return def;
        }
    }

    private Integer cfgAny(String... keys) {
        try {
            if (ManejoUsuarios.UsuarioActivo == null || ManejoUsuarios.UsuarioActivo.configuracion == null) {
                return null;
            }
            for (String k : keys) {
                Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get(k);
                if (v != null) {
                    return v;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void putCfg(String key, int value) {
        try {
            ManejoUsuarios.UsuarioActivo.configuracion.put(key, value);
        } catch (Exception ignored) {
        }
    }

    private void putCfgAny(int value, String... keys) {
        try {
            if (ManejoUsuarios.UsuarioActivo == null || ManejoUsuarios.UsuarioActivo.configuracion == null) {
                return;
            }
            for (String k : keys) {
                if (ManejoUsuarios.UsuarioActivo.configuracion.containsKey(k)) {
                    ManejoUsuarios.UsuarioActivo.configuracion.put(k, value);
                    return;
                }
            }
            ManejoUsuarios.UsuarioActivo.configuracion.put(keys[0], value);
        } catch (Exception ignored) {
        }
    }

    private class KeyBinder {

        final Label labelName;
        final TextButton buttonKey;
        int originalKeycode;
        int currentKeycode;
        boolean listening = false;

        KeyBinder(String displayName, String configKey, int keycode) {
            this.labelName = new Label(displayName, skin, "label-dim");
            this.labelName.setFontScale(1.02f);
            this.buttonKey = new TextButton(Input.Keys.toString(keycode), skin, "key");
            this.buttonKey.getLabel().setFontScale(1.02f);
            this.buttonKey.getLabel().setColor(Color.WHITE);

            this.originalKeycode = keycode;
            this.currentKeycode = keycode;

            buttonKey.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    listening = true;
                    buttonKey.setText(Lang.pressAKey());
                    buttonKey.getLabel().setColor(Color.WHITE);
                }
            });
        }

        boolean tryCapture(int keycode) {
            if (!listening) {
                return false;
            }
            if (keycode == Input.Keys.ESCAPE) {
                listening = false;
                buttonKey.setText(Input.Keys.toString(currentKeycode));
                buttonKey.getLabel().setColor(Color.WHITE);
                return true;
            }
            currentKeycode = keycode;
            buttonKey.setText(Input.Keys.toString(currentKeycode));
            listening = false;
            buttonKey.getLabel().setColor(Color.WHITE);
            return true;
        }

        boolean hasChanged() {
            return currentKeycode != originalKeycode;
        }

        void confirm() {
            originalKeycode = currentKeycode;
        }

        int getCurrentKeycode() {
            return currentKeycode;
        }
    }

    private Skin buildSkin() {
        Skin s = new Skin();

        // Fuentes con contorno centralizadas (desde BaseScreen)
        BitmapFont baseFont = createOutlinedFont(36, new Color(1, 1, 1, 0.94f), 2, Color.BLACK);
        BitmapFont smallFont = createOutlinedFont(30, new Color(1, 1, 1, 0.86f), 2, Color.BLACK);
        BitmapFont sectionFont = createOutlinedFont(38, new Color(1, 1, 1, 0.92f), 2, Color.BLACK);
        BitmapFont monoFont = createOutlinedFont(32, new Color(1, 1, 1, 0.92f), 2, Color.BLACK);

        s.add("base-font", baseFont, BitmapFont.class);
        s.add("small-font", smallFont, BitmapFont.class);
        s.add("section-font", sectionFont, BitmapFont.class);
        s.add("mono-font", monoFont, BitmapFont.class);

        Pixmap whitePx = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        whitePx.setColor(Color.WHITE);
        whitePx.fill();
        Texture whiteTexture = new Texture(whitePx);
        whitePx.dispose();
        s.add("white", whiteTexture);

        s.add("default", new Label.LabelStyle(baseFont, new Color(1, 1, 1, 0.94f)));
        s.add("label-dim", new Label.LabelStyle(smallFont, new Color(1, 1, 1, 0.86f)));
        s.add("hint", new Label.LabelStyle(smallFont, new Color(1, 1, 1, 0.52f)));
        s.add("section", new Label.LabelStyle(sectionFont, new Color(1, 1, 1, 0.92f)));
        s.add("dialog", new Label.LabelStyle(baseFont, new Color(1, 1, 1, 0.96f)));
        s.add("mono", new Label.LabelStyle(monoFont, new Color(1, 1, 1, 0.92f)));

        TextButton.TextButtonStyle button = new TextButton.TextButtonStyle();
        button.font = baseFont;
        button.up = s.newDrawable("white", new Color(1, 1, 1, 0.12f));
        button.over = s.newDrawable("white", new Color(1, 1, 1, 0.18f));
        button.down = s.newDrawable("white", new Color(1, 1, 1, 0.25f));
        button.disabled = s.newDrawable("white", new Color(1, 1, 1, 0.06f));
        button.fontColor = Color.WHITE;
        s.add("default", button);

        TextButton.TextButtonStyle cta = new TextButton.TextButtonStyle(button);
        cta.up = s.newDrawable("white", new Color(0.30f, 0.62f, 1f, 0.90f));
        cta.over = s.newDrawable("white", new Color(0.35f, 0.67f, 1f, 0.98f));
        cta.down = s.newDrawable("white", new Color(0.27f, 0.58f, 0.95f, 1f));
        cta.disabled = s.newDrawable("white", new Color(0.30f, 0.62f, 1f, 0.40f));
        cta.font = baseFont;
        cta.fontColor = Color.WHITE;
        s.add("cta", cta);

        TextButton.TextButtonStyle ghost = new TextButton.TextButtonStyle(button);
        ghost.up = s.newDrawable("white", new Color(1, 1, 1, 0.10f));
        ghost.over = s.newDrawable("white", new Color(1, 1, 1, 0.14f));
        ghost.down = s.newDrawable("white", new Color(1, 1, 1, 0.20f));
        ghost.font = baseFont;
        s.add("ghost", ghost);

        TextButton.TextButtonStyle key = new TextButton.TextButtonStyle(button);
        key.up = s.newDrawable("white", new Color(1, 1, 1, 0.18f));
        key.over = s.newDrawable("white", new Color(1, 1, 1, 0.24f));
        key.down = s.newDrawable("white", new Color(1, 1, 1, 0.30f));
        key.font = baseFont;
        key.fontColor = Color.WHITE;
        s.add("key", key);

        Window.WindowStyle window = new Window.WindowStyle();
        window.titleFont = smallFont;
        window.titleFontColor = Color.WHITE;
        window.background = s.newDrawable("white", new Color(0f, 0f, 0f, 0.86f));
        s.add("default", window);

        Slider.SliderStyle slider = new Slider.SliderStyle();
        slider.background = s.newDrawable("white", new Color(1, 1, 1, 0.16f));
        slider.knob = s.newDrawable("white", Color.WHITE);
        slider.knobBefore = s.newDrawable("white", new Color(1, 1, 1, 0.60f));
        slider.knobAfter = s.newDrawable("white", new Color(1, 1, 1, 0.12f));
        s.add("default", slider);

        Slider.SliderStyle thick = new Slider.SliderStyle(slider);
        thick.background.setMinHeight(12f);
        thick.knob.setMinHeight(26f);
        thick.knob.setMinWidth(26f);
        thick.knobBefore.setMinHeight(12f);
        thick.knobAfter.setMinHeight(12f);
        s.add("thick", thick);

        SelectBox.SelectBoxStyle select = new SelectBox.SelectBoxStyle();
        select.font = baseFont;
        select.fontColor = Color.WHITE;
        select.background = s.newDrawable("white", new Color(1, 1, 1, 0.14f));
        select.backgroundOver = s.newDrawable("white", new Color(1, 1, 1, 0.20f));
        select.backgroundOpen = s.newDrawable("white", new Color(1, 1, 1, 0.20f));

        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = smallFont;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.selection = s.newDrawable("white", new Color(1, 1, 1, 0.94f));
        listStyle.background = s.newDrawable("white", new Color(0, 0, 0, 0.68f));
        select.listStyle = listStyle;

        select.scrollStyle = new ScrollPane.ScrollPaneStyle();
        s.add("default", select);

        ScrollPane.ScrollPaneStyle scroll = new ScrollPane.ScrollPaneStyle();
        scroll.background = s.newDrawable("white", new Color(1, 1, 1, 0.04f));
        scroll.vScroll = s.newDrawable("white", new Color(1, 1, 1, 0.10f));
        scroll.vScrollKnob = s.newDrawable("white", new Color(1, 1, 1, 0.35f));
        scroll.hScroll = s.newDrawable("white", new Color(1, 1, 1, 0.10f));
        scroll.hScrollKnob = s.newDrawable("white", new Color(1, 1, 1, 0.35f));
        s.add("default", scroll);

        return s;
    }

    private Table makeCard() {
        Table card = new Table();
        card.defaults().pad(6f);
        card.pad(14f);
        card.background(skin.newDrawable("white", new Color(1, 1, 1, 0.08f)));
        Image topDivider = new Image(skin.newDrawable("white", new Color(1, 1, 1, 0.10f)));
        topDivider.setHeight(1.2f);
        topDivider.setScaling(Scaling.stretchX);
        card.add(topDivider).growX().height(1.2f).padBottom(12f).row();
        return card;
    }

    private Texture makeVerticalGradient(int w, int h, Color top, Color bottom) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        for (int y = 0; y < h; y++) {
            float t = 1f - (y / (float) (h - 1));
            float r = top.r * t + bottom.r * (1f - t);
            float g = top.g * t + bottom.g * (1f - t);
            float b = top.b * t + bottom.b * (1f - t);
            float a = top.a * t + bottom.a * (1f - t);
            pixmap.setColor(r, g, b, a);
            pixmap.drawLine(0, y, w, y);
        }
        Texture tx = new Texture(pixmap);
        pixmap.dispose();
        return tx;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (skin != null) {
            skin.dispose();
        }
        if (gradientTexture != null) {
            gradientTexture.dispose();
        }
    }
}
