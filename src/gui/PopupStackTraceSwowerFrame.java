package gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import utils.Constants;
import utils.FileUtil;

/**
 * Frame that details stack trace information of nonconformances.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class PopupStackTraceSwowerFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	// Set variables to adjust window size.
	private static final int WIDTH = 650;
	private static final int HEIGHT = 470;
	
	/**
	 * Create the frame.
	 */
	public PopupStackTraceSwowerFrame(List<String> list) {
		// Set default font, to a bigger size. 
		FileUtil.setUIFont(new javax.swing.plaf.FontUIResource(Constants.MAIN_FONT));
		
		// Set layout.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, WIDTH, HEIGHT);
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		
		List<Image> icons = new ArrayList<Image>();
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(16x16).jpg")).getImage());
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(32x32).jpg")).getImage());
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(64x64).jpg")).getImage());
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(128x128).jpg")).getImage());
		setIconImages(icons);
		
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		
		// Configure exit button.
		JButton btnNewButton = new JButton("Exit");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, btnNewButton, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnNewButton, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(btnNewButton);
		
		// Configure stack trace label.
		JLabel lblStackTrace = new JLabel("Stack Trace");
		springLayout.putConstraint(SpringLayout.WEST, lblStackTrace, 16, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, lblStackTrace, 10, SpringLayout.NORTH, getContentPane());
		getContentPane().add(lblStackTrace);
		
		// Configure text area for stack trace info.
		JScrollPane scrollPane = new JScrollPane();		
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, lblStackTrace);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 16, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -13, SpringLayout.NORTH, btnNewButton);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -27, SpringLayout.EAST, getContentPane());
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		getContentPane().add(scrollPane);
		// Set text for stack trace info.
		textArea.setText(getStackTraceString(list));
		textArea.setEditable(false);
	}
	
	/**
	 * Exit function, independent for other frames.
	 */
	private void exit() {
		this.setVisible(false);
	}

	/**
	 * Get list containing names for class, in a calling order of the Exception launch.
	 * @param list The list containing names for class, in a calling order of the Exception launch.
	 * @return the text informing a resume from stack trace list showed by Java Exception.
	 */
	private String getStackTraceString(List<String> list) {
		String toShow = "";
		toShow += "Error appeared in " + list.get(0) + "\n";
		for (int i = 1; i < list.size(); i++) {
			toShow += "----> at " + list.get(i) + "\n";
		}
		return toShow;
	}
	
}
