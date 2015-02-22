package org.imixs.eclipse.workflowmodeler.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.actions.CopyAllModelEntitiesAction;
import org.imixs.eclipse.workflowmodeler.actions.WorkflowEntityContextMenuProvider;
import org.imixs.eclipse.workflowmodeler.model.ActivityEntity;
import org.imixs.eclipse.workflowmodeler.model.Association;
import org.imixs.eclipse.workflowmodeler.model.ProcessEntity;
import org.imixs.eclipse.workflowmodeler.model.ProcessTree;
import org.imixs.eclipse.workflowmodeler.ui.editparts.ModelTreeEditPart;
import org.imixs.eclipse.workflowmodeler.ui.editparts.ModelTreeEditPartFactory;
import org.imixs.eclipse.workflowmodeler.ui.editparts.ProcessEntityEditPart;

/**
 * @author Ralph Soika GraphicalEditorWithFlyoutPalette
 *         org.eclipse.gef.ui.parts.GraphicalEditorWithPalette
 * 
 */
public class IXGraphicalEditor extends
		org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette implements
		PropertyChangeListener, ITabbedPropertySheetPageContributor {

	
	private PaletteRoot paletteRoot;
/*
	static ProcessTree processTree;

	static ProcessEntity currentProcessEntity = null;
*/
	private ProcessTree processTree;

	private ProcessEntity currentProcessEntity = null;

	public IXGraphicalEditor() {
		setEditDomain(new DefaultEditDomain(this));

		WorkflowmodelerPlugin.getPlugin().addPropertyChangeListener(this);

	}
	
	
	
	/*

	protected void createActions() {
		// TODO Auto-generated method stub
		super.createActions();
		
		IAction action = new CopyAllModelEntitiesAction();
		getActionRegistry().registerAction(action);
		getSelectionActions().add(action.getId());
		
		
		
	}

	*/










	/**
	 * returns the current processentity.
	 * 
	 * @return
	 */
//	public static ProcessEntity getCurrentProcessEntity() {
	public  ProcessEntity getCurrentProcessEntity() {
		if (currentProcessEntity == null)
			currentProcessEntity = (ProcessEntity) processTree
					.getProcessEntities().get(0);
		return currentProcessEntity;
	}

	public void setCurrentProcessEntity(ProcessEntity aentity) {
		currentProcessEntity = aentity;
	}

	/**
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart,
	 *      ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object o = structuredSelection.getFirstElement();
			
			if (o instanceof ProcessEntityEditPart) {
				ProcessEntityEditPart editPart = (ProcessEntityEditPart) o;
				this.setCurrentProcessEntity(editPart.getProcessEntity());
			}
		}
	}

	protected void setInput(IEditorInput input) {
		// Model anzeigen
		processTree = ((EditorInput) input).getProcessTree();

		// resort ProcessTree
		processTree.sortProcessEntities();

		super.setInput(input);

		// show WorkflowModell name + ProcessTree name
		this.setPartName(processTree.getWorkflowModel().getName() + ": "
				+ input.getName());
	}

	/**
	 * provide a standard PalletViewerProvider
	 */
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain());

	}

	/**
	 * configures the editors context menues
	 */
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		// create ScalableFreeformRootEditPart to suppot ZoomManager
		ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
		getGraphicalViewer().setRootEditPart(root);
		getGraphicalViewer().setEditPartFactory(new ModelTreeEditPartFactory());

		// create context menu for the Entities displayed in the Editor
		ContextMenuProvider provider = new WorkflowEntityContextMenuProvider(
				getGraphicalViewer(), getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
		getSite().registerContextMenu(
				"org.eclipse.gef.examples.flow.editor.contextmenu", //$NON-NLS-1$
				provider, getGraphicalViewer());

		// add Zoom function ...
		List zoomLevels = new ArrayList(3);
		zoomLevels.add(ZoomManager.FIT_ALL);
		zoomLevels.add(ZoomManager.FIT_WIDTH);
		zoomLevels.add(ZoomManager.FIT_HEIGHT);

		root.getZoomManager().setZoomLevels(
				new double[] { 0.25, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 });
		root.getZoomManager().setZoomLevelContributions(zoomLevels);
		root.getZoomManager().setZoom(1.0);

		// Add Mouse Wheel Support
		getGraphicalViewer().setProperty(
				MouseWheelHandler.KeyGenerator.getKey(SWT.MOD2),
				MouseWheelZoomHandler.SINGLETON);

		// register short keys
		IAction zoomIn = new ZoomInAction(root.getZoomManager());
		IAction zoomOut = new ZoomOutAction(root.getZoomManager());
		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
		getSite().getKeyBindingService().registerAction(zoomIn);
		getSite().getKeyBindingService().registerAction(zoomOut);

	}

	/**
	 * Initialisierung des Viewers
	 */
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		getGraphicalViewer().setContents(processTree);
		ModelTreeEditPart modelTreeEditPart = (ModelTreeEditPart) getGraphicalViewer()
				.getContents();
		modelTreeEditPart.collapseAllProcessEntities();
	}

	public void doSave(IProgressMonitor monitor) {
		// noop

	}

	public void doSaveAs() {
		// noop
	}

	public void dispose() {
		// resort ProcessTree
		processTree = ((EditorInput) this.getEditorInput()).getProcessTree();
		processTree.sortProcessEntities();
		super.dispose();
	}

	/**
	 * returns always false, because the IXXMLEditor handles this behavior. Only
	 * when closing the IXXMLEditor changes will be saved
	 */
	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		// noop
		return false;
	}

	/**
	 * Diese Methode stellt eine Pallte bereit, in dem ein neues PaletteRoot
	 * Objekt erzeugt wird und dann durch den Aufruf von der Methode
	 * createCategories die einzelnen Elemente hinzugef�gt werden.
	 * 
	 * @return
	 */
