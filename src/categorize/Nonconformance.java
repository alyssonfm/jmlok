package categorize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import utils.Constants;
import utils.FileUtil;

/**
 * Class that represents a nonconformance.
 * @author Alysson Milanez and Dennis Sousa.
 *
 */
public class Nonconformance {

	private Category type;
	private String cause = "";
	private String test = "";
	private String testFile = "";
	private String className = "";
	private String methodName = "";
	private String packageName = "";
	private String sampleLineOfError = "";
	private String methodCalling = "";
	private String message = "";
	private List<String> stackTraceOrder;
	private int countOcurrencesLineOfError;
	
	/**
	 * Constructor of the class. Since it will be used widely with the sets, we decided to leave
	 * the constructor empty and the sets will do the main work to initialize the fields. 
	 */
	public Nonconformance() {
		this.stackTraceOrder = new ArrayList<String>();
	}
	
	/**
	 * Get the type of the nonconformance.
	 * @return the type of the nonconformance.
	 */
	public String getType() {
		return type.getType();
	}

	/**
	 * Set the type of the nonconformance.
	 * @param type The type of the nonconformance.
	 */
	public void setType(Category type) {
		this.type = type;
	}

	/**
	 * Get the cause of the nonconformance.
	 * @return the cause of the nonconformance.
	 */
	public String getCause() {
		return cause;
	}

	/**
	 * Set the cause of the nonconformance.
	 * @param cause The cause of the nonconformance.
	 */
	public void setCause(String cause) {
		this.cause = cause;
	}

	/**
	 * Get the test name that generate the nonconformance.
	 * @return the test name that generate the nonconformance.
	 */
	public String getTest() {
		return test;
	}

	/**
	 * Set the test name that generate the nonconformance.
	 * @param test The test name that generate the nonconformance.
	 */
	public void setTest(String test) {
		this.test = test;
	}

	/**
	 * Get the method in which the nonconformance occurred.
	 * @return the method in which the nonconformance occurred.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Set the method in which the nonconformance occurred.
	 * @param methodName The method in which the nonconformance occurred.
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * Get the class in which the nonconformance occurred.
	 * @return the class in which the nonconformance occurred.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Set the class in which the nonconformance occurred.
	 * @param className The class in which the nonconformance occurred.
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Get the name of the java file in which the nonconformance occurred.
	 * @return the name of the java file in which the nonconformance occurred.
	 */
	public String getTestFile() {
		return testFile;
	}

	/**
	 * Set the name of the java file in which the nonconformance occurred.
	 * @param testFile The name of the java file in which the nonconformance occurred.
	 */
	public void setTestFile(String testFile) {
		this.testFile = testFile;
	}

	/**
	 * Get the package name in which the nonconformance occurred.
	 * @return the package name in which the nonconformance occurred.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Set the package name in which the nonconformance occurred.
	 * @param packageName the package name in which the nonconformance occurred.
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	/**
	 * Get copy of line from the test file which generate the nonconformance.
	 * @return copy of line from the test file which generate the nonconformance.
	 */
	public String getSampleLineOfError() {
		return sampleLineOfError;
	}
	
	/**
	 * Set copy of line from the test file which generate the nonconformance.
	 * @param specifiedLine copy of line line where error was thrown.
	 */
	public void setSampleLineOfError(int specifiedLine) {
		int[] arr = new int[1];
		arr[0] = 0;
		try {
			this.sampleLineOfError = FileUtil.lineSampleWhoOriginatedError(this.testFile, specifiedLine, this.test, arr);
			this.setCountOcurrencesLineOfError(arr[0]);
		} catch (IOException e) {
			this.sampleLineOfError = "";
		}
	}
	
	/**
	 * Get number of ocurrences of instruction that generated Error, before it crashes.
	 * @return number of ocurrences of instruction that generated Error, before it crashes.
	 */
	public int getCountOcurrencesLineOfError() {
		return countOcurrencesLineOfError;
	}

	/**
	 * Set number of ocurrences of instruction that generated Error, before it crashes.
	 * @param countOcurrencesLineOfError number of ocurrences of instruction that generated Error, before it crashes.
	 */
	public void setCountOcurrencesLineOfError(int countOcurrencesLineOfError) {
		this.countOcurrencesLineOfError = countOcurrencesLineOfError;
	}
	
