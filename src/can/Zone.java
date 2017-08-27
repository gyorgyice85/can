package can;

import javafx.util.Pair;

/**
 * Created by gyorgyi on 22/08/17.
 */
public class Zone {

    /** Bottom left corner */
    private double x1, y1;
    /** Top right corner */
    private double x2, y2;

    public Zone(double x1, double x2, double y1, double y2) {

        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    /**
     * Decides whether point (x; y) is in this zone
     * @param x
     * @param y
     * @return true when the point is in the zone
     */
    public boolean contains(double x, double y) {
        if((x >= x1 && x <= x2) && (y >= y1 && y <= y2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Decides whether this zone and zone z are neighbours
     * @param z zone
     * @return true when this zone and zone z are neighbours
     */
    public boolean isNeighbour(Zone z) {

        if(sectionsOverlap(z.x1, z.x2, this.x1, this.x2) && sectionsTouch(z.y1, z.y2, this.y1, this.y2)) {
            return true;
        }else if(sectionsOverlap(z.y1, z.y2, this.y1, this.y2) && sectionsTouch(z.x1, z.x2, this.x1, this.x2)) {
            return true;
        }else {
            return false;
        }
    }


    /**
     * Determines whether sections a and b overlap
     * @param a1 starting point of section a
     * @param a2 end point of section a
     * @param b1 starting point of section b
     * @param b2 end point of section b
     * @return true when the two section overlap
     */
    private static boolean sectionsOverlap(double a1, double a2, double b1, double b2) {
        if(((a2 > b1) && (a2 <= b2)) || ((b2 > a1) && (b2 <= a2))) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * Determines whether sections a and b touch each other
     * @param a1 starting point of section a
     * @param a2 end point of section a
     * @param b1 starting point of section b
     * @param b2 end point of section b
     * @return true when the two section touch each other
     */
    private static boolean sectionsTouch(double a1, double a2, double b1, double b2) {
        if((a2 == b1) || (b2 == a1)) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * Split a zone along its longest side into two new zones.

     * @apiNote The contents of the node(s) owning this zone are not modified by this method, and thus has to be done elsewhere.
     *
     * @return the new pair of zones created by splitting this zone
     */
    public Pair<Zone,Zone> split() {

        // we split the zone along the longest side
        if (getLengthX() > getLengthY()) {

            double midX = x1 + getLengthX() / 2.0;
            return new Pair<Zone, Zone>(new Zone(x1, midX, y1, y2), new Zone(midX, x2, y1, y2));

        } else {

            double midY = y1 + getLengthY() / 2.0;
            return new Pair<>(new Zone(x1, x2, y1, midY), new Zone(x1, x2, midY, y2));
        }
    }

    /**
     * Get the length of the Y side of the zone
     */
    private double getLengthY() {
        return y2 - y1;
    }

    /**
     * Get the length of the X side of the zone
     */
    private double getLengthX() {
        return x2 - x1;
    }

    /**
     * Merge two zones and create a single one from the merged zones.
     * The two zones have to be neighbours and share a common side of the same length in order to be mergeable.
     *
     * @param z1 The first zone to merge
     * @param z2 The second zone to merge
     * @return the zone created by merging the two zones
     */
    public static Zone merge(Zone z1, Zone z2) {

        if (!z1.isNeighbour(z2)) {
            throw new IllegalArgumentException("ERROR: " + z1 + " and " + z2 + " are not neighbours, and thus cannot be merged.");

        } else if (z1.getLengthX() == z2.getLengthX() && z1.x1 == z2.x1 && z1.x2 == z2.x2) {

            // the two zones share an X-side of the same length, now we check which one is above the other
            if (z1.y2 == z2.y1) {

                // z1 is below z2

                return new Zone(z1.x1, z1.x2, z1.y1, z2.y2);
            } else {

                // z1 is above z2

                return new Zone(z1.x1, z1.x2, z2.y1, z1.y2);
            }

        } else if (z1.getLengthY() == z2.getLengthY() && z1.y1 == z2.y1 && z1.y2 == z2.y2) {

            // the two zones share a Y-side of the same length, now we check which one is right to the other
            if (z1.x2 == z2.x1) {

                // z2 is right to z1

                return new Zone(z1.x1, z2.x2, z1.y1, z1.y2);
            } else {

                // z2 is left to z1

                return new Zone(z2.x1, z1.x2, z1.y1, z1.y2);
            }

        } else {
            throw new IllegalArgumentException("ERROR: " + z1 + " and " + z2 + " are neighbours, but do not have sides of the same length," +
                    " and thus cannot be merged.");
        }
    }

    @Override
    public String toString() {
        return "Zone{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Zone zone = (Zone) o;

        if (Double.compare(zone.x1, x1) != 0) return false;
        if (Double.compare(zone.y1, y1) != 0) return false;
        if (Double.compare(zone.x2, x2) != 0) return false;
        return Double.compare(zone.y2, y2) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x1);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y1);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(x2);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y2);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
