package game.shad.tempus.hearts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

public class GameThread extends Thread
{
	public static final String TAG = "Hearts--GameThread";

	public enum State {
		RUNNING(), PAUSED(), DEAD();
	}
	public volatile AtomicReference<State> state = new AtomicReference<GameThread.State>(State.PAUSED);
	public String eol = System.getProperty("line.separator");

    private final static int MAX_FPS = 50;
    private final static int MAX_FRAME_SKIPS = 5;
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;
    
    private DecimalFormat df = new DecimalFormat("0.##");
    private final static int STAT_INTERVAL = 1000;
    private final static int FPS_HISTORY_NR = 10;
    private long statusIntervalTimer = 0l;
    private long totalFramesSkipped = 0l;
    private long framesSkippedPerStatCycle = 0l;
    
    private int frameCountPerStatCycle = 0;
    private long totalFrameCount = 0l;
    private double fpsStore[];
    private long statsCount = 0;
    private double averageFps = 0.0;
    
    private SurfaceHolder surfaceHolder;
    private Game game;
    private MainActivity main;
    private Canvas canvas;
    private Context context;
    public GameView gameView;
    private boolean running;
	private boolean firstStart = false;
	private boolean justPickedUpPile = false;

    public void setRunning(boolean running){
        this.running = running;
    }
    
    public GameThread(Context context, MainActivity main, Game game, GameView gameView){
        super();
        this.gameView = gameView;
        this.context = context;
        this.main = main;
        this.game = game;
    }
    public void firstInit(){
    	heartsMe();
        main.gt.start();

    }
    public void run(){
    	Thread.currentThread().setName("Craps Game Thread");
		
		while (state.get() != State.DEAD) {
			try{
				if(state.get() == State.PAUSED) {
					sleep(5000);
				}
				pause(420);
				//TODO if trading swap cards
				//Detect when the table has 4 cards
				//Deals with selected Card
				//Depending on How user selects card deselecting last card could be tricky.
				//
				
				
	            main.gameView.update();
  
            } catch(InterruptedException e){
				Log.d(TAG, "Interrupted Exception!!! state=" + state.get(), e);
            }
		}
    }
    
    public void heartsMe(){ 	
		Log.d(TAG, "start()");
		
		//should not need to make deck, just shuffle the old one.
	    if(game.restart){
	    	//TODO reset all points
			Log.d(TAG, "Game first start");
	    	makeDeck();
			shuffle();
	        deal();
	        //This needs to be done later trade();  //ha ha more like set who to trade too.
	        //TODO let play select cards and trade.
	        voidCheck();
	        //NOW IN THREAD
	        //createViews();
	        //TODO trade two
	        checkForTwoMethod();//This more or less starts the game.
	        displayCards(game.p1);
	    }
	
	    else{
	    	Log.d(TAG, "New round delt");
	    	//New round keep playing
			makeDeck();
	    	shuffle();	
	        deal();
	        //TODO trade()
	        voidCheck();
	        //createViews();
	        
	        checkForTwoMethod();//This more or less starts the game.
	        displayCards(game.p1);
	    }
	    //thread.setRunning(true);
	
	   // thread.run();
	  //DECKHOLDER
	    
	}
	
