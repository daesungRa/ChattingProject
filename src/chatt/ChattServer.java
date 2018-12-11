package chatt;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 *	========== 채팅 프로그램 ==========
 *	1. 채팅을 위한 UI 창 구현
 *		- 서버주소필드, 서버상태필드, 서버구동 및 정지버튼, 접속된유저목록, 대화창, 파일전송버튼, 메시지입력필드, 메시지전송 및 귓속말 모드 버튼
 *	2. Runnable 인터페이스를 구현하여 메인스레드 구동
 *		- 메인스레드는 ServerSocket 생성 이후 서버중지 시 ServerSocket 을 close 할때까지 구동된다
 *		- 클라이언트의 접속요청 시 소켓을 연결하고 별도의 ServerThread 생성 및 구동 후 (ConcurrentHashMap) clients 리스트에 저장한다
 *			- ConcurrentHashMap 사용의 주된 이유는 synchronized 특성에 있다. 멀티스레드 환경이기 때문
 *			- 또한 Key, Value 로 이루어진 Map 의 일종이므로 삽입과 삭제가 빠르고, 검색 속도도 개선되었다.
 *			- Key 는 각 클라의 (String) id 이고, Value 는 구동중인 ServerThread 객체이다
 *			- 또한 Key 의 유일성이 자동으로 보장되므로 결과적으로 데이터 무결성에 최적화되어 있다
 *		- (JList) userList 를 위한 컬렉션은 (Vector) users 로 한다 ( >> 동기화 보장, 단순 String 저장 )
 *		- 클라이언트가 접속을 해제하면 해당 서버스레드를 종료시키고(flag 를 false 로) 리스트에서 삭제한다
 *	=================================
 */
public class ChattServer extends JFrame implements Runnable {

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panel_1;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JTextArea mainTextArea;
	private JList usrListField;
	private JLabel lblList;
	private JLabel lblDialogue;
	private JButton btnStop;
	private JButton btnStart;
	private JLabel lblState;
	private JLabel ip;
	private JLabel lblIP;
	private JButton btnFileSend;
	private JPanel panel_2;
	private JButton btnSendMsg;
	private JButton btnWhisper;
	private JTextField txtfield;
	private JLabel cstate;

	/**
	 * Launch the application.
	 */

	/* ================ declare variables ================ */
	private ServerSocket serverSocket;
	private int serverPort = 9000;
	private Thread mainThread;
	
	// 메인스레드 구동 플래그
	private boolean mainFlag = false;
	
	// 귓속말 모드 플래그
	private boolean whisperMode = false;
	
	private Map<String, ServerThread> clients = new ConcurrentHashMap<String, ServerThread>();
	private Vector<String> users = new Vector<String>(10, 5);
	private JList<String> userList;
	/* ============= end of declare variables ============= */

	/* ============= define methods ============= */
	@Override
	public void run() {
		try {
			mainFlag = true;
			// 서버소켓 생성
			serverSocket = new ServerSocket(serverPort);

			// serverStop() 호출시까지 활성화 상태로 순환
			while (mainFlag) {
				// 클라로부터 접속요청이 있기까지 대기
				Socket socket = serverSocket.accept();
				
				if (mainFlag) {
					// 클라이언트 접속 성공 메시지
					InetSocketAddress inetd = (InetSocketAddress) socket.getRemoteSocketAddress();
					this.msg(inetd.getAddress() + " 접속했습니다.", false);
					
					// 각종 처리를 위한 서버스레드 별도 생성 (구동은 생성자에서)
					ServerThread serverThread = new ServerThread(ChattServer.this, socket);
					
					// this.clients 에 ServerThread 추가하는 로직은 생성된 ServerThread 내부에서 login 커맨드 수신로직에서 Data 객체를 기반으로 한다
					// Data 객체에 클라이언트의 id 가 포함되어 전송되기 때문
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			ChattServer.this.msg("서버 구동 중 문제 발생(2)", false);
		}

	}

	private void serverStart() {
		try {
			// 버튼 활성화/비활성화
			btnStart.setEnabled(false);
			btnStop.setEnabled(true);
			
			// 서버시작 메시지 출력
			this.mainTextArea.setText("");
			this.msg("서버 시작...", false);
			
			// 메인 스레드 생성 이후 서버 구동
			mainThread = new Thread(ChattServer.this);
			this.msg("메인 스레드 생성 완료", false);
			mainThread.start();
			this.msg("메인 스레드 구동중", false);
			
			this.cstate.setText("Operating");
		} catch (Exception ex) {
			ex.printStackTrace();
			ChattServer.this.msg("서버 구동 중 문제 발생(1)", false);
		}
	}

	private void serverStop() {
		try {
			// 버튼 활성화/비활성화
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
			
			this.mainTextArea.setText("");
			this.msg("서버 종료 중...", false);
			
			// 접속 중인 모든 클라이언트에 접속 종료 메시지 전송
			// 3 번 메시지를 받은 각 클라이언트는 스레드 내에서 접속 종료 로직을 수행한다
			Iterator<String> ite = this.clients.keySet().iterator();
			if (ite.hasNext()) {
				String id = ite.next();
				clients.get(id).send(new Data("server", 3, "서버를 중지합니다. 자동으로 접속 종료됩니다."));
			}
			
			// 각 목록 초기화
			this.clients.clear();
			this.users.clear();
			
			// 메인스레드 종료시킴
			// 메인플래그를 false 로 바꾸고 임시 접속요청으로 serverSocket.accept() 메서드 블러킹을 해제시킴으로 메인스레드를 종료시킨다
			mainFlag = false;
			new Socket(InetAddress.getLocalHost().getHostAddress(), this.serverPort);
			
			// 서버소켓 해제
			this.serverSocket.close();
			// gc 가 자원을 곧바로 회수하지는 않으므로 해당 변수는 강제로 비운다
			this.serverSocket = null;
			
			// 사용자 체감 중지시간 지연
			Thread.sleep(2000);
		} catch (Exception ex) {
			ex.printStackTrace();
			this.msg("서버 종료 중 문제 발생", false);
		}
		
		this.msg("서버 종료 완료", false);
		this.cstate.setText("Server Stopped");
	}

	// flag 가 true 면 일반 메시지, 아니면 시스템 메시지
	void msg(String msg, boolean flag) {
		if (flag) {
			mainTextArea.append(msg + "\n");
		} else {
			mainTextArea.append("alert > " + msg + "\n");
		}
	}
	
	
	// 1. disable whisper mode - send message that located in [txtField] to all clients that connected to server
	// 2. enable whisper mode - send message that located in [txtField] to selected clients in userList
	public void sendMsg() {
		Data sendData;
		// if whisperMode sets true, written message will be sent selected users
		// The user to receive the whisper message is selected in the userlist
		if (whisperMode) {

			// JList 에서 선택된 유저들에게만 전송
			List<String> selectedUsers = usrListField.getSelectedValuesList();
			
			// 메시지를 서버에 출력
			// 리스트를 배열로 변환해서 Arrays.deepToString 으로 그대로 출력
			this.msg("[귓]server to " + Arrays.deepToString(selectedUsers.toArray()) + " > " + txtfield.getText(), true);
			
			// 전달할 귓속말 객체 생성
			sendData = new Data("server", 4, txtfield.getText().trim());
			
			for (String selUser : selectedUsers) {
				this.clients.get(selUser).send(sendData);
			}
			
			// if whisperMode sets false, written message will be sent all users
		} else {

			// 메시지를 서버에 출력
			this.msg("[메시지]server > " + txtfield.getText(), true);
			
			sendData = new Data("server", 1, this.txtfield.getText());
			for (String toUsers : this.users) {
				this.clients.get(toUsers).send(sendData);
			}
			
		}
	}
	
	/* ========== end of define methods ========== */
	
	/* ========== getter and setter ========== */
	// flag 변수를 int 로 해야 하나??
	public void setClients(String id, ServerThread st, boolean flag) {
		// true 면 객체 추가, 아니면 삭제
		if (flag) {
			this.clients.put(id, st);
		} else {
			this.clients.remove(id);
		}
	}
	
	// 새로 생성된 서버스레드 추가 시 필요함
	public Map<String, ServerThread> getClients(){
		return this.clients;
	}
	
	public void setUsers(String id, boolean flag) {
		// true 면 객체 추가, 아니면 객체 삭제
		if (flag) {
			this.users.add(id);
		} else {
			this.users.remove(id);
		}
	}
	
	// 접속 유저 리스트를 전송할 경우가 있으므로 필요함
	public Vector<String> getUsers() {
		return this.users;
	}
	/* ========== end of getter and setter ========== */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChattServer frame = new ChattServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChattServer() {
		setTitle("LET'S CHATT");
		setMinimumSize(new Dimension(800, 500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(getPanel(), BorderLayout.NORTH);
		contentPane.add(getPanel_1(), BorderLayout.SOUTH);
		contentPane.add(getScrollPane(), BorderLayout.WEST);
		contentPane.add(getScrollPane_1(), BorderLayout.CENTER);
		
		try {
			InetAddress inetd = InetAddress.getLocalHost();
			String addr = inetd.getHostAddress();
			this.ip.setText(addr);
		} catch (Exception ex) {
			ex.printStackTrace();
			this.msg("네트워크 에러 발생", true);
		}
		
		btnStop.setEnabled(false);
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBackground(new Color(143, 188, 143));
			panel.setPreferredSize(new Dimension(10, 40));
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
			panel.add(getLblIP());
			panel.add(getIp());
			panel.add(getLblState());
			panel.add(getCstate());
			panel.add(getBtnStart());
			panel.add(getBtnStop());
		}
		return panel;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setPreferredSize(new Dimension(10, 40));
			panel_1.setLayout(new BorderLayout(0, 0));
			panel_1.add(getBtnFileSend(), BorderLayout.WEST);
			panel_1.add(getPanel_2(), BorderLayout.EAST);
			panel_1.add(getTxtfield(), BorderLayout.CENTER);
		}
		return panel_1;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setPreferredSize(new Dimension(200, 2));
			scrollPane.setViewportView(getUsrListField());
			scrollPane.setColumnHeaderView(getLblList());
		}
		return scrollPane;
	}
	private JScrollPane getScrollPane_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setViewportView(getMainTextArea());
			scrollPane_1.setColumnHeaderView(getLblDialogue());
		}
		return scrollPane_1;
	}
	private JTextArea getMainTextArea() {
		if (mainTextArea == null) {
			mainTextArea = new JTextArea();
			mainTextArea.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					if (me.getButton() == MouseEvent.BUTTON3) {
						if (JOptionPane.showConfirmDialog(ChattServer.this, "clear this area?") == JOptionPane.OK_OPTION) {
							mainTextArea.setText("");
						}
					}
				}
			});
			mainTextArea.setEditable(false);
		}
		return mainTextArea;
	}
	JList getUsrListField() {
		if (usrListField == null) {
			usrListField = new JList();
		}
		return usrListField;
	}
	private JLabel getLblList() {
		if (lblList == null) {
			lblList = new JLabel("USERS");
			lblList.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblList;
	}
	private JLabel getLblDialogue() {
		if (lblDialogue == null) {
			lblDialogue = new JLabel("DIALOGUES");
			lblDialogue.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblDialogue;
	}
	private JButton getBtnStop() {
		if (btnStop == null) {
			btnStop = new JButton("SERVER STOP");
			btnStop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					serverStop();
				}
			});
			btnStop.setPreferredSize(new Dimension(120, 30));
		}
		return btnStop;
	}
	private JButton getBtnStart() {
		if (btnStart == null) {
			btnStart = new JButton("SERVER START");
			btnStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					serverStart();
				}
			});
			btnStart.setPreferredSize(new Dimension(130, 30));
		}
		return btnStart;
	}
	private JLabel getLblState() {
		if (lblState == null) {
			lblState = new JLabel("STATE : ");
			lblState.setPreferredSize(new Dimension(60, 30));
			lblState.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblState;
	}
	private JLabel getIp() {
		if (ip == null) {
			ip = new JLabel("IPAddr");
			ip.setPreferredSize(new Dimension(110, 30));
		}
		return ip;
	}
	private JLabel getLblIP() {
		if (lblIP == null) {
			lblIP = new JLabel("IP :");
			lblIP.setHorizontalAlignment(SwingConstants.CENTER);
			lblIP.setPreferredSize(new Dimension(30, 30));
		}
		return lblIP;
	}
	private JButton getBtnFileSend() {
		if (btnFileSend == null) {
			btnFileSend = new JButton("FILESEND");
		}
		return btnFileSend;
	}
	private JPanel getPanel_2() {
		if (panel_2 == null) {
			panel_2 = new JPanel();
			panel_2.add(getBtnSendMsg());
			panel_2.add(getBtnWhisper());
		}
		return panel_2;
	}
	private JButton getBtnSendMsg() {
		if (btnSendMsg == null) {
			btnSendMsg = new JButton("SEND");
			btnSendMsg.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					sendMsg();
					txtfield.setText("");
					txtfield.requestFocus();
				}
			});
			btnSendMsg.setPreferredSize(new Dimension(65, 30));
		}
		return btnSendMsg;
	}
	private JButton getBtnWhisper() {
		if (btnWhisper == null) {
			btnWhisper = new JButton("WHISPER ON");
			btnWhisper.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (whisperMode) {
						btnWhisper.setText("WHISPER ON");
						whisperMode = false;
					} else {
						btnWhisper.setText("WHISPER OFF");
						whisperMode = true;
					}
				}
			});
			btnWhisper.setPreferredSize(new Dimension(120, 30));
		}
		return btnWhisper;
	}
	private JTextField getTxtfield() {
		if (txtfield == null) {
			txtfield = new JTextField();
			txtfield.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent ke) {
					if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
						sendMsg();
						txtfield.setText("");
						txtfield.requestFocus();
					}
				}
			});
			txtfield.setColumns(10);
		}
		return txtfield;
	}
	private JLabel getCstate() {
		if (cstate == null) {
			cstate = new JLabel("CURRENT STATE");
			cstate.setPreferredSize(new Dimension(110, 30));
		}
		return cstate;
	}

}
