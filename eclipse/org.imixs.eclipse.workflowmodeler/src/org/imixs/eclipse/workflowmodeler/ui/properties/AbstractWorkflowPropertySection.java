package org.imixs.eclipse.workflowmodeler.ui.properties;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.imixs.eclipse.workflowmodeler.model.ModelObject;
import org.imixs.eclipse.workflowmodeler.ui.ImageLabel;
import org.imixs.eclipse.workflowmodeler.ui.editparts.AssociationEditPart;

/**
 * This Class can be used as a base for individual IWorkflowPropertyTab
 * implementations. The class implements the ISelectionListener and also an
 * ModifyListener. Implementations of this class should use the
 * <code>createInput</code> Methode to use standard funktionality
 * 
 * 
 * @author Ralph Soika
 * 
 */
public abstract class AbstractWorkflowPropertySection extends
		AbstractPropertySection implements ModifyListener {

	private ModelObject modelObject = null;
	private Hashtable hashtableInputElements = new Hashtable();
	// private Vector listeners;
	//private String attributeName=null;

	private int labelWith = 100;

	public ModelObject getModelObject() {
		return modelObject;
	}

	public int getLabelWith() {
		return labelWith;
	}

	public void setLabelWith(int labelWith) {
		this.labelWith = labelWith;
	}

	/**
	 * This method creates a section with a client composite
	 * 
	 * @param workflowPropertyControl
	 * @param compo
	 * @param title
	 * @param desc
	 * @param iStyle
	 * @param numColumns
	 * @return
	 */
	public Composite createSection(Composite parent, String title, String desc,
			int iStyle, int numColumns) {

		Section section = getWidgetFactory().createSection(parent, iStyle);

		section.setText(title);
		section.setDescription(desc);
		section.setLayout(new FormLayout());
		FormData data;
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);

		section.setLayoutData(data);

		Composite client1 = getWidgetFactory().createFlatFormComposite(section);

		section.setClient(client1);

		return client1;

	}

	/**
	 * This Methode creates a standard Text Input with an Label and registers a
	 * ModifyListener to react on Changes. This Changes are populatet to the
	 * current ModelObject.
	 * 
	 * @param parent
	 * @param toolkit
	 * @param aLabel
	 * @param aAttribute
	 *            - the Value of the ModelObject
	 */
	public Composite createGridComposite(Composite parent, String aLabel,
			Image image, int iColls, Control attacheto) {

		/** Eingabe Feld */
		FormData data;

		Composite compositeGrid = getWidgetFactory().createComposite(parent,
				SWT.NONE);
		data = new FormData();
		if (aLabel != null)
			data.left = new FormAttachment(0, labelWith);
		else
			data.left = new FormAttachment(0, 0);

		data.right = new FormAttachment(100, 0);

		if (attacheto != null)
			data.top = new FormAttachment(attacheto, 0,
					ITabbedPropertyConstants.VSPACE);
		else
			data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);

		compositeGrid.setLayoutData(data);

		/* now set a gridLayout for checkbox Composite */
		GridLayout layout = new GridLayout();

		layout.numColumns = iColls;
		compositeGrid.setLayout(layout);

		/* add label */
		if (aLabel != null) {
			CLabel labelLabel = getWidgetFactory().createCLabel(parent, aLabel); //$NON-NLS-1$
			data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(compositeGrid,
					-ITabbedPropertyConstants.HSPACE);
			data.top = new FormAttachment(compositeGrid, 0, SWT.CENTER);
			labelLabel.setLayoutData(data);
		}

		return compositeGrid;
	}

	/**
	 * This Methode creates a standard Text Input with an Label and registers a
	 * ModifyListener to react on Changes. This Changes are populatet to the
	 * current ModelObject.
	 * 
	 * @param parent
	 * @param toolkit
	 * @param aLabel
	 * @param aAttribute
	 *            - the Value of the ModelObject
	 */
	public Text createTextInput(Composite parent, String aLabel, Image image,
			FormData formdata, String aAttribute, Control attacheto) {

		/** Eingabe Feld */
		FormData data;
		Text textInput = getWidgetFactory().createText(parent, ""); //$NON-NLS-1$

		if (formdata != null)
			data = formdata;
		else {
			data = new FormData();
			data.left = new FormAttachment(0, labelWith);
			data.right = new FormAttachment(100, 0);

			if (attacheto != null)
				data.top = new FormAttachment(attacheto, 0,
						ITabbedPropertyConstants.VSPACE);
			else
				data.top = new FormAttachment(0,
						ITabbedPropertyConstants.VSPACE);
		}

		textInput.setLayoutData(data);

		CLabel labelLabel = getWidgetFactory().createCLabel(parent, aLabel); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(textInput,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(textInput, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);

		this.registerInput(textInput, aAttribute);

		return textInput;
	}

	public Text createTextareaInput(Composite parent, String aLabel,
			Image image, String aAttribute, Control attacheto, int height) {

		/** Eingabe Feld */
		FormData data;
		Text textInput = getWidgetFactory().createText(parent, "",
				SWT.MULTI | SWT.V_SCROLL); //$NON-NLS-1$

		data = new FormData();
		data.left = new FormAttachment(0, labelWith);
		data.right = new FormAttachment(100, 0);

		if (attacheto != null)
			data.top = new FormAttachment(attacheto, 0,
					ITabbedPropertyConstants.VSPACE);
		else
			data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);

		data.height = height;
		textInput.setLayoutData(data);

		CLabel labelLabel = getWidgetFactory().createCLabel(parent, aLabel); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(textInput,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(textInput, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);

		this.registerInput(textInput, aAttribute);

		return textInput;
	}

	/**
	 * This Methode creates a standard Text Input with an Label and registers a
	 * ModifyListener to react on Changes. This Changes are populatet to the
	 * current ModelObject.
	 * 
	 * @param parent
	 * @param toolkit
	 * @param aLabel
	 * @param aAttribute
	 *            - the Value of the ModelObject
	 */
	public Composite createOptionInput(Composite parent, String aLabel,
			Image image, Collection cOptions, int iColls, String aAttribute,
			boolean bCheckbox, FormData formdata, Control attacheto) {

		/** Eingabe Feld */
		FormData data;

		Composite compositeCheckbox = getWidgetFactory().createComposite(
				parent, SWT.NONE);
		data = new FormData();
		if (aLabel != null)
			data.left = new FormAttachment(0, labelWith);
		else
			data.left = new FormAttachment(0, 0);

		data.right = new FormAttachment(100, 0);

		if (attacheto != null)
			data.top = new FormAttachment(attacheto, 0,
					ITabbedPropertyConstants.VSPACE);
		else
			data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);

		compositeCheckbox.setLayoutData(data);

		/* now set a gridLayout for checkbox Composite */
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;

		layout.numColumns = iColls;
		compositeCheckbox.setLayout(layout);

		/* add label */
		if (aLabel != null) {
			CLabel labelLabel = getWidgetFactory().createCLabel(parent, aLabel); //$NON-NLS-1$
			data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(compositeCheckbox,
					-ITabbedPropertyConstants.HSPACE);
			data.top = new FormAttachment(compositeCheckbox, 0, SWT.CENTER);
			labelLabel.setLayoutData(data);
		}
		registerInput(compositeCheckbox, aAttribute);
		// create options
		updateInputOptions(cOptions, aAttribute, bCheckbox);

		return compositeCheckbox;
	}

	public CCombo createComboInput(Composite parent, String aLabel,
			Image image, Collection cOptions, String aAttribute,
			FormData formdata, Control attacheto) {

		/** Eingabe Feld */
		FormData data;
		CCombo combo = getWidgetFactory().createCCombo(parent, SWT.NONE);

		final String sAttribute = aAttribute;
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CCombo c = (CCombo) e.widget;
				// no the selected text will be back translatet
				// into to value right of the | if available
				String sText = c.getText();
				Hashtable theOptions = (Hashtable) c.getData();
				String sValue = (String) theOptions.get(sText);
				if (sValue == null)
					sValue = sText;
				modelObject.setPropertyValue(sAttribute, sValue);
			}
		});

		if (formdata != null)
			data = formdata;
		else {
			data = new FormData();
			data.left = new FormAttachment(0, labelWith);
			data.right = new FormAttachment(100, 0);

			if (attacheto != null)
				data.top = new FormAttachment(attacheto, 0,
						ITabbedPropertyConstants.VSPACE);
			else
				data.top = new FormAttachment(0,
						ITabbedPropertyConstants.VSPACE);
		}

		combo.setLayoutData(data);

		CLabel labelLabel = getWidgetFactory().createCLabel(parent, aLabel); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(combo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(combo, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);

		this.registerInput(combo, aAttribute);
		updateComboOptions(cOptions, aAttribute);
		return combo;
	}

	/**
	 * This Method creates an Listelement to diplay Entries of an Vector The
	 * Elemet is complete by to buttons to add and remove elements
	 * 
	 * @param parent
	 * @param toolkit
	 * @param aLabel
	 * @param aAttribute
	 * @param width
	 * @param hight
	 */
	public Composite createTableInput(Composite parent, String aLabel,
			Image image, String aAttribute, int width, int hight,
			Control attacheto) {

		/** Eingabe Feld */
		FormData data;

		Composite composite = getWidgetFactory().createComposite(parent,
				SWT.NONE);
		data = new FormData();

		if (aLabel != null)
			data.left = new FormAttachment(0, labelWith);
		else
			data.left = new FormAttachment(0, 0);

		data.right = new FormAttachment(100, 0);

		if (attacheto != null)
			data.top = new FormAttachment(attacheto, 0,
					ITabbedPropertyConstants.VSPACE);
		else
			data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		composite.setLayoutData(data);

		/* now set a gridLayout for checkbox Composite */
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 5;
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);

		// setGridData(composite, width + 100, hight + 25, GridData.BEGINNING,
		// false, GridData.BEGINNING, false);

		final Table table = getWidgetFactory().createTable(composite,
				SWT.MULTI | SWT.V_SCROLL);
		setGridData(table, width, hight, GridData.BEGINNING, false,
				GridData.BEGINNING, false);
		// Buttons erg�nzen
		// Button Composite erzeugen
		Composite compositeButtons = getWidgetFactory().createComposite(
				composite, SWT.NONE);
		FillLayout fillLayoutButtons = new FillLayout();
		fillLayoutButtons.spacing = 2;
		fillLayoutButtons.type = SWT.VERTICAL;
		compositeButtons.setLayout(fillLayoutButtons);
		setGridData(compositeButtons, -1, -1, GridData.BEGINNING, false,
				GridData.BEGINNING, false);
		final String sAttributeID = aAttribute;
		final Shell shell = parent.getShell();
		Button button = getWidgetFactory().createButton(compositeButtons,
				"Add...", SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InputDialog inputDlg = new InputDialog(shell, "Add Entry",
						"New value", "", null);
				if (inputDlg.open() == InputDialog.OK) {
					TableItem tabelItem = new TableItem(table, SWT.NONE);
					tabelItem.setText(inputDlg.getValue());
					// werte neu setzen
					Vector v = new Vector();
					TableItem[] items = table.getItems();
					for (int i = 0; i < items.length; i++) {
						v.add(items[i].getText());
					}
					modelObject.setPropertyValue(sAttributeID, v);
				}
			}
		});

		button = getWidgetFactory().createButton(compositeButtons, "Remove",
				SWT.PUSH);
		// button.setData(table);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// MessageDialog.o
				int iCurrent = table.getSelectionIndex();
				if (iCurrent >= 0)
					table.remove(iCurrent);
				// werte neu setzen
				Vector v = new Vector();
				TableItem[] items = table.getItems();
				for (int i = 0; i < items.length; i++) {
					v.add(items[i].getText());
				}
				modelObject.setPropertyValue(sAttributeID, v);
			}
		});

		if (aLabel != null) {
			CLabel labelLabel = getWidgetFactory().createCLabel(parent, aLabel); //$NON-NLS-1$
			data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(composite,
					-ITabbedPropertyConstants.HSPACE);
			data.top = new FormAttachment(composite, 0, SWT.TOP);
			labelLabel.setLayoutData(data);
		}

		// getWidgetFactory().paintBordersFor(composite);
		table.setData(aAttribute);
		registerInput(table, aAttribute);

		return composite;
	}

	/**
	 * This Method creates complex input widget to manage usernames and roles
	 * 
	 * @param parent
	 * @param toolkit
	 * @param aLabel
	 * @param aAttribute
	 *            - the Value of the ModelObject
	 */
	public Composite createUserRoleInput(Composite parent, String aLabel,
			Image image, FormData formdata, String aAttribute1,
			String aAttribute2, Control attacheto) {
		FormData data;
		/** Filedset */
		Composite composite = getWidgetFactory().createGroup(parent, aLabel);
		data = new FormData();
		// data.left = new FormAttachment(0, labelWith);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);

		if (attacheto != null)
			data.top = new FormAttachment(attacheto, 0,
					ITabbedPropertyConstants.VSPACE);
		else
			data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		composite.setLayoutData(data);

		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 5;
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);

		// Jetzt zwei Behälter bauen.
		Composite composite1 = getWidgetFactory().createComposite(composite,
				SWT.NONE);
		composite1.setLayout(new FormLayout());
		Composite composite2 = getWidgetFactory().createComposite(composite,
				SWT.NONE);
		composite2.setLayout(new FormLayout());

		createTableInput(composite1, null, null, aAttribute1, 100, 30, null);

		createOptionInput(composite2, null, null, null, 2, aAttribute2, true,
				null, null);

		return composite;
	}

	private void createImagelable(Composite parent, String aLabel, Image image,
			GridData gd) {

		ImageLabel imageLabel = new ImageLabel(parent, SWT.LEFT, image, aLabel);
		if (gd == null)
			setGridData(imageLabel, SWT.DEFAULT, SWT.DEFAULT, GridData.FILL,
					true, GridData.CENTER, false);
		else
			imageLabel.setLayoutData(gd);

	}

	public void createTextareaInput(Composite parent, FormToolkit toolkit,
			String aLabel, Image image, String aAttribute, GridData gd) {
		Label label = toolkit.createLabel(parent, aLabel, SWT.LEFT);
		// label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		setGridData(label, -1, -1, GridData.BEGINNING, false,
				GridData.BEGINNING, false);
		if (image != null) {
			label.setImage(image);
			label.setToolTipText(aLabel);
		}

		Text textInput = toolkit.createText(parent, "", SWT.MULTI
				| SWT.V_SCROLL);

		// Default GridDataLayout?
		if (gd == null)
			setGridData(textInput, SWT.DEFAULT, SWT.DEFAULT, GridData.FILL,
					true, GridData.CENTER, false);
		else
			textInput.setLayoutData(gd);
		textInput.setData(aAttribute);
		registerInput(textInput, aAttribute);
	}

	/**
	 * Updates the checkbox or radio Button Options of a composite For each
	 * button a SelectionListener is registerd to update the underlying
	 * modelObject. Also the current Options are compared with the actual
	 * PropertyValue of the ModelObject if some Options are removed the
	 * propertyValue will be updated
	 * 
	 * @param cOptions
	 * @param aAttribute
	 */
	public void updateInputOptions(Collection cOptions, final String aAttribute,
			boolean bCheckbox) {
		Vector vectorValues = new Vector();
		Vector vectorOptionValueList = new Vector();
		Composite compositeCheckbox = (Composite) hashtableInputElements
				.get(aAttribute);

		if (modelObject != null) {
			Object o = modelObject.getPropertyValue(aAttribute);
			if (o instanceof Vector) {
				vectorValues = (Vector) o;
			} else if (o instanceof String) {
				vectorValues.add(o);
			}
		}

		// check if buttons should be removed
		Control[] oldControls = compositeCheckbox.getChildren();
		for (int i = 0; i < oldControls.length; i++) {
			Control old = oldControls[i];
			// System.out.println("ich erntferne ein altes control :-)");
			old.dispose();
			old = null;
		}

		if (cOptions == null)
			return;
		// Pro Object ein Button
		
		Iterator iter = cOptions.iterator();
		while (iter.hasNext()) {
			String sOption = iter.next().toString();
			String sText = sOption;
			// if '|' then separate Value from text
			if (sOption.indexOf("|") > -1) {
				sText = sOption.substring(0, sOption.indexOf("|"));
				sOption = sOption.substring(sOption.indexOf("|") + 1);
			}
		
			sText = sText.trim();
			sOption = sOption.trim();
			// save option value to compare values later with current property
			vectorOptionValueList.add(sOption);

			Button buttonCheckbox;
			if (bCheckbox)
				// buttonCheckbox = new Button(compositeCheckbox, SWT.CHECK);
				buttonCheckbox = getWidgetFactory().createButton(
						compositeCheckbox, sText, SWT.CHECK);
			else
				// buttonCheckbox = new Button(compositeCheckbox, SWT.RADIO);
				buttonCheckbox = getWidgetFactory().createButton(
						compositeCheckbox, sText, SWT.RADIO);

			buttonCheckbox.setText(sText);
			buttonCheckbox.setData(sOption);

			// Selectiert?
			if (vectorValues.indexOf(sOption) > -1)
				buttonCheckbox.setSelection(true);
			else
				buttonCheckbox.setSelection(false);

		//	final String sAttributeName = aAttribute;
		//	attributeName=aAttribute;
			buttonCheckbox.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Button bb = (Button) e.getSource();

					// update Modelobjekt
					if (modelObject != null) {
						// read property Value and convert into Vector
						Vector vValues = new Vector();
						Object o = modelObject.getPropertyValue(new String(aAttribute));
						if (o instanceof Vector) {
							vValues = (Vector) o;
						} else if (o instanceof String) {
							vValues = new Vector();
							vValues.add(o);
						}
						// selected?
						if (bb.getSelection()) {
							if (vValues.indexOf(bb.getData()) == -1)
								vValues.add(bb.getData());
						} else {
							if (vValues.indexOf(bb.getData()) > -1)
								vValues.remove(bb.getData());
						}
						// set new PropertyValue
						modelObject.setPropertyValue(aAttribute, vValues);
			
					}
				}
			});
		}

		// Falls eine Option im Property vorhanden ist, die im OptionsFeld
		// selbst fehlt,
		// mu� diese entfernt werden!
		Vector vectorRemoveList = new Vector();
		iter = vectorValues.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (!vectorOptionValueList.contains(o))
				vectorRemoveList.add(o);
		}
		if (vectorRemoveList.size() > 0) {
			for (int i = 0; i < vectorRemoveList.size(); i++)
				vectorValues.remove(vectorRemoveList.elementAt(i));
			modelObject.setPropertyValue(aAttribute, vectorValues);
		}

		// refresh layout to render new size of checkboxes
		compositeCheckbox.getParent().layout(true);
	}

	/**
	 * adds a SelectionListener to the checkbox or radio Button Options of a
	 * composite For each button the SelectionListener will be added
	 * 
	 * @param listener
	 * @param aAttribute
	 */
	
	public void addOptionSelectionListener(String aAttribute,
			SelectionListener listener) {

		Composite compositeCheckbox = (Composite) hashtableInputElements
				.get(aAttribute);
		// check all buttons
		Control[] optionControls = compositeCheckbox.getChildren();
		for (int i = 0; i < optionControls.length; i++) {
			if (optionControls[i] instanceof Button) {
				((Button) optionControls[i]).addSelectionListener(listener);
			}
		}
	}


	/**
	 * Updates the Options of a Combo Widget
	 * 
	 * @param cOptions
	 * @param aAttribute
	 */
	public void updateComboOptions(Collection cOptions, String aAttribute) {
		String sCurrentValue = "";
		CCombo combo = (CCombo) hashtableInputElements.get(aAttribute);
		// Remove all current entries
		combo.removeAll();
		// read current value from ModelObject
		if (modelObject != null) {
			Object o = modelObject.getPropertyValue(aAttribute);
			if (o instanceof Vector) {
				sCurrentValue = ((Vector) o).firstElement().toString();
			} else if (o instanceof String)
				sCurrentValue = (String) o;
		}

		Iterator iter = cOptions.iterator();
		Hashtable hashData = new Hashtable();
		while (iter.hasNext()) {
			// split left | and right to create an Hashtable
			String sValue = iter.next().toString();
			String sText = sValue;
			if (sValue.indexOf("|") > -1) {
				sText = sValue.substring(0, sValue.indexOf("|"));
				sValue = sValue.substring(sValue.indexOf("|") + 1);
			}
			sText = sText.trim();
			sValue = sValue.trim();
			hashData.put(sText, sValue);
			combo.add(sText);
			if (sCurrentValue.equals(sValue))
				combo.setText(sText);
		}
		combo.setData(hashData);

	}

	/**
	 * This Method registers an Text Widget as a modifyListener
	 * 
	 * @param text
	 * @param aAttribute
	 */
	public void registerInput(Widget input, String aAttribute) {
		if (input instanceof Text)
			((Text) input).addModifyListener(this);
		input.setData(aAttribute);
		hashtableInputElements.put(aAttribute, input);
	}

	/**
	 * This method is called if a EditPart was selected or a node in the Object
	 * Viewer was selected. The method maps the corresponding imixs model object
	 */
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);

		Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object == null)
			return;

		// check if object is an Editpart (GEF) -> then get the Model Object!
		if (object instanceof org.eclipse.gef.EditPart) {

			// if Object is a AssociationEditPart select the connected
			// ActivityEditPart
			if (object instanceof AssociationEditPart) {
				AssociationEditPart aep = (AssociationEditPart) object;
				object = aep.getSource();
			}
			// now fetch the model object form the editpart...
			object = ((org.eclipse.gef.EditPart) object).getModel();
		}

		if (object instanceof ModelObject) {
			modelObject = (ModelObject) object;
		}

	}

	/**
	 * This Method refreshes the registred Input widgets with the values of the
	 * current workflowEntity The methode checks if an property value existes to
	 * the registred widget if not the widget will become emty
	 * 
	 */
	public void refresh() {
		if (modelObject == null)
			return;
		// alle registrierten Inputs pr�fen...
		Iterator it = hashtableInputElements.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String sName = (String) entry.getKey();

			Object inputObject = entry.getValue();
			if (inputObject == null)
				continue;

			/** * Text Input * */
			if (inputObject instanceof Text) {
				Text textInput = (Text) inputObject;

				Object o = modelObject.getPropertyValue(sName);
				if (o != null)
					textInput.setText(o.toString());
				else
					// empty!
					textInput.setText("");

				continue;
			}

			/** * Combo Input * */
			if (inputObject instanceof CCombo) {
				CCombo combo = (CCombo) inputObject;
				// get current property Value
				Object o = modelObject.getPropertyValue(sName);
				String sText = "";
				if (o != null) {
					String sValue = o.toString();
					// try all keys to find the coresponding Text
					Hashtable theOptions = (Hashtable) combo.getData();
					Iterator iterSet = theOptions.keySet().iterator();
					while (iterSet.hasNext()) {
						String sKey = iterSet.next().toString();
						if (theOptions.get(sKey).equals(sValue)) {
							sText = sKey;
							break;
						}
					}
				}
				combo.setText(sText);
				continue;
			}

			/** * Table Input * */
			if (inputObject instanceof Table) {
				Table table = (Table) inputObject;
				table.removeAll();

				// Items neu aufbauen
				Object o = modelObject.getPropertyValue(sName);

				if (o instanceof Vector) {
					Vector v = (Vector) o;
					Enumeration enumerate = v.elements();
					while (enumerate.hasMoreElements()) {
						TableItem tabelItem = new TableItem(table, SWT.NONE);
						tabelItem.setText(enumerate.nextElement().toString());
					}
				} else if (o instanceof String) {
					TableItem tabelItem = new TableItem(table, SWT.NONE);
					tabelItem.setText(o.toString());
				}
				continue;
			}

			/** * Checkbox Input * */
			if (inputObject instanceof Composite) {

				Composite compositeCheckbox = (Composite) inputObject;

				Object o = modelObject.getPropertyValue(sName);
				Vector v = new Vector();
				if (o instanceof Vector) {
					v = (Vector) o;
				} else if (o instanceof String) {
					v.add(o);
				}

				// Selectionen aller Buttons pr�fen
				Control[] Buttons = compositeCheckbox.getChildren();
				for (int ib = 0; ib < Buttons.length; ib++) {
					Button button = (Button) Buttons[ib];
					if (v.indexOf(button.getData().toString()) > -1)
						button.setSelection(true);
					else
						button.setSelection(false);
				}
				continue;

			}

		}

	}

	/**
	 * Sent when the text is modified.
	 * 
	 * @param e
	 *            an event containing information about the modify
	 */
	public void modifyText(ModifyEvent e) {
		Widget widget = e.widget;
		if (widget instanceof Text) {
			String aAttribute = (String) widget.getData();
			modelObject.setPropertyValue(aAttribute, ((Text) widget).getText());
		}

	}

	private void setSize(Control component, int width, int hight) {
		GridData gd = new GridData();
		gd.widthHint = width;
		gd.heightHint = hight;
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.verticalAlignment = GridData.BEGINNING;
		component.setLayoutData(gd);
	}

	private void setGridData(Control component, int width, int hight,
			int horizontalAligment, boolean grabExcessHorizontalSpace,
			int verticalAligment, boolean grabExcessVerticalSpace) {
		GridData gd = new GridData();
		gd.widthHint = width;

		gd.heightHint = hight;
		gd.horizontalAlignment = horizontalAligment;
		gd.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		gd.verticalAlignment = verticalAligment;
		gd.grabExcessVerticalSpace = grabExcessVerticalSpace;
		component.setLayoutData(gd);
	}

}
