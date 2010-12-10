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


public class FagusExporter extends CitationExporter {
	
	private FagusWriter fc;

	public FagusExporter(String projectName, String thesaurusName, String projectType) {
		
		super(projectName, thesaurusName, projectType);
		
		fc= new FagusWriter();
	
	
	}
	
	public void openCitation(){
		
		fc.openCitation();
		
	}
	
	public void closeCitation(){
	
		fc.closeCitation();
		
	}
	
	public void createCitationField(String attName, String label, String value,String category){
		
		if(attName.compareTo("OriginalTaxonName")==0){
			
			fc.setTaxon(value);
			
		}
		else if(attName.compareTo("origin")==0){
			
			fc.writeCitation(value, "Botanical");
			
		}
		
		else if(attName.compareTo("CitationNotes")==0){
			
			fc.addComment(value);
			
		}
		else if(attName.compareTo("ObservationAuthor")==0){
			
			fc.writeAuthor(value);
			
		}
		
		else if(attName.compareTo("Sureness")==0){
			
			fc.addTaxon(value);
			
		}
		else{
			

			fc.createSideData(label, attName, value,isLast(),category);

			
		}
		
		
		
	}
	
	public void writeCitationCoordinateLatLong(double latitude, double longitude) {
		
		if(latitude>90 || longitude>180){
    		
			fc.writeCitationCoordinate("");

    	}
		else fc.writeCitationCoordinate(latitude+", "+longitude);

	}


	public void writeCitationCoordinateUTM(String utmShortForm) {

		fc.writeSecondaryCitationCoordinate(utmShortForm);

	}


	public void writeCitationDate(String date) {
		
		fc.addDate(date);

		
	}
	
	public void openDocument(){
		
		fc.openDocument();
		
		
	}
	
	public void closeDocument(){
		
		fc.closeDocument();
		setFormat(".xml");
		setResult(fc.convertXML2String());
		
	}
	

}
