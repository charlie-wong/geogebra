package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * animation for scale (no translation)
 *
 */
public class EuclidianView3DAnimationScale extends EuclidianView3DAnimationScaleAbstract {

	private double newScale;

	/**
	 * 
	 * @param view3D
	 * @param animator
	 * @param newScale
	 */
	public EuclidianView3DAnimationScale(EuclidianView3D view3D, EuclidianView3DAnimator animator, double newScale) {
		super(view3D, animator);
		this.newScale = newScale;
	}

	public void setupForStart() {

		xScaleStart = view3D.getXscale();
		yScaleStart = view3D.getYscale();
		zScaleStart = view3D.getZscale();
		animatedScaleStartX = view3D.getXZero();
		animatedScaleStartY = view3D.getYZero();
		animatedScaleStartZ = view3D.getZZero();

		Coords v;
		if (view3D.getCursor3DType() == EuclidianView3D.PREVIEW_POINT_NONE) { // use cursor only if on
			// point/path/region or xOy plane
			v = new Coords(-animatedScaleStartX, -animatedScaleStartY, -animatedScaleStartZ, 1);
			// takes center of the scene for fixed point
		} else {
			v = view3D.getCursor3D().getInhomCoords();
			if (!v.isDefined()) {
				v = new Coords(-animatedScaleStartX, -animatedScaleStartY, -animatedScaleStartZ, 1);
				// takes center of the scene for fixed point
			}
		}

		double factor = view3D.getXscale() / newScale;

		animatedScaleEndX = -v.getX() + (animatedScaleStartX + v.getX()) * factor;
		animatedScaleEndY = -v.getY() + (animatedScaleStartY + v.getY()) * factor;
		animatedScaleEndZ = -v.getZ() + (animatedScaleStartZ + v.getZ()) * factor;

		animatedScaleTimeStart = view3D.getApplication().getMillisecondTime();
		xScaleEnd = xScaleStart / factor;
		yScaleEnd = yScaleStart / factor;
		zScaleEnd = zScaleStart / factor;

		animatedScaleTimeFactor = 0.005; // it will take about 1/2s to achieve it

	}
}
