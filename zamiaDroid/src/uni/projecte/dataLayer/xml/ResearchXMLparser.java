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

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uni.projecte.controler.ResearchControler;
import uni.projecte.dataTypes.ParsedDataSet;

import android.content.Context;

public class ResearchXMLparser {

	private ResearchControler rsH;

	public ResearchXMLparser(ResearchControler rh){
		
		this.rsH=rh;
		
	}
	
	public void readXML(Context c,String url, boolean internet){
	try {
         /* Create a URL we want to load some xml-data from. */
  	  
		   
        /* Get a SAXParser from the SAXPArserFactory. */
        SAXParserFactory spf = SAXParserFactory.newInstance();
		
		SAXParser sp = spf.newSAXParser();
		 XMLReader xr = sp.getXMLReader();
		 
		ResearchHandlerXML myExampleHandler = new ResearchHandlerXML(rsH);
	         xr.setContentHandler(myExampleHandler);
		
  	  if (internet){
  		  
  		URL urlR = new URL(url); 
  		  
  		 xr.parse(new InputSource(urlR.openStream())); 
  		  
  	  }
  	  
  	  else{
  		  
  	  	  InputStream fis = c.getAssets().open(url);
  	  	 xr.parse(new InputSource(fis));
  		  
  	  }
  	
         

         /* Get the XMLReader of the SAXParser we created. */
        
         /* Create a new ContentHandler and apply it to the XML-Reader*/
    
       
         
         /* Parse the xml-data from our URL. */
        
         /* Parsing has finished. */

         /* Our ExampleHandler now provides the parsed data to us. */
         ParsedDataSet parsedExampleDataSet =
                                       myExampleHandler.getParsedData();
         
         rsH.printSamples();

         System.out.println(parsedExampleDataSet.toString());
         
         /* Set the result to be displayed in our GUI. */
         //tv.setText(parsedExampleDataSet.toString());
         
    } catch (Exception e) {
         /* Display any Error to the GUI. */
      //   tv.setText("Error: " + e.getMessage());
        // Log.e(MY_DEBUG_TAG, "WeatherQueryError", e);
    }
	
	
	}
	
}
