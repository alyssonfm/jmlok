package categorize;

import java.util.ArrayList;
import java.util.List;

import org.jmlspecs.openjml.Factory;
import org.jmlspecs.openjml.IAPI;
import org.jmlspecs.openjml.JmlToken;
import org.jmlspecs.openjml.JmlTree;
import org.jmlspecs.openjml.JmlTree.JmlClassDecl;
import org.jmlspecs.openjml.JmlTree.JmlMethodClause;
import org.jmlspecs.openjml.JmlTree.JmlMethodClauseExpr;
import org.jmlspecs.openjml.JmlTree.JmlMethodDecl;
import org.jmlspecs.openjml.JmlTree.JmlSingleton;
import org.jmlspecs.openjml.JmlTree.JmlVariableDecl;

import utils.Constants;
import utils.FileUtil;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCAssignOp;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCUnary;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

/**
 * Class used to do some operations on the AST that we use to 
 * determinate which is the likely cause for a nonconformance.
 * 
 * @author Alysson Milanez and Dennis Sousa.
 * 
 */
public class Examinator {
	
	private static final int INF = Integer.MAX_VALUE;
	private static final int VAR_FALSE_VALUE = -INF;
	private String srcDir = "";
	private String principalClassName = "";
	private String methodCalling = "";
	private ArrayList<String> variables;
	private boolean isAllVarUpdated = false;
	
	/**
	 * Declares some constants that will be used in Examinator for indicate which verification
	 * the class are doing.
	 * @author Alysson Milanez and Dennis Sousa.
	 *
	 */
	public enum Operations {
		ATR_VAR_IN_PRECONDITION, REQUIRES_TRUE, ATR_MOD, ISNT_NULL_RELATED, ENSURES_TRUE 		
	}
	
	/**
	 * Constructs an PatternsTool object with the directory of the source project paste.
	 * 
	 * @param dir
	 *            directory of the source project paste
	 */
	public Examinator(String dir) {
		this.srcDir = dir;
	}
	
	/**
	 * Get the complete name of the principal class examined. 
	 * @return complete name of the principal class examined.
	 */
	public String getPrincipalClassName() {
		return principalClassName;
	}
	
	/**
	 * Change the principal class examined from this object.
	 * @param principalClassName the principal class name examined from this object.
	 */
	public void setPrincipalClassName(String principalClassName) {
		this.principalClassName = principalClassName;
		this.variables = FileUtil.getVariablesFromClass(principalClassName);
		this.isAllVarUpdated = false;
	}
	
	/**
	 * Declares isAllVarUpdated to default value, for class variable analysis.
	 */
	public void resetIsAllVarUpdated(){
		this.isAllVarUpdated = false;
	}
	
	/**
	 * Method that gets the class name and return it without the package names.
	 * @param className The name of the class.
	 * @return the class name without the package name.
	 */
	private String getOnlyClassName(String className){
		return className.substring(className.lastIndexOf(".") + 1);
	}
	
	/**
	 * Method that returns the complete java path to class, whose class name was
	 * received as parameter.
	 * 
	 * @param className
	 *            - the name of the class.
	 * @return - the full path to the file.
	 */
	private String getJavaPathFromFile(String className) {
		String name = className.replace('.', '/');
		name += ".java";
		return srcDir + Constants.FILE_SEPARATOR + name;
	}
	/**
	 * Method that returns the complete jml path to class, whose class name was
	 * received as parameter.
	 * 
	 * @param className
	 *            - the name of the class.
	 * @return - the full path to the file.
	 */
	private String getJmlPathFromFile(String className) {
		String name = className.replace('.', '/');
		name += ".jml";
		return srcDir + Constants.FILE_SEPARATOR + name;
	}
	
	/**
	 * This method creates an File object desired with class name and type of the
	 * file, if it's jml or java file.
	 * @param classname Complete name of the class desired to examine.
	 * @param isJMLFile If it's an jml or java file.
	 * @return The File created.
	 */
	private java.io.File getFileToInvestigate(String classname, boolean isJMLFile) {
		if(isJMLFile)
			return new java.io.File(getJmlPathFromFile(classname));
		else
			return new java.io.File(getJavaPathFromFile(classname));
	}
	
