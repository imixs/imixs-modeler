package org.imixs.eclipse.workflowmodeler.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.workflow.ItemCollection;

/**
 * Der ProcessTree bildet das Workflowmodell einer WorkflowGruppe ab. Es wird
 * eine eigene Methode createProcessEntity bereitgestellt, die ein neuen
 * ProzessEntity dem Modell hinzuf�gt. Das ModellObjekt ProcessEntity verf�gt
 * wiederum �ber eine Methode um ActivtyEntities zu erzeugen. Somit ist die
 * eindeugite Zuordnung eines ActivityEntity zu einem ProzessEntiy immer
 * garantiert und das Model konsistent.
 * 
 * Die Methode refreshModel() dient dazu die verbindungen (Associations)
 * zwischen den ProcessEntities und ActivityEntities herzustellen Die Klasse
 * implementiert zus�tzlich das Interface <code>org.imixs.workflow.Model</code>
 * 
 * 
 * TODO man sollte hier mal versuchen auf den Vector workflowEntities ganz zu
 * verzichten so dass ein ProcessTree lediglich seine ProcessEntiteis kennt und
 * die ProzessEntites eben nur die ActivityEntities - das k�nnte den Code
 * vereinfachen ...
 * 
 * 
 * @author Ralph Soika
 */
public class ProcessTree extends ModelObject implements IGroupPropertySource {
	private WorkflowModel workflowModel;

	private Vector workflowEntities = new Vector();

	private Vector processEntites = new Vector();

	private HashMap modelMap = new HashMap();

	private String name;

	// private ProcessEntity currentProcessEntity=null;

	public ProcessTree(String aName) {
		name = aName;
	}

	/**
	 * This Method returns the current name of the Process tree. Also namend the
	 * ProcessGroups Every ProcessEntity have an attribute txtWorkflowGroup with
	 * this name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * This Method sets the name of the process tree
	 * 
	 * @param aName
	 */
	public void setName(String aName) {
		name = aName;
	}

	public void setWorkflowModel(WorkflowModel aWorkflowModel) {
		workflowModel = aWorkflowModel;
	}

	public WorkflowModel getWorkflowModel() {
		return workflowModel;
	}

	public List getWorkflowEntities() {
		return workflowEntities;
	}

	public List getProcessEntities() {
		return processEntites;
	}

	public ProcessEntity createProcessEntity() {
		return createProcessEntity(null);
	}

	/**
	 * This method creates a new processentity. The method expects a
	 * ItemCollection with attributes for the ProcessEntiry. If ItemColleciton
	 * is null the method creates an emty processentity. The methode verfies if
	 * the numprocessid it free in the current process tree. ProcessIDs have to
	 * be unique! The method also sets the attribute "txtWorkflowGroup" to the
	 * name of the processtree.
	 * 
	 * @param itemCol
	 * @return ProcessEntity
	 */
	public ProcessEntity createProcessEntity(ItemCollection itemCol) {
		try {
			if (itemCol == null) {
				// create new itemcollection
				itemCol = new ItemCollection();
				itemCol.replaceItemValue("txtName", "[new Process]");
				// set next free process id
				itemCol.replaceItemValue("numProcessID", new Integer(this
						.getWorkflowModel().getFeeProcessID()));
			} else {
				// check if provided ID is available in current tree?
				int iPid = itemCol.getItemValueInteger("numProcessID");
				if (!this.getWorkflowModel().isFeeProcessID(iPid)) {
					// ProcessID already taken!
					System.out.println("ERROR createProcessEntity(): ID "
							+ iPid + " allready defined!");
					return null;
				}
			}
			// set WorkflowGroupname!
			itemCol.replaceItemValue("txtWorkflowGroup", getName());

		} catch (Exception e) {
			System.out.println("[ProcessTree] createProcessEntity error: " + e);

		}
		ProcessEntity entity = new ProcessEntity(itemCol);
		processEntites.add(entity);

		// resort existing ProcessEntities
		// resorting will also be processed after Property numProcessid changed
		// in the
		// ProcessEntity Object. @See ProcessEntity.setPropertyValue()
		sortProcessEntities();

		addWorkflowEntity(entity);
		return entity;
	}

	/**
	 * helper method: resorts the entries of the activitiesEntities Vector
	 * dependend on the numActivityID.
	 */
	public void sortProcessEntities() {

		Collections.sort(processEntites, new Comparator() {
			public int compare(Object o1, Object o2) {
				ProcessEntity a1 = (ProcessEntity) o1;
				ProcessEntity a2 = (ProcessEntity) o2;

				// Ids ermitteln
				Integer id1 = new Integer(a1.getItemCollection()
						.getItemValueInteger("numProcessID"));
				Integer id2 = new Integer(a2.getItemCollection()
						.getItemValueInteger("numProcessID"));

				return (id1.compareTo(id2));
			}
		});
	}

