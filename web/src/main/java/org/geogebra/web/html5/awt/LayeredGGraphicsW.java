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
	int previewLayer = 0;

	/**
	 * @param canvas Primary canvas
	 * @param canvasParent parent of all canvases
	 */
	public LayeredGGraphicsW(Canvas canvas, Panel canvasParent) {
		super(canvas);
		this.canvasParent = canvasParent;
		layers.add(getContext());
	}

	/**
	 * @return z-index for embedded item
	 */
	public int embed() {
		currentLayer++;
		if (layers.size() <= currentLayer) {
			addLayer();
		}
		color = null;
		updateAndClear();
		return 2 * currentLayer - 1;
	}

	private void updateAndClear() {
		setContext(layers.get(currentLayer));
		clearAll();
		getContext().getCanvas().getStyle().setDisplay(Style.Display.BLOCK);
	}

	private void addLayer() {
		CanvasElement currentCanvas = getContext().getCanvas();
		Canvas nextLayer = Canvas.createIfSupported();
		nextLayer.addStyleName("layer" + layers.size()); // only for debugging purposes
		nextLayer.setCoordinateSpaceHeight(currentCanvas.getHeight());
		nextLayer.setCoordinateSpaceWidth(currentCanvas.getWidth());
		Style style = nextLayer.getCanvasElement().getStyle();
		style.setPosition(Style.Position.ABSOLUTE);
		style.setZIndex(2 * layers.size());
		canvasParent.add(nextLayer);
		JLMContext2d nextContext = JLMContext2d.forCanvas(nextLayer);
		layers.add(nextContext);
	}

	@Override
	public void resetLayer() {
		currentLayer = 0;
		for (int i = 1; i < layers.size(); i++) {
			layers.get(i).getCanvas().getStyle().setDisplay(Style.Display.NONE);
		}
		setContext(layers.get(0));
	}

	@Override
	public void setPreviewLayer() {
		if (previewLayer <= 0) {
			previewLayer = layers.size();
			addLayer();
		}
		currentLayer = previewLayer;
		updateAndClear();
	}
}
