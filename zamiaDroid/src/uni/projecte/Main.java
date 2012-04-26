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

import uni.projecte.Activities.Citations.CitationManager;
import uni.projecte.Activities.Citations.Sampling;
import uni.projecte.Activities.Maps.CitationMap;
import uni.projecte.Activities.Miscelaneous.ConfigurationActivity;
import uni.projecte.Activities.Projects.ProjectManagement;
import uni.projecte.controler.BackupControler;
import uni.projecte.controler.PreferencesControler;
import uni.projecte.controler.ProjectControler;
import uni.projecte.dataTypes.Utilities;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class Main extends Activity {
    /** Called when the activity is first created. */
		
	long idSample=-1;
	long predPrefId;
	
	
	public static final int CONFIGURATION = Menu.FIRST;
	public static final int PROJECT_CHOOSE = Menu.FIRST+1;
	public static final int ABOUT_US = Menu.FIRST+2;
	public static final int BACKUP = Menu.FIRST+3;

	public static final String PREF_FILE_NAME = "PredProject";
	private SharedPreferences preferences;
	private SharedPreferences configPrefs;
	private PreferencesControler prefCnt;
	
	private Location currentLocation;
	
	private TextView locality;
	
	private String projNameS;
	
	/*private static final int ID_UP     = 1;
	private static final int ID_DOWN   = 2;
	private static final int ID_SEARCH = 3;
	
	private QuickAction quickAction;*/


	 
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	

		prefCnt= new PreferencesControler(this);
        Utilities.setLocale(this);

    	setContentView(R.layout.main);
        
      
        //we get all 4 buttons and create their listeners
        //each listener will create an Intent for changing the activity
        
        Button btnSampleCreate = (Button)findViewById(R.id.sampling);
        btnSampleCreate.setOnClickListener(btnSampleCreateListener);
        
        Button btnSincro = (Button)findViewById(R.id.sincro);
        btnSincro.setOnClickListener(btnSincroListener);
      
      
        Button btnRsCreate = (Button)findViewById(R.id.bCrearEstudi);
        btnRsCreate.setOnClickListener(btnRsListener);
        
        locality = (TextView)findViewById(R.id.tvLocality);

        Button btShowMap = (Button)findViewById(R.id.bShowMapMain);
        btShowMap.setOnClickListener(bShowMapListener);
        
        //Button btConfiguration = (Button)findViewById(R.id.btMainOptions);
        //btConfiguration.setOnClickListener(btConfigListener);
      
        //Button btShowGallery = (Button)findViewById(R.id.btShowGallery);
        //btShowGallery.setOnClickListener(btShowGalleryListener);
        
        //ImageButton ibExtendedMenu = (ImageButton)findViewById(R.id.ibExtendedMenu);
        //ibExtendedMenu.setOnClickListener(btExtendedInfo);
        
		configPrefs=PreferenceManager.getDefaultSharedPreferences(this);

		/*quickAction = new QuickAction(this, QuickAction.VERTICAL);
		ActionItem nextItem 	= new ActionItem(ID_DOWN, "Informació del projecte", getResources().getDrawable(R.drawable.arrow_down));
		ActionItem prevItem 	= new ActionItem(ID_UP, "Canviar de projecte", getResources().getDrawable(R.drawable.arrow_down));
        ActionItem searchItem 	= new ActionItem(ID_SEARCH, "Gestió tesaures", getResources().getDrawable(R.drawable.arrow_down));

		//add action items into QuickAction
        quickAction.addActionItem(nextItem);
		quickAction.addActionItem(prevItem);
		quickAction.addActionItem(searchItem);
		 */
    
        createFolderStructure();
        
        if(prefCnt.isFirstRun()) createFistExecutionDialog();
        
        
    	PreferencesControler pC= new PreferencesControler(getApplicationContext());
		pC.setAutoField("locality", "");
		
		
		if(pC.isFirstUpdate()){
		
			BackupControler bc= new BackupControler(this);
			bc.copyProjects();
			
			
			pC.setFirstUpdate(false);
		
		}
		
		if(pC.isSecondUpdate()){
			
			BackupControler bc= new BackupControler(this);
			
			bc.clearTH();
			
			pC.setSecondUpdate(false);
		
		}
		
		

    }
    

    
    private void loadLocalLocale() {

    	
    	String localName=prefCnt.getLang();
    	Locale locale = new Locale(localName);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    
        
	}



	@Override
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

        	ProjectControler projCnt= new ProjectControler(this);
        	long result=projCnt.loadProjectInfoById(predPrefId);
        	
        	if(result>-1){
        		
        		projNameS=projCnt.getName();
        		
            	projName.setText(projNameS);
            	
            		
        	}
        	
        	else{
        		
            	projName.setText(getString(R.string.noProjectChosen));
                
    	        SharedPreferences.Editor editor = preferences.edit();
    	        editor.putLong("predProjectId", -1);
    	        editor.commit();
        		
        	}
        	
        	
        }
    	
    	
    }
    
    
