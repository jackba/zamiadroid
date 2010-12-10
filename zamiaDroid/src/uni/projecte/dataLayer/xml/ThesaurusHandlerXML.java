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

import uni.projecte.controler.ThesaurusControler;
import uni.projecte.dataTypes.ParsedDataSet;


public class ThesaurusHandlerXML extends DefaultHandler{

     // ===========================================================
     // Fields
     // ===========================================================
     
	 private String tempVal;
	 private String genus;
	 private String specie;
	 private String subspecie;
	 private String iCode;
	 private String nameCode;
	 private String author;
	 private String level;
	 private int total;
	 
	 private ThesaurusControler thCont;
     
     
     
     public ThesaurusHandlerXML(ThesaurusControler thCont) {
		super();
		total=0;
		this.thCont=thCont;
		
	}

	private ParsedDataSet myParsedExampleDataSet = new ParsedDataSet();
	//private boolean in_innertag;
	//private boolean in_outertag;

     // ===========================================================
     // Getter & Setter
     // ===========================================================

     public ParsedDataSet getParsedData() {
          return this.myParsedExampleDataSet;
     }
     
     public int getTotalItems(){
    	  	 
    	 return this.total;
    	 
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

     
     
     }

     @Override
     public void startElement(String namespaceURI, String localName,
               String qName, Attributes atts) throws SAXException {
      
    	 
    	 if (localName.equals("taxon")) {
              // this.in_innertag = true;
    		 
               String category=atts.getValue("name");
               level=atts.getValue("level");
               iCode=atts.getValue("icode");
               nameCode=atts.getValue("namecode");
               
	         /*   if (level.equals("Class")){ 
		               
          		}
	            
	            else if (level.equals("Order")){
	            	
	            }*/
	            
	            
	            if (level.equals("Species")){

	            	specie=category;
	            	
	            }
	            
	      
	            
	            else if (level.equals("Subspecies")){
  	
	            	subspecie=category;

	            }
	            
	            else if(level.equals("Genus")){
  	
	            	genus=category;
	            	
	            }
                           
          }
    	 
         else if(localName.equals("author")){
         	
         	
         }
      
      else if (localName.equals("taxon_pool")) {
          // this.in_outertag = true;
    	  thCont.startTransaction();
    	  
    	  
      }
     }
     
     /** Gets be called on closing tags like:
      * </tag> */
     @Override
     public void endElement(String namespaceURI, String localName, String qName)
               throws SAXException {
    	 
    	 
         if (localName.equals("taxon")) {


	          if (level.equals("Species")){
	          		
	          	thCont.addElement(genus, specie,"", iCode, nameCode, author);
	          	level="Genus";
	        	
	          }
	          
	          else if (level.equals("Subspecies")){
		          	
	          	thCont.addElement(genus,specie,subspecie, iCode, nameCode, author);
	          	subspecie="";
	          	level="Species";
	
	          }

          
          }
          else if (localName.equals("author")) {
              
        	  if(tempVal.equals("null")) author="";
        	  else author=tempVal;
              
          }
         
          else if (localName.equals("taxon_pool")) {

        	  
        	  
         }
     }
     
     /** Gets be called on the following structure:
      * <tag>characters</tag> */
     @Override
    public void characters(char ch[], int start, int length) {
        //  if(this.in_mytag){
         // myParsedExampleDataSet.setExtractedString(new String(ch, start, length));
  		 tempVal = new String(ch,start,length);

    // }
    }
}