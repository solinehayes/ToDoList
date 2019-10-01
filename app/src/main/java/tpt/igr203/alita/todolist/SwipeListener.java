package tpt.igr203.alita.todolist;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by RIQUIER_Melvin on 11/04/2019.
 */

public class SwipeListener implements View.OnTouchListener {
    // Variable
    private GestureDetector gestureDetector;

    /**
     * Constructor
     * @param cxt : context in which the swipe is created
     */
    public SwipeListener(Context cxt) {
        gestureDetector = new GestureDetector(cxt, new SwipeDetector());
    }

    /**
     * Overrides onTouch from onTouchListener
     * @param view : view touched
     * @param motionEvent : motion made
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    /**
     * OnGestureListener class for our swipe
     */
    private final class SwipeDetector extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 100;
        private static final int SWIPE_MIN_VELOCITY = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    //Horizontal Swipe
                    if (Math.abs(diffX) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                } else {
                    //Vertical Swipe
                    if (Math.abs(diffY) > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_MIN_VELOCITY) {
                        if (diffY > 0) {
                            onSwipeUp();
                        } else {
                            onSwipeDown();
                        }
                        result = true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
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
