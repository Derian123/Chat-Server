import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClientGUI extends JFrame implements ActionListener, Runnable {

	private Socket socket = null;
	private final String serverName = "localhost"; //"localhost"//or your friend's ip address
	private final int serverPortNumber = 8080; //needs to match

	private DataInputStream strIn = null;
	private DataOutputStream strOut = null;

	private ChatClientThread client = null;
	private boolean done = true;//until connected you are "done"
	private String line = "";

	public String name = "";

	private JTextArea displayText = new JTextArea();
	public JTextField input = new JTextField(30);
	private JButton btnConnect = new JButton("Connect");
	public JButton btnSend = new JButton("Send");
	private JButton btnQuit = new JButton("Bye");
	private JButton btnPrivate = new JButton("Private");
	private JPanel mainJP = new JPanel();
	private JPanel displayJP = new JPanel();
	private JPanel btnsJP = new JPanel();



	public ChatClientGUI() {
		this.setTitle("My Chat");
		mainJP.setLayout(new BorderLayout());
		displayJP.setLayout(new GridLayout(2, 1));
		displayJP.add(displayText); //added text area to jpanel
		displayJP.add(input);//added input below text area to jpanel
		btnsJP.setLayout(new GridLayout(1, 4));

		btnPrivate.addActionListener(this);
		btnConnect.addActionListener(this);
		btnSend.addActionListener(this);
		btnQuit.addActionListener(this);

		btnsJP.add(btnPrivate);
		btnsJP.add(btnConnect);
		btnsJP.add(btnSend);
		btnsJP.add(btnQuit);

		mainJP.add(displayJP, BorderLayout.CENTER);//add to center
		mainJP.add(btnsJP, BorderLayout.SOUTH);//add to bottom

		add(mainJP);

		btnQuit.setEnabled(false);
		btnPrivate.setEnabled(false);
		btnSend.setEnabled(false);
	}

	@Override
	public void run() {
		while (!done) {
			try {

				line = strIn.readUTF();
				println(line);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		//When connected clients connect to one another
		if (e.getSource() == btnConnect)
			connect(serverName, serverPortNumber);

		//Sends message when send button is hit
		if (e.getSource() == btnSend)
			send();
		//Private message to another client
		if(e.getSource() == btnPrivate)
			privateMsg();

		//leaves the chat server
		if (e.getSource() == btnQuit)
			disconnect();



	}

	public void privateMsg() {
		String msg = input.getText();
		System.out.print(msg);

		if (msg.startsWith("encrypt")) {//Change back to private

			try {
				OneTimePad otp = new OneTimePad(msg.substring(13));
				String en = msg.substring(0,12);
				String key = otp.getCurrentKey();
				String encmsg = otp.encrypt(msg.substring(13));
				strOut.writeUTF(en + encmsg + key); //strOut.writeUTF(msg);
				strOut.flush();
				input.setText("");
				//displayText.append(otp.encrypt(msg)); // Comment out when testing for encrypt


				System.out.print(msg);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(msg.startsWith("private")){
			try {
				strOut.writeUTF(msg);
				strOut.flush();
				input.setText("");



			}catch (IOException e){
				e.printStackTrace();
			}
		}

	}

	public void connect(String serverName, int serverPortNumber) {
		try {
			done = false;
			btnSend.setEnabled(true);
			btnPrivate.setEnabled(true);
			btnQuit.setEnabled(true);
			btnConnect.setEnabled(false);


			socket = new Socket(serverName, serverPortNumber);
			System.out.println("We got connected");
			println("Connected to the server port number: " + serverPortNumber);

			open();
			btnSend.enable();
			btnConnect.enable();
			btnQuit.enable();
			btnPrivate.enable();




			//Enables our buttons and creats a new socket when we hit connect.


		} catch (UnknownHostException e) {
			e.printStackTrace();
			done = true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			done = true;
		}

	}



	public void send() {
		try {

			String msgs = input.getText() ;
			if(msgs.startsWith("private")){ // Change back to private
				println("This is considered a private message! ");
			}
			if(msgs.startsWith("encypt")){
				println("Please use private button to send! ");
			}
			else {
				strOut.writeUTF(msgs);
				strOut.flush();


				input.setText("");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void disconnect() {
		done = true;
		btnQuit.setEnabled(false);
		btnSend.setEnabled(false);
		btnPrivate.setEnabled(false);
		btnConnect.setEnabled(true);
		input.setText("BYE");
		send();

	}

	public void open() {
		try {
			strOut = new DataOutputStream(socket.getOutputStream());
			strIn = new DataInputStream(socket.getInputStream());
			new Thread(this).start();//to be able to listen in

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void println(String msg) {

		displayText.append(msg + "\n");
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater( new Runnable(){
			public void run() {
				ChatClientGUI chatclient = new ChatClientGUI();
				chatclient.pack();
				chatclient.setVisible(true);

			}
		}
		);
	}
}
