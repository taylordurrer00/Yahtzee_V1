/* ------------------------------------------------------------------------- *
 * Filename:     ScoreController.java                                        *
 * Description:  Controller class for the score portion of the game.  This   *
 *               includes the scoring and GUI.                               *
 * Author:       Taylor Durrer                                               *
 * Date:         July 22, 2015                                               *
 * ------------------------------------------------------------------------- */

import java.awt.event.*;       // For ActionListener (button clicks)
import javax.swing.*;          // For JComponents (GUI)
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;       // For searching array with binary search

public class ScoreController implements ActionListener
{
  // Divide by this to halve a number
  private static final int HALF_FACTOR = 2;

  // Amount of time to pause thread to make GUI more responsive
  private static final int PAUSE = 5;

  // Number of upper and lower score components (each)
  private static final int NUM_SCORES = 9;

  // Values denoting what section each score component is located in
  private static final int UPPER_SECTION = 0;
  private static final int LOWER_SECTION = 1;

  // Min and max possible dice values
  private static final int MIN_DICE_VAL = 1;
  private static final int MAX_DICE_VAL = 6;

  // Names of upper score elements
  private static final String[ ] UP_NAMES = { "Ones:", "Twos:", "Threes:",
                                              "Fours:", "Fives:", "Sixes:",
                                              "Upper Score:", "Bonus:",
                                              "Upper Total:" };

  // Names of lower score elements
  private static final String[ ] LOW_NAMES = { "3 of a Kind:", "4 of a Kind:",
                                               "Full House:", "Small Straight:",
                                               "Large Straight:", "Yahtzee:",
                                               "Chance:", "Lower Total:",
                                               "Grand Total:" };

  // Array indeces of upper scores
  private static final int ONES = 0;
  private static final int TWOS = 1;
  private static final int THREES = 2;
  private static final int FOURS = 3;
  private static final int FIVES = 4;
  private static final int SIXES = 5;
  private static final int UPPER_SUM = 6;
  private static final int UP_BONUS = 7;
  private static final int UPPER_TOTAL = 8;

  // Array indeces of lower scores
  private static final int THREE_O_KIND = 0;
  private static final int FOUR_O_KIND = 1;
  private static final int FULL_HOUSE = 2;
  private static final int SM_STRAIGHT = 3;
  private static final int LG_STRAIGHT = 4;
  private static final int YAHTZEE = 5;
  private static final int CHANCE = 6;
  private static final int LOWER_TOTAL = 7;
  private static final int GRAND_TOTAL = 8;

  // Array of point values for lower scores (parallel array to Score[ ] upper)
  // (Zero indicates point value = sum of all dice)
  private static final int[ ] LOWER_SCORES = { 0, 0, 25, 30, 40, 50, 0 };

  // Indeces of lower scores in the order that scores are calculated in
  private static final int[ ] SCORE_CALC_ORDER = { 5, 1, 0, 2, 3, 4, 6 };
  
  // Number of matching dice needed for yahtzee
  private static final int YAHTZEE_SAME_DICE = 5;

  // Number of matching dice needed for four of a kind
  private static final int FOUR_KIND_SAME_DICE = 4;

  // Number of matching dice needed for three of a kind
  private static final int THREE_KIND_SAME_DICE = 3;

  // Number of sequential dice needed for a small straight
  private static final int SM_STRAIGHT_SEQ = 4;

  // Number of sequential dice needed for a large straight
  private static final int LG_STRAIGHT_SEQ = 5;

  // Minimum number of points required in upper section to get upper bonus
  private static final int UP_BONUS_THRESHOLD = 63;

  // Bonus points for getting the upper bonus
  private static final int UP_BONUS_POINTS = 35;


  // Score elements
  private Score[ ] upper;   // Array of upper score elements
  private Score[ ] lower;   // Array of lower score elements

