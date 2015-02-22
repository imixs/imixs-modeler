package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.imixs.eclipse.workflowmodeler.*;
import org.imixs.eclipse.workflowmodeler.model.*;
import org.imixs.eclipse.workflowmodeler.ui.editparts.AssociationEditPart;

public class PropertyLabelProvider extends LabelProvider {
	private Map imageCache = new HashMap(11);

	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;

		if (element instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) element).getFirstElement();
			if (object == null)
				return null;

			// chek if object is an Editpart (GEF) -> then get the Model Object!
			if (object instanceof org.eclipse.gef.EditPart) {
				if (object instanceof AssociationEditPart)
					descriptor = WorkflowmodelerPlugin.getPlugin().getIcon(
							"viewer/activity.gif");
				else
					object = ((org.eclipse.gef.EditPart) object).getModel();
			}
			if (object instanceof ActivityEntity)
				descriptor = WorkflowmodelerPlugin.getPlugin().getIcon(
						"viewer/activity.gif");
			if (object instanceof ProcessEntity)
				descriptor = WorkflowmodelerPlugin.getPlugin().getIcon(
						"viewer/process.gif");
			if (object instanceof ProcessTree)
				descriptor = WorkflowmodelerPlugin.getPlugin().getIcon(
						"viewer/processtree.gif");

		}

		if (descriptor == null)
			return null;

		// obtain the cached image corresponding to the descriptor
		Image image = (Image) imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;

	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {

		if (element instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) element).getFirstElement();
			if (object == null)
				return "";

			// chek if object is an Editpart (GEF) -> then get the Model Object!
			if (object instanceof org.eclipse.gef.EditPart) {
				if (object instanceof AssociationEditPart)
					return "Activity Entity";
				else
					object = ((org.eclipse.gef.EditPart) object).getModel();
			}
			if (object instanceof ActivityEntity)
				return "Activity Entity";
			if (object instanceof ProcessEntity)
				return "Process Entity";
			if (object instanceof ProcessTree)
				return "Process Group";

		}

		return "";

	}

	public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

}
