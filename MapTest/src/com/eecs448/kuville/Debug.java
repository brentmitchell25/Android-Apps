package com.eecs448.kuville;

import android.util.Log;

public class Debug {
	public static synchronized void Assert(boolean cond, String message) {
		try {
			if (!cond) {
				throw new AssertException(message); 
			}
		} catch (AssertException e) {
			Log.println(Log.ASSERT, "ASSERT FAILURE", e.getMessage());
			/*StackTraceElement[] st = e.getStackTrace();
			for (StackTraceElement t : st) {
				Log.println(Log.ASSERT, "ASSERT FAILURE", t.getClassName() + "(" + t.getFileName() + ":" + t.getLineNumber() + ")");
			}*/
			
		}
	}
	
	public static void Assert(boolean cond) {
		Assert(cond, "Assertion Failed");
	}
	
	private static class AssertException extends Exception{
		private static final long serialVersionUID = 1L;

		AssertException(String message) {
			super(message);
		}
	}
}
