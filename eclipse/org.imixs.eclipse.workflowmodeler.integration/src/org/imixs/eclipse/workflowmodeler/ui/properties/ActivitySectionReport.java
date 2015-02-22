package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Vector;

import org.eclipse.swt.custom.CCombo;
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
public class ActivitySectionReport extends AbstractWorkflowPropertySection {

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

	
		Composite client1 = createSection(
				container,
				"Generate Report",
				"Create an Imixs Report to be stored into the filesystem or attached to the current Workitem or LOB Workitem.",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);
		
		
		
		Vector v=new Vector();
		v.add("attach to Workitem|0");
		v.add("attach to LOB Workitem|1");
		v.add("save to disk|2");
		Composite comu=this.createOptionInput(client1, "Target:",null,v,3,"txtReportTarget",false,null,null);

		FormData data = new FormData();
		data.left = new FormAttachment(0, getLabelWith());
		//data.right = new FormAttachment(100, 0);
		data.width=90;
		data.top = new FormAttachment(comu,0,
					ITabbedPropertyConstants.VSPACE);
		
		Text text=createTextInput(client1, "Report:", null,data,"txtReportName",comu);
	
		
	
		
		
		
		text=createTextInput(client1, "Filename:", null,null,"txtReportFilePath",text);
		
		
		text=createTextInput(client1, "Parameter:", null,null,"txtReportParams",text);
		
	}

}
