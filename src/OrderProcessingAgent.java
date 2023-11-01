import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class OrderProcessingAgent extends Agent {
    protected void setup() {
        System.out.println("Order Processing Agent " + getAID().getName() + " is ready.");

        addBehaviour(new ProcessOrderBehaviour());
        addBehaviour(new WarehouseConfirmationBehaviour());
    }


    private class ProcessOrderBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);


            if (msg != null) {
                // Handle order request from Customer Agent
                String itemName = msg.getContent();

                ACLMessage msgWarehouse = new ACLMessage(ACLMessage.REQUEST);
                msgWarehouse.setContent(itemName);
                msgWarehouse.addReceiver(getAID("WarehouseManager"));
                send(msgWarehouse);




            } else {
                block();
            }
        }
    }

    private class WarehouseConfirmationBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);


            if (msg != null) {
                // Handle order confirmation from warehouse
                String itemName = msg.getContent();

                System.out.println("Order for "+itemName+" was successfully registered");

                ACLMessage msgInventory = new ACLMessage(ACLMessage.REQUEST);
                msgInventory.addReceiver(getAID("Inventory"));
                msgInventory.setContent(itemName);
                send(msgInventory);


            } else {
                block();
            }
        }
    }
    private void sendInventoryRequest(String itemName){
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent(itemName);
        msg.addReceiver(getAID("Warehouse Manager"));
        send(msg);
    }

}