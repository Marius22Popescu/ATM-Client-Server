import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ATMClient {
	public static void main(String[] args) {
		//create the ATM client socket
		Socket ATMClientSk = null;
		PrintWriter os = null;
		BufferedReader is = null;
		double amount = 0;
		try {
			//initialize and open the socket to port 8080
			ATMClientSk = new Socket("localhost", 8080);
			os = new PrintWriter(ATMClientSk.getOutputStream(), true);
            is = new BufferedReader(new InputStreamReader(ATMClientSk.getInputStream()));

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: hostname");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: hostname");
		}

		if (ATMClientSk != null && os != null && is != null) {
			try {
				//Welcome and get info from user
				Scanner console = new Scanner(System.in);
				System.out.println("Welcome to ATM!");
				System.out.println("Please enter your user name: ");
				String username = console.nextLine();
				System.out.println("Please enter your pin number: ");
				String pinNr = console.nextLine();
				//Send request to the server
				os.println("login;"+ username + ";" + pinNr);
				String responseLine = is.readLine();
				//System.out.println("Server response: " + responseLine);
				String token = responseLine.split("=")[1];  //get the token
				//Display drop down menu
				System.out.println("Please select the service rquired:\nDeposit press 1\nWithdraw press 2\nBalance press 3\nLogout press 4");
				int answer = console.nextInt();
				do { 
					//deposit case
					if (answer == 1) {  
				System.out.println("Please insert the amounth you want to deposit");
				amount = console.nextDouble();
				ATMClientSk = new Socket("localhost", 8080);  //initialize and open the socket to port 8080
				os = new PrintWriter(ATMClientSk.getOutputStream(), true);
			    is = new BufferedReader(new InputStreamReader(ATMClientSk.getInputStream()));				      
				os.println("deposit;" + token + ";" + amount);
				responseLine = is.readLine();
				System.out.println("Server response: " + responseLine);
					}  //withdraw case
					else if (answer == 2) {
				System.out.println("Please insert the amounth you want to withdraw");
				amount = console.nextDouble();
				ATMClientSk = new Socket("localhost", 8080);  //initialize and open the socket to port 8080
				os = new PrintWriter(ATMClientSk.getOutputStream(), true);
			    is = new BufferedReader(new InputStreamReader(ATMClientSk.getInputStream()));
		        os.println("withdraw;" + token + ";" + amount);
				responseLine = is.readLine();
				if (responseLine != null) 
					System.out.println("Server response: " + responseLine);
				else
					System.out.println("Server response: You do not have enough money in your account!");
					}  //balance case
					else if (answer == 3) {  
				ATMClientSk = new Socket("localhost", 8080);  //initialize and open the socket to port 8080
				os = new PrintWriter(ATMClientSk.getOutputStream(), true);
			    is = new BufferedReader(new InputStreamReader(ATMClientSk.getInputStream()));
				os.println("balance;" + token);
				responseLine = is.readLine();
				System.out.println("Server response: " + responseLine);
					}
				System.out.println("Please select the service rquired:\nDeposit press 1\nWithdraw press 2\nBalance press 3\nLogout press 4");
				answer = console.nextInt();
				}while(answer != 4);
				//logout case
				ATMClientSk = new Socket("localhost", 8080);  //initialize and open the socket to port 8080
				os = new PrintWriter(ATMClientSk.getOutputStream(), true);
	            is = new BufferedReader(new InputStreamReader(ATMClientSk.getInputStream()));
				os.println("logout;" + token);
				responseLine = is.readLine();
				System.out.println("Server response: Thank you for using ATM services! Good bye! ");
				// close the socket
				os.close();
				is.close();
				ATMClientSk.close();
			} catch (UnknownHostException e) {
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}
}
