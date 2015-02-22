
package org.imixs.eclipse.workflowmodeler.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.imixs.eclipse.workflowmodeler.model.*;

/**
 * Diese Klasse stellt einen EditorInput bereit. 
 * Dabei werden einzelne Instanzen von ProcessTree verwaltet
 * Diese m�ssen im Construcktor �bergeben werden 
 * @author Ralph Soika
 *
 */
public class EditorInput implements IEditorInput {
	ProcessTree processTree;
	
	
	public EditorInput(ProcessTree aProcessTree) {
		processTree=aProcessTree;
	}
	
	public ProcessTree getProcessTree() {
		return processTree;
	}
	
	/**
	 * Two ProcessTrees are equals if their Names are equals and
	 * their WorkflowModel Names equals!
	 */
	public boolean equals(Object aInputType) {
		if (aInputType instanceof EditorInput) {
			EditorInput neuerInput=(EditorInput)aInputType;
			String sName=processTree.getName();
			String sNameNeu=neuerInput.getName();
			
			String sNameModel=processTree.getWorkflowModel().getName();
			String sNameModelNeu=((EditorInput)aInputType).processTree.getWorkflowModel().getName();
			return (sName.equals(sNameNeu)  && sNameModel.equals(sNameModelNeu));
		}
		return false;
	}
	
	
	
	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	
	public String getName() {
		return processTree.getName();
	}

	
	public IPersistableElement getPersistable() {
		return null;
	}

	
	public String getToolTipText() {
		return "Workflowgroup " + processTree.getName();
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
	

}
