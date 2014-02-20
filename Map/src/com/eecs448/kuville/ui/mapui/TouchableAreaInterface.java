package com.eecs448.kuville.ui.mapui;

import android.view.View;

public interface TouchableAreaInterface {
	public boolean contains(float x, float y);
	public void onClick(View view);
}
