package de.buildinggl.drawable;

import java.util.ArrayList;
import java.util.List;

import melb.mSafe.model.Node;
import melb.mSafe.model.Vector3D;
import melb.mSafe.model.Way;
import de.buildinggl.animation.PathAnimation;
import de.buildinggl.utilities.FloatBufferHelper;
import de.buildinggl.utilities.Helper;
import de.buildinggl.utilities.ShaderProgram;

public class WayGL implements IDrawableObject {

	private Way way;
	private boolean shouldAnimate;
	private IDrawableObject glWay;
	private IDrawableObject animatedWay;
	private PathAnimation animation;
	private long timeToFinish;

	private float[] defaultColor;
	private float[] arrowColor;
	private boolean isVisible = true;

	public WayGL(Way way, float[] color, boolean shouldAnimate,
			long timeToFinish) {
		this.way = way;
		this.shouldAnimate = shouldAnimate;
		this.arrowColor = color;
		this.defaultColor = color;
		this.defaultColor[3] = 0.5f;
		this.timeToFinish = timeToFinish;

		List<Vector3D> points = new ArrayList<Vector3D>();
		for (Node node : way.getPoints()) {
			points.add(new Vector3D(node.getX(), node.getY(), node.getZ()));
		}
		glWay = new DrawableObject(points, defaultColor);

		if (shouldAnimate) {
			animation = new PathAnimation(timeToFinish, PathAnimation.INFINITY,
					points);
			animation.start();
			animatedWay = new DrawableObject(FloatBufferHelper.createArrow(1,
					arrowColor));
		}
	}

	@Override
	public void draw(float[] mvpMatrix) {
		if (isVisible) {
			glWay.draw(mvpMatrix);
			if (shouldAnimate) {
				float[] animatedMatrix = Helper.animateObject(
						animation.animate(), mvpMatrix);
				animatedWay.draw(animatedMatrix);
			}
		}
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	@Override
	public void initWithGLContext(ShaderProgram program) {
		glWay.initWithGLContext(program);
		if (shouldAnimate) {
			animatedWay.initWithGLContext(program);
		}
	}

}