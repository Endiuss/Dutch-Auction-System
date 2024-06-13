package AuctionProject;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class DutchAuctionMain extends Agent {
    public DutchAuctionMain() {
    }

    protected void setup() {
        // Create the JADE runtime
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController cc = rt.createMainContainer(p);

        try {
            // Create and start the Auctioneer agent
            AgentController auctioneer = cc.createNewAgent("auctioneer", "AuctionProject.AuctioneerAgent", null);

            // Wait to ensure the auctioneer is started before creating bidders


            // Create and start the Bidder agents with different strategies
            String[] strategies = {
                    "naive", "strategic",
                    "fibonacci_0.786", "fibonacci_0.618",
                    "fibonacci_0.5", "fibonacci_0.382", "fibonacci_0.236"
            };
            for (String strategy : strategies) {
                AgentController bidder = cc.createNewAgent("bidder-" + strategy, "AuctionProject.BidderAgent", new Object[]{strategy});
                bidder.start();
            }
            Thread.sleep(2000);
            auctioneer.start();

        } catch (StaleProxyException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
