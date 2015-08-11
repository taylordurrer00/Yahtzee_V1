/* ------------------------------------------------------------------------- *
 * Filename:     Yahtzee.java                                                *
 * Description:  Controller class for the Yahtzee game which sets up the     *
 *               gameplay and GUI.                                           *
 * Author:       Taylor Durrer                                               *
 * Date:         July 22, 2015                                               *
 * ------------------------------------------------------------------------- */

import javax.swing.*;         // For JComponents (GUI)
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;    // For centering window
import java.awt.Toolkit;      // For centering window

public class Yahtzee
{
  // Divide by this to halve a number
  private static final int HALF_DIV = 2;

  private static final int H_GAP = 10;  // Horizontal gap between components
  private static final int V_GAP = 10;  // Vertical gap between components

  // GUI components
  private JFrame frame;           // Game window
  private Container contentPane;  // Container for all the GUI components

  // Game components
  private ScoreController scoreControl;  // Score portion of game (includes GUI)
  private DiceController diceControl;    // Dice portion of game (includes GUI)


  /* ----------------------------------------------------------------------- *
   * Ctor Name:      Yahtzee()
   * Prototype:      public Yahtzee( );
   * Description:    Set up the game window and all GUI components to begin 
   *                 the Yahtzee game.
   * Parameters:     None
   * ----------------------------------------------------------------------- */
  public Yahtzee( )
  {
    // Set up the game window
    frame = new JFrame( "Yahtzee" );
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    // Set up container to put components on
    contentPane = frame.getContentPane( );
    contentPane.setLayout( new BorderLayout( H_GAP, V_GAP ) );

    // Set up components and add them to the container
    scoreControl = new ScoreController( contentPane );
    diceControl = new DiceController( contentPane, scoreControl );

    // Size the frame
    frame.pack( );

    // Vertically and horizontally center the game window on the screen
    Dimension dim = Toolkit.getDefaultToolkit( ).getScreenSize( );
    int xPosition = dim.width / HALF_DIV - frame.getSize( ).width / HALF_DIV;
    int yPosition = dim.height / HALF_DIV - frame.getSize( ).height / HALF_DIV;
    frame.setLocation( xPosition, yPosition );

    // Display the window
    frame.setVisible( true );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  main()
   * Prototype:      public static void main( String[ ] args );
   * Description:    Starts a new Yahtzee game on the event dispatch thread.
   * Parameters:
   *      arg 1:     String[ ] args -- Command line arguments (not used)
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  public static void main( String[ ] args )
  {
    // Initialize the GUI on the event dispatch thread
    SwingUtilities.invokeLater( new Runnable( )
    {
      public void run( )
      {
        Yahtzee game = new Yahtzee( );
      }
    } );
  }
}
