package utils.datastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import utils.categorize.AcessingTestCases;
import utils.commons.Constants;

/**
 * Class that represents a nonconformance.
 * 
 * @author Alysson Milanez and Dennis Sousa.
 *
 */
public class Nonconformance {

	private CategoryType type;
	// For nonconformance filtering.
	private boolean jmlRac = true;
	private boolean meaningless = false;
	// For results.xml presentation.
	private String testFile = "";
	private String numberedTest = "";
	private String errorMessage = "";
	private String className = "";
	private String methodName = "";
	private String packageName = "";
	// For analysis purpose only.
	private String methodCalling = "";
	private String packageAndClassCalling = "";
	private int lineNumberOnJavaThatRevealsNC;
	// Categorization module main product.
	private String cause = "";
	// Highlight related.
	private int lineNumberOnTestThatRevealsNC;	
	private String sampleLineOfError = "";
	private int countOcurrencesLineOfError;
	private String testCaseCode = "";
	// StackTrace related.
	private List<String> stackTraceOrder;

	public enum CategoryType {
		PRECONDITION("precondition"), POSTCONDITION("postcondition"), INVARIANT("invariant"), 
		CONSTRAINT("constraint"), EVALUATION("evaluation"), MEANINGLESS("meaningless"), 
		NEUTRAL("");

		private String name;

		private CategoryType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
	
	/**
	 * The alternative constructor of this class. Takes only two arguments as parameter.
	 * For OpenJML use only.
	 * @param summary - the message of the error.
	 * @param type - the type of the error.
	 * @param message - the details from the error log.
	 */
	public Nonconformance(String summary, String type, String message){
		// this.setOpenJMLType(type);
		this.setType(type);
		this.setErrorMessage(message);
		this.setClassName(summary);
		this.setMethodName(summary);
	}

	/**
	 * The constructor of this class, receives a name, a message and a type to the nonconformance.
	 * @param numberedTest = the name of current test.
	 * @param testFile = the name of the .java used for finding this test error.
	 * @param summary = the summary of current test error info.
	 * @param nonconformanceType = the type of error occurred.
	 * @param message = more detailed info about the test error.
	 */
	public Nonconformance(String numberedTest, String testFile, String summary, String nonconformanceType, String message) {
		this.setType(nonconformanceType);
		if(!this.isMeaningless()){
			// Necessary for results.xml
			this.setTestFile(testFile);
			this.setNumberedTest(numberedTest);
			this.setErrorMessage(message);
			this.setClassName(message); 
			this.setMethodName(summary);
			this.setPackageName();
			// Necessary for Categorization details
			this.setLineNumberOnJavaThatRevealsNC(summary);
			this.stackTraceOrder = new ArrayList<String>();
		}
	}
	
	/**
	 * Get the type of the nonconformance.
	 * 
	 * @return the type of the nonconformance.
	 */
	public CategoryType getType() {
		return type;
	}

	/**
	 * Set the type of the nonconformance.
	 * 
	 * @param type
	 *            The type of the nonconformance.
	 */
	public void setType(String type) {
		if (!type.contains("jmlrac")) {
			this.setJmlRac(false);
			this.type = CategoryType.NEUTRAL;
		}
		if (type.contains("Invariant")) {
			this.type = CategoryType.INVARIANT;
		} else if (type.contains("Postcondition")) {
			this.type = CategoryType.POSTCONDITION;
		} else if (type.contains("Precondition")) {
			if (type.contains("Entry")) {
				this.setMeaningless(true);
				this.type = CategoryType.MEANINGLESS;
			} else
				this.type = CategoryType.PRECONDITION;
		} else if (type.contains("Constraint")) {
			this.type = CategoryType.CONSTRAINT;
		} else if (type.contains("Evaluation"))
			this.type = CategoryType.EVALUATION;
	}

	/**
	 * Get the cause of the nonconformance.
	 * 
	 * @return the cause of the nonconformance.
	 */
	public String getCause() {
		return cause;
	}

	/**
	 * Set the cause of the nonconformance.
	 * 
	 * @param cause
	 *            The cause of the nonconformance.
	 */
	public void setCause(String cause) {
		this.cause = cause;
	}

