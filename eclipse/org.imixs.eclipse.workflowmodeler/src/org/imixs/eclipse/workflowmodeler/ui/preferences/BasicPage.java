package org.imixs.eclipse.workflowmodeler.ui.preferences;

import java.util.Hashtable;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.imixs.eclipse.workflowmodeler.ServerConnector;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.styles.IEditorStyle;

/**
 * @author Ralph Soika
 */
public class BasicPage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private Hashtable hashTableStyles = new Hashtable();
	private Combo comboStyle = null;
	private Combo comboServer = null;

	// private Table tableDefaultProperties;

	public void init(IWorkbench workbench) {
		this.setPreferenceStore(WorkflowmodelerPlugin.getPlugin()
				.getPreferenceStore());

	}

	protected Control createContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE); // NONE

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;

		composite.setLayout(gridLayout);

		Label label = new Label(composite, SWT.NONE);
		label.setText("General Workflow preferences");

		/*** Styles ****/
		Group group = new Group(composite, SWT.NONE);
		group.setText("Graphical Editor Style:");
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		group.setLayout(gridLayout);

		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		group.setLayoutData(gd);

		label = new Label(group, SWT.NONE);
		label.setText("This setting controls which style is used by the graphical Editor"
				+ "\nThe Style defines the Layout and Design of the graphical components."
				+ "\nYou can choose different Styles defined by this product.");

		comboStyle = new Combo(group, SWT.NONE);

		gd = new GridData();
		gd.widthHint = 200;
		comboStyle.setLayoutData(gd);

		// read Extension Point for Routers to define the Combo Options
		hashTableStyles = new Hashtable();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("org.imixs.eclipse.workflowmodeler.editorstyles");
		IExtension[] extensions = extensionPoint.getExtensions();

		IConfigurationElement[] elements = extensions[0]
				.getConfigurationElements();
		String[] sStyleList = new String[elements.length];
		for (int j = 0; j < elements.length; j++) {
			try {
				// Name auslesen
				String sName = elements[j].getAttribute("name");
				Object editorStyle = elements[j]
						.createExecutableExtension("class");
				if (editorStyle instanceof IEditorStyle) {
					hashTableStyles.put(sName, editorStyle);
				}
				sStyleList[j] = sName;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		comboStyle.setItems(sStyleList);
		comboStyle.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo c = (Combo) e.widget;
				// the selected text will be back translatet
				// into the router class name
				String sText = c.getText();
				IEditorStyle editorStyle = (IEditorStyle) hashTableStyles
						.get(sText);
				WorkflowmodelerPlugin.getPlugin().setEditorStyle(editorStyle);

			}
		});

		// set current value
		comboStyle.setText(this.getPreferenceStore().getString("editorstyle"));

		
		
		
		
		// server connector list
		
		Group group2 = new Group(composite, SWT.NONE);
		group2.setText("Server Connector:");
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		group2.setLayout(gridLayout);

		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		group2.setLayoutData(gd);

		comboServer = new Combo(group2, SWT.NONE);

		gd = new GridData();
		gd.widthHint = 200;
		comboServer.setLayoutData(gd);

		// read Extension Point for Routers to define the Combo Options
		hashTableStyles = new Hashtable();
		registry = Platform.getExtensionRegistry();
		extensionPoint = registry
				.getExtensionPoint("org.imixs.eclipse.workflowmodeler.serverconnectors");
		extensions = extensionPoint.getExtensions();

		elements = extensions[0].getConfigurationElements();
		String[] sServerList = new String[elements.length];
		for (int j = 0; j < elements.length; j++) {
			try {
				// Name auslesen
				String sName = elements[j].getAttribute("name");
				Object editorStyle = elements[j]
						.createExecutableExtension("class");
				if (editorStyle instanceof IEditorStyle) {
					hashTableStyles.put(sName, editorStyle);
				}
				sServerList[j] = sName;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		comboServer.setItems(sServerList);
		comboServer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo c = (Combo) e.widget;
				// the selected text will be back translatet
				// into the router class name
				String sText = c.getText();

			}
		});

		// set current value
		comboServer.setText(this.getPreferenceStore().getString("servertype"));

		return composite;

	}

	protected void performDefaults() {
		super.performDefaults();

		// default Editor style
		String sStyle = this.getPreferenceStore().getDefaultString(
				"editorstyle");
		IEditorStyle editorStyle = (IEditorStyle) hashTableStyles.get(sStyle);
		WorkflowmodelerPlugin.getPlugin().setEditorStyle(editorStyle);
		comboStyle.setText(sStyle);

	}

	public boolean performOk() {

		// set editorstyle
		this.getPreferenceStore().setValue("editorstyle", comboStyle.getText());
		this.getPreferenceStore().setValue("servertype", comboServer.getText());

		return true;
	}

}
