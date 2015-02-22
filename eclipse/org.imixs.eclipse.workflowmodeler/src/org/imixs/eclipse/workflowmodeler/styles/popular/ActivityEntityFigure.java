package org.imixs.eclipse.workflowmodeler.styles.popular;

import java.util.Vector;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.AbstractWorkflowEntity;
import org.imixs.eclipse.workflowmodeler.styles.IEntityFigure;

/**
 * Activity Figure
 * 
 * @author Ralph Soika
 */
public class ActivityEntityFigure extends Figure implements IEntityFigure {

	Label labelName;
	Label labelActivityID, labelNextID;
	Label labelMail, labelSchedule;
	Label labelRead, labelWrite,labelRestriction;
	Label labelPublic, labelResult;
	private static Image imageName = WorkflowmodelerPlugin.getPlugin().getIcon(
			"styles/popular/activity.gif").createImage();
	private static Image imageField = WorkflowmodelerPlugin.getPlugin()
			.getIcon("styles/popular/field.gif").createImage();

	private static Image imageID = WorkflowmodelerPlugin.getPlugin().getIcon(
			"styles/popular/id.gif").createImage();
	private static Image imageNextID = WorkflowmodelerPlugin.getPlugin()
			.getIcon("styles/popular/next_id.gif").createImage();
	private static Image imageNextTree = WorkflowmodelerPlugin.getPlugin()
			.getIcon("styles/popular/next_tree.gif").createImage();

	private static Image imageMail = WorkflowmodelerPlugin.getPlugin().getIcon(
			"styles/popular/mail.gif").createImage();
	private static Image imageSchedule = WorkflowmodelerPlugin.getPlugin()
			.getIcon("styles/popular/schedule.gif").createImage();

	private static Image imageRestrction = WorkflowmodelerPlugin.getPlugin()
	.getIcon("styles/popular/restricted.gif").createImage();

	
	private static Image imageRead = WorkflowmodelerPlugin.getPlugin()
	.getIcon("styles/popular/read.gif").createImage();
	
	private static Image imageWrite = WorkflowmodelerPlugin.getPlugin()
	.getIcon("styles/popular/acl.gif").createImage();
	
	private static Image imagePublic = WorkflowmodelerPlugin.getPlugin()
	.getIcon("styles/popular/public.gif").createImage();
	
	private static Image imageResult = WorkflowmodelerPlugin.getPlugin()
	.getIcon("styles/popular/result.gif").createImage();
	
	
	
	
	
	private CompartmentFigure idFigure = new CompartmentFigure();

	private CompartmentFigure imageFigure = new CompartmentFigure();

	PopularStyle popularStyle;

	public ActivityEntityFigure(PopularStyle style) {
		popularStyle = style;
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(true);
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		// setBackgroundColor(classColor);
		// setOpaque(true);

		/*** Title ***/
		Font nameFont = new Font(null, "Arial", 10, SWT.BOLD);
		labelName = new Label("Activity", imageName);
		labelName.setFont(nameFont);
		labelName.setText("");
		labelName.setForegroundColor(new Color(null, 255, 255, 255));
		labelName.setLabelAlignment(PositionConstants.LEFT);
		/*** Atributes ***/

		labelActivityID = new Label("", imageID);
		labelNextID = new Label("", imageNextID);

		// attributeFigure.get
		idFigure.add(labelActivityID);

		labelMail = new Label("", imageMail);
		labelSchedule = new Label("", imageSchedule);

		labelRead = new Label("", null);
		labelWrite = new Label("", null);
		labelPublic = new Label("", null);
		labelResult = new Label("", null);
		labelRestriction=new Label("",null);
		
		
		imageFigure.add(labelNextID);
		imageFigure.add(labelPublic);
		imageFigure.add(labelRestriction);
		imageFigure.add(labelResult);
		imageFigure.add(labelRead);
		imageFigure.add(labelWrite);
		imageFigure.add(labelMail);
		imageFigure.add(labelSchedule);

		add(labelName);
		add(idFigure);
		add(imageFigure);

		// set colors
		idFigure.setBackgroundColor(popularStyle
				.getColor(PopularStyle.COLOR_ACTIVITY_BODY));
		idFigure.setOpaque(true);

		idFigure.setBorder(null);

		imageFigure.setBackgroundColor(popularStyle
				.getColor(PopularStyle.COLOR_ACTIVITY_BODY));
		imageFigure.setOpaque(true);
		imageFigure.setBorder(null);

		labelName.setBackgroundColor(popularStyle
				.getColor(PopularStyle.COLOR_ACTIVITY_TITLE));
		labelName.setOpaque(true);

	}

