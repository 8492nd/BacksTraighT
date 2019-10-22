package WebServer;

import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer
{
  public static void main(String argv[]) throws Exception
  {
    int port = 2021;

    // Establish the listen socket.
    ServerSocket listenSocket = new ServerSocket(port);
    
    while (true) {
      // Listen for a TCP connection request.
      Socket connectionSocket = listenSocket.accept();
      
      // Construct an object to process the HTTP request message
      HttpRequest request = new HttpRequest(connectionSocket);
      
      // Create a new thread to process the request.
      Thread thread = new Thread(request);
      
      // Start the thread.
      thread.start();
    }
  }
}

