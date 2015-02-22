package org.imixs.eclipse.workflowmodeler.rest;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.imixs.eclipse.workflowmodeler.ServerConnector;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.Configuration;
import org.imixs.eclipse.workflowmodeler.model.WorkflowModel;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.services.rest.RestClient;

/**
 * This class supports function for download and upload a model from a REST
 * service endpoint. The URI to be used is
 * stored in the environment ENVIRONMENT_WEBSERVICE
 * 
 * This Environment can be configured by the WorkflowModelWebServicePage
 * 
 * This ServiceConnector provides the multi-model feature. Upload is performed
 * by using spcific modelversion. 
 * 
 * @see org.imixs.eclipse.workflowmodeler.ui.editors.WorkflowModelWebServicePage
 * @author Ralph Soika
 */
public class RestServiceConnector implements ServerConnector {
	private WorkflowModel workflowModel = null;

	private RestClient restClient;

	//private String sModelID;

	//private String sUniqueID;

	private String sUserID,sURI;

	/**
	 * This Method initialize JAX-RCP Web Service Client
	 * 
	 * The ServerConnector could be improved. Feel free to post your thoughts!
	 * 
	 */
	public boolean initializeService() {
		ItemCollection webServiceConfiguration;
		 
		String sPassword;
		try {
			/** * Login Dialog to get UserID and Password ** */
			Shell shell = getShell();
			LoginDialog loginDlg = new LoginDialog(shell);
			if (loginDlg.open() != LoginDialog.OK)
				return false; // cancel login!
			sUserID = loginDlg.getSecurityPrinzipal();
			sPassword = loginDlg.getSecurityCredentials();

			// read configuration environment for the webservice endpoint
			Configuration configurationWebService = workflowModel
					.getEnvironment(WorkflowmodelerPlugin.ENVIRONMENT_PROFILE);
			if (configurationWebService == null) {
				System.out
						.println("[RestServiceConnector] initializeService error: no Envionment settings found");
				webServiceConfiguration = new ItemCollection();
				configurationWebService = new Configuration(
						WorkflowmodelerPlugin.ENVIRONMENT_PROFILE,
						webServiceConfiguration);
				workflowModel.addEnvironment(configurationWebService);
			}

			webServiceConfiguration = configurationWebService
					.getItemCollection();

			sURI = webServiceConfiguration
					.getItemValueString("txtWebServiceLocation");

			System.out.println("[RestServiceConnector] " + sURI);

			restClient = new RestClient();
			restClient.setCredentials(sUserID,sPassword);
		} catch (Exception e) {

			System.out
					.println("[RestServiceConnector] initializeService failed!");

			MessageDialog.openInformation(getShell(), "WebServiceConnector",
					"Initialize WebService failed! \n\n" + e.getMessage());

			e.printStackTrace();
			return false;

		}
		return true;
	}

	/**
	 * Downloads a model from a webservice and storese the model into a file.
	 * 
	 * @param aworkflowModel
	 * @param afile
	 */
	public void downloadModel(WorkflowModel aworkflowModel, IFile afile)
			throws Exception {

		workflowModel = aworkflowModel;

		// not implemented
		
		System.out.println("NOT IMPLEMENTED");
		if (1==1)
			throw new Exception ("downloadModel not yet implemented");
		// Initialize Web Service
		if (initializeService() == false)
			throw new Exception("Error inizialize WebService");

		/***********************************************************************
		 * JOB START
		 **********************************************************************/
		/*
		final DownloadJob job = new DownloadJob("downloading model...",
				workflowModel, restClient, afile);

		job.setUser(true);
		job.schedule();
	
		if (job.getState() == IStatus.OK) {
			// no op
		} else {
			// no op
		}
		*/
	}

	/**
	 * This method saves a model up to a Imixs JEE Rest Service endpoint
	 * 
	 * The ProcessEntities and ActivityEntities a crated with an "txtmodelid"
	 * which is used as an Index inside the Database. ProcessEntities are always
	 * sorted with 4 digits e.g. 0001 , 0010, 0100, ... ActivityEntities are
	 * always sortes with 3 diggits and an pr�fix e.g. 0001-010, 0010-200, ...
	 * 
	 * @param workflowModel
	 */
	public void uploadModel(WorkflowModel aworkflowModel) throws Exception {
		workflowModel = aworkflowModel;
		// Initialize Web Service
		if (initializeService() == false)
			throw new Exception("Error inizialize WebService");
 
		final UploadJobMultiModel job = new UploadJobMultiModel("uploading workflowmodel...",
				workflowModel,sURI, restClient); 
		job.setUser(true);
		job.schedule();
	}
 
	private Shell getShell() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getShell();
	}

}