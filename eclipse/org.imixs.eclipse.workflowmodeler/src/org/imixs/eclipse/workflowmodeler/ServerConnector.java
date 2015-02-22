

package org.imixs.eclipse.workflowmodeler;

import org.eclipse.core.resources.IFile;
import org.imixs.eclipse.workflowmodeler.model.WorkflowModel;

/**
 * Interface for a ServerConnector to upload and download workflowmodels to a external server
 * The ServerConnectors appears in a WorkflowModelDefaultPage .
 
 * @author Ralph Soika
 * @see org.imixs.eclipse.workflowmodeler.ui.editors.WorkflowModelDefaultPage
 *
 */
public interface ServerConnector {
	
	/**
	 * Downloads a model from a Server and stores the model into a file instance.
	 * 
	 * @param workflowModel
	 * @param file
	 */
	public void downloadModel(WorkflowModel workflowModel, IFile file) throws Exception;
		
	/**
	 * uploads a model form a server
	 * @param workflowModel
	 */
	public void uploadModel(WorkflowModel workflowModel) throws Exception;
			
}
