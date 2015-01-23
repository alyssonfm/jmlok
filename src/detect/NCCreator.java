
package detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import utils.commons.Constants;
import utils.datastructure.Nonconformance;

/**
 * Class used to creates the file with the distinct nonconformances detected by the tool.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class NCCreator {

	/**
	 * A simple counter of nonconformances, used to GUI purposes (to show the number of NCs to the user of JMLOK).
	 */
	private int ncCount;
	
	/**
	 * The constructor of this class, creates a new instance of Result Producer class, and initializes the nonconformances counter.
	 */
	public NCCreator() {
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
	public Set<Nonconformance> listNonconformances(int compiler){
		File results;
		if(compiler == Constants.CODECONTRACTS_COMPILER){
			results = new File(Constants.TEST_ERRORS);
		}else{			
			results = new File(Constants.TEST_RESULTS);
		}
		Set<Nonconformance> result;
		if(compiler == Constants.JMLC_COMPILER){
			result = getErrorsFromXML(results);
		} else if(compiler == Constants.CODECONTRACTS_COMPILER){
			result = getErrorsFromTextFile(results);
		} else {
			result = getErrorsFromFile(results);
		}
		this.ncCount = result.size();
		return result;
	}
	
	private Set<Nonconformance> getErrorsFromTextFile(File results) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Method used to get the nonconformances from the result file of Randoop, when the jmlc is used as compiler.
	 * @param file = the path to result file of Randoop.
	 * @return - the list of nonconformances present in the test result file.
	 */
	private Set<Nonconformance> getErrorsFromXML(File file) {
		Set<Nonconformance> result = new HashSet<Nonconformance>();
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
								Nonconformance te = new Nonconformance(name, testFile, message, errorType, detailedErrorMessage);
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
	private Set<Nonconformance> getErrorsFromFile(File file) {
		Set<Nonconformance> result = new HashSet<Nonconformance>();
		try {
			FileReader f = new FileReader(file);
			BufferedReader in = new BufferedReader(f);
			String line = "";
			while ((line=in.readLine()) != null) {
				StringBuilder text = new StringBuilder();
				if (line.contains("JML ")) {
					text.append(in.readLine());
					if(!line.contains(Nonconformance.CategoryType.PRECONDITION.getName())){
						in.readLine();
						text.append(in.readLine());
						text.append(in.readLine());
						text.append("\n");
					}
					Nonconformance te = new Nonconformance(text.toString(), line, "");
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
	
}
