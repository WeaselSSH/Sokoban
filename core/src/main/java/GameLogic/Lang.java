package GameLogic;

// 1 = ES, 2 = EN
public final class Lang {

    private static int current = 1;

    public static String errUserExists() {
       return (current==2) ? "User already in use":"Usuario ya en uso";
    }

    public static String errPasswordMismatch() {
       return (current==2)? "Password does not match":"La contrasena no coincide";
    }

    private Lang() {
    }

    public static void init(int idioma) {
        current = (idioma == 2) ? 2 : 1;
    }

    public static int get() {
        return current;
    }

    // === MENU / GENERALES ===
    public static String gameTitle() {
        return "SOKOBAN";
    }

    public static String play() {
        return (current == 2) ? "Play" : "Jugar";
    }

    public static String tutorial() {
        return "Tutorial";
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

    // === TUTORIAL GATE ===
    public static String mustDoTutorialTitle() {
        return "Tutorial";
    }

    public static String mustDoTutorialBody() {
        return (current == 2) ? "You will start with the tutorial." : "Comenzaras con el tutorial.";
    }

    // === LOGIN ===
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

    // === CONFIG ===
    public static String cfgControls() {
        return (current == 2) ? "Controls" : "Controles";
    }

    public static String cfgUp() {
        return (current == 2) ? "Up" : "Arriba";
    }

    public static String cfgDown() {
        return (current == 2) ? "Down" : "Abajo";
    }

    public static String cfgLeft() {
        return (current == 2) ? "Left" : "Izquierda";
    }

    public static String cfgRight() {
        return (current == 2) ? "Right" : "Derecha";
    }

    public static String cfgRestart() {
        return (current == 2) ? "Restart" : "Reiniciar";
    }

    public static String cfgAudio() {
        return "Audio";
    }

    public static String cfgVolume() {
        return (current == 2) ? "Volume" : "Volumen";
    }

    public static String cfgLanguage() {
        return (current == 2) ? "Language" : "Idioma";
    }

    public static String cfgSelect() {
        return (current == 2) ? "Select" : "Seleccionar";
    }

    public static String saveChanges() {
        return (current == 2) ? "Save changes" : "Guardar cambios";
    }

    public static String back() {
        return (current == 2) ? "Back" : "Regresar";
    }

    public static String cfgHintPressKey() {
        return (current == 2)
                ? "Click a button and press a key · ESC cancels"
                : "Clic en un botón y presiona una tecla · ESC cancela";
    }

    public static String pressAKey() {
        return (current == 2) ? "Press a key…" : "Presiona una tecla…";
    }

    public static String savedCheck() {
        return (current == 2) ? "✅  Changes saved" : "✅  Cambios guardados";
    }

    public static String langSpanish() {
        return "Espanol";
    }

    public static String langEnglish() {
        return "Ingles";
    }

    // === HUD / GAME ===
    public static String hudLevel() {
        return (current == 2) ? "Level" : "Nivel";
    }

    public static String hudSteps() {
        return (current == 2) ? "Steps" : "Pasos";
    }

    public static String hudPushes() {
        return (current == 2) ? "Pushes" : "Empujes";
    }

    public static String hudTime() {
        return (current == 2) ? "Time" : "Tiempo";
    }

    public static String hudControls() {
        return (current == 2) ? "Controls" : "Controles";
    }

    // === VICTORY ===
    public static String victoryTutorial() {
        return (current == 2) ? "Congrats on completing the tutorial" : "Felicidades por pasarte el tutorial";
    }

    public static String victoryGame() {
        return (current == 2) ? "CONGRATULATIONS! YOU BEAT THE GAME!" : "FELICIDADES POR PASARTE EL JUEGO!";
    }

    public static String victoryThanks() {
        return (current == 2) ? "THANKS FOR PLAYING!" : "GRACIAS POR JUGARLO!";
    }

    public static String victoryLevelCompleted() {
        return (current == 2) ? "Level completed" : "Nivel completado";
    }

    public static String victoryHintBack() {
        return (current == 2) ? "[ESC] Back to selector" : "[ESC] Volver al selector";
    }

    public static String victoryHintNextBack() {
        return (current == 2) ? "[ENTER] Next level     [ESC] Back to selector"
                : "[ENTER] Siguiente nivel     [ESC] Volver al selector";
    }

    // === ELEVATOR ===
    public static String elevatorEnteringLevel() {
        return (current == 2) ? "Entering Level" : "Entrando al Nivel";
    }

    public static String elevatorHintOpen() {
        return (current == 2) ? "ENTER: Select level" : "ENTER: Seleccionar nivel";
    }

    public static String elevatorSelectTitle() {
        return (current == 2) ? "Select level (ESC to exit)" : "Selecciona nivel (ESC para salir)";
    }

    public static String elevatorLevel() {
        return (current == 2) ? "Level" : "Nivel";
    }

    // === HISTORIAL ===
    public static String historyTitle() {
        return (current == 2) ? "Match History" : "Historial de Partidas";
    }

    public static String historyEmpty() {
        return (current == 2) ? "No matches recorded yet" : "Aun no hay partidas registradas";
    }

    public static String historyDate() {
        return (current == 2) ? "Date" : "Fecha";
    }

    public static String historyLevel() {
        return (current == 2) ? "Level" : "Nivel";
    }

    public static String historyTime() {
        return (current == 2) ? "Time" : "Tiempo";
    }

    public static String historyAttempts() {
        return (current == 2) ? "Attempts" : "Intentos";
    }

    public static String historyAchievements() {
        return (current == 2) ? "Achievements" : "Logros";
    }

    public static String backToMenu() {
        return (current == 2) ? "Back" : "Volver";
    }

    // === BASE PLAY (PAUSA / LOGS) ===
    public static String pauseTitle() {
        return (current == 2) ? "GAME PAUSED" : "JUEGO PAUSADO";
    }

    public static String pauseResume(String keyLabel) {
        return (current == 2) ? keyLabel + ": Resume" : keyLabel + ": Reanudar";
    }

    public static String exitGame() {
        return (current == 2) ? "Exit Game" : "Salir del Juego";
    }

    public static String historyTryAgain() {
        return (current == 2) ? "Nice try, keep improving" : "Buen intento, sigue mejorando";
    }

    public static String logCompletedLevel() {
        return (current == 2) ? "You completed the level" : "Haz completado el nivel";
    }

    public static String logNewBestSteps() {
        return (current == 2) ? "New record for steps" : "Nuevo récord de pasos";
    }

    public static String logNewBestTime() {
        return (current == 2) ? "New record for time" : "Nuevo récord de tiempo";
    }
}
