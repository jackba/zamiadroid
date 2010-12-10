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


import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import uni.projecte.dataLayer.bd.AttributeDbAdapter;
import uni.projecte.dataLayer.bd.FieldItemAdapter;
import uni.projecte.dataLayer.bd.ResearchDbAdapter;
import uni.projecte.dataLayer.xml.ResearchXMLparser;
import uni.projecte.dataTypes.ProjectField;
import uni.projecte.dataTypes.Research;
import android.content.Context;
import android.database.Cursor;


public class ResearchControler {
	

	Dictionary<String, Research> researches;
	
	private AttributeDbAdapter fieldAdapter;
	private ResearchDbAdapter mDdResearchTypes;
	private ResearchXMLparser rXML;
	Context c;
	
	private boolean mandatory;

	private AttributeDbAdapter mDbAttribute;

	private String desc;
	private long idRs;
	private String name;
	private String citationType;

	
	
	public ResearchControler (Context c) {
		super();
		
		this.c=c;
		researches=new Hashtable<String, Research>();
		
	}
	
	public String getCitationType() {
		return citationType;
	}


	
	public void changeTh(long rsId,String thName){
		
		
		 mDdResearchTypes = new ResearchDbAdapter(c);
		 mDdResearchTypes.open();
		
		 mDdResearchTypes.updateRsTh(rsId,thName);

		 mDdResearchTypes.close();

		
	}
	
	public void loadResearchInfoByName(String name){
		
		
		 mDdResearchTypes = new ResearchDbAdapter(c);
		 mDdResearchTypes.open();
		
		Cursor c=mDdResearchTypes.fetchResearchByName(name);
		c.moveToFirst();
		this.desc=c.getString(2);
		this.idRs=c.getLong(0);
		this.name=name;
		
		c.close();	
		
		mDdResearchTypes.close();

		
		
	}
	
	public String getFieldNameByLabel(long projId,String label){
		
		String name="";
		
		AttributeDbAdapter mDbAttribute = new AttributeDbAdapter(c);
		mDbAttribute.open();
		
		Cursor cur=mDbAttribute.fetchFieldNameByLabel(projId,label);
		cur.moveToFirst();
		
		if(cur.getCount()>0) name=cur.getString(2);
		
		cur.close();
		
		mDbAttribute.close();
		
		return name;
		
	
	}
	
	public String getFieldLabelByName(long projId,String name){
		
		
		AttributeDbAdapter mDbAttribute = new AttributeDbAdapter(c);
		mDbAttribute.open();
		
		Cursor cur=mDbAttribute.fetchFieldLabelByName(projId,name);
		cur.moveToFirst();
		
			String label=cur.getString(4);
		
		cur.close();
		
		mDbAttribute.close();
		
		return label;
		
	
	}
	
	
	
	public long getFieldIdByName(long projId,String name){
		
		
		AttributeDbAdapter mDbAttribute = new AttributeDbAdapter(c);
		
		long id=-1;
		
		mDbAttribute.open();
		
		Cursor cur=mDbAttribute.fetchFieldIdByName(projId,name);
		cur.moveToFirst();
		
		if(cur.getCount()>0) id=cur.getLong(0);
		
		cur.close();
		
		mDbAttribute.close();
		
		return id;
		
	
	}
	
	
	public Cursor getProjectFieldsCursor(long rsId){
		
		AttributeDbAdapter fieldAdapter = new AttributeDbAdapter(c);
		
		fieldAdapter.open();
		
		Cursor cur=fieldAdapter.fetchAllFieldsFromProject(rsId);
		cur.moveToFirst();
		
		fieldAdapter.close();
		
		return cur;
	}
	
	public CharSequence[] getListProjFields(long rsId){
		
		AttributeDbAdapter mDbAttribute = new AttributeDbAdapter(c);
		
		mDbAttribute.open();
		
		Cursor cu=mDbAttribute.fetchAttributesFromRsNotOrdered(rsId);
		 cu.moveToFirst();
		 int n=cu.getCount();
		 
		 CharSequence[] list=new CharSequence[n];
		  
		  for (int i=0;i<n;i++){
			  
			  list[i]=cu.getString(4);
			  cu.moveToNext();
			  
		  }
		  
		  cu.close();
		
		mDbAttribute.close();
		
		return list;
	}
	
