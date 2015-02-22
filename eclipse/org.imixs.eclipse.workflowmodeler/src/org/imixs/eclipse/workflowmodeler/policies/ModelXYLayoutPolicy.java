
package org.imixs.eclipse.workflowmodeler.policies;

import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
//import org.eclipse.draw2d.geometry.Rectangle;
//import org.imixs.eclipse.workflowmodeler.model.AbstractWorkflowEntity;

/**
 * Diese Klasse stellt eine XYLayoutEditPolicy bereit, welche das Positionieren neuer 
 * EditParts und das Verschieben von EditParts auf der ModelTreeEditPart Oberfläche 
 * ermöglicht
 * 
 * @author Ralph Soika
 *
 */
public class ModelXYLayoutPolicy extends XYLayoutEditPolicy {
	public ModelXYLayoutPolicy(XYLayout layout) {
        super();
        setXyLayout(layout);
    }
	
    protected Command createAddCommand(EditPart child, Object constraint) {
        return null;
    }
    
    /**
     * Durch diese Methode werden Commands zum Verschieben von EditPart Komponenten 
     * empfangen und die Position an das entsprechende WorkflowEntity weitergegeben,
     * wedurch sich dieses verschiebt
     * 
     * Da das verschieben von Elementen in diesem Modeler nicht vorgesehen ist wird 
     * kein entsprechender Command erzeugt (return null;). Der Code ist daher auskommentiert.
     */
    protected Command createChangeConstraintCommand(final EditPart child, final Object constraint) {
    	return null;
       
    	/*return new Command() {
            public void execute() {
                Rectangle newBounds = (Rectangle) constraint;
                AbstractWorkflowEntity abstractWorkflowEntity = (AbstractWorkflowEntity) child.getModel();
                abstractWorkflowEntity.setX(newBounds.x);
                abstractWorkflowEntity.setY(newBounds.y);
            }
        };
        */
       
    }
    
    /**
     * Diese Methode dient eigentlich nur dazu nach dem ein neues ModellObjekt durch einen
     * Command Befehl (IXGraphicalEditor) erzeugt wurde diese mit den korrekten X/Y Koordinaten
     * zu versehen. Diese können über das request Objekt ermittelt werden.
     * 
     * Der Imixs Modeler ordnet aber die Elemente sowiso automtisch an, so das die beiden 
     * Positionierungmethoden
     * 
     *   		 pe.setX(request.getLocation().x);
     *  		 pe.setY(request.getLocation().y);
     * 
     * nicht benötigt werden. 
     * Daher gibt die Methode eine leere Command Methode zurück, die lediglich das "Hineinziehen" 
     * von elementen erlaubt, aber nicht deren Positionsdaten an das neue Objekt übergibt. 
     * 
     */
    protected Command getCreateCommand(final CreateRequest request) {
        return new Command() {
        	public void execute() {
            	// Positionsdaten werden nicht übergeben, da diese vom Modell selbst berechnet werden
           		// AbstractWorkflowEntity pe= (AbstractWorkflowEntity)request.getNewObject();
        		// pe.setX(request.getLocation().x);
        		// pe.setY(request.getLocation().y);
        	}
        	
        };
    }
    
    /**
     * Befehl nicht unterstützt
     */
    protected Command getDeleteDependantCommand(Request request) {
        return null;
    }
}
