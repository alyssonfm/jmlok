package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.output.ByteArrayOutputStream;

import utils.Constants;
import utils.FileUtil;
import controller.Controller;
import detect.Detect;
import detect.DetectEvent;
import detect.DetectListener;

/**
 * Screen shown after Detection phase, executed by the program. An advisor screen
 * were it just shows that detection occurred with no problems.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class DetectionScreenAdvisorFrame extends JFrame {

	private static final long serialVersionUID = 7840357361061019283L;
	private static final int WIDTH = 700;
	private static final int HEIGHT = 400;
	private JPanel contentPane;
	private boolean detectionSuceeded;
	private SpringLayout springLayout;
	private ByteArrayOutputStream baos;
	private JTextArea textArea;
	private JButton btnNexts;
	private JProgressBar progressBar;
	private JLabel lblDetectionPhaseIs;
	private SwingWorker<Object, Object> worker;
	protected int seconds = 1000;
	protected double velNumPerSec = 2;
	
	/**
	 * Create the frame.
	 */
	public DetectionScreenAdvisorFrame(Detect d, ByteArrayOutputStream caos) {
		FileUtil.setUIFont(new javax.swing.plaf.FontUIResource(Constants.MAIN_FONT));
		
		setDetectionSuceeded(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 458);
		setMinimumSize(new Dimension(DetectionScreenAdvisorFrame.WIDTH, DetectionScreenAdvisorFrame.HEIGHT));
		
		List<Image> icons = new ArrayList<Image>();
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(16x16).jpg")).getImage());
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(32x32).jpg")).getImage());
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(64x64).jpg")).getImage());
		icons.add((Image) new ImageIcon(getClass().getResource("images/logo(128x128).jpg")).getImage());
		setIconImages(icons);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		springLayout = new SpringLayout();
		contentPane.setLayout(springLayout);
		
		lblDetectionPhaseIs = new JLabel("Current Stage: " + "Creating Directories");
		springLayout.putConstraint(SpringLayout.NORTH, lblDetectionPhaseIs, 10, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, lblDetectionPhaseIs, 405, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, lblDetectionPhaseIs, 30, SpringLayout.WEST, contentPane);
		lblDetectionPhaseIs.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblDetectionPhaseIs);
		
		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 45, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 12, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -45, SpringLayout.SOUTH, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -12, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		btnNexts = new JButton("Nonconformances");
		springLayout.putConstraint(SpringLayout.NORTH, btnNexts, 0, SpringLayout.NORTH, lblDetectionPhaseIs);
		springLayout.putConstraint(SpringLayout.SOUTH, btnNexts, 0, SpringLayout.SOUTH, lblDetectionPhaseIs);
		springLayout.putConstraint(SpringLayout.EAST, btnNexts, -39, SpringLayout.EAST, contentPane);
		contentPane.add(btnNexts);
		btnNexts.setVisible(false);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		baos = caos;
		textArea.setEditable(false);
		
		progressBar = new JProgressBar(0, 100);
		springLayout.putConstraint(SpringLayout.NORTH, progressBar, 10, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 0, SpringLayout.WEST, scrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, progressBar, -10, SpringLayout.SOUTH, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, scrollPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);

		addListeners(d);
		startProgressWorker();
	}

	/**
	 * Restart progress worker. Stop worker, set progressBar value to a point, and
	 * then start progress worker to increase its value.
	 * @param progressNumberToSet Value to set progress.
	 */
	private void restartProgressWorker(int progressNumberToSet){
		stopProgress();
		progressBar.setValue(progressNumberToSet);
		startProgressWorker();
	}

	/**
	 * Stop progress of progress bar, useful when Error surges.
	 */
	private void stopProgress() {
		if(!worker.isDone())
			worker.cancel(true);
	}
	
	/**
	 * Put progress worker to action. The progress bar then keep increasing until the verge of current stage.
	 */
	private void startProgressWorker() {
		worker = new SwingWorker<Object, Object>() { 
		double velocity = velNumPerSec;
			@Override 
			protected Void doInBackground() throws Exception { 
				// Simulate doing something useful. 
				for (int i = 0; i < 20; i++) { 
					Thread.sleep((long) (seconds / velocity)); 
					int percent = progressBar.getValue();
					progressBar.setValue(percent + 1);
					velocity /= 2;
				} 
				return null; 
			} 
		}; 
		worker.execute();
	}

	/**
	 * Defines the function of the button that leaves the Frame.
	 */
	public void modifyButton() {
		if(isDetectionSuceeded()){
			btnNexts.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					callsCategorization();
				}
			});
		}else{
			btnNexts.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
						exit();
				}
			});
			btnNexts.setText("Exit");
		}
		btnNexts.setVisible(true);
	}

	/**
	 * Add listeners to Frame, to keep record of stages in Detect execution.
	 * @param d Detect object that will be listen by Frame.
	 */
	private void addListeners(Detect d) {
		d.addDetectListener(new DetectListener() {
			
			@Override
			public void detectGeneratedTestsWithRandoop(DetectEvent e) {
				textArea.setText(baos.toString());
				restartProgressWorker(80);
				lblDetectionPhaseIs.setText("Current Stage: " + "Executing Tests");
			}
			
			@Override
			public void detectExecutedTests(DetectEvent e) {
				textArea.setText(baos.toString());
				progressBar.setValue(100);
				lblDetectionPhaseIs.setText("Detection Phase finished.");
				modifyButton();
			}
			
			@Override
			public void detectCreatedDirectories(DetectEvent e) {
				textArea.setText(baos.toString());
				restartProgressWorker(20);
				lblDetectionPhaseIs.setText("Current Stage: " + "Compiling with Java");
			}
			
			@Override
			public void detectCompiledProjectWithJava(DetectEvent e) {
				textArea.setText(baos.toString());
				restartProgressWorker(40);
				lblDetectionPhaseIs.setText("Current Stage: " + "Compiling with JML");
			}
			
			@Override
			public void detectCompiledProjectWithJML(DetectEvent e) {
				textArea.setText(baos.toString());
				restartProgressWorker(60);
				lblDetectionPhaseIs.setText("Current Stage: " + "Generating Tests");
			}

			@Override
			public void detectErrorOnGeneratingTests(DetectEvent e) {
				textArea.setText(baos.toString());
				stopProgress();
				setDetectionSuceeded(false);
				modifyButton();
			}
		});
	}

	/**
	 * Close the Window.
	 */
	protected void exit() {
		setVisible(false);
	}

	/**
	 * Calls categorization Screen.
	 */
	protected void callsCategorization() {
		Controller.showCategorizationScreen();
		setVisible(false);
	}

	/**
	 * Verify if detection phase was succeeded.
	 * @return if detection phase was succeeded.
	 */
	public boolean isDetectionSuceeded() {
		return detectionSuceeded;
	}

	/**
	 * Set information if detection phase was succeeded.
	 * @param detectionSuceeded if detection phase was succeded.
	 */
	public void setDetectionSuceeded(boolean detectionSuceeded) {
		this.detectionSuceeded = detectionSuceeded;
	}
}
