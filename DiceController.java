/* ------------------------------------------------------------------------- *
 * Filename:     DiceController.java                                         *
 * Description:  Controller class for the dice portion of the game.  This    *
 *               class sets up all the dice and contains the roll button.    *
 * Author:       Taylor Durrer                                               *
 * Date:         July 22, 2015                                               *
 * ------------------------------------------------------------------------- */

import java.awt.event.*;            // For ActionListener (button clicks)
import javax.swing.*;               // For JComponents (GUI)
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Arrays;            // For sorting arrays


public class DiceController implements ActionListener
{
  // Amount of time to pause thread to make GUI more responsive
  private static final int PAUSE = 5;

  private static final int NUM_OF_DICE = 5;     // Number of dice in game

  private static final int MAX_ROLLS = 3;       // Max rolls per round
  private static final int MAX_ROUNDS = 13;     // Max rounds per game

  private static final int H_GAP = 5;           // Horizontal gap between dice

  private static final int GAME_PANEL_ROWS = 2; // # of rows in gamePanel grid
  private static final int GAME_PANEL_COLS = 1; // # of cols in gamePanel grid

  // Vertical internal padding factor for game panel
  private static final double GP_INT_FACTOR_V = .5; 

  // Vertical internal padding factor for roll panel
  private static final double RP_INT_FACTOR_V = .075;

  // Horizontal internal padding factor for roll panel
  private static final double RP_INT_FACTOR_H = .01;

  // Minimum number of pixels between the dice panel and edge of game panel
  private static final int GP_EXT_PADDING = 15;

  // Minimum number of pixels added to the size of the roll panel
  private static final int RP_INT_PADDING = 10;

  // External padding between left/right edges of roll button and game panel
  private static final int RP_EXT_PADDING_LR = 40;

  // External padding between bottom edge of roll button and game panel
  private static final int RP_EXT_PADDING_B = 180;


  // Number of rolls used this round (between 0 and 3)
  private static int numRolls = 0;
  private static int round = 0;


  private JPanel gamePanel;    // Panel containing dice gameplay area

  private JPanel dicePanel;    // Panel to build dice on
  private Dice[ ] dice;        // Array of dice

  private JButton rollButton;  // Button to roll the dice
  private JPanel rollPanel;    // Panel to build roll button on

  private ScoreController scoreControl; // Reference to access scoring method


  /* ----------------------------------------------------------------------- *
   * Ctor Name:      DiceController()
   * Prototype:      public DiceController( Container contentPane,
   *                                        ScoreController score );
   * Description:    Initialize the dice portion of the game and build the GUI.
   * Parameters:
   *      arg 1:     Container contentPane -- Container to build GUI on
   *      arg 2:     ScoreController score -- Reference to access scoring 
   *                 method from
   * ----------------------------------------------------------------------- */
  public DiceController( Container contentPane, ScoreController score )
  {
    // Save reference to score controller to access scoring method when rolling
    scoreControl = score;

    // Set up the game panel
    gamePanel = new JPanel( );
    gamePanel.setLayout( new GridBagLayout( ) );
    gamePanel.setBorder( BorderFactory.createTitledBorder( "Game:" ) );
    contentPane.add( gamePanel, BorderLayout.CENTER );

    // Set up the constraints for the layout
    GridBagConstraints gbc = new GridBagConstraints( );
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = GAME_PANEL_COLS;
    gbc.gridheight = GAME_PANEL_ROWS;
    gbc.weighty = GP_INT_FACTOR_V;

    // Set up the dice panel
    dicePanel = new JPanel( );
    dicePanel.setLayout( new GridLayout( 1, NUM_OF_DICE, H_GAP, 0 ) );

    // Set up the layout constraints specific to the dice panel
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 0, GP_EXT_PADDING, 0, GP_EXT_PADDING );
    gamePanel.add( dicePanel, gbc );

    // Make the roll button
    rollButton = new JButton( "Roll" );
    rollButton.addActionListener( this );

    // Set up the roll panel
    rollPanel = new JPanel( );
    rollPanel.setLayout( new BorderLayout( ) );
    rollPanel.add( rollButton, BorderLayout.CENTER );

