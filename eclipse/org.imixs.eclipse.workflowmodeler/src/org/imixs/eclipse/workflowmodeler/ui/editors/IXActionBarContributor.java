package org.imixs.eclipse.workflowmodeler.ui.editors;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Manages the installation/deinstallation of global actions for multi-page
 * editors. Responsible for the redirection of global actions to the active
 * editor. Multi-page contributor replaces the contributors for the individual
 * editors in the multi-page editor.
 */
public class IXActionBarContributor extends ActionBarContributor {
	private IEditorPart activeEditorPart;
	private Action sampleAction;

	protected void buildActions() {
		addRetargetAction(new DeleteRetargetAction());
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
	}

	protected void declareGlobalActionKeys() {
		// currently none
	}

	/**
	 * Creates a multi-page contributor.
	 */
	public IXActionBarContributor() {
		super();
		createActions();
	}

	/**
	 * Returns the action registed with the given text editor.
	 * 
	 * @return IAction or null if editor is null.
	 */
	protected IAction getAction(ITextEditor editor, String actionID) {
		return (editor == null ? null : editor.getAction(actionID));
	}

	private void createActions() {

		sampleAction = new Action() {
			public void run() {
				MessageDialog.openInformation(null, "Actiontest Plug-in",
						"Sample Action Executed");

				/*
				 * IWorkspace workspace=ResourcesPlugin.getWorkspace(); IProject
				 * project=workspace.getRoot().getProject(); Object[]
				 * elements=new Object[1]; elements[0]=project;
				 * StructuredSelection selection=new
				 * StructuredSelection(elements);
				 * 
				 * NewModel wizard=new NewModel(); Workbench
				 * workbench=Workbench.getInstance();
				 * 
				 * ResourcesPlugin.getWorkspace().get
				 * wizard.init(workbench,selection); WizardDialog dialog=new
				 * WizardDialog
				 * (workbench.getActiveWorkbenchWindow().getShell().g,wizard);
				 * dialog.open();
				 */
			}
		};
		sampleAction.setText("Sample Action");
		sampleAction.setToolTipText("Sample Action tool tip");
		sampleAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						IDE.SharedImages.IMG_OBJS_TASK_TSK));
	}

	public void contributeToMenu(IMenuManager manager) {
	    super.contributeToMenu(manager);

	    //IMenuManager menu = new MenuManager("Editor &Menu");
		//manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
		//menu.add(sampleAction);
		
	    MenuManager viewMenu = new MenuManager("&View");
	    viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
	    viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
	    manager.insertAfter(IWorkbenchActionConstants.M_EDIT, viewMenu);
	
	}

	public void contributeToToolBar(IToolBarManager manager) {
		super.contributeToToolBar(manager);
		manager.add(new Separator());

		manager.add(getAction(ActionFactory.UNDO.getId()));
		manager.add(getAction(ActionFactory.REDO.getId()));

		// manager.add(sampleAction);
		manager.add(new ZoomComboContributionItem(getPage()));
		
	}
}
