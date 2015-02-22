
package org.imixs.eclipse.workflowmodeler.model;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * This class implements the logical connections betwwen to entities as a directed graph.
 * the Class is gerneall and not specialized on the behavor of the workflowmodel
 * @author Ralph Soika
 *
 */
public class Association extends ModelObject {

    private AbstractWorkflowEntity fromEntity;

    private AbstractWorkflowEntity toEntity;

    public AbstractWorkflowEntity getFromEntity() {
        return fromEntity;
    }

    public void setFromEntity(AbstractWorkflowEntity fromEntity) {
        Object oldValue = this.fromEntity;
        this.fromEntity = fromEntity;
        firePropertyChange("fromEntity", oldValue, fromEntity);
        // inform listenders
        fromEntity.addAssociationOutgoing(this);
    }
 
    public AbstractWorkflowEntity getToEntity() {
        return toEntity;
    }

    public void setToEntity(AbstractWorkflowEntity toEntity) {
        Object oldValue = this.toEntity;
        this.toEntity = toEntity;
        toEntity.addAssociationIncoming(this);
        // inform listenders
        firePropertyChange("toEntity", oldValue, toEntity);
    }

    public void connect() {
        if (fromEntity != null) {
        	fromEntity.addAssociationOutgoing(this);
        }
        if (toEntity != null) {
        	toEntity.addAssociationIncoming(this);
        }
    }

    public void disconnect() {
        if (fromEntity != null) {
        	fromEntity.removeAssociationOutgoing(this);
        }
        if (toEntity != null) {
        	toEntity.removeAssociationIncoming(this);
        }
    }
 
    /**
     * overrides the default getPropertyDescriptors form the
     * ModelObject class to return an empty descriptor
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[0];
    }
  
}