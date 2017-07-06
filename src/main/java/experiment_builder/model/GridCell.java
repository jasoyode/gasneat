package experiment_builder.model;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

public class GridCell {
	
	private static Logger logger = Logger.getLogger( GridCell.class );
	
	private @Getter @Setter HashMap<Integer, HashSet<String>> cellEvents;
	private @Getter @Setter HashMap<Integer, HashSet<String>> cellProperty;
	private @Getter @Setter Rectangle[] boundingRectangle;
	//	private @Getter @Setter Reward[] reward;
	private @Getter @Setter HashMap<Integer, Reward> reward;
	private @Getter @Setter int rows, cols;
	private boolean[] visibility;
	
	public GridCell(int total) {
		boundingRectangle = new Rectangle[total];
		reward = new HashMap<>();
		visibility = new boolean[total];
		Arrays.fill(visibility, false);

	}

	public GridCell(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		cellProperty = new HashMap<Integer, HashSet<String>>();
		cellEvents = new HashMap<Integer, HashSet<String>>();
		boundingRectangle = new Rectangle[rows * cols];
		reward = new HashMap<>();
		visibility = new boolean[rows * cols];
		Arrays.fill(visibility, false);

	}

	public boolean[] getVisibility() {
		return visibility;
	}

	public void setVisibility(boolean[] visibility) {
		this.visibility = visibility;
	}
}