//	static PaletteRoot createPalette() {
		 PaletteRoot createPalette() {
		PaletteRoot palette = new PaletteRoot();
		palette.addAll(createCategories(palette));
		return palette;
	}

	/**
	 * Returns the PaletteRoot for the palette viewer.
	 * 
	 * @return the palette root
	 */
	protected PaletteRoot getPaletteRoot() {
		if (paletteRoot == null) {
			paletteRoot = createPalette();
		}
		return paletteRoot;
	}

	/**
	 * Erzeugen der einzelnen Katgorieen f�r die Palette
	 * 
	 * @param root
	 * @return
	 */
	//static private List createCategories(PaletteRoot root) {
	private List createCategories(PaletteRoot root) {
		List categories = new ArrayList();
		categories.add(createControlGroup(root));
		categories.add(createComponentsDrawer());
		return categories;
	}

	/**
	 * Standard Palette witch Selection- and Marquee Tool
	 * 
	 * @param root
	 * @return
	 */
	static private PaletteContainer createControlGroup(PaletteRoot root) {
		ImageDescriptor icon;
		// PaletteGroup controlGroup = new PaletteGroup("Tools");
		icon = WorkflowmodelerPlugin.getPlugin().getIcon("editor/folder.gif");
		PaletteDrawer drawer = new PaletteDrawer("Tools", icon); //$NON-NLS-1$

		List entries = new ArrayList();

		// selection tool
		ToolEntry tool = new PanningSelectionToolEntry();
		entries.add(tool);
		root.setDefaultEntry(tool);

		// no Marqueee Tool
		// tool = new MarqueeToolEntry();
		// entries.add(tool);

		PaletteSeparator sep = new PaletteSeparator(
				"org.eclipse.gef.examples.logicdesigner.logicplugin.sep2"); //$NON-NLS-1$
		sep
				.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		entries.add(sep); //$NON-NLS-1$

		// controlGroup.addAll(entries);
		// return controlGroup;
		drawer.addAll(entries);
		return drawer;
	}

	/**
	 * Tool Palette mit CreateCommands f�r Modell Objekte installieren F�r jedes
	 * Tool wird ein sogenannte CreateionFactory bereitgestellt, welche
	 * lediglich daf�r sorgt, das das entsprechende ModelObjekt (z.B.:
	 * ProessEntity) erzeugt wird. Die Darstellung des Elementes wird dabei
	 * automatisch durch den Editor bzw. das ObserverPattern realisiert. Es wird
	 * dazu automatisch die Methode createEditPart der ModelTreeEditPartFactory
	 * aufgerufen.
	 * 
	 * @return
	 */