	/**
	 * Get the method name of test that generate the nonconformance.
	 * 
	 * @return the test name that generate the nonconformance.
	 */
	public String getNumberedTest() {
		return numberedTest;
	}

	/**
	 * Set the method name of test that generate the nonconformance.
	 * 
	 * @param test
	 *            The test name that generate the nonconformance.
	 */
	public void setNumberedTest(String test) {
		this.numberedTest = test;
	}

	/**
	 * Get the method in which the nonconformance occurred.
	 * 
	 * @return the method in which the nonconformance occurred.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Method that sets the method name for the current nonconformance.
	 * @param message The details from the error log.
	 */
	public void setMethodName(String message) {
		String result = "";
		if(isJmlRac()){
			String aux = message;
			String[] text = aux.split(" ");
			if (this.type.equals(CategoryType.PRECONDITION) || this.type.equals(CategoryType.POSTCONDITION)) {
				result = text[2].substring(text[2].indexOf(".")+1, text[2].length());
			} else if(this.type.equals(CategoryType.INVARIANT)){
				if(text[2].contains("init")){
					result = getClassName();
				} else {
					result = text[2].substring(text[2].indexOf(".")+1, text[2].indexOf("@"));
				}
			} else if(this.type.equals(CategoryType.CONSTRAINT)){
				result = text[2].substring(text[2].indexOf(".")+1, text[2].indexOf("@"));
			} else {
				int firstIndex = this.errorMessage.indexOf("at ");
				firstIndex = this.errorMessage.indexOf("$", firstIndex);
				int lastIndex = this.errorMessage.indexOf("$", firstIndex+1);
				result = this.errorMessage.substring(firstIndex+1, lastIndex);
			}
		}
		this.methodName = result;
	}

	/**
	 * Get the class in which the nonconformance occurred.
	 * 
	 * @return the class in which the nonconformance occurred.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Set the class in which the nonconformance occurred.
	 * 
	 * @param className
	 *            The class in which the nonconformance occurred.
	 */
	public void setClassName(String message) {
		String result = "";
		String aux = "";
		if (isJmlRac()) {
			int first = message.indexOf("(")+1;
			int lastIndex = message.indexOf(")");
			aux = message.substring(first, lastIndex);
		 result = aux.substring(0, aux.indexOf("."));
		}
		this.className = result;
	}

	/**
	 * Get the name of the java file in which the nonconformance occurred.
	 * 
	 * @return the name of the java file in which the nonconformance occurred.
	 */
	public String getTestFile() {
		return testFile;
	}

	/**
	 * Set the name of the java file in which the nonconformance occurred.
	 * 
	 * @param testFile
	 *            The name of the java file in which the nonconformance
	 *            occurred.
	 */
	public void setTestFile(String testFile) {
		this.testFile = testFile;
	}

	/**
	 * Get the package name in which the nonconformance occurred.
	 * 
	 * @return the package name in which the nonconformance occurred.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Set the package name in which the nonconformance occurred.
	 */
	public void setPackageName() {
		int firstIndex = this.errorMessage.indexOf("at ");
		int lastIndex = 0;
		if(this.getType().equals(CategoryType.CONSTRAINT)){
			firstIndex = this.errorMessage.indexOf("at ", firstIndex + 3);
			lastIndex = this.errorMessage.indexOf("." + this.className, firstIndex);
		} else {
			lastIndex = this.errorMessage.indexOf("." + this.className + ".");
		}
		if(lastIndex != -1)
			this.packageName = this.errorMessage.substring(firstIndex + 3, lastIndex);
	}
	
	/**
	 * Method that sets the line where current error was found.
	 */
	public void setLineNumberOnTestThatRevealsNC() {
		int firstIndex = this.errorMessage.lastIndexOf(this.testFile);
		firstIndex = this.errorMessage.indexOf(":", firstIndex) + 1;
		int lastIndex = this.errorMessage.indexOf(")", firstIndex);
		Integer aux = new Integer(this.errorMessage.substring(firstIndex, lastIndex));
		this.lineNumberOnTestThatRevealsNC = aux.intValue();
	}	

	/**
	 * Method that returns the number of line where current error was found.
	 * @return Line where current test Error was found in the Test File.
	 */
	public int getLineNumberOnTestThatRevealsNC() {
		return this.lineNumberOnTestThatRevealsNC;
	}

