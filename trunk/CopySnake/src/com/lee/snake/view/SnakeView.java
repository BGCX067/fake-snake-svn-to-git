package com.lee.snake.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TextView;

import com.lee.snake.R;
import com.lee.snake.RefreshHandler;

public class SnakeView extends TileView {
	
	private static final String TAG = "SnakeView";
	
	private int RED_APPLE = 1;
	private int GREEN_APPLE = 2;
	private int YELLOW_APPLE = 3;
	/**
	 * 游戏运行状态，根据状态来控制游戏的运行
	 */
	private int mMode;
	private static final int PAUSE = 1;
	private static final int RUNNING = 2;
	private static final int READY = 3;
	private static final int LOSE = 0;
	
	/**
	 * 游戏的方向，包括现在和将要运行的方向
	 */
	private int mCurrentDirection;
	private int mNextDirection;
	private static final int NORTH = 1;
	private static final int SOUTH = 2;
	private static final int WEST = 3;
	private static final int EAST = 4;
	
	private int mDelay;
	private long mLastMove;
	private int mScore;
	
	/**
	 * Snake的坐标表示
	 */
	private ArrayList<Coordination> mSnakeTrail;
	/**
	 * Apple的坐标表示
	 */
	private ArrayList<Coordination> mAppleLocation;
	
	private TextView statusTextView;
	private RefreshHandler mDrawableHandler;
	
	private static final Random random = new Random();
	
	/**
	 * 初始化局部变量
	 */
	{
		mMode = READY;
		mCurrentDirection = SOUTH;
		mNextDirection = SOUTH;
		mSnakeTrail = new ArrayList<Coordination>();
		mAppleLocation = new ArrayList<Coordination>();
		mDrawableHandler = new RefreshHandler(this);
	}

