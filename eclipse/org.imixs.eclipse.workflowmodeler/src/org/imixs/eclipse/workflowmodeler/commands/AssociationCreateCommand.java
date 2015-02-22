package org.imixs.eclipse.workflowmodeler.commands;

import org.eclipse.gef.commands.Command;
import org.imixs.eclipse.workflowmodeler.model.*;

public class AssociationCreateCommand extends Command {
    private AbstractWorkflowEntity source;

    private AbstractWorkflowEntity target;

    private Association verwendung;

    public AssociationCreateCommand(AbstractWorkflowEntity source, AbstractWorkflowEntity target) {
        super("Create Verwendung");
        this.source = source;
        // Jetzt sollte man eigentlich bestehnde Verbindungen löschen......
        this.target = target;
    }

    public void setTarget(AbstractWorkflowEntity newTarget) {
        target = newTarget;
    }

    public boolean canExecute() {
        if (source == null || target == null) {
            return false;
        }
        if (source.equals(target)) {
            return false;
        }
        
        return true;
    }

    public void execute() {
        verwendung = new Association();
        verwendung.setFromEntity((AbstractWorkflowEntity) source);
        verwendung.setToEntity((AbstractWorkflowEntity) target);
        redo();
    }

    public void redo() {
     //   verwendung.connect();
    }

    public void undo() {
        verwendung.disconnect();
    }
}