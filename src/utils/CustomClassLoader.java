package utils;

import java.io.FileInputStream;

/**
 * Class CustomClassLoader used to load the class file of a given path.
 * @author Alysson Milanez and Dennis Sousa.
 *
 */

public class CustomClassLoader extends ClassLoader {
	
	final String basePath = Constants.SOURCE_BIN+Constants.FILE_SEPARATOR;
	
	@Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        String fullName = name.replace('.', '/');
        fullName += ".class";

        String path = basePath + fullName;
        
        try {
            FileInputStream fis = new FileInputStream(path);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            Class<?> res = defineClass(name, data, 0, data.length);
            fis.close();
            return res;
        } catch(Exception e) {
            return super.findClass(name);
        }
    }


}
