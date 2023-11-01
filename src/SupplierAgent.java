import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class SupplierAgent extends Agent {
    protected void setup() {
        System.out.println("Supplier Agent " + getAID().getName() + " is ready.");

        addBehaviour(new RequestSuppliesBehaviour());
    }

    private class RequestSuppliesBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Handle supply request from Warehouse Manager Agent
                String itemName = msg.getContent();

                // Send reply to Warehouse Manager Agent
                ACLMessage reply = msg.createReply();
                addToSupply(itemName);
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("Supply updated successfully");
                System.out.println("Supply for "+ itemName + " updated successfully!");
                System.out.println("----------------------------------------------");
                send(reply);
                }

            else {
                block();
            }
        }
    }

    private void addToSupply(String itemName) {
        try {
            // Delay for 1 second (1000 milliseconds)
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                    // Increase the supply by 10

                    entry.put("quantity", quantity + 10);

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
}
