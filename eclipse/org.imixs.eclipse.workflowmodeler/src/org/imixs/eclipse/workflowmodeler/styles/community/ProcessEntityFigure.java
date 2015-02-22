package org.imixs.eclipse.workflowmodeler.styles.community;

import org.eclipse.swt.graphics.Image;
import org.eclipse.draw2d.*;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.AbstractWorkflowEntity;
import org.imixs.eclipse.workflowmodeler.styles.IEntityFigure;
import org.eclipse.swt.graphics.Color;

/**
 * Diese Klasse
 * 
 * @author Ralph Soika
 */
public class ProcessEntityFigure extends BasisFigure implements IEntityFigure {

	private static Image imagePlus = WorkflowmodelerPlugin.getPlugin().getIcon(
			"checkin_action.gif").createImage();
	private static Image imageMinus = WorkflowmodelerPlugin.getPlugin()
			.getIcon("checkout_action.gif").createImage();

	private ImageFigure imageFigurePlus;

	public ProcessEntityFigure() {

		super();

		// nur als Abstandhalter ;-)
		contentFigure.add(new Label(""));
		contentFigure.add(new Label(""));

		// imageIcon=
		// WorkflowmodelerPlugin.getPlugin().getIcon("process.gif").createImage();

		imageFigurePlus = new ImageFigure(imageMinus);
		getImageFigure().add(imageFigurePlus);

		// this.addMouseListener(this);

		// Farben setzen
		setColorSelect(new Color(null, 255, 255, 60));
		setColorActiv(new Color(null, 255, 255, 149));
	}

	public void refreshVisuals(AbstractWorkflowEntity workflowEntity) {
		// Farben setzen
		setColorSelect(new Color(null, 255, 255, 60));
		setColorActiv(new Color(null, 255, 255, 149));
		title.setText("Process: "
				+ workflowEntity.getItemCollection().getItemValueString(
						"txtName"));

		String sID = workflowEntity.getItemCollection().getItemValueInteger(
				"numProcessID")
				+ "";
		attribute1.setText("ID: " + sID);

		attribute2.setText("Public: true");

	}

	// Diese Methode zeigt abh�ngig von expand das plus- oder minuszeichen an
	public void showExpandStatus(boolean expand) {
		if (expand)
			imageFigurePlus.setImage(imageMinus);
		else
			imageFigurePlus.setImage(imagePlus);
	}

}
