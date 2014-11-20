package categorize;

import java.util.List;
import java.util.Set;

import utils.commons.Constants;
import utils.commons.GenerateResult;
import utils.datastructure.Nonconformance;

/**
 * Class used to categorize the nonconformances discovered into the SUT.
 * @author Alysson Milanez and Dennis Sousa.
 */
public class Categorize {

	private Examinator examine; 
	private List<String> methodsList;
	
	public enum Cause {
		STRONG_PRE("Strong Precondition"), WEAK_PRE("Weak Precondition"), STRONG_POST("Strong Postcondition"), 
		WEAK_POST("Weak Postcondition"), STRONG_INV("Strong Invariant"), STRONG_CONST("Strong Constraint"),
		NOT_EVAL_EXP("Cannot be Evaluated"), BAD_FORMMED_EXP("Incorrect Expression"), NULL_RELATED("Null-Related - Code Error");

		private String name;

		private Cause(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
	
	/**
	 * Method that receives the set of nonconformances, and the source folder and returns a set of 
	 * nonconformances with category and likely cause. This is the principal method of the Categorize 
	 * module, because in this method we categorize all nonconformances discovered in Detect module.
	 * @param errors - The set of nonconformances detected by the Detect module.
	 * @param sourceFolder - The source folder of the SUT.
	 * @return a set of nonconformances with categories and likely causes.
	 */
	public Set<Nonconformance> categorize(Set<Nonconformance> errors, String sourceFolder){
		this.examine = new Examinator(sourceFolder);
		this.methodsList = this.examine.generatePossibleMethodsList(Constants.CLASSES);
		for(Nonconformance n : errors){
			switch (n.getType()) {
			case PRECONDITION:
				n.setLineNumberOnTestThatRevealsNC();
				n.setPackageAndClassCalling();
				n.setMethodCalling(sourceFolder);
				n.setCause(categorizePrecondition(n, sourceFolder));
				n.setSampleLineOfError();
				n.setStackTraceOrder(this.methodsList);
				n.setTestCaseCode(this.examine.showsMethodCode(n));
				break;
				
			case POSTCONDITION:
				n.setLineNumberOnTestThatRevealsNC();
				n.setPackageAndClassCalling();
				n.setMethodCalling(sourceFolder);
				n.setCause(categorizePostcondition(n, sourceFolder));
				n.setSampleLineOfError();
				n.setStackTraceOrder(this.methodsList);
				n.setTestCaseCode(this.examine.showsMethodCode(n));
				break;

			case INVARIANT:
				n.setLineNumberOnTestThatRevealsNC();
				n.setPackageAndClassCalling();
				n.setMethodCalling(sourceFolder);
				n.setCause(categorizeInvariant(n, sourceFolder));
				n.setSampleLineOfError();
				n.setStackTraceOrder(this.methodsList);
				n.setTestCaseCode(this.examine.showsMethodCode(n));
				break;
				
			case CONSTRAINT:
				n.setLineNumberOnTestThatRevealsNC();
				n.setPackageAndClassCalling();
				n.setMethodCalling(sourceFolder);
				n.setCause(categorizeConstraint(n, sourceFolder));
				n.setSampleLineOfError();
				n.setStackTraceOrder(this.methodsList);
				n.setTestCaseCode(this.examine.showsMethodCode(n));
				break;
				
			case EVALUATION:
				n.setLineNumberOnTestThatRevealsNC();
				n.setPackageAndClassCalling();
				n.setMethodCalling(sourceFolder);
				n.setCause(categorizeEvaluation(n, sourceFolder));				
				n.setSampleLineOfError();
				n.setStackTraceOrder(this.methodsList);
				n.setTestCaseCode(this.examine.showsMethodCode(n));
				break;
				
			default:
				break;
			}
		}
		GenerateResult.generateResult(errors);
		return errors;
	}
	
	/**
	 * Method that returns a likely cause for a nonconformance of precondition. Receives a test 
	 * error - the nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - The nonconformance.
	 * @param sourceFolder - The folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this precondition error.
	 */
	private String categorizePrecondition(Nonconformance e, String sourceFolder){
		if(e.getPackageName() == "")
			this.examine.setPrincipalClassName(e.getClassName());
		else
			this.examine.setPrincipalClassName(e.getPackageName() + "." + e.getClassName());
		this.examine.setMethodCalling(e.getMethodCalling());
		if(this.examine.checkStrongPrecondition(e.getMethodName())) 
			return Cause.STRONG_PRE.getName();
		else 
			return Cause.WEAK_POST.getName();
	}
	
	/**
	 * Method that returns a likely cause for a nonconformance of postcondition. Receives a test 
	 * error - the nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - The nonconformance.
	 * @param sourceFolder - The folder that contains the class with a nonconformance..
	 * @return the string that corresponds the likely cause for this postcondition error.
	 */
	private String categorizePostcondition(Nonconformance e, String sourceFolder){
		if(e.getPackageName() == "")
			this.examine.setPrincipalClassName(e.getClassName());
		else
			this.examine.setPrincipalClassName(e.getPackageName() + "." + e.getClassName());
		this.examine.setMethodCalling(e.getMethodCalling());
		if(this.examine.checkWeakPrecondition(e.getMethodName()))
			return Cause.WEAK_PRE.getName();
		else
			return Cause.STRONG_POST.getName();
	}
	
	/**
	 * Method that returns a likely cause for a nonconformance of invariant. Receives a test 
	 * error - the nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - The nonconformance
	 * @param sourceFolder - The folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this invariant error.
	 */
	private String categorizeInvariant(Nonconformance e, String sourceFolder){
		if(e.getPackageName() == "")
			this.examine.setPrincipalClassName(e.getClassName());
		else 
			this.examine.setPrincipalClassName(e.getPackageName() + "." + e.getClassName());
		this.examine.setMethodCalling(e.getMethodCalling());
		if(!e.getMethodCalling().contains("<init>") && !e.getMethodCalling().contains("defaultConstructor")){
			if(e.getErrorMessage().contains("@pre"))
				return Cause.NULL_RELATED.getName();
			else{
				if(this.examine.checkWeakPrecondition(e.getMethodName()))
					return Cause.WEAK_PRE.getName();
				else
					return Cause.STRONG_INV.getName();
			}
		}else{
			if(this.examine.checkNull(e.getMethodName())) 
				return Cause.NULL_RELATED.getName();
			else if(this.examine.checkWeakPrecondition(e.getMethodName())) 
				return Cause.WEAK_PRE.getName();
			else 
				return Cause.STRONG_INV.getName();
		}
	}
	
	/**
	 * Method that returns a likely cause for a nonconformance of history constraint. Receives a test 
	 * error - the nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - The nonconformance.
	 * @param sourceFolder - The folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this history constraint error.
	 */
	private String categorizeConstraint(Nonconformance e, String sourceFolder){
		String classInvolved = (e.getPackageAndClassCalling().equals("")) ? 
							   ((e.getPackageName() == "") ? (e.getClassName())
							 : (e.getPackageName() + "." + e.getClassName()))
							 : (e.getPackageAndClassCalling());
		this.examine.setPrincipalClassName(classInvolved);
		this.examine.setMethodCalling(e.getMethodCalling());
		if(this.examine.checkNull(e.getMethodName())) 
			return Cause.NULL_RELATED.getName();
		else if(this.examine.checkWeakPrecondition(e.getMethodName())) 
			return Cause.WEAK_PRE.getName();
		else 
			return Cause.STRONG_CONST.getName();
	}
	
	/**
	 * Method that returns a likely cause for a nonconformance of evaluation. Receives a test
	 * error - The nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - The nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this evaluation error.
	 */
	private String categorizeEvaluation(Nonconformance e, String sourceFolder){
		if(e.getPackageName() == "")
			this.examine.setPrincipalClassName(e.getClassName());
		else
			this.examine.setPrincipalClassName(e.getPackageName() + "." + e.getClassName());
		this.examine.setMethodCalling(e.getMethodCalling());
		if(this.examine.checkWeakPrecondition(e.getMethodName()))
			return Cause.WEAK_PRE.getName();
		else
			return Cause.STRONG_POST.getName();
	}
	
}