	public ArrayList<ProjectField> getProjFields(long rsId){
		
		ArrayList<ProjectField> attList=new ArrayList<ProjectField>();
		
		mDbAttribute = new AttributeDbAdapter(c);
		
		mDbAttribute.open();
		
		Cursor c=mDbAttribute.fetchAttributesFromRs(rsId); 
		c.moveToFirst();
		
		int n=c.getCount();
		  
		  for (int i=0;i<n;i++){
			  
			  ProjectField atV=new ProjectField(c.getLong(0),c.getString(2), c.getString(3),c.getString(4),c.getString(5));
			  
			  attList.add(atV);
			  
			  c.moveToNext();
			  
		  }
		  
		  c.close();
		
		mDbAttribute.close();
		
		return attList;
	}
	
	public long loadProjectInfoById(long id){

		 ResearchDbAdapter mDdResearchTypes = new ResearchDbAdapter(c);
		 mDdResearchTypes.open();
		
		Cursor c=mDdResearchTypes.fetchResearch(id);
		c.moveToFirst();
		
		
		if(c.getCount()>0){
			
			this.desc=c.getString(2);
			this.idRs=c.getLong(0);
			this.name=c.getString(1);
		
		}
		else{
			
			this.idRs=-1;
			
		}
		
		c.close();			
		 mDdResearchTypes.close();

		return idRs;
		
	}
	
	public void setType(String value, Research tmpRs) {
		
		this.researches.put(value, tmpRs);
		
	}
	
	
	public int readXML(String url, Boolean internet, ArrayList<String> exportLogList){
		
		rXML=new ResearchXMLparser(this);
		rXML.readXML(c,url,internet);
		
		return importData2DB(exportLogList);
		
	}
	
	 private int importData2DB(ArrayList<String> exportLogList) {

	    	Enumeration<Research> list=this.getResearches();
	    	
	    	mDdResearchTypes= new ResearchDbAdapter(c);
	    	mDbAttribute=new AttributeDbAdapter(c);
	    	
	    	mDdResearchTypes.open();
	    	mDbAttribute.open();
	    	
	    	Cursor c=null;
	    	
	    	int numImportats=0;
	    	
	    	
	    	while(list.hasMoreElements() ){
				
				Research rs=list.nextElement();
				
				c=mDdResearchTypes.fetchResearchByName(rs.getName());
				
				if(c.getCount()!=0){
					
			    	exportLogList.add(rs.getName()+" ja és a la base de dades");

					
				}
				
				else{
					
			    	exportLogList.add(rs.getName()+" exportat amb èxit");
	
					numImportats++;

					
					long researchNum=(int) mDdResearchTypes.createResearch(rs.getName(), rs.getDescription());
					
					int n=rs.getAtributtes().size();
					
					for (int i=0;i<n;i++){
						
						
						mDbAttribute.createField(researchNum, rs.getAtributtes().get(i).getName(), rs.getAtributtes().get(i).getName(),"buida","",rs.getAtributtes().get(i).getType(),"",true);
						
					}
				}
				
			}
	    	
    	
	    	c.close();
	    	
	    	mDdResearchTypes.close();
	    	mDbAttribute.close();
	    	
	    	
	    	return numImportats;
		}
	 


	
	
	public Cursor getResearchListCursor(){
		
		
		mDdResearchTypes = new ResearchDbAdapter(c);

	    mDdResearchTypes.open();
	        
	    Cursor rt= mDdResearchTypes.fetchAllResearches();
	    
	    rt.moveToFirst();

	    mDdResearchTypes.close();
    	
    	return rt;

		
	}
	
	public void printSamples(){
		
		
		Enumeration<Research> ex=researches.elements();
		

		while(ex.hasMoreElements() ){
			
			ex.nextElement().printElement();
			
			
			
		}
		
	}


	public void addItem(String itemName, String selectedItem, Research rs) {
		
		rs.addAtribbute(itemName, selectedItem);
		
	}

	public Enumeration<Research> getResearches() {

		return researches.elements();
		
	}
	
