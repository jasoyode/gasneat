package experiment_builder.constants;

public interface Constants {
	// Reward enum
	public static enum REWARD_TYPES {
		FOOD, WATER
	}
		
	public static final double BOUND_FOUR = 1.0;

	public static final double BOUND_ONE = 0.25;
	public static final double BOUND_THREE = 0.75;

	public static final double BOUND_TWO = 0.5;
	public static final int COLOFFSET = 20;

	public static final int DELAY = 2000; // milliseconds
	public static final int FOUR = 4;

	public static final int GRIDVIEWHEIGHT = 900;
	public static final int GRIDVIEWWIDTH = 500;

	public static final int MAINFRAMEHEIGHT = 900;
	public static final int MAINFRAMEWIDTH = 1400;

	public static final int MAZECREATORHEIGHT = 900;
	public static final int MAZECREATORWIDTH = 500;
	public static final int MAZEFRAMEHEIGHT = 900;
	public static final int MAZEFRAMEWIDTH = 1400;
	public static final int ONE = 1;

	public static final int RIGHTPANELHEIGHT = MAINFRAMEHEIGHT - 50;
	public static final int RIGHTPANELWIDTH = (2 * MAINFRAMEWIDTH) / 3 - 20;
	
	// For row and column spacing in grid
	public static final int ROWOFFSET = 20;
	public static final int THREE = 3;

	public static final int TWO = 2;

}
