package ec.distributedME;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.Population;

/**
 SimpleDistributedMEExchanger.java
 * 
 * @see ec.exchange.InterPopulationExchange
 * Basic stub.
 *
 * 
 * Created: 10 February, 2022
 * @author Derek Hon
 * @version 1.0
 */
public class SimpleDistributedMEExchanger extends DistributedMEExchanger {
    public void setup(final EvolutionState state, final Parameter base) {}

    public void preBreedingExchangeEliteMap (EvolutionState state) {
        // return ((DistributedMEEvolutionState) state).distributedMap;
    }

    public void postBreedingExchangEliteMap (EvolutionState state) {
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
