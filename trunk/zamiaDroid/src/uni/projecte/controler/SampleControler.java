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

import uni.projecte.R;
import uni.projecte.dataLayer.bd.AttributeDbAdapter;
import uni.projecte.dataLayer.bd.CitacionDbAdapter;
import uni.projecte.dataLayer.bd.SampleAttributeDbAdapter;
import uni.projecte.dataLayer.bd.SampleDbAdapter;
import uni.projecte.dataLayer.dataConverter.CSVExporter;
import uni.projecte.dataLayer.dataConverter.CitationExporter;
import uni.projecte.dataLayer.dataConverter.FagusExporter;
import uni.projecte.dataLayer.dataConverter.JSONExporter;
import uni.projecte.dataTypes.DialogSize;
import uni.projecte.dataTypes.LocationCoord;
import uni.projecte.maps.MapLocation;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import edu.ub.bio.biogeolib.CoordConverter;
import edu.ub.bio.biogeolib.CoordinateLatLon;
import edu.ub.bio.biogeolib.CoordinateUTM;




public class SampleControler {
	
	Context c;
	private CitacionDbAdapter mDbSample;
	private CitacionDbAdapter mDbAttributes;
	
	private CitationExporter cExporter;
	
	private CoordinateUTM utm;

	/*
	private int KEY_TIPUS;
	private int LATITUT;
	private int LONGITUT;*/
	private int KEY_DATA;
	
	private String date;
	private double latitude;
	private double longitude;
	
	private long sampleId;
	private String firstFieldLabel;
	private String author;

	
	

	public SampleControler(Context c) {
		super();
		
		this.c=c;
		
		PreferencesControler pc=new PreferencesControler(c);
		author=pc.getUsername();

	
	}
	
	public long createCitation(long idRs, double latPoint, double longPoint, String comment){
	
		CitacionDbAdapter mDbSample=new CitacionDbAdapter(c);
		mDbSample.open();
			
		long idSample=  mDbSample.createCitation(idRs, latPoint, longPoint, comment);
			
		mDbSample.close();
		
		return idSample;		
		
	}
	
	/** Methods invocated by Fagus Reader***/
	
	public long createEmptyCitation(long idRs){
		
		
		this.sampleId=  mDbAttributes.createEmptyCitation(idRs);

		
		return sampleId;		
		
	}
	

	public void updateCitationDate(String date){

		mDbAttributes.updateDate(this.sampleId, date);

		
	}
	
	
	public void updateCitationLocation(double lat, double longitude){
		
		mDbAttributes.updateLocation(this.sampleId, lat, longitude);


	}
	
	

	/*
	 * 
	 * Method used by CitationEditor
	 * 
	 * It requires a started transaction
	 * 
	 */
	
	public void updateCitationField(long sampleId, int idAtt, String newValue) {
		
		mDbAttributes.updateSampleFieldValue(sampleId, idAtt, newValue);

	}
	
	
	public void loadCitation(long sampleId){
		
		CitacionDbAdapter sa=new CitacionDbAdapter(c);
		sa.open();
		
		Cursor c=sa.fetchSampleBySampleId(sampleId);
		c.moveToFirst();
		
		this.date=c.getString(4);
		this.latitude=c.getDouble(2);
		this.longitude=c.getDouble(3);
		
		c.close();
		sa.close();
		
	}
	
