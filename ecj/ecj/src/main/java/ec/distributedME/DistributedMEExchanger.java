package ec.distributedME;

import java.io.Serializable;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.Exchanger;
import ec.Population;

/**
 * DistributedMEBreeder.java
 * 
 * @see ec.exchange.InterPopulationExchange
 * Used InterPopulatioNExchange as the basis for this class. Modifiede it slightly to be used 
 * by MAP-Elites.
 *
 * 
 * Created: 10 February, 2022
 * @author Derek Hon
 * @version 1.0
 */
public class DistributedMEExchanger extends Exchanger {
    private static final long serialVersionUID = 1;

    static class ExchangeInformation implements Serializable {

        /** Name of the submap */
        String name;

        /** Immigrant selection method */
        MapElitesSelectionMethod immigrantSelectionMethod;
        
        /** Death/replace selection method */
        MapElitesSelectionMethod deathSelectionMethod;
        
        /** Number of destination sub-maps */
        int numDest;

        /** Sub-maps where individuals need to be sent */
        int[] destinations;

        /** modulo */
        int modulo;

        /** Start offset value */
        int offset;

        /** Size */
        int size;
    }


    /** Submaps */
    public static final String P_NAME = "name";

    /** Submaps */
    public static final String P_SUBMAPS = "submaps";

    /** The sub-map delimiter */
    public static final String P_SUBMAP = "submap";

    /** The parameter for the modulo (how many evaluations should pass between consecutive sendings of individuals */
    public static final String P_MODULO = "mod";

    /** The number of emigrants to be sent */
    public static final String P_SIZE = "size";

    /** How many evaluations to pass at the beginning of the evolution before the first
        emigration from the current sub-map */
    public static final String P_OFFSET = "start";

    /** The number of destinations from current island */
    public static final String P_DEST_FOR_SUBPOP = "num-dest";

    /** The prefix for destinations */
    public static final String P_DEST = "dest";

    /** The selection method for sending individuals to other islands */
    public static final String P_SELECT_METHOD = "select";

    /** The selection method for deciding individuals to be replaced by immigrants */
    public static final String P_SELECT_TO_DIE_METHOD = "select-to-die";

    /** Whether or not we're chatty */
    public static final String P_CHATTY = "chatty";

    ExchangeInformation[] exchangeInformation;

    /** Mailbox for immigrants to be sent */
    Elite[][] immigrants;

    /** Number of immigrants in storage for each sub-map */
    int[] immigrantStorage;

    int sources;

    public boolean chatty;

