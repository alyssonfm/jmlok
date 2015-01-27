package utils.categorize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import utils.commons.Constants;

/**
 * Used on Categorize module to pull info from Test Cases.
 * 
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 * 
 */
public class AcessingTestCases {
	
	/**
	 * Method to read a file received as parameter.
	 * 
	 * @param name
	 *            - the name of the file to be read.
	 * @return - the content of the file.
	 */
	public static String readFile(String name) {
		String result = "";
		try {
			FileReader fr = new FileReader(new File(name));
			BufferedReader buf = new BufferedReader(fr);
			while (buf.ready()) {
				result += buf.readLine();
				result += "\n";
			}
			buf.close();
		} catch (Exception e) {
			System.err.println("Error in method FileUtil.readFile()");
		}
		return result;
	}
	
	/**
	 * Method to read a single line of a file received as parameter.
	 * 
	 * @param name
	 *            - the name of the file to be read.
	 * @param line
	 *            - the line to be read.
	 * @return - the content of the file.
	 */
	public static String readSingleLineOfFile(String name, int line) {
		String result = "";
		int lines = 0;
		try {
			FileReader fr = new FileReader(new File(name));
			BufferedReader buf = new BufferedReader(fr);
			while (buf.ready()) {
				if (lines == line) {
					while ((result.trim().startsWith("*")
							|| result.trim().startsWith("/") || result.trim()
							.startsWith("@")) && buf.ready())
						result = buf.readLine();
					break;
				}
				result = buf.readLine();
				lines++;
			}
			buf.close();
		} catch (Exception e) {
			System.err
					.println("Error in method FileUtil.readSingleLineOfFile()");
		}
		return result;
	}
	
	/**
	 * Returns copy of line in method to highlight in test cases whom discovered
	 * specified error.
	 * 
	 * @param testFile
	 *            The test file where the error was called.
	 * @param wishedLine
	 *            The line of the file where the error was founded.
	 * @param arr
	 *            Array containing the number of occurrences of the of line that
	 *            originated error, before it.
	 * @return copy of line that originated error.
	 * @throws IOException
	 *             When failing to read the file.
	 */
	public static String lineSampleWhoOriginatedError(String testFile,
			int wishedLine, String test, int[] arr) throws IOException {
		BufferedReader f = new BufferedReader(new FileReader(Constants.TEST_DIR
				+ Constants.FILE_SEPARATOR + testFile));
		String line;
		String testExtract = "";
		boolean foundTest = false;

		int counterLines = 0;
		while ((line = f.readLine()) != null) {
			counterLines++;
			if (line.contains("public void " + test + "()")) {
				foundTest = true;
			} else if (counterLines == wishedLine) {
				f.close();
				arr[0] = countMatches(testExtract, line);
				return line;
			} else if (foundTest) {
				testExtract += line;
			}
		}
		f.close();
		return "";
	}

	/**
	 * Counts how many times the substring appears in the larger String. A null
	 * or empty ("") String input returns 0.
	 *
	 * countMatches(null, *) = 0 countMatches("", *) = 0 countMatches("abba",
	 * null) = 0 countMatches("abba", "") = 0 countMatches("abba", "a") = 2
	 * countMatches("abba", "ab") = 1 countMatches("abba", "xxx") = 0
	 *
	 * @param str
	 *            the String to check, may be null
	 * @param sub
	 *            the substring to count, may be null
	 * @return the number of occurrences, 0 if either String is
	 *         <code>null</code>
	 */
	public static int countMatches(String str, String sub) {
		if (stringIsEmpty(str) || stringIsEmpty(sub)) {
			return 0;
		}
		int count = 0;
		int idx = 0;
		while ((idx = str.indexOf(sub, idx)) != -1) {
			count++;
			idx += sub.length();
		}
		return count;
	}

	/**
	 * Checks if a String is empty ("") or null.
	 *
	 * stringIsEmpty(null) = true stringIsEmpty("") = true stringIsEmpty(" ") =
	 * false stringIsEmpty("bob") = false stringIsEmpty("  bob  ") = false
	 *
	 * @param str
	 *            the String to check, may be null
	 * @return true if the String is empty or null
	 */
	public static boolean stringIsEmpty(String str) {
		return str == null || str.length() == 0;
	}
}
