/* ------------------------------------------------------------------------- *
 * Filename:     ActiveScore.java                                            *
 * Description:  Active score objects are Score objects that the user can    *
 *               interact with (meaning they also have a button that         *
 *               displays the score that the current dice would give).       *
 * Author:       Taylor Durrer                                               *
 * Date:         July 13, 2015                                               *
 * ------------------------------------------------------------------------- */

import java.awt.event.*;   // For ActionListener (button clicks)
import javax.swing.*;      // For JComponents (GUI)

public class ActiveScore extends Score implements ActionListener
{
  private int tempValue;         // Score of this component for current dice
  private JButton valueButton;   // Button to choose this score component
  private boolean used;          // Whether this component has been used yet


  /* ----------------------------------------------------------------------- *
   * Ctor Name:      ActiveScore()
   * Prototype:      public ActiveScore( String name, int section, 
   *                                     JPanel basePanel );
   * Description:    Create a new active score object with the specified name
   *                 and default value on the passed in JPanel.
   * Parameters:
   *      arg 1:     String name -- Name to use for the name label
   *      arg 2:     int section -- Value denoting what section this score
   *                 component is located in
   *      arg 3:     JPanel basePanel -- GUI panel to build active score on
   * ----------------------------------------------------------------------- */
  public ActiveScore( String name, int section, JPanel basePanel )
  {
    // Call the Score constructor
    super( name, section, basePanel );

    // Set up the button
    valueButton = new JButton( );
    valueButton.setEnabled( false );
    valueButton.addActionListener( this );
    scoreComponent.add( valueButton );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  actionPerformed()
   * Prototype:      public void actionPerformed( ActionEvent evt );
   * Description:    Handles button clicks for the score button.
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
        scoreButtonClicked( );
      }
    };
    scoreButtonThread.setPriority( Thread.NORM_PRIORITY );
    scoreButtonThread.start( );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  scoreButtonClicked()
   * Prototype:      private void scoreButtonClicked( );
   * Description:    Handle button clicks for the value button in a non-UI
   *                 thread.
   * Parameters:     None
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void scoreButtonClicked( )
  {
    // Save temp value as permanent value
    value = tempValue;

    // Invoke on the event dispatch thread
    SwingUtilities.invokeLater( new Runnable( )
    {
      public void run( )
      {
        valueLabel.setText( "" + value );
      }
    } );

    // Disable the button for the rest of the game
    enableButton( false );
    used = true;
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  addTempValue()
   * Prototype:      public void addTempValue( int value );
   * Description:    Add the value passed in to the tempValue.
   * Parameters:
   *      arg 1:     int value -- Value to add to tempValue
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  public void addTempValue( int value )
  {
    tempValue += value;
    updateValueButton( );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  scoreTempValue()
   * Prototype:      public void scoreTempValue( int pointValue, int[ ] dice );
   * Description:    Set the tempValue for this score component to the passed
   *                 in point value.  If the passed in pointValue is zero, set
   *                 the tempValue as the sum of the dice values.
   * Parameters:
   *      arg 1:     int pointValue -- Points awarded for this component
   *      arg 2:     int[ ] dice -- Dice values sorted in increasing order
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  public void scoreTempValue( int pointValue, int[ ] dice )
  {
    // Set tempValue equal to pointValue as long as pointValue isn't zero
    if( pointValue != 0 )
    {
      tempValue = pointValue;
    }

    // Otherwise, the point value is the sum of the dice
    else
    {
      // Reset tempValue
      tempValue = 0;

      // Sum up the dice values
      for( int index = 0; index < dice.length; index++ )
      {
        tempValue += dice[ index ];
      }
    }

    updateValueButton( );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  resetTempValue()
   * Prototype:      public void resetTempValue( );
   * Description:    Reset the temp value back to zero.
   * Parameters:     None
   * Return Values:  None
   * ----------------------------------------------------------------------- */
  public void resetTempValue( )
  {
    tempValue = 0;
    updateValueButton( );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  getTempValue()
   * Prototype:      public int getTempValue( );
   * Description:    Return the temporary score value.
   * Parameters:     None
   * Return Value:   int -- Temp score value
   * ----------------------------------------------------------------------- */
  public int getTempValue( )
  {
    return tempValue;
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  updateValueButton()
   * Prototype:      private void updateValueButton( );
   * Description:    Update the value button to match the tempValue.
   * Parameters:     None
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  private void updateValueButton( )
  {
    // Exit method if score component has already been used
    if( used ) return;

    // Invoke on the event dispatch thread
    SwingUtilities.invokeLater( new Runnable( )
    {
      public void run( )
      {
        valueButton.setText( "" + tempValue );
      }
    } );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  enableButton()
   * Prototype:      public void enableButton( boolean enable );
   * Description:    Enable/disable the value button if this score component
   *                 hasn't already been used.
   * Parameters:
   *      arg 1:     boolean enable -- Whether or not to enable the button
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  public void enableButton( boolean enable )
  {
    // Exit method if this score component has already been used
    if( used ) return;

    // If disabling button, remove its text first
    if( !enable )
    {
      // Invoke this on the event dispatch thread
      SwingUtilities.invokeLater( new Runnable( )
      {
        public void run( )
        {
          valueButton.setText( "" );
        }
      } );
    }

    // Invoke this on the event dispatch thread
    SwingUtilities.invokeLater( new Runnable( )
    {
      public void run( )
      {
        valueButton.setEnabled( enable );
      }
    } );
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  getButton()
   * Prototype:      public JButton getButton( );
   * Description:    Return the value button (useful for adding objects as an
   *                 action listener for the button).
   * Parameters:     None
   * Return Value:   JButton -- Value button
   * ----------------------------------------------------------------------- */
  public JButton getButton( )
  {
    return valueButton;
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  isUsed()
   * Prototype:      public boolean isUsed( );
   * Description:    Return true or false indicating whether or not this score
   *                 component has been used yet.
   * Parameters:     None
   * Return Value:   boolean -- Whether or not this score has been used
   * ----------------------------------------------------------------------- */
  public boolean isUsed( )
  {
    return used;
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  toString()
   * Prototype:      public String toString( );
   * Description:    Return a string representation of this active score.
   * Parameters:     None
   * Return Value:   String -- String representation of this active score
   * ----------------------------------------------------------------------- */
  @Override
  public String toString( )
  {
    return "\nActive Score" +
           "\n    name:       " + name +
           "\n    value:      " + value +
           "\n    tempValue:  " + tempValue +
           "\n    used:       " + used;
  }
}
