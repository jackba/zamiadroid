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


package uni.projecte;

import uni.projecte.controler.ThesaurusControler;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;


public class ActivityProvadora extends Activity {
	
	private ThesaurusControler tC;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.actprovadora);
	    
	    Button provaBtn = (Button) findViewById(R.id.provaButton);
	    
	    provaBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                    Intent settingsActivity = new Intent(getBaseContext(),
                                   ProjecteChooser.class);
                    startActivity(settingsActivity);
            }
    });
	 
	    
    Button importThBtn = (Button) findViewById(R.id.thImport);

	    
	    importThBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                    
            	importThesaure();
    	
            	
            	
            }
    });
	    
	    
	    

	    AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.auto);
	    
	  //   ThesaurusControler tc= new ThesaurusControler();
	    // ArrayList<String> tsList=tc.ReadSettings(this);
	     
	     textView.setWidth(300);
	        
	    

	    
	    
	 //   DataTypeControler dtC = new DataTypeControler(this);
	    
	  // long id= dtC.addDT("tesaurus", "lala", 2);
	   
	
	   //tsList=tc.ReadSettings(this);
	     
	  // Iterator<String> itr = tsList.iterator();
	
	   
	   //String[] items=dtC.getItemsbyDTName("tesaurus");
		 
	   // AutoCompleteTextView e = (AutoCompleteTextView) findViewById(R.id.auto1);

	   


		//	  e.setWidth(180);
			  
			//  ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this,
	          //          android.R.layout.simple_spinner_item,items);
	
			  
			  // adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			   
			  
			   //e.setAdapter(adapter2);   
			    
		  

        
       
        /*
        String[] from= new String[] { ThesaurusItemsDbAdapter.GENUS , ThesaurusItemsDbAdapter.SPECIE, ThesaurusItemsDbAdapter.SUBSPECIE};
        int[] to = new int[] { R.id.Slat , R.id.Slong, R.id.Sdate}; 
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter mostres = new SimpleCursorAdapter(this, R.layout.types_row, c, from, to);
        
        
      llista.setAdapter(mostres);*/
      
      
      
      
      
      /*CursorToStringConverter converter = new CursorToStringConverter() {

         public CharSequence convertToString(Cursor cursor) {

            return cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3);
         }
      }; */

    //  notes.setCursorToStringConverter(converter);

      
	 // ThesaurusListAdapter notes = fillData();

      
     // textView.setAdapter(notes);
      
     // textView.setHint("escriu nom tàxon...");
      
    //  tC.close();
	     
	    // ProjectXMLparser pr= new ProjectXMLparser(null);
	    // pr.readXML(this, "lalaa", false);
	        
	   

			
	    
	}
	
	public void importThesaure(){
		
		tC= new ThesaurusControler(this);
		

		//tC.importThesaurus();
    	
    	tC.close();
		
	}
	



	
	

}
