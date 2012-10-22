package game.shad.tempus.hearts;




import game.shad.tempus.hearts.GameThread.State;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Starts the craps game,
 * 
 * @author Shadoath
 *
 */
public class MainActivity extends Activity {
	/** For use with Android logging functions */
	public static final String TAG = "Hearts--Main";
	
	public Game game;
	public GameView gameView;
	public GameThread gt;
	public GameView view;
    private Toast myToast;

	public Handler handler; //Handler to UI thread to post tasks to.
	private WakeLock mWakeLock;
	private boolean loaded =false;
	private Bundle gameBundle;
	private Context appContext;
	private MainActivity main;
    @Override
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent gameIntent = getIntent();
        gameBundle = gameIntent.getExtras();
        Log.d(TAG, "onCreate");
        setContentView(R.layout.table);
        appContext = getApplicationContext();
    	main= this;
    	game = new Game(gameBundle, getApplicationContext());
		gameView = new GameView(appContext, main, game, gameBundle.getInt("width"), gameBundle.getInt("height"));
		gt = new GameThread(appContext, main, game, gameView);
        myToast  = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
    	gameView.createViews();
        Log.d(TAG, "created Views");
        gt.firstInit();
        gameView.clubsPlayed.setText("C="+game.clubsPlayedInt);
		gameView.diamondsPlayed.setText("D="+game.diamondsPlayedInt);
		gameView.spadesPlayed.setText("A="+game.spadesPlayedInt);
		gameView.heartsPlayed.setText("H="+game.heartsPlayedInt);
		int total=game.clubsPlayedInt+game.diamondsPlayedInt+game.spadesPlayedInt+game.heartsPlayedInt;
		gameView.totalPlayed.setText("total= "+total);
    	
    }
    
	@Override
    protected void onStart(){
    	super.onStart();
    	Log.d(TAG, "onStart");       
        handler= new Handler();
        if(gt==null){
        	return;
        }
        if(gt.state.compareAndSet(State.PAUSED, State.RUNNING)){
      		Log.d(TAG, "Restarting game thread in start");
	        gt.interrupt();
      	}


  	}
	
    @Override
    protected void onPause() {
	    Log.d(TAG, "onPause");
	
	    super.onPause();
		if(gt.state.compareAndSet(State.RUNNING, State.PAUSED)) {
			gt.interrupt();
		}
            
    }
        
    @Override
    protected void onResume() {
    	Log.d(TAG, "onResume");
    	super.onResume();

		if(gt.state.compareAndSet(State.PAUSED, State.RUNNING)) {
			gt.interrupt();

		}
		//view.setInitializedTo(true);

    }
    
    @Override
    public void onStop() {
    	Log.d(TAG, "onStop");
    	super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	
    	Log.d(TAG, "onDestroy");

    	super.onDestroy();
    	gt.state.set(State.DEAD);
    	gt.interrupt();
    }
    
    @Override
    public void onRestart(){
    	super.onRestart();
    	Log.d(TAG, "onRestart");
    	Log.d(TAG, "gt.state="+gt.state);
    }

    public void onDebugButtonPressed(View v){
    	gameView.setInitializedTo(true);
		game.showHand(game.p1);
		game.showHand(game.p2);
		game.showHand(game.p3);
		game.showHand(game.p4);
		Toast.makeText(this, "Printed Hands",  Toast.LENGTH_SHORT).show();
		game.p1.updateDeck();
		gameView.deckHolder.updateDeck(game.p1.getDeck());
		gameView.deckHolder.initialized=true;
		game.playing=!game.playing;
				

	}
	public void onNextButtonPressed(View v){
		if(game.playing){
			
			if(game.playerHelper&&game.playerHelperInt>0){
				game.playerHelperInt=0;
				gt.GO();
				
			}
			else{
				game.playerHelperInt++;
				Toast.makeText(this, "Please play card",  Toast.LENGTH_SHORT).show();
			}
		}
		else{
			gt.GO();
		}

	}
	public void onTablePressed(View v){
		Log.d(TAG, "onClearPressed");
        gameView.updateTH();

	}
	public void onDeckPressed(View v){
        gameView.updateDH();

	}
	public void onExitPressed(View v){
		finish();
	}
	
	public void onSwipeLeftPressed(View v){
		gameView.deckHolder.swipeLeft();
		Toast.makeText(this, "position is "+gameView.deckHolder.getPosition(),  Toast.LENGTH_SHORT).show();

	}
	
	public void onSwipeRightPressed(View v){
		gameView.deckHolder.swipeRight();
		Toast.makeText(this, "position is "+gameView.deckHolder.getPosition(),  Toast.LENGTH_SHORT).show();
		

	}

	
	public void onPlayCardPressed(View v){
		if(game.cardToPlay!=null&&game.playing){ //make sure we have a card selected and we have not already played.
			if(game.pile.size()==0){
				gameView.clearTableCards();
			}
			game.cardToPlay.setTouched(false);
			game.pile.add(game.cardToPlay);			
			game.playing=false;
			this.game.p1.deck.removeCard(game.cardToPlay);
			gt.playCard(game.cardToPlay);
			game.cardToPlay=null;
		}
		else{
			Toast.makeText(this, "Not your turn",  Toast.LENGTH_SHORT).show();
		}
		game.playing=false;
		game.p1.updateSuits();
		gameView.deckHolder.updateDeck(game.p1.getDeck());

		

	}
}






