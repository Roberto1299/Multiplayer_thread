package prueba5;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class HiloServidor implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    //Variable para guardar el jugador
    private int jugador;
    private String palabra;
    ArrayList<Socket> usuarios = new ArrayList<>();
    private static char[] caracter;
    static int errores = 0;

    public HiloServidor(Socket socket, ArrayList<Socket> usuarios, int jugador, String palabra) {
        this.socket = socket;
        this.jugador = jugador;
        this.usuarios = usuarios;
        this.palabra = palabra;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            caracter = new char[palabra.length()];
            for (int i = 0; i < palabra.length(); i++) {
                if (i == 0) {
                    caracter[i] = palabra.charAt(i);
                } else {
                    caracter[i] = '_';
                }
            }
            int turno = 1;

            out.println("Bienvenido al juego eres el jugador " + jugador);
            if (isComplete()) {
                difundirMensaje("Que comience el juego la palabra comienza con " + String.valueOf(caracter));
                difundirMensaje("Es turno del jugador " + turno);
                System.out.println("[SERVIDOR]: El juego ha comenzado");
            }


            while (true) {
                //Se espera la llegada de un mensaje
                String mensaje = in.readLine();
                //Método que sirve para verificar el avance de las jugadas
                verificarJugada(palabra, mensaje, caracter);
                //Verificamos si hay un ganador
                if (hayGanador()) {
                    cerrarJuegoGanado();
                } else {
                    //Cambiamos el turno
                    cambiarTurno(jugador, turno);
                }

            }
        } catch (SocketException e) {
            System.out.println("[SERVIDOR]: Jugador eliminado");
            try {
                this.socket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    private boolean hayGanador() {
        int contador = 0;
        for (int i = 0; i < caracter.length; i++) {
            if (caracter[i] != '_') {
                contador++;
            }
        }

        if (palabra.length() == contador) {
            int correctas = 0;
            for (int i = 0; i < palabra.length(); i++) {
                if (palabra.charAt(i) == caracter[i]) {
                    correctas++;
                }
            }
            if (correctas == palabra.length()) {
                return true;
            }
        }
        return false;
    }

    private void cambiarTurno(int jugador, int turno) throws IOException {
        turno = jugador + 1;
        if (turno <= 4) {
            difundirMensaje("Es turno del jugador " + turno);
        } else {
            turno = 1;
            difundirMensaje("Es turno del jugador " + turno);
        }
        System.out.println("[SERVIDOR]: Turno del jugador "+turno);
    }

    private void verificarJugada(String palabra, String mensaje, char[] caracter) throws IOException {
        if (palabra.contains(mensaje)) {
            for (int i = 0; i < palabra.length(); i++) {
                if (palabra.charAt(i) == mensaje.toCharArray()[0]) {
                    caracter[i] = mensaje.toCharArray()[0];
                    System.out.println("[SERVIDOR]: El jugador ha acertado la palabra");
                }
            }
        } else {
            System.out.println("[SERVIDOR]: El jugador ha fallado la palabra");
            difundirMensaje("Buena suerte cada vez más cerca de ahorcarte");
            errores++;
            dibujarAhorcado(errores);
        }
        difundirMensaje("Avance: " + String.valueOf(caracter));
    }

    private void dibujarAhorcado(int errores) throws IOException {
        switch (errores) {
            case 1:
                difundirMensaje(" ____ ");
                difundirMensaje("|    |");
                difundirMensaje("|     ");
                difundirMensaje("|     ");
                difundirMensaje("|     ");
                difundirMensaje("|     ");
                break;
            case 2:
                difundirMensaje(" ____ ");
                difundirMensaje("|    |");
                difundirMensaje("|    O");
                difundirMensaje("|     ");
                difundirMensaje("|     ");
                difundirMensaje("|     ");
                break;
            case 3:
                difundirMensaje(" ____ ");
                difundirMensaje("|    |");
                difundirMensaje("|    O");
                difundirMensaje("|    |");
                difundirMensaje("|     ");
                difundirMensaje("|     ");
                break;
            case 4:
                difundirMensaje(" ____ ");
                difundirMensaje("|    |");
                difundirMensaje("|    O");
                difundirMensaje("|   /|");
                difundirMensaje("|     ");
                difundirMensaje("|     ");
                break;
            case 5:
                difundirMensaje(" ____ ");
                difundirMensaje("|    |");
                difundirMensaje("|    O");
                difundirMensaje("|   /|\\");
                difundirMensaje("|     ");
                difundirMensaje("|     ");
                break;
            case 6:
                difundirMensaje(" ____ ");
                difundirMensaje("|    |");
                difundirMensaje("|    O");
                difundirMensaje("|   /|\\");
                difundirMensaje("|   / ");
                difundirMensaje("|     ");
                break;
            case 7:
                difundirMensaje(" ____ ");
                difundirMensaje("|    |");
                difundirMensaje("|    O");
                difundirMensaje("|   /|\\");
                difundirMensaje("|   / \\");
                difundirMensaje("|     ");
                cerrarJuego();
                break;
        }
    }

    private void cerrarJuego() throws IOException {
        System.out.println("[SERVIDOR]: HAN PERDIDO EL JUEGO SUERTE A LA PRÓXIMA");
        difundirMensaje("HAN PERDIDO EL JUEGO, SUERTE A LA PRÓXIMA!!!");
        for (Socket usuario : usuarios) {
            usuario.close();
        }
    }

    private void cerrarJuegoGanado() throws IOException {
        System.out.println("[SERVIDOR]: TODOS HAN GANADO");
        difundirMensaje("FELICIDADES A TODOS HAN GANADO!!!");
        for (Socket usuario : usuarios) {
            usuario.close();
        }
    }

    private void difundirMensaje(String mensaje) throws IOException {
        for (Socket usuario : usuarios) {
            out = new PrintWriter(usuario.getOutputStream(), true);
            out.println(mensaje);
        }
    }

    public boolean isComplete() {
        if (usuarios.size() == 4) {
            return true;
        }
        return false;
    }
}
