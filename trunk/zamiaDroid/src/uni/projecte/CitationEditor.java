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

import uni.projecte.controler.DataTypeControler;
import uni.projecte.controler.ResearchControler;
import uni.projecte.controler.SampleControler;
import uni.projecte.controler.ThesaurusControler;
import uni.projecte.controler.ThesaurusControler.ThesaurusListAdapter;
import uni.projecte.dataLayer.bd.AttributeDbAdapter;
import uni.projecte.dataTypes.AttributeValue;
import uni.projecte.dataTypes.ProjectField;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.ub.bio.biogeolib.CoordConverter;
import edu.ub.bio.biogeolib.CoordinateLatLon;
import edu.ub.bio.biogeolib.CoordinateUTM;

public class CitationEditor extends Activity {

	
	/*dataBase helpers*/
	

	   private AttributeDbAdapter mDbAttributeType;
	   private ResearchControler rsCont;
	   private  ThesaurusControler tC;
	   private SampleControler sC;
	   

	   private ListView attListView;
	   public final static int SUCCESS_RETURN_CODE = 1;
	   
	   
	   private long idSample=-1;
	   private int numAttr;
	   

	   private ArrayList<String> attList;
	   private TextView txtName;
	   private ArrayList<AttributeValue> attValuesList;
	   
	   private TextView mDateDisplay;
	   private TextView mLocationDisplay;
	   
	   private Button bModifyCitation;
	   
	   private ArrayList<String> formValues;
	   


		private ArrayList<View> elementsList;
		private int n;
		
