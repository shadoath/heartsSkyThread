package game.shad.tempus.hearts;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ParserError")
public class Player {
	public static final String TAG = "Hearts--Player";
	private Game game;
	ArrayList<Card> hand;
	public ArrayList<ArrayList<Card>> takenHands = new ArrayList<ArrayList<Card>>();
	public Deck deck =new Deck();
	public int seat=0; //position
	public String realName = "";
	public int colorInt = 0;
	public int state = 0;   //using 1-4
	public Player curPlayer;
	public int score = 0;
	public int totalScore=0;
	public int passTo = 0;
	public Deck clubs = new Deck();     //0
	int highClub = 0;
	int lowClub = 0;
	public Deck diamonds = new Deck();  //1
	int highDiamonds = 0;
	int lowDiamonds = 0;
	public Deck spades = new Deck();	   //2
	int highSpades = 0;
	int lowSpades = 0;
	public Deck hearts = new Deck();    //
	int highHearts = 0;
	int lowHearts = 0;
	boolean winner = false;
	boolean voidClubs;
	boolean voidDiamonds;
	boolean voidHearts;
	boolean voidSpades;
	private int IQ;	//Then just a simple slider to change it and easy greater than if statments to do bot AI.
	//This will let the IQ be changed in game. just Pust a slider around the AI portrait.
	public LayoutInflater factory;

	public View textEntryView;
	public Card twoOfClubs = null;
	
	public Player(Game game, Deck deck, int score, int seat, String name, int color){
		this.game = game;
		this.deck = deck;
		this.score = score;
		this.seat = seat;
		this.realName = name;
		this.colorInt = color;
		sortHand();
				

	}
	/**Checks if void in said suit.....Should be redone.
	 * 
	 * this is not called from HeartsActivity but is an internal method.
	 * @param i the suit value.
	 * @return true if void in that suit.
	 */
	
public boolean checkVoid(int i){;
		switch(i){
		case 0:
			if(voidClubs)
				return voidClubs;
			break;
		case 1:
			if(voidDiamonds)
				return voidDiamonds;
			break;
		case 2:
			if(voidSpades)
				return voidSpades;
			break;
		case 3: 
			if(voidHearts)
				return voidHearts;
			break;
		}
		return false;
	}
	

	public boolean canPlaySuit(int i){
		switch (i){
		case 0:
			return(voidClubs);
		case 1:
			return (voidDiamonds);
		case 2:
			return (voidSpades);
		case 3:
			return(voidHearts);
		}
		return false;
	}
	public Card go(int round, ArrayList<Card> pile , Player p){
		curPlayer=p;
		Log.d(TAG, "player.go ="+curPlayer.getRealName());
		if(pile.size()==0){
			return playLow(0); //get two of clubs	
			//TODO add AI smarts.
			//TODO start on different suits.  aka go for voids.
		}
		int suit=pile.get(0).getSuit();
		if(checkVoid(suit)){//This is the void CODE
			Log.d(TAG, "void--PlayHigh("+suit+")");
			if(suit<3){
				//playing next suit
				suit++;
			}
			else{  //suit is too high try for clubs
				//No real problems with sending the wrong suit the call for play high and play low are recursive
				suit=0;
			}
			Log.d(TAG, "going for void state="+state);
			return playHigh(suit);
			//TODO do all code for voids
		}
		Log.d(TAG, "state ="+state);
		switch (state){  //REPLACED ps with STATE, removed call for it in constructor
		case 1:{
			switch (round){
			case 0:
				return playHigh(0);
			case 1: 
				return playHigh(0);
			case 2:
				
			case 3:
				
			case 4:
			case 5:
			case 6:
			case 7:
				Log.d(TAG, "round 0-7 --PlayLow("+suit+")");
				return playLow(suit);
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
				Log.d(TAG, "round 8-12 --PlayLow("+suit+")");
				return playLow(suit);
			}
		}
		case 2:

			switch (round){
			case 0:
			case 1: 
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				Log.d(TAG, "round 0-7 --PlayLow("+suit+")");
				return playLow(suit);
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:			
			case 13:

				Log.d(TAG, "round 8-12 --PlayLow("+suit+")");
				return playLow(suit);
			}
		case 3:		
			switch (round){
			case 0:
			case 1: 
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				Log.d(TAG, "round 8-12 --PlayLow("+suit+")");
				return playLow(suit);
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
				Log.d(TAG, "round 0-7 --PlayLow("+suit+")");
				return playLow(suit);
			}
		case 4:
			if(checkForPoints(pile)){
				Log.d(TAG, "Play low--POINTS!!");
				return playLow(suit);
			}
			else{
				Log.d(TAG, "Play high--no points");
				return playHigh(suit);
			}
			/*
			switch (round){
			case 0:
			case 1: 
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				Log.d(TAG, "round 0-7 --PlayLow("+suit+")", 175);
				return playLow(suit);
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
				Log.d(TAG, "round 8-12 --PlayHigh("+suit+")", 183);
				return playHigh(suit);

			}
			*/
		}
		Log.d(TAG, "Out of all Loops !!SHOULD NOT HAPPEN!!--PlayHigh("+suit+")");
		return playHigh(suit);		
	}

