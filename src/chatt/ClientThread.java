package chatt;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

public class ClientThread extends Thread {
	// 전달받은 ChattClient 저장
	private ChattClient cc;
	// ChattClient 로 부터 전달받는 ChattServer 와 연결된 소켓 저장
	private Socket clientSocket ;
	// 객체 입력스트림과 출력스트림
	private OutputStream os;
	private ObjectOutputStream oos;
	private InputStream is;
	private ObjectInputStream ois;
	// 스레드 플래그
	private boolean threadFlag = false;
	
	ClientThread(ChattClient cc, Socket socket) {
		this.cc = cc;
		this.clientSocket = socket;
		this.start();
	}
	
	@Override
	public void run() {
		// 현재 해당 서버스레드로 접속 중인 유저의 아이디 저장
		String curID = null;
		
		try {
			threadFlag = true;

			os = clientSocket.getOutputStream();
			is = clientSocket.getInputStream();
			
			oos = new ObjectOutputStream(os);
			ois = new ObjectInputStream(is);
			
			// cc.msg("*** 스트림 생성완료");
			
			// 로그인 요청 메시지
			// 본래는 cc 에서 ct 생성하고 ct 와는 별도로 수행하는 로직이었으나,
			// 스트림이 생성된 후에 실행된다는 보장이 없어서 ct 내부로 옮김
			curID = cc.getId().getText().trim();
			int command = 2; // login
			String message = new String("로그인 할게요~");

			// 요청 내용을 Data 객체에 담아 생성한 ClientThread 의 send() 로 서버에 전송
			Data sendData = new Data(curID, command, message);
			this.send(sendData);
			
			while (threadFlag) {
				// 구축된 스트림으로부터 전송된 객체를 수용한다
				Data receiveData = (Data) ois.readObject();
				
				// 객체 판별은 id 로 한다
				String id = receiveData.getId();
				String msg = receiveData.getMessage();
				
				switch (receiveData.getCommand()) {
				case 1: // message
					
					// 받은 메시지가 자기가 보낸 메시지인지 판별
					if (curID.equals(id)) {
						this.cc.msg("[메시지]나" + " > " + msg, true);
					} else {
						this.cc.msg("[메시지]" + id + " > " + msg, true);
					}
					
					break;
				case 2: // login
					
					// 자기 자신이라면,
					if (id.equals(cc.getId().getText().trim())) {
						
						// 로그인 성공 메시지 출력
						this.cc.msg("[로그인 성공]" + id + " > " + msg, false);
						
						// 최초 로그인이므로 cc 의 users 를 넘겨받은 users 로 초기화한다
						Vector<String> receiveUsers = (Vector<String>) receiveData.getUsers();
						for (String tid : receiveUsers) {
							this.cc.setUsers(tid, true);
						}
						
					} else {
						
						// 타 유저 로그인 메시지 출력
						this.cc.msg("[유저 로그인]" + id + " 사용자가 로그인하였습니다.", false);
						
						// cc 의 users 벡터에 추가
						this.cc.setUsers(id, true);
						
					}
					
					// 접속 유저 목록을 JList 에 반영
					cc.getUsrListField().setListData(this.cc.getUsers());
					
					break;
				case 3: // logout
				
					// 자기 자신이라면,
					if (id.equals(this.cc.getId().getText().trim())) {
						
						// 접속종료 메시지
						this.cc.msg("[접속종료중]" + curID + " > 접속을 종료합니다.", false);
						
						// 플래그를 거짓으로 하고 로직을 흘려보낸다 >> 스레드 종료됨
						this.threadFlag = false;

						// 벡터 users 초기화
						this.cc.getUsers().clear();
						this.cc.getUsrListField().setListData(this.cc.getUsers());
						
					// 자기 자신이 아니라면 벡터 users 와 리스트만 갱신
					} else {
						
						// 접속종료 메시지
						this.cc.msg("[접속종료]" + id + " > " + msg, false);
						
						// 벡터 users 에서 해당 유저 삭제(flag == false) 및 리스트 갱신
						this.cc.setUsers(id, false);
						this.cc.getUsrListField().setListData(this.cc.getUsers());
						
					}
					
					break;
				case 4: // whisper mode
					
					// 자기 자신이 아니라면 출력
					if (!id.equals(this.cc.getId().getText().trim())) {
						// 받은 메시지 출력
						this.cc.msg("[귓]" + id + " > " + msg, true);
					}
					
					break;
				case 12:
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			cc.msg("[스레드 에러]" + curID + "클라이언트 스레드 구동 중 문제가 발생했습니다.", false);
		} finally {
			try {
				oos.flush();
				oos.close();
				os.close();
				ois.close();
				is.close();
			} catch (Exception ex) { }
		}
		
		// 접속종료 메시지 출력
		this.cc.msg("[접속종료]" + curID + "접속 종료를 완료했습니다.", false);
	}
	
	public void send(Data d) {
		try {
			oos.writeObject(d);
			oos.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
			cc.msg("send 중 문제가 발생했습니다.", false);
		}
	}
	
}