	/**
	 * Get the message displayed on the error log of the nonconformance. 
	 * @return the message displayed on the error log of the nonconformance. 
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Set the message displayed on the error log of the nonconformance. 
	 * @param message The message displayed on the error log of the nonconformance.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Get the method definition which nonconformance was generated, for method overridden issues,
	 * this is necessary. This differentiate invariant, constraint and evaluation errors, where
	 * the line was explicit in error log.
	 * @return the method definition which nonconformance was generated.
	 */
	public String getMethodCalling() {
		return this.methodCalling;
	}
	
	/**
	 * Set the method definition which nonconformance was generated, for method overridden issues,
	 * this is necessary. This differentiate invariant, constraint and evaluation errors, where
	 * the line was explicit in error log.
	 * @param lineOfErrorInJava The line in java file of the class where the method was generated.
	 * @param sourceFolder The folder where the .java of the project are.
	 */
	public void setMethodCalling(int lineOfErrorInJava, String sourceFolder) {
		if(lineOfErrorInJava == -1)
			this.methodCalling = "";
		else if(lineOfErrorInJava == 0)
			this.methodCalling = "defaultConstructor is not explicit";
		else{
			String name = (this.packageName + "." + this.className).replace('.', '/');
			name += ".java";
		    this.methodCalling = FileUtil.readSingleLineOfFile(sourceFolder + Constants.FILE_SEPARATOR + name, lineOfErrorInJava);
		    int temp = this.methodCalling.lastIndexOf("{");
		    if(temp != -1)
		    	this.methodCalling = this.methodCalling.substring(0, this.methodCalling.lastIndexOf("{")).trim();
		}
		if(this.methodCalling.contains(this.getClassName())){
			this.methodCalling = this.methodCalling.replaceFirst(this.className, "<init>");
		}
	}

	/**
	 * Get a sequence of calling methods whom shows error of nonconformance.
	 * @return a sequence of calling methods whom shows error of nonconformance.
	 */
	public List<String> getStackTraceOrder(){
		return this.stackTraceOrder;
	}

	/**
	 * Set in a list, sequence of calling methods whom shows error of nonconformance.
	 * @param methodsList The possible methods to be called.
	 */
	public void setStackTraceOrder(List<String> methodsList) {
		StringReader sr = new StringReader(this.message);
		BufferedReader buf = new BufferedReader(sr);
		try {
			String firstState = this.packageName + "." + this.className + "." + this.methodName;
			String lastState  = this.testFile.substring(0, this.testFile.indexOf(".java")) + "." + this.test;
			String initString = "&lt;init&gt";
			
			this.stackTraceOrder.add(firstState);
			
			buf.readLine();
			while(buf.ready()){
				String info = buf.readLine();

				int begin = info.indexOf("at ");
				int last  = info.indexOf("(", begin);
				String trace;
				try {
					trace = info.substring(begin + 3, last);					
				} catch (IndexOutOfBoundsException e) {
					// There are some lines on message board, that will not contain
					// at Class.method () structure, so we ignore it.
					continue;
				}
				if(trace.equals(lastState)){
					this.stackTraceOrder.add(lastState);
					return;
				}else if(trace.matches(initString)){
					int left  = trace.indexOf(".") + 1;
					int right = trace.indexOf(".", left);
					this.stackTraceOrder.add(trace.replaceAll(initString, trace.substring(left, right)));
				}else{
					for (int i = 0; i < methodsList.size(); i++) {
						if(trace.equals(methodsList.get(i))){
							this.stackTraceOrder.add(trace);
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if((obj instanceof Nonconformance) 
				&& ((Nonconformance) obj).getType().equals(this.getType()) 
				&& ((Nonconformance) obj).getCause().equalsIgnoreCase(this.getCause())
				&& ((Nonconformance) obj).getTest().equalsIgnoreCase(this.getTest())
				&& ((Nonconformance) obj).getTestFile().equalsIgnoreCase(this.getTestFile())
				&& ((Nonconformance) obj).getClassName().equalsIgnoreCase(this.getClassName())
				&& ((Nonconformance) obj).getMethodName().equalsIgnoreCase(this.getMethodName())
				&& ((Nonconformance) obj).getPackageName().equalsIgnoreCase(this.getPackageName()))
		{ 
			return true;
		} else {
			return false;
	
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cause == null) ? 0 : cause.hashCode());
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((test == null) ? 0 : test.hashCode());
		result = prime * result
				+ ((testFile == null) ? 0 : testFile.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

}
