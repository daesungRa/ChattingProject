package chatt;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.ClientInfoStatus;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChattClient extends JFrame {

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panel_1;
	private JButton btnFileSend;
	private JTextField txtField;
	private JPanel panel_2;
	private JButton btnSendMsg;
	private JButton btnWhisper;
	private JButton btnSetting;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JLabel password;
	private JLabel lblPwd;
	private JLabel id;
	private JLabel lblID;
	private JLabel serverAddr;
	private JLabel lblServerIP;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JList<String> usrListField;
	private JTextArea mainTextArea;
	private JLabel lblList;
	private JLabel lblDialogue;

	/**
	 * Launch the application.
	 */
	
	/* ================ declare variables ================ */
	// 서버와 연결되는 메인 소켓
	private Socket socket;
	
	// 디폴트 서버 주소
	private String serverIP = "192.168.0.7";
	
	// 서버 포트
	private int serverPort = 9000;
	
	// 접속시 생성되는 클라이언트 스레드
	private ClientThread ct;
	
	// 현재 유저의 아이디 저장
	// setting 을 해야만 초기화됨
	private String curID = null;
	
	// 귓속말 모드 플래그
	private boolean whisperMode = false;
	
	// usrListField 에 적용되는 현재 접속자 리스트
	private Vector<String> users = new Vector<String>(10, 5);
	/* ============= end of declare variables ============= */
	
	/* ============= define methods ============= */
	private void connectServer() {
		try {
			
			// NULL 혹은 빈 문자열인지 판별 후 필터링
			if (this.id.getText().equals("NULL") || this.id.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(ChattClient.this, "ID 를 입력하세요.");
				return;
			}
			
			// 버튼 활성화/비활성화
			btnConnect.setEnabled(false);
			btnDisconnect.setEnabled(true);
			// 접속중이면 setting 불가하도록
			btnSetting.setEnabled(false);
			
			// 서버와 연결되는 소켓 생성
			// UI 상단부에 세팅된 주소와 디폴트 포트번호를 기반으로 한다
			socket = new Socket(this.serverAddr.getText().trim(), serverPort);
			
			// 클라이언트스레드 생성 및 구동
			ct = new ClientThread(ChattClient.this, this.socket);			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			this.msg("서버 접속 중 에러가 발생했습니다.", false);
		}
	}
	
	private void disconnectServer() {
		try {
			
			// 버튼 활성화/비활성화
			btnConnect.setEnabled(true);
			btnDisconnect.setEnabled(false);
			// 접속 종료하면 setting 가능
			btnSetting.setEnabled(true);
			
			// 서버에 로그아웃 객체 전송
			// 서버는 스트림으로 로그아웃 객체를 재전송 함으로써 ct 스레드를 종료시킨다
			Data sendData = new Data(curID, 3, "로그아웃 할게요~");
			this.ct.send(sendData);
			
			// 벡터 users 초기화
			this.users.clear();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			this.msg("[접속종료에러]" + curID + "접속 해제 중 문제가 발생했습니다.", false);
		}
	}
	
	// flag 가 true 면 일반 메시지, 아니면 시스템 메시지
	void msg(String msg, boolean flag) {
		if (flag) {
			mainTextArea.append(msg + "\n");
		} else {
			mainTextArea.append("alert > " + msg + "\n");
		}
	}

	// 1. enable whisper mode - send message that located in [txtField] to selected clients in userList
	// 2. disable whisper mode - send message that located in [txtField] to all clients which connected to server
	private void sendMsg() {

		Data sendData;
		String msg = this.txtField.getText();
		
		// if whisperMode sets true, written message will be sent selected users
		// The user to receive the whisper message is selected in the userlist		
		if (whisperMode) {
			
			// include selected users list
			List<String> selectedUsers = usrListField.getSelectedValuesList();
			
			// 귓 메시지 출력
			this.msg("[귓]나 to " + Arrays.deepToString(selectedUsers.toArray()) + " > " + msg, true);
			
			sendData = new Data(curID, 4, msg, selectedUsers);
			this.ct.send(sendData);
			
			// if whisperMode sets false, written message will be sent all connected users
		} else {
			
			sendData = new Data(curID, 1, this.txtField.getText());
			this.ct.send(sendData);
			
		}
	}
	/* ========== end of define methods ========== */
	
	/* ========== getter and setter ========== */
	// flag 변수를 int 로 해야 하나??
	public void setUsers(String id, boolean flag) {
		// true 면 객체 추가, 아니면 객체 삭제
		if (flag) {
			this.users.add(id);
		} else {
			this.users.remove(id);
		}
	}
	
	public Vector<String> getUsers() {
		return this.users;
	}
	/* ========== end of getter and setter ========== */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChattClient frame = new ChattClient();
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
	// constructor
	public ChattClient() {
		setTitle("LET'S CHATT - USER");
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
			// 일단 서버주소를 호스트에서 얻어와 생성자에서 세팅
			InetAddress inetd = InetAddress.getLocalHost();
			serverIP = inetd.getHostAddress();
			this.serverAddr.setText(this.serverIP);
		} catch (Exception ex) {
			ex.printStackTrace();
			this.msg("네트워크 에러 발생", false);
		}
		
		btnDisconnect.setEnabled(false);
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBackground(new Color(176, 224, 230));
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			panel.setPreferredSize(new Dimension(10, 40));
			panel.add(getLblServerIP());
			panel.add(getServerAddr());
			panel.add(getLblID());
			panel.add(getId());
			panel.add(getLblPwd());
			panel.add(getPassword());
			panel.add(getBtnSetting());
			panel.add(getBtnConnect());
			panel.add(getBtnDisconnect());
		}
		return panel;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setPreferredSize(new Dimension(10, 40));
			panel_1.setLayout(new BorderLayout(0, 0));
			panel_1.add(getBtnFileSend(), BorderLayout.WEST);
			panel_1.add(getTxtField(), BorderLayout.CENTER);
			panel_1.add(getPanel_2(), BorderLayout.EAST);
		}
		return panel_1;
	}
	private JButton getBtnFileSend() {
		if (btnFileSend == null) {
			btnFileSend = new JButton("FILESEND");
		}
		return btnFileSend;
	}
	private JTextField getTxtField() {
		if (txtField == null) {
			txtField = new JTextField();
			txtField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent ke) {
					if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
						sendMsg();
						txtField.setText("");
						txtField.requestFocus();
					}
				}
			});
			txtField.setColumns(10);
		}
		return txtField;
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
				public void actionPerformed(ActionEvent e) {
					sendMsg();
					txtField.setText("");
					txtField.requestFocus();
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
	private JButton getBtnConnect() {
		if (btnConnect == null) {
			btnConnect = new JButton("CONNECT");
			btnConnect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					connectServer();
				}
			});
			btnConnect.setFont(new Font("굴림", Font.BOLD, 11));
			btnConnect.setPreferredSize(new Dimension(95, 30));
		}
		return btnConnect;
	}
	private JButton getBtnDisconnect() {
		if (btnDisconnect == null) {
			btnDisconnect = new JButton("DISCONNECT");
			btnDisconnect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					disconnectServer();
				}
			});
			btnDisconnect.setFont(new Font("굴림", Font.BOLD, 11));
			btnDisconnect.setPreferredSize(new Dimension(120, 30));
		}
		return btnDisconnect;
	}
	private JLabel getPassword() {
		if (password == null) {
			password = new JLabel("NULL");
			password.setPreferredSize(new Dimension(60, 30));
		}
		return password;
	}
	private JLabel getLblPwd() {
		if (lblPwd == null) {
			lblPwd = new JLabel("PASSWORD :");
			lblPwd.setHorizontalAlignment(SwingConstants.CENTER);
			lblPwd.setPreferredSize(new Dimension(85, 30));
		}
		return lblPwd;
	}
	JLabel getId() {
		if (id == null) {
			id = new JLabel("NULL");
			id.setPreferredSize(new Dimension(50, 30));
		}
		return id;
	}
	private JLabel getLblID() {
		if (lblID == null) {
			lblID = new JLabel("ID :");
			lblID.setHorizontalAlignment(SwingConstants.CENTER);
			lblID.setPreferredSize(new Dimension(30, 30));
		}
		return lblID;
	}
	private JLabel getServerAddr() {
		if (serverAddr == null) {
			serverAddr = new JLabel("NULL");
			serverAddr.setPreferredSize(new Dimension(80, 30));
		}
		return serverAddr;
	}
	private JLabel getLblServerIP() {
		if (lblServerIP == null) {
			lblServerIP = new JLabel("SERVER ADDR :");
			lblServerIP.setHorizontalAlignment(SwingConstants.CENTER);
			lblServerIP.setPreferredSize(new Dimension(100, 30));
		}
		return lblServerIP;
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
	JList getUsrListField() {
		if (usrListField == null) {
			usrListField = new JList();
		}
		return usrListField;
	}
	private JTextArea getMainTextArea() {
		if (mainTextArea == null) {
			mainTextArea = new JTextArea();
			mainTextArea.addMouseListener(new MouseAdapter() {
				// mainTextArea clear
				@Override
				public void mouseClicked(MouseEvent me) {
					if (me.getButton() == MouseEvent.BUTTON3) {
						if (JOptionPane.showConfirmDialog(ChattClient.this,
								"clear this area?") == JOptionPane.OK_OPTION) {
							mainTextArea.setText("");
						}
					}
				}
			});
			mainTextArea.setEditable(false);
		}
		return mainTextArea;
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
	private JButton getBtnSetting() {
		if (btnSetting == null) {
			btnSetting = new JButton("SETTING");
			btnSetting.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String setID = JOptionPane.showInputDialog(ChattClient.this, "ID 를 입력하세요.");
					id.setText(setID);
					curID = setID;
					String setPWD = JOptionPane.showInputDialog(ChattClient.this, "Password 를 입력하세요.");
					password.setText(setPWD);
				}
			});
			btnSetting.setFont(new Font("굴림", Font.BOLD, 11));
			btnSetting.setPreferredSize(new Dimension(90, 30));
		}
		return btnSetting;
	}
}
