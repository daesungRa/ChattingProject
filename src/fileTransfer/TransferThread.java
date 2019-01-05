package fileTransfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

// 여기서는 전송할 파일명과 소켓경로만을 받아
// 파일만 전송한다
public class TransferThread extends Thread {
	// 기본 파일전송 패널
	private FileTransfer ft;
	
	// 파일을 전송할 소켓
	private Socket socket;
	
	// 파일전송 스트림
	private OutputStream os;
	private BufferedOutputStream bos;
	
	// 파일전송 단위
	private byte[] data = new byte[8192];
	private int readByte = 0;
	private int percent;
	
	// 전송할 파일경로 + 파일명
	private String fileName;
	
	// 파일 객체
	private File file;
	
	// 전송할 파일 읽어오기
	private FileInputStream fis;
	private BufferedInputStream bis;
	
	public TransferThread(FileTransfer ft, String fileName, Socket socket) {
		this.ft = ft;
		this.fileName = fileName;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			os = socket.getOutputStream();
			bos = new BufferedOutputStream(os);
			
			file = new File(fileName);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			
			long max = file.length();
			long nowLength = 0;
			
			while ((readByte = bis.read(data)) != -1) {
				bos.write(data);
				nowLength += readByte;
				percent = (int) ((double) nowLength / max * 100);
				// ft.getProgressBar_1_1().setValue(percent); // 이상하게 메서드명 변경이 불가..
				// ft.getPercentCurr().setText("" + percent + " %");

				this.sleep(100);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
