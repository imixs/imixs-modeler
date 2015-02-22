package org.imixs.eclipse.workflowmodeler.ui.viewer;


import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.imixs.eclipse.workflowmodeler.WorkflowmodelerPlugin;
import org.imixs.eclipse.workflowmodeler.model.AbstractWorkflowEntity;
import org.imixs.eclipse.workflowmodeler.model.ActivityEntity;
import org.imixs.eclipse.workflowmodeler.model.Configuration;
import org.imixs.eclipse.workflowmodeler.model.ModelObject;
import org.imixs.eclipse.workflowmodeler.model.ProcessEntity;
import org.imixs.eclipse.workflowmodeler.model.ProcessTree;
import org.imixs.eclipse.workflowmodeler.ui.editors.EditorInput;


/**
 * This view displays the workflowmodel in a tree hirarchie.
 *
 * @see ViewPart
 */
public class WorkflowModelView extends ViewPart implements  ITabbedPropertySheetPageContributor  {
	protected TreeViewer treeViewer;
//	protected Text text;
	protected WorkflowModelLabelProvider labelProvider;
	protected Action onlyProcessTreesAction;
	protected Action addEntityAction, removeEntityAction;
	protected ViewerFilter onlyProcessTreeFilter;
	private ModelObject modelObject=null; // current selection!
	

