import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WarehouseInventorySystem {
    public static void main(String[] args) {
        PopulateProductsArray();
        try {
            // Get the JADE runtime instance
            Runtime rt = Runtime.instance();

            // Create a default profile
            Profile profile = new ProfileImpl();
            profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
            profile.setParameter(ProfileImpl.GUI, "true");

            // Create a new main container
            AgentContainer container = rt.createMainContainer(profile);

            // Create and add agents to the container
            AgentController warehouseManagerAgent = container.createNewAgent("WarehouseManager", WarehouseManagerAgent.class.getName(), null);
            warehouseManagerAgent.start();

            AgentController supplierAgent = container.createNewAgent("Supplier", SupplierAgent.class.getName(), null);
            supplierAgent.start();

            AgentController inventoryAgent = container.createNewAgent("Inventory", InventoryAgent.class.getName(), null);
            inventoryAgent.start();

            AgentController orderProcessingAgent = container.createNewAgent("OrderProcessing", "OrderProcessingAgent", null);
            orderProcessingAgent.start();

            AgentController deliveryAgent = container.createNewAgent("Delivery", DeliveryAgent.class.getName(), null);
            deliveryAgent.start();

            AgentController customerAgent = container.createNewAgent("Customer", CustomerAgent.class.getName(), null);
            customerAgent.start();

            AgentController packagingAgent = container.createNewAgent("Packaging", PackagingAgent.class.getName(), null);
            packagingAgent.start();

            // Start the JADE platform
            //rt.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void PopulateProductsArray() {
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
                Globals.products.add(name);




            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();

        }
    }
}
