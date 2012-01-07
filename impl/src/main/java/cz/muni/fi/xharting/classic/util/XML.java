package cz.muni.fi.xharting.classic.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML utilities. Copied from Seam 2.2
 * @author Jozef Hartinger
 *
 */
public class XML
{
   /**
    * Parses an XML document safely, as to not resolve any external DTDs
    */
   public static Element getRootElementSafely(InputStream stream) 
       throws DocumentException
   {
       SAXReader saxReader = new SAXReader();
       saxReader.setEntityResolver(new NullEntityResolver());
       saxReader.setMergeAdjacentText(true);
       return saxReader.read(stream).getRootElement();       
   }
   
   
   public static class NullEntityResolver 
       implements EntityResolver 
   {
       private static final byte[] empty = new byte[0];

       public InputSource resolveEntity(String systemId, String publicId) 
           throws SAXException, 
                  IOException 
       {
           return new InputSource(new ByteArrayInputStream(empty));
       }

   }
}
