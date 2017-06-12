import java.io.*;
import javax.net.*;
import javax.net.ssl.*;
import java.security.*;

class ClientHandler implements Runnable {

  SSLSocket socket;
  ClientHandler (SSLSocket socket) {
    this.socket = socket;
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  public void run() {

    System.out.println("Client be served:");
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream());
      String inputLine, outputLine;
      while ((inputLine = in.readLine()) != null) {
        outputLine = inputLine;
        out.println(outputLine);
        out.flush();
      }
      System.out.println("Client disconnected");
      out.close();
      in.close();
      socket.close();
    } catch(Exception exception) {
      exception.printStackTrace();
      System.exit(1);
    }
  }
}

public class SSLserver {
  public static void main(String[] args) {

    if (args.length != 1) {
        System.out.println("Usage: "+SSLserver.class.getName()+" <port>");
        System.exit(1);
    }

    try {
      SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
      SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(Integer.parseInt(args[1]));

      String [] protocols = new String [] { "TLSv1.2" };

      //sslserversocket.setEnabledCipherSuites(ciphers);
      sslserversocket.setEnabledProtocols(protocols);

      for(;;){
        SSLSocket client = (SSLSocket) sslserversocket.accept();
        new ClientHandler(client);
      }
    } catch(Exception e) {
      exception.printStackTrace();
      System.exit(1);
    }
  }
}
