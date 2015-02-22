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
public class ActivitySectionOwner extends AbstractWorkflowPropertySection {
	private Vector vectorMailReceiverFields = null;

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		/** create a Container Composite **/
		setLabelWith(50);; 

		Composite container = getWidgetFactory()
				.createFlatFormComposite(parent);

		Composite client1 = createSection(
				container,
				"Ownership",
				"Defines the ownership of a workitem. Update option will leave current ownership settings. Renew option will replace ownership settings",
				Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED, 0
		);


		/** Eingabe Feld */
		
		
		Vector v=new Vector();
		v.add("Update|1");
		v.add("Renew|0");
		Composite ccheck=this.createOptionInput(client1, "Mode:",null,v,2,"keyOwnershipMode",false,null,null);
	
		
		FormData data;
		/** Filedset*/
		/*
		Composite composite =getWidgetFactory().createGroup(client1, "hallo");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);


			data.top = new FormAttachment(ccheck, 0,
					ITabbedPropertyConstants.VSPACE);
		composite.setLayoutData(data);
		 
		composite.setLayout(new FormLayout());
		ccheck=this.createTableInput(composite, "Names:",null, "namOwnershipNames",100,30,ccheck);
			
		ccheck=this.createOptionInput(composite, "Fields:",null,null,2,"keyOwnershipFields",true,null,ccheck);
		
		*/
		
		
		this.createUserRoleInput(client1, "Names && Roles", null,null,
				"namOwnershipNames","keyOwnershipFields", ccheck) ;
		
		
		
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
					if (!vOptions.equals(vectorMailReceiverFields)) {
						vectorMailReceiverFields=vOptions;
						updateInputOptions(vOptions,"keyOwnershipFields",true);
					
					}
				}
			}
		}
	}
}
