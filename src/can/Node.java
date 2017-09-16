package can;

import javafx.util.Pair;
import java.util.*;

/**
 * Created by gyorgyi on 27/08/17.
 */
public class Node {

    /** Die CAN Zone dieses Nodes. */
    private Zone zone;
    /** Liste von Peers dieses Nodes. */
    private ArrayList<Node> peers;

    public static int MAX_PEERS = 3;
    private int peerCount = 0;
    /** Das Set der Fotos dieses Nodes. Key: FotoID (integer), Value: filename (string) */
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
     * Zuweisung ein neues Peer zu diesem Node und zu zeinen Peers.
     * Alle Fotos werden zu dem neuen Peer zugewiesen.
     * Wenn die Anzahl von Peers groesser als MAX_PEERS, die Zone wird aufgeteilt und
     * die Nodes werden updated.
     *
     * @param node das neue Peer von Node
     */
    public void addPeer(Node node) {

        if (!node.getZone().equals(zone)) {
            throw new IllegalArgumentException("ERROR: " + node + " has a different zone than this nodes zone.");
        }

        // fuegt ein neues Peer zu Peers dieses Nodes ein
        for (Node peer : peers) {
            peer.peers.add(node);
            peer.peerCount++;
            node.peers.add(peer);
            node.peerCount++;
        }

        // fuegt ein neues Peer zu diesem Node ein
        this.peers.add(node);
        this.peerCount++;
        node.peers.add(this);
        node.peerCount++;


        // fuegt alle Fotos zu neuem Peer
        node.fotos.putAll(fotos);

        if (peerCount == MAX_PEERS + 1) {
            splitZone();
        }
    }

    /**
     * Methode fuer Splitting einer Zone
     */
    private void splitZone() {

        // Pair Klasse um zwei Zonen zurueckzugeben

        Pair<Zone, Zone> newZones = zone.split();
        Zone zoneA = newZones.getKey();   //nicht gleich mit Key von Foto
        Zone zoneB = newZones.getValue(); //nicht gleich mit Value von Foto

        // Die erste Haelfte der Peers werden zum ZoneA zugewiesen
        ArrayList<Node> peersInZoneA = new ArrayList<Node>(peers.subList(0, ((MAX_PEERS + 1) / 2) + 1));
        // Die zweite Haelfte der Peers mit diesem Node werden zum ZoneB zugewiesen
        ArrayList<Node> peersInZoneB = new ArrayList<Node>(peers.subList(((MAX_PEERS + 1) / 2 )+ 1, MAX_PEERS + 1));
        peersInZoneB.add(this);

        // Liste fuer Fotos aus ZoneA und ZoneB zu erzeugen
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


        // (1) Zuweisung die neu gesplittete Zone(zoneA) zu allen Peers von sublist peersInZoneA
        // (2) Entfernung die Peers aus der Sublist peersInZoneB
        // (3) Entfernung die Fotos aus der Sublist peersInZoneB
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
    /**
     * Methode um ein Peer aus der Liste von Node zu entfernen
     * @param node der zu entfernende Peer
     */
    public void removePeer(Node node) {
        if (!peers.contains(node)) {
            throw new IllegalArgumentException("ERROR: this node does not contain " + node);
        }

        peers.remove(node);
        peerCount--;
    }

    /**
     * Methode um alle Peer aus der Liste von Node zu entfernen
     * @param nodes die zu entfernende Peers
     */
    private void removePeers(List<Node> nodes) {
        for (Node node : nodes) {
            removePeer(node);
        }
    }

    /**
     * Methode um Fotos zu entfernen
     * @param fotoIDs die zu entfernende Fotos
     */
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
