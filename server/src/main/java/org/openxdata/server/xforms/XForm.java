package org.openxdata.server.xforms;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Jonny Heggheim
 */
public interface XForm {

    /**
     * 
     * @return A List with all the field names.
     */
    List<String> getFieldNames();
    
    /**
     * 
     * @return A List with all the names that is a GPS field.
     */
    List<String> getGPSFields();

    /**
     * 
     * @return A list with all the names that is a multimedia field.
     */
    List<String> getMultimediaFields();

    /**
     * 
     * @return A map that contains the name of the field with a list of all the options.
     */
    Map<String, List<String>> getMultiSelectFields();

    /**
     * 
     * @return the original unmodified XForm
     */
    String getXForm();
}
