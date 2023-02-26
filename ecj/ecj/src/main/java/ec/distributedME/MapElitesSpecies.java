/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.distributedME;

import ec.Prototype;
import ec.Fitness;
import ec.EvolutionState;

import ec.util.Parameter;

import ec.gp.GPIndividual;

import java.util.HashMap;

import java.io.LineNumberReader;
import java.io.IOException;
import java.io.DataInput;

/**
 * MapElitesSpecies.java
 *
 * @see ec.Species
 * 
 * Modifications: 
 * 
 * Created: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */
public abstract class MapElitesSpecies implements Prototype {
    public static final String P_INDIVIDUAL = "ind";
    public static final String P_PIPE = "pipe";
    public static final String P_FITNESS = "fitness";

    /** The prototypical individual for this species. */
    public Elite i_prototype;

    /** The prototypical breeding pipeline for this species. */
    public MapElitesBreedingSource pipe_prototype;

    /** The prototypical fitness for individuals of this species. */
    public Fitness f_prototype;

    public Object clone() {
        try {
            MapElitesSpecies myobj = (MapElitesSpecies) (super.clone());
            myobj.i_prototype = (Elite) i_prototype.clone();
            myobj.f_prototype = (Fitness) f_prototype.clone();
            myobj.pipe_prototype = (MapElitesBreedingSource) pipe_prototype.clone();
            return myobj;
        } catch (Exception e) {
            throw new InternalError();
        } // never happens
    }

    /**
     * Provides a brand-new individual to fill in a population. The default form
     * simply calls clone(), creates a fitness, sets evaluated to false, and sets
     * the species. If you need to make a more custom genotype (as is the case for
     * GPSpecies, which requires a light rather than deep clone), you will need to
     * override this method as you see fit.
     */

    public Elite newIndividual(final EvolutionState state, int thread) {
        Elite newind = (Elite) (i_prototype.clone());

        // Set the fitness
        newind.fitness = (Fitness) (f_prototype.clone());
        newind.evaluated = false;

        // Set the species to me
        newind.species = this;

        // ...and we're ready!
        return newind;
    }

    /**
     * Provides an individual read from a stream, including the fitness; the
     * individual will appear as it was written by printIndividual(...). Doesn't
     * close the stream. Sets evaluated to false and sets the species. If you need
     * to make a more custom mechanism (as is the case for GPSpecies, which requires
     * a light rather than deep clone), you will need to override this method as you
     * see fit.
     */

    public Elite newIndividual(final EvolutionState state, final LineNumberReader reader) throws IOException {
        Elite newind = (Elite) (i_prototype.clone());

        // Set the fitness
        newind.fitness = (Fitness) (f_prototype.clone());
        newind.evaluated = false; // for sanity's sake, though it's a useless line

        // load that sucker
        newind.readIndividual(state, reader);

        // Set the species to me
        newind.species = this;

        // and we're ready!
        return newind;
    }

    /**
     * Provides an individual read from a DataInput source, including the fitness.
     * Doesn't close the DataInput. Sets evaluated to false and sets the species. If
     * you need to make a more custom mechanism (as is the case for GPSpecies, which
     * requires a light rather than deep clone), you will need to override this
     * method as you see fit.
     */

    public Elite newIndividual(final EvolutionState state, final DataInput dataInput) throws IOException {
        Elite newind = (Elite) (i_prototype.clone());

        // Set the fitness
        newind.fitness = (Fitness) (f_prototype.clone());
        newind.evaluated = false; // for sanity's sake, though it's a useless line

        // Set the species to me
        newind.species = this;

        // load that sucker
        newind.readIndividual(state, dataInput);

        // and we're ready!
        return newind;
    }

    /**
     * The default version of setup(...) loads requested pipelines and calls
     * setup(...) on them and normalizes their probabilities. If your individual
     * prototype might need to know special things about the species (like
     * parameters stored in it), then when you override this setup method, you'll
     * need to set those parameters BEFORE you call super.setup(...), because the
     * setup(...) code in Species sets up the prototype.
     * 
     * @see Prototype#setup(EvolutionState,Parameter)
     */

    public void setup(final EvolutionState state, final Parameter base) {
        Parameter def = defaultBase();

        // load the breeding pipeline
        pipe_prototype = (MapElitesBreedingPipeline) (state.parameters.getInstanceForParameter(base.push(P_PIPE),
                def.push(P_PIPE), MapElitesBreedingPipeline.class));
        pipe_prototype.setup(state, base.push(P_PIPE));

        // I promised over in BreedingSource.java that this method would get called.
        state.output.exitIfErrors();
        // load our individual prototype
        i_prototype = (Elite) (state.parameters.getInstanceForParameter(base.push(P_INDIVIDUAL), def.push(P_INDIVIDUAL),
                GPIndividual.class));
        // set the species to me before setting up the individual, so they know who I am
        i_prototype.species = this;
        i_prototype.setup(state, base.push(P_INDIVIDUAL));

        // load our fitness
        f_prototype = (Fitness) state.parameters.getInstanceForParameter(base.push(P_FITNESS), def.push(P_FITNESS),
                Fitness.class);
        f_prototype.setup(state, base.push(P_FITNESS));
    }

    /** Copied from ec.Species.java */
    public HashMap<String, Object> buildMisc(EvolutionState state, int subpopIndex, int thread) {
        return new HashMap<String, Object>();
    }

}