	/**
	 * Get copy of line from the test file which generate the nonconformance.
	 * 
	 * @return copy of line from the test file which generate the
	 *         nonconformance.
	 */
	public String getSampleLineOfError() {
		return sampleLineOfError;
	}

	/**
	 * Set copy of line from the test file which generate the nonconformance.
	 */
	public void setSampleLineOfError() {
		int[] arr = {0};
		try {
			this.sampleLineOfError = AcessingTestCases
					.lineSampleWhoOriginatedError(this.testFile, this.lineNumberOnTestThatRevealsNC,
							this.numberedTest, arr);
			this.setCountOcurrencesLineOfError(arr[0]);
		} catch (IOException e) {
			this.sampleLineOfError = "";
		}
	}

	/**
	 * Get number of ocurrences of instruction that generated Error, before it
	 * crashes.
	 * 
	 * @return number of ocurrences of instruction that generated Error, before
	 *         it crashes.
	 */
	public int getCountOcurrencesLineOfError() {
		return countOcurrencesLineOfError;
	}

	/**
	 * Set number of ocurrences of instruction that generated Error, before it
	 * crashes.
	 * 
	 * @param countOcurrencesLineOfError
	 *            number of ocurrences of instruction that generated Error,
	 *            before it crashes.
	 */
	public void setCountOcurrencesLineOfError(int countOcurrencesLineOfError) {
		this.countOcurrencesLineOfError = countOcurrencesLineOfError;
	}

	/**
	 * Get the package and the class which used the interface where error was thrown.
	 * @return the package and the class which used the interface where error was thrown.
	 */
	public String getPackageAndClassCalling() {
		return packageAndClassCalling;
	}
	
	/**
	 * Set the package and the class which used the interface where error was thrown.
	 */
	public void setPackageAndClassCalling() {
		int firstIndex = 0, temp = 0;
		if(getType().equals(CategoryType.CONSTRAINT)){
			while((temp = this.errorMessage.indexOf("$JmlSurrogate", firstIndex + 1)) != -1){
				firstIndex = temp;
			}
			if(firstIndex != 0){
				firstIndex = this.errorMessage.indexOf("at ", firstIndex);
				int lastIndex = this.errorMessage.indexOf(".", firstIndex);
				this.packageAndClassCalling += this.errorMessage.substring(firstIndex + 3, lastIndex);
				firstIndex = lastIndex + 1;
				lastIndex = this.errorMessage.indexOf(".", firstIndex);
				this.packageAndClassCalling += "." + this.errorMessage.substring(firstIndex, lastIndex);
			}
		}
	}
	
	/**
	 * Get the message displayed on the error log of the nonconformance.
	 * 
	 * @return the message displayed on the error log of the nonconformance.
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * Set the message displayed on the error log of the nonconformance.
	 * 
	 * @param message
	 *            The message displayed on the error log of the nonconformance.
	 */
	public void setErrorMessage(String message) {
		this.errorMessage = message;
	}

	/**
	 * Get the method definition which nonconformance was generated, for method
	 * overridden issues, this is necessary. This differentiate invariant,
	 * constraint and evaluation errors, where the line was explicit in error
	 * log.
	 * 
	 * @return the method definition which nonconformance was generated.
	 */
	public String getMethodCalling() {
		return this.methodCalling;
	}

	/**
	 * Set the method definition which nonconformance was generated, for method
	 * overridden issues, this is necessary. This differentiate invariant,
	 * constraint and evaluation errors, where the line was explicit in error
	 * log.
	 * 
	 * @param sourceFolder
	 *            The folder where the .java of the project are.
	 */
	public void setMethodCalling(String sourceFolder) {
		if (this.lineNumberOnJavaThatRevealsNC == -1)
			this.methodCalling = "";
		else if (this.lineNumberOnJavaThatRevealsNC == 0)
			this.methodCalling = "defaultConstructor is not explicit";
		else {
			String name = (this.packageName + "." + this.className).replace(
					'.', '/');
			name += ".java";
			this.methodCalling = AcessingTestCases.readSingleLineOfFile(
					sourceFolder + Constants.FILE_SEPARATOR + name,
					this.lineNumberOnJavaThatRevealsNC);
			int temp = this.methodCalling.lastIndexOf("{");
			if (temp != -1)
				this.methodCalling = this.methodCalling.substring(0,
						this.methodCalling.lastIndexOf("{")).trim();
		}
		if (this.methodCalling.contains(this.getClassName())) {
			this.methodCalling = this.methodCalling.replaceFirst(
					this.className, "<init>");
		}
	}

