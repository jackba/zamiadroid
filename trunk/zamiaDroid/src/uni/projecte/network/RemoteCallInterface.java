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

package uni.projecte.network;

import uni.projecte.LoginScreen;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;



public class RemoteCallInterface{
    /** Called when the activity is first created. */
	
	
	Context c;
	Editor editor;
	

    Button signin;
    String loginmessage = null;
    private String url="http://pfc.adriavalles.net/";
    
	private SharedPreferences mPreferences; 
    
    String sUserName;
    String sPassword;

   
   public RemoteCallInterface(Context c){

	   this.c=c;
                 		          	             
       
    }
   

   //hack modifing URL
   
   public void setUrl(String url) {
		this.url = url;
	}

   public String createURLRest(String service,String username, String uniqueObject){
    	
    	this.url="/api/";
    	
    	url=url+username+"/"+service+"/";
    	
    	if(uniqueObject.compareTo("")>0) url=url+uniqueObject+"/";
    	    	
    	return url;
    	
    	
    }
    
    
    public void preparePostAndConnect(String[] names, String[] values){
    	
    	mPreferences = c.getSharedPreferences("CurrentUser", Context.MODE_PRIVATE); 
        
        editor=mPreferences.edit();
            
        editor.clear();
                   	
        if (!checkLoginInfo()) {
        				
        	Intent intent = new Intent(c, LoginScreen.class);
            c.startActivity(intent);  
        		
        }
        
        else {
                	  
            Toast.makeText(c, 
          		  "Ets l'usuari "+mPreferences.getString("UserName","No loginejat"), 
                    Toast.LENGTH_LONG).show();
            
            preparePostParamsAndConnect(names, values);
            
       	
        }  
    	
    }
    
    
    private void preparePostParamsAndConnect(String[] names, String[] values){
    	
    	//falta comprovar que tinguin la mateixa mida
    	
    	Intent intent = new Intent(c,uni.projecte.RemoteCallAct.class);
		Bundle b;
		
		b = new Bundle();
		b.putInt("nParams", names.length);
		intent.putExtras(b);
		
		for (int i=0;i<names.length;i++){
			
			
			b = new Bundle();
			b.putString("n"+i, names[i]);
			intent.putExtras(b);

			b = new Bundle();
			b.putString("v"+i, values[i]);
			intent.putExtras(b);


			
		}
		
		b = new Bundle();
		b.putString("url", url);
		intent.putExtras(b);
		
		((Activity) c).startActivity(intent);

    	
    }
    
   
    
   //Checking whether the username and password has stored already or not
   private final boolean checkLoginInfo() {
         boolean username_set = mPreferences.contains("UserName");
         boolean password_set = mPreferences.contains("PassWord");
         if ( username_set || password_set ) {
               return true;
         }
         return false;
   } 
	
	
	
}