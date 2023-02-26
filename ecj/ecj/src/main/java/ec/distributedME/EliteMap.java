package ec.distributedME;

import ec.util.Parameter;
import ec.EvolutionState;
import ec.Individual;
import ec.Setup;
import ec.ECDefaults;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 * EliteMap.java
 * 
 * Created: 25 January, 2020
 * @author Derek Hon
 * @version 1.0
 * 
 * Parameter example: map.behaviours = 2 map.behaviour.0.intervals = 2
 * map.behaviour.1.intervals = 5 map.startInds = 8
 */
public class EliteMap implements Setup, Cloneable {
    private static final long serialVersionUID = 1;

    public Behaviours[] behaviours;

    /** How many individuals have we added to the initial map? */
    int individualCount;

    /** Map parameters */
    public static final String P_STARTING_INDS           = "starting-individuals";

    public static final String P_MAP                     = "map";
    /** Parameter for number of behaviours in the map */
    public static final String P_SIZE                    = "behaviours";
    public static final String P_BEHAVIOUR               = "behaviour";
    /** Species of the individual */
    public static final String P_SPECIES                 = "species";
    /** Starting individuals for the map taken as a percentage of the max amount of individuals in the map */
    public static final String P_START_INDS              = "startInds";
    public static final String P_NAME                    = "name";
    public static final String NUM_INDIVIDUALS_PREAMBLE  = "Number of Individuals: ";
    public static final String INDIVIDUAL_INDEX_PREAMBLE = "Individual Number: ";

    /** Species for the map */
    public MapElitesGPSpecies species;

    /** Arraylist used as the map with elements of elites */
    // public ArrayList<Elite> MAP;

    /** HashMap used to house elite arhcive */
    Map<String, Elite> eliteHashMap;

    /** Size of the map based off of the intervals */
    public int mapSize;

    /** Upper limit of evaluations when populating the map so it doesn't take forever */
    private int populateEvaluations;

    /** Name of the submap */
    String name; 


    public Parameter file;

    public Parameter defaultBase() {
        return ECDefaults.base().push(P_BEHAVIOUR);
    }

