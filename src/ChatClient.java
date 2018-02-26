import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class ChatClient implements Runnable{

	private Socket socket = null;
	protected BufferedReader console = null;
	private DataOutputStream strOut = null;
	private DataInputStream strIn = null;
	private Thread thread = null;
	ChatClientThread client = null;

	String line = "";
	
	public ChatClient(String serverName, int serverPort){
		try {
			System.out.println("Will try to connect to "+serverName+" at "+serverPort);
			socket = new Socket(serverName, serverPort);
			System.out.println("WE CONNECTED TO SERVER:"+serverName);
			start();
		} catch (UnknownHostException e) {
			System.out.println("Unknown host "+e.getMessage());
		} catch (IOException e) {
			System.err.println("IO Exception "+e.getMessage());
		}
	}
	
	
	@Override
	public void run() {
		while((thread!=null) &&(!line.equalsIgnoreCase("BYE") )){//until user says bye
			try {
				line = console.readLine();//read line from them
				strOut.writeUTF(line);//send it to the server
				strOut.flush();
			} catch (IOException e) {
				System.err.println("IO Exception inside ChatClient run method "+e.getMessage());
				
			}
			
		}
	}

	public void start() throws IOException{
		console = new BufferedReader(new InputStreamReader(System.in));
		strOut = new DataOutputStream(socket.getOutputStream());
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void handle(String msg){

		if( line.equalsIgnoreCase("bye") ) {
			stop();
		}
		else {
			System.out.println( msg );
		}

		//If the client sends bye then we quit else we send a message
	}
	public void stop() {
		{
			if (thread != null) {
				thread.stop();
				thread = null;
			}
			try {
				if (console != null) console.close();
				if (strOut != null) strOut.close();
				if (socket != null) socket.close();
			} catch (IOException ioe) {
				System.out.println("Error closing ...");
			}
			client.close();
			client.stop();
		}
	}




	public static void main(String[] args){
		ChatClient client = null;
		if(args.length !=2){
			System.out.println("To chat you must specify both a host and a port ");
		}
		else{
			client = new ChatClient(args[0],Integer.parseInt(args[1]));
		}
	}
	
}