	public ArrayList<LocationCoord> getSamplesLocationByProjectId(long projectId, ArrayList<MapLocation> mapLocations){
		
		mDbSample=new CitacionDbAdapter(c);
		mDbSample.open();
		
		ResearchControler rsC= new ResearchControler(c);
		long idField=rsC.getFieldIdByName(projectId, "OriginalTaxonName");
		
		Cursor cursor =null;
		
		/* Hi ha original taxon Name */
		if(idField>0) {
			
			firstFieldLabel=rsC.getFieldLabelByName(projectId, "OriginalTaxonName");
			cursor= mDbSample.fetchSamplesByField(projectId,"OriginalTaxonName");

			
		} 
		else{
			
			Cursor first=mDbSample.fetchSamplesByResearchId(projectId);
			first.moveToFirst();
			
			
			
			if(first.getCount()>0) {

				String firstFieldName=first.getString(4);
				
				//firstFieldName="OriginalTaxonName";
				
				firstFieldLabel=rsC.getFieldLabelByName(projectId, firstFieldName);
				
				
				cursor= mDbSample.fetchSamplesByField(projectId,firstFieldName);
				
			}
			else{
				
				cursor=mDbSample.fetchSamples(projectId);
				
			}
			
			first.close();
			
			
			
		}
		
		
		int n=cursor.getCount();
		
		ArrayList<LocationCoord> coordinates=new ArrayList<LocationCoord>();
		
		cursor.moveToFirst();
		
		for(int i=0;i<n;i++){
			
			Double lat=cursor.getDouble(4);
			Double longitude=cursor.getDouble(5);
			
		 	if(lat<=90 && longitude<=180){
		 		
		 		coordinates.add(new LocationCoord(lat,longitude));
		 		mapLocations.add(new MapLocation(cursor.getLong(0),cursor.getString(1),cursor.getDouble(4),cursor.getDouble(5)));
			
		 	}
			cursor.moveToNext();
			
		}
		
		cursor.close();
		
		mDbSample.close();
		
		return coordinates;
		
		
	}
	
	
	public ArrayList<LocationCoord> getSampleLocationBySampleId(long projId,long sampleId, ArrayList<MapLocation> mapLocations){
		
		mDbSample=new CitacionDbAdapter(c);
		mDbSample.open();
		
		ResearchControler rsC= new ResearchControler(c);
		long idField=rsC.getFieldIdByName(projId, "OriginalTaxonName");
		
		Cursor cursor =null;
		
		/* Hi ha original taxon Name */
		if(idField>0) {
			
			firstFieldLabel=rsC.getFieldLabelByName(projId, "OriginalTaxonName");
			cursor= mDbSample.fetchSampleByField(projId,sampleId,"OriginalTaxonName");

			
		} 
		else{
		
			cursor=mDbSample.fetchSampleBySampleIdWithFirstField(sampleId);
			
		}
		

		cursor.moveToFirst();
		int n=cursor.getCount();

		ArrayList<LocationCoord> coordinates=new ArrayList<LocationCoord>();

		
		for(int i=0;i<n;i++){
			
			Double lat=cursor.getDouble(4);
			Double longitude=cursor.getDouble(5);
			
		 	if(lat<=90 && longitude<=180){
		 		
		 		coordinates.add(new LocationCoord(lat,longitude));
		 		mapLocations.add(new MapLocation(cursor.getLong(0),cursor.getString(1),cursor.getDouble(4),cursor.getDouble(5)));
			
		 	}

			cursor.moveToNext();
		
		
		
		}
		
		cursor.close();
		
		mDbSample.close();
		
		return coordinates;
		
		
	}
	
	
	public Cursor getCitationListCursorByProjIdUnsyncro(long projId){
		
		CitacionDbAdapter citationAdapter = new CitacionDbAdapter(c);
		
		citationAdapter.open();
		   
		Cursor cursor= citationAdapter.fetchUnsyncronisedSamples(projId);

		citationAdapter.close();
	   
	   return cursor;
		
		
	}
	
	public Cursor getCitationListCursorByProjId(long projId){
		
		CitacionDbAdapter mDbAttributes = new CitacionDbAdapter(c);
		
		mDbAttributes.open();
		   
		Cursor cursor= mDbAttributes.fetchSamplesByResearchId(projId);
		

	   mDbAttributes.close();
	   
	   return cursor;
		
		
	}
	
	public Cursor getCitationsWithFirstFieldByProjectId(long projId){
		
		CitacionDbAdapter mDbAttributes = new CitacionDbAdapter(c);
		
		ResearchControler rc=new ResearchControler(c);
		
		mDbAttributes.open();
		
		
		ResearchControler rsC= new ResearchControler(c);
		long idField=rsC.getFieldIdByName(projId, "OriginalTaxonName");
		
		Cursor cursor =null;
		
		/* Hi ha original taxon Name */
		if(idField>0) {
			
			firstFieldLabel=rsC.getFieldLabelByName(projId, "OriginalTaxonName");
			cursor= mDbAttributes.fetchSamplesByField(projId,"OriginalTaxonName");

			
		} 
		
		/*No hi ha original*/
		else{
			
			Cursor first=mDbAttributes.fetchSamplesByResearchId(projId);
			first.moveToFirst();
			
			
			
			if(first.getCount()>0) {

				String firstFieldName=first.getString(4);
				
				//firstFieldName="OriginalTaxonName";
				
				firstFieldLabel=rc.getFieldLabelByName(projId, firstFieldName);
				
				
				cursor= mDbAttributes.fetchSamplesByField(projId,firstFieldName);
				
			}
			else{
				
				cursor=mDbAttributes.fetchSamples(projId);
				
			}
			
			first.close();
			
			
		}
		
		
		mDbAttributes.close();
		
		return cursor;
	   
		
		
	}
	
