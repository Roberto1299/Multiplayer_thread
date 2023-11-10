package prueba5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;

public class Servidor {
    //Inicializamos el puerto
    private final int puerto = 6789;
    private ArrayList<Socket> usuarios = new ArrayList<>();
    private int jugador=1;
    private String diccionario[]={"arbol","perro","gato","oruga","ambulancia","caracol","pescado","iphone","perico","canario","remolino"};


    public void escuchar(){
        try {
            ServerSocket servidor = new ServerSocket(puerto);
            System.out.println("Servidor en ejecucion");
            int numero = (int)(Math.random()*diccionario.length);
            String palabra = diccionario[numero];

            while(true){
                Socket cliente = servidor.accept();
                System.out.println("El jugador "+jugador+" ha entrado");
                usuarios.add(cliente);
                Runnable run = new HiloServidor(cliente,usuarios,jugador,palabra);
                jugador++;
                Thread hilo = new Thread(run);
                hilo.start();

            }
        }catch (SocketException e){
            System.out.print(e);
        }catch (IOException e){
            System.out.println(e);
        }
    }


    public static void main(String[] args) {
        Servidor servidor= new Servidor();
        servidor.escuchar();
    }
}
