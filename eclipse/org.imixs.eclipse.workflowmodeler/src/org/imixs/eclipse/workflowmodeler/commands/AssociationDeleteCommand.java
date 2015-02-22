package org.imixs.eclipse.workflowmodeler.commands;

import org.eclipse.gef.commands.Command;
import org.imixs.eclipse.workflowmodeler.model.*;

public class AssociationDeleteCommand extends Command {
    private final Association verwendung;

    public AssociationDeleteCommand(Association verwendung) {
        super("Delete Association");
        this.verwendung = verwendung;
    }

    public void execute() {
        redo();
    }

    public void redo() {
        verwendung.disconnect();
    }
    
    public void undo() {
      //  verwendung.connect();
    }
}