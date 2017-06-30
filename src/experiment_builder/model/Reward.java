package experiment_builder.model;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

public class Reward {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( Reward.class );
	
	private @Setter @Getter String type;
	private @Setter @Getter double value;
	
	public Reward(){
		
	}
	public Reward(String type, double value){
		this.type = type;
		this.value = value;
	}
	
	public Reward deepClone() {
		return new Reward(type, value);
	}
	
	@Override
	public String toString(){
		return "["  + type + "," + value +"]";
		
	}
 
}
