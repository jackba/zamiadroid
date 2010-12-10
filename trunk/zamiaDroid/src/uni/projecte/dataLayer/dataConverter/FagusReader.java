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

package uni.projecte.dataLayer.dataConverter;

import android.content.Context;
import uni.projecte.R;
import uni.projecte.controler.ResearchControler;
import uni.projecte.controler.SampleControler;


public class FagusReader {

	
	private SampleControler sC;
	private ResearchControler rC;
	private Context c;
	private long projectId;
	private long sampleId;
	private int numSamples=0;
	private boolean visible=false;
	
	

	public FagusReader(Context c, long projectId){
		
		this.c=c;
		rC=new ResearchControler(c);
		
		sC=new SampleControler(c);
		sC.startTransaction();
		
		this.projectId=projectId;
		
	}
	
	
	public void startTransaction(){
		
		
		sC.startTransaction();
		
		
	}
	
	
	public void createNewSample(String origin){
		
		this.sampleId=sC.createEmptyCitation(projectId);
		
		String label = c.getString(R.string.origin);

		rC.createField(projectId,"Origin",label,"ECO","simple",visible);
		
		sC.addCitationField(projectId,this.sampleId, this.projectId,"Origin",origin);
		
		numSamples++;
	}
	
	public int getNumSamples() {
		return numSamples;
	}
	

	public void createOriginalTaxonName(String taxonName, String sureness){
		
		
		String label = c.getString(R.string.OriginalTaxonName);

		if(numSamples<2) rC.createField(projectId,"OriginalTaxonName",label,"A","thesaurus",visible);
		
		sC.addCitationField(projectId,this.sampleId, this.projectId,"OriginalTaxonName",taxonName);
		
		
		label = c.getString(R.string.sureness);

		if(numSamples<2) rC.createField(projectId,"Sureness",label,"A","simple",visible);
		
		sC.addCitationField(projectId,this.sampleId, this.projectId,"Sureness",sureness);
		
	}
	
	public void createCorrectedTaxonName(String taxonName, String sureness){
		
		String label = c.getString(R.string.CorrectedTaxonName);

		if(numSamples<2) rC.createField(projectId,"CorrectedTaxonName",label,"A","thesaurus",visible);
		
		sC.addCitationField(projectId,this.sampleId, this.projectId,"CorrectedTaxonName",taxonName);
		
		
		label = c.getString(R.string.correctedSureness);

		if(numSamples<2) rC.createField(projectId,"correctedSureness",label,"A","simple",visible);
		
		sC.addCitationField(projectId,this.sampleId, this.projectId,"correctedSureness",sureness);
		
	}
	

	public void createInformatisationDate(String day,String month, String year, String hours, String minutes,String seconds){
		
		//Date format: yyyy-MM-dd hh:mm:ss
		
		String date=year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds;
		
		String label = c.getString(R.string.InformatisationDate);
		
		if(numSamples<2) rC.createField(projectId,"InformatisationDate",label,"ECO","simple",visible);
		
		sC.addCitationField(projectId,this.sampleId, this.projectId, "InformatisationDate",date);
		
		
	}
	
	public void createObservationDate(String day,String month, String year, String hours, String minutes,String seconds){
		
		//Date format: yyyy-MM-dd hh:mm:ss
		
		String date=year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds;
		
		sC.updateCitationDate(date);
		
		
		
		
	}


	public void createSecondaryCitationCoordinate(String location){
		
		String label = c.getString(R.string.SecondaryCitationCoordinate);
		
		if(numSamples<2) rC.createField(projectId,"SecondaryCitationCoordinate",label,"ECO","simple",visible);
		
		sC.addCitationField(projectId,this.sampleId, this.projectId, "SecondaryCitationCoordinate",location);
		
		
	}
	
	public void createCitationCoordinate(String location){
		
		//convert
		
		if(!location.equals("")){
			
			location=location.replace(",", ".");
			String [] loc=location.split(" ");
			String lat=(String) loc[0].subSequence(0, loc[0].length()-2);
			
			sC.updateCitationLocation(Double.valueOf(lat), Double.valueOf(loc[1]));
			
		}
		
		
		
		
	}
	
	public void createDefaultFields(String tagName,String value){
		
		String label="";

		
		if(tagName.equals("LifeCycleStatus")){
			
			label=c.getString(R.string.LifeCycleStatus);
			
			
		}
		else if(tagName.equals("Natureness")){
			
			label=c.getString(R.string.Natureness);
			
			
		}
		else if(tagName.equals("Accepted")){
			
			label=c.getString(R.string.Accepted);
			
			
		}
		else if(tagName.equals("Informatiser")){
			
			label=c.getString(R.string.Informatiser);
			
			
		}
		else if(tagName.equals("ObservationAuthor")){
			
			label=c.getString(R.string.ObservationAuthor);			
			
		}
		else if(tagName.equals("biological_record_type")){
			
			label=c.getString(R.string.biological_record_type);

			
		}
		else if(tagName.equals("CitationNotes")){
			
			label=c.getString(R.string.CitationNotes);
			
		}

		//get Field and if it doesn't exists 
		
		if(label.equals("")){
			//incorrecte
			
		}
		else{
			
			if(numSamples<2) rC.createField(projectId,tagName,label,"ECO","simple",visible);
			sC.addCitationField(projectId,this.sampleId, this.projectId, tagName, value);
			
		
		}
		
	}

	public void createDatumFields(String tempVal, String name, String label,
			String category) {
		
		if(name.equals("photo")) {
			
			if(numSamples<2) rC.createField(projectId,name,label,category,"photo",visible);

			
		}
		else{

			if(numSamples<2) rC.createField(projectId,name,label,category,"simple",visible);
		
		}
		sC.addCitationField(projectId,this.sampleId, this.projectId, name, tempVal);
		
		
	}
	
	public void finishReader(){
		
		sC.EndTransaction();
		
	}
	
	/*
	 * 
	 * 	<string name="LifeCycleStatus">Estadi del cicle vital</string>
	<string name="Natureness">Natura</string>
	<string name="Accepted">Acceptat</string>
	<string name="Informatiser">Informatitzador</string>
	<string name="ObservationAuthor">Autor</string>
	<string name="biological_record_type">Botànica</string>
	<string name="CitationNotes">Comentaris</string>
	<string name="OriginalTaxonName">Nom Original</string>
	<string name="CorrectedTaxonName">Nom Corregit</string>
	<string name="sureness">Certesa</string>
	 * 
	 * 
	 * 
	 */
	

	
	/*
	 * 
	 *  <ObservationAuthor>David Martí Pino</ObservationAuthor>
    	<ObservationDate day="10" month="10" year="2010" />
    	<Informatiser>David Martí</Informatiser>
    	<InformatisationDate day="14" hours="9" mins="37" month="09" secs="16" year="2010" />
    	<LifeCycleStatus>Flowering and fructification</LifeCycleStatus>
    	<Natureness>Accidental</Natureness>
    	<Accepted>true</Accepted>
    	<CitationCoordinate code="42,12, 1,2" precision="1.0" type="UTM alphanum" units="1m" />
    	<SecondaryCitationCoordinate code="42,12, 1,2" precision="0.0" type="UTM num" units="1m" />
	 * 
	 * 
	 */
	
	
	
	
	
	
	
}
