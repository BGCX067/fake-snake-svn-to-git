package com.lee.snake.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.lee.snake.R;

public class TileView extends View {
	
	private final String TAG = "TileView";
	
	protected int mTileSize;
	protected int mXTileCount;
	protected int mYTileCount;
	protected int mXOffset;
	protected int mYOffset;
	
	protected Bitmap[] mTileArray;
	protected int[][] mTileGrid;
	protected final Paint mPaint = new Paint();
	
	/**
	 * 初始化tileSize
	 */
	{
		mTileSize = 12;
	}
	
	public TileView (Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		
	}
	
	public TileView(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx, attrs, defStyle);
	}
	
	/**
	 * 设置某坐标的Tile为空 Red Green Yellow三种的某一种
	 * @param index
	 * @param x
	 * @param y
	 */
	public void setTile(int index, int x, int y) {
		//Check x and y coordination to ensure in bound
		/*if (x < 0 || y < 0 || x >= mXTileCount || y >= mYTileCount) {
			Log.w(TAG, "x or y coordination out of bound");
			throw new IllegalArgumentException(TAG + "x or y coordination out of bound");
		}*/
		
		mTileGrid[x][y] = index;
	}
	
	/**
	 * 将所有Tile清除
	 */
	public void clearTile() {
		for (int i = 0; i < mXTileCount; ++i) {
			for (int j = 0; j < mYTileCount; ++j) {
				setTile(0, i, j);
			}
		}
	}
	
	/**
	 * 将三种颜色形成Bitmap载入mTileArray
	 * @param index
	 * @param drawable
	 */
	public void loadTile(int index, Drawable drawable) {
		Bitmap bm = Bitmap.createBitmap(mTileSize, mTileSize, Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);
		drawable.setBounds(0, 0, mTileSize, mTileSize);
		drawable.draw(canvas);
		
		mTileArray[index] = bm;
	}
	
	/**
	 * 将tileBitmap清空
	 * @param tileCount
	 */
	public void resetTiles(int tileCount) {
		mTileArray = new Bitmap[tileCount];
	}
	
	/**
	 * 初始化屏幕
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mXTileCount = (int) Math.floor(w / mTileSize);
		mYTileCount = (int) Math.floor(h / mTileSize);
		
		mXOffset = (w - mTileSize * mXTileCount) / 2;
		mYOffset = (h - mTileSize * mYTileCount) / 2;
		
		mTileGrid = new int[mXTileCount][mYTileCount];
		clearTile();
	}
	
	/**
	 * 每隔一段时间重画屏幕
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		for (int x = 0; x < mXTileCount; x++) {
			for (int y = 0; y < mYTileCount; y++) {
				//Caution!
				if (mTileGrid[x][y] > 0) {
					canvas.drawBitmap(mTileArray[mTileGrid[x][y]], mXOffset
							+ mTileSize * x, mYOffset + mTileSize * y, mPaint);
				}
			}
		}
		
		
	}
	
}
