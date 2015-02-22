package org.imixs.eclipse.workflowmodeler.model;

/**
 * This interface is to support a common interface for selected objects in a
 * org.eclipse.ui.views.properties.tabbed.propertySections
 * 
 * Both: org.imixs.eclipse.workflowmodeler.model.ModelObject and
 * org.imixs.eclipse.workflowmodeler.ui.editparts.ProcessEntityEditPart
 * 
 * are selection parts which need to be hooked to the same tabbedPropertySections
 * 
 * The Reason is that the IXGraphicalEditor supports EditPart Objects instead of
 * ModelObjects. So both objects need the same common Property Dialog. You can
 * see in the PropertyDialog Sections that the setInput() method checks for
 * these different objects types.
 * 
 * I think that this can not be further optimized as the GEF needs 
 * AbstractGraphicalEditPart. So if you select a object in a GEF this will 
 * always be a AbstractGraphicalEditPart and if you select an Object in a View
 * this is a Model Object.
 * 
 * 
 * @author rsoika
 * 
 */

public interface IActivityPropertySource {

}
