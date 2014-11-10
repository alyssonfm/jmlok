package utils;

import java.io.IOException;
import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.Method;

/**
 * An modification of the System Class Folder, necessary for operations on Class Folder needed, on
 * categorization phase of the program.
 * @author Alysson Milanez and Dennis Sousa.
 *
 */
public class ClassPathHacker {

	@SuppressWarnings("rawtypes")
	private static final Class[] parameters = new Class[]{URL.class};

	/**
	 * Add a file to System Default Class Loader, using an path for a file. 
	 * @param s The path for the file to be add.
	 * @throws IOException When the addition are not fulfilled.
	 */
	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}

	/**
	 * Add a file to System Default Class Loader. 
	 * @param f The file to be add.
	 * @throws IOException When the addition are not fulfilled.
	 */
	@SuppressWarnings("deprecation")
	public static void addFile(File f) throws IOException {
		addURL(f.toURL());
	}


	/**
	 * Add a file to System Default Class Loader, using an URL for the file. 
	 * @param u The URL for the file to be add.
	 * @throws IOException When the addition are not fulfilled.
	 */
	@SuppressWarnings("unchecked")
	public static void addURL(URL u) throws IOException {

		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		@SuppressWarnings("rawtypes")
		Class sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[]{u});
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	
	}

}