import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Random;

public class CustomerAgent extends Agent {
    protected void setup() {
        System.out.println("Customer Agent " + getAID().getName() + " is ready.");

        addBehaviour(new OrderConfirmationBehaviour());
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(getAID("OrderProcessing"));
                Random random = new Random();
                int randomProduct = random.nextInt(5);
                // Set the content of the message as the item to order
                msg.setContent(Globals.products.get(randomProduct));

                send(msg);

            }
        });
    }



    private class OrderConfirmationBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Handle message from delivery agent
                String itemName = msg.getContent();
                System.out.println(itemName + " was received by the customer ");
                System.out.println("--------------------------------------");
            } else {
                block();
            }
        }
    }
}
