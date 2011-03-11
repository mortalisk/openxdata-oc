package UI.Organiser;

//Purpose: Gaps for use in GridBagLayout (or any other).
//Library alternatives are available in the Box class.

import java.awt.*;


public class GBHelper extends GridBagConstraints{
	//============================================================== constructor
    /* Creates helper at top left, component always fills cells. */
    public GBHelper() {
        gridx = 0;
        gridy = 0;
        fill = GridBagConstraints.BOTH;  // Component fills area 
    }
    
    //================================================================== nextCol
    /* Moves the helper's cursor to the right one column. */
    public GBHelper nextCol() {
        gridx++;
        return this;
    }
    
    //================================================================== nextRow
    /* Moves the helper's cursor to first col in next row. */
    public GBHelper nextRow() {
        gridx = 0;
        gridy++;
        return this;
    }
    
    //================================================================== expandW
    /* Expandable Width.  Returns new helper allowing horizontal expansion. 
       A new helper is created so the expansion values don't
       pollute the origin helper. */
    public GBHelper expandW() {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.weightx = 1.0;
        return duplicate;
    }
    
    //================================================================== expandH
    /* Expandable Height. Returns new helper allowing vertical expansion. */
    public GBHelper expandH() {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.weighty = 1.0;
        return duplicate;
    }
    
    //==================================================================== width
    /* Sets the width of the area in terms of number of columns. */
    public GBHelper width(int colsWide) {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.gridwidth = colsWide;
        return duplicate;
    }
    
    //==================================================================== width
    /* Width is set to all remaining columns of the grid. */
    public GBHelper width() {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.gridwidth = REMAINDER;
        return duplicate;
    }
    
    //=================================================================== height
    /* Sets the height of the area in terms of rows. */
    public GBHelper height(int rowsHigh) {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.gridheight = rowsHigh;
        return duplicate;
    }
    
    //=================================================================== height
    /* Height is set to all remaining rows. */
    public GBHelper height() {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.gridheight = REMAINDER;
        return duplicate;
    }
    
    //==================================================================== align
    /* Alignment is set by parameter. */
    public GBHelper align(int alignment) {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.fill   = NONE;
        duplicate.anchor = alignment;
        return duplicate;
    }

	
	
}
