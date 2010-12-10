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

import uni.projecte.controler.ResearchControler;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ResearchImport extends Activity implements


RadioGroup.OnCheckedChangeListener{
	
	

	   private TextView importInfo;

	   private Button btimport;

	   private EditText urlImport;
	   
	   private ListView llista;
	   private ArrayList <String> exportLogList;

	   
	   private ResearchControler rsH;
	   
	   private RadioGroup mRadioGroup;

	   private int idLoc;
	 //  private int idInternet=-1;
	   
	  private RadioButton local;
	   private RadioButton internet;
	
	    @Override
	   public void onCreate(Bundle state) {
	        super.onCreate(state);
	        
	        setContentView(R.layout.resarchimport);

	        
	        btimport=(Button)findViewById(R.id.btRsImport);
	        btimport.setOnClickListener(bImportListener);

	        importInfo=(TextView)findViewById(R.id.tvImportInfo);

	        llista = (ListView)findViewById(R.id.exportList);
	        exportLogList= new ArrayList<String>();
	        
	        urlImport=(EditText)findViewById(R.id.etUrLimport);
	        
	        urlImport.setText("http://pfc.adriavalles.net/api/utoPiC/research/");
	        
	        mRadioGroup = (RadioGroup)  findViewById(R.id.groupTypeImport);
	        
	        mRadioGroup.setOnCheckedChangeListener(this); 
	          
	        local = (RadioButton) findViewById(R.id.cBLocal);
	        internet = (RadioButton) findViewById(R.id.cBInternet);
	        
	        idLoc=local.getId();
	       // idInternet=internet.getId();
	        
	        
	        local.setChecked(true);


	        
	   }
	   @Override
	    protected void onStop(){
	       super.onStop();
	    
	      // Save user preferences. We need an Editor object to
	      // make changes. All objects are from android.context.Context
	    
	    }
	   
	   

	   public void onCheckedChanged(RadioGroup arg0, int checkedId) {
	        
		   	if (checkedId==idLoc) internet.setChecked(false);
		   		
		   	else local.setChecked(false);

	   }


	   private OnClickListener bImportListener = new OnClickListener()
	    {
	        public void onClick(View v)
	        {                        
	        	
	        	rsH=new ResearchControler(v.getContext());
	        	
	        	
	        	
	        	RadioButton cb= (RadioButton)findViewById(R.id.cBInternet);
	        	
	        	int importedRs;
	        		        	
	        	
	        	if (!cb.isChecked()){
	        		
	        			importedRs=rsH.readXML("researchtypes.xml",false,exportLogList);	
	        	
	        	}
	        	
	        	else{
	        		
	        		importedRs=rsH.readXML((String)urlImport.getText().toString(),true,exportLogList);
	        		
	        	}
	        	
	        	
	        	importInfo.setText("S'han importat correctament "+importedRs+" estudis");
	        	
	        	Toast.makeText(getBaseContext(), 
	        			"S'han importat correctament "+importedRs+" estudis", 
                        Toast.LENGTH_SHORT).show();
		    	
		    	setAdapter();
	        	
	        }

			
	    };
	    
	    private void setAdapter(){
	    	
	    	llista.setAdapter(new ArrayAdapter<String>(this, R.layout.atrib_row, exportLogList));

	    	
	    }
	   
	    
   
	   
	
}
