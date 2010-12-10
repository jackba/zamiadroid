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

public class CSVExporter extends CitationExporter {
	
	
	private boolean first;
	
	private String result;
	
	private String citation;
	
	private String fieldNames;
	
	
	public CSVExporter(String projectName, String thesaurusName,
			String projectType) {
		
		super(projectName, thesaurusName, projectType);
		
		result="";
		citation="";
		fieldNames="";
		this.first=true;

	
	}
	
	public void openCitation(){
		
		
		
	}
	
	public void closeCitation(){
	
		if(this.first) result=fieldNames+"\n"+citation+"\n";
		else result=result+citation+"\n";
		
		citation="";
		first=false;

	}
	
	public void createCitationField(String attName, String label, String value,String category){
		
		if(this.first) {
			
			if(isLast()) fieldNames=fieldNames+label;
			else fieldNames=fieldNames+label+"\t";
			
		
		}

		
		if(isLast()) citation=citation+value;
		else citation=citation+value+"\t";
		
		
	}
	
	public void writeCitationCoordinateLatLong(double latitude, double longitude) {

		if(this.first) {
			
			fieldNames=fieldNames+"CitationCoordinates\t";
		
		}

		if(latitude>90 || longitude>180){
    		
			citation=" "+"\t";

    	}
		else citation=citation+latitude+" "+longitude+"\t";
		
		 


		
	}


	public void writeCitationCoordinateUTM(String utmShortForm) {

		if(this.first) {
			
			fieldNames=fieldNames+"SecondaryCitationCoordinates\t";
		
		}
	
		citation=citation+utmShortForm+"\t";

	}


	public void writeCitationDate(String date) {
		
		if(this.first) {
			
			fieldNames=fieldNames+"Date\t";
		
		}
		
		
		citation=citation+date+"\t";

		
	}
	
	public void openDocument(){
		
		
		
	}
	
	public void closeDocument(){
		
		setFormat(".tab");
		setResult(result);
		
	}
	

}
