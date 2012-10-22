package game.shad.tempus.hearts;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


public class Game extends Activity{
	public static final String TAG = "Hearts--Game";
	private Context context;
	//current issues
	//the pickup pile method needs to correctly pick who wins.  using when it was played needs aditional playstate check to confirm seat.
	//playing someone elses cards....needs testing
	// when doing the Play2clubs can play Ace instead due to getting the first item in array
	//BOTS ARE CHEATING AND NOT PLAYING CARDS WHEN THEY SHOULD!!
	//Points are not being kept...p1+p2 do odd things
	//
	public String eol = System.getProperty("line.separator");
	public String name;
	public Deck deck=new Deck();
	public int[] cardArrayRes= new int[52];
	public Card[] deckCards=new Card[4];
    public ArrayList<Card> pile=new ArrayList<Card>();
    public Deck blankCards=new Deck();

    public ArrayList<ArrayList<Card>> roundHands= new ArrayList<ArrayList<Card>>(); 
	
    public Player p1;
	public Player p2;
	public Player p3;
	public Player p4;
	public Player curPlayer;
	public Card cardToPlay;
	public Bundle gameIntent;
	public Canvas canvas = new Canvas();
	public Paint paint = new Paint();

	public int selectedCard=0;
	public int selectedCardSuit=-1;
		
	int clubsPlayedInt=0;
	int diamondsPlayedInt=0;
	int spadesPlayedInt=0;
	int heartsPlayedInt=0;
	
	
    public EditText et1;
    //Booleans for setting game states
    public boolean debug = false;
    public boolean playing = false;  //initialized to false but set true during check for 2
    public boolean heartsBroken;
    public boolean restart = false;
    public boolean voidHelper;
    public boolean playerHelper;
    public boolean newRound=false;
    public boolean trading =false; //TODO set to true when trading
    
    
    
    public int round=1;
    public int count=0;
    public int players=4;
    public int difficulty=1;
	int size;

	public int shuffleType;
	int playerHelperInt=0;
   
	
    
	
    public Game(Bundle gameIntent2, Context context) {
        Bundle b = gameIntent2;
        this.context = context;
        this.name=(String) b.get("name").toString().trim();
        this.voidHelper = (Boolean) b.get("voidHelper");
        this.playerHelper = (Boolean) b.get("playerHelper");
        Log.d(TAG, "player helper is "+playerHelper);
        Log.d(TAG, this.name);
        if (this.name.equalsIgnoreCase("Your name")||this.name.equals("")){
        	this.name = "You";
        }
        this.difficulty =  (Integer) b.get("diff");
        this.shuffleType =  (Integer) b.get("shuffle");
        Log.d(TAG, "shuffle type= "+shuffleType);

        Log.d(TAG, "difficulty= "+difficulty);
               
        this.restart = (Boolean) b.get("restart");
//        createBlankHand();
       //start(); 

	}

    

	public void clearALL(){
		//TODO fix cleartablecards
		//clearTableCards();
		p1 = null;
		p2 = null;
		p3 = null;
		p4 = null;
		playing = false;
		heartsBroken=false;
	    round=1;
	    count=0;
		
	}
		
	public void createBlankHand(){	
		Deck deck = new Deck();
			for(int value=0;value<7;value++){	//Blue back
				Card cd = new Card(0, 0, context);
				deck.addCard(cd);
			}
			for(int value=0;value<6;value++){	//RedBack
				Card cd = new Card(3, 0, context);
				deck.addCard(cd);
			}
			blankCards.clearALL();
			blankCards.addAllCards(deck);
	}
	/**
	 * Finds lowest scoring player and sets winner to true.
	 * Does less than or equal to...should be just less than or tie.
	 */
	public Player winnerCheck(){//this prob needs some rework
		int scorep1 = p1.getScore();
		int scorep2 = p2.getScore();
		int scorep3 = p3.getScore();
		int scorep4 = p4.getScore();

		if(scorep1<=scorep2){
			if(scorep1<=scorep3){
				if(scorep1<=scorep4){
					Log.d(TAG, "YOU WON");
					p1.winner=true;
					return p1;
				}
				else{
					Log.d(TAG, "P4 WON");
					p4.winner=true;
					return p4;
				}
			}
			else if(scorep3<=scorep4){
				Log.d(TAG, "P3 WON");
				p3.winner=true;
				return p3;
			}

		}
		else if(scorep2<=scorep3){
			if(scorep2<=scorep4){
				Log.d(TAG, "P2 WON");
				p2.winner=true;
				return p2;
			}
			else{
				Log.d(TAG, "P4 WON");
				p4.winner=true;
				return p4;
				
			}
		}
		else if(scorep3<=scorep4){
				Log.d(TAG, "P3 WON");
				p3.winner=true;
				return p3;
			}
			else{
				Log.d(TAG, "P4 WON");
				p4.winner=true;
				return p4;
				
			}
		Log.d(TAG, "returning null on winner");
		return null;
		}
		
	public boolean endGameCheck(){
		if(p1.getScore()>=100){
			Log.d(TAG, "Player 1 LOOSES");
			return true;
		}
		if(p2.getScore()>=100){
			Log.d(TAG, "Player 2 LOOSES");
			return true;

		}
		if(p3.getScore()>=100){
			Log.d(TAG, "Player 3 LOOSES");
			return true;

		}
		if(p4.getScore()>=100){
			Log.d(TAG, "Player 4 LOOSES");
			return true;

		}
		return false;  //Nobody has too many points
		//TODO End game mode, find winner, show scores, 
	}
	

	public Player nextPlayer(Player p){
		switch(p.getSeat()){
			case 1:
				return this.p2;		
			case 2:
				return this.p3;		
			case 3:
				return this.p4;		
			case 4:
				playing=true;
				return this.p1;
		}
		return null;
	}
	
	
	public void showHand(Player p){
		Deck hand = p.gethand();
		String wholeHand=p.getRealName()+" ";
		for(int i=0;i<hand.getSize();i++){
			wholeHand+=hand.getCard(i).name+", ";
		}
		Log.d(TAG, wholeHand);
		Log.d(TAG, "total="+hand.getSize());
	}
	
	

}















