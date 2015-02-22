package org.imixs.eclipse.workflowmodeler.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.imixs.eclipse.workflowmodeler.model.*;

public class WorkflowEntityChangeConstraintCommand extends Command {

    //private final AbstractWorkflowEntity workflowEntity;

    //private final Rectangle constraint;

    int oldX;

    int oldY;

    public WorkflowEntityChangeConstraintCommand(AbstractWorkflowEntity entity, Rectangle constraint) {
        super("Change Position");
       // this.workflowEntity = entity;
        //this.constraint = constraint;
    }

    public void execute() {
       // oldX = workflowEntity.getX();
       // oldY = workflowEntity.getY();
        redo();
    }

    public void redo() {
    	//workflowEntity.setX(constraint.x);
    	//workflowEntity.setY(constraint.y);
    }

    public void undo() {
    	//workflowEntity.setX(oldX);
    	//workflowEntity.setY(oldY);
    }
}