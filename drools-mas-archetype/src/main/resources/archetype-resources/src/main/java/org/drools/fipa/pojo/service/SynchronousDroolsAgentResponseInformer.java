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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.pojo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.mas.ACLMessage;
import org.drools.mas.core.DroolsAgentResponseInformer;

/**
 *
 * @author esteban
 * @author salaboy
 */
public class SynchronousDroolsAgentResponseInformer implements DroolsAgentResponseInformer {

    private Map<ACLMessage,List<ACLMessage>> responses = new HashMap<ACLMessage, List<ACLMessage>>();
    
    public synchronized void informResponse(ACLMessage originalMessage, ACLMessage reponse) {
        if (!responses.containsKey(originalMessage)){
            responses.put(originalMessage, new ArrayList<ACLMessage>());
        }
        
        responses.get(originalMessage).add(reponse);
    }
     
    public synchronized List<ACLMessage> retrieveResponses(ACLMessage originalMessage){
        return this.responses.remove(originalMessage);
    }
    
}
