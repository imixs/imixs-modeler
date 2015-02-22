package org.imixs.eclipse.workflowmodeler.ui.editors;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.Configuration;
import org.imixs.eclipse.workflowmodeler.model.WorkflowModel;
import org.imixs.workflow.ItemCollection;


/**
 * This Page is the default FormePage of the IXXMLEditor. 
 * It will show the Serverconfiguration and the existing ProcessTrees
 * 
 * @author Ralph Soika
 */
public class WorkflowModelProfilePage extends IXEditorPage { 
	
	private Configuration configurationProfile=null;
	private FormToolkit toolkit ;
	private WorkflowModel workflowModel;
	
	
	ScrolledForm form;
	Composite compositeDebug ;
	/**
	 * The Method checks if a ServerTpy Environment exists and automatical creates one if not.
	 */
	public void initializeWorkflowModel(WorkflowModel amodel) {
		workflowModel=amodel;
		
		// check if ENVIRONMENT_PROFILE Environment exists
		configurationProfile=workflowModel.getEnvironment(WorkflowmodelerPlugin.ENVIRONMENT_PROFILE);
		if (configurationProfile==null) {
			// create Environment !
			ItemCollection itemCol=new ItemCollection();
			configurationProfile=new Configuration(WorkflowmodelerPlugin.ENVIRONMENT_PROFILE,itemCol);
			workflowModel.addEnvironment(configurationProfile);
			configurationProfile.setPropertyValue("keyDebugLevel","0");
			
			// Set Default version
			configurationProfile.setPropertyValue("txtWorkflowModelVersion","1.0.0");
			
			
			// Set default plugins
			Vector vPlugins=new Vector();
			vPlugins.add("org.imixs.workflow.plugins.AccessPlugin");
			vPlugins.add("org.imixs.workflow.plugins.HistoryPlugin");
			vPlugins.add("org.imixs.workflow.plugins.ResultPlugin");
			configurationProfile.setPropertyValue("txtPlugins",vPlugins);
			
			
		}		
	}
	
	
	public void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();	
		toolkit = managedForm.getToolkit();
		form.setText("Workflow Profile");
		
	
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight=0;
		gridLayout.numColumns=2;
		form.getBody().setLayout(gridLayout);
		
		createNameObjectsSection(managedForm);
		
		createPluginSection(managedForm) ;
			createDebugSection( managedForm);
		
		
		
		//compositeDebug.layout();
		form.reflow(true);
	}	
	
	
	/**
	 * create the ServerControl Section
	 * 
	 * @param mform
	 * @param title
	 * @param desc
	 */
	private void createDebugSection(IManagedForm mform) {
		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.widthHint = 300;
		
		 compositeDebug = createSection(mform, "Logging",
				"This section describes the logging settings for the workflow engine. ",				
				Section.TITLE_BAR | Section.DESCRIPTION
				| Section.TWISTIE, 2,gd);
		
		
		
		Label l1=toolkit.createLabel( compositeDebug, "Logging level:", SWT.LEFT );
		l1.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		
		
		 gd=new GridData();
		gd.verticalAlignment=GridData.CENTER;
		l1.setLayoutData(gd);
		
		
		
		
		
		/*** Debug Buttons ***/
		Composite compositeButtons=toolkit.createComposite(compositeDebug, SWT.NONE);
		GridLayout gl=new GridLayout();
		gl.numColumns=3;
		gl.marginWidth = 10;
		gl.marginHeight=0;
		compositeButtons.setLayout(gl);
		Button button=toolkit.createButton(compositeButtons, "Errors", SWT.RADIO);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				configurationProfile.setPropertyValue("keyDebugLevel","0");
			}		
		});		
		button.setSelection("0".equals(configurationProfile.getPropertyValue("keyDebugLevel")));
		
		button=toolkit.createButton(compositeButtons, "Warnings", SWT.RADIO);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				configurationProfile.setPropertyValue("keyDebugLevel","1");
			}		
		});	
		button.setSelection("1".equals(configurationProfile.getPropertyValue("keyDebugLevel")));

		
		button=toolkit.createButton(compositeButtons, "All",SWT.RADIO);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				configurationProfile.setPropertyValue("keyDebugLevel","2");
			}		
		});	
		button.setSelection("2".equals(configurationProfile.getPropertyValue("keyDebugLevel")));
		
	
		/*** Error Result ***/
		createFormInput(compositeDebug,toolkit,"Logger:","txtWorkflowLogger",configurationProfile);
		
		//compositeDebug.layout();
	}


	private void createPluginSection(IManagedForm mform) {
		
		Composite compositeObjectsNames= createSection(mform, "Plugins", 
				"List of plugins which should be processed through the workflow engine.",
				 Section.TITLE_BAR | Section.DESCRIPTION
				| Section.TWISTIE, 1,null);

		final Image imageName;
		imageName=WorkflowmodelerPlugin.getPlugin().getIcon("editor/plugin_obj.gif").createImage();

			
		this.createTableInput(compositeObjectsNames,toolkit, "txtPlugins", configurationProfile,imageName);
		
		
			
	}
	
	
	
	
	
	
	
	private void createNameObjectsSection(IManagedForm mform) {
		Composite compositeObjectsNames= createSection(mform, "Actors", 
				"List of workflow actors.\n" +
				"Separate the display name from the workitem property with a '|' char. e.g. 'Employee | namEmployee'",
				Section.EXPANDED | Section.TITLE_BAR | Section.DESCRIPTION
				| Section.TWISTIE, 1,null);

		final Image imageName;
		imageName=WorkflowmodelerPlugin.getPlugin().getIcon("editor/name_obj.gif").createImage();

		final Image imageTime;
		imageTime=WorkflowmodelerPlugin.getPlugin().getIcon("editor/time_obj.gif").createImage();
		
		this.createTableInput(compositeObjectsNames,toolkit, "txtFieldMapping", configurationProfile,imageName);
		
		// Time Objekcts
		Composite compositeObjectsTime= createSection(mform, "Timer", 
				"List of timer properties for scheduled tasks. " +
				"\n" +
				"Separate the display name from the workitem property with a '|' char. e.g. 'Escalation | datEscalationDate'",
				Section.EXPANDED | Section.TITLE_BAR | Section.DESCRIPTION
				| Section.TWISTIE, 1,null);
		
		this.createTableInput(compositeObjectsTime,toolkit, "txtTimeFieldMapping", configurationProfile,imageTime);
	}

}










