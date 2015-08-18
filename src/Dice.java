/* ------------------------------------------------------------------------- *
 * Filename:     Dice.java                                                   *
 * Description:  Creates dice objects that have a number, picture, and roll  *
 *               button.                                                     *
 * Author:       Taylor Durrer                                               *
 * Date:         June 23, 2015                                               *
 * ------------------------------------------------------------------------- */

import java.awt.event.*;       // For ActionListener (button clicks)
import javax.swing.*;          // For JComponents (GUI)
import java.awt.Component;
import java.net.URL;           // For loading images from jar file

public class Dice implements ActionListener
{
  // Default dice images
  private static final ImageIcon[ ] DICE_IMG =
  {
    loadImage( "OneDice.GIF" ),
    loadImage( "TwoDice.GIF" ),
    loadImage( "ThreeDice.GIF" ),
    loadImage( "FourDice.GIF" ),
    loadImage( "FiveDice.GIF" ),
    loadImage( "SixDice.GIF" )
  };

  // Held dice images
  private static final ImageIcon[ ] DICE_IMG_HELD =
  {
    loadImage( "OneDiceHeld.GIF" ),
    loadImage( "TwoDiceHeld.GIF" ),
    loadImage( "ThreeDiceHeld.GIF" ),
    loadImage( "FourDiceHeld.GIF" ),
    loadImage( "FiveDiceHeld.GIF" ),
    loadImage( "SixDiceHeld.GIF" )
  };

  // Amount of time to pause thread to make GUI more responsive
  private static final int PAUSE = 5;

  private static final int NUM_OF_DICE = 5;  // Number of dice in the game
  private static final int MIN_VAL = 1;      // Minimum/default value of dice
  private static final int MAX_VAL = 6;      // Maximum possible value of dice


  private JPanel diceComponent; // Panel containing single dice and hold button
  private int value;            // Value of the dice
  private JLabel image;         // Image of dice being displayed
  private boolean hold;         // Whether or not the dice is being held
  private JButton holdButton;   // Button to hold/unhold dice


  /* ----------------------------------------------------------------------- *
   * Function Name:  Dice()
   * Prototype:      public Dice( JPanel basePanel );
   * Description:    Constructs a new dice object on the passed in panel.
   * Parameters:
   *      arg 1:     JPanel basePanel -- GUI panel to build dice on
   * ----------------------------------------------------------------------- */
  public Dice( JPanel basePanel )
  {
    // Set up this dice component's JPanel
    diceComponent = new JPanel( );
    diceComponent.setLayout( new BoxLayout( diceComponent, BoxLayout.Y_AXIS ) );

    // Initialize data members
    hold = false;
    value = MIN_VAL;

    // Set up the dice image
    image = new JLabel( DICE_IMG[ value - 1 ] );
    image.setAlignmentX( Component.CENTER_ALIGNMENT );
    diceComponent.add( image );

    // Set up the hold button
    holdButton = new JButton( "Hold" );
    holdButton.setAlignmentX( Component.CENTER_ALIGNMENT );
    holdButton.addActionListener( this );
    diceComponent.add( holdButton );

    // Disable the hold buttons when the game starts
    holdButton.setEnabled( false );

    // Add the entire dice component to the base panel
    basePanel.add( diceComponent );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  updateImage()
   * Prototype:      private void updateImage( );
   * Description:    Update the image of the dice to its corresponding value
   *                 and hold status.
   * Parameters:     None
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void updateImage( )
  {
    // If dice is held
    if( hold )
    {
      // Invoke on the event dispatch thread
      SwingUtilities.invokeLater( new Runnable( )
      {
        public void run( )
        {
          image.setIcon( DICE_IMG_HELD[ value - 1 ] );
        }
      } );
    }

    // If dice is not held
    else
    {
      // Invoke on the event dispatch thread
      SwingUtilities.invokeLater( new Runnable( )
      {
        public void run( )
        {
          image.setIcon( DICE_IMG[ value - 1 ] );
        }
      } );
    }
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  actionPerformed()
   * Prototype:      public void actionPerformed( ActionEvent evt );
   * Description:    Calls the appropriate method to handle button clicks for 
   *                 the hold button and the score buttons.
   * Parameters:
   *      arg 1:     ActionEvent evt -- Provides info on what button was clicked
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  @Override
  public void actionPerformed( ActionEvent evt )
  {
    Thread actionThread = new Thread( )
    {
      public void run( )
      {
        // The hold button was clicked
        if( evt.getSource( ) == holdButton )
        {
          hold( );
        }

        // A score button was clicked
        else
        {
          // Disable the hold button (on the event dispatch thread)
          SwingUtilities.invokeLater( new Runnable( )
          {
            public void run( )
            {
              holdButton.setEnabled( false );
            }
          } );
        }
      }
    };
    actionThread.setPriority( Thread.NORM_PRIORITY );
    actionThread.start( );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  hold()
   * Prototype:      private void hold( );
   * Description:    Hold this die.  (Called when the hold button is clicked).
   * Parameters:     None
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void hold( )
  {
    hold = !hold;
    updateImage( );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  roll()
   * Prototype:      public void roll( int numRolls, int maxRolls );
   * Description:    Roll the dice and enable/disable the hold buttons depending
   *                 on how many times the dice have been rolled this round.
   * Parameters:
   *      arg 1:     int numRolls -- # of times dice have been rolled this round
   *      arg 2:     int maxRolls -- Max # of rolls per round
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  public void roll( int numRolls, int maxRolls )
  {
    // If this is the first roll, reset dice so none are held
    if( numRolls == 1 )
    {
      hold = false;
    }

    // Only roll the dice if they are not held
    if( !hold )
    {
      // Generate a random value for the die
      value = (int)( Math.floor( Math.random( ) * ( NUM_OF_DICE + 1 ) ) + 1 );
      updateImage( );
    }

    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( 5 );
    }
    catch( InterruptedException ex ) {}

    // Disable hold button if on last roll of round, otherwise enable
    boolean enable = ( numRolls < maxRolls )? true: false;
    
    // Enable/disable the hold button in the event dispatch thread
    SwingUtilities.invokeLater( new Runnable( )
    {
      public void run( )
      {
        holdButton.setEnabled( enable );
      }
    } );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  getValue()
   * Prototype:      public int getValue( );
   * Description:    Return the value of this dice.
   * Parameters:     None
   * Return Value:   int -- The current value of this dice.
   * ----------------------------------------------------------------------- */
  public int getValue( )
  {
    return value;
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  toString()
   * Prototype:      public String toString( );
   * Description:    Return a string representation of this dice.
   * Parameters:     None
   * Return Value:   String -- String representation of this dice
   * ----------------------------------------------------------------------- */
  @Override
  public String toString( )
  {
    return "\nDice" +
           "\n    value:      " + value +
           "\n    hold:       " + hold;
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  loadImage()
   * Prototype:      private static ImageIcon loadImage( String name );
   * Description:    Load the specified image from the jar file.
   * Parameters:
   *      arg 1:     String name -- File name of the image to load
   * Return Value:   ImageIcon -- Image loaded from jar file
   * ----------------------------------------------------------------------- */
  private static ImageIcon loadImage( String name )
  {
    // Convert the path name into a URL
    URL resource = Dice.class.getResource( name );

    // Return the image
    return new ImageIcon( resource );
  }
}
