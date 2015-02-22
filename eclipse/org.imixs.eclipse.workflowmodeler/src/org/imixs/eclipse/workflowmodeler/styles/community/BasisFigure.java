
package org.imixs.eclipse.workflowmodeler.styles.community;

import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.draw2d.*;

/**
 * Diese Klasse stellt das graphische Grundger�st f�r die beiden WorkflowEntities 
 * ProcessEntity und ActivityEntity dar.
 * Die Klasse stellt einen wie�en Rahmen mit einem Icon in der linken oberen Ecke dar.
 * Darunter befindet sich ein Container, welcher einen Tiel sowie eine Attributeliste besitzt.
 * Die AttrubuteFigure kann beliebige Attribute darstellen. 
 * Bei MouseOver Events reagiert der Container farblich .
 * 
 * Insgesamt kennt die Komponente drei Farbzust�nde
 *   - normal (weis / transparent)
 *   - aktiv
 *   - selektiert
 * 
 * 
 *  |-----------------------------|imageFigure
 *  |  #####                      |
 *  | #####                       |
 *  |---------------------------- |contentFigure
 *  | Title                       |
 *  |                             |
 *  | Atribures                   |
 *  |                             |
 *  |-----------------------------|
 * 
 * @author Ralph Soika
 */
public abstract class BasisFigure extends Figure implements MouseMotionListener {
	
	final boolean bRounded=true;
	
	// Blau     : 153,153,255
	// hellblau : 229,229,255
	// Gelb     : 255,255,60
	// hellgelb : 255,255,149
	
	
	public Color colorActiv = new Color(null, 153,153,255);
	public Color colorSelect = new Color(null, 153,153,255);
	public Label title;
	public Label attribute1,attribute2;
	
	// eingebettete Figure Objekte
	Figure imageFigure;
	Figure contentFigure;
	private Image imageIcon; // Icon in der linken oberen Ecke



	
	public BasisFigure() {
		imageIcon= WorkflowmodelerPlugin.getPlugin().getIcon("process.gif").createImage();
	
		
		/*** Mainlayout setzen ***/
		ToolbarLayout mainLayout = new ToolbarLayout();
		mainLayout.setSpacing(8);
		setLayoutManager(mainLayout);

	
		
		/*** create Image Figure ***/ 
		imageFigure = new Figure();
		ToolbarLayout ilay=new ToolbarLayout();
		ilay.setVertical(false);
		imageFigure.setLayoutManager(ilay);
		// ad default image
		imageFigure.add(new ImageFigure(imageIcon));
	
		
		
		/*** create Content Figure ***/
		if (bRounded) {
			contentFigure = new RoundedRectangle();
			((RoundedRectangle)contentFigure).setCornerDimensions(new org.eclipse.draw2d.geometry.Dimension(10,10));
			((RoundedRectangle)contentFigure).setLineWidth(1);
			//((RoundedRectangle)contentFigure).setLineStyle(Graphics.LINE_DOT);
		}
		else
			contentFigure = new Figure();
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		layout.setStretchMinorAxis(false);
		layout.setSpacing(1);
		contentFigure.setLayoutManager(layout);
		contentFigure.setBackgroundColor(colorActiv);
		contentFigure.setOpaque(true);
		contentFigure.setForegroundColor(ColorConstants.darkGray);
	
		Font classFont = new Font(null, "Arial", 10, SWT.BOLD);
		title = new Label("");
		title.setFont(classFont);
		contentFigure.add(title);

		
		attribute1 = new Label("");
		attribute2 = new Label("");
		contentFigure.add(attribute1);
		contentFigure.add(attribute2);
		
		
		
		/*** Container zusammensetzen ***/
		this.add(imageFigure);
		this.add(contentFigure);
		
		
		/*** register Motion Listener ***/
		this.addMouseMotionListener(this );
		
	}
	
	public Figure getImageFigure() {
		return imageFigure;
	}
	public Figure getAttributeFigure() {
		return contentFigure;
	}
		
	
	
	
	
	public void setColorActiv(Color acolor) {
		colorActiv=acolor;
		contentFigure.setBackgroundColor(colorActiv);
		setBackgroundColor(colorActiv);
	}
	public void setColorSelect(Color acolor) {
		colorSelect=acolor;
		contentFigure.setBackgroundColor(colorSelect);
		setBackgroundColor(colorSelect);
	}

	

   
	
    /**
     * Mouse Motion Events
     */
	public void mouseDragged(MouseEvent me) {
	}
	public void mouseEntered(MouseEvent me) {
		// Farbe �ndern...
		contentFigure.setBackgroundColor(colorSelect);
	}
	public void mouseExited(MouseEvent me) {
		// Farbe �ndern...
		contentFigure.setBackgroundColor(colorActiv);
	}
	public void mouseHover(MouseEvent me) {
	}
	public void mouseMoved(MouseEvent me) {
	}	   


}
