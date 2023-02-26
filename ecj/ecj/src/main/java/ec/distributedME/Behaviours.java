package ec.distributedME;

import ec.Setup;

import java.util.ArrayList;
import java.util.Arrays;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * Behaviours.java
 * 
 * Instance of a single behaviour of the solution
 * Holds the intervals and name for the behaviour
 * 
 * Created: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 * 
 * Parameter example:
 * map.behaviour.0.name = "behaviour1"
 * map.behaviour.0.interval.0.ubound = 5
 * map.behaviour.0.interval.1.ubound = 10
 *
 * map behaviour.1.name = "behaviour2"
 * map.behaviour.1.interval.0.ubound = 12.5
 * map.behaviour.1.interval.1.ubound = 15.75
 * map.behaviour.1.interval.2.ubound = 25
 * map.behaviour.1.interval.3.ubound = 27.1
 * map.behaviour.1.interval.4.ubound = max
 *
 * max signifies max value for doubles
 */
public class Behaviours implements Setup, Cloneable {
    private static final long serialVersionUID = 1;

    /** The species for individuals in the behaviours. */
    public String name;

    /** Stores all the limit values of a behaviour */
    public double[] limits;

    /** Minimum behaviour value */
    public double min;

    /** String version of each  */
    public ArrayList<String> limitNames;

    //Parameter names for ECJ
    public static final String P_BSIZE     = "intervals";
    public static final String P_INT       = "interval";
    public static final String P_UBOUND    = "ubound";
    public static final String P_NAME      = "name";
    public static final String P_MIN       = "min";

    public Behaviours emptyClone() {
        try {
            Behaviours m = (Behaviours)clone();
            return m;
        } //try
        catch(CloneNotSupportedException e) {
            throw new InternalError();
        } //catch
    } //emptyClone

    public void clear() {
        name = "";
        for (int i = 0 ; i < limits.length ; i ++)
            limits[i] = 0;
    } //clear

    public void setup(final EvolutionState state, final Parameter base) {
        //Name of the behaviour
        name = state.parameters.getString(base.push(P_NAME), null);
        //Total intervals
        int intervals = state.parameters.getInt(base.push(P_BSIZE), null);
        //Upper limits of behaviour intervals
        limits = new double[intervals];
        limitNames = new ArrayList<String>();

        if (state.parameters.exists(base.push(P_MIN), null)) {
            min = state.parameters.getDouble(base.push(P_MIN), null);
        }
        else {
            min = 0;
        }

        //Parsing parameter string into intervals just in case max is used
        for (int i = 0 ; i < intervals ; i ++) {
            String interval = state.parameters.getString(
                    base.push(P_INT).push("" + i).push(P_UBOUND), null
            );
            
            if (interval.equals("max"))
                limits[i] = Double.MAX_VALUE;
            else
                limits[i] = Double.parseDouble(interval);
        } //for
    } //setup

    /** Gets the limit value at a certain index */
    public double getLimit(int index) { return limits[index]; }

    /** Returns the total number of limits */
    public int totalLimits() { return limits.length; }

    /** Gets the name of the behaviour */
    public String getName() { return name; };

    /** Returns all the limits in string form as an array */
    public String[] getAllNames() { return Arrays.copyOf(limitNames.toArray(), limitNames.toArray().length, String[].class); }
    
    @Override
    public String toString() {
        String output = "Behaviour " + name + "\n";

        for (int i = 0 ; i < limits.length ; i ++) {
            output += limitNames.get(i) + ": " + limits[i] + "\n";
        }
        return output;
    }
} //Behaviours