/*    	This file is part of ZamiaDroid.
*
*	ZamiaDroid is free software: you can redistribute it and/or modify
*	it under the terms of the GNU General Public License as published by
*	the Free Software Foundation, either version 3 of the License, or
*	(at your option) any later version.
*
*    	ZamiaDroid is distributed in the hope that it will be useful,
*    	but WITHOUT ANY WARRANTY; without even the implied warranty of
*    	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    	GNU General Public License for more details.
*
*    	You should have received a copy of the GNU General Public License
*    	along with ZamiaDroid.  If not, see <http://www.gnu.org/licenses/>.
*/

package uni.projecte.dataLayer.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



import uni.projecte.controler.DataTypeControler;
import uni.projecte.dataTypes.ParsedDataSet;


public class DataTypesHandlerXML extends DefaultHandler{

     // ===========================================================
     // Fields
     // ===========================================================
     
     private boolean in_mytag = false;
     private DataTypeControler dtHandler;
     private long dtId;
	 private String tempVal;
     
     
     
     public DataTypesHandlerXML(DataTypeControler dtH) {
		super();
		
		this.dtHandler=dtH;
	}

	private ParsedDataSet myParsedExampleDataSet = new ParsedDataSet();

     public ParsedDataSet getParsedData() {
          return this.myParsedExampleDataSet;
     }
     @Override
     public void startDocument() throws SAXException {
          this.myParsedExampleDataSet = new ParsedDataSet();
     }

     @Override
     public void endDocument() throws SAXException {
     }

     /** Gets be called on opening tags like:
      * <tag>
      * Can provide attribute(s), when xml was like:
      * <tag attribute="attributeValue">*/
     @Override
     public void startElement(String namespaceURI, String localName,
               String qName, Attributes atts) throws SAXException {
          if (localName.equals("DataTypes")) {

          }else if (localName.equals("DataType")) {
               String name=atts.getValue("name");
               String desc=atts.getValue("description");
               String type=atts.getValue("type");

                      	   
               dtId=dtHandler.addDT(name, desc, type);
               
               dtHandler.incImported();
               
               
          }else if (localName.equals("item")) {
        	  
               this.in_mytag = true;
               
               
          }else if (localName.equals("tagwithnumber")) {
               // Extract an Attribute
               String attrValue = atts.getValue("thenumber");
               int i = Integer.parseInt(attrValue);
               myParsedExampleDataSet.setExtractedInt(i);
          }
     }
     
     @Override
     public void endElement(String namespaceURI, String localName, String qName)
               throws SAXException {
          if (localName.equals("DataTypes")) {
              // this.in_outertag = false;
          }else if (localName.equals("DataType")) {
              // this.in_innertag = false;
          }else if (localName.equals("item")) {
               this.in_mytag = false;
   				
               dtHandler.addItemsDTbyDTid(dtId, tempVal, "més informació");
            
               
          }else if (localName.equals("tagwithnumber")) {
               // Nothing to do here
          }
     }
     
     @Override
    public void characters(char ch[], int start, int length) {
          if(this.in_mytag){
          myParsedExampleDataSet.setExtractedString(new String(ch, start, length));
  		 tempVal = new String(ch,start,length);

     }
    }
}