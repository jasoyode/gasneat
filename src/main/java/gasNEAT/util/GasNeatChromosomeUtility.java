package gasNEAT.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.jgap.Allele;

import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;

import gasNEAT.geneticEncoding.GasNeatNeuronAllele;

public class GasNeatChromosomeUtility extends NeatChromosomeUtility {
	
	private static Logger logger = Logger.getLogger( GasNeatChromosomeUtility.class );
	

	
	//neurons that produce gas are given a chance to exist in the genome even if they have no impact
	//since there may be a need for an evolutionary staging area to explore (and create a new species)
	//so a neuron may be added that does nothing functional, but later a neuron's receptor
	//changes and then results in a different behavior
	//if we want to restrict the alleles that absolutely have a functional impact on the performance,
	//if they cannot be activated, then there is no purpose to including them here
	public static Collection<Allele> getAllUnactivatableAllelesFromSrcNeurons( 
			Collection<Allele> alleles ) {
		
		SortedMap<Long, Allele> inputNeuronMap = NeatChromosomeUtility.getNeuronMap(alleles, NeuronType.INPUT );
		SortedMap neuronMap = NeatChromosomeUtility.getNeuronMap(alleles  );
		SortedMap connectionMap = getConnectionMap( alleles  );
		
		//track alleles that will never activate
		Collection<Allele> allelesToBeRemoved = new HashSet<Allele>();
		//track alleles that have been activated/traversed
		Collection<Long> activatedAlleles = new HashSet<Long>();
		//currently iterating through these
		Collection<Long> currentlyVisitingIds = new HashSet<Long>();
		//ids that have not been visited, if never visited, they get added to be removed
		Collection<Long> unvisitedIds = new HashSet<Long>();
		
		//connections to neurons that are not activated yet at the time of traversal
		//these are checked at the end to make sure their destinations are not pruned
		Collection<Long> potentiallyActivatedConnections = new HashSet<Long>();
		
		//all alleles start as unvisited
		for (Allele a: alleles) {
			unvisitedIds.add(  a.getInnovationId() );
		}
		
		//the algorithm starts by activating and exploring the inputs
		for (Long id: inputNeuronMap.keySet() ) {
			currentlyVisitingIds.add( id);
		}
		
		//until we have nothing new to explore/visit
		while ( !currentlyVisitingIds.isEmpty() ) {
			Collection<Long> toBeVisitedAlleles = new HashSet<Long>();
			
			//explore each allele (neuron) on our visit list
			for (Long id: currentlyVisitingIds) {
				logger.debug("Pruning Transcriber currentlyVisitingIds: " + currentlyVisitingIds);
				logger.debug("Pruning Transcriber toBeVisitedAlleles: " + toBeVisitedAlleles);
				
				//we always explore neurons, because we need to know what they produce when checking connections
				GasNeatNeuronAllele neuron = (GasNeatNeuronAllele)neuronMap.get(id);

				logger.debug("Pruning Transcriber [exploringing neuronAllele ]: "+ neuron.getInnovationId());
				int gasProduced = neuron.getGasEmissionType();
				//if the gas type is G0 we need to check the synaptic gas and connections
				//in order to see what gets activated
				if (gasProduced == 0) 
				{
					logger.debug("Pruning Transcriber    [neuronAllele "+ neuron.getInnovationId()+"] is a G0 producer examining connections"     );
					int synapticGas = neuron.getSynapticGasEmissionType();
					Collection<Long> connectionIds = getConnectionsFromNeuronId( id, unvisitedIds, connectionMap, neuronMap );
					//ITERATE THROUGH SYNAPSES FOR EACH NEURON
					for (Long connectionId: connectionIds) {
						
						ConnectionAllele connection = (ConnectionAllele)connectionMap.get(connectionId);
						GasNeatNeuronAllele destNeuronAllele = (GasNeatNeuronAllele)neuronMap.get(connection.getDestNeuronId());
						logger.debug("Pruning Transcriber     [exploringing connection ]: "+ connection);
						if (destNeuronAllele == null) {
							System.out.println( "HOW DID A CONNECTION GET STORED THAT DID NOT HAVE A TARGET?"  );
							System.exit(1);
						}
						
						logger.debug("Pruning Transcriber         [exploring dest ]: "+ destNeuronAllele.getInnovationId());
						
						//IF THE TARGET NEURON IS ACTIVATED BY THE SYNGAS THEN ACTIVATE IT
						//if the target neuron is activated by the gas produced by the source neuron, we have success
						if  (destNeuronAllele.getReceptorType().substring(0, 2).equals("G"+ synapticGas ) ) {
							logger.debug("Pruning Transcriber             [exploring dest ]: YES ACTIVATE BY SYN GAS!" );
							
							//no longer unvisited
							unvisitedIds.remove(connectionId);
							
							//synapse was activated
							activatedAlleles.add(connectionId);
							
							//activated neuron should be visited next time
							toBeVisitedAlleles.add( connection.getDestNeuronId() );
							
						//IF THE TARGET NEURON IS NOT ACTIVATED BY THE SYN GAS, CHECK IF IT IS MODULATED BY IT
						} else {
							
							//THE CONNECTION MODULATES THE TARGET NEURON WITH ITS CURRENT RECEPTOR
							if (  destNeuronAllele.getReceptorType().contains("G"+synapticGas) ) {
								
								logger.debug("Pruning Transcriber             [exploring dest ]: YES MODULATED, SO CONNECTION ACTIVATED, BUT NEURON NOT ACTIVATED YET!" );
								
								//this does not activate the neuron though
								//synapse modulates the target neuron
								//NEED TO PRUNE THIS IF NEURON NEVER GETS ACTIVATED!
								//We will add it now to a list of potential connections to keep
								potentiallyActivatedConnections.add(connectionId);
								
							} else {
								logger.debug("Pruning Transcriber             [exploringing dest ]: CONNECTION DOES NOTHING! PRUNE!" );
								//THE CONNECTION COULD THE TARGET NEURON IF IT CHANGED RECEPTORS
								//#GASNEATEVOLUTION
								//need to decide if we want to prune the connection or not
								
								//synapse does nothing at all to target functionally
								allelesToBeRemoved.add(connection);
								
								//no longer unvisited
								unvisitedIds.remove(connectionId);
							}
						}
					}
					
				} else {
					//connections from this neuron are not valid, so will be omitted
					//by their lack of inclusion from the unvisited list
					
					//find all neurons activated by the gas produced by this neuron that have not been visited
					//if they have previously been visited, we do not need to worry about them even if they were activated
					//by some other means
					
					logger.debug("Pruning Transcriber [must check for gas receivers from ]: "+ neuron.getInnovationId());
					
					Collection<Long> gasActivatedIds = extractGasActivatedNeuronIds(neuron, unvisitedIds, neuronMap);
					
					//add all such activated neurons to our list to be visited
					toBeVisitedAlleles.addAll( gasActivatedIds );
				}
				
			}
			
			//since these were explored, we know they were activated
			activatedAlleles.addAll(currentlyVisitingIds);
			
			//since these were explored, they are no longer unvisited
			unvisitedIds.removeAll(currentlyVisitingIds);
			
			//clear the currently visiting list, it has been processed
			currentlyVisitingIds.clear();
			
			//add newly found alleles to explore list
			currentlyVisitingIds.addAll(toBeVisitedAlleles);
			
		}
		
		//must go through and see if the synaptically modulated neurons ever got activated
		//if they did, then we need to make sure those synapses are marked as activated
		for (Long connectionId: potentiallyActivatedConnections ) {
			ConnectionAllele conn = (ConnectionAllele)connectionMap.get(connectionId);
			if (conn != null) {

				logger.debug("Pruning Transcriber             [considering connection"+ connectionId +" with destId = "+ conn.getDestNeuronId() );
				if ( activatedAlleles.contains( conn.getDestNeuronId() )  ) {
					activatedAlleles.add(connectionId);
					unvisitedIds.remove(connectionId);
					logger.debug("Pruning Transcriber             [considering connection"+ connectionId +" YES DEST GOT ACTVIATED DO NOT PRUNE!" );
				} else {
					logger.debug("Pruning Transcriber             [considering connection"+ connectionId +" NO DEST NOT ACTVIATED! PRUNE!" );
				}
			}
		}
		
		Collection<Allele> unvisitedAlleles = new HashSet<Allele>();
		
		//all alleles that never got visited must be pruned
		for (Long id: unvisitedIds) {
			
			NeuronAllele neuron = (NeuronAllele)neuronMap.get(id);
			ConnectionAllele conn = (ConnectionAllele)connectionMap.get(id);
			if (neuron != null) {
				unvisitedAlleles.add(neuron);
			} else if (conn != null) {
				unvisitedAlleles.add(conn);
			} else {
				System.out.println("must be a neuron or conn ");
				System.exit(1);
			}
			
		}
		
		allelesToBeRemoved.addAll( unvisitedAlleles );
		
		
		//sanity check proof of concept
		//make sure every neuron that is pruned as originating connections pruned as well!
		for( Allele a :allelesToBeRemoved ) {
			if (a instanceof GasNeatNeuronAllele ) {
				GasNeatNeuronAllele neuron  = (GasNeatNeuronAllele)a;
				
				for (Object o:connectionMap.values() ){
					ConnectionAllele conn = (ConnectionAllele)o;
					
					//if its set to be included
					if ( !  allelesToBeRemoved.contains(conn) ) {
						if (conn.getDestNeuronId().equals( neuron.getInnovationId()   )) {
							System.out.println("We should not have a connection remaining whose dest neuron is gone!");
							System.exit(1);
						} else if (conn.getSrcNeuronId().equals( neuron.getInnovationId()   )) {
							System.out.println("We should not have a connection remaining whose src neuron is gone!");
							System.exit(1);
						}
					}
				}
			}
		}
		
		if ( logger.isDebugEnabled()     ) {
		
			for( Allele a :allelesToBeRemoved ) {
				if (a instanceof GasNeatNeuronAllele ) {
					GasNeatNeuronAllele neuron  = (GasNeatNeuronAllele)a;
					logger.debug( "CONNECTIONS FOR NEURON: "+ neuron.getInnovationId()  );
					for (Object o: connectionMap.values() ) {
						ConnectionAllele c = (ConnectionAllele)o;
						if (c.getDestNeuronId().equals(neuron.getInnovationId() )) {
							logger.debug( "  MATCHING DEST: "+ c);
							if ( allelesToBeRemoved.contains( c ) ) {
								logger.debug( "    SET TO BE REMOVED!");
							} else {
								logger.debug( "    SET TO REMAIN!");
								System.exit(1);
							}
						} else if (c.getSrcNeuronId().equals(neuron.getInnovationId() )) {
							logger.debug( "  MATCHING SRC: "+ c);
							if ( allelesToBeRemoved.contains( c ) ) {
								logger.debug( "    SET TO BE REMOVED!");
							} else {
								logger.debug( "    SET TO REMAIN!");
								System.exit(1);
							}
						}
					}
				}
			}
		}
	
		//temp fix so output neurons are guaranteed
		Collection<Allele> outputIds = NeatChromosomeUtility.getNeuronMap(alleles, NeuronType.OUTPUT  ).values();
		allelesToBeRemoved.removeAll( outputIds );
		
		
		return allelesToBeRemoved;
	}
	
	
	
	
	public static void checkThatAllelesHaveNothingMissing( Collection<Allele> alleles) {
		
		Collection<Long> ids = new HashSet();
		
		for( Allele a :alleles ) {
			ids.add( a.getInnovationId() );
		}
		
		//for all connections, make sure neurons exist at src and dest
		for( Allele a :alleles ) {
			
			if (a instanceof ConnectionAllele) {
				ConnectionAllele conn = (ConnectionAllele)a;
				if ( ! ids.contains(  conn.getDestNeuronId() )  ) {
					logger.debug("-------------------------------");
					for (Allele alll: alleles) {
						logger.debug(  alll );
					}
					logger.debug("-------------------------------");
					logger.debug("BAD CHROMOSOME");
					logger.debug("connection " + conn );
					logger.debug("LACK DEST NEURON!");
					System.exit(1);
				} else if ( ! ids.contains(  conn.getSrcNeuronId() )  ) {
					logger.debug("-------------------------------");
					for (Allele alll: alleles) {
						logger.debug(  alll );
					}
					logger.debug("-------------------------------");
					logger.debug("BAD CHROMOSOME");
					logger.debug("connection " + conn );
					logger.debug("LACK SRC NEURON!");
					System.exit(1);
				} else {
					//ok
				}
			}
		}
	}

