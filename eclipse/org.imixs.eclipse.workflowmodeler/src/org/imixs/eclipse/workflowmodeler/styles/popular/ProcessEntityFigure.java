package org.imixs.eclipse.workflowmodeler.styles.popular;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.AbstractWorkflowEntity;
import org.imixs.eclipse.workflowmodeler.styles.IEntityFigure;

/**
 * ProcessEntity Figure 
 * 
 * @author Ralph Soika
 */
public class ProcessEntityFigure extends Figure implements IEntityFigure {

	Label labelName;
	Label attributeID,attributeDescription;
	private static Image imageName= WorkflowmodelerPlugin.getPlugin().getIcon("styles/popular/process.gif").createImage();
	private static Image imageField= WorkflowmodelerPlugin.getPlugin().getIcon("styles/popular/field.gif").createImage();
	private static Image imageID= WorkflowmodelerPlugin.getPlugin().getIcon("styles/popular/id.gif").createImage();

	private CompartmentFigure attributeFigure = new CompartmentFigure();

	private CompartmentFigure imageFigure = new CompartmentFigure();

	
	PopularStyle popularStyle;
	
	
	public ProcessEntityFigure(PopularStyle style) {
		popularStyle=style;
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));

		/*** Title ***/
		Font nameFont = new Font(null, "Arial", 10, SWT.BOLD);
		labelName = new Label("Process", imageName);
		labelName.setFont(nameFont);
		labelName.setText("");
		labelName.setLabelAlignment(PositionConstants.LEFT);

		
		/*** Atributes ***/
		attributeID = new Label("",imageID);
		attributeDescription = new Label("");
		attributeDescription.setLabelAlignment(PositionConstants.LEFT);
		GridLayout descrLayout = new GridLayout();
		
		
	//	attributeDescription.setSize(150, 180);
		attributeFigure.add(attributeID);
		attributeFigure.add(attributeDescription);
		// onyl spacing ;-)
	//	attributeFigure.add(new Label(""));
		
		add(labelName);
		add(attributeFigure);
		add(imageFigure);
		
		// set colors
		attributeFigure.setBackgroundColor(popularStyle.getColor(PopularStyle.COLOR_PROCESS_BODY));
		attributeFigure.setOpaque(true);
		
		//attributeFigure
		
		labelName.setBackgroundColor(popularStyle.getColor(PopularStyle.COLOR_PROCESS_TITLE));
		labelName.setOpaque(true);
	}

	
	
	public void refreshVisuals(AbstractWorkflowEntity workflowEntity){
		labelName.setText(""+workflowEntity.getItemCollection().getItemValueString("txtName"));
		
		String sID=workflowEntity.getItemCollection().getItemValueInteger("numProcessID") + "";
		attributeID.setText("ID: " + sID);
		
		// Description text can not be displayed - is to long :-/
		//String sDescription=workflowEntity.getItemCollection().getItemValueString("rtfdescription") ;
		//attributeDescription.setText("\n \n "+sDescription);

		//String sEditor=workflowEntity.getItemCollection().getItemValueString("txtEditorID") ;
		//attributeDescription.setText("\n \n "+sEditor);
		
		// no text information can be displayed here! it is to less space !!
		attributeDescription.setText("\n \n ");
	}
	

}















