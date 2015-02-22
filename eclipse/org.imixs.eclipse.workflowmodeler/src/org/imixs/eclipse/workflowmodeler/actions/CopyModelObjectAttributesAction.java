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
public class CopyModelObjectAttributesAction extends Action {

	ModelObject currentEntity;

	public CopyModelObjectAttributesAction() {
		setText("Copy Properties");
	}

	public CopyModelObjectAttributesAction(ModelObject aEntity) {
		setText("Copy Properties");
		currentEntity = aEntity;

	}

	/**
	 * copy attributres using the WorkflowmodelerPlugin
	 * 
	 */
	public void run() {
		WorkflowmodelerPlugin.getPlugin().copyModelObjectAttributes(
				currentEntity);
	}

}
