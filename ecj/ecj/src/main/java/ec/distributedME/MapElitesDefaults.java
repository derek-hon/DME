package ec.distributedME;

import ec.DefaultsForm;
import ec.util.Parameter;

/**
 * MapElitesDefaults.java
 * @see ec.steadystate.SteadyStateDefaults
 *
 * Modifications: 
 * 
 * @author Derek Hon
 * @version 1.0
 */


public final class MapElitesDefaults implements DefaultsForm
{
    public static final String P_MAPELITES = "mapelites";

    /** Returns the default base. */
    public static final Parameter base()
    {
        return new Parameter(P_MAPELITES);
    }
}
