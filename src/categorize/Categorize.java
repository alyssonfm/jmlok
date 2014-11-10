package categorize;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.Constants;
import detect.TestError;

/**
 * Class used to categorize the nonconformances discovered into the SUT.
 * @author Alysson Milanez and Dennis Sousa.
 */
public class Categorize {

	private Examinator examine; 
	private List<String> methodsList;
	
	/**
	 * Method that receives the set of nonconformances, and the source folder and returns a set of 
	 * nonconformances with category and likely cause. This is the principal method of the Categorize 
	 * module, because in this method we categorize all nonconformances discovered in Detect module.
	 * @param errors - The set of nonconformances detected by the Detect module.
	 * @param sourceFolder - The source folder of the SUT.
	 * @return a set of nonconformances with categories and likely causes.
	 */
	public Set<Nonconformance> categorize(Set<TestError> errors, String sourceFolder){
		Set<Nonconformance> nonconformances = new HashSet<Nonconformance>();
		this.examine = new Examinator(sourceFolder);
		this.methodsList = this.examine.generatePossibleMethodsList(Constants.CLASSES);
		for(TestError te : errors){
			Nonconformance n = new Nonconformance();
			switch (te.getType()) {
			case CategoryName.PRECONDITION:
				n.setClassName(te.getClassName());
				n.setMethodName(te.getMethodName());
				n.setPackageName(te.getPackageName());
				n.setType(new Precondition());
				n.setTest(te.getName());
				n.setMessage(te.getMessage());
				n.setMethodCalling(te.getLineOfErrorInJava(), sourceFolder);
				n.setCause(categorizePrecondition(te, sourceFolder, n.getMethodCalling()));
				n.setTestFile(te.getTestFile());
				n.setSampleLineOfError(te.getNumberRevealsNC());
				n.setStackTraceOrder(this.methodsList);
				nonconformances.add(n);
				break;
				
			case CategoryName.POSTCONDITION:
				n.setClassName(te.getClassName());
				n.setMethodName(te.getMethodName());
				n.setPackageName(te.getPackageName());
				n.setType(new Postcondition());
				n.setTest(te.getName());
				n.setMessage(te.getMessage());
				n.setMethodCalling(te.getLineOfErrorInJava(), sourceFolder);
				n.setCause(categorizePostcondition(te, sourceFolder, n.getMethodCalling()));
				n.setTestFile(te.getTestFile());
				n.setSampleLineOfError(te.getNumberRevealsNC());
				n.setStackTraceOrder(this.methodsList);
				nonconformances.add(n);
				break;

			case CategoryName.INVARIANT:
				n.setClassName(te.getClassName());
				n.setMethodName(te.getMethodName());
				n.setPackageName(te.getPackageName());
				n.setType(new Invariant());
				n.setTest(te.getName());
				n.setMessage(te.getMessage());
				n.setMethodCalling(te.getLineOfErrorInJava(), sourceFolder);
				n.setCause(categorizeInvariant(te, sourceFolder, n.getMethodCalling()));
				n.setTestFile(te.getTestFile());
				n.setSampleLineOfError(te.getNumberRevealsNC());
				n.setStackTraceOrder(this.methodsList);
				nonconformances.add(n);
				break;
				
			case CategoryName.CONSTRAINT:
				n.setClassName(te.getClassName());
				n.setMethodName(te.getMethodName());
				n.setPackageName(te.getPackageName());
				n.setType(new Constraint());
				n.setTest(te.getName());
				n.setMessage(te.getMessage());
				n.setMethodCalling(te.getLineOfErrorInJava(), sourceFolder);
				n.setCause(categorizeConstraint(te, sourceFolder, n.getMethodCalling()));
				n.setTestFile(te.getTestFile());
				n.setSampleLineOfError(te.getNumberRevealsNC());
				n.setStackTraceOrder(this.methodsList);
				nonconformances.add(n);
				break;
				
			case CategoryName.EVALUATION:
				n.setClassName(te.getClassName());
				n.setMethodName(te.getMethodName());
				n.setPackageName(te.getPackageName());
				n.setType(new Evaluation());
				n.setTest(te.getName());
				n.setMessage(te.getMessage());
				n.setMethodCalling(te.getLineOfErrorInJava(), sourceFolder);
				n.setCause(categorizeEvaluation(te, sourceFolder, n.getMethodCalling()));				
				n.setTestFile(te.getTestFile());
				n.setSampleLineOfError(te.getNumberRevealsNC());
				n.setStackTraceOrder(this.methodsList);
				nonconformances.add(n);
				break;
				
			default:
				break;
			}
		}
		return nonconformances;
	}
	