    // Set up the layout constraints specific to the roll panel
    gbc.gridy = GridBagConstraints.RELATIVE;
    gbc.ipadx = RP_INT_PADDING;
    gbc.ipady = RP_INT_PADDING;
    gbc.insets = new Insets( 0, RP_EXT_PADDING_LR, RP_EXT_PADDING_B, 
                             RP_EXT_PADDING_LR );
    gbc.weightx = RP_INT_FACTOR_H;
    gbc.weighty = RP_INT_FACTOR_V;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.PAGE_START;
    gamePanel.add( rollPanel, gbc );

    // Make the dice
    dice = new Dice[ NUM_OF_DICE ];
    for( int index = 0; index < NUM_OF_DICE; index++ )
    {
      dice[ index ] = new Dice( dicePanel );
    }

    // Set up the dice and dice controller as action listeners for score buttons
    scoreControl.setUpActionListener( dice, this );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  actionPerformed()
   * Prototype:      public void actionPerformed( ActionEvent evt );
   * Description:    Handle button clicks for the roll button.
   * Parameters:
   *      arg 1:     ActionEvent evt -- Provides info on what button was clicked
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  @Override
  public void actionPerformed( ActionEvent evt )
  {
    // Start a new thread to handle non-GUI related actions
    Thread actionThread = new Thread( )
    {
      public void run( )
      {
        // The roll button was clicked
        if( evt.getSource( ) == rollButton )
        {
          rollButtonClicked( );
        }

        // A score button was clicked
        else
        {
          // Start a new round
          nextRound( );

          // Enable the roll button as long as the game isn't over yet
          SwingUtilities.invokeLater( new Runnable( )
          {
            public void run( )
            {
              rollButton.setEnabled( round < MAX_ROUNDS );
            }
          } );
        }
      }
    };
    actionThread.setPriority( Thread.NORM_PRIORITY );
    actionThread.start( );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  rollButtonClicked()
   * Prototype:      private void rollButtonClicked( );
   * Description:    Handle button clicks for the roll button in a non-GUI
   *                 thread.
   * Parameters:     None
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void rollButtonClicked( )
  {
    // Roll the dice
    rollDice( );

    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}

    // If this is the last roll this round, disable the roll button
    if( numRolls == MAX_ROLLS )
    {
      // Disable the roll button in the event dispatch thread
      SwingUtilities.invokeLater( new Runnable( )
      {
        public void run( )
        {
          rollButton.setEnabled( false );
        }
      } );
    }

    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}

    // Send dice values to the scoring method through the score controller
    scoreControl.calcScores( getDiceValues( ) );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  rollDice()
   * Prototype:      private void rollDice( );
   * Description:    Roll the dice by calling the roll method on each dice and
   *                 increment the number of rolls.
   * Parameters:     None
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void rollDice( )
  {
    numRolls++;

    // Roll each dice
    for( int index = 0; index < dice.length; index++ )
    {
      dice[ index ].roll( numRolls, MAX_ROLLS );
    }
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  getDiceValues()
   * Prototype:      public int[ ] getDiceValues( );
   * Description:    Return the current dice values in a sorted array of ints.
   * Parameters:     None
   * Return Value:   int[ ] -- Sorted array of integer dice values
   * ----------------------------------------------------------------------- */
  public int[ ] getDiceValues( )
  {
    // Make an array to hold the dice values
    int[ ] diceValues = new int[ dice.length ];

    // Copy the dice values into the int array
    for( int index = 0; index < dice.length; index++ )
    {
      diceValues[ index ] = dice[ index ].getValue( );
    }

    // Sort the array of dice values in increasing order
    Arrays.sort( diceValues );

    // Return the array
    return diceValues;
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  nextRound()
   * Prototype:      public static void nextRound( );
   * Description:    Reset the number of rolls back to zero and start a new
   *                 round.
   * Parameters:     None
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  public static void nextRound( )
  {
    numRolls = 0;
    round++;
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  toString()
   * Prototype:      public String toString( );
   * Description:    Return a string representation of this dice controller.
   * Parameters:     None
   * Return Value:   String -- String representation of this dice controller
   * ----------------------------------------------------------------------- */
  @Override
  public String toString( )
  {
    return "\nDice Controller";
  }
}
