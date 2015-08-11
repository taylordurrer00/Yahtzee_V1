/* ------------------------------------------------------------------------- *
 * Filename:     AutoScore.java                                              *
 * Description:  Auto score objects are Score objects that the user cannot   *
 *               interact with; AutoScore components autonomously calculate  *
 *               their score values throughout gameplay.                     *
 * Author:       Taylor Durrer                                               *
 * Date:         July 29, 2015                                               *
 * ------------------------------------------------------------------------- */

import javax.swing.*;      // For JComponents (GUI)

public class AutoScore extends Score
{
  private JLabel filler;       // Blank filler to align GUI properly


  /* ----------------------------------------------------------------------- *
   * Ctor Name:      AutoScore()
   * Prototype:      public AutoScore( String name, int section, 
   *                                   JPanel basePanel );
   * Description:    Create a new auto score object with the specified name and
   *                 default value on the passed in JPanel.
   * Parameters:
   *      arg 1:     String name -- Name to use for the name label
   *      arg 2:     int section -- Value denoting what section this score 
   *                 component is located in
   *      arg 3:     JPanel basePanel -- GUI panel to build auto score on
   * ----------------------------------------------------------------------- */
  public AutoScore( String name, int section, JPanel basePanel )
  {
    // Call the Score constructor
    super( name, section, basePanel );

    // Set up the filler panel
    filler = new JLabel( );
    scoreComponent.add( filler );
  }
}