	/**
	 * creates 52 cards 13 of each suit.
	 */
	public void makeDeck() { 
		Log.d(TAG, "make Deck");
		game.deck.clearALL();
		for(int suit=0;suit<4;suit++){			
			for(int value=1;value<14;value++){
				Card cd = new Card(value, suit, context);
				game.deck.addCard(cd);
			}
	
		}
		
	}
	/**
	 * WOULD NOT BET LIFE ON THIS SHUFFLE!!!
	 * Probably a very uneven shuffle.
	 */
	public void shuffle(){
		Log.d(TAG, "shuffle");
			if(game.shuffleType==1){
				Log.d(TAG, "shuffle type 1");
	
				//New shuffle going in 8/16
				int x = 0;
				int z = 50;
				int total =0;
				
				Deck deck2 = new Deck();
				Deck deck3 = new Deck();
				Deck deck4 = new Deck();
				deck2.addAllCards(game.deck);
				int j=0;
				int a=(int)  (Math.random()*7)+1;
				int dLength=0;;
				boolean stop = false;
				while(x<z&&!stop){
					x++;
					dLength=deck2.getSize();
					while(dLength>0){
						if(dLength<=a){
							Log.d(TAG, "STOP, too Small. "+dLength);
							deck3.addAllCards(deck2);
							deck2.clearALL();
							a=-1;	//must be negative or it will go into the while loop
							stop=true;
							//maybe just break and drop all cards.
						}		
						//print ("a="+a, 449);
						while(a>=0){
							deck3.addCard(deck2.getCard(a));
							deck2.removeCardAtIndex(a);
							a--;	//goes to -1, but thats ok
							j++;	//seemed like it adds an extra but a>=0 is why.
						}
						
						deck4.addAllCards(deck3);
						deck3.clearALL();
						
						a=(int) (Math.random()*7)+1;
						dLength=deck2.getSize();
						//Log.d(TAG, "deck2= "+dLength, 461);
						//Log.d(TAG, "deck3= "+deck3.getSize(), 462);
						//Log.d(TAG, "deck4= "+deck4.getSize(), 463);
					}
					//deck2.clearALL();  should be empty
					stop=false;
					deck2.addAllCards(deck4);  //dont use = it makes them clones of each other
					deck4.clearALL();
					Log.d(TAG, "x="+x+"  z="+z);
					
				}
				Log.d(TAG, "j is "+j);
				game.deck.clearALL();
				game.deck.addAllCards(deck2);
			}
			else if(game.shuffleType==2){	//this shuffle may not work after changing everything to Deck...
				Log.d(TAG, "shuffle type 2");
				int x=0;
				int z=50;//times to loop the deck and 'randomly' switch cards.
				int r = 51;
				boolean stop=false;
				Deck deck2 = new Deck();
		
				while(x<z){
					x++;
					int j=0;
					int a=(int) (Math.random()*r);
					for(int i=52; i>0&&!stop; i--){
						int loop=0;
						while(game.deck.getCard(a)!=null&&!stop){
							loop++;
							a=(int) (Math.random()*r);
							if(loop>15&&!stop){ //careful on this Set loop to 15 from 10.
								r = 0;
								for(int q=0; q<game.deck.getSize();q++){//Random math not finding cards, Empty rest and start again.
									if(game.deck.getCard(q)!=null){
										deck2.addCardAtIndex(q, game.deck.getCard(q));
										r++;
										j++;
									}
									
									
								}
								stop=true;
								
								
							}
						}
						if(!stop){
							deck2.addCardAtIndex(a, game.deck.getCard(a));
							game.deck.addCardAtIndex(a, null);			
							j++;
						}
					}
					
					game.deck=deck2;
					
					
					
				}
			}
			else{
				//TODO finish.
				Log.d(TAG, "shuffle type 3");
				int x = 0;
				int z = 50;
				int total =0;
				ArrayList<Deck> decks= new ArrayList<Deck>();
				Deck deck2 = new Deck();
				deck2.addAllCards(game.deck);
				while(x<z){
					decks.addAll(splitDeck(deck2));
					Log.d(TAG, "the deck is "+decks.size());
					deck2.clearALL();
					deck2.addAllCards(mixOutIn(decks));
					decks.clear();
					x++;
				}	
				
				
				
				
				
			}
		}
	
	/**
	 * Takes a Deck and returns an array of decks, 11 of them 5 per array and 2 in the last one.
	 * @param a The Deck to be split
	 * @return	ArrayList<Deck>(size = 11)
	 */
	private ArrayList<Deck> splitDeck(Deck a) {
		ArrayList<Deck> decks = new ArrayList<Deck>();
		for(int i =0; i<10;i++){
			Deck deck2 = new Deck();
	
			int t=0;
			while(t<5){
				deck2.addCard(a.getCard(t));
				t++;
			}
			t--;
			while(t>=0){
				a.removeCardAtIndex(t);
				t--;
			}
			decks.add(deck2);
		}
		decks.add(a);
		return decks;
	}
	
