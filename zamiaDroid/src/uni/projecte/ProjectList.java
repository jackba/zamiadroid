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
import java.util.Iterator;

import uni.projecte.controler.ResearchControler;
import uni.projecte.controler.SampleControler;
import uni.projecte.dataLayer.bd.AttributeDbAdapter;
import uni.projecte.dataLayer.bd.ResearchDbAdapter;
import uni.projecte.dataTypes.ProjectField;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectList extends Activity {
	
		private ListView projectList;

		static final int DIALOG_RS_DESCRIPTION = 0;



		
		private String name;
		private String desc;
		private String attributes;
		
		private RadioButton chosenRB;
		private SharedPreferences preferences;
		
		private String defaultProject="";
		
		private Cursor cRsList;


 
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	     
	        
	        setContentView(R.layout.researchlist);

	        projectList = (ListView)findViewById(R.id.listResearches);
	 	          
	        
	        //simple click listener
	        projectList.setOnItemClickListener(theListListener);
	          
	        
	        //listener for item cliked more than 3 seconds
	        //projectList.setOnItemLongClickListener(theListLongListener);

	        preferences = getSharedPreferences(Main.PREF_FILE_NAME, MODE_PRIVATE);

	        long projId = preferences.getLong("predProjectId", -1);
	        
	        if(projId!=-1) {
	        	
	        	ResearchControler rs= new ResearchControler(this);
	        	rs.loadProjectInfoById(projId);
	        	defaultProject=rs.getName();
	        	
	        }
	        
	        loadProjects();
          
	       
	    }
	    
	    @Override
	    public void onResume()
	    {
	             super.onResume();
	             loadProjects();
	     }
	    
	    public void onStop(){
	    	
	    	super.onStop();
	    	
	    	cRsList.close();
	    	
	    	
	    }
	    
	    /*
	     * 
	     * Project Removal providing its name
	     * 
	     */
	    
	    private int removeProject(String projName){
	    	
	    	int status=0;
	    	
	    	ResearchDbAdapter mDdResearchTypes = new ResearchDbAdapter(this);
	 	   	AttributeDbAdapter mDbHAtrib = new AttributeDbAdapter(this);
	 	   	
	 	   	SampleControler sC=new SampleControler(this);
	 	   	
	 	   	
	 	 //  	SampleDbAdapter mDbSample =new SampleDbAdapter(this);
	 	   	
	 	   	
	 	   	mDbHAtrib.open();

	 	    mDdResearchTypes.open();
	 	   	 
	 	   	 Cursor c=mDdResearchTypes.fetchResearchByName(projName);
	 	   	 
	 	   	 c.moveToFirst();
	 	   	 
	 	   	 long projId=c.getLong(0);
	 	   	 
	 	   	 Cursor numSamp=sC.getCitationsWithFirstFieldByProjectId(projId);
	 	   	 	 	   	
	 	   	numSamp.moveToFirst();
	 	   	
	 	   	if (numSamp.getCount()<=0){
	 	   	 
	 	   	 	 	   	 
	 	   	 mDdResearchTypes.deleteResearch(projId);
		 	   	 
	 	   	 mDbHAtrib.deleteAttributes(projId);
	 	   	 
	 	   	}
	 	   	
	 	   	else{
	 	   		
		 	   	 Toast.makeText(getBaseContext(), 
		 	              R.string.hasCitations, 
		 	              Toast.LENGTH_LONG).show();
		 	   	 
		 	   	 status=-1;
	 	   		
	 	   	}
	 	   	 
	 	   	numSamp.close();
	 	   	
	 	   	mDdResearchTypes.close();
	 	   	mDbHAtrib.close();
	 	   	
	 	   	return status;
	    	
	    	
	    }
	    
	    /*
	     * It fills the listAdapter with the list of project Names
	     * 
	     */
	    

	    private void loadProjects (){
	    	
	    	ResearchControler rsC= new ResearchControler(this);

	    	cRsList=rsC.getResearchListCursor();
	    	
	   
	        startManagingCursor(cRsList);
	        
	        ProjectListAdapter projectsAdapter = new ProjectListAdapter(this, cRsList);
	        
	        projectList.setAdapter(projectsAdapter);

	    	
	    }
	    
	    
	  /*
	   * 
	   * This Listener is triggered when a list item is clicked.
	   * 
	   * The method fetchs projFields and shows them in a Dialog.
	   * 
	   */
	  
	  
	  public OnItemClickListener theListListener = new OnItemClickListener() {
	    
		    public void onItemClick(android.widget.AdapterView<?> parent, View v, int position, long id) {
		        
			   	 TextView tv=(TextView)v;
			   	 String projName=(String)tv.getText();
			   	 
			   	 ResearchControler rsC=new ResearchControler(parent.getContext());
		     
			     rsC.loadResearchInfoByName(projName);
	 
			   	 name=rsC.getName();
			   	 desc=rsC.getThName();
			   	 
			   	 
			   	Intent intent = new Intent(v.getContext(), ProjectInfo.class);
			       
	 			Bundle b = new Bundle();
	 			b.putLong("Id", rsC.getIdRs());
	 			intent.putExtras(b);
	 			
	 			b.putString("projName", name);
	 			intent.putExtras(b);
	 			intent.putExtras(b);
	 			b = new Bundle();
	 			b.putString("projDescription", desc);
	 			intent.putExtras(b);
	 		
		 		startActivityForResult(intent, 1);   
		    	
		    }
	    };
	    
	    /*
	     * Listener triggered when a list item is pressed during 3 seconds
	     * The method creates a dialog to confirm the choosen project removal
	     * 
	     */
	    
	    
	 /*   public OnItemLongClickListener theListLongListener = new OnItemLongClickListener() {
		    
		    public boolean onItemLongClick(android.widget.AdapterView<?> parent, final View v, int position, long id) {
		        
		    	AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		    	
		    	
		    	builder.setMessage(R.string.deleteRSquestion)
		    	       .setCancelable(false)
		    	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    
		    	      	   	 TextView tv=(TextView)v;
		    	    	   	 String projName=(String)tv.getText();
		    	        	 removeProject(projName);
		    	        
		    	        	 loadProjects();

		    	        	   
		    	           }
		    	       })
		    	       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	                
		    	        	   dialog.cancel();
		    	                
		    	           }
		    	       });
		    	AlertDialog alert = builder.create();
		    	
		    	alert.show();
		    	   
		    	   return true;
		    	
		    }
		    
		    
		    }; */
	    

		 /*
		  * 
		  * Function that creates a string with a concatenated list 
		  * of Attribute Labels contained in the Cursor
		  * 
		  */
		    
	    @SuppressWarnings("unused")
		private String createStringAtrib(ArrayList<ProjectField> atriC) {
			
			   String attList="";
			   
			   Iterator<ProjectField> itr = atriC.iterator();

	
			   
			   while(itr.hasNext()){
				   
				   attList=attList.concat(" | "+itr.next().getLabel());
			
				   
			   }
			   
			   
			   return attList;
			   
		}
	    
	    public boolean isDefaultProject(String projName){
	    	
	    	return projName.equals(defaultProject);
	    	
	    }
	    
	    
	    private class ProjectListAdapter extends ResourceCursorAdapter {

	        public ProjectListAdapter(Context context, Cursor cur) {
	            super(context, R.layout.projectlistrow, cur);

	            
	            
	        }

	        @Override
	        public View newView(Context context, Cursor cur, ViewGroup parent) {
	            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            return li.inflate(R.layout.projectlistrow, parent, false);
	        }

	        @Override
	        public void bindView(View view, Context context, Cursor cur) {
	            
	        	TextView tvProjectText = (TextView)view.findViewById(R.id.tvProjectName);
	            RadioButton cbListCheck = (RadioButton)view.findViewById(R.id.cBedit);

	            String projName=cur.getString(cur.getColumnIndex(ResearchDbAdapter.NAME));
	          
	        	tvProjectText.setText(projName);

	        	boolean isDefault=isDefaultProject(projName);
	        	
	        	if(isDefault) chosenRB=cbListCheck;
	        	
	            cbListCheck.setChecked(isDefault);
	            
	            cbListCheck.setOnClickListener(new OnClickListener() {  
	            	
	            	public void onClick(View v) { 
	            	
	            		RadioButton cBox = (RadioButton) v; 
	            		View e=(View) v.getParent().getParent();
	            		
	            		 TextView tv= (TextView) e.findViewById(R.id.tvProjectName);
		  
	            			ResearchControler rc=new ResearchControler(v.getContext());
	            			rc.loadResearchInfoByName(tv.getText().toString());
	            			
	            		 	
	            	        SharedPreferences.Editor editor = preferences.edit();
	            	        editor.putLong("predProjectId", rc.getIdRs());
	            	        editor.putString("predField", null);
	            	        editor.commit();
	            	        
	            	        defaultProject=tv.getText().toString();
	            	        //desactivar la resta
	            	        if(chosenRB!=null) chosenRB.setChecked(false);
	            	        
	            	        chosenRB=cBox;
	            	} 
	            	
	            
	            }
	            
	            );
	            
	            	tvProjectText.setOnClickListener(new OnClickListener() {  
	            	
	            	public void onClick(View v) { 
	            	
	            	
	            		String projName=((TextView)v).getText().toString();
	            		
	            		ResearchControler rsC=new ResearchControler(v.getContext());
	       		     
	   			     	rsC.loadResearchInfoByName(projName);
	   	 
	   			     	name=rsC.getName();
	   			     	desc=rsC.getThName();

	   			     	Intent intent = new Intent(v.getContext(), ProjectInfo.class);
	   			       
	   			     	Bundle b = new Bundle();
	   			     	b.putLong("Id", rsC.getIdRs());
	   			     	intent.putExtras(b);
	   	 			
	   			     	b.putString("projName", name);
	   			     	intent.putExtras(b);
	   			     	intent.putExtras(b);
	   			     	b = new Bundle();
	   			     	b.putString("projDescription", desc);
	   			     	intent.putExtras(b);
	   	 		
	   	 			
	   			     	startActivityForResult(intent, 1);   
	            		
	            	} 
	            	
	            
	            }
	            
	            );
	            	
	            	
	            	
	            	tvProjectText.setOnLongClickListener(new OnLongClickListener() {
	        		    

						public boolean onLongClick(final View v) {

							
							AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
	        		    	
	        		    	
	        		    	builder.setMessage(R.string.deleteProjQuestion)
	        		    	       .setCancelable(false)
	        		    	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	        		    	           public void onClick(DialogInterface dialog, int id) {
	        		    
	        		    	      	   	 TextView tv=(TextView)v;
	        		    	    	   	 String projName=(String)tv.getText();
	        		    	        	 int status=removeProject(projName);
	        		    	        	 
	        		    	        	 if(status>=0){
	        		    	        		 
	        		    	        		 if(projName!=null) {
	        		    	        		 
		        		    	        		 if(defaultProject.equals(projName)) {
		        		    	        			 
		        		    	        			chosenRB=null;
		        		    	        		
		        		    	        	        SharedPreferences.Editor editor = preferences.edit();
		        			            	        editor.putLong("predProjectId", -1);
		        			            	        editor.putString("predField", null);
		        			            	        editor.commit();
		        		    	        	 
		        		    	        		 }
	        		    	        		 
	        		    	        		 }
	        		    	        
	        		    	        	 	loadProjects();
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
	        		    	   
	        		    	   return true;
						}
	        		    
	        		    
	        		    }
	            	
	            	);
	            

	        }
	    }
	    
}
