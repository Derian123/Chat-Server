public class OneTimePad {
	
	private String plainMessage ="";
	private String encryptedMessage = "";
	private String currentKey="";
	
	public OneTimePad(){
		plainMessage="no message";
		currentKey = generateKey(plainMessage);
		encryptedMessage = encrypt(plainMessage);
		
	}
	public OneTimePad(String msg){
		plainMessage= msg;
		currentKey = generateKey(msg);
		encryptedMessage = encrypt(msg);
		
	}
	private int getNumFromChar(char c){
		return Character.valueOf(c);
	}


	private char getCharFromNum(int num){
		return Character.toChars(num)[0];
	}
	
	public String encrypt(String plainMsg){
		String encMsg = "";

		for(int i=0; i<plainMsg.length(); i++){
		//String g = generateKey(plainMsg);
		int numForPlainChar = getNumFromChar(plainMsg.charAt(i));//get num from plain char
		int numForKeyChar = getNumFromChar(currentKey.charAt(i));//get num from the key's char
		int numForEncChar = numForPlainChar + numForKeyChar;
		char encryptedChar = getCharFromNum(numForEncChar); 
		encMsg += encryptedChar;// append the char to out encrypted message
		}
		return encMsg;
	}
	
	public String decrypt(String encMsg){
		String decMsg = "";
		for(int i=0; i<encMsg.length(); i++){
			int numForEncChar = getNumFromChar(encMsg.charAt(i));
			int numForKeyChar = getNumFromChar(currentKey.charAt(i));
			int numForPlainChar = numForEncChar - numForKeyChar;
			char plainChar = getCharFromNum(numForPlainChar);
			decMsg += plainChar;
		}
		return decMsg;
	}
	
	public String generateKey(String plainMsg){
		String key = "";
		for(int i=0; i<plainMsg.length(); i++){
			int randNum = 64 + (int)(Math.random()*26);
			key += getCharFromNum(randNum);
		}
		return key;
	}

	public String getPlainMessage() {
		return plainMessage;
	}

	public String getEncryptedMessage() {
		return encryptedMessage;
	}

	public void setPlainMessage(String plainMessage) {
		this.plainMessage = plainMessage;
	}

	public void setEncryptedMessage(String encryptedMessage) {
		this.encryptedMessage = encryptedMessage;
	}

	public void setCurrentKey(String currentKey) {
		this.currentKey = currentKey;
	}

	public String getCurrentKey() {
		return currentKey;
	}


	public static void main(String [] args){
		OneTimePad otp = new OneTimePad("hi");

		System.out.println("The Plain Message: "+ otp.plainMessage);
		System.out.println("The Key for the  Message: "+ otp.currentKey);
		String encMsg = otp.encryptedMessage;
		String deMsg = otp.decrypt(encMsg);
		System.out.println("The Encrypted Message: "+ encMsg );

		System.out.println("The decrypted Message: "+ deMsg );


		System.out.println("The Plain Message: "+ otp.decrypt(encMsg));
	}
	;
}
