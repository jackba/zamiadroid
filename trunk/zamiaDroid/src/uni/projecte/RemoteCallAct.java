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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class RemoteCallAct extends Activity {
	
	private static final String TAG = "Login";

	
	private static final int CHANGE_USERNAME = Menu.FIRST;
    private ProgressDialog dialog;
    private TextView connTV;
    
	private static final int CONNECTED =200;
	private static final int Unauthorized =401;
	private static final int BADREQUEST =400;
	
    private SharedPreferences.Editor editor;
    private Thread t;
       
   // private static String urlBase ="http://pfc.adriavalles.net/";
    
    private String messageLogin;

    private List<BasicNameValuePair> nvps;
    
    
    String getParam;

	
	
	  public void onCreate(Bundle icicle) {
		     super.onCreate(icicle);

		     // load up the layout
		        setContentView(R.layout.remotecall);
		        
		        String url=getIntent().getExtras().getString("url"); 
		        
		        connTV = (TextView) findViewById(R.id.tvConnectionMessage);
		        connTV.setText("Enviant mostra........");
		        
		        getPostParams();
		        
		        connect(url);
		        
	  }
	  
	  
	  private void getPostParams(){
		  
		  
		int n= getIntent().getExtras().getInt("nParams");
		 
		nvps= new ArrayList<BasicNameValuePair>();

	    	
  		for(int i=0;i<n;i++){
  			
  	           nvps.add(new BasicNameValuePair(getIntent().getExtras().getString("n"+i), getIntent().getExtras().getString("v"+i)));
  	           
  	           getParam=getIntent().getExtras().getString("v"+i);
  	           
  	           
  		}
		  
		  
		  
		  
	  }
	  
	  private void connect(final String url){
	    	
	        
	        showDialog(0);
	           t=new Thread() {
	        	   
	        	   
	                 public void run() {
	               	  
	               	  
	               	  remoteCall(url);
	               	  
	               	  
	                 }
	           };
	           
	           t.start();
	    	
	    	
	    }
	  
	    public static String addSlashes( String text ){    	
	        final StringBuffer sb                   = new StringBuffer( text.length() * 2 );
	        final StringCharacterIterator iterator  = new StringCharacterIterator( text );
	        
	  	  	char character = iterator.current();
	        
	        while( character != StringCharacterIterator.DONE ){
	            if( character == '"' ) sb.append( "\\\"" );
	            else if( character == '\'' ) sb.append( "\\\'" );
	            else if( character == '\\' ) sb.append( "\\\\" );
	            else if( character == '\n' ) sb.append( "\\n" );
	            //else if( character == '{'  ) sb.append( "\\{" );
	            //else if( character == '}'  ) sb.append( "\\}" );
	            else sb.append( character );
	            
	            character = iterator.next();
	        }
	        
	        return sb.toString();
	    }
	    
	    
	    public static final String escapeHTML(String s){
	    	   StringBuffer sb = new StringBuffer();
	    	   int n = s.length();
	    	   for (int i = 0; i < n; i++) {
	    	      char c = s.charAt(i);
	    	      switch (c) {
	    	         case '<': sb.append("&lt;"); break;
	    	         case '>': sb.append("&gt;"); break;
	    	         //case '&': sb.append("&amp;"); break;
	    	         case '"': sb.append("&quot;"); break;
	    	         case 'à': sb.append("&agrave;");break;
	    	         case 'À': sb.append("&Agrave;");break;
	    	         case 'â': sb.append("&acirc;");break;
	    	         case 'Â': sb.append("&Acirc;");break;
	    	         case 'ä': sb.append("&auml;");break;
	    	         case 'Ä': sb.append("&Auml;");break;
	    	         case 'å': sb.append("&aring;");break;
	    	         case 'Å': sb.append("&Aring;");break;
	    	         case 'æ': sb.append("&aelig;");break;
	    	         case 'Æ': sb.append("&AElig;");break;
	    	         case 'ç': sb.append("&ccedil;");break;
	    	         case 'Ç': sb.append("&Ccedil;");break;
	    	         case 'é': sb.append("&eacute;");break;
	    	         case 'É': sb.append("&Eacute;");break;
	    	         case 'è': sb.append("&egrave;");break;
	    	         case 'È': sb.append("&Egrave;");break;
	    	         case 'ê': sb.append("&ecirc;");break;
	    	         case 'Ê': sb.append("&Ecirc;");break;
	    	         case 'ë': sb.append("&euml;");break;
	    	         case 'Ë': sb.append("&Euml;");break;
	    	         case 'ï': sb.append("&iuml;");break;
	    	         case 'Ï': sb.append("&Iuml;");break;
	    	         case 'ô': sb.append("&ocirc;");break;
	    	         case 'Ô': sb.append("&Ocirc;");break;
	    	         case 'ö': sb.append("&ouml;");break;
	    	         case 'Ö': sb.append("&Ouml;");break;
	    	         case 'ø': sb.append("&oslash;");break;
	    	         case 'Ø': sb.append("&Oslash;");break;
	    	         case 'ß': sb.append("&szlig;");break;
	    	         case 'ù': sb.append("&ugrave;");break;
	    	         case 'Ù': sb.append("&Ugrave;");break;         
	    	         case 'û': sb.append("&ucirc;");break;         
	    	         case 'Û': sb.append("&Ucirc;");break;
	    	         case 'ü': sb.append("&uuml;");break;
	    	         case 'Ü': sb.append("&Uuml;");break;
	    	         case '®': sb.append("&reg;");break;         
	    	         case '©': sb.append("&copy;");break;   
	    	         case '€': sb.append("&euro;"); break;
	    	         // be carefull with this one (non-breaking whitee space)
	    	         case ' ': sb.append("_");break;         
	    	         
	    	         default:  sb.append(c); break;
	    	      }
	    	   }
	    	   return sb.toString();
	    	}

	  
	  
	  private void remoteCall(String url){
	    	

	        DefaultHttpClient client = new DefaultHttpClient();
	        

	        String ale="http://pfc.adriavalles.net/index.php?action=addSample&controler=Sample&username=utoPiC"+escapeHTML(getParam);
	              
	        HttpGet httppost = new HttpGet(ale);

	        //client.getCookieStore();
	        
	        try { 
	             

	        
	        //UrlEncodedFormEntity p_entity;
			
			//p_entity = new UrlEncodedFormEntity(nvps,HTTP.UTF_8);
			
	        //httppost.setEntity(p_entity);

	        HttpResponse response = client.execute(httppost);
	                       
	        Log.v(TAG, response.getStatusLine().toString());
	        
	        
	        HttpEntity responseEntity = response.getEntity();
	        
	        
	        Log.v(TAG, "Set response to responseEntity");
	        
	        
	        InputStream inputStream = responseEntity.getContent();
	                      
	            
	        final char[] buffer = new char[512];
	        StringBuilder out = new StringBuilder();
	        Reader in = new InputStreamReader(inputStream, "UTF-8");
	        int read;
	        do {
	          read = in.read(buffer, 0, buffer.length);
	          if (read>0) {
	            out.append(buffer, 0, read);
	          }
	        } while (read>=0);

	        
	        messageLogin=out.toString();
		        
	     //   if (messageLogin.contains("200")) {
	            // Store the username and password in SharedPreferences after the successful login
	           	
	        	if(messageLogin.contains("401")){
	        		
	        		Message myMessage=new Message();
	                myMessage.obj=Unauthorized;
	                handler.sendMessage(myMessage);

	        		
	        	}
	        	
	        	else if(messageLogin.contains("201")){
	        		
	        		Message myMessage=new Message();
	                myMessage.obj=CONNECTED;
	                handler.sendMessage(myMessage);
	        		
	        	}
	        	
	        	else if(messageLogin.contains("400")){
	        		
	        		Message myMessage=new Message();
	                myMessage.obj=BADREQUEST;
	                handler.sendMessage(myMessage);
	        		
	        	}
	        	
	        	
	        	else{
	        
	        	Message myMessage=new Message();
	            myMessage.obj=CONNECTED;
	            handler.sendMessage(myMessage);
	        	}
	                  
	      //} 
	     
	     // else if(messageLogin.contains("401")) {
	          //  Intent intent = new Intent(getApplicationContext(), LoginError.class);
	        //    intent.putExtra("LoginMessage", parsedLoginDataSet.getMessage());
	          //  startActivity(intent);
	          //tornar enrere i mostrar error
	          

	    	/*    Message myMessage=new Message();
	            myMessage.obj=Unauthorized;
	            handler.sendMessage(myMessage);
	          
	          
	      }*/
	        
	        
	        
	        } 
	        catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			} catch (ClientProtocolException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    }
	  
	  
	  @Override
	    protected Dialog onCreateDialog(int id) {
	          switch (id) {
	                case 0: {
	                      dialog = new ProgressDialog(this);
	                      dialog.setMessage("Connectant, esperi...");
	                      dialog.setIndeterminate(true);
	                      dialog.setCancelable(true);
	                      return dialog;
	                }
	          }
	          return null;
	    }
	    private Handler handler = new Handler() {
	          @Override
	          public void handleMessage(Message msg) {
	                int loginmsg=(Integer)msg.obj;
	                
	                connTV = (TextView) findViewById(R.id.tvConnectionMessage);
      		        connTV.setText("Estat connexió:");
	                
	                if(loginmsg==CONNECTED) {
	                      removeDialog(0);

	                      Toast.makeText(getBaseContext(), 
	                    		  "Mostra enviada correctament", 
	                              Toast.LENGTH_LONG).show();
	                      
	                      
	                     
	                      
	                	  TextView responseTV = (TextView) findViewById(R.id.tvConnectionResponse);
	                	  responseTV.setText("Enviament correcte de la mostra");

	                      //Intent intent=new Intent(getApplicationContext(),LectorSIOC.class);
	                      //startActivity(intent);
	                     finish();
	                }
	                
	                else if(loginmsg==Unauthorized) {
	                	
	                	
	                }
	                
	                else if(loginmsg==BADREQUEST) {
	                	
	                		removeDialog(0);

	                      Toast.makeText(getBaseContext(), 
	                    		  "Mostra repetida", 
	                              Toast.LENGTH_LONG).show();
	                      
	                      
	                     
	                      
	                	  TextView responseTV = (TextView) findViewById(R.id.tvConnectionResponse);
	                	  responseTV.setText("Mostra repetida");

	                      //Intent intent=new Intent(getApplicationContext(),LectorSIOC.class);
	                      //startActivity(intent);
	                     finish();
	                }
	                
	          }
	    };
	    
	    
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	
	    	menu.add(0, CHANGE_USERNAME, 0,"Logout Username");
	       
	        
	        return super.onCreateOptionsMenu(menu);
	    }
	    
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	
	    	
			switch (item.getItemId()) {
			case CHANGE_USERNAME:
				
				  editor.clear();
			        editor.commit();
			        
					Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
	                //    intent.putExtra("LoginMessage", parsedLoginDataSet.getMessage());
	                  startActivity(intent);  
				 			 
			
				break;
				
			
			}
			return super.onOptionsItemSelected(item);
		}
		       

}