    public EliteMap emptyClone() {
        try {
            EliteMap m = (EliteMap) clone();
            m.species =  species;
            // m.MAP = new ArrayList<>();
            m.eliteHashMap = new HashMap<>();
            return m;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    // public EliteMap emptyClone() {
    //     try {
    //         EliteMap m = (EliteMap) clone();
    //         m.species = species;
    //         m.Map = new ArrayList<Elite>(); //empty
    //         return m;
    //     } catch (CloneNotSupportedException e) {
    //         throw new InternalError();
    //     }
    // }

    public void clearMap() {
        this.eliteHashMap.clear();
    }

    public void setup(final EvolutionState state, Parameter base) {

        // this.MAP = new ArrayList<Elite>();
        this.eliteHashMap = new HashMap<>();
        this.mapSize = 1;
        this.populateEvaluations = 0;

        name = state.parameters.getString(base.push(P_NAME), null);

        // Number of behaviours
        Parameter size = base.push(P_SIZE);
        int numBehaviours = state.parameters.getInt(size, null, 1);
        if (numBehaviours != 2)
            state.output.fatal("Number of behaviours per island needs to be 2.", base.push(P_SIZE));

        this.behaviours = new Behaviours[numBehaviours];

        // species
        this.species = (MapElitesGPSpecies) state.parameters.getInstanceForParameter(base.push(P_SPECIES), null,
        MapElitesSpecies.class);
        ((MapElitesGPSpecies) species).setup(state, base.push(P_SPECIES));

        double mapPercent = 0;
        int[] intervalValues = new int[2];
        
        // Set up behaviour limits
        for (int i = 0; i < numBehaviours; i++) {
            // behaviour parameter
            Parameter p = base.push(P_BEHAVIOUR).push("" + i);
            if (!state.parameters.exists(p, null)) {
                int defaultBehaviour = state.parameters.getInt(p, null, 0);
                if (defaultBehaviour >= 0) {
                    state.output.warning("Using behaviour " + defaultBehaviour + " as default for behaviour " + i);
                    p = base.push(P_BEHAVIOUR).push("" + defaultBehaviour);
                }
            }

            // Behaviour handling
            Parameter def = defaultBase();

            int intervals;

            intervals = state.parameters.getInt(p.push(Behaviours.P_BSIZE), null, 1);
            if (intervals <= 0) {
                state.output.fatal("Number of intervals must be >= 1.\n", p.push(Behaviours.P_BSIZE),
                        def.push(Behaviours.P_BSIZE));
            }
            intervalValues[i] = intervals;

            this.mapSize *= intervals;

            this.behaviours[i] = (Behaviours) (state.parameters.getInstanceForParameterEq(p, null, Behaviours.class));
            this.behaviours[i].setup(state, p);
        } // for loop

        // state.output.fatal(Arrays.toString(intervalValues));

        state.output.message("Total Map Size: " + mapSize);

        if (state.parameters.exists(MapElitesDefaults.base().push(P_STARTING_INDS), null)) {
            mapPercent = state.parameters.getDouble(MapElitesDefaults.base().push(P_STARTING_INDS), null);
            if (mapPercent < 0.0) // uh oh
                state.output.error("Starting Individual Percent must be between 0.0 and 1.0 inclusive.",
                        MapElitesDefaults.base().push(P_STARTING_INDS), null);
            state.output.message("Starting individuals will be " + mapPercent + "% of the map: " + (int)(mapPercent * getTotalSize()) + " individuals");
        } else {
            mapPercent = 0.5;
            state.output.message("Starting individuals not defined: using 50% of the map size.");
        }

        if (mapPercent > 1.0) {
            individualCount = (int) mapPercent;
        } else {
            individualCount = (int) (getTotalSize() * mapPercent);
        }
    } // setup
    
    /**
     * Previous version with array list implementation
     * @param state
     * @param e
     * @param submap
     */
    public void populate(EvolutionState state, int thread, int submap) {
        int maxEvals = this.mapSize * 10;
        state.output.message("Populating " + individualCount + " individuals or evaluating " + maxEvals + " times...");
        
        while (this.eliteHashMap.size() < individualCount && populateEvaluations < maxEvals) {

            Elite e = ((MapElitesGPSpecies) species).newIndividual(state, thread);
            ((DistributedMEEvolutionState) state).evaluateIndividual(e, submap);
            
            newEliteProcess(state, e);

            populateEvaluations ++;

            if (populateEvaluations % 50 == 0) {
                System.out.println("Evaluation: " + populateEvaluations);
            }
        } // for

        state.output.message("Duplicate check after populating...");
        duplicateCheck(state);
        state.output.message("Map populated.");
    } // populate


    public void newEliteProcess(EvolutionState state, Elite e) {
        Elite argElite = evaluateBehaviour(state, e);

        String key = argElite.getKey();

        if (eliteHashMap.containsKey(key)) {
            Elite temp = eliteHashMap.get(key);

            if (((KozaFitness) argElite.fitness).betterThan(temp.fitness)) {
                eliteHashMap.put(key, (Elite) argElite.clone());
            }
        }
        else {
            eliteHashMap.put(key, (Elite) argElite.clone());
        }
    }

    public Elite evaluateBehaviour(EvolutionState state, Elite e) {
        double[] behaviourValues = e.getBehaviours();

        double[] tempLimits = new double[behaviours.length];
        String key = "";
        int[] position = new int[2];
        int counter = 0;

        for (int i = 0; i < behaviours.length; i++) {
            key += behaviours[i].name;
            double behaviourValue = behaviourValues[i];

            for (double val : behaviours[i].limits) {
                if (behaviourValue < val || (val == behaviours[i].limits[behaviours[i].totalLimits() - 1] && behaviourValue <= val)) {
                    tempLimits[i] = val;
                    key += "" + val;
                    position[i] = counter;
                    break;
                } // if
                counter ++;
            } // for
        } // for

        e.setKey(key);
        e.setLimits(tempLimits);
        e.setBehaviourPosition(position);

        return (Elite) e.clone();
    }
    
    public void saveAll(EvolutionState state, int submap) {
        // state.output.message("in save all");
        for (Elite e : this.eliteHashMap.values()) {
            Elite tempOne = (Elite) e.clone();
            // state.output.message(tempOne.toString());
            BufferedImage tempImg = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
            int[][] values = tempOne.getPixels();
            for (int imageX = 0 ; imageX < 128 ; imageX ++) {
                for (int imageY = 0 ; imageY < 128 ; imageY ++) {
                    tempImg.setRGB(imageX, imageY, values[imageX][imageY]);
                                    // if (e.getPixels()[x][y] !=  result.getRGB(x, y))
                                    //     state.output.fatal("different rgb");
                }
            }
            try {
                ImageIO.write(tempImg, "png", new File(System.getProperty("user.dir") + "/haarWavelet/ME/test/eval" + state.evaluations + tempOne.getKey() + ".png"));
            } catch (IOException exception) {
                state.output.fatal(exception + "");
            }
        }
    }

    public int generateValues(double[] values) {
        String key = "";
        for (int i = 0; i < behaviours.length; i++) {
            for (double val : behaviours[i].limits) {
                if (values[i] <= val) {
                    key += "" + (int)val;
                    break;
                } // if
            } // for
        } // for
        return Integer.parseInt(key);
    } // generateKey

    /**
     * 
     * MAP OPERATIONS
     * 
     */
    public void print(EvolutionState state) {
        for (Elite e : this.eliteHashMap.values()) {
            state.output.message(e.getKey() + "\t" + Arrays.toString(e.getLimits()));
        }

        state.output.message("\n");
    }

    public Individual[] asIndArray() {
        Elite[] old = asArray();
        Individual[] newInd = new Individual[old.length];

        for (int i = 0; i < old.length; i++)
            newInd[i] = (Individual) old[i];

        return newInd;
    } // asIndArray

    public Elite[] asArray() {
        return (Elite[]) this.eliteHashMap.values().toArray(new Elite[0]);
    } // asArray

    public int getTotalSize() {
        return this.mapSize;
    } // getSize

    public Behaviours[] getBehaviours() {
        return this.behaviours;
    }

    public int getOccupants() {
        return this.eliteHashMap.size();
    }

    public Elite getEliteByKey(String key) {
        return this.eliteHashMap.get(key);
    }

    // public Elite getEliteByLimits(double[] limits) {
    //     Iterator<Elite> iterator = this.MAP.iterator();
    //     while (iterator.hasNext()) {
    //         Elite temp = iterator.next();
    //         if (Arrays.equals(limits,temp.getLimits()))
    //             return temp;
    //     }
    //     return null;
    // }

    public Elite getElite(int index) {
        Elite[] arr = (Elite[]) this.eliteHashMap.values().toArray(new Elite[0]);
        return arr[index];
    }

    public Elite getEliteForMigration(int index) {
        Elite[] arr = (Elite[]) this.eliteHashMap.values().toArray(new Elite[0]);
        Elite e = arr[index];
        this.eliteHashMap.remove(e.getKey());
        return e;
    }

    public void duplicateCheck(EvolutionState state) {
        ArrayList<Elite> MAP = new ArrayList<Elite>(this.eliteHashMap.values());
        
        for (int i = 0 ; i < MAP.size() ; i ++) {
            double[] limitOne = MAP.get(i).getLimits();
            for (int j = i + 1 ; j < MAP.size() ; j ++) {
                double[] limitTwo = MAP.get(j).getLimits();
                if (Arrays.equals(limitOne, limitTwo)) {
                    for (Elite elite : MAP)
                        state.output.message(elite.getKey() + "\t" + Arrays.toString(elite.getLimits()));
                    state.output.fatal("Duplicate: " + MAP.get(i).getKey() + "\t" + Arrays.toString(MAP.get(i).getLimits()) + "\tpopulate evaluation: " + populateEvaluations);
                }
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean compareLimits(Elite one, Elite two) {
        return Arrays.equals(one.getLimits(), two.getLimits());
    }

    public String keysToString() {
        ArrayList<Elite> MAP = (ArrayList<Elite>) this.eliteHashMap.values();

        String output = "[";
        for (int i = 0; i < MAP.size(); i++) {
            output += i + ": " + MAP.get(i).getKey() + ", ";
        }
        output = output.substring(0, output.length() - 2);
        output += "]";
        return output;
    }
} // EliteMap