	/**
	 * 
	 * @param suit
	 * @return the lowest card of a given suit.
	 */
	public Card playLow(int suit){
		int outOfCards=0; //TODO
		Card nextCard = null;
		switch(suit){
		case 0:
			if(clubs.getSize()==0){
				return playLow(++suit);
			}
			nextCard=clubs.getCard(0);
			clubs.removeCardAtIndex(0);
			return nextCard;
		case 1:
			if(diamonds.getSize()==0){
				return playLow(++suit);
			}
			nextCard=diamonds.getCard(0);
			diamonds.removeCardAtIndex(0);
			return nextCard;
		case 2:
			if(spades.getSize()==0){
				return playLow(++suit);
			}
			nextCard=spades.getCard(0);
			spades.removeCardAtIndex(0);
			return nextCard;
		case 3: 
			if(hearts.getSize()==0){
				return playLow(0);
			}
			nextCard=hearts.getCard(0);
			hearts.removeCardAtIndex(0);
			return nextCard;
		case 4:
			Log.d(TAG, "something Broken");		
			break;
		}	
		
		return nextCard;
	}
	public Card playHigh(int suit){
		Card nextCard = null;
		switch(suit){
		case 0:
			if(clubs.getSize()==0){  //if suit is empty just recall this method with a higher suit.
				return playHigh(++suit);
			}
			else{
				nextCard = clubs.getCard(clubs.getSize()-1);
				clubs.removeCardAtIndex(clubs.getSize()-1);
				Log.d(TAG, nextCard.cardToString());
				return nextCard;
			}	
		case 1:
			if(diamonds.getSize()==0){
				return playHigh(++suit);
			}
			else{
				nextCard=diamonds.getCard(diamonds.getSize()-1);
				diamonds.removeCardAtIndex(diamonds.getSize()-1);
				Log.d(TAG, nextCard.cardToString());
				return nextCard;
			}
		case 2:
			if(spades.getSize()==0){
				return playHigh(++suit);
			}
			nextCard=spades.getCard(spades.getSize()-1);
			spades.removeCardAtIndex(spades.getSize()-1);
			Log.d(TAG, nextCard.cardToString());
			return nextCard;
		case 3: 
			if(hearts.getSize()==0){
				return playHigh(0);
			}
			nextCard=hearts.getCard(hearts.getSize()-1);
			hearts.removeCardAtIndex(hearts.getSize()-1);
			Log.d(TAG, nextCard.cardToString());
			return nextCard;
		}
		return nextCard;
	}
	

	public void setRealName(String name){
		realName = name;
	}
	
	public String getRealName(){
		return realName;
	}
	
	public void setColor(int c){
		colorInt=c;
	}

	public int getColor(){
		return colorInt;
	}
	public void setSeat(int s) {
		seat=s;
	}
	public int getSeat() {
		return seat;
	}
	public void setValue(int c) {
		seat = c;
	}
	public int getState() {
		return state;
	}
	public void setState(int c) {
		state = c;
	}
	
	public void addDeckItem(ArrayList<Card> h){
		takenHands.add(h);		//deck.
	}
	
	public Deck getDeck(){
		return deck;
	}
	
	public void addToScore(int score){
		this.score += score;
	}
	public void addToTotalScore(int i){
		totalScore+=i;
	}
	public int getScore(){
		return score;
	}
	
