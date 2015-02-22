package org.imixs.eclipse.workflowmodeler.model;

import java.util.Iterator;
import java.util.List;

import org.imixs.workflow.ItemCollection;

/**
 * Das ActivityEntity Model Objekt verf�gt �ber je eine eingehende und eine
 * ausgehende Verbindung. Das Model Objekt h�lt immer diese beide Verbindungen,
 * da ein ActivityEntity immer einem Prozess zugeordnet ist (numProcessID) und
 * immer eine FolgeProzess (numNextProcessID) besitzt
 * 
 * @author Ralph Soika
 * 
 */
public class ActivityEntity extends AbstractWorkflowEntity implements IActivityPropertySource {
	private ProcessEntity processEntity;

	public ActivityEntity(ItemCollection aItemCollection) {
		super(aItemCollection);
		setItemCategory("txtName", "Activity Entity");
	}

	public void setProcessEntity(ProcessEntity pe) {
		processEntity = pe;
	}

	public ProcessEntity getProcessEntity() {
		return processEntity;
	}

	/**
	 * returns the next process entity conected to this activity
	 * 
	 * @return ProcessEntity
	 */
	public ProcessEntity getNextProcessEntity() {
		try {
			int iNextID = this.getItemCollection().getItemValueInteger(
					"numNextProcessID");
			return processEntity.getProcessTree().getProcessEntityModelObject(
					iNextID);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * In the following the method checks if numNextProcessID should be changed.
	 * The Method checks the outgoing associations and disconnect the old
	 * connection and builds a new associations to the new processEntity.
	 * numNextProcessID must point to an valid and existing ProcessEntity. Only
	 * one outgoing connection is allowed! With the method
	 * association.setToEntity() the association will be informed about the
	 * reconnect. The Association initiate then the redraw with the call of the
	 * method firePropertyChange("toEntity", oldValue, toEntity) The model
	 * objects guarantees that a ActivityEntity always have an outgoing and one
	 * incoming Association
	 */
	public void setPropertyValue(Object id, Object value) {
	
		// change of NextProcessID ?
		if ("numNextProcessID".equalsIgnoreCase(id.toString())) {
			// check if new value is valid

			List list = this.getAssociationOutgoing();
			// consistence check: if more then one connection exists, they will
			// now be removed
			if (list != null && list.size() > 1) {
				System.out
						.println("[ActivityEntity] consistence error: more then one outgoing connection!");
				while (list.size() > 1) {
					Association association = (Association) list.get(1);
					association.disconnect();
					list.remove(association);
				}
			}

			// only one outgoing Association allowed
			if (list != null && list.size() == 1) {
				// check if numNextProcessID point to an existing ProcessEntity
				// inside the current ProcessTree
				AbstractWorkflowEntity newEntity = this.getProcessTree()
						.getWorkflowEntityFromMap(value.toString());
				

				// if newEntity == null the Entity exists not in the same
				// ProcessTree
				if (newEntity == null) {
					// Start an extended search in other ProcessTrees of this
					// model....
					WorkflowModel wfModel = this.getProcessTree()
							.getWorkflowModel();
					Iterator iterProcessTrees = wfModel.getProcessTrees()
							.iterator();
					while (iterProcessTrees.hasNext()) {
						ProcessTree tree = (ProcessTree) iterProcessTrees
								.next();
						newEntity = tree.getWorkflowEntityFromMap(value
								.toString());
						if (newEntity != null) {
							// ProcessEntity was found in other ProcessTree
							// set ProcessTree name Property
							super.setPropertyValue("txtNextProcessTree", tree
									.getName());
							// connect to itself to indecate that a special routing is expected
							// @see EntityConnectionRouter
							newEntity=this;
							break;
						}
					}

				} else {
					// reset txtNextProcessGroup as ProcessID is in same ProcessTree
					super.setPropertyValue("txtNextProcessTree","");
				}
				// is newEntity still null then no valid ProcessID was found in
				// the whole model
				if (newEntity == null) {
					// no valid ProcessEntity found - return without call of
					// super.setPropertyValue(id,value)
					return;
				} else {
					// reconnect old association ?
					Association association = (Association) list.get(0);
					AbstractWorkflowEntity oldentiy = association.getToEntity();
					// reconnect only if processEntity has changed
					if (oldentiy != newEntity) {
						association.disconnect();
						// reconnect
						association.setToEntity(newEntity);
						association.setFromEntity(this);
						association.connect();
					}

				}
			}
		}

		if ("numprocessid".equalsIgnoreCase(id.toString())) {
			// falls die nextID auf die aktuelle zeicht mu� die nextID angepasst
			// werden!
			if (getPropertyValue("numnextprocessid").equals(
					getPropertyValue("numprocessid"))) {
				// System.out.println("## equals -> update nextprocessID!");
				setPropertyValue("numnextprocessid", value);
				// ModelMap aktualiseren!
				String sIdOld = getPropertyValue("numprocessid").toString();
				String sActivityId = getPropertyValue("numactivityid")
						.toString();
				this.getProcessTree().updateModelMap(
						sIdOld + "." + sActivityId, value + "." + sActivityId);
			}
		}

		if ("numactivityid".equalsIgnoreCase(id.toString())) {
			// ModelMap aktualiseren!
			String sIdOld = getPropertyValue("numactivityid").toString();
			String sProcessId = getPropertyValue("numprocessid").toString();
			
			// check if new ID is uniquq in current model?
			
			AbstractWorkflowEntity aaE=this.getProcessTree().getWorkflowEntityFromMap(sProcessId+"."+value);
			if (aaE!=null && aaE!=this) {
				System.out.println("ActivityEntity - new ID Conflict");
				return;
			}
			
			this.getProcessTree().updateModelMap(sProcessId + "." + sIdOld,
					sProcessId + "." + value);
			// Sort Activities
			this.getProcessEntity().sortActivities();
		}

		super.setPropertyValue(id, value);

	}

	/***************************************************************************
	 * Conection Handling
	 * 
	 * Activities k�nnen immer nur eine Eingehende und eine Ausgehende
	 * Verbindung besitzten. Daher wird im folgenden gepr�ft ob ggf. bereits
	 * vorhandene Verbindungen entfernt werden m�ssen, bevor neue aufgebaut
	 * werden d�rfen
	 * 
	 **************************************************************************/

	public void addAssociationOutgoing(Association association) {

		// Gibt es bereist ausgehende Verbindungen?
		while (associationsOutgoing.size() > 0) {
			Association aOld = (Association) associationsOutgoing.get(0);
			aOld.disconnect();
			aOld = null;
		}
		
		super.addAssociationOutgoing(association);
	}

}
