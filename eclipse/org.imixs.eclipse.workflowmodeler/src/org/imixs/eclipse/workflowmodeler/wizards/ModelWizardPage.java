

package org.imixs.eclipse.workflowmodeler.wizards;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.wizard.*;
import org.eclipse.swt.*;
/**
 * Wizard Page to create a model
 * @author Ralph Soika
 */
public class ModelWizardPage extends WizardPage implements ModifyListener {
	Text  textProcess;
	//Text textModel, textProcess;
	
	
	public ModelWizardPage(String s) {
		super(s);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		
		
		GridLayout layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		layout.numColumns = 2;
		layout.verticalSpacing=15;
		
		composite.setLayout(layout);
		GridData gd=new GridData();
		
		gd.horizontalAlignment=GridData.FILL;
		gd.verticalAlignment=GridData.BEGINNING;
		composite.setLayoutData(gd);
		
		/** Model Input **/
		
		/*
		Label label = new Label (composite, SWT.NONE);
		label.setText("Workflow Model: ");
		textModel=new Text( composite, SWT.BORDER);
		gd=new GridData();
		
		gd.horizontalAlignment=GridData.FILL;
		gd.verticalAlignment=GridData.BEGINNING;
		gd.widthHint=300;
		
		textModel.setLayoutData(gd);
		textModel.addModifyListener(this);
		
		*/
		
		/** Process Input **/
		
		org.eclipse.swt.widgets.Group group=new org.eclipse.swt.widgets.Group(composite,SWT.NONE);
		
		gd=new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan=2;
		group.setLayoutData(gd);
		
		group.setText("Default Process Group:");
	
		layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		layout.numColumns = 2;
		group.setLayout(layout);
		
		
		
		Label label = new Label (group, SWT.WRAP);
		label.setText("A Process Group defines a singel business process inside the model. "
				+"The Process Group can contain Process Entities and Process Activities edited "
				+" using the graphical model editor. ");
		//label.setBounds(120, 10, 100, 100);
		gd=new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan=2;
		gd.widthHint=200;
		label.setLayoutData(gd);
		
		
		label = new Label (group, SWT.NONE);
		label.setText("Process Name: ");
		
		textProcess=new Text( group, SWT.BORDER);
	
		textProcess.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		textProcess.addModifyListener(this);
		
		
		setControl(composite);
		return;
	}
	
	/*
	public String getModelName() {
		return textModel.getText();
	}
	*/
	
	public String getProcessName() {
		return textProcess.getText();
	}
	
	
	public void modifyText(ModifyEvent e) {
		/*
		String sModel=textModel.getText();
		String sTree=textProcess.getText();
		
		
		
		setPageComplete(!"".equals(sTree) && !"".equals(sModel));
		*/
	}

}
