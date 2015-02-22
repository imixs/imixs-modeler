
package org.imixs.eclipse.workflowmodeler.ui.editparts;

/**
 * A listener interface for receiving Collapse or Exchange  Events.
 * @author Ralph Soika
 */
public interface ExpandListener {

	/**
	 * Called when an entity is expanded or collapsed
	 * @param expand boolean 
	 */
	void changeExpandStatus();

}