	//extract all neurons that could be influenced by the target neuron
	//regardless of their receptor type
	private static Collection<Long> extractGasActivatedNeuronIds(GasNeatNeuronAllele neuron,
			Collection<Long> unvisitedIds, SortedMap neuronMap) {
		
		Collection<Long> targetNeuronIds = new ArrayList<Long>();
		
		int x1 = neuron.getXCoordinate();
		int y1 = neuron.getYCoordinate();
		
		for (Long targetId: unvisitedIds) {
			
			//if you use a neuron id the result will be null
			GasNeatNeuronAllele targetNeuron = (GasNeatNeuronAllele)neuronMap.get(targetId);
			
			//only check further if a neuron was matched with the get from the neuronMap
			if (targetNeuron != null) {
				
				//the activation type of the target must match the gas produced by the originating neuron
				if (  targetNeuron.getReceptorType().substring(0,2).equals("G"+neuron.getGasEmissionType() ) ) {
					
					int x2 = targetNeuron.getXCoordinate();
					int y2 = targetNeuron.getYCoordinate();
					double distance = Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
					
					logger.debug("Pruning Transcriber     [receiver neuron ]: "+ targetNeuron.getInnovationId());
					//must make sure that the neuron is within the radius
					if ( distance <= neuron.getGasEmissionRadius() ) {
						logger.debug("Pruning Transcriber     [receiver neuron ]:  YES IN RANGE!");
						targetNeuronIds.add(targetId);
					} else {
						logger.debug("Pruning Transcriber     [receiver neuron ]:  NO NOT IN RANGE!");
						
					}
				}
			}
		}
		
		return targetNeuronIds;
	}