	/**
	 * Diese Methode f�gt ein neues WorkfowEntity in das Modell ein und
	 * aktualisiert gleichzeitig die modelMap f�r den gezielten Zugriff auf
	 * ProcessEntities und ActivityEntities Dem WorkflowEntity wird
	 * geleichzeitig eine ProcessTree Referenz �bergeben
	 * 
	 * Die Methode pr�ft ob ggf bereits ein geleiches WorkflowEntity in die
	 * ModelMap eingetragen ist. Falls dies der Fall ist, wird das vorhandene
	 * automatisch entfernt!
	 * 
	 * @param entity
	 */
	void addWorkflowEntity(AbstractWorkflowEntity entity) {
		// if (entity instanceof ProcessEntity)
		this.workflowEntities.add(entity);

		try {
			if (entity instanceof ProcessEntity) {
				int iID = entity.getItemCollection().getItemValueInteger(
						"numProcessID");
				// existiert bereits ein ProcessEntity mit dieser ID?
				AbstractWorkflowEntity oldEntity = (AbstractWorkflowEntity) modelMap
						.get("" + iID);
				if (oldEntity != null) {
					// altes Entity entfernen!
					this.removeWorkflowEntity(oldEntity);
				}
				modelMap.put("" + iID, entity);
			}
			if (entity instanceof ActivityEntity) {
				int iID = entity.getItemCollection().getItemValueInteger(
						"numProcessID");
				int iActivityID = entity.getItemCollection()
						.getItemValueInteger("numActivityID");

				// existiert bereits ein ProcessEntity mit dieser ID und NextID?
				AbstractWorkflowEntity oldEntity = (AbstractWorkflowEntity) modelMap
						.get(iID + "." + iActivityID);
				if (oldEntity != null) {
					// altes Entity entfernen!
					this.removeWorkflowEntity(oldEntity);
				}
				modelMap.put(iID + "." + iActivityID, entity);
			}
		} catch (Exception e) {

		}
		entity.setProcessTree(this);
		// Der ProcessTree registriert sich selbst als Listener beim Entity!
		entity.addPropertyChangeListener(this);

		// nun wird die Modell�nderung an registriete Observer gesendet
		// (ModelTreeEditPart)
		// damit eine Aktualisierung des Editors erfolgt
		firePropertyChange("add_workflowentity", null, entity);
	}

	/**
	 * Diese Methode entfernt eine einzelnes WorkflowEntity und k�mmert sich
	 * auch darum, das alle vorhandenen Verbindungsobjekte entfernt wrden!
	 * 
	 * Die Methode unterscheidet zwischen ProcessEntities und ActivityEntites
	 * wird ein ProzessEntity gel�scht, l�scht die Methode auch recursiv die
	 * dazugeh�rigen ActivityEntites.
	 * 
	 * TODO Die Methode sollte eine Liste mit den gel�schten Entities
	 * zur�ckliefern, damit eine evtl. Undo funktion die M�glichkeit hat die
	 * Elemente wiederherzustellen.
	 * 
	 * @param entity
	 */
	public List removeWorkflowEntity(AbstractWorkflowEntity entity) {
		Vector vector = new Vector();

		// Falls es sich um ein ProcessEntity handelt werden
		// die zugeordneten ActivityEntities ermittelt und recursiv gel�scht.
		if (entity instanceof ProcessEntity) {
			List list = getActivityObjects((ProcessEntity) entity);
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				ActivityEntity aActivityEntity = (ActivityEntity) iter.next();
				vector.add(aActivityEntity);
				// recusriver aufruf zur L�schung der Activities
				removeWorkflowEntity(aActivityEntity);

			}

		}

		// Verbindungen ermitteln!
		Association[] verwendungenIncoming = (Association[]) entity
				.getAssociationIncoming()
				.toArray(
						new Association[entity.getAssociationIncoming().size()]);
		Association[] verwendungenOutgoing = (Association[]) entity
				.getAssociationOutgoing()
				.toArray(
						new Association[entity.getAssociationOutgoing().size()]);
		// Eingehende Verbindunen l�schen
		for (int i = 0; i < verwendungenIncoming.length; i++)
			verwendungenIncoming[i].disconnect();
		// Ausgehende Verbindunen l�schen
		for (int i = 0; i < verwendungenOutgoing.length; i++)
			verwendungenOutgoing[i].disconnect();

		// ModelMap bereinigen bzw. Prozess und Activity List bereinigen
		try {
			if (entity instanceof ProcessEntity) {
				int iID = entity.getItemCollection().getItemValueInteger(
						"numProcessID");
				modelMap.remove("" + iID);

				// Falls das ProcessEntity noch activities hat diese auch
				// l�schen
				((ProcessEntity) entity).getActivityEntities().removeAll(null);
				// Eintrag aus processEntity List l�schen
				processEntites.remove(entity);
			}

			if (entity instanceof ActivityEntity) {
				int iID = entity.getItemCollection().getItemValueInteger(
						"numProcessID");
				int iActivityID = entity.getItemCollection()
						.getItemValueInteger("numActivityID");
				modelMap.remove(iID + "." + iActivityID);

				// Eintrag aus processEntity.Activity List l�schen
				((ActivityEntity) entity).getProcessEntity()
						.getActivityEntities().remove(entity);
			}
		} catch (Exception e) {
		}

