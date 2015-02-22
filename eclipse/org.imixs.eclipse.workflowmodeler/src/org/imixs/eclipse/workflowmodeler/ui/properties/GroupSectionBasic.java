package org.imixs.eclipse.workflowmodeler.ui.properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.imixs.eclipse.workflowmodeler.model.ModelObject;

/**
 * Activity Property Tab for configuration the name of a processtree (workflowgroup)
 * 
 * @author Ralph Soika
 */
public class GroupSectionBasic extends AbstractWorkflowPropertySection {
	private Text labelText;

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent arg0) {
			ModelObject mo = getModelObject();
			mo.setPropertyValue("name", labelText.getText());
		}
	};

	public void createControls(Composite aparent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(aparent, aTabbedPropertySheetPage);

		/** create a Container Composite **/

		Composite container = getWidgetFactory().createFlatFormComposite(
				aparent);

		Composite client1 = createSection(
				container,
				"General Information",
				"This section defines the Name for the Processgroup",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0

		);

		FormData data;

		labelText = getWidgetFactory().createText(client1, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		labelText.setLayoutData(data);
		labelText.addModifyListener(listener);

		CLabel labelLabel = getWidgetFactory().createCLabel(client1, "Name:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(labelText,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(labelText, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);

	}

	public void refresh() {
		labelText.removeModifyListener(listener);
		ModelObject mo = getModelObject();

		String sName = (String) mo.getPropertyValue("name");
		if (sName == null)
			sName = "no value";
		labelText.setText(sName);
		labelText.addModifyListener(listener);
	}

}
