package org.imixs.eclipse.workflowmodeler.styles.popular;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import org.imixs.eclipse.workflowmodeler.styles.*;

/**
 * Diese Klasse stellt einen eigenen ConnectionRouter bereit, der die Verbindung
 * zwischen ProcessEntities und ActivityEntities steuert. Dabei wird ein
 * individuelles Routing angewendet, welches feste Routen berechnet. Die Klasse
 * unterscheidet zwei Routen 1) von ProcessEntity -> ActivityEntity 2) von
 * ActivityEntity -> ProcessEntity
 * 
 * @author Ralph Soika
 * 
 */
public class EntityConnectionRouter extends AbstractRouter {
	/**
	 * Die route Methode berechnet die Route zwischen zwei Objekten, in dem eine
	 * Liste von Points (PointList) errechnet wird und an das connection Objekt
	 * �bergeben wird. Der Parameter connection ist also ein valueObjekt,
	 * welches durch die Methode manipuliert wird.
	 */
	public void route(Connection connection) {
		IEntityFigure fromFigure = null, toFigure = null;
		PointList points = connection.getPoints();
		points.removeAllPoints();
		Point pStart, pEnd;

		/** Startpunkt berechnen * */
		try {
			fromFigure = (IEntityFigure) connection.getSourceAnchor()
					.getOwner();
			pStart = computeStartPoint(fromFigure);
		} catch (Exception expStart) {
			// das Ziel mu� kein Entty sein, wen die Maus �ber einen freien
			// Bereich bewegt wird!
			pStart = getStartPoint(connection); // Default!
		}
		// connection.translateToRelative(pStart );
		points.addPoint(pStart);

		/***********************************************************************
		 * compute Target Point Target will point to next ProcessEntity. If the
		 * next ProcessEntity is not in the same processtree the target points
		 * to itself
		 * 
		 * @see org.imixs.eclipse.workflowmodeler.model.ProcessEntity - method
		 *      createConnections In this case the routing will be onyl a stumb
		 */
		
		try {
			toFigure = (IEntityFigure) connection.getTargetAnchor().getOwner();

			// Dummy connection?
			if (fromFigure == toFigure) {
				points.addPoint(new Point(pStart.x-20,pStart.y));
				pEnd=new Point(pStart.x-20,pStart.y+40);
			} else {
				pEnd = computeEndPoint(toFigure);
			}
		} catch (Exception extEnd) {
			pEnd = getEndPoint(connection);
		
		}
		// connection.translateToRelative(pEnd);

		points.addPoint(pEnd);
		// compute route if no dummy connection to itself
		if (fromFigure != toFigure) {
			if (fromFigure instanceof ProcessEntityFigure)
				routeFromProzessToActivity(points);
			else if (fromFigure instanceof ActivityEntityFigure)
				routeFromActivityToProcess(points);
			else {
				Point p = new Point(pStart.x, pEnd.y);
				points.insertPoint(p, 1);
			}
		}

		/** R�ckgabe der Route * */
		connection.setPoints(points);

	}

	/**
	 * Diese Methode routet von einem ProzessEntity zu einem ActivityEntity
	 * 
	 * --------------------------- | | | V
	 * 
	 * ------------------------ | | | - ->
	 * 
	 * @param pointList
	 */
	public void routeFromProzessToActivity(PointList pointList) {
		Point pStart = pointList.getPoint(0);
		Point pEnd = pointList.getPoint(1);

		pointList.insertPoint(new Point(pStart.x, pStart.y - 20), 1);
		pointList.insertPoint(new Point(pEnd.x, pStart.y - 20), 2);

	}

	/**
	 * Diese Methode routet von einem ActivityEntity zu einem ProzessEntity
	 * 
	 * ---- | | <---------------------------
	 * 
	 * ---- | | <------------------------
	 * 
	 * @param pointList
	 */
	public void routeFromActivityToProcess(PointList pointList) {
		Point pStart = pointList.getPoint(0);
		Point pEnd = pointList.getPoint(1);
		pointList.insertPoint(new Point(pStart.x - 20, pStart.y), 1);
		pointList.insertPoint(new Point(pStart.x - 20, pEnd.y), 2);

	}

	public Point computeStartPoint(IEntityFigure figure) {
		if (figure instanceof ActivityEntityFigure) {
			Rectangle r = figure.getBounds();
			Point p1 = r.getBottomLeft();
			return new Point(p1.x, p1.y - 5);
		} else {
			Point p1 = ((Figure) figure).getLocation();
			// XYLayout layout=(XYLayout)figure.getParent().getLayoutManager();
			// Rectangle ptest=(Rectangle)layout.getConstraint(figure);
			return new Point(p1.x + 7, p1.y);
		}

	}

	public Point computeEndPoint(IEntityFigure figure) {

		if (figure instanceof ActivityEntityFigure) {
			Rectangle r = figure.getBounds();
			Point p1 = r.getTopLeft();
			return new Point(p1.x + 8, p1.y - 1);
		} else {
			Rectangle r = figure.getBounds();
			Point p1 = r.getBottomRight();
			return new Point(p1.x, p1.y - 5);
		}
	}
}
