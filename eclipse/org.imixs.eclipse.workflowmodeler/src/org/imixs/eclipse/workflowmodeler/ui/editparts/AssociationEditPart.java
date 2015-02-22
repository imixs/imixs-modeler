package org.imixs.eclipse.workflowmodeler.ui.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.commands.AssociationDeleteCommand;
import org.imixs.eclipse.workflowmodeler.model.Association;
import org.imixs.eclipse.workflowmodeler.model.IActivityPropertySource;

public class AssociationEditPart extends AbstractConnectionEditPart implements IActivityPropertySource {
    protected IFigure createFigure() {
    	/*
    	 * Create an Figure to draw the connections. nomraly an PolylineConnection
    	 * the parameter indicates if the connection goes out from an processEntity or in.
    	 */
    	return WorkflowmodelerPlugin.getPlugin().getStyle().createConnectionFigure(this.getSource() instanceof ProcessEntityEditPart);
    }
    
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());

        installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
            protected Command getDeleteCommand(final GroupRequest request) {
                return new AssociationDeleteCommand((Association) getModel());
            }
        });
    }

}














