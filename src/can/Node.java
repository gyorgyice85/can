package can;

import javafx.util.Pair;
import java.util.*;

/**
 * Created by gyorgyi on 27/08/17.
 */
public class Node {

    /** The CAN zone of this node. */
    private Zone zone;
    /** The set of peer node of this node. */
    private ArrayList<Node> peers;

    public static int MAX_PEERS = 3;
    private int peerCount;
    /** The set of fotos stored locally in this can node, as mapping of fotoID (integer) to filename (string). */
    private HashMap<Integer, String> fotos;

    public Node(Zone zone) {
        this.zone = zone;
        peers = new ArrayList<>();
        fotos = new HashMap<>();
    }

    /////////////////////////////////////////////////////////
    // METHOD STUBS THAT WILL BE FULLY IMPLEMENTED BY TEEM MEMBERS ONLY:
    public void deleteLocalFoto(Integer fotoID) {

        if (!fotos.containsKey(fotoID)) {
            throw new IllegalArgumentException("ERROR: foto " + fotoID + " is not stored at this node.");
        }
        fotos.remove(fotoID);
    }

    public void insertLocalFoto(Integer fotoID, String fileName) {
        fotos.put(fotoID, fileName);
    }

    public static Double hash1(Integer fotoID) {
        return fotoID.hashCode() / new Double(Integer.MAX_VALUE);
    }

    public static Double hash2(Integer fotoID) {
        return new Random(fotoID).nextDouble();
    }


    public static Double[] getHash(Integer fotoID) {
        return new Double[]{hash1(fotoID), hash2(fotoID)};
    }
    // METHOD STUBS THAT WILL BE IMPLEMENTED BY TEEM MEMBERS (END)
    /////////////////////////////////////////////////////////

    /**
     * Adds a new peer to this node and its current peers. It also inserts all fotos into the new peer node.
     * If the number of peers would get larger than MAX_PEERS, the zone will be split, and the nodes updated.
     *
     * @param node the new peer of the node
     */
    public void addPeer(Node node) {

        if (!node.getZone().equals(zone)) {
            throw new IllegalArgumentException("ERROR: " + node + " has a different zone than this nodes zone.");
        }

        // register new peer at this node's the current peers
        for (Node peer : peers) {
            peer.peers.add(node);
            peer.peerCount++;
            node.peers.add(peer);
            node.peerCount++;
        }

        // register new peer at this node
        this.peers.add(node);
        this.peerCount++;
        node.peers.add(this);
        node.peerCount++;


        // inserts all fotos into the new peer node
        node.fotos.putAll(fotos);

        if (peerCount == MAX_PEERS + 1) {
            splitZone();
        }
    }

    /**
     * Method for splitting a zone and updating the peers' state:
     *  (1) assign the new splitted zones to each peer
     *  (2) assign new peers according to the new zone of the peers
     *  (3) delete fotos that fall outside of the new zones of the peers
     */
    private void splitZone() {

        Pair<Zone, Zone> newZones = zone.split();
        Zone zoneA = newZones.getKey();
        Zone zoneB = newZones.getValue();

        // the first half of the peers will be moved to zoneA
        ArrayList<Node> peersInZoneA = new ArrayList<Node>(peers.subList(0, (MAX_PEERS + 1) / 2 + 1));
        // the second half of the peers plus this node will be moved to zoneB
        ArrayList<Node> peersInZoneB = new ArrayList<Node>(peers.subList((MAX_PEERS + 1) / 2 + 1, MAX_PEERS + 1));
        peersInZoneB.add(this);

        // create a list of fotos in zoneA and zoneB
        ArrayList<Integer> fotosInZoneA = new ArrayList<>();
        ArrayList<Integer> fotosInZoneB = new ArrayList<>();
        for (Integer fotoID : fotos.keySet()) {

            Double[] hash = getHash(fotoID);

            if (zoneA.contains(hash[0], hash[1])) {
                fotosInZoneA.add(fotoID);
            } else {
                fotosInZoneB.add(fotoID);
            }
        }


        // (1) assign the new splitted zones to each peer AND
        // (2) assign new peers according to the new zone of the peers AND
        // (3) delete fotos that fall outside of the new zones of the peers
        for (Node node : peersInZoneA) {
            node.setZone(zoneA);
            node.removePeers(peersInZoneB);
            node.deleteLocalFotos(fotosInZoneB);
        }

        for (Node node: peersInZoneB) {
            node.setZone(zoneB);
            node.removePeers(peersInZoneA);
            node.deleteLocalFotos(fotosInZoneA);
        }

    }

    public void removePeer(Node node) {
        if (!peers.contains(node)) {
            throw new IllegalArgumentException("ERROR: this node does not contain " + node);
        }

        peers.remove(node);
        peerCount--;
    }

    private void removePeers(List<Node> nodes) {
        for (Node node : nodes) {
            removePeer(node);
        }
    }

    private void deleteLocalFotos(List<Integer> fotoIDs) {
        for (Integer fotoID : fotoIDs) {
            deleteLocalFoto(fotoID);
        }
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public ArrayList<Node> getPeers() {
        return peers;
    }

    @Override
    public String toString() {
        return "Node{" +
                "zone=" + zone +
                ", peerCount=" + peerCount +
                '}';
    }
}
