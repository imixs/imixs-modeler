
package org.imixs.eclipse.workflowmodeler.styles.popular;

import org.eclipse.swt.graphics.Color;
/**
 * Default community Style
 * 
 * @author Ralph Soika

 */
public class TechnicalStyle extends PopularStyle {
	
	static Color c_ab=new Color(null, 233,233,233);
	static Color c_at=new Color(null, 170,170,170);
	

	public Color getColor(int type) {
		switch (type) {
			case COLOR_PROCESS_BODY: return c_pb;
			case COLOR_PROCESS_TITLE: return c_pt;
		
			case COLOR_ACTIVITY_BODY: return c_ab;
			case COLOR_ACTIVITY_TITLE: return c_at;

			default: return c_pb;
		}
		
		
	}
	
}
