/* ------------------------------------------------------------------------- *
 * Filename:     Score.java                                                  *
 * Description:  A Score object is a simple score component that only has a  *
 *               name, label, and value.                                     *
 * Author:       Taylor Durrer                                               *
 * Date:         July 22, 2015                                               *
 * ------------------------------------------------------------------------- */

import javax.swing.*;        // For JComponents (GUI)
import java.awt.GridLayout;

public class Score
{
  private static final int NUM_GUI_COMP = 3; // Number of GUI components

  // Amount of time to pause thread to make GUI more responsive
  private static final int PAUSE = 5;

  protected JPanel scoreComponent;  // Panel containing entire score component
  protected JLabel name;            // Name to display on score card
  protected JLabel valueLabel;      // Label to display value of score component
  protected int value;              // Value of this score component
  protected int section;            // Value denoting which section this score
                                    // is in (0 for upper, 1 for lower)


  /* ----------------------------------------------------------------------- *
   * Ctor Name:    Score()
   * Prototype:    public Score( String scoreName, int section,
   *                             JPanel basePanel );
   * Description:  Creates a new Score object with the specified name and 
   *               default value on the passed in JPanel.
   * Parameters:
   *      arg 1:   String scoreName -- Name of the score component
   *      arg 2:   int section -- Value denoting which section this score 
   *               component is in (0 for upper, 1 for lower)
   *      arg 3:   JPanel basePanel -- JPanel to build the score component on
   * ----------------------------------------------------------------------- */
  public Score( String scoreName, int section, JPanel basePanel )
  {
    // Set up this score component's JPanel
    scoreComponent = new JPanel( );
    scoreComponent.setLayout( new GridLayout( 1, NUM_GUI_COMP ) );

    // Initialize all data members
    name = new JLabel( scoreName );
    valueLabel = new JLabel( "" + value, SwingConstants.CENTER );

    // Assign components to locations in containers
    scoreComponent.add( name );
    scoreComponent.add( valueLabel );
    basePanel.add( scoreComponent );

    // Save value indicating which section this score is in
    this.section = section;
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  addToValue()
   * Prototype:      public void addToValue( int points );
   * Description:    Add the passed in points to this score's value.
   * Parameters:
   *      arg 1:     int points -- Number of points to add to value
   * Return Value:   None
   * ----------------------------------------------------------------------- */
  public void addToValue( int points )
  {
    value += points;

    // Update the value label on the event dispatch thread
    SwingUtilities.invokeLater( new Runnable( )
    {
      public void run( )
      {
        valueLabel.setText( "" + value );
      }
    } );

    // Pause this thread to allocate more CPU for the GUI thread
    try
    {
      Thread.sleep( PAUSE );
    }
    catch( InterruptedException ex ) {}
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  getValue()
   * Prototype:      public int getValue( );
   * Description:    Return the value of this score component.
   * Parameters:     None
   * Return Value:   int -- Value of this score component.
   * ----------------------------------------------------------------------- */
  public int getValue( )
  {
    return value;
  }


  /* ----------------------------------------------------------------------- *
   * Function Name:  getSection()
   * Prototype:      public int getSection( );
   * Description:    Return the value corresponding to the section this score
   *                 component is located in (0 for upper, 1 for lower).
   * Parameters:     None
   * Return Value:   int -- Section this score component is located in.
   * ----------------------------------------------------------------------- */
  public int getSection( )
  {
    return section;
  }
}
