package co.edu.uniandes.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DMNParser {

	private File file;
	private Document document;
	private String propertiesText;
	private Attribute[] attributes;
	
	public DMNParser(String filePath){
		
		try {
			file = new File(filePath);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(file);
			propertiesText = "";
		} 
		
		catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void parseToProperties(){
		pardeHeader();
		parseAttributes();
		System.out.println(propertiesText);
	}
	
	private void pardeHeader(){
		NodeList inputs = document.getElementsByTagName("input");
		NodeList rules = document.getElementsByTagName("rule");
	
		addTextToProperties("N = " + inputs.getLength());
		addTextToProperties("M = " + rules.getLength());
	}
	
	private void parseAttributes(){
		NodeList inputs = document.getElementsByTagName("input");
		attributes = new Attribute[inputs.getLength()];
		
		for(int i = 0; i < inputs.getLength(); i++){
			Node current = inputs.item(i);
			NodeList children = current.getChildNodes();
			
			if(current.getNodeType() == Node.ELEMENT_NODE){
				Element currentE = (Element) current;
				Node child = children.item(0);
				boolean found = false;
				
				for(int j = 0; j < children.getLength() && !found; j++){
					child = children.item(j);
					if(child.getNodeType() == Node.ELEMENT_NODE){
						found = true;
					}
				}
				
				if(found) {
					Element childE = (Element) child;
					
					String name = currentE.getAttribute("label").replace(" ", "");
					String type = childE.getAttribute("typeRef").replace(" ", "");
					
					Attribute attribute = new Attribute(name, type);
					attributes[i] = attribute;
					System.out.println(name + " - " + type);
				}
				
			}
		}
	}
	
	private void addTextToProperties(String text){
		propertiesText+=text + '\n';
	}
}