	/*
	 * @see IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {
		/* Create a grid layout object so the text and treeviewer
		 * are layed out the way I want. */
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);
		
		// layout the text field above the treeviewer
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		/* Create a "label" to display information in. I'm
		 * using a text field instead of a lable so you can
		 * copy-paste out of it. */
	/*	text = new Text(parent, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(layoutData);
		*/
		// Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(parent);
	
		treeViewer.setContentProvider(new WorkflowModelContentProvider());
		
		labelProvider = new WorkflowModelLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		
		treeViewer.setUseHashlookup(true);
		
		// layout the tree viewer below the text field
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
		
		createFilters();
		createActions();
		createMenus();
		createToolbar();
		hookListeners();
		
		// wichtig ist hier das null gesetzt wird!
		treeViewer.setInput(null);
		treeViewer.expandAll();
		
		// Jetzt den TreeViewer als Selection Provider anmelden, damit z.B.: die Propry Page
		// automatisch aktualisiert wird
		getSite().setSelectionProvider(treeViewer); 
	
	}
	
	
	
	protected void createFilters() {
		onlyProcessTreeFilter = new ProcessTreeFilter();
	}

	
	
	protected void hookListeners() {
		/**
		 * SelectionChange Listener f�r selektierung von Elementen
		 */
		
	/*	
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// if the selection is empty clear the label
				if(event.getSelection().isEmpty()) {
					//text.setText("");
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					//StringBuffer toShow = new StringBuffer();
					
					// selektierte s Element abfragen
					//String sValue="";
					try {
System.out.println("View: Die Selection ändert sich und es ist - na klar ein ModelObject");						
						modelObject= (ModelObject) selection.getFirstElement();
						if (modelObject instanceof ProcessEntity) {
							//sValue="ID="+((ProcessEntity)modelObject).getItemCollection().getItemValueInteger("numProcessID");
						}
						if (modelObject instanceof ActivityEntity) {
						}
						//toShow.append(sValue);
					} catch (Exception e) {
					}
					

					//text.setText(toShow.toString());
				}
			}
		});
		*/
		
		/**
		 * DoubleClik Listener f�r �ffnen eines neuen Editors
		 */
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				// if the selection is empty return
				if(event.getSelection().isEmpty()) 
					return;
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					// selektierte s Element abfragen
					Object domain = (ModelObject) selection.getFirstElement();
					
					// Falls es sich bei dem selektierten Objekt in eine ActivityEntity oder 
					// eine ProcessEntity handelt den ProcessTree suchen und daf�r den Editor �ffnen
					if (domain instanceof ProcessEntity) {
						domain=((ProcessEntity)domain).getProcessTree();
					}
					if (domain instanceof ActivityEntity) {
						domain=((ActivityEntity)domain).getProcessTree();
					}
					
					if (domain instanceof ProcessTree) {
						// EditorInput anhand es ProcessTrees erzeugen und Editor �ffnen
						IWorkbenchWindow workbenchWindow =getSite().getWorkbenchWindow();
						IWorkbenchPage page = workbenchWindow.getActivePage();
						String editorId = "org.imixs.eclipse.workflowmodeler.IXGraphicalEditor";
						EditorInput input = new EditorInput((ProcessTree)domain);
						try {
						   page.openEditor(input, editorId, true);
						}
						catch ( PartInitException e ) { 
							e.printStackTrace();
						}
					}
				}			
			}
		});
		
	}
	
	
	
	
	protected void createActions() {
		onlyProcessTreesAction = new Action("Only ProcessTrees") {
			public void run() {
				updateFilter(onlyProcessTreesAction);
			}
		};
		onlyProcessTreesAction.setChecked(false);
		
		addEntityAction = new Action("Create Entity") {
			public void run() {
				addNewEntity();
			}			
		};
		addEntityAction.setToolTipText("creates a new Entity");
		addEntityAction.setImageDescriptor(WorkflowmodelerPlugin.getPlugin().getIcon("viewer/newentity.gif"));

		removeEntityAction = new Action("Delete Entity") {
			public void run() {
				if (MessageDialog.openConfirm(null,"Delete","Delete current Element from Workflowmodel?"))
					removeSelected();
			}			
		};
		removeEntityAction.setToolTipText("Deletes a Entity");
		removeEntityAction.setImageDescriptor(WorkflowmodelerPlugin.getPlugin().getIcon("viewer/remove.gif"));
		
	}
	
	
	/** Add a new book to the selected moving box.
	 * If a moving box is not selected, use the selected
	 * obect's moving box. 
	 * 
	 * If nothing is selected add to the root. */
	
	protected void addNewEntity() {
		ModelObject modelObject;
		if (treeViewer.getSelection().isEmpty()) 
			return;
	
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		ModelObject selectedDomainObject = (ModelObject) selection.getFirstElement();
		modelObject=(ModelObject)selectedDomainObject;
		
		// Activity hinzuf�gen?
		if (modelObject instanceof ProcessEntity) {
			((ProcessEntity)modelObject).createActivityEntity();
			return;
		}
		if (modelObject instanceof ProcessTree) {
			((ProcessTree)modelObject).createProcessEntity();
			return;
		}
		
		
	}
	

	/** 
	 * Remove the selected domain object(s).
	 * If multiple objects are selected remove all of them.
	 * 
	 * If nothing is selected do nothing. */
	protected void removeSelected() {
		ModelObject modelObject;
		if (treeViewer.getSelection().isEmpty()) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		/* Tell the tree to not redraw until we finish
		 * removing all the selected children. */
		treeViewer.getTree().setRedraw(false);
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			modelObject = (ModelObject) iterator.next();
			if (modelObject instanceof AbstractWorkflowEntity) {
				((AbstractWorkflowEntity)modelObject).getProcessTree().removeWorkflowEntity((AbstractWorkflowEntity)modelObject);
			}
			
			if (modelObject instanceof Configuration) {
				Configuration config=(Configuration)modelObject;
				config.getWorkflowModel().removeConfiguration(config);
			}
			
			if (modelObject instanceof ProcessTree) {
				ProcessTree protree=(ProcessTree)modelObject;
				protree.getWorkflowModel().removeProcessTree(protree.getName());
			}
			
			
			
		}
		treeViewer.getTree().setRedraw(true);
	}
	
	
	protected void createMenus() {
		IMenuManager rootMenuManager = getViewSite().getActionBars().getMenuManager();
		rootMenuManager.setRemoveAllWhenShown(true);
		rootMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillMenu(mgr);
			}
		});
		fillMenu(rootMenuManager);
	}


	protected void fillMenu(IMenuManager rootMenuManager) {
		IMenuManager filterSubmenu = new MenuManager("Filters");
		rootMenuManager.add(filterSubmenu);
		filterSubmenu.add(onlyProcessTreesAction);		
		
	}
	
	
	
	
	// Multiple filters can be enabled at a time. 
	protected void updateFilter(Action action) {
		if(action == onlyProcessTreesAction) {
			if(action.isChecked()) {
				treeViewer.addFilter(onlyProcessTreeFilter);
			} else {
				treeViewer.removeFilter(onlyProcessTreeFilter);
			}
		} 
	}
	
	
	protected void createToolbar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(addEntityAction);
		toolbarManager.add(removeEntityAction);
	}
	
	
	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {}
	
	
	/**
	 * This mehtod adaptes the ix property page 	 * 
	 * 
	 * We need to tell the workbench to use the tabbed property view. Each
	 * workbench part can define its own custom property sheet page by providing
	 * an adaptable for IPropertySheetPage. The workbench will call
	 * getAdapter() method and ask for an IPropertySheetPage. It is at this
	 * point that we tell Eclipse to use our tabbed property sheet page.
	 **/
	public Object getAdapter(Class adapter) {
		/*
		if (adapter.equals(org.eclipse.ui.views.properties.IPropertySheetPage.class)) {
			return new org.imixs.eclipse.workflowmodeler.ui.properties.WorkflowModelPropertyPage();
		}
		*/
		
		if (adapter == IPropertySheetPage.class)
            return new TabbedPropertySheetPage(this);
		
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








