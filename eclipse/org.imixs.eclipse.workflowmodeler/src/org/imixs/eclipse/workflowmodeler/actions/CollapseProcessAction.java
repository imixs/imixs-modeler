package org.imixs.eclipse.workflowmodeler.actions;

import org.eclipse.jface.action.Action;
import org.imixs.eclipse.workflowmodeler.ui.editparts.*;
/**
 * @author Ralph Soika
 * 
 * Diese Action dient dazu ein ProcessEntityEditPart zusammenzuklappen um so 
 * die zugeh�rigen ActivityEditParts zu verschtekcen
 *
 */
public class CollapseProcessAction extends Action  {
	private ProcessEntityEditPart processEntityEditPart=null;
	
	public CollapseProcessAction() {
		setText("Collapse");
	}
	
	public CollapseProcessAction(ProcessEntityEditPart aEntity) {
		setText("Collapse");

		processEntityEditPart=aEntity;
	}
	
	
	/**
	 * In dieser Methode wird auf basis des aktuellen ProcessEntities ein neues
	 * ActivityEntity erzeugt und dem ProcessTree hinzugef�gt.
	 */
	public void run() {
		processEntityEditPart.collapse();
	}
	
	
}

