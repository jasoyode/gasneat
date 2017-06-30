package experiment_builder.events_commands;

public interface EventCommand {
	
	public String className();
	public void execute();
	public void register();
	
}
