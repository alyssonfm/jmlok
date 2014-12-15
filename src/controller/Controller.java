package controller;


import gui.CategorizationScreenAdvisorFrame;
import gui.DetectionScreenAdvisorFrame;
import gui.Main;

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

import org.apache.commons.io.output.ByteArrayOutputStream;

import utils.commons.ClassPathHacker;
import utils.commons.Constants;
import utils.commons.FileUtil;
import utils.datastructure.Nonconformance;
import categorize.Categorize;
import detect.Detect;

public class Controller {
	
	private static Set<Nonconformance> errors;
	private static Set<Nonconformance> nonconformities;
	private static String source;
	private int compiler;
	private int os;
	
	public Controller(int compiler, int os) {
		this.compiler = compiler;
		this.os = System.getProperty("os.name").contains("Windows") ? 0 : 1;
	}
	
	/**
	 * Check whether source folder wasn't present on input.
	 * @param srcFolder Source folder input.
	 * @throws Exception When source folder input is a empty or a null String.
	 */
	public void checkSrcFolderField(String srcFolder) throws Exception{
		if (srcFolder.equals("") || srcFolder == null) throw new Exception("Choose the source folder before running.");
	}
	
	/**
	 * Check a configuration problem on non-Windows OS, whereas randoop.jar needs to be on
	 * the CLASSPATH to be ran on Java.
	 * @throws Exception When Randoop is not on CLASSPATH if Windows isn't the OS.
	 */
	public void checkRandoopRequirements() throws Exception{
		if(!System.getProperty("os.name").contains("Windows") 
		&& !(System.getenv("CLASSPATH").contains("randoop.jar"))){
			throw new Exception("The file randoop.jar was not configured using JMLOKSetup. "
					+ "Please runs JMLOKSetup again and put randoop.jar into your choosen ext lib folder.");
		}
	}
	
	/**
	 * Uses a regex to check if the input of time contain only digits. 
	 * @param time String informing time in seconds for tool execution.
	 * @throws TimeException if time is not valid value.
	 */
	public void checkTimeField(String time) throws Exception{
		if(!(time.matches("\\d+"))) throw new Exception("Please insert a valid number of seconds.");
	}
	
	/**
	 * Check if the folder selected is valid and exist. If is a empty string, will use default values
	 * @param extLibFolder The value of libraries folder input.
	 * @return The default value for libraries folder or itself.
	 */
	public String correctLibFolder(String extLibFolder) {
		if(extLibFolder.equals("")) {
			if(this.os == Constants.WINDOWS_OS)
				if(this.compiler == Constants.JMLC_COMPILER){
					extLibFolder = Constants.JMLC_LIB;
				}else{
					extLibFolder = "";
				}
			else
				extLibFolder = System.getenv("USER_CLASSPATH_LIB");
		}
		return extLibFolder;
	}
	
	/**
	 * Check if the time value isn't empty, and then return it or the default value.
	 * @param time Input value for time in seconds.
	 * @return correct time for Time value.
	 */
	public String correctTimeValue(String time){
		if(time.equals("")){
			time = "10";
		}
		return time;
	}
	
	/**
	 * Receive how parameters two booleans and returns the index of correct compiler
	 * @param jml State of the radio button for select jml compiler
	 * @param cc State of the radio button for select c# compiler
	 * @return the compiled selected by the user
	 */
	public int chooseCompiler(boolean jml, boolean cc) throws Exception{
		if (jml){
			this.compiler = Constants.JMLC_COMPILER;
			return this.compiler;
		}else if (cc){
			this.compiler = Constants.CODECONTRACTS_COMPILER;
			return this.compiler;
		}else 
			throw new Exception("Please, select the compiler");
	}

	/**
	 * Prepare the for the detect phase of the program.
	 * @param compiler The compiler that will be used. 
	 * @param sourceFolder The source folder that the program will analyze.
	 * @param lib The library folder in which the analysis depend of. 
	 * @param time The time (in seconds) to generate tests (with Randoop).
	 * @throws Exception When some XML cannot be read.
	 */
	public static void prepareToDetectPhase(int compiler, String sourceFolder, String lib, String time) throws Exception{
		setSystemVariableClassPath(lib);
		 source = sourceFolder;
		 showDetectionScreen(compiler, lib, time);
	}

	/**
	 * Return the path name where running Jar was found to use.
	 * @return the path name where running Jar was found to use.
	 */
	public String jarPath() {
		Path path = null;
		try {
			path = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path.getParent().toString();
	}
	
	/**
	 * Shows the Detection Screen, with console, and button showing.
	 * @param compiler The compiler that will be used. 
	 * @param lib The library folder in which the analysis depend of. 
	 * @param time The time (in seconds) to generate tests (with Randoop).
	 * @throws Exception When some XML cannot be read.
	 */
	public static void showDetectionScreen(int compiler, String lib, String time) throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);
		
	    final Detect d = new Detect(compiler);
	    EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DetectionScreenAdvisorFrame frame;
					frame = new DetectionScreenAdvisorFrame(d, baos);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	    
	    errors = d.detect(source, lib, time);
		// System.out.flush();
	    System.setOut(old);
	}
	
	/**
	 * Show the Categorization Screen with all of the fields filled.
	 */
	public static void showCategorizationScreen() {
		final List<Nonconformance> nonconformance = fulfillCategorizePhase(errors, source);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CategorizationScreenAdvisorFrame frame = new CategorizationScreenAdvisorFrame(nonconformance);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Set an hack to add jars on SystemClassLoader.
	 * @param libFolder The name of the library folder containing jars.
	 */
	private static void setSystemVariableClassPath(String libFolder) {
		boolean isWindows = System.getProperty("os.name").contains("Windows");
		String separator = (isWindows)?";":":";
		// ClassLoader must know source directory
		String pathVar = "." + separator  + FileUtil.getListPathPrinted(libFolder, FileUtil.JAR_FILES);
		for(String jar : pathVar.split(separator)){
			try {
				ClassPathHacker.addFile(jar);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Call Categorize for the categorization phase.
	 * @param errors The set of test errors to categorize.
	 * @param source The source folder where the errors where detected.
	 * @return an list of nonconformances already categorized.
	 */
	private static List<Nonconformance> fulfillCategorizePhase(Set<Nonconformance> errors, String source) {
		Categorize c = new Categorize();
		List<Nonconformance> x = new ArrayList<Nonconformance>();
		nonconformities = c.categorize(errors, source);
		for(Nonconformance n : nonconformities)
			x.add(n);
		return x;
	}

	/**
	 * Copy the file results.xml generated from the categorization to another file specified.
	 * @param path The path where the file will be copied.
	 * @throws IOException When the path is invalid.
	 */
	public static void saveResultsInXML(String path) throws IOException {
		Path source = (new File(Constants.RESULTS)).toPath();
		Path target = (new File(path + Constants.FILE_SEPARATOR + "results.xml")).toPath();
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

}
