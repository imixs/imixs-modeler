package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Activity Property Tab for configuration the ownership to an Workitem
 * 
 * @author Ralph Soika
 */
public class ActivitySectionBasic extends AbstractWorkflowPropertySection {
	private Vector vectorMailReceiverFields = null;
	Text textInputNextID;
	
	
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

		Composite client1 = createSection(
				container,
				"General Information",
				"This section describes general informations about the Activity.",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);

		/** Eingabe Felder */
		FormData data = new FormData();
		data.left = new FormAttachment(0, getLabelWith());
		//data.right = new FormAttachment(100, 0);
		data.width=90;
		data.top = new FormAttachment(0,
					ITabbedPropertyConstants.VSPACE);
		
		
		Text text=createTextInput(client1, "Activity ID:", null,data,"numActivityID",null);
		
		text=this.createTextInput(client1, "Name:",null,null, "txtname",text);

		
		
		Composite colFollowUp= createGridComposite(client1, "Followup:",
				null,2, 
				text);
		
		// kleines Composit for radiobutton bauen
		Composite com1= getWidgetFactory().createComposite(
				colFollowUp, SWT.NONE);
		
		com1.setLayout(new FormLayout());
		GridData gd = new GridData();
	
		
		Vector vFolloUp=new Vector();
		vFolloUp.add("none | 0");
		vFolloUp.add("Activity | 1");
		vFolloUp.add("Process | 2");
		this.createOptionInput(com1, null,null,vFolloUp,3,"keyFollowUp",false,null,null);
	
		
		// kleines Composit for id input bauen
		Composite com2= getWidgetFactory().createComposite(
				colFollowUp, SWT.NONE);
		com2.setLayout(new FormLayout());
		
		 gd = new GridData();
		gd.widthHint = 150;
		
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.verticalAlignment = GridData.BEGINNING;
		
		com2.setLayoutData(gd);
		
		data = new FormData();
		data.left = new FormAttachment(0, 30);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0,ITabbedPropertyConstants.VSPACE);
	
		textInputNextID=createTextInput(com2, "ID:", null,data,"numNextID",null);
		textInputNextID.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					updateFollowUpID();
				} catch (NumberFormatException enf) {
					// no op
				}
			}
		});
		
		
		
		//this.setLabelWith(ilW);
		this.createTextareaInput(client1, "Description:",null, "rtfdescription",colFollowUp,50);


	}

	/** 
	 * Sets default values
	 * and checks if processID has changed
	 */
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		
		// update ProcessID Label
		if (getModelObject()!=null) {
			// check default for keyPublicResult
			if (this.getModelObject().getPropertyValue("keyPublicResult")==null)
				this.getModelObject().setPropertyValue("keyPublicResult","1");
			
			// check default for keyFollowUp
			if (this.getModelObject().getPropertyValue("keyFollowUp")==null) {
				this.getModelObject().setPropertyValue("keyFollowUp","2");
				//super.selectionChanged(part,selection);
			}
			updateFollowUpControl();
			
			// update ProcessID
			//int iProcessID=this.getModelObject().getItemCollection().getItemValueInteger("numProcessID");
			//labelID.setText(iProcessID+".");
		}
	}
	
	
	
	
	/**
	 * this method updates the numNextProcessID or numNextActivityID Attribute
	 * depending on keyFollowUP 
	 */
	private void updateFollowUpID() {
		Integer nextID;
		String sID=textInputNextID.getText();
		try {
			nextID=new Integer(sID);
		} catch (Exception e) {
			return;
		}
		
		if (getModelObject()==null)
			return;
		Object oFollowUp= this.getModelObject().getPropertyValue("keyFollowUp");
		if (oFollowUp!=null) {
			if ("0".equals(oFollowUp.toString())) { // no follow up -> nextProcessID=ProcessID
				Object oID=this.getModelObject().getPropertyValue("numProcessID");
				this.getModelObject().setPropertyValue("numNextProcessID",oID);
				// set nextActivityID to 0
				this.getModelObject().setPropertyValue("numNextActivityID",new Integer(0));
			}
			if ("1".equals(oFollowUp.toString())) { // nextActivityID
				// set nextActivityID to nextID
				this.getModelObject().setPropertyValue("numNextActivityID",nextID);
				// set new NextProcessID to itself
				Object oID=this.getModelObject().getPropertyValue("numProcessID");
				this.getModelObject().setPropertyValue("numNextProcessID",oID);
			}
			
			if ("2".equals(oFollowUp.toString())) {
				// set nextActivityID to 0
				this.getModelObject().setPropertyValue("numNextActivityID",new Integer(0));
				// set nextProcessID to nextID
				this.getModelObject().setPropertyValue("numNextProcessID",nextID);
			}
			
		}
		
	}
	
	
	/**
	 * this method shows or hides the NextID Composite 
	 */
	private void updateFollowUpControl() {
		if (getModelObject()==null)
			return;
		Object oFollowUp= this.getModelObject().getPropertyValue("keyFollowUp");
		if (oFollowUp!=null) {
			if ("0".equals(oFollowUp.toString())) { // no follow up -> hide control
				textInputNextID.setVisible(false);
				// set nextProcessID to numProcessID
				if (!getModelObject().getPropertyValue("numProcessID").equals(this.getModelObject().getPropertyValue("numNextProcessID")))
					this.getModelObject().setPropertyValue("numNextProcessID",getModelObject().getPropertyValue("numProcessID"));
			}
			else {	
				String sNextID="";
				// now update the Text Field nextID
				if ("1".equals(oFollowUp.toString())) { // set numNextProcessID...
					Object oID=this.getModelObject().getPropertyValue("numNextActivityID");
					sNextID=""+oID;
					// set nextProcessID to numProcessID
					
					if (!getModelObject().getPropertyValue("numProcessID").equals(this.getModelObject().getPropertyValue("numNextProcessID")))
						this.getModelObject().setPropertyValue("numNextProcessID",getModelObject().getPropertyValue("numProcessID"));
				}
				if ("2".equals(oFollowUp.toString())) { // set numNextProcessID...
					sNextID=this.getModelObject().getPropertyValue("numNextProcessID").toString();
				}
				// no value yet?
				if (sNextID==null || "".equals(sNextID))
					sNextID=this.getModelObject().getPropertyValue("numProcessID").toString();
				
				textInputNextID.setVisible(true);
				textInputNextID.setText(sNextID);
			}
			textInputNextID.getParent().layout(true);
		}
		
	}
}
