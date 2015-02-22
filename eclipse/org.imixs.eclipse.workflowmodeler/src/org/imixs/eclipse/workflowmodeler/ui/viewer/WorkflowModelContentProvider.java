package org.imixs.eclipse.workflowmodeler.ui.viewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.imixs.eclipse.workflowmodeler.*;
import org.imixs.eclipse.workflowmodeler.model.*;

public class WorkflowModelContentProvider implements ITreeContentProvider, PropertyChangeListener {
	private static Object[] EMPTY_ARRAY = new Object[0];
//	public static String NODE_CONFIGURATION="Configuration";
	//public static String NODE_PLUGINS="Plugins";
	public static String NODE_ENVIRONMENT="Environment";
	//private static String[] CONFIGURATION_ARRAY = null; // für Plugin und Environment Konoten
	protected TreeViewer viewer;
	protected WorkflowModel workflowModel;
	
	
	
	public WorkflowModelContentProvider() {
		WorkflowmodelerPlugin.getPlugin().addPropertyChangeListener(this);
	}
	
	
	
	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {}

	
	/**
	* Notifies this content provider that the given viewer's input
	* has been switched to a different element.
	* <p>
	* A typical use for this method is registering the content provider as a listener
	* to changes on the new input (using model-specific means), and deregistering the viewer 
	* from the old input. In response to these change notifications, the content provider
	* propagates the changes to the viewer.
	* </p>
	*
	* @param viewer the viewer
	* @param oldInput the old input element, or <code>null</code> if the viewer
	*   did not previously have an input
	* @param newInput the new input element, or <code>null</code> if the viewer
	*   does not have an input
	*/
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if(oldInput != null) {
			((WorkflowModel)oldInput).removePropertyChangeListener(this);
		}
		if(newInput != null) {
			((WorkflowModel)newInput).addPropertyChangeListener(this);
		}
		// Referenz auf Modell speichern
		workflowModel=(WorkflowModel)newInput;
		
		
	}
	
	
	

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {

		if(parentElement instanceof WorkflowModel) {
			workflowModel = (WorkflowModel)parentElement;
			Object[] processTrees=workflowModel.getProcessTrees().toArray();
			Object[] both = new Object[processTrees.length + 1 ];
			
			// Es werden hier zur Struktururierung der Konfiguration einfache Sting-Objekte angelegt
		//	both[0]=NODE_CONFIGURATION;
			both[0]=NODE_ENVIRONMENT;
			System.arraycopy(processTrees, 0, both, 1, processTrees.length);
			return both;
		}
		
		if(parentElement instanceof ProcessTree) {
			ProcessTree processTree = (ProcessTree)parentElement;
			return processTree.getProcessEntities().toArray();
		}

		if(parentElement instanceof ProcessEntity) {
			ProcessEntity processEntity = (ProcessEntity)parentElement;
			return processEntity.getActivityEntities().toArray();
		}

		/*
		if(parentElement instanceof WorkflowConfiguration) {
			WorkflowConfiguration workflowConfiguration = (WorkflowConfiguration)parentElement;
			return workflowConfiguration.getPluginDescriptions().toArray();
		}
		*/
		
		/*
		if(parentElement.toString().equals(NODE_CONFIGURATION)) {
			// Knoten für Plugins und Environments zurückgeben
			String[] configurations = new String[2];
			// Es werden hier zur Struktururierung der Konfiguration einfache Sting-Objekte angelegt
			if (CONFIGURATION_ARRAY==null) {
				CONFIGURATION_ARRAY = new String[2];
				//CONFIGURATION_ARRAY[0]=NODE_PLUGINS;
				CONFIGURATION_ARRAY[0]=NODE_ENVIRONMENT;
			}
			return CONFIGURATION_ARRAY;
		}
		*/
		
		/*
		if(parentElement.toString().equals(NODE_PLUGINS)) {
			// Pluginliste von WOrkflowmodell ermitteln
			// dazu den RootKonoten ermiteln
			return workflowModel.getPlugins().toArray();
			
		}
		*/
		
		if(parentElement.toString().equals(NODE_ENVIRONMENT)) {
			// Pluginliste von WOrkflowmodell ermitteln
			// dazu den RootKonoten ermiteln
			return workflowModel.getEnvironments().toArray();
			 
		}
		
		
		
		return EMPTY_ARRAY;
	}
	
	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		
		//System.out.println(" HAALLO jetzu tfrägt mich was nach Parents und so...");
		
		if(element instanceof ProcessEntity) {
			return ((ProcessEntity)element).getProcessTree();
		}
		if(element instanceof ActivityEntity) {
			return ((ActivityEntity)element).getProcessEntity();
		}
		if(element instanceof Configuration) {
			return workflowModel;
		}
		if(element instanceof ProcessTree) {
			return ((ProcessTree)element).getWorkflowModel();
		}

		// Bei reinen Textknoten wird einefach das Workflowmodell zurückgegeben
		if(element instanceof String) {
			return workflowModel;
		}
		
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}


	 
	/**
	 * Diese Methode reagiert auf PropertyChange Events um auf 
	 * Änderungen an der Modellklasse zu reagieren  
	 * oder änderungen des gesamten Modells zu registireren
	 */	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(WorkflowmodelerPlugin.MODEL_CHANGED)) {
			if (evt.getNewValue() instanceof WorkflowModel)
				viewer.setInput(evt.getNewValue());
			return;
		}
		if (evt.getPropertyName().equals(WorkflowmodelerPlugin.MODEL_CLOSED)) {
			if (evt.getOldValue()==workflowModel)
				viewer.setInput(null);
			return;
		}
		
		// änderung im Modell -> viewer refreshen (z.B. kommt dieses Event wenn Entities hinzugefügt oder entfernt werden
		viewer.refresh();
		
	}	
}












