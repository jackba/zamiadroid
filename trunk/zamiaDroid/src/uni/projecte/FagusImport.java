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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import uni.projecte.controler.PreferencesControler;
import uni.projecte.controler.ProjectControler;
import uni.projecte.controler.ResearchControler;
import uni.projecte.controler.ThesaurusControler;
import uni.projecte.dataLayer.dataConverter.FagusReader;
import uni.projecte.dataLayer.xml.FagusXMLparser;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;




public class FagusImport extends Activity{
	
	   private AutoCompleteTextView txtName;
	   private  ResearchControler rsCont;
	   private List<String> elements = null;
	   private ListView fileList;
	   private String url;
	   private Button createProject;
	   private FagusReader fR;
	   
	   private long projectId;
	   private PreferencesControler pC;
	   private ProgressDialog pd;

	   
	
	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.project_import);
	        
	        
	        pC=new PreferencesControler(this);

	 	   
	        projectId=getIntent().getExtras().getLong("Id");
	        


	        
	        fileList = (ListView)findViewById(R.id.projectList);
	        if(isSdPresent()) fillFileList(new File("/sdcard/"+pC.getDefaultPath()+"/Citations/").listFiles(new XMLFilter()));
	        else {
	        	
	        	Toast.makeText(getBaseContext(), 
	                    R.string.noSdAlert, 
	                    Toast.LENGTH_SHORT).show();
	        	
	        	
	        }
	        fileList.setOnItemClickListener(theListListener);
	        
	        
	  }
	  
	  protected void onRestart(){
		   
		   if(isSdPresent()) fillFileList(new File("/sdcard/"+pC.getDefaultPath()+"/Citations/").listFiles(new XMLFilter()));
		   else {
	        	
	        	Toast.makeText(getBaseContext(), 
	                    R.string.noSdAlert, 
	                    Toast.LENGTH_SHORT).show();
	        	
	        }
		   
	   }
	   
	 
	  public static boolean isSdPresent() {

	      return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	
	  }
	  
	  private void fillFileList(File[] listFiles) {
		
		        elements = new ArrayList<String>();
		        elements.add(getString(R.string.root));
		        
		        for( File archivo: listFiles)
		            elements.add(archivo.getPath());
		       
		        ArrayAdapter<String> listaArchivos= new ArrayAdapter<String>(this, R.layout.row, elements);
		        fileList.setAdapter(listaArchivos);
		    
		
	}
	   private void rellenarConElRaiz() {
	        fillFileList(new File("/").listFiles());
	    } 
	   
	
	  
	  public OnItemClickListener theListListener = new OnItemClickListener() {
		    
		    public void onItemClick(android.widget.AdapterView<?> parent, View v, int position, long id) {
		    		
		    	  int IDFilaSeleccionada = position;
		          if (IDFilaSeleccionada==0){
		              rellenarConElRaiz();
		          } else {
		              File archivo = new File(elements.get(IDFilaSeleccionada));
		              if (archivo.isDirectory()){
		            	  fillFileList(archivo.listFiles(new XMLFilter()));
		            	  
		              }
		               else{
		            
		            	   
		            	   importFagus(archivo.getPath());
		            	   
		    	
		    }
		    
		    
		    }
		    }   
		
		    };
		    
		    
	 
		    class XMLFilter implements FilenameFilter {
		    	  
		    	  
		              public boolean accept(File dir, String name) {
	
		                return (name.endsWith(".xml"));
	
		        }
	              
		    }
		    
		    private long importProject(String name, String desc, String url){
		    	
		    	  ProjectControler pC= new ProjectControler(this);
		    	  
		    	  return pC.importProject(name,desc,url);
		    	  
		    }
		    
		    private void importFagusThread(String url){
		    	
         	   Log.d("Citations","FagusImport.Start");

         	   fR=new FagusReader(this,projectId);

         	   
	           	 FagusXMLparser fXML=new FagusXMLparser(fR);
	       	 	 fXML.readXML(this, url, false);
	       	 	 
	       	 	 if(fXML.isError()) handler.sendEmptyMessage(-1);
	       	 	 else handler.sendEmptyMessage(0);
	       	 	 
          	   Log.d("Citations","FagusImport.End");


    		 
			 }
			 
			 private Handler handler = new Handler() {

					public void handleMessage(Message msg) {
						
						pd.dismiss();
						
						if(msg.what==0){
							
				       	 	String numCit= getString(R.string.numCitationsImported);
							Toast.makeText(getBaseContext(), 
			   	                    fR.getNumSamples()+" "+numCit, 
			   	                    Toast.LENGTH_SHORT).show();	
							finish();
							
						}
						else{
							
							Toast.makeText(getBaseContext(), 
			   	                    "Error a l'arxiu de citacions", 
			   	                    Toast.LENGTH_SHORT).show();	
							
							
						}
					
				        


					}
				};
		    
			 private void importFagus(final String url) {
				 
				 pd = ProgressDialog.show(this, getString(R.string.citationLoading), getString(R.string.citationLoadingTxt), true,false);
				 
				                 Thread thread = new Thread(){
				  	        	   
					                 public void run() {
					               	  
					                	 importFagusThread(url);
					               	  
					                 }
					           };
					           
					           
				   thread.start();
				 
				}
		    
		    
		   @SuppressWarnings("unused")
		private void createProjectDialog(String prName) {
		        
			  	final Dialog dialog;
		        
			  	
			  		ThesaurusControler thCont= new ThesaurusControler(this);
		  		        	
		        	//Context mContext = getApplicationContext();
		    	   	dialog = new Dialog(this);
		    	   	
		        	dialog.setContentView(R.layout.projectcreation);
		    	   	dialog.setTitle(getString(R.string.insert_data));
		    	   	
		    	   	createProject = (Button)dialog.findViewById(R.id.bAddItem);
		    	   	EditText name=(EditText)dialog.findViewById(R.id.etNameItem);
	
		    	   	
		    	   	Spinner thList=(Spinner)dialog.findViewById(R.id.thList);
		    	   	
		    	    
		    	   	ArrayAdapter<String> dtAdapter = new ArrayAdapter<String>(this,
		                    android.R.layout.simple_spinner_item, thCont.getThList());
		        
		        
		 		   dtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 		   thList.setAdapter(dtAdapter);
		    	 

		    	   	name.setText(prName);

		    	    createProject.setOnClickListener(new Button.OnClickListener(){
		    	    	             
		    	    	
		    	    	public void onClick(View v)
		    	    	              {

		    	    	                 EditText et=(EditText)dialog.findViewById(R.id.etNameItem);
		    	    			    	 Spinner thList=(Spinner)dialog.findViewById(R.id.thList);

		    	    			    	 String projName=et.getText().toString();
		    	    			    	 String thName=(String)thList.getSelectedItem();
		    	    	            
		    	    	                 long projId=importProject(projName,thName,url);
		    	    	                 
		    	    	                 if(projId<0) {
			    	    	   	             
			    	    	                	String sameProject=getBaseContext().getString(R.string.sameNameProject);
			    	    	                	Toast.makeText(getBaseContext(), 
			    	    	   		              sameProject+" "+projName, 
			    	    	   		              Toast.LENGTH_LONG).show();
			    	    	                	
			    	    	            	 
			    	    	              }
			    	    	                else{
				    	    	                 
			    	    	                	dialog.dismiss();
			    	    	                }
			    	    	                	    	    	            	 
		    	    	              }
		    	    	             
		    	    });
		    	    
		    	    dialog.show();


		    	 
		    }

	@SuppressWarnings("unused")
	private OnClickListener bAddSampleAttributesListener = new OnClickListener()
	    {
	        public void onClick(View v)
	        {                        
	        	if (txtName.length()!=0){
	        		
	        		String name=txtName.getText().toString();

	        		rsCont.loadResearchInfoByName(name);
	        		
	        		String rsDescription=rsCont.getThName();
	        		long idRs=rsCont.getIdRs();
	        		
	        		Intent myIntent = new Intent(v.getContext(), Sampling.class);
		        	myIntent.putExtra("rsName", name);
		        	myIntent.putExtra("rsDescription", rsDescription);
		        	myIntent.putExtra("id", idRs);
		
		            startActivityForResult(myIntent, 1);
	            
	      	}
	        	
	        	else{

	             Toast.makeText(getBaseContext(), 
	              R.string.projNameMissing, 
	              Toast.LENGTH_LONG).show();
	        		
	        	}
	        }
	        	
	    }; 

}
