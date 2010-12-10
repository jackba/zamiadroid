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

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesControler {

    private SharedPreferences settings;
	
	public PreferencesControler(Context c) {
		
		settings = c.getSharedPreferences("uni.projecte_preferences", 0);
		
	}
	public boolean isUTM(){
		
	     String coorSystem = settings.getString("listPrefCoord", "UTM");
	     
	     if(coorSystem.equals("UTM")) return true;
	     else return false;
		
	}
	
	public boolean isLatLong(){
		
	   return !isUTM();
		
	}
	
	public void setGPSNeeded(boolean needed){
		
	    SharedPreferences.Editor editor = settings.edit();

	    editor.putBoolean("gpsNeeded",needed);

	    editor.commit();
		
		
	}
	
	public boolean gpsNeeded(){
		
	   return settings.getBoolean("gpsNeeded", true);
		
	}
	
	public void setFirstRun(boolean firstRun){
		
	    SharedPreferences.Editor editor = settings.edit();

	    editor.putBoolean("firstRun",firstRun);

	    editor.commit();
		
		
	}
	
	public boolean isFirstRun(){
		
	   return settings.getBoolean("firstRun", true);
		
	}
	
	public String getDefaultPath(){
		
	     return settings.getString("urlThPref", "zamiaDroid");

		
	}
	
	public String getUsername(){
		
	     return settings.getString("usernamePref", "Unknown User");

		
	}
	
	public String getLang(){
		
	     return settings.getString("listPref", "ca");

		
	}

}
