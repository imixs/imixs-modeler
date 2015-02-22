package org.imixs.eclipse.workflowmodeler.actions;

import org.eclipse.jface.action.Action;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.ModelObject;
import org.imixs.eclipse.workflowmodeler.model.ProcessTree;

/**
 * 
 * This Action copies all entities included in a tree into another processTree
 * 
 * @author Ralph Soika
 * 
 */
public class PasteAllModelEntitiesAction extends Action {

	ProcessTree processTree;

	public PasteAllModelEntitiesAction() {
		setText("Paste all Entities");
		
	}

	public PasteAllModelEntitiesAction(ProcessTree aEntity) {
		setText("Paste all Entities");
		processTree = aEntity;

	}

	/**
	 * copy attributres using the WorkflowmodelerPlugin
	 * 
	 */
	public void run() {
		WorkflowmodelerPlugin.getPlugin().pasteProcessTree(processTree);
	}

}
