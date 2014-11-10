package detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import utils.Constants;
import utils.FileUtil;

/**
 * Class used to detect nonconformances in Java/JML programs.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class Detect {

	private boolean isWindows = false;
	private boolean isJMLC = false;
	private boolean isOpenJML = false;
	private String jmlLib;
	private File jmlokDir = new File(Constants.TEMP_DIR);
	private File javaBin = new File(Constants.SOURCE_BIN);
	private File jmlBin = new File(Constants.JML_BIN);
	private File testSource = new File(Constants.TEST_DIR);
	private File testBin = new File(Constants.TEST_BIN);
	private long startTime;
	private List<DetectListener> detectListeners;
	private String sourceFolder;
	private String librariesFolder;
	private String timeout;
	
	private enum StagesDetect{
		CREATED_DIRECTORIES, COMPILED_JAVA, COMPILED_JML, GENERATED_TESTS, EXECUTED_TESTS, ERROR_ON_DETECTION
	}
	
	/**
	 * The constructor of this class, creates a new instance of Detect class, creates the jmlok directory and set the JML compiler used.
	 * @param comp = the integer that indicates which JML compiler will be used.
	 */
	public Detect(int comp) {
		while (!jmlokDir.exists()) {
			jmlokDir.mkdirs();
		}
		switch (comp) {
		case Constants.JMLC_COMPILER:
			isJMLC = true;
			jmlLib = Constants.JMLC_LIB;
			break;
		case Constants.OPENJML_COMPILER:
			isOpenJML = true;
			jmlLib = Constants.OPENJML_SRC;
			break;
		default:
			break;
		}
		isWindows = System.getProperty("os.name").contains("Windows");
		detectListeners = new ArrayList<DetectListener>();
	}
	
	/**
	 * Method used to detect the nonconformances.
	 * @param source = the path to classes directory.
	 * @param lib = the path to external libraries directory.
	 * @param timeout = the time to tests generation.
	 * @return - The list of nonconformances detected.
	 * @throws Exception When some XML cannot be read.
	 */
	public Set<TestError> detect(String source, String lib, String timeout){
		try {
			execute(source, lib, timeout);			
			ResultProducer r = new ResultProducer();
			if(isJMLC) return r.listErrors(Constants.JMLC_COMPILER);
			else return r.listErrors(Constants.OPENJML_COMPILER);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			triggersEvent(StagesDetect.ERROR_ON_DETECTION);
			// This line below commented serves to inform all errors just on Detection console.
			// e.printStackTrace(); 
			return null;
		}
	}
	
	/**
	 * Method that executes the scripts to conformance checking.
	 * @param srcFolder = the path to source of files to be tested.
	 * @param libFolder = the path to external libraries needed for the current SUT.
	 * @param time = the time to tests generation.
	 * @throws Exception When some XML cannot be read.
	 */
	public void execute(String srcFolder, String libFolder, String time) throws Exception {
		try {
			sourceFolder = srcFolder;
			librariesFolder = libFolder;

			getClassListFile(sourceFolder);
			initTimer();
			
			timeout = time;
			runStage("Creating directories", "Directories created in", StagesDetect.CREATED_DIRECTORIES);
			runStage("Compiling the project", "Project compiled in", StagesDetect.COMPILED_JAVA);
			runStage("Compiling with JML compiler", "Project compiled with JML in", StagesDetect.COMPILED_JML);

			if(!FileUtil.getListPathPrinted(Constants.JML_BIN, FileUtil.DIRECTORIES).equals("")){
				runStage("Generating tests", "Tests generated in", StagesDetect.GENERATED_TESTS);
				runStage("Running JUnit to test the JML code", "Tests ran in", StagesDetect.EXECUTED_TESTS);
			}else{
				throw new Exception("JML couldn't compile the files.");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Initiates timer, to count seconds of each stages of Detect.
	 */
	private void initTimer() {
		setStartTime(0);
		countTime();
	}

	/**
	 * Run a stage from Detection phase.
	 * @param iniMsg message introducing the stage.
	 * @param finMsg message advising end of the stage.
	 * @param stagesDetect which stage will be executed.
	 * @throws Exception When some stage shows some error.
	 */
	private void runStage(String iniMsg, String finMsg, StagesDetect stagesDetect) throws Exception {
		System.out.print(iniMsg + "...");
		switch (stagesDetect) {
		case CREATED_DIRECTORIES:
			createDirectories();
			cleanDirectories();			
			break;
		case COMPILED_JAVA:
			javaCompile(sourceFolder, librariesFolder);
			break;
		case COMPILED_JML:
			jmlCompile(sourceFolder);
			break;
		case GENERATED_TESTS:
			generateTests(librariesFolder, timeout);
			break;
		case EXECUTED_TESTS:
			runTests(librariesFolder);
			break;
		case ERROR_ON_DETECTION:
			break;
		default:
			break;
		}
		System.out.println(finMsg + " " + ((double) countTime() * 0.001) + " seconds");
		triggersEvent(stagesDetect);
	}

	/**
	 * Method used to list all classes present into the directory received as parameter.
	 * @param sourceFolder = the directory source of the files.
	 * @return - the file containing all classes.
	 */
	private File getClassListFile(String sourceFolder) {
		List<String> listClassNames = FileUtil.listNames(sourceFolder, "", ".java");
		StringBuffer lines = new StringBuffer();
		for (String className : listClassNames) {
			className = className + "\n";
			lines.append(className);
		}
		return FileUtil.makeFile(Constants.CLASSES, lines.toString());
	}
	
	/**
	 * Method used to creates all directories to be used by the tool.
	 */
	private void createDirectories(){
		while (!javaBin.exists()) {
			javaBin.mkdirs();
		}
		while (!jmlBin.exists()) {
			jmlBin.mkdirs();
		}
		while (!testSource.exists()) {
			testSource.mkdirs();
		}
		while (!testBin.exists()) {
			testBin.mkdirs();
		}
	}
	
	/**
	 * Method used to clean all directories - for the case of several executions of the tool.
	 */
	private void cleanDirectories(){
		try {
			FileUtils.cleanDirectory(javaBin);
			FileUtils.cleanDirectory(jmlBin);
			FileUtils.cleanDirectory(testSource);
			FileUtils.cleanDirectory(testBin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to Java compilation of the files (needed for tests generation).
	 * @param sourceFolder = the path to source files.
	 * @param libFolder = the path to external libraries needed to Java compilation.
	 * @throws Exception problem with ANT projects.
	 */
	public void javaCompile(String sourceFolder, String libFolder) throws Exception{
		final StringBuilder buff = new StringBuilder();
		jmlLib = jmlLib + libFolder;

		// Run ant file
		Project p = new Project();
		DefaultLogger consoleLogger = createLogger(buff);
		File buildFile = accessFile("javaCompile.xml");
		p.setUserProperty("source_folder", sourceFolder);
		p.setUserProperty("source_bin", Constants.SOURCE_BIN);
		p.setUserProperty("lib", libFolder);
		p.setUserProperty("jmlLib", jmlLib);		
		runProject(buff, p, buildFile, "javaCompile.xml", "compile_project", consoleLogger);
	}
	
	/**
	 * Method used to generate the tests to conformance checking.
	 * @param libFolder = the path to external libraries needed to tests generation and compilation.
	 * @param timeout = the time to tests generation.
	 * @throws Exception When the XML cannot be read.
	 */
	public void generateTests(String libFolder, String timeout) throws Exception{
		final StringBuilder buff = new StringBuilder();
		jmlLib = jmlLib + libFolder;
		
		// Run Randoop
		String pathToRandoop;
		pathToRandoop = getJARPath() + Constants.FILE_SEPARATOR + "lib" 
					  + Constants.FILE_SEPARATOR + "randoop.jar";			
		runRandoop(libFolder, timeout, pathToRandoop);
		
		// Run ant file
		Project p = new Project();
		DefaultLogger consoleLogger = createLogger(buff);
		File buildFile = accessFile("generateTests.xml");
		p.setUserProperty("classes", Constants.CLASSES);
		p.setUserProperty("source_bin", Constants.SOURCE_BIN);
		p.setUserProperty("tests_src", Constants.TEST_DIR);
		p.setUserProperty("tests_bin", Constants.TEST_BIN);
		p.setUserProperty("tests_folder", Constants.TESTS);
		p.setUserProperty("lib", libFolder);
		p.setUserProperty("jmlLib", jmlLib);
		p.setUserProperty("timeout", timeout);
		runProject(buff, p, buildFile, "generateTests.xml", "compile_tests", consoleLogger);
	}
	
	/**
	 * Uses a command to run Randoop to generate tests.
	 * @param libFolder = the path to external libraries needed to tests generation and compilation.
	 * @param timeout = the time to tests generation.
	 * @param pathToRandoop = location of Randoop JAR.
	 * @throws IOException = bad command interpretation.
	 * @throws InterruptedException = bad command.
	 */
	private void runRandoop(String libFolder, String timeout,
			String pathToRandoop) throws IOException, InterruptedException {
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(FileUtil.getCommandToUseRandoop(timeout, pathToRandoop, FileUtil.getListPathPrinted(libFolder, FileUtil.JAR_FILES)));
		final InputStreamReader ou = new InputStreamReader(proc.getInputStream());
		final InputStreamReader er = new InputStreamReader(proc.getErrorStream());
		final BufferedReader bo = new BufferedReader(ou); 
		final BufferedReader be = new BufferedReader(er);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String line = null;
				try {
					while ((line = bo.readLine()) != null) {
						System.out.println(line);
					}
					ou.close();
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String line = null;
				try {
					while ((line = be.readLine()) != null) {
						System.out.println(line);
					}
					er.close();
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}).start();


		int exitVal = proc.waitFor();
		if(exitVal != 0) {
			System.out.println("Error reading: " + pathToRandoop + "\n"
					+ "Java couldn't run Randoop. Verify if command below works."
					+ "Command Used -> " + FileUtil.getCommandToUseRandoop(timeout, pathToRandoop, FileUtil.getListPathPrinted(libFolder, FileUtil.JAR_FILES) + pathToRandoop));
		}
	}
	
	/**
	 * Method used to do the JML compilation of the files.
	 * @param sourceFolder = the source of files to be compiled.
	 * @throws Exception problem with ANT projects.
	 */
	public void jmlCompile(String sourceFolder) throws Exception{
		final StringBuilder buff = new StringBuilder();
		if(FileUtil.hasDirectories(sourceFolder)){
			if(isJMLC){
				runJMLCompiler(sourceFolder, buff, "jmlcCompiler.xml", isJMLC);
			} else if(isOpenJML){
				runJMLCompiler(sourceFolder, buff, "openjmlCompiler.xml", isJMLC);
			}
		} else {
			if(isJMLC){
				runJMLCompiler(sourceFolder, buff, "jmlcCompiler2.xml", isJMLC);
			} else if(isOpenJML){
				runJMLCompiler(sourceFolder, buff, "openjmlCompiler2.xml", isJMLC);
			}
		}
	}

	/**
	 * Run respective JML compiler.
	 * @param sourceFolder = the source of files to be compiled.
	 * @param buff = where error an log will be printed.
	 * @param nameFile = name of .xml to be executed.
	 * @param isJMLC = true if JML compiler used will be jmlc, false, if OpenJML.
	 * @throws Exception problems with ANT projects.
	 */
	private void runJMLCompiler(String sourceFolder, final StringBuilder buff,
			String nameFile, boolean isJMLC) throws Exception{
		Project p = new Project();
		DefaultLogger consoleLogger = createLogger(buff);
		File buildFile = setJMLProperties(sourceFolder, nameFile, p);
		if(isJMLC)
			p.setUserProperty("jmlcExec", (isWindows)?(Constants.JMLC_SRC+"jmlc.bat"):(Constants.JMLC_SRC + "jmlc-unix"));
		runProject(buff, p, buildFile, nameFile, "jmlc", consoleLogger);
	}
	
	/**
	 * Set common properties to run JML compiler.
	 * @param sourceFolder = the source of files to be compiled.
	 * @param nameFile = name of .xml to be executed.
	 * @param p = project to be run.
	 * @return buildFile to be parsed.
	 */
	private File setJMLProperties(String sourceFolder, String nameFile, Project p){
		File buildFile;
		buildFile = accessFile(nameFile);
		p.setUserProperty("source_folder", sourceFolder);
		p.setUserProperty("jmlBin", Constants.JML_BIN);
		return buildFile;
	}
	
	/**
	 * Method used to run the tests with the JML oracles.
	 * @param libFolder = the path to external libraries needed to tests execution.
	 * @throws Exception problems with ANT project.
	 */
	private void runTests(String libFolder) throws Exception{
		final StringBuilder buff = new StringBuilder();
		
		// Run ant file
		Project p = new Project();
		DefaultLogger consoleLogger = createLogger(buff);
		File buildFile = accessFile("runTests.xml");
		p.setUserProperty("lib", libFolder);
		p.setUserProperty("jmlBin", Constants.JML_BIN);
		if(isJMLC) p.setUserProperty("jmlCompiler", Constants.JMLC_SRC);
		else if(isOpenJML) p.setUserProperty("jmlCompiler", Constants.OPENJML_SRC);
		p.setUserProperty("tests_src", Constants.TEST_DIR);
		p.setUserProperty("tests_bin", Constants.TEST_BIN);
		runProject(buff, p, buildFile, "runTests.xml", "run_tests", consoleLogger);
	}
	
	/**
	 * Run specify ANT project.
	 * @param buff = where log will be printed.
	 * @param p = project to be run.
	 * @param buildFile = buildFile to be parsed.
	 * @param nameFile = name of .xml to be executed.
	 * @param targetName = name of ANT process.
	 * @param consoleLogger = logger who will print error and info about ANT execution.
	 * @throws Exception parsing or executing ANT problems.
	 */
	private void runProject(final StringBuilder buff, Project p, File buildFile, String nameFile, String targetName, DefaultLogger consoleLogger) throws Exception {
		p.addBuildListener(consoleLogger);
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);		
		try {
			helper.parse(p, buildFile);			
		} catch (Exception e) {
			System.out.println(buff.toString());
			throw new Exception("Error while trying to parse file "
					+ "ant" + Constants.FILE_SEPARATOR + nameFile
					+ " Running directory: " + getJARPath());
		}
		try {
			p.executeTarget(targetName);			
		} catch (Exception e) {
			System.out.println(buff.toString());
			throw new Exception(e.getMessage());
		}
		System.out.println(buff.toString());
	}
	
	/**
	 * Defines an Logger to transmit info about ANT execution to an StringBuffer.
	 * @param buff buffer that will receive info about ANT execution.
	 * @return Logger to transmit info about ANT execution to an StringBuffer.
	 */
	private DefaultLogger createLogger(final StringBuilder buff) {
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(new PrintStream(new OutputStream() {  
            public void write(int b) throws IOException {                
                buff.append(String.valueOf((char) b));  
            }
            }));
		consoleLogger.setOutputPrintStream(new PrintStream(new OutputStream() {  
	           public void write(int b) throws IOException {  
	               buff.append(String.valueOf((char) b));  
	           }  
	           }));
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		return consoleLogger;
	}
	
	/**
	 * Access ant file to be run.
	 * @param nameFile name of the file to be run.
	 * @return File of ant file that will be run.
	 */
	private File accessFile(String nameFile) {
		File buildFile = null;
		try {
			if(isWindows)
				buildFile = new File("ant" + Constants.FILE_SEPARATOR + nameFile);
			else
				buildFile = new File(new File(getJARPath()), "ant" + Constants.FILE_SEPARATOR + nameFile);
		} catch (Exception e) {
			System.out.println("Error while trying to access file "
					+ "ant" + Constants.FILE_SEPARATOR + nameFile
					+ " Running directory: " + getJARPath());
		}
		return buildFile;
	}
	
	/**
	 * Return jar path of this program itself, where are being executed.
	 * @return jar path of this program itself, where are being executed.
	 */
	private String getJARPath() {
		Path path = null;
		try {
			path = Paths.get(Detect.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path.getParent().toString();
	}
	
	/**
	 * Get time of running some stage.
	 * @return time of running some stage.
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Set time of running some stage.
	 * @param startTime time of running some stage.
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	/**
	 * Count time between stages, take value of stage duration, reset timer, and inform value.
	 * @return value of timer.
	 */
	private long countTime(){
		if(getStartTime() == 0){
			setStartTime(System.currentTimeMillis());
			return getStartTime();
		}else{
			long s = getStartTime();
			setStartTime(System.currentTimeMillis());
			return getStartTime() - s;
		}
	}
	
	/**
	 * Trigger event to be detected by all listeners of Detect.
	 * @param stage stage of execution of Detection phase.
	 */
	private void triggersEvent(StagesDetect stage){
		DetectEvent e = new DetectEvent(this);
		for (DetectListener l : detectListeners) {
			switch (stage) {
			case CREATED_DIRECTORIES:
				l.detectCreatedDirectories(e);
				break;
			case COMPILED_JAVA:
				l.detectCompiledProjectWithJava(e);
				break;
			case COMPILED_JML:
				l.detectCompiledProjectWithJML(e);
				break;
			case GENERATED_TESTS:
				l.detectGeneratedTestsWithRandoop(e);
				break;
			case EXECUTED_TESTS:
				l.detectExecutedTests(e);
				break;
			case ERROR_ON_DETECTION:
				l.detectErrorOnGeneratingTests(e);
				break;
			}
		}
	}
	
	public synchronized void addDetectListener(DetectListener l) {  
        if(!detectListeners.contains(l)) {  
            detectListeners.add(l);  
        }  
    }  
  
    public synchronized void removeDetectListener(DetectListener l) {  
        detectListeners.remove(l);  
    }
	
}