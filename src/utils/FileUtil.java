package utils;

import gui.CategorizationScreenAdvisorFrame;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class that contains some methods to manipulate files.
 * 
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 * 
 */
public class FileUtil {

	public static String JAR_FILES = ".jar";
	public static String DIRECTORIES = "files";

	/**
	 * Returns a list of path names from files in a same paste, separated by a
	 * ';'.
	 * 
	 * @param libFolder
	 *            Folder to list its files.
	 * @param formatSearched
	 *            format to be searched.
	 * @return String containing list of path names from files in a same paste,
	 *         separated by a ';', for Windows and separated by a ':' for
	 *         others.
	 */
	public static String getListPathPrinted(String libFolder,
			String formatSearched) {
		if (libFolder.equals(""))
			return libFolder;
		File dir = new File(libFolder);
		if (!dir.exists()) {
			throw new RuntimeException("Directory " + dir.getAbsolutePath()
					+ " does not exist.");
		}
		File[] arquivos = dir.listFiles();
		// Separator must be correctly settled to classLoader work
		String separator = (System.getProperty("os.name").contains("Windows")) ? ";"
				: ":";
		String toReturn = "";
		if (formatSearched.equals(FileUtil.DIRECTORIES)) {
			for (File file : arquivos) {
				toReturn += file.toString() + separator;
			}
		} else {
			for (File file : arquivos) {
				if (file.toString().contains(formatSearched)) {
					toReturn += file.toString() + separator;
				}
			}
		}
		return toReturn;
	}

	/**
	 * Method to list all names into the project.
	 * 
	 * @param path
	 *            - base directory of the project.
	 * @param base
	 *            - used to indicate the base name of directory, utilized for
	 *            purposes of recursion.
	 * @return - The names of all files presents into the current project.
	 */
	public static List<String> listNames(String path, String base,
			String fileExtension) {
		List<String> result = new ArrayList<String>();
		try {
			File dir = new File(path);

			if (!dir.exists()) {
				throw new RuntimeException("Directory " + dir.getAbsolutePath()
						+ " does not exist.");
			}
			File[] arquivos = dir.listFiles();
			int tam = arquivos.length;
			for (int i = 0; i < tam; i++) {
				if (arquivos[i].isDirectory()) {
					String baseTemp = base + arquivos[i].getName() + ".";
					result.addAll(listNames(arquivos[i].getAbsolutePath(),
							baseTemp, fileExtension));
				} else {
					if (arquivos[i].getName().endsWith(fileExtension)) {
						String temp = base + arquivos[i].getName();
						temp = removeExtension(temp, fileExtension);
						if (!result.contains(temp))
							result.add(temp);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error in FileUtil.listNames()");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Method to remove the extension of the files.
	 * 
	 * @param arquivo
	 *            - the file that extension will be removed.
	 * @param extension
	 *            - the extension to be removed.
	 * @return - the filename without the extension.
	 */
	private static String removeExtension(String arquivo, String extension) {
		arquivo = arquivo.replaceAll(extension + "\\b", "");
		return arquivo;
	}

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
			System.err.println("Error in method FileUtil.makeFile()");
		}
		return result;
	}

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
	 * @param arr Array containing the number of occurrences of the of line that originated error, before it.
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
			if(line.contains("public void " + test + "()")){
				foundTest = true;
			}else if (counterLines == wishedLine) {
				f.close();
				arr[0] = StringUtils.countMatches(testExtract, line);
				return line;
			}else if(foundTest){
				testExtract += line;
			}
		}
		f.close();
		return "";
	}


	/**
	 * Method that creates a XML file used to store the nonconformances
	 * detected.
	 * 
	 * @param path
	 *            - the name of the XML file to be produced.
	 * @return - the XML document.
	 */
	public static Document createXMLFile(String path) {
		File f = new File(path);
		DocumentBuilderFactory docFactory;
		DocumentBuilder docBuilder;
		Document doc = null;

		try {
			f.createNewFile();
			while (!f.canWrite()) {
				f.createNewFile();
			}
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();

			Element root = doc.createElement("NonconformancesSuite");
			doc.appendChild(root);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			StreamResult result = new StreamResult(f);
			transformer.transform(source, result);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

		return doc;
	}

	/**
	 * Method that gets all variables from a class received as parameter.
	 * 
	 * @param path
	 *            = the path for the class that the variables will be got.
	 * @return = the list of all variables from the class received as parameter.
	 */
	public static ArrayList<String> getVariablesFromClass(String path) {
		ArrayList<String> variables = new ArrayList<String>();
		try {
			Class<?> clazz = Class.forName(path, true, new CustomClassLoader());
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				String aux = field.toString();
				aux = aux.substring(aux.lastIndexOf(".") + 1, aux.length());
				variables.add(aux);
			}
		} catch (ClassNotFoundException e) {
			System.err
					.println("Error in method FileUtil.getVariablesFromClass()");
		}
		return variables;
	}

	/**
	 * Get complete class names of the interfaces implemented by a Class.
	 * 
	 * @param path
	 *            The complete class name from the class searched.
	 * @return Array containing all complete class names from the interfaces
	 *         implemented.
	 */
	public static ArrayList<String> getInterfacesPathFromClass(String path) {
		ArrayList<String> interfacesPackagePath = new ArrayList<String>();
		try {
			Class<?> clazz = Class.forName(path, true, new CustomClassLoader());
			Class<?>[] interfaces = clazz.getInterfaces();
			for (Class<?> i : interfaces) {
				interfacesPackagePath.add(i.getName());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return interfacesPackagePath;
	}

	/**
	 * Get complete class name of the Super Class extended by a Class.
	 * 
	 * @param path
	 *            The complete class name from the class searched.
	 * @param srcDir
	 *            The source directory where the project examined are located.
	 * @return String representing the complete class name from the Super Class
	 *         extended.
	 */
	public static String getSuperclassPathFromClass(String path, String srcDir) {
		String superClassPackagePath = "";
		try {
			Class<?> clazz = Class.forName(path, true, new CustomClassLoader());
			Class<?> superclass = clazz.getSuperclass();
			if (superclass != null
					&& listNames(srcDir, "", ".java").contains(
							superclass.getName()))
				superClassPackagePath = superclass.getName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return superClassPackagePath;
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
				+ Constants.SOURCE_BIN
				+ ((liblist.equals("")) ? ("") : (";" + liblist))
				+ "\" randoop.main.Main gentests --classlist="
				+ Constants.CLASSES + " --timelimit=" + timeout
				+ " --junit-output-dir=" + Constants.TEST_DIR;
		if (System.getProperty("os.name").contains("Windows"))
			return bruteCommand;
		else
			return bruteCommand.replaceAll(";", ":");
	}

	/**
	 * Forces a default Font for all components of actual JFrame.
	 * 
	 * @param a
	 *            Font for all components of actual JFrame.
	 */
	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				FontUIResource orig = (FontUIResource) value;
				Font font = new Font(f.getFontName(), orig.getStyle(),
						f.getSize());
				UIManager.put(key, new FontUIResource(font));
			}
		}
	}

	/** 
	 * Returns an ImageIcon, or null if the path was invalid. 
	 */
	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = CategorizationScreenAdvisorFrame.class
				.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
}
