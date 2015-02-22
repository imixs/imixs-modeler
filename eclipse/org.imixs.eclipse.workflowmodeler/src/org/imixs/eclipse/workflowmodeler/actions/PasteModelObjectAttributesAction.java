package org.imixs.eclipse.workflowmodeler.actions;

import org.eclipse.jface.action.Action;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.ModelObject;

/**
 * 
 * This Action copies the attributes from a given Activity Object to be pased to
 * onother Object
 * 
 * @author Ralph Soika
 * 
 */
public class PasteModelObjectAttributesAction extends Action {

	ModelObject currentEntity;

	public PasteModelObjectAttributesAction() {
		setText("Paste Properties");
		
	}

	public PasteModelObjectAttributesAction(ModelObject aEntity) {
		setText("Paste Properties");
		currentEntity = aEntity;

	}

	/**
	 * copy attributres using the WorkflowmodelerPlugin
	 * 
	 */
	public void run() {
		WorkflowmodelerPlugin.getPlugin().pasteModelObjectAttributes(
				currentEntity);
	}

}
