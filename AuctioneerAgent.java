package AuctionProject;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class AuctioneerAgent extends Agent {
    private double initialPrice = 100.0;
    private double decrement = 10.0;
    private int maxRounds = 50;
    private int roundCounter = 0;
    private List<ACLMessage> bids;

    protected void setup() {
        addBehaviour(new AuctionBehaviour());
    }

    private class AuctionBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            if (roundCounter < maxRounds) {
                bids = new ArrayList<>();
                double currentPrice = initialPrice;

                while (currentPrice > 0) {
                    System.out.println("Auctioneer sending price: " + currentPrice + " to bidders");
                    ACLMessage priceMessage = new ACLMessage(ACLMessage.INFORM);
                    priceMessage.setContent(String.valueOf(currentPrice));
                    priceMessage.addReceiver(new jade.core.AID("bidder-naive", jade.core.AID.ISLOCALNAME));
                    priceMessage.addReceiver(new jade.core.AID("bidder-strategic", jade.core.AID.ISLOCALNAME));

                    priceMessage.addReceiver(new jade.core.AID("bidder-fibonacci_0.786", jade.core.AID.ISLOCALNAME));
                    priceMessage.addReceiver(new jade.core.AID("bidder-fibonacci_0.618", jade.core.AID.ISLOCALNAME));
                    priceMessage.addReceiver(new jade.core.AID("bidder-fibonacci_0.5", jade.core.AID.ISLOCALNAME));
                    priceMessage.addReceiver(new jade.core.AID("bidder-fibonacci_0.382", jade.core.AID.ISLOCALNAME));
                    priceMessage.addReceiver(new jade.core.AID("bidder-fibonacci_0.236", jade.core.AID.ISLOCALNAME));
                    send(priceMessage);

                    // Wait for the first bid from bidders
                    long endTime = System.currentTimeMillis() + 5000;  // 5 seconds to receive bids
                    ACLMessage firstBid = null;
                    while (System.currentTimeMillis() < endTime && firstBid == null) {
                        ACLMessage bid = receive();
                        if (bid != null) {
                            firstBid = bid;
                            System.out.println("Received bid: " + bid.getContent() + " from " + bid.getSender().getLocalName());
                        }
                    }

                    if (firstBid != null) {
                        double bidAmount = Double.parseDouble(firstBid.getContent());
                        System.out.println("Auction won by " + firstBid.getSender().getLocalName() + " at price " + bidAmount);

                        ACLMessage winMessage = new ACLMessage(ACLMessage.INFORM);
                        winMessage.setContent("won " + bidAmount);
                        winMessage.addReceiver(firstBid.getSender());
                        send(winMessage);

                        // Notify all other bidders that the auction has ended
                        ACLMessage endMessage = new ACLMessage(ACLMessage.INFORM);
                        endMessage.setContent("ended");
                        endMessage.addReceiver(new jade.core.AID("bidder-naive", jade.core.AID.ISLOCALNAME));
                        endMessage.addReceiver(new jade.core.AID("bidder-strategic", jade.core.AID.ISLOCALNAME));

                        endMessage.addReceiver(new jade.core.AID("bidder-fibonacci_0.786", jade.core.AID.ISLOCALNAME));
                        endMessage.addReceiver(new jade.core.AID("bidder-fibonacci_0.618", jade.core.AID.ISLOCALNAME));
                        endMessage.addReceiver(new jade.core.AID("bidder-fibonacci_0.5", jade.core.AID.ISLOCALNAME));
                        endMessage.addReceiver(new jade.core.AID("bidder-fibonacci_0.382", jade.core.AID.ISLOCALNAME));
                        endMessage.addReceiver(new jade.core.AID("bidder-fibonacci_0.236", jade.core.AID.ISLOCALNAME));
                        send(endMessage);

                        break;
                    }

                    currentPrice -= decrement;
                }

                if (bids.isEmpty()) {
                    System.out.println("Auction ended with no winners.");
                }

                roundCounter++;
            } else {
                System.out.println("Maximum number of rounds reached. Auction ending.");
                doDelete();
            }
        }
    }
}
