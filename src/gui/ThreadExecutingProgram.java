package gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import utils.Constants;
import controller.Controller;

/**
 * Class used to make thread control of GUI and the execution of the program, more easy. 
 * @author Alysson Milanez and Dennis Sousa.
 *
 */
public class ThreadExecutingProgram extends Thread {
	
	private String srcFolder;
	private String libFolder;
	private String timeout;
	private JFrame main;

	/**
	 * Initialize the thread.
	 * @param srcFolder The source folder where program will operate.
	 * @param libFolder The library folder which program execution depends.
	 * @param timeout The time(in seconds) where test will be generated.
	 */
	public ThreadExecutingProgram(JFrame main, String srcFolder, String libFolder, String timeout) {
		this.srcFolder = srcFolder;
		this.libFolder = libFolder;
		this.timeout = timeout;
		this.main = main;
	}
	
	@Override
	public void run() {
		try {
			Controller.prepareToDetectPhase(Constants.JMLC_COMPILER, srcFolder, libFolder, timeout);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this.main, e.getMessage());
			e.printStackTrace();
		}
	}

}
