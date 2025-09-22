package GameLogic;

public final class Lang {

    private static Language current = Language.ES;

    private Lang() {
    }

    public static void init(Language language) {
        current = (language == null) ? Language.ES : language;
    }

    public static Language get() {
        return current;
    }

    public static String gameTitle() {
        return current == Language.EN ? "SOKOBAN" : "SOKOBAN";
    }

    public static String play() {
        return current == Language.EN ? "Play" : "Jugar";
    }

    public static String tutorial() {
        return current == Language.EN ? "Tutorial" : "Tutorial";
    }

    public static String settings() {
        return current == Language.EN ? "Settings" : "Configuraciones";
    }

    public static String history() {
        return current == Language.EN ? "Match History" : "Historial de Partidas";
    }

    public static String universe() {
        return current == Language.EN ? "Sokoban Universe" : "Universo Sokoban";
    }

    public static String logout() {
        return current == Language.EN ? "Sign Out" : "Cerrar Sesion";
    }

    public static String guest() {
        return current == Language.EN ? "Guest" : "Invitado";
    }

    public static String mustDoTutorialTitle() {
        return current == Language.EN ? "Tutorial" : "Tutorial";
    }

    public static String mustDoTutorialBody() {
        return current == Language.EN
                ? "You will start with the tutorial."
                : "Comenzaras con el tutorial.";
    }
}