	/*
	 * 
	 * This method creates a Project with the name rsName that uses the thesaurus thName
	 * 
	 */
	
	public long createProject(String rsName, String thName){
		
		 mDdResearchTypes = new ResearchDbAdapter(c);
		 mDdResearchTypes.open();
		
		long idRs= mDdResearchTypes.createResearch(rsName, thName);
		
		this.idRs=idRs;
		
		mDdResearchTypes.close();
		
		
		return idRs;
	}
	
	public void removeProject(long projId){
		
		 mDdResearchTypes = new ResearchDbAdapter(c);
		 mDdResearchTypes.open();
		
		mDdResearchTypes.deleteResearch(projId);
		
		mDdResearchTypes.close();
	
	}
	
	
	public long addFieldItem(long fieldId, String value){
		
		FieldItemAdapter fIBd=new FieldItemAdapter(c);
		
		fIBd.open();
		
		long idItem=fIBd.addFieldItem(fieldId, value);
		
		fIBd.close();
		
		return idItem;
		
		
		
	}
	
	
	public void startTransaction(){
		
		fieldAdapter=new AttributeDbAdapter(c);
		fieldAdapter.open();
		fieldAdapter.startTransaction();

		
		
	}
	
	public void endTransaction(){
		
		fieldAdapter.endTransaction();
		fieldAdapter.close();
		
	}
	
	/*
	 * 
	 * This method creates a Field for the project with projId provided. 
	 * 
	 * @param projectId project's identifier
	 * @param fieldName field's name
	 * @param label field's label or long name
	 * @param fieldType field's type
	 * @param fieldCategory field's category
	 * 
	 */
	
	public long addProjectField(long projId,String fieldName, String label, String desc,String value, String fieldType,String fieldCategory){
		
	    long projFieldId;
	    		
	    
	    if(mandatory){
	    
	    	if(fieldName.equals("OriginalTaxonName")){
	    		
				projFieldId=fieldAdapter.createDefField(projId, fieldName,label,desc,value, "thesaurus");
   		
	    		
	    	}
	    	else{
	    		
	    		projFieldId=fieldAdapter.createDefField(projId, fieldName,label,desc,value, fieldType);
	    		
	    	}
	    	
	    }
	    
	    else{
	    	
			projFieldId=fieldAdapter.createField(projId,fieldName,label,desc,value,fieldType,fieldCategory,true);

	    }

    	    		
		
		return projFieldId;
		
		
	}
	
	/*
	 * 
	 * This method creates a Field for the project with projId provided. 
	 * 
	 * @param projectId project's identifier
	 * @param fieldName field's name
	 * @param label field's label or long name
	 * @param fieldType field's type
	 * @param fieldCategory field's category
	 * 
	 */
	
	public long addProjectNotEditableField(long projId,String fieldName, String label, String desc,String value, String fieldType,String fieldCategory){
		
	    long projFieldId;   
		

	    if(mandatory){
	    
	    	if(fieldName.equals("OriginalTaxonName")){
	    		
				projFieldId=fieldAdapter.createDefField(projId, fieldName,label,desc,value, "thesaurus");
   		
	    		
	    	}
	    	else{
	    		
	    		projFieldId=fieldAdapter.createNotEdDefField(projId, fieldName,label,desc,value, fieldType);
	    		
	    	}
	    	
	    }
	    
	    else{
	    	
			projFieldId=fieldAdapter.createField(projId,fieldName,label,desc,value,fieldType,fieldCategory,true);

	    }

    	    		

		return projFieldId;
		
		
	}
	
	/*
	 * This method provides us a list of the projects in the system
	 * 
	 * @return an ArrayList with the project names
	 * 
	 */
	
	public ArrayList<String> getProjectList(){
		
		
		mDdResearchTypes = new ResearchDbAdapter(c);

	    mDdResearchTypes.open();
	        
	    Cursor rt= mDdResearchTypes.fetchAllResearches();
		
		ArrayList<String> list = new ArrayList<String>();
   	
	   	rt.moveToFirst();
	   	int n=rt.getCount();
	   	
	   	
	   	for (int i=0;i<n;i++){
	   		
	   		list.add(rt.getString(1));
	   		rt.moveToNext();
	   		
	   	}
	   	
	   	rt.close();
	   	
	   	
	   	return list;
		
		
	}

	
	/*
	 * 
	 * This method gets the projects that uses the provided thesaurus name. If any thesaurus is returned the method gives true 
	 * 
	 */

