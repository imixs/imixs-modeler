package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.ActivityEntity;
import org.imixs.eclipse.workflowmodeler.model.Configuration;

/**
 * Activity Property Tab for configuration the ownership to an Workitem
 * 
 * @author Ralph Soika
 */
public class ActivitySectionMail extends AbstractWorkflowPropertySection {
	private Vector vectorNameFields = null;

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/
		//setLabelWith(50);; 

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

		Composite client1 = createSection(
				container,
				"Mail",
				"Defines a Mail Event. Enter receipients, subject and mailbody.",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0
		);
	

		/** Eingabe Feld */		
		//Image img=WorkflowmodelerPlugin.getPlugin().getIcon("properties/actor.gif").createImage();
		
		Composite comp=this.createUserRoleInput(client1, "SendTo:", null,null,
				"namMailReceiver","keyMailReceiverFields", null) ;
		

		comp=this.createUserRoleInput(client1, "CopyTo:", null,null,
				"namMailReceiverCC","keyMailReceiverFieldsCC", comp) ;
		
		//setLabelWith(70);
		Text text=this.createTextInput(client1, "Subject:",null,null, "txtmailsubject",comp);

		//this.setLabelWith(ilW);
		this.createTextareaInput(client1, "Body:",null, "rtfmailbody",text,160);

		
	}

	
	
	/**
	 * This Method checks if the fieldMapings form the profile configiguration have changed
	 */
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		
		if (this.getModelObject()!=null) {
			Configuration confiProfil=((ActivityEntity)this.getModelObject()).getProcessTree().getWorkflowModel().getEnvironment(WorkflowmodelerPlugin.ENVIRONMENT_PROFILE);
			if (confiProfil!=null) {
				Vector vOptions;
				Object o=confiProfil.getPropertyValue("txtFieldMapping");
				if (o instanceof String) {
					vOptions=new Vector();
					vOptions.add(o);
				} else
					vOptions=(Vector)o;
				if (vOptions!=null) {
					// so jetzt buttons bauen!
					if (!vOptions.equals(vectorNameFields)) {
						vectorNameFields=vOptions;
						updateInputOptions(vOptions,"keyMailReceiverFields",true);
						updateInputOptions(vOptions,"keyMailReceiverFieldsCC",true);
					
					}
				}
			}
		}
	}
}
