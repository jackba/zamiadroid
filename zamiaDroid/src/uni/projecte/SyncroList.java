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


import java.util.ArrayList;

import uni.projecte.dataLayer.bd.ResearchDbAdapter;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class SyncroList extends Activity {
   
	   private ResearchDbAdapter mDdResearchTypes;


	   public static final String KEY_NOM = "Nom";
	   public static final String KEY_ID = "Id";
	   public static final String DESCRIPCIO = "Descripcio";


	   private ListView rsList;
	   public final static int SUCCESS_RETURN_CODE = 1;

	   

	 public void onCreate(Bundle savedInstanceState) {
	    
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.researchlistsamples);
	    
	  
	     rsList = (ListView)findViewById(R.id.lresearches);
	   
	    
	     mDdResearchTypes = new ResearchDbAdapter(this);
	
	     mDdResearchTypes.open();
	     
	     Cursor rt= mDdResearchTypes.fetchAllResearches();
	     
	  
	     
	     // Now create an array adapter and set it to display using our row
	
	     ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	             android.R.layout.simple_list_item_1, prepareList(rt));
	     
	    rsList.setAdapter(adapter);
	    
	   rsList.setOnItemClickListener(theListListener);
	    
	
	 }
 

	 public OnItemClickListener theListListener = new OnItemClickListener() {
		 
		 public void onItemClick(android.widget.AdapterView<?> parent, View v, int position, long id) {
		     
			 TextView tv=(TextView)v;
			 String choosenRs=(String)tv.getText();
			 
			 Cursor c=mDdResearchTypes.fetchResearchByName(choosenRs);
			 
			c.moveToFirst();
			 //String nom=c.getString(1);
			 
		/*	 String nom="ujuj";
		 	
		 	   Toast.makeText(getBaseContext(), 
		                 "Id:" +id+ " Has seleccionat:" + nom , 
		                 Toast.LENGTH_SHORT).show();
		 	   */
		 
		 	   
		 	   Intent intent = new Intent(v.getContext(),SamplesList.class);
					Bundle b = new Bundle();

					b.putLong("id", c.getLong(0));
					intent.putExtras(b);
					
					
					startActivity(intent);
			
		 	
		 }
	 };

 //---create an anonymous class to act as a button click listener---

	 private ArrayList<String>  prepareList(Cursor rt) {
			
	     ArrayList<String> list = new ArrayList<String>();
	     rt.moveToFirst();
	 	
	     int n=rt.getCount();
	 	
	
	 	
	 	
	 	for (int i=0;i<n;i++){
	
	 		
	 		list.add(rt.getString(1));
	 		rt.moveToNext();
	 		
	 	}
	 	
	 	
	 	return list;
	 	
	 	
		}
	
	 
	}

 