		vector.add(entity);

		// Eintrag aus ProcessTree l�schen
		this.workflowEntities.remove(entity);
		firePropertyChange(WorkflowmodelerPlugin.MODELOBJECT_REMOVED, entity,
				null);

		return vector;
	}

	/**
	 * This methos returns all Activities of a ProcessEntity
	 * 
	 * @param aProcessEntity
	 * @return
	 */
	public List getActivityObjects(ProcessEntity aProcessEntity) {
		Vector vector = new Vector();
		try {
			int iID = aProcessEntity.getItemCollection().getItemValueInteger(
					"numProcessID");
			// Alle ActivityEntites zu dieser ID ermitteln
			Iterator iter = workflowEntities.iterator();
			while (iter.hasNext()) {
				// Map.Entry entry = (Map.Entry) iter.next();
				AbstractWorkflowEntity aEntity = (AbstractWorkflowEntity) iter
						.next();
				if ((aEntity instanceof ActivityEntity)
						&& (iID == aEntity.getItemCollection()
								.getItemValueInteger("numProcessID")))
					vector.add(aEntity);
			}
		} catch (Exception e) { /* noop */
		}
		return vector;
	}

	/**
	 * This method returns a WorkflowEntity out from the modelMap For a
	 * ProcessEntity the ModelEntiry need to be a String respresenting the
	 * "<processid>" For an ActivityEntity the ModelEntry nee to be a String in
	 * the format "<processid>.<activityid>"
	 * 
	 * @param aModelEntry
	 * @return
	 */
	public AbstractWorkflowEntity getWorkflowEntityFromMap(String aModelEntry) {
		AbstractWorkflowEntity entity = (AbstractWorkflowEntity) modelMap
				.get(aModelEntry);
		return entity;
	}

	/**
	 * This methods updates a single entry in the ModelMap This is importend
	 * after an update or a ProcessID or ActivityID!
	 * 
	 * @param aOldKey
	 * @param aNewKey
	 */
	public void updateModelMap(String aOldKey, String aNewKey) {
		AbstractWorkflowEntity entity = (AbstractWorkflowEntity) modelMap
				.get(aOldKey);
		if (entity == null) { 
			// exist not yet!
			
			// fatal - can not happen!
			//addWorkflowEntity(entity);
		} else {
			if (aOldKey.equals(aNewKey))
				return;

			// replace key !
			modelMap.remove(aOldKey);
			// resort
			modelMap.put(aNewKey, entity);
			
			
			// Now we need to verify if in case of renaming a ProcessENtiy ID 
			// activities can be affecated!!!
			if (1==2 && entity instanceof ProcessEntity) {
				// it is a ProcessENtity-
				// lets see if we need to update activities
				List activities = getActivityObjects((ProcessEntity)entity);
				for (Object aentry: activities) {
					ActivityEntity activity=(ActivityEntity)aentry;
					
					//int nextID=
				}
				
			//	asdf
			}
			
			
			
		}
	}

	public ProcessEntity getProcessEntityModelObject(int processid) {
		return (ProcessEntity) modelMap.get("" + processid);
	}

	public ActivityEntity getActivityEntityModelObject(int processid,
			int activityid) {
		return (ActivityEntity) modelMap.get(processid + "." + activityid);

	}

	/**
	 * The ProcessTree Class has an individual implementation of the
	 * IPropertySource Interface The only supported property is the property
	 * "name" If the name of an ProcessTree changed the processtree must be also
	 * updated inside the current workflowModel Object.
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {

		if (propertyDescriptors == null) {
			// ProcessTree has only one property
			propertyDescriptors = new IPropertyDescriptor[1];
			TextPropertyDescriptor descriptor = new TextPropertyDescriptor(
					"name", "Name");
			propertyDescriptors[0] = (IPropertyDescriptor) descriptor;
		}
		return propertyDescriptors;
	}

	/**
	 * This methode overrides the setPropertyValue method to handle changes of
	 * the name Property. If the Name of the processTree changed the old object
	 * is disconnected form the WorkflowModel Object an then reconnected with
	 * the new name This Method also refreshes the attribute txtWorkflowGroup of
	 * all existing processEntity Objects
	 * 
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("name".equalsIgnoreCase(id.toString())) {
			String sOld = getName();
			name = value.toString();
			// setName(value.toString());
			// disconnect tree under old name
			if (workflowModel != null) {
				workflowModel.removeProcessTree(sOld);
				workflowModel.addProcessTree(this);
			}

			// change txtworkflowGroup attribute of all existing processEntities
			Iterator iter = processEntites.iterator();
			while (iter.hasNext()) {
				try {
					ProcessEntity entity = (ProcessEntity) iter.next();
					entity.getItemCollection().replaceItemValue(
							"txtWorkflowGroup", name);
				} catch (Exception e) {
					// unable to set attribute
				}
			}

			firePropertyChange(id.toString(), sOld, value);
		}
	}

	public Object getPropertyValue(Object id) {
		if ("name".equals(id))
			return getName();
		else
			return null;
	}

}
