
package org.imixs.eclipse.workflowmodeler.styles.popular;

import org.eclipse.swt.graphics.Color;
/**
 * Default community Style
 * 
 * @author Ralph Soika

 */
public class CoffeeStyle extends PopularStyle {
		
	static Color c_ab=new Color(null, 222,211,193);
	static Color c_at=new Color(null, 156,146,130);
	

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
