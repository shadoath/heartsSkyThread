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

public class TableHolder extends SurfaceView implements Callback, OnTouchListener{
	public static final String TAG = "Hearts--TableHolder";

    private Deck deck;
    private Card Card;
    private int screenWidth;
    private int screenHeight;
    private int position=0;
	private SurfaceHolder surfaceHolder;

    private Context mContext;
	private GameView gameView;

    private boolean full=false;
	public boolean initialized = false; //made true on surfaceCreated()

    //Holds players deck, class to call for updates about deck and drawing the deck
   
    public TableHolder(Context context, GameView gameView, int sW, int sH){
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
    
    
    //protected void onMeasure(int width, int height){
    //    setMeasuredDimension(measureWidth(width),measureHeight(height));
    //}

	
    public Card getCard(int i){
        return this.deck.getCard(i);
    }
    
    public void addDeck(Deck deck){
        this.deck = deck;
    }
    

    public void addCard(Card c){
    	Log.d(TAG, "CARD added to table, "+c.name);
    	this.deck.removeCardAtIndex(0);
    	this.deck.addCard(c);
    	postInvalidate();
    }
    public void removeAll(){
    	this.deck.clearALL();
    }
    
    public void addBlankCards(){
    	this.deck.clearALL();
    	this.position=0;
    	int i = 0;
    	while(i  < 4){
	        this.deck.addCard(new Card(0,0, mContext));
	        i++;
    	}
        
      
    }
    @Override
    protected void onDraw(Canvas canvas){
//       Log.d(TAG, "painting, deck size= "+deck.getSize());
        full=false;
        int cardWidth=(screenWidth/4);
        for (int i=0;i<this.deck.getSize();i++){
    		Card c=this.deck.getCard(i);
    		c.resizeBitmap(cardWidth, screenHeight);
    		c.setCoords(cardWidth*(i), 0, cardWidth+cardWidth*(i), screenHeight);
    		c.draw(canvas);
        	
        }
    }
    
    
    public void updateDeck(Deck deck){
        this.deck = deck;
    }
    
    public void updateCurrentCard(Card Card){
        this.Card = Card;
    }

	public Rect getBounds() {
		return new Rect(0, 0, this.screenWidth, this.screenHeight);

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
		return false;
	}

    

}
