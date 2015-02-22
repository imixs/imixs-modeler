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
public class ActivitySectionArchive extends AbstractWorkflowPropertySection {

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

	
		Composite client1 = createSection(
				container,
				"Archive Activity",
				"The repository defines the location of the Archive System. \nThe ProcessID defines the " +
				"Process ID the archived workitem will adopt. \nThe optional ActivityID defines " +
				"a workflow activity which will be processed by the Archive Plugin inside the archive repository.",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);
		
		
		
		Vector v=new Vector();
		v.add("disabled|0");
		v.add("enabled|1");
		Composite comu=this.createOptionInput(client1, "Archive:",null,v,2,"keyArchive",false,null,null);

		Text text=createTextInput(client1, "Repository:", null,null,"txtArchivePath",comu);
	
		
		FormData data = new FormData();
		data.left = new FormAttachment(0, getLabelWith());
		//data.right = new FormAttachment(100, 0);
		data.width=90;
		data.top = new FormAttachment(text,0,
					ITabbedPropertyConstants.VSPACE);
		
		
		
		text=createTextInput(client1, "Process ID:", null,data,"numArchiveProcessID",text);
		
		data = new FormData();
		data.left = new FormAttachment(0, getLabelWith());
		//data.right = new FormAttachment(100, 0);
		data.width=90;
		data.top = new FormAttachment(text,0,
					ITabbedPropertyConstants.VSPACE);
		
		
		text=createTextInput(client1, "Activity ID:", null,data,"numArchiveActivityID",text);
	}

}
