
package org.imixs.eclipse.workflowmodeler.styles.community;

import org.imixs.eclipse.workflowmodeler.model.AbstractWorkflowEntity;
import org.imixs.eclipse.workflowmodeler.styles.*;
import org.eclipse.swt.graphics.Color;

/**
 * Activity Figure 
 * 
 * @author Ralph Soika
 */
public class ActivityEntityFigure extends BasisFigure implements IEntityFigure  {
	

	public ActivityEntityFigure() {
		super();
	//	imageIcon= WorkflowmodelerPlugin.getPlugin().getIcon("activity.gif").createImage();
		setColorSelect(new Color(null,153,153,255));
		setColorActiv(new Color(null,229,229,255));
	}

	
	public void refreshVisuals(AbstractWorkflowEntity workflowEntity){
		setColorSelect(new Color(null,153,153,255));
		setColorActiv(new Color(null,229,229,255));
		
		title.setText(""+workflowEntity.getItemCollection().getItemValueString("txtName"));
		
		String sID=workflowEntity.getItemCollection().getItemValueInteger("numProcessID") + "."+workflowEntity.getItemCollection().getItemValueInteger("numActivityID");
		attribute1.setText("ID: " + sID);
			
		
		String sNextID=workflowEntity.getItemCollection().getItemValueInteger("numNextProcessID") + "";
		attribute2.setText("Next ID: " + sNextID);
	}
	
	
	
	
	
	
	

	
	
	
}
