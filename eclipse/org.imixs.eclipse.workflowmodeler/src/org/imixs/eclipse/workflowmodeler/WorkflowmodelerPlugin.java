package org.imixs.eclipse.workflowmodeler;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.imixs.eclipse.workflowmodeler.model.ActivityEntity;
import org.imixs.eclipse.workflowmodeler.model.Configuration;
import org.imixs.eclipse.workflowmodeler.model.ModelObject;
import org.imixs.eclipse.workflowmodeler.model.ProcessEntity;
import org.imixs.eclipse.workflowmodeler.model.ProcessTree;
import org.imixs.eclipse.workflowmodeler.model.WorkflowModel;
import org.imixs.eclipse.workflowmodeler.styles.IEditorStyle;
import org.imixs.eclipse.workflowmodeler.styles.popular.PopularStyle;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.WorkflowKernel;
import org.imixs.workflow.xml.EntityCollection;
import org.imixs.workflow.xml.XMLItemCollection;
import org.imixs.workflow.xml.XMLItemCollectionAdapter;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class
 * 
 * @Autor rsoika
 */
public class WorkflowmodelerPlugin extends AbstractUIPlugin {
	private transient PropertyChangeSupport propertyChangeSupport;
	// The shared instance.
	private static WorkflowmodelerPlugin plugin;
	// Resource bundle.
	private ResourceBundle resourceBundle;
	// WorkflowModel
	private WorkflowModel workflowModel = null;
	private Hashtable workflowModelList = new Hashtable();

	// copy of a model object for Copy&Paste Attributes
	private ItemCollection entityCopy = null;
	private ProcessTree processTreeCopy = null;

	// storage for removed entities
	private Vector deletionStore = new Vector();

	// storage for uniqueids
	private static Vector unqiueIds = new Vector();

	// private ConnectionRouter connectionRouter;
	private IEditorStyle editorStyle = null;

	public static String PLUGIN_ID = "org.imixs.eclipse.workflowmodeler";
	// Events
	public static String MODEL_CHANGED = "model_changed";
	public static String MODEL_CLOSED = "model_closed";
	public static String MODELOBJECT_REMOVED = "modelobject_removed";
	public static String ENVIRONMENT_PROFILE = "environment.profile";
	public static String ENVIRONMENT_LICENCE = "environment.licence";
	public static String ENVIRONMENT_SERVERTYP = "environment.servertyp";
	public static String ENVIRONMENT_DOMINO = "environment.domino";
	public static String ENVIRONMENT_EJB = "environment.ejb";
	public static String ENVIRONMENT_WEBSERVICE = "environment.webservice";