  // GUI Components
  private JPanel scorePanel;      // Outer score panel
  private JPanel upperPanel;      // Inner panel for upper scores
  private JPanel lowerPanel;      // Inner panel for lower scores


  /* ----------------------------------------------------------------------- *
   * Ctor Name:      ScoreController()
   * Prototype:      public ScoreController( Container contentPane );
   * Description:    Initialize the score portion of the game and build the GUI.
   * Parameters:
   *      arg 1:     Container contentPane -- Container to build GUI on
   * ----------------------------------------------------------------------- */
  public ScoreController( Container contentPane )
  {
    // Set up the outer score panel
    scorePanel = new JPanel( );
    scorePanel.setLayout( new BoxLayout( scorePanel, BoxLayout.Y_AXIS ) );
    scorePanel.setBorder( BorderFactory.createTitledBorder( "Score Sheet:" ) );

    // Set up the upper score panel
    upperPanel = new JPanel( );
    upperPanel.setLayout( new GridLayout( NUM_SCORES, 1 ) );
    upperPanel.setBorder( BorderFactory.createTitledBorder( "UPPER" ) );

    // Set up the lower panel
    lowerPanel = new JPanel( );
    lowerPanel.setLayout( new GridLayout( NUM_SCORES, 1 ) );
    lowerPanel.setBorder( BorderFactory.createTitledBorder( "LOWER" ) );

    // Set up the score arrays
    upper = new Score[ NUM_SCORES ];
    lower = new Score[ NUM_SCORES ];
    initScores( upper, UP_NAMES, UPPER_SUM, UPPER_SECTION, upperPanel );
    initScores( lower, LOW_NAMES, LOWER_TOTAL, LOWER_SECTION, lowerPanel );

    // Assign the panels to locations
    scorePanel.add( upperPanel );
    scorePanel.add( lowerPanel );
    contentPane.add( scorePanel, BorderLayout.EAST );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  initScores()
   * Prototype:      private void initScores( Score[ ] score, String[ ] name,
   *                                          int limit, int section, 
   *                                          JPanel basePanel );
   * Description:    Initialize all the score components in the passed in 
   *                 Score array with the given names.
   * Parameters:
   *      arg 1:     Score[ ] score -- Array of scores to initialize
   *      arg 2:     String[ ] name -- Array of names to initialize scores with
   *      arg 3:     int limit -- Index to switch from active scores to auto
   *                 scores
   *      arg 4:     int section -- Value denoting which section the score
   *                 components are located in
   *      arg 5:     JPanel basePanel -- Panel to build score components on
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void initScores( Score[ ] score, String[ ] name, int limit, 
                           int section, JPanel basePanel )
  {
    // Initialize each score component in the array
    for( int index = 0; index < score.length; index++ )
    {
      // If index is below the limit, initialize as an ActiveScore
      if( index < limit )
      {
        score[ index ] = new ActiveScore( name[ index ], section, basePanel );

        // Add score controller as an action listener for every value button
        ( (ActiveScore) score[ index ] ).getButton( ).addActionListener( this );
      }

      // Otherwise, initialize as an AutoScore
      else
      {
        score[ index ] = new AutoScore( name[ index ], section, basePanel );
      }
    }
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  calcScores()
   * Prototype:      public void calcScores( int[ ] dice );
   * Description:    Calculate all the score components by delegating to 
   *                 other functions.
   * Parameters:
   *      arg 1:     int[ ] dice -- Array of dice values to calculate scores
   *                                with, sorted in increasing order
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  public void calcScores( int[ ] dice )
  {
    // Reset all the temp values of the active scores
    resetTempActiveScores( );

    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}

    // Enable all the score buttons of the active scores
    enableScoreButtons( true );

    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}

    // Calculate the upper active scores
    scoreUpper( dice );

    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}

