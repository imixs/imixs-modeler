
package org.imixs.eclipse.workflowmodeler.styles.community;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import org.imixs.eclipse.workflowmodeler.styles.*;

/**
 * Diese Klasse stellt einen eigenen ConnectionRouter bereit, der die Verbindung zwischen
 * ProcessEntities und ActivityEntities steuert. Dabei wird ein individuelles Routing angewendet,
 * welches feste Routen berechnet.
 * Die Klasse unterscheidet zwei Routen
 *  1) von ProcessEntity -> ActivityEntity
 *  2) von ActivityEntity -> ProcessEntity
 * 
 * @author Ralph Soika
 *
 */
public class EntityConnectionRouter extends AbstractRouter {
	final boolean bRoundedEdges=false;
	/**
	 * Die route Methode berechnet die Route zwischen zwei Objekten, in dem eine 
	 * Liste von Points (PointList) errechnet wird und an das connection Objekt übergeben wird.
	 * Der Parameter connection ist also ein valueObjekt, welches durch die Methode manipuliert wird.
	 */
	public void route(Connection connection) {
		IEntityFigure fromFigure=null,toFigure=null;
		PointList points = connection.getPoints();
		points.removeAllPoints();
		Point pStart,pEnd;
		
		/** Startpunkt berechnen  **/
		try {
			fromFigure=(IEntityFigure)connection.getSourceAnchor().getOwner();
			pStart=computeStartPoint(fromFigure);			
		} catch (Exception expStart) {
			// das Ziel muß kein Entty sein, wen die Maus über einen freien Bereich bewegt wird!
			pStart=getStartPoint(connection); // Default!
		}
		//connection.translateToRelative(pStart  );
		points.addPoint(pStart);
		
		/** Zielpunkt berechnen  **/
		try {
			toFigure=(IEntityFigure)connection.getTargetAnchor().getOwner();
			pEnd=computeEndPoint(toFigure);
		} catch (Exception extEnd) {
			pEnd = getEndPoint(connection);
		}
		//connection.translateToRelative(pEnd);
		
		points.addPoint(pEnd);
		
		
//		if (fromFigure instanceof org.imixs.eclipse.workflowmodeler.ui.figures.ProcessEntityFigure)
		if (fromFigure instanceof ProcessEntityFigure)
			routeFromProzessToActivity(points);
		else
			if (fromFigure instanceof ActivityEntityFigure)
				routeFromActivityToProcess(points);
		    else {
		    	Point p=new Point(pStart.x, pEnd.y);
		    	points.insertPoint(p,1);
		    }
		/** Rückgabe der Route **/
		connection.setPoints(points);
		

	}

	/**
	 * Diese Methode routet von einem ProzessEntity zu einem ActivityEntity
	 * 
	 * ---------------------------
	 * |                         |
	 * |                         V  
	 * 
	 * ------------------------
	 * |                      |
	 * |                      - ->  
	 * 
	 * @param pointList
	 */
	public void routeFromProzessToActivity(PointList pointList) {
		Point pStart=pointList.getPoint(0);
		Point pEnd=pointList.getPoint(1);
		
		// Gerudete Ecken
		if (bRoundedEdges) {
			pointList.insertPoint(new Point(pStart.x,pStart.y-13),1);
			pointList.insertPoint(new Point(pStart.x+2,pStart.y-15),2);
			pointList.insertPoint(new Point(pEnd.x-2,pStart.y-15),3);
			pointList.insertPoint(new Point(pEnd.x,pStart.y-13),4);
		}
		else {
			//alte variante mit scharfen ecken
			pointList.insertPoint(new Point(pStart.x,pStart.y-15),1);
			pointList.insertPoint(new Point(pEnd.x,pStart.y-15),2);
			
			
		}
		//pointList.insertPoint(new Point(pEnd.x,pStart.y-15),3);
		
		
		
		/*pointList.insertPoint(new Point(pStart.x,pStart.y-15),1);
		pointList.insertPoint(new Point(pEnd.x-20,pStart.y-15),2);
		pointList.insertPoint(new Point(pEnd.x-20,pEnd.y),3);
		*/	
	}
	
	/**
	 * Diese Methode routet von einem ActivityEntity zu einem ProzessEntity
	 * 
	 *                          ----
	 *                             |
	 *                             |  
	 *  <---------------------------
	 * 
	 *                          ----
	 *                          |
	 *                          |  
	 *  <------------------------
	 * 
	 * @param pointList
	 */
	public void routeFromActivityToProcess(PointList pointList) {
		Point pStart=pointList.getPoint(0);
		Point pEnd=pointList.getPoint(1);

		if (bRoundedEdges) {
			// Variante mit geundeten Ecken
			pointList.insertPoint(new Point(pStart.x-18,pStart.y),1);
			pointList.insertPoint(new Point(pStart.x-20,pStart.y+2),2);
			
			pointList.insertPoint(new Point(pStart.x-20,pEnd.y-2),3);
			pointList.insertPoint(new Point(pStart.x-22,pEnd.y),4);
		} else  {
			// alte Variante mit scahrfen Ecken
			//pointList.insertPoint(new Point(pStart.x+5,pStart.y),1);
			//pointList.insertPoint(new Point(pStart.x+5,pEnd.y),2);
			pointList.insertPoint(new Point(pStart.x-20,pStart.y),1);
			pointList.insertPoint(new Point(pStart.x-20,pEnd.y),2);
			
		}
		
		
	}
	
	
	

	public Point computeStartPoint(IEntityFigure figure) {
		if (figure instanceof ActivityEntityFigure) {
			Rectangle r=figure.getBounds();
			Point p1=r.getBottomLeft();
			return new Point(p1.x,p1.y-5);
		} else {
			Point p1=((Figure)figure).getLocation();
			//XYLayout layout=(XYLayout)figure.getParent().getLayoutManager();
			//Rectangle ptest=(Rectangle)layout.getConstraint(figure);
			return new Point(p1.x+7,p1.y+2);
		}
		
	}
		
	public Point computeEndPoint(IEntityFigure figure) {
		if (figure instanceof ActivityEntityFigure) {
			Rectangle r=figure.getBounds();
			Point p1=r.getTopLeft();
			return new Point(p1.x+8,p1.y+2);
		} else {
			Rectangle r=figure.getBounds();
			Point p1=r.getBottomRight();
			return new Point(p1.x,p1.y-5);
		}
	}
		
	
	
	
	
}
