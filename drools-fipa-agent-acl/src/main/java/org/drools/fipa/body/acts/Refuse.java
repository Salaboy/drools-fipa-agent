package org.drools.fipa.body.acts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.drools.fipa.Act;
import org.drools.fipa.body.content.Action;
import org.drools.fipa.body.content.Info;


@XmlType(name = "Refuse", namespace = "http://acts.body.fipa.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class Refuse extends AbstractMessageBody {
    
    private Action action;
    private Info cause;


    public Refuse() {
    }

    
    public Refuse(Action action, Info cause) {
        this.action = action;
        this.cause = cause;
    }

    
    
    @Override
    public String toString() {
        return "Refuse{"
                + "action=" + action
                + ", cause=" + cause
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Refuse that = (Refuse) o;

        if (action != null ? !action.equals(that.action) : that.action != null) {
            return false;
        }
        if (cause != null ? !cause.equals(that.cause) : that.cause != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + (cause != null ? cause.hashCode() : 0);
        return result;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Info getCause() {
        return cause;
    }

    public void setCause(Info cause) {
        this.cause = cause;
    }
}
