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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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

    private KeyBinder kbUp, kbDown, kbLeft, kbRight, kbReiniciar;
    private Slider sliderVolumen;
    private Label lblVolumenValor;
    private SelectBox<String> sbIdioma;

    private TextButton btnGuardar, btnVolver;

    private int volOriginal;
    private int idiomaOriginal;

    private Texture gradientTex;

    private static final float CONTENT_MAX_WIDTH = 900f;
    private static final float TOP_OFFSET = 16f;

    public ConfigScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        int idioma = (ManejoUsuarios.UsuarioActivo != null) ? ManejoUsuarios.UsuarioActivo.getIdioma() : 1;
        Lang.init(idioma);

        skin = buildSkin();

        BitmapFont f = skin.getFont("base-font");
        f.getData().setScale(1.15f);

        int kUp = getCfg("MoverArriba", Input.Keys.UP);
        int kDown = getCfg("MoverAbajo", Input.Keys.DOWN);
        int kLeft = getCfg("MoverIzq", Input.Keys.LEFT);
        int kRight = getCfg("MoverDer", Input.Keys.RIGHT);
        int kRei = getCfg("Reiniciar", Input.Keys.R);

        Integer vVol = cfgAny("volumen", "Volumen");
        volOriginal = vVol != null ? vVol : 70;

        Integer vId = cfgAny("idioma", "Idioma");
        idiomaOriginal = vId != null ? vId : 1;

        gradientTex = makeVerticalGradient(16, 400,
                new Color(0.08f, 0.10f, 0.13f, 1f),
                new Color(0.12f, 0.14f, 0.18f, 1f));
        Image bg = new Image(new TextureRegionDrawable(new TextureRegion(gradientTex)));
        bg.setFillParent(true);
        bg.setScaling(Scaling.fill);
        stage.addActor(bg);

        kbUp = new KeyBinder(Lang.cfgUp(), "MoverArriba", kUp);
        kbDown = new KeyBinder(Lang.cfgDown(), "MoverAbajo", kDown);
        kbLeft = new KeyBinder(Lang.cfgLeft(), "MoverIzq", kLeft);
        kbRight = new KeyBinder(Lang.cfgRight(), "MoverDer", kRight);
        kbReiniciar = new KeyBinder(Lang.cfgRestart(), "Reiniciar", kRei);

        Label lblVol = new Label(Lang.cfgVolume(), skin, "section");
        lblVol.setFontScale(1.1f);
        sliderVolumen = new Slider(0, 100, 1, false, skin, "thick");
        sliderVolumen.setValue(volOriginal);
        lblVolumenValor = new Label(volOriginal + "%", skin, "mono");
        lblVolumenValor.setFontScale(1.05f);

        sliderVolumen.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onVolChanged();
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                onVolChanged();
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int b) {
                onVolChanged();
            }
        });

        Label tituloIdioma = new Label(Lang.cfgLanguage(), skin, "section");
        tituloIdioma.setFontScale(1.1f);
        sbIdioma = new SelectBox<>(skin);
        sbIdioma.setItems(Lang.langSpanish(), Lang.langEnglish());
        sbIdioma.setSelected(idiomaOriginal == 2 ? Lang.langEnglish() : Lang.langSpanish());
        sbIdioma.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                actualizarEstadoGuardar();
            }
        });

        btnGuardar = new TextButton(Lang.saveChanges(), skin, "cta");
        btnGuardar.getLabel().setFontScale(1.05f);
        btnGuardar.setDisabled(true);
        btnGuardar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (btnGuardar.isDisabled()) {
                    return;
                }

                putCfg("MoverArriba", kbUp.getActual());
                putCfg("MoverAbajo", kbDown.getActual());
                putCfg("MoverIzq", kbLeft.getActual());
                putCfg("MoverDer", kbRight.getActual());
                putCfg("Reiniciar", kbReiniciar.getActual());

                int nuevoVol = Math.round(sliderVolumen.getValue());
                putCfgAny(nuevoVol, "volumen", "Volumen");

                int nuevoIdioma = sbIdioma.getSelected().equals(Lang.langEnglish()) ? 2 : 1;
                putCfgAny(nuevoIdioma, "idioma", "Idioma");
                try {
                    ArchivoGuardar.guardarConfiguracion();
                } catch (Exception ignored) {
                }

                // Reaplica idioma si cambió
                if (nuevoIdioma != idiomaOriginal) {
                    if (ManejoUsuarios.UsuarioActivo != null) {
                        ManejoUsuarios.UsuarioActivo.setIdioma(nuevoIdioma);
                    }
                    Lang.init(nuevoIdioma);
                }

                kbUp.confirmar();
                kbDown.confirmar();
                kbLeft.confirmar();
                kbRight.confirmar();
                kbReiniciar.confirmar();
                volOriginal = nuevoVol;
                idiomaOriginal = nuevoIdioma;
                actualizarEstadoGuardar();

                Dialog d = new Dialog("", skin);
                d.setModal(true);
                d.getContentTable().pad(12f);
                d.getContentTable().add(new Label(Lang.savedCheck(), skin, "dialog")).left();
                d.button("OK", true);
                d.show(stage);

                // Recargar pantalla para reflejar textos nuevos si cambió idioma
                if (nuevoIdioma != idioma) {
                    game.setScreen(new ConfigScreen(game));
                }
            }
        });

        btnVolver = new TextButton(Lang.back(), skin, "ghost");
        btnVolver.getLabel().setFontScale(1.05f);
        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table content = new Table();
        content.pad(20f, 24f, 20f, 24f);

        // Tarjeta: CONTROLES
        Table cardControles = makeCard();
        Label secControles = new Label(Lang.cfgControls(), skin, "section");
        secControles.setFontScale(1.12f);
        cardControles.add(secControles).left().padBottom(8f).row();
        Label hint = new Label(Lang.cfgHintPressKey(), skin, "hint");
        cardControles.add(hint).left().padBottom(6f).row();

        Table t = new Table();
        t.defaults().pad(8f);
        t.add(kbUp.lbl).left().padRight(12f).width(150f);
        t.add(kbUp.btn).width(300f).height(40f).row();
        t.add(kbDown.lbl).left().padRight(12f).width(150f);
        t.add(kbDown.btn).width(300f).height(40f).row();
        t.add(kbLeft.lbl).left().padRight(12f).width(150f);
        t.add(kbLeft.btn).width(300f).height(40f).row();
        t.add(kbRight.lbl).left().padRight(12f).width(150f);
        t.add(kbRight.btn).width(300f).height(40f).row();
        t.add(kbReiniciar.lbl).left().padRight(12f).width(150f);
        t.add(kbReiniciar.btn).width(300f).height(40f).row();
        cardControles.add(t).growX();
        content.add(cardControles).growX().padBottom(14f).row();

        // Tarjeta: AUDIO
        Table cardAudio = makeCard();
        Label secAudio = new Label(Lang.cfgAudio(), skin, "section");
        secAudio.setFontScale(1.12f);
        cardAudio.add(secAudio).left().padBottom(8f).row();
        Table ta = new Table();
        ta.defaults().pad(8f);
        ta.add(lblVol).left().padRight(12f).width(150f);
        ta.add(sliderVolumen).growX().padRight(12f).minWidth(420f).height(26f);
        ta.add(lblVolumenValor).width(80f).left();
        cardAudio.add(ta).growX();
        content.add(cardAudio).growX().padBottom(14f).row();

        // Tarjeta: IDIOMA
        Table cardIdioma = makeCard();
        cardIdioma.add(tituloIdioma).left().padBottom(8f).row();
        Table ti = new Table();
        ti.defaults().pad(8f);
        ti.add(new Label(Lang.cfgSelect(), skin)).left().padRight(12f).width(150f);
        ti.add(sbIdioma).width(300f).left();
        cardIdioma.add(ti).growX();
        content.add(cardIdioma).growX().padBottom(10f).row();

        Table botones = new Table();
        botones.defaults().pad(10f).width(240f).height(52f);
        botones.add(btnVolver);
        botones.add(btnGuardar);
        content.add(botones).right();

        ScrollPane scroller = new ScrollPane(content, skin);
        scroller.setFadeScrollBars(false);
        scroller.setScrollingDisabled(true, false);

        Table frame = new Table();
        frame.setFillParent(true);
        frame.top().padTop(TOP_OFFSET).padBottom(16f);
        frame.add(scroller).width(CONTENT_MAX_WIDTH).growY().top();
        stage.addActor(frame);

        // Captura teclas
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                boolean handled = false;
                handled |= kbUp.tryCapture(keycode);
                handled |= kbDown.tryCapture(keycode);
                handled |= kbLeft.tryCapture(keycode);
                handled |= kbRight.tryCapture(keycode);
                handled |= kbReiniciar.tryCapture(keycode);
                if (handled) {
                    actualizarEstadoGuardar();
                }
                return handled;
            }
        });
    }

    private void onVolChanged() {
        int v = Math.round(sliderVolumen.getValue());
        lblVolumenValor.setText(v + "%");
        AudioBus.setMasterVolume(v / 100f);
        actualizarEstadoGuardar();
    }

    private void actualizarEstadoGuardar() {
        int idiomaNow = sbIdioma.getSelected().equals(Lang.langEnglish()) ? 2 : 1;
        boolean cambioValido
                = kbUp.cambioValido()
                || kbDown.cambioValido()
                || kbLeft.cambioValido()
                || kbRight.cambioValido()
                || kbReiniciar.cambioValido()
                || Math.round(sliderVolumen.getValue()) != volOriginal
                || idiomaNow != idiomaOriginal;
        btnGuardar.setDisabled(!cambioValido);
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

        final Label lbl;
        final TextButton btn;
        int original;
        int actual;
        boolean escuchando = false;

        KeyBinder(String nombre, String cfgKey, int valor) {
            this.lbl = new Label(nombre, skin, "label-dim");
            this.lbl.setFontScale(1.02f);
            this.btn = new TextButton(Input.Keys.toString(valor), skin, "key");
            this.btn.getLabel().setFontScale(1.02f);
            this.btn.getLabel().setColor(Color.WHITE);

            this.original = valor;
            this.actual = valor;

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    escuchando = true;
                    btn.setText(Lang.pressAKey());
                    btn.getLabel().setColor(Color.WHITE);
                }
            });
        }

        boolean tryCapture(int keycode) {
            if (!escuchando) {
                return false;
            }
            if (keycode == Input.Keys.ESCAPE) {
                escuchando = false;
                btn.setText(Input.Keys.toString(actual));
                btn.getLabel().setColor(Color.WHITE);
                return true;
            }
            actual = keycode;
            btn.setText(Input.Keys.toString(actual));
            escuchando = false;
            btn.getLabel().setColor(Color.WHITE);
            return true;
        }

        boolean cambioValido() {
            return actual != original;
        }

        void confirmar() {
            original = actual;
        }

        int getActual() {
            return actual;
        }
    }

    private Skin buildSkin() {
        Skin skin = new Skin();

        FreeTypeFontGenerator fontGenerator
                = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pokemon_fire_red.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams
                = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParams.borderWidth = 2f;
        fontParams.borderColor = Color.BLACK;

        fontParams.size = 36;
        fontParams.color = new Color(1, 1, 1, 0.94f);
        BitmapFont baseFont = fontGenerator.generateFont(fontParams);

        fontParams.size = 30;
        fontParams.color = new Color(1, 1, 1, 0.86f);
        BitmapFont smallFont = fontGenerator.generateFont(fontParams);

        fontParams.size = 38;
        fontParams.color = new Color(1, 1, 1, 0.92f);
        BitmapFont sectionFont = fontGenerator.generateFont(fontParams);

        fontParams.size = 32;
        fontParams.color = new Color(1, 1, 1, 0.92f);
        BitmapFont monoFont = fontGenerator.generateFont(fontParams);

        fontGenerator.dispose();

        skin.add("base-font", baseFont, BitmapFont.class);
        skin.add("small-font", smallFont, BitmapFont.class);
        skin.add("section-font", sectionFont, BitmapFont.class);
        skin.add("mono-font", monoFont, BitmapFont.class);

        Pixmap whitePx = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        whitePx.setColor(Color.WHITE);
        whitePx.fill();
        Texture whiteTexture = new Texture(whitePx);
        whitePx.dispose();
        skin.add("white", whiteTexture);

        skin.add("default", new Label.LabelStyle(baseFont, new Color(1, 1, 1, 0.94f)));
        skin.add("label-dim", new Label.LabelStyle(smallFont, new Color(1, 1, 1, 0.86f)));
        skin.add("hint", new Label.LabelStyle(smallFont, new Color(1, 1, 1, 0.52f)));
        skin.add("section", new Label.LabelStyle(sectionFont, new Color(1, 1, 1, 0.92f)));
        skin.add("dialog", new Label.LabelStyle(baseFont, new Color(1, 1, 1, 0.96f)));
        skin.add("mono", new Label.LabelStyle(monoFont, new Color(1, 1, 1, 0.92f)));

        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle();
        btn.font = baseFont;
        btn.up = skin.newDrawable("white", new Color(1, 1, 1, 0.12f));
        btn.over = skin.newDrawable("white", new Color(1, 1, 1, 0.18f));
        btn.down = skin.newDrawable("white", new Color(1, 1, 1, 0.25f));
        btn.disabled = skin.newDrawable("white", new Color(1, 1, 1, 0.06f));
        btn.fontColor = Color.WHITE;
        skin.add("default", btn);

        TextButton.TextButtonStyle ctaBtn = new TextButton.TextButtonStyle(btn);
        ctaBtn.up = skin.newDrawable("white", new Color(0.30f, 0.62f, 1f, 0.90f));
        ctaBtn.over = skin.newDrawable("white", new Color(0.35f, 0.67f, 1f, 0.98f));
        ctaBtn.down = skin.newDrawable("white", new Color(0.27f, 0.58f, 0.95f, 1f));
        ctaBtn.disabled = skin.newDrawable("white", new Color(0.30f, 0.62f, 1f, 0.40f));
        ctaBtn.font = baseFont;
        ctaBtn.fontColor = Color.WHITE;
        skin.add("cta", ctaBtn);

        TextButton.TextButtonStyle ghostBtn = new TextButton.TextButtonStyle(btn);
        ghostBtn.up = skin.newDrawable("white", new Color(1, 1, 1, 0.10f));
        ghostBtn.over = skin.newDrawable("white", new Color(1, 1, 1, 0.14f));
        ghostBtn.down = skin.newDrawable("white", new Color(1, 1, 1, 0.20f));
        ghostBtn.font = baseFont;
        skin.add("ghost", ghostBtn);

        TextButton.TextButtonStyle keyBtn = new TextButton.TextButtonStyle(btn);
        keyBtn.up = skin.newDrawable("white", new Color(1, 1, 1, 0.18f));
        keyBtn.over = skin.newDrawable("white", new Color(1, 1, 1, 0.24f));
        keyBtn.down = skin.newDrawable("white", new Color(1, 1, 1, 0.30f));
        keyBtn.font = baseFont;
        keyBtn.fontColor = Color.WHITE;
        skin.add("key", keyBtn);

        Window.WindowStyle window = new Window.WindowStyle();
        window.titleFont = smallFont;
        window.titleFontColor = Color.WHITE;
        window.background = skin.newDrawable("white", new Color(0f, 0f, 0f, 0.86f));
        skin.add("default", window);

        Slider.SliderStyle slider = new Slider.SliderStyle();
        slider.background = skin.newDrawable("white", new Color(1, 1, 1, 0.16f));
        slider.knob = skin.newDrawable("white", Color.WHITE);
        slider.knobBefore = skin.newDrawable("white", new Color(1, 1, 1, 0.60f));
        slider.knobAfter = skin.newDrawable("white", new Color(1, 1, 1, 0.12f));
        skin.add("default", slider);

        Slider.SliderStyle thickSlider = new Slider.SliderStyle(slider);
        thickSlider.background.setMinHeight(12f);
        thickSlider.knob.setMinHeight(26f);
        thickSlider.knob.setMinWidth(26f);
        thickSlider.knobBefore.setMinHeight(12f);
        thickSlider.knobAfter.setMinHeight(12f);
        skin.add("thick", thickSlider);

        SelectBox.SelectBoxStyle select = new SelectBox.SelectBoxStyle();
        select.font = baseFont;
        select.fontColor = Color.WHITE;
        select.background = skin.newDrawable("white", new Color(1, 1, 1, 0.14f));
        select.backgroundOver = skin.newDrawable("white", new Color(1, 1, 1, 0.20f));
        select.backgroundOpen = skin.newDrawable("white", new Color(1, 1, 1, 0.20f));

        List.ListStyle dropdownList = new List.ListStyle();
        dropdownList.font = smallFont;
        dropdownList.fontColorSelected = Color.BLACK;
        dropdownList.fontColorUnselected = Color.WHITE;
        dropdownList.selection = skin.newDrawable("white", new Color(1, 1, 1, 0.94f));
        dropdownList.background = skin.newDrawable("white", new Color(0, 0, 0, 0.68f));
        select.listStyle = dropdownList;

        select.scrollStyle = new ScrollPane.ScrollPaneStyle();
        skin.add("default", select);

        ScrollPane.ScrollPaneStyle scrollPane = new ScrollPane.ScrollPaneStyle();
        scrollPane.background = skin.newDrawable("white", new Color(1, 1, 1, 0.04f));
        scrollPane.vScroll = skin.newDrawable("white", new Color(1, 1, 1, 0.10f));
        scrollPane.vScrollKnob = skin.newDrawable("white", new Color(1, 1, 1, 0.35f));
        scrollPane.hScroll = skin.newDrawable("white", new Color(1, 1, 1, 0.10f));
        scrollPane.hScrollKnob = skin.newDrawable("white", new Color(1, 1, 1, 0.35f));
        skin.add("default", scrollPane);

        return skin;
    }

    private Table makeCard() {
        Table card = new Table();
        card.defaults().pad(6f);
        card.pad(14f);
        card.background(skin.newDrawable("white", new Color(1, 1, 1, 0.08f)));
        Image sepTop = new Image(skin.newDrawable("white", new Color(1, 1, 1, 0.10f)));
        sepTop.setHeight(1.2f);
        sepTop.setScaling(Scaling.stretchX);
        card.add(sepTop).growX().height(1.2f).padBottom(12f).row();
        return card;
    }

    private Texture makeVerticalGradient(int w, int h, Color top, Color bottom) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        for (int y = 0; y < h; y++) {
            float t = 1f - (y / (float) (h - 1));
            float r = top.r * t + bottom.r * (1f - t);
            float g = top.g * t + bottom.g * (1f - t);
            float b = top.b * t + bottom.b * (1f - t);
            float a = top.a * t + bottom.a * (1f - t);
            p.setColor(r, g, b, a);
            p.drawLine(0, y, w, y);
        }
        Texture tx = new Texture(p);
        p.dispose();
        return tx;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (skin != null) {
            skin.dispose();
        }
        if (gradientTex != null) {
            gradientTex.dispose();
        }
    }
}
