package categorize;

/**
 * An category of nonconformance, the Evaluation Error.
 * @author Alysson Milanez and Dennis Sousa.
 *
 */
public class Evaluation implements Category{

	public String causeToString() {
		return Cause.NOT_EVAL_EXP;
	}

	public String getType() {
		return CategoryName.EVALUATION;
	}

}