	public Deck gethand() {
		return deck;
	}
	public void sethand(Deck deck1) {
		this.deck.clearALL();
		this.deck=deck1;
		sortHand();
	}
	public int getPass() {
		return passTo;
	}
	public void setPass(int passTo) {
		this.passTo=passTo;
	}
    
	public void setClubs(Deck c){
		this.clubs.addAllCards(c);
		Log.d(TAG, "clubs.size="+clubs.getSize());
	}
	public void setDiamonds(Deck d){
		this.diamonds.addAllCards(d);
		Log.d(TAG, "Diamonds.size="+diamonds.getSize());
	}
	public void setSpades(Deck s){
		this.spades.addAllCards(s);
		Log.d(TAG, "Spades.size="+spades.getSize());
	}
	public void setHearts(Deck h){
		this.hearts.addAllCards(h);
		Log.d(TAG, "Hearts.size="+hearts.getSize());
	}
	
	public void setClubsVoid(boolean c){
		voidClubs=c;
		//Is there was easy mode On show that they are void to help Player.
	    //Button p4ClubsB =(Button)findViewById(R.id.p4clubsButton);
	    //p4ClubsB.setText("c=VOID");
	}
	public void setDiamondsVoid(boolean d){
		voidDiamonds=d;
	}
	public void setSpadesVoid(boolean s){
		voidSpades=s;
		
	    //Button p4SpadesB =(Button)findViewById(R.id.p4spadesButton);
	    //p4SpadesB.setText("c=VOID");
	}
	public void setHeartsVoid(boolean h){
		voidHearts=h;
	}
	public Deck getClubs(){
		return clubs;
	}
	public Deck getDiamonds(){
		return diamonds;
	}
	public Deck getSpades(){
		return spades;
	}
	public Deck getHearts(){
		return hearts;
	}
	public boolean checkForTwo(){
		int i;
		for (i=0;i<13;i++){//Should only need to check the first two the rest is overkill.
			if(this.deck.getCard(i).getValue()==(2)&&this.deck.getCard(i).getSuit()==(0)){	
				twoOfClubs = this.deck.getCard(i);
				return true;				
			}
		}
		return false;
	}
	
