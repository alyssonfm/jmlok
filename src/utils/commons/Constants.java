package utils.commons;

import java.awt.Color;
import java.awt.Font;

/**
 * Class that storage the main constants used in the JMLOK project.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 * 
 */

public class Constants {
	//Constant to get the file separator of the System.
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	//Constants to folders created path.
	//public static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + FILE_SEPARATOR + "codespecok";
	public static final String TEMP_DIR = "C:" + FILE_SEPARATOR + "CodeSpecOK";
	public static final String JML_SOURCE_BIN = TEMP_DIR + FILE_SEPARATOR + "bin";
	//public static final String CODECONTRACTS_SOURCE_BIN = TEMP_DIR + FILE_SEPARATOR + "cSharpBin";
	public static final String CODECONTRACTS_SOURCE_BIN = TEMP_DIR + FILE_SEPARATOR + "CSharpBin";
	public static final String RANDOOP_OUTPUT_FOLDER = TEMP_DIR + FILE_SEPARATOR + "Randoop_Tests";
	public static final String JML_BIN = TEMP_DIR + FILE_SEPARATOR + "jmlBin";
	public static final String TESTS = TEMP_DIR + FILE_SEPARATOR + "tests";
	public static final String TEST_DIR = TESTS + FILE_SEPARATOR + "src";
	public static final String TEST_BIN = TESTS + FILE_SEPARATOR + "bin";
	//Constant to file that has the class names. 
	public static final String CLASSES = TEMP_DIR + FILE_SEPARATOR  + "classes.txt";
	//Constants to result of Randoop execution under SUT.
	public static final String TEST_FILE = TEST_DIR + FILE_SEPARATOR + "RandoopTest0.java";
	public static final String TEST_RESULTS = TEST_DIR + FILE_SEPARATOR + "TEST-RandoopTest.xml";
	//Constant to file that contains the result more cleaned. The nonconformances detected by the tool.
	public static final String RESULTS = TEMP_DIR+FILE_SEPARATOR+"results.xml";
	//Constants to choose the jml compiler to be used.
	public static final int JMLC_COMPILER = 0;
	public static final int OPENJML_COMPILER = 1;
	public static final int CODECONTRACTS_COMPILER = 2;
	public static final int WINDOWS_OS = 0;
	public static final int LINUX_OS = 1;
	public static final int MAC_OS = 2;
	//Constants that indicates the path to jml compilers.
	public static final String OPENJML_SRC = "C:" + FILE_SEPARATOR + "openjml";
	public static final String JMLC_LIB = System.getenv("JMLDIR");
	public static final String JMLC_SRC = JMLC_LIB + FILE_SEPARATOR + "bin" + FILE_SEPARATOR;
	//Related to Gui
	public static final Font MAIN_FONT = new Font("Verdana", 0, 18);
	public static final Color HILIT_COLOR = Color.YELLOW;
}
