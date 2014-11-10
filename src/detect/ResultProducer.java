
package detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import utils.Constants;
import utils.FileUtil;
import categorize.CategoryName;
import categorize.Nonconformance;

/**
 * Class used to creates the file with the distinct nonconformances detected by the tool.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class ResultProducer {

	/**
	 * A simple counter of nonconformances, used to GUI purposes (to show the number of NCs to the user of JMLOK).
	 */
	private int ncCount;
	
	/**
	 * The constructor of this class, creates a new instance of Result Producer class, and initializes the nonconformances counter.
	 */
	public ResultProducer() {
		ncCount = 0;
	}
	
	/**
	 * Method that returns the number of nonconformances detected in the current SUT.
	 * @return - the number of nonconformances detected.
	 */
	public int getNCTotal(){
		return this.ncCount;
	}

	/**
	 * Method used to list the distinct nonconformances that were detected by the JMLOK tool.
	 * @param compiler = the integer that indicates the JML compiler used.
	 * @return - the of list the distinct nonconformances that were detected by the JMLOK tool.
	 */
	public Set<TestError> listErrors(int compiler){
		File results = new File(Constants.TEST_RESULTS);
		Set<TestError> result;
		if(compiler == Constants.JMLC_COMPILER){
			result = getErrorsFromXML(results);
		} else {
			result = getErrorsFromFile(results);
		}
		this.ncCount = result.size();
		return result;
	}
	
	/**
	 * Method used to get the nonconformances from the result file of Randoop, when the jmlc is used as compiler.
	 * @param file = the path to result file of Randoop.
	 * @return - the list of nonconformances present in the test result file.
	 */
	private Set<TestError> getErrorsFromXML(File file) {
		Set<TestError> result = new HashSet<TestError>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		DocumentBuilder docBuilder;

		try {
			docBuilder = dbf.newDocumentBuilder();
			Document xml = docBuilder.parse(file);

			NodeList list = xml.getDocumentElement().getElementsByTagName(
					"testcase");

			for (int i = 0; i < list.getLength(); i++) {
				Element testcase = (Element) list.item(i);
				if (testcase.hasChildNodes()) {
					NodeList subNodes = testcase.getChildNodes();
					for (int j = 0; j < subNodes.getLength(); j++) {
						if (subNodes.item(j) instanceof Element) {
							Element problem = (Element) subNodes.item(j);
							if (problem.getTagName().equals("error")) {
								String name = testcase.getAttribute("name");
								String testFile = testcase.getAttribute("classname")+".java";
								String errorType = problem.getAttribute("type");
								String message = problem.getAttribute("message");
								String detailedErrorMessage = problem.getFirstChild().toString();
								TestError te = new TestError(name, testFile, message, errorType, detailedErrorMessage);
								if(te.isNonconformance()){
									result.add(te);
								}
							}
						}
					}
				}

			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Method used to get the nonconformances from the result file of Randoop, when the OpenJml is used as compiler.
	 * @param file = the path to result file of Randoop.
	 * @return - the list of nonconformances present in the test result file.
	 */
	private Set<TestError> getErrorsFromFile(File file) {
		Set<TestError> result = new HashSet<TestError>();
		try {
			FileReader f = new FileReader(file);
			BufferedReader in = new BufferedReader(f);
			String line = "";
			while ((line=in.readLine()) != null) {
				StringBuilder text = new StringBuilder();
				if (line.contains("JML ")) {
					text.append(in.readLine());
					if(!line.contains(CategoryName.PRECONDITION)){
						in.readLine();
						text.append(in.readLine());
						text.append(in.readLine());
						text.append("\n");
					}
					TestError te = new TestError(text.toString(), line, "");
					if(te.isNonconformance()){
						result.add(te);
					}
				}
			}
			in.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Method used to create an element to put into the XML file.
	 * @param doc = the Document where the element will be put.
	 * @param n = the nonconformance to extract info.
	 * @return - an element to put into the XML file.
	 */
	private static Element createsElement(Document doc, Nonconformance n){
		Element nonconformance = doc.createElement("Nonconformance");
		nonconformance.setAttribute("class", n.getClassName());
		nonconformance.setAttribute("method", n.getMethodName());
		nonconformance.setAttribute("type", n.getType());
		nonconformance.setAttribute("likelyCause", n.getCause());
		nonconformance.setAttribute("stackTrace", getStackTraceString(n.getStackTraceOrder()));
		
		Element error = doc.createElement("Error");
		error.setAttribute("testName", n.getTest());
		error.setAttribute("testFile", n.getTestFile());
		error.setAttribute("message", n.getMessage());
		
		nonconformance.appendChild(error);
		return nonconformance;
	}
	
	
	/**
	 * Get list containing names for class, in a calling order of the Exception launch.
	 * @param list The list containing names for class, in a calling order of the Exception launch.
	 * @return the text informing a resume from stack trace list showed by Java Exception.
	 */
	private static String getStackTraceString(List<String> list) {
		String toShow = "";
		toShow += "Error appeared in " + list.get(0) + "\n";
		for (int i = 1; i < list.size(); i++) {
			toShow += "----> at " + list.get(i) + "\n";
		}
		return toShow;
	}
	
	/**
	 * Method that generates the file containing the nonconformances that were detected.
	 * @param nonconformances = the set of nonconformances to go extracting info. 
	 * @return - the list of nonconformances detected by the JMLOK tool.
	 */
	public static Set<Nonconformance> generateResult(Set<Nonconformance> nonconformances){
		Document doc = FileUtil.createXMLFile(Constants.RESULTS);
		Element raiz = doc.getDocumentElement();
		for (Nonconformance nc : nonconformances) {
			Element e = createsElement(doc, nc);
			raiz.appendChild(e);
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result;
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			result = new StreamResult(new FileOutputStream(Constants.RESULTS));
			transformer = transFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		} catch (TransformerException e1) {
			e1.printStackTrace();
		}
		return nonconformances;
	}
}