	public void checkForVoids(){ 	
		int empty =0;
		if(clubs.getSize()==0){
			empty++;
			setClubsVoid(true);
		}
		if(spades.getSize()==0){
			empty++;
			setSpadesVoid(true);
		}
		if(diamonds.getSize()==0){
			empty++;
			setDiamondsVoid(true);
		}
		if(hearts.getSize()==0){
			empty++;
			setHeartsVoid(true);
		}
		if(empty==4){
			Log.d(TAG, "The ship is SINKING!!, we are out of cards");
		}
		//could this game be used to teach people hearts?
		/*
		if(voidHelper){
			clubsButton.setText("c="+clubs.size());
			clubsButton.setBackgroundColor(Color.MAGENTA);
			diamondsButton.setText("d="+diamonds.size());
			diamondsButton.setBackgroundColor(Color.MAGENTA);
			spadesButton.setText("s="+spades.size());
			spadesButton.setBackgroundColor(Color.MAGENTA);
			heartsButton.setText("h="+hearts.size());
			heartsButton.setBackgroundColor(Color.MAGENTA);
		}
		 */

	}
	/**
	 * Takes the deck and assigns all cards to the player
	 * then sorts them into suits
	 * and updates the DeckHolder.
	 * or it should....
	 */
	public void sortHand() {
		Log.d(TAG, "sorting hand for "+this.realName);
		Deck thand = new Deck();
		thand.addAllCards(deck);
		int z=0;
		while(thand.getSize()>z){	//Lets the cards be kept and reviewed later
			thand.getCard(z).setOwner(this);//had some issues this fixed them.
			z++;
		}
		if(z!=13){
			//if this is called the bots are probably cheating!
			Log.d(TAG, "Hand was not 13 cards!!!");
		}
		int hc = 0;  //high club
        int hd = 0;  //high diamond
        int hs = 0;  //high spade
        int hh = 0;  //high heart/
		int clubsCounter = 0;
		int diamondsCounter = 0;
		int spadesCounter = 0;
		int heartsCounter = 0;
		int cc2 = 0; //(clubs counter 2) keep track of counters when rearranging deck
		int dc2 = 0;
		int sc2 = 0;
		int hc2 = 0;
        Deck c = new Deck();  
        Deck d = new Deck();
        Deck s = new Deck();
        Deck h = new Deck();
        for(int i=0;i<thand.getSize();i++){
        	int ncard=thand.getCard(i).getValue();
        	switch(thand.getCard(i).getSuit()){
	        	case 0:	
	        		cc2=clubsCounter;
	        		if(hc>ncard){	        			
	        			while(clubsCounter>0&&ncard<c.getCard(clubsCounter-1).getValue()){
	        				clubsCounter--;
	        			}     					

	        		}
	        		c.addCardAtIndex(clubsCounter, thand.getCard(i));	 
	        		hc=c.getCard(c.getSize()-1).getValue(); 
	        		clubsCounter=cc2;
	        		clubsCounter++;

	        		break;
		    	case 1:

		    		dc2=diamondsCounter;
	        		if(hd>ncard){	   
	        			while(diamondsCounter>0&&ncard<d.getCard(diamondsCounter-1).getValue()){
	        				diamondsCounter--;
	        			}     					

	        		}        			  
	        		d.addCardAtIndex(diamondsCounter, thand.getCard(i));	
	        		hd=d.getCard(d.getSize()-1).getValue();
	        		diamondsCounter=dc2;
	        		diamondsCounter++;

		    		break;
		    	case 2:

	        		sc2=spadesCounter;
	        		if(hs>ncard){	        			
	        			while(spadesCounter>0&&ncard<s.getCard(spadesCounter-1).getValue()){
	        				spadesCounter--;
	        			}     					

	        		}			  
	        		s.addCardAtIndex(spadesCounter, thand.getCard(i));	
	        		hs=s.getCard(s.getSize()-1).getValue();
	        		spadesCounter=sc2;
	        		spadesCounter++;

		    		break;
		    	case 3:

		    		hc2=heartsCounter;
	        		if(hh>ncard){	   
	        			while(heartsCounter>0&&ncard<h.getCard(heartsCounter-1).getValue()){
	        				heartsCounter--;
	        			}     					

	        		} 			  
	        		h.addCardAtIndex(heartsCounter, thand.getCard(i));	
	        		hh=h.getCard(h.getSize()-1).getValue();
	        		heartsCounter=hc2;
	        		heartsCounter++;
		    		break;
        	}
        }
        setClubs(c);
        setDiamonds(d);
        setSpades(s);
        setHearts(h);
        updateDeck();
	}
	
	public void updateDeck(){
        Deck newDeck = new  Deck();
        newDeck.addAllCards(this.clubs);
        newDeck.addAllCards(this.diamonds);
        newDeck.addAllCards(this.spades);
        newDeck.addAllCards(this.hearts);
        updateDeckCards(newDeck);
	}
	public void updateSuits(){
		clubs.clearALL();
		diamonds.clearALL();
		spades.clearALL();
		hearts.clearALL();
		for(Card card : deck.getDeck()){
			switch(card.getSuit()){
			case Card.CLUBS:
				clubs.addCard(card);
				break;
			case Card.DIAMONDS:
				diamonds.addCard(card);
				break;
			case Card.SPADES:
				spades.addCard(card);
				break;
			case Card.HEARTS:
				hearts.addCard(card);
				break;
			}
		}
	}
	
	public void updateDeckCards(Deck cards){
		deck.clearALL();
		for(int i = 0; i<cards.getSize();i++){
			this.deck.addCard(cards.getCard(i));
		}
	}
	public boolean checkForPoints(ArrayList<Card> c) {
		int points=0;
		for(int i=0; i<c.size();i++){
			int curCard = c.get(i).getValue();
			int curSuit = c.get(i).getSuit();
			if(curSuit==1){ //Diamonds  check for jack.
				if(curCard==11){
					points-=10;

				}
			}
			if(curSuit==2){//Spades   check for queen
				if(curCard==12){
					points+=13;

				}
			}
			if(curSuit==3){//heart--add points
				points++;
			}

			
		}
		if(points>0){
			return true;
		}
		return false;
	}

}
