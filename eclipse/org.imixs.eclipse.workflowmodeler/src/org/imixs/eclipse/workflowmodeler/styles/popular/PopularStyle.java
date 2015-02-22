
package org.imixs.eclipse.workflowmodeler.styles.popular;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.imixs.eclipse.workflowmodeler.styles.*;
/**
 * Default community Style
 * 
 * @author Ralph Soika

 */
public class PopularStyle implements IEditorStyle {
	
	public static final int COLOR_PROCESS_BODY=1;
	public static final int COLOR_PROCESS_TITLE=2;
	public static final int COLOR_ACTIVITY_BODY=3;
	public static final int COLOR_ACTIVITY_TITLE=4;
	
	private ConnectionRouter connectionRouter;
	
	/* (non-Javadoc)
	 * @see org.imixs.eclipse.workflowmodeler.styles.IEditorStyle#createActivityFigure()
	 */
	public IEntityFigure createActivityFigure() {
		return new ActivityEntityFigure(this);
	}
	
	/* (non-Javadoc)
	 * @see org.imixs.eclipse.workflowmodeler.styles.IEditorStyle#createProcessFigure()
	 */
	public IEntityFigure createProcessFigure() {
		return new ProcessEntityFigure(this);
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
    	polylineConnection.setLineStyle(SWT.LINE_DASHDOTDOT);
    	//polylineConnection.setLineStyle(SWT.LINE_SOLID);
    	polylineConnection.setLineWidth(1);
    	if (!outgoing)
    		//polylineConnection.setForegroundColor(new Color(null,42,66,135));
    		polylineConnection.setForegroundColor(getColor(PopularStyle.COLOR_ACTIVITY_TITLE));
    	
    	
    	else
        	//polylineConnection.setForegroundColor(new Color(null,64,64,64));
    		polylineConnection.setForegroundColor(getColor(PopularStyle.COLOR_PROCESS_TITLE));

    	
    	
        return polylineConnection;
	}
	
	
	// Colors
	
	static Color c_pb=new Color(null, 255, 255, 204);
	static Color c_pt=new Color(null, 255, 204, 0);
	static Color c_ab=new Color(null, 219, 228, 240);
	static Color c_at=new Color(null, 93, 125, 173);
	
	public Color getColor(int type) {
		switch (type) {
			case COLOR_PROCESS_BODY: return c_pb;
			case COLOR_PROCESS_TITLE: return c_pt;
		
			case COLOR_ACTIVITY_BODY: return c_ab;
			case COLOR_ACTIVITY_TITLE: return c_at;

			default: return c_pb;
		}
		
		
	}
	
}