	/**
	 * Method that add variables from superclass not added before to variables
	 * list of the class.
	 * @param classname Name of the class desired to update the variables from.
	 */
	private void updateVariables(String classname) {
		if(!classname.equals(this.getPrincipalClassName()))
			for(String s : FileUtil.getVariablesFromClass(classname))
				if(!this.variables.contains(s))
					this.variables.add(s);
	}
	
	/**
	 * Get the method calling identifier.
	 * @return The method calling identifier.
	 */
	public String getMethodCalling() {
		return methodCalling;
	}
	
	/**
	 * Set the method calling identifier.
	 * @param methodCalling The method calling identifier.
	 */
	public void setMethodCalling(String methodCalling) {
		this.methodCalling = methodCalling;
	}
	
	/**
	 * Checks if the Precondition clauses from a method are too strong.
	 * @param methodName Name of the method studied.
	 * @return true if the Precondition clauses are too strong, or false.
	 */
	public boolean checkStrongPrecondition(String methodName){
		this.setMethodCalling("");
		if(methodName.equals(getOnlyClassName(this.getPrincipalClassName())))
			methodName = "<init>";
		try {
			return examineJavaAndJMLCode(this.getPrincipalClassName(), methodName, false, Operations.ATR_VAR_IN_PRECONDITION);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if the Precondition clauses from a method are too weak.
	 * @param methodName Name of the method studied.
	 * @return true if the Precondition clauses are too weak, false otherwise.
	 */
	public boolean checkWeakPrecondition(String methodName) {
		this.setMethodCalling("");
		if(methodName.equals(getOnlyClassName(this.getPrincipalClassName())))
			methodName = "<init>";
		try {
			if(examineJavaAndJMLCode(this.getPrincipalClassName(), methodName, false, Operations.REQUIRES_TRUE))
				return true;
			if(examineJavaAndJMLCode(this.getPrincipalClassName(), methodName, false, Operations.ATR_MOD)) 
				return true;
			if(examineJavaAndJMLCode(this.getPrincipalClassName(), methodName, false, Operations.ENSURES_TRUE))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if the nonconformance where caused by some variable with null value.
	 * @param methodName Name of the method studied.
	 * @return if the nonconformance are null-related.
	 */
	public boolean checkNull(String methodName){
		this.setPrincipalClassName(this.getPrincipalClassName());
		try {
			if(!examineJavaAndJMLCode(this.principalClassName, "<init>", false, Operations.ISNT_NULL_RELATED)){
				return true;
			}
		} catch (Exception e) {
				e.printStackTrace();
		}
		this.resetIsAllVarUpdated();
		return false;
	}
	
	/**
	 * Realize some examinations on clauses in a desired class,
	 * and its related class, like interfaces or superclasses. 
	 * 
	 * @param className
	 *            name of the class searched.
	 * @param methodName
	 *            name of the method searched.
	 * @param isJMLFile
	 * @param typeOfExamination
	 * 			  operation desired to do on the method.
	 * @return true if the conditions of the desired method
	 * 	          where fulfilled.
	 * @throws Exception When the API can't be created.
	 */
	private boolean examineJavaAndJMLCode(String className, String methodName, boolean isJMLFile, Operations typeOfExamination) throws Exception {
		JmlClassDecl ourClass = takeClassFromFile(getFileToInvestigate(className, isJMLFile), className);
		if(ourClass == null)
			return false;
		updateVariables(className);
		if(!isJMLFile && examineAllClassAssociated(className, methodName, typeOfExamination))
			return true;
		if(isAllVarUpdated && typeOfExamination == Operations.ISNT_NULL_RELATED)
			verifyVarInitializedOutsideMethods(ourClass);
		return examineMethods(takeMethodsFromClass(ourClass, methodName), typeOfExamination);
	}
	
	/**
	 * Examine various methods to do some type of Operation.
	 * @param ourMethods The methods selected for examination.
	 * @param typeOfExamination Type of examination.
	 * @return true If the specified examination returns true.
	 * @throws Exception When the code were bad formulated.
	 */
	private boolean examineMethods(List<JmlMethodDecl> ourMethods, Operations typeOfExamination) throws Exception{
		if(ourMethods != null){
			for (JmlMethodDecl anMethod : ourMethods) {
				if(typeOfExamination == Operations.ATR_MOD || typeOfExamination == Operations.ISNT_NULL_RELATED){
					if(examineCodeFromMethod(anMethod, typeOfExamination))
						return true;
				}if (anMethod.cases != null){
					if(typeOfExamination == Operations.ATR_VAR_IN_PRECONDITION || typeOfExamination == Operations.REQUIRES_TRUE || typeOfExamination == Operations.ENSURES_TRUE){ 
						if(examineSpecifiedPrePostClause(typeOfExamination, anMethod,anMethod.cases.cases.head.clauses))
							return true;
					}
				}else if(typeOfExamination == Operations.REQUIRES_TRUE){
					return true;
				}
			}
		}
		if(typeOfExamination == Operations.ISNT_NULL_RELATED)
			return this.variables.isEmpty();
		return false;
	}
	
	/**
	 * Examine an Requires or Ensures Clause from an method to do some operations.
	 * @param typeOfExamination The type of examination used.
	 * @param anMethod The method searched.
	 * @param clauses The clauses from the method.
	 * @return true If the examination has returned true.
	 * @throws Exception For bad-code formulation.
	 */
	private boolean examineSpecifiedPrePostClause(Operations typeOfExamination, JmlMethodDecl anMethod,
			com.sun.tools.javac.util.List<JmlMethodClause> clauses) throws Exception {
		boolean isRequiresClauseNotFounded = true, isEnsuresClauseNotFounded = true;
		for (com.sun.tools.javac.util.List<JmlMethodClause> traversing = clauses; !traversing.isEmpty(); traversing = traversing.tail) {
			if (traversing.head.token.equals(JmlToken.REQUIRES)) {
				isRequiresClauseNotFounded = false;
				switch (typeOfExamination) {
				case ATR_VAR_IN_PRECONDITION:
					if(isAtrAndVarInPrecondition(anMethod, traversing))
						return true;
					break;
				case REQUIRES_TRUE:
					if(isClauseTrue(traversing))
						return true;
					break;
				default:
					break;
				}
			}
			if (traversing.head.token.equals(JmlToken.ENSURES)){
				isEnsuresClauseNotFounded = false;
				if(typeOfExamination.equals(Operations.ENSURES_TRUE)){
					if(isClauseTrue(traversing))
						return true;
				}
			}
		}
		if(typeOfExamination == Operations.REQUIRES_TRUE)
			return isRequiresClauseNotFounded;
		if(typeOfExamination == Operations.ENSURES_TRUE)
			return isEnsuresClauseNotFounded;
		return false;
	}
	
	/**
	 * Examine if the attribute or variable from a Class method are on the requires clauses.
	 * @param anMethod The method examined.
	 * @param traversing The clause examined from the method.
	 * @return true If the attribute or variable was found.
	 */
	private boolean isAtrAndVarInPrecondition(JmlMethodDecl anMethod, com.sun.tools.javac.util.List<JmlMethodClause> traversing){
		for (com.sun.tools.javac.util.List<JCVariableDecl> acessing = anMethod.params; 
				!acessing.isEmpty(); acessing = acessing.tail)
			if(verifyFieldsInClause(traversing, acessing.head.name.toString(), true))
				return true;
		for (String var : this.variables) 
			if(verifyFieldsInClause(traversing, var, false))
				return true;
		return false;
	}
	
	/**
	 * Examine if the method has some requires or ensures with value constantly true.
	 * @param traversing The clause to examine.
	 * @return true If the method has some requires or ensures with constant true value.
	 */
	private boolean isClauseTrue(com.sun.tools.javac.util.List<JmlMethodClause> traversing) {
		JCTree expression = ((JmlMethodClauseExpr) traversing.head).expression;
		return hasTrueValue(expression) != 0 && hasTrueValue(expression) != VAR_FALSE_VALUE;
	}
	
	/**
	 * Take the code from the method to perform some examination.
	 * @param anMethod The method to examine.
	 * @param typeOfExamination The type of examination.
	 * @return true If the examination returns it.
	 */
	private boolean examineCodeFromMethod(JmlMethodDecl anMethod, Operations typeOfExamination) {
		JCBlock block = anMethod.body;
		com.sun.tools.javac.util.List<JCVariableDecl> params = anMethod.params;
		switch (typeOfExamination) {
		case ATR_MOD:
			if(isSomeVarOtAtrGettingAttribution(params, block))
				return true;
			break;
		case ISNT_NULL_RELATED:
			if(isAllVariableInitialized(block))
				return true;
			break;
		default:
			break;
		}
		return false;
	}
	
	/**
	 * Verify if some variable from class or parameter from the method are receiving a value
	 * in an attribution.
	 * @param params The parameters from the method.
	 * @param block The code lines from the method.
	 * @return true If the method attribute value to some field.
	 */
	private boolean isSomeVarOtAtrGettingAttribution(com.sun.tools.javac.util.List<JCVariableDecl> params, JCBlock block) {
		for (com.sun.tools.javac.util.List<JCStatement> traversing = block.stats; !traversing.isEmpty(); traversing = traversing.tail){
			
			if(traversing.head instanceof JCExpressionStatement){
				if(verifyAttribution(params, ((JCExpressionStatement) traversing.head).expr))
					return true;
				else if(((JCExpressionStatement) traversing.head).expr instanceof JCMethodInvocation)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Verify if all variables were initialized on the constructor.
	 * @param block The lines of the constructor.
	 * @return true if all variables were initialized on the constructor.
	 */
	private boolean isAllVariableInitialized(JCBlock block) {
		for (com.sun.tools.javac.util.List<JCStatement> traversing = block.stats; !traversing.isEmpty(); traversing = traversing.tail){
			if(traversing.head instanceof JCExpressionStatement){
				if(((JCExpressionStatement) traversing.head).expr instanceof JCAssign 
				&& !((JCAssign) ((JCExpressionStatement) traversing.head).expr).rhs.toString().equals("null")){
					String toTest = ((JCAssign) ((JCExpressionStatement) traversing.head).expr).lhs.toString();		
					int i = 0; boolean isNecessaryToRemove = false;
					for(i = 0; i < this.variables.size(); i++){
						if(toTest.equals(this.variables.get(i)) || toTest.equals("this." + this.variables.get(i))){
							isNecessaryToRemove = true;
							break;
						}
					}
					if(isNecessaryToRemove)
						this.variables.remove(i);
				}
			}
		}
		return this.variables.isEmpty();
	}
	
	/**
	 * Take the Class desired from an file or return null.
	 * @param f File to search.
	 * @param className Name of the class to be searched.
	 * @return null if none class was found, otherwise the class founded.
	 */
	private JmlClassDecl takeClassFromFile(java.io.File f, String className){
		IAPI app;
		try {
			if(!f.exists())
				throw new Exception("The File acessed " + f.getName() + "does not exist.");
			app = Factory.makeAPI();
			List<JmlTree.JmlCompilationUnit> ast = app.parseFiles(f);
			com.sun.tools.javac.util.List<JCTree> acesser;
			JmlTree.JmlClassDecl ourClass = null;
			for (acesser = ast.get(0).defs; !acesser.isEmpty(); acesser = acesser.tail)
				if(acesser.head.getKind().equals(Tree.Kind.CLASS) || acesser.head.getKind().equals(Tree.Kind.INTERFACE)){
					ourClass = (JmlClassDecl) acesser.head;
					if(ourClass.name.toString().equals(getOnlyClassName(className)))
						return ourClass;
				}
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * Take all methods with the same name desired from an class, and return listed.
	 * @param clazz The class to walk.
	 * @param methodName The name of the method desired.
	 * @return An list of methods with the name desired.
	 */
	private List<JmlTree.JmlMethodDecl> takeMethodsFromClass(JmlTree.JmlClassDecl clazz, String methodName){
		List<JmlTree.JmlMethodDecl> methods = new ArrayList<JmlTree.JmlMethodDecl>();
		com.sun.tools.javac.util.List<JCTree> traverser;
		for (traverser = clazz.defs; !traverser.isEmpty(); traverser = traverser.tail)
			if(traverser.head.getKind().equals(Tree.Kind.METHOD) 
			   && ((JmlMethodDecl)traverser.head).name.toString().equals(methodName)
			   && (traverser.head.toString().contains(this.methodCalling.trim()))){
				methods.add((JmlMethodDecl) traverser.head);
			}
		return methods;
	}
	
	/**
	 * Take an class declaration and return a list with all Methods from there.
	 * @param clazz Class declaration examined.
	 * @return list with all methods from class examined.
	 */
	private List<String> takeMethodsList(JmlTree.JmlClassDecl clazz){
		List<String> methods = new ArrayList<>();
		com.sun.tools.javac.util.List<JCTree> traverser;
		try {
			for (traverser = clazz.defs; !traverser.isEmpty(); traverser = traverser.tail) {
				if(traverser.head.getKind().equals(Tree.Kind.METHOD))
					methods.add(((JmlMethodDecl) traverser.head).name.toString());
			}			
		} catch (Exception e) {
		}
		return methods;
		
	}
	
	/**
	 * Do an specified operation in all interfaces, superclasses
	 * or jml files associated with the .java who called it.
	 * @param className The name of the class who called it.
	 * @param methodName The method searched.
	 * @param typeOfExamination The operation made in the class.
	 * @return true if it in any of the operations returns it.
	 * @throws Exception If any Operation was bad-formulated on Code.
	 */
	private boolean examineAllClassAssociated(String className, String methodName, Operations typeOfExamination) throws Exception {
		if(typeOfExamination == Operations.ATR_VAR_IN_PRECONDITION || typeOfExamination == Operations.REQUIRES_TRUE){
			ArrayList<String> interfacesOfClass = FileUtil.getInterfacesPathFromClass(className);
			if(!interfacesOfClass.isEmpty())
				for (String i : interfacesOfClass)
					if(examineJavaAndJMLCode(i, methodName, false, typeOfExamination))
						return true;
		}
		String superClassOfClass = FileUtil.getSuperclassPathFromClass(className, srcDir);
		if(!(superClassOfClass == "")){
			if(examineJavaAndJMLCode(superClassOfClass, methodName, false, typeOfExamination))
				return true;
		}else
			isAllVarUpdated = true;
		if(typeOfExamination == Operations.ATR_VAR_IN_PRECONDITION || typeOfExamination == Operations.REQUIRES_TRUE){
			if(examineJavaAndJMLCode(className, methodName, true, typeOfExamination))
				return true;
		}
		return false;
	}
	
	/**
	 * Verify if some variable were already initialized on its declaration.
	 * @param ourClass The class to examine.
	 */
	private void verifyVarInitializedOutsideMethods(JmlClassDecl ourClass) {
		com.sun.tools.javac.util.List<JCTree> traverser;
		for (traverser = ourClass.defs; !traverser.isEmpty(); traverser = traverser.tail){
			if(traverser.head instanceof JmlVariableDecl) 
				if( ((JmlVariableDecl)traverser.head).vartype instanceof JCPrimitiveTypeTree){
					String toRemove = ((JmlVariableDecl)traverser.head).name.toString();
					this.variables.remove(this.variables.indexOf(toRemove));
				} else if(((JmlVariableDecl)traverser.head).init != null){
					if(((JmlVariableDecl)traverser.head).init instanceof JCLiteral){
						if(((JCLiteral)((JmlVariableDecl)traverser.head).init).value != null){
							String toRemove = ((JmlVariableDecl)traverser.head).name.toString();
							this.variables.remove(this.variables.indexOf(toRemove));			
						}
					}else{
						String toRemove = ((JmlVariableDecl)traverser.head).name.toString();
						this.variables.remove(this.variables.indexOf(toRemove));			
					}
				}
		}
	}
	
	/**
	 * Verify all components of the Method Clause, if it's JCBinary or JCIdent type, that there are at least
	 * one field equals to the String toTest given.
	 * @param traversing Method Clause to verify.
	 * @param toTest Name of the field to compare.
	 * @param isFieldParameterOfMethod true, if the type of the field are just methods parameters or false. 
	 * @return true if it was found any component of the clause equal the name of the field. 
	 */
	private boolean verifyFieldsInClause(com.sun.tools.javac.util.List<JmlMethodClause> traversing, String toTest, boolean isFieldParameterOfMethod) {
		if (isThereInMiniTree(toTest,((JmlMethodClauseExpr) traversing.head).expression))
			return true;
		if(!isFieldParameterOfMethod && (isThereInMiniTree("this." + toTest,((JmlMethodClauseExpr) traversing.head).expression)
									  || isThereInMiniTree(toTest, ((JmlMethodClauseExpr) traversing.head).expression)))
			return true;
		return false;
	}
	
	/**
	 * This function walks into a tree with nodes of type JCBinary to search for 
	 * a string as one of the elements of the tree.
	 * @param toTest The String searched as Element in the tree.
	 * @param expression The bifurcation tree searched.
	 * @return True if the string was found, or False.
	 */
	private boolean isThereInMiniTree(String toTest, JCTree expression) {
		if(expression instanceof JCParens){
			if(isThereInMiniTree(toTest, ((JCParens) expression).expr))
				return true;
		} else if(expression instanceof JCIdent){
			if(((JCIdent) expression).name.toString().equals(toTest))
				return true;
		} else if(expression instanceof JCBinary){
			if(isThereInMiniTree(toTest, ((JCBinary) expression).rhs))
				return true;
			if(isThereInMiniTree(toTest, ((JCBinary) expression).lhs))
				return true;
		}
		return false;
	}
	
	/**
	 * Verify if the boolean expression have a true value despite of any variable values. 
	 * @param expression The boolean expression examined.
	 * @return 1 When the expression is always true.
	 */
	private int hasTrueValue(JCTree expression) {
		if(expression instanceof JCParens){
			if(hasTrueValue(((JCParens)expression).expr) != 0)
				return 1;
		}else if(expression instanceof JCLiteral){
			if(((JCLiteral) expression).value == null)
				return VAR_FALSE_VALUE;
			return (int) ((JCLiteral) expression).value;
		}else if(expression instanceof JmlSingleton){
			return 1;
		}else if(expression instanceof JCBinary){
			return resolveBooleanOperations(expression);
		}
		return VAR_FALSE_VALUE;
	}
	
	/**
	 * Calculate the value from binary expression, always considering variables with false value.
	 * @param expression The binary boolean expression examined.
	 * @return 1 When the expression has value true. 
	 */
	private int resolveBooleanOperations(JCTree expression) {
		int rexp = hasTrueValue(((JCBinary) expression).rhs);
		int lexp = hasTrueValue(((JCBinary) expression).lhs);
		switch (((JCBinary) expression).getTag()) {
			case JCTree.OR:
				if((rexp != 0)&&(rexp != VAR_FALSE_VALUE) || (lexp != 0)&&(lexp != VAR_FALSE_VALUE))
					return 1;
				return 0;
			case JCTree.AND:
				if((rexp == 0)||(rexp == VAR_FALSE_VALUE) || (lexp == 0)||(lexp == VAR_FALSE_VALUE))
					return 0;
				return 1;
			case JCTree.EQ:
				if(rexp == VAR_FALSE_VALUE || lexp == VAR_FALSE_VALUE)
					return VAR_FALSE_VALUE;
				return ((rexp != 0) == (lexp != 0)) ? 1 : 0;
			case JCTree.NE:
				if(rexp == VAR_FALSE_VALUE || lexp == VAR_FALSE_VALUE)
					return VAR_FALSE_VALUE;
				return ((rexp != 0) != (lexp != 0)) ? 1 : 0;
			case JCTree.LT:
				if(rexp == VAR_FALSE_VALUE || lexp == VAR_FALSE_VALUE)
					return VAR_FALSE_VALUE;
				return (rexp < lexp) ? 1 : 0;
			case JCTree.GT:
				if(rexp == VAR_FALSE_VALUE || lexp == VAR_FALSE_VALUE)
					return VAR_FALSE_VALUE;
				return (rexp > lexp) ? 1 : 0;
			case JCTree.LE:
				if(rexp == VAR_FALSE_VALUE || lexp == VAR_FALSE_VALUE)
					return VAR_FALSE_VALUE;
				return (rexp <= lexp) ? 1 : 0;
			case JCTree.GE:
				if(rexp == VAR_FALSE_VALUE || lexp == VAR_FALSE_VALUE)
					return VAR_FALSE_VALUE;
				return (rexp >= lexp) ? 1 : 0;
			default:
				return 0;
		}
	}
	
	/**
	 * Verify if the expression is an assign and its assign to some value in parameters or variables from class.
	 * @param params The parameters from the method.
	 * @param expr The expression to analyze.
	 * @return true if the expression is an assign to variable or parameter of the method.
	 */
	private boolean verifyAttribution(com.sun.tools.javac.util.List<JCVariableDecl> params, JCExpression expr) {
		if(expr instanceof JCUnary){
			String toTest = ((JCUnary) expr).arg.toString();
			if(compareFieldsWith(params, toTest))
				return true;
		}else if(expr instanceof JCAssign){
			String toTest = ((JCAssign) expr).lhs.toString();
			if(compareFieldsWith(params, toTest))
				return true;
		}else if(expr instanceof JCAssignOp){
			String toTest = ((JCAssignOp) expr).lhs.toString();
			if(compareFieldsWith(params, toTest))
				return true;
		}
		return false;
	}
	
	/**
	 * Verify if the name given is some of the parameters of a method or variables from the class.
	 * @param params The parameters from the method to analyze.
	 * @param toTest The name to search.
	 * @return true If the name correspond to some of the fields.
	 */
	private boolean compareFieldsWith(com.sun.tools.javac.util.List<JCVariableDecl> params, String toTest) {
		for (com.sun.tools.javac.util.List<JCVariableDecl> acessing = params; !acessing.isEmpty(); acessing = acessing.tail)
			if(acessing.head.name.toString().equals(toTest))
				return true;
		for (String var : this.variables) 
			if(toTest.equals(var)){
				return true;
			}else if(toTest.equals("this."+var)){
				return true;
			}
		return false;
	}
	
	/**
	 * Generate an List with all possible pair within the classname given and its methods.
	 * @param classname Class path name of class to be examined.
	 * @return list with all possible pair within the classname given and its methods.
	 */
	private List<String> generateListAllClassMethodPair(String classname){
		List<String> methods = takeMethodsList(takeClassFromFile(getFileToInvestigate(classname, false), classname));
		List<String> classMethodPair = new ArrayList<String>();
		for (int index = 0; index < methods.size(); index++) {
			classMethodPair.add(classname + "." + methods.get(index));
		}
		return classMethodPair;
	}
	
	/**
	 * Generate all possible methods full path name from the classes listed
	 * within classesFilenName. 
	 * @param classesFileName Name of the file containing listing of classes.
	 * @return 
	 */
	public List<String> generatePossibleMethodsList(String classesFileName){
		List<String> methods = new ArrayList<String>();
		List<String> classes = generateClassList(classesFileName);
		for (int indexA = 0; indexA < classes.size(); indexA++) {
			List<String> aux = generateListAllClassMethodPair(classes.get(indexA));
			for (int indexB = 0; indexB < aux.size(); indexB++) {
				methods.add(aux.get(indexB));
			}
		}
		return methods;
	}

	/**
	 * Takes an path name for a File listing classes and then return its
	 * classes listed in a ArrayList.
	 * @param classesFileName File to be listed.
	 * @return list with all classes named in File.
	 */
	private List<String> generateClassList(String classesFileName){
		List<String> classList = new ArrayList<String>();
		String classNames = FileUtil.readFile(classesFileName);
		int index = 0;
		while(index < classNames.length()){
			int endInput = classNames.indexOf("\n" ,index);
			classList.add(classNames.substring(index, endInput));
			index = endInput + 1;
		}
		return classList;
	}
	
	/**
	 * Return code of method desired.
	 * @param file file of where method is located.
	 * @param classname class of where method is located.
	 * @param methodname name of method desired.
	 * @return code of method desired.
	 */
	public String showsMethodCode(java.io.File file, String classname, String methodname){
		JmlClassDecl ourClass = takeClassFromFile(file, classname);
		JmlMethodDecl anMethod = takeMethodsFromClass(ourClass, methodname).get(0);
		return anMethod.toString();
	}
	
}
