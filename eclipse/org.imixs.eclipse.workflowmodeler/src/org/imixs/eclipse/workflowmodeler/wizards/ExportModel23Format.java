package org.imixs.eclipse.workflowmodeler.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.XMLModelParser;
import org.imixs.eclipse.workflowmodeler.model.WorkflowModel;

public class ExportModel23Format extends Wizard implements IExportWizard {
	private IStructuredSelection selection;
	private WizardNewFileCreationPage filepage;
	private IFile sourceFile = null;
	private IFile targetFile = null;

	public void init(IWorkbench workbench, IStructuredSelection aselection) {
		selection = aselection;
		setWindowTitle("Imixs Workflow Model Export Wizard");
		setDefaultPageImageDescriptor(WorkflowmodelerPlugin.getPlugin()
				.getIcon("wizard/wizard.gif"));
		setNeedsProgressMonitor(true);

		Object o = selection.getFirstElement();

		if (o instanceof IFile) {
			sourceFile = (IFile) o;
		}
	}

	public void addPages() {
		filepage = new WizardNewFileCreationPage("filePage", selection);
		filepage.setDescription("This Export Wizard will export the current Model File into the Version 2.3.x Format. Please specify the location of the file export.");
		addPage(filepage);
	}

	public boolean performFinish() {
		System.out.println("[ExportModel23Format] export model in old 2.3 format....");

		if (sourceFile != null) {
			try {

				// create model file
				System.out.println("[ExportModel23Format] Targetfile: "
						+ sourceFile.getName());

				String sFileName = filepage.getFileName();
				if (!sFileName.endsWith(".ixm")) {
					sFileName += ".ixm";
					filepage.setFileName(sFileName);

				}

				// Create new file and save the file with the old format
				targetFile = filepage.createNewFile();

				try {
					// puts the data into a database ...
					getContainer().run(true, true, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask("Export File", 100);
							monitor.worked(40);

							// store data here ...
							WorkflowModel aModel = WorkflowmodelerPlugin
									.getPlugin().loadWorkflowModel(sourceFile);

							ByteArrayOutputStream out = XMLModelParser
									.transformModel(aModel);
							try {
								targetFile.setContents(
										new ByteArrayInputStream(out
												.toByteArray()), true, true,
										null);
								out.close();
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// clear Dirty Flag
							aModel.clearDirtyFlag();

							Thread.sleep(200);
							monitor.done();
						}
					});
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				System.out.println("[ExportModel23Format] export successfull");
			} catch (Exception e) {
				System.out
						.println("[ExportModel23Format] unable to export file format 2.3");
				e.printStackTrace();
				return false;
			}

		} else
			System.out
					.println("[ExportModel23Format] no Imixs Model File selected! ");

		return true;
	}

}
