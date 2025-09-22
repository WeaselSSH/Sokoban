package com.elkinedwin.LogicaUsuario;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class LeerArchivo {

    public static void cargarUsuario() throws IOException {
        if (ManejoUsuarios.UsuarioActivo == null) return;
        leerDatos();
        leerProgreso();
        leerConfig();
        leerPartidas();
        cargarRivales();
    }

    private static void leerDatos() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoDatos;
        if (f == null) return;

        f.seek(0);
        long fechaRegistro = f.readLong();
        long ultimaSesion  = f.readLong();

        f.seek(16);
        String nombre = safeReadUTF(f);

        String packed = safeReadUTF(f);
        String usuario = "";
        String pass = "";
        String img = "";
        if (packed != null) {
            String[] parts = packed.split(",", -1);
            if (parts.length > 0) usuario = parts[0];
            if (parts.length > 1) pass    = parts[1];
            if (parts.length > 2) img     = parts[2];
        }

        Usuario u = ManejoUsuarios.UsuarioActivo;
        u.setFechaRegistro(fechaRegistro);
        u.setUltimaSesion(ultimaSesion);
        u.setNombre(nombre == null ? "" : nombre);
        u.setUsuario(usuario == null ? "" : usuario);
        u.setContrasena(pass == null ? "" : pass);
        u.avatar = (img == null ? "" : img);
    }

    private static void leerProgreso() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        if (f == null) return;

        Usuario u = ManejoUsuarios.UsuarioActivo;

        f.seek(0);
        u.setTutocomplete(f.readBoolean());

        f.seek(1);
        for (int i = 1; i <= 7; i++) {
            u.setNivelCompletado(i, f.readBoolean());
        }

        f.seek(8);
        for (int i = 1; i <= 7; i++) {
            u.setMayorPuntuacion(i, f.readInt());
        }

        f.seek(36);
        u.setTiempoJugadoTotal(f.readInt());

        f.seek(40);
        u.setPuntuacionGeneral(f.readInt());

        f.seek(44);
        u.setPartidasTotales(f.readInt());

        f.seek(48);
        for (int i = 1; i <= 7; i++) {
            u.setPartidasPorNivel(i, f.readInt());
        }

        f.seek(102);
        for (int i = 1; i <= 7; i++) {
            u.setTiempoPorNivel(i, f.readInt());
        }

        f.seek(130);
        for (int i = 1; i <= 7; i++) {
            u.setMejorTiempoPorNivel(i, f.readInt());
        }

        f.seek(158);
        for (int i = 1; i <= 7; i++) {
            u.setEmpujesNivel(i, f.readInt());
        }
    }

    private static void leerConfig() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoConfig;
        if (f == null) return;

        f.seek(0);
        int vol       = f.readInt();
        int arriba    = f.readInt();
        int abajo     = f.readInt();
        int der       = f.readInt();
        int izq       = f.readInt();
        int reiniciar = f.readInt();
        int idioma    = f.readInt();

        Usuario u = ManejoUsuarios.UsuarioActivo;
        u.setConfiguracion("Volumen", vol);
        u.setConfiguracion("MoverArriba", arriba);
        u.setConfiguracion("MoverAbajo", abajo);
        u.setConfiguracion("MoverDer", der);
        u.setConfiguracion("MoverIzq", izq);
        u.setConfiguracion("Reiniciar", reiniciar);
        u.setConfiguracion("Idioma", idioma);
    }

    private static void leerPartidas() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoPartidas;
        if (f == null) return;

        Usuario u = ManejoUsuarios.UsuarioActivo;
        if (u.historial != null) {
            u.historial.clear();
        }

        if (f.length() == 0) return;

        f.seek(0);
        int count = f.readInt();
        for (int i = 0; i < count; i++) {
            String fecha  = safeReadUTF(f);
            int intentos  = f.readInt();
            String logros = safeReadUTF(f);
            int tiempo    = f.readInt();
            int nivel     = f.readInt();

            Partida p = new Partida(fecha, intentos, logros, tiempo, nivel);
            u.historial.add(p);
        }
    }

    private static String safeReadUTF(RandomAccessFile f) {
        try { return f.readUTF(); } catch (Exception e) { return ""; }
    }

    private static void cargarRivales() {
        try {
            if (ManejoArchivos.carpetaUsuarios == null) return;
            File base = ManejoArchivos.carpetaUsuarios;
            if (!base.exists() || !base.isDirectory()) return;

            Usuario activo = ManejoUsuarios.UsuarioActivo;
            ArrayList<Usuario> lista = new ArrayList<>();

            File[] sub = base.listFiles(File::isDirectory);
            if (sub == null) sub = new File[0];

            for (File dir : sub) {
                try {
                    String userFolderName = dir.getName();
                    Usuario rival = leerUsuarioDesdeCarpetaSoloLectura(dir, userFolderName);
                    if (rival != null) lista.add(rival);
                } catch (Exception ignored) {}
            }

            boolean reemplazado = false;
            for (int i = 0; i < lista.size(); i++) {
                Usuario u = lista.get(i);
                if (u != null && activo != null &&
                    u.getUsuario() != null && u.getUsuario().equalsIgnoreCase(activo.getUsuario())) {
                    lista.set(i, activo);
                    reemplazado = true;
                    break;
                }
            }
            if (!reemplazado && activo != null) {
                lista.add(activo);
            }

            if (activo != null) activo.setRivales(lista);
        } catch (Exception ignored) {}
    }

    private static Usuario leerUsuarioDesdeCarpetaSoloLectura(File carpetaUsuario, String userFolderName) {
        RandomAccessFile fDatos = null, fProg = null, fCfg = null;
        try {
            File datos     = new File(carpetaUsuario, "Datos.bin");
            File progreso  = new File(carpetaUsuario, "Progreso.bin");
            File config    = new File(carpetaUsuario, "Config.bin");

            if (!datos.exists() || !progreso.exists()) return null;

            fDatos = new RandomAccessFile(datos, "r");
            fProg  = new RandomAccessFile(progreso, "r");
            if (config.exists())   fCfg  = new RandomAccessFile(config, "r");

            Usuario rival = new Usuario(userFolderName, userFolderName, "", 0L);

            fDatos.seek(0);
            long fechaRegistro = fDatos.readLong();
            long ultimaSesion  = fDatos.readLong();

            fDatos.seek(16);
            String nombre = safeReadUTF(fDatos);

            String packed = safeReadUTF(fDatos);
            String usuario = "";
            String pass = "";
            String img = "";
            if (packed != null) {
                String[] parts = packed.split(",", -1);
                if (parts.length > 0) usuario = parts[0];
                if (parts.length > 1) pass    = parts[1];
                if (parts.length > 2) img     = parts[2];
            }

            rival.setFechaRegistro(fechaRegistro);
            rival.setUltimaSesion(ultimaSesion);
            rival.setNombre(nombre == null ? "" : nombre);
            rival.setUsuario(usuario == null ? userFolderName : usuario);
            rival.setContrasena(pass == null ? "" : pass);
            rival.avatar = (img == null ? "" : img);

            fProg.seek(0);
            rival.setTutocomplete(fProg.readBoolean());

            fProg.seek(1);
            for (int i = 1; i <= 7; i++) rival.setNivelCompletado(i, fProg.readBoolean());

            fProg.seek(8);
            for (int i = 1; i <= 7; i++) rival.setMayorPuntuacion(i, fProg.readInt());

            fProg.seek(36);
            rival.setTiempoJugadoTotal(fProg.readInt());

            fProg.seek(40);
            rival.setPuntuacionGeneral(fProg.readInt());

            fProg.seek(44);
            rival.setPartidasTotales(fProg.readInt());

            fProg.seek(48);
            for (int i = 1; i <= 7; i++) rival.setPartidasPorNivel(i, fProg.readInt());

            fProg.seek(102);
            for (int i = 1; i <= 7; i++) rival.setTiempoPorNivel(i, fProg.readInt());

            fProg.seek(130);
            for (int i = 1; i <= 7; i++) rival.setMejorTiempoPorNivel(i, fProg.readInt());

            fProg.seek(158);
            for (int i = 1; i <= 7; i++) rival.setEmpujesNivel(i, fProg.readInt());

            if (fCfg != null) {
                fCfg.seek(0);
                int vol       = fCfg.readInt();
                int arriba    = fCfg.readInt();
                int abajo     = fCfg.readInt();
                int der       = fCfg.readInt();
                int izq       = fCfg.readInt();
                int reiniciar = fCfg.readInt();
                int idioma    = fCfg.readInt();

                rival.setConfiguracion("Volumen", vol);
                rival.setConfiguracion("MoverArriba", arriba);
                rival.setConfiguracion("MoverAbajo", abajo);
                rival.setConfiguracion("MoverDer", der);
                rival.setConfiguracion("MoverIzq", izq);
                rival.setConfiguracion("Reiniciar", reiniciar);
                rival.setConfiguracion("Idioma", idioma);
            }

            return rival;
        } catch (Exception ignored) {
            return null;
        } finally {
            try { if (fDatos != null) fDatos.close(); } catch (Exception ignored) {}
            try { if (fProg  != null) fProg.close(); }  catch (Exception ignored) {}
            try { if (fCfg   != null) fCfg.close(); }   catch (Exception ignored) {}
        }
    }
}
