package org.drools.fipa.body.acts;

import org.drools.fipa.ACLMessage;
import org.drools.fipa.AgentID;
import org.drools.fipa.body.content.Rule;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.drools.fipa.Act;

@XmlType(name = "Propagate", namespace = "http://acts.body.fipa.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class Propagate extends AbstractMessageBody {

   
    private AgentID[] targets;
    private ACLMessage message;
    private Rule condition;
    
    public Propagate(AgentID[] targets, ACLMessage message, Rule condition) {
        this.targets = targets;
        this.message = message;
        this.condition = condition;
    }

    public Propagate() {
    }

   
    
    
    @Override
    public String toString() {
        return "Propagate{"
                + "targets=" + (targets == null ? null : Arrays.asList(targets))
                + ", message=" + message
                + ", condition=" + condition
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

        Propagate propagate = (Propagate) o;

        if (condition != null ? !condition.equals(propagate.condition) : propagate.condition != null) {
            return false;
        }
        if (message != null ? !message.equals(propagate.message) : propagate.message != null) {
            return false;
        }
        if (!Arrays.equals(targets, propagate.targets)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = targets != null ? Arrays.hashCode(targets) : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        return result;
    }

    public AgentID[] getTargets() {
        return targets;
    }

    public void setTargets(AgentID[] targets) {
        this.targets = targets;
    }

    public ACLMessage getMessage() {
        return message;
    }

    public void setMessage(ACLMessage message) {
        this.message = message;
    }

    public Rule getCondition() {
        return condition;
    }

    public void setCondition(Rule condition) {
        this.condition = condition;
    }

    public Object[] getArguments() {
        return new Object[]{targets, message, condition.getDrl()};
    }    
}