	public boolean isUsed(String thName) {
		
		boolean used=false;

		 mDdResearchTypes = new ResearchDbAdapter(c);
		 mDdResearchTypes.open();
		
		Cursor c=mDdResearchTypes.fetchResearchByTh(thName);
		c.moveToFirst();
		
		if (c.getCount()>0) used=true;
		
		c.close();
		
			
		 mDdResearchTypes.close();
		
		return used;
	}

	
	/*
	 * 
	 * This method helps us to create a field for a concrete project with the parameters provided.
	 * 
	 * @param projectId project's identifier
	 * @param fieldName field's name
	 * @param label field's label or long name
	 * @param category field's category
	 * @param type field's type
	 * 
	 * @return gives back the recently created fieldId
	 * 
	 */

	public long createField(long projectId, String fieldName, String label, String category,String type, boolean visible) {
		
		
		long fieldId;
		
		System.out.println("No hi ha camp "+fieldName);

		  mDbAttribute = new AttributeDbAdapter(c);
		  mDbAttribute.open();
		  
		  Cursor fieldC=mDbAttribute.fetchFieldsFromProject(projectId, fieldName);
		  fieldC.moveToFirst();
		  
		  
		  //it exists
		  if(fieldC.getCount()>0){
			  
			 fieldId=fieldC.getLong(0);
			  
		  }
		  else{
			  
			  
			  fieldId=mDbAttribute.createField(projectId, fieldName, label, "", "", type, category,visible);
			  
		  }
		  
		   fieldC.close();
		
		  mDbAttribute.close();
		  
		  return fieldId;
		
		
	}


	/*
	 * 
	 * This method provides all the field elements from a concrete project
	 * 
	 * @param projectId is the id of our project
	 * @return gives an array list of ProjectFields
	 * 
	 */
	
	
	public ArrayList<ProjectField> getAllProjectFields(long projectId) {
		
		
		ArrayList<ProjectField> attList=new ArrayList<ProjectField>();
		
		mDbAttribute = new AttributeDbAdapter(c);
		
		mDbAttribute.open();
		
		Cursor c=mDbAttribute.fetchAllFieldsFromProject(projectId);
		c.moveToFirst();
		 
		 
		 int n=c.getCount();
		  
		  for (int i=0;i<n;i++){
			  
			  ProjectField atV=new ProjectField(c.getLong(0),c.getString(2), c.getString(3),c.getString(4),c.getString(5));
			  
			  attList.add(atV);
			  
			  c.moveToNext();
			  
		  }
		  
		  c.close();
		
		mDbAttribute.close();
		
		return attList;
	}

	
	public void removeField(long idField) {

		
	AttributeDbAdapter mDbAttribute = new AttributeDbAdapter(c);
		
		mDbAttribute.open();
		
		mDbAttribute.removeProjecteField(idField);

		mDbAttribute.close();
		
		
		
	}

	/*
	 * This method helps us to change the visibility of a field.
	 * 
	 * @param projectId is the id of the project that contains the field
	 * @param fieldName is the name of which we want to change the visibility
	 * @param visible says if the field has to be visible or not
	 * 
	 */
	
	public void changeFieldVisibility(long projectId, String attName, boolean visible) {

		AttributeDbAdapter mDbAttribute = new AttributeDbAdapter(c);
		
		mDbAttribute.open();
		
		mDbAttribute.setVisibilty(projectId,attName,visible);

		mDbAttribute.close();
		
		
	}
	
	public String getThName() {
		return desc;
	}


	public long getIdRs() {
		return idRs;
	}


	public String getName() {
		return name;
	}
	
	public void setOptional() {
		
		mandatory=false;
	}


	public void setMandatory() {
		

		mandatory=true;
		
	}
	
	public void setCitationType(String type){
		
		citationType=type;
		
	}


}