	public Cursor getSamplesByField(long projId, String label){
		
		CitacionDbAdapter citationAdapter = new CitacionDbAdapter(c);
		ResearchControler rC= new ResearchControler(c);
		
		String fieldName=rC.getFieldNameByLabel(projId,label);
		Cursor cursor=null;
		
		if(fieldName.equals("")){
			
		
			
		}
		else{
			
			citationAdapter.open();
			
			cursor= citationAdapter.fetchSamplesByField(projId,fieldName);
			

			citationAdapter.close();
			
		}
	
	
	   
		return cursor;
		
		
	}



/*	public void addSampleAttribute(long idSample, int id, String value) {
		
		mDbAttributes = new CitacionDbAdapter(c);
		
		mDbAttributes.open();
		   
		mDbAttributes.createSampleAttribute(idSample,id, value,"");  

	   mDbAttributes.close();
  
		
	}*/
	

	
	/* 
	 * 
	 * Method used by Sampling Activity
	 * 
	 * It's required to start and finish DB transaction
	 * 
	 * 
	 * */
	
	public void addCitationField(long projId,long idSample, long idRs,String attName, String value){
		
		
		long attId;
		
		AttributeDbAdapter aTypes=new AttributeDbAdapter(c);
		aTypes.open();
		

		Cursor att= aTypes.fetchFieldsFromProject(projId, attName);
		
		att.moveToFirst();
		
		//attExists
		
		if(att.getCount()>0){
			
			attId=att.getLong(0);
			mDbAttributes.createSampleAttribute(idSample,attId,value,attName);  

			
		}
		
		//we have to create a new Attribute
		else{
			
			//aTypes.createAttribute(idRs, attName, label, desc, value, type, cat);
			
			
		}
		
		att.close();		
		aTypes.close();
	
		
	}
	
	public void addObservationAuthor(long projId,long idSample){
		
		
		
		AttributeDbAdapter aTypes=new AttributeDbAdapter(c);
		aTypes.open();

		Cursor att= aTypes.fetchFieldsFromProject(projId,"ObservationAuthor");
		
		att.moveToFirst();
		
		//attExists
		
		if(att.getCount()>0){
			
			long id=att.getLong(0);
			
			//mDbAttributes.updateSampleFieldValue(idSample, id, author);
			
			mDbAttributes.createSampleAttribute(idSample,id,author,"ObservationAuthor");  

			
		}
		
		//we have to create a new Attribute
		else{
			
			long id=aTypes.createField(projId, "ObservationAuthor", c.getString(R.string.ObservationAuthor), "", "", "simple", "ADDED",false);
			mDbAttributes.createSampleAttribute(idSample,id,author,"ObservationAuthor");  

			
		}
	
		
		att.close();

		aTypes.close();

		
		
	}
	

	public int exportProject(long projId, Context co,String fileName, String exportFormat){
		
				
		ResearchControler rC= new ResearchControler(co);
		SampleControler sC= new SampleControler(co);
	
		Cursor citations= sC.getCitationListCursorByProjIdUnsyncro(projId);
		KEY_DATA=citations.getColumnIndex(SampleDbAdapter.DATE);
		
		int n= citations.getCount();
		
		rC.loadProjectInfoById(projId);
		
		//Depending on the chosen type of file we'll instantiate the concrete exporter subclass
		
		if(exportFormat.equals("Fagus")){
			
			cExporter=new FagusExporter(rC.getName(),rC.getThName(),rC.getCitationType());
			
		}
		
		else if (exportFormat.equals("TAB")){
			
			cExporter=new CSVExporter(rC.getName(),rC.getThName(),rC.getCitationType());

		}
		else if (exportFormat.equals("JSON")){
			
			cExporter=new JSONExporter(rC.getName(),rC.getThName(),rC.getCitationType());

		}
		
		cExporter.openDocument();

		
		//c= list of types
		Cursor fields=rC.getProjectFieldsCursor(projId);

		Log.d("Citacions","Exportant Citacions Start "+exportFormat);
				
		for (int i=0; i<n; i++){
			
			Log.d("Citacions","Citacio "+i);

			exportCitation(citations, fields);
			cExporter.setLast(false); 
			citations.moveToNext();

		}
		
		Log.d("Citacions","Exportant Citacions End "+exportFormat);

		fields.close();
		cExporter.closeDocument();
		cExporter.stringToFile(fileName,c);
		
		
		return 0;
		
	}
	
	
	private void exportCitation(Cursor citations, Cursor fields){
		
		cExporter.openCitation();
		
		fields.moveToFirst();
		
		int m=fields.getCount();
		
		cExporter.writeCitationCoordinateLatLong(citations.getDouble(2),citations.getDouble(3));
		
		if(citations.getDouble(2)>90 || citations.getDouble(3)>180){
    		
			cExporter.writeCitationCoordinateUTM("");
				
    	}
		
		else {
			
			utm = CoordConverter.getInstance().toUTM(new CoordinateLatLon(citations.getDouble(2),citations.getDouble(3)));
			cExporter.writeCitationCoordinateUTM(utm.getShortForm());
			

			
		}
			
		cExporter.writeCitationDate(citations.getString(KEY_DATA));
		
			
				for(int j=0;j<m;j++){
					
					//Field name: fields.getString(2) 
					//Label: fields.getString(4)
					
					if(j==m-1) cExporter.setLast(true); 
					
					cExporter.createCitationField(fields.getString(2), fields.getString(4), getFieldValueNoTrans(citations.getLong(0), fields.getLong(0)), fields.getString(6));
					
					//createField(fields.getString(4), fields.getString(2), getFieldValue(citations.getLong(0), fields.getLong(0)),darrer,fields.getString(6));
			
					fields.moveToNext();
						
				}
		
		cExporter.closeCitation();
			
		
	}
		
