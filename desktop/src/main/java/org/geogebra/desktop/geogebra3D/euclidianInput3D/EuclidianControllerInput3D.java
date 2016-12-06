package org.geogebra.desktop.geogebra3D.euclidianInput3D;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianControllerCompanion;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianController3DD;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianView3DD;

/**
 * controller with specific methods from leonar3do input system
 * 
 * @author mathieu
 * 
 */
public class EuclidianControllerInput3D extends EuclidianController3DD {

	protected Input3D input3D;

	protected Robot robot;
	protected int robotX, robotY;

	/**
	 * constructor
	 * 
	 * @param kernel
	 *            kernel
	 * @param input3d
	 *            input3d
	 */
	public EuclidianControllerInput3D(Kernel kernel, Input3D input3d) {
		super(kernel);

		this.input3D = input3d;

		((EuclidianControllerInput3DCompanion) companion).setInput3D(input3d);

		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		input3D.setScreenHalfDimensions(gd.getDisplayMode().getWidth() / 2.0,
				gd.getDisplayMode().getHeight() / 2.0);

		// robot
		robot = null;
		if (input3d.useMouseRobot()) {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	protected EuclidianControllerCompanion newCompanion() {
		return new EuclidianControllerInput3DCompanion(this);
	}

	private boolean isNotMovingObjectOrView() {
		return moveMode == MOVE_NONE && isNotMovingView;
	}

	@Override
	public void updateInput3D() {

		// update panel values
		Dimension panelDimension = ((EuclidianView3DD) view3D).getJPanel()
				.getSize();
		Point panelPosition = ((EuclidianView3DD) view3D).getJPanel()
				.getLocationOnScreen();

		input3D.setPanel(panelDimension.width, panelDimension.height,
				panelPosition.x, panelPosition.y);

		if (input3D.update()) {

			// ////////////////////
			// set values

			// eyes : set position only if we use glasses
			input3D.updateHeadTracking();

			// update on screen position to check if we allow to use regular
			// mouse
			if (robot != null && (isNotMovingObjectOrView()
					|| !input3D.hasMouseDirection())) {
				input3D.updateOnScreenPosition();
			}

			// regular 2D mouse is used? then return
			if (input3D.currentlyUseMouse2D()) {
				return;
			}

			// 2D cursor pos
			if (robot != null) {
				if (isNotMovingObjectOrView() || !input3D.hasMouseDirection()) {
					// process mouse
					if (robotX != input3D.getOnScreenX()
							|| robotY != input3D.getOnScreenY()) {
						// Log.debug(inputPosition[0]+","+inputPosition[1]+","+inputPosition[2]);
						// Log.debug(x+","+y);
						robotX = input3D.getOnScreenX();
						robotY = input3D.getOnScreenY();
						robot.mouseMove(robotX, robotY);
					}

				}
			}

			// mouse pos
			input3D.updateMousePosition();

			// check if the 3D mouse is on 3D view
			if (input3D.hasMouse()) {
				if (!input3D.useInputDepthForHitting()
						|| input3D.getMouse3DPosition().getZ() < view3D
								.getRenderer().getEyeToScreenDistance()) {

					input3D.updateMouse3DEvent();

					input3D.handleButtons();
				}

			} else if (robot != null) { // bird outside the view

				// process right press / release
				if (input3D.isRightPressed()) {
					if (input3D.wasRightReleased()) {
						robot.mousePress(InputEvent.BUTTON3_MASK);
						input3D.setWasRightReleased(false);
					}
				} else {
					if (!input3D.wasRightReleased()) {
						robot.mouseRelease(InputEvent.BUTTON3_MASK);
						input3D.setWasRightReleased(true);
					}
				}

				// process left press / release
				if (input3D.isLeftPressed()) {
					if (input3D.wasLeftReleased()) {
						robot.mousePress(InputEvent.BUTTON1_MASK);
						input3D.setWasLeftReleased(false);
					}
				} else {
					if (!input3D.wasLeftReleased()) {
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
						input3D.setWasLeftReleased(true);
					}
				}

			}
		}

	}

	private boolean isNotMovingView = true;

	private void processRightRelease() {
		((EuclidianView3D) view).setRotContinueAnimation(
				app.getMillisecondTime() - timeOld, animatedRotSpeed);
	}

	@Override
	protected void setMouseLocation(AbstractEvent event) {
		if (input3D.currentlyUseMouse2D()) {
			super.setMouseLocation(event);
		} else {
			mouseLoc = event.getPoint();
		}
	}

	protected Coords movedGeoPointStartCoords = new Coords(0, 0, 0, 1);

	@Override
	protected void updateMovedGeoPointStartValues(Coords coords) {
		if (input3D.currentlyUseMouse2D()) {
			super.updateMovedGeoPointStartValues(coords);
		} else {
			movedGeoPointStartCoords.set(coords);
			if (input3D.hasMouseDirection()) {
				startZNearest = ((EuclidianViewInput3D) view3D).getCompanion()
						.getZNearest();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (input3D.currentlyUseMouse2D()) {
			super.mousePressed(e);
		} else if (input3D.useHandGrabbing() && mode != MOVE_NONE) {
			releaseGrabbing();
			super.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (input3D.currentlyUseMouse2D()) {
			super.mouseReleased(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (input3D.currentlyUseMouse2D()) {
			super.mouseMoved(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (input3D.currentlyUseMouse2D()) {
			super.mouseDragged(e);
		}
	}

	@Override
	public boolean hasInput3D() {
		return true;
	}

	@Override
	public boolean useInputDepthForHitting() {
		return input3D.useInputDepthForHitting();
	}

	@Override
	final protected boolean handleMovedElementFreePlane() {
		if (movedGeoElement.isGeoPlane()) {
			moveMode = MOVE_PLANE;
			setMovedGeoPlane(movedGeoElement);

			return true;
		}

		return false;
	}

	protected GeoPlane3D movedGeoPlane;
	protected CoordSys movedGeoPlaneStartCoordSys;
	private Coords movedGeoStartPosition;

	protected ArrayList<GeoPointND> stickyPoints;

	/**
	 * set plane to move
	 * 
	 * @param geo
	 *            moved geo
	 */
	public void setMovedGeoPlane(GeoElement geo) {

		movedGeoPlane = (GeoPlane3D) geo;

		if (movedGeoPlaneStartCoordSys == null) {
			movedGeoPlaneStartCoordSys = new CoordSys(2);
		}
		movedGeoPlaneStartCoordSys.set(movedGeoPlane.getCoordSys());

		if (movedGeoStartPosition == null) {
			movedGeoStartPosition = new Coords(4);
		}
		movedGeoStartPosition.set(input3D.getMouse3DPosition());

		updateMovedGeoPointStartValues(
				view3D.getCursor3D().getInhomCoordsInD(3));

		view3D.setDragCursor();

		// set sticky points
		if (stickyPoints == null) {
			stickyPoints = new ArrayList<GeoPointND>();
		} else {
			stickyPoints.clear();
		}

		for (GeoElement geo1 : geo.getConstruction()
				.getGeoSetConstructionOrder()) {
			if (geo1.isGeoPoint() && geo1.isVisibleInView3D()
					&& !geo1.isChildOf(geo)) {
				stickyPoints.add((GeoPointND) geo1);
			}
		}

	}

	protected double startZNearest;

	@Override
	public float getPointCapturingPercentage() {
		return 2f * super.getPointCapturingPercentage();
	}

	@Override
	public boolean cursor3DVisibleForCurrentMode(int cursorType) {
		if (mode == EuclidianConstants.MODE_MOVE && !input3D.hasMouseDirection()
				&& !input3D.currentlyUseMouse2D()) {
			return false;
		}

		return super.cursor3DVisibleForCurrentMode(cursorType);

	}

	@Override
	public GPoint getMouseLoc() {
		if (input3D.currentlyUseMouse2D()) {
			return super.getMouseLoc();
		}

		return input3D.getMouseLoc();
	}

	@Override
	protected void setMouseOrigin(GeoPoint3D point) {

		if (input3D.hasMouseDirection() && !input3D.currentlyUseMouse2D()) {
			point.setWillingCoords(input3D.getMouse3DScenePosition());
		} else {
			super.setMouseOrigin(point);
		}
	}

	@Override
	protected int getModeForShallMoveView(AbstractEvent event) {
		if (input3D.currentlyUseMouse2D()) {
			return super.getModeForShallMoveView(event);
		}

		return EuclidianConstants.MODE_MOVE;
	}

	@Override
	public void wrapMouseReleased(AbstractEvent e) {
		isNotMovingView = true;
		if (!input3D.wasRightReleased() && !input3D.useQuaternionsForRotate()) {
			processRightRelease();
			return;
		}

		super.wrapMouseReleased(e);

	}

	/**
	 * release hand grabbing
	 */
	protected void releaseGrabbing() {
		((EuclidianViewInput3D) view3D).getCompanion().getStationaryCoords()
				.consumeLongDelay();
		((EuclidianControllerInput3DCompanion) getCompanion())
				.releaseGrabbing();
	}

	@Override
	public void exitInput3D() {
		input3D.exit();
	}

	@Override
	public boolean isZSpace() {
		return input3D.isZSpace();
	}

}
