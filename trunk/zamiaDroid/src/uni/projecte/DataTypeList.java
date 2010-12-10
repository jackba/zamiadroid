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


import uni.projecte.controler.DataTypeControler;
import uni.projecte.dataLayer.bd.DataTypeDbAdapter;
import uni.projecte.dataLayer.bd.ItemDTDbAdapter;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;



public class DataTypeList extends ExpandableListActivity {

    ExpandableListAdapter mAdapter;
	static final int DIALOG_PAUSED_ID = 0;
	private String selectedDT;
	private Button addItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up our adapter
        mAdapter = new MyExpandableListAdapter();
        ((MyExpandableListAdapter) mAdapter).fillAdapter(this);
        
        setListAdapter(mAdapter);
        
        
        registerForContextMenu(getExpandableListView());
        
      
    	
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Opcions DataType");
       menu.add(0, 0, 0, "Afegir Item al DataType");
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

        String title = ((TextView) info.targetView).getText().toString();
        
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                  
            return true;
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
           
            //Toast.makeText(this, title + ": Group " + groupPos + " clicked", Toast.LENGTH_SHORT).show();
            selectedDT=title;
            showDialog(DIALOG_PAUSED_ID);
            //addDataType(title);
            
            return true;
        }
        
        return false;
    }
    
    protected void  onPrepareDialog  (int id, Dialog  dialog){
        
        switch(id) {
        case DIALOG_PAUSED_ID:
        	
            
    	   	
    	   	break;
     
        default:
            dialog = null;
        }
	
    	
    }
    
  
    
    
  protected Dialog onCreateDialog(int id) {
        
	  	final Dialog dialog;
        
        switch(id) {
        case DIALOG_PAUSED_ID:
        	
        	//Context mContext = getApplicationContext();
    	   	dialog = new Dialog(this);
    	   	
        	dialog.setContentView(R.layout.itemscreation);
    	   	dialog.setTitle("Introdueixi les dades");
    	   	
    	   	addItem = (Button)dialog.findViewById(R.id.bAddItem);
    	    addItem.setOnClickListener(new Button.OnClickListener(){
    	    	             
    	    	
    	    	public void onClick(View v)
    	    	              {

    	    	                 EditText et=(EditText)dialog.findViewById(R.id.etNameItem);
    	    	                 
    	    	                 EditText etInfo=(EditText)dialog.findViewById(R.id.etInfoItem);

    	    	                 addDataType(et.getText().toString(), etInfo.getText().toString());
    	    	                 
    	    	                 dialog.dismiss();
    	    	            	 
    	    	              }
    	    	             
    	    });


    	   	break;
     
        default:
            dialog = null;
        }
        return dialog;
    }

    private void addDataType(String itemName, String itemDesc) {
		

		DataTypeDbAdapter dtHnd= new DataTypeDbAdapter(this);
		
		dtHnd.open();
		
	
		Cursor c=dtHnd.fetchDTbyName(selectedDT);
		c.moveToFirst();
		
		
		
		ItemDTDbAdapter itemsHnd= new ItemDTDbAdapter(this);
		
		itemsHnd.open();
		
		itemsHnd.addItemDT(c.getInt(0), itemName, itemDesc);
		
		itemsHnd.close();
		
		dtHnd.close();
		

		
		
	}

    
    //special list adapter for DataTypes
    
    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    	
        private String[] dtNames;
        private String[][] items;        
        
        public void fillAdapter(Context c){
        	
        	DataTypeControler dtH=new DataTypeControler(c);

        	//getting DT names List
          	dtNames=dtH.getDTList();
          	
          	
          	int n=dtNames.length;

          	int i=0;
          	items=new String[n][];
          	
          	for(i=0;i<n;i++){
      			 
          		
          		//filling each list of items
          		items[i]= dtH.getItemsbyDTName(dtNames[i]);   			

      			 
      		}	
        	
        }

        
        public Object getChild(int groupPosition, int childPosition) {
            return items[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return items[groupPosition].length;
        }

        public TextView getGenericView() {

        	
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, 64);

            TextView textView = new TextView(DataTypeList.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(36, 0, 0, 0);
            return textView;
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return dtNames[groupPosition];
        }

        public int getGroupCount() {
            return dtNames.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
}