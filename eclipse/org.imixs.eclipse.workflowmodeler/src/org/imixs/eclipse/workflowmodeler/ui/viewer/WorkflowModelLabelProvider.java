package org.imixs.eclipse.workflowmodeler.ui.viewer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.imixs.eclipse.workflowmodeler.*;
import org.imixs.eclipse.workflowmodeler.model.*;

public class WorkflowModelLabelProvider extends LabelProvider {	
	private Map imageCache = new HashMap(11);
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		
		
		if (element instanceof ProcessTree) 
			descriptor= WorkflowmodelerPlugin.getPlugin().getIcon("viewer/processtree.gif");

		
		if (element instanceof ProcessEntity) 
			descriptor= WorkflowmodelerPlugin.getPlugin().getIcon("viewer/process.gif");

		if (element instanceof Configuration) { 
			// handelt es sich um ein Plugin
		/*	if ((Configuration)element==((Configuration)element).getWorkflowModel().getPlugin(((Configuration)element).getName()))
			//if (((Configuration)element).getWorkflowModel().getPlugins().indexOf(element)>-1)
				descriptor= WorkflowmodelerPlugin.getPlugin().getIcon("viewer/plugin_obj.gif");
			else*/
				descriptor= WorkflowmodelerPlugin.getPlugin().getIcon("viewer/environment_obj.gif");
		}
		
		if (element instanceof String) {
			/*
			if (element.toString().equals(WorkflowModelContentProvider.NODE_CONFIGURATION))
				descriptor= WorkflowmodelerPlugin.getPlugin().getIcon("viewer/configuration.gif");
				*/
			/*if (element.toString().equals(WorkflowModelContentProvider.NODE_PLUGINS))
				descriptor= WorkflowmodelerPlugin.getPlugin().getIcon("viewer/plugin_config.gif");
		*/
			if (element.toString().equals(WorkflowModelContentProvider.NODE_ENVIRONMENT))
				descriptor= WorkflowmodelerPlugin.getPlugin().getIcon("viewer/environment_config.gif");
		}
		
	/*	if (element instanceof PluginDescription) {
			descriptor= WorkflowmodelerPlugin.getPlugin().getIcon("viewer/plugin_obj.gif");
		}
		*/
		if (element instanceof ActivityEntity)
			descriptor= WorkflowmodelerPlugin.getPlugin().getIcon("viewer/activity.gif");
	
		
		if (descriptor==null)
			return null;
		
		//obtain the cached image corresponding to the descriptor
		Image image = (Image)imageCache.get(descriptor);
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
		if (element instanceof String) {
			return element.toString();
		}
		if (element instanceof ProcessTree) {
			return ((ProcessTree)element).getName();
		}
		if (element instanceof Configuration) {
			return ((Configuration)element).getName();
		}
		if (element instanceof AbstractWorkflowEntity) {
			try {
				return ((AbstractWorkflowEntity)element).getItemCollection().getItemValueString("txtName");
			} catch (Exception e) {
				return "- no title-";
			}
		}
		return "- no value -";
	}

	public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

}