	/**
	 * Mix an ArrayList<Deck> into 
	 * @param a
	 * @return
	 */
	
	
	private Deck mixOutIn(ArrayList<Deck> a){
		int size = a.size();
		boolean extra=false;
		if(size%2==1){
			size--;
			extra=true;
		}
		Deck d= new Deck();
		for(int i =0;i<size/2;i++){
			d.addAllCards(mixDecks(a.get(i), a.get(size-i-1)));
		}
		d.addAllCards(a.get(size/2));
		
		return d;
	}
	
	public Deck mixDecks(Deck a, Deck b){
		for(int i=0; i<a.getSize();i++){
			b.addCardAtIndex(i+i, a.getCard(i));
		}
		return b;
	}
	
	public ArrayList<Card> addLowHigh(ArrayList<Card> fromDeck){
		ArrayList<Card> toDeck= new ArrayList<Card>();
		for(int i=0;i<fromDeck.size();i++){
			toDeck.add(fromDeck.get(i));
		}
		return toDeck;
	}
	
	public ArrayList<Card> addHighLow(ArrayList<Card> fromDeck){
		ArrayList<Card> toDeck= new ArrayList<Card>();
		for(int i=fromDeck.size()-1;i>=0;i--){
			toDeck.add(fromDeck.get(i));
		}
		return toDeck;
	}
	
	
	/**
		* Create the and deals them out and then finds the two
	 * NEEDS SHUFFLE.
	 * */
	public void deal() {
		Log.d(TAG, "dealing");
		Deck hand1 = new Deck();
		Deck hand2 = new Deck();
		Deck hand3 = new Deck();
		Deck hand4 = new Deck();
		for(int i=0;i<game.deck.getSize();i+=4){
			int d1=i;
			int d2=i+1;
			int d3=i+2;
			int d4=i+3;
			hand1.addCard(game.deck.getCard(d1));
			hand2.addCard(game.deck.getCard(d2));
			hand3.addCard(game.deck.getCard(d3));
			hand4.addCard(game.deck.getCard(d4));
			
			
		}
		if(game.p1==null||game.restart){//first Start  or restart called
			Log.d(TAG, "New hands dealt.");
			int b = Color.rgb(0, 50 , 200);
			game.p1 = new Player(game, hand1, 0, 1, game.name, Color.WHITE); 
			game.p2 = new Player(game, hand2, 0, 2, "Chuck  (P2)", Color.RED);
			game.p3 = new Player(game, hand3, 0, 3, "Skippy  (P3)", b);
			game.p4 = new Player(game, hand4, 0, 4, "Jeff  (P4)", Color.YELLOW);
			gameView.deckHolder.addDeck(hand1);
			//p2.setColor(Color.RED);
		}
		else {//else new round and New cards are dealt
			Log.d(TAG, "new round being delt");
			game.p1.sethand(hand1);
			game.p2.sethand(hand2);
			game.p3.sethand(hand3);
			game.p4.sethand(hand4);
		}			
		
		//TODO if resume code 
		/* New Deal to work the proper way.
		ArrayList<card> hand1 = new ArrayList<card>();
		ArrayList<card> hand2 = new ArrayList<card>();
		ArrayList<card> hand3 = new ArrayList<card>();
		ArrayList<card> hand4 = new ArrayList<card>();
	
		for(int i=0;i<13;i++){//could be buggy; also could be shot for shuffling like this.
			int d1=i;
			int d2=13+i;
			int d3=26+i;
			int d4=39+i;
			hand1.add(deck[d1]);
			hand2.add(deck[d2]);
			hand3.add(deck[d3]);
			hand4.add(deck[d4]);
		}
		if(p1==null||restart){//first Start  or restart called
			Log.d(TAG, "New hands dealt.", 483);
			int b = Color.rgb(0, 50 , 200);
			p1 = new Player(hand1, 0, 1, name, Color.WHITE); 
			p2 = new Player(hand2, 0, 2, "Chuck  (P2)", Color.RED);
			p3 = new Player(hand3, 0, 3, "Skippy  (P3)", b);
			p4 = new Player(hand4, 0, 4, "Jeff  (P4)", Color.YELLOW);
			//p2.setColor(Color.RED);
		}
		else if(count==0){//else new round and New cards are dealt
			Log.d(TAG, "new round being delt", 492);
			p1.sethand(hand1);
			p2.sethand(hand2);
			p3.sethand(hand3);
			p4.sethand(hand4);
		}
		else{
			Log.d(TAG, "IMPORTANT STUFF", 494);
			//we are loading an old game.....
			//TODO more code for setting it up right?
			
		}	
	*/
		
	}
	
	
	/**
	 * Call on round 0;
	 * Finds who has the 2 of clubs and forces them to play it
	 * Sets the states for the next round
	 * Then advances the curPlayer
	 */
	public void checkForTwoMethod(){
		Log.d(TAG, "checkForTwoMethod() called");
		if(game.p1.checkForTwo()){
			Log.d(TAG, "p1 played the 2 of clubs");
			game.curPlayer=game.p1;
			//tableCard1.setText(selectedCardSuit+"-"+selectedCard);  //TODO remove this line
			//playing=true;
			//New code to set up each round play state then not modify till next round.
			game.p1.setState(1);
			game.p2.setState(2);
			game.p3.setState(3);
			game.p4.setState(4);
			playTwoOfClubs();
			gameView.displayCards(game.p1);
	
	
		}
		else if(game.p2.checkForTwo()){
			Log.d(TAG, "p2 plays 2 of clubs");
			game.curPlayer=game.p2;
			game.p1.setState(4);
			game.p2.setState(1);
			game.p3.setState(2);
			game.p4.setState(3);
			playTwoOfClubs();
			}
		else if(game.p3.checkForTwo()){
			Log.d(TAG, "p3 plays 2 of clubs");
			game.curPlayer=game.p3;
			game.p1.setState(3);
			game.p2.setState(4);
			game.p3.setState(1);
			game.p4.setState(2);
			playTwoOfClubs();
		}
		else if(game.p4.checkForTwo()){
			Log.d(TAG, "p4 plays 2 of clubs");
			game.curPlayer=game.p4;
			game.p1.setState(2);
			game.p2.setState(3);
			game.p3.setState(4);
			game.p4.setState(1);
			playTwoOfClubs();
		}
		else{
			Log.d(TAG, "The game has already started.");
		}
		Log.d(TAG, " 2 check done--curPlayer="+game.curPlayer.getRealName());
		}
	    
