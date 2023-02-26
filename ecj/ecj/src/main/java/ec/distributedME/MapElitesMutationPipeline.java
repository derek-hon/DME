/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.distributedME;

import ec.EvolutionState;
import ec.util.Parameter;

import ec.gp.GPNodeSelector;
import ec.gp.koza.GPKozaDefaults;
import ec.gp.GPNodeBuilder;
import ec.gp.GPNode;
import ec.gp.GPTree;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MapElitesMutationPipeline.java
 *
 * @see ec.gp.koza.MutationPipeline
 * Modifications are the exact same as the Crossover Pipeline
 * 
 * Created: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */
public class MapElitesMutationPipeline extends MapElitesBreedingPipeline {
    private static final long serialVersionUID = 1;

    public static final String P_NUM_TRIES = "tries";
    public static final String P_MAXDEPTH = "maxdepth";
    public static final String P_MAXSIZE = "maxsize";
    public static final String P_MUTATION = "mutate";
    public static final String P_BUILDER = "build";
    public static final String P_EQUALSIZE = "equal";
    public static final int INDS_PRODUCED = 1;
    public static final int NUM_SOURCES = 1;
    public static final int NO_SIZE_LIMIT = -1;

    /** How the pipeline chooses a subtree to mutate */
    public GPNodeSelector nodeselect;

    /** How the pipeline builds a new subtree */
    public GPNodeBuilder builder;

    /**
     * The number of times the pipeline tries to build a valid mutated tree before
     * it gives up and just passes on the original
     */
    int numTries;

    /** The maximum depth of a mutated tree */
    int maxDepth;

    /**
     * The largest tree (measured as a nodecount) the pipeline is allowed to form.
     */
    public int maxSize;

    /** Do we try to replace the subtree with another of the same size? */
    boolean equalSize;

    /** Is our tree fixed? If not, this is -1 */
    int tree;

    public Parameter defaultBase() {
        return GPKozaDefaults.base().push(P_MUTATION);
    }

    public int numSources() {
        return NUM_SOURCES;
    }

    public Object clone() {
        MapElitesMutationPipeline c = (MapElitesMutationPipeline) (super.clone());

        // deep-cloned stuff
        c.nodeselect = (GPNodeSelector) (nodeselect.clone());

        return c;
    }

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        Parameter def = defaultBase();
        Parameter p = base.push(P_NODESELECTOR).push("" + 0);
        Parameter d = def.push(P_NODESELECTOR).push("" + 0);

        nodeselect = (GPNodeSelector) (state.parameters.getInstanceForParameter(p, d, GPNodeSelector.class));
        nodeselect.setup(state, p);

        p = base.push(P_BUILDER).push("" + 0);
        d = def.push(P_BUILDER).push("" + 0);

        builder = (GPNodeBuilder) (state.parameters.getInstanceForParameter(p, d, GPNodeBuilder.class));
        builder.setup(state, p);

        numTries = state.parameters.getInt(base.push(P_NUM_TRIES), def.push(P_NUM_TRIES), 1);
        if (numTries == 0)
            state.output.fatal("Mutation Pipeline has an invalid number of tries (it must be >= 1).",
                    base.push(P_NUM_TRIES), def.push(P_NUM_TRIES));

        maxDepth = state.parameters.getInt(base.push(P_MAXDEPTH), def.push(P_MAXDEPTH), 1);
        if (maxDepth == 0)
            state.output.fatal("The Mutation Pipeline " + base + "has an invalid maximum depth (it must be >= 1).",
                    base.push(P_MAXDEPTH), def.push(P_MAXDEPTH));

        maxSize = NO_SIZE_LIMIT;
        if (state.parameters.exists(base.push(P_MAXSIZE), def.push(P_MAXSIZE))) {
            maxSize = state.parameters.getInt(base.push(P_MAXSIZE), def.push(P_MAXSIZE), 1);
            if (maxSize < 1)
                state.output.fatal("Maximum tree size, if defined, must be >= 1");
        }

        equalSize = state.parameters.getBoolean(base.push(P_EQUALSIZE), def.push(P_EQUALSIZE), false);

