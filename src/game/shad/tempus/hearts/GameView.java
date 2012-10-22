package game.shad.tempus.hearts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class GameView implements Callback, OnTouchListener{
	public static final String TAG = "Hearts--GameView";
	public String eol = System.getProperty("line.separator");

	Context context;
	private MainActivity main;
	public Game game;
	private boolean initialized = false; //made true on surfaceCreated()
	private boolean userUpdate = false;
	private Canvas canvas;				// rendering by alocating a ton of memory each frame
	Canvas deckHolderCanvas = null;
	Canvas tableHolderCanvas = null;
    private GestureDetector gestures;
	private int screenWidth;
    private int screenHeight;
    LinearLayout bottomLayout;
    LinearLayout deckHolderLayout;
    LinearLayout tableHolderLayout;
    public SurfaceView deckHolderSV;
    public SurfaceView tableHolderSV;

    public DeckHolder deckHolder;
    public TableHolder tableHolder;
    
    LinearLayout.LayoutParams tableHolderlayoutParams;
    LinearLayout.LayoutParams deckHolderlayoutParams;
    public TextView clubsPlayed;
    public TextView diamondsPlayed;
    public TextView spadesPlayed;
    public TextView heartsPlayed;
    public TextView roundView;
	TextView totalPlayed;
 
    public TextView p1tvScore;
    public TextView p2tvScore;
    public TextView p3tvScore;
    public TextView p4tvScore;

    public TextView bottomText;
    public TextView bottomText2;
	public RectF rect = new RectF(); // public rect to be passed to drawable objects for drawing bitmaps
	public Paint paint = new Paint(); // public paint to be passed to drawable objects


	public GameView(Context context, MainActivity main, Game game, int width, int height) {
		this.context = context;
		this.main = main;
		this.game = game;
		
        screenWidth = width;
        screenHeight = height;
        canvas = new Canvas();
        firstStart();
		// TODO Auto-generated constructor stub
	}
	 


	/**
     * Basically declares all the views into  objects.
     * Should only be called from OnCreate(); and OnRestart();
     * 
     */
    public void firstStart() {
    	Log.d(TAG, "firstStart()");
    	    	
        p1tvScore = (TextView) main.findViewById(R.id.p1tvScore);
        p2tvScore = (TextView) main.findViewById(R.id.p2tvScore);
        p3tvScore = (TextView) main.findViewById(R.id.p3tvScore);
        p4tvScore = (TextView) main.findViewById(R.id.p4tvScore);
        
    	roundView = (TextView) main.findViewById(R.id.roundView);
    	bottomText = (TextView) main.findViewById(R.id.bottomTV);
    	bottomText2 = (TextView) main.findViewById(R.id.bottomTV2);
	
        clubsPlayed = (TextView) main.findViewById(R.id.clubsPlayed);
        diamondsPlayed = (TextView) main.findViewById(R.id.diamondsPlayed);
        spadesPlayed = (TextView) main.findViewById(R.id.spadesPlayed);
        heartsPlayed = (TextView) main.findViewById(R.id.heartsPlayed);
        totalPlayed = (TextView) main.findViewById(R.id.totalPlayed);
        deckHolderLayout = (LinearLayout) main.findViewById(R.id.DeckHolderLayout);
        tableHolderLayout = (LinearLayout) main.findViewById(R.id.TableHolderLayout);
//        deckHolderSV = (SurfaceView) main.findViewById(R.id.deckHolderSV);
//        tableHolderSV = (SurfaceView) main.findViewById(R.id.tableHolderSV);

        //createViews();
	}

	
	public void createViews(){
		Log.d(TAG, "CreateViews");

    	deckHolder = new DeckHolder(context, this, screenWidth, screenHeight/8);
        deckHolderlayoutParams = new LinearLayout.LayoutParams(screenWidth, screenHeight/8);

        tableHolder = new TableHolder(context, this, (int) (screenWidth*.6), screenHeight/8);
        tableHolderlayoutParams = new LinearLayout.LayoutParams((int) (screenWidth*.7), screenHeight/8);

        deckHolder.setLayoutParams(deckHolderlayoutParams);
        tableHolder.setLayoutParams(tableHolderlayoutParams);
     
       // this.deckHolderSV.set = deckHolder;
       // this.tableHolderSV = tableHolder;

        main.gameView.deckHolderLayout.addView(this.deckHolder);
        main.gameView.tableHolderLayout.addView(this.tableHolder);

        this.initialized=true;
    }
	 /**
	 * Call to refresh the screen.
	 */
	public synchronized void update() {
		if(this.initialized){
			
	//		canvas = holder.lockCanvas();
	//		onDraw(canvas);
	//		holder.unlockCanvasAndPost(canvas);
			updateDH();
			updateTH();
		}
		else{
			Log.d(TAG, "not Initialized");
		}
	}
	
	public synchronized void updateDH(){
		if (!deckHolder.initialized){
			Log.d(TAG, "DeckHolder not initialized");
		}
		else{
			deckHolderCanvas = deckHolder.getHolder().lockCanvas();
			deckHolder.onDraw(deckHolderCanvas);
			deckHolder.getHolder().unlockCanvasAndPost(deckHolderCanvas);
			
		}
	}
	public synchronized void updateTH(){
		if (!tableHolder.initialized){
			Log.d(TAG, "TableHolder not initialized");
		}
		else{
			tableHolderCanvas = tableHolder.getHolder().lockCanvas();
			tableHolder.onDraw(tableHolderCanvas);
			tableHolder.getHolder().unlockCanvasAndPost(tableHolderCanvas);
			}
	}
	
	public void UserUpdate(){
			displayCards(game.p1);
	        p1tvScore.invalidate();
	        p2tvScore.invalidate();
	        p3tvScore.invalidate();
	        p4tvScore.invalidate();
	
	     	roundView.invalidate();
	    	bottomText.invalidate();
	    	bottomText2.invalidate();
		    	
	        clubsPlayed.invalidate();
	        diamondsPlayed.invalidate();
	        spadesPlayed.invalidate();
	        heartsPlayed.invalidate();
	        totalPlayed.invalidate();
	        userUpdate=false;
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
		
		//TODO update gameView in thread.

		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d(TAG, "Touching at x="+event.getX()+", y="+event.getY());
		// TODO Auto-generated method stub
		return false;
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
		// TODO Set height width here.
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surface Created");
		initialized = false;

		// TODO Set height width here.
			
	}
	public void setInitializedTo(boolean b) {
		Log.d(TAG, "Set Initialized to "+b);
		initialized = b;
	}
	
  

	public void clearTableCards(){
		//
		tableHolder.removeAll();
		tableHolder.addBlankCards();
		/*
	    tableCard1.setText("1");
	    tableCard2.setText("2");
	    tableCard3.setText("3");
	    tableCard4.setText("4");
	    tableCard1.setBackgroundColor(Color.LTGRAY);
	    tableCard2.setBackgroundColor(Color.LTGRAY);
	    tableCard3.setBackgroundColor(Color.LTGRAY);
	    tableCard4.setBackgroundColor(Color.LTGRAY);
	*/
	    
	}


	
	
	
	
	public DeckHolder getDeckHolder() {
		return deckHolder;
	}

	public TableHolder getTableHolder() {
		return tableHolder;
	}

	public void deckViewTouched(int x, int y) {
    	for(Card c :deckHolder.getDeck().getDeck()){
	    	if(c.getBounds().contains(x, y)){
	    		if(game.trading){
	    			//Pick up to three cards
	    		}
	    		else{	//Select a card to play.
		    			if(game.cardToPlay==null){//Nothing picked yet
		    				game.cardToPlay=c;
		    				game.cardToPlay.setTouched(true);
		    				Toast.makeText(context, "You picked the "+c.cardToString(), Toast.LENGTH_SHORT).show();
		    				break;
		    			}
		    			else{
		    				game.cardToPlay.setTouched(false);
		    				game.cardToPlay=c;
		    				game.cardToPlay.setTouched(true);
		    				Toast.makeText(context, "You picked the "+c.cardToString(), Toast.LENGTH_SHORT).show();
		    				break;
		    			}
		    		}
	            updateDH();
	    		//TODO untouch other cards.
	    	}
	    	
	    }
    }

	public void addTableCard(Card r) {
		tableHolder.addCard(r);		
	}
	

	/*
	public void onPlayCardPressed(View v){
		if(cardToPlay!=null&&playing){ //make sure we have a card selected and we have not already played.
			if(pile.size()==0){
				clearTableCards();
			}
			cardToPlay.setTouched(false);
			pile.add(cardToPlay);			
			playing=false;
			p1.getDeck().removeCard(cardToPlay);
			tableViewDH.addCard(cardToPlay);

			if(pile.size()==4){
				//Toast.makeText(HeartsActivity.this, "Last card",  Toast.LENGTH_SHORT).show();
				pickUpHand();			
			}
			else{
				curPlayer=nextPlayer(curPlayer);
			}
			bottomText.setText("You played the "+cardToPlay.name);
			displayCards(p1);
			cardToPlay=null;
		}
		else{
			Toast.makeText(Game.this, "Not your turn",  Toast.LENGTH_SHORT).show();
		}
		p1.updateDeck();
		cardViewDH.updateDeck(p1.getDeck());
		cardViewDH.invalidate();
		tableViewDH.invalidate();
		tableViewDH.refreshDrawableState();
		cardViewDH.refreshDrawableState();
		

	}
	*/
	
	
}
