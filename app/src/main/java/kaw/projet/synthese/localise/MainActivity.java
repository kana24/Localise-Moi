package kaw.projet.synthese.localise;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Timer;
import java.util.TimerTask;

import helpers.MqttHelper;

public class MainActivity extends AppCompatActivity implements MqttCallback{
    private Button b1, b2,exit;
    MqttHelper mqttHelper;
    private TextView text1, dataReceived;
    private BroadcastReceiver br;
    private GPS_Service gp;
    String mdp="open", passoepnhab="open", Broker= "tcp://test.mosquitto.org";
    private String username="openhab";
    private String Topic="almamyngaiithub";
private String date;
    private String   Password="openhab";
    MqttAndroidClient client ;

    @Override
    protected void onResume() {
        super.onResume();
        if(br==null){
            br=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    text1.setText("\n"+intent.getExtras().get("Coordinates"));
                     date=("\n"+intent.getExtras().get("dateh"));


                }
            };
        }
        registerReceiver(br,new IntentFilter("location Update"));
    }
    
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(br!=null){
            unregisterReceiver(br);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(),Broker,
                        clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        //options.setUserName(username);
       // options.setPassword(Password.toCharArray());
        //options.setUserName(username);
        // options.setPassword(Password.toCharArray());
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic , MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                dataReceived.setText(mqttMessage.toString());

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Connecté", Toast.LENGTH_LONG).show();
                    Timer myTimer = new Timer();
                    myTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(text1.getText()==null){
                                Toast.makeText(MainActivity.this, "recois riens", Toast.LENGTH_SHORT).show();
                            }
                            else 
                            pub();
                            pubmdp();
                        }
                    }, 0, 10000);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        b1=(Button)findViewById(R.id.b1);
        b2=(Button)findViewById(R.id.b2);
        dataReceived=(TextView)findViewById(R.id.dataReceived);
        //suscribe();
        startMqtt();
        text1=(TextView) findViewById(R.id.t1);
        gp= new GPS_Service();
        // configure button & text view;
        if (!runtimetime_permission())
            enable_buttons();

    }

    private void enable_buttons() {
      
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GPS_Service.class);
                startService(intent);


                if (br == null) {
                    br = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                        }
                    };
                }
            }


        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),GPS_Service.class);
                stopService(intent);
               /* Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/


            }
        });

        //   }     //   });

    } 
    
    public void pub(){
        String topic="test/local";
        String topi1="Info";

        String message= (String) text1.getText();

       Log.e("coordonnee", message);
        
        try {

            client.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void pubmdp(){
        String topic="test/local/mdp";
        

        String message="passw" ;

        Log.e("", message);

        try {

            client.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private void startMqtt() {
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                dataReceived.setText(mqttMessage.toString());
                mdp.valueOf(mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    



    
    
    






    private Boolean runtimetime_permission() {
        if(Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;


    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}

