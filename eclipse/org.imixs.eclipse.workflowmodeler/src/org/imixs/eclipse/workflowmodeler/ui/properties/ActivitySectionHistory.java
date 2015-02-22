package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Activity Property Tab for configuration the ownership to an Workitem
 * 
 * @author Ralph Soika
 */
public class ActivitySectionHistory extends AbstractWorkflowPropertySection {

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		setLabelWith(120);
		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

	
		Composite client1 = createSection(
				container,
				"Workflow History",
				"Defines a new entry in the workitem process history. " +
				"Optional Date- Timeformat adds a Timestamp at the beginning of the Entry. ",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);
		
		Vector v=new Vector();
		v.add("Full|0");
		v.add("Long|1");
		v.add("Medium|2");
		v.add("Short|3");
		v.add("none|-1");
		
		
		FormData fd=new FormData();
		fd.width=100;
		fd.left = new FormAttachment(0, 120);
		//fd.right = new FormAttachment(100, 0);
		
		
		
		CCombo cc=this.createComboInput(client1, "Date Format:",null,v,"keyLogDateFormat",fd,null);
		
		
		
		FormData fd2=new FormData();
		fd2.width=100;
		fd2.left = new FormAttachment(cc, 0, SWT.LEFT);
		//fd2.right = new FormAttachment(cc, 220, SWT.RIGHT);
		fd2.top=new FormAttachment(cc,0,SWT.BOTTOM);
		cc=this.createComboInput(client1, "Time Format:",null,v,"keyLogTimeFormat",fd2,null);
		
		Text text=this.createTextareaInput(client1, "Entry:",null, "rtfResultLog",cc,100);
		
		
	}

}