	/**
	 * Constructor of the WorkflowmodlerPlugiin initializes the ResourceBundle
	 * and registers the ServerConnectors defined by the ServerConnector
	 * Extension Points.
	 */
	public WorkflowmodelerPlugin() {
		super();

		plugin = this;
		try {
			resourceBundle = ResourceBundle
					.getBundle("org.imixs.eclipse.workflowmodeler.WorkflowmodelerPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}

	}

	protected void initializeDefaultPreferences(IPreferenceStore store) {
		// set default preference for editor style
		store.setDefault("editorstyle", "Popular");

		store.setDefault("servertype", "REST Service");

		/*
		 * store.setDefault("editorstyle", "Popular");
		 * store.setDefault("activitypropertytabs",
		 * "[Basic];[Access];[Mail];[History];[Result];[Other]");
		 */
	}

	/**
	 * Returns a WorkflowModel specified by its name
	 * 
	 * @param aName
	 * @return
	 */
	public WorkflowModel getWorkflowModel(String aName) {
		return (WorkflowModel) workflowModelList.get(aName);
	}

	/**
	 * This Method read a Model from an xml or .ixm file int a new WorkfowModel
	 * Object.
	 * 
	 * The Method exects a xml file containing a Imixs Workflow XML
	 * EntityCollection. The method uses the unmarshaller to read the file. If
	 * this fails then the method tries to load the content with the old
	 * deprectead method from the XMLModelParser.
	 * 
	 * @param fileInput
	 * @see org.imixs.eclipse.workflowmodeler.XMLModelParser
	 */
	public WorkflowModel loadWorkflowModel(IFile fileInput) {
		WorkflowModel workflowModelNew = null;

		unqiueIds = new Vector();

		// try to load the model with XML Unmarshaller

		ItemCollection itemCollection;
		// extract item collections from request stream.....

		try {
			workflowModelNew = parseModel(fileInput);

		} catch (Exception e) {
			System.out
					.println("[Warning] unable to read ixm file - try to load old file format version 2.3...");
			// we were unable to read the format. So lets try to read the old
			// ixm file format....
			workflowModelNew = XMLModelParser.parseModel(fileInput);
		}

		workflowModelList.put(workflowModelNew.getName(), workflowModelNew);
		// clear Dirty Flag
		workflowModelNew.clearDirtyFlag();

		return workflowModelNew;
	}

	/**
	 * The Method exects a xml file containing a Imixs Workflow XML
	 * EntityCollection. The method uses the unmarshaller to read the file. The
	 * method throws an exception if the file format is invalid.
	 * 
	 * Finally the method parses input and creates a new WorkflowModel Instance
	 * 
	 * The Method also verifies if all $UniqueIds are unique! - we have seen old
	 * corrupted model filed in the past
	 * 
	 * 
	 * @param fileInput
	 * @return
	 * @throws JAXBException
	 *             if input file format is not parseable
	 * @throws CoreException
	 *             if input file format is not parseable
	 */
	public static WorkflowModel parseModel(IFile fileInput)
			throws JAXBException, CoreException {
		EntityCollection modelCollection = null;
		Vector processTrees = new Vector();

		// create a new model instance
		WorkflowModel workflowModel = new WorkflowModel(fileInput.getName());

		// unmarshal the xml file
		JAXBContext context;

		context = JAXBContext.newInstance(EntityCollection.class);
		Unmarshaller u = context.createUnmarshaller();
		modelCollection = (EntityCollection) u.unmarshal(fileInput
				.getContents());

		XMLItemCollection[] xmlCol = modelCollection.getEntity();
		// phase 1: read all Environments
		for (XMLItemCollection aXMLItem : xmlCol) {
			ItemCollection itemcol = XMLItemCollectionAdapter
					.getItemCollection(aXMLItem);

			// envorionment entity found?
			if ("WorkflowEnvironmentEntity".equals(itemcol
					.getItemValueString("type"))) {
				// create Configuraton Object
				String sName = itemcol.getItemValueString("txtName");
				Configuration configuration = new Configuration(sName, itemcol);
				workflowModel.addEnvironment(configuration);
			}

		}

		// phase 2: read all ProcessEntities
		for (XMLItemCollection aXMLItem : xmlCol) {
			ItemCollection itemcol = XMLItemCollectionAdapter
					.getItemCollection(aXMLItem);

			// ProcessEntity found?
			if ("ProcessEntity".equals(itemcol.getItemValueString("type"))) {
				// identify processGroup
				String sGroup = itemcol.getItemValueString("txtWorkflowGroup");
				// test if tree alredy exists..
				ProcessTree processTree = workflowModel.getProcessTree(sGroup);
				if (processTree == null) {
					// create new empty tree
					processTree = new ProcessTree(sGroup);
					workflowModel.addProcessTree(processTree);
				}

				// verify unqiueid
				verifyUnqiueId(itemcol);

				// add entity into the tree
				processTree.createProcessEntity(itemcol);

			}

		}

		// phase 3: finanlly read all ActivityEntities and add them to the
		// proces Entities
		for (XMLItemCollection aXMLItem : xmlCol) {
			ItemCollection itemcol = XMLItemCollectionAdapter
					.getItemCollection(aXMLItem);

			// ProcessEntity found?
			if ("ActivityEntity".equals(itemcol.getItemValueString("type"))) {
				// identify processEntity
				int sProcessID = itemcol.getItemValueInteger("numProcessID");
				// search the processTree containing the coresponding
				// ProcessEntity (which was added in phase 2)

				Collection<ProcessTree> colTrees = workflowModel
						.getProcessTrees();
				for (ProcessTree aTree : colTrees) {
					ProcessEntity processEntity = aTree
							.getProcessEntityModelObject(sProcessID);
					if (processEntity != null) {

						/*
						 * in the past the property was named 'namreaders'. We
						 * need to migrate to the standard proerpty $ReadAccess.
						 * So we do a migration here...
						 */
						// test old field
						Vector vReader = itemcol.getItemValue("namreaders");
						if (vReader.size() > 0
								&& !"".equals(vReader.firstElement().toString())
								&& !itemcol.hasItem("$ReadAccess")) {
							try {
								itemcol.replaceItemValue("$ReadAccess", vReader);
							} catch (Exception e) {

								e.printStackTrace();
							}
						}

						// verify unqiueid
						verifyUnqiueId(itemcol);

						processEntity.createActivityEntity(itemcol);
						break;
					}
				}

			}

		}

		return workflowModel;
	}

	/**
	 * This Method save a Model Object into a IFile. The Method exports the
	 * model into a Imixs Workfow XML Entity Collection.
	 * 
	 * @param aModel
	 * @param file
	 * @param monitor
	 * 
	 * @see org.imixs.eclipse.workflowmodeler.XMLModelParser
	 * 
	 */
	public void saveWorkflowModel(WorkflowModel aModel, IFile file,
			IProgressMonitor monitor) {
		ProcessEntity processEntity;
		ActivityEntity activityEntity;
		ProcessTree processTree;
		String modelVersion = null;
		try {
			// count ProzessEntties

			int iProcesses = 0;
			Iterator iteratorcount = aModel.getProcessTrees().iterator();
			while (iteratorcount.hasNext()) {
				processTree = (ProcessTree) iteratorcount.next();
				iProcesses += processTree.getProcessEntities().size();
			}

			monitor.beginTask("save model", (iProcesses * 2) + 6);
			monitor.worked(1);

			// compute current version number...
			try {
				ItemCollection itemColProfile = aModel.getEnvironment(
						WorkflowmodelerPlugin.ENVIRONMENT_PROFILE)
						.getItemCollection();
				modelVersion = itemColProfile
						.getItemValueString("txtWorkflowModelVersion");
			} catch (Exception ee) {
				// create a default model version number
				modelVersion = "1.0.0";
			}

			monitor.worked(1);

			/*
			 * simply create a collecction containg all ProcessEntities,
			 * ActivityEntities and Environment Entities.
			 */
			Iterator iteratorProcessTrees = aModel.getProcessTrees().iterator();

			Vector vector = new Vector();
			while (iteratorProcessTrees.hasNext()) {
				// load ProcessTree
				processTree = (ProcessTree) iteratorProcessTrees.next();
				Iterator iteratorProcesses = processTree.getProcessEntities()
						.iterator();
				monitor.worked(1);

				while (iteratorProcesses.hasNext()) {
					processEntity = (ProcessEntity) iteratorProcesses.next();
					processEntity.getItemCollection().replaceItemValue(
							"$modelversion", modelVersion);
					processEntity.getItemCollection().replaceItemValue("type",
							"ProcessEntity");
					vector.add(processEntity.getItemCollection());

					// add all coresponding activities
					Iterator iterAcvities = processEntity.getActivityEntities()
							.iterator();
					while (iterAcvities.hasNext()) {
						activityEntity = (ActivityEntity) iterAcvities.next();
						activityEntity.getItemCollection().replaceItemValue(
								"$modelversion", modelVersion);
						activityEntity.getItemCollection().replaceItemValue(
								"type", "ActivityEntity");
						vector.add(activityEntity.getItemCollection());
						monitor.worked(1);
					}
				}
			}
			monitor.worked(1);
			// add all Environment entities...
			Iterator iteratorEnvironment = aModel.getEnvironments().iterator();

			while (iteratorEnvironment.hasNext()) {
				Configuration config = (Configuration) iteratorEnvironment
						.next();
				ItemCollection itemCol = config.getItemCollection();
				itemCol.replaceItemValue("txtName", config.getName());
				itemCol.replaceItemValue("$modelversion", modelVersion);
				itemCol.replaceItemValue("type", "WorkflowEnvironmentEntity");

				vector.add(itemCol);
				monitor.worked(1);
			}

			// now we have the full collection!
			monitor.worked(1);

			// build an entityCollection

			EntityCollection entCol = XMLItemCollectionAdapter
					.putCollection(vector);
			JAXBContext context = JAXBContext
					.newInstance(EntityCollection.class);
			Marshaller m = context.createMarshaller();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			m.marshal(entCol, out);

			try {
				file.setContents(new ByteArrayInputStream(out.toByteArray()),
						true, true, null);
				out.close();
				// clear Dirty Flag
				aModel.clearDirtyFlag();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			monitor.done();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// editorSaving = false;
		}
	}

	/**
	 * This Methode shwiches the model and informs registered
	 * PropertyChangeListeners for example a Editor switches the model on
	 * setFocus() to inform Listeners like TreeViews zu update there View.
	 * 
	 * @param model
	 */
	public void switchModel(WorkflowModel model) {
		if (model == workflowModel)
			return; // No Changes

		WorkflowModel workflowModelOld = workflowModel;
		firePropertyChange(MODEL_CHANGED, workflowModelOld, model);
	}

	/**
	 * This Method removes a WorkflowModel form the workflowModelList and
	 * informs registered PropertyChangeListeners. For example - this Methode is
	 * used by editors when closed to Informs Viewers like the WorkflowModelView
	 * component
	 * 
	 * @param model
	 */
	public void closeModel(WorkflowModel model) {
		workflowModelList.remove(model);
		firePropertyChange(MODEL_CLOSED, model, null);
	}

	/**
	 * Das Plugin selber ist in der Lage PropertyChanges zu senden Dadurch
	 * k�nnen sich z.B.: ContentProvider direkt am Plugin registrieren, um sich
	 * �ber eine �nderung des Models zu informieren. Dies tut z.B.: der
	 * WorkflwoModelContnetProvider f�r seinen TreeViewer das neue InputElement
	 * zu setzen
	 * 
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		getPropertyChangeSupport().firePropertyChange(propertyName, oldValue,
				newValue);
	}

	public Vector getDeletionStore() {
		return deletionStore;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// loadWorkflowModel();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		workflowModel = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static WorkflowmodelerPlugin getPlugin() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = WorkflowmodelerPlugin.getPlugin()
				.getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * returns an ImageDescriptor to the Image Ressource name
	 * 
	 * @param name
	 * @return
	 */
	public ImageDescriptor getIcon(String name) {
		String iconPath = "icons/";
		URL pluginUrl = getBundle().getEntry("/");
		try {
			return ImageDescriptor.createFromURL(new URL(pluginUrl, iconPath
					+ name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	/*
	 * public void log(String message, Exception ex) { getLog().log(new
	 * Status(IStatus.ERROR, "net.sf.tritos.eclipsespezial", 1, message, ex)); }
	 */

	private PropertyChangeSupport getPropertyChangeSupport() {
		if (propertyChangeSupport == null) {
			propertyChangeSupport = new PropertyChangeSupport(this);
		}
		return propertyChangeSupport;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}

	/**
	 * Definies the current Connection Router to be used by the
	 * <code>AssociationEditPart</code>
	 * 
	 * @param aRouter
	 */
	public void setEditorStyle(IEditorStyle aStyle) {
		editorStyle = aStyle;
	}

	/**
	 * returns the current graphical editor style. If no Style is defined the
	 * PopularStyle defaults.
	 * 
	 * @return IEditorStyle
	 */
	public IEditorStyle getStyle() {
		if (editorStyle == null) {
			editorStyle = new PopularStyle();
		}
		return editorStyle;
	}

	/**
	 * this method copies the attributes of a ModelObject. The method is used to
	 * support copy & paste actions on the EditParts in the IXGraphcialEditor.
	 * The method expects the ModelObject to be copied. This class is stored by
	 * the member variable entityCopy.
	 * 
	 * The method verifies if a property of the modelObject can be copied. e.g.
	 * properties like txtName or numProcessID can not be copied.
	 * 
	 * @see method pasteModelObjectAttributes
	 * @param aEntity
	 */
	public void copyModelObjectAttributes(ModelObject aEntity) {

		// COpy all attributes into the entityCopy
		ItemCollection itemColCurrent = aEntity.getItemCollection();

		entityCopy = new ItemCollection();
		Iterator iter = itemColCurrent.getAllItems().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) iter.next();

			String sFieldName = mapEntry.getKey().toString();
			// copy attribute?
			if (isValidFieldForCopyAndPaste(sFieldName))
				entityCopy.getAllItems().put(sFieldName, mapEntry.getValue());
		}
	}

	/**
	 * This method copyies a full ProcessTree into a internal member variable.
	 * The method pasteProcessTree can be used to paste the entities included in
	 * a tree into another tree.
	 * 
	 * @param aEntity
	 */
	public void copyProcessTree(ProcessTree aEntity) {
		processTreeCopy = aEntity;
	}

	/**
	 * This method pastes the entities of processTreeCopy into a processTree.
	 * 
	 * @param aEntity
	 */
	public void pasteProcessTree(ProcessTree aProcessTree) {

		HashMap processIDCache = new HashMap(); // storage for changed
												// ProcessIDs after paste action
		Vector activityCache = new Vector(); // storage for pasted Activities

		if (processTreeCopy != null) {

			/*
			 * iterate over all ProcessEntities....
			 */
			List listProcesses = processTreeCopy.getProcessEntities();
			Iterator iterProcesses = listProcesses.iterator();
			while (iterProcesses.hasNext()) {
				ProcessEntity aProcessEnity = (ProcessEntity) iterProcesses
						.next();
				try {
					// System.out.println("copy ProcessEntity...");
					// create a new ItemCollection and copy attriubtes of
					// current ProcessEntity..
					ItemCollection itemColProcess = new ItemCollection(
							aProcessEnity.getItemCollection().getAllItems());
					// Test if id is available in current model
					int iPid = itemColProcess
							.getItemValueInteger("numProcessID");
					if (!aProcessTree.getWorkflowModel().isFeeProcessID(iPid)) {
						// System.out.println("ID " + iPid + " is not free !");
						// replace NumProcessID now with valid id
						itemColProcess.replaceItemValue("numProcessID",
								new Integer(aProcessTree.getWorkflowModel()
										.getFeeProcessID()));

						int iNewPid = itemColProcess
								.getItemValueInteger("numProcessID");

						// generate new uniqueid
						itemColProcess.replaceItemValue("$UniqueID",
								WorkflowKernel.generateUniqueID());

						// System.out.println("new ID " + iNewPid + "");
						// put id into cache...
						processIDCache.put(new Integer(iPid), new Integer(
								iNewPid));

					}
					// finally create new processEntity
					ProcessEntity newProcessEntiy = aProcessTree
							.createProcessEntity(itemColProcess);

					/*
					 * Now copy all ActivityEntities
					 */
					List listActivities = aProcessEnity.getActivityEntities();
					Iterator iterActivities = listActivities.iterator();
					while (iterActivities.hasNext()) {
						ActivityEntity aActivityEnity = (ActivityEntity) iterActivities
								.next();

						// create a new ItemCollection and copy attriubtes of
						// current ActivityEntity..
						ItemCollection itemColActivity = new ItemCollection(
								aActivityEnity.getItemCollection()
										.getAllItems());

						// System.out.println("copy activity: "
						// +itemColActivity.getItemValueString("txtName") );

						// replace numProcessID with id of current created new
						// ProcessEntity
						int iProcessID = newProcessEntiy.getItemCollection()
								.getItemValueInteger("numProcessID");
						itemColActivity.replaceItemValue("numProcessID",
								new Integer(iProcessID));
						// generate new uniqueid
						itemColActivity.replaceItemValue("$UniqueID",
								WorkflowKernel.generateUniqueID());

						// final add new ActivityEntity to new ProcessEntity
						ActivityEntity newActivity = newProcessEntiy
								.createActivityEntity(itemColActivity);
						// put new Activity into cache...
						activityCache.add(newActivity);
					}

				} catch (Exception e) {

					e.printStackTrace();
				}

			}

			// now test all new created Activity Entity if there next ProcessID
			// need to be updated
			Iterator iterNewActivities = activityCache.iterator();
			while (iterNewActivities.hasNext()) {
				ActivityEntity newActivity = (ActivityEntity) iterNewActivities
						.next();
				// verify numNextProcessID
				Integer iNextID = new Integer(newActivity.getItemCollection()
						.getItemValueInteger("numNextProcessID"));
				Integer movedProcessID = (Integer) processIDCache.get(iNextID);
				if (movedProcessID != null) {
					// update NextProcessID now!
					newActivity.setPropertyValue("numNextProcessID",
							movedProcessID);
				}
			}
		}
	}

	/**
	 * this method paste the attributes selected before to a Model object
	 * 
	 * @param aEntity
	 */
	public void pasteModelObjectAttributes(ModelObject aEntity) {
		if (entityCopy != null) {
			// Copy all attributes from the entityCopy
			ItemCollection itemColCurrent = aEntity.getItemCollection();

			Iterator iter = entityCopy.getAllItems().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry mapEntry = (Map.Entry) iter.next();
				String sFieldName = mapEntry.getKey().toString();
				itemColCurrent.getAllItems().put(sFieldName,
						mapEntry.getValue());
			}

			// update unqiueid
			try {
				itemColCurrent.replaceItemValue("$UniqueID",
						WorkflowKernel.generateUniqueID());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// refresh Entity....
			aEntity.setItemCollection(itemColCurrent);
		}
	}

	public ItemCollection getEntityCopy() {
		return entityCopy;
	}

	public ProcessTree getProcessTreeCopy() {
		return processTreeCopy;
	}

	/**
	 * returns false if the field is a not copy able field!
	 * 
	 * @param aName
	 * @return
	 */
	private boolean isValidFieldForCopyAndPaste(String aName) {

		if ("txtname".equalsIgnoreCase(aName))
			return false;
		if ("numActivityID".equalsIgnoreCase(aName))
			return false;
		if ("keyFollowUp".equalsIgnoreCase(aName))
			return false;
		if ("numProcessID".equalsIgnoreCase(aName))
			return false;
		if ("numNextProcessID".equalsIgnoreCase(aName))
			return false;
		if ("numNextActivityID".equalsIgnoreCase(aName))
			return false;
		if ("numNextID".equalsIgnoreCase(aName))
			return false;

		if ("rtfdescription".equalsIgnoreCase(aName))
			return false;

		return true;
	}

	/**
	 * This method checkes if the unqiueid storage contains the same uqnieudid.
	 * In this case the entity will get a new uniqueid
	 */
	private static void verifyUnqiueId(ItemCollection aentity) {

		String id = aentity.getItemValueString("$UniqueID");
		if (id != null && !"".equals(id)) {

			// verify id
			if (unqiueIds.contains(id)) {
				try {
					id = WorkflowKernel.generateUniqueID();
					aentity.replaceItemValue("$UniqueID", id);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
			unqiueIds.add(id);

		}

	}
}
