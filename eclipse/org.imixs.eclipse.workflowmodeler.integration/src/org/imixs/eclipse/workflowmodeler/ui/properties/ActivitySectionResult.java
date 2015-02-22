package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Vector;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Activity Property Tab for configuration the ownership to an Workitem
 * 
 * @author Ralph Soika
 */
public class ActivitySectionResult extends AbstractWorkflowPropertySection {

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

	
		Composite client1 = createSection(
				container,
				"Result Message",
				"A Result Message defines an information given back to the workitem after processing. This could be an URL Information or HTML Code.",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);
		
		this.createTextareaInput(client1, "Message:",null, "txtActivityResult",null,80);

	}

}
