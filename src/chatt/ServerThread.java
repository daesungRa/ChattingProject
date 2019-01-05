package chatt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import fileTransfer.FileServerPort;

public class ServerThread extends Thread {

	private ChattServer cs;
	private Socket serverSocket;
	private OutputStream os;
	private ObjectOutputStream oos;
	private InputStream is;
	private ObjectInputStream ois;
	private boolean threadFlag = false;
	
	// 현재 해당 서버스레드로 접속 중인 유저의 아이디 저장
	private String curID = null;
	
	// 파일을 수신할 스레드
	private ReceiveThread rt;
	
	// 파일을 송신할 스레드
	private SendThread st;
	
	// 파일수신을 위해 임시생성하는 파일서버
	private class ReceiveThread extends Thread {
		// 파일수신을 위한 서버소켓
		private ServerSocket serverSocket;
		
		// 파일수신을 위한 소켓
		private Socket socket;
		
		// 사용할 포트번호
		private int port;
		
		// 파일을 수신할 스트림
		private InputStream is;
		private BufferedInputStream bis;
		
		// 수신할 데이터 단위
		private byte[] data = new byte[8192];
		private int readByte = 0;
		
		// 수신받을 파일명 목록
		private Vector<String> fileNames;
		
		// 클라로부터 수신받은 파일을 저장할 경로 - 사용자 홈 디렉토리
		private File fileSavePath = new File(System.getProperty("user.home"));
		
		// 수신받은 파일을 저장할 스트림
		private FileOutputStream fos;
		private BufferedOutputStream bos;
		
		// 생성자
		// 수신받을 파일명 목록과 포트번호
		ReceiveThread(List<String> fileNames, int port) {
			this.fileNames = (Vector<String>) fileNames;
			this.port = port;
		}
		
		@Override
		public void run() {
			try {
				
				// 서버소켓 생성
				serverSocket = new ServerSocket(port);
				
				// 서버소켓 생성 후 클라의 파일전송 대기
				socket = serverSocket.accept();
				
				// 파일전송이 시작되면 전송되는 파일을 단위별로 읽어들이는 InputStream
				// 클라이언트와 연결된 스트림
				is = socket.getInputStream();
				bis = new BufferedInputStream(is);
				
				// 수신받은 파일을 지정된 경로에 저장하기 위한 OutputStream
				// 로컬에 저장
				fos = new FileOutputStream(fileSavePath);
				bos = new BufferedOutputStream(fos);
				
				// 단위별로 읽어들이기
				while ((readByte = bis.read(data)) != -1) {
					// 지정된 경로에 저장
					bos.write(data);
				}
				
			} catch (Exception ex) {
				
				ex.printStackTrace();
				cs.msg("파일 수신 실패", false);
				
			} finally {
				try {
					// 모든 스트림 close
					bos.flush();
					bos.close();
					fos.close();
					bis.close();
					is.close();
					
					socket.close();
					serverSocket.close();
					
				} catch (Exception ex) { }
			}
		}
	}
	
	// 파일송신을 위해 임시생성하는 파일서버
	private class SendThread extends Thread {
		// 파일송신을 위한 서버소켓;
		ServerSocket serverSocket;
		
		// 사용할 포트번호
		int port;
		
		Vector<String> fileNames;
		
		public SendThread(List<String> fileNames, int port) {
			this.fileNames = (Vector<String>) fileNames;
			this.port = port;
		}
		
		@Override
		public void run() {
			try {
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	ServerThread(ChattServer cs, Socket socket) {
		this.cs = cs;
		this.serverSocket = socket;
		this.start();
	}
	
	@Override
	public void run() {

		// 여러 요청을 수용하거나 재전송할 객체
		Data receiveData = null;
		Data responseData = null;
		
		try {
			// ChattServer 에서 ServerThread 생성 후 생성자에서 해당 스레드를 구동(start)하면,
			// 스레드 플래그를 true 로 하고 클라이언트가 접속 종료 (3) 할때까지 구동된다
			threadFlag = true;
			
			// 서버로부터 넘겨받은 소켓으로부터 스트림 생성
			os = serverSocket.getOutputStream();
			is = serverSocket.getInputStream();
			
			oos = new ObjectOutputStream(os);
			ois = new ObjectInputStream(is);
			
			// 로그인 ~ 로그아웃 시까지 생명주기
			while (threadFlag) {
				// 구축된 스트림으로부터 전송된 객체를 수용한다
				receiveData = (Data) ois.readObject();
				
				// 객체 판별은 id 로 한다
				String id = receiveData.getId();
				String msg = receiveData.getMessage();
				
				// 최초 로그인 시에만 실행됨
				// null 이거나 빈 문자열이면 실행됨
				// 전역변수에 접속주체의 아이디를 저장
				if (curID == null || curID.equals("")) {
					curID = id;
				}
				
				switch (receiveData.getCommand()) {
				case 1: // message
					
					// 받은 메시지 출력
					this.cs.msg("[메시지]" + id + " > " + msg, true);
					
					// 접속한 모든 유저에 재전송
					this.sendAll(receiveData);
					
					break;
				case 2: // login
					// 로그인 요청 메시지 출력
					cs.msg("[로그인 요청]" + id + " > " + msg, false);
					
					// 해당 서버스레드를 ChattServer 의 clients 맵구조에 추가
					this.cs.setClients(id, ServerThread.this, true);
					
					// id 를 users 에 추가하고 list 에 세팅
					this.cs.setUsers(id, true);
					this.cs.getUsrListField().setListData(this.cs.getUsers());
					
					// 로그인 완료 메시지를 담은 객체
					responseData = new Data(id, 2, "로그인 성공", this.cs.getUsers());
					
					// 모든 접속유저에게 새 유저의 접속 알림
					this.sendAll(responseData);
					
					break;
				case 3: // logout
					this.cs.msg("[접속종료]" + curID + " > " + msg, true);
					
					// 접속종료 객체를 통신 중인 모든 클라이언트스레드로 재전송하여 해당 유저의 접속종료를 알린다
					this.sendAll(receiveData);
					
					// 서버에서 해당 스레드 및 유저 삭제 (flag == false)
					this.cs.setClients(curID, ServerThread.this, false);
					this.cs.setUsers(curID, false);
					
					// JList 갱신
					this.cs.getUsrListField().setListData(this.cs.getUsers());
					
					// 플래그를 거짓으로 하고 로직을 흘려보낸다 >> 스레드 종료됨
					this.threadFlag = false;
					
					break;
				case 4: // whisper mode
					
					// 받은 메시지 출력
					this.cs.msg("[귓]" + id + " to " + Arrays.deepToString(receiveData.getUsers().toArray()) + " > " + msg, true);
					
					// 선택된 유저들에게만 재전송 (그냥 받은 객체 재전송)
					// 벡터 리스트로 반환됨
					for (String selectedUsers : receiveData.getUsers()) {
						this.cs.getClients().get(selectedUsers).send(receiveData);
					}
					
					break;
				case 11: // file transfer request from client
					
					// 서버에 파일전송 요청이 들어왔음을 알림
					this.cs.msg("[파일전송 요청]" + id + " > " + msg, false);
					
					// 포트번호 변경
					if (FileServerPort.fileServerPort == 9999) {
						FileServerPort.fileServerPort = 9000;
					} else {
						FileServerPort.fileServerPort++;
					}
					
					// 파일수신을 위한 ReceiveThread 생성 및 구동, 여기서 가변포트로 서버소켓을 새로 생성한다
					// 요청한 클라에 12 번 커맨드를 재송신하여 파일을 보내도 된다고 알려준다
					// 파일수신이 시작되면 모두 끝나기까지 join 을 건다
					// receiveData.getUsers() 는 수신할 파일목록임. 기존의 벡터 users 재사용했기 때문에 이름이 겹친다(개선..?)
					rt = new ReceiveThread(receiveData.getUsers(), FileServerPort.fileServerPort);
					// 메세지 위치에 해당 가변포트번호를 송신
					responseData = new Data("server", 12, FileServerPort.fileServerPort + "");
					this.send(responseData);
					rt.setDaemon(true);
					rt.join();
					rt.start();
					
					// 포트번호 변경
					// rt 의 작업이 종료되어도 gc 에 의해 자원이 회수되지 않았을 가능성이 있으므로 아예 다른 포트번호로 한다
					if (FileServerPort.fileServerPort == 9999) {
						FileServerPort.fileServerPort = 9000;
					} else {
						FileServerPort.fileServerPort++;
					}
					
					cs.msg("[수신완료] 파일 수신 완료 후 포트 가변", false);
					
					// rt 의 작업이 종료된 이후(join) 접속된 모든 클라이언트로 받은 파일을 재전송하는 SendThread 를 생성,
					// 여기서도 가변포트로 서버소켓을 새로 생성한다.
					// 이때 모든 클라로 13 번 커맨드와 포트번호를 전송하여 파일수신대기상태가 되도록 요청한다
					// receiveData.getUsers() 는 전송할 파일목록임. 기존의 벡터 users 재사용했기 때문에 이름이 겹친다(개선..?)
					st = new SendThread(receiveData.getUsers(), FileServerPort.fileServerPort);
					responseData = new Data("server", 13, "파일을 전송을 요청합니다.");
					this.sendAllWithoutMySelf(responseData);
					st.setDaemon(true);
					st.join();
					st.start();
					
					cs.msg("[송신완료] 수신받은 파일 Broadcast 완료", false);
					
					break;
				}
			}
		} catch (SocketException se) {
			se.printStackTrace();
			this.cs.msg("[접속종료]" + curID + " > 클라이언트의 비정상적 종료", false);
			
			// 접속종료 객체를 통신 중인 모든 클라이언트스레드로 재전송하여 해당 유저의 접속종료를 알린다
			if (receiveData != null) {
				Data sendData = new Data(curID, 3, "로그아웃 할게요~");
				this.sendAllWithoutMySelf(sendData);
			}
			
			// 서버에서 해당 스레드 및 유저 삭제 (flag == false)
			this.cs.setClients(curID, ServerThread.this, false);
			this.cs.setUsers(curID, false);
			
			// JList 갱신
			this.cs.getUsrListField().setListData(this.cs.getUsers());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			this.cs.msg("[접속종료]" + curID + " > 서버스레드 구동 중 문제 발생", false);
		} finally {
			try {
				oos.flush();
				oos.close();
				os.close();
				ois.close();
				is.close();
			} catch (Exception ex) { }
		}
	}
	
	public void send(Data d) {
		try {
			oos.writeObject(d);
			oos.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
			cs.msg("send 중 에러 발생", false);
		}
	}
	
	// 반복자로 접속한 유저들의 ServerThread 얻어와서 Data 객체를 send 한다
	public void sendAll(Data d) {
		Iterator<String> ite = cs.getClients().keySet().iterator();
		while (ite.hasNext()) {
			String cid = ite.next();
			cs.getClients().get(cid).send(d);
		}
	}
	
	// 자기 자신을 제외한 sendAll
	public void sendAllWithoutMySelf(Data d) {
		Iterator<String> ite = cs.getClients().keySet().iterator();
		while (ite.hasNext()) {
			String cid = ite.next();
			if (!cid.equals(curID)) {
				cs.getClients().get(cid).send(d);
			}
		}
	}
}