	/*
public void createFagusProject(long projId, Context co,String fileName){
		
	
		fc= new FagusWriter();
		
		fc.openDocument();
		
		pC=new PreferencesControler(co);

		
		ResearchControler rC= new ResearchControler(co);
		SampleControler sC= new SampleControler(co);
	
		Cursor citations= sC.getSampleListCursorByProjIdUnsyncro(projId);
		KEY_DATA=citations.getColumnIndex(SampleDbAdapter.DATE);

		
		int n= citations.getCount();
		long id=citations.getLong(KEY_TIPUS);
		
		rC.loadProjectInfoById(projId);
		
		String rsName=rC.getName();
		
		
		//c= list of types
		Cursor fields=rC.getProjectFieldsCursor(projId);

				
		for (int i=0; i<n; i++){
			
			
			prepareSampleDataFagus(id, citations, fields, rsName,sC);
			citations.moveToNext();

		}
		//sh.printSamples();
		//sh.mostrarTipus("Caques");
		
		//writeToFile(mostres);
		

		fc.closeDocument();
		fc.writeToFile(fc.convertXML2String(), fileName,c);
		
		
	} */
		
	

/*
	
public void sendDataSamples(Cursor list, Context co){
		
		int n= list.getCount();
		
	//	sh= new SamplesHandler();
		
		ResearchDbAdapter st=new ResearchDbAdapter(co);
		AttributeDbAdapter aTypes=new AttributeDbAdapter(co);
		mDbSample=new CitacionDbAdapter(co);
		
		mDbSampleAttribute=new CitacionDbAdapter(co);
		
		mDbSampleAttribute.open();
		aTypes.open();
		mDbAttributes=new CitacionDbAdapter(co);
		st.open();
		mDbAttributes.open();
		mDbSample.open();
		
		KEY_TIPUS=list.getColumnIndex(SampleDbAdapter.KEY_RS);
		LATITUT=list.getColumnIndex(SampleDbAdapter.LATITUDE);
		LONGITUT=list.getColumnIndex(SampleDbAdapter.LONGITUDE);
		   

		int id=list.getInt(KEY_TIPUS);
		
		
		Cursor rsC= st.fetchResearch(id);
		rsC.moveToFirst();
		
		String rsName=rsC.getString(1);
		//KEY_ROWID, KEY_TIPUS, LATITUT,LONGITUT,KEY_DATA
		
		//c= list of types
		Cursor c=aTypes.fetchAttributesFromRs(id);
			
		//iteratiioin over all the samples
		//list
		
		RemoteCallInterface rc=new RemoteCallInterface(co);
		rc.createURLRest("sample", "utoPiC", "");
		
		rc.setUrl("api/utoPiC/sample/");
	
		String[] names={"sample"};
		String[] values=new String[1];
		
		String mostres="";
				
		for (int i=0; i<n; i++){
			
			
			String sample=createJSONfromSample(id, list, c, rsName);
			mostres=mostres+sample+"\n";
			
			values[0]=sample;
			rc.preparePostAndConnect(names, values);
			
			mDbSample.updateToSyncronised(list.getInt(0));
			
			list.moveToNext();

		}
		//sh.printSamples();
		//sh.mostrarTipus("Caques");
		
		//writeToFile(mostres);
		
		st.close();
		mDbAttributes.close();
		mDbSample.close();
		aTypes.close();
	}
	
*/
	
