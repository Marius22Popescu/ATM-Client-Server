import java.io.*;
import java.net.*;

class ATMServer 
{
    // this is the port the ATM server listens on
    private static final int PORT_NUMBER = 8080;

    // main entry point for the application
    public static void main(String args[]) 
    {
        try 
        {
            // create account db
            AccountDB db = new AccountDB();

            // open socket
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);

            // start listener thread
            Thread listener = new Thread(new SocketListener(serverSocket, db));
            listener.start();

            // message explaining how to connect
            System.out.println("To connect to this server to test, try \"telnet 127.0.0.1 8080\"");

            // wait until finished
            System.out.println("Press enter to quit...");
            Console cons = System.console(); 
            String enterString = cons.readLine();

            // kill listener thread
            listener.interrupt();

            // close the socket
            serverSocket.close();
        } 
        catch (Exception e) 
        {
            System.err.println("ATMServer::main: " + e.toString());
            e.printStackTrace();
        }
    }
}

class SocketListener implements Runnable 
{
    private ServerSocket serverSocket;

    private AccountDB db;

    public SocketListener(ServerSocket serverSocket, AccountDB db)   
    {
        this.serverSocket = serverSocket;
        this.db = db;
    }

    // this thread listens for connections, launches a separate socket connection
    //  thread to interact with them
    public void run() 
    {
        while(!this.serverSocket.isClosed())
        {
            try
            {
                Socket clientSocket = serverSocket.accept();
                Thread connection = new Thread(new SocketConnection(clientSocket, db));
                connection.start();
                Thread.yield();
            }
            catch(IOException e)
            {
                if (!this.serverSocket.isClosed())
                {
                    System.err.println("SocketListener::run: " + e.toString());
                }
            }
        }
    }
}

class SocketConnection implements Runnable 
{
    private Socket clientSocket;
    
    private AccountDB db;

    public SocketConnection(Socket clientSocket, AccountDB db)   
    {
        this.clientSocket = clientSocket;
        this.db = db;
    }

    // one of these threads is spawned and used to talk to each connection
    public void run() 
    {       
        try
        {
            BufferedReader request = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            PrintWriter response = new PrintWriter(this.clientSocket.getOutputStream(), true);
            this.handleConnection(request, response);
        }
        catch(IOException e)
        {
            System.err.println("SocketConnection::run: " + e.toString());
        }
    }
    
    private void handleConnection(BufferedReader request, PrintWriter response)
    {
        try
        {
            /*
             *  accessing the account database by:
             *  this.db.{method name you want to call}   
             */
            String message = this.readMessageFromConnection(request);  //create the message
            System.out.println("Message for client: " + message);
            
            if (message != null) {
            		String[] words = message.split(";");  //create array of strings in order to receive the message from ATM client
            		if (message.startsWith("login")) {    //	login;will;1234
            			System.out.println("logging in...");
            			String username = words[1];
            			Integer pin = Integer.valueOf(words[2]);
            			String token = this.db.Login(username, pin);  //generate token from DB
            			response.print("token=" + token);       //send token to user
            		} else if (message.startsWith("logout")) {	//	logout;token
            			System.out.println("logging out...");
            			String token = words[1];         //get the token from the array string
            			this.db.Logout(token);
            			response.print("token=" + token);
            		} else if (message.startsWith("deposit")) {	//	deposit;token;amount
            			System.out.println("deposit...");
            			String token = words[1];
            			Double amount = Double.valueOf(words[2]);
            			this.db.Deposit(token, amount);            			
            		} else if (message.startsWith("withdraw")) {  //	withdraw;token;amount
            			System.out.println("withdraw...");
            			String token = words[1];
            			Double amount = Double.valueOf(words[2]);
            			this.db.Withdraw(token, amount);            			
            		} else if (message.startsWith("balance")) {    // balance;token
            			String token = words[1];
            			this.db.Balance(token);
            			System.out.println("balance... ");
            			
            		}
            }
            
            // close the socket
            this.clientSocket.close();
        }
        catch(IOException e)
        {
            System.err.println("SocketConnection::handleConnection: " + e.toString());
        }
    }

    private String readMessageFromConnection(BufferedReader reader)
    {
        // read and print the reply
        String message = "";
        try 
        {
            message = reader.readLine();
        }
        catch (IOException e) 
        {
            System.err.println("SocketConnection::readMessageFromConnection: " + e.toString());
        } 
        return message;
    }
}
