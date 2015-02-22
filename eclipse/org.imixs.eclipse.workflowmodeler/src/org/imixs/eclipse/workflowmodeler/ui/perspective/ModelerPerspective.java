
package org.imixs.eclipse.workflowmodeler.ui.perspective;

import org.eclipse.ui.*;

/**
 * The Imixs IX Workflow Modeler Perspective
 * 
 * @author Ralph Soika
 */
public class ModelerPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
		defineLayout(layout);
	}

	public void defineActions(IPageLayout layout) {
		// Add "new wizards".
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");

		// Add "show views".
		layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
		// .ID_RES_NAV
		layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
	}

	public void defineLayout(IPageLayout layout) {
		// Editors are placed for free.
		String editorArea = layout.getEditorArea();

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,(float) 0.20, editorArea);
		//left.addView(IPageLayout.ID_RES_NAV);
		left.addView(IPageLayout.ID_PROJECT_EXPLORER);
		
		IFolderLayout leftbottom = layout.createFolder("left_bottom", IPageLayout.BOTTOM,(float) 0.60, "left");
		//leftbottom.addView(IPageLayout.ID_OUTLINE);
		leftbottom.addView("org.imixs.eclipse.workflowmodeler.viewer.WorkflowModelView");
		
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM,(float) 0.60, editorArea);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(IPageLayout.ID_TASK_LIST);

	}

}