	/*private void prepareSampleDataFagus(long id, Cursor citations, Cursor fields, String rsName, SampleControler sC){
		
		//c1 SampleAttributes
		//list --> projecte
		//c llista d'atributs
		
		//int idMostra=list.getInt(0);
		
		fc.openCitation();
		
		fields.moveToFirst();
		
		int m=fields.getCount();
		
		boolean darrer = false;

			fc.writeCitationCoordinate(citations.getDouble(2)+", "+citations.getDouble(3));
			
			utm = CoordConverter.getInstance().toUTM(new CoordinateLatLon(citations.getDouble(2),citations.getDouble(3)));
		
			fc.writeSecondaryCitationCoordinate(utm.getShortForm());

			fc.writeCitationCoordinate(citations.getDouble(2)+", "+citations.getDouble(3));
			
			//fc.writeAuthor(pC.getUsername());

			
				for(int j=0;j<m;j++){
					
					//fields.getString(2) tenim el nom de l'Atribut
					//també necessitarem l'etiqueta que estarà a fields.getString(4)
					
					if(j==m-1) darrer=true; 
					
					createField(fields.getString(4), fields.getString(2), getFieldValue(citations.getLong(0), fields.getLong(0)),darrer,fields.getString(6));
			
					fields.moveToNext();
						
				}
			

			fc.addDate(citations.getString(KEY_DATA));
		
			fc.closeCitation();
				
		
	}
	*/
	
	/*
	private String createJSONfromSample(int idRs, Cursor list, Cursor c, String rsName){
		
	
		//int idMostra=list.getInt(0);
		
		String urlGet="";
		
		
		Cursor c1=mDbAttributes.fetchSampleAttributesBySampleId(list.getLong(0));
		c1.moveToFirst();
		
		c.moveToFirst();
		
		int m=c1.getCount();
			
		String attributes=m+",";
		
		try {
			
			JSONObject mostra=new JSONObject();
			JSONArray atList=new JSONArray();
		
			
			//preparing attribute list
			
			String att="";
			
			
				for(int j=0;j<m;j++){
						
					attributes=attributes.concat("T:"+c1.getString(2)+" V:"+c1.getString(3));
				
					att=att+"&atName"+j+"="+c.getString(2)+"&atValue"+j+"="+c1.getString(3);
					
					JSONObject attribute=new JSONObject();
					
					attribute.put(c.getString(2), c1.getString(3));
		
					atList.put(attribute);
					
					c1.moveToNext();
					c.moveToNext();
						
				}
			
			
				//sh.newSample(c.getString(1), c.getString(2), String.valueOf(list.getInt(LATITUT)), String.valueOf(list.getInt(LONGITUT)), attributes,(String)list.getString(KEY_DATA));
		

			//mostra.put("Name", c.getString(1));
			mostra.put("rsName", rsName);
			mostra.put("username", "utoPiC");
			mostra.put("X", list.getDouble(LATITUT));
			mostra.put("Y", list.getDouble(LONGITUT));
			mostra.put("date", list.getString(KEY_DATA));
			mostra.put("attributes",atList);

			
			urlGet=urlGet+"&rsName="+rsName+"&X="+list.getDouble(LATITUT)+"&Y="+list.getDouble(LONGITUT)+"&date="+list.getString(KEY_DATA)+att+"&atribN="+m;
			
			System.out.println("Json: "+ mostra.toString());
			
			//envia al servidor
			
			//ServerSender ss=new ServerSender();
			//ss.sendData("utoPiC", mostra.toString());
			
			
			
			return urlGet;


			
			
		} catch (JSONException e) {

			e.printStackTrace();
			
			return "";
		}
		
				
		
	}
	
	*/
	
	
	public String getFieldValue(long sampleId,long attId){

		
		Cursor c=mDbAttributes.fetchSampleAttributeBySampleAttId(sampleId,attId);
		c.moveToFirst();
		
		String value;
	
		if(c.getCount()>0) value=c.getString(3);
		else value="";
		
		c.close();
		
		return value;
		
		
	}
	
