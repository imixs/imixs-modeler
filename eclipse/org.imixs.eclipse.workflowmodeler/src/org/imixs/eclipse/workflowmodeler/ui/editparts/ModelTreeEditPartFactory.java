package org.imixs.eclipse.workflowmodeler.ui.editparts;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.imixs.eclipse.workflowmodeler.model.*;


/**
 * Diese Klasse dient dazu aus den Modelobjekten entsprechende Graphische Representationen 
 * zu erzeugen. Dazu wird die Methode createEditPart überschrieben, und anhand des Typs 
 * des übergebenen Model Objekts das dazugehörige EditPart Objekt erzeugt.
 * 
 * Die ModelTreeEditPartFactory ist dem IXGraphicalEditor zugeordnet damit dieser die EditParts
 * erzeugen kann.
 * 
 * @author Ralph Soika
 */
public class ModelTreeEditPartFactory implements EditPartFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object modelObject) {
		EditPart result=null;
		// entsprechendes Graphikobjekt erzeugen
		if (modelObject instanceof ProcessTree)
			result=new ModelTreeEditPart();
		else if (modelObject instanceof ProcessEntity)
			result = new ProcessEntityEditPart();
	     else if (modelObject instanceof ActivityEntity) {
            result = new ActivityEntityEditPart();
            /*
            try {
	          //  ActivityEntity ae=(ActivityEntity)modelObject;
            }catch (Exception e) {
            }*/
            
	     }
	     	else if (modelObject instanceof Association)
	     		result = new AssociationEditPart();		
	     
        result.setModel(modelObject);
        return result;
	}

}