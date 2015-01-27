package controller;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Class used to make thread control of GUI and the execution of the program, more easy. 
 * @author Alysson Milanez and Dennis Sousa.
 *
 */
public class ThreadExecutingProgram extends Thread {
	
	private JFrame main;
	private Controller controller;

	/**
	 * Initialize the thread.
	 * @param srcFolder The source folder where program will operate.
	 * @param libFolder The library folder which program execution depends.
	 * @param timeout The time(in seconds) where test will be generated.
	 */
	public ThreadExecutingProgram(JFrame main, Controller controller) {
		this.main = main;
		this.controller = controller;
	}
	
	@Override
	public void run() {
		try {
			controller.prepareToDetectPhase();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this.main, e.getMessage());
			e.printStackTrace();
		}
	}

}
