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

import uni.projecte.controler.PreferencesControler;
import uni.projecte.controler.ResearchControler;
import uni.projecte.controler.SampleControler;
import uni.projecte.dataLayer.bd.CitacionDbAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class SamplesList extends Activity{
	
	   private ListView llista;
	  // private SampleDbAdapter mDbHelper;
	 //  private AttributeDbAdapter mDbHAtrib;

	   private Cursor cUnSyncro;
	   private Cursor ctotal;
	   
	   private long projId;
	   private SimpleCursorAdapter mostres;
	   private String projectName;
	   private String thName;

		private static final int OK_ALL_ITEMS = Menu.FIRST;
		private static final int OK_UNSYNCRO =Menu.FIRST+1;
		private static final int REMOVE_SAMPLES =Menu.FIRST+2;
		private static final int EXPORT_CITATIONS =Menu.FIRST+3;
		private static final int IMPORT_CITATIONS =Menu.FIRST+4;
		private static final int SHOW_FIELDS =Menu.FIRST+5;
		private static final int CHOOSE_FIELD =Menu.FIRST+6;
		private static final int SHOW_MAP =Menu.FIRST+7;
		
		
		public static final String PREF_FILE_NAME = "PredProject";
		private SharedPreferences preferences;

		private ResearchControler rC;

		private boolean stateAll=false;
		static final int DIALOG_RS_FIELDS=1;
		private int numCitations;
		
		private Dialog dialog;
		
		private Cursor cTemp;
		
		String attributes;
		String predField;
		String fileName;
		
		private ProgressDialog pd;
		
		int statusFinal=0;
		

	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        
	        setContentView(R.layout.sampleresearch);
	        
	        rC= new ResearchControler(this);
	        rC.loadProjectInfoById(getIntent().getExtras().getLong("id"));

	        
	        TextView tip= (TextView)findViewById(R.id.tvRschName);
	        
	        projectName=rC.getName();
	        thName=rC.getThName();
	       
	        String projNameString=getString(R.string.cLprojectName);
	        String thNameString=getString(R.string.cLthName);
	        

	        tip.setText(Html.fromHtml("<b>"+projNameString+":</b> "+projectName));
	        TextView desc= (TextView)findViewById(R.id.tvRschDesc);
	        desc.setText(Html.fromHtml("<b>"+thNameString+": </b>"+rC.getThName())); 
	        
	        projId=getIntent().getExtras().getLong("id"); 
	              
	        llista = (ListView)findViewById(R.id.lSamples);

	      //simple click listener
	        llista.setOnItemClickListener(theListListener);
	          

	   }
	   
	   public void onResume(){
		   
		   super.onResume();
		   
	    	preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
	        predField = preferences.getString("predField", null);

		   if(predField==null) fillSampleList();
		   else fillSampleField(predField);
		  
		   
	   }
	   
	   public void onPause(){
		   
		   super.onPause();
		   
		   cUnSyncro.close();
		   
		   
	   }
	   
	   private void setDefaultField(String fieldName){
		   
	        SharedPreferences.Editor editor = preferences.edit();
	        editor.putString("predField", fieldName);
	        editor.commit();
		   
	   }
	   
	   public void fillSampleList(){
		   
		    SampleControler sC= new SampleControler(this);
		
	      // cUnSyncro = sC.getSampleListCursorByProjIdUnsyncro(idRs);
		    
		    Log.d("SampleList","A. Num Total Citations");
		    
		    Cursor allCitations=sC.getCitationListCursorByProjIdUnsyncro(projId);
		    
		    allCitations.moveToFirst();
		    
		    TextView numSamples= (TextView)findViewById(R.id.tvNumSamplesNoSyncro);
		    
		    numCitations=allCitations.getCount();
		    
	        numSamples.setText("/"+numCitations+"]");
	        
	        allCitations.close();
		    
		    Log.d("SampleList","A. Getting samples by Field");

		    cUnSyncro = sC.getCitationsWithFirstFieldByProjectId(projId);
	        
//	        ctotal= sC.getSampleListCursorByProjId(idRs);
		    ctotal=null;
	        
	        cUnSyncro.moveToFirst();
	        
	        TextView numFilteredSamples= (TextView)findViewById(R.id.tvNumFilteredSamples);
	        numFilteredSamples.setText("["+cUnSyncro.getCount());
	        
	        TextView tvFilteredField= (TextView)findViewById(R.id.tvFilteredFieldName);
	        tvFilteredField.setText(sC.getFirstFieldLabel());
	        
	       setDefaultField(sC.getFirstFieldLabel());

		    Log.d("SampleList","A. Setting Adapter");

	       
	        String[] from= new String[] {CitacionDbAdapter.KEY_ROWID,CitacionDbAdapter.VALUE,CitacionDbAdapter.DATE/* , SampleDbAdapter.LONGITUDE, SampleDbAdapter.DATE */};
	        int[] to = new int[] { R.id.tvId,R.id.Slat,R.id.Slong}; 

		   
	        // Now create an array adapter and set it to display using our row
	        mostres = new SimpleCursorAdapter(this, R.layout.types_row, cUnSyncro, from, to);
	        
	        llista.setAdapter(mostres);
		   
	   }
	   
	
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	
	        //menu.add(0, OK_ALL_ITEMS, 0,"Veure totes les Mostres");

	    	menu.add(0, SHOW_FIELDS, 0,R.string.mShowProjectProperties).setIcon(android.R.drawable.ic_menu_info_details);
	    	menu.add(0, REMOVE_SAMPLES, 0,R.string.mRemoveCitations).setIcon(android.R.drawable.ic_menu_delete);
	    	menu.add(0, SHOW_MAP, 0,R.string.mShowCitationMap).setIcon(android.R.drawable.ic_menu_mapmode);
	    	menu.add(0, CHOOSE_FIELD, 0,R.string.mChooseFilteredField).setIcon(android.R.drawable.ic_menu_view);
	     	menu.add(0, EXPORT_CITATIONS, 0,R.string.mCitationExport).setIcon(android.R.drawable.ic_menu_upload);
	    	menu.add(0, IMPORT_CITATIONS, 0,R.string.mCitationImport).setIcon(android.R.drawable.ic_menu_save);

	        stateAll=false;
	        
	        return super.onCreateOptionsMenu(menu);
	    }

	    
	    
	    public boolean onPrepareOptionsMenu  (Menu menu){
	    	
	    	
	    	if(stateAll){
	    		
	    		menu.clear();
	    		//menu.add(0, OK_UNSYNCRO, Menu.NONE,"Veure mostres no sincronitzades");
		   
	    		menu.add(0, SHOW_FIELDS, 0,R.string.mShowProjectProperties).setIcon(android.R.drawable.ic_menu_info_details);
		    	menu.add(0, REMOVE_SAMPLES, 0,R.string.mRemoveCitations).setIcon(android.R.drawable.ic_menu_delete);
		    	menu.add(0, SHOW_MAP, 0,R.string.mShowCitationMap).setIcon(android.R.drawable.ic_menu_mapmode);
		    	menu.add(0, CHOOSE_FIELD, 0,R.string.mChooseFilteredField).setIcon(android.R.drawable.ic_menu_view);
		     	menu.add(0, EXPORT_CITATIONS, 0,R.string.mCitationExport).setIcon(android.R.drawable.ic_menu_upload);
		    	menu.add(0, IMPORT_CITATIONS, 0,R.string.mCitationImport).setIcon(android.R.drawable.ic_menu_save);
	    		
	    	}
	    	else{
	    		
	    		menu.clear();
		       // menu.add(0, OK_ALL_ITEMS, Menu.NONE,"Veure totes les Mostres");
	    		menu.add(0, SHOW_FIELDS, 0,R.string.mShowProjectProperties).setIcon(android.R.drawable.ic_menu_info_details);
		    	menu.add(0, REMOVE_SAMPLES, 0,R.string.mRemoveCitations).setIcon(android.R.drawable.ic_menu_delete);
		    	menu.add(0, SHOW_MAP, 0,R.string.mShowCitationMap).setIcon(android.R.drawable.ic_menu_mapmode);
		    	menu.add(0, CHOOSE_FIELD, 0,R.string.mChooseFilteredField).setIcon(android.R.drawable.ic_menu_view);
		     	menu.add(0, EXPORT_CITATIONS, 0,R.string.mCitationExport).setIcon(android.R.drawable.ic_menu_upload);
		    	menu.add(0, IMPORT_CITATIONS, 0,R.string.mCitationImport).setIcon(android.R.drawable.ic_menu_save);



	    	}
	    	
	    	return super.onPrepareOptionsMenu(menu);
	    }
	    
	    
	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {
	    	
	    	
			switch (item.getItemId()) {
			case OK_ALL_ITEMS:
				
				//ctotal.moveToFirst();
			/*	mDbHelper.open();
		        ctotal= mDbHelper.fetchSamplesByResearchId(projId);
				mostres.changeCursor(ctotal);
				stateAll=true;
				mDbHelper.close(); */
				 			 
				break;
				
			case OK_UNSYNCRO:
				
				
				//cUnSyncro);
				//cUnSyncro.moveToFirst();
				/*mDbHelper.open();
		        cUnSyncro = mDbHelper.fetchUnsyncronisedSamples(projId);
		        mostres.changeCursor(cUnSyncro);
		        stateAll=false;
				mDbHelper.close();*/

			
		        break;
		        
			case EXPORT_CITATIONS:
				
				
				   if(isSdPresent()) exportCitations();

				   else {
			        	
			        	Toast.makeText(getBaseContext(), 
			                    R.string.noSdAlert, 
			                    Toast.LENGTH_SHORT).show();
			        	
			        }
	
			
		        break;
		        
			case IMPORT_CITATIONS:
				
					importFagus();
	
		        break;
		        
			case SHOW_FIELDS:
				
				showFields();

	        break;
	        
			case CHOOSE_FIELD:
				
				chooseField();

	        break;

			case SHOW_MAP:
				
				showMap();

	        break;
		
			case REMOVE_SAMPLES:
				
			   AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    
			    
			    builder.setMessage(String.format(getString(R.string.removeAllCitationsQuestion), numCitations))
			           .setCancelable(false)
			           .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			            	   
			            	   removeCitacionsThreadCreator();
			   	  		  	
			               }
			           })
			           .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			                    dialog.cancel();
			               }
			           });
			   
			    
				 AlertDialog alert = builder.create();
				 alert.show();
				
		        		   
			
		        break;
			}
			return super.onOptionsItemSelected(item);
		}
	    
	    
	    private void removeCitacionsThreadCreator() {
			 
			 pd = ProgressDialog.show(this, getString(R.string.citRemovalLoading), getString(R.string.citRemovalTxt), true,false);

			                 Thread thread = new Thread(){
			  	        	   
				                 public void run() {
				               	  
				                	 removeCitationsThread();
				               	  
				                 }
				           };
				           
				           
			   thread.start();
			}	
		 
		 
		   private void removeCitationsThread(){
			   
			   SampleControler sC=new SampleControler(getBaseContext());
        	   sC.deleteAllCitationsFromProject(projId);
     	 	
			   handlerRemove.sendEmptyMessage(0);
     			

		   }
		   
			 private Handler handlerRemove = new Handler() {

					public void handleMessage(Message msg) {	
			
						pd.dismiss();
						fillSampleList();

					

					}
				};
	    
	    
	    
	    
	    public static boolean isSdPresent() {

		      return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		
		  }
	    
	   
	    private void chooseField() {

	    	final CharSequence[] items=rC.getListProjFields(projId);

	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setTitle(R.string.chooseField);
	    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int item) {
	    	       // Toast.makeText(getApplicationContext(), "Resultats filtrats pel camp: "+items[item], Toast.LENGTH_SHORT).show();
	    	        fillSampleField((String) items[item]);
	    	        
	    		    setDefaultField((String) items[item]);

	    	        dialog.dismiss();
	    	    }
	    	});
	    	
	    	AlertDialog alert = builder.create();
	    	
	    	alert.show();
	    	
	    	
		}

	    private void fillSampleField(String fieldName){
		    
	    		SampleControler sC= new SampleControler(this);
	    		
			    Log.d("SampleList","B. Num Total Citations");
	    		
	    		Cursor total=sC.getCitationListCursorByProjIdUnsyncro(projId);
	    		total.moveToFirst();
	    		numCitations=total.getCount();
	    		total.close();
	    		
			    Log.d("SampleList","B. Num Total Citations by Field");

			    cUnSyncro = sC.getSamplesByField(projId,fieldName);
			    
			    if(cUnSyncro==null){
			    	
			    	fillSampleList();
			    	
			    }
			    
			    else{
			    	
			    
			        cUnSyncro.moveToFirst();
			        
			        
			        TextView numSamples= (TextView)findViewById(R.id.tvNumSamplesNoSyncro);
			        numSamples.setText("/"+numCitations+"]");
			        
			        TextView numFilteredSamples= (TextView)findViewById(R.id.tvNumFilteredSamples);
			        numFilteredSamples.setText("["+cUnSyncro.getCount());
			        
			        TextView tvFilteredField= (TextView)findViewById(R.id.tvFilteredFieldName);
			        tvFilteredField.setText(fieldName);
			        
				    Log.d("SampleList","B. Setting Adapter");
	
			        String[] from= new String[] {CitacionDbAdapter.KEY_ROWID,CitacionDbAdapter.VALUE,CitacionDbAdapter.DATE/* , SampleDbAdapter.LONGITUDE, SampleDbAdapter.DATE */};
			        int[] to = new int[] { R.id.tvId,R.id.Slat,R.id.Slong}; 
	
			        // Now create an array adapter and set it to display using our row
			        mostres = new SimpleCursorAdapter(this, R.layout.types_row, cUnSyncro, from, to);
			        llista.setAdapter(mostres);
		        
			    }

	    	
	    }
	    
	    
		private void importFagus() {
	    	
	    	Intent myIntent = new Intent(this, FagusImport.class);
        	myIntent.putExtra("Id", projId);
            startActivityForResult(myIntent, 0);
			
		}

	    
	    private void showMap(){
	                        
	        	if (mostres.isEmpty()){
	        		
	        		Toast.makeText(getBaseContext(), R.string.noCitations, Toast.LENGTH_SHORT).show();
	        		
	        	}
	        	
	        	else{
		        	
	        		Intent myIntent = new Intent(this, CitationMap.class);
		        	myIntent.putExtra("id", projId);
		
		            startActivityForResult(myIntent, 0);
	            
	        	}
	        	
	    }
	    
	   private void showFields(){
		  
		   	 
		   	Intent intent = new Intent(this, ProjectInfo.class);
		       
 			Bundle b = new Bundle();
 			b.putLong("Id", projId);
 			intent.putExtras(b);
 			
 			b.putString("projName", projectName);
 			intent.putExtras(b);
 			intent.putExtras(b);
 			b = new Bundle();
 			b.putString("projDescription",thName);
 			intent.putExtras(b);
 		
 			
	 		startActivityForResult(intent, 1);   
		   
		   
		 //       atriC = mDbHAtrib.fetchAttributesFromRsNotOrdered(projId);
	      //  	createStringAtrib(atriC);
	   	   	// 	showDialog(DIALOG_RS_FIELDS);

	   }
	  
	    
	    public OnItemClickListener theListListener = new OnItemClickListener() {
		    
		    public void onItemClick(android.widget.AdapterView<?> parent, View v, int position, long id) {
		        
		   	 TextView tv=(TextView)v.findViewById(R.id.tvId);
		   	 String idd=(String)tv.getText();
		   	 
	    	 
	    	 ResearchControler rc= new ResearchControler(v.getContext());
	    	 rc.loadProjectInfoById(projId);
	    	 
			Intent intent = new Intent(v.getContext(), CitationEditor.class);
       
		 			Bundle b = new Bundle();
		 			b.putString("rsName", rc.getName());
		 			intent.putExtras(b);
		 			b = new Bundle();
		 			b.putLong("id", rc.getIdRs());
		 			intent.putExtras(b);
		 			b = new Bundle();
		 			b.putString("rsDescription", rc.getThName());
		 			intent.putExtras(b);
		 			b = new Bundle();
		 			b.putLong("idSample", Long.parseLong(idd));
		 			intent.putExtras(b);
		 		
		 			
		 		   startActivityForResult(intent, 1);   
		   	
		    	
		    }
		    };
		    
		  
	

		   private void exportCitations(){
			   
	        	
	        	if (stateAll) cTemp=ctotal;
	        	else cTemp=cUnSyncro;
	        	
	        	
				   cTemp.moveToFirst();
				  // sC.sendDataSamples(cTemp,this);
				   
				   AlertDialog.Builder builder = new AlertDialog.Builder(this);
				   builder.setTitle(getString(R.string.chooseExportFormat));
				   
				   final String[] formats=getResources().getStringArray(R.array.exportFormats);
				   
				   builder.setSingleChoiceItems(formats,-1, new DialogInterface.OnClickListener() {
				       public void onClick(DialogInterface dialog, int item) {
				           
				    	   dialog.cancel();
				    	   createCitationExportDialog(formats[item]);
				    	   
				       }
				   });
				   AlertDialog alert = builder.create();
				   alert.show();
					
			   
		   }
		   
		private void createCitationExportDialog(final String format){
			
			dialog = new Dialog(this);
    	   	
        	dialog.setContentView(R.layout.fagusfilecreation);
    	   	dialog.setTitle(R.string.insert_data);
    	   	
    	   	Button bExportFagus = (Button)dialog.findViewById(R.id.bExportFagus);
    		
    	   	EditText name=(EditText)dialog.findViewById(R.id.fileName);
    	   	name.setText(projectName+"_"+format);
    	   	
    	   	
    	    bExportFagus.setOnClickListener(new Button.OnClickListener(){
    	    	             
    	    	
    	    	public void onClick(View v)
    	    	              {

    	    	                 EditText et=(EditText)dialog.findViewById(R.id.fileName);
    	    	                 
    	    	                 
    	    	                 createCitationFile(et.getText().toString(),format);
    	    	                 
    	    	            	 
    	    	              }
    	    	             
    	    });
    	    
    	    dialog.show();
			
			
		}   
		
		
		 
		 private void createCitationFileThread(String format){
			 
				SampleControler sC=new SampleControler(this);
				statusFinal=sC.exportProject(projId, this, fileName, format);

				Log.d("SampleList","C. SampleListExported:"+projectName+":"+fileName+":"+format);
			 
				handler.sendEmptyMessage(statusFinal);

		 }
		 
		 
			
			
			
			private void createCitationFile(String fileName, final String format){
				
				//checking that file exists
				
				statusFinal=0;
				
				PreferencesControler pC=new PreferencesControler(this);
				File file=null;
				
				if(format.equals("Fagus")){
					
			        file = new File("/sdcard/"+pC.getDefaultPath()+"/Citations/", fileName+".xml");

					
				}
				
				else{
					
			        file = new File("/sdcard/"+pC.getDefaultPath()+"/Citations/", fileName+".tab");

					
				}
				
				this.fileName=fileName;
				
		        
		        if(file.exists()) {
		        	 
		        	
		        	
		        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        	
					    builder.setMessage(getString(R.string.citFileExists))
					           .setCancelable(false)
					           .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					               public void onClick(DialogInterface dialog, int id) {
					            	   
					   
					   				dialog.dismiss();
					   				
					   				createExportThread(format);
					            	   
					   	  		  	
					               }
					           })
					           .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					               public void onClick(DialogInterface dialog, int id) {
					            	   	
						   				dialog.dismiss();
						
					               }
					           });
					   
					    
						 AlertDialog alert = builder.create();
						 alert.show();
		        	
		        	
		        
		        	
		        }
		        else{
		
		        	
					createExportThread(format);
		

			    
		        }
		       
				
			}
			
		 
			private void createExportThread(final String format){
				
				
				 pd = ProgressDialog.show(this, getString(R.string.citExportLoading), getString(R.string.citExportTxt), true,false);
				 
				                 Thread thread = new Thread(){
				  	        	   
					                 public void run() {
					               	  
					               	  
					                	createCitationFileThread(format); 
					               	  
					                 }
					           };
					           
					           
				   thread.start();
			}
		 
		 
		 private Handler handler = new Handler() {

				public void handleMessage(Message msg) {
					
					pd.dismiss();
					
					dialog.dismiss();
					
					String toastText=getString(R.string.bFagusFileExported);
		                
		           Toast.makeText(getBaseContext(), 
		       	                toastText+fileName, 
		       	                 Toast.LENGTH_SHORT).show();
				
		
				}
			};
		
		   
			       
	   @SuppressWarnings("unused")
	private void sincronize(){
		   

        	if (stateAll) cTemp=ctotal;
        	else cTemp=cUnSyncro;
        	
        	
			   cTemp.moveToFirst();
			  // sC.sendDataSamples(cTemp,this);
			   
			   dialog = new Dialog(this);
	    	   	
	        	dialog.setContentView(R.layout.fagusfilecreation);
	    	   	dialog.setTitle("Introdueixi les dades");
	    	   	
	    	   	Button bExportFagus = (Button)dialog.findViewById(R.id.bExportFagus);
	    		
	    	   	EditText name=(EditText)dialog.findViewById(R.id.fileName);
	    	   	name.setText(projectName+"_fagus.xml");
	    	   	
	    	   	
	    	    bExportFagus.setOnClickListener(new Button.OnClickListener(){
	    	    	             
	    	    	
	    	    	public void onClick(View v)
	    	    	              {

	    	    	                 EditText et=(EditText)dialog.findViewById(R.id.fileName);
	    	    	  			   		
	    	    	                 createCitationFile(et.getText().toString(),"sincro");
	    	    	                 
	    	    	                 String toastText=v.getContext().getString(R.string.bFagusFileExported);
	    	    	                 
	    	    	                 Toast.makeText(getBaseContext(), 
	    	    	        	                toastText+et.getText().toString(), 
	    	    	        	                 Toast.LENGTH_SHORT).show();
	    	    	            	 
	    	    	              }
	    	    	             
	    	    });
	    	    
	    	    dialog.show();
	    	   
		   
	   }
	   
	   @SuppressWarnings("unused")
	private String createStringAtrib(Cursor atriC) {
		
		   attributes="";
		   
		   atriC.moveToFirst();
		   
		   int n=atriC.getCount();
		   
		   for(int i=0;i<n;i++){
			   
			   attributes=attributes.concat(" | "+atriC.getString(4));
			   
			   atriC.moveToNext();
			   
		   }
		   
		   
		   return attributes;
		   
	}
	   
	  	       
	   
}