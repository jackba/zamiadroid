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

//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginScreen extends Activity {

    String sUserName;
    String sPassword;
    Thread t;
    ProgressDialog dialog;
    
	//private static final String TAG = "Login";
    //private static byte[] sBuffer = new byte[512];
    
	//private static final int CONNECTED =200;
	//private static final int Unauthorized =401;

    
    String messageLogin;



    
    private SharedPreferences mPreferences; 
    
    
	public void onCreate(Bundle icicle) {
	     super.onCreate(icicle);

	     // load up the layout
	        setContentView(R.layout.loginpage);
	       
	        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE); 
	        
	        
	        Button launch = (Button)findViewById(R.id.login_button);
	        
	                          
	        
	        launch.setOnClickListener( new OnClickListener()
	        {
	         
	          public void onClick(View viewParam)
	          {
	               // this gets the resources in the xml file and assigns it to a local variable of type EditText
	                EditText usernameEditText = (EditText) findViewById(R.id.txt_username);
	                EditText passwordEditText = (EditText) findViewById(R.id.txt_password);
	               
	                // the getText() gets the current value of the text box
	                // the toString() converts the value to String data type
	                // then assigns it to a variable of type String
	                sUserName = usernameEditText.getText().toString();
	                sPassword = passwordEditText.getText().toString();
	               
	                // this just catches the error if the program cant locate the GUI stuff
	                if(sUserName.equals("") || sPassword.equals("")){
	                	
	                    Toast.makeText(getBaseContext(), 
	                            "Els camps estan buits", 
	                            Toast.LENGTH_LONG).show();
	                
	                }else{
	              	                     
	                                   
	                	   SharedPreferences.Editor editor=mPreferences.edit();
	                       editor.putString("UserName", sUserName);
	                       editor.putString("PassWord", sPassword);
	                       editor.commit();
	                    
	              }
	          }
	        }
	       
	        ); // end of launch.setOnclickListener
	                    
	                    
	      }
	
	
    /*private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
              int loginmsg=(Integer)msg.obj;
              
              if(loginmsg==CONNECTED) {
                    removeDialog(0);

                    Toast.makeText(getBaseContext(), 
                  		 "Usuari i password CORRECTES", 
                            Toast.LENGTH_LONG).show();
                    
                    
                    finish();
              }
              
              else if(loginmsg==Unauthorized) {
              	
            	  removeDialog(0);
            	  
            	  Toast.makeText(getBaseContext(), 
                  		  "Usuari i/o passwords Incorrectes | Torna a intentar-ho", 
                            Toast.LENGTH_LONG).show();
              	
              }
              
        }
  }; */
	
	/*
	private void startSession(String username, String password) {
		
		 Log.v("Tag","Trying to Login");

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://10.0.2.2/~david/loginSystem/api/membersMobile.php");
        
        try {
       	 
       	   List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
              
              
              nvps.add(new BasicNameValuePair("username", username));
              nvps.add(new BasicNameValuePair("pass", password));
              nvps.add(new BasicNameValuePair("service", "login"));

              
              
              UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(nvps,HTTP.UTF_8);
              httppost.setEntity(p_entity);

              HttpResponse response = client.execute(httppost);
              
              
              int status_code = response.getStatusLine().getStatusCode();

                             
              Log.v(TAG, response.getStatusLine().toString());
              HttpEntity responseEntity = response.getEntity();
              Log.v(TAG, "Set response to responseEntity");
              
              
              InputStream inputStream = responseEntity.getContent();
                            

              ByteArrayOutputStream content = new ByteArrayOutputStream();

              // Read response into a buffered stream
              int readBytes = 0;
              while ((readBytes = inputStream.read(sBuffer)) != -1) {
                  content.write(sBuffer, 0, readBytes);
              }
              
              messageLogin=new String(sBuffer);
             
            
              
              if (messageLogin.contains("200")) {
                    // Store the username and password in SharedPreferences after the successful login
                    SharedPreferences.Editor editor=mPreferences.edit();
                    editor.putString("UserName", username);
                    editor.putString("PassWord", password);
                    editor.commit();
                    Message myMessage=new Message();
                    myMessage.obj=CONNECTED;
                    handler.sendMessage(myMessage);
                    
                          
              } 
              
              else if(messageLogin.contains("401")) {
                  //  Intent intent = new Intent(getApplicationContext(), LoginError.class);
                //    intent.putExtra("LoginMessage", parsedLoginDataSet.getMessage());
                  //  startActivity(intent);
                  //tornar enrere i mostrar error
                  

            	    Message myMessage=new Message();
                    myMessage.obj=Unauthorized;
                    handler.sendMessage(myMessage);
                  
                  
              }
        } catch (Exception e)
        {
             Intent intent = new Intent(getApplicationContext(), LoginError.class);
              intent.putExtra("LoginMessage", "Unable to login");
              startActivity(intent);
              removeDialog(0);
        }
		
		
	}*/
	
	
    protected Dialog onCreateDialog(int id) {
        switch (id) {
              case 0: {
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("Validant Ususari i password, esperi...");
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(true);
                    return dialog;
              }
        }
        return null;
  }
	  	
	
}
