package org.drools.fipa.body.acts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.drools.fipa.Act;
import org.drools.fipa.Encodings;
import org.drools.fipa.body.content.Info;

@XmlType(name = "QueryIf", namespace="http://acts.body.fipa.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryIf extends AbstractMessageBody {


    public static final Act performative = Act.QUERY_IF;
    public Act getPerformative() { return performative; }
    
    @XmlElement(required = true) 
    private Info proposition;

    public QueryIf() {
    }


    
    
    public QueryIf(Info proposition) {
        this.proposition = proposition;
    }


    @Override
    public String toString() {
        return "QueryIf{" +
                "proposition=" + proposition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryIf that = (QueryIf) o;

        if (proposition != null ? !proposition.equals(that.proposition) : that.proposition != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return proposition != null ? proposition.hashCode() : 0;
    }

    public Info getProposition() {
        return proposition;
    }

    public void setProposition(Info proposition) {
        this.proposition = proposition;
    }

    public String getEncodedContent() {
        return proposition.getEncodedContent();
    }

    public boolean isEncoded() {
        return proposition.isEncoded();
    }

    public void encode(Encodings encoding) {
        proposition.encode(encoding);
    }

    public void decode(Encodings encoding) {
        proposition.decode(encoding);
    }


    public Object[] getArguments() {
        return new Object[] { proposition.getData() };
    }
}
