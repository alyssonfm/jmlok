package detect;

/**
 * Event class used to define events thrown by Detect.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class DetectEvent extends java.util.EventObject {

	/**
	 * Event for Detect monitoring stages.
	 */
	private static final long serialVersionUID = 1L;

	public DetectEvent(Detect source) {
		super(source);
	}

}
