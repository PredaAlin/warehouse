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

public class InventoryAgent extends Agent {
    protected void setup() {
        System.out.println("Inventory Agent " + getAID().getName() + " is ready.");

        addBehaviour(new RequestInventoryBehaviour());
        addBehaviour(new LowStockNotificationBehaviour());
        addBehaviour(new StockRequestedFromOrders());
    }
    private class StockRequestedFromOrders extends CyclicBehaviour{
        public void action(){
            //send the warehouse manager agent a request to decrease the supply of the desired product
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null){
                String itemName = msg.getContent();

                ACLMessage msgWarehouse = new ACLMessage(ACLMessage.QUERY_REF);
                msgWarehouse.addReceiver(getAID("WarehouseManager"));
                msgWarehouse.setContent(itemName);

                send(msgWarehouse);
            }

        }
    }
    private class RequestInventoryBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Handle confirmation from Warehouse Manager Agent
                String itemName = msg.getContent();

                System.out.println(itemName + " is about to be packaged");
               ACLMessage msgPackaging = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
               msgPackaging.addReceiver(getAID("Packaging"));
               msgPackaging.setContent(itemName);
               send(msgPackaging);
            } else {
                block();
            }
        }
    }

    private class LowStockNotificationBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Handle low stock notification from Warehouse Manager Agent
                String itemName = msg.getContent();
                System.out.println("Low stock notification received for " + itemName);
                ACLMessage msgStock = new ACLMessage(ACLMessage.REQUEST);
                msgStock.addReceiver(getAID("Supplier"));
                msgStock.setContent(itemName);
                send(msgStock);
            } else {
                block();
            }
        }
    }


}
