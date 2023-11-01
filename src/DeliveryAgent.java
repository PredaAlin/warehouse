import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class DeliveryAgent extends Agent {
    protected void setup() {
        System.out.println("Delivery Agent " + getAID().getName() + " is ready.");

        addBehaviour(new DeliveryRequestBehaviour());
    }

    private class DeliveryRequestBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Handle delivery request from packaging agent
                String itemName = msg.getContent();

                // Deliver item to the customer
                System.out.println("Delivering " + itemName);
                ACLMessage msgCustomer = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                msgCustomer.setContent(itemName);
                msgCustomer.addReceiver(getAID("Customer"));
                send(msgCustomer);
            } else {
                block();
            }
        }
    }
}
