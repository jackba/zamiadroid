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

//import org.xml.sax.Attributes;
//import org.xml.sax.SAXException;
//import org.xml.sax.helpers.DefaultHandler;

import uni.projecte.controler.ResearchControler;
import uni.projecte.dataTypes.ParsedDataSet;
import uni.projecte.dataTypes.Research;


public class ResearchHandlerXML extends DefaultHandler{

     // ===========================================================
     // Fields
     // ===========================================================
     
    // private boolean in_outertag = false;
     //private boolean in_innertag = false;
     private boolean in_mytag = false;
     private ResearchControler rsHandler;
     private Research tmpRS;
	 private String tempVal;
	 private String tmpDesc;
     
     
     
     public ResearchHandlerXML(ResearchControler rsH) {
		super();
		
		this.rsHandler=rsH;
	}

	private ParsedDataSet myParsedExampleDataSet = new ParsedDataSet();

     // ===========================================================
     // Getter & Setter
     // ===========================================================

     public ParsedDataSet getParsedData() {
          return this.myParsedExampleDataSet;
     }

     // ===========================================================
     // Methods
     // ===========================================================
     @Override
     public void startDocument() throws SAXException {
          this.myParsedExampleDataSet = new ParsedDataSet();
     }

     @Override
     public void endDocument() throws SAXException {
          // Nothing to do
     }

     /** Gets be called on opening tags like:
      * <tag>
      * Can provide attribute(s), when xml was like:
      * <tag attribute="attributeValue">*/
     @Override
     public void startElement(String namespaceURI, String localName,
               String qName, Attributes atts) throws SAXException {
          if (localName.equals("DataTypes")) {
              // this.in_outertag = true;
          }else if (localName.equals("Research")) {
              //s this.in_innertag = true;
               String name=atts.getValue("name");
               String desc=atts.getValue("description");

               tmpRS=new Research(name,desc);
               rsHandler.setType(name,tmpRS);
               
          }else if (localName.equals("Attribute")) {
        	  
               this.in_mytag = true;
               
               tmpDesc=atts.getValue("type");
               
          }else if (localName.equals("tagwithnumber")) {
               // Extract an Attribute
               String attrValue = atts.getValue("thenumber");
               int i = Integer.parseInt(attrValue);
               myParsedExampleDataSet.setExtractedInt(i);
          }
     }
     
     /** Gets be called on closing tags like:
      * </tag> */
     @Override
     public void endElement(String namespaceURI, String localName, String qName)
               throws SAXException {
          if (localName.equals("ResearchTypes")) {
              // this.in_outertag = false;
          }else if (localName.equals("Research")) {
              // this.in_innertag = false;
          }else if (localName.equals("Attribute")) {
               this.in_mytag = false;
   			
               tmpRS.addAtribbute(tempVal,tmpDesc);
   			
          }else if (localName.equals("tagwithnumber")) {
               // Nothing to do here
          }
     }
     
     /** Gets be called on the following structure:
      * <tag>characters</tag> */
     @Override
    public void characters(char ch[], int start, int length) {
          if(this.in_mytag){
          myParsedExampleDataSet.setExtractedString(new String(ch, start, length));
  		 tempVal = new String(ch,start,length);

     }
    }
}