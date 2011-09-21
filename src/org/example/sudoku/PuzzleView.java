package org.example.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class PuzzleView extends View {
	private static final String TAG = "Sudoku";
	private final Game game;
	
	public PuzzleView(Context context){
		super(context);
		this.game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	private float width;
	private float height;
	private int selX;
	private int selY;
	private final Rect selRect = new Rect();
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		width = w/9f;
		height = h/9f;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + ", height " + height);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void getRect(int x, int y, Rect rect){
		rect.set((int) (x*width), (int) (y*height), (int) (x*width+width), (int) (y*height+height));
	}
	
	protected void onDraw(Canvas canvas){
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0,0,getWidth(),getHeight(), background);
		
		//Draw board
		//Grid lines
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		
		//Minor grid lines
		for(int i=0; i<9; i++){
			canvas.drawLine(0, i*height, getWidth(), i*height, light);
			canvas.drawLine(0, i*height + 1, getWidth(), i*height+1, hilite);
			canvas.drawLine(i*width, 0, i*width, getHeight(), light);
			//canvas.drawLine(i*width + 1, 0, i*width+1, getHeight(), hilite);
		}
		
		//Major Grid Lines
		for(int i=0; i<9; i++){
			if(i%3 == 0){
				canvas.drawLine(0, i*height, getWidth(), i*height, dark);
				canvas.drawLine(0, i*height + 1, getWidth(), i*height+1, hilite);
				canvas.drawLine(i*width, 0, i*width, getHeight(), dark);
				canvas.drawLine(i*width + 1, 0, i*width+1, getHeight(), hilite);
			}
		}
		
		//Draw numbers
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width/height);
		foreground.setTextAlign(Paint.Align.CENTER);
		
		FontMetrics fm = foreground.getFontMetrics();
		float x  = width/2;
		float y = height/2 - (fm.ascent + fm.descent) / 2;
		
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				canvas.drawText(this.game.getTileString(i,j), i*width+x, j*height+y, foreground);
			}
		}
		
		//Draw hints
		Paint hint = new Paint();
		int c[] = { getResources().getColor(R.color.puzzle_hint_0), getResources().getColor(R.color.puzzle_hint_1), getResources().getColor(R.color.puzzle_hint_2) };
		Rect r = new Rect();
		
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				int movesleft = 9 - game.getUsedTiles(i,j).length;
				if(movesleft < c.length){
					getRect(i,j,r);
					hint.setColor(c[movesleft]);
					canvas.drawRect(r, hint);
				}
			}
		}
		
		//Draw selection
		Log.d(TAG, "selRect=" + selRect);
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect, selected);
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);

			select((int) Math.floor(event.getX()/width), (int) Math.floor(event.getY()/height));
			game.showKeypadOrError(selX, selY);
			Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
		return true;
	}
	private void select(int x, int y){
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}
	public void setSelectedTile(int value){
		if(game.setTileIfValid(selX, selY, value)){
			invalidate();
		} else {
			// Number is not valid for this tile
			Log.d(TAG, "setSelectedTile: invalid: " + value);
			startAnimation(AnimationUtils.loadAnimation(game, R.anim.shake));
		}
	}
	
}
