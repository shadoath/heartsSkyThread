package game.shad.tempus.hearts;


import game.shad.tempus.hearts.GameThread.State;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class MainMenu extends Activity{
	public static final String TAG = "Hearts--Main";

	EditText et;
	public Game game;
	public GameThread gt;
	public GameView gameView;
	public Handler handler; //Handler to UI thread to post tasks to.

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    	et = (EditText) findViewById(R.id.playerName);
    	et.setText("Your name");

    }
 


	public void onStartPressed(View v){
		Intent gameIntent  =new Intent(this, game.shad.tempus.hearts.MainActivity.class);
		Bundle gameBundle = new Bundle();
		et = (EditText) findViewById(R.id.playerName);
		String  s = et.getText().toString();
		System.out.println(s);
    	gameBundle.putString("name", s);
    	gameBundle.putBoolean("restart", true);
		RadioButton easy = (RadioButton) findViewById(R.id.easy);
		RadioButton medium = (RadioButton) findViewById(R.id.medium);
		RadioButton hard = (RadioButton) findViewById(R.id.hard);

		if(easy.isChecked()){
	    	gameBundle.putInt("diff", 1);
		}
		else if(medium.isChecked()){
	    	gameBundle.putInt("diff", 2);
		}
		else if(hard.isChecked()){
	    	gameBundle.putInt("diff", 3);
		}	

		RadioButton shuffleDrop = (RadioButton) findViewById(R.id.dropShuffle);
		RadioButton shuffleSwap = (RadioButton) findViewById(R.id.randomSwap);
		RadioButton mixShuffle = (RadioButton) findViewById(R.id.mixShuffle);

		if(shuffleDrop.isChecked()){
	    	gameBundle.putInt("shuffle", 1);
		}
		else if(shuffleSwap.isChecked()){
	    	gameBundle.putInt("shuffle", 2);
		}
		else if(mixShuffle.isChecked()){
	    	gameBundle.putInt("shuffle", 3);
		}
		CheckBox playerHelper = (CheckBox) findViewById(R.id.playerHelper);
		boolean ph = playerHelper.isChecked();
		gameBundle.putBoolean("playerHelper", ph);
		CheckBox voidHelper = (CheckBox) findViewById(R.id.trackVoidsBox);
		boolean vh = voidHelper.isChecked();
		gameBundle.putBoolean("voidHelper", vh);
        Display display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();
        int height = display.getHeight();
        gameBundle.putInt("height", height);
        gameBundle.putInt("width", width);
        gameIntent.putExtras(gameBundle);
    	startActivity(gameIntent);
	
	}
    	
    
    
    

	

    
    
 /* Old start
  * 	public void onStartPressed(View v){
		Intent gameIntent  =new Intent(this, game.shad.tempus.hearts.Game.class);
		et = (EditText) findViewById(R.id.playerName);
		String  s = et.getText().toString();
		System.out.println(s);
    	gameIntent.putExtra("name", s);
    	gameIntent.putExtra("restart", true);
		RadioButton easy = (RadioButton) findViewById(R.id.easy);
		RadioButton medium = (RadioButton) findViewById(R.id.medium);
		RadioButton hard = (RadioButton) findViewById(R.id.hard);

		if(easy.isChecked()){
	    	gameIntent.putExtra("diff", 1);
		}
		else if(medium.isChecked()){
	    	gameIntent.putExtra("diff", 2);
		}
		else if(hard.isChecked()){
	    	gameIntent.putExtra("diff", 3);
		}	

		RadioButton shuffleDrop = (RadioButton) findViewById(R.id.dropShuffle);
		RadioButton shuffleSwap = (RadioButton) findViewById(R.id.randomSwap);
		RadioButton mixShuffle = (RadioButton) findViewById(R.id.mixShuffle);

		if(shuffleDrop.isChecked()){
	    	gameIntent.putExtra("shuffle", 1);
		}
		else if(shuffleSwap.isChecked()){
	    	gameIntent.putExtra("shuffle", 2);
		}
		else if(mixShuffle.isChecked()){
	    	gameIntent.putExtra("shuffle", 3);
		}
		CheckBox playerHelper = (CheckBox) findViewById(R.id.playerHelper);
		boolean ph = playerHelper.isChecked();
		gameIntent.putExtra("playerHelper", ph);
		CheckBox voidHelper = (CheckBox) findViewById(R.id.trackVoidsBox);
		boolean vh = voidHelper.isChecked();
		gameIntent.putExtra("voidHelper", vh);
		Bundle b = new Bundle();
		startActivity(gameIntent);
    	// finish();
	}

*/
  
	
	public void onResumePressed(View v){
		Intent gameIntent  =new Intent(this, game.shad.tempus.hearts.Game.class);
		et = (EditText) findViewById(R.id.playerName);
		String  s = et.getText().toString();
		System.out.println(s);
    	gameIntent.putExtra("name", s);
    	gameIntent.putExtra("restart", false);
		RadioButton easy = (RadioButton) findViewById(R.id.easy);
		RadioButton medium = (RadioButton) findViewById(R.id.medium);
		RadioButton hard = (RadioButton) findViewById(R.id.hard);
		if(easy.isChecked()){
	    	gameIntent.putExtra("diff", 1);
		}
		else if(medium.isChecked()){
	    	gameIntent.putExtra("diff", 2);
		}
		else if(hard.isChecked()){
	    	gameIntent.putExtra("diff", 3);
		}	
		
		
		CheckBox voidHelper = (CheckBox) findViewById(R.id.trackVoidsBox);
		boolean vh = voidHelper.isChecked();
		gameIntent.putExtra("voidHelper", vh);
    	startActivity(gameIntent);
    	// finish();
	}
	
	public void textSelected(View v){
		
	}
	
	
	public void todo(View v){
		Toast.makeText(MainMenu.this, "Still in progress...",  Toast.LENGTH_SHORT).show();

	}
	

}