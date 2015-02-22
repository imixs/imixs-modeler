package org.imixs.eclipse.workflowmodeler.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard Page to enter a file path.
 * 
 * I was unable to find one example to do this with a standard eclipse wizard
 * page (grrrr)
 * 
 * @author Ralph Soika
 */
public class FilePathWizardPage extends WizardPage {
	Text textFilePath;

	public FilePathWizardPage(String s) {
		super(s);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.numColumns = 2;
		layout.verticalSpacing = 15;

		composite.setLayout(layout);
		GridData gd = new GridData();

		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		composite.setLayoutData(gd);

		/** Process Input **/

		org.eclipse.swt.widgets.Group group = new org.eclipse.swt.widgets.Group(
				composite, SWT.NONE);

		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		group.setText("File Import:");

		layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.numColumns = 2;
		group.setLayout(layout);

		Label label;

		label = new Label(group, SWT.NONE);
		label.setText("Filepath: ");

		textFilePath = new Text(group, SWT.BORDER);

		textFilePath.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				true));

		setControl(composite);
		return;
	}

	public String getFilePath() {
		return textFilePath.getText();
	}

}
