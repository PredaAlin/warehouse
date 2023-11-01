import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class WarehouseManagerAgent extends Agent {
    protected void setup() {
        System.out.println("Warehouse Manager Agent " + getAID().getName() + " is ready.");

        addBehaviour(new RequestInventoryBehaviour());
        addBehaviour(new CheckStockBehaviour());
    }

    private class CheckStockBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {

                String itemName = msg.getContent();
                processOrder(itemName);
                System.out.println("Order for " + itemName + " has been successfully processed");
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContent(itemName);

                send(reply);


            } else
                block();

        }
    }

    private class RequestInventoryBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {

                // Handle inventory request from Order Processing Agent
                String itemName = msg.getContent();

                // Check inventory and send reply to Order Processing Agent
                ACLMessage reply = msg.createReply();
                if (checkInventory(itemName)) {
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    reply.setContent(itemName);

                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent(itemName + " Not available");
                }
                send(reply);
            } else {
                block();
            }
        }
    }


    private boolean checkInventory(String itemName) {
        String filePath = "src/database.json";
        boolean available = false;

        try {
            // Read the JSON file
            String json = Files.readString(Paths.get(filePath));

            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(json);

            // Read the inventory array
            JSONArray inventoryArray = jsonObject.getJSONArray("inventory");

            // Process inventory data
            for (int i = 0; i < inventoryArray.length(); i++) {
                JSONObject productObject = inventoryArray.getJSONObject(i);

                String name = productObject.getString("name");


                if (Objects.equals(itemName, name)) {
                    available = true;


                }


            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return available;
    }


    private void processOrder(String itemName) {
        String filePath = "src/database.json";

        try {
            String json = Files.readString(Paths.get(filePath));

            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(json);

            // Get the "inventory" array
            JSONArray inventory = jsonObject.getJSONArray("inventory");



            // Find the entry to modify based on its name
            for (int i = 0; i < inventory.length(); i++) {
                JSONObject entry = inventory.getJSONObject(i);
                int quantity = entry.getInt("quantity");

                if (entry.getString("name").equals(itemName)) {
                    // whenever supply for a product drops under 10, send a low stock notification
                    if (quantity < 10){
                        LowStockNotification(itemName);
                    }

                    entry.put("quantity", quantity - 1);

                    break;
                }
            }

            // Write the modified JSON object back to the file
            FileWriter fileWriter = new FileWriter("src/database.json");
            fileWriter.write(jsonObject.toString());
            fileWriter.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void LowStockNotification(String itemName){

        ACLMessage msgSupplier = new ACLMessage(ACLMessage.REQUEST);
        msgSupplier.addReceiver(getAID("Supplier"));
        msgSupplier.setContent(itemName);
        System.out.println("---------------------------------------");
        System.out.println("Requesting an increase in stock for " + itemName);
        System.out.println("---------------------------------------");
        send(msgSupplier);
    }
}