@Override
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
    

    
	/*private OnClickListener btConfigListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
        	    	
        	 Intent settingsActivity = new Intent(getBaseContext(),
                     ConfigurationActivity.class);
		       startActivityForResult(settingsActivity,3);
        	
        }
    };*/
    
	/*private OnClickListener btExtendedInfo = new OnClickListener()
    {
        public void onClick(View v)
        {                        
        	    	
        	
        	
    		
    		
    		//quickAction.show(v);
        	
        }
    };*/


  /*  private OnClickListener btShowGalleryListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
        	    	
        	
      	if(predPrefId==-1){
    		
    		Intent projActivity = new Intent(getBaseContext(),ProjectManagement.class);
    		projActivity.putExtra("tab", 1);
        	startActivity(projActivity);
        	
    		
    		
    	}
    	else {
    		
    		Intent intent = new Intent(getBaseContext(), GalleryGrid.class);
            
     		Bundle b = new Bundle();
     		b = new Bundle();
     		b.putLong("id", predPrefId);
     		intent.putExtras(b);

     		startActivity(intent);


    	}
         
        }
    };*/

    
    
   
	private OnClickListener changeProjListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
        	    	
        	int tabId;
        	if(predPrefId<0) tabId=1;
        	else tabId=0;
        	
    		Intent projActivity = new Intent(getBaseContext(),ProjectManagement.class);
    		projActivity.putExtra("tab", tabId);
        	startActivity(projActivity);
        	
        }
    };
    
	/*private OnClickListener provaListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
        	    	
    
        	if(predPrefId==-1){
        		
        		Intent projActivity = new Intent(getBaseContext(),ProjectManagement.class);
        		projActivity.putExtra("tab", 1);
            	startActivity(projActivity);
            	
        		
        		
        	}
        	else {
        		
        		Intent intent = new Intent(v.getContext(), ImageGalery.class);
        	       
	 			Bundle b = new Bundle();
	 			b = new Bundle();
	 			b.putLong("id", predPrefId);
	 			intent.putExtras(b);

	            startActivity(intent);


        	}
        	
        	
        	
        	
        }
    };*/
    
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
        		
        		Intent intent = new Intent(v.getContext(), CitationManager.class);        	       
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
    
    
    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
             pi = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }
    
        
    
    
    private void createAboutUsDialog() {

    	final Dialog dialog;
    	PackageInfo versionInfo = getPackageInfo();
    	
    	dialog = new Dialog(this);
 	   	
    	dialog.setContentView(R.layout.aboutus);
    	dialog.setTitle(getString(R.string.aboutUs)+": ZamiaDroid");
   	  
    	TextView tvAboutUsExt=(TextView) dialog.findViewById(R.id.tvAboutUsExtended);
    	tvAboutUsExt.setText(Html.fromHtml(getString(R.string.aboutUsExtended)));
    	TextView tvVersion=(TextView) dialog.findViewById(R.id.tvZamiaDroidVersion);
    	tvVersion.setText(Html.fromHtml(String.format(getString(R.string.appVersion), versionInfo.versionName)));
    	
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
           
    	 final EditText etUserName=(EditText)dialog.findViewById(R.id.userName);

    	 etUserName.setImeOptions(EditorInfo.IME_ACTION_DONE);
   	
    	  
    	  languageList.setOnClickListener(new Button.OnClickListener(){
	             
  	    	
  	    	public void onClick(View v)
  	    	              {
						
						
						AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
						
				    	builder.setTitle("Idiomes | Languages");
				    	
				    	 final String[] languages=getResources().getStringArray(R.array.languages);
				    	
				    	builder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
				    	    public void onClick(DialogInterface dialog, int item) {
				    	      
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

  	    	                 String userName=etUserName.getText().toString();

  	    	  			   		
  	    	                 if(userName.equals("")){
	  	    	                 
  	    	                	 Toast.makeText(getBaseContext(), 
	  	    	        	                v.getResources().getString(R.string.noUserName), 
	  	    	        	                 Toast.LENGTH_SHORT).show();
  	    	                 
  	    	                 }
  	    	                 
  	    	                 else{
  	    	                	 
  	    	                   
  	    	                   editor.putString("usernamePref",userName);
  	    	                   editor.commit();
  	    	                   dialog.dismiss();
  	    	                   prefCnt.setFirstRun(false);
  	    	                   
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
    	
    	
    	String path= prefCnt.getDefaultPath();

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

		f = new File("/sdcard/"+path+"/Backups");
		if(!f.exists())	f.mkdir();
    }
    
    
    @Override
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
    
   
    
 /*   private void handleLocationChanged(Location loc)
    {
    	// Save the latest location
    	this.currentLocation = loc;
    	// Update the latitude & longitude TextViews
    
    	handleReverseGeocodeClick();
    }
    
    private void handleReverseGeocodeClick()
    {
    	if (this.currentLocation != null)
    	{
    		// Kickoff an asynchronous task to fire the reverse geocoding
    		// request off to google
    		ReverseGeocodeLookupTask task = new ReverseGeocodeLookupTask();
    		task.applicationContext = this;
    		task.execute();
    	}
    	else
    	{
    		// If we don't know our location yet, we can't do reverse
    		// geocoding - display a please wait message
    		showToast("Please wait until we have a location fix from the gps");
    	}
    }
    */
	public void showToast(CharSequence message)
    {
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(getApplicationContext(), message, duration);
		toast.show();
    }
	
	public class ReverseGeocodeLookupTask extends AsyncTask <Void, Void, String>
    {
    	private ProgressDialog dialog;
    	protected Context applicationContext;
    	
    	@Override
    	protected void onPreExecute()
    	{
    		this.dialog = ProgressDialog.show(applicationContext, "Please wait...contacting the tubes.", 
                    "Requesting reverse geocode lookup", true);
    	}
    	
		@Override
		protected String doInBackground(Void... params) 
		{
			String localityName = "";
			
			if (currentLocation != null)
			{
				//localityName = Geocoder.reverseGeocode(currentLocation);
			}
			
			return localityName;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			this.dialog.cancel();
			locality.setText(result);
			
			if(result!=null && !result.equals("")){
			
				PreferencesControler pC= new PreferencesControler(getApplicationContext());
				pC.setAutoField("locality", result);
			
			}
			//uni.projecte.dataTypes.Utilities.showToast("Your Locality is: " + result, applicationContext);
//			locationManager.removeUpdates(listener);
			
		}
    }
    
}

    

    