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
import uni.projecte.controler.ThesaurusControler.ThesaurusListAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ThesaurusList extends Activity{
	

		private ThesaurusControler tC;
		
		private String name;
		private AutoCompleteTextView autoThItems;
		private boolean thInit=false;
		
		private static final int TH_IMPORT = Menu.FIRST;

 
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	     
	        setContentView(R.layout.thlist);

	        tC=new ThesaurusControler(this);

	        Button bChooseTh = (Button)findViewById(R.id.bChooseTh);
	        bChooseTh.setOnClickListener(bChooseThListener);
	        
	        Button bRemoveTh = (Button)findViewById(R.id.bRemoveTh);
	        bRemoveTh.setOnClickListener(bRemoveThListener);
	        
	       // Button importTh = (Button)findViewById(R.id.importTh);
	       // importTh.setOnClickListener(importThListener);
	        
			autoThItems=(AutoCompleteTextView) findViewById(R.id.thAutoView);	
	        
	        autoThItems.setOnItemClickListener(autoThListener);
	        
	        name=tC.defaultTH;
	        loadThInfo(tC.defaultTH);
          
	       
	    }
	    
	    protected void onStop(){
	    	
	    	super.onStop();
	    	
	    	tC.closeCursors();
	    	if(thInit) tC.closeThReader();
	    	
	    }
	    
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	
	    	menu.add(0, TH_IMPORT, 0,R.string.bImportTh).setIcon(android.R.drawable.ic_menu_upload);
	    	
	    	return super.onCreateOptionsMenu(menu);
	    }


	    
	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {
	    	
	    	
			switch (item.getItemId()) {
			case TH_IMPORT:
				
			  	Intent intent = new Intent(getBaseContext(), DataTypeImport.class);
	        	startActivityForResult(intent,0);
				 			 
				break;
				
			}
			
			return super.onOptionsItemSelected(item);
			
		}
	    
	    private OnItemClickListener autoThListener = new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
            	
            	String value=autoThItems.getText().toString();
            	
            	Cursor c=tC.fetchThesaurusItembyName(value);
            	
            	fillThItemData(c.getString(5), c.getString(6), c.getString(4));
            	
            	c.close();
            
            }

		
    }; 
    
    @SuppressWarnings("unused")
	private OnClickListener importThListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
         
        	Intent intent = new Intent(getBaseContext(),
                    DataTypeImport.class);
        	
        	startActivityForResult(intent,0);
        	
        }
    };
	    
    
    /*
     * 
     * It retrieves thesaurus items properties and show i.
     *  
     * 
     */
    
    
	    
	    public void fillThItemData(String icode, String name, String author){
	    	
	    	TextView tvIcode=(TextView)findViewById(R.id.tvIcode);
        	tvIcode.setText("Icode: "+icode);
        	
        	TextView tvNameCode=(TextView)findViewById(R.id.tvNameCode);
        	tvNameCode.setText("NameCode: "+name);
        	
        	TextView tvAuthor=(TextView)findViewById(R.id.tvAuthor);
        	tvAuthor.setText("Auhor: "+author);
        	
        	TextView tvComplete=(TextView)findViewById(R.id.tvCorrect);

        	if(icode.compareTo("")==0){
	        	
        		tvComplete.setText(R.string.tvIcodeMissing);
        	
        	}
        	else{
        		
        		if(icode.compareTo(name)==0){
	        		
	        		tvComplete.setText(R.string.tvAccepted);
	        		
	        	}
	        	
	        	else{
	        		
	        		String text=getString(R.string.tvSynonymous);
	        		tvComplete.setText(text+" "+tC.fetchThesaurusSynonymous(icode));
	
        	}
        		
        		
        	}
        	
	    }
	    
	    @Override
	    public void onResume()
	    {
	             super.onResume();
	             //loadThesaurus();
	     }
	    
