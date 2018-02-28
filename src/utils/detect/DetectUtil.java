package utils.detect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import utils.commons.Constants;

/**
 * Class used for Detect module to manipulate files.
 * 
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 * 
 */
public class DetectUtil {
	
	/**
	 * Method to creates a new file with the name and content received as
	 * parameter.
	 * 
	 * @param name
	 *            - the name of the new file.
	 * @param text
	 *            - the content of the file.
	 * @return - the new file created.
	 */
	public static File makeFile(String name, String text) {
		File result = new File(name);
		try {
			result.createNewFile();
			FileWriter fw;
			while (!result.canWrite()) {
				result.createNewFile();
			}
			fw = new FileWriter(result);
			fw.write(text);
			fw.close();
		} catch (IOException e) {
			System.err.println("Error in method DetectUtil.makeFile()");
		}
		return result;
	}

	/**
	 * Method that returns if a directory has sub directories.
	 * 
	 * @param sourcePath
	 *            the path for the directory source.
	 * @return a boolean to indicates if the directory has or not sub
	 *         directories.
	 */
	public static boolean hasDirectories(String sourcePath) {
		File dir = new File(sourcePath);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory())
				return true;
		}
		return false;
	}

	/**
	 * Return the ideal command to execute Randoop and generate tests.
	 * 
	 * @param timeout
	 *            The time used to generate Tests.
	 * @param pathToRandoop
	 *            The path where randoop.jar will be located.
	 * @return The String to be executed in Runtime execution.
	 */
	public static String getCommandToUseRandoop(String timeout,
			String pathToRandoop, String liblist) {
		String bruteCommand = "java -cp \"" + pathToRandoop + ";"
				+ Constants.JML_BIN
				+ ((liblist.equals("")) ? ("") : (";" + liblist))
				+ "\" randoop.main.Main gentests --classlist="
				+ Constants.CLASSES + " --timelimit=" + timeout
				+ " --junit-output-dir=" + Constants.TEST_DIR;
		if (System.getProperty("os.name").contains("Windows"))
			return bruteCommand;
		else
			return bruteCommand.replaceAll(";", ":");
	}
}