	/**
	 * Get a sequence of calling methods whom shows error of nonconformance.
	 * 
	 * @return a sequence of calling methods whom shows error of nonconformance.
	 */
	public List<String> getStackTraceOrder() {
		return this.stackTraceOrder;
	}

	/**
	 * Set in a list, sequence of calling methods whom shows error of
	 * nonconformance.
	 * 
	 * @param methodsList
	 *            The possible methods to be called.
	 */
	public void setStackTraceOrder(List<String> methodsList) {
		StringReader sr = new StringReader(this.errorMessage);
		BufferedReader buf = new BufferedReader(sr);
		try {
			String firstState = this.packageName + "." + this.className + "."
					+ this.methodName;
			String lastState = this.testFile.substring(0,
					this.testFile.indexOf(".java"))
					+ "." + this.numberedTest;
			String initString = "&lt;init&gt";

			this.stackTraceOrder.add(firstState);

			buf.readLine();
			while (buf.ready()) {
				String info = buf.readLine();

				int begin = info.indexOf("at ");
				int last = info.indexOf("(", begin);
				String trace;
				try {
					trace = info.substring(begin + 3, last);
				} catch (IndexOutOfBoundsException e) {
					// There are some lines on message board, that will not
					// contain
					// at Class.method () structure, so we ignore it.
					continue;
				}
				if (trace.equals(lastState)) {
					this.stackTraceOrder.add(lastState);
					return;
				} else if (trace.matches(initString)) {
					int left = trace.indexOf(".") + 1;
					int right = trace.indexOf(".", left);
					this.stackTraceOrder.add(trace.replaceAll(initString,
							trace.substring(left, right)));
				} else {
					for (int i = 0; i < methodsList.size(); i++) {
						if (trace.equals(methodsList.get(i))) {
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

	/**
	 * Get the line where error was thrown in the java file. Useful for invariant NC differentiation.
	 * @return the line where error was thrown in the java file.
	 */
	public int getLineNumberOnJavaThatRevealsNC() {
		return lineNumberOnJavaThatRevealsNC;
	}

	/**
	 * Set the line where error was thrown in the java file. Useful for invariant NC differentiation.
	 * @param message Details on method that calls this function.
	 */
	public void setLineNumberOnJavaThatRevealsNC(String message) {
		int firstIndex = message.indexOf("line ");
		if(firstIndex == -1)
			this.lineNumberOnJavaThatRevealsNC = firstIndex;
		else{
			int temp = message.indexOf(',', firstIndex + 5);
			int lastIndex = (temp == -1)? 0 : temp;
			this.lineNumberOnJavaThatRevealsNC = (lastIndex == 0)? 0 : Integer.parseInt(message.substring(firstIndex + 5, lastIndex));
		}
	}
	
	public String getIdentifier(){
		return this.type.getName() + "." + this.packageName + "." + this.className + "." + this.methodName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if((obj instanceof Nonconformance) 
				&& ((Nonconformance) obj).getIdentifier().equalsIgnoreCase(this.getIdentifier())) 
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
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public String getTestCaseCode() {
		return testCaseCode;
	}

	public void setTestCaseCode(String testCaseMethod) {
		this.testCaseCode = testCaseMethod;
	}

	public boolean isJmlRac() {
		return jmlRac;
	}

	public void setJmlRac(boolean jmlRac) {
		this.jmlRac = jmlRac;
	}

	public boolean isMeaningless() {
		return meaningless;
	}

	public void setMeaningless(boolean meaningless) {
		this.meaningless = meaningless;
	}

	/**
	 * Method that returns if the current error is a nonconformance.
	 * @return = if current error is a nonconformance. 
	 */
	public boolean isNonconformance(){
		return (isJmlRac() && !isMeaningless());
	}
}