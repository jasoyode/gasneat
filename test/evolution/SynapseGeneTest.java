package evolution;

public class SynapseGeneTest {

	/*
	SynapseGene synapseGene;
	GasNeatSynapse synapse;
	GasNeatNeuralNetwork neuralNetwork;
	HashMap<String, GasNeatSynapse> synapseMap;
	@Before
	public void setUp() {
		synapse = mock(GasNeatSynapse.class);
		neuralNetwork = mock(GasNeatNeuralNetwork.class);
		synapseMap = new HashMap<String, GasNeatSynapse>();
		synapseMap.put("123", synapse);
		when(synapse.getSynapseID()).thenReturn("123");
		when(synapse.getSynapticWeight()).thenReturn(1.1);
		synapseGene = new SynapseGene(synapse);
	}
	@Test
	public void constructorTest() {
	
		Assert.assertEquals("123", synapseGene.getSynapseID());
		Assert.assertTrue(1.1 == synapseGene.getSynapticWeight());
	}
	@Test
	public void writeMyselfTest() {
		when(neuralNetwork.getSynapseMap()).thenReturn(synapseMap);
		synapseGene.writeMyself(neuralNetwork);
		Assert.assertEquals("123", synapseGene.getSynapseID());
		Assert.assertTrue(1.1 == synapseGene.getSynapticWeight());		
	}
	@Test
	public void mutateMyselfTest() {
		synapseGene.mutateMyself();
		Assert.assertTrue((Constants.MAXIMUM_SYNAPTIC_WEIGHT >= synapseGene.getSynapticWeight()) || (Constants.MINIMUM_SYNAPTIC_WEIGHT <= synapseGene.getSynapticWeight())); 
	}
	*/
	
}