	public String getFieldValueNoTrans(long sampleId,long attId){

		
		mDbAttributes = new CitacionDbAdapter(c);
		mDbAttributes.open();
		
		Cursor c=mDbAttributes.fetchSampleAttributeBySampleAttId(sampleId,attId);
		c.moveToFirst();
		
		String value;
	
		if(c.getCount()>0) value=c.getString(3);
		else value="";
		
		c.close();
		mDbAttributes.close();
		
		
		return value;
		
		
	}
	
	
	
	public String[] getCitationValues(long citationId, DialogSize ds) {
	
		
		CitacionDbAdapter citations = new CitacionDbAdapter(c);
		citations.open();
		
		Cursor citList=citations.fetchSampleAttributesBySampleId(citationId);
		
		citList.moveToFirst();
		
		int n=citList.getCount();
		
		String[] result = new String[n];

		ds.setySize(n);
		
		int xMaxSize=0;
		
		for(int i=0;i<n;i++){
			
			String fieldName=citList.getString(4);
			String fieldValue=citList.getString(3);
			
			if(fieldValue==null) fieldValue="";
			
			result[i]=fieldName+" : "+fieldValue;
			citList.moveToNext();
			
			if(fieldName.length()+fieldValue.length()>xMaxSize) xMaxSize=fieldName.length()+fieldValue.length();
			
		}
		
		ds.setxSize(xMaxSize);
		
		citList.close();

		citations.close();
		
		return result;
		
	}
	
	public int getCitationsWithField(long idField) {
	
		mDbAttributes = new CitacionDbAdapter(c);
		mDbAttributes.open();
		
		Cursor fields=mDbAttributes.fetchSamplesByFieldId(idField);
		fields.moveToFirst();
		
		int numFields=fields.getCount();
		fields.close();
		
		mDbAttributes.close();

		return numFields;
		
	}
	



	public void startTransaction() {

		mDbAttributes = new CitacionDbAdapter(c);
		mDbAttributes.open();

		mDbAttributes.startTransaction();
		
		
	}

	public void EndTransaction() {
		
		mDbAttributes.endTransaction();
		mDbAttributes.close();
		
	}
	
	
	public void deleteCitation(long sampleId){
		
		CitacionDbAdapter sa=new CitacionDbAdapter(c);
	
		sa.open();
		
		Cursor attribC=sa.fetchSampleAttributesBySampleId(sampleId);
		attribC.moveToFirst();
			
		int m=attribC.getCount();
			
		for(int j=0; j<m; j++){
				
			sa.deleteSampleAttribute(attribC.getLong(0));
				
		}
			
		sa.deleteCitation(sampleId);
		attribC.close();
			
	}
		


	
	public int deleteAllCitations(Cursor c, Context co){
		
		int n= c.getCount();
		long idMostra;
		SampleAttributeDbAdapter sa=new SampleAttributeDbAdapter(co);
		SampleDbAdapter sampl=new SampleDbAdapter(co);
		sampl.open();

		sa.open();
		
		
		for(int i=0; i<n; i++){
			
			idMostra=(long)c.getDouble(0);
			
			Cursor attribC=sa.fetchSampleAttributesBySampleId(idMostra);
			attribC.moveToFirst();
			
			int m=attribC.getCount();
			
			for(int j=0; j<m; j++){
				
				sa.deleteSampleAttribute((long)attribC.getDouble(0));
				
			}
				sampl.deleteSample(idMostra);
				c.moveToNext();
		
		}
		
		return n;
		
	}
	
	
	public void deleteCitationField(long idField) {

		mDbAttributes = new CitacionDbAdapter(c);
		mDbAttributes.open();
		
		mDbAttributes.deleteField(idField);
		mDbAttributes.close();

		ResearchControler rC= new ResearchControler(c);
		rC.removeField(idField);
			
	}
	
	
	public void deleteAllCitationsFromProject(long projId) {
		
		CitacionDbAdapter cAdapt = new CitacionDbAdapter(c);
		cAdapt.open();
	
		Cursor citations=cAdapt.fetchSamples(projId);
		citations.moveToFirst();
		
		while (citations.isAfterLast() == false) {

			long citationId=citations.getLong(0);
			
			cAdapt.deleteCitationFields(citationId);
			cAdapt.deleteCitation(citationId);
			
       	    citations.moveToNext();
        }

		
		
		
	
		cAdapt.close();
		
		
		
	}

	
	
	
	public String getDate() {
		return date;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public String getFirstFieldLabel() {
		return firstFieldLabel;
	}



	

}
