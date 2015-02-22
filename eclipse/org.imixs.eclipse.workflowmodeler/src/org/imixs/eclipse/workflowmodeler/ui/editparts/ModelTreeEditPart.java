
package org.imixs.eclipse.workflowmodeler.ui.editparts;

import java.util.*;
import java.beans.*;
import org.eclipse.draw2d.*;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.*;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.imixs.eclipse.workflowmodeler.model.*;
import org.imixs.eclipse.workflowmodeler.policies.*;

/**
 * Diese Klasse ist Umspannende Figure Objetke f�r die Anzeige der 
 * einzelnen EditPart Komponenten.
 *
 *
 * 
 * ScalableRootEditPart
 * AbstractGraphicalEditPart
 * 
 * @author Ralph Soika
 */
public class ModelTreeEditPart extends AbstractGraphicalEditPart 
			implements PropertyChangeListener,IGroupPropertySource {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
	
		/*
		Figure f=new Figure();
		f.setOpaque(true);
		f.setBackgroundColor(ColorConstants.white);
		f.setLayoutManager(new XYLayout());
		return f;
		*/
		
		
		Figure f = new FreeformLayer();
		f.setBorder(new MarginBorder(3));
		f.setLayoutManager(new FreeformLayout());

		// Create the static router for the connection layer
	//	ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
	//	connLayer.setConnectionRouter(new ShortestPathConnectionRouter(f));
		
		return f;
		
		
	}
	
	
	public void activate() {
        //super.activate();
        // Im folgenden registriert sich die EditPart Komponente als Observer f�r Modell�dnerungen
        // Dadurch k�nnen ModelObjekte hinzugef�gt werden ohne das die Anzeige explizit aktualisert
        // werden mu�
       // getModelTree().addPropertyChangeListener(this);
		
		if (!isActive()) {
			super.activate();
			getModelTree().addPropertyChangeListener(this);
		}
    }

    public void deactivate() {
        getModelTree().removePropertyChangeListener(this);
        super.deactivate();
    }

    /**
     * In dieser Methode werden EditPolicies registriert, welche zum Beispiel das Verschieben
     * von Elementen erlauben (XYLayoutEditPolicy)
     */
    protected void createEditPolicies() {
		// das Verschieben von Elementen wir in ModelXYLayoutPolicy unterbunden
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ModelXYLayoutPolicy((XYLayout) getFigure().getLayoutManager()));
	}

 
    public void propertyChange(PropertyChangeEvent evt) {
        refreshChildren();
    }    
 
    /**
     * Returns a <code>List</code> containing the children
     * model objects. If this EditPart's model is a container, this method should be
     * overridden to returns its children. This is what causes children EditParts to be
     * created.
     * <P>
     * Callers must not modify the returned List. Must not return <code>null</code>.
     * @return the List of children
     */
	protected List getModelChildren() {
		return getModelTree().getWorkflowEntities();
	}

	
	private ProcessTree getModelTree() {
		return (ProcessTree)getModel();
	}
	
	
	
	/**
	 * This Method collapse all ProcessEntities to hide there Activities
	 *
	 */
	public void collapseAllProcessEntities() {
		List listChildren=this.getChildren();
		Iterator iter=listChildren.iterator();
		while (iter.hasNext()) {
			AbstractGraphicalEditPart aEditPart=(AbstractGraphicalEditPart)iter.next();
			if (aEditPart instanceof ProcessEntityEditPart) {
				if (((ProcessEntityEditPart)aEditPart).isExpanded())
					((ProcessEntityEditPart)aEditPart).collapse();
				
			}
			
		}
		
	}
	
	
	
	
	
}















