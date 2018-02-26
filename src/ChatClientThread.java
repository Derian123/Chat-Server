import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientThread extends Thread{
	private Socket socket = null;
	private ChatClient client = null;
	DataInputStream strIn = null;
	private boolean done = true;
	
	public ChatClientThread(ChatClient theClient, Socket theSocket){
		client = theClient;
		socket = theSocket;
		open();
		start();
	}
	
	public void open(){
		try{
		strIn = new DataInputStream(socket.getInputStream()); //new BufferedInputStream
		}catch(IOException e) {
				// TODO Auto-generated catch block
		System.out.println("inside ChatClientTrhead open.. IOException");

		}
	}
	
	public void close(){
		if(strIn !=null){
			try {
				strIn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void run(){
		done = false;
		while(!done){
			try{
				client.handle(strIn.readUTF());
			}catch(IOException e){
				client.stop();
				done = true;
			}
		}
	}
	
}
