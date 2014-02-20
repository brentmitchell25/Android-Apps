package com.eecs448.kuville.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.eecs448.kuville.ui.mapui.Map;
import com.test.maptest.R;

public class MapSurface extends SurfaceView implements SurfaceHolder.Callback {
	private static MapSurface rs;
	private Context c;
	private SurfaceHolder sh;

	private RenderSurfaceThread render_thread;

	private static Map map;

	public MapSurface(Context context, AttributeSet attr) {
		super(context, attr);

		c = context;
		sh = getHolder();

		sh.addCallback(this);

		render_thread = new RenderSurfaceThread();

		setFocusable(true);
	}

	public static MapSurface getInstance() {
		return rs;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		rs = this;
		map = new Map(c.getResources(), R.drawable.map, getWidth(), getHeight());
		render_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		render_thread.stopThread();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return MapTouchHandler.handleTouch(this, map, e);
	}

	public final Map getMap() {
		return map;

	}

	public void requestRedraw() {
		render_thread.throttleThread();
	}

	public int getState() {
		return render_thread.thread_state;
	}

	public RenderSurfaceThread getThread() {
		return render_thread;
	}

	class RenderSurfaceThread extends Thread {

		// Some thread state variables.
		public static final int THREAD_UNDEFINED = -1;
		public static final int THREAD_NEW = 0;
		public static final int THREAD_IDLE = 1;
		public static final int THREAD_EXECUTING = 2;
		public static final int THREAD_KILL = 3;
		public static final int THREAD_DEAD = 4;

		// Thread state variable.
		private int thread_state = THREAD_UNDEFINED;

		// Idle lock object
		private final Object RUN_LOCK = new Object();

		private RenderSurfaceThread() {
			setState(THREAD_NEW);

		}

		@Override
		public void run() {
			setState(THREAD_EXECUTING);

			while (thread_state != THREAD_KILL) {
				map.transformImage();
				doDraw();

				// Thread will idle until redraw is requested.
				idleThread();
			}

			setState(THREAD_DEAD);
		}

		private void doDraw() {
			Canvas canvas = null;
			try {
				canvas = sh.lockCanvas(null);
				synchronized (sh) {
					// Critical section. Do not allow mRun to be set false until
					// we are sure all canvas draw operations are complete.
					//
					// If mRun has been toggled false, inhibit canvas
					// operations.
					synchronized (RUN_LOCK) {
						if (thread_state == THREAD_EXECUTING) {
							// Do our drawing here
							canvas.drawBitmap(map.getMapBitmap(), 0, 0, null);
						}
					}
				}
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (canvas != null) {
					sh.unlockCanvasAndPost(canvas);
				}
			}
		}

		private void idleThread() {
			setState(THREAD_IDLE);
			try {
				do {
					synchronized (sh) {
						sh.wait();
					}
				} while (thread_state != THREAD_EXECUTING); // Ensure the notify
															// means that it is
															// time to execute
				// and not something else
			} catch (InterruptedException e) {
				Log.e("HI", e.toString());
			}
		}

		private void throttleThread() {
			synchronized (sh) {
				setState(THREAD_EXECUTING);
				sh.notifyAll();
			}
		}

		private void setState(int state) {
			synchronized (sh) {
				synchronized (RUN_LOCK) {
					assert (thread_state != THREAD_DEAD);
					thread_state = state;
					// RUN_LOCK.notifyAll();
				}
			}
		}

		private void stopThread() {
			// we have to tell thread to shut down & wait for it to finish, or
			// else
			// it might touch the Surface after we return and explode
			boolean retry = true;
			try {
				do {

					if (thread_state == THREAD_IDLE) {
						setState(THREAD_KILL);
						throttleThread();
					} else {
						try {
							// RUN_LOCK.wait();
						} catch (Exception e) {
							Log.e("HI2", e.toString());
						}
					}
				} while (thread_state != THREAD_KILL
						&& thread_state != THREAD_DEAD);

				while (retry) {
					try {
						join();
						retry = false;
					} catch (InterruptedException e) {
					}
				}
			} catch (Exception e) {
				Log.e("DJL:D", e.toString());
			}
		}
	}
}
