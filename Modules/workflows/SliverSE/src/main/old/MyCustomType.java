package main.old;

import org.ksoap2.serialization.SoapObject;

public class MyCustomType extends SoapObject{
    
	public MyCustomType(){
        this(null);
    }
    
	/**
	 * firstParameter = obj
	 */
    public MyCustomType(Object obj){
        super("http://some/Namespace", "MyCustomType");
        addProperty("somePropertyName", obj);
    }
}
