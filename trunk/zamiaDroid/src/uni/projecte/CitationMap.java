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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uni.projecte.controler.SampleControler;
import uni.projecte.dataLayer.bd.SampleDbAdapter;
import uni.projecte.dataTypes.DialogSize;
import uni.projecte.dataTypes.LocationCoord;
import uni.projecte.maps.MapLocation;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import edu.ub.bio.biogeolib.CoordConverter;
import edu.ub.bio.biogeolib.CoordinateLatLon;
import edu.ub.bio.biogeolib.CoordinateUTM;



public class CitationMap extends MapActivity implements LocationListener {
    /** Called when the activity is first created. */
	
	 private MapController mc;
	 private MapView mapView;
	 GeoPoint p;
	 GeoPoint current;
	 GeoPoint last;
	 GeoPoint unique;
	 SampleDbAdapter dBHelper;
	 private long idSample;
	 ArrayList<LocationCoord> coordinates;
	 private static final int MY_LOCATION=Menu.FIRST;
	 
	 private Location currentLocation;
	 private Location lastLocation;
	 
	 private boolean noGPS=true;
	 
	 private List<Overlay> listOfOverlays;
	 private MyLocationOverlay myLocationOverlay;
	 
	 private ArrayList<MapLocation> mapLocations;
	 
	 private SampleControler sC;
	 
	 private float bearing;
	 
	 Point pBefore;
	 Point pAfter;
	 
	 private TextView pos;
	 
	 private String coorSystem;
	 
	 private long projId;
	 
