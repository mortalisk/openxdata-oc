package main.old;

import java.io.IOException;

import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParserException;

import edu.wustl.mobilab.sliver.soap.Channel;
import edu.wustl.mobilab.sliver.soap.MustUnderstandException;

public class MyCustomChannel extends Channel{

	protected void closeImpl() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public Element receiveElement() throws IOException, XmlPullParserException,
			MustUnderstandException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object receiveObject() throws IOException, XmlPullParserException,
			MustUnderstandException {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendElement(Element element) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void sendObject(Object object) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
