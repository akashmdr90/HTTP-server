/**
	   * To implement a simplified Anonymous E-Mail Sender (AMS) Server
	   *  server gets a “GET / HTTP/1.1” request. 
	   *  Create a HTTP response message in which you will 
	   *  send a simple ams.html file to the client.
	   *  The user clicks Submit. Then your server will get a “POST / HTTP/1.1” 
	   *  request which contains basic e-mail information.
	   *  send an e-mail using basic SMTP protocol.
	   **/ 

import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.smtp.SMTPTransport;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

public class WebServer {

  protected void start() {
	   /**
	   * This method is to handle the GET and POST request
	   * for pushing mails from the POST request .
	   * Here we are using Gmail server to send mail.
	   * @from The from address is being stored
	   * @to The To address is being stored here
	   * @subject The subject line of the mail is being stored
	   * @message The message body is being stored 
	   * @serverSocket The server socket number is stored here
	   * 
	   */
	  
    ServerSocket serverSocket;
    System.out.println("The server is started and waiting for connection in 8080");
    try {
      // create the main server socket in port 8080
      serverSocket = new ServerSocket(8080);
    } catch (Exception e) {
      System.out.println("Error: " + e); // thows error when there is issues in port creation
      return;
    }

    System.out.println("Waiting for connection"); // Waiting for connection from browser
    // Wait until the connection is established
    for (;;) {
           try {
    	    Socket socket = serverSocket.accept();
    	    BufferedReader rBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	    BufferedWriter oBuffer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    	    // read request for the server into the buffer 
    	    String sLine;
    	    sLine = rBuffer.readLine();
    	    StringBuilder raw = new StringBuilder();
    	    raw.append("" + sLine);
    	    boolean isPost = sLine.startsWith("POST");
    	    int contentLength = 0;
    	    while (!(sLine = rBuffer.readLine()).equals("")) {
    	        raw.append('\n' + sLine);
    	       // Check if the request is POST
    	        if (isPost) {
    	            final String contentHeader = "Content-Length: ";
    	            if (sLine.startsWith(contentHeader)) 
    	            {
    	                contentLength = Integer.parseInt(sLine.substring(contentHeader.length()));
    	            }
    	        }
    	    }
    	    // Creating the POST URL so that to extract the email details to push mail using SMTP
    	    StringBuilder pURL = new StringBuilder();
    	    if (isPost) {
    	        int c = 0;
    	        for (int i = 0; i < contentLength; i++) {
    	            c = rBuffer.read();
    	            pURL.append((char) c);
    	         }
    	    }
    	    raw.append(pURL.toString());
    	    
    	    // Set the response for the request received
    	    oBuffer.write("HTTP/1.1 200 OK\r\n");
    	    oBuffer.write("Content-Type: text/html\r\n");
    	    oBuffer.write("\r\n");
    	  if (isPost) // Splitting and decoding the POST URL rBuffer the encoded form
    	  {
    	        String[] splitURL = pURL.toString().split("&");
    	        String[] splitFrm = splitURL[0].split("=");
    	        String from = java.net.URLDecoder.decode(splitFrm[1], "UTF-8");
    	        String[] splitTo = splitURL[1].split("=");
    	        String To = java.net.URLDecoder.decode(splitTo[1], "UTF-8");
    	        String[] splitSub = splitURL[2].split("=");
    	        String subject = java.net.URLDecoder.decode(splitSub[1], "UTF-8");
    	        String[] splitMessage = splitURL[3].split("=");
    	        String message = java.net.URLDecoder.decode(splitMessage[1], "UTF-8");
    	        String emailStatus =SmtpInterface(from, To, subject, message);
    	        oBuffer.write("<head><title> E-Mail Sent Sucessfully !!! </title></head>");
    	        oBuffer.write("<h1>Email Sent !!!!</h1>");
    	        oBuffer.write("<h2>Status</h2>");
    	        oBuffer.write(emailStatus);
    	        System.out.println(emailStatus);	
    	        System.out.println("Email Sent on POST request");		    // Print post request success	        
    	    } else // If its a get request ELSE part is fired and HTML asm.html contents are loaded
    	    
    	    {   System.out.println(" Get Request End ........");
    	    	oBuffer.write("<head><title> Simple Anonymous E-Mail Sender -- Web Interface</title></head>");
    	        oBuffer.write("<body bgcolor='#b5ceff'><h1>Anonymous E-Mail Sender (AMS) </h1>");
    	        oBuffer.write("<p> <form action='http://192.168.56.1:8080' method=post>"); // IP of the server system is being entered here
    	        oBuffer.write("From:    <input name='from' value='optional' size=30> <br>");
    	        oBuffer.write("To:      <input name='to'   value='' size=30> <br>");
    	        oBuffer.write("Subject: <input name='subject' value='AMS:' size=60> <br>");
    	        oBuffer.write("<br>E-mail message: <br>");
    	        oBuffer.write("<TEXTAREA NAME='msg' value='' COLS=40 ROWS=10></TEXTAREA>"); 
    	        oBuffer.write("<br><input type=submit name='Action' value='Submit'></input>");
    	        oBuffer.write("</form></html>");
    	        System.out.println("GEt request Completed .....");  // Print the Get Request 
    	    }
    	    // Flush and close the output buffer and connection 
    	    oBuffer.flush();
    	    oBuffer.close();
    	    socket.close();
    	    System.out.println("Connection Closed ......");
    	    //
    	} catch (Exception e) {  // Error thrown is handled here
    	    e.printStackTrace();
    	    StringWriter sw = new StringWriter();
    	    e.printStackTrace(new PrintWriter(sw));
     	}
    }
  }

  
  public String SmtpInterface(String from, String To, String subject, String message) throws AddressException, MessagingException
  {
	  
	   /**
	   * This method is used for creating an SMTP connection
	   * for pushing mails from the POST request .
	   * Here we are using Gmail server to send mail.
	   * @from The from address is being stored
	   * @to The To address is being stored here
	   * @subject The subject line of the mail is being stored
	   * @message The message body is being stored 
	   * @return string This returns message of success.
	   */
	  System.out.println("SMTP server started ......");
	  Properties props = System.getProperties();
      props.put("mail.smtps.host","smtp.gmail.com"); // Gmail server
      props.put("mail.smtps.auth","true"); // authentication True
      Session session = Session.getInstance(props, null);
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(from)); // From address
      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(To, false));  // To address is eneterd here
      msg.setSubject(subject); // Subject line is entered
      msg.setText(message);   // Message body is being entered here
      SMTPTransport t = (SMTPTransport)session.getTransport("smtps");
      t.connect("smtp.gmail.com", "network123@gmail.com", "pass"); // User name and Password of the gmail account from which its sent
      t.sendMessage(msg, msg.getAllRecipients());
      System.out.println("SMTP server end ......");
      t.close();
      return t.getLastServerResponse();
	  	  
  }
  
  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer serverObj = new WebServer();
    serverObj.start();
  }
}

