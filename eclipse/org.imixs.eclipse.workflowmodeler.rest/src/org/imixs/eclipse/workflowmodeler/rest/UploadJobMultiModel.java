package org.imixs.eclipse.workflowmodeler.rest;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.ActivityEntity;
import org.imixs.eclipse.workflowmodeler.model.Configuration;
import org.imixs.eclipse.workflowmodeler.model.ProcessEntity;
import org.imixs.eclipse.workflowmodeler.model.ProcessTree;
import org.imixs.eclipse.workflowmodeler.model.WorkflowModel;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.services.rest.RestClient;
import org.imixs.workflow.xml.EntityCollection;
import org.imixs.workflow.xml.XMLItemCollection;
import org.imixs.workflow.xml.XMLItemCollectionAdapter;

/**
 * This uploadJob synchronized the model object with a rest service. The
 * expected WebService Implementation is based on the Imixs JEE Workflow Version
 * 2.0.2 or grater. This Version provides a multi-version Model. So same model
 * can be uploaded in different Versionnumbers. The Version Number is read form
 * the Environment Profile. See also details about the Imixs Rest Service on
 * www.imixs.org
 * 
 * The UploadJob adds the current version number of the model to all objects
 * before uploded. The current version number is read from the environment
 * object WorkflowmodelerPlugin.ENVIRONMENT_WEBSERVICE The version number will
 * be stored into the fied $modelversion of each model object (processEntity,
 * Activityentity and EnvironmentEntity) The Version number can be used by a
 * model manager to store different versions of a model. This feature depends on
 * the implementation of a modelManager
 * 
 * 
 * Before the Upload procedure starts the Job removes any existing Model Entity
 * for the corresponding ModelVersion
 * 
 * Notice! The UploadJob depends on the Imixs JEE Workflow Version 2.0.2 or
 * higher.
 * 
 * @author rsoika
 * 
 */
public class UploadJobMultiModel extends Job {
	WorkflowModel workflowModel;
	RestClient restClient;
	String modelVersion = "";
	String sURI;
	String httpErrorMessage = "";

	public UploadJobMultiModel(String aName, WorkflowModel amodel, String aURI,
			RestClient aClient) {
		super(aName);
		workflowModel = amodel;
		sURI = aURI;
		restClient = aClient;
	}

	protected IStatus run(IProgressMonitor monitor) {

		try {
			// So some work
			ProcessEntity processEntity;
			ActivityEntity activityEntity;
			ProcessTree processTree;

			// Anzahl der ProzessEntties messen

			int iProcesses = 0;
			Iterator iteratorcount = workflowModel.getProcessTrees().iterator();
			while (iteratorcount.hasNext()) {
				processTree = (ProcessTree) iteratorcount.next();
				iProcesses += processTree.getProcessEntities().size();
			}

			monitor.beginTask("upload model", (iProcesses * 2) + 6);
			monitor.worked(1);

			// compute current version number...
			try {
				ItemCollection itemColProfile = workflowModel.getEnvironment(
						WorkflowmodelerPlugin.ENVIRONMENT_PROFILE)
						.getItemCollection();
				modelVersion = itemColProfile
						.getItemValueString("txtWorkflowModelVersion");
			} catch (Exception ee) {
				// create a default model version number
				modelVersion = "1.0.0";
			}

			monitor.worked(1);

			/*
			 * simply create a collecction containg all ProcessEntities,
			 * ActivityEntities and Environment Entities.
			 */
			Iterator iteratorProcessTrees = workflowModel.getProcessTrees()
					.iterator();

			Vector vector = new Vector();
			while (iteratorProcessTrees.hasNext()) {
				// load ProcessTree
				processTree = (ProcessTree) iteratorProcessTrees.next();
				Iterator iteratorProcesses = processTree.getProcessEntities()
						.iterator();
				monitor.worked(1);

				while (iteratorProcesses.hasNext()) {
					processEntity = (ProcessEntity) iteratorProcesses.next();
					processEntity.getItemCollection().replaceItemValue(
							"$modelversion", modelVersion);
					processEntity.getItemCollection().replaceItemValue("type",
							"ProcessEntity");
					vector.add(processEntity.getItemCollection());

					// add all coresponding activities
					Iterator iterAcvities = processEntity.getActivityEntities()
							.iterator();
					while (iterAcvities.hasNext()) {
						activityEntity = (ActivityEntity) iterAcvities.next();
						activityEntity.getItemCollection().replaceItemValue(
								"$modelversion", modelVersion);
						activityEntity.getItemCollection().replaceItemValue(
								"type", "ActivityEntity");
						vector.add(activityEntity.getItemCollection());
						monitor.worked(1);
					}
				}
			}
			monitor.worked(1);
			// add all Environment entities...
			Iterator iteratorEnvironment = workflowModel.getEnvironments()
					.iterator();

			while (iteratorEnvironment.hasNext()) {
				Configuration config = (Configuration) iteratorEnvironment
						.next();
				ItemCollection itemCol = config.getItemCollection();
				itemCol.replaceItemValue("txtName", config.getName());
				itemCol.replaceItemValue("$modelversion", modelVersion);
				itemCol.replaceItemValue("type", "WorkflowEnvironmentEntity");

				vector.add(itemCol);
				monitor.worked(1);
			}

			// now we have the full collection!
			monitor.worked(1);

			// build an entityCollection
			EntityCollection entCol = this.buildEntityCollection(vector);

			// post the model to the provided location with additional model
			// version
			int iHTTPResult = restClient.postCollection(sURI + "/"
					+ modelVersion, entCol);

			System.out.println("[RestServiceConnector] post model: result="
					+ iHTTPResult);

			if (iHTTPResult < 200 || iHTTPResult > 299) {

				if (iHTTPResult == 404)
					httpErrorMessage = "The requested resource could not be found. Please verifiy your web service location.";
				else if (iHTTPResult == 403)
					httpErrorMessage = "The username/password you entered were not correct. Your request was denied as you have no permission to access the server. Please try again.";
				else
					httpErrorMessage = "The model data could not be uploaded to the workflow server. Please verifiy your server settings. HTTP Result="
							+ iHTTPResult;
				throw new Exception(httpErrorMessage);

			}

			monitor.worked(1);

			if (isModal(this)) {
				// The progress dialog is still open so
				// just open the message
				// showResults();

			} else {
				setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
				setProperty(IProgressConstants.ACTION_PROPERTY,
						getReservationCompletedAction());
			}

			return Status.OK_STATUS;
		} catch (Exception uploadException) {

			uploadException.printStackTrace();
			org.eclipse.core.runtime.Status status = new org.eclipse.core.runtime.Status(
					IStatus.ERROR,
					org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin.PLUGIN_ID,
					-1, "Upload Model failed!\n\n"
							+ uploadException.getMessage(), uploadException);
			return status;

		}
	}

	/*
	 * 
	 * display.asyncExec(new Runnable() { public void run() { } });
	 */
	protected Action getReservationCompletedAction() {
		return new Action("View Upload status") {
			public void run() {
				MessageDialog.openInformation(getShell(), "Upload Complete",
						"Upload of the workflow model has been completed");
			}
		};
	}

	public boolean isModal(Job job) {
		Boolean isModal = (Boolean) job
				.getProperty(IProgressConstants.PROPERTY_IN_DIALOG);
		if (isModal == null)
			return false;
		return isModal.booleanValue();
	}

	private Shell getShell() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getShell();
	}

	private EntityCollection buildEntityCollection(Collection entCol)
			throws Exception {
		EntityCollection entcol = new EntityCollection();
		if (entCol == null || entCol.size() == 0)
			return entcol;
		int iMax = entCol.size();

		XMLItemCollection[] entyArray = new XMLItemCollection[iMax];

		int i = 0;
		Iterator iter = entCol.iterator();
		while (iter.hasNext()) {
			ItemCollection itemCol = (ItemCollection) iter.next();
			XMLItemCollection enti = XMLItemCollectionAdapter
					.putItemCollection(itemCol);
			entyArray[i] = enti;
			i++;
		}
		entcol.setEntity(entyArray);

		return entcol;

	}

}
