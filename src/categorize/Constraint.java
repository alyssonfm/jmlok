package categorize;

/**
 * An category of nonconformance, the Constraint Error.
 * @author Alysson Milanez and Dennis Sousa.
 *
 */
public class Constraint implements Category{

	public String getType() {
		return CategoryName.CONSTRAINT;
	}

}
