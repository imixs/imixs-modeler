package org.imixs.eclipse.workflowmodeler.ui.viewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.imixs.eclipse.workflowmodeler.model.*;
/**
 * Dieser Filter filtert ProcessTrees aus
 * @author Ralph Soika
 *
 */
public class ProcessTreeFilter extends ViewerFilter {

	/*
	 * @see ViewerFilter#select(Viewer, Object, Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return element instanceof ProcessTree || element instanceof AbstractWorkflowEntity;
	}

}
