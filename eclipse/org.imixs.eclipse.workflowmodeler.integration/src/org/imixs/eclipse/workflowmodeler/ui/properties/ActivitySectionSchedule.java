package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.ActivityEntity;
import org.imixs.eclipse.workflowmodeler.model.Configuration;

/**
 * Activity Property Tab for configuration the ownership to an Workitem
 * 
 * @author Ralph Soika
 */
public class ActivitySectionSchedule extends AbstractWorkflowPropertySection {
	private Composite compositeFieldComboGrid; 
	private Vector vectorTimeCompareFields;
	
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

		Composite client1 = createSection(
				container,
				"Schedule Activity",
				"A schedule Activity could be processed by a scheduled Plugin or Task",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);

		Vector v = new Vector();
		v.add("disabled|0");
		v.add("enabled|1");
		Composite comu = this.createOptionInput(client1, null, null, v,
				2, "keyScheduledActivity", false, null, null);

	//	Text text = createTextInput(client1, "Selection:", null,
	//			null, "txtScheduledView", comu);

		Text text=this.createTextareaInput(client1, "Selection:",null, "txtScheduledView",comu,80);
		
		
		
		
		/** Time GRID **/		
		Composite compositeTimeGrid = this.createGridComposite(client1, "Time", null, 2,
				text);

		Composite com1 = getWidgetFactory().createComposite(compositeTimeGrid,
				SWT.NONE);
		com1.setLayout(new FormLayout());
		Composite com2= getWidgetFactory().createComposite(
				compositeTimeGrid, SWT.NONE);
		com2.setLayout(new FormLayout());
		
		
		
		/** Time Input **/	
		int iLabelWidth=this.getLabelWith();
		this.setLabelWith(0);
		text=createTextInput(com1, null, null,null,"numActivityDelay",null);
		
		
		
		/** Radio Options **/
		v=new Vector();
		v.add("min|1");
		v.add("hours|2");
		v.add("days|3");
		comu = this.createOptionInput(com2, null, null, v,
				3, "keyActivityDelayUnit", false, null, null);

		
		
		
		
		this.setLabelWith(iLabelWidth);

		/** After **/
		v=new Vector();
		v.add("creation | 3");
		v.add("last process |1");
		v.add("last modification|2");
		v.add("managed| 4");
		
		comu = this.createOptionInput(client1,  "After:", null,v,
				4, "keyScheduledBaseObject", false, null, compositeTimeGrid);

		addOptionSelectionListener("keyScheduledBaseObject",new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateIDLayout();
			}		
		});
		
		/* Field Combo */
		compositeFieldComboGrid = this.createGridComposite(client1, null, null, 1,
				comu);
		compositeFieldComboGrid.setLayout(new FormLayout());
		
		v=new Vector();
		//iLabelWidth=this.getLabelWith();
		//this.setLabelWith(0);
		CCombo cc=this.createComboInput(compositeFieldComboGrid, "Attribute:",null,v,"keyTimeCompareField",null,null);
		
		this.setLabelWith(iLabelWidth);
	}

	/** 
	 * Sets default values
	 * updates visuals
	 */
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		
		// update the compositeIDInput visibility
		if (getModelObject()!=null) {
			
			
			Configuration confiProfil=((ActivityEntity)this.getModelObject()).getProcessTree().getWorkflowModel().getEnvironment(WorkflowmodelerPlugin.ENVIRONMENT_PROFILE);
			if (confiProfil!=null) {
				Vector vOptions;
				Object o=confiProfil.getPropertyValue("txtTimeFieldMapping");
				if (o instanceof String) {
					vOptions=new Vector();
					vOptions.add(o);
				} else
					vOptions=(Vector)o;
				if (vOptions!=null) {
					// so jetzt buttons bauen!
					if (!vOptions.equals(vectorTimeCompareFields)) {
						vectorTimeCompareFields=vOptions;
						updateComboOptions(vOptions,"keyTimeCompareField");
					
					}
				}
			}
			
			
			
			
			
			
			
			
			updateIDLayout();
			
			
			
			
		}
	}
	
	
	/**
	 * update the compositeIDInput visibility
	 *
	 */
	private void updateIDLayout() {
		if (this.getModelObject()!=null) {
			Object oFollowUp= getModelObject().getPropertyValue("keyScheduledBaseObject");
			if (oFollowUp!=null) {
				if ("4".equals(oFollowUp.toString())) 
					compositeFieldComboGrid.setVisible(true);
				else
					compositeFieldComboGrid.setVisible(false);
				compositeFieldComboGrid.getParent().layout(true);
			}
		}
	}
		
}
