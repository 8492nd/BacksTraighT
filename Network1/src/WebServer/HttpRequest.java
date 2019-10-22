package WebServer;

import java.io.*;
import java.net.Socket;
import java.util.*;

final class HttpRequest implements Runnable
{
	// Set up Carriage Return and Line Feed variable
	final static String CRLF = "\r\n";
    		Socket socket;

	public HttpRequest(Socket socket) throws Exception
	{
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface
	public void run()
	{
		try
		{
			processRequest();
		}
		catch (Exception e)
		{
			System.out.println(e);
    	}
	}

	private void processRequest() throws Exception
	{
		// Get references to sockets input & output streams
		InputStream is = this.socket.getInputStream();
		DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());

		// Set up input stream filter
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		// Get the request line of HTTP message
		String requestLine = br.readLine();

		// Extract request, filename, and httpVersion from the request line
		if(requestLine != null) {
			StringTokenizer tokens = new StringTokenizer(requestLine);
			System.out.println("request line: " + requestLine.toString());
			tokens.nextToken();
			
			
			String fileName = tokens.nextToken();
			fileName = "." + fileName;
			String httpVersion = tokens.nextToken();
	
			// Open the requested file
			FileInputStream fist = null;
			boolean fileExists = true;
			try
			{
				fist = new FileInputStream(fileName);
			}
			catch (FileNotFoundException e)
			{
				System.out.println("fuck\n");
				fileExists = false;
			}
	
			// Construct the response message
			String statusLine = null;
			String contentTypeLine = null;
			String entityBody = null;
	
			// Check request for error
			if (!httpVersion.equals("HTTP/1.1"))
			{
				statusLine = "HTTP/1.1 400 Bad Request" + CRLF;
			}
			else if (fileExists)
			{
				statusLine = "HTTP/1.1 200 OK" + CRLF;
				contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
			}
			else
			{
				statusLine = "HTTP/1.1 404 Not Found" + CRLF;
				contentTypeLine = "NONE";
				entityBody = "<HTML>" + "<HEAD><TITLE>NOT FOUND</TITLE></HEAD>" + "<BODY>NOT FOUND</BODY></HTML>";
			}
			// Send the status line
			os.writeBytes(statusLine);
	
			// Send the content type line
			os.writeBytes(contentTypeLine);
	
			// Send a blank line to indicate the end of the header lines
			os.writeBytes(CRLF);
	
			// Send the entity body
			if (fileExists)
			{
				sendBytes(fist, os);
			}
			else
			{
				os.writeBytes(entityBody);
			}
			if(fist != null) {
				fist.close();
			}
		}

		// Close the streams
		os.close();
		br.close();
		socket.close();
	}

	private String contentType(String fileName)
	{
		// Check requested file type
		if(fileName.endsWith(".htm") || fileName.endsWith(".html"))
			return "text/html";
		else if(fileName.endsWith(".gif"))
			return "image/gif";
		else if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg"))
			return "image/jpg";
		else if(fileName.endsWith(".txt"))
			return "text/txt";
		else
			return "application/octet-stream";
	}

	private static void sendBytes(FileInputStream fist, OutputStream os) throws Exception
	{
		// Construct a 1K buffer
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Copy requested file into the socket's output stream
		while((bytes = fist.read(buffer)) != -1 )
			os.write(buffer, 0, bytes);
	}
}