package chatt;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Iterator;

public class ServerThread extends Thread {

	private ChattServer cs;
	private Socket serverSocket;
	private OutputStream os;
	private ObjectOutputStream oos;
	private InputStream is;
	private ObjectInputStream ois;
	private boolean threadFlag = false;
	
	ServerThread(ChattServer cs, Socket socket) {
		this.cs = cs;
		this.serverSocket = socket;
		this.start();
	}
	
	@Override
	public void run() {
		// 현재 해당 서버스레드로 접속 중인 유저의 아이디 저장
		String curID = null;
		
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
				Data receiveData = (Data) ois.readObject();
				Data responseData;
				
				// 객체 판별은 id 로 한다
				String id = receiveData.getId();
				String msg = receiveData.getMessage().trim();
				
				// 최초 로그인 시에만 실행됨
				// 전역변수에 접속주체의 아이디를 저장
				if (curID == null) {
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
					break;
				case 4: // whisper mode
					
					// 받은 메시지 출력
					this.cs.msg("[귓] to " + Arrays.deepToString(receiveData.getUsers().toArray()) + " > " + msg, true);
					
					// 선택된 유저들에게만 재전송 (그냥 받은 객체 재전송)
					// 벡터 리스트로 반환됨
					for (String selectedUsers : receiveData.getUsers()) {
						this.cs.getClients().get(selectedUsers).send(receiveData);
					}
					
					break;
				case 11: // file transfer request from client
					break;
				}
			}
		} catch (SocketException se) {
			se.printStackTrace();
			this.cs.msg("[유저아이디]" + curID + " > 클라이언트의 비정상적 종료", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			this.cs.msg("[유저아이디]" + curID + " > 서버스레드 구동 중 문제 발생", false);
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
	// 단, 접속한 유저의 아이디와 매개변수로 넘어온 Data 객체의 아이디가 같다면(동일한 유저라면) send 하지 않는다
	public void sendAll(Data d) {
		Iterator<String> ite = cs.getClients().keySet().iterator();
		while (ite.hasNext()) {
			String cid = ite.next();
			cs.getClients().get(cid).send(d);
		}
	}
}







