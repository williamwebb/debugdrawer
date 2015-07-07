package com.jug6ernaut.debugdrawer.views.elements;

import android.animation.ValueAnimator;
import com.jug6ernaut.debugdrawer.views.SpinnerElement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by williamwebb on 7/3/15.
 */
public class AnimationSpeedElement extends SpinnerElement {

	public AnimationSpeedElement() {
		super("Animations",toArray());
	}

	@Override
	public void onItemSelect(String item) {
		float multiplier = AnimationSettings.from(item).speed;
		applyAnimationSpeed(multiplier);
	}

	private void applyAnimationSpeed(float multiplier) {
		try {
			Method method = ValueAnimator.class.getDeclaredMethod("setDurationScale", float.class);
			method.invoke(null, multiplier);
		} catch (Exception e) {
			throw new RuntimeException("Unable to apply animation speed.", e);
		}
	}

	private static String[] toArray() {
		List<String> names = new ArrayList<>();
		for (AnimationSettings as : AnimationSettings.values()){
			names.add(as.name);
		}
		return names.toArray(new String[names.size()]);
	}

	private enum AnimationSettings {

		NORMAL("Normal",1),
		TWO_X("2x",2),
		THREE_X("3x",3),
		FIVE_X("5x",5),
		TEN_X("10x",10),
		ZERO("None",0);

		private final String name;
		private final float speed;

		AnimationSettings(String name, int speed) {
			this.name = name;
			this.speed = speed;
		}

		static AnimationSettings from(String name) {
			for (AnimationSettings as : values()) {
				if(as.name.equals(name)) return as;
			}
			throw new AssertionError("Not Found");
		}
	}

}
