package org.imixs.eclipse.workflowmodeler.model;

import java.util.*;

import org.imixs.workflow.ItemCollection;

/**
 * This Class build the modelObjejct for a ProcessEntity. The Object observes
 * the creation of Activities by the the methode createActivityEntity and adds
 * them automaticaly to a member variable.
 * 
 * The methods collapse() and expand() handle the expandtion and collapse of the
 * related activityEntities.
 * 
 * @author Ralph Soika
 */

public class ProcessEntity extends AbstractWorkflowEntity implements
		IProcessPropertySource {
	Vector activitiesEntities = new Vector();

	public ProcessEntity(ItemCollection aItemCollection) {
		super(aItemCollection);
		setItemCategory("txtName", "Process Entity");
	}

	public List getActivityEntities() {
		return activitiesEntities;
	}

	public ActivityEntity createActivityEntity() {
		return createActivityEntity(null);
	}

	/**
	 * Diese Methode erzeugt ein neues ActivityEntity und ordnet dieses dem
	 * ProcessEntity zu. Es wird dazu eine ItemCollection mit den Attributen des
	 * ProzessEntity erwarte Die Methode kann auch Parameterlos aufgerufen
	 * werden. In diesem Fall erzeugt die Methode eine leere ItemCollection
	 * 
	 * @param itemCol
	 * @return ActivityEntity
	 */
	public ActivityEntity createActivityEntity(ItemCollection itemCol) {

		try {
			if (itemCol == null) {
				itemCol = new ItemCollection();
				// get current ProcessID
				int iProcessID = this.getItemCollection().getItemValueInteger(
						"numProcessID");
				itemCol.replaceItemValue("numProcessID",
						new Integer(iProcessID));
				itemCol.replaceItemValue("numNextProcessID", new Integer(
						iProcessID));
				itemCol.replaceItemValue("txtName", "[new Activity]");
				itemCol.replaceItemValue("numActivityID", new Integer(
						getFeeActivityID()));
			}

			/* check if the supported ID is free */
			int iPid = itemCol.getItemValueInteger("numProcessID");
			int iAid = itemCol.getItemValueInteger("numActivityID");
			if (!isFreeActivityID(iAid)) {
				// Activity ID blocked by another activity!
				System.out.println("ERROR createActivityEntity(): ID " + iPid
						+ "." + iAid + " allready defined!");
				return null;
			}

			/* Set defaultvalues */
			if ("".equals(itemCol.getItemValueString("keyOwnershipMode")))
				itemCol.replaceItemValue("keyOwnershipMode", "1");
			if ("".equals(itemCol.getItemValueString("keyAccessMode")))
				itemCol.replaceItemValue("keyAccessMode", "1");
			if ("".equals(itemCol.getItemValueString("keyVersion")))
				itemCol.replaceItemValue("keyVersion", "0");
			if ("".equals(itemCol.getItemValueString("keyArchive")))
				itemCol.replaceItemValue("keyArchive", "0");
			if ("".equals(itemCol.getItemValueString("keyScheduledActivity")))
				itemCol.replaceItemValue("keyScheduledActivity", "0");

		} catch (Exception e) {
			e.printStackTrace();
		}

		ActivityEntity activityEntity = new ActivityEntity(itemCol);
		activityEntity.setProcessEntity(this);
		activitiesEntities.add(activityEntity);
		getProcessTree().addWorkflowEntity(activityEntity);
		// resort existing Activities
		// resorting will also be processed after Property numactivityid chaned
		// in the
		// ActivityEntity Object. @See ActivityEntity.setPropertyValue()
		sortActivities();

		// create connections
		createConnections();
		return activityEntity;
	}

	/**
	 * helper method: resorts the entries of the activitiesEntities Vector
	 * dependend on the numActivityID.
	 */
	public void sortActivities() {

		Collections.sort(activitiesEntities, new Comparator() {
			public int compare(Object o1, Object o2) {
				ActivityEntity a1 = (ActivityEntity) o1;
				ActivityEntity a2 = (ActivityEntity) o2;

				// Ids ermitteln
				Integer id1 = new Integer(a1.getItemCollection()
						.getItemValueInteger("numActivityID"));
				Integer id2 = new Integer(a2.getItemCollection()
						.getItemValueInteger("numActivityID"));

				return (id1.compareTo(id2)); // c1.startTime.compareTo(c2.startTime);
			}
		});
	}

	/**
	 * Diese Methode dient dazu die verbindungen (Associations) zwischen dem
	 * aktuellen ProcessEntity und dem neu erzeugen ActivityEntities zu erzeugen
	 * Diese Methode wird ausschlie�lich von createActivityEntity() verwendet!
	 * Dadurch ist garantiert, das ein ActivityEntity genau eine Ein- und eine
	 * Aus- gehende Verbindung besitzt.
	 * 
	 * It is also possible that the target ProcessEntity is not in the same
	 * ProcessTree as the ActivityEntity. In this case a dummy connection will
	 * be created wich point to itself!
	 */
	private void createConnections() {
		try {
			Iterator iteratorActivityList = this.getActivityEntities()
					.iterator();
			while (iteratorActivityList.hasNext()) {
				ActivityEntity activityEntity = (ActivityEntity) iteratorActivityList
						.next();
				/*
				 * System.out.println("create Connections for " +
				 * activityEntity.getItemCollection()
				 * .getItemValueInteger("numProcessID") + "." +
				 * activityEntity.getItemCollection()
				 * .getItemValueInteger("numActivityID"));
				 */

				// Eingehende Verbindung neu erzeugen
				Association association = new Association();
				association.setFromEntity(this);
				association.setToEntity(activityEntity);

				// set Association to target ProcessEnty onyl if in same
				// ProcessTree
				if (activityEntity.getNextProcessEntity() != null) {
					// creat outgoing connection
					association = new Association();
					association.setFromEntity(activityEntity);
					association.setToEntity(activityEntity
							.getNextProcessEntity());
					// System.out.println("create association successfull");
				} else {
					// point to yourself
					// create dummy connection to yourself
					association = new Association();
					association.setFromEntity(activityEntity);
					association.setToEntity(activityEntity);
					// System.out.println("create dummy association successfull")
					// ;
				}

			}
		} catch (Exception e) {

		}
	}

	/**
	 * This Method returns the next free Activity ID. The Method is used to
	 * create empty ActivityEntities
	 * 
	 * @return
	 */
	private int getFeeActivityID() {
		int iCurrentID = -1;
		int iHighestID = -1;
		List list = getProcessTree().getActivityObjects(this);
		Iterator iter = list.iterator();
		// alle bekannten ActivityEnities pr�fen und die h�chste ProcessID
		// ermitteln
		while (iter.hasNext()) {
			try {
				AbstractWorkflowEntity entity = (AbstractWorkflowEntity) iter
						.next();
				iCurrentID = entity.getItemCollection().getItemValueInteger(
						"numActivityID");
				if (iCurrentID > iHighestID)
					iHighestID = iCurrentID;
			} catch (Exception e) {
			}
		}

		iCurrentID = iHighestID
				+ this.getProcessTree().getWorkflowModel().ID_INTERVAL
				- (iHighestID % getProcessTree().getWorkflowModel().ID_INTERVAL);
		return iCurrentID;
	}

	/**
	 * This Method checks if an Activity ID is unused
	 * 
	 * @return
	 */
	private boolean isFreeActivityID(int iID) {
		int iCurrentID = -1;
		List list = getProcessTree().getActivityObjects(this);
		Iterator iter = list.iterator();
		// alle bekannten ActivityEnities pr�fen und die h�chste ProcessID
		// ermitteln
		while (iter.hasNext()) {
			AbstractWorkflowEntity entity = (AbstractWorkflowEntity) iter
					.next();
			iCurrentID = entity.getItemCollection().getItemValueInteger(
					"numActivityID");
			if (iCurrentID == iID)
				return false;
		}

		return true;
	}

	/***************************************************************************
	 * Conection Hanlding
	 * 
	 * wird vollst�ndig von dem Activity Model Objekt geregelt
	 * 
	 **************************************************************************/

	/**
	 * Im folgenden wird eine neue Eingehende Verbindung gepr�ft. In diesem Fall
	 * wird das zugeh�rige Activity in Bezug auf seine numNextProcessID ge�ndert
	 */
	public void addAssociationIncoming(Association association) {
		try {
			AbstractWorkflowEntity processEntity = association.getToEntity();
			AbstractWorkflowEntity activityEntity = association.getFromEntity();
			int iProcessID = -1;
			// String sTitel="";
			iProcessID = processEntity.getItemCollection().getItemValueInteger(
					"numProcessID");
			// sTitel=entity.getItemCollection().getItemValueString("txtTitel");
			int iOldValue = activityEntity.getItemCollection()
					.getItemValueInteger("numNextProcessID");
			activityEntity.getItemCollection().replaceItemValue(
					"numNextProcessID", new Integer(iProcessID));
			activityEntity.firePropertyChange("numNextProcessID", new Integer(
					iOldValue), new Integer(iProcessID));
		} catch (Exception e) {

		}

		super.addAssociationIncoming(association);
	}

	/**
	 * This methode overrides the setPropertyValue method to handle changes of
	 * numProcessID. The Method reorganizes the processIDs of the connected
	 * activities If the new Value of the ProcessID is allredy defined by
	 * another processEntity the mthode returns without changing the value
	 */
	public void setPropertyValue(Object id, Object value) {

		// convert to number object?
		if (id.toString().toLowerCase().startsWith("num"))
			value = convertToNumber(value);

		// if equals - return
		Object oldValue = getPropertyValue(id.toString());
		if (oldValue == null)
			oldValue = "";
		if (oldValue != null && value != null && value.equals(oldValue))
			return;

		ActivityEntity activityEntity;
		/** check if the processID have changed.... * */
		if ("numprocessid".equalsIgnoreCase(id.toString())) {

			/** check if new processID already exists * */
			Collection colProcessTrees = this.getProcessTree()
					.getWorkflowModel().getProcessTrees();
			Iterator iterTrees = colProcessTrees.iterator();
			while (iterTrees.hasNext()) {
				ProcessTree pt = (ProcessTree) iterTrees.next();
				AbstractWorkflowEntity awe = pt.getWorkflowEntityFromMap(""
						+ value);
				// not equals?
				if (awe != null && !awe.equals(this)) {
					System.out.println("ProcessEntity - new ID conflict");
					return;
				}
			}

			/** update ModelMap **/
			this.getProcessTree().updateModelMap("" + oldValue, "" + value);

			/** Update all Activities belong to this ProcessEntity * */
			List currentActivityList = this.getActivityEntities();
			Iterator iter = currentActivityList.iterator();
			while (iter.hasNext()) {
				activityEntity = (ActivityEntity) iter.next();
				// System.out.println("processentity updates
				// activityentity...");
				activityEntity.setPropertyValue("numprocessid", value);
			}

			/**
			 * Check if an AcivityEntity has an outgoing connection to this
			 * ProcessEntity *
			 */

			List listProcesses = this.getProcessTree().getProcessEntities();
			 iter = listProcesses.iterator();
			while (iter.hasNext()) {
				ProcessEntity aProcessEnity = (ProcessEntity) iter.next();

//				System.out.println(" Updating processentity  "
//						+ aProcessEnity.getPropertyValue("numprocessid"));

				Iterator iterActivities = aProcessEnity.getActivityEntities()
						.iterator();
				while (iterActivities.hasNext()) {
					activityEntity = (ActivityEntity) iterActivities.next();
					// outgoing connection to this processEntity?
					
					int iNextID=activityEntity.getItemCollection().getItemValueInteger("numnextprocessid");
					
					if (oldValue.toString().equals(""+iNextID)) {
					
			//		if (activityEntity.getNextProcessEntity() == this) {

//						System.out
//								.println("processentity updates nextID of activityentity "
//										+ activityEntity
//												.getPropertyValue("numprocessid")
//										+ "."
//										+ activityEntity
//												.getPropertyValue("numactivityid"));
//
//						System.out.println(" old numnextprocessid="
//								+ activityEntity
//										.getPropertyValue("numnextprocessid"));

//						activityEntity.setPropertyValue("numnextprocessid",
//								value);
						
						
						try {
							activityEntity.getItemCollection().replaceItemValue("numnextprocessid",
									value);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		} // end of id==numprocessid
		super.setPropertyValue(id, value);

	}

}