//	static private PaletteContainer createComponentsDrawer() {
	private PaletteContainer createComponentsDrawer() {
		ImageDescriptor icon;
		CombinedTemplateCreationEntry combined;

		icon = WorkflowmodelerPlugin.getPlugin().getIcon("editor/folder.gif");
		PaletteDrawer drawer = new PaletteDrawer("Elements", icon); //$NON-NLS-1$

		List entries = new ArrayList();

		/** * Process Tool ** */
		icon = WorkflowmodelerPlugin.getPlugin().getIcon("editor/process.gif");
		
		// ProcessTree aLocalTree=processTree;
		combined = new CombinedTemplateCreationEntry("Process",
				"create new process entity", "some template code",
				new CreationFactory() {
					public Object getNewObject() {
						ProcessEntity entity = processTree
								.createProcessEntity();
						return entity;
					}

					public Object getObjectType() {
						return ProcessEntity.class;
					}
				}, icon, icon);
		entries.add(combined);

		/** * Activity Tool ** */
		icon = WorkflowmodelerPlugin.getPlugin().getIcon("editor/activity.gif");
		combined = new CombinedTemplateCreationEntry("Activity",
				"create new activity entity", "some template code",
				new CreationFactory() {
					public Object getNewObject() {
						/**
						 * TODO hier muss noch das aktuaell selektierte
						 * ermittelt wreden
						 */
						ProcessEntity processEntity = getCurrentProcessEntity();
						ActivityEntity entity = processEntity
								.createActivityEntity();
						return entity;
					}

					public Object getObjectType() {
						return ActivityEntity.class;
					}
				}, icon, icon);
		entries.add(combined);

		/** * Verbindung Tool ** */
		icon = WorkflowmodelerPlugin.getPlugin().getIcon(
				"editor/verwendung.gif");
		ConnectionCreationToolEntry cce = new ConnectionCreationToolEntry(
				"Connect", "connect activity with process entityVerwendung",
				new CreationFactory() {

					public Object getNewObject() {
						return new Association();
					}

					public Object getObjectType() {
						return Association.class;
					}
				}, icon, icon);
		entries.add(cce);

		drawer.addAll(entries);
		return drawer;
	}

	/**
	 * Persistenzmechnsimu� f�r ToolPalette bereitstellen
	 */
	protected static final String PALETTE_DOCK_LOCATION = "Dock location"; //$NON-NLS-1$

	protected static final String PALETTE_SIZE = "Palette Size"; //$NON-NLS-1$

	protected static final String PALETTE_STATE = "Palette state"; //$NON-NLS-1$

	protected static final int DEFAULT_PALETTE_SIZE = 130;

	protected FlyoutPreferences getPalettePreferences() {
		return new FlyoutPreferences() {

			public int getDockLocation() {
				return WorkflowmodelerPlugin.getPlugin().getPreferenceStore()
						.getInt(PALETTE_DOCK_LOCATION);
			}

			public int getPaletteState() {
				return WorkflowmodelerPlugin.getPlugin().getPreferenceStore()
						.getInt(PALETTE_STATE);
			}

			public int getPaletteWidth() {
				return WorkflowmodelerPlugin.getPlugin().getPreferenceStore()
						.getInt(PALETTE_SIZE);
			}

			public void setDockLocation(int location) {
				WorkflowmodelerPlugin.getPlugin().getPreferenceStore()
						.setValue(PALETTE_DOCK_LOCATION, location);
			}

			public void setPaletteState(int state) {
				WorkflowmodelerPlugin.getPlugin().getPreferenceStore()
						.setValue(PALETTE_STATE, state);
			}

			public void setPaletteWidth(int width) {
				WorkflowmodelerPlugin.getPlugin().getPreferenceStore()
						.setValue(PALETTE_SIZE, width);
			}

		};

	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		super.setFocus();
		WorkflowmodelerPlugin.getPlugin().switchModel(
				processTree.getWorkflowModel());
		
		// aktualisiere currentProcessEntity
		if (processTree.getProcessEntities().size()>0) {
			currentProcessEntity=(ProcessEntity) processTree.getProcessEntities().iterator().next();
		
		} else
			currentProcessEntity=null;
		
	}

	/**
	 * Diese Methode reagiert auf PropertyChange Events um darauf zu regieren,
	 * wenn das zuggeh�rige Model geschlossen wurde. In diesem Fall mu� sich
	 * auch der Editor schlie�en!
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(WorkflowmodelerPlugin.MODEL_CLOSED)) {
			// Handelt es sich um das aktuell angezeigte Model?
			if (evt.getOldValue() == processTree.getWorkflowModel()) {
				// Editor schlie�en...
				IWorkbenchWindow workbenchWindow = getSite()
						.getWorkbenchWindow();
				IWorkbenchPage page = workbenchWindow.getActivePage();
				page.closeEditor(this, false);
			}
		}
	}

	/**
	 * This mehtod adaptes the ix property page and a zoomManager for the
	 * graphical editor.
	 * 
	 * 
	 * We need to tell the workbench to use the tabbed property view. Each
	 * workbench part can define its own custom property sheet page by providing
	 * an adaptable for IPropertySheetPage. The workbench will call
	 * getAdapter() method and ask for an IPropertySheetPage. It is at this
	 * point that we tell Eclipse to use our tabbed property sheet page.
	 **/
	public Object getAdapter(Class adapter) {
		/*
		if (adapter
				.equals(org.eclipse.ui.views.properties.IPropertySheetPage.class))
			return new org.imixs.eclipse.workflowmodeler.ui.properties.WorkflowModelPropertyPage();
		*/
		
		if (adapter == IPropertySheetPage.class) 
            return new TabbedPropertySheetPage(this);
            		
		
		if (adapter == ZoomManager.class)
			return ((ScalableFreeformRootEditPart) getGraphicalViewer()
					.getRootEditPart()).getZoomManager();

		return super.getAdapter(adapter);
	}

	/**
	 * A workbench part that provides a tabbed property view needs to implement
	 * the ITabbedPropertySheetPageContributor interface. This interface returns
	 * the contributor identifier for our configuration. We will simply use the
	 * view identifier
	 * 
	 * http://www.eclipse.org/articles/Article-Tabbed-Properties/
	 * tabbed_properties_view.html
	 */
	public String getContributorId() {
		return "org.imixs.eclipse.workflowmodeler.EntityProperyView";
	}
}
