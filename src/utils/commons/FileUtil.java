package utils.commons;

import gui.CategorizationScreenAdvisorFrame;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

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
		if (formatSearched.equals(DIRECTORIES)) {
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
