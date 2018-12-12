package fileTransfer;

import java.net.Socket;

public class TransferThread extends Thread {
	private FileTransfer ft;
	private Socket socket;
	private String id;
	
	public TransferThread(FileTransfer ft, Socket socket) {
		this.ft = ft;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		
	}
}
