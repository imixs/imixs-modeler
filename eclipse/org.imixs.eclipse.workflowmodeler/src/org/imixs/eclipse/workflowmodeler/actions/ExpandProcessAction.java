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
public class ExpandProcessAction extends Action  {
	private ProcessEntityEditPart processEntityEditPart=null;
	
	public ExpandProcessAction() {
		setText("Expand");
	}
	
	public ExpandProcessAction(ProcessEntityEditPart aEntity) {
		setText("Expand");
		processEntityEditPart=aEntity;
	}
	
	
	/**
	 * In dieser Methode wird auf basis des aktuellen ProcessEntities ein neues
	 * ActivityEntity erzeugt und dem ProcessTree hinzugef�gt.
	 */
	public void run() {
		processEntityEditPart.expand();
	}
	
	
}

