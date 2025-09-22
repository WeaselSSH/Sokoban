package GameLogic;


//1 = ES, 2 = EN

public final class Lang {

    private static int current = 1;

    private Lang() {
    }

    public static void init(int idioma) {
        current = (idioma == 2) ? 2 : 1;
    }

    public static int get() {
        return current;
    }

    public static String gameTitle() {
        return (current == 2) ? "SOKOBAN" : "SOKOBAN";
    }

    public static String play() {
        return (current == 2) ? "Play" : "Jugar";
    }

    public static String tutorial() {
        return (current == 2) ? "Tutorial" : "Tutorial";
    }

    public static String settings() {
        return (current == 2) ? "Settings" : "Configuraciones";
    }

    public static String history() {
        return (current == 2) ? "Match History" : "Historial de Partidas";
    }

    public static String universe() {
        return (current == 2) ? "Sokoban Universe" : "Universo Sokoban";
    }

    public static String logout() {
        return (current == 2) ? "Sign Out" : "Cerrar Sesion";
    }

    public static String guest() {
        return (current == 2) ? "Guest" : "Invitado";
    }

    public static String mustDoTutorialTitle() {
        return (current == 2) ? "Tutorial" : "Tutorial";
    }

    public static String mustDoTutorialBody() {
        return (current == 2) ? "You will start with the tutorial."
                : "Comenzaras con el tutorial.";
    }

    public static String loginScreenTitle() {
        return (current == 2) ? "SIGN IN" : "INICIO DE SESION";
    }

    public static String loginButton() {
        return (current == 2) ? "Sign in" : "Iniciar sesion";
    }

    public static String createPlayerButton() {
        return (current == 2) ? "Create player" : "Crear jugador";
    }

    public static String exitButton() {
        return (current == 2) ? "Exit" : "Salir";
    }

    public static String dlgLoginTitle() {
        return (current == 2) ? "Sign in" : "Iniciar sesion";
    }

    public static String fieldUser() {
        return (current == 2) ? "User" : "Usuario";
    }

    public static String fieldPassword() {
        return (current == 2) ? "Password" : "Contrasena";
    }

    public static String dlgCancel() {
        return (current == 2) ? "Cancel" : "Cancelar";
    }

    public static String dlgEnter() {
        return (current == 2) ? "Enter" : "Entrar";
    }

    public static String errOnlyAlnum() {
        return (current == 2) ? "Letters or numbers only" : "Solo letras o numeros";
    }

    public static String errUserNotFound() {
        return (current == 2) ? "User does not exist" : "Usuario no existe";
    }

    public static String errWrongPassword() {
        return (current == 2) ? "Wrong password" : "Contrasena incorrecta";
    }

    public static String errFail() {
        return (current == 2) ? "Failed" : "Fallo";
    }

    public static String dlgCreateTitle() {
        return (current == 2) ? "Create player" : "Crear jugador";
    }

    public static String fieldName() {
        return (current == 2) ? "Name" : "Nombre";
    }

    public static String dlgCreate() {
        return (current == 2) ? "Create" : "Crear";
    }

    public static String createdOkTitle() {
        return (current == 2) ? "Done" : "Listo";
    }

    public static String createdOkBody() {
        return (current == 2) ? "User created" : "Usuario creado";
    }
}
