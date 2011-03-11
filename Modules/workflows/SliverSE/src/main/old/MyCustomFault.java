package main.old;

import edu.wustl.mobilab.sliver.soap.SerializableException;

public class MyCustomFault extends SerializableException
{
    public MyCustomFault()
    {
        this(null);
    }
    
    /**
     * firstChild = child
     * @param child
     */
    public MyCustomFault(Object child)
    {
        super("http://some/Namespace", "MyCustomFault");
        addProperty("someChildName", child);
    }
}