	//will return connections to neurons that are already activated if they are present
	//this is not a problem as long as you are adding to sets (which guarantee single copies of unique elements)
	private static Collection<Long> getConnectionsFromNeuronId(Long neuronId, Collection<Long> unvisitedIds,
			SortedMap<Long, ConnectionAllele> connectionMap, SortedMap<Long, NeuronAllele> neuronMap) {

		Collection<Long> connectionIds = new ArrayList<Long>();
		
		for (Long connectionId: unvisitedIds) {
			
			//if you use a neuron id the result will be null
			ConnectionAllele connection = (ConnectionAllele)connectionMap.get(connectionId);
			
			//only check if a connection was returned
			if (connection != null) {
				
				logger.debug("Pruning Transcriber    [neuronAllele "+ neuronId+"] testing connection "+connection     );
				
				//the connection must come from the neuronId in question
				if (  connection.getSrcNeuronId().equals( new Long( neuronId)  )  ) {
					//we intentionally will return connections even if the target neuron has already been activated
					connectionIds.add(connectionId);
					logger.debug("Pruning Transcriber    [neuronAllele "+ neuronId+"]             connection "+connection +"matches!"  );
				} else {
					logger.debug("Pruning Transcriber    [neuronAllele "+ neuronId+"]             connection "+connection +"does not match!"  );
					logger.debug("Pruning Transcriber    [neuronAllele "+ neuronId+"]             connection "+connection +" " + connection.getSrcNeuronId() +" != " + neuronId  );
					
				}
			}
		}
		
		return connectionIds;
	}

	
	
