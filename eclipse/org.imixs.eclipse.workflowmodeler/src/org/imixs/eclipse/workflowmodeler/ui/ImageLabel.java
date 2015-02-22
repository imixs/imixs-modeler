package org.imixs.eclipse.workflowmodeler.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;

public class ImageLabel extends Composite {
	public ImageLabel (Composite parent, int style) {
		super (parent ,style);
		
	}
	
	public ImageLabel (Composite parent, int style, Image aImage, String aLabel) {
		super (parent ,style);
		
		RowLayout rowLayout=new RowLayout(SWT.HORIZONTAL);
		rowLayout.marginLeft=0;
		rowLayout.marginHeight=0;
		rowLayout.marginBottom=0;
		rowLayout.marginTop=0;
		rowLayout.marginWidth=0;
		rowLayout.spacing=5;
		this.setLayout(rowLayout);
		
		if (aImage!=null) {
			Label labelImage=new Label(this,  SWT.ICON );
			labelImage.setImage(aImage);	
		}
		Label labelText=new Label( this,  SWT.NONE  );
		labelText.setText(aLabel);
	
		this.layout();
	}

	
	
}
