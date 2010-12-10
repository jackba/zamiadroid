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
import java.util.Hashtable;
import java.util.Iterator;

import uni.projecte.controler.PreferencesControler;
import uni.projecte.controler.ResearchControler;
import uni.projecte.dataLayer.examples.ExampleProjectCreator;
import uni.projecte.controler.ThesaurusControler;
import uni.projecte.dataTypes.ProjectField;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;



public class ProjectCreator extends Activity{
	
	   private ListView listAttributesPres;
	   private EditText projName;
	   private Spinner spinnerTh;
	   private ArrayList <String> fieldList;
	   private ArrayList <ProjectField> objFieldList;
	   private Hashtable<String,ProjectField> fieldNames;
	   private ArrayAdapter<String> m_adapterForSpinner;
	   private CheckBox checkBox;
	   private ResearchControler rsCont;
	   private ExampleProjectCreator epC;
	   private long projId;
	   private SharedPreferences preferences;

 
	   
	   private ProgressDialog pd;

		
		static final int IMPORT_FAGUS = Menu.FIRST;
		static final int IMPORT_CITACIONS = Menu.FIRST+1;
		static final int CREATE_EXAMPLE=Menu.FIRST+2;


	
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.researchcreation);

	        ThesaurusControler thCont= new ThesaurusControler(this);
	        
	        preferences = getSharedPreferences(Main.PREF_FILE_NAME, MODE_PRIVATE);

	
	       
	        //getting view items     
	        Button bAfegirAtribut = (Button)findViewById(R.id.bAfegirAtribut);
	        bAfegirAtribut.setOnClickListener(addAttributeListener);
	        
	        
	        Button bProjectCreation = (Button)findViewById(R.id.bCrearEstudi);
	        bProjectCreation.setOnClickListener(createRsListener);
	       
	        projName=(EditText)findViewById(R.id.eTextEstudi);
	        listAttributesPres = (ListView)findViewById(R.id.llistaAtributs);
	        spinnerTh = (Spinner) findViewById(R.id.thList);
	        
	        checkBox = (CheckBox) findViewById(R.id.chNoTh);
	        checkBox.setOnClickListener(new View.OnClickListener() 
	        {
	            public void onClick(View v) {
	                if (((CheckBox)v).isChecked()) {
	                	
	                	spinnerTh.setVisibility(Spinner.INVISIBLE);
	                	
	                }	                	
	                else{
	                	
	                	spinnerTh.setVisibility(Spinner.VISIBLE);

	                }
	            }
	        });

	       
	        //data Structures that will store the createdFields
	        
	        fieldList= new ArrayList<String>();
	        objFieldList= new ArrayList<ProjectField>();
	        fieldNames= new Hashtable<String, ProjectField>();
	        

	        //binding Thesaurus List to Spinner view
	        
	    	ArrayAdapter<String> dtAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, thCont.getThList());
        
        
 		   dtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 		   
 		   spinnerTh.setAdapter(dtAdapter);

	        
	   }
	   
	
	   
	   public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    		
	        	
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    	
		    	builder.setMessage(R.string.backFromProjectCreation)
		    	       .setCancelable(false)
		    	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    
		    	        	   finish();
	        	   
		    	           }

						
		    	       })
		    	       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	                
		    	        	   
		    	        	   dialog.dismiss();
	 
		    	           }
		    	       });
		    	
		    	AlertDialog alert = builder.create();
		    	
		    	alert.show();
	        		
		        return true;

	        	
	        }
	        
	        return false;

	    }
	   
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	
	    	menu.add(0, IMPORT_CITACIONS, 0,R.string.mImportProjectFromCitations).setIcon(android.R.drawable.ic_menu_upload);
	    	menu.add(0, IMPORT_FAGUS, 0,R.string.mImportFagus).setIcon(android.R.drawable.ic_menu_upload);
	    	menu.add(0, CREATE_EXAMPLE, 0,R.string.mCreateDefProj).setIcon(android.R.drawable.ic_menu_upload);

	    	
	    	
	    	return super.onCreateOptionsMenu(menu);
	    }


	    
	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {
	    	
	    	
			switch (item.getItemId()) {
			case IMPORT_FAGUS:
				
				
				 AlertDialog.Builder builder = new AlertDialog.Builder(this);
				   builder.setTitle(getString(R.string.chooseImportFormatProj));
				   
				   final String[] formats=getResources().getStringArray(R.array.importProjFormats);
				   
				   builder.setSingleChoiceItems(formats,-1, new DialogInterface.OnClickListener() {
				       public void onClick(DialogInterface dialog, int item) {
				           
				    	dialog.dismiss();
				   		Intent myIntent = new Intent(getBaseContext(), ProjectImport.class);
			            startActivityForResult(myIntent,1);
			            
				    	   
				       }
				   });
				   AlertDialog alert = builder.create();
				   alert.show();
				

	    
				 			 
				break;
			case IMPORT_CITACIONS:
				
				 AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
				   builder2.setTitle(getString(R.string.chooseImportFormatCit));
				   
				   final String[] formats2=getResources().getStringArray(R.array.importCitFormats);
				   
				   builder2.setSingleChoiceItems(formats2,-1, new DialogInterface.OnClickListener() {
				       public void onClick(DialogInterface dialog, int item) {
				           
					    	dialog.dismiss();
				    		Intent intent = new Intent(getBaseContext(), CitationProjectImport.class);
				            startActivityForResult(intent,1);
				    	   
				       }
				   });
				   AlertDialog alert2 = builder2.create();
				   alert2.show();
				
				
	    	
				 			 
				break;	
				
			case CREATE_EXAMPLE:
				
				PreferencesControler pC=new PreferencesControler(getBaseContext());
				
				
				epC=new ExampleProjectCreator(getBaseContext(),pC.getLang());
				createProjectDialog(epC.getProjectName());
			
				
				
			}
			
			return super.onOptionsItemSelected(item);
			
		}
	   
	    
	    private void createProjectDialog(String prName) {
	        
  		        	
        	//Context mContext = getApplicationContext();
    	   final Dialog dialog = new Dialog(this);
    	   	
        	dialog.setContentView(R.layout.projectcreationsimple);
    	   	dialog.setTitle(getString(R.string.insert_data));
    	   	
    	   	Button createProject = (Button)dialog.findViewById(R.id.bAddItemS);
    	   	EditText name=(EditText)dialog.findViewById(R.id.etNameItemS);

    	   	name.setText(prName);

    	    createProject.setOnClickListener(new Button.OnClickListener(){
    	    	             
    	    	
    	    	public void onClick(View v)
    	    	              {

    	    	                 EditText et=(EditText)dialog.findViewById(R.id.etNameItemS);

    	    			    	 String projName=et.getText().toString();
    	    			    	
    	    			    	 
    	    			    	 projId=epC.createBasicProject(projName);
    	    						
    	    					 if(projId>0){
    	    						 
    	    						 Toast.makeText(getBaseContext(), String.format(getString(R.string.projSuccesCreated), epC.getProjectName()),Toast.LENGTH_LONG).show();
    	    						 dialog.dismiss();
    	    						 
    	    						setDefaultProject();
    	    						finish();
    	    						
    	    					 }
    	    					 
    	    					 else{
    	    						 
    	    							String sameProject=getBaseContext().getString(R.string.sameNameProject);
    	    	    	    	      	Toast.makeText(getBaseContext(), 
    	    		    	   		              sameProject+" "+epC.getProjectName().toString(), 
    	    		    	   		              Toast.LENGTH_LONG).show();
    	    						 
    	    					 }
    	    	                
    	    	              }
    	    	             
    	    });
    	    
    	    dialog.show();


    	 
    }
   
	   
	    private OnClickListener addAttributeListener = new OnClickListener()
	    {
	        public void onClick(View v)
	        {                     
	        	
	        	final CharSequence[] items = getBaseContext().getResources().getStringArray(R.array.newFieldTypes);

	        	AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
	        	builder.setTitle(R.string.fieldTypeMessage);
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
	    };
	    
	    
		   private void createPredFieldDialog() {
		        
			   final Dialog dialog;
		        
			   dialog = new Dialog(this);
		    	   	
			   dialog.setContentView(R.layout.predfieldcreation);
			   dialog.setTitle(R.string.projCreationTitle);
		    	   	

			   Button addField = (Button)dialog.findViewById(R.id.bCreateField);

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
			    	    					
				    	        	    	a = new ProjectField("OriginalTaxonName",etDesc.getText().toString(),fieldLabel,"","thesaurus");

			    	    					
			    	    				}
			    	    				else if (selected.equals(predValues[1])){
			    	    					
				    	        	    	a = new ProjectField("photo",etDesc.getText().toString(),fieldLabel,"","photo");

				    	        	    	
			    	    				}
			    	    				else if (selected.equals(predValues[2])){
			    	    					
				    	        	    	a = new ProjectField("CitationNotes",etDesc.getText().toString(),fieldLabel,"","simple");

			    	    				}
			    	    				
			    	    				
			    	    				if(fieldNames.containsKey(a.getName())){
			    	    					
			    	    					String rep=getBaseContext().getString(R.string.predFieldRepeated);
			    	    					
			    	    					 Toast.makeText(getBaseContext(), rep+" "+selected,
					        		   	              Toast.LENGTH_LONG).show();
			    	    					
			    	    					
			    	    				}
			    	    				else{
			    	    					

			    	    					String name=getBaseContext().getResources().getString(R.string.fieldName);
				    	        	    	String label=getBaseContext().getResources().getString(R.string.fieldLabel);
				    	        	    	
				    	        	    	objFieldList.add(a);
				    	        	    	fieldNames.put(a.getName(), a);
				    	        	    	fieldList.add(name+": "+a.getName()+" "+label+": "+fieldLabel);  	
				    	        	    	listAttributesPres.setAdapter(new ArrayAdapter<String>(v.getContext(), R.layout.atrib_row, fieldList));
			    	    				
				    	        	    	dialog.dismiss();
			    	    					
			    	    				}
			    	    					
			    	    			}
	 
			    	    	     }
			    	    	             
			    	    });
		    	    
		    	    dialog.show();


		    	 
		    }
	 
	    
	    /*
	     * Method called when addField button is clicked.
	     * It creates a dialog with a form for the creation of new Fields 
	     * 
	     * It contains three clicks listener which handler the addition 
	     * of new predefined values and the field creation.
	     * 
	     */

		   private void createFieldCreatorDialog() {
		        
			  	final Dialog dialog;
		        

		    	  dialog = new Dialog(this);
		    	   	
		    	  dialog.setContentView(R.layout.fieldcreation);
		    	  dialog.setTitle(R.string.projCreationTitle);
		    	   	
		    	  Button addPredField = (Button)dialog.findViewById(R.id.bAddPredField);
		    	  Button remPredField = (Button)dialog.findViewById(R.id.bRemovePredField);
		    	  Button addField = (Button)dialog.findViewById(R.id.bCreateField);

	
		    	  Spinner predList=(Spinner)dialog.findViewById(R.id.sPrefFields);
		    	  
		    	  m_adapterForSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		    	  m_adapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    	  predList.setAdapter(m_adapterForSpinner);
		    	  
		    	  
		    	  remPredField.setOnClickListener(new Button.OnClickListener(){
	    	             
		    	    	
		    	    	public void onClick(View v)
		    	    		{

		    	    				Spinner predList=(Spinner)dialog.findViewById(R.id.sPrefFields);

		    	    				String element= predList.getSelectedItem().toString();
		    	    				m_adapterForSpinner.remove(element);
		    	    	            	 
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
			    	    			else{
			    	    					
			    	    					if(fieldLabel.length()==0){
				    	    					
				    	    					etLabel.setText(fieldName);
						    	    			fieldLabel=etLabel.getText().toString();

				    	    					
				    	    				}
			    	    				
			    	    				
			    	    				
				    	    				if(fieldNames.containsKey(fieldName)){
				    	    					
				    	    					 Toast.makeText(getBaseContext(), R.string.repeatedFieldName, 
						        		   	              Toast.LENGTH_LONG).show();
				    	    					
				    	    				}
				    	    				else {
				    	    				
					    	        	    	ProjectField a = new ProjectField(fieldName,etDesc.getText().toString(),fieldLabel,predValue);
					    	        	    	insertSpinnerItems(predList, a);
		
					    	    				String name=getBaseContext().getResources().getString(R.string.fieldName);
					    	        	    	String label=getBaseContext().getResources().getString(R.string.fieldLabel);
					    	        	    	
					    	        	    	objFieldList.add(a);
					    	        	    	fieldNames.put(fieldName,a);
					    	        	    	fieldList.add(name+": "+a.getName()+" "+label+": "+fieldLabel);  	
					    	   
					    	        	    	      	    	
					    	        	    	listAttributesPres.setAdapter(new ArrayAdapter<String>(v.getContext(), R.layout.atrib_row, fieldList));
		
					    	        	    				    	    				
					    	        	    	dialog.dismiss();
				    	        	    	
				    	    				}
			    	    				
			    	    			}

			    	    	            	 
			    	    	     }
			    	    	             
			    	    });
		    	    
		    	    dialog.show();


		    	 
		    }
		   
		/*
		 * This method takes the items from the Spinner View 
		 * and adds them into the Attribute class
		 *    
		 */
		   
		private void insertSpinnerItems(Spinner s, ProjectField a){

			int n=s.getCount();
			
			for(int i=0;i<n;i++){
				
				a.insertPredValue(s.getItemAtPosition(i).toString());
				
			}
			
		}
		
		
		protected boolean fieldNameExists(String fieldName,
				ArrayList<ProjectField> objFieldList) {
			
			return false;
		}
		
		/*
		 * This Method gets the project name and the created Attributes or Fields and creates a new Project in the system
		 * 
		 * It also takes into account the chosen Thesaurus
		 * 
		 */
	    
	    private OnClickListener createRsListener = new OnClickListener()
	    {
	        public void onClick(View v)
	        {                      
	        	
	  
	        	
	        	    if (!projName.getText().equals("")){
	        	
	        	    	if (fieldList.isEmpty()){
	        	    		
	        	    		 Toast.makeText(getBaseContext(), R.string.projFieldsMissing, 
	        		   	              Toast.LENGTH_LONG).show();
	        	    		
	        	    	}   
	        	    	
	        	    	else{
	        	    		
	        	    		rsCont=new ResearchControler(v.getContext());
	        	    		

	        	    		if(checkBox.isShown()){
	        	    			
	        	    			Spinner thList=(Spinner)findViewById(R.id.thList);
	        	    			
	        	    			if(thList.getCount()==0){
	        	    				
	        	    				projId=rsCont.createProject(projName.getText().toString(), "");

	        	    				
	        	    			}
	        	    			else{
	        	    				projId=rsCont.createProject(projName.getText().toString(), thList.getSelectedItem().toString());
	        	    			}

	        	    		}
	        	    		else{
	        	    			
		        	    		projId=rsCont.createProject(projName.getText().toString(), "");

	        	    			
	        	    		}
	        	    		
    	    				
	        	    		if (projId>0){
	        	    			
	        	    		
	        	    			createProjectThreadCreator(projId);
	        	    		
	        	    		}
	        	    		
	        	    		else{
	        	    			
	    	                	String sameProject=getBaseContext().getString(R.string.sameNameProject);

	        	    	      	Toast.makeText(getBaseContext(), 
	    	    	   		              sameProject+" "+projName.getText().toString(), 
	    	    	   		              Toast.LENGTH_LONG).show();
	        	    		}
	        	    		
	        	    	}
	        	    	
	        	    }
	        	    
	        	    else {
	        	    	
	        	    Toast.makeText(getBaseContext(), R.string.projNameMissing, 
	   	              Toast.LENGTH_LONG).show();
	        	    	
	        	    }

	          
	        }
	    };
	    
	    
	    private void createProjectThreadCreator(final long projId) {
			 
			 pd = ProgressDialog.show(this, getBaseContext().getString(R.string.projCreationLoading), getBaseContext().getString(R.string.projCreationTxt), true,false);

			                 Thread thread = new Thread(){
			  	        	   
				                 public void run() {
				               	  
				                	 createProjectThread(projId);
				               	  
				                 }
				           };
				           
				           
			   thread.start();
			}	
		 
		 
		   private void createProjectThread(long projId){
			   
			   Iterator<ProjectField> itr = objFieldList.iterator();
	    		rsCont.startTransaction();
	    		
	    		while(itr.hasNext()){
	    		
	    			ProjectField at=(ProjectField) itr.next();
	    			
	    			ArrayList <String> predValues=at.getPredValuesList();
	    			
	    			Iterator<String> itrPreValues = predValues.iterator();
	    			
	    			int numPred=predValues.size();
	    			
	    			long attId;
	    			
	    			if(numPred<1){
	    				
	    				
	    				if(at.getType()==null){
	    					
	    					attId=rsCont.addProjectField(projId, at.getName(),at.getLabel(), at.getType(),at.getValue(),"simple","ECO");
	    					
	    				}
	    				else if(at.getType().equals("thesaurus")){
	    					
	    					attId=rsCont.addProjectField(projId, at.getName(),at.getLabel(), at.getDesc(),at.getValue(),"thesaurus","A");

	    				}
	    				else if(at.getType().equals("CitationNotes")){
	    					
	    					attId=rsCont.addProjectField(projId, at.getName(),at.getLabel(), at.getDesc(),at.getValue(),"simple","A");

	    				}
	    				else if (at.getType().equals("photo")){
	    					
	    					attId=rsCont.addProjectField(projId, at.getName(),at.getLabel(), at.getDesc(),at.getValue(),"photo","ECO");

	    					
	    				}
	    				else{
	    					attId=rsCont.addProjectField(projId, at.getName(),at.getLabel(), at.getType(),at.getValue(),"simple","ECO");
	    				}
	    			}
	  
	    			else{
	    				
   	    			attId=rsCont.addProjectField(projId, at.getName(),at.getLabel(), at.getDesc(),at.getValue(),"complex","ECO");

	    				
	    			}


   	    		while(itrPreValues.hasNext()){
       	    		
   	    			rsCont.addFieldItem(attId, (String)itrPreValues.next());
   	    			
   	    		}

	    	
	    		}

	    		rsCont.endTransaction();
	    		
	    		 
	
    	 	
			   handlerCreate.sendEmptyMessage(0);
    			

		   }
		   
			 private Handler handlerCreate = new Handler() {

					public void handleMessage(Message msg) {	
			
						pd.dismiss();
						
						Toast.makeText(getBaseContext(), String.format(getString(R.string.projSuccesCreated), projName.getText().toString()), 
				   	              Toast.LENGTH_LONG).show();
			    		
						   
						setDefaultProject();
	            	    
	            	    finish();
	
				

					}
				};

	   private void setDefaultProject(){
		   
			SharedPreferences.Editor editor = preferences.edit();
    	    editor.putLong("predProjectId", projId);
    	    editor.putString("predField", null);
    	    editor.commit();
		   
	   }
	
	   
	   protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	       // super.onActivityResult(requestCode, resultCode, intent);
	   	
	       	
	        switch(requestCode) {
	        case 0 :
	            
	        	//back from research choice
	        	
	        /*	nomEstudi = extras.getString(KEY_NOM);
	            idRs=extras.getLong(KEY_ID);
	            desc=extras.getString(DESCRIPCIO);

	            txtName.setText(nomEstudi);*/
	            
	            break;
	            
	            
	        case 1 :
	        	
	        	if(intent!=null){
	        		
	        	 	 Bundle ext = intent.getExtras();
		        	 projId= ext.getLong("projId");
		        	 setDefaultProject();
	        		
		        	 finish();
		        	 
	        	}
	       
	        	
	           
	            break;
	            
	            default:
	            	
	            	
	  
	        }
	        

	    }
}
