package gasNEAT.foodWaterTask;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class FoodAndWaterTaskCanvas extends Canvas {

//TODO FIXME  - this whole thing needs to be redone if it is to be used at all
	
	
private double trackLength;

private double trackLengthHalf;

private double[] poleLengths;

private double cartPos = 0;

private double[] poleAngles;

private double maxPoleLength = 0;

private static final int DISPLAY_CART_WIDTH = 20;

private static final int DISPLAY_CART_HEIGHT = 5;

/**
 * @param g
 */
public void paint( Graphics g ) {
	Color orig = g.getColor();
	int displayTrackLength = (int) ( getWidth() * 0.80 );
	double scaleRatio = displayTrackLength / trackLength;

	// track
	g.setColor( Color.BLACK );
	int displayTrackYPos = (int) ( getHeight() * 0.90 );
	int displayTrackLeftXPos = ( getWidth() / 2 ) - ( displayTrackLength / 2 );
	g.drawLine( displayTrackLeftXPos, displayTrackYPos,
			( displayTrackLeftXPos + displayTrackLength ), displayTrackYPos );

	// cart
	g.setColor( Color.MAGENTA );
	int displayCartCenterXPos = displayTrackLeftXPos
			+ (int) ( displayTrackLength * ( ( cartPos + trackLengthHalf ) / trackLength ) );
	int displayCartLeftXPos = (int) ( displayCartCenterXPos - ( (double) DISPLAY_CART_WIDTH / 2 ) );
	g.fillRect( displayCartLeftXPos, displayTrackYPos - DISPLAY_CART_HEIGHT, DISPLAY_CART_WIDTH,
			DISPLAY_CART_HEIGHT );

	// poles
	ArrayList colors = new ArrayList();
	colors.add( Color.BLUE );
	colors.add( Color.CYAN );
	for ( int i = 0; i < poleAngles.length; ++i ) {
		g.setColor( (Color) colors.get( i ) );
		double displayPoleLength = poleLengths[ i ] * scaleRatio;
		double radians = poleAngles[ i ] * Math.PI;
		double x = Math.sin( radians ) * displayPoleLength;
		double y = Math.cos( radians ) * displayPoleLength;
		g.drawLine( displayCartCenterXPos, displayTrackYPos - DISPLAY_CART_HEIGHT,
				(int) ( displayCartCenterXPos + x ),
				(int) ( ( displayTrackYPos - DISPLAY_CART_HEIGHT ) - y ) );
	}

	g.setColor( orig );
}

/**
 * ctor
 * 
 * @param aTrackLength
 * @param aPoleLengths
 */
public FoodAndWaterTaskCanvas( double aTrackLength, double[] aPoleLengths ) {
	trackLength = aTrackLength;
	trackLengthHalf = trackLength / 2;
	poleLengths = aPoleLengths;
	poleAngles = new double[ poleLengths.length ];
	for ( int i = 0; i < poleLengths.length; ++i ) {
		if ( poleLengths[ i ] > maxPoleLength )
			maxPoleLength = poleLengths[ i ];
		poleAngles[ i ] = 0;
	}
}

/**
 * @param aCartPos
 * @param aPoleAngles
 */
public void step( double aCartPos, double[] aPoleAngles ) {
	if ( poleLengths.length != aPoleAngles.length )
		throw new IllegalArgumentException( "wrong # poles, expected " + poleLengths.length
				+ ", got " + aPoleAngles.length );
	if ( aCartPos < -trackLengthHalf || aCartPos > trackLengthHalf )
		throw new IllegalArgumentException( "wrong cart pos, expected abs < " + trackLengthHalf
				+ ", got " + aCartPos );
	cartPos = aCartPos;
	poleAngles = aPoleAngles;
}

}