	/**
	 * Method that returns a likely cause for a nonconformance of precondition. Receives a test 
	 * error - the nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - The nonconformance.
	 * @param sourceFolder - The folder that contains the class with a nonconformance.
	 * @param methodCalling - The string that will be contained on the method declaration(for validation). 
	 * @return the string that corresponds the likely cause for this precondition error.
	 */
	private String categorizePrecondition(TestError e, String sourceFolder, String methodCalling){
		if(e.getPackageName() == "")
			this.examine.setPrincipalClassName(e.getClassName());
		else
			this.examine.setPrincipalClassName(e.getPackageName() + "." + e.getClassName());
		this.examine.setMethodCalling(methodCalling);
		if(this.examine.checkStrongPrecondition(e.getMethodName())) 
			return Cause.STRONG_PRE;
		else 
			return Cause.WEAK_POST;
	}
	
	/**
	 * Method that returns a likely cause for a nonconformance of postcondition. Receives a test 
	 * error - the nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - The nonconformance.
	 * @param sourceFolder - The folder that contains the class with a nonconformance..
	 * @param methodCalling - The string that will be contained on the method declaration(for validation). 
	 * @return the string that corresponds the likely cause for this postcondition error.
	 */
	private String categorizePostcondition(TestError e, String sourceFolder, String methodCalling){
		if(e.getPackageName() == "")
			this.examine.setPrincipalClassName(e.getClassName());
		else
			this.examine.setPrincipalClassName(e.getPackageName() + "." + e.getClassName());
		this.examine.setMethodCalling(methodCalling);
		if(this.examine.checkWeakPrecondition(e.getMethodName()))
			return Cause.WEAK_PRE;
		else
			return Cause.STRONG_POST;
	}
	
	/**
	 * Method that returns a likely cause for a nonconformance of invariant. Receives a test 
	 * error - the nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - The nonconformance
	 * @param sourceFolder - The folder that contains the class with a nonconformance.
	 * @param methodCalling - The string that will be contained on the method declaration(for validation). 
	 * @return the string that corresponds the likely cause for this invariant error.
	 */
	private String categorizeInvariant(TestError e, String sourceFolder, String methodCalling){
		if(e.getPackageName() == "")
			this.examine.setPrincipalClassName(e.getClassName());
		else 
			this.examine.setPrincipalClassName(e.getPackageName() + "." + e.getClassName());
		this.examine.setMethodCalling(methodCalling);
		if(!methodCalling.contains("<init>") && !methodCalling.contains("defaultConstructor")){
			if(e.getMessage().contains("@pre"))
				return Cause.NULL_RELATED;
			else{
				if(this.examine.checkWeakPrecondition(e.getMethodName()))
					return Cause.WEAK_PRE;
				else
					return Cause.STRONG_INV;
			}
		}else{
			if(this.examine.checkNull(e.getMethodName())) 
				return Cause.NULL_RELATED;
			else if(this.examine.checkWeakPrecondition(e.getMethodName())) 
				return Cause.WEAK_PRE;
			else 
				return Cause.STRONG_INV;
		}
	}
	
	/**
	 * Method that returns a likely cause for a nonconformance of history constraint. Receives a test 
	 * error - the nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - The nonconformance.
	 * @param sourceFolder - The folder that contains the class with a nonconformance.
	 * @param methodCalling - The string that will be contained on the method declaration(for validation). 
	 * @return the string that corresponds the likely cause for this history constraint error.
	 */
	private String categorizeConstraint(TestError e, String sourceFolder, String methodCalling){
		String classInvolved = (e.getPackageAndClassCalling().equals("")) ? 
							   ((e.getPackageName() == "") ? (e.getClassName())
							 : (e.getPackageName() + "." + e.getClassName()))
							 : (e.getPackageAndClassCalling());
		this.examine.setPrincipalClassName(classInvolved);
		this.examine.setMethodCalling(methodCalling);
		if(this.examine.checkNull(e.getMethodName())) 
			return Cause.NULL_RELATED;
		else if(this.examine.checkWeakPrecondition(e.getMethodName())) 
			return Cause.WEAK_PRE;
		else 
			return Cause.STRONG_CONST;
	}
	
	/**
	 * Method that returns a likely cause for a nonconformance of evaluation. Receives a test
	 * error - The nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - The nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @param methodCalling - The string that will be contained on the method declaration(for validation). 
	 * @return the string that corresponds the likely cause for this evaluation error.
	 */
	private String categorizeEvaluation(TestError e, String sourceFolder, String methodCalling){
		if(e.getPackageName() == "")
			this.examine.setPrincipalClassName(e.getClassName());
		else
			this.examine.setPrincipalClassName(e.getPackageName() + "." + e.getClassName());
		this.examine.setMethodCalling(methodCalling);
		if(this.examine.checkWeakPrecondition(e.getMethodName()))
			return Cause.WEAK_PRE;
		else
			return Cause.STRONG_POST;
	}
	
}
