import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ChatServer implements Runnable {
	
	private int clientCount =0;
	private ChatServerThread clients[] = new ChatServerThread[50];
	private ServerSocket server = null;
	Thread thread = null;
	boolean done;

	//same as version3
	public ChatServer(int port){
		try{
			server = new ServerSocket(port);
			System.out.println("Started the server...waiting for a client");
			start(); //the chatserver's start method that goes ahead and creates a new thread
		}
		catch(IOException e){
			System.err.println("ERROR "+e.getMessage());
			
		}
	}
	
	public void start(){
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {//same as version 3
		while(thread !=null){
			try{
				System.out.println("Waiting for a client...");
				//now we add a new Thread and accept a client
				addThread(server.accept());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void addThread(Socket socket){
		if(clientCount < clients.length){
		   clients[clientCount] = new ChatServerThread(this, socket);
			try {
				 clients[clientCount].open();//open the stream for the ChatServerThread client
				 clients[clientCount].start();//start to run the ChatServerThread client
				 clientCount++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	public synchronized void handle(int ID, String input) {

		String privMsg = "private";
		String encMsg = "encrypt";
		done = false;


		if (input.startsWith(encMsg)) {
			int ID_SendTo = Integer.parseInt(input.substring(privMsg.length(),
					privMsg.length() + 5));
			String msg = input.substring(privMsg.length() + 6);
			System.out.print(msg);


			OneTimePad otp = new OneTimePad(msg);


			if (findClient(ID_SendTo) != -1) {
				clients[findClient(ID_SendTo)].send("User " + ID + " Said " +
						": " + msg);

			} else {
				clients[findClient(ID)].send("User: " + ID_SendTo + "Was not found.");
			}
		}

		if (input.startsWith(privMsg)) {
			int ID_SendTo = Integer.parseInt(input.substring(privMsg.length(),
					privMsg.length() + 5));
			String msg = input.substring(privMsg.length() + 6);
			;

			if (findClient(ID_SendTo) != -1) {
				clients[findClient(ID_SendTo)].send("User " + ID + " Said " +
						": " + msg);
			} else {
				clients[findClient(ID)].send("User: " + ID_SendTo + "Was not found.");
			}
		} else {
			System.out.println("Message from" + ID + ":" + input);
			for (int i = 0; i < clientCount; i++) {
				clients[i].send("User: " + ID + " said: " + input);
			}
		}
		if (input.equalsIgnoreCase("bye")) {
			remove(ID);
		}
//		if (input.startsWith(en)) {
//			OneTimePad op = new OneTimePad(input);
//			op.encrypt(input);
//
//			int ID_SendTo = Integer.parseInt(input.substring(en.length(),
//					en.length() + 5));
//			String msg = input.substring(en.length() + 6);
//
//
//			if (findClient(ID_SendTo) != -1) {
//				clients[findClient(ID_SendTo)].send("User " + ID + " Said " +
//						": " + msg);
//			}
//		}
	}
	public synchronized void remove(int ID){
		int position = findClient(ID);
		if(position >=0){
			ChatServerThread toRemove = clients[position];
			if(position <clientCount-1){
				for(int i= position+1; i <clientCount; i++){
					clients[i-1] = clients[i];
				}
				clientCount--;
			}
			try {
				toRemove.close();//close the person's that said bye connection
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private int findClient(int ID){
		for(int i=0; i<clientCount; i++){
			if(clients[i].getID() == ID){
				return i;
			}
		}
		return -1;//not in the array
	}
	
	public static void main(String [] args){
		ChatServer myServer = null;
		if(args.length !=1){
			System.out.println("You need to specify a port number!!!");
		}
		else{
			int portNum = Integer.parseInt(args[0]);
			myServer = new ChatServer(portNum);//create an instance of my ChatServer
		}
	}
	
	

}
