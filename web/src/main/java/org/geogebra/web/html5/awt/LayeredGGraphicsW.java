package org.geogebra.web.html5.awt;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

public class LayeredGGraphicsW extends GGraphics2DW {
	private final Panel canvasParent;
	ArrayList<JLMContext2d> layers = new ArrayList<>();
	int currentLayer = 0;

	/**
	 * @param canvas Primary canvas
	 * @param canvasParent parent of all canvases
	 */
	public LayeredGGraphicsW(Canvas canvas, Panel canvasParent) {
		super(canvas);
		this.canvasParent = canvasParent;
		layers.add(getContext());
	}

	public int embed() {
		currentLayer++;
		CanvasElement currentCanvas = getContext().getCanvas();
		if (layers.size() <= currentLayer) {
			Canvas nextLayer = Canvas.createIfSupported();
			nextLayer.addStyleName("layer" + currentLayer); // only for debugging purposes
			nextLayer.setCoordinateSpaceHeight(currentCanvas.getHeight());
			nextLayer.setCoordinateSpaceWidth(currentCanvas.getWidth());
			Style style = nextLayer.getCanvasElement().getStyle();
			style.setPosition(Style.Position.ABSOLUTE);
			style.setZIndex(2 * currentLayer);
			canvasParent.add(nextLayer);
			JLMContext2d nextContext = JLMContext2d.forCanvas(nextLayer);
			layers.add(nextContext);
		}
		color = null;
		updateContext();
		getContext().clearRect(0, 0, currentCanvas.getWidth(), currentCanvas.getHeight());
		getContext().getCanvas().getStyle().setDisplay(Style.Display.BLOCK);
		return 2 * currentLayer - 1;
	}

	private void updateContext() {
		setContext(layers.get(currentLayer));
	}

	@Override
	public void resetLayer() {
		currentLayer = 0;
		for (int i = 1; i < layers.size(); i++) {
			layers.get(i).getCanvas().getStyle().setDisplay(Style.Display.NONE);
		}
		updateContext();
	}
}
