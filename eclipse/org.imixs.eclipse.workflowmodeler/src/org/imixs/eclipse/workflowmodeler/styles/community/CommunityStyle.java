
package org.imixs.eclipse.workflowmodeler.styles.community;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.imixs.eclipse.workflowmodeler.styles.*;
import org.imixs.eclipse.workflowmodeler.styles.popular.EntityConnectionRouter;
/**
 * Default community Style
 * 
 * !!!
 * This Style is currently not full implemented! 
 * So its recommanded to use only the Styles form the package
 *  org.imixs.eclipse.workflowmodeler.styles.popular
 * 
 * @author Ralph Soika

 */
public class CommunityStyle implements IEditorStyle {
	private ConnectionRouter connectionRouter;
	
	/* (non-Javadoc)
	 * @see org.imixs.eclipse.workflowmodeler.styles.IEditorStyle#createActivityFigure()
	 */
	public IEntityFigure createActivityFigure() {
		return new ActivityEntityFigure();
	}
	  
	/* (non-Javadoc)
	 * @see org.imixs.eclipse.workflowmodeler.styles.IEditorStyle#createProcessFigure()
	 */
	public IEntityFigure createProcessFigure() {
		return new ProcessEntityFigure();
	}
	
	
	
	/**
     * The IXGraphicalEditor supports different typs of ConnectionRouters to connect
     * ProcessEntityEditPart and ActivityEntityEditPart objects. 
	 * this method returns an instance of the community ConnectionRouter.
     * 
     * @return ConnectionRouter
	 */
	public ConnectionRouter getConnectionRouter() {
		if (connectionRouter==null) 
    		connectionRouter=new EntityConnectionRouter();
    	return connectionRouter;
	}
	
	/** 
	 * Creates an simple PolylineConnection with an individual Connection Router 
	 */
	public IFigure createConnectionFigure(boolean outgoing) {
		PolylineConnection polylineConnection = new PolylineConnection();
    	polylineConnection.setTargetDecoration(new PolygonDecoration());
        // polylineConnection.setConnectionRouter( new ManhattanConnectionRouter());
    	// polylineConnection.setConnectionRouter( new EntityConnectionRouter());
    	polylineConnection.setConnectionRouter(getConnectionRouter());
    	polylineConnection.setLineStyle(SWT.LINE_SOLID);
		/*
		 * SWT.LINE_SOLID
		 * SWT.LINE_DASH;
		 * SWT.LINE_DASHDOT;
		 * SWT.LINE_DASHDOTDOT;
		 * SWT.LINE_DOT;
		 */
    	polylineConnection.setLineWidth(1);
    	polylineConnection.setForegroundColor(new Color(null,64,64,64));
        return polylineConnection;
	}
}
