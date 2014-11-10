package detect;

/**
 * Listener used to define events thrown by Detect.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public interface DetectListener extends java.util.EventListener{

	void detectCreatedDirectories(DetectEvent e);
	void detectCompiledProjectWithJava(DetectEvent e);
	void detectCompiledProjectWithJML(DetectEvent e);
	void detectGeneratedTestsWithRandoop(DetectEvent e);
	void detectExecutedTests(DetectEvent e);
	
	void detectErrorOnGeneratingTests(DetectEvent e);
	
}
