package com.codepath.smartodo.listeners;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener {

	private GestureDetector gestureDetector;

	public OnSwipeTouchListener(Context context) {
		gestureDetector = new GestureDetector(context, new GestureListener());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return gestureDetector.onTouchEvent(event);
	}

	private final class GestureListener extends SimpleOnGestureListener {

		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			boolean result = false;

			try {
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();

				// Horizontal swipe
				if (Math.abs(diffX) > Math.abs(diffY)) {

					if (Math.abs(diffX) > SWIPE_THRESHOLD
							&& Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0) {
							onSwipeRight();
						} else {
							onSwipeLeft();
						}
					}
				} else {// Vertical swipe
					if (Math.abs(diffY) > SWIPE_THRESHOLD
							&& Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffY > 0) {
							onSwipeDown();
						} else {
							onSwipeUp();
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return result;
		}

	}

	public void onSwipeRight() {
	}

	public void onSwipeLeft() {
	}

	public void onSwipeUp() {
	}

	public void onSwipeDown() {
	}
}
