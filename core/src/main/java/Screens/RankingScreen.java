package Screens;

import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import com.elkinedwin.LogicaUsuario.Usuario;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankingScreen extends BaseScreen {

    private final Game game;

    private FreeTypeFontGenerator generator;
    private BitmapFont titleFont, h2Font, bodyFont, smallFont, bigFont;

    private Texture texPanelBg, texDivider;

    public RankingScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        generator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));
        titleFont = genFont(88, "E6DFC9");
        h2Font    = genFont(48, "E6DFC9");
        bodyFont  = genFont(34, "E6DFC9");
        smallFont = genFont(26, "BFC4D0");
        bigFont   = genFont(56, "FFFFFF");

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        Label.LabelStyle h2Style    = new Label.LabelStyle(h2Font, h2Font.getColor());
        Label.LabelStyle thStyle    = new Label.LabelStyle(bodyFont, new Color(1,1,1,0.95f));
        Label.LabelStyle cellStyle  = new Label.LabelStyle(bodyFont, Color.WHITE);
        Label.LabelStyle bigStyle   = new Label.LabelStyle(bigFont, bigFont.getColor());
        Label.LabelStyle hintStyle  = new Label.LabelStyle(smallFont, new Color(1,1,1,0.6f));

        texPanelBg = makeColorTex(255,255,255,22);
        texDivider = makeColorTex(255,255,255,38);

        TextureRegionDrawable panelBg   = new TextureRegionDrawable(new TextureRegion(texPanelBg));
        TextureRegionDrawable dividerBg = new TextureRegionDrawable(new TextureRegion(texDivider));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        TextButton.TextButtonStyle backStyle = new TextButton.TextButtonStyle();
        backStyle.font = bodyFont;
        backStyle.fontColor = Color.WHITE;
        TextButton btnBack = new TextButton("Volver", backStyle);
        btnBack.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e, float x, float y){
                game.setScreen(new MenuScreen(game));
            }
        });

        Table topbar = new Table();
        topbar.add(btnBack).left().pad(12f);
        root.add(topbar).expandX().fillX().row();

        Table content = new Table();
        content.top().pad(24f);
        content.defaults().pad(10f);

        ScrollPane sp = new ScrollPane(content);
        sp.setFadeScrollBars(false);
        root.add(sp).expand().fill().pad(0, 16f, 16f, 16f).row();

        content.add(new Label("Ranking Global", titleStyle)).center().row();
        content.add(cardHeader("", h2Style)).expandX().fillX().padTop(12f).row();

        Table card = new Table();
        card.setBackground(panelBg);
        card.pad(14f);
        card.defaults().left().pad(6f);

        // Encabezado de dos filas con el distintivo del bloque acumulado
        Table header = new Table();
        header.defaults().left().pad(6f);

        header.add(new Label("Pos", thStyle)).width(80f);
        header.add(new Label("Avatar", thStyle)).width(120f);
        header.add(new Label("Usuario", thStyle)).width(240f);
        header.add(new Label("Niveles", thStyle)).width(120f);
        header.add(new Label("Partidas", thStyle)).width(140f);
        header.add(new Label("Tiempo de juego", thStyle)).width(200f);
        header.add(new Label("Acumulado", bigStyle)).colspan(3).center().padBottom(2f).row();

        header.add().width(80f);
        header.add().width(120f);
        header.add().width(240f);
        header.add().width(120f);
        header.add().width(140f);
        header.add().width(200f);
        header.add(new Label("Pasos", thStyle)).width(140f);
        header.add(new Label("Empujes", thStyle)).width(140f);
        header.add(new Label("Tiempo", thStyle)).width(200f).row();

        card.add(header).expandX().fillX().row();

        List<Usuario> ranking = buildRankingList();
        int pos = 1;
        Usuario activo = ManejoUsuarios.UsuarioActivo;

        for (Usuario u : ranking) {
            boolean isActive = (activo != null && u != null &&
                                activo.getUsuario() != null &&
                                activo.getUsuario().equalsIgnoreCase(u.getUsuario()));

            Table row = new Table();
            row.defaults().left().pad(6f);

            row.add(new Label(String.valueOf(pos), cellStyle)).width(80f);

            Image avatar = new Image(loadAvatar(u));
            row.add(avatar).size(72f, 72f).width(120f);

            Label lu = new Label(u != null && u.getUsuario()!=null ? u.getUsuario() : "-", cellStyle);
            if (isActive) lu.setColor(Color.valueOf("E6DFC9"));
            row.add(lu).width(240f);

            row.add(new Label(String.valueOf(u.getConteoNivelesCompletados()), cellStyle)).width(120f);

            int partidasPorHistorial = (u != null && u.historial != null) ? u.historial.size() : 0;
            row.add(new Label(String.valueOf(partidasPorHistorial), cellStyle)).width(140f);

            row.add(new Label(formatSecondsHuman(u.getTiempoJugadoTotal()), cellStyle)).width(200f);
            row.add(new Label(String.valueOf(u.getSumaPasosMejorIntento()), cellStyle)).width(140f);
            row.add(new Label(String.valueOf(u.getSumaEmpujesMejorIntento()), cellStyle)).width(140f);
            row.add(new Label(formatSecondsHuman(u.getSumaTiempoMejorIntento()), cellStyle)).width(200f).row();

            card.add(row).expandX().fillX().row();

            if (pos < ranking.size()) {
                Image div = new Image(dividerBg);
                card.add(div).height(1f).expandX().fillX().padTop(2f).padBottom(2f).row();
            }
            pos++;
        }

        // Leyenda aclaratoria del bloque acumulado
        Label legend = new Label("Pasos/Empujes/Tiempo son acumulados del mejor intento de cada nivel.", hintStyle);
        card.add(legend).left().padTop(8f).row();

        content.add(card).expandX().fillX().row();
    }

    private List<Usuario> buildRankingList() {
        Usuario activo = ManejoUsuarios.UsuarioActivo;
        ArrayList<Usuario> lista = (activo != null && activo.getRivales() != null)
                ? new ArrayList<>(activo.getRivales())
                : new ArrayList<>();

        // Asegurar que el usuario activo aparezca también en el ranking
        if (activo != null) {
            boolean yaEsta = false;
            for (Usuario u : lista) {
                if (u != null && activo.getUsuario() != null &&
                    activo.getUsuario().equalsIgnoreCase(u.getUsuario())) {
                    yaEsta = true; break;
                }
            }
            if (!yaEsta) lista.add(activo);
        }

        lista.sort(new Comparator<Usuario>() {
            @Override
            public int compare(Usuario a, Usuario b) {
                if (a == null && b == null) return 0;
                if (a == null) return 1;
                if (b == null) return -1;

                int compNiv = Integer.compare(b.getConteoNivelesCompletados(), a.getConteoNivelesCompletados());
                if (compNiv != 0) return compNiv;

                int compPasos = Integer.compare(a.getSumaPasosMejorIntento(), b.getSumaPasosMejorIntento());
                if (compPasos != 0) return compPasos;

                int compTiempo = Integer.compare(a.getSumaTiempoMejorIntento(), b.getSumaTiempoMejorIntento());
                if (compTiempo != 0) return compTiempo;

                int compEmp = Integer.compare(a.getSumaEmpujesMejorIntento(), b.getSumaEmpujesMejorIntento());
                if (compEmp != 0) return compEmp;

                String ua = a.getUsuario() == null ? "" : a.getUsuario();
                String ub = b.getUsuario() == null ? "" : b.getUsuario();
                return ua.compareToIgnoreCase(ub);
            }
        });

        return lista;
    }

    private Texture loadAvatar(Usuario u) {
        String def = "ui/default_avatar.png";
        try {
            if (u != null && u.avatar != null && !u.avatar.trim().isEmpty()) {
                if (files.internal(u.avatar).exists()) {
                    return new Texture(files.internal(u.avatar));
                } else if (files.absolute(u.avatar).exists()) {
                    return new Texture(files.absolute(u.avatar));
                }
            }
        } catch (Exception ignored) {}
        return new Texture(files.internal(def));
    }

    private BitmapFont genFont(int size, String hex) {
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = size; p.color = Color.valueOf(hex);
        return generator.generateFont(p);
    }

    private Texture makeColorTex(int r, int g, int b, int a) {
        Pixmap pm = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pm.setColor(r/255f, g/255f, b/255f, a/255f);
        pm.fill();
        Texture t = new Texture(pm); pm.dispose();
        return t;
    }

    private Table cardHeader(String text, Label.LabelStyle style) {
        Table t = new Table();
        t.defaults().left();
        t.add(new Label(text, style)).left();
        return t;
    }

    private String formatSecondsHuman(int sec){
        if (sec <= 0) return "0 s";
        int h = sec / 3600;
        int m = (sec % 3600) / 60;
        int s = sec % 60;
        if (h > 0) return String.format("%dh %02dmin %02ds", h, m, s);
        if (m > 0) return String.format("%dmin %02ds", m, s);
        return String.format("%ds", s);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (generator != null) generator.dispose();
        if (titleFont != null) titleFont.dispose();
        if (h2Font != null)    h2Font.dispose();
        if (bodyFont != null)  bodyFont.dispose();
        if (smallFont != null) smallFont.dispose();
        if (bigFont != null)   bigFont.dispose();
        if (texPanelBg != null) texPanelBg.dispose();
        if (texDivider != null) texDivider.dispose();
    }
}
