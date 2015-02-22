package org.imixs.eclipse.workflowmodeler.ui.properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Activity Property Tab for configuration the Abstract text to an Workitem
 * 
 * @author Ralph Soika
 */
public class ProcessSectionAbstract extends AbstractWorkflowPropertySection {

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

		Composite client1 = createSection(
				container,
				"Workflow Abstract",
				"Abstract text information about this workflow status.",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);

		
		Text text=createTextInput(client1, "Summary:", null,null,"txtworkflowsummary",null);

		text=this.createTextareaInput(client1, "Abstract:",null, "txtworkflowabstract",text,90);


		
		
		
	
	}

}
