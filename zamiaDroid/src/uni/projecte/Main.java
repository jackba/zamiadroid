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
import java.util.Locale;

import uni.projecte.controler.PreferencesControler;
import uni.projecte.controler.ResearchControler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;



public class Main extends Activity {
    /** Called when the activity is first created. */
		
	long idSample=-1;
	long predPrefId;
	
	
	public static final int CONFIGURATION = Menu.FIRST;
	public static final int PROJECT_CHOOSE = Menu.FIRST+1;
	public static final int ABOUT_US = Menu.FIRST+2;

	public static final String PREF_FILE_NAME = "PredProject";
	private SharedPreferences preferences;
	private SharedPreferences configPrefs;
	private PreferencesControler pc;
	
	 
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	

		pc= new PreferencesControler(this);
        loadLocalLocale();

    	
        setContentView(R.layout.main);
      
        //we get all 4 buttons and create their listeners
        //each listener will create an Intent for changing the activity
        
        ImageButton btnSampleCreate = (ImageButton)findViewById(R.id.mostra);
        btnSampleCreate.setOnClickListener(btnSampleCreateListener);
        
        ImageButton btnSincro = (ImageButton)findViewById(R.id.sincro);
        btnSincro.setOnClickListener(btnSincroListener);
      
      
        ImageButton btnRsCreate = (ImageButton)findViewById(R.id.bCrearEstudi);
        btnRsCreate.setOnClickListener(btnRsListener);
        
      /*  ImageButton btnDataType= (ImageButton)findViewById(R.id.bDataType);
        btnDataType.setOnClickListener(bDataTypeListener); */
        
      
        
        //Button provaBtn = (Button) findViewById(R.id.provaButton);

        ImageButton btShowMap = (ImageButton)findViewById(R.id.bShowMapMain);
        btShowMap.setOnClickListener(bShowMapListener);
        
		configPrefs=PreferenceManager.getDefaultSharedPreferences(this);

    
        createFolderStructure();
        
        if(pc.isFirstRun()) createFistExecutionDialog();
        

    }
    

    
    private void loadLocalLocale() {

    	
    	String localName=pc.getLang();
    	Locale locale = new Locale(localName);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    	

        
	}



	public void onResume(){
    	
    	super.onResume();
    	
    	preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        predPrefId = preferences.getLong("predProjectId", -1);
    	
        TextView projName= (TextView) findViewById(R.id.projectNameM);
        
        projName.setOnClickListener(changeProjListener);


        
        if(predPrefId==-1) {	
        	
        	projName.setText(getString(R.string.noProjectChosen));
        
        }
        else {

        	ResearchControler rC= new ResearchControler(this);
        	long result=rC.loadProjectInfoById(predPrefId);
        	
        	if(result>-1){
        		
            	projName.setText(rC.getName());
            	
            		
        	}
        	
        	else{
        		
            	projName.setText(getString(R.string.noProjectChosen));
                
    	        SharedPreferences.Editor editor = preferences.edit();
    	        editor.putLong("predProjectId", -1);
    	        editor.commit();
        		
        	}
        	
        	
        }
    	
    	
    }
    
    
