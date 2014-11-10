package categorize;

/**
 * Class used to set the cause name of each nonconformance revealed by the Detect module.
 * @author Alysson Milanez and Dennis Sousa.
 *
 */
public class Cause {
	public static final String STRONG_PRE = "Strong Precondition";
	public static final String WEAK_PRE = "Weak Precondition";
	public static final String STRONG_POST = "Strong Postcondition";
	public static final String WEAK_POST = "Weak Postcondition";
	public static final String STRONG_INV = "Strong Invariant";
	public static final String STRONG_CONST = "Strong Constraint";
	public static final String NOT_EVAL_EXP = "Cannot be Evaluated";
	public static final String BAD_FORMMED_EXP = "Incorrect Expression";
	public static final String NULL_RELATED = "Null-Related - Code Error";
}
