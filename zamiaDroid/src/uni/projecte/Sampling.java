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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import uni.projecte.controler.DataTypeControler;
import uni.projecte.controler.PreferencesControler;
import uni.projecte.controler.ResearchControler;
import uni.projecte.controler.SampleControler;
import uni.projecte.controler.ThesaurusControler;
import uni.projecte.controler.ThesaurusControler.ThesaurusListAdapter;
import uni.projecte.dataTypes.ProjectField;
import uni.projecte.hardwareInterfaces.GPSaccess;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.ub.bio.biogeolib.CoordConverter;
import edu.ub.bio.biogeolib.CoordinateLatLon;
import edu.ub.bio.biogeolib.CoordinateUTM;

public class Sampling extends Activity {

	
	/*dataBase helpers*/
	   private ResearchControler rsCont;
	   private  ThesaurusControler tC;

	   public final static int SUCCESS_RETURN_CODE = 1;
	   public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 3;
	   public static final int ENABLE_GPS=Menu.FIRST;
	   
	   private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
	   
	   private long projId=-1;
	   
	   private  PreferencesControler pm;
	   private TextView rsNameTV;
	   
	   private TextView mLocationDisplay;
	   
	   
	   /*top date Display*/
	   private TextView mDateDisplay;
	    private int mYear;
	    private int mMonth;
	    private int mDay;


		private ArrayList<View> elementsList;
		private int n;

		
    	private Double lat;
    	private Double longitude;
    	private double elevation;
    	
    	private ImageView photoTV;
    	private EditText etPhotoPath;
    	private String fileName;
    	private Uri imageUri;
    	private String _path;
    	
    	private ImageButton photoButton;
    	private Button bUpdateLoc;
    	
