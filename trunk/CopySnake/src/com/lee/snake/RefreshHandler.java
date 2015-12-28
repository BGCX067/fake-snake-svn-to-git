package com.lee.snake;

import android.os.Handler;
import android.os.Message;

import com.lee.snake.view.SnakeView;

public class RefreshHandler extends Handler {
	private SnakeView snakeView;
	
	public RefreshHandler(SnakeView snakeView) {
		super();
		this.snakeView = snakeView;
	}

	@Override
	public void handleMessage(Message msg) {
		snakeView.update();
		snakeView.invalidate();
	}
	
	public void sleep(long delay) {
		removeMessages(0);
		sendMessageDelayed(obtainMessage(0), delay);
	}
}