		private static final int REMOVE_CITATION = Menu.FIRST;
		private static final int SHOW_MAP =Menu.FIRST+1;




 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.citationeditor);
        
        /*DB access*/
        
        rsCont=new ResearchControler(this);
        
        
        mDbAttributeType= new AttributeDbAdapter(this);
        
        
        sC=new SampleControler(this);
        
        tC= new ThesaurusControler(this);
        
        bModifyCitation = (Button)findViewById(R.id.bModifySample);
        bModifyCitation.setOnClickListener(bModifyCitationListener);

        
        /* button Listeners*/ 
        
        
        mDateDisplay = (TextView) findViewById(R.id.citationDate);
        mLocationDisplay = (TextView) findViewById(R.id.tvLocation);

        
        txtName = (TextView)findViewById(R.id.projectName);

              
        // we assign the adapter to the researchList
        long previousRs=getIntent().getExtras().getLong("id");
        
        idSample=getIntent().getExtras().getLong("idSample");

        
        if (previousRs!=-1){
        	
        	/*in case that we have chosen another research in the past*/
        	        	
        	rsCont.loadProjectInfoById(previousRs);
        	
        	txtName.setText(rsCont.getName());
        	
        	createForm(previousRs);
        	
        }
        
        updateDisplay();
        
        
        


    }

    
    public boolean onCreateOptionsMenu(Menu menu) {
    	

    	menu.add(0, REMOVE_CITATION, 0,R.string.mRemoveCitation).setIcon(android.R.drawable.ic_menu_delete);
    	menu.add(0, SHOW_MAP, 0,R.string.mShowUniqueCitation).setIcon(android.R.drawable.ic_menu_mapmode);

    	return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
    		
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	
	    	builder.setMessage(R.string.backFromCitationEditor)
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


    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	
    	
		switch (item.getItemId()) {
		case REMOVE_CITATION:
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	
	    	
	    	builder.setMessage(R.string.deleteCitationQuestion)
	    	       .setCancelable(false)
	    	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    
	    	        	removeSample();
	    	        	    	        	   
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
			
		case SHOW_MAP:
			
			
				showMap();

		
	        break;
	        
		}
		return super.onOptionsItemSelected(item);
	}
    
	private void showMap() {
		
		Intent myIntent = new Intent(this, CitationMap.class);
    	myIntent.putExtra("idSample", idSample);
    	myIntent.putExtra("id", rsCont.getIdRs());


        startActivityForResult(myIntent, 0);
		
		
	}	    
   
	
	private void removeSample() {
		
		   SampleControler sC=new SampleControler(this);
		   sC.deleteCitation(idSample);
		   finish();

		
	}
    
    private boolean emptyThesaurus(){
    	
    	
    	boolean notEmpty=true;
    	
    	
    	
    	//are all the attributes filled?
    	
    	for (int i=0;i<n;i++){
    		
    		View vi=elementsList.get(i);
    		
    		if (vi instanceof EditText){
				
    		//	if (((EditText)vi).length()==0) notEmpty=false;	
			}
    		
    		else if((vi instanceof AutoCompleteTextView)){
    			
    			if (((AutoCompleteTextView)vi).length()==0) notEmpty=false;	

    		}
			
    		
    		else if((vi instanceof CheckBox)){
    			
    			
    			//rubish
    		}
			
			else {
				
				//if (((Spinner)vi).getSelectedItem().toString().length()==0) notEmpty=false;	
				
			}
    		
    	   		
    		
    	}
    	
    	return notEmpty;
    	
    }
    
    private OnClickListener bModifyCitationListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
            
        	if (!emptyThesaurus()){
        	    	
        	    	 Toast.makeText(getBaseContext(), 
        	                R.string.fieldMissing, 
        	                 Toast.LENGTH_SHORT).show();
        	    	
        	    }
        	  
        	  else{
        		 
        			if(txtName.length()==0){
        	    		
        	    		Toast.makeText(getBaseContext(), 
        	                    R.string.projNameMissing, 
        	                    Toast.LENGTH_SHORT).show();
        	    	}
        	    	
        	    	else  {
        	    		
        	    		modifyAttributes(idSample, sC);
        	    		
        	    	      String toastText=v.getContext().getString(R.string.tModifiedCitation);
	    	                 
	    	                 Toast.makeText(getBaseContext(), 
	    	        	                toastText, 
	    	        	                 Toast.LENGTH_SHORT).show();
        	    		
        	    		finish();
        	    			
        	    		}
        	    		
        	    		
        	          
        	    		        	    	
        	    	}

        	  }
        	
    };
    

    
    private void modifyAttributes(long idSample, SampleControler smplCntr){
    	
		
		n=elementsList.size();
		
		smplCntr.startTransaction();
		
		
		for (int i=0;i<n;i++){
			
			String value="";
			
			View et=elementsList.get(i);
							
			if (et instanceof EditText){
				value= ((TextView) et).getText().toString();
			}
			
			else if(et instanceof CheckBox){
				
				if (((CheckBox) et).isChecked()) value="true";
				else value="false";
				
			}
			else if(et instanceof TextView){
				
				value =((TextView) et).getText().toString();
			}
			
			else {
				
				value =((Spinner) et).getSelectedItem().toString();

				
			}
			
			int id= et.getId();

			if(value.equals(formValues.get(i))){
				
				
			}
			else smplCntr.updateCitationField(idSample, id, value);
			
			

		}
		
		smplCntr.EndTransaction();



	
}
    
    
 
    private void updateDisplay() {
    	
    	sC.loadCitation(idSample);

	     
	     SharedPreferences settings = getSharedPreferences("uni.projecte_preferences", 0);
	     String coorSystem = settings.getString("listPrefCoord", "UTM");
	     
    	 
	    	Double lat=sC.getLatitude();
	    	Double longitude=sC.getLongitude();
	     
	    	
	    //no location 	
	 	if(lat>90 || longitude>180){
    		
	    	mLocationDisplay.setText(R.string.noGPS);

    	}
	 	else{

		     if(coorSystem.equals("UTM")){
		    	 
		         CoordinateUTM utm = CoordConverter.getInstance().toUTM(new CoordinateLatLon(lat,longitude));
			     mLocationDisplay.setText(utm.getShortForm());
	
		     }
		     else {
	
			    mLocationDisplay.setText(lat.toString().subSequence(0, 7)+" - "+longitude.toString().subSequence(0, 7));
	
		     }
		     
	 	}
	     
        mDateDisplay.setText(sC.getDate());
        
        
    }

    
    private void updateField(int idAtt){
    	
    	View v=findViewById(idAtt); 
    	String newValue;
    	
    	if (v instanceof EditText){
			newValue= ((TextView) v).getText().toString();
		}
		
		else if(v instanceof CheckBox){
			
			if (((CheckBox) v).isChecked()) newValue="true";
			else newValue="false";
			
		}
		
		else {
			
			newValue =((Spinner) v).getSelectedItem().toString();

			
		}

    	sC.updateCitationField(this.idSample,idAtt,newValue);
    	
    	
    	
    }
 
    
    @SuppressWarnings("unused")
	private OnClickListener updateSampleFieldListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
            
        	int idAtt= (Integer) v.getTag();
          	 updateField(idAtt);
          	 
          	 Button bt=(Button) v;
          //	 bt.setBackgroundResource(R.color.solid_green); 
          	
        }
    }; 
    
    
    private void createForm(long id){
		   
		   elementsList=new ArrayList<View>();
		   formValues=new ArrayList<String>();

		   LinearLayout l= (LinearLayout)findViewById(R.id.atributsS);
		   

		   ResearchControler rsC=new ResearchControler(this);
		   rsC.loadProjectInfoById(id);
		   sC.startTransaction();
		   
		   
		   boolean thStatus=tC.initThReader(rsC.getThName());

		   
		   DataTypeControler dtH=new DataTypeControler(this);
		   
		   ArrayList<ProjectField> attList=rsC.getAllProjectFields(id);		   
		   Iterator<ProjectField> it=attList.iterator();
		   
		   int i=0;
		   
		  while(it.hasNext()){
			  
			   LinearLayout lp=new LinearLayout(this);
			   
			   TextView t=new TextView(getBaseContext());
			   
			   ProjectField att=it.next();
			   
			   String tipus =att.getType();
			   String titol= att.getLabel();
			   t.setText(titol);

			   //t.setWidth(LayoutParams.WRAP_CONTENT);
			   
			   lp.addView(t);
			   lp.setPadding(4, 4, 4, 4);

			   //lp.setBackgroundColor(Color.argb(120, 120, 120, 120));
		   
			   //simple types
			   if (tipus.equals("simple")){
				   
				   EditText e=(EditText) new EditText(getBaseContext());	   
				   
				   int idD= (int) att.getId();
				   e.setId(idD);
				   
				   String pred=sC.getFieldValue(idSample,att.getId());
				   
				   if(pred!=null && pred.length()>0) {
					   e.setText(pred);
					   formValues.add(pred);
				   }
				   else{
					   
					   formValues.add("");

				   }
				   
				   e.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				   e.setLayoutParams(new LayoutParams
					        (ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.
					                WRAP_CONTENT));
				   
				  lp.addView(e);
				   elementsList.add(e);

				   
			   }
			   
			   else if (tipus.equals("photo")){
				   
				   TextView e=(TextView) new TextView(getBaseContext());	   
				   
				   int idD= (int) att.getId();
				   e.setId(idD);
				   
				   String pred=sC.getFieldValue(idSample,att.getId());
				   
				   if(pred!=null && pred.length()>0) {
					   String predSpaced="  ";
					   e.setText(Html.fromHtml(predSpaced+"<b>"+pred+"</b>"));
					   formValues.add(pred);
				   }
				   else{
					   
					   formValues.add("");

				   }
				   
				   e.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				   e.setLayoutParams(new LayoutParams
					        (ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.
					                WRAP_CONTENT));
				   
				   
				  lp.addView(e);
				   elementsList.add(e);

				   
			   }
			   
			   
			   else if (tipus.equals("bool")){
				   
				   CheckBox e=(CheckBox) new CheckBox(getBaseContext());	   
				   
				   int idD= (int) att.getId();
				   e.setId(idD);
				   e.setLayoutParams(new LayoutParams
					        (ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.
					                WRAP_CONTENT));
				   
				  lp.addView(e);
				   elementsList.add(e);
			   }
			   
			   else if(tipus.equals("thesaurus")){
				   
				   View e;
				   
				   if(thStatus){
					   
					   ThesaurusListAdapter thItems = tC.fillData();
	
					   e=(AutoCompleteTextView) new AutoCompleteTextView(getBaseContext());	
					   
					    ((AutoCompleteTextView) e).setAdapter(thItems);
				   
				   }
				   
				   else{
					   
					   e=(EditText) new EditText(getBaseContext());
					   
				   }
				   
				   int idD= (int) att.getId();
				   e.setId(idD);
				  ((TextView) e).setImeOptions(EditorInfo.IME_ACTION_NEXT);
				   
				      e.setLayoutParams(new LayoutParams
						        (ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.
						                WRAP_CONTENT));

				      ((TextView) e).setHint(R.string.taxonHint);
				      
					   String pred=sC.getFieldValue(idSample,att.getId());
					   
					  if(pred.length()>0){ 
						  
						  ((TextView) e).setText(pred);
						  formValues.add(pred);
			   		}
			   			else{
				   
			   			formValues.add("");

			   		}

					  lp.addView(e, LayoutParams.FILL_PARENT);
					  
					  
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
						   
				   String pred=sC.getFieldValue(idSample,att.getId());
						   
					if(pred!=null && pred.length()>0) {
						setDefaultSpinnerItem(e,pred,items);
						formValues.add(pred);
			   		}
			   			else{
				   
			   			formValues.add("");

			   		}
				
						  
						   int idD= (int) att.getId();
						   e.setId(idD);
						   
						  lp.addView(e);
						  elementsList.add(e);

			   }
			   
		   
			   l.addView(lp,i);
			   i++;
				
		   }
		   
		  n=i;
		  sC.EndTransaction();
		  
			if(n>=1) elementsList.get(1).requestFocus();


		   
	   }
    	
    private void setDefaultSpinnerItem(Spinner e, String defaultValue, String[] items){
    
    	int n=items.length;
    	boolean trobat=false;
    	int pos=-1;
    	int i;
    	
    	for(i=0; i<n && !trobat;i++){
    		
    		if (items[i].compareTo(defaultValue)==0){ trobat=true; pos=i;}
    		
    	}
    	
    	if(trobat) e.setSelection(pos);    	
    	
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        
        if(intent!=null){
        	
        	
        switch(requestCode) {
        case 0 :
            
        	//back from research choice
        	
        /*	nomEstudi = extras.getString(KEY_NOM);
            idRs=extras.getLong(KEY_ID);
            desc=extras.getString(DESCRIPCIO);

            txtName.setText(nomEstudi);*/
            
            break;
            
            
        case 1 :
        	
        	//back from filled attributes
        	
        	 if(intent!=null){
        	
        	 Bundle extras = intent.getExtras();
        	
        	
            numAttr= extras.getInt("nAttributes");
            
           
           
	            mDbAttributeType.open();
	
	            
	            attValuesList=new ArrayList<AttributeValue>();
	            
	            //we get value-attName pairs from Attribute Activity
	
	            for (int i=0; i<numAttr; i++){
	        	
	            	int id= extras.getInt("Id"+i);
	            	Cursor c=mDbAttributeType.fetchAtribute(id);
	            	c.moveToFirst();
	            	
	            	attList.add(c.getString(2)+" : "+extras.getString("attId"+i));    	
	            	attValuesList.add(new AttributeValue((int) extras.getDouble("Id"+i),extras.getString("attId"+i)));
	            	
	            	
	            }
	            
	            //fill the list
	        	
	        	attListView.setAdapter(new ArrayAdapter<String>(this, R.layout.atrib_row, attList));
	        
	            mDbAttributeType.close();
	            
            }
           
            break;
            
       
     
        }
        

        }
    }
    
}

    

    