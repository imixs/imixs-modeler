package org.imixs.eclipse.workflowmodeler.rest;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.Configuration;
import org.imixs.eclipse.workflowmodeler.model.ProcessEntity;
import org.imixs.eclipse.workflowmodeler.model.ProcessTree;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.services.WorkflowModelService;
import org.imixs.workflow.services.dataobjects.Entity;
import org.imixs.workflow.services.dataobjects.EntityAdapter;
import org.imixs.workflow.services.dataobjects.EntityCollection;
import org.imixs.eclipse.workflowmodeler.model.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.widgets.*;

/**
 * This Job imports a model from a webservice and stores the model into a IFile
 * object.
 * 
 * 
 * @author Ralph Soika
 * 
 */
public class DownloadJob extends Job implements IJobChangeListener {
	WorkflowModel workflowModel;

	Display display;
 
	WorkflowModelService workflowModelService;

	IFile file;

	

	public DownloadJob(String aName, WorkflowModel amodel,
			WorkflowModelService aWorkflowModelService, IFile afile) {
		super(aName);
		file = afile;
		
		workflowModel = amodel;
		workflowModelService = aWorkflowModelService;
		// display=adisplay;

		this.addJobChangeListener(this);

	}

	protected IStatus run(IProgressMonitor monitor) {
		try {
			ItemCollection itemCol = null;
			Collection collProcessList =null;
			ProcessTree processTree = null;
			monitor.beginTask("upload model", 6);
			monitor.worked(1);
			// First remove all existing process Trees
			Iterator iterProcessTrees = workflowModel.getProcessTrees()
					.iterator();
			Vector vPList = new Vector();
			while (iterProcessTrees.hasNext()) {
				processTree = (ProcessTree) iterProcessTrees.next();
				vPList.add(processTree.getName());
			}
			for (int i = 0; i < vPList.size(); i++)
				workflowModel.removeProcessTree(vPList.elementAt(i).toString());
			monitor.worked(1);
			
			
			/** Convert EntityCOlleciton into Vector of ItemCollections... **/
			EntityCollection entCol= workflowModelService.getProcessList();
			collProcessList=this.buildCollection(entCol);
			
			
	
			monitor.worked(1);
			if (collProcessList != null) {

				// now add Process Trees and process Entities...

				Iterator it = collProcessList.iterator();
				while (it.hasNext()) {
					//itemCol = new ItemCollection((Map) it.next());
					itemCol = (ItemCollection) it.next();
					
					
					
					String sProcessGroup = itemCol
							.getItemValueString("txtWorkflowGroup");

					// Check if ProcessTree already exists
					processTree = workflowModel.getProcessTree(sProcessGroup);
					if (processTree == null) {
						// add new Process Tree
						processTree = new ProcessTree(sProcessGroup);

						// display.asyncExec(new Runnable() { public void run()
						// {
						workflowModel.addProcessTree(processTree);
						// } });
					}
					// add PrecessEntity
					processTree.createProcessEntity(itemCol);
				}

				/***************************************************************
				 * Jetzt nochmal durchgehen und die zugeh�rigen Activityies
				 * anlegen da jetzt alle Verbindungen hergestellt werden k�nnen
				 * m�ssen!
				 */

				it = collProcessList.iterator();
				while (it.hasNext()) {
					//itemCol = new ItemCollection((Map) it.next());
					itemCol = (ItemCollection) it.next();
					int iProcessID = itemCol
							.getItemValueInteger("numprocessid");

					// search for ProcessTree and ProcessEntity
					ProcessEntity processEntity = getProcessEntity(iProcessID);
					if (processEntity != null) {
						// Activities hohlen
						EntityCollection entColAct = workflowModelService
								.getActivityList(iProcessID);

						
						
						Collection colActivityList=this.buildCollection(entColAct);
						// System.out.println("..found " +
						// colActivityList.size() +
						// " activitries");
						Iterator itActivitys = colActivityList.iterator();
						while (itActivitys.hasNext()) {
						//	itemCol = new ItemCollection((Map) itActivitys.next());
							itemCol =(ItemCollection) itActivitys.next();
							processEntity.createActivityEntity(itemCol);
						}
					}
				}
			}
			monitor.worked(1);
			// download Environments
			EntityCollection entcolenv = workflowModelService.getEnvironmentList();
			
			Collection colEnvList=this.buildCollection(entcolenv);
			if (colEnvList != null) {
				Iterator it = colEnvList.iterator();
				while (it.hasNext()) {
					//itemCol = new ItemCollection((Map) it.next());
					itemCol = ( ItemCollection) it.next();
					String sName = itemCol.getItemValueString("txtName");
					workflowModel.addEnvironment(new Configuration(sName,
							itemCol));
				}
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
		} catch (Exception downloadException) {
			downloadException.printStackTrace();
			org.eclipse.core.runtime.Status status = new org.eclipse.core.runtime.Status(
					IStatus.ERROR,
					org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin.PLUGIN_ID,
					-1, "Download Model failed!\n\n"
							+ downloadException.toString(), downloadException);
			return status;
		}
	}

	/*
	 * 
	 * display.asyncExec(new Runnable() { public void run() { } });
	 * 
	 */
	protected Action getReservationCompletedAction() {
		return new Action("View reservation status") {
			public void run() {
				MessageDialog.openInformation(getShell(), "Download Complete",
						"Download of the workflow model has been completed");
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

	/**
	 * This Helper Methode searches the right processEntity defined by aID in
	 * all exisitng ProcessTrees
	 * 
	 * @param aID
	 * @return
	 */
	private ProcessEntity getProcessEntity(int aID) {
		ProcessEntity processentity = null;

		Iterator iterProcessTrees = workflowModel.getProcessTrees().iterator();
		while (iterProcessTrees.hasNext()) {
			ProcessTree ptree = (ProcessTree) iterProcessTrees.next();
			try {
				processentity = ptree.getProcessEntityModelObject(aID);
			} catch (Exception ee) {
				// no op
			}
			if (processentity != null)
				return processentity;

		}
		return null;
	}

	public void aboutToRun(IJobChangeEvent event) {

	}

	public void awake(IJobChangeEvent event) {

	}

	public void done(IJobChangeEvent event) {

		WorkflowmodelerPlugin.getPlugin().saveWorkflowModel(workflowModel,
				file, null);

	}

	public void running(IJobChangeEvent event) {

	}

	public void scheduled(IJobChangeEvent event) {

	}

	public void sleeping(IJobChangeEvent event) {

	}
	
	
	private Collection buildCollection(EntityCollection entCol) {
		Vector v=new Vector();
		
		if (entCol==null || entCol.getEntities()==null || entCol.getEntities().length==0)
			return v;
		
		for (int i=0;i<entCol.getEntities().length;i++) {
			Entity ent=entCol.getEntities(i);
			ItemCollection itcol=EntityAdapter.getItemCollection(ent);
			v.add(itcol);
		}
		return v;
		
	}

}
