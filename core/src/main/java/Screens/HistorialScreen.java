package Screens;

import GameLogic.Lang;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import com.elkinedwin.LogicaUsuario.Partida;
import java.util.ArrayList;

public class HistorialScreen extends BaseScreen {

    private final Game game;

    private BitmapFont titleFont;
    private BitmapFont sectionFont;
    private BitmapFont bodyFont;
    private BitmapFont smallFont;

    private Texture panelBackgroundTexture;
    private Texture dividerTexture;

    public HistorialScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        titleFont = createOutlinedFont(88, Color.valueOf("E6DFC9"), 2, Color.BLACK);
        sectionFont = createOutlinedFont(48, Color.valueOf("E6DFC9"), 2, Color.BLACK);
        bodyFont = createOutlinedFont(34, Color.valueOf("E6DFC9"), 2, Color.BLACK);
        smallFont = createOutlinedFont(26, Color.valueOf("BFC4D0"), 2, Color.BLACK);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        Label.LabelStyle keyStyle = new Label.LabelStyle(bodyFont, new Color(1, 1, 1, 0.85f));
        Label.LabelStyle valStyle = new Label.LabelStyle(bodyFont, Color.WHITE);

        panelBackgroundTexture = makeSolidTexture(255, 255, 255, 22);
        dividerTexture = makeSolidTexture(255, 255, 255, 38);

        TextureRegionDrawable panelBg = new TextureRegionDrawable(new TextureRegion(panelBackgroundTexture));
        TextureRegionDrawable dividerBg = new TextureRegionDrawable(new TextureRegion(dividerTexture));

        TextButton.TextButtonStyle backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle.font = bodyFont;
        backButtonStyle.fontColor = Color.WHITE;
        Texture transparent = makeSolidTexture(0, 0, 0, 0);
        TextureRegionDrawable transparentDrawable = new TextureRegionDrawable(new TextureRegion(transparent));
        backButtonStyle.up = backButtonStyle.over = backButtonStyle.down
                = backButtonStyle.checked = backButtonStyle.disabled = transparentDrawable;

        TextButton backButton = new TextButton(Lang.back(), backButtonStyle);
        backButton.getLabel().setColor(Color.WHITE);
        backButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Table topBar = new Table();
        topBar.add(backButton).left().pad(12f);
        root.add(topBar).expandX().fillX().row();

        Table content = new Table();
        content.top().pad(24f);
        content.defaults().pad(10f);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFadeScrollBars(false);
        root.add(scroll).expand().fill().pad(0, 16f, 16f, 16f).row();

        content.add(new Label(Lang.history(), titleStyle)).center().padBottom(10f).row();

        Table historyCard = new Table();
        historyCard.setBackground(panelBg);
        historyCard.pad(14f);
        historyCard.defaults().left().pad(8f);
        content.add(historyCard).expandX().fillX().row();

        ArrayList<Partida> history = (ManejoUsuarios.UsuarioActivo != null)
                ? ManejoUsuarios.UsuarioActivo.historial
                : null;

        if (history == null || history.isEmpty()) {
            Label empty = new Label(Lang.historyEmpty(), new Label.LabelStyle(sectionFont, new Color(1, 1, 1, 0.8f)));
            historyCard.add(empty).left().pad(12f).row();
        } else {
            Table header = new Table();
            header.defaults().left().pad(6f);
            header.add(new Label(Lang.historyDate(), keyStyle)).width(280f);
            header.add(new Label(Lang.hudLevel(), keyStyle)).width(120f);
            header.add(new Label(Lang.historyTime(), keyStyle)).width(160f);
            header.add(new Label(Lang.historyAttempts(), keyStyle)).width(140f);
            header.add(new Label(Lang.historyAchievements(), keyStyle)).growX().row();
            historyCard.add(header).expandX().fillX().row();

            Image topDivider = new Image(dividerBg);
            historyCard.add(topDivider).height(1.2f).expandX().fillX().row();

            for (int i = history.size() - 1; i >= 0; i--) {
                Partida entry = history.get(i);

                String dateText = safeText(entry.getFecha());
                String levelText = String.valueOf(entry.getNivel());
                String timeText = formatTimeHuman(entry.getTiempo());
                String attemptsText = String.valueOf(entry.getIntentos());
                String achievements = safeText(entry.getLogros());

                Table row = new Table();
                row.defaults().left().pad(6f);
                row.add(new Label(dateText, valStyle)).width(280f);
                row.add(new Label(levelText, valStyle)).width(120f);
                row.add(new Label(timeText, valStyle)).width(160f);
                row.add(new Label(attemptsText, valStyle)).width(140f);

                Label achievementsLabel = new Label(achievements, valStyle);
                achievementsLabel.setWrap(true);
                row.add(achievementsLabel).growX().width(520f).row();

                historyCard.add(row).expandX().fillX().row();

                if (i > 0) {
                    Image divider = new Image(dividerBg);
                    historyCard.add(divider).height(1.0f).expandX().fillX().row();
                }
            }
        }
    }

    private String formatTimeHuman(int seconds) {
        if (seconds <= 0) {
            return "0s";
        }
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        if (h > 0) {
            return String.format("%d:%02d:%02d", h, m, s);
        }
        return String.format("%02d:%02d", m, s);
    }

    private String safeText(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (sectionFont != null) {
            sectionFont.dispose();
        }
        if (bodyFont != null) {
            bodyFont.dispose();
        }
        if (smallFont != null) {
            smallFont.dispose();
        }
        if (panelBackgroundTexture != null) {
            panelBackgroundTexture.dispose();
            panelBackgroundTexture = null;
        }
        if (dividerTexture != null) {
            dividerTexture.dispose();
            dividerTexture = null;
        }
    }
}
