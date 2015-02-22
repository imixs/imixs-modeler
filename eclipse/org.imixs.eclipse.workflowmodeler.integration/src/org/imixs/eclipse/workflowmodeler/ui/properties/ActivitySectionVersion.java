package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Activity Property Tab for configuration the ownership to an Workitem
 * 
 * @author Ralph Soika
 */
public class ActivitySectionVersion extends AbstractWorkflowPropertySection {
	private Composite compositeIDInput;
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

	
		Composite client1 = createSection(
				container,
				"Version",
				" - Creating of a new Version copies the current workitem and process the new Version by this activity\n"+
				" - Conversion to a Master Version converts the current document into a Master Version."+
				" A existing Master Version will be processed by this activity\n  and converted into a Version of the current Document.",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);
		
		Vector v=new Vector();
		v.add("no Version (default) |0");
		v.add("create a new Version|1");
		v.add("convert to Master Version |2");
		Composite comu=this.createOptionInput(client1, "Version:",null,v,1,"keyVersion",false,null,null);

		addOptionSelectionListener("keyVersion",new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateIDLayout();
			}		
		});
		
		 compositeIDInput= this.createGridComposite(client1, "",
				null, 1, 
				comu) ;
		compositeIDInput.setLayout(new FormLayout());
		
		FormData data = new FormData();
		data.left = new FormAttachment(0, getLabelWith());
		data.width=90;		
		data.top = new FormAttachment(0,
					ITabbedPropertyConstants.VSPACE);
		
		
		
		createTextInput(compositeIDInput, "Activity ID:", null,data,"numVersionActivityID",null);
		
		
		//getWidgetFactory().paintBordersFor(compositeIDInput);
		updateIDLayout();
	}

	
	/** 
	 * Sets default values
	 * and checks if processID has changed
	 */
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		
		// update the compositeIDInput visibility
		if (getModelObject()!=null) {
			updateIDLayout();
			
		}
	}
	
	
	/**
	 * update the compositeIDInput visibility
	 *
	 */
	private void updateIDLayout() {
		if (this.getModelObject()!=null) {
			Object oFollowUp= getModelObject().getPropertyValue("keyVersion");
			if (oFollowUp!=null) {
				if ("0".equals(oFollowUp.toString())) 
					compositeIDInput.setVisible(false);
				else
					compositeIDInput.setVisible(true);
				compositeIDInput.getParent().layout(true);
			}
		}
	}
		
}