    public void setup(final EvolutionState state, final Parameter base) {
        Parameter p_numSubmaps = new Parameter("map").push(P_SUBMAPS);
        int numSubmaps = state.parameters.getInt(p_numSubmaps, null, 1);
        
        // how many individuals (maximally) would each of the mailboxes have to hold
        int[] incoming = new int[numSubmaps];

        exchangeInformation = new ExchangeInformation[numSubmaps];
        for  (int i = 0 ; i < numSubmaps ; i ++)
            exchangeInformation[i] = new ExchangeInformation();
        immigrantStorage = new int[numSubmaps];
        Parameter p, 
                  localBase = base.push(P_SUBMAP);
        
        chatty = state.parameters.getBoolean(base.push(P_CHATTY), null, true);

        for (int i = 0 ; i < numSubmaps ; i ++) {

            p = localBase.push("" + i);

            //Name
            exchangeInformation[i].name = (String) state.parameters.getString(p.push(P_NAME), null);

            //Selection method
            exchangeInformation[i].immigrantSelectionMethod = (MapElitesSelectionMethod)
                state.parameters.getInstanceForParameter( p.push( P_SELECT_METHOD ), base.push(P_SELECT_METHOD), MapElitesSelectionMethod.class );
            if( exchangeInformation[i].immigrantSelectionMethod == null )
                state.output.fatal( "Invalid parameter.",  p.push( P_SELECT_METHOD ), base.push(P_SELECT_METHOD) );
            exchangeInformation[i].immigrantSelectionMethod.setup( state, p.push(P_SELECT_METHOD) );

            // read the selection method
            if( state.parameters.exists( p.push( P_SELECT_TO_DIE_METHOD ), base.push(P_SELECT_TO_DIE_METHOD ) ) )
                exchangeInformation[i].deathSelectionMethod = (MapElitesSelectionMethod)
                    state.parameters.getInstanceForParameter( p.push( P_SELECT_TO_DIE_METHOD ), base.push( P_SELECT_TO_DIE_METHOD ), MapElitesSelectionMethod.class );
            else // use RandomSelection
                exchangeInformation[i].deathSelectionMethod = new DistributedMERandomSelection();
            exchangeInformation[i].deathSelectionMethod.setup( state, p.push(P_SELECT_TO_DIE_METHOD));

            // get the modulo
            exchangeInformation[i].modulo = state.parameters.getInt( p.push( P_MODULO ), base.push(P_MODULO ), 1 );
            if( exchangeInformation[i].modulo == 0 )
                state.output.fatal( "Parameter not found, or it has an incorrect value.", p.push( P_MODULO ), base.push( P_MODULO ) );
            
            // get the offset
            exchangeInformation[i].offset = state.parameters.getInt( p.push( P_OFFSET ), base.push( P_OFFSET ), 0 );
            if( exchangeInformation[i].offset == -1 )
                state.output.fatal( "Parameter not found, or it has an incorrect value.", p.push( P_OFFSET ), base.push( P_OFFSET ) );
            
            // get the size
            exchangeInformation[i].size = state.parameters.getInt( p.push( P_SIZE ), base.push( P_SIZE ), 1 );
            if( exchangeInformation[i].size == 0 )
                state.output.fatal( "Parameter not found, or it has an incorrect value.", p.push( P_SIZE ), base.push( P_SIZE ) );

            // get the number of destinations
            exchangeInformation[i].numDest = state.parameters.getInt( p.push( P_DEST_FOR_SUBPOP ), null, 0 );
            if( exchangeInformation[i].numDest == -1 )
                state.output.fatal( "Parameter not found, or it has an incorrect value.", p.push( P_DEST_FOR_SUBPOP ) );

            exchangeInformation[i].destinations = new int[ exchangeInformation[i].numDest ];
            // read the destinations
            for( int j = 0 ; j < exchangeInformation[i].numDest ; j++ ) {
                exchangeInformation[i].destinations[j] =
                    state.parameters.getInt( p.push( P_DEST ).push( "" + j ), null, 0 );
                if( exchangeInformation[i].destinations[j] == -1 ||
                    exchangeInformation[i].destinations[j] >= numSubmaps )
                    state.output.fatal( "Parameter not found, or it has an incorrect value.", p.push( P_DEST ).push( "" + j ) );
                // update the maximum number of incoming individuals for the destination island
                incoming[ exchangeInformation[i].destinations[j] ] += exchangeInformation[i].size;
            }

            // calculate the maximum number of incoming individuals to be stored in the mailbox
        int max = -1;

        for( int j = 0 ; j < incoming.length ; j++ )
            if( max == - 1 || max < incoming[j] )
                max = incoming[j];

        // set up the mailboxes
        immigrants = new Elite[ numSubmaps ][ max ];
        }

    }