    // Calculate the lower active scores
    scoreLower( dice );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  resetTempActiveScores()
   * Prototype:      private void resetTempActiveScores( );
   * Description:    Reset all the active scores' temp values to zero.
   * Parameters:     None
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void resetTempActiveScores( )
  {
    // Reset temp values for all upper active scores
    for( int index = 0; index < UPPER_SUM; index++ )
    {
      ( (ActiveScore) upper[ index ] ).resetTempValue( );
    }

    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    } 
    catch( InterruptedException ex ) {}

    // Reset temp values for all lower active scores
    for( int index = 0; index < LOWER_TOTAL; index++ )
    {
      ( (ActiveScore) lower[ index ] ).resetTempValue( );
    }
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  enableScoreButtons()
   * Prototype:      private void enableScoreButtons( boolean enable );
   * Description:    Enable/disable all unused score buttons depending on 
   *                 boolean passed in.
   * Parameters:
   *      arg 1:     boolean enable -- Whether to enable or disable buttons
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void enableScoreButtons( boolean enable )
  {
    // Enable/disable upper active score buttons
    for( int index = 0; index < UPPER_SUM; index++ )
    {
      ( (ActiveScore) upper[ index ] ).enableButton( enable );
    }

    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}

    // Enable/disable lower active score buttons
    for( int index = 0; index < LOWER_TOTAL; index++ )
    {
      ( (ActiveScore) lower[ index ] ).enableButton( enable );
    }
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  scoreUpper()
   * Prototype:      private void scoreUpper( int[ ] dice );
   * Description:    Calculate the active score components of the upper scores.
   * Parameters:
   *      arg 1:     int[ ] dice -- Dice values sorted in increasing order
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void scoreUpper( int[ ] dice )
  {
    // Iterate through the dice array and tally up the upper score components
    for( int index = 0; index < dice.length; index++ )
    {
      // Add the dice value to the corresponding score component
      ((ActiveScore) upper[ dice[ index ] - 1 ]).addTempValue( dice[ index ] );
    }
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  scoreLower()
   * Prototype:      private void scoreLower( int[ ] dice );
   * Description:    Calculate the active score components of the lower scores
   *                 based on the upper scores and array of dice values.
   * Parameters:
   *      arg 1:     int[ ] dice -- Dice values sorted in increasing order
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void scoreLower( int[ ] dice )
  {
    // Always score chance
    scoreComponent( CHANCE, dice );


    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}


    int sameDice = 0;     // Number of matching dice
    int sameDiceVal = 0;  // Value of the matching dice

    // Use the upper scores to count the largest number of matching dice
    for( int index = 0; index < UPPER_SUM; index++ )
    {
      // Calculate the number of dice with the value "index + 1"
      int numDice = ( (ActiveScore) upper[ index ] ).getTempValue( ) /
                    ( index + 1 );

      // Record the largest number of identical dice
      if( numDice > sameDice )
      {
        sameDice = numDice;
        sameDiceVal = index + 1;
      }
    }


    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}


    // Calculate the lower scores based on the number of matching dice
    int orderIndex = 0;
    int sameDiceNeeded = YAHTZEE_SAME_DICE;

