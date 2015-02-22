package org.imixs.eclipse.workflowmodeler.ui.properties;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Activity Property Tab for configuration the ownership to an Workitem
 * 
 * @author Ralph Soika
 */
public class ProcessSectionBasic extends AbstractWorkflowPropertySection {
	//private Vector vectorMailReceiverFields = null;

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

		Composite client1 = createSection(
				container,
				"General Information",
				"This section describes general informations about the Process",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);

		/** Eingabe Felder */
		FormData data = new FormData();
		data.left = new FormAttachment(0, getLabelWith());
		//data.right = new FormAttachment(100, 0);
		data.width=50;
		data.top = new FormAttachment(0,
					ITabbedPropertyConstants.VSPACE);
		
		
		Text text=createTextInput(client1, "Process ID:", null,data,"numProcessID",null);
		
		text=this.createTextInput(client1, "Name:",null,null, "txtname",text);

		this.createTextareaInput(client1, "Description:",null, "rtfdescription",text,90);


		
		
	
	}

}
