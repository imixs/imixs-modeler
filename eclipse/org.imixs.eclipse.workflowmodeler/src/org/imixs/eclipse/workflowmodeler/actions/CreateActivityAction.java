package org.imixs.eclipse.workflowmodeler.actions;

import org.eclipse.jface.action.Action;
import org.imixs.eclipse.workflowmodeler.model.*;

/**
 * 
 * This Action creates a new Activity Object. The Method expects a ProcessEntity
 * in the constructor. Outgoing from this Entity an Activity with the right
 * numProcessID will be created
 * 
 * @author Ralph Soika
 * 
 */
public class CreateActivityAction extends Action {

	private ProcessEntity processEntity = null;

	public CreateActivityAction() {
		setText("Create new activity");
	}

	public CreateActivityAction(ProcessEntity aEntity) {
		setText("Create new activity");
		processEntity = aEntity;
	}

	/**
	 * This method creates a new Activity for the current ProcessEntity Model
	 * object
	 * 
	 */
	public void run() {
		processEntity.createActivityEntity();
	}

}
