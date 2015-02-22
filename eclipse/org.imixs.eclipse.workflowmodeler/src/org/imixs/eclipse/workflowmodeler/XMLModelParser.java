package org.imixs.eclipse.workflowmodeler;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import org.eclipse.core.resources.IFile;
import org.imixs.eclipse.workflowmodeler.model.*;
import org.imixs.workflow.ItemCollection;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

/**
 * This Class parses an XML File to create an IX WorkflowModel Object or save an
 * IX WorkflowModel into an File. The class uses the Interface
 * org.eclipse.core.resources.IFile
 * 
 * This Class is deprecated. The only usage is from the ExportModel23Format
 * Wizard to export into the old format
 * 
 * 
 * @see org.eclipse.core.resources.IFile
 * @author Ralph Soika
 * 
 */
@Deprecated
public class XMLModelParser {

	/**
	 * This Method parses an XML File and creates a new WorkflowModel Instnace
	 * 
	 * @param fileInput
	 * @return
	 */
	public static WorkflowModel parseModel(IFile fileInput) {
		// System.out.println("[XMLModelParser] parseModel....");
		WorkflowModel workflowModel = new WorkflowModel(fileInput.getName());
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document doc = builder.parse(fileInput.getContents());
			// org.w3c.dom.Node rootNode = doc.getFirstChild();

			// Alle Enviroments auslesen
			NodeList nodes = doc.getElementsByTagName("environment");
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);

				Configuration confiuration = parseEnvironment(element);

				// ItemCollection itemc=confiuration.getItemCollection();

