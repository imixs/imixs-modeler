package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Vector;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.IWorkbenchPart;
import org.imixs.eclipse.workflowmodeler.model.*;
import org.imixs.eclipse.workflowmodeler.*;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;

/**
 * Activity Property Tab for configuration the ownership to an Workitem
 * 
 * @author Ralph Soika
 */
public class ProcessSectionApplication extends AbstractWorkflowPropertySection {
	private Vector vectorMailReceiverFields = null;

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

		Composite client1 = createSection(
				container,
				"Display Information",
				"Specify a form name in which this process state should be displayed and executed." +
				"\nThe Image URL can be used to visualize the current workflow status." +
				"\nThe Type property defines the type associated with the workitem. ",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);

		
		Text text=createTextInput(client1, "Form Name:", null,null,"txtEditorID",null);
		
		text=createTextInput(client1, "Image URL:", null,null,"txtImageURL",text);

		text=createTextInput(client1, "Type:", null,null,"txtType",text);

		
	
	}

}
