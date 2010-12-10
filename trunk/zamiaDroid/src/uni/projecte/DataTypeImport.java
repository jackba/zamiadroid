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

import uni.projecte.controler.DataTypeControler;
import uni.projecte.controler.PreferencesControler;
import uni.projecte.controler.ThesaurusControler;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DataTypeImport extends Activity implements
RadioGroup.OnCheckedChangeListener {
	
	
	public static final String PREFS_NAME = "darreraSincro";
	private PreferencesControler pC;


	   private int idLoc;

	  private RadioButton local;
	  private RadioButton internet;
	  
	   private List<String> elements = null;
	   private ListView fileList;
	   
	   private String url;
	   private long thId;
	   
	   private ProgressDialog pd;
	   
	   String urlThFile;
	   String thName;
	   
	   
	
	    @Override
	   public void onCreate(Bundle state) {
	        super.onCreate(state);
	        
	        setContentView(R.layout.datatypeimport);
	      //  mDbHelper = new SamplesDbAdapter(this);
	       // mDbHelper.open();
	        
	    //    btimport=(Button)findViewById(R.id.btImportDataTypes);
	      //  btimport.setOnClickListener(bSincroListener);

	     //   importInfo=(TextView)findViewById(R.id.tvImportInfoDT);

	      //  llista = (ListView)findViewById(R.id.exportListDT);
	       // exportLogList= new ArrayList<String>();
	        
	       // urlImport=(EditText)findViewById(R.id.etUrLimportDT);
	        
	        //urlImport.setText("http://davidmapi.eu/pfc/api/utoPiC/dataType/");
	        
	      //  mRadioGroup = (RadioGroup)  findViewById(R.id.groupTypeImportDT);
	        
	       // mRadioGroup.setOnCheckedChangeListener(this); 
	          
	       // local = (RadioButton) findViewById(R.id.cBLocalDT);
	       // internet = (RadioButton) findViewById(R.id.cBInternetDT);
	        
	       //idLoc=local.getId();
	     ///   idInternet=internet.getId();
	        
	        
	        //local.setChecked(true);
	
	        
	        pC=new PreferencesControler(this);
	        
	        fileList = (ListView)findViewById(R.id.thList);

	        
	        if(isSdPresent()) fillFileList(new File("/sdcard/"+pC.getDefaultPath()+"/Thesaurus/").listFiles(new XMLFilter()));
	        else {
	        	
	        	Toast.makeText(getBaseContext(), R.string.noSdAlert, Toast.LENGTH_SHORT).show();
	        	
	        }
	        
	        fileList.setOnItemClickListener(theListListener);
	        
	   }
	   @Override
	    protected void onStop(){
	       super.onStop();
	    
	      // Save user preferences. We need an Editor object to
	      // make changes. All objects are from android.context.Context
	    
	    }
	   
	   protected void onRestart(){
		   
		   if(isSdPresent()) fillFileList(new File("/sdcard/"+pC.getDefaultPath()+"/Thesaurus/").listFiles(new XMLFilter()));
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
	   
	 
	      class XMLFilter implements FilenameFilter {
	    	  
	    	  
	  	              public boolean accept(File dir, String name) {
	  
	                      return (name.endsWith(".xml"));
	  
	              }
	  
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
	            	   
	            	   //AlertDialog.Builder builder= new AlertDialog.Builder(v.getContext());
	            	   

    		            url= archivo.getAbsolutePath();
    		            
    		            String name=archivo.getName();
    		            
    		            name=name.replace(".xml", "");

    		            createThDialog(name);   
	            	   
	  
	    	
	    }
	    
	    
	    }
	    }   
	
	    };
	    
	    
	   
		private void createThDialog(String fileName) {
	        
		  	final Dialog dialog;
	        
	  		        	
	        	//Context mContext = getApplicationContext();
	    	   	dialog = new Dialog(this);
	    	   	
	        	dialog.setContentView(R.layout.thesauruscreation);
	    	   	dialog.setTitle(R.string.thName);
	    	   	
	    	   	Button createProject = (Button)dialog.findViewById(R.id.bCreateTh);
	    		
	    	   	EditText name=(EditText)dialog.findViewById(R.id.etNameItem);
	    	   	name.setText(fileName);
	    	   	
	    	   	
	    	    createProject.setOnClickListener(new Button.OnClickListener(){
	    	    	             
	    	    	
	    	    	public void onClick(View v)
	    	    	              {

	    	    	                 EditText et=(EditText)dialog.findViewById(R.id.etNameItem);
	    	    	                 
	    	    	                 thName=et.getText().toString();
	    	    	                 urlThFile=url;
	    	    	           
	    	    	                 ThesaurusControler thCntr= new ThesaurusControler(v.getContext());
	    	    	                 thId=thCntr.createThesaurus(thName);
	    	    	                 
	    	    	                 if(thId>0){
	    	    	                	 
	    	    	                     importTh();
		    	    	                 dialog.dismiss();
		    	    	                 
	    	    	                	 
	    	    	                 }
	    	    	                 else{
	    	    	                	 
		    	    	                String sameTh=getBaseContext().getString(R.string.sameThName);

	    	    	                 	Toast.makeText(getBaseContext(), 
			    	    	   		              sameTh+" "+thName, 
			    	    	   		              Toast.LENGTH_LONG).show();
			    	    	                	
	    	    	                	 
	    	    	                 }
	    	    	                 
	    	    	      
	    	    	                 
	    	    	        
	    	    	            	 
	    	    	              }
	    	    	             
	    	    });
	    	    
	    	    dialog.show();


	    	 
	    } 

	 private void importTh() {
		 
		 pd = ProgressDialog.show(this, getString(R.string.thLoading), getString(R.string.thLoadingTxt), true,false);
		 
		                 Thread thread = new Thread(){
		  	        	   
		  	        	   
			                 public void run() {
			               	  
			               	  
			                	importThThread(); 
			               	  
			                 }
			           };
			           
			           
		   thread.start();
		 
		}
	 
	 private void importThThread(){
		 
		 
		 ThesaurusControler thCntr= new ThesaurusControler(this);
		 
		 thName=thName.replace(".", "_");

		 boolean error=thCntr.addThItems(thId,thName, url);

		if(!error) handler.sendEmptyMessage(0);
		else handler.sendEmptyMessage(1);
		 


		 
	 }
	 
	 
	 
	 private Handler handler = new Handler() {

			public void handleMessage(Message msg) {
				
				
				pd.dismiss();
				
				if (msg.what==0){
					
					Intent intent = new Intent();
			    	
					Bundle b = new Bundle();
					b.putString("thName", thName);
								
					intent.putExtras(b);

					setResult(1, intent);
					
				}
				
				else{
					
				  	Toast.makeText(getBaseContext(), 
  	   		              "Wrong File", 
  	   		              Toast.LENGTH_LONG).show();
					
				}
				
		
		        finish();


			}
		};
	 
	 
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
	        
		   	if (checkedId==idLoc) internet.setChecked(false);
		   		
		   	else local.setChecked(false);

	   }


	   @SuppressWarnings("unused")
	private OnClickListener bSincroListener = new OnClickListener()
	    {
	        public void onClick(View v)
	        {                        
	        	
	        	
	        	DataTypeControler dtH= new DataTypeControler(v.getContext());
	        	
	       // 	RadioButton cb= (RadioButton)findViewById(R.id.cBInternetDT);
	        	

	        		        	
	        	
	        /*	if (!cb.isChecked()){
	        		
	        		//rXML.readXML(v.getContext(),"researchtypes.xml",false);
	        		
	        		dtH.parseLocalDataTypes();
	        	
	        	}
	        	
	        	else{
	        		
	        		dtH.parseRemoteDataTypes((String)urlImport.getText().toString());
	        		//dtH.clearDTdb();
	        		
	        		
	        	}
	    		
	    		Toast.makeText(getBaseContext(), 
	                    "S'han importat "+dtH.getImportedItems()+" DataTypes", 
	                    Toast.LENGTH_SHORT).show();
	        	
	        	//importData2DB();
	        	
	    		 finish();
*/
	        
	        }
	        
	        	

			
	    };
	   
	    
    
	/*    private void importData2DB() {

	    	Enumeration<Research> list=rsH.getResearches();
	    	
	    	mDbHelper= new ResearchDbAdapter(this);
	    	mDbHelperAtt=new AttributeDbAdapter(this);
	    	
	    	mDbHelper.open();
	    	mDbHelperAtt.open();
	    	
	    	int numImportats=0;
	    	
	    	
	    	while(list.hasMoreElements() ){
				
				Research rs=list.nextElement();
				
				Cursor c=mDbHelper.fetchResearchByName(rs.getName());
				
				if(c.getCount()!=0){
					
			    	exportLogList.add(rs.getName()+" ja és a la base de dades");

					
				}
				
				else{
					
			    	exportLogList.add(rs.getName()+" exportat amb èxit");
	
					numImportats++;

					
					long researchNum=(int) mDbHelper.createResearch(rs.getName(), rs.getDescription());
					
					int n=rs.getAtributtes().size();
					
					for (int i=0;i<n;i++){
						
						
						mDbHelperAtt.createAttribute(researchNum, rs.getAtributtes().get(i).getName(), rs.getAtributtes().get(i).getType());
						
					}
				}
				
			}
	    	
	    	importInfo.setText("S'han importat correctament "+numImportats+" estudis");
	    	
	    	
	    	llista.setAdapter(new ArrayAdapter<String>(this, R.layout.atrib_row, exportLogList));
	    	mDbHelper.close();
	    	mDbHelperAtt.close();
	    	
		}
	*/
}
