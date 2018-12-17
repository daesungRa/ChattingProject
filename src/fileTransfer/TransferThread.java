package fileTransfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

// 여기서는 전송할 파일명과 소켓경로만을 받아
// 파일만 전송한다
public class TransferThread extends Thread {
	// 파일을 전송할 소켓
	private Socket socket;
	
	// 파일전송 스트림
	private OutputStream os;
	private BufferedOutputStream bos;
	
	// 파일전송 단위
	byte[] data = new byte[8192];
	int readByte = 0;
	
	// 전송할 파일경로 + 파일명
	private String fileName;
	
	// 전송할 파일 읽어오기
	private FileInputStream fis;
	private BufferedInputStream bis;
	
	public TransferThread(String fileName, Socket socket) {
		this.fileName = fileName;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			os = socket.getOutputStream();
			bos = new BufferedOutputStream(os);
			
			fis = new FileInputStream(fileName);
			bis = new BufferedInputStream(fis);
			
			while ((readByte = bis.read(data)) != -1) {
				bos.write(data);
			}
		} catch (Exception ex) {
		} finally {
			try {
				bos.flush();
				bos.close();
				bis.close();
				os.close();
				fis.close();
			} catch (Exception ex) { }
		}
	}
}
