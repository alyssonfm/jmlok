package controller;

import gui.CategorizationScreenAdvisorFrame;
import gui.DetectionScreenAdvisorFrame;
import gui.ThreadExecutingProgram;
import gui.ViewNonconformances;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.output.ByteArrayOutputStream;

import utils.commons.ClassPathHacker;
import utils.commons.Constants;
import utils.commons.ContractAwareCompiler;
import utils.commons.FileUtil;
import utils.commons.OperatingSystem;
import utils.datastructure.Nonconformance;
import categorize.Categorize;
import categorize.Examinator;
import detect.Detect;

public class Controller {
	private Set<Nonconformance> errors;
	private Set<Nonconformance> nonconformities;
	private ContractAwareCompiler compiler;
	private OperatingSystem os;
	private String srcFolder;
	private String extLibFolder;
	private String time;

	/**
	 * Constructor for Controller, it initializes the compiler and OS options for
	 * execution of JmlOk2 tool.
	 */
	public Controller(ContractAwareCompiler comp) {
		this.compiler = comp;
		chooseOS();
	}

	/**
	 * Check errors to avoid problems through the tool's process.
	 * 
	 * @param srcFolder
	 *            Input for source folder.
	 * @param time
	 *            Input for time in seconds.
	 * @throws Exception
	 *             If any information are incorrect for our instructions of use.
	 */
	public void checkProblemsWithInput(String srcFolder, String time)
			throws Exception {
		checkSrcFolderField(srcFolder);
		checkTimeField(time);
		checkRandoopRequirements();
	}

	/**
	 * Check whether source folder wasn't present on input.
	 * 
	 * @param srcFolder
	 *            Source folder input.
	 * @throws Exception
	 *             When source folder input is a empty or a null String.
	 */
	private void checkSrcFolderField(String srcFolder) throws Exception {
		if (srcFolder.equals("") || srcFolder == null)
			throw new Exception("Choose the source folder before running.");
	}

	/**
	 * Check a configuration problem on non-Windows OS, whereas randoop.jar
	 * needs to be in the CLASSPATH to be ran on Java.
	 * 
	 * @throws Exception
	 *             When Randoop is not in CLASSPATH if Windows isn't the OS.
	 */
	private void checkRandoopRequirements() throws Exception {
		if (this.os != OperatingSystem.WINDOWS
				&& !(System.getenv("CLASSPATH").contains("randoop.jar"))) {
			throw new Exception(
					"The file randoop.jar was not configured using JMLOKSetup. "
							+ "Please runs JMLOKSetup again and put randoop.jar into your choosen ext lib folder.");
		}
	}

	/**
	 * Uses a regex to check if the input of time contain only digits.
	 * 
	 * @param time
	 *            String informing time in seconds for tool execution.
	 * @throws TimeException
	 *             if time is not valid value.
	 */
	private void checkTimeField(String time) throws Exception {
		if (!(time.matches("\\d+")) && !time.equals(""))
			throw new Exception("Please insert a valid number of seconds.");
	}

	/**
	 * Turn all input values to correct form and then prepare Controller for
	 * execution.
	 * 
	 * @param srcFolder
	 *            Source Folder input.
	 * @param extLibFolder
	 *            External Libraries Folder input.
	 * @param time
	 *            Time input in seconds.
	 */
	public void validateInput(String srcFolder, String extLibFolder, String time) {
		this.srcFolder = srcFolder;
		this.extLibFolder = correctLibFolder(extLibFolder);
		this.time = correctTimeValue(time);
	}

	/**
	 * Check if the folder selected is valid and exist. If is a empty string,
	 * will use default values
	 * 
	 * @param extLibFolder
	 *            The value of libraries folder input.
	 * @return The default value for libraries folder or itself.
	 */
	private String correctLibFolder(String extLibFolder) {
		if (extLibFolder.equals("")) {
			if (this.os == OperatingSystem.WINDOWS)
				if (this.compiler == ContractAwareCompiler.JMLC) {
					extLibFolder = Constants.JMLC_LIB;
				} else {
					extLibFolder = "";
				}
			else
				extLibFolder = System.getenv("USER_CLASSPATH_LIB");
		}
		return extLibFolder;
	}

	/**
	 * Check if the time value isn't empty, and then return it or the default
	 * value.
	 * 
	 * @param time
	 *            Input value for time in seconds.
	 * @return correct time for Time value.
	 */
	private String correctTimeValue(String time) {
		if (time.equals("")) {
			time = "10";
		}
		return time;
	}

