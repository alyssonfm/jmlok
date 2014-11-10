package gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import utils.Constants;

/**
 * Main class to the GUI. Represents the first screen showed to JMLOK user.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 *
 */
public class Main extends JFrame {

	/**
	 * Creates the frame.
	 */
	private static final long serialVersionUID = 9142967374337903926L;
	
	private JPanel contentPane;
	private JLabel textFieldSrcFolder;
	private JLabel textFieldExtLibFolder;
	private String srcFolder = "";
	private String extFolder = "";
	private JTextField textFieldTime;
	private JButton btnRun;
	private JFileChooser dirSources;
	private JFileChooser dirLibs;
	private Task task;
	public boolean done;
	// Parameters for window size.
	private final int WIDTH = 750;
	private final int HEIGHT = 210;
	
	/**
	 * Display the frame. Initialize the program.
	 * @param args from command line(non-used).
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * An class used for thread control, to make GUI changes an the execution of the program
	 * fluid, with response for user.
	 * @author Alysson Milanez and Dennis Sousa.
	 *
	 */
    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            //Initialize progress property.
            setProgress(0);
            runningProgram();
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
        	//Tell progress listener to stop updating progress bar.
            done = true;
            btnRun.setEnabled(true);
            setCursor(null); //turn off the wait cursor
        }
    }
	
	/**
	 * Create the frame.
	 */
	public Main() {
		// Set window options.
		setTitle("JMLOK 2.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 219);
		setMinimumSize(new Dimension(this.WIDTH, this.HEIGHT));
		setMaximumSize(new Dimension(1450, this.HEIGHT));
		setMaximizedBounds(new Rectangle(1450, this.HEIGHT));
		
		List<Image> icons = new ArrayList<Image>();
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(16x16).jpg")).getImage());
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(32x32).jpg")).getImage());
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(64x64).jpg")).getImage());
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(128x128).jpg")).getImage());
		setIconImages(icons);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout springLayout = new SpringLayout();
		contentPane.setLayout(springLayout);

		// Initialize FileChooser used in this Frame.
		dirLibs = new JFileChooser();
		dirSources = new JFileChooser();
		
		JLabel lblExternalLibFolder = new JLabel("Choose external libraries folder");
		springLayout.putConstraint(SpringLayout.EAST, lblExternalLibFolder, 350, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, lblExternalLibFolder, 50, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, lblExternalLibFolder, 20, SpringLayout.WEST, contentPane);
		lblExternalLibFolder.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblExternalLibFolder);

		JLabel lblSrcFolder = new JLabel("Choose source folder  ");
		springLayout.putConstraint(SpringLayout.NORTH, lblSrcFolder, -70, SpringLayout.SOUTH, lblExternalLibFolder);
		springLayout.putConstraint(SpringLayout.WEST, lblSrcFolder, 0, SpringLayout.WEST, lblExternalLibFolder);
		springLayout.putConstraint(SpringLayout.SOUTH, lblSrcFolder, -6, SpringLayout.NORTH, lblExternalLibFolder);
		springLayout.putConstraint(SpringLayout.EAST, lblSrcFolder, 0, SpringLayout.EAST, lblExternalLibFolder);
		lblSrcFolder.setHorizontalAlignment(SwingConstants.RIGHT);
		springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, lblSrcFolder, 0, SpringLayout.HORIZONTAL_CENTER, lblExternalLibFolder);
		lblSrcFolder.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblSrcFolder);
		
		textFieldSrcFolder = new JLabel();
		springLayout.putConstraint(SpringLayout.NORTH, textFieldSrcFolder, 5, SpringLayout.NORTH, lblSrcFolder);
		springLayout.putConstraint(SpringLayout.EAST, textFieldSrcFolder, -10, SpringLayout.EAST, contentPane);
		textFieldSrcFolder.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(textFieldSrcFolder);
		
		textFieldExtLibFolder = new JLabel();
		springLayout.putConstraint(SpringLayout.NORTH, textFieldExtLibFolder, -2, SpringLayout.NORTH, lblExternalLibFolder);
		springLayout.putConstraint(SpringLayout.WEST, textFieldExtLibFolder, 520, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, textFieldExtLibFolder, -10, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, textFieldSrcFolder, 0, SpringLayout.WEST, textFieldExtLibFolder);
		textFieldExtLibFolder.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(textFieldExtLibFolder);
		
		JButton btnBrowseSrcFolder = new JButton("Browse");
		springLayout.putConstraint(SpringLayout.NORTH, btnBrowseSrcFolder, 2, SpringLayout.NORTH, lblSrcFolder);
		springLayout.putConstraint(SpringLayout.WEST, btnBrowseSrcFolder, 6, SpringLayout.EAST, lblSrcFolder);
		btnBrowseSrcFolder.setFont(new Font("Verdana", Font.BOLD, 18));
		btnBrowseSrcFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseSrcFolder();
				
			}
		});
		contentPane.add(btnBrowseSrcFolder);
		
		JButton btnBrowseExtLibFolder = new JButton("Browse");
		springLayout.putConstraint(SpringLayout.NORTH, btnBrowseExtLibFolder, -5, SpringLayout.NORTH, lblExternalLibFolder);
		springLayout.putConstraint(SpringLayout.EAST, btnBrowseExtLibFolder, 116, SpringLayout.EAST, lblExternalLibFolder);
		springLayout.putConstraint(SpringLayout.EAST, btnBrowseSrcFolder, 0, SpringLayout.EAST, btnBrowseExtLibFolder);
		springLayout.putConstraint(SpringLayout.WEST, btnBrowseExtLibFolder, 6, SpringLayout.EAST, lblExternalLibFolder);
		btnBrowseExtLibFolder.setFont(new Font("Verdana", Font.BOLD, 18));
		btnBrowseExtLibFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseExtLibFolder();
			}
		});
		contentPane.add(btnBrowseExtLibFolder);
		
		JLabel lblTime = new JLabel("Time to tests generation  ");
		springLayout.putConstraint(SpringLayout.NORTH, lblTime, 13, SpringLayout.SOUTH, lblExternalLibFolder);
		lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
		springLayout.putConstraint(SpringLayout.WEST, lblTime, 0, SpringLayout.WEST, lblExternalLibFolder);
		springLayout.putConstraint(SpringLayout.EAST, lblTime, 0, SpringLayout.EAST, lblExternalLibFolder);
		lblTime.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblTime);
		
		textFieldTime = new JTextField("");
		springLayout.putConstraint(SpringLayout.NORTH, textFieldTime, 11, SpringLayout.SOUTH, lblExternalLibFolder);
		springLayout.putConstraint(SpringLayout.WEST, textFieldTime, 0, SpringLayout.WEST, btnBrowseExtLibFolder);
		springLayout.putConstraint(SpringLayout.EAST, textFieldTime, 0, SpringLayout.EAST, btnBrowseExtLibFolder);
		textFieldTime.setFont(new Font("Verdana", Font.PLAIN, 18));
		contentPane.add(textFieldTime);
		textFieldTime.setColumns(6);
		
		btnRun = new JButton("Run");
		springLayout.putConstraint(SpringLayout.WEST, btnRun, -274, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.SOUTH, btnRun, -10, SpringLayout.SOUTH, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, btnRun, -184, SpringLayout.EAST, contentPane);
		btnRun.setFont(new Font("Verdana", Font.BOLD, 18));
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textFieldSrcFolder.getText().equals(""))
					JOptionPane.showMessageDialog(Main.this, "Choose the source folder before running.");
				else if(!textFieldTime.getText().matches("\\d+"))
					JOptionPane.showMessageDialog(Main.this, "Please insert a valid number of seconds.");
				else if(!System.getProperty("os.name").contains("Windows") && !(System.getenv("CLASSPATH").contains("randoop.jar")))
					JOptionPane.showMessageDialog(Main.this, "The file randoop.jar was not configured using JMLOKSetup. Please runs JMLOKSetup again and put randoop.jar into your choosen ext lib folder.");
				else{	
					btnRun.setEnabled(false);
			        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			        //Instances of javax.swing.SwingWorker are not reusable, so
			        //we create new instances as needed.
			        task = new Task();
			        task.execute();
				}
			}
		});
		contentPane.add(btnRun);
		
		JLabel lblSeconds = new JLabel("seconds");
		springLayout.putConstraint(SpringLayout.NORTH, lblSeconds, 0, SpringLayout.NORTH, lblTime);
		springLayout.putConstraint(SpringLayout.WEST, lblSeconds, 520, SpringLayout.WEST, contentPane);
		lblSeconds.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblSeconds);
		
		JButton btnClean = new JButton("Clean");
		springLayout.putConstraint(SpringLayout.SOUTH, btnClean, -10, SpringLayout.SOUTH, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, btnClean, -51, SpringLayout.EAST, contentPane);
		btnClean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetProgram();		
			}
		});
		btnClean.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(btnClean);
		
		BufferedImage imgLabel = null;
		try {
			imgLabel = ImageIO.read(Main.class.getResource("/gui/images/folder.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Image dFolderImg = imgLabel.getScaledInstance(24, 24, Image.SCALE_SMOOTH);

		JLabel lblIconFolder1 = new JLabel("");
		lblIconFolder1.setHorizontalAlignment(SwingConstants.CENTER);
		lblIconFolder1.setIcon(new ImageIcon(dFolderImg));		
		springLayout.putConstraint(SpringLayout.NORTH, lblIconFolder1, 0, SpringLayout.NORTH, btnBrowseSrcFolder);
		springLayout.putConstraint(SpringLayout.WEST, lblIconFolder1, 2, SpringLayout.EAST, btnBrowseExtLibFolder);
		springLayout.putConstraint(SpringLayout.SOUTH, lblIconFolder1, 0, SpringLayout.SOUTH, btnBrowseSrcFolder);
		springLayout.putConstraint(SpringLayout.EAST, lblIconFolder1, 2, SpringLayout.WEST, textFieldExtLibFolder);
		contentPane.add(lblIconFolder1);
		
		JLabel lblIconFolder2 = new JLabel("");
		lblIconFolder2.setFont(new Font("Dialog", Font.BOLD, 8));
		lblIconFolder2.setHorizontalAlignment(SwingConstants.CENTER);
		lblIconFolder2.setIcon(new ImageIcon(dFolderImg));
		springLayout.putConstraint(SpringLayout.NORTH, lblIconFolder2, 0, SpringLayout.NORTH, btnBrowseExtLibFolder);
		springLayout.putConstraint(SpringLayout.WEST, lblIconFolder2, 2, SpringLayout.EAST, btnBrowseExtLibFolder);
		springLayout.putConstraint(SpringLayout.SOUTH, lblIconFolder2, 0, SpringLayout.SOUTH, btnBrowseExtLibFolder);
		springLayout.putConstraint(SpringLayout.EAST, lblIconFolder2, 2, SpringLayout.WEST, textFieldExtLibFolder);
		contentPane.add(lblIconFolder2);

		try {
			imgLabel = ImageIO.read(Main.class.getResource("/gui/images/time.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Image dTimeImg = imgLabel.getScaledInstance(24, 24, Image.SCALE_SMOOTH);

		
		JLabel labelTimeIcon = new JLabel("");
		springLayout.putConstraint(SpringLayout.NORTH, labelTimeIcon, 0, SpringLayout.NORTH, textFieldTime);
		springLayout.putConstraint(SpringLayout.WEST, labelTimeIcon, 2, SpringLayout.EAST, textFieldTime);
		springLayout.putConstraint(SpringLayout.SOUTH, labelTimeIcon, 0, SpringLayout.SOUTH, textFieldTime);
		springLayout.putConstraint(SpringLayout.EAST, labelTimeIcon, 2, SpringLayout.WEST, lblSeconds);
		labelTimeIcon.setHorizontalAlignment(SwingConstants.CENTER);
		labelTimeIcon.setFont(new Font("Dialog", Font.BOLD, 8));
		labelTimeIcon.setIcon(new ImageIcon(dTimeImg));
		contentPane.add(labelTimeIcon);
	}

	/**
	 * Execute the program, after some variables are correctly initialized.
	 */
	protected void runningProgram() {
		// showInterestingMessage();
		String extLibFolder = extFolder;
		String time = textFieldTime.getText();
		if(extLibFolder.equals("")) {
			if(System.getProperty("os.name").contains("Windows"))
				extLibFolder = Constants.JMLC_LIB;
			else
				extLibFolder = System.getenv("USER_CLASSPATH_LIB");
		}if(time.equals("")){
			time = "10";
		}
		ThreadExecutingProgram t = new ThreadExecutingProgram(this, srcFolder, extLibFolder, time);
		t.run();
	}

	/**
	 * Resets the main screen, leaving all fields blanked.
	 */
	protected void resetProgram() {
		textFieldSrcFolder.setText("");
		textFieldExtLibFolder.setText("");
		textFieldTime.setText("");
	}

	/**
	 * Make user sets the library folder.
	 */
	protected void browseExtLibFolder() {
		dirLibs.setApproveButtonText("Select");
		dirLibs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirLibs.setCurrentDirectory(new File(jarPath()));
		if (dirLibs.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
			extFolder = dirLibs.getSelectedFile().getAbsolutePath();
			int begin = extFolder.lastIndexOf(File.separator);
			textFieldExtLibFolder.setText(extFolder.substring(begin + 1));
		}
	}

	/**
	 * Make user sets the source folder.
	 */
	protected void browseSrcFolder() {
		dirSources.setApproveButtonText("Select");
		dirSources.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirSources.setCurrentDirectory(new File(jarPath()));
		if (dirSources.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
			srcFolder = dirSources.getSelectedFile().getAbsolutePath();
			int begin = srcFolder.lastIndexOf(File.separator);
			textFieldSrcFolder.setText(srcFolder.substring(begin + 1));
		}
	}
	
	/**
	 * Return the path name where running Jar was found to use.
	 * @return the path name where running Jar was found to use.
	 */
	private String jarPath() {
		Path path = null;
		try {
			path = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path.getParent().toString();
	}
	
}