	/**
	 * returns all connections in <code>alleles</code> as <code>SortedMap</code>
	 * 
	 * @param alleles <code>SortedSet</code> contains <code>Allele</code> objects
	 * @return <code>SortedMap</code> containing key <code>Long</code> innovation id, value
	 * <code>ConnectionAllele</code> objects
	 * It is dumb that this needed creation, but allows the alleles to be passed in as a collection as needed above
	 */
	public static SortedMap getConnectionMap( Collection alleles ) {
		TreeMap result = new TreeMap();
		Iterator iter = alleles.iterator();
		while ( iter.hasNext() ) {
			Allele allele = (Allele) iter.next();

			if ( allele instanceof ConnectionAllele ) {
				ConnectionAllele connAllele = (ConnectionAllele) allele;
				Long id = connAllele.getInnovationId();

				// sanity check
				if ( result.containsKey( id ) )
					throw new IllegalArgumentException( "chromosome contains duplicate connection gene: "
							+ allele.toString() );

				result.put( id, allele );
			}
		}
		return result;
	}


	//GET NUMBER OF RECURRENT STEPS TO BE USED IN NETWORK
	
	//neurons that produce gas are given a chance to exist in the genome even if they have no impact
	//since there may be a need for an evolutionary staging area to explore (and create a new species)
	//so a neuron may be added that does nothing functional, but later a neuron's receptor
	//changes and then results in a different behavior
	//if we want to restrict the alleles that absolutely have a functional impact on the performance,
	//if they cannot be activated, then there is no purpose to including them here
	public static int getMinimumStepsFromSrcToDestNeurons( Collection<Allele> alleles, int gasSpeed ) {
		
		int currentTimeStep = 1;
		
		SortedMap<Long, Allele> inputNeuronMap = NeatChromosomeUtility.getNeuronMap(alleles, NeuronType.INPUT );
		SortedMap neuronMap = NeatChromosomeUtility.getNeuronMap(alleles  );
		SortedMap connectionMap = getConnectionMap( alleles  );
		
		//track alleles that will never activate
		Collection<Allele> allelesToBeRemoved = new HashSet<Allele>();

		//track alleles that have been activated/traversed
		Collection<Long> activatedAlleles = new HashSet<Long>();
	    
		
		//key is Long id, value is the number of timesteps taken to reach that id
		//Below we usea search through currentlyVisitingIds to find the smallest each time
		Map< Long, Integer> currentlyVisitingIds = new HashMap<Long, Integer>(  );
		
		///processed Ids - that should no longer be considered
		SortedMap< Long, Integer> processedIds = new TreeMap<Long, Integer>( );
			
		//ids that have not been visited, if never visited, they get added to be removed
		Collection<Long> unvisitedIds = new HashSet<Long>();
		
		//connections to neurons that are not activated yet at the time of traversal
		//these are checked at the end to make sure their destinations are not pruned
		Collection<Long> potentiallyActivatedConnections = new HashSet<Long>();
		
		
		//all alleles start as unvisited
		for (Allele a: alleles) {
			unvisitedIds.add(  a.getInnovationId() );
		}
		
		//the algorithm starts by activating and exploring the inputs
		for (Long id: inputNeuronMap.keySet() ) {
			currentlyVisitingIds.put( id,  currentTimeStep  );
		}
		
		//until we have nothing new to explore/visit
		while ( !currentlyVisitingIds.isEmpty() ) {

			Long id = getLowestTimeId( currentlyVisitingIds );
			currentTimeStep = currentlyVisitingIds.get( id );
			
			
			//since we just removed this id, it should be counted as processed
			if (processedIds.containsKey(id)) {
				System.out.println("You should never be visiting a node that was processed!");
				System.exit(1);
			}
			processedIds.put(id, currentlyVisitingIds.get( id ) );
			currentlyVisitingIds.remove( id );
			
			if ( !processedIds.containsKey(id ) ) {
				//add times to ids as they are found
				if ( !currentlyVisitingIds.containsKey(id )   ) {
					currentlyVisitingIds.put( id , currentTimeStep );
				} else if (currentlyVisitingIds.get(id) > currentTimeStep  ) {
					//sometimes if there is gas neuron that is also synaptically activated
					//it might be faster to activate
					currentlyVisitingIds.put( id , currentTimeStep );
				}
			}
			
			logger.debug("Pruning Transcriber currentlyVisitingIds: " + currentlyVisitingIds);
			logger.debug("Pruning Transcriber processedIds: " + processedIds);
			
			//we always explore neurons, because we need to know what they produce when checking connections
			GasNeatNeuronAllele neuron = (GasNeatNeuronAllele)neuronMap.get(id);

			logger.debug("Pruning Transcriber [exploringing neuronAllele ]: "+ neuron.getInnovationId());
			int gasProduced = neuron.getGasEmissionType();
			//if the gas type is G0 we need to check the synaptic gas and connections
			//in order to see what gets activated
			if (gasProduced == 0) 
			{
				logger.debug("Pruning Transcriber    [neuronAllele "+ neuron.getInnovationId()+"] is a G0 producer examining connections"     );
				int synapticGas = neuron.getSynapticGasEmissionType();
				Collection<Long> connectionIds = getConnectionsFromNeuronId( id, unvisitedIds, connectionMap, neuronMap );
				//ITERATE THROUGH SYNAPSES FOR EACH NEURON
				for (Long connectionId: connectionIds) {
					
					ConnectionAllele connection = (ConnectionAllele)connectionMap.get(connectionId);
					GasNeatNeuronAllele destNeuronAllele = (GasNeatNeuronAllele)neuronMap.get(connection.getDestNeuronId());
					logger.debug("Pruning Transcriber     [exploringing connection ]: "+ connection);
					if (destNeuronAllele == null) {
						System.out.println( "HOW DID A CONNECTION GET STORED THAT DID NOT HAVE A TARGET?"  );
						System.exit(1);
					}
					
					logger.debug("Pruning Transcriber         [exploring dest ]: "+ destNeuronAllele.getInnovationId());
					
					//IF THE TARGET NEURON IS ACTIVATED BY THE SYNGAS THEN ACTIVATE IT
					//if the target neuron is activated by the gas produced by the source neuron, we have success
					if  (destNeuronAllele.getReceptorType().substring(0, 2).equals("G"+ synapticGas ) ) {
						logger.debug("Pruning Transcriber             [exploring dest ]: YES ACTIVATE BY SYN GAS!" );
						
						//no longer unvisited
						unvisitedIds.remove(connectionId);
						
						//synapse was activated
						activatedAlleles.add(connectionId);
						
						if ( !processedIds.containsKey(id )  ) {
							//activated neuron should be visited next time with one more timestep for the synapse
							currentlyVisitingIds.put( connection.getDestNeuronId(), currentTimeStep + 1  );
							
						}
						
						
					//IF THE TARGET NEURON IS NOT ACTIVATED BY THE SYN GAS, CHECK IF IT IS MODULATED BY IT
					} else {
						
						//THE CONNECTION MODULATES THE TARGET NEURON WITH ITS CURRENT RECEPTOR
						if (  destNeuronAllele.getReceptorType().contains("G"+synapticGas) ) {
							
							logger.debug("Pruning Transcriber             [exploring dest ]: YES MODULATED, SO CONNECTION ACTIVATED, BUT NEURON NOT ACTIVATED YET!" );
							
							//this does not activate the neuron though
							//synapse modulates the target neuron
							//NEED TO PRUNE THIS IF NEURON NEVER GETS ACTIVATED!!!!!!!!
							//We will add it now to a list of potential connections to keep
							potentiallyActivatedConnections.add(connectionId);
							
						} else {
							logger.debug("Pruning Transcriber             [exploringing dest ]: CONNECTION DOES NOTHING! PRUNE!" );
							//THE CONNECTION COULD THE TARGET NEURON IF IT CHANGED RECEPTORS
							//#GASNEATEVOLUTION
							//need to decide if we want to prune the connection or not
							
							//synapse does nothing at all to target functionally
							allelesToBeRemoved.add(connection);
							
							//no longer unvisited
							unvisitedIds.remove(connectionId);
						}
					}
				}
				
			} else {
				//connections from this neuron are not valid, so will be omitted
				//by their lack of inclusion from the unvisited list
				
				//find all neurons activated by the gas produced by this neuron that have not been visited
				//if they have previously been visited, we do not need to worry about them even if they were activated
				//by some other means
				
				logger.debug("Pruning Transcriber [must check for gas receivers from ]: "+ neuron.getInnovationId());
				
				Collection<Long> gasActivatedIds = extractGasActivatedNeuronIds(neuron, unvisitedIds, neuronMap);
				
				
				for (Long targetId: gasActivatedIds ) {
					
					
					// calculate the time taken for the gas to get to the location
					int x1 = ((GasNeatNeuronAllele)neuronMap.get(targetId)).getXCoordinate() ;
					int y1 = ((GasNeatNeuronAllele)neuronMap.get(targetId)).getYCoordinate() ;
					int x2 = ((GasNeatNeuronAllele)neuronMap.get( id )).getXCoordinate() ;
					int y2 = ((GasNeatNeuronAllele)neuronMap.get( id )).getYCoordinate() ;
					
					int timeToArrive = (int) Math.ceil( Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)  ) / gasSpeed );
					
					//System.out.println("currentlyVisitingIds "+  currentlyVisitingIds );
					//System.out.println("id " + id);
					//System.out.println("currentlyVisitingIds.get(id)"  + currentlyVisitingIds.get(id));
					
					if ( !processedIds.containsKey(targetId ) ) {
						//add times to ids as they are found
						if ( ! currentlyVisitingIds.containsKey(targetId )) {
							currentlyVisitingIds.put( targetId , currentTimeStep + timeToArrive );
						} else if (currentlyVisitingIds.get(targetId) > currentTimeStep + timeToArrive  ) {
							//sometimes if there is gas neuron that is also synaptically activated
							//it might be faster to activate
							currentlyVisitingIds.put( targetId , currentTimeStep + timeToArrive );
						}
					}
				}
			}
			//since these were explored, we know they were activated
			//iterating one at a time to satisfy dijkstra's algo
			activatedAlleles.add( id );
			
