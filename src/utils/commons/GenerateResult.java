package utils.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import utils.datastructure.Nonconformance;

/**
 * Class used to creates the file with the distinct nonconformances detected by the tool.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class GenerateResult {
	
	/**
	 * Method used to create an element to put into the XML file.
	 * @param doc = the Document where the element will be put.
	 * @param n = the nonconformance to extract info.
	 * @return - an element to put into the XML file.
	 */
	private static Element createsElement(Document doc, Nonconformance n){
		Element nonconformance = doc.createElement("Nonconformance");
		nonconformance.setAttribute("package", n.getPackageName());
		nonconformance.setAttribute("class", n.getClassName());
		nonconformance.setAttribute("method", n.getMethodName());
		nonconformance.setAttribute("type", n.getType().getName());
		nonconformance.setAttribute("likelyCause", n.getCause());
		nonconformance.setAttribute("stackTrace", getStackTraceString(n.getStackTraceOrder()));
		
		Element error = doc.createElement("Error");
		error.setAttribute("testName", n.getNumberedTest());
		error.setAttribute("testFile", n.getTestFile());
		error.setAttribute("message", n.getErrorMessage());
		
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
		Document doc = createXMLFile(Constants.RESULTS);
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
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
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
	
	/**
	 * Method that creates a XML file used to store the nonconformances
	 * detected.
	 * 
	 * @param path
	 *            - the name of the XML file to be produced.
	 * @return - the XML document.
	 */
	public static Document createXMLFile(String path) {
		File f = new File(path);
		DocumentBuilderFactory docFactory;
		DocumentBuilder docBuilder;
		Document doc = null;

		try {
			f.createNewFile();
			while (!f.canWrite()) {
				f.createNewFile();
			}
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();

			Element root = doc.createElement("NonconformancesSuite");
			doc.appendChild(root);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			StreamResult result = new StreamResult(f);
			transformer.transform(source, result);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

		return doc;
	}
}
