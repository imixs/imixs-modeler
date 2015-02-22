package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.ActivityEntity;
import org.imixs.eclipse.workflowmodeler.model.Configuration;

/**
 * Activity Property Tab for configuration the ownership to an Workitem
 * 
 * @author Ralph Soika
 */
public class ActivitySectionACL extends AbstractWorkflowPropertySection {
	private Vector vectorNameFields = null;
	Section sectionReader = null;
	Section sectionAuthor = null;
	Button buttonCheckbox = null;
	Composite writeCheckbox = null;
	Composite readCheckbox = null;
	Composite writeRoles = null;
	Composite readRoles = null;

	Composite dreck;;

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		/** create a Container Composite **/
		setLabelWith(50);

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);
		dreck = parent;
		Composite client1 = createSection(container,
				"Access Control List (ACL)",
				"The Access Control defines the Read- and Write Access assigend "
						+ "to a workitem. ", Section.TITLE_BAR
						| Section.DESCRIPTION | Section.EXPANDED, 0);

		buttonCheckbox = getWidgetFactory().createButton(client1,
				"Change ACL Settings", SWT.CHECK);

		Composite composite = getWidgetFactory().createComposite(client1,
				SWT.NONE);
		FormData data = new FormData();
		// data.left = new FormAttachment(0, labelWith);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);

		data.top = new FormAttachment(buttonCheckbox, 0,
				ITabbedPropertyConstants.VSPACE);
		composite.setLayoutData(data);

		// three columns layout
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 5;
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);

		// build reader section
		sectionReader = getWidgetFactory().createSection(composite,
				Section.TITLE_BAR | Section.EXPANDED);
		sectionReader.setText("Reader");
		sectionReader.setLayout(new FormLayout());
		Composite composite1 = getWidgetFactory().createComposite(
				sectionReader, SWT.NONE);
		composite1.setLayout(new FormLayout());

		sectionReader.setClient(composite1);

		// build Author section
		sectionAuthor = getWidgetFactory().createSection(composite,
				Section.TITLE_BAR | Section.EXPANDED);// | Section..EXPANDED
		sectionAuthor.setText("Authors");
		sectionAuthor.setLayout(new FormLayout());
		Composite composite2 = getWidgetFactory().createComposite(
				sectionAuthor, SWT.NONE);
		composite2.setLayout(new FormLayout());
		sectionAuthor.setClient(composite2);

		// checkboxen einfüllen

		readCheckbox = createOptionInput(composite1, null, null, null, 1,
				"keyAddReadFields", true, null, null);
		writeCheckbox = createOptionInput(composite2, null, null, null, 1,
				"keyAddWriteFields", true, null, null);

		
		
		// other roles
		
		readRoles = createTableInput(composite1, "Other:", null,
				"namAddReadAccess", 100, 30, readCheckbox);
		writeRoles = createTableInput(composite2, "Other:", null,
				"namAddWriteAccess", 100, 30, writeCheckbox);

		// Action Listener für Checkbox
		buttonCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button bb = (Button) e.getSource();
				changeACLMode(bb.getSelection());
			}
		});

	}

	/**
	 * Updates sthe ACL mode
	 */
	public void changeACLMode(boolean b) {
		if (b)
			getModelObject().setPropertyValue("keyAccessMode", "0");
		else
			getModelObject().setPropertyValue("keyAccessMode", "1");

		sectionAuthor.setVisible(b);
		sectionReader.setVisible(b);

		// clear and backup values?
		if (!b) {
			// backup
			getModelObject().setPropertyValue("keyAddReadFieldsBackup",
					getModelObject().getPropertyValue("keyAddReadFields"));
			getModelObject().setPropertyValue("keyAddWriteFieldsBackup",
					getModelObject().getPropertyValue("keyAddWriteFields"));

			getModelObject().setPropertyValue("namAddReadAccessBackup",
					getModelObject().getPropertyValue("namAddReadAccess"));
			getModelObject().setPropertyValue("namAddWriteAccessBackup",
					getModelObject().getPropertyValue("namAddWriteAccess"));

			getModelObject().setPropertyValue("namAddReadAccess", new Vector());
			getModelObject()
					.setPropertyValue("namAddWriteAccess", new Vector());
			getModelObject().setPropertyValue("keyAddReadFields", new Vector());
			getModelObject()
					.setPropertyValue("keyAddWriteFields", new Vector());

		} else {

			// restore values - if possible
			getModelObject()
					.setPropertyValue(
							"keyAddReadFields",
							getModelObject().getPropertyValue(
									"keyAddReadFieldsBackup"));
			getModelObject().setPropertyValue(
					"keyAddWriteFields",
					getModelObject()
							.getPropertyValue("keyAddWriteFieldsBackup"));

			getModelObject()
					.setPropertyValue(
							"namAddReadAccess",
							getModelObject().getPropertyValue(
									"namAddReadAccessBackup"));
			getModelObject().setPropertyValue(
					"namAddWriteAccess",
					getModelObject()
							.getPropertyValue("namAddWriteAccessBackup"));

			// force a full redraw of all checkboxes (I think that I hate
			// Eclipse SWT.....)
			vectorNameFields = null;
			updateActors();

			this.refresh();
			writeCheckbox.layout(true);
			readCheckbox.layout(true);

			dreck.pack(true);

			dreck.layout(true, true);

			dreck.update();
		}

	}

	/**
	 * This Method checks if the fieldMapings form the profile configiguration
	 * have changed
	 */
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);

		if (this.getModelObject() != null) {

			// update current mode in ui
			Object scurrentMode = getModelObject().getPropertyValue(
					"keyAccessMode");

			boolean bCurrentMode = (scurrentMode == null || "0"
					.equals(scurrentMode));
			buttonCheckbox.setSelection(bCurrentMode);
			sectionAuthor.setVisible(bCurrentMode);
			sectionReader.setVisible(bCurrentMode);

			if (bCurrentMode) {
				updateActors();
				dreck.pack(true);
				dreck.layout(true, true);

				dreck.update();
			}

		}
	}

	private void updateActors() {
		// update Actors
		Configuration confiProfil = ((ActivityEntity) this.getModelObject())
				.getProcessTree().getWorkflowModel().getEnvironment(
						WorkflowmodelerPlugin.ENVIRONMENT_PROFILE);
		if (confiProfil != null) {
			Vector vOptions;
			Object o = confiProfil.getPropertyValue("txtFieldMapping");
			if (o instanceof String) {
				vOptions = new Vector();
				vOptions.add(o);
			} else
				vOptions = (Vector) o;
			if (vOptions != null) {
				// so jetzt buttons bauen!
				if (!vOptions.equals(vectorNameFields)) {
					vectorNameFields = vOptions;
					updateInputOptions(vOptions, "keyAddReadFields", true);
					updateInputOptions(vOptions, "keyAddWriteFields", true);

				}
			}
		}
	}
}
