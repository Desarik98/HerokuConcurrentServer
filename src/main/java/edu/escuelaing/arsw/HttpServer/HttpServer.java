package edu.escuelaing.arsw.HttpServer;

import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HttpServer implements Runnable {
    private static final HttpServer instance = new HttpServer();
    protected Thread runningThread = null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(40);
    protected boolean isRunning = true;
    protected ServerSocket serverSocket = null;
    private HttpServer(){}
    private static HttpServer getInstance(){
        return instance;
    }
    public static void main(String[] args) throws IOException {
        new Thread(HttpServer.getInstance()).start();
        try {
            Thread.sleep(200000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        HttpServer.getInstance().stop();
    }

    public void startServer() throws IOException {
        int port = getPort();
        try {
            serverSocket = new ServerSocket(port);
            Socket clientSocket;
            while (isRunning){
                System.out.println("Listo para recibir en puerto "+port);
                clientSocket = serverSocket.accept();
                ClientRequest clientRequest = new ClientRequest(clientSocket);
                threadPool.execute(clientRequest);
            }
            this.threadPool.shutdown();
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+port);
            System.exit(1);
        }
    }

    public synchronized void stop(){
        this.isRunning = false;
        try{
            this.serverSocket.close();
        }catch (IOException e){
            throw new RuntimeException("Error closing server",e);
        }
    }

    @Override
    public void run() {
        try {
            synchronized (this){
                this.runningThread = Thread.currentThread();
            }
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int getPort(){
        if(System.getenv("PORT") != null){
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 36000;
    }
}
