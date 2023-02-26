/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.distributedME;

import ec.EvolutionState;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.util.Parameter;
import ec.util.IntBag;

import ec.gp.GPNodeSelector;
import ec.gp.koza.GPKozaDefaults;
import ec.gp.GPInitializer;
import ec.gp.GPNode;
import ec.gp.GPTree;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MapElitesCrossoverPipeline.java
 * @see ec.gp.koza.CrossoverPipeline
 * Slight modifications to allow for the MAP-Elites configuration
 * 
 * Modified: 25 January, 2022
 * 
 * @author Derek Hon
 * @version 1.0
 */

public class MapElitesCrossoverPipeline extends MapElitesBreedingPipeline {
    private static final long serialVersionUID = 1;

    public static final String P_NUM_TRIES = "tries";
    public static final String P_MAXDEPTH = "maxdepth";
    public static final String P_MAXSIZE = "maxsize";
    public static final String P_CROSSOVER = "xover";
    public static final String P_TOSS = "toss";
    public static final int INDS_PRODUCED = 2;
    public static final int NUM_SOURCES = 2;
    public static final int NO_SIZE_LIMIT = -1;
    public static final String KEY_PARENTS = "parents";

    /** How the pipeline selects a node from individual 1 */
    public GPNodeSelector nodeselect1;

    /** How the pipeline selects a node from individual 2 */
    public GPNodeSelector nodeselect2;

    /** Is the first tree fixed? If not, this is -1 */
    public int tree1;

    /** Is the second tree fixed? If not, this is -1 */
    public int tree2;

    /** How many times the pipeline attempts to pick nodes until it gives up. */
    public int numTries;

    /**
     * The deepest tree the pipeline is allowed to form. Single terminal trees are
     * depth 1.
     */
    public int maxDepth;

    /**
     * The largest tree (measured as a nodecount) the pipeline is allowed to form.
     */
    public int maxSize;

    /** Should the pipeline discard the second parent after crossing over? */
    public boolean tossSecondParent;

    /** Temporary holding place for parents */
    public ArrayList<Elite> parents;

    public MapElitesCrossoverPipeline() {
        parents = new ArrayList<Elite>();
    }

    public Parameter defaultBase() {
        return GPKozaDefaults.base().push(P_CROSSOVER);
    }

    public int numSources() {
        return NUM_SOURCES;
    }

    public Object clone() {
        MapElitesCrossoverPipeline c = (MapElitesCrossoverPipeline) (super.clone());

        // deep-cloned stuff
        c.nodeselect1 = (GPNodeSelector) (nodeselect1.clone());
        c.nodeselect2 = (GPNodeSelector) (nodeselect2.clone());
        c.parents = new ArrayList<Elite>(parents);

        return c;
    }

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        Parameter def = defaultBase();
        Parameter p = base.push(P_NODESELECTOR).push("0");
        Parameter d = def.push(P_NODESELECTOR).push("0");

        nodeselect1 = (GPNodeSelector) (state.parameters.getInstanceForParameter(p, d, GPNodeSelector.class));
        nodeselect1.setup(state, p);

        p = base.push(P_NODESELECTOR).push("1");
        d = def.push(P_NODESELECTOR).push("1");

        if (state.parameters.exists(p, d) && state.parameters.getString(p, d).equals(V_SAME))
            // can't just copy it this time; the selectors
            // use internal caches. So we have to clone it no matter what
            nodeselect2 = (GPNodeSelector) (nodeselect1.clone());
        else {
            nodeselect2 = (GPNodeSelector) (state.parameters.getInstanceForParameter(p, d, GPNodeSelector.class));
            nodeselect2.setup(state, p);
        }

        numTries = state.parameters.getInt(base.push(P_NUM_TRIES), def.push(P_NUM_TRIES), 1);
        if (numTries == 0)
            state.output.fatal("MapElitesCrossover Pipeline has an invalid number of tries (it must be >= 1).",
                    base.push(P_NUM_TRIES), def.push(P_NUM_TRIES));

        maxDepth = state.parameters.getInt(base.push(P_MAXDEPTH), def.push(P_MAXDEPTH), 1);
        if (maxDepth == 0)
            state.output.fatal("MapElitesCrossover Pipeline has an invalid maximum depth (it must be >= 1).",
                    base.push(P_MAXDEPTH), def.push(P_MAXDEPTH));

        maxSize = NO_SIZE_LIMIT;
        if (state.parameters.exists(base.push(P_MAXSIZE), def.push(P_MAXSIZE))) {
            maxSize = state.parameters.getInt(base.push(P_MAXSIZE), def.push(P_MAXSIZE), 1);
            if (maxSize < 1)
                state.output.fatal("Maximum tree size, if defined, must be >= 1");
        }

        tree1 = TREE_UNFIXED;
        if (state.parameters.exists(base.push(P_TREE).push("" + 0), def.push(P_TREE).push("" + 0))) {
            tree1 = state.parameters.getInt(base.push(P_TREE).push("" + 0), def.push(P_TREE).push("" + 0), 0);
            if (tree1 == -1)
                state.output.fatal("Tree fixed value, if defined, must be >= 0");
        }