public boolean onCreateOptionsMenu(Menu menu) {
    	
		menu.add(0, CONFIGURATION, 0,R.string.mConfiguration).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, PROJECT_CHOOSE, 0,R.string.mChooseProject).setIcon(android.R.drawable.ic_menu_agenda);
		menu.add(0, ABOUT_US, 0,R.string.mAboutUs).setIcon(android.R.drawable.ic_dialog_info);



    	return super.onCreateOptionsMenu(menu);
    }

	

    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	
    	
		switch (item.getItemId()) {
			
			case CONFIGURATION:
				
			       Intent settingsActivity = new Intent(getBaseContext(),
                           ConfigurationActivity.class);
			       startActivityForResult(settingsActivity,3);
				
				 			 
			break;
			
			case PROJECT_CHOOSE:
				
				int tabId;
	        	if(predPrefId<0) tabId=1;
	        	else tabId=0;
	        	
	    		Intent projActivity = new Intent(getBaseContext(),ProjectManagement.class);
	    		projActivity.putExtra("tab", tabId);
	        	startActivity(projActivity);
				
				
			break;
			
			case ABOUT_US:
				
				createAboutUsDialog();
				
			break;
				
		}
		
	
		return super.onOptionsItemSelected(item);
	}
    


	private OnClickListener changeProjListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
        	
        	/*Intent projActivity = new Intent(getBaseContext(),
                    ProjecteChooser.class);*/
		
        	/*if(predPrefId<0){
        		
        		Intent projActivity = new Intent(getBaseContext(),ProjectCreator.class);
            	startActivity(projActivity);
        		
        		
        	}
        	else {
        		
        		Intent projActivity = new Intent(getBaseContext(),ProjectList.class);
    		
            	startActivity(projActivity);
        		
        		
        	}*/
        	
        	int tabId;
        	if(predPrefId<0) tabId=1;
        	else tabId=0;
        	
    		Intent projActivity = new Intent(getBaseContext(),ProjectManagement.class);
    		projActivity.putExtra("tab", tabId);
        	startActivity(projActivity);
        	
        	

        
        	
        }
    };
    
    private OnClickListener bShowMapListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
        	
        	if (predPrefId==-1){
        		
        		Intent projActivity = new Intent(getBaseContext(),ProjectManagement.class);
        		projActivity.putExtra("tab", 1);
            	startActivity(projActivity);
        		
        		
        	}
        	
        	else{
	        	
        		Intent myIntent = new Intent(v.getContext(), CitationMap.class);
	        	myIntent.putExtra("id", predPrefId);
	
	            startActivityForResult(myIntent, 0);
            
        	}
        	
        }
    };
    
    private OnClickListener btnSampleCreateListener = new OnClickListener()
    {
        public void onClick(View v)
        {        
      
        	if(predPrefId==-1){
        		
        		Intent projActivity = new Intent(getBaseContext(),ProjectManagement.class);
        		projActivity.putExtra("tab", 1);
            	startActivity(projActivity);
            	
        		
        		
        	}
        	else {
        		
        		Intent intent = new Intent(v.getContext(), Sampling.class);
        	       
	 			Bundle b = new Bundle();
	 			b = new Bundle();
	 			b.putLong("id", predPrefId);
	 			intent.putExtras(b);

	            startActivity(intent);


        	}
            
        	       	
                	
        }
    };
    
 /*   private OnClickListener bDataTypeListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
      

            Intent myIntent = new Intent(v.getContext(), DataTypeManagement.class);
            startActivity(myIntent);
       	  	
        	
        }
    }; */
    
    
    private OnClickListener btnSincroListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
      
        	if(predPrefId==-1){
        		
        		Intent projActivity = new Intent(getBaseContext(),ProjectManagement.class);
        		projActivity.putExtra("tab", 1);
            	startActivity(projActivity);
        		
        		
        	}
        	else {
        		
        		Intent intent = new Intent(v.getContext(), SamplesList.class);        	       
	 			Bundle b = new Bundle();
	 			b = new Bundle();
	 			b.putLong("id", predPrefId);
	 			intent.putExtras(b);

	            startActivity(intent);


        	}
            
       	  	
        	
        }
    }; 
    
    private OnClickListener btnRsListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
      
        	
        	int tabId;
        	if(predPrefId<0) tabId=1;
        	else tabId=0;
        
        	Intent myIntent1 = new Intent(v.getContext(), ProjectManagement.class);
        	myIntent1.putExtra("tab", tabId);
        	myIntent1.setAction(Intent.ACTION_CONFIGURATION_CHANGED);
            startActivity(myIntent1);
       	  	
        	
        }
    }; 
    
    
    
    private void createAboutUsDialog() {

    	final Dialog dialog;
    	
    	dialog = new Dialog(this);
 	   	
    	dialog.setContentView(R.layout.aboutus);
    	dialog.setTitle("Sobre Nosaltres");
   	  
    	
    	
	    dialog.show();

	}

    
    
    
    private void createFistExecutionDialog() {
        
	  	final Dialog dialog;
        
  		        	
        	//Context mContext = getApplicationContext();
    	  dialog = new Dialog(this);
    	   	
    	  dialog.setContentView(R.layout.welcomedialog);
    	  dialog.setTitle("ZamiaDroid");
    	  
           final SharedPreferences.Editor editor = configPrefs.edit();

    	   	
    	  Button bStart = (Button)dialog.findViewById(R.id.bStart);
    	 
    	   //	EditText name=(EditText)dialog.findViewById(R.id.etNameItem);

    	 Button languageList=(Button)dialog.findViewById(R.id.bChlanguage);
    	  
   	
    	  
    	  languageList.setOnClickListener(new Button.OnClickListener(){
	             
  	    	
  	    	public void onClick(View v)
  	    	              {
						
						
						AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
						
				    	builder.setTitle("Idiomes | Languages");
				    	
				    	 final String[] languages=getResources().getStringArray(R.array.languages);
				    	
				    	builder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
				    	    public void onClick(DialogInterface dialog, int item) {
				    	        Toast.makeText(getApplicationContext(), "Resultats filtrats pel camp: "+languages[item], Toast.LENGTH_SHORT).show();
				    	        
				    	        String language=languages[item];


								if(language.equals("Castellano")){
			    	                	
			    	                	   editor.putString("listPref","es");
			    	                	   
			    	                   }
			    	                   else if (language.equals("English")) {
			    	                	   
			  	    	                	editor.putString("listPref","en");
			    	                	   
			    	                   }
			    	                   
			    	                   else{
			    	                	   
			  	    	                	editor.putString("listPref","ca");

			    	                   }
			    	                   
			    	                	editor.commit();
			    
			    	                	
			    	                	loadLocalLocale();
			    	                	
			    	                	refreshWelcomeScreen();
		                      
				    	        
				    	        dialog.dismiss();
				    	    }
				    	});
				    	
				    	AlertDialog alert = builder.create();
				    	
				    	alert.show();
						
						
						
					}
                  });

    	  
    	   //	Spinner thList=(Spinner)dialog.findViewById(R.id.thList);
    	   	
    	  bStart.setOnClickListener(new Button.OnClickListener(){
	             
  	    	
  	    	public void onClick(View v)
  	    	              {

  	    	                 EditText et=(EditText)dialog.findViewById(R.id.userName);
  	    	                 String userName=et.getText().toString();
  	    	                
  	    	    
  	    	  			   		
  	    	                 if(userName.equals("")){
	  	    	                 
  	    	                	 Toast.makeText(getBaseContext(), 
	  	    	        	                v.getResources().getString(R.string.noUserName), 
	  	    	        	                 Toast.LENGTH_SHORT).show();
  	    	                 
  	    	                 }
  	    	                 
  	    	                 else{
  	    	                	 
  	    	                   
  	    	                   editor.putString("usernamePref",userName);
  	    	                   editor.commit();
  	    	                   dialog.dismiss();
  	    	                   pc.setFirstRun(false);
  	    	                   
  	    	                 }
  	    	                 
  	    	            	 
  	    	              }
  	    	             
  	    });
    	   	
    	   	//name.setText(prName);
    	
    	    
    	    dialog.show();


    	 
    }
    
	  private void refreshWelcomeScreen() {
			
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		  
		}
    
    
    private void createFolderStructure(){
    	
    	
    	String path= pc.getDefaultPath();

    	File f;

    	f = new File("/sdcard/"+path+"/");
		if(!f.exists())	f.mkdir();
		
		f = new File("/sdcard/"+path+"/Citations");
		if(!f.exists())	f.mkdir();
		
		f = new File("/sdcard/"+path+"/Thesaurus");
		if(!f.exists())	f.mkdir();

		f = new File("/sdcard/"+path+"/Projects");
		if(!f.exists())	f.mkdir();

		f = new File("/sdcard/"+path+"/Photos");
		if(!f.exists())	f.mkdir();

    	
    }
    
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
    
        switch(requestCode) {
        
        case 0:
        	
        
        case 3 :

          	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        
            break;
            
                
            default:
            	
            	System.out.println("default");
     
        }
    }
    
}

    

    