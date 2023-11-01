import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PackagingAgent extends Agent {
    protected void setup() {
        System.out.println("Packaging Agent " + getAID().getName() + " is ready.");

        addBehaviour(new PackagingRequestBehaviour());
    }

    private class PackagingRequestBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Handle packaging
                String itemName = msg.getContent();

                // Package order and send it to the Delivery agent
                System.out.println("Packaging " + itemName);
                ACLMessage msgDelivery = new ACLMessage(ACLMessage.REQUEST);
                msgDelivery.setContent(itemName);
                msgDelivery.addReceiver(getAID("Delivery"));
                send(msgDelivery);
            } else {
                block();
            }
        }
    }
}
