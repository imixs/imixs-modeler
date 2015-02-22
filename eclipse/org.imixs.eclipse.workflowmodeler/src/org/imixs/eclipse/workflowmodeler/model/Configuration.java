package org.imixs.eclipse.workflowmodeler.model;

import java.beans.PropertyChangeEvent;

import org.imixs.workflow.*;

/**
 * This Class manages configurations.
 * Configurations are used by the WorkflowModel Class to manage
 * Plugins and Environments
 * 
 * @author Ralph Soika
 */
public class Configuration extends ModelObject  {  
	private WorkflowModel workflowModel;
	private String sName;

	public Configuration(String aName, ItemCollection itemCol) {
		super();
		setItemCollection(itemCol);
		sName=aName;
		addPropertyChangeListener(this);
	}
	

	public void setWorkflowModel(WorkflowModel aWorkflowModel) {
		workflowModel=aWorkflowModel;
	}
	public WorkflowModel getWorkflowModel() {
		return workflowModel;
	}	
	

	public String getName() {
		return sName;
	}
 
	
	/**
	 * Listen to name changes!
	 */
    public void propertyChange(PropertyChangeEvent evt) {
    	
    	String sPropertyName=evt.getPropertyName();
    	if ("txtname".equalsIgnoreCase(sPropertyName)) {
     		// change key
    		String sNewKey=evt.getNewValue().toString();

    		// check if configuration is a plugin ?
    		/*
    		Configuration config=workflowModel.getPlugin(getName());
    		if (config!=null) {
    			workflowModel.removePlugin(getName());
    			sName=sNewKey;
    			workflowModel.addPlugin(this);
    			return;
    		}
    		*/
       		// check if configuration is a environment ?
    		Configuration config=workflowModel.getEnvironment(getName());
    		if (config!=null) {
    			workflowModel.removeEnvironment(getName());
    			sName=sNewKey;
    			workflowModel.addEnvironment(this);
    			return;
    		}   		
    	}  
    }	    
	     

}



