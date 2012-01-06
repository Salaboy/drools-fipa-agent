/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.mas.pojo.service;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;




import org.drools.mas.ACLMessage;
import org.drools.mas.body.acts.*;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Query;
import org.drools.mas.body.content.Ref;
import org.drools.mas.body.content.Rule;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.mappers.MyMapArgsEntryType;
import org.drools.mas.mappers.MyMapReferenceEntryType;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author salaboy
 * @author esteban
 */

@WebService()
@XmlSeeAlso(value={Inform.class, QueryIf.class, InformIf.class, 
                    Agree.class, Failure.class, Action.class, Rule.class, 
                    QueryRef.class, Query.class, 
                    Ref.class, InformRef.class, Request.class, RequestWhen.class,
                    MyMapReferenceEntryType.class, MyMapArgsEntryType.class})
public class SynchronousDroolsAgentServiceImpl implements SynchronousDroolsAgentService {

    private static Logger logger = LoggerFactory.getLogger(SynchronousDroolsAgentServiceImpl.class);
    private DroolsAgent agent;
    private SynchronousDroolsAgentResponseInformer responseInformer;
    
    
    public SynchronousDroolsAgentServiceImpl() {
    
        
    }

    public void setAgent(DroolsAgent agent) {
        this.agent = agent;
    }

    public SynchronousDroolsAgentResponseInformer getResponseInformer() {
        return responseInformer;
    }

    public void setResponseInformer(SynchronousDroolsAgentResponseInformer responseInformer) {
        this.responseInformer = responseInformer;
    }

    
    @WebMethod
    public List<ACLMessage> tell(@WebParam ACLMessage message) {
        logger.info(" >>> IN Message -> " + message.getPerformative().name());
        System.out.println(" >>> IN Message -> " + message.getPerformative().name());
        try {
            agent.tell(message);
        } catch (Throwable t) {
            logger.error(">>>>>>>>>>>>> exception => " + t.getMessage());
            System.out.println(">>>>>>>>>>>>> exception => " + t.getMessage());

            t.printStackTrace();
        }
        List<ACLMessage> retrieveResponses = responseInformer.retrieveResponses(message);
        if (retrieveResponses != null) {
            
            logger.info(" >>> Number of OUT Messages -> " + retrieveResponses.size());
            System.out.println(" >>> Number of OUT Messages -> " + retrieveResponses.size());
            for (ACLMessage outMessage : retrieveResponses) {
                logger.info(" >>> OUT Message -> " + outMessage.getPerformative().name());
                System.out.println(" >>> OUT Message -> " + outMessage.getPerformative().name());
            }
        } else {
            logger.info(">>> 0 OUT Messages");
            System.out.println(">>> 0 OUT Messages");
        }
        return retrieveResponses;
    }
}
