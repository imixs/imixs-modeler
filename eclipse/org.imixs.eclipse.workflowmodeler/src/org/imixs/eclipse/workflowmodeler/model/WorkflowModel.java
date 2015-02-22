package org.imixs.eclipse.workflowmodeler.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;

/**
 * The WorkflowModel Class manages the ProcesTrees. One WorkflowModel can 
 * manage one ore many different ProcessTrees
 * 
 * @author Ralph Soika
 */
public class WorkflowModel extends ModelObject  {  // implements Model
	public final int ID_INTERVAL=10;

	private String name;
	private HashMap workflowGroups = new HashMap();
	//private HashMap plugins = new HashMap();
	private HashMap environments = new HashMap();
	
	public void clearDirtyFlag() {
		bIsDirty=false;

	}
	
	public void setDirtyFlag() {
		bIsDirty=true;
		firePropertyChange("model_dirty", null, null);
	}

	
	public WorkflowModel(String aModelName) {
		name=aModelName;
	}
	
	public String getName() {
		return name;
	}
	

	
	public void addEnvironment(Configuration config) {
		config.setWorkflowModel(this);
		environments.put(config.getName(),config);
		//  das WorkflowModell registriert sich selbst als Listener beim ConfigurationEntity!
		config.addPropertyChangeListener(this);
		setDirtyFlag();
		firePropertyChange("add_environment", null, config);
	}
	
	
	public void removeEnvironment(String aName) {
		Configuration config=getEnvironment(aName);
		removeConfiguration(config);
	}
	
	/** 
	 * Removes a Configuration ModelObjects an fires an MODEL_CHANGED Evnet
	 * @param config
	 */
	public void removeConfiguration(Configuration config) {
		environments.remove(config.getName());
		config.firePropertyChange(WorkflowmodelerPlugin.MODELOBJECT_REMOVED, config, null);
		config.removePropertyChangeListener(this);
	}
	
	
	
	/**
	 * retruns the List of Plugin Configurations
	 * @return
	 */
	public Collection getEnvironments() {
		return environments.values();
	}
	
	
	public Configuration getEnvironment(String aName) {
		return (Configuration)environments.get(aName);
	}
	

	
    /**
     * returns a sorted List of ProcessTrees
     * List is sorted by the processtree name
     * 
     * @return
     */
	public Collection<ProcessTree> getProcessTrees() {
		List sortedList = new ArrayList();
		Collection c= workflowGroups.values();
		
		Iterator iterGroups=c.iterator();
		while (iterGroups.hasNext()) {
			ProcessTree pt=(ProcessTree)iterGroups.next();
			sortedList.add(pt);		
		}		
		Collections.sort(sortedList,
				new ProcessTreeComparator(new Locale("DE"), true));

		return sortedList;
    }
	
	
	/**
	 * Diese Methode liefert den ProcessTree f�r eine bestimmte WorkflowGruppe
	 * @param aWorkflowGroup
	 * @return
	 */
	public ProcessTree getProcessTree(String aWorkflowGroup) {
    	return (ProcessTree)workflowGroups.get(aWorkflowGroup);
    }

	/**
	 * Returns the default ProcessTree. The first ProcessTree in List
	 * @return
	 */
	public ProcessTree getProcessTree() {
		Iterator it = workflowGroups.entrySet().iterator();
		//int i = 0;
		if (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			//String sName = (String) entry.getKey();
			return (ProcessTree) entry.getValue();
		}
		
		// no entry found!
		return null;
    }
	
	
    public void removeProcessTree(String aWorkflowGroup) {
    
    	workflowGroups.remove(aWorkflowGroup);
    	
    	firePropertyChange(WorkflowmodelerPlugin.MODELOBJECT_REMOVED, aWorkflowGroup, null);

    }

    
    
    
    /**
     * This Method add a ProcessTree tu the Model List.
     * If a ProcessTree with the same name already exists the method
     * automatically increases the name with a number. So the method garanties unique 
     * ProcessTree Names!
     * 
     * @param aProcessTree
     */
    public void addProcessTree( ProcessTree aProcessTree) {
    	aProcessTree.setWorkflowModel(this);
    	// Falls kein Name �bergeben wrude "Default" eintragen
    	if (aProcessTree.getName()==null || "".equals(aProcessTree.getName()))
    		aProcessTree.setName("Default");
    	
    	// Check if Name allready exists
    	String sUniqueTreeName=aProcessTree.getName();
    	int iCounter=1;
    	while (workflowGroups.get(sUniqueTreeName)!=null) {
    		sUniqueTreeName=aProcessTree.getName()+iCounter;
    		iCounter++;
    	}
    	if (!sUniqueTreeName.equals(aProcessTree.getName()))
    		aProcessTree.setName(sUniqueTreeName);
    	workflowGroups.put(aProcessTree.getName(),aProcessTree);
    	// Das WorkflowModell registriert sich als Listender beim ProzessTree
    	aProcessTree.addPropertyChangeListener(this);
    	
    	firePropertyChange("add_processtree", null, aProcessTree);

    }

    
    
    
   
	
	
	
	
	/**
     * This Method returns the next unique processID 
     * The Mehod validates all existing Process IDs in all exiting processTrees
     * This Method is used by the ProcessTree to create unique ProcessEntities
     * @return
     */
    public int getFeeProcessID() {
    	int iCurrentID=-1;
    	int iHighestID=-1;
    	
    	Iterator iterProcessTrees=getProcessTrees().iterator();
    	while (iterProcessTrees.hasNext()) {
    		ProcessTree tree=(ProcessTree)iterProcessTrees.next();
    		// check all processEntities
    		Iterator iterProcessEntities=tree.getProcessEntities().iterator();
    		while (iterProcessEntities.hasNext()) {
    			ProcessEntity entity=(ProcessEntity)iterProcessEntities.next();
    			iCurrentID=entity.getItemCollection().getItemValueInteger("numProcessID");
	    		if (iCurrentID>iHighestID)
	    			iHighestID=iCurrentID;	
	    	}
    	}
    	iCurrentID=iHighestID+ID_INTERVAL-(iHighestID%ID_INTERVAL);
    	return iCurrentID;
    }
    
    /**
     * This Method checks if a ProcessID is unuesed in the current processTrees
     * @return
     */
    public boolean isFeeProcessID(int iID) {
    	int iCurrentID=-1;
    	//int iHighestID=-1;
    	
    	Iterator iterProcessTrees=getProcessTrees().iterator();
    	while (iterProcessTrees.hasNext()) {
    		ProcessTree tree=(ProcessTree)iterProcessTrees.next();
    		// check all processEntities
    		Iterator iterProcessEntities=tree.getProcessEntities().iterator();
    		while (iterProcessEntities.hasNext()) {
    			ProcessEntity entity=(ProcessEntity)iterProcessEntities.next();
    			iCurrentID=entity.getItemCollection().getItemValueInteger("numProcessID");
    			if (iCurrentID==iID)
    				return false;
	    	}
    	}
    	return true;
    }
	
    
   private class ProcessTreeComparator implements Comparator {
    	private final Collator collator;

    	private final boolean ascending;

    	public ProcessTreeComparator(Locale locale, boolean ascending) {
    		this.collator = Collator.getInstance(locale);
    		this.ascending = ascending;
    	}

    	
		public int compare(Object a, Object b) {
			int result = this.collator.compare(((ProcessTree)a).getName(), ((ProcessTree)b).getName());
    		if (!this.ascending) {
    			result = -result;
    		}
    		return result;
		}

    }
}



