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

package uni.projecte.controler;

import uni.projecte.R;
import uni.projecte.dataLayer.bd.ThesaurusIndexDbAdapter;
import uni.projecte.dataLayer.bd.ThesaurusItemsDbAdapter;
import uni.projecte.dataLayer.xml.ThesaurusXMLparser;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class ThesaurusControler {
	
	public final String defaultTH = "Flora";

	Context c;
	
	private static ThesaurusItemsDbAdapter thDbAdapter;
	private ThesaurusIndexDbAdapter thIndexAdapter;
	private ThesaurusXMLparser thXMLp;
	public int numElem;
	public Cursor allTc;
	
	
	public ThesaurusControler(Context c){
		
		this.c=c;
		
	}
	
	public void closeCursors(){
		
		if(allTc!=null) allTc.close();
		
	}
	
	public boolean initThReader(String thName){

		
		if (thName==null || thName.compareTo("")==0) {
			
			return false;
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																						
		}
		
		else{
			

			thIndexAdapter= new ThesaurusIndexDbAdapter(c);
			thIndexAdapter.open();
		
			Cursor cursor=thIndexAdapter.fetchThbyName(thName);
			cursor.moveToFirst();
			
			String tableName=cursor.getString(2);
			
			thIndexAdapter.close();
	
			cursor.close();
			
			thDbAdapter= new ThesaurusItemsDbAdapter(c);
			
			thDbAdapter.open(tableName);
			
			return true;
		
		}
		
	}
	
	public int removeTh(String thName){
		
		//its necessary to check if any project needs this Th
		
		
		ResearchControler rsC= new ResearchControler(c);
		
		if(!rsC.isUsed(thName)){ 

			thIndexAdapter= new ThesaurusIndexDbAdapter(c);
			thIndexAdapter.open();
			
			Cursor index=thIndexAdapter.fetchThbyName(thName);
			index.moveToFirst();
			String dbName=index.getString(2);
			index.close();
			
			thIndexAdapter.removeThbyName(thName);
	
			thIndexAdapter.close();
			
			
			thDbAdapter= new ThesaurusItemsDbAdapter(c);
	    	thDbAdapter.open(dbName);
	    	
	    	thDbAdapter.dropTable(dbName);
	 
		    	
			thDbAdapter.close();
			
			return 0;
		
		}
		
		else {
			
			return -1;
			
		}
		
		
		
		
	}
	
	public void closeThReader(){
		
		thDbAdapter.close();

		
	}
	
	
	public Cursor fetchAllThesaurus(){
		
		thIndexAdapter= new ThesaurusIndexDbAdapter(c);
		thIndexAdapter.open();
		
		Cursor c=thIndexAdapter.fetchAllTH();
		c.moveToFirst();
		
		thIndexAdapter.close();
		
		return c;
		
	}
	
	public Cursor fetchThesaurusItembyName(String name){
		
		String[] spec=name.toString().split(" ");
 
		Cursor c;
 	    
 	    if(spec.length>2){
 	    
		
 	    	c=thDbAdapter.fetchTbhItem(spec[0], spec[1], spec[2]);
		
 	    }
 	    
 	    else {
 	    	
 	    	c=thDbAdapter.fetchTbhItem(spec[0], spec[1], "");

 	    	
 	    }
 	    
		c.moveToFirst();
		
		
		return c;
		
	}
	
	public String fetchThesaurusSynonymous(String icode){
		
 
		Cursor c;
 	    
 	    c=thDbAdapter.fetchSynonymous(icode);

		c.moveToFirst();
		
		String completeName=c.getString(1)+" "+c.getString(2)+" "+c.getString(3);
		
		c.close();
		
		return completeName;
		
	}
	
	public Cursor getThInfo(String thName){
		
		thIndexAdapter= new ThesaurusIndexDbAdapter(c);
		thIndexAdapter.open();
		
		Cursor c=thIndexAdapter.fetchThbyName(thName);
		c.moveToFirst();
		
		thIndexAdapter.close();
		
		return c;
		
		
	}
	
	public String[] getThList(){
		
		thIndexAdapter= new ThesaurusIndexDbAdapter(c);
		thIndexAdapter.open();
		
		Cursor c=thIndexAdapter.fetchAllTH();
		c.moveToFirst();
		
		int n= c.getCount();
		
		String[] resultList=new String[n];
		
		for(int i=0; i<n;i++){
			
			
			resultList[i]=c.getString(1);
			c.moveToNext();
			
			
		}
		
		c.close();
		thIndexAdapter.close();
		
		
		return resultList;
			
		
		
	}
	
	public long createThesaurus(String name){
		
		String dbName=name.replace(" ", "_");
		
		thIndexAdapter = new ThesaurusIndexDbAdapter(c);
		thIndexAdapter.open();
	    	
		long thId=thIndexAdapter.createTh(name, dbName,0);
		
		thIndexAdapter.close();  
		
		return thId;
		
		
	}
	
	public boolean addThItems(long thId,String name, String url){
		
		String dbName=name.replace(" ", "_");

		thDbAdapter= new ThesaurusItemsDbAdapter(c);
	    thDbAdapter.open(dbName);
	 
				thXMLp= new ThesaurusXMLparser(this);
		    	thXMLp.readXML(c, url, false);
		    	
		    	boolean error=thXMLp.isError();
		    	
		    if(!error){
		    	
					thDbAdapter.endTransaction();
					
					
					
					Cursor curItems= thDbAdapter.fetchNumAllItems();
			    	curItems.moveToFirst();
			    	numElem=curItems.getCount();
			    	curItems.close();
	
			thDbAdapter.close();
			
			thIndexAdapter = new ThesaurusIndexDbAdapter(c);
			thIndexAdapter.open();
		
			thIndexAdapter.updateThCount(thId,numElem);
	
			thIndexAdapter.close();  
			
		    }
		    
		    else{
		    	
		    	thDbAdapter.dropTable(dbName);
		    	
				thDbAdapter.close();
		    	
		    	
		    }

		    return error;
		
	}
	
	
	public Cursor fetchAllTh(){
		
		thIndexAdapter = new ThesaurusIndexDbAdapter(c);
	

		Cursor list=thIndexAdapter.fetchAllTH();
		
		thIndexAdapter.close();

		return list;
		
	}
	
    /*public ArrayList<String> ReadSettings(Context context){ 
    	
    InputStream fIn = null; 
   	 InputStreamReader isr = null;
   	 String line;
   	 
   	 char[] inputBuffer = new char[255]; 
   	 String data = null;
   	 ArrayList<String> array=new ArrayList<String>();
   
   	 try{
   		 
   		 
   		 //	fIn = context.openFileInput("thesaurus-taxons.sec");       		 
   		 	
   		 	
   		 //	isr = new InputStreamReader(fIn); 
   		 	
   		 
   		fIn=context.getAssets().open("thesaurus-taxons.sec");
   		
   		isr = new InputStreamReader(fIn);
   		
   		 BufferedReader stdin = new BufferedReader(isr);
   		 	
   		 int id;
   		 	
	         isr.read(inputBuffer); 
	         
	         while ((line = stdin.readLine()) != null)
	         {
	        	 
	        	 id=line.indexOf("#");
	          
	        	 if (id!=-1){
	        		 
		        	 array.add(line.substring(id+1));

	        	 }
	          
	         }
	         
	         int size = array.size();
	         
         Toast.makeText(context, "Settings read",Toast.LENGTH_SHORT).show();
         
         Toast.makeText(context, size + " plantetes llegides",Toast.LENGTH_SHORT).show();

         } 
         catch (Exception e) {       
         e.printStackTrace(); 
         Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
         } 
         finally { 
            try { 
                   isr.close(); 
                   fIn.close(); 
                   } catch (IOException e) { 
                   e.printStackTrace(); 
                   } 
         }
		return array; 
    }
    */
	
	
	public void startTransaction(){
		
		
		thDbAdapter.startTransaction();
		
	}
    
	
	public void endTransaction(){
		
		
	}
	
	
    public void addElement(String genus, String specie, String subspecie, String iCode,String nameCode, String author ){
    	
    	thDbAdapter.addThesaurusItem(genus, specie, subspecie, iCode, nameCode, author);

    	
    }
    
    public void close(){
    	
    	thDbAdapter.close();
    	
    }

	public Cursor getNextItems(String selection) {
		
		allTc = thDbAdapter.fetchNext(selection);
		allTc.moveToFirst();

		return allTc;
	}
    
	/**
	 * 
	 * 
	 * 
	 * @return
	 */
	
	
	public ThesaurusListAdapter fillData()  {
		
		allTc= fetchAllTc();	 
		allTc.moveToFirst();
    	
		ThesaurusListAdapter thList = new ThesaurusListAdapter(c, allTc);

		return thList;
  }
	
	
	
	private Cursor fetchAllTc() {

	    return thDbAdapter.fetchAllItems();

	}



	public  class ThesaurusListAdapter extends CursorAdapter
  {
      public ThesaurusListAdapter(Context context, Cursor c)
      {
              super(context, c);
      }

      @Override
      public void bindView(View view, Context context, Cursor cursor)
      {
    	  
     	  String result="";
	  		if(cursor.getString(3).equals("")){
	  			
	  			result=cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4);
	  			
	  		}
	  		else{
	  			
	  			result=cursor.getString(1)+" "+cursor.getString(2)+" subsp. "+cursor.getString(3)+" "+cursor.getString(4);

	  			
	  		}
           
              ((TextView) view).setText(result);
      }

      @Override
      public String convertToString(Cursor cursor)
      {
    	  
    	  String result="";
    	  		if(cursor.getString(3).equals("")){
    	  			
    	  			result=cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4);
    	  			
    	  		}
    	  		else{
    	  			
    	  			result=cursor.getString(1)+" "+cursor.getString(2)+" subsp. "+cursor.getString(3)+" "+cursor.getString(4);

    	  			
    	  		}

              
            return result;
      }

      @Override
      public View newView(Context context, Cursor cursor, ViewGroup parent)
      {
              final LayoutInflater inflater = LayoutInflater.from(context);
              final TextView view = (TextView) inflater.inflate
(R.layout.atrib_row, parent, false);
              view.setText(cursor.getString(1));
              view.setTextColor(Color.BLACK);
              view.setTextSize(16.0f);
              return view;
      }

      
      /**
       * 
       * It's called when the string in the Autocomplete View has changed. 
       * New Query in Background is proceeded for new typed Genus and Specie.
       * 
       * @return A Cursor with the list of possible Thesaurus Item
       * 
       */
      
      @Override
      public Cursor runQueryOnBackgroundThread(CharSequence constraint)
      {                if (constraint == null){
                      return  null;
                      
              }
              
              else{
            	  
            	    String[] spec=constraint.toString().split(" ");
            	    String selection;
            	    
            	    if(spec.length>1){
            	    	
            	    	selection = "Genus like \'" + spec[0]
    	                +"%\' and Specie like \'" + spec[1]
    	                +"%\' ";
            	    	
            	    }
            	    else{
            	    	
            	       selection = "Genus like \'" + constraint.toString()
     	               // +"%\' or Specie like \'" + constraint.toString()
     	                +"%\' ";

            	    }
            	    
            	    Cursor c= getNextItems(selection);
            	    
            	    c.moveToFirst();

	                return c;
              
              }
      }

}



	public void changeProjectTh(long rsId,String thName) {

		ResearchControler rc=new ResearchControler(c);
		rc.changeTh(rsId,thName);
		
	} 

	
}
