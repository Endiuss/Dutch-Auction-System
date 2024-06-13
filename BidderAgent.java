package AuctionProject;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BidderAgent extends Agent {
    private String strategy;
    private double balance = 1000.0;

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            strategy = (String) args[0];
        }
        System.out.println(getLocalName() + " is starting with strategy: " + strategy);

        addBehaviour(new BidBehaviour());
    }

    private class BidBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                String content = msg.getContent();
                if (content.startsWith("won")) {
                    double winningPrice = Double.parseDouble(content.split(" ")[1]);
                    balance -= winningPrice;
                    System.out.println(getLocalName() + " won the auction with price: " + winningPrice + ", new balance: " + balance);
                } else if (content.equals("ended")) {
                    System.out.println(getLocalName() + " received auction end notification.");
                } else {
                    double price = Double.parseDouble(content);
                    System.out.println(getLocalName() + " received price: " + price);
                    if (balance >= price && shouldBid(price)) {
                        double bid = generateBid(price);
                        ACLMessage bidMessage = new ACLMessage(ACLMessage.INFORM);
                        bidMessage.setContent(String.valueOf(bid));
                        bidMessage.addReceiver(msg.getSender());
                        send(bidMessage);
                        //System.out.println(getLocalName() + " made a bid: " + bid + ", remaining balance: " + balance);
                    }
                }
                saveBalanceToFile();
            } else {
                block();
            }
        }

        private double generateBid(double price) {
            switch (strategy) {
                case "naive":
                    return price - 1;
                case "strategic":
                    return price * 0.9;
                case "collusive":
                    return price * 0.8;
                case "fibonacci_0.786":
                    return price * 0.786;
                case "fibonacci_0.618":
                    return price * 0.618;
                case "fibonacci_0.5":
                    return price * 0.5;
                case "fibonacci_0.382":
                    return price * 0.382;
                case "fibonacci_0.236":
                    return price * 0.236;
                default:
                    return price - 1;
            }
        }

        private boolean shouldBid(double price) {
            switch (strategy) {
                case "naive":
                    return true;
                case "strategic":
                    return price <= balance * 0.9;
                case "collusive":
                    return price <= balance * 0.8;
                case "fibonacci_0.786":
                    return price <= balance * 0.786;
                case "fibonacci_0.618":
                    return price <= balance * 0.618;
                case "fibonacci_0.5":
                    return price <= balance * 0.5;
                case "fibonacci_0.382":
                    return price <= balance * 0.382;
                case "fibonacci_0.236":
                    return price <= balance * 0.236;
                default:
                    return true;
            }
        }

        private void saveBalanceToFile() {
            String filename = getLocalName() + "_balance.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write("Current balance: " + balance + "\n");
            } catch (IOException e) {
                System.err.println("Error writing balance to file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
