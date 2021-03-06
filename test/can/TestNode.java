package can;

import org.junit.jupiter.api.Test;

/**
 * Created by gyorgyi on 27/08/17.
 */
public class TestNode {

    @Test
    public void testSplit() {

        Zone zone = new Zone(0, 1, 0, 1);
        Node node1 = new Node(zone);

        Node node2 = new Node(zone);
        node1.addPeer(node2);

        Node node3 = new Node(zone);
        node1.addPeer(node3);

        Node node4 = new Node(zone);
        node1.addPeer(node4);

        // this should lead to a split
        Node node5 = new Node(zone);
        node1.addPeer(node5);

        //Es gibt die Peers von Node aus
        for (Node node : node1.getPeers()) {
            System.out.println(node.getZone());
        }
        // Es gibt aus, ob alle Peers von Node die selbe Zone haben
        for (Node node : node1.getPeers().get(0).getPeers()) {
           System.out.println(node.getZone());
        }

        System.out.println("----------------");
        System.out.println(node1);
        System.out.println(node2);
        System.out.println(node3);
        System.out.println(node4);
        System.out.println(node5);
    }
}
