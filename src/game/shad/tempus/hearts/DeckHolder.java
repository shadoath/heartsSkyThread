package game.shad.tempus.hearts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class DeckHolder extends SurfaceView implements Callback, OnTouchListener
{
	public static final String TAG = "Hearts--DeckHolder";

    private Deck deck;
    private Card Card;
    private int screenWidth;
    private int screenHeight;
    private int position=0;
    private boolean full=false;
	public boolean initialized = false; //made true on surfaceCreated()
	private SurfaceHolder surfaceHolder;




	private Context mContext;
	private GameView gameView;
    //Holds players deck, class to call for updates about deck and drawing the deck

    public DeckHolder(Context context, GameView gameView, int sW, int sH){
        super(context);
        this.mContext=context;
        this.gameView = gameView;
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
	    this.screenWidth = sW;
        this.screenHeight = sH;
        this.deck = new Deck();
        addBlankCards();
        

    }

    
    public void addBlankCards(){
    	this.deck.clearALL();
    	this.position=0;
    	int i = 0;
    	while(i  < 12){
	        this.deck.addCard(new Card(0,0, mContext));
	        i++;
    	}
    }
    
    public Card getCard(int i){
        return this.deck.getCard(i);
    }
    
    public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	public Rect getBounds(){
		return new Rect(0, this.screenHeight, this.screenWidth, this.screenHeight);
	}

	public void addDeck(Deck deck){
		this.deck.clearALL();
        this.deck = deck;
    }
 
    
    public void swipeLeft(){
    	if(getPosition()<deck.getSize()-1)
    		setPosition(getPosition() + 1);
    	else
    		setPosition(0);
    }
    public void swipeRight(){
    	if(getPosition()>=1)
    		setPosition(getPosition() - 1);
    	else{
    		setPosition(deck.getSize()-1);
    	}

    }
    public void addCard(Card c){
    	this.deck.addCard(c);
    }
    public void removeAll(){
    	this.deck.clearALL();
    }
    
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
//        Log.d(TAG, "onDraw DH");
        full=false;
        int displayed=0;
        //TODO resize cards 
       	int cardWidth=screenWidth/7;
       	int i=getPosition();
       	int loop=0;
       	while(!full){
        	if(displayed>7){
        		//j+=60;
        		//i2=8;
        		full=true;
        	}
        	if(!full){
        		if(i>=deck.getSize()&&deck.getSize()>7){
        			loop=i;
        			i=0;
        		}
        		if(i>=deck.getSize()){
        			break;
        		}
        		if(i<0){
        			i=deck.getSize()+i;
        		}
        		
        		Card c=deck.getCard(i);
        		c.resizeBitmap(cardWidth, screenHeight);
        		c.setCoords(cardWidth*((i+loop)-getPosition()), 0, cardWidth+cardWidth*((i+loop)-getPosition()), screenHeight);
        		c.draw(canvas);
           		i++;
           		displayed++;
        	}
        }
       	
    }
    
    
    public void updateDeck(Deck deck){
        this.deck = deck;
        refreshDrawableState();
    }
    
    public Deck getDeck(){
        return this.deck;
    }
    
    public void updateCurrentCard(Card Card){
        this.Card = Card;
    }


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surface Created");
		initialized = true;
		setOnTouchListener(this);
		this.deck = gameView.game.p1.deck;
	    // TODO Set height width here.
			
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surface Created");
		initialized = false;

		// TODO Set height width here.
			
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d(TAG, "Touching at x="+event.getX()+", y="+event.getY());
		gameView.deckViewTouched((int)event.getX(), (int)event.getY());
		// TODO Auto-generated method stub
		return false;
	}





    

}