        tree2 = TREE_UNFIXED;
        if (state.parameters.exists(base.push(P_TREE).push("" + 1), def.push(P_TREE).push("" + 1))) {
            tree2 = state.parameters.getInt(base.push(P_TREE).push("" + 1), def.push(P_TREE).push("" + 1), 0);
            if (tree2 == -1)
                state.output.fatal("Tree fixed value, if defined, must be >= 0");
        }
        tossSecondParent = state.parameters.getBoolean(base.push(P_TOSS), def.push(P_TOSS), false);
    }

    /**
     * Returns 2 * minimum number of typical individuals produced by any sources,
     * else 1* minimum number if tossSecondParent is true.
     */
    public int typicalIndsProduced() {
        return 1;
    }

    /** Returns true if inner1 can feasibly be swapped into inner2's position. */

    public boolean verifyPoints(final GPInitializer initializer, final GPNode inner1, final GPNode inner2) {
        // first check to see if inner1 is swap-compatible with inner2
        // on a type basis
        if (!inner1.swapCompatibleWith(initializer, inner2))
            return false;

        // next check to see if inner1 can fit in inner2's spot
        if (inner1.depth() + inner2.atDepth() > maxDepth)
            return false;

        // check for size
        // NOTE: this is done twice, which is more costly than it should be. But
        // on the other hand it allows us to toss a child without testing both times
        // and it's simpler to have it all here in the verifyPoints code.
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

    @Override
    public int produce(final int submap, final ArrayList<Elite> inds, final EvolutionState state,
            final int thread, HashMap<String, Object> misc)

    {
        int start = inds.size();

        // how many individuals should we make?
        int n = typicalIndsProduced();

        // should we bother?
        if (!state.random[thread].nextBoolean(likelihood)) {
            // just load from source 0 and clone 'em
            sources[0].produce(submap, inds, state, thread, misc);
            return n;
        }

        IntBag[] parentparents = null;
        IntBag[] preserveParents = null;
        if (misc != null && misc.get(KEY_PARENTS) != null) {
            preserveParents = (IntBag[]) misc.get(KEY_PARENTS);
            parentparents = new IntBag[2];
            misc.put(KEY_PARENTS, parentparents);
        }

        DistributedMEInitializer initializer = (DistributedMEInitializer) state.initializer;

        for (int q = start; q < n + start; /* no increment */) // keep on going until we're filled up
        {
            parents.clear();

            // grab two individuals from our sources
            if (sources[0] == sources[1]) { // grab from the same source
                sources[0].produce(submap, parents, state, thread, misc);
            }
            // grab from different sources
            else {
                sources[0].produce(submap, parents, state, thread, misc);
                sources[1].produce(submap, parents, state, thread, misc);
            }

            // at this point, parents[] contains our two selected individuals

            // are our tree values valid?
            if (tree1 != TREE_UNFIXED && (tree1 < 0 || tree1 >= ((Elite) (parents.get(0))).trees.length))
                // uh oh
                state.output.fatal(
                        "Map Elites Crossover Pipeline attempted to fix tree.0 to a value which was out of bounds of the array of the individual's trees.  Check the pipeline's fixed tree values -- they may be negative or greater than the number of trees in an individual");
            if (tree2 != TREE_UNFIXED && (tree2 < 0 || tree2 >= ((Elite) (parents.get(1))).trees.length))
                // uh oh
                state.output.fatal(
                        "Map Elites Crossover Pipeline attempted to fix tree.1 to a value which was out of bounds of the array of the individual's trees.  Check the pipeline's fixed tree values -- they may be negative or greater than the number of trees in an individual");

            int t1 = 0;
            int t2 = 0;
            if (tree1 == TREE_UNFIXED || tree2 == TREE_UNFIXED) {
                do
                // pick random trees -- their GPTreeConstraints must be the same
                {
                    if (tree1 == TREE_UNFIXED)
                        if (((Elite) (parents.get(0))).trees.length > 1)
                            t1 = state.random[thread].nextInt(((Elite) (parents.get(0))).trees.length);
                        else
                            t1 = 0;
                    else
                        t1 = tree1;

                    if (tree2 == TREE_UNFIXED)
                        if (((Elite) (parents.get(1))).trees.length > 1)
                            t2 = state.random[thread].nextInt(((Elite) (parents.get(1))).trees.length);
                        else
                            t2 = 0;
                    else
                        t2 = tree2;
                } while (((Elite) (parents.get(0))).trees[t1]
                        .constraints(initializer) != ((Elite) (parents.get(1))).trees[t2].constraints(initializer));
            } else {
                t1 = tree1;
                t2 = tree2;
                // make sure the constraints are okay
                if (((Elite) (parents.get(0))).trees[t1]
                        .constraints(initializer) != ((Elite) (parents.get(1))).trees[t2].constraints(initializer)) // uh
                                                                                                                    // oh
                    state.output.fatal(
                            "Map Elites Crossover Pipeline's two tree choices are both specified by the user -- but their GPTreeConstraints are not the same");
            }

            // validity results...
            boolean res1 = false;
            boolean res2 = false;

            // prepare the nodeselectors
            nodeselect1.reset();
            nodeselect2.reset();

            // pick some nodes

            GPNode p1 = null;
            GPNode p2 = null;

            for (int x = 0; x < numTries; x++) {
                // pick a node in individual 1
                p1 = nodeselect1.pickNode(state, submap, thread, ((Elite) (parents.get(0))),
                        ((Elite) (parents.get(0))).trees[t1]);

                // pick a node in individual 2
                p2 = nodeselect2.pickNode(state, submap, thread, ((Elite) (parents.get(1))),
                        ((Elite) (parents.get(1))).trees[t2]);

                // check for depth and swap-compatibility limits
                res1 = verifyPoints(initializer, p2, p1); // p2 can fill p1's spot -- order is important!
                if (n - (q - start) < 2 || tossSecondParent)
                    res2 = true;
                else
                    res2 = verifyPoints(initializer, p1, p2); // p1 can fill p2's spot -- order is important!

                // did we get something that had both nodes verified?
                // we reject if EITHER of them is invalid. This is what lil-gp does.
                // Koza only has numTries set to 1, so it's compatible as well.
                if (res1 && res2)
                    break;
            }

            // at this point, res1 AND res2 are valid, OR either res1
            // OR res2 is valid and we ran out of tries, OR neither is
            // valid and we ran out of tries. So now we will transfer
            // to a tree which has res1 or res2 valid, otherwise it'll
            // just get replicated. This is compatible with both Koza
            // and lil-gp.

            // at this point I could check to see if my sources were breeding
            // pipelines -- but I'm too lazy to write that code (it's a little
            // complicated) to just swap one individual over or both over,
            // -- it might still entail some copying. Perhaps in the future.
            // It would make things faster perhaps, not requiring all that
            // cloning.

            // Create some new individuals based on the old ones -- since
            // GPTree doesn't deep-clone, this should be just fine. Perhaps we
            // should change this to proto off of the main species prototype, but
            // we have to then copy so much stuff over; it's not worth it.

            Elite j1 = (Elite) (((Elite) (parents.get(0))).lightClone());
            Elite j2 = null;
            if (n - (q - start) >= 2 && !tossSecondParent)
                j2 = (Elite) (((Elite) (parents.get(1))).lightClone());

            // Fill in various tree information that didn't get filled in there
            j1.trees = new GPTree[((Elite) (parents.get(0))).trees.length];
            if (n - (q - start) >= 2 && !tossSecondParent)
                j2.trees = new GPTree[((Elite) (parents.get(1))).trees.length];

            // at this point, p1 or p2, or both, may be null.
            // If not, swap one in. Else just copy the parent.

            for (int x = 0; x < j1.trees.length; x++) {
                if (x == t1 && res1) // we've got a tree with a kicking cross position!
                {
                    j1.trees[x] = (GPTree) (((Elite) (parents.get(0))).trees[x].lightClone());
                    j1.trees[x].owner = j1;
                    j1.trees[x].child = ((Elite) (parents.get(0))).trees[x].child.cloneReplacing(p2, p1);
                    j1.trees[x].child.parent = j1.trees[x];
                    j1.trees[x].child.argposition = 0;
                    j1.evaluated = false;
                } // it's changed
                else {
                    j1.trees[x] = (GPTree) (((Elite) (parents.get(0))).trees[x].lightClone());
                    j1.trees[x].owner = j1;
                    j1.trees[x].child = (GPNode) (((Elite) (parents.get(0))).trees[x].child.clone());
                    j1.trees[x].child.parent = j1.trees[x];
                    j1.trees[x].child.argposition = 0;
                }
            }

            if (n - (q - start) >= 2 && !tossSecondParent)
                for (int x = 0; x < j2.trees.length; x++) {
                    if (x == t2 && res2) // we've got a tree with a kicking cross position!
                    {
                        j2.trees[x] = (GPTree) (((Elite) (parents.get(1))).trees[x].lightClone());
                        j2.trees[x].owner = j2;
                        j2.trees[x].child = ((Elite) (parents.get(1))).trees[x].child.cloneReplacing(p1, p2);
                        j2.trees[x].child.parent = j2.trees[x];
                        j2.trees[x].child.argposition = 0;
                        j2.evaluated = false;
                    } // it's changed
                    else {
                        j2.trees[x] = (GPTree) (((Elite) (parents.get(1))).trees[x].lightClone());
                        j2.trees[x].owner = j2;
                        j2.trees[x].child = (GPNode) (((Elite) (parents.get(1))).trees[x].child.clone());
                        j2.trees[x].child.parent = j2.trees[x];
                        j2.trees[x].child.argposition = 0;
                    }
                }

            // add the individuals to the population
            // inds.set(q, j1);
            inds.add(j1);
            if (preserveParents != null) {
                parentparents[0].addAll(parentparents[1]);
                preserveParents[q] = parentparents[0];
            }
            q++;
            if (q < n + start && !tossSecondParent) {
                // inds.set(q, j2);
                inds.add(j2);
                if (preserveParents != null) {
                    parentparents[0].addAll(parentparents[1]);
                    preserveParents[q] = parentparents[0];
                }
                q++;
            }
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
