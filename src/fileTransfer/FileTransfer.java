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
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FileTransfer extends JFrame {

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JButton btnNewButton;
	private JPanel panel_3;
	private JPanel panel_4;
	private JPanel panel_5;
	private JPanel panel_6;
	private JPanel panel_7;
	private JPanel panel_8;
	private JPanel panel_9;
	private JPanel panel_10;
	private JPanel panel_11;
	private JPanel panel_12;
	private JPanel panel_13;
	private JPanel panel_14;
	private JPanel panel_15;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel label;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel lblNewLabel_4;
	private JProgressBar totBar;
	private JLabel label_4;
	private JButton btnsendFile;
	private JScrollPane scrollPane_1;
	private JList list;
	private JScrollPane scrollPane;
	private JList list_1;

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
	public FileTransfer() {
		setMinimumSize(new Dimension(550, 650));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(getPanel(), BorderLayout.SOUTH);
		contentPane.add(getPanel_1(), BorderLayout.CENTER);
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
			panel_2.add(getBtnNewButton());
			panel_2.add(getBtnsendFile());
		}
		return panel_2;
	}
	private JButton getBtnNewButton() {
		if (btnNewButton == null) {
			btnNewButton = new JButton("+ ADD FILE");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FileDialog fileDialog;
					fileDialog = new FileDialog(FileTransfer.this, "File Transfer", FileDialog.LOAD);
					fileDialog.setVisible(true);
				}
			});
			btnNewButton.setPreferredSize(new Dimension(100, 30));
		}
		return btnNewButton;
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
			panel_6.add(getPanel_7(), BorderLayout.NORTH);
			panel_6.add(getPanel_8(), BorderLayout.CENTER);
		}
		return panel_6;
	}
	private JPanel getPanel_7() {
		if (panel_7 == null) {
			panel_7 = new JPanel();
			panel_7.setPreferredSize(new Dimension(10, 130));
			panel_7.setLayout(new BorderLayout(0, 0));
			panel_7.add(getPanel_9(), BorderLayout.NORTH);
			panel_7.add(getPanel_10(), BorderLayout.SOUTH);
			panel_7.add(getPanel_11(), BorderLayout.CENTER);
		}
		return panel_7;
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
	private JPanel getPanel_9() {
		if (panel_9 == null) {
			panel_9 = new JPanel();
			panel_9.setBackground(Color.WHITE);
			panel_9.setPreferredSize(new Dimension(10, 50));
			panel_9.add(getLblNewLabel_4());
			panel_9.add(getTotBar());
			panel_9.add(getLabel_4());
		}
		return panel_9;
	}
	private JPanel getPanel_10() {
		if (panel_10 == null) {
			panel_10 = new JPanel();
			panel_10.setPreferredSize(new Dimension(10, 40));
			panel_10.setLayout(new GridLayout(1, 0, 0, 0));
			panel_10.add(getPanel_14());
			panel_10.add(getPanel_15());
		}
		return panel_10;
	}
	private JPanel getPanel_11() {
		if (panel_11 == null) {
			panel_11 = new JPanel();
			panel_11.setPreferredSize(new Dimension(10, 40));
			panel_11.setLayout(new GridLayout(1, 0, 0, 0));
			panel_11.add(getPanel_12());
			panel_11.add(getPanel_13());
		}
		return panel_11;
	}
	private JPanel getPanel_12() {
		if (panel_12 == null) {
			panel_12 = new JPanel();
			panel_12.setBackground(Color.WHITE);
			panel_12.add(getLblNewLabel());
			panel_12.add(getLblNewLabel_1());
		}
		return panel_12;
	}
	private JPanel getPanel_13() {
		if (panel_13 == null) {
			panel_13 = new JPanel();
			panel_13.setBackground(Color.WHITE);
			panel_13.add(getLabel_2());
			panel_13.add(getLabel_3());
		}
		return panel_13;
	}
	private JPanel getPanel_14() {
		if (panel_14 == null) {
			panel_14 = new JPanel();
			panel_14.setBackground(Color.WHITE);
			panel_14.add(getLblNewLabel_2());
			panel_14.add(getLblNewLabel_3());
		}
		return panel_14;
	}
	private JPanel getPanel_15() {
		if (panel_15 == null) {
			panel_15 = new JPanel();
			panel_15.setBackground(Color.WHITE);
			panel_15.add(getLabel());
			panel_15.add(getLabel_1());
		}
		return panel_15;
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
	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("파일 수 : ");
		}
		return lblNewLabel;
	}
	private JLabel getLblNewLabel_1() {
		if (lblNewLabel_1 == null) {
			lblNewLabel_1 = new JLabel("New label");
		}
		return lblNewLabel_1;
	}
	private JLabel getLblNewLabel_2() {
		if (lblNewLabel_2 == null) {
			lblNewLabel_2 = new JLabel("전송 용량 : ");
		}
		return lblNewLabel_2;
	}
	private JLabel getLblNewLabel_3() {
		if (lblNewLabel_3 == null) {
			lblNewLabel_3 = new JLabel("New label");
		}
		return lblNewLabel_3;
	}
	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("전송 속도 : ");
		}
		return label;
	}
	private JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel("New label");
		}
		return label_1;
	}
	private JLabel getLabel_2() {
		if (label_2 == null) {
			label_2 = new JLabel("전송 시간 : ");
		}
		return label_2;
	}
	private JLabel getLabel_3() {
		if (label_3 == null) {
			label_3 = new JLabel("New label");
		}
		return label_3;
	}
	private JLabel getLblNewLabel_4() {
		if (lblNewLabel_4 == null) {
			lblNewLabel_4 = new JLabel("TOTAL : ");
			lblNewLabel_4.setPreferredSize(new Dimension(55, 30));
		}
		return lblNewLabel_4;
	}
	private JProgressBar getTotBar() {
		if (totBar == null) {
			totBar = new JProgressBar();
			totBar.setPreferredSize(new Dimension(380, 15));
		}
		return totBar;
	}
	private JLabel getLabel_4() {
		if (label_4 == null) {
			label_4 = new JLabel("100 %");
		}
		return label_4;
	}
	private JButton getBtnsendFile() {
		if (btnsendFile == null) {
			btnsendFile = new JButton("↑ SEND FILE");
			btnsendFile.setPreferredSize(new Dimension(110, 30));
		}
		return btnsendFile;
	}
	private JScrollPane getScrollPane_1_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setPreferredSize(new Dimension(120, 2));
			scrollPane_1.setColumnHeaderView(getBtnNewButton_2());
			scrollPane_1.setViewportView(getList());
		}
		return scrollPane_1;
	}
	private JList getList() {
		if (list == null) {
			list = new JList();
		}
		return list;
	}
	private JScrollPane getScrollPane_2() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setPreferredSize(new Dimension(350, 2));
			scrollPane.setColumnHeaderView(getBtnNewButton_1());
			scrollPane.setViewportView(getList_1());
		}
		return scrollPane;
	}
	private JList getList_1() {
		if (list_1 == null) {
			list_1 = new JList();
		}
		return list_1;
	}
}
