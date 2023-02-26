

package ec.distributedME;

import ec.EvolutionState;

/**
 * MapElitesSourceForm.java
 *
 * @see ec.steadystate.SteadyStateSourceForm
 *
 * 
 * Created: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */
public interface MapElitesSourceForm
{
    /** Called whenever an individual has been replaced by another
     in the population. */
    public void individualReplaced(final EvolutionState state,
                                   final int submap,
                                   final int thread,
                                   final int individual);

    /** Issue an error (not a fatal -- we guarantee that callers
     of this method will also call exitIfErrors) if any
     of your sources, or <i>their</i> sources, etc., are not
     of SteadyStateBSourceForm.*/
    public void sourcesAreProperForm(final EvolutionState state);
}
