package utils.commons;

import java.awt.Color;
import java.awt.Font;

/**
 * Class that storage the main constants used in the JmlOk2 project.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 * 
 */

public class Constants {
	//Constant to get the file separator of the System.
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	//Constants to folders created path.
	public static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + FILE_SEPARATOR + "jmlok";
	public static final String JML_SOURCE_BIN = TEMP_DIR + FILE_SEPARATOR + "bin";
	public static final String RANDOOP_OUTPUT_FOLDER = TEMP_DIR + FILE_SEPARATOR + "RandoopTests";
	public static final String JML_BIN = TEMP_DIR + FILE_SEPARATOR + "jmlBin";
	public static final String TESTS = TEMP_DIR + FILE_SEPARATOR + "tests";
	public static final String TEST_DIR = TESTS + FILE_SEPARATOR + "src";
	public static final String TEST_BIN = TESTS + FILE_SEPARATOR + "bin";
	//Constant to the file that has the class names. 
	public static final String CLASSES = TEMP_DIR + FILE_SEPARATOR  + "classes.txt";
	//Constants to the result of Randoop execution under SUT.
	public static final String TEST_RESULTS = TEST_DIR + FILE_SEPARATOR + "TEST-RegressionTest.xml";
	public static final String ERROR_TEST_RESULTS = TEST_DIR + FILE_SEPARATOR + "TEST-ErrorTest.xml";
	//Constant to the file that contains the result more cleaned. The nonconformances detected by the tool.
	public static final String RESULTS = TEMP_DIR+FILE_SEPARATOR+"results.xml";
	//Constants that indicates the path to jml compilers.
	public static final String JMLC_LIB = System.getenv("JMLDIR");
	public static final String JMLC_SRC = JMLC_LIB + FILE_SEPARATOR + "bin" + FILE_SEPARATOR;
	//Related to Gui properties
	public static final Font MAIN_FONT = new Font("Verdana", 0, 18);
	public static final Color HILIT_COLOR = Color.YELLOW;
}
