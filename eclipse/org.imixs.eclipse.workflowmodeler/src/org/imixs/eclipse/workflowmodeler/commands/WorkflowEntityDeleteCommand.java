package org.imixs.eclipse.workflowmodeler.commands;

import java.util.*;
import org.eclipse.gef.commands.Command;
import org.imixs.eclipse.workflowmodeler.model.*;
import org.imixs.eclipse.workflowmodeler.*;


/**
 * Diese Klasse unterstützt das Löschen von WorkflowEntities als Command 
 * inclusive einer Undo Funktion.
 * Dazu werden die per Command gelöschten WorkflowEntities in einem deletionStore von der 
 * Klasse <code>WorkflowmodelerPlugin</code> abgelegt. 
 * In der Undo funktion wird dann auf den deletionStore zugegriffen um die gelöschten Entiies
 * wiederherstellen zu können.
 * 
 * @author Ralph Soika
 *
 */
public class WorkflowEntityDeleteCommand extends Command {

  private AbstractWorkflowEntity currentWorkflowEntity;

    public WorkflowEntityDeleteCommand(AbstractWorkflowEntity workflowEntity) {
        super("Delete WorkflowEntity");
        currentWorkflowEntity=workflowEntity;
     
    }

   

    public void execute() {
    	redo();
    }

    /**
     * Diese Methode enternt ein WorkflwoEntity aus dem Modell
     */
    public void redo() {
    	List list=currentWorkflowEntity.getProcessTree().removeWorkflowEntity(currentWorkflowEntity);
   		WorkflowmodelerPlugin.getPlugin().getDeletionStore().add(list);
    }

    /**
     * Diese Methode dient dazu einen evtl. DeleteCommand rückgängig zu machen
     * also alle zuvor gelöschten ModellObjekte zu restaurieren!
     */
    public void undo() {
    	// Jetzt nehmen wir das letzte vom Stapel
    	System.out.println("bin in undo");
    	
    	Vector vstore=WorkflowmodelerPlugin.getPlugin().getDeletionStore();
    	List list=(List)vstore.lastElement();
    	
    	Iterator iter=list.iterator();
    	while (iter.hasNext()) {
    		currentWorkflowEntity=(AbstractWorkflowEntity)iter.next();
    		
        	if (currentWorkflowEntity instanceof ActivityEntity) {
    	    	ActivityEntity activityEntity=(ActivityEntity) currentWorkflowEntity;
    	    	activityEntity.getProcessEntity().createActivityEntity(currentWorkflowEntity.getItemCollection());
        	}
        	if (currentWorkflowEntity instanceof ProcessEntity) {
        		ProcessEntity processEntity=(ProcessEntity) currentWorkflowEntity;
        		processEntity.getProcessTree().createProcessEntity(currentWorkflowEntity.getItemCollection());
        	}
    	}
    	// Element vom Stapel entfernen
    	WorkflowmodelerPlugin.getPlugin().getDeletionStore().remove(list.size()-1);
    }
}












