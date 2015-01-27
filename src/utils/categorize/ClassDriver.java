package utils.categorize;

import java.lang.reflect.Field;
import java.util.ArrayList;

import utils.commons.FileUtil;

/**
 * Used on Categorize module to pull info from Class Objects.
 * 
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 * 
 */
public class ClassDriver {

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
					&& FileUtil.listNames(srcDir, "", ".java").contains(
							superclass.getName()))
				superClassPackagePath = superclass.getName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return superClassPackagePath;
	}
}
