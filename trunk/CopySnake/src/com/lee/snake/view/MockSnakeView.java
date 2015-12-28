package com.lee.snake.view;

import com.lee.snake.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class MockSnakeView extends TileView {
	
	private int RED_STAR = 1;
	private int GREEN_STAR = 2;
	private int YELLOW_STAR = 3;

	public MockSnakeView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		initSnakeView();
		
	}

	public MockSnakeView(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx, attrs, defStyle);
		initSnakeView();
	}

	private void initSnakeView() {
		setFocusable(true);
		setClickable(true);
		
		resetTiles(4);
		Drawable green = this.getContext().getResources().getDrawable(R.drawable.greenstar);
		loadTile(GREEN_STAR, green);
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		for (int y = 0; y < mYTileCount; ++y) {
			setTile(GREEN_STAR, 0, y);
			setTile(GREEN_STAR, mXTileCount - 1, y);	
		}
		
		for (int x = 1; x < mXTileCount - 1; x++) {
			setTile(GREEN_STAR, x, 0);
			setTile(GREEN_STAR, x, mYTileCount - 1);
		}
		
		invalidate();
		return super.onKeyDown(keyCode, event); 
	}
}