    // Score the first half of the lower scores
    while( orderIndex < SCORE_CALC_ORDER.length/HALF_FACTOR )
    {
      // Check if the criteria for this score component is met
      if( sameDice >= sameDiceNeeded )
      {
        // Calculate the score for this component
        scoreComponent( orderIndex, dice );
      }

      orderIndex++;
      sameDiceNeeded--;
    }


    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    } 
    catch( InterruptedException ex ) {}


    // Check for a full house if three of the dice match
    if( sameDice == ++sameDiceNeeded )
    {
      // Condition 1: the first two dice match and are not sameDiceVal
      boolean cond1 = ( dice[ 0 ] == dice[ 1 ] ) &&
                      ( dice[ 0 ] != sameDiceVal );

      // Condition 2: the last two dice match and are not sameDiceVal
      boolean cond2 = ( dice[ dice.length - 1 ] == dice[ dice.length - 2 ] ) &&
                      ( dice[ dice.length - 1 ] != sameDiceVal );

      // If one of the conditions is met, there is a full house
      if( cond1 || cond2 )
      {
        scoreComponent( orderIndex, dice );
      }
    }

    orderIndex++;
    sameDiceNeeded--;


    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}


    // Check for small and large straights
    if( sameDice <= sameDiceNeeded )
    {
      int sequentialNums = 1;   // Count the number of sequential numbers

      // Iterate through the dice value array to count the # of sequential #'s
      for( int index = 0; index < dice.length - 1; index++ )
      {
        // If the adjacent numbers are sequential
        if( dice[ index + 1 ] - dice[ index ] == 1 )
        {
          sequentialNums++;
        }

        // Pause this thread to allocate more CPU for the GUI thread
        try
        {
          Thread.sleep( PAUSE );
        }
        catch( InterruptedException ex ) {}
      }


      // Pause this thread to allocate more CPU for the GUI thread
      try
      {
        Thread.sleep( PAUSE );
      }
      catch( InterruptedException ex ) {}


      // Check if there is a small straight
        // First check if there are enough seq. numbers for a small straight
      if( ( sequentialNums >= SM_STRAIGHT_SEQ ) &&

        // All small straights must have 3 and 4
        ( ( Arrays.binarySearch( dice, 3 ) >= 0 ) && 
          ( Arrays.binarySearch( dice, 4 ) >= 0 ) ) &&

        // The remaining numbers could be 1 and 2
        ( ( ( Arrays.binarySearch( dice, 1 ) >= 0 ) && 
            ( Arrays.binarySearch( dice, 2 ) >= 0 ) ) ||

        // Or the remaining numbers could be 2 and 5
          ( ( Arrays.binarySearch( dice, 2 ) >= 0 ) &&
            ( Arrays.binarySearch( dice, 5 ) >= 0 ) ) ||

        // Or the remaining numbers could be 5 and 6
          ( ( Arrays.binarySearch( dice, 5 ) >= 0 ) &&
            ( Arrays.binarySearch( dice, 6 ) >= 0 ) ) ) )
      {
        scoreComponent( orderIndex, dice );
      }

      // Pause this thread to allocate more CPU for the GUI thread
      try
      {
        Thread.sleep( PAUSE );
      }
      catch( InterruptedException ex ) {}

      orderIndex++;

      // Check if there is a large straight
      if( sequentialNums == LG_STRAIGHT_SEQ )
      {
        scoreComponent( orderIndex, dice );
      }
    }


    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  scoreComponent()
   * Prototype:      private void scoreComponent( int orderIndex, int[ ] dice );
   * Description:    Calculate the point value of a score component by 
   *                 delegating to scoreTempValue() by interpreting the 
   *                 orderIndex passed in.
   * Parameters:
   *      arg 1:     int orderIndex -- Index in SCORE_CALC_ORDER that 
   *                 corresponds to the index in the lower score array to score
   *      arg 2:     int[ ] dice -- Dice values sorted in increasing order
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void scoreComponent( int orderIndex, int[ ] dice )
  {
    // Determine index in lower score array from orderIndex
    int index = SCORE_CALC_ORDER[ orderIndex ];

    // Set the temporary score of the score component at index
    ( (ActiveScore) lower[ index ] ).scoreTempValue( LOWER_SCORES[ index ],
                                                     dice );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  actionPerformed()
   * Prototype:      public void actionPerformed( ActionEvent evt );
   * Description:    Respond to active score buttons being clicked by disabling
   *                 all score buttons.
   * Parameters:
   *      arg 1:     ActionEvent evt -- Provides info on what button was clicked
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  @Override
  public void actionPerformed( ActionEvent evt )
  {
    // Start a new thread to handle non-GUI related actions
    Thread scoreButtonThread = new Thread( )
    {
      public void run( )
      {
        // Get the action listeners of the score button just clicked
        ActionListener[ ] listeners = ( (JButton) evt.getSource( ) )
                                      .getActionListeners( );

        // Get the Active Score object that the clicked score button belongs to
        ActiveScore clickedScore = ( (ActiveScore) 
                                     listeners[ listeners.length - 1 ] );

        // Number of new points to be added to totals
        int newPoints = clickedScore.getTempValue( );

        // Pause this thread to allocate more CPU for the GUI thread
        try
        {
          Thread.sleep( PAUSE );
        }
        catch( InterruptedException ex ) {}

        // Score button clicked was in the upper section
        if( clickedScore.getSection( ) == UPPER_SECTION )
        {
          // Update upper total
          upper[ UPPER_SUM ].addToValue( newPoints );
          upper[ UPPER_TOTAL ].addToValue( newPoints );

          // Check for upper bonus
          if( upper[ UP_BONUS ].getValue( ) == 0 && 
              upper[ UPPER_SUM ].getValue( ) >= UP_BONUS_THRESHOLD )
          {
            upper[ UP_BONUS ].addToValue( UP_BONUS_POINTS );
            upper[ UPPER_TOTAL ].addToValue( UP_BONUS_POINTS );
            lower[ GRAND_TOTAL ].addToValue( UP_BONUS_POINTS );
          }
        }

        // Score button clicked was in the lower section
        else
        {
          // Update lower total
          lower[ LOWER_TOTAL ].addToValue( newPoints );
        } 

        // Update grand total
        lower[ GRAND_TOTAL ].addToValue( newPoints );

        // Pause this thread to allocate more CPU for the GUI thread
        try
        {
          Thread.sleep( PAUSE );
        }
        catch( InterruptedException ex ) {}

        // Disable all the score buttons
        enableScoreButtons( false );
      }
    };
    scoreButtonThread.setPriority( Thread.NORM_PRIORITY );
    scoreButtonThread.start( );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  setUpActionListener()
   * Prototype:      public void setUpActionListener( Dice[ ] dice, 
   *                                   DiceController diceControl );
   * Description:    Set up the dice and dice controller as an action listener
   *                 for the active score value buttons.
   * Parameters:
   *      arg 1:     Dice[ ] dice -- Dice to add as an action listener
   *      arg 2:     DiceController diceControl -- Dice controller to add as
   *                 an action listener
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  public void setUpActionListener( Dice[ ] dice, DiceController diceControl )
  {
    JButton temp;       // Temporary JButton to store active score button

    // Add as action listener for upper active score buttons
    for( int scoreIndex = 0; scoreIndex < UPPER_SUM; scoreIndex++ )
    {
      // Get the button
      temp = ( (ActiveScore) upper[ scoreIndex ] ).getButton( );

      // Add dice control as an action listener
      temp.addActionListener( diceControl );

      // Add all dice as action listeners
      for( int diceIndex = 0; diceIndex < dice.length; diceIndex++ )
      {
        temp.addActionListener( dice[ diceIndex ] );
      }
    }

    // Add as action listener for lower active score buttons
    for( int scoreIndex = 0; scoreIndex < LOWER_TOTAL; scoreIndex++ )
    {
      // Get the button
      temp = ( (ActiveScore) lower[ scoreIndex ] ).getButton( );

      // Add dice control as an action listener
      temp.addActionListener( diceControl );

      // Add all dice as action listeners
      for( int diceIndex = 0; diceIndex < dice.length; diceIndex++ )
      {
        temp.addActionListener( dice[ diceIndex ] );
      }
    }
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  toString()
   * Prototype:      public String toString( );
   * Description:    Return a string representation of this score controller.
   * Parameters:     None
   * Return Value:   String -- String representation of this score controller
   * ----------------------------------------------------------------------- */
  @Override
  public String toString( )
  {
    return "\nScore Controller";
  }
}
