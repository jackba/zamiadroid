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
import uni.projecte.controler.ThesaurusControler;
import uni.projecte.dataLayer.bd.AttributeDbAdapter;
import uni.projecte.dataTypes.ProjectField;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class ProjectInfo extends Activity{
	
	   private ListView llista;

	   /* project*/
	   private long projId;
	   private String projectName;
	   private long fieldId;
	   
	   
	   private ThesaurusControler tc;
	   private TextView thName;
	   private ResearchControler rsC;
	   private SampleControler sC;
	   
	   //private static final int DIALOG_RS_FIELDS=1;
	   private static final int ADD_FIELD=Menu.FIRST;
	   private static final int CHANGE_TH=Menu.FIRST+1;

		String attributes;
		
		private ArrayAdapter<String> m_adapterForSpinner;
		

	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.projectinfo);
	        
	        
        	tc= new ThesaurusControler(this);
	        
	        TextView tip= (TextView)findViewById(R.id.tvRschName);
	        
	        projectName=getIntent().getExtras().getString("projName");
	       
	        
	        String projNameText=getApplicationContext().getString(R.string.tvProjectName);

	        tip.setText(Html.fromHtml("<b>"+projNameText+"</b> "+projectName));
	        thName= (TextView)findViewById(R.id.tvProjTh);
	        
	        
	        String defTh=getApplicationContext().getString(R.string.tvDefaultTh);
	        thName.setText(Html.fromHtml("<b>"+defTh+"</b> "+getIntent().getExtras().getString("projDescription"))); 
	        
	        projId= getIntent().getExtras().getLong("Id"); 
	        
	       /* Button btAddField = (Button)findViewById(R.id.bAddNewField);
	        btAddField.setOnClickListener(bAddNewFieldListener);*/
	        
	        
    
	        llista = (ListView)findViewById(R.id.lFields);
	        
	        llista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        llista.setItemsCanFocus(true);

	        
	    //    llista.setOnItemClickListener(theListListener);


	        rsC=new ResearchControler(this);
	        fillFieldList();
	        
	          

	        //listener for item cliked more than 3 seconds
	       llista.setOnItemLongClickListener(theListLongListener);

	        
	        
	        
	   }
	   
	   public void fillFieldList(){
		   
			
		   Cursor cFields=rsC.getProjectFieldsCursor(projId);
	       startManagingCursor(cFields);
		   
	        // Now create an array adapter and set it to display using our row
	       ProjectFieldAdapter fieldsAdapter = new ProjectFieldAdapter(this, cFields);
	        
	        llista.setAdapter(fieldsAdapter);
		   
	   }
	   
	   public OnItemLongClickListener theListLongListener = new OnItemLongClickListener() {
		    
		    public boolean onItemLongClick(android.widget.AdapterView<?> parent, final View v, int position, long id) {
		        
		    	AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		    	
		    	
		    	builder.setMessage(R.string.deleteProjQuestion)
		    	       .setCancelable(false)
		    	       .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    
		    	        	// removeResearch(rsName);
		    	        
		    	        	 //loadResearches();

		    	        	   
		    	           }
		    	       })
		    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	                
		    	        	   dialog.cancel();
		    	                
		    	           }
		    	       });
		    	AlertDialog alert = builder.create();
		    	
		    	alert.show();
		    	   
		    	   return true;
		    	
		    }
		    
		    
		    };
	    
	   
	   
	   private class ProjectFieldAdapter extends ResourceCursorAdapter {

	        public ProjectFieldAdapter(Context context, Cursor cur) {
	            super(context, R.layout.projectfileldsrow, cur);

	            
	            
	        }

	        @Override
	        public View newView(Context context, Cursor cur, ViewGroup parent) {
	            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            return li.inflate(R.layout.projectfileldsrow, parent, false);
	        }

	        @Override
	        public void bindView(View view, Context context, Cursor cur) {
	            
	        	TextView tvListText = (TextView)view.findViewById(R.id.tvFieldName);
	            TextView tvListLabel = (TextView)view.findViewById(R.id.tvFieldLabel);
	            CheckBox cbListCheck = (CheckBox)view.findViewById(R.id.cBedit);
	            ImageButton imgButton = (ImageButton)view.findViewById(R.id.removeImgButton);
	            imgButton.setBackgroundResource(android.R.drawable.ic_menu_delete);

	            tvListText.setText(cur.getString(cur.getColumnIndex(AttributeDbAdapter.NAME)));
	            tvListLabel.setText(cur.getString(cur.getColumnIndex(AttributeDbAdapter.LABEL)));
	            cbListCheck.setChecked((cur.getInt(cur.getColumnIndex(AttributeDbAdapter.VISIBLE))==0? false:true));
	            
	            cbListCheck.setOnClickListener(new OnClickListener() {  
	            	
	            	public void onClick(View v) { 
	            	
	            		CheckBox cBox = (CheckBox) v; 
	            		View e=(View) v.getParent().getParent();
	            		
	            		 TextView tv= (TextView) e.findViewById(R.id.tvFieldName);
	            		
	            		 
	    	        	   String attName=tv.getText().toString();
	    	        	
	            		
	            		if (cBox.isChecked()) 
	            		{ 
	            			rsC.changeFieldVisibility(projId,attName,true);
	            		
	            		} 
	            		
	            		else if (!cBox.isChecked()) { 
	            			
	            			rsC.changeFieldVisibility(projId,attName,false);

	            		
	            		} } }
	            
	            );
	            
	            
	            imgButton.setOnClickListener(new OnClickListener() {  
	            	
	            	public void onClick(View v) { 
	            		
	            		View e=(View) v.getParent().getParent();
	            		
	            		 TextView tv= (TextView) e.findViewById(R.id.tvFieldName);
	            		
	            		 
	    	        	   String attName=tv.getText().toString();
	            		
	    	      	   	removeField(attName);

	            	
	             } }
	            
	            );


	        }
	    }

	   

		public boolean onCreateOptionsMenu(Menu menu) {
	    	

	    	menu.add(0, ADD_FIELD, 0,R.string.mAddField).setIcon(android.R.drawable.ic_menu_add);
	    	menu.add(0, CHANGE_TH, 0,R.string.mChangeTh).setIcon(android.R.drawable.ic_menu_agenda);

	    	
	    	return super.onCreateOptionsMenu(menu);
	    }

		
		public void createFieldDialogType(){
			
			final CharSequence[] items = getBaseContext().getResources().getStringArray(R.array.newFieldTypes);

        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("Escull un tipus");
        	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	    	
        	    	dialog.dismiss();
        	    	
        	        if(items[item].equals(items[0])){
        	        	
        	        	createFieldCreatorDialog();
        	        }
        	        else{
        	        	
        	        	createPredFieldDialog();

        	        	
        	        }
        	    }
        	});
        	AlertDialog alert = builder.create();
        	alert.show();
			
			
		}

	    
	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {
	    	
	    	
			switch (item.getItemId()) {
			case ADD_FIELD:
				
				createFieldDialogType();
				 			 
				break;
				
			case CHANGE_TH:
				
				changeTh();
				 			 
				break;
				
			}
			
		
			return super.onOptionsItemSelected(item);
		}
	
	    
	    
	   private void removeField(String fieldName){
		   
		   ResearchControler rC= new ResearchControler(this);
		   
		   fieldId =rC.getFieldIdByName(projId, fieldName);
		   
		   sC= new SampleControler(this);
		   
		   
		   int numCitations= sC.getCitationsWithField(fieldId);
		 
			   
		   AlertDialog.Builder builder = new AlertDialog.Builder(this);
		   
		   String removeQuestion="";
		    	
		   if(numCitations>0) { 	
			   
			   removeQuestion= String.format(getString(R.string.removeFieldQuestion), numCitations, fieldName);
			   	
		   }
		   
		   else{
			   
			   	removeQuestion= String.format(getString(R.string.removeFieldQuestionNoCit), fieldName);

			   
		   }
		    	
		    	builder.setMessage(removeQuestion)
		    	       .setCancelable(false)
		    	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    
		    	        	   sC.deleteCitationField(fieldId);
		    	        	   fillFieldList();
		    	        	   
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
	    
	 
	   private void changeTh(){
		   
		   final String [] thList=tc.getThList();

       	
       	if(thList.length==0){
       		

   			Toast.makeText(getBaseContext(), 
	                    R.string.emptyThList, 
	                    Toast.LENGTH_SHORT).show();
   		   
       		
       	}
       	else{
       	
	        	AlertDialog.Builder builder;
	        	
	        	builder= new AlertDialog.Builder(this);
	        	
	        	
	        	builder.setTitle(R.string.thChooseIntro);
	        	
	        	
	        	builder.setSingleChoiceItems(thList, -1, new DialogInterface.OnClickListener() {
	        	    
	        		
	        		public void onClick(DialogInterface dialog, int item) {
	        	        
	        			String thChanged=getApplicationContext().getString(R.string.thChangedText);
	        			
	        			Toast.makeText(getApplicationContext(),thChanged+" "+thList[item], Toast.LENGTH_SHORT).show();
	        	        
	        	        tc.changeProjectTh(projId,thList[item]);
	        	        
	        	        String defTh=getApplicationContext().getString(R.string.tvDefaultTh);
	        	        thName.setText(Html.fromHtml("<b>"+defTh+"</b>"+thList[item])); 

	        	        
	        	        dialog.dismiss();
	        	     
	        	        
	        	        
	        	        
	        	    }
	        	});
	        	AlertDialog alert = builder.create();
	        	
	        	alert.show();
		   
		   
	   } 
   
	   }
	   
	   private void createPredFieldDialog() {
	        
		  	final Dialog dialog;
	        
	  		        	
	        	//Context mContext = getApplicationContext();
	    	  dialog = new Dialog(this);
	    	   	
	    	  dialog.setContentView(R.layout.predfieldcreation);
	    	  dialog.setTitle(R.string.projCreationTitle);
	    	   	

	    	  Button addField = (Button)dialog.findViewById(R.id.bCreateField);


	    	   //	EditText name=(EditText)dialog.findViewById(R.id.etNameItem);

	    	  Spinner predList=(Spinner)dialog.findViewById(R.id.sPrefFields);
	    	  
			  final String[] predValues=getResources().getStringArray(R.array.predValues);

	    	  m_adapterForSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,predValues);
	    	  m_adapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	  predList.setAdapter(m_adapterForSpinner);
	    	  
	
	    	    
	    	    //listener for the creation of a new field. Data is collected and checked from EditText fields.
	    	    //if all necessary data is correct the dialog is destroyed
	    	    
	    	 	  addField.setOnClickListener(new Button.OnClickListener(){
	    	             
		    	    	
		    	    	public void onClick(View v)
		    	    		{
	    	                 			
		    	    			EditText etLabel=(EditText)dialog.findViewById(R.id.etFieldLabel);
		    	    			EditText etDesc=(EditText)dialog.findViewById(R.id.etFieldDesc);
		    	    			
	    	    				Spinner predList=(Spinner)dialog.findViewById(R.id.sPrefFields);


		    	    			String fieldLabel=etLabel.getText().toString();


		    	    			if(fieldLabel.length()==0){
		    	    				
		    	    				 Toast.makeText(getBaseContext(), R.string.tFieldValuesMissing, 
			        		   	              Toast.LENGTH_LONG).show();
		    	    				
		    	    				
		    	    			} 	
		    	    			else{
		    	    				
		    	    				String selected=predList.getSelectedItem().toString();
		    	    				ProjectField a=null;
		    	    				
		    	    				if(selected.equals(predValues[0])){
		    	    					
			    	        	    	a = new ProjectField("OriginalTaxonNames",etDesc.getText().toString(),fieldLabel,"","thesaurus");

		    	    					
		    	    				}
		    	    				else if (selected.equals(predValues[1])){
		    	    					
			    	        	    	a = new ProjectField("photo",etDesc.getText().toString(),fieldLabel,"","photo");

		    	    					
		    	    				}
		    	    				else if (selected.equals(predValues[2])){
		    	    					
			    	        	    	a = new ProjectField("CitationNotes",etDesc.getText().toString(),fieldLabel,"","simple");

		    	    					
		    	    				}

		    	    				createField(a);
		    	        	    	
		    	        	    	dialog.dismiss();
		    	    				
		    	    				
		    	    			}

		    	    	            	 
		    	    	     }
		    	    	             
		    	    });
	    	    
	    	    dialog.show();


	    	 
	    }

	    
	    private void createFieldCreatorDialog() {
	        
		  	final Dialog dialog;
	        
	  		        	
	        	//Context mContext = getApplicationContext();
	    	  dialog = new Dialog(this);
	    	   	
	    	  dialog.setContentView(R.layout.fieldcreation);
	    	  dialog.setTitle("Introdueixi les dades");
	    	   	
	    	  Button addPredField = (Button)dialog.findViewById(R.id.bAddPredField);
	    	  Button remPredField = (Button)dialog.findViewById(R.id.bRemovePredField);
	    	  Button addField = (Button)dialog.findViewById(R.id.bCreateField);


	    	   //	EditText name=(EditText)dialog.findViewById(R.id.etNameItem);

	    	  Spinner predList=(Spinner)dialog.findViewById(R.id.sPrefFields);
	    	  
	    	  m_adapterForSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	    	  m_adapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	  predList.setAdapter(m_adapterForSpinner);
	    	  
	    	   //	Spinner thList=(Spinner)dialog.findViewById(R.id.thList);
	    	   	
	    	    
	    	   	
	    	   	//name.setText(prName);
	    	  
	    	  remPredField.setOnClickListener(new Button.OnClickListener(){
    	             
	    	    	
	    	    	public void onClick(View v)
	    	    		{

	    	    				Spinner predList=(Spinner)dialog.findViewById(R.id.sPrefFields);

	    	    				String element= predList.getSelectedItem().toString();
	    	    				m_adapterForSpinner.remove(element);
	    	    				
	    	    				//predList.removeViewAt(0);	    	                	 

	    	    	            	 
	    	    	     }
	    	    	             
	    	    }); 
	    	  

	    	    addPredField.setOnClickListener(new Button.OnClickListener(){
	    	    	             
	    	    	
	    	    	public void onClick(View v){

	    	    	                 EditText et=(EditText)dialog.findViewById(R.id.etPredValue);
	    	    	                 
	    	    	                 String text=et.getText().toString();

	    	    	                 if(text.compareTo("")==0){
	    	    	                	 
	    	    	                	 
	    	    	                	 
	    	    	                	 
	    	    	                 }
	    	    	                 
	    	    	                 else{
	    	    	                	 
	    	    	                	 m_adapterForSpinner.insert(text, 0);
	 		    	    	            
		    	    			    	 et.setText("");
	    	    	                	 
	    	    	                 }
	    	    	                 
	    	    			    	
	    	    	                 
	    	    	               //  dialog.dismiss();
	    	    	            	 
	    	    	              }
	    	    	             
	    	    }); 
	    	    
	    	    //listener for the creation of a new field. Data is collected and checked from EditText fields.
	    	    //if all necessary data is correct the dialog is destroyed
	    	    
	    	 	  addField.setOnClickListener(new Button.OnClickListener(){
	    	             
		    	    	
		    	    	public void onClick(View v)
		    	    		{
	    	                 			
		    	    			EditText etName=(EditText)dialog.findViewById(R.id.etFieldName);
		    	    			EditText etLabel=(EditText)dialog.findViewById(R.id.etFieldLabel);
		    	    			EditText etPredValue=(EditText)dialog.findViewById(R.id.etFieldPredValue);
		    	    			EditText etDesc=(EditText)dialog.findViewById(R.id.etFieldDesc);
		    	    			
	    	    				Spinner predList=(Spinner)dialog.findViewById(R.id.sPrefFields);


		    	    			String fieldName=etName.getText().toString();
		    	    			String fieldLabel=etLabel.getText().toString();
		    	    			String predValue=etPredValue.getText().toString();



		    	    			if(fieldName.length()==0){
		    	    				
		    	    				 Toast.makeText(getBaseContext(), R.string.tFieldValuesMissing, 
			        		   	              Toast.LENGTH_LONG).show();
		    	    				
		    	    				
		    	    			} 	
		    	    			else {
		    	    				
		    	    					
		    	    				if(fieldLabel.length()==0){
			    	    					
			    	    				etLabel.setText(fieldName);
					    	    		fieldLabel=etLabel.getText().toString();

			    	    			}
		    	    				
		    	    				ProjectField a = new ProjectField(fieldName,etDesc.getText().toString(),fieldLabel,predValue);
		    	        	    	
		    	        	    	insertSpinnerItems(predList, a);
		    	    				
		    	        	    	createField(a);
		    	        	    	
		    	        	    	dialog.dismiss();
		    	        	 

		    	    				
		    	    			}

		    	    	            	 
		    	    	     }
		    	    	             
		    	    });
	    	    
	    	    dialog.show();


	    	 
	    }
	    
		private void insertSpinnerItems(Spinner s, ProjectField a){

			int n=s.getCount();
			
			for(int i=0;i<n;i++){
				
				a.insertPredValue(s.getItemAtPosition(i).toString());
				
			}
			
			
		}
	    
	    private void createField(ProjectField at){
	    	
	       	rsC.startTransaction();
	       	
			ArrayList <String> predValues=at.getPredValuesList();
			
			Iterator<String> itrPreValues = predValues.iterator();
			
			int numPred=predValues.size();
			
			long attId;
			
			if(numPred<1){
				
				
				if(at.getType()==null){
					
					attId=rsC.addProjectField(projId, at.getName(),at.getLabel(), at.getType(),at.getValue(),"simple","ECO");
					
				}
				else if(at.getType().equals("thesaurus")){
					
					attId=rsC.addProjectField(projId, at.getName(),at.getLabel(), at.getDesc(),at.getValue(),"thesaurus","A");

				}
				else if(at.getType().equals("CitationNotes")){
					
					attId=rsC.addProjectField(projId, at.getName(),at.getLabel(), at.getDesc(),at.getValue(),"simple","A");

				}
				else if (at.getType().equals("photo")){
					
					attId=rsC.addProjectField(projId, at.getName(),at.getLabel(), at.getDesc(),at.getValue(),"photo","ECO");

					
				}
				else{
					attId=rsC.addProjectField(projId, at.getName(),at.getLabel(), at.getType(),at.getValue(),"simple","ECO");
				}
			}
			else{
				
    			attId=rsC.addProjectField(projId, at.getName(),at.getLabel(), at.getType(),at.getValue(),"complex","ECO");

				
			}


    		while(itrPreValues.hasNext()){
	    		
    			rsC.addFieldItem(attId, (String)itrPreValues.next());
    			
    		}
    		
    		 Toast.makeText(getBaseContext(),getString(R.string.newCreatedField)+at.getLabel(), 
	   	              Toast.LENGTH_LONG).show();
    		 
 	    	rsC.endTransaction();

    		 fillFieldList();
	    	
	    }
	    
	  	       
	   
}