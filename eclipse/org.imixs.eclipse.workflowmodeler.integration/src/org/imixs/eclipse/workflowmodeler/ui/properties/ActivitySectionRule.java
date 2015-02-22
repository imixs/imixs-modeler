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
public class ActivitySectionRule extends AbstractWorkflowPropertySection {

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

	
		Composite client1 = createSection(
				container,
				"Business-Rule",
				"Definition of optional business rules which should be evaluated.",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);
		
		
	
		
		Text text=createTextInput(client1, "Engine:", null,null,"txtBusinessRuleEngine",null);
	
		this.createTextareaInput(client1, "Rule:",null, "txtBusinessRule",text,140);

	}

}
