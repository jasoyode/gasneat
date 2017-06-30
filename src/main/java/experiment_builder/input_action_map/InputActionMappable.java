package experiment_builder.input_action_map;

public interface InputActionMappable {
	
	public void actFromDoubleValue( double  d);
	public void actFromDoubleArrayValue( double[]  d);
	public String stringActionFromDoubleValue( double  d);
	public String stringActionFromDoubleArrayValue( double[]  d);

}