	/**
	 * Discover which OS the program is being accessed.
	 * 
	 * @return the OS used during execution.
	 */
	private void chooseOS() {
		if (System.getProperty("os.name").contains("Windows")) {
			this.os = OperatingSystem.WINDOWS;
		} else {
			this.os = OperatingSystem.LINUX;
		}
	}

	/**
	 * Run JMLOK Program, with another Thread to allow freezing GUI while
	 * program runs.
	 * 
	 * @param mainFrame
	 *            The main screen where all messages and windows will show in
	 *            front of.
	 */
	public void runProgram(JFrame mainFrame) {
		try {
			ThreadExecutingProgram t = new ThreadExecutingProgram(mainFrame,
					this);
			t.run();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
		}
	}

	/**
	 * Prepare the for the detect phase of the program.
	 * 
	 * @param compiler
	 *            The compiler that will be used.
	 * @param sourceFolder
	 *            The source folder that the program will analyze.
	 * @param lib
	 *            The library folder in which the analysis depend of.
	 * @param time
	 *            The time (in seconds) to generate tests (with Randoop).
	 * @throws Exception
	 *             When some XML cannot be read.
	 */
	public void prepareToDetectPhase() throws Exception {
		setSystemVariableClassPath();
		showDetectionScreen();
	}

	/**
	 * Return the path name where running Jar was found to use.
	 * 
	 * @return the path name where running Jar was found to use.
	 */
	public String jarPath() {
		Path path = null;
		try {
			path = Paths.get(Controller.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path.getParent().toString();
	}

	/**
	 * Shows the Detection Screen, with console, and button showing.
	 * 
	 * @throws Exception
	 *             When some XML cannot be read.
	 */
	public void showDetectionScreen() throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);
		final Detect d = new Detect(this.compiler);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DetectionScreenAdvisorFrame frame;
					frame = new DetectionScreenAdvisorFrame(d, baos,
							Controller.this);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		errors = d.detect(this.srcFolder, this.extLibFolder, this.time);
		// System.out.flush();
		System.setOut(old);
	}

	/**
	 * Show the Categorization Screen with all of the fields filled.
	 */
	public void showCategorizationScreen() {
		final List<Nonconformance> nonconformance = fulfillCategorizePhase(
				errors, this.srcFolder);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CategorizationScreenAdvisorFrame frame = new CategorizationScreenAdvisorFrame(
							nonconformance, Controller.this);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Show the Categorization Screen with all of the fields filled.
	 */
	public void showNonconformancesScreen() {
		final List<Nonconformance> nonconformance = fulfillDetectionPhase(errors);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewNonconformances frame = new ViewNonconformances(nonconformance, Controller.this);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Set an hack to add jars on SystemClassLoader.
	 */
	private void setSystemVariableClassPath() {
		boolean isWindows = System.getProperty("os.name").contains("Windows");
		String separator = (isWindows) ? ";" : ":";
		// ClassLoader must know source directory
		String pathVar = "."
				+ separator
				+ FileUtil.getListPathPrinted(this.extLibFolder,
						FileUtil.JAR_FILES);
		for (String jar : pathVar.split(separator)) {
			try {
				ClassPathHacker.addFile(jar);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Call Categorize for the categorization phase.
	 * 
	 * @param errors
	 *            The set of test errors to categorize.
	 * @param source
	 *            The source folder where the errors where detected.
	 * @return a list of nonconformances already categorized.
	 */
	private List<Nonconformance> fulfillCategorizePhase(Set<Nonconformance> errors, String source) {
		Categorize c = new Categorize();
		List<Nonconformance> x = new ArrayList<Nonconformance>();
		nonconformities = c.categorize(errors, source);
		for (Nonconformance n : nonconformities)
			x.add(n);
		return x;
	}
	
	/**
	 * Used for fulfill the Detection phase.
	 * 
	 * @param errors
	 *            The set of test errors to categorize.
	 * @return a list of nonconformances.
	 */
	private List<Nonconformance> fulfillDetectionPhase(Set<Nonconformance> errors) {
		List<Nonconformance> noncs = new ArrayList<Nonconformance>();
		Examinator ex = new Examinator(this.srcFolder);
		for (Nonconformance n : errors)
			n.setTestCaseCode(ex.showsMethodCode(n));
		for (Nonconformance n : errors)
			noncs.add(n);
		return noncs;
	}

	/**
	 * Copy the file results.xml generated from the categorization to another
	 * file specified.
	 * 
	 * @param path
	 *            The path where the file will be copied.
	 * @throws IOException
	 *             When the path is invalid.
	 */
	public void saveResultsInXML(String path) throws IOException {
		Path source = (new File(Constants.RESULTS)).toPath();
		Path target = (new File(path + Constants.FILE_SEPARATOR + "results.xml"))
				.toPath();
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}
}