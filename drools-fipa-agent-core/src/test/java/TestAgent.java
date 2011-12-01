
import org.drools.fipa.body.content.Query;
import org.drools.fipa.body.acts.InformIf;
import mock.MockFact;
import org.drools.fipa.body.acts.Inform;
import org.drools.fipa.util.*;
import org.drools.fipa.body.acts.InformRef;
import org.drools.fipa.body.content.Action;
import org.drools.fipa.body.content.Ref;
import org.drools.fipa.body.content.Rule;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.Variable;
import org.junit.*;

import java.util.*;
import org.drools.fipa.ACLMessage;
import org.drools.fipa.Act;
import org.drools.fipa.Encodings;
import org.drools.fipa.core.*;
import org.drools.fipa.mappers.MyMapArgsEntryType;

import static org.junit.Assert.*;

public class TestAgent {

    private static DroolsAgent mainAgent;
    private static DroolsAgent clientAgent;
    private static MockResponseInformer mainResponseInformer;
    private static MockResponseInformer clientResponseInformer;

    @Before
    public void createAgents() {

        mainResponseInformer = new MockResponseInformer();
        clientResponseInformer = new MockResponseInformer();

        DroolsAgentConfiguration mainConfig = new DroolsAgentConfiguration();
        mainConfig.setAgentId("Mock Test Agent");
        mainConfig.setChangeset("mainTestAgent_changeset.xml");
        mainConfig.setResponseInformer(mainResponseInformer);
        DroolsAgentConfiguration.SubSessionDescriptor subDescr1 = new DroolsAgentConfiguration.SubSessionDescriptor("session1", "sub1.xml", "NOT_USED_YET");
        mainConfig.addSubSession(subDescr1);
        DroolsAgentConfiguration.SubSessionDescriptor subDescr2 = new DroolsAgentConfiguration.SubSessionDescriptor("session2", "sub2.xml", "NOT_USED_YET");
        mainConfig.addSubSession(subDescr2);
        mainAgent = DroolsAgentFactory.getInstance().spawn(mainConfig);
        assertNotNull(mainAgent);
        assertNotNull(mainAgent.getInnerSession("session1"));
        assertNotNull(mainAgent.getInnerSession("session2"));

        DroolsAgentConfiguration clientConfig = new DroolsAgentConfiguration();
        clientConfig.setAgentId("Humble Test Client");
        clientConfig.setChangeset("clientTestAgent_changeset.xml");
        clientConfig.setResponseInformer(clientResponseInformer);
        clientAgent = DroolsAgentFactory.getInstance().spawn(clientConfig);
        assertNotNull(clientAgent);
    }

    @After
    public void cleanUp() {
        if (mainAgent != null) {
            mainAgent.dispose();
        }

        if (clientAgent != null) {
            clientAgent.dispose();
        }
    }

    @Test
    public void testSimpleInform() {
        MockFact fact = new MockFact("patient1", 18);
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage info = factory.newInformMessage("me", "you", fact);
        mainAgent.tell(info);

        assertNull(mainResponseInformer.getResponses(info));
        StatefulKnowledgeSession target = mainAgent.getInnerSession("session1");
        assertTrue(target.getObjects().contains(fact));

    }

    @Test
    public void testInformAsTrigger() { 
        MockFact fact = new MockFact("patient1", 22);
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);



        ACLMessage info = factory.newInformMessage("me", "you", fact);
        mainAgent.tell(info);


