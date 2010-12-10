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

import uni.projecte.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;


	
public class DataTypeManagement extends TabActivity {
	
	private TabHost mTabHost;
	
	
	/* DataType Management Tab activity  */

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    
	    setContentView(R.layout.tabtext);

	    mTabHost = getTabHost();
	    CharSequence str;
	    setDefaultTab(0);

        TabHost tabs = getTabHost();

        //TabHost.TabSpec   tab = tabs.newTabSpec("dataTypes list");
        
        TabHost.TabSpec tab;

      /*  tab.setContent(new Intent().setClassName("uni.projecte","uni.projecte.DataTypeList"));
        str = getString(R.string.dtListTabName);
        tab.setIndicator(str);
        tabs.addTab(tab);*/
                  
        
        tab = tabs.newTabSpec("import DataTypes");
        tab.setContent(new Intent().setClassName("uni.projecte","uni.projecte.DataTypeImport"));
        str = getString(R.string.dtImportTabName);
        tab.setIndicator(str);
        tabs.addTab(tab);
        
        tab = tabs.newTabSpec("Thesaurus List");
        tab.setContent(new Intent().setClassName("uni.projecte","uni.projecte.ThesaurusList"));
        str = getString(R.string.thListTab);
        tab.setIndicator(str);
        tabs.addTab(tab);
       
       
	    
	    mTabHost.setCurrentTab(0);
	    
	}
	
}