/*
	    private void loadThesaurus (){
	    	
	    	
	    	Cursor cThList = tC.fetchAllThesaurus();
	        startManagingCursor(cThList);

	        String[] from= new String[] { ThesaurusIndexDbAdapter.THNAME, ThesaurusIndexDbAdapter.ITEMS};
	        int[] to = new int[] { R.id.Slat, R.id.Slong}; 
	        
	        // Now create an array adapter and set it to display using our row
	        SimpleCursorAdapter thesaurusCursor = new SimpleCursorAdapter(this, R.layout.types_row, cThList, from, to);
	        
	        
	      llista.setAdapter(thesaurusCursor);
	      
	    	
	    }*/
	    
	    
	    private OnClickListener bChooseThListener = new OnClickListener()
	    {
	        public void onClick(View v)
	        {                        
	         
	        	ThesaurusControler tc= new ThesaurusControler(v.getContext());
	        	
	        	AlertDialog.Builder builder;
	        	
	        	builder= new AlertDialog.Builder(v.getContext());
	        	
	        	final String [] thList=tc.getThList();
	        	
	        	
	        	if(thList.length==0){
	        		

        			Toast.makeText(getBaseContext(), 
    	                    R.string.emptyThList, 
    	                    Toast.LENGTH_SHORT).show();
	        		
	        	}
	        	
	        	else{
	        		
		        	builder.setTitle(R.string.thChooseIntro);
	
		        	builder.setSingleChoiceItems(thList, -1, new DialogInterface.OnClickListener() {
		        	    
		        		
		        		public void onClick(DialogInterface dialog, int item) {
		        	       // Toast.makeText(getApplicationContext(), thList[item], Toast.LENGTH_SHORT).show();
		        	        dialog.dismiss();
		        	        name=thList[item];
		        	        
		        	        tC.closeCursors();
		        	        
		        	        loadThInfo(thList[item]);
		        	        
		        	    }
		        	});
		        	AlertDialog alert = builder.create();
		        	
		        	alert.show();
	        	}
	        	
	        }
	    };
	    
	    private OnClickListener bRemoveThListener = new OnClickListener()
	    {
	        public void onClick(View v)
	        {           
	        	
	        	final ThesaurusControler tc= new ThesaurusControler(v.getContext());
	        	AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		    	
		    	builder.setMessage(R.string.deleteThQuestion)
		    	       .setCancelable(false)
		    	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    
		    	        	   if(tc.removeTh(name)<0){
		    	        		   
		    	        			Toast.makeText(getBaseContext(), 
		            	                    R.string.thUsed, 
		            	                    Toast.LENGTH_SHORT).show();
		    	        		   
		    	        	   }
		    	        	   else{
		    	        		   
		    	        		   tC.closeCursors();
		    	        		   
		    	        		  LinearLayout ll = (LinearLayout)findViewById(R.id.linearThInfo);
		    	   	    			ll.setVisibility(LinearLayout.INVISIBLE);
		    	   	    			
		    	        		   
		    	        	   }
		    	        	   
		    	           }
		    	       })
		    	       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	                
		    	        	   dialog.cancel();
		    	                
		    	           }
		    	       });
		    	AlertDialog alert = builder.create();
		    	
		    	alert.show();
	        	
	        	
	        }
	    };
	    
	    
	    
	    public void loadThInfo(String thName){
	    	
	    	tC=new ThesaurusControler(this);
	    	
	    	
	    	Cursor c=tC.getThInfo(thName);
	    	c.moveToFirst();
    		LinearLayout ll = (LinearLayout)findViewById(R.id.linearThInfo);

	    	
	    	if(c.getCount()<1){
	    		
	    		
	    		ll = (LinearLayout)findViewById(R.id.linearThInfo);
	    		ll.setVisibility(LinearLayout.INVISIBLE);
	    		Button removeTh= (Button) findViewById(R.id.bRemoveTh);
	    		removeTh.setVisibility(Button.INVISIBLE);
	    	}
	    	
	    	else{
		    	
	    		Button removeTh= (Button) findViewById(R.id.bRemoveTh);
	    		removeTh.setVisibility(Button.VISIBLE);
	    		ll.setVisibility(LinearLayout.VISIBLE);
	    		
	    		TextView tvThName = (TextView)findViewById(R.id.tvThName);
	    		
		        
		        tvThName.setText(getString(R.string.thInfoName)+thName);
		        
		        TextView tvThNumItems = (TextView)findViewById(R.id.tvThNumItems);
	
		        tvThNumItems.setText(getString(R.string.thInfoNumItems)+c.getInt(3));
		        
		        tC.initThReader(thName);
		        
		        thInit=true;
		        
		        ThesaurusListAdapter autoListAdapter = tC.fillData();

		        autoThItems.setAdapter(autoListAdapter);   
				autoThItems.setWidth(300);
				autoThItems.setHint(R.string.taxonHint);
			
	    	}
	    	
	    	c.close();

	    	
	    }



	    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	        super.onActivityResult(requestCode, resultCode, intent);

	        
	        if(intent!=null){
	        	
	        	
	        switch(requestCode) {
	        case 0 :
	        	
	        	
	        	Bundle ext = intent.getExtras();
	         	
	        	loadThInfo(ext.getString("thName"));
	        	
	            
	        	break;
	            
	        case 1 :
	        	
	        	
	           
	            break;

	        

	        }
	    }
	    
	   }

	    
}
