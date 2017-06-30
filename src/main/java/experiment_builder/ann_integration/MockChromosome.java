package experiment_builder.ann_integration;

import org.apache.logging.log4j.LogManager;
import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

public class MockChromosome extends Chromosome {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( MockChromosome.class );
	
	private @Setter @Getter int fitnessValue;
	private @Setter @Getter String name;
	
	public MockChromosome(String name) {
		super( new ChromosomeMaterial() , (long) 1);
		this.name=name;
	}


}
