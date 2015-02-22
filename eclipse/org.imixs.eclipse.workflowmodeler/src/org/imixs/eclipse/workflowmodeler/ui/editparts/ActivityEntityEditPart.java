package org.imixs.eclipse.workflowmodeler.ui.editparts;

import java.beans.*;
import java.util.*;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.gef.*;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.*;
import org.imixs.eclipse.workflowmodeler.commands.*;
import org.imixs.eclipse.workflowmodeler.styles.*;

/**
 * Diese Klasse ist die graphische Representation eines ActivityEntitys
 * 
 * @author Ralph Soika
 */
public class ActivityEntityEditPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener, NodeEditPart,IActivityPropertySource {
	private ChopboxAnchor anchor;
	private boolean visible = true; // gibt an ob das Activity angezeigt wird
									// (aufgeklapptes ProcessEntity)
	public int LEFT_MARGIN = 20;
	public int TOP_MARGIN = 50;
	private int x = 10, y = 10;
	private IEntityFigure activityFigure = null;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int newX) {
		this.x = newX;
	}

	public void setY(int newY) {
		this.y = newY;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean v) {
		this.visible = v;
		refreshVisuals();
	}

	protected IFigure createFigure() {
		activityFigure = WorkflowmodelerPlugin.getPlugin().getStyle()
				.createActivityFigure();
		return activityFigure;
	}

	protected void refreshVisuals() {

		activityFigure.refreshVisuals(getActivityEntity());

		/*** compute X Y Position... **/
		// Position innerhalb der ProzessListe (Vector) ermitteln und anhand
		// dessen die Y-Koordinate errechnen
		List activityEntities = getActivityEntity().getProcessEntity()
				.getActivityEntities();

		int iCurrentPos = activityEntities.indexOf(getActivityEntity());
		setX(((iCurrentPos + 1) * 200) + LEFT_MARGIN);
		// Y-Koordinate anhand des zugeh�rigen ProcessEntities ermiteln!
		setY(getProcessEntityEditPart().getY());

		IFigure f = this.getFigure();
		Dimension size = f.getPreferredSize();
		// feste breite einstellen
		size.width = 150;
		// Rectangle bounds=new Rectangle(new
		// org.eclipse.draw2d.geometry.Point(getActivityEntity().getX(),getActivityEntity().getY()),size);
		Rectangle bounds = new Rectangle(new org.eclipse.draw2d.geometry.Point(
				getX(), getY()), size);
		// label.setBounds(bounds);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
				bounds);

		// visibility depends on the visiblity of my modelObejct
		this.getFigure().setVisible(this.getVisible());
		showConnections(this.getVisible());

		// Sort ActivityEntityEditParts!
		// The new order will be visible after a new expand.
		// Currently no visual effect because this will confuse the user
		Collections.sort(activityEntities, new Comparator() {
			public int compare(Object o1, Object o2) {
				ActivityEntity a1 = (ActivityEntity) o1;
				ActivityEntity a2 = (ActivityEntity) o2;

				// Ids ermitteln
				Integer id1 = new Integer(a1.getItemCollection()
						.getItemValueInteger("numActivityID"));
				Integer id2 = new Integer(a2.getItemCollection()
						.getItemValueInteger("numActivityID"));

				return (id1.compareTo(id2)); // c1.startTime.compareTo(c2.startTime);
			}
		});

	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			protected Command createDeleteCommand(
					final GroupRequest deleteRequest) {
				return new WorkflowEntityDeleteCommand(getActivityEntity());
			}
		});

		// �ber diese EditPolicy wird festgelegt, welche Arten von neune, Ein-
		// und Ausgehenden
		// Connections von dem EditPart unterst�tzt werden.
		// eine ProcessEntityEditPart erlaubt nur die erzeugung von ausgehdnen
		// Connections
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new GraphicalNodeEditPolicy() {
					// nicht unterst�tzt
					protected Command getConnectionCompleteCommand(
							CreateConnectionRequest request) {
						return null;
					}

					// Hier wird festgelegt, das das ActivityEntityEditPart
					// ausgehende Connections
					// selbst inizieren kann
					protected Command getConnectionCreateCommand(
							CreateConnectionRequest request) {
						request.setSourceEditPart(getHost());
						AssociationCreateCommand cmd = new AssociationCreateCommand(
								((AbstractWorkflowEntity) getHost().getModel()),
								null);
						request.setStartCommand(cmd);
						return cmd;
					}

					// nicht unterst�tzt
					protected Command getReconnectSourceCommand(
							ReconnectRequest request) {
						return null;
					}

					// nicht unterst�tzt
					protected Command getReconnectTargetCommand(
							ReconnectRequest request) {
						return null;
					}
				});
	}

	public void activate() {
		super.activate();
		getActivityEntity().addPropertyChangeListener(this);
	}

	public void deactivate() {
		getActivityEntity().removePropertyChangeListener(this);
		super.deactivate();
	}

	/**
	 * Diese Methode reagiert auf PropertyChange Events um abh�ngig vom Event
	 * Typ (bzw. PropertyName) bestimmte Aktionen f�r das EditPart auszul�sen
	 * Hier werden u.a. ein- und ausgehende Verbindungslinen versteckt/angezeigt
	 * falls das Proerpty "visible" ge�ndert wurde
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("associationsOutgoing".equals(evt.getPropertyName())) {
			refreshSourceConnections();
		} else if ("associationsIncoming".equals(evt.getPropertyName())) {
			refreshTargetConnections();
		} else {
			refreshVisuals();
		}
	}

	/**
	 * Diese Methode dient dazu die ein und ausgehenden Verbindungen anzuzeigen,
	 * bzw. zu versteicken Die Methode wird durch propertyChange 'visible'
	 * aufugerufen So kann ein ProcessEntity seine activitys volls�tndig
	 * (inclusive Verbindungslienen) unsichtbar machen
	 * 
	 * @param bVisible
	 */
	private void showConnections(boolean bVisible) {
		int i;
		ConnectionEditPart editPart;

		// Eingehende Verbindungen anzeigen/verstecken
		List editParts = getSourceConnections();
		for (i = 0; i < editParts.size(); i++) {
			editPart = (ConnectionEditPart) editParts.get(i);
			editPart.getFigure().setVisible(bVisible);
		}

		// Ausgehende Verbindungen anzeigen/verstecken
		editParts = this.getTargetConnections();
		for (i = 0; i < editParts.size(); i++) {
			editPart = (ConnectionEditPart) editParts.get(i);

			// ((AssociationEditPart)editPart).hammer();
			editPart.getFigure().setVisible(bVisible);
		}

	}

	/*
	 * Helper method to avoid downcasts.
	 */
	public ActivityEntity getActivityEntity() {
		return (ActivityEntity) getModel();
	}

	protected List getModelSourceConnections() {
		return getActivityEntity().getAssociationOutgoing();
	}

	protected List getModelTargetConnections() {
		return getActivityEntity().getAssociationIncoming();
	}

	/*** Connections ****/
	private ConnectionAnchor getAnchor() {
		if (anchor == null) {
			anchor = new ChopboxAnchor(getFigure());
		}
		return anchor;
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return getAnchor();
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return getAnchor();
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getAnchor();
	}

	/**
	 * Diese Methode liefert das ProcessEntityEditPart zu einem bestimmten
	 * ActivityEntityEditPart zur�ck. Die Methode kann dazu verwendet werden um
	 * die relative Position eines ActivityEditParts zu seinem ProcessEditPart
	 * zu errechnen
	 * 
	 * @param editPart
	 * @return
	 */
	private ProcessEntityEditPart getProcessEntityEditPart() {
		List list = this.getParent().getChildren();
		try {
			int iProcessID = this.getActivityEntity().getItemCollection()
					.getItemValueInteger("numProcessID");
			// Jetzt alle Children pr�fen, und den passenden ProcessEntity
			// finden
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				Object object = iter.next();
				// jetzt die Activities mit der selben nummer �bergeben
				if ((object instanceof ProcessEntityEditPart)
						&& (iProcessID == ((ProcessEntityEditPart) object)
								.getProcessEntity().getItemCollection()
								.getItemValueInteger("numProcessID")))
					return (ProcessEntityEditPart) object;
			}
		} catch (Exception e) {

		}
		return null;
	}

}
