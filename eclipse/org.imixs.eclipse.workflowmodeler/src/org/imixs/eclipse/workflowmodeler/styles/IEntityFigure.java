
package org.imixs.eclipse.workflowmodeler.styles;

import org.eclipse.draw2d.IFigure;
import org.imixs.eclipse.workflowmodeler.model.*;
/**
 * Defines the interface for figures used by the editParts 
 * ActivityEntityEditPart and ProcessEntityEditPart
 * 
 * @author Ralph Soika
 */
public interface IEntityFigure extends IFigure {

	/**
	 * Refreshes the EntityFigures <i>visuals</i>. This method is called by the
	 * org.imixs.eclipse.workflowmodeler.ui.editparts in response to notifications from the model. 
	 
	 */
	public void refreshVisuals(AbstractWorkflowEntity workflowEntity);
	
	
}