    public void preBreedingExchangeEliteMap (EvolutionState state) {
        // exchange individuals between submap
        // BUT ONLY if the modulo and offset are appropriate for this
        // generation (state.generation)
        // I am responsible for returning a map. This could
        // be a new population that I created fresh, or I could modify
        // the existing map and return that.

        // for each of the islands that sends individuals
        for( int island = 0 ; island < exchangeInformation.length ; island++ ) {

            // else, check whether the emigrants need to be sent
            if( ( ((DistributedMEEvolutionState) state).evaluations >= exchangeInformation[island].offset ) &&
                    ( ( exchangeInformation[island].modulo == 0 ) ||
                    ( ( ( ((DistributedMEEvolutionState) state).evaluations - exchangeInformation[island].offset ) % exchangeInformation[island].modulo ) == 0 ) ) )
                {

                // send the individuals!!!!

                // for each of the islands where we have to send individuals
                for( int exchangeDest = 0 ; exchangeDest < exchangeInformation[island].numDest ; exchangeDest++ ) {

                    if (chatty) state.output.message( "Sending the emigrants from map " +
                    island + " to map " +
                        exchangeInformation[island].destinations[exchangeDest] );

                    // select "size" individuals and send then to the destination as emigrants
                    exchangeInformation[island].immigrantSelectionMethod.prepareToProduce( state, island, 0 );
                     // send all necesary individuals
                    for( int y = 0 ; y < exchangeInformation[island].size ; y++ ) {
                        // get the index of the immigrant
                        int index = exchangeInformation[island].immigrantSelectionMethod.produce( island, state, 0 );
                        Elite e = process(state, 0, null, exchangeInformation[island].destinations[exchangeDest], 
                                            ((DistributedMEEvolutionState) state).distributedMap.subMAPs[island].getEliteForMigration(index));
                        
                        // copy the individual to the mailbox of the destination submap
                        immigrants[exchangeInformation[island].destinations[exchangeDest]][immigrantStorage[exchangeInformation[island].destinations[exchangeDest]]] = e;

                        immigrantStorage[ exchangeInformation[island].destinations[exchangeDest] ]++;
                        // ((DistributedMEEvolutionState) state).distributedMap.submaps.get(island).remove(state, key);
                    }
                    exchangeInformation[island].immigrantSelectionMethod.finishProducing( state, island, 0 ); // end the selection step
                }
            }
        }

        // return ((DistributedMEEvolutionState) state).distributedMap;
    }

    //TODO: individual reading to map implementation
    public void postBreedingExchangEliteMap (EvolutionState state) {
        // receiving individuals from other islands
        // same situation here of course.

        for( int x = 0 ; x < immigrantStorage.length ; x++ )
            {

            if( immigrantStorage[x] > 0 && chatty )
                {
                state.output.message( "Immigrating " +  immigrantStorage[x] +
                    " individuals from mailbox for submap " + x );
                }
                
            int len = ((DistributedMEEvolutionState) state).distributedMap.subMAPs[x].getOccupants();
            // double check that we won't go into an infinite loop!
            if ( immigrantStorage[x] >= len )
                state.output.fatal("Number of immigrants ("+immigrantStorage[x] +
                    ") is larger than submap #" + x + "'s size (" +
                    len +").  This would cause an infinite loop in the selection-to-die procedure.");

            boolean[] selected = new boolean[ len ];
            int[] indices = new int[ immigrantStorage[x] ];
            for( int i = 0 ; i < selected.length ; i++ )
                selected[i] = false;
            exchangeInformation[x].deathSelectionMethod.prepareToProduce( state, x, 0 );
            for( int i = 0 ; i < immigrantStorage[x] ; i++ )
                {
                do {
                    indices[i] = exchangeInformation[x].deathSelectionMethod.produce( x, state, 0 );
                    }
                while( selected[indices[i]] );
                selected[indices[i]] = true;
                }
            exchangeInformation[x].deathSelectionMethod.finishProducing( state, x, 0 );

            for( int y = 0 ; y < immigrantStorage[x] ; y++ ) {
                // read the individual
                ((DistributedMEEvolutionState) state).ProcessIndividual(immigrants[x][y], x);
            }

            // reset the number of immigrants in the mailbox for the current subpopulation
            // this doesn't need another synchronization, because the thread is already synchronized
            immigrantStorage[x] = 0;
            }
        // return ((DistributedMEEvolutionState) state).distributedMap;
    }

    @Override
    public Population preBreedingExchangePopulation(EvolutionState state) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Population postBreedingExchangePopulation(EvolutionState state) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String runComplete(EvolutionState state) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Utilizes Exchanger's process except making it compliant for elites due to mismatch of class with Individual.
     * 
     * @see ec.Exchanger#process
     */
    Elite process(EvolutionState state, int thread, String island, int subpop, Elite ind) {
        return ind;
    }
    
}
