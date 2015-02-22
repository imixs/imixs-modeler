

package org.imixs.eclipse.workflowmodeler.actions;

import java.util.List;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.ProcessTree;
import org.imixs.eclipse.workflowmodeler.ui.editparts.ActivityEntityEditPart;
import org.imixs.eclipse.workflowmodeler.ui.editparts.ModelTreeEditPart;
import org.imixs.eclipse.workflowmodeler.ui.editparts.ProcessEntityEditPart;

/**
 * Provides a context menu for the graphical WorkflowModel editor.
 * 
 * @author Ralph Soika
 */
public class WorkflowEntityContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;


	private EditPartViewer viewer;

	/**
	 * Creates a new WorkflowEntityContextMenuProvider assoicated with the given viewer
	 * and action registry.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param registry
	 *            the action registry
	 */
	public WorkflowEntityContextMenuProvider(EditPartViewer viewer,
			ActionRegistry registry) {
		super(viewer);
		setActionRegistry(registry);
		this.viewer = viewer;
		
	}

	/**
	 * This method creates the menu. depended from the current selection entries will be disabled
	 */
	public void buildContextMenu(IMenuManager menu) {

		GEFActionConstants.addStandardActionGroups(menu);

		// Check if onyl onw ProcessEntityPart was selected
		List list = viewer.getSelectedEditParts();
		if (list.size()==1) {
			EditPart editPart = (EditPart)list.get(0);
			if (editPart instanceof ProcessEntityEditPart) {
				ProcessEntityEditPart processEntityEditPart = (ProcessEntityEditPart) editPart;

				
				// Add Create Acctivity Action
				menu.add(new CreateActivityAction(processEntityEditPart.getProcessEntity() ));
				// Collapse bzw. Expand anzeigen
				if (((ProcessEntityEditPart) editPart).isExpanded())
					menu.add(new CollapseProcessAction(processEntityEditPart ));
				else
					menu.add(new ExpandProcessAction(processEntityEditPart ));
			}
			
			
			// Add copy paste support
			if (editPart instanceof ProcessEntityEditPart || editPart instanceof ActivityEntityEditPart ) {
				
				// Add Create Acctivity Action
				if (editPart instanceof ProcessEntityEditPart) {
					ProcessEntityEditPart processEntityEditPart = (ProcessEntityEditPart) editPart;
					menu.add(new CopyModelObjectAttributesAction(processEntityEditPart.getProcessEntity() ));
					// paste Action is disabled if no EntityCopy is available
					Action ac=new PasteModelObjectAttributesAction(processEntityEditPart.getProcessEntity()  );
					ac.setEnabled(WorkflowmodelerPlugin.getPlugin().getEntityCopy()!=null);
					menu.add(ac);
				}
				if (editPart instanceof ActivityEntityEditPart) {
					ActivityEntityEditPart activityEntityEditPart = (ActivityEntityEditPart) editPart;
					menu.add(new CopyModelObjectAttributesAction(activityEntityEditPart.getActivityEntity() ));
					// paste Action is disabled if no EntityCopy is available
					Action ac=new PasteModelObjectAttributesAction(activityEntityEditPart.getActivityEntity() );
					ac.setEnabled(WorkflowmodelerPlugin.getPlugin().getEntityCopy()!=null);
					menu.add(ac);
				}			
				
			}
		
		}

		/* Add Undo and Redo Actions */
		IAction action;
		action = getActionRegistry().getAction(
				org.eclipse.ui.actions.ActionFactory.UNDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(
				org.eclipse.ui.actions.ActionFactory.REDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(
				org.eclipse.ui.actions.ActionFactory.DELETE.getId());
		
		
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		
		// add CopyAllEntities Action		
		EditPart editPart =	viewer.getFocusEditPart();
		ProcessTree processTree=null;
		// find the corresponding ProcessTree ModelObject		
		if (editPart instanceof ProcessEntityEditPart) {
			ProcessEntityEditPart processEntityEditPart = (ProcessEntityEditPart) editPart;
			processTree=processEntityEditPart.getProcessEntity().getProcessTree();
		}
		if (editPart instanceof ActivityEntityEditPart) {
			ActivityEntityEditPart activityEntityEditPart = (ActivityEntityEditPart) editPart;
			processTree=activityEntityEditPart.getActivityEntity().getProcessTree();
		}
		if (editPart instanceof ModelTreeEditPart) {
			ModelTreeEditPart treeEditPart = (ModelTreeEditPart) editPart;
			processTree=(ProcessTree) treeEditPart.getModel();
		}
		if (processTree!=null) {
			menu.add(new CopyAllModelEntitiesAction(processTree));
			// paste Action is disabled if no ProcessTreeCopy is available
			Action ac=new PasteAllModelEntitiesAction(processTree);
			boolean bEnabled=(WorkflowmodelerPlugin.getPlugin().getProcessTreeCopy()!=null);
			// also disable if same processTree 
			if (WorkflowmodelerPlugin.getPlugin().getProcessTreeCopy()==processTree)
				bEnabled=false;
			ac.setEnabled(bEnabled);
			menu.add(ac);
			
		}
	}

	private ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	/**
	 * Sets the action registry
	 * 
	 * @param registry
	 *            the action registry
	 */
	public void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

}