	public void voidCheck(){
	    game.p1.checkForVoids();
	    game.p2.checkForVoids();     
	    game.p3.checkForVoids();
	    game.p4.checkForVoids();
	}

	public void displayCards(Player p){ 
		Deck c = p.getClubs();
		Deck d = p.getDiamonds();
		Deck s = p.getSpades();
		Deck h = p.getHearts();
		String clubs = "";
		String diamonds = "";
		String spades = "";
		String hearts = "";
		for(int i=0; i<c.getSize();i++){
			clubs+=c.getCard(i).getValue()+", ";
		}
		for(int i=0; i<d.getSize();i++){
			diamonds+=d.getCard(i).getValue()+", ";
		}
		for(int i=0; i<s.getSize();i++){
			spades+=s.getCard(i).getValue()+", ";
		}
		for(int i=0; i<h.getSize();i++){
			hearts+=h.getCard(i).getValue()+", ";
		}
	
		switch(p.getSeat()){
			case 1:
				gameView.p1tvScore.setText("P1:"+game.p1.getScore());
				break;
			case 2:
				gameView.p2tvScore.setText("P2:"+game.p2.getScore());
		        break;
				//TODO
			case 3:
				gameView.p3tvScore.setText("P3:"+game.p3.getScore());
		        break;
				//TODO	
			case 4:
		        gameView.p4tvScore.setText("P4:"+game.p4.getScore());
		        break;
				//TODO
		}

		
	}

	
	/**
	 * Plays the selected card and advances the curPlayer
	 * @param p seat of person who is playing a card
	 * @param rs the string to be set to the layout.
	 */
		