			//since these were explored, they are no longer unvisited
			unvisitedIds.remove( id );
			
		}
		
		//temp fix so output neurons are guaranteed
		Collection<Allele> outputNeuronAlleles = NeatChromosomeUtility.getNeuronMap(alleles, NeuronType.OUTPUT  ).values();
		
		//1000 means that it is never reached		
		int max = 1000;
		for (Allele a: outputNeuronAlleles) {
			if (processedIds.get(a.getInnovationId() ) != null) {
				if (   processedIds.get(a.getInnovationId() ) < max  ) {
					max = processedIds.get(a.getInnovationId() );
				}
			}
		}
		
		if ( max != 1000 && max > 50) {
			logger.warn("Timesteps per tick is very high: " + max +"  This will slow things down drastically!");
		}
		
		return max;
	}

	//used to find the id of the node with the lowest total time thus far
	public static Long getLowestTimeId( Map<Long, Integer> currentlyVisitingIds ) {
		
		Long bestId = (long) -1;
		int bestTime = Integer.MAX_VALUE;
		
		for (Map.Entry<Long, Integer> entry: currentlyVisitingIds.entrySet() ) {
			if ( entry.getValue() < bestTime ) {
				bestId = entry.getKey();
				bestTime = entry.getValue();
			}
		}
		
		if (bestId == -1) {
			System.err.println("Tried to get the smallest id and there was none!");
			System.exit(1);
		}
		
		return bestId;
		
	}
	

}

