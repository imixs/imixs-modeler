package org.imixs.eclipse.workflowmodeler.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.events.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.*;
import org.imixs.workflow.*;
import org.imixs.eclipse.workflowmodeler.*;
import org.imixs.eclipse.workflowmodeler.model.*;

/**
 * This Page is the default Page of the IXXMLEditor. It will show the
 * Serverconfiguration and the existing ProcessTrees The SyncSection includes
 * widgets to start a syncronation process connecting to a webservice defined on
 * the WorkflowModelWebServicePage
 * 
 * @see org.imixs.eclipse.workflowmodeler.ui.editors.WorkflowModelWebServicePage
 * @author Ralph Soika
 * 
 */
public class WorkflowModelDefaultPage extends IXEditorPage implements
		PropertyChangeListener {
	private FormToolkit toolkit;


	private Configuration configurationProfile = null;

	private WorkflowModel workflowModel;

	ScrolledForm form;

	Composite clientProcessList = null, clientSync = null;

	ImageHyperlink hyperLinkSynchronize = null;

	Label labelID = null;

	Label labelVersion = null;

	/**
	 * The Method checks if a ServerTyp Environment exists and automatical
	 * creates one if not. The Method checks also if a ServerTyp Environment
	 * exists and automatical creates one if not.
	 */
	public void initializeWorkflowModel(WorkflowModel amodel) {
		workflowModel = amodel;
	

		// check if ENVIRONMENT_PROFILE Environment exists
		configurationProfile = workflowModel
				.getEnvironment(WorkflowmodelerPlugin.ENVIRONMENT_PROFILE);
		if (configurationProfile == null) {
			// create Environment !
			ItemCollection itemCol = new ItemCollection();
			configurationProfile = new Configuration(
					WorkflowmodelerPlugin.ENVIRONMENT_PROFILE, itemCol);
			workflowModel.addEnvironment(configurationProfile);
			configurationProfile.setPropertyValue("keyDebugLevel", "0");

			// Set default plugins
			Vector vPlugins = new Vector();
			vPlugins.add("org.imixs.workflow.plugins.AccessPlugin");
			vPlugins.add("org.imixs.workflow.plugins.HistoryPlugin");
			vPlugins.add("org.imixs.workflow.plugins.ResultPlugin");
			configurationProfile.setPropertyValue("txtPlugins", vPlugins);

		}

		/* Set default values */
		if (configurationProfile.getPropertyValue("txtWorkflowModelVersion") == null)
			configurationProfile.setPropertyValue("txtWorkflowModelVersion",
					"1.0.0");
		if (configurationProfile.getPropertyValue("txtWorkflowModelID") == null)
			configurationProfile.setPropertyValue("txtWorkflowModelID", "none");

		configurationProfile.addPropertyChangeListener(this);
	}

	/**
	 * create the ServerConnection Section The Method checks if a ServerTpy
	 * Environment exists and automatical creates one if not.
	 * 
	 * @param mform
	 */
	public void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();

		toolkit = managedForm.getToolkit();
		form.setText("Workflow Overview");

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;
		form.getBody().setLayout(gridLayout);

		form.setBackgroundImage(WorkflowmodelerPlugin.getPlugin().getIcon(
				"editor/form_banner.gif").createImage());

		createSyncSection(managedForm);
		createProcessTreeSection(managedForm);

		form.reflow(true);

	}

	/**
	 * This Section supports a HyperLink to upload the servermodel. The
	 * Hyperlink uses the corresponding ServerConnector to update the model
	 * 
	 * @param mform
	 * @param title
	 * @param desc
	 */
	private void createSyncSection(IManagedForm mform) {

		GridData gdSection = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gdSection.widthHint = 150;

		clientSync = createSection(
				mform,
				"General Information",
				"This section describes general informations about the Model",

				Section.TITLE_BAR | Section.DESCRIPTION, 1, gdSection);

		
		/*** Model Version ***/
		//createFormInput(clientSync,toolkit,"ID:","txtWorkflowModelID",configurationProfile);
		createFormInput(clientSync,toolkit,"Model Version:","txtWorkflowModelVersion",configurationProfile);
		
	
		// dummy
		labelID = toolkit.createLabel(clientSync, " ");
		
		// sync
		
		createFormInput(clientSync,toolkit,"Web Service Location:","txtWebServiceLocation",configurationProfile);
		
		
		
		
		
		hyperLinkSynchronize = this.createLink(clientSync, toolkit,
				WorkflowmodelerPlugin.getPlugin().getIcon("editor/upload.gif"),
				"upload model ");
		hyperLinkSynchronize.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent linkEvent) {
				// Modell upload
				// get selected ServerConnector
				ServerConnector connector = getServerConnector();
				if (connector != null) {
					try {
						connector.uploadModel(workflowModel);
					} catch (Exception ec) {
						// no op
					}
				}

			}
		});
		
		
		
		
		
		
	
	}

	private void createProcessTreeSection(IManagedForm mform) {

		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.widthHint = 300;
		// gd.horizontalSpan = 2;
		clientProcessList = createSection(mform, "Workflow Groups",
				"List of all workflow groups",

				Section.TITLE_BAR | Section.DESCRIPTION, 1, gd,
				WorkflowmodelerPlugin.getPlugin().getIcon("editor/refresh.gif"));

		ImageHyperlink updateLink = (ImageHyperlink) ((Section) clientProcessList
				.getParent()).getTextClient();
		updateLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent linkEvent) {
				// update section
				updateProcessTreeList();

			}
		});

		updateProcessTreeList();
	}

	/**
	 * This Method create or update the ProcessTree List inside the
	 * clientProcessList composite
	 * 
	 */
	private void updateProcessTreeList() {

		// first remove all clients
		Control[] oldControls = clientProcessList.getChildren();
		for (int i = 0; i < oldControls.length; i++) {
			Control old = oldControls[i];
			old.dispose();
			old = null;
		}

		// create new ProcessTree List...
		Iterator iter = workflowModel.getProcessTrees().iterator();

		// Sort Process Trees

		while (iter.hasNext()) {
			ProcessTree processTree = (ProcessTree) iter.next();

			// Sich selbst als Property Change Listener entfernen ums sich nach
			// dem update
			// wieder neu zu registrieren.
			// dadurch kann auf änderungen im Namen sofort reagiert werden
			processTree.removePropertyChangeListener(this);

			ImageHyperlink processTreeLink = this.createLink(clientProcessList,
					toolkit, WorkflowmodelerPlugin.getPlugin().getIcon(
							"editor/processtree.gif"), processTree.getName());

			processTreeLink.setData(processTree);
			processTreeLink.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent linkEvent) {
					// EditorInput anhand es ProcessTrees erzeugen und Editor
					// �ffnen
					Hyperlink link = (Hyperlink) linkEvent.getSource();
					IWorkbenchWindow workbenchWindow = getSite()
							.getWorkbenchWindow();
					IWorkbenchPage page = workbenchWindow.getActivePage();
					String editorId = "org.imixs.eclipse.workflowmodeler.IXGraphicalEditor";
					EditorInput input = new EditorInput((ProcessTree) link
							.getData());
					try {
						page.openEditor(input, editorId, true);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			});
		}

		// Sich selbst als Property Change Listener registrieren um auf
		// Änderungen des Namens sofort reagieren zu können
		iter = workflowModel.getProcessTrees().iterator();
		while (iter.hasNext()) {
			ProcessTree processTree = (ProcessTree) iter.next();
			processTree.addPropertyChangeListener(this);
		}

		toolkit.createLabel(clientProcessList,
				"Add a new workflow group  ");
		// Create new ProcessTree Link bauen
		ImageHyperlink createLink = this.createLink(clientProcessList, toolkit,
				WorkflowmodelerPlugin.getPlugin().getIcon(
						"editor/new_processgroup.gif"),
				"create new");
		createLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent linkEvent) {
				// create ProcessTree
				ProcessTree newProcessTree = new ProcessTree("[New Group]");
				workflowModel.addProcessTree(newProcessTree);
				updateProcessTreeList();
			}
		});

		clientProcessList.layout();

		form.reflow(true);
	}

	/**
	 * returns the first registered server connector
	 * 
	 * @param sType
	 * @return
	 */
	public ServerConnector getServerConnector() {
String sType=null;
		// get the servertype...
		sType=WorkflowmodelerPlugin.getPlugin().getPreferenceStore().getString("servertype");
		
		System.out.println("current servertype=" + sType);
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("org.imixs.eclipse.workflowmodeler.serverconnectors");
		IExtension[] extensions = extensionPoint.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] elements = extensions[i]
					.getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				final String sname = elements[j].getAttribute("name");
				// ist der name der richtige?
				if (sname.equals(sType)) {
					try {
						Object o = elements[j]
								.createExecutableExtension("class");
						if (o instanceof ServerConnector) {
							ServerConnector serverConnector = (ServerConnector) o;
							return serverConnector;
						}
					} catch (org.eclipse.core.runtime.CoreException ec) {
						System.out
								.println("[WorkflowModelDefaultPage] unable to create ServerConnector "
										+ sType);
						ec.printStackTrace();
					} catch (Exception e) {
						System.out
								.println("[WorkflowModelDefaultPage] unable to create ServerConnector "
										+ sType + " " + e.toString());
					}
					break;
				}
			}
		}
		return null;
	}

	/**
	 * Update ID and version number if changed
	 */
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getSource() instanceof org.imixs.eclipse.workflowmodeler.model.ProcessTree) {
			if (evt.getPropertyName().equalsIgnoreCase("name")) {
				// sobald sich ein name eines ProcessTree Objekts ändert wird
				// der baum neu gebaut
				updateProcessTreeList();
				return;
			}
		}

		if (evt.getPropertyName().equalsIgnoreCase("txtWorkflowModelID")
				&& labelID != null) {
			labelID.setText(evt.getNewValue().toString());
			labelID.getParent().layout();
		}

		if (evt.getPropertyName().equalsIgnoreCase("txtWorkflowModelVersion")
				&& labelVersion != null) {
			labelVersion.setText(evt.getNewValue().toString());
			labelVersion.getParent().layout();
		}

	}

}
