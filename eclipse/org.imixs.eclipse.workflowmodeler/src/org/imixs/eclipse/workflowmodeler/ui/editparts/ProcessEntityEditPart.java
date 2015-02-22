package org.imixs.eclipse.workflowmodeler.ui.editparts;

import java.beans.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.*;
import org.imixs.eclipse.workflowmodeler.styles.IEntityFigure;
import org.imixs.eclipse.workflowmodeler.commands.*;

/**
 * @author Ralph Soika
 */
public class ProcessEntityEditPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener, NodeEditPart, MouseListener,
		IProcessPropertySource {
	private ChopboxAnchor anchor;
	boolean expanded = true;
	public int LEFT_MARGIN = 20;
	public int TOP_MARGIN = 50;
	private IEntityFigure processFigure = null;
	// Position.
	private int x = 10, y = 10;

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

	protected IFigure createFigure() {

		processFigure = WorkflowmodelerPlugin.getPlugin().getStyle()
				.createProcessFigure();
		processFigure.addMouseListener(this);
		return processFigure;
	}

	protected void refreshVisuals() {
		processFigure.refreshVisuals(getProcessEntity());

		/*** X Y Position ausrechnen... **/
		// Position innerhalb der ProzessListe (Vector) ermitteln und anhand
		// dessen die Y-Koordinate errechnen
		List processEntities = getProcessEntity().getProcessTree()
				.getProcessEntities();

		int iCurrentPos = processEntities.indexOf(getProcessEntity());
		setY((iCurrentPos * 130) + TOP_MARGIN);
		setX(0 + LEFT_MARGIN);

		Dimension size = figure.getPreferredSize();
		// feste breite einstellen
		size.width = 150;

		// Dimension size=new Dimension(150,120);
		// Rectangle bounds=new Rectangle(new
		// org.eclipse.draw2d.geometry.Point(getProcessEntity().getX(),getProcessEntity().getY()),size);
		Rectangle bounds = new Rectangle(new org.eclipse.draw2d.geometry.Point(
				getX(), getY()), size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
				bounds);

	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			protected Command createDeleteCommand(
					final GroupRequest deleteRequest) {
				return new WorkflowEntityDeleteCommand(getProcessEntity());
			}
		});

		// �ber diese EditPolicy wird festgelegt, welche Arten von neune, Ein-
		// und Ausgehenden
		// Connections von dem EditPart unterst�tzt werden.
		// eine ProcessEntityEditPart erlaubt nur eingehende Connections von
		// Activities
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new GraphicalNodeEditPolicy() {

					// Eingehnde Connection unterst�tzen
					protected Command getConnectionCompleteCommand(
							CreateConnectionRequest request) {
						AssociationCreateCommand cmd = (AssociationCreateCommand) request
								.getStartCommand();
						cmd.setTarget((AbstractWorkflowEntity) getHost()
								.getModel());
						return cmd;
					}

					// nicht erlaubt
					protected Command getConnectionCreateCommand(
							CreateConnectionRequest request) {
						return null;
					}

					// nicht erlaubt
					protected Command getReconnectSourceCommand(
							ReconnectRequest request) {
						return null;
					}

					protected Command getReconnectTargetCommand(
							ReconnectRequest request) {
						return new AssociationReconnectCommand(
								(Association) request.getConnectionEditPart()
										.getModel(), null,
								(AbstractWorkflowEntity) getHost().getModel());
					}
				});
	}

	public void activate() {
		super.activate();
		getProcessEntity().addPropertyChangeListener(this);
	}

	public void deactivate() {
		getProcessEntity().removePropertyChangeListener(this);
		super.deactivate();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// kann ja gar keine Verbindungs�nderungen warnehmen (?)
		if ("associationsOutgoing".equals(evt.getPropertyName())) {

			refreshSourceConnections();
		} else if ("associationsIncoming".equals(evt.getPropertyName())) {
			refreshTargetConnections();
		} else {

			refreshVisuals();
		}
	}

	/*
	 * Helper method to avoid downcasts.
	 */
	public ProcessEntity getProcessEntity() {
		return (ProcessEntity) getModel();
	}

	protected List getModelSourceConnections() {
		return getProcessEntity().getAssociationOutgoing();
	}

	protected List getModelTargetConnections() {
		return getProcessEntity().getAssociationIncoming();
	}

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
	 * This method returns a list of the corresponding ActivityEntityParts. The
	 * method is used by the expand and collapse methods
	 * 
	 * @param editPart
	 * @return
	 */
	public List getActivityEntityEditParts() {
		Vector vectorActivityEditParts = new Vector();
		List list = this.getParent().getChildren();

		try {
			int iProcessID = this.getProcessEntity().getItemCollection()
					.getItemValueInteger("numProcessID");
			// check the processID of all child elements
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				Object object = iter.next();
				// select all activity parts....
				if ((object instanceof ActivityEntityEditPart)
						&& (iProcessID == ((ActivityEntityEditPart) object)
								.getActivityEntity().getItemCollection()
								.getItemValueInteger("numProcessID")))
					vectorActivityEditParts.add(object);
			}
		} catch (Exception e) {

		}

		return vectorActivityEditParts;
	}

	/**
	 * this methode hides all associated activities
	 */
	public void collapse() {
		ActivityEntityEditPart activityEntityEditPart;
		Iterator iter = this.getActivityEntityEditParts().iterator();
		while (iter.hasNext()) {
			activityEntityEditPart = (ActivityEntityEditPart) iter.next();
			activityEntityEditPart.setVisible(false);
		}
		expanded = false;
		refresh();
	}

	/**
	 * this method shows all associated activities and first collapse currently
	 * opend activities of other processEntityEditParts
	 */
	public void expand() {
		// first collapse all!;
		ModelTreeEditPart modelTreeEditPart = (ModelTreeEditPart) this
				.getParent();
		modelTreeEditPart.collapseAllProcessEntities();

		ActivityEntityEditPart activityEntityEditPart;
		Iterator iter = this.getActivityEntityEditParts().iterator();
		while (iter.hasNext()) {
			activityEntityEditPart = (ActivityEntityEditPart) iter.next();
			activityEntityEditPart.setVisible(true);
			// activityEntityEditPart.refreshVisuals();
		}
		expanded = true;
		refresh();
	}

	/**
	 * Gibt an ob das ProcessEntity expandiert oder collapsed ist
	 * 
	 * @return
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/*
	 * public void addExpandListener(ExpandListener o) {
	 * expandListenders.add(o); } public void
	 * removeExpandListener(ExpandListener o) { expandListenders.remove(o); }
	 */

	/**
	 * Mouse Events
	 */
	public void mouseDoubleClicked(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
		if (this.isExpanded())
			this.collapse();
		else
			this.expand();
		refreshVisuals();

	}

	public void mouseReleased(MouseEvent me) {
	}

}
