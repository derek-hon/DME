/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

/*
  A simple class which contains both an Individual and the Queue it's located in.
  Used by SteadyState and by various assistant functions in the distributed evaluator
  to provide individuals to SteadyState
*/

package ec.distributedME;
// import ec.gp.*;

/**
 * QueueIndividual
 * @see ec.steadystate.QueueIndividual
 * 
 * Created: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */
public class QueueIndividual implements java.io.Serializable
{
    public Elite ind;
    public int submap;
    public  QueueIndividual(Elite i, int submap)
    {
        this.ind = i;
        this.submap = submap;
    }
};