        tree = TREE_UNFIXED;
        if (state.parameters.exists(base.push(P_TREE).push("" + 0), def.push(P_TREE).push("" + 0))) {
            tree = state.parameters.getInt(base.push(P_TREE).push("" + 0), def.push(P_TREE).push("" + 0), 0);
            if (tree == -1)
                state.output.fatal("Tree fixed value, if defined, must be >= 0");
        }
    }

    /** Returns true if inner1 can feasibly be swapped into inner2's position */

    public boolean verifyPoints(GPNode inner1, GPNode inner2) {
        // We know they're swap-compatible since we generated inner1
        // to be exactly that. So don't bother.

        // next check to see if inner1 can fit in inner2's spot
        if (inner1.depth() + inner2.atDepth() > maxDepth)
            return false;

        // check for size
        if (maxSize != NO_SIZE_LIMIT) {
            // first easy check
            int inner1size = inner1.numNodes(GPNode.NODESEARCH_ALL);
            int inner2size = inner2.numNodes(GPNode.NODESEARCH_ALL);
            if (inner1size > inner2size) // need to test further
            {
                // let's keep on going for the more complex test
                GPNode root2 = ((GPTree) (inner2.rootParent())).child;
                int root2size = root2.numNodes(GPNode.NODESEARCH_ALL);
                if (root2size - inner2size + inner1size > maxSize) // take root2, remove inner2 and swap in inner1. Is
                                                                   // it still small enough?
                    return false;
            }
        }

        // checks done!
        return true;
    }

    public int produce(final int submap, final ArrayList<Elite> inds, final EvolutionState state,
            final int thread, HashMap<String, Object> misc) {
        int start = inds.size();

        // grab individuals from our source and stick 'em right into inds.
        // we'll modify them from there
        int n = sources[0].produce(submap, inds, state, thread, misc);

        // should we bother?
        if (!state.random[thread].nextBoolean(likelihood)) {
            return n;
        }

        DistributedMEInitializer initializer = (DistributedMEInitializer) state.initializer;
            

        // now let's mutate 'em
        for (int q = start; q < n + start; q++) {
            Elite i = (Elite) inds.get(q);

            if (tree != TREE_UNFIXED && (tree < 0 || tree >= i.trees.length))
                // uh oh
                state.output.fatal(
                        "GP Mutation Pipeline attempted to fix tree.0 to a value which was out of bounds of the array of the individual's trees.  Check the pipeline's fixed tree values -- they may be negative or greater than the number of trees in an individual");

            int t;
            // pick random tree
            if (tree == TREE_UNFIXED)
                if (i.trees.length > 1)
                    t = state.random[thread].nextInt(i.trees.length);
                else
                    t = 0;
            else
                t = tree;

            // validity result...
            boolean res = false;

            // prepare the nodeselector
            nodeselect.reset();

            // pick a node

            GPNode p1 = null; // the node we pick
            GPNode p2 = null;

            for (int x = 0; x < numTries; x++) {
                // pick a node in individual 1
                p1 = nodeselect.pickNode(state, submap, thread, i, i.trees[t]);

                // generate a tree swap-compatible with p1's position

                int size = GPNodeBuilder.NOSIZEGIVEN;
                if (equalSize)
                    size = p1.numNodes(GPNode.NODESEARCH_ALL);

                p2 = builder.newRootedTree(state, p1.parentType(initializer), thread, p1.parent,
                        i.trees[t].constraints(initializer).functionset, p1.argposition, size);

                // check for depth and swap-compatibility limits
                res = verifyPoints(p2, p1); // p2 can fit in p1's spot -- the order is important!

                // did we get something that had both nodes verified?
                if (res)
                    break;
            }

            if (res) // we're in business
            {
                p2.parent = p1.parent;
                p2.argposition = p1.argposition;
                if (p2.parent instanceof GPNode)
                    ((GPNode) (p2.parent)).children[p2.argposition] = p2;
                else
                    ((GPTree) (p2.parent)).child = p2;
                i.evaluated = false; // we've modified it
            }

            // add the new individual, replacing its previous source
            inds.set(q, i);

        }
        return n;
    }

    @Override
    public void individualReplaced(EvolutionState state, int submap, int thread, int individual) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sourcesAreProperForm(EvolutionState state) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void prepareToProduce(EvolutionState state, int submap, int thread) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void finishProducing(EvolutionState state, int submap, int thread) {
        // TODO Auto-generated method stub
        
    }
}