	/**
	 * Sets the first round of the game going
	 * Plays two and then sets next CurPlayer
	 */
	public void playTwoOfClubs(){
//		Card nextCard=(game.curPlayer.clubs.getCard(0));
//		if(nextCard.getValue()!=2){  //has the one and the two of clubs
//			nextCard=(game.curPlayer.clubs.getCard(1));
//			game.curPlayer.clubs.removeCardAtIndex(1);//Thus only remove 2
//		}
//		else{
//			game.curPlayer.clubs.removeCardAtIndex(0); //Does not have Ace thus remove place one.
//		}
		Card nextCard = game.curPlayer.twoOfClubs;
		game.curPlayer.deck.removeCard(nextCard);
		game.curPlayer.updateSuits();
		Log.d(TAG, "game started by "+game.curPlayer.getRealName());
		game.pile.add(nextCard);
		gameView.bottomText2.setText(game.curPlayer.getRealName()+" played the 2 of Clubs. ");
		playCard(nextCard);
		
		Log.d(TAG, "0101010");
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

	public void GO(){
		Log.d(TAG, "GO()");
		gameView.roundView.setText("Round="+game.round);
		Card nextCard=(game.curPlayer.go(game.round, game.pile, game.curPlayer));
		game.pile.add(nextCard);
		Log.d(TAG, "###"+game.curPlayer.realName+" played a "+nextCard.name+". Pile size="+game.pile.size() );

		gameView.bottomText2.setText(game.curPlayer.getRealName()+" played the "+ nextCard.name);
		game.curPlayer.updateDeck();

		playCard(nextCard);

	}
	
	public void playCard(Card nextCard){
		if(justPickedUpPile){
			gameView.clearTableCards();
			this.justPickedUpPile=false;
		}
		this.gameView.addTableCard(nextCard);
		if(game.pile.size()>=4){
			Log.d(TAG, "picking up hand");
			pickUpHand();
		}
		else{
			this.game.curPlayer=nextPlayer(game.curPlayer);	//this is the error line
		}
		gameView.bottomText.setText("Current player is "+game.curPlayer.getRealName());

	}
	
	public Player nextPlayer(Player p){
		switch(p.getSeat()){
			case 1:
				game.playing=false;
				return game.p2;		
			case 2:
				return game.p3;		
			case 3:
				return game.p4;		
			case 4:
				game.playing=true;
				return game.p1;
		}
		return null;
	}

	public void setCurPlayer(Player p){
		game.curPlayer = p;
	}
	
	public void pickUpHand(){
		game.roundHands.add(game.pile);
		this.justPickedUpPile=true;
		int high = 0;
		int highSeat  = 0;
		int points = 0;
		int firstSuit=game.pile.get(0).getSuit();
		for(int i=0;i<game.pile.size();i++){		// maybe start at the back of the pile...yet it does not really matter
			game.pile.get(i).setPlayed(true);	//just added Not sdfused yet but could be used to check what cards have been played on crash.
			int curSuit=game.pile.get(i).getSuit();
			int curCard =game.pile.get(i).getValue();
			if(firstSuit==curSuit){  	
				if(high<=curCard){ //equals so that p1 can take the lead.
					high=curCard;
					highSeat=game.pile.get(i).getOwner().getSeat();//Seats are 1-4 
				}
			}
			if(curSuit==0){
				game.clubsPlayedInt++;
			}
			
			else if(curSuit==1){ //Diamonds  check for jack.
				game.diamondsPlayedInt++;
				if(curCard==11){
					points-=10;
					Toast.makeText(context, "Jack - 10",  Toast.LENGTH_SHORT).show();

				}
			}
			else if(curSuit==2){//Spades   check for queen
				game.spadesPlayedInt++;

				if(curCard==12){
					game.heartsBroken=true;
					points+=13;
					Toast.makeText(context, "Queen + 13",  Toast.LENGTH_SHORT).show();

				}
			}
			else if(curSuit==3){//heart--add points
				game.heartsPlayedInt++;
				game.heartsBroken=true;
				points++;
			}
				
		}
		gameView.clubsPlayed.setText("C="+game.clubsPlayedInt);
		gameView.diamondsPlayed.setText("D="+game.diamondsPlayedInt);
		gameView.spadesPlayed.setText("S="+game.spadesPlayedInt);
		gameView.heartsPlayed.setText("H="+game.heartsPlayedInt);
		int total=game.clubsPlayedInt+game.diamondsPlayedInt+game.spadesPlayedInt+game.heartsPlayedInt;
		gameView.totalPlayed.setText("total= "+total);
		switch(highSeat){
		case 1:
			game.curPlayer=game.p1;
			game.p1.addToScore(points);
			gameView. p1tvScore.setText("P1:"+game.p1.getScore());
			game.playing=true;
			gameView.bottomText2.setText("You won, points="+points);
			gameView.bottomText2.append(eol+"Pick a Start Card");
			
			break;
		case 2:
			game.curPlayer=game.p2;
			game.p2.addToScore(points);
			gameView.p2tvScore.setText("P2:"+game.p2.getScore());
			gameView.bottomText2.append(eol+"P2 won, points="+points);
			
			break;
		case 3:
			game.curPlayer=game.p3;
			game.p3.addToScore(points);
			gameView.p3tvScore.setText("P3:"+game.p3.getScore());
			gameView.bottomText2.append(eol+"P3 won, points="+points);
			
			break;
		case 4: 
			game.curPlayer=game.p4;
			game.p4.addToScore(points);
			gameView.p4tvScore.setText("P4:"+game.p4.getScore());
			gameView.bottomText2.append(eol+"P4 won, taking="+points+" points");
			break;
		}	
		Log.d(TAG, "pickUpHand() for "+game.curPlayer.getRealName());
		game.curPlayer.addDeckItem(game.pile);
		game.pile.clear();
		setCurPlayer(game.curPlayer);  //for next round.


		game.round++;	//cards left counter
		if(game.round==14){//Done playing need new cards.'
			game.count++;
			game.round=1;  	//ROUND RESET!!!
			if(game.endGameCheck()){
				game.curPlayer =game.winnerCheck();
				gameView.bottomText.setText(game.curPlayer.getRealName()+" won the game!");
				gameView.bottomText2.setText("Press menu, then restart to play again");

			}
			else{
				game.newRound=true;
				Log.d(TAG, "New round");
				game.restart=false;
				game.clubsPlayedInt=0;
				game.diamondsPlayedInt=0;
				game.spadesPlayedInt=0;
				game.heartsPlayedInt=0;
				Toast.makeText(context, "New Round Dealt",  Toast.LENGTH_LONG).show();

				heartsMe();
			}
			
		}
		
				
	}
	
	
	
	
    private long lastUpdate = System.currentTimeMillis();
	private long timeElapsed = 1;
	private void pause(long ms) throws InterruptedException{
		//System.out.println(ms - System.currentTimeMillis() + lastUpdate);
		Thread.sleep(Math.max(0, ms - System.currentTimeMillis() + lastUpdate));
	
		timeElapsed = System.currentTimeMillis() - lastUpdate;
		lastUpdate = System.currentTimeMillis();
	}

	private void storeStats() {
        frameCountPerStatCycle++;
        totalFrameCount++;
        statusIntervalTimer += FRAME_PERIOD;
        
        if(statusIntervalTimer >= STAT_INTERVAL){
            double actualFps = (double)(frameCountPerStatCycle / (STAT_INTERVAL / 1000));
            fpsStore[(int)statsCount % FPS_HISTORY_NR] = actualFps;
            statsCount++;
            double totalFps = 0.0;
            
            for(int i =0; i < FPS_HISTORY_NR; i++) {
                totalFps += fpsStore[i];
            }
            
            if(statsCount < FPS_HISTORY_NR){
                averageFps = totalFps / statsCount;
            }else {
                averageFps = totalFps / FPS_HISTORY_NR;
            }
            
            totalFramesSkipped += framesSkippedPerStatCycle;
            framesSkippedPerStatCycle = 0;
            statusIntervalTimer = 0;
            frameCountPerStatCycle = 0;
            //game.setAvgFps("FPS: " + df.format(averageFps));
            
        }
    }
    
    private void initTimingElements(){
        fpsStore = new double[FPS_HISTORY_NR];
        for(int i = 0; i < FPS_HISTORY_NR; i++){
            fpsStore[i] = 0.0;
        }
        Log.d(TAG + ".initTimingElements()","Timing elements for stats intialized");
    }
    

}
