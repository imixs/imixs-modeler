package org.imixs.eclipse.workflowmodeler.commands;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.imixs.eclipse.workflowmodeler.model.*;

public class AssociationReconnectCommand extends Command {

    private Association verwendung;

    private AbstractWorkflowEntity newSource;

    private AbstractWorkflowEntity newTarget;

    private AbstractWorkflowEntity oldSource;

    private AbstractWorkflowEntity oldTarget;

    public AssociationReconnectCommand(Association verwendung, AbstractWorkflowEntity newSource, AbstractWorkflowEntity newTarget) {
        super("Reconnect Association");
        this.verwendung = verwendung;
        oldSource = verwendung.getFromEntity();
        oldTarget = verwendung.getToEntity();
        this.newSource = newSource == null ? oldSource : newSource;
        this.newTarget = newTarget == null ? oldTarget : newTarget;
    }

    public boolean canExecute() {
        if (newSource.equals(newTarget)) {
            return false;
        }
        for (Iterator it = newSource.getAssociationOutgoing().iterator(); it.hasNext();) {
        	Association verwendung = (Association) it.next();
            if (verwendung.getToEntity().equals(newTarget)) {
                return false;
            }
        }
        return true;
    }

    public void execute() {
        redo();
    }

    public void redo() {
        verwendung.disconnect();
        verwendung.setFromEntity(newSource);
        verwendung.setToEntity(newTarget);
       // verwendung.connect();
    }

    public void undo() {
        verwendung.disconnect();
        verwendung.setFromEntity(oldSource);
        verwendung.setToEntity(oldTarget);
       // verwendung.connect();
    }
}