package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Collection;
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
public class ActivitySectionBasicAccess extends AbstractWorkflowPropertySection {
	private Vector vectorNameFields = null;

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

		Composite client2 = createSection(
				container,
				"Visibility and Access",
				"Set visible to 'No' if the Activity should not be shown in the user frontend. "
						+ "Add roles to restrict the accessibility only for defined actors in the user interface."
						+ "", Section.TITLE_BAR | Section.DESCRIPTION
						| Section.EXPANDED, 0

		);

		Image img = WorkflowmodelerPlugin.getPlugin()
				.getIcon("properties/actor.gif").createImage();
		Vector v = new Vector();
		v.add("Yes|1");
		v.add("No|0");
		Composite comu = this.createOptionInput(client2, "Visible:", null, v,
				2, "keyPublicResult", false, null, null);

		// $ReadAccess
		this.createTableInput(client2, "Roles:", img, "$ReadAccess", 100, 50,
				comu);

	}

}