	public void refreshVisuals(AbstractWorkflowEntity workflowEntity) {
		labelName.setText(workflowEntity.getItemCollection()
				.getItemValueString("txtName"));

		String sID = ""
				+ workflowEntity.getItemCollection().getItemValueInteger(
						"numActivityID");
		labelActivityID.setText("ID: " + sID);

		String sNextTree = workflowEntity.getItemCollection()
				.getItemValueString("txtNextProcessTree");
		String sNextID = workflowEntity.getItemCollection()
				.getItemValueInteger("numNextProcessID")
				+ "";
		labelNextID.setText("");
		// point to other process tree?
		if (!"".equals(sNextTree))
			labelNextID.setIcon(imageNextTree);
		else
			labelNextID.setIcon(imageNextID);

		// display Mail icon ?
		if (hasMail(workflowEntity))
			labelMail.setIcon(imageMail);
		else
			labelMail.setIcon(null);
		
		// display Scheudled icon ?
		if (isScheduled(workflowEntity))
			labelSchedule.setIcon(imageSchedule);
		else
			labelSchedule.setIcon(null);
		
		
		/*
		// display ReadAccess icon ?
		if (hasReadAccess(workflowEntity))
			labelRead.setIcon(imageRead);
		else
			labelRead.setIcon(null);
		
		// display WriteAccess icon ?
		if (hasWriteAccess(workflowEntity))
			labelWrite.setIcon(imageWrite);
		else
			labelWrite.setIcon(null);
		*/
		
		if (hasWriteAccess(workflowEntity) || hasReadAccess(workflowEntity))
			labelRead.setIcon(imageWrite);
		else
			labelRead.setIcon(null);
		
		
		// display Public icon ?
		if (isPublic(workflowEntity))
			labelPublic.setIcon(imagePublic);
		else
			labelPublic.setIcon(null);
		
		// display Result icon ?
		if (hasResult(workflowEntity))
			labelResult.setIcon(imageResult);
		else
			labelResult.setIcon(null);

		
		
		// display Restricted icon ?
		if (hasRestriction(workflowEntity))
			labelRestriction.setIcon(imageRestrction);
		else
			labelRestriction.setIcon(null);

	}

	
	
	
	
	/**
	 * check if mail receifers are defined ?
	 * @param workflowEntity
	 * @return
	 */
	private boolean hasMail(AbstractWorkflowEntity workflowEntity) {
		if (workflowEntity.getItemCollection().getItemValue("namMailReceiver")
				.size() > 0)
			return true;
		if (workflowEntity.getItemCollection().getItemValue(
				"keyMailReceiverFields").size() > 0)
			return true;
		if (workflowEntity.getItemCollection()
				.getItemValue("namMailReceiverCC").size() > 0)
			return true;
		if (workflowEntity.getItemCollection().getItemValue(
				"keyMailReceiverFieldsCC").size() > 0)
			return true;

		return false;

	}
	
	/**
	 * check if scheduled activity ?
	 * @param workflowEntity
	 * @return
	 */
	private boolean isScheduled(AbstractWorkflowEntity workflowEntity) {
	
		return ("1".equals(workflowEntity.getItemCollection().getItemValueString("keyScheduledActivity")));
	}

	
	
	
	private boolean hasReadAccess(AbstractWorkflowEntity workflowEntity) {
		if (workflowEntity.getItemCollection().getItemValue("namAddReadAccess")
				.size() > 0)
			return true;
		if (workflowEntity.getItemCollection().getItemValue(
				"keyAddReadFields").size() > 0)
			return true;
	

		return false;

	}

	

	
	private boolean hasWriteAccess(AbstractWorkflowEntity workflowEntity) {
		if (workflowEntity.getItemCollection().getItemValue("namAddWriteAccess")
				.size() > 0)
			return true;
		if (workflowEntity.getItemCollection().getItemValue(
				"keyAddWriteFields").size() > 0)
			return true;
	

		return false;

	}
	
	
	
	
	
	private boolean isPublic(AbstractWorkflowEntity workflowEntity) {
		
		return ("1".equals(workflowEntity.getItemCollection().getItemValueString("keyPublicResult")));
	}
	
	
	

	private boolean hasResult(AbstractWorkflowEntity workflowEntity) {
		
		Vector v=workflowEntity.getItemCollection().getItemValue("txtActivityResult");
		
		if (v.size() > 0 && !"".equals(v.elementAt(0)))
			return true;
	

		return false;

	}
	
	


	private boolean hasRestriction(AbstractWorkflowEntity workflowEntity) {
		
		
	Vector v=workflowEntity.getItemCollection().getItemValue("$ReadAccess");
		
		if (v.size() > 0 && !"".equals(v.elementAt(0)))
			return true;
	

		return false;

		
	
	}
	
	
	
	
	
	
	
	
}
