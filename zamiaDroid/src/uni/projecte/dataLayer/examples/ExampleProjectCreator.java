package uni.projecte.dataLayer.examples;

import android.content.Context;
import uni.projecte.controler.ResearchControler;

public class ExampleProjectCreator {
	
	private ResearchControler rsC;
	private Context context;
	private String language;
	
	

	public ExampleProjectCreator(Context c, String language){
		
		this.context=c;
		this.language=language;
		rsC=new ResearchControler(context);
		
	}
	
	public String getProjectName() {
		
		if(language.equals("ca")){
			
			return "Projecte de prova";
			
			
		}
		else if(language.equals("es")){
			
			return "Proyecto de prueba";

			
		}
		else if(language.equals("en")){
			
			return "Demo project";

			
		}
		else{
			
			return "Test project";
			
			
		}

		
	}

	
	public long createBasicProject(String projectName){
		
		long projId=-1;
		
		if(language.equals("ca")){
			
			projId=rsC.createProject(projectName, "");
			
			if(projId>0){
				
				rsC.startTransaction();
					rsC.addProjectField(projId, "CitationNotes", "Comentari", "", "comentari per defecte", "simple", "ADDED");
					rsC.addProjectField(projId, "locality", "Localitat", "", "", "simple", "ADDED");
					rsC.addProjectField(projId, "photo", "Fotografia", "", "", "photo", "ADDED");
				rsC.endTransaction();
			
			}

			
		}
		else if(language.equals("es")){
			
			projId=rsC.createProject(projectName, "");
			
			if(projId>0){
				
				rsC.startTransaction();
					rsC.addProjectField(projId, "CitationNotes", "Comentario", "", "comentario por defecto", "simple", "ADDED");
					rsC.addProjectField(projId, "locality", "Localidad", "", "", "simple", "ADDED");
					rsC.addProjectField(projId, "photo", "Fotografía", "", "", "photo", "ADDED");
				rsC.endTransaction();
			
			}

			
			
		}
		else if(language.equals("en")){
			
			projId=rsC.createProject(projectName, "");
			
			if(projId>0){
				
				rsC.startTransaction();
					rsC.addProjectField(projId, "CitationNotes", "Comment", "", "comentari per defecte", "simple", "ADDED");
					rsC.addProjectField(projId, "locality", "Locality", "", "", "simple", "ADDED");
					rsC.addProjectField(projId, "photo", "Photo", "", "", "photo", "ADDED");
				rsC.endTransaction();
			
			}

			
			
		}
		else{
			
		projId=rsC.createProject(projectName, "");
			
			if(projId>0){
				
				rsC.startTransaction();
					rsC.addProjectField(projId, "CitationNotes", "Commentaire", "", "comentari per defecte", "simple", "ADDED");
					rsC.addProjectField(projId, "locality", "Lieu", "", "", "simple", "ADDED");
					rsC.addProjectField(projId, "photo", "Photographie", "", "", "photo", "ADDED");
				rsC.endTransaction();
			
			}

			
			
		}
		
		return projId;
		
	}
	

}
