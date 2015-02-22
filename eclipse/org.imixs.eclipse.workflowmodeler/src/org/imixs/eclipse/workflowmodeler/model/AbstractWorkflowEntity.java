package org.imixs.eclipse.workflowmodeler.model;

import java.util.*;
import org.eclipse.ui.views.properties.*;
import org.imixs.workflow.ItemCollection;

/**
 * This Abstract Class encapsulate the basic functions of a Workflow
 * ProcessEntity and ActivtyEntity AbstractWorkflowEntity supports a
 * ItemCollection functionality with an IPropertyDescriptor and a Conection
 * Hanlding to handle incomming and outcomming Associations
 * 
 * @author Ralph Soika
 *  
 */
public abstract class AbstractWorkflowEntity extends ModelObject {
	// associations
	List associationsOutgoing = new ArrayList();
	List associationsIncoming = new ArrayList();


	// ProcessTree Referenz
	private ProcessTree modelTree = null;

	// Constructor needs ItemCollection
	public AbstractWorkflowEntity(ItemCollection aItemCollection) {
		setItemCollection(aItemCollection);

	}

	public ProcessTree getProcessTree() {
		return modelTree;
	}

	public void setProcessTree(ProcessTree aModelTree) {
		modelTree = aModelTree;
	}



	/***************************************************************************
	 * Property Handling for ItemCollection
	 **************************************************************************/

	
	/**
	 * AbstractWorkflowEntity groups all Items into the Category "Items" With
	 * this Method you can regroup a named Item into another Category
	 */
	public void setItemCategory(Object itemID, String newCategory) {
		// Initialize propertyDescriptor if not done yet
		if (propertyDescriptors == null)
			getPropertyDescriptors();

		// find Descriptor....
		for (int i = 0; i < propertyDescriptors.length; i++) {
			TextPropertyDescriptor descriptor = (TextPropertyDescriptor) propertyDescriptors[i];

			if (descriptor.getId().equals(itemID)) {
				descriptor.setCategory(newCategory);
				return;
			}
		}
	}

	/***************************************************************************
	 * Conection Hanlding
	 * 
	 * Code wird durch ActivityEntity überschrieben
	 **************************************************************************/

	public void addAssociationOutgoing(Association association) {
		associationsOutgoing.add(association);
		firePropertyChange("associationsOutgoing", null, association);
	}

	public void addAssociationIncoming(Association association) {
		associationsIncoming.add(association);
		firePropertyChange("associationsIncoming", null, association);

	}

	public void removeAssociationOutgoing(Association association) {
		associationsOutgoing.remove(association);
		firePropertyChange("associationsOutgoing", association, null);
	}

	public void removeAssociationIncoming(Association association) {
		associationsIncoming.remove(association);
		firePropertyChange("associationsIncoming", association, null);
	}

	public List getAssociationIncoming() {
		return associationsIncoming;
	}

	public List getAssociationOutgoing() {
		return associationsOutgoing;
	}

}