				workflowModel.addEnvironment(confiuration);
			}

			// Alle ProcessEntities auslesen
			nodes = doc.getElementsByTagName("processgroup");
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				parseProcessGroup(element, workflowModel);

				// workflowModel.addProcessTree(processTree);
			}

		} catch (Exception e) {
			// parse error - return model as it is.
			// e.printStackTrace();
		}

		workflowModel.clearDirtyFlag();
		return workflowModel;
	}

	/**
	 * Diese Methode parsed einen Prozessbaum Dabei werden zun�chst nur die
	 * ProcessEnties gelesen und erzeugt und in einem zweiten Durchgang die
	 * dazugeh�rigen ActivityEntities. Dies ist notwnedig damit die Connections
	 * auch zur nachfolgenden ProcessEntities erzeugt werden k�nnen
	 * 
	 * @param nodeProcessTree
	 */
	private static ProcessTree parseProcessGroup(Element processTreeElement,
			WorkflowModel aworkflowModel) {
		NodeList itemcollectionList;
		Element elementItemCollection;
		ItemCollection itemCollection;
		String sName, sProcessID, sActivityID, sNextID;

		String sProcessGroupName = processTreeElement.getAttribute("name");
		ProcessTree processTree = new ProcessTree(sProcessGroupName);
		aworkflowModel.addProcessTree(processTree);
		try {
			/**
			 * zuerst Alle ProcessEntities auslesen - erst danach k�nnen die
			 * Activities kontsruiert werden da sonst noch nicht alle
			 * "Anschlu�stellen" bekannt sind!
			 */
			NodeList nodes = processTreeElement
					.getElementsByTagName("processentity");
			for (int i = 0; i < nodes.getLength(); i++) {
				Element elementProcessEntity = (Element) nodes.item(i);

				// ItemCollection f�r ProcessEntity parsen
				itemcollectionList = elementProcessEntity
						.getElementsByTagName("itemcollection");
				elementItemCollection = (Element) itemcollectionList.item(0);
				itemCollection = parseItemCollection(elementItemCollection);

				// ID und Name parsen
				sName = elementProcessEntity.getAttribute("name");
				sProcessID = elementProcessEntity.getAttribute("id");
				// System.out.println("[XMLModelParser] ProcessEntity: ID=" +
				// sProcessID + " name=" + sName);
				itemCollection.replaceItemValue("txtName", sName);
				itemCollection.replaceItemValue("numProcessID", new Integer(
						sProcessID));
				processTree.createProcessEntity(itemCollection);
			}

			/*******************************************************************
			 * Jetzt nochmal durchgehen und die zugeh�rigen Activityies anlegen
			 * da jetzt alle Verbindungen hergestellt werden k�nnen m�ssen!
			 */
			nodes = processTreeElement.getElementsByTagName("processentity");
			for (int i = 0; i < nodes.getLength(); i++) {
				Element elementProcessEntity = (Element) nodes.item(i);
				sProcessID = elementProcessEntity.getAttribute("id");
				ProcessEntity processEntity = processTree
						.getProcessEntityModelObject(new Integer(sProcessID)
								.intValue());

				/** * Activities parsen ** */
				NodeList activityNodeList = elementProcessEntity
						.getElementsByTagName("activityentity");
				for (int j = 0; j < activityNodeList.getLength(); j++) {
					Element elementActivityEntity = (Element) activityNodeList
							.item(j);
					// ItemCollection f�r ProcessEntity parsen
					itemcollectionList = elementActivityEntity
							.getElementsByTagName("itemcollection");
					elementItemCollection = (Element) itemcollectionList
							.item(0);
					itemCollection = parseItemCollection(elementItemCollection);

					// ID, NextID und Name parsen
					sName = elementActivityEntity.getAttribute("name");
					sActivityID = elementActivityEntity.getAttribute("id");
					sNextID = elementActivityEntity.getAttribute("nextid");
					// System.out.println("[XMLModelParser] ActivityEntity: ID="
					// + sProcessID + "." + sActivityID + " -> " + sNextID + "
					// name=" + sName);
					itemCollection.replaceItemValue("txtName", sName);
					itemCollection.replaceItemValue("numProcessID",
							new Integer(sProcessID));
					itemCollection.replaceItemValue("numActivityID",
							new Integer(sActivityID));
					itemCollection.replaceItemValue("numNextProcessID",
							new Integer(sNextID));

					processEntity.createActivityEntity(itemCollection);
				}
			}
		} catch (Exception enf) {
		}
		return processTree;
	}

	/**
	 * Diese Methode parsed ein Environment Tag
	 * 
	 * @param environmentElement
	 */
	private static Configuration parseEnvironment(Element environmentElement) {
		NodeList itemcollectionList;
		Element elementItemCollection;
		ItemCollection itemCollection;
		String sName = environmentElement.getAttribute("name");
		try {
			// ItemCollection f�r ProcessEntity parsen
			itemcollectionList = environmentElement
					.getElementsByTagName("itemcollection");
			elementItemCollection = (Element) itemcollectionList.item(0);
			itemCollection = parseItemCollection(elementItemCollection);

			Configuration configuration = new Configuration(sName,
					itemCollection);
			return configuration;
		} catch (Exception enf) {
		}

		return null;
	}

	/**
	 * Diese Methode parst einen ItemColleciton Tag und �bertr�gt die Inhalte in
	 * eine ItemCollection
	 * 
	 * @param nodeSection
	 * @return
	 */
	private static ItemCollection parseItemCollection(Element nodeItemcollection) {
		ItemCollection itemCollection = new ItemCollection();
		String sFieldType, sFieldName, sFieldValue;
		// Hashtable hashFields = new Hashtable();
		try {

			// Items Tags parsen.....
			NodeList items = nodeItemcollection.getElementsByTagName("item");
			for (int j = 0; j < items.getLength(); j++) {
				// System.out.println("parse ein Item..");
				Element item = (Element) items.item(j);

				sFieldName = item.getAttribute("name");
				sFieldType = item.getAttribute("type");
				// System.out.println(" Name=" + sFieldName);
				// System.out.println(" Type=" + sFieldType);
				Vector vectorValue = new Vector();

				// check if multi value
				NodeList valueList = item.getElementsByTagName("value");
				if (valueList != null && valueList.getLength() > 0) {
					for (int i = 0; i < valueList.getLength(); i++) {
						// changed for JDK 1.4
						// Node textnode=textlist.item(i);
						Element textnode = (Element) valueList.item(i);
						if ("number".equalsIgnoreCase(sFieldType)) {
							// changed for JDK 1.4
							// vectorValue.add(new
							// Double(textnode.getTextContent()));
							sFieldValue = getCharacterDataFromElement(textnode);

							// change : 24.09.2006
							// test if sFieldValue should become an Integer or
							// Double Object
							try {
								// double or integer?
								if (sFieldValue.indexOf(".") > -1)
									vectorValue.add(new Double(sFieldValue));
								else
									vectorValue.add(new Integer(sFieldValue));
							} catch (NumberFormatException e) {
								// convertion not possible
								vectorValue.add(new Integer(0));
							}
							// OLD CODE: did not checks integer or double type :
							// vectorValue.add(new Double(sFieldValue));
						} else {
							// changed for JDK 1.4
							// vectorValue.add(textnode.getTextContent());
							sFieldValue = getCharacterDataFromElement(textnode);
							vectorValue.add(sFieldValue.trim());
						}
					}
				}

				itemCollection.replaceItemValue(sFieldName, vectorValue);
			}
		} catch (Exception e) {
			System.out
					.println("[WorkflowmodelerPlugin] parseItemCollection error: "
							+ e.toString());
		}
		return itemCollection;
	}

	private static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child == null)
			return "";
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}

	/**
	 * This Method transforms a WorkflowModel Objekt into a OutputStream This
	 * Method can be used to save a model into a IFile
	 * 
	 * @see <code>org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin</code>
	 * @param workflowModel
	 * @return
	 */
	public static ByteArrayOutputStream transformModel(
			WorkflowModel workflowModel) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

			Document doc = domBuilder.newDocument();

			// Insert the root element node
			Element root = doc.createElement("model");
			doc.appendChild(root);

			// Insert a comment in front of the element node
			Comment comment = doc
					.createComment("Imixs IX XML Model  -  www.imixs.com");
			doc.insertBefore(comment, root);

			Element configurationElement = doc.createElement("configuration");
			root.appendChild(configurationElement);

			/** Plugins bauen * */
			/*
			 * Element pluginsElement = doc.createElement("plugins");
			 * configurationElement.appendChild(pluginsElement); Iterator iter =
			 * workflowModel.getPlugins().iterator(); while (iter.hasNext()) {
			 * Configuration configuration = (Configuration) iter.next();
			 * transformPlugin(doc, pluginsElement, configuration); }
			 */

			/** Environments bauen * */
			Element environmentsElement = doc.createElement("environments");
			configurationElement.appendChild(environmentsElement);
			Iterator iter = workflowModel.getEnvironments().iterator();
			while (iter.hasNext()) {
				Configuration configuration = (Configuration) iter.next();
				transformEnvironment(doc, environmentsElement, configuration);
			}

			/** Processgroups bauen * */
			iter = workflowModel.getProcessTrees().iterator();
			while (iter.hasNext()) {
				ProcessTree processTree = (ProcessTree) iter.next();
				transformProcessTree(doc, root, processTree);
			}

			// Prepare the DOM document for writing
			Source source = new DOMSource(doc);

			// Write the DOM document to the file
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setAttribute("indent-number", new Integer(2));
			Transformer transformer = factory.newTransformer();

			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
			// "3");
			// Result result = new StreamResult(out);
			// transformer.transform(source, result);

			// (3)wrap the otuputstream with a writer (or bufferedwriter)
			transformer.transform(source, new StreamResult(
					new OutputStreamWriter(out, "utf-8")));

		} catch (Exception e) {

		}
		return out;

	}

	/**
	 * Diese Methode parsed einen Prozessbaum
	 * 
	 * @param nodeProcessTree
	 */
	private static void transformProcessTree(Document doc, Element rootelement,
			ProcessTree processTree) {
		Element processgroup = doc.createElement("processgroup");

		processgroup.setAttribute("name", processTree.getName());
		rootelement.appendChild(processgroup);
		// Prozesse anlegen
		Iterator iter = processTree.getProcessEntities().iterator();
		while (iter.hasNext()) {
			try {
				ProcessEntity processEntity = (ProcessEntity) iter.next();
				Element processelement = doc.createElement("processentity");
				processelement.setAttribute("name", processEntity
						.getItemCollection().getItemValueString("txtName"));
				processelement.setAttribute("id", ""
						+ processEntity.getItemCollection()
								.getItemValueInteger("numProcessID"));
				processgroup.appendChild(processelement);
				// ItemCollection bauen
				transformItemCollection(doc, processelement,
						processEntity.getItemCollection());

				// Activity bauen
				transformActivities(doc, processelement, processEntity);

			} catch (Exception e) {

			}

		}
	}

	/**
	 * Diese Methode parsed einen Prozessbaum
	 * 
	 * @param nodeProcessTree
	 */
	private static void transformActivities(Document doc, Element rootelement,
			ProcessEntity processEntity) {
		// Activities anlegen
		Iterator iter = processEntity.getActivityEntities().iterator();
		while (iter.hasNext()) {
			try {
				ActivityEntity activityEntity = (ActivityEntity) iter.next();
				Element activityelement = doc.createElement("activityentity");
				activityelement.setAttribute("name", activityEntity
						.getItemCollection().getItemValueString("txtName"));
				activityelement.setAttribute("id", ""
						+ activityEntity.getItemCollection()
								.getItemValueInteger("numActivityID"));
				activityelement.setAttribute("nextid", ""
						+ activityEntity.getItemCollection()
								.getItemValueInteger("numNextProcessID"));
				rootelement.appendChild(activityelement);
				// ItemCollection bauen
				transformItemCollection(doc, activityelement,
						activityEntity.getItemCollection());
			} catch (Exception e) {

			}
		}
	}

	/**
	 * Diese Methode erzeugt das DOM Model f�r eine Configuration
	 * 
	 * @param nodeProcessTree
	 */
	/*
	 * private static void transformPlugin(Document doc, Element rootelement,
	 * Configuration configuration) {
	 * 
	 * try { Element environmentelement = doc.createElement("plugin");
	 * environmentelement.setAttribute("name", configuration.getName());
	 * rootelement.appendChild(environmentelement); // ItemCollection bauen
	 * transformItemCollection(doc, environmentelement, configuration
	 * .getItemCollection()); } catch (Exception e) { } }
	 */

	/**
	 * Diese Methode erzeugt das DOM Model f�r eine Configuration
	 * 
	 * @param nodeProcessTree
	 */
	private static void transformEnvironment(Document doc, Element rootelement,
			Configuration configuration) {

		try {
			Element environmentelement = doc.createElement("environment");
			environmentelement.setAttribute("name", configuration.getName());
			rootelement.appendChild(environmentelement);
			// ItemCollection bauen
			transformItemCollection(doc, environmentelement,
					configuration.getItemCollection());
		} catch (Exception e) {

		}
	}

	/**
	 * Diese Methode parsed einen Prozessbaum
	 * 
	 * @param nodeProcessTree
	 */
	private static void transformItemCollection(Document doc,
			Element rootelement, ItemCollection itemCollection) {

		Element itemcollectionElement = doc.createElement("itemcollection");

		// Map map = itemCollection.getAllItems();
		Iterator it = itemCollection.getAllItems().entrySet().iterator();
		// int i = 0;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String sName = (String) entry.getKey();
			// ItemValue ermitteln
			Vector v = (Vector) entry.getValue();
			if (v.size() > 0) {
				Element itemElement = doc.createElement("item");
				itemElement.setAttribute("name", sName);

				// check type...
				Object o = v.elementAt(0);
				if (o instanceof Integer || o instanceof Double)
					itemElement.setAttribute("type", "number");

				Enumeration enumvalues = v.elements();
				while (enumvalues.hasMoreElements()) {
					String sValue = enumvalues.nextElement().toString();
					Element valueElement = doc.createElement("value");
					// changed for JDK 1.4
					// text.setTextContent(sValue);
					valueElement.appendChild((doc.createTextNode(sValue)));
					itemElement.appendChild(valueElement);
				}
				itemcollectionElement.appendChild(itemElement);
			}
		}
		rootelement.appendChild(itemcollectionElement);

	}

}