    	private boolean predefinedLocation=false;
    	

		
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.sampling);
        
        /*DB access*/
        
        rsCont=new ResearchControler(this);
        tC= new ThesaurusControler(this);
        
        
        /* button Listeners*/ 
        
        Button bCreateSample = (Button)findViewById(R.id.bCreateSample);
        bCreateSample.setOnClickListener(bCreateCitationListener);
        
        bUpdateLoc = (Button)findViewById(R.id.bUpdateLocation);
        bUpdateLoc.setOnClickListener(bUpdateLocationListener);
        
        /* instances of main view elements */
        
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mLocationDisplay = (TextView) findViewById(R.id.tvLocation);
        rsNameTV = (TextView)findViewById(R.id.projectName);

        projId=getIntent().getExtras().getLong("id");
        
        rsCont.loadProjectInfoById(projId);
        	
        rsNameTV.setText(rsCont.getName());
        
        pm= new PreferencesControler(this);


        lat=getIntent().getExtras().getDouble("latitude");
        longitude=getIntent().getExtras().getDouble("longitude");

        if(lat==0.0 && longitude==0.0){
        	
        	lat=null;
        	longitude=null;
        }
        
        
        //we'll create a citation with a pre-setted location
        if(lat!=null && longitude!=null){
        	
        	predefinedLocation=true;
        	bUpdateLoc.setVisibility(Button.INVISIBLE);
        	
        	
        }
        else{
        	
        	  if(pm.gpsNeeded()){
        	        
              	callGPS();
              
              }
        
        }
        
        
      
        
        createFieldForm(projId);
        updateDisplay();

        
    }

    
    protected void onStop(){
		  
		  super.onStop();
		  
		  tC.closeCursors();
		  
		  
		  
	  }
    
    private void callGPS(){
    	
    	   Intent myIntent = new Intent(this, GPSaccess.class);
	        startActivityForResult(myIntent, 2);

    }
    
    
	public boolean onCreateOptionsMenu(Menu menu) {
    	
		if(pm.gpsNeeded()) menu.add(0, ENABLE_GPS, 0,R.string.mDesactivateGPS).setIcon(android.R.drawable.ic_menu_mylocation);
		else menu.add(0, ENABLE_GPS, 0,R.string.mActivateGPS).setIcon(android.R.drawable.ic_menu_mylocation);

    	return super.onCreateOptionsMenu(menu);
    }

	

    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	
    	
		switch (item.getItemId()) {
			case ENABLE_GPS:
				
				if(pm.gpsNeeded()){
					
					pm.setGPSNeeded(false);
					
					
				} 
				else{
					
					pm.setGPSNeeded(true);
					callGPS();
					
				}
				 			 
			break;
				
		}
		
	
		return super.onOptionsItemSelected(item);
	}
    
    
    /*
     * When backButton is pressed a dialog is shown showing an alert.
     * The alert advices the user that it will lose data if it finishes the activity.
     * 
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */

 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
    		
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	
	    	builder.setMessage(R.string.backFromCitationMessage)
	    	       .setCancelable(false)
	    	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    
	    	        	   pm.setGPSNeeded(true);
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

    
    /*
     * It takes the date of the system and location and shows it.
     * 
     * 
     */
    
    
    private void updateDisplay() {
    	
        final Calendar c = Calendar.getInstance();

    	 mYear = c.get(Calendar.YEAR);
         mMonth = c.get(Calendar.MONTH);
         mDay = c.get(Calendar.DAY_OF_MONTH);
         


        mDateDisplay.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mMonth + 1).append("-")
                    .append(mDay).append("-")
                    .append(mYear).append(" ")
                   /* .append(" - ")
                    .append(pad(mHour)).append(":")
                    .append(pad(mMinute))*/);
        
        
        
        if(lat==null || longitude==null){
        	
    		pm.setGPSNeeded(false);

 	    	 mLocationDisplay.setText(R.string.gps_disabled);
 	    	 
 	    	 bUpdateLoc.setVisibility(Button.INVISIBLE);

        	
        }
        else{
        	
        	   if(pm.isUTM()){
      	    	 
      	         CoordinateUTM utm = CoordConverter.getInstance().toUTM(new CoordinateLatLon(lat,longitude));
      		     mLocationDisplay.setText(utm.getShortForm()+" : "+elevation);

      	     }
      	     else {
      	    	 
      	    	 mLocationDisplay.setText(lat.toString().subSequence(0, 7)+" - "+longitude.toString().subSequence(0, 7)+" - "+elevation);
      	    	 
      	     }
        	
   	    	if(!predefinedLocation) bUpdateLoc.setVisibility(Button.VISIBLE);

        }

	
    
    }
    

 
    /*
     * Listener of the citation creation button.
     * 
     * The thesaurus is mandatory. When no geo-location button is check, GPS activity won't be called. 
     * 
     */
    
    private OnClickListener bCreateCitationListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
            
        	//if the thesaurus is not chosen when can't create a citation
        	
        	if (!emptyThesaurus()){
        	    
        	    	 Toast.makeText(getBaseContext(), 
        	                R.string.fieldMissing, 
        	                 Toast.LENGTH_SHORT).show();
        	    	
        	    }
        	  
        	  else{
        		 
        			if(rsNameTV.length()==0){
        	    		
        	    		Toast.makeText(getBaseContext(), 
        	                    R.string.projNameMissing, 
        	                    Toast.LENGTH_SHORT).show();
        	    	}
        	    	
        	    	else  {
        	    		
        	    			if(lat==null && longitude==null){
        	    				
              	    			 createSample(100, 190);

        	    				
        	    			}
        	    			else{
        	    				
        	    				//GPS Activity that will give back the latitude and longitude
           	    			 createSample(lat, longitude);
           	    			 

        	    				
        	    			}
        	    		        	    	
        	    	}

        	  }
        	
        }
    }; 
    
    
    /*
     * Method called when finish button is pressed.
     * It only finishes the Activity
     * 
     */
    
    private void finishActivity(){
    	
    	
        Intent intent = new Intent();
        setResult(0, intent);
   			
        finish();
    	
    	
    }
    
    private OnClickListener bUpdateLocationListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
             
            if(pm.gpsNeeded()){
                
            	callGPS();
            
            }
        	
        }
    };
    
    
    /*
     * 
     * This listener is similar to BCreateCitationListener.
     * There is only one difference. When the citation is stored the activity is finished.
     * 
     */
    
    @SuppressWarnings("unused")
	private OnClickListener bFinishListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
            
        	if (!emptyThesaurus()){
        	    	
        	    	 Toast.makeText(getBaseContext(), 
        	                R.string.fieldMissing, 
        	                 Toast.LENGTH_SHORT).show();
        	    	
        	    }
        	  
        	  else{
        		 
        			if(rsNameTV.length()==0){
        	    		
        	    		Toast.makeText(getBaseContext(), 
        	                    R.string.projNameMissing, 
        	                    Toast.LENGTH_SHORT).show();
        	    	}
        	    	
        	    	else  {

        	    			 createSample(lat, longitude);
        	        		 finishActivity();
 	    	
        	    	}

        	  }
        	
        }
    };
    
   
    
    /*
     * It checks if the thesaurus field is filled
     * 
     */
    
    private boolean emptyThesaurus(){
    	
    	
    	boolean notEmpty=true;

    	
    	for (int i=0;i<n;i++){
    		
    		View vi=elementsList.get(i);

    		
    		if((vi instanceof AutoCompleteTextView)){
    			
    			if (((AutoCompleteTextView)vi).length()==0) notEmpty=false;	

    		}

    		
    	}
    	
    	return notEmpty;
    	
    }
    
    @SuppressWarnings("unused")
    
	private boolean emptyAttributes(){
    	
    	boolean notEmpty=true;
    	

    	//are all the attributes filled?
    	
    	for (int i=0;i<n;i++){
    		
    		View vi=elementsList.get(i);
    		
    		if (vi instanceof EditText){
				
    			if (((EditText)vi).length()==0) notEmpty=false;	
			}
    		
    		else if((vi instanceof AutoCompleteTextView)){
    			
    			if (((AutoCompleteTextView)vi).length()==0) notEmpty=false;	

    		}
			
    		
    		else if((vi instanceof CheckBox)){
    			
    			
    			//rubish
    		}
			
			else {
				
				if (((Spinner)vi).getSelectedItem().toString().length()==0) notEmpty=false;	
				
			}
    		
    		
    	}

    	return notEmpty;  
    	    	
    }
    
    
    /*
     * 
     * This method clears the form for the next citation
     * 
     */
    private void removeAttributes(){


			n=elementsList.size();
			
			for (int i=0;i<n;i++){
				
				
				View et=elementsList.get(i);
								
				if (et instanceof EditText){
					((TextView) et).setText("");
				}
				
				else if(et instanceof CheckBox){

					
				}
				
			}
			
   		    if(predefinedLocation) {
   		    	
   		    	finish();
   		    	
   		    }
   		    
   		    else{


				   if(pm.gpsNeeded()){
				        
			        	callGPS();
			        
			        }
	
				 if(photoTV!=null) {
					 
					 photoTV.setImageBitmap(null);
			
					 photoButton.setVisibility(ImageButton.VISIBLE);
					 etPhotoPath.setText(getText(R.string.photoMissing));
					 
	
				 }
				 
				   
				 elementsList.get(0).requestFocus();
			 
   		    }

    }
    
    /*
     * This method takes the values filled by the user in the form and store them into the DB
     * For each type of field the retrieval of the value will be different
     * 
     */
    
    private void addFieldValues(long idSample, SampleControler smplCntr){
    			
			n=elementsList.size();
			
			smplCntr.startTransaction();
			
			
			for (int i=0;i<n;i++){
				
				String value="";
				String label="";
				
				View et=elementsList.get(i);
								
				if (et instanceof EditText){
					
					value= ((TextView) et).getText().toString();
					label= ((TextView) et).getTag().toString();
					
				}
				
				else if(et instanceof CheckBox){
					
					if (((CheckBox) et).isChecked()) value="true";
					else value="false";
					
				}
				
				else {
					
					value =((Spinner) et).getSelectedItem().toString();
					label= ((Spinner) et).getTag().toString();

					
				}
				
				
					/*if value is empty we don't add the value into the database*/
					if(value.compareTo("")==0){
						
					}
					
					else{
						
						smplCntr.addCitationField(projId,idSample, et.getId(),label, value);
	
						
					}

			}
			
			
			smplCntr.addObservationAuthor(projId,idSample);

			smplCntr.EndTransaction();

    	
    }
    
    /*
     * 
     * This method stores the citation in the DB. Getting location and projectId
     * It also calls the addFildValues method for the insertion of the filled fields.
     * 
     */
    
    
    private long createSample(double latPoint, double longPoint){
    	
    
	    SampleControler sampleCntr=new SampleControler(this);
	    Log.d("Citations","New SC");

        		
    	// Sample Creation with lat,long, comment and rsId		
        long idSample=sampleCntr.createCitation(projId, latPoint, longPoint, "");
        
	    Log.d("Citations","CreateCitation");

        
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(300);
        
        addFieldValues(idSample, sampleCntr);
          
	    Log.d("Citations","Fields Added");

        
       	Toast.makeText(getBaseContext(), 
                getString(R.string.bSuccessCitationCreation), 
                Toast.LENGTH_SHORT).show();
       	
	    Log.d("Citations","CitationCreated:"+rsNameTV.getText().toString()+":"+idSample+"|"+latPoint+":"+longPoint);

       	
       	removeAttributes();
       	
       	updateDisplay();
       	

	    
	    
       	return idSample;
   
    
    }
    
    /*
     * This method creates the project fields form.
     * For each field it creates a TextView with the field label and also another 
     * view that will be field with data provided by the user.
     * 
     * Type: 
     * 		simple -> EditText
     * 		thesaurus -> AutoCompleteEditText
     * 		bool -> CheckBox
     * 		complex -> Spinner or (dropdowList in other languages)
     * 
     */
    
    private void createFieldForm(long projId){
    	
    	
    	//list where items will the hold
    	
		   elementsList=new ArrayList<View>();
		   
		  DataTypeControler dtH=new DataTypeControler(this);

		  LinearLayout lPhoto=null;
		   
		   //main layout that will include the form
		   LinearLayout l= (LinearLayout)findViewById(R.id.fieldsLay);

		   
		   ResearchControler rsC=new ResearchControler(this);
		   
		   
		   rsC.loadProjectInfoById(projId);
		   
		   
		   //we load the Th reader that will help to create the autocomplete view with the items of the provided thName.

		   boolean thStatus=tC.initThReader(rsC.getThName());
		   
		   ArrayList<ProjectField> projFieldList=rsC.getProjFields(projId);
		   
		   Iterator<ProjectField> it=projFieldList.iterator();
		   
		   int i=0;
  
		   
		   //for each field we will create the TextView with the label and an "special" view where the user will provide the field value
		   

		   
		  while(it.hasNext()){
			  

			   LinearLayout lp=new LinearLayout(this);

			   TextView t=new TextView(getBaseContext());
			   
			   ProjectField att=it.next();
			   
			   String fieldType =att.getType();
			   
			   String fieldLabel= att.getLabel();
			   t.setText(fieldLabel);

			   lp.addView(t);
			   lp.setPadding(4, 4, 4, 4);


			   //when the field is Simple we create an EditText		   
			   
			   if (/*fieldType.equals("Real") || fieldType.equals("Enter") |*/ fieldType.equals("simple") /*|| fieldType.equals("string") || fieldType.equals("enter") || fieldType.equals("Text") ||  fieldType.equals("text") || fieldType.equals("String")*/){
				   
				   EditText e=(EditText) new EditText(getBaseContext());	   
				   
				   int idD= (int) att.getId();
				   String tag=att.getName();
				   
				   if(att.getValue().length()>0) e.setText(att.getValue());

				   e.setTag(tag);
				   e.setId(idD);
				   
				   e.setImeOptions(EditorInfo.IME_ACTION_NEXT);


				   e.setLayoutParams(new LayoutParams
					        (ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.
					                WRAP_CONTENT));
				  lp.addView(e);
				   elementsList.add(e);

				   
			   }
			   
			   //when the field is boolean we create a CheckBox
			   
			   else if (fieldType.equals("bool")){
				   
				   CheckBox e=(CheckBox) new CheckBox(getBaseContext());	   		   
				   int idD= (int) att.getId();
				   e.setId(idD);
				   
				   lp.addView(e);
				   elementsList.add(e);
			   }
			   
			   	//when the field is photo we show photo Interface
			   
			   else if (fieldType.equals("photo")){
				   
				   ImageView e=(ImageView) new ImageView(getBaseContext());
				   EditText et= (EditText) new EditText(getBaseContext());
				   
				   
				   et.setText(getText(R.string.photoMissing));
				   
				   int idD= (int) att.getId();
				   
				   photoTV=e;
				   
				   et.setId(idD);
				 
				   et.setTag(att.getName());
				   				   
				   etPhotoPath=et;
				   
				  et.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				 
				   photoButton=(ImageButton) new ImageButton(getBaseContext());
				   photoButton.setImageResource(android.R.drawable.ic_menu_camera);

				   photoButton.setOnClickListener(new OnClickListener() {

		                public void onClick(View v) {
		                  
		                	String rsName=rsNameTV.getText().toString();
		                	String currentTime = formatter.format(new Date());
		                	rsName=rsName.replace(" ", "_");
		                	
		                	fileName = rsName+currentTime+".jpg";
		                	
		                	//create parameters for Intent with filename
		                	ContentValues values = new ContentValues();
		                	values.put(MediaStore.Images.Media.TITLE, fileName);
		                	values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
		                	
		                	//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
		                	_path=Environment.getExternalStorageDirectory() + "/zamiaDroid/Photos/"+fileName;

		                  	File file = new File( _path );
	                	    imageUri = Uri.fromFile( file );
		                	
		                	//create new Intent
		                	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		 
	                	    intent.putExtra( MediaStore.EXTRA_OUTPUT, imageUri );		                	
		                	intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		                	startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

		                }
		        });
				   
				   lp.addView(et);

				   lPhoto=new LinearLayout(this);
				   lPhoto.setOrientation(LinearLayout.HORIZONTAL);
				   lPhoto.addView(photoButton);
				   lPhoto.addView(e);
		
				  // lp.addView(e);
				   elementsList.add(et);
			   
			   }
			   
			   //when the field is a thesaurus we create an AutoCompleteView and we fill it with the items provided by the ThesaurusControler
			   
			   else if(fieldType.equals("thesaurus")){
				   
				   String tag=att.getName();
				   
				   View e;
				   
				   if(thStatus){
					   

					   ThesaurusListAdapter elements = tC.fillData();
					   e=(AutoCompleteTextView) new AutoCompleteTextView(getBaseContext());	   

					  ((AutoCompleteTextView) e).setAdapter(elements);
					  

					   
				   }
				   else{
					   
					   e=(EditText) new EditText(getBaseContext());
					   
					   
				   }
				   
				   ((TextView) e).setImeOptions(EditorInfo.IME_ACTION_NEXT);

				   
				   
					  ((TextView) e).setHint(getString(R.string.thHint));
	  
					  e.setTag(tag);

				      
				      int idD= (int) att.getId();
					   e.setId(idD);
					   
				      
					   e.setLayoutParams(new LayoutParams
						        (ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.
						                WRAP_CONTENT));
					   
					  lp.addView(e);
					   elementsList.add(e);
			   }
			   
			 else{
				   
					
				 String[] items=dtH.getItemsbyFieldId(att.getId());
				 
				 Spinner e=(Spinner)new Spinner(this);
				 e.setPrompt(getString(R.string.chooseItem));
				 
				 
				  e.setLayoutParams(new LayoutParams
					        (ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.
					                WRAP_CONTENT));

						 ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
				                    android.R.layout.simple_spinner_item,items);
				
						  
						   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						   
						  
						   e.setAdapter(adapter);
						   
						   if(att.getValue().length()>0) setDefaultSpinnerItem(e,att.getValue(),items);
						   
						   int idD= (int) att.getId();
						   e.setId(idD);
						   e.setTag(att.getName());

						   
						  lp.addView(e);
						   elementsList.add(e);


	   
			   }
			   
			   if (fieldType.equals("photo")){ 
				   
				   LinearLayout ll= new LinearLayout(this);
				   ll.setOrientation(LinearLayout.VERTICAL);
				   ll.setPadding(3,3,3,3);

				   ll.addView(lp);
				   ll.addView(lPhoto);
				   ll.setBackgroundColor(0xff333333);
				   l.addView(ll);
			   }
			   else l.addView(lp,i);
			   
			   i++;
				
			   
			   
		   }
		  

		   
		  n=i;
		  
		if(elementsList.size()>1){
		  
			if(elementsList.get(n-1) instanceof TextView) ((TextView) elementsList.get(n-1)).setImeOptions(EditorInfo.IME_ACTION_DONE);
			
		}

		   
	   }
    
    /*
     * This method sets the default spinner item using defValue parameter provided.
     * If the spinner doesn't contain the value, spinner is not modified. 
     * 
     */
    	
    private void setDefaultSpinnerItem(Spinner e, String defaultValue, String[] items){
    
    	int n=items.length;
    	boolean found=false;
    	int pos=-1;
    	int i;
    	
    	for(i=0; i<n && !found;i++){
    		
    		if (items[i].compareTo(defaultValue)==0){ found=true; pos=i;}
    		
    	}
    	
    	if(found) e.setSelection(pos);
    	
    	
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
        	
        	//this case is not used because the UI was redesigned and attribute creation was mixed with sample capturing
        	//back from filled attributes
        	
           
            break;
            
        case 2 :
        	
        	//back from GPS Activity
        	
        	   if(intent!=null){
        	
		        	 Bundle ext = intent.getExtras();
		        	
		        	lat= ext.getDouble("latitude");
		        	longitude= ext.getDouble("longitude");
		        	elevation= ext.getDouble("elevation");
		
		
		
		        	//it can be 0 and 0 when it's located in the south of nigeria. It has to be improved.
		        	if (lat==0 || longitude ==0){
		        		
		            	Toast.makeText(getBaseContext(), 
		            			getBaseContext().getString(R.string.gps_missing), 
		                        Toast.LENGTH_SHORT).show();
		            	
		            	
		        	}
		        	
		        	else{
		        		
		        		pm.setGPSNeeded(true);
		
		        		 updateDisplay();
		        		
		        		
		        	}
        	
        	   }
        	
        	
        	break;
        	
        case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE :
        	
        	 if (resultCode == RESULT_OK) {
        		   
        		// thumbnail = (Bitmap) intent.getExtras().get("data");  
        		 
        		 etPhotoPath.setText(_path);
        		 
        		  BitmapFactory.Options options = new BitmapFactory.Options();
        		    options.inSampleSize = 5;
        		    


        		    Bitmap PhotoFromCamera = BitmapFactory.decodeFile( _path, options );

    				MediaStore.Images.Media.insertImage(getContentResolver(), PhotoFromCamera, "fileName", "");


        		 
        		/* Bundle b = intent.getExtras();
        			if (b != null && b.containsKey(MediaStore.EXTRA_OUTPUT)) { // large image?
        		        
        		       	Toast.makeText(getBaseContext(), 
        		                "Large Image", 
        		                Toast.LENGTH_SHORT).show();
        		       	
        			        // Shouldn't have to do this ... but
        				
        				
        			} else {
        				
        				Toast.makeText(getBaseContext(), 
        		                "Small Image", 
        		                Toast.LENGTH_SHORT).show();
        				
        				
        				MediaStore.Images.Media.insertImage(getContentResolver(), thumbnail, timestamp, timestamp);
        			}*/
        			
    				photoButton.setVisibility(View.INVISIBLE);
        			photoTV.setImageBitmap(PhotoFromCamera);
        		
        	        photoTV.setScaleType(ScaleType.CENTER);
        	        
        
        			
        			
        			break;

        			

        	    } else if (resultCode == RESULT_CANCELED) {
        	        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
        	    } else {
        	        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
        	    }

        	
        	break;
            
            default:
            	
            	
  
        }
        

    }
    
}

    

    