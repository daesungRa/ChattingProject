package fileTransfer;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import chatt.ChattClient;
import chatt.Data;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.Socket;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class FileTransfer extends JFrame implements Runnable {

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JButton btnAddFile;
	private JPanel panel_3;
	private JPanel panel_4;
	private JPanel panel_5;
	private JPanel panel_6;
	private JPanel panel_8;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JButton btnSendFile;
	private JScrollPane scrollPane_1;
	private JList<String> fileListThroughput;
	private JScrollPane scrollPane;
	private JList<String> fileList;

	// 현재 파일전송 창을 호출한 ChattClient 로딩
	private ChattClient cc;
	
	// 파일서버을 위한 포트번호
	private int port;

	// 리스트업 할 파일경로 + 파일명
	// 스트림을 통해 서버에 실제 송신할때 사용
	private Vector<String> files = new Vector<String>();

	// 전송할 파일명 목록(파일명만)
	// 서버에 요청할때만 사용
	public Vector<String> sendFiles = new Vector<String>();

	@Override
	public void run() {
		try {
			// 임시로 생성된 파일서버와 연동
			Socket socket = new Socket(this.cc.getId().getText().trim(), port);
			
			// 생성된 파일전송 소켓을 기반으로 목록에 있는 파일을 전송하는 스레드를 각각 생성
			for (int i = 0; i < sendFiles.size(); i++) {
				TransferThread tt = new TransferThread(FileTransfer.this, this.files.get(i), socket);
				tt.setDaemon(true);
				tt.join();
				tt.start();
			}
		} catch (Exception ex) { }
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileTransfer frame = new FileTransfer();
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
	public FileTransfer() {
		setMinimumSize(new Dimension(550, 350));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(getPanel(), BorderLayout.SOUTH);
		contentPane.add(getPanel_1(), BorderLayout.CENTER);
	}

	public FileTransfer(ChattClient cc) {
		this();
		this.cc = cc;
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setPreferredSize(new Dimension(10, 80));
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getPanel_2(), BorderLayout.SOUTH);
		}
		return panel;
	}

	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setLayout(new BorderLayout(0, 0));
			panel_1.add(getPanel_3(), BorderLayout.WEST);
			panel_1.add(getPanel_4(), BorderLayout.NORTH);
			panel_1.add(getPanel_5(), BorderLayout.EAST);
			panel_1.add(getPanel_6(), BorderLayout.CENTER);
		}
		return panel_1;
	}

	private JPanel getPanel_2() {
		if (panel_2 == null) {
			panel_2 = new JPanel();
			panel_2.setPreferredSize(new Dimension(10, 60));
			panel_2.add(getBtnAddFile());
			panel_2.add(getBtnSendFile());
		}
		return panel_2;
	}

	private JButton getBtnAddFile() {
		if (btnAddFile == null) {
			btnAddFile = new JButton("+ ADD FILE");
			btnAddFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					FileDialog fileDialog;
					fileDialog = new FileDialog(FileTransfer.this, "File Transfer", FileDialog.LOAD);
					fileDialog.setVisible(true);

					// 파일 경로
					String filePath = fileDialog.getDirectory();
					// 파일명
					String fileName = fileDialog.getFile();

					// 선택된 파일이 null 이거나 입력된 파일경로에 파일이 존재하지 않을 경우 == false, else 문이 실행된다
					if (fileName != null && new File(filePath + fileName).exists()) {
						// 전송할 파일명을 벡터 sendFiles 에 저장
						sendFiles.add(fileName);

						// 리스트업 할 파일명(경로 + 파일)을 벡터 files 에 저장
						files.add(filePath + fileName);

						// 갱신된 files 목록을 fileList 에 적용
						fileList.setListData(files);
					} else {
						JOptionPane.showMessageDialog(FileTransfer.this, "선택된 파일이 없습니다.", "File Not Found", 2);
					}
				}
			});
			btnAddFile.setPreferredSize(new Dimension(100, 30));
		}
		return btnAddFile;
	}

	private JPanel getPanel_3() {
		if (panel_3 == null) {
			panel_3 = new JPanel();
			panel_3.setPreferredSize(new Dimension(15, 10));
		}
		return panel_3;
	}

	private JPanel getPanel_4() {
		if (panel_4 == null) {
			panel_4 = new JPanel();
			panel_4.setPreferredSize(new Dimension(10, 15));
		}
		return panel_4;
	}

	private JPanel getPanel_5() {
		if (panel_5 == null) {
			panel_5 = new JPanel();
			panel_5.setPreferredSize(new Dimension(15, 10));
		}
		return panel_5;
	}

	private JPanel getPanel_6() {
		if (panel_6 == null) {
			panel_6 = new JPanel();
			panel_6.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel_6.setLayout(new BorderLayout(0, 0));
			panel_6.add(getPanel_8(), BorderLayout.CENTER);
		}
		return panel_6;
	}

	private JPanel getPanel_8() {
		if (panel_8 == null) {
			panel_8 = new JPanel();
			panel_8.setLayout(new BorderLayout(0, 0));
			panel_8.add(getScrollPane_1_1(), BorderLayout.EAST);
			panel_8.add(getScrollPane_2(), BorderLayout.CENTER);
		}
		return panel_8;
	}

	private JButton getBtnNewButton_1() {
		if (btnNewButton_1 == null) {
			btnNewButton_1 = new JButton("FILE NAME");
		}
		return btnNewButton_1;
	}

	private JButton getBtnNewButton_2() {
		if (btnNewButton_2 == null) {
			btnNewButton_2 = new JButton("THROUGHPUT");
		}
		return btnNewButton_2;
	}

	private JButton getBtnSendFile() {
		if (btnSendFile == null) {
			btnSendFile = new JButton("↑ SEND FILE");
			btnSendFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					// 11 번 커맨드를 포함한 파일전송요청 Data 객체를 cc 와 연결된 서버스레드로 전송
					// 전송하기 위한 파일명 목록(Vector)도 포함
					Data sendData = new Data(cc.getId().getText().trim(), 11, "", sendFiles);
					cc.ct.send(sendData);
				}
			});
			btnSendFile.setPreferredSize(new Dimension(110, 30));
		}
		return btnSendFile;
	}

	private JScrollPane getScrollPane_1_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setPreferredSize(new Dimension(120, 2));
			scrollPane_1.setColumnHeaderView(getBtnNewButton_2());
			scrollPane_1.setViewportView(getFileListThroughput());
		}
		return scrollPane_1;
	}

	private JList<String> getFileListThroughput() {
		if (fileListThroughput == null) {
			fileListThroughput = new JList<String>();
		}
		return fileListThroughput;
	}

	private JScrollPane getScrollPane_2() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setPreferredSize(new Dimension(350, 2));
			scrollPane.setColumnHeaderView(getBtnNewButton_1());
			scrollPane.setViewportView(getFileList());
		}
		return scrollPane;
	}

	private JList<String> getFileList() {
		if (fileList == null) {
			fileList = new JList<String>();
		}
		return fileList;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
