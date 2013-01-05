package com.umich.umd.obdpractice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
// import android.widget.TextView;

public class MainActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void sendMessage(View view) 
    {
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	// TextView textView = (TextView) findViewById(R.id.edit_message);
    	// String message = textView.getText().toString();
    	// intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);    	
    }
    
    public void setupNetwork(View view)
    {
    	Intent intent = new Intent(this,NetworkSetupActivity.class);
    	startActivity(intent);
    	
    }
    
    public void startLogging(View view)
    {
    	
    }
    
    public void monitorGryphon(View view)
    {
    	
    }
    
    public void connectCloud(View view)
    {
    	
    }
    
    public void viewStorage(View view)
    {
    	
    }
    
}
