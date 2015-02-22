
package org.imixs.eclipse.workflowmodeler.styles;

import org.eclipse.draw2d.IFigure;

/**
 * This Interface defines a Style to be used by the 
 * IXGraphicalEditor to create the model figures
 * The Styles ard defined by ExtensionPoints and configured by the 
 * Modeler Preferences Page .
 * 
 * @author Ralph Soika
 */
public interface IEditorStyle {

	/**
	 * Creates a new Instace of an ProcessEntityFigure.
	 * This Method is used by the ProcessEntityEditPart
	 * @return
	 */
	public IEntityFigure createProcessFigure();
	 
	/**
	 * Creates a new Instace of an ActivityEntityFigure.
	 * This Method is used by the ActivityEntityEditPart
	 * @return
	 */
	public IEntityFigure createActivityFigure();
	
	/**
	 * Creates a new Instace of an Connection Figure.
	 * This Method is used by the AssociationEditPart
	 * The parameter outgoing indicates if the figure is used
	 * to draw the outgoing connection form an processEntityEditPart 
	 * 
	 * @param outgoing
	 * @return
	 */
	public IFigure createConnectionFigure(boolean outgoing);
		
}
