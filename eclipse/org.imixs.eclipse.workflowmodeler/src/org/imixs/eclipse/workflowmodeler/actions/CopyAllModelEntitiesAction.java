package org.imixs.eclipse.workflowmodeler.actions;

import org.eclipse.jface.action.Action;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.ProcessTree;

/**
 * 
 * This Action copies a full ProcessTree form one IXGraphicalEditor into another.
 * So this action is used as general functionallity in the GEF Editor. 
 * 
 * @see WorkflowEntityContextMenuProvider
 * @author Ralph Soika
 * 
 */
public class CopyAllModelEntitiesAction extends Action {

	ProcessTree processTree;
	

	public CopyAllModelEntitiesAction() {
		setText("Copy All Entities");
	}

	public CopyAllModelEntitiesAction(ProcessTree aEntity) {
		setText("Copy All Entities");
		processTree = aEntity;

	}

	/**
	 * copy attributres using the WorkflowmodelerPlugin
	 * 
	 */
	public void run() {
		WorkflowmodelerPlugin.getPlugin().copyProcessTree(processTree);
	}

}