	 private MapOverlay mapOverlay;
	 
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.samplesmap);
        
        mapView = (MapView) findViewById(R.id.mapview);
        
        pos = (TextView) findViewById(R.id.locationTV);
        
        
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);
        
        sC=new SampleControler(this);

        
        mc = mapView.getController();
        
        mapLocations=new ArrayList<MapLocation>();
 
        coordinates= loadProjectCitations();
        
        
        
        SharedPreferences settings = getSharedPreferences("uni.projecte_preferences", 0);
  	     coorSystem = settings.getString("listPrefCoord", "UTM");
          
   

         // mc.setZoom(13);
     
        //---Add a location marker---
          mapOverlay = new MapOverlay(mapLocations,this,sC,projId);

      	myLocationOverlay = new MyLocationOverlay();

          listOfOverlays = mapView.getOverlays();
          listOfOverlays.add(mapOverlay); 
          
          ImageButton gpsButton = (ImageButton)findViewById(R.id.myLocationButton);
          gpsButton.setOnClickListener(activateGPSListener);
          
          
        
        
        if(coordinates.isEmpty()) {
        	
            Toast.makeText(getBaseContext(), 
   	                R.string.noLocationsCitations, 
   	                 Toast.LENGTH_SHORT).show();
            
            
            double lat = 41.692;
		    double lng = 1.620;
		 
		      unique = new GeoPoint(
		            (int) (lat * 1E6), 
		            (int) (lng * 1E6));

		      mc.animateTo(unique);
		      mc.setZoom(4);
 
        	
        }
        else{
        	
    	    Log.d("CitationsMap",coordinates.size()+" citations Loaded");

        	
            double lat = coordinates.get(0).getLatitude();
            double lng = coordinates.get(0).getLongitude();
     
            p = new GeoPoint(
                (int) (lat * 1E6), 
                (int) (lng * 1E6));
            
            

            
   	    

          // gpsManagement();
          
   		centerUniqueSample();
        	
        	
        }
        
        
        
		
    }
    
    protected void onResume() {
    	
    	super.onResume();

    	  mapLocations=new ArrayList<MapLocation>();
    	  
          coordinates= loadProjectCitations();
          
          listOfOverlays.remove(mapOverlay);
          
          mapOverlay = new MapOverlay(mapLocations,this,sC,projId);
          
          listOfOverlays.add(mapOverlay); 

    	
    }
    
    
        
    private OnClickListener activateGPSListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
         
			gpsManagement();
        	
        }
    };
    
    
    public class FakeView extends MapView {
  
 


		public FakeView(Context context, String apiKey) {

			super(context, apiKey);
			
		}

		@Override
        public void draw(Canvas canvas) {
            try {
                super.draw(canvas);
            } catch (Exception ex) {
                        Log.getStackTraceString(ex);
            }
        }

    }

    
    private void gpsManagement(){
    	
    	 LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
 		
 		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
 	
 			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10000, this);
 			listOfOverlays.add(myLocationOverlay);
 	
 		}
 		
 		else{

 			AlertDialog.Builder builder = new AlertDialog.Builder(this);
 		    	
 		    	
 		    	builder.setMessage(R.string.enableGPSQuestion)
 		    	       .setCancelable(false)
 		    	       .setPositiveButton(R.string.enableGPS, new DialogInterface.OnClickListener() {
 		    	           public void onClick(DialogInterface dialog, int id) {
 		    
 		    	        	   Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
 		    	        	   startActivity(callGPSSettingIntent);
 		    	        		        	   
 		    	           }

 						
 		    	       })
 		    	       .setNegativeButton(R.string.noGPS, new DialogInterface.OnClickListener() {
 		    	           public void onClick(DialogInterface dialog, int id) {
 		    	                
 		    	        	setNoGPS();
 		    	        	centerUniqueSample();
 			    	        dialog.dismiss();
 		    	                
 		    	           }

 						
 		    	       });
 		    	
 		    	AlertDialog alert = builder.create();
 		    	
 		    	alert.show();


 		}
    	
    	
    	
    }
    
    private void setNoGPS() {
	
    	this.noGPS=true;
		
	}
    	
    
    /*
     * 
     * This method get's all citations from the provided project with its location
     * 
     * @return string list with pairs of latitude and longitude
     * 
     */


	private void centerUniqueSample() {
		
		if(idSample!=0 || noGPS){
			
			if(idSample!=0){
				
	    	    Log.d("CitationsMap","B. Centering to unique Citations");
				
			}
			else{

	    	    Log.d("CitationsMap","A. Centering to last Citation");


			}

			 double lat = coordinates.get(coordinates.size()-1).getLatitude();
		     double lng = coordinates.get(coordinates.size()-1).getLongitude();
		 
		      unique = new GeoPoint(
		            (int) (lat * 1E6), 
		            (int) (lng * 1E6));

		      mc.animateTo(unique);
		        
		}
		
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
    	

    	menu.add(0, MY_LOCATION, 0,"Veure localització").setIcon(android.R.drawable.ic_menu_mylocation);

    	return super.onCreateOptionsMenu(menu);
    }

	

    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	
    	
		switch (item.getItemId()) {
		case MY_LOCATION:
			
				gpsManagement();
			 			 
			break;
			
		}
		
	
		return super.onOptionsItemSelected(item);
	}


	private ArrayList<LocationCoord> loadProjectCitations() {
		
		projId=getIntent().getExtras().getLong("id");
		idSample=getIntent().getExtras().getLong("idSample");
		
		
		ArrayList<LocationCoord> coordinates;

		
		if(idSample==0){
			
			 coordinates= sC.getSamplesLocationByProjectId(projId, mapLocations);

			
			
		}
		else{
			
			coordinates= sC.getSampleLocationBySampleId(projId,idSample,mapLocations);
			
		}

	
		
		return coordinates;
	}
	
	
	

	public void onLocationChanged(Location location) {
		if (location != null) {
			
			Double lat = location.getLatitude();
			Double lng = location.getLongitude();
			//String currentLocation = "Lat: " + lat + " Lng: " + lng;
			
			
			if(coorSystem.equals("UTM")){
		    	 
		         CoordinateUTM utm = CoordConverter.getInstance().toUTM(new CoordinateLatLon(lat,lng));
			     pos.setText(utm.getShortForm());
	
		     }
		     else {
	
		    	 pos.setText(lat.toString().subSequence(0, 7)+"\n"+lng.toString().subSequence(0, 7));
	
		     }

		   

			
			if(currentLocation!=null){
				
				lastLocation=currentLocation;
				currentLocation=location;

				bearing=calculateBearing(lastLocation, currentLocation);
				
				last=current;
				
			}
			else {
				
				currentLocation=location;
				
			}
			
			
			current = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
			
			
			mc.animateTo(current);
			
		}
	}
	
	public void onProviderDisabled(String provider) {
		// required for interface, not used
	}
	
	public void onProviderEnabled(String provider) {
		// required for interface, not used
	}
	
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// required for interface, not used
	}
	
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public float calculateBearing(Location before, Location after) {
		pBefore = location2Point(before);
		pAfter = location2Point(after);

		float res = -(float) (Math.atan2(pAfter.y - pBefore.y, pAfter.x
				- pBefore.x) * 180 / 3.1416) + 90.0f;
		Log.d("MapView", "Bearing: " + res);
		if (res < 0)
			return res + 360.0f;
		else
			return res;
	}
	
	public Point location2Point(Location aLocation){
		return new Point((int) (aLocation.getLongitude() * 1E6),
						(int) (aLocation.getLatitude() * 1E6));
	}
	
	
	
	
	
	/* Class overload draw method which actually plot a marker,text etc. on Map */
	protected class MyLocationOverlay extends com.google.android.maps.Overlay {
		
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
			Paint paint = new Paint();
			
			super.draw(canvas, mapView, shadow);
			
			
			// Converts lat/lng-Point to OUR coordinates on the screen.
			
			Point myScreenCoords = new Point();
			
			if(current!=null){
			
				mapView.getProjection().toPixels(current, myScreenCoords);
				
				paint.setStrokeWidth(1);
				paint.setARGB(255, 255, 255, 255);
				paint.setStyle(Paint.Style.STROKE);
				
	
				Bitmap arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
				Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_maps_indicator_verd);
				
				Matrix matrix = new Matrix();
		        matrix.postRotate(bearing);
		        
				
		        Bitmap arrowR = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
	
				canvas.drawBitmap(arrowR, 15, 15, paint);

				canvas.drawBitmap(bmp, myScreenCoords.x-bmp.getWidth()/2, myScreenCoords.y-bmp.getHeight()/2, paint);
				paint.setStyle(Paint.Style.FILL);
				paint.setAntiAlias(true);
				paint.setTextSize(18);
				
				
				canvas.drawText("Estàs aquí", myScreenCoords.x+5, myScreenCoords.y, paint);
				
				paint=new Paint();
				
				paint.setColor(Color.RED);
	            paint.setAntiAlias(true);
	            paint.setStrokeWidth(4);
	            paint.setStrokeCap(Paint.Cap.ROUND);
	            paint.setStyle(Paint.Style.STROKE);
	            
	            
	            if(last!=null){
	            
		        	Point start = new Point();
					mapView.getProjection().toPixels(last, start);
					
				  	Point end = new Point();
					mapView.getProjection().toPixels(current, end);
		
					canvas.drawLine(start.x, start.y, end.x, end.y, paint);
					
	            }
            
			}
			
			return true;
		}
		
		public Point location2Point(Location aLocation){
			return new Point((int) (aLocation.getLongitude() * 1E6),
							(int) (aLocation.getLatitude() * 1E6));
		}
		
	
		
		
	}
    
	class MapOverlay extends com.google.android.maps.Overlay
    {
    
	 	ArrayList<MapLocation> mapLocations = new ArrayList<MapLocation>();
	 	
	    private Bitmap bubbleIcon, shadowIcon;
	    private Paint	innerPaint, borderPaint, textPaint;
	    
	    //  The currently selected Map Location...if any is selected.  This tracks whether an information  
	    //  window should be displayed & where...i.e. whether a user 'clicked' on a known map location
	    private MapLocation selectedMapLocation;
		private long startTime;
		private long endTime; 
		
		private Context c;
		
		private SampleControler sC;
		
		private long projId;
	 	

	   public MapOverlay(ArrayList<MapLocation> mapLocations,Context c, SampleControler sC, long projId) {

		 	
		   this.mapLocations=mapLocations;
		 
		 
		   bubbleIcon = BitmapFactory.decodeResource(getResources(),R.drawable.bubble);
		   shadowIcon = BitmapFactory.decodeResource(getResources(),R.drawable.shadoww);
		   
		   this.sC=sC;
		   this.projId=projId;
		   this.c=c;
		   
	    }
	   
	   public boolean onTouchEvent(MotionEvent event, MapView mapView) 
       {   
           //---when user lifts his finger---
		   
		   
		   if(event.getAction() == MotionEvent.ACTION_DOWN){
		         //record the start time
		         startTime = event.getEventTime();
		      }else if(event.getAction() == MotionEvent.ACTION_UP){
		         //record the end time
		         endTime = event.getEventTime();
		      }

		      //verify
		      if(endTime - startTime > 1000){
		         //we have a 1000ms duration touch
		         

		    	  GeoPoint p = mapView.getProjection().fromPixels(
		                   (int) event.getX(),
		                   (int) event.getY());
		       /*            Toast.makeText(getBaseContext(), 
		                       p.getLatitudeE6() / 1E6 + "," + 
		                       p.getLongitudeE6() /1E6 , 
		                       Toast.LENGTH_SHORT).show();*/
		                   
		                   
		               	//  Store whether prior popup was displayed so we can call invalidate() & remove it if necessary.
		       			boolean isRemovePriorPopup = selectedMapLocation != null;  

		       			//  Next test whether a new popup should be displayed
		       			selectedMapLocation = getHitInfoMapLocation(mapView,p);
		       			if ( isRemovePriorPopup || selectedMapLocation != null) {
		       				mapView.invalidate();
		       			}		
		       			
		       			
		       			return selectedMapLocation != null; 

		      }

		                                 
           return false;
       }      
	   
		@Override
		public boolean onTap(GeoPoint p, MapView	mapView)  {
			
			//  Store whether prior popup was displayed so we can call invalidate() & remove it if necessary.
			boolean isRemovePriorPopup = selectedMapLocation != null;  

			//  Next test whether a new popup should be displayed
			selectedMapLocation = getHitMapLocation(mapView,p);
			if ( isRemovePriorPopup || selectedMapLocation != null) {
				mapView.invalidate();
			}		
			
			//  Lastly return true if we handled this onTap()
			return selectedMapLocation != null;
		}
		
		
		  private MapLocation getHitInfoMapLocation(MapView	mapView, GeoPoint	tapPoint) {
		    	
		    	//  Track which MapLocation was hit...if any
		    	MapLocation hitMapLocation = null;
				
		    	RectF hitTestRecr = new RectF();
				Point screenCoords = new Point();
		    	Iterator<MapLocation> iterator = mapLocations.iterator();
		    	
		    	boolean noHit=false;
		    	
		    	while(iterator.hasNext()) {
		    		MapLocation testLocation = iterator.next();
		    		
		    		//  Translate the MapLocation's lat/long coordinates to screen coordinates
		    		mapView.getProjection().toPixels(testLocation.getPoint(), screenCoords);

			    	// Create a 'hit' testing Rectangle w/size and coordinates of our icon
			    	// Set the 'hit' testing Rectangle with the size and coordinates of our on screen icon
		    		hitTestRecr.set(-bubbleIcon.getWidth()/2,-bubbleIcon.getHeight(),bubbleIcon.getWidth()/2,0);
		    		hitTestRecr.offset(screenCoords.x,screenCoords.y);

			    	//  Finally test for a match between our 'hit' Rectangle and the location clicked by the user
		    		mapView.getProjection().toPixels(tapPoint, screenCoords);
		    		
		    		
		    		if (hitTestRecr.contains(screenCoords.x,screenCoords.y)) {
		    			
		    			
		    			hitMapLocation = testLocation;
		    			hitMapLocation.setMoreInfo(true);
		    			noHit=true;
		    			break;
		    		}
		    		
		    		/*
		    		
		    		else {
		    			
		    			
		    			
		    		}
		    		
		    		*/
		    	}
		    	
		    	if(!noHit){
		    		
		    		final double latitude=tapPoint.getLatitudeE6()/1E6;
	    			
	    			final double longitude=tapPoint.getLongitudeE6()/1E6;
	    			
	    			tapPoint = null;
	    			
	    			
	 			   AlertDialog.Builder builder = new AlertDialog.Builder(c);
	 			    
	 			    
	 			    builder.setMessage("Vols crear una citació al punt "+latitude+" "+longitude)
	 			           .setCancelable(false)
	 			           .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	 			               public void onClick(DialogInterface dialog, int id) {
	 			            	   
	 			            	   createCitation(latitude,longitude);
	 			            	   dialog.dismiss();

	 			               }
	 			           })
	 			           .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	 			               public void onClick(DialogInterface dialog, int id) {
	 			                    
	 			            	   dialog.dismiss();
	 			            	   
	 			            	   
	 			               }
	 			           });
	 			   
	 			    
	 				 AlertDialog alert = builder.create();
	 				 alert.show();
	 			
		    	}
		    	
		    	//  Lastly clear the newMouseSelection as it has now been processed
		    	tapPoint = null;
		    	
		    	return hitMapLocation; 
		    }
		  
		  private void createCitation(double latitude, double longitude){
			  
				Intent myIntent=new Intent(c, Sampling.class);
				myIntent.putExtra("latitude", latitude);
				myIntent.putExtra("longitude", longitude);
				
				Bundle b = new Bundle();
	 	 		b = new Bundle();
	 			b.putLong("id",projId);
	 			myIntent.putExtras(b);

	 			
		        c.startActivity(myIntent);
	  		  	
			  
			  
		  }
		
	    private MapLocation getHitMapLocation(MapView	mapView, GeoPoint	tapPoint) {
	    	
	    	//  Track which MapLocation was hit...if any
	    	MapLocation hitMapLocation = null;
			
	    	RectF hitTestRecr = new RectF();
			Point screenCoords = new Point();
	    	Iterator<MapLocation> iterator = mapLocations.iterator();
	    	while(iterator.hasNext()) {
	    		MapLocation testLocation = iterator.next();
	    		
	    		//  Translate the MapLocation's lat/long coordinates to screen coordinates
	    		mapView.getProjection().toPixels(testLocation.getPoint(), screenCoords);

		    	// Create a 'hit' testing Rectangle w/size and coordinates of our icon
		    	// Set the 'hit' testing Rectangle with the size and coordinates of our on screen icon
	    		hitTestRecr.set(-bubbleIcon.getWidth()/2,-bubbleIcon.getHeight(),bubbleIcon.getWidth()/2,0);
	    		hitTestRecr.offset(screenCoords.x,screenCoords.y);

		    	//  Finally test for a match between our 'hit' Rectangle and the location clicked by the user
	    		mapView.getProjection().toPixels(tapPoint, screenCoords);
	    		if (hitTestRecr.contains(screenCoords.x,screenCoords.y)) {
	    			hitMapLocation = testLocation;
	    			hitMapLocation.setMoreInfo(false);
	    			break;
	    		}
	    	}
	    	
	    	//  Lastly clear the newMouseSelection as it has now been processed
	    	tapPoint = null;
	    	
	    	return hitMapLocation; 
	    }
	    
		public void draw(Canvas canvas, MapView	mapView, boolean shadow) {
	    	
	   		drawMapLocations(canvas, mapView, shadow);
	   		drawInfoWindow(canvas, mapView, shadow);
	    }
	    
	    private void drawMapLocations(Canvas canvas, MapView mapView, boolean shadow) {
	    	
			Iterator<MapLocation> iterator = mapLocations.iterator();
			Point screenCoords = new Point();
	    	while(iterator.hasNext()) {	   
	    		MapLocation location = iterator.next();
	    		mapView.getProjection().toPixels(location.getPoint(), screenCoords);
				
		    	if (shadow) {
		    		//  Only offset the shadow in the y-axis as the shadow is angled so the base is at x=0; 
		    		canvas.drawBitmap(shadowIcon, screenCoords.x, screenCoords.y - shadowIcon.getHeight(),null);
		    	} else {
	    			canvas.drawBitmap(bubbleIcon, screenCoords.x - bubbleIcon.getWidth()/2, screenCoords.y - bubbleIcon.getHeight(),null);
		    	}
	    	}
	    }
	   
	    private void drawInfoWindow(Canvas canvas, MapView	mapView, boolean shadow) {
	    	
	    	if ( selectedMapLocation != null) {
	    		if ( shadow) {
	    			//  Skip painting a shadow in this tutorial
	    		} else {
					//  First determine the screen coordinates of the selected MapLocation
					
	    			String citationInfo="";
	    			DialogSize dS=new DialogSize();
	    			int INFO_WINDOW_WIDTH;
	    			int INFO_WINDOW_HEIGHT;
	    			//  Draw the MapLocation's name
					int TEXT_OFFSET_X = 10;
					int TEXT_OFFSET_Y = 15;
					
					Point selDestinationOffset = new Point();
					mapView.getProjection().toPixels(selectedMapLocation.getPoint(), selDestinationOffset);
			    	
			    	//  Setup the info window with the right size & location
					
					RectF infoWindowRect;
					
	    			
					if(selectedMapLocation.isMoreInfo()) {
						
						String [] citationInfoList=sC.getCitationValues(selectedMapLocation.getCitationId(),dS);
						INFO_WINDOW_WIDTH = 7*dS.getxSize();
						INFO_WINDOW_HEIGHT = 12*dS.getySize()+35;
						
						infoWindowRect= new RectF(0,0,INFO_WINDOW_WIDTH,INFO_WINDOW_HEIGHT);				
						int infoWindowOffsetX = selDestinationOffset.x-INFO_WINDOW_WIDTH/2;
						int infoWindowOffsetY = selDestinationOffset.y-INFO_WINDOW_HEIGHT-bubbleIcon.getHeight();
						infoWindowRect.offset(infoWindowOffsetX,infoWindowOffsetY);
						
						//  Draw inner info window
						canvas.drawRoundRect(infoWindowRect, 5, 5, getInnerPaint());
						
						//  Draw border for info window
						canvas.drawRoundRect(infoWindowRect, 5, 5, getBorderPaint());
						
						for(int i=0;i<dS.getySize();i++){
							
							canvas.drawText(citationInfoList[i],infoWindowOffsetX+TEXT_OFFSET_X,infoWindowOffsetY+TEXT_OFFSET_Y+i*13,getTextPaint());
							
						}


						
					}
					
					else{
						
						citationInfo=selectedMapLocation.getName();
						if(selectedMapLocation.getName()!=null){
							
							INFO_WINDOW_WIDTH = 12+6*selectedMapLocation.getName().length();

						}
						else{
							
							citationInfo="";
							INFO_WINDOW_WIDTH = 12;

						}
						INFO_WINDOW_HEIGHT = 25;
						
						infoWindowRect= new RectF(0,0,INFO_WINDOW_WIDTH,INFO_WINDOW_HEIGHT);				
						int infoWindowOffsetX = selDestinationOffset.x-INFO_WINDOW_WIDTH/2;
						int infoWindowOffsetY = selDestinationOffset.y-INFO_WINDOW_HEIGHT-bubbleIcon.getHeight();
						infoWindowRect.offset(infoWindowOffsetX,infoWindowOffsetY);
						
						//  Draw inner info window
						canvas.drawRoundRect(infoWindowRect, 5, 5, getInnerPaint());
						
						//  Draw border for info window
						canvas.drawRoundRect(infoWindowRect, 5, 5, getBorderPaint());
						
						canvas.drawText(citationInfo,infoWindowOffsetX+TEXT_OFFSET_X,infoWindowOffsetY+TEXT_OFFSET_Y,getTextPaint());

						
					}
	    			

	    		}
	    	}
	    }
	    
	    
	    public Paint getInnerPaint() {
			if ( innerPaint == null) {
				innerPaint = new Paint();
				innerPaint.setARGB(225, 75, 75, 75); //gray
				innerPaint.setAntiAlias(true);
			}
			return innerPaint;
		}
	    
	    public Paint getTextPaint() {
			if ( textPaint == null) {
				textPaint = new Paint();
				textPaint.setARGB(255, 255, 255, 255);
				textPaint.setAntiAlias(true);
			}
			return textPaint;
		}
	    
	    public Paint getBorderPaint() {
			if ( borderPaint == null) {
				borderPaint = new Paint();
				borderPaint.setARGB(255, 255, 255, 255);
				borderPaint.setAntiAlias(true);
				borderPaint.setStyle(Style.STROKE);
				borderPaint.setStrokeWidth(2);
			}
			return borderPaint;
		}
	    
      /*  public boolean draw(Canvas canvas, MapView mapView, 
        boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   
 
            //---translate the GeoPoint to screen pixels---
            
            int n=points.length;
            
            for(int i=0; i<n; i=i+2){
            	
            	 drawMarker(canvas,mapView,points[i],points[i+1]);
            	
            }
     
            
            return true;
        } */
        
     /*   private void drawMarker(Canvas canvas, MapView mapView, String posX, String posY){
        	
        	Point screenPts = new Point();
            p=converPoint(posX, posY); 
            mapView.getProjection().toPixels(p, screenPts);
 
            //---add the marker---
          /*  Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.pet);  
            
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);
            
            */
       /*  
            RectF oval = new RectF(screenPts.x,screenPts.y,screenPts.x+10,screenPts.y+10);
            Paint paint = new Paint();
            paint.setARGB(200, 0, 255, 0);
            canvas.drawOval(oval, paint); 
        	
        }*/
        
      /*  private GeoPoint converPoint(String coorX,String coorY){
        	
        	
             double lat = Double.parseDouble(coorX);
             double lng = Double.parseDouble(coorY);
      
            GeoPoint p = new GeoPoint(
                 (int) (lat * 1E6), 
                 (int) (lng * 1E6));
             
             return p;
        	
        } */
        
        
    } 

    
}