        assertNull(mainResponseInformer.getResponses(info));
        StatefulKnowledgeSession target = mainAgent.getInnerSession("session2");
        for (Object o : target.getObjects()) {
            System.err.println("\t Inform-Trigger test : " + o);
        }
        assertTrue(target.getObjects().contains(new Double(22.0)));
        assertTrue(target.getObjects().contains(new Integer(484)));
    }

    @Test
    public void testQueryIf() {
        MockFact fact = new MockFact("patient1", 18);
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage info = factory.newInformMessage("me", "you", fact);
        mainAgent.tell(info);

        ACLMessage qryif = factory.newQueryIfMessage("me", "you", fact);
        assertNull(mainResponseInformer.getResponses(qryif));
        mainAgent.tell(qryif);


        assertNotNull(mainResponseInformer.getResponses(qryif));
        assertEquals(1, mainResponseInformer.getResponses(qryif).size());

        ACLMessage answer = mainResponseInformer.getResponses(qryif).get(0);
        MessageContentEncoder.decodeBody(answer.getBody(), answer.getEncoding());
        assertEquals(Act.INFORM_IF, answer.getPerformative());
        assertEquals(((InformIf) answer.getBody()).getProposition().getData(), fact);
    }

    @Test
    public void testQueryRef() {
        MockFact fact = new MockFact("patient1", 18);
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage info = factory.newInformMessage("me", "you", fact);
        mainAgent.tell(info);
        Query query = MessageContentFactory.newQueryContent("ageOfPatient", new Object[]{MessageContentHelper.variable("?mock"), "patient1", MessageContentHelper.variable("?age")});
        ACLMessage qryref = factory.newQueryRefMessage("me", "you", query);
        mainAgent.tell(qryref);

        assertNotNull(mainResponseInformer.getResponses(qryref));
        assertEquals(2, mainResponseInformer.getResponses(qryref).size());

        ACLMessage answer = mainResponseInformer.getResponses(qryref).get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        ACLMessage answer2 = mainResponseInformer.getResponses(qryref).get(1);
        assertEquals(Act.INFORM_REF, answer2.getPerformative());
    }

    @Test
    public void testRequest() {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("x", new Double(36));

        Action action = MessageContentFactory.newActionContent("squareRoot", args);
        ACLMessage req = factory.newRequestMessage("me", "you", action);



        mainAgent.tell(req);

        assertNotNull(mainResponseInformer.getResponses(req));
        assertEquals(2, mainResponseInformer.getResponses(req).size());

        ACLMessage answer = mainResponseInformer.getResponses(req).get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        ACLMessage answer2 = mainResponseInformer.getResponses(req).get(1);
        assertEquals(Act.INFORM, answer2.getPerformative());

        assertTrue(((Inform)answer2.getBody()).getProposition().getEncodedContent().contains("6.0"));

    }

    @Test
    public void testRequestWhen() {

        Double in = new Double(36);

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("x", in);


        Rule condition = new Rule();
        condition.setDrl("String( this == \"actionTrigger\" || this == \"actionTrigger2\")");

        Action action = MessageContentFactory.newActionContent("squareRoot", args);
        ACLMessage req = factory.newRequestWhenMessage("me", "you", action, condition);
        mainAgent.tell(req);

        ACLMessage info = factory.newInformMessage("me", "you", new String("actionTrigger"));
        mainAgent.tell(info);


        ACLMessage info2 = factory.newInformMessage("me", "you", new String("actionTrigger2"));
        mainAgent.tell(info2);


        StatefulKnowledgeSession s2 = mainAgent.getInnerSession("session2");
        QueryResults ans = s2.getQueryResults("squareRoot", in, Variable.v);
        assertEquals(1, ans.size());
        assertEquals(6.0, (Double) ans.iterator().next().get("$return"), 1e-6);




    }

    @Ignore
    public void testRequestWhenever() {

        Double in = new Double(36);

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("x", in);


        Rule condition = new Rule();
        condition.setDrl("String( this == \"actionTrigger\" || this == \"actionTrigger2\")");

        Action action = MessageContentFactory.newActionContent("squareRoot", args);
        ACLMessage req = factory.newRequestWheneverMessage("me", "you", action, condition);
        mainAgent.tell(req);

        ACLMessage info = factory.newInformMessage("me", "you", new String("actionTrigger"));
        mainAgent.tell(info);


        ACLMessage info2 = factory.newInformMessage("me", "you", new String("actionTrigger2"));
        mainAgent.tell(info2);


        StatefulKnowledgeSession s2 = mainAgent.getInnerSession("session2");
        QueryResults ans = s2.getQueryResults("squareRoot", in, Variable.v);
        assertEquals(2, ans.size());
        Iterator<QueryResultsRow> iter = ans.iterator();
        assertEquals(6.0, (Double) iter.next().get("$return"), 1e-6);
        assertEquals(6.0, (Double) iter.next().get("$return"), 1e-6);


        fail("INCOMPLETE TEST : Needs open queries to send answer back with a message, but keep trigger rule!");

    }

    @Test
    public void testRequestWithMultipleOutputs() {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        Double x = 32.0;

        args.put("x", x);
        args.put("?y", Variable.v);
        args.put("?inc", Variable.v);


        Action action = MessageContentFactory.newActionContent("randomSum", args);
        ACLMessage req = factory.newRequestMessage("me", "you", action);



        mainAgent.tell(req);

        assertNotNull(mainResponseInformer.getResponses(req));
        assertEquals(2, mainResponseInformer.getResponses(req).size());

        ACLMessage answer = mainResponseInformer.getResponses(req).get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        ACLMessage answer2 = mainResponseInformer.getResponses(req).get(1);
        assertEquals(Act.INFORM_REF, answer2.getPerformative());

        //answer2.getBody().decode(answer2.getEncoding());
        MessageContentEncoder.decodeBody(answer2.getBody(), answer2.getEncoding());
        assertEquals(InformRef.class, answer2.getBody().getClass());

        Ref ref = ((InformRef) answer2.getBody()).getReferences();
        assertNotNull(ref.getReferences());
        boolean containsInc = false;
        boolean containsY = false;
        for(MyMapArgsEntryType entry : ref.getReferences()){
            if(entry.getKey().equals("?inc")){
                 containsInc = true;
            }
            if(entry.getKey().equals("?y")){
                 containsY = true;
            }
        }
        assertTrue(containsInc);
        assertTrue(containsY);
        assertEquals(Double.class, ref.getReferences().get(0).getValue().getClass());
        assertEquals(Double.class, ref.getReferences().get(1).getValue().getClass());

        Double z = (Double) ref.getReferences().get(0).getValue();
        Double y = (Double) ref.getReferences().get(1).getValue();

        assertEquals(y, x + z, 1e-6);

    }

    @Test
    public void testSimpleInformInNewSession() {
        MockFact fact = new MockFact("patient3", 18);
        MockFact fact2 = new MockFact("patient3", 44);

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage info = factory.newInformMessage("me", "you", fact);
        mainAgent.tell(info);

        assertNull(mainResponseInformer.getResponses(info));
        StatefulKnowledgeSession target = mainAgent.getInnerSession("patient3");
        assertNotNull(target);
        assertTrue(target.getObjects().contains(fact));

        ACLMessage info2 = factory.newInformMessage("me", "you", fact2);
        mainAgent.tell(info2);

        assertTrue(target.getObjects().contains(fact2));

    }
}

class MockResponseInformer implements DroolsAgentResponseInformer {

    private Map<ACLMessage, List<ACLMessage>> responses = new HashMap<ACLMessage, List<ACLMessage>>();

    public synchronized void informResponse(ACLMessage originalMessage, ACLMessage response) {
        if (!responses.containsKey(originalMessage)) {
            responses.put(originalMessage, new ArrayList<ACLMessage>());
        }

        responses.get(originalMessage).add(response);
    }

    public List<ACLMessage> getResponses(ACLMessage originalMessage) {
        return this.responses.get(originalMessage);
    }
}