	public SnakeView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		initSnakeView();
				
	}
	
	public SnakeView(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx, attrs, defStyle);
		initSnakeView();
		
	}

	private void initNewGames() {
		mSnakeTrail.clear();
		mAppleLocation.clear();
		//添加蛇
		
		mSnakeTrail.add(new Coordination(7, 7));
		mSnakeTrail.add(new Coordination(6, 7));
		mSnakeTrail.add(new Coordination(5, 7));
		mSnakeTrail.add(new Coordination(4, 7));
		mSnakeTrail.add(new Coordination(3, 7));
		mSnakeTrail.add(new Coordination(2, 7));
		mNextDirection = SOUTH;
		//设置基本参数,添加苹果
		addRandomApple();
		addRandomApple();
		
		mScore = 0;
		mDelay = 500;
		
	}
	
	private void initSnakeView() {
		setFocusable(true);
		
		Resources res = this.getContext().getResources();
		resetTiles(4);
		
		loadTile(RED_APPLE, res.getDrawable(R.drawable.redstar));
		loadTile(GREEN_APPLE, res.getDrawable(R.drawable.greenstar));
		loadTile(YELLOW_APPLE, res.getDrawable(R.drawable.yellowstar));
		
		clearTile();
	}
	
	public void update() {
		if (mMode == RUNNING) {
			long now = System.currentTimeMillis();
			if (now - mLastMove >= mDelay) {
				clearTile();
				updateWall();
				updateApples();
				updateSnake();
				
				mLastMove = now;
			}
			
			mDrawableHandler.sleep(mDelay);
		}
		
	}
	
	private void updateWall() {
		//画围墙的上边框，下边框
		for (int x = 0; x < mXTileCount; x++) {
			setTile(GREEN_APPLE, x, 0);
			setTile(GREEN_APPLE, x, mYTileCount - 1);
		}
		//画围墙的左右边框
		for (int y = 1; y < mYTileCount - 1; ++y) {
			setTile(GREEN_APPLE, 0, y);
			setTile(GREEN_APPLE, mXTileCount - 1, y);
		}
	}
	
	private void updateSnake() {
		boolean growSnake = false;
		//获取当前和下一步的蛇头位置，并判断Snake是否撞墙，是否撞在自己身体上
		Coordination oldHead = mSnakeTrail.get(0);
		Coordination newHead = new Coordination(0, 0);
		mCurrentDirection = mNextDirection;
		
		if (mNextDirection == NORTH) {
			newHead.setX(oldHead.getX());
			newHead.setY(oldHead.getY() - 1);
		}
		
		if (mNextDirection == SOUTH) {
			newHead.setX(oldHead.getX());
			newHead.setY(oldHead.getY() + 1);			
		}
		
		if (mNextDirection == WEST) {
			newHead.setX(oldHead.getX() - 1);
			newHead.setY(oldHead.getY());
		}
		
		if (mNextDirection == EAST) {
			newHead.setX(oldHead.getX() + 1);
			newHead.setY(oldHead.getY());
		}
		//检查蛇是否撞墙
		if (newHead.getX() < 1 || newHead.getX() > mXTileCount - 2 ||
				newHead.getY() < 1 || newHead.getY() > mYTileCount - 2) {
			setMode(LOSE);
			return;
		}
		//检查是是否撞到自己
		for (Coordination c : mSnakeTrail) {
			if (c.equals(newHead)) {
				setMode(LOSE);
				return;
			}
		}
		//检查Snake head的位置是否属于某一苹果的位置，如果是，吃掉这个苹果
		for (int i = 0; i < mAppleLocation.size(); ++i) {
			Coordination c = mAppleLocation.get(i);
			if (newHead.equals(c)) {
				mAppleLocation.remove(i);
				addRandomApple();
				
				mScore++;
				mDelay *= 0.9;
				growSnake = true;
			}
		}
		
		mSnakeTrail.add(0, newHead);
		//如果没有吃苹果，删掉最后一格，将新的头加入
		if (!growSnake) {
			mSnakeTrail.remove(mSnakeTrail.size() - 1);
		}
		
		int index = 0;
		for (Coordination c : mSnakeTrail) {
			if (index == 0) {
				setTile(RED_APPLE, c.getX(), c.getY());
			} else {
				setTile(YELLOW_APPLE, c.getX(), c.getY());
			}
			index++;	
		}
	}
	
	private void updateApples() {
		for (Coordination coord : mAppleLocation) {
			setTile(YELLOW_APPLE, coord.getX(), coord.getY());
		}
	}
	
	private void addRandomApple() {
		boolean collision = true;
		//生成x,y坐标.不包括围墙
		Coordination appleCoord = null;
		while (collision) {
			int x = random.nextInt(mXTileCount - 2) + 1;
			int y = random.nextInt(mYTileCount - 2) + 1;
			collision = false;
			appleCoord = new Coordination(x, y);
			//检测生成的坐标是否与Snake的坐标冲突
			Iterator<Coordination> iter = mSnakeTrail.iterator();
			while (iter.hasNext()) {
				Coordination snakeCoord = iter.next();
				if (snakeCoord.equals(appleCoord)) {
					collision = true;
				}
			}//end while
		}//end while
		
		mAppleLocation.add(appleCoord);
		
	}
	
	/**
	 * 按键后，根据游戏运行状态以及方向键来决定游戏的下一步动作
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (mMode == READY || mMode == LOSE) {
				initNewGames();
				setMode(RUNNING);
				update();
	
				return true;
			}
			
			if (mMode == PAUSE) {
				setMode(RUNNING);
				update();
				return true;
			}
			
			if (mCurrentDirection != SOUTH) {
				mNextDirection = NORTH;
			}
			return true;
		}
		
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			if (mCurrentDirection != NORTH) {
				mNextDirection = SOUTH;
			}
		}
		
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			if (mCurrentDirection != EAST) {
				mNextDirection = WEST;
			}
			
		}
		
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (mCurrentDirection != WEST) {
				mNextDirection = EAST;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}

	public void setMode(int mode) {
		int oldMode = mMode;
		mMode = mode;
		
		//从其它状态转向运行状态
		if (oldMode != RUNNING && mMode == RUNNING) {
			statusTextView.setVisibility(INVISIBLE);
			update();
			return;
		}
		
		//非运行状态，根据具体状态在TextView上显示信息
		Resources res = this.getContext().getResources();
		String str = "";
		
		if (mMode == PAUSE) {
			str = res.getString(R.string.mode_pause);
		}
		if (mMode == LOSE) {
			str = res.getString(R.string.mode_lose_prefix) + mScore + res.getString(R.string.mode_lose_suffix);
		}
		if (mMode == READY) {
			str = res.getString(R.string.mode_ready);
		}
		
		statusTextView.setText(str);
		statusTextView.setVisibility(VISIBLE);
	
		
	}

	public void setStatusTextView(TextView statusTextView) {
		this.statusTextView = statusTextView;
	}
	
	
	/*public Bundle saveState() {
		
	}
	
	public void restoreState(Bundle stat) {
		
	}*/
	
	/*private ArrayList coordToArrayList() {
		
	}
	
	private arrayListToCoord*/
	
}
