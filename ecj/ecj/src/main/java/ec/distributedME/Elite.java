package ec.distributedME;

import java.util.Arrays;
import java.util.Comparator;

import ec.EvolutionState;
import ec.gp.GPIndividual;
// import java.awt.image.BufferedImage;
import ec.util.Parameter;

/**
 * Elite.java
 * 
 * Builds off of GPIndividual in order to hold the 
 * behaviour values that have been evaluated for it
 * 
 * 
 * Created: 25 January, 2022
 * @author Derek Hon
 */

public class Elite extends GPIndividual {
    /** Values of behaviours evaluated */
    private double[] behaviourValues;
    // public BufferedImage render;
    /** Species of the Elite */
    MapElitesSpecies species;

    /** MAP Key */
    private String identifier;

    private int eliteValues;

    /** Running fitness of the cell it fills probably should be changed into the MAP */
    private double runningFitness = 0.0;

    /** How many times it has replaced a cell probably should also be integreated into the MAP  */
    private int replacementCounter = 0;

    /** Values of the limits in the MAP based off of the behaviour values */
    private double[] limits;

    /** Pixels for procedural texture */
    private int pixels[][];

    /** Position for check */
    private int[] position;

    private int[] behaviourPosition;

    public void setup (final EvolutionState state, final Parameter base) {
        super.setup(state, base);
    }

    public void setKey(String key) {
        this.identifier = key;
    }

    public void setValues(int value) {
        this.eliteValues = value;
    }

    public void setFitness(double fitness) {
        this.runningFitness = fitness; 
    }

    public void setCounter(int counter) {
        this.replacementCounter = counter + 1;
    }

    public void replaceFitness(double fitnessOne, double fitnessTwo) {
        this.runningFitness = fitnessOne + fitnessTwo;
    }

    public int getValues() {
        return this.eliteValues;
    }

    public String getKey() {
        return this.identifier;
    }

    public double[] getBehaviours() {
        return this.behaviourValues;
    }

    public double getRunningFitness() {
        return this.runningFitness;
    }

    public int getReplacementCounter() {
        return this.replacementCounter;
    }

    public void setLimits(double[] limit) {
        this.limits = limit.clone();
    }

    public double[] getLimits() {
        return this.limits;
    }

    public void setBehaviours(double[] values) {
        this.behaviourValues = values.clone();
    }

    public void setPixels(int[][] pixel) {
        this.pixels = pixel.clone();
    }

    public int[][] getPixels() {
        return this.pixels;
    }

    public void setPosition(int[] positionsToSet) {
        this.position = positionsToSet.clone();
    }

    public int[] getPosition() {
        return this.position;
    }

    public void setBehaviourPosition(int[] position) {
        this.behaviourPosition = position;
    }

    public int[] getBehaviourPosition() {
        return this.behaviourPosition;
    }

    public Object clone() {
        Elite myobj = (Elite) (super.clone());
        myobj.behaviourValues = new double[this.behaviourValues.length];
        for (int i = 0 ; i < behaviourValues.length ; i ++)
            myobj.behaviourValues[i] = this.behaviourValues[i];

        myobj.limits = this.limits.clone();
        myobj.behaviourPosition = this.behaviourPosition.clone();
            
        // myobj.render = render;
        myobj.species = species;
        myobj.identifier = identifier;
        myobj.eliteValues = eliteValues;
        myobj.pixels = this.pixels.clone();
        myobj.replacementCounter = replacementCounter;
        myobj.runningFitness = runningFitness;
        return myobj;
    } // clone

    public Elite lightClone() {
        // a light clone
        Elite myobj = (Elite) (super.lightClone());
        myobj.behaviourValues = behaviourValues;
        // myobj.render = render;
        myobj.species = species;

        return myobj;
    } // lightClone

    public static Comparator<Elite> COMPARE_BY_KEY = new Comparator<Elite>() {
        public int compare(Elite one, Elite two) {
            return one.getKey().compareTo(two.getKey());
        }
    };

    @Override
    public String toString() {
        String output = "";
        output += getKey() + " behaviours: " + Arrays.toString(behaviourValues) + " limits: " + Arrays.toString(limits);
        return output;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((pixels == null) ? 0 : pixels.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        
        Elite other = (Elite) obj;
        if (identifier == null && other.getKey() != null)
            return false;
        if (!(identifier.equals(other.getKey())))
            return false;
        if (pixels == null && other.getPixels() != null)
            return false;
        if (!(Arrays.equals(pixels, other.getPixels())))
            return false;
        return true;
    }

    public boolean compare(Elite e) {
        for (int i = 0 ; i < this.behaviourValues.length ; i ++) {
            if (e.behaviourValues[i] != this.behaviourValues[i])
                return false;
        }
        return true;
    }
}