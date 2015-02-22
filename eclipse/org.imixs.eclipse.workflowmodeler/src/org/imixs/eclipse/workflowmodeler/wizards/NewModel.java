package org.imixs.eclipse.workflowmodeler.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.ProcessTree;
import org.imixs.eclipse.workflowmodeler.model.WorkflowModel;

/**
 * Wizard Page to create a new Model inside a model Project
 * 
 * @author Ralph Soika
 */
public class NewModel extends Wizard implements INewWizard {
	private ModelWizardPage modelPage;
	private WizardNewFileCreationPage filePage;
	private IWorkbench workbench;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench aworkbench, IStructuredSelection aselection) {
		workbench = aworkbench;
		setWindowTitle("Imixs Workflow Model Wizard");
		setDefaultPageImageDescriptor(WorkflowmodelerPlugin.getPlugin()
				.getIcon("wizard/wizard.gif"));
		setNeedsProgressMonitor(true);

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		String sFileName = filePage.getFileName();

		if (sFileName != null && !"".equals(sFileName)) {
			if (!sFileName.endsWith(".ixm")) {
				sFileName += ".ixm";
				filePage.setFileName(filePage.getFileName() + ".ixm");
			}
			IFile file = filePage.createNewFile();

			WorkflowModel model = new WorkflowModel(sFileName);
			ProcessTree process = new ProcessTree(modelPage.getProcessName());
			model.addProcessTree(process);
			WorkflowmodelerPlugin.getPlugin().saveWorkflowModel(model, file,
					null);

		}
		return true;
	}

	public void addPages() {

		IStructuredSelection currentSelection = (IStructuredSelection) workbench
				.getActiveWorkbenchWindow().getSelectionService()
				.getSelection();

		filePage = new WizardNewFileCreationPage("Model File", currentSelection);

		modelPage = new ModelWizardPage("ModelPage");
		modelPage.setTitle("Imixs Workflow Modeler");
		modelPage.setDescription("Create a new Imixs Workflow Model");

		addPage(filePage);
		addPage(modelPage);
	}
}
