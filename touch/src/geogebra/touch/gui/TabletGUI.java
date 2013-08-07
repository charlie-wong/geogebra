package geogebra.touch.gui;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.Kernel;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.gui.elements.toolbar.ToolBar;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.gui.laf.LookAndFeel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.ToolBarCommand;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TabletGUI extends HeaderPanel implements GeoGebraTouchGUI {
	public static final float ALGEBRA_VIEW_WIDTH_FRACTION = 0.2f;
	public static final int FOOTER_BORDER_WIDTH = 1;
	private static final int ALGEBRA_BUTTON_WIDTH = 50;
	public static final int MINIMAL_WIDTH_FOR_TWO_VIEWS = 400;

	public static int computeAlgebraWidth() {
		if (Window.getClientWidth() < MINIMAL_WIDTH_FOR_TWO_VIEWS) {
			return Window.getClientWidth();
		}
		return Math.max(250,
				(int) (Window.getClientWidth() * ALGEBRA_VIEW_WIDTH_FRACTION));
	}

	public static GColor getBackgroundColor() {
		return GColor.LIGHT_GRAY;
	}

	private static LookAndFeel getLaf() {
		return TouchEntryPoint.getLookAndFeel();
	}

	List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();

	TouchModel touchModel;
	DockLayoutPanel contentPanel;
	private ToolBar toolBar;

	EuclidianViewPanel euclidianViewPanel;
	AlgebraViewPanel algebraViewPanel;

	StyleBar styleBar;

	private FlowPanel algebraViewButtonPanel, algebraViewArrowPanel;

	private TouchApp app;

	private boolean editing = true;

	private StandardImageButton algebraButton;

	/**
	 * Sets the viewport and other settings, creates a link element at the end
	 * of the head, appends the css file and initializes the GUI elements.
	 */
	public TabletGUI() {
		// required to start the kernel
		this.euclidianViewPanel = new EuclidianViewPanel();
	}

	public void addResizeListener(ResizeListener rl) {
		this.resizeListeners.add(rl);
	}

	@Override
	public void allowEditing(boolean b) {
		if (this.editing == b) {
			return;
		}
		this.editing = b;
		this.resetMode();
		this.toolBar.setVisible(b);
		this.algebraViewButtonPanel.setVisible(b);
		this.setAlgebraVisible(this.isAlgebraShowing());
		this.styleBar.setVisible(b);

		if (b) {
			this.touchModel.getGuiModel().setStyleBar(this.styleBar);
		} else {
			this.touchModel.getGuiModel().setStyleBar(null);
		}
	}

	public void editTitle() {
		if (this.getHeaderWidget() instanceof TabletHeaderPanel) {
			((TabletHeaderPanel) this.getHeaderWidget()).editTitle();
		}
	}

	@Override
	public AlgebraViewPanel getAlgebraViewPanel() {
		return this.algebraViewPanel;
	}

	public TouchApp getApp() {
		return this.app;
	}

	public String getConstructionTitle() {
		if (this.getHeaderWidget() instanceof TabletHeaderPanel) {
			return ((TabletHeaderPanel) this.getHeaderWidget())
					.getConstructionTitle();
		}
		return "";
	}

	public DockLayoutPanel getContentPanel() {
		return this.contentPanel;
	}

	@Override
	public EuclidianViewPanel getEuclidianViewPanel() {
		return this.euclidianViewPanel;
	}

	public Widget getEuWidget() {
		return this.getContentWidget();
	}

	public TouchModel getTouchModel() {
		return this.touchModel;
	}

	/**
	 * Creates a new instance of {@link TouchController} and
	 * {@link MobileAlgebraController} and initializes the
	 * {@link EuclidianViewPanel euclidianViewPanel} and
	 * {@link AlgebraViewPanel algebraViewPanel} according to these instances.
	 * 
	 * @param kernel
	 *            Kernel
	 */
	@Override
	public void initComponents(final Kernel kernel) {
		this.touchModel = new TouchModel(kernel, this);
		this.app = (TouchApp) kernel.getApplication();
		// Initialize GUI Elements
		TouchEntryPoint.getLookAndFeel().buildHeader(this, this.touchModel);

		this.contentPanel = new DockLayoutPanel(Unit.PX);

		final TouchController ec = new TouchController(this.touchModel,
				this.app);
		ec.setKernel(kernel);

		final int width = Window.getClientWidth() - computeAlgebraWidth();
		final int height = Window.getClientHeight()
				- TouchEntryPoint.getLookAndFeel().getPanelsHeight();
		this.euclidianViewPanel.setPixelSize(width, height);
		this.euclidianViewPanel.initEuclidianView(ec, super.getHeaderWidget(),
				width, height);

		this.styleBar = new StyleBar(this.touchModel,
				this.euclidianViewPanel.getEuclidianView());
		this.touchModel.getGuiModel().setStyleBar(this.styleBar);

		this.algebraViewPanel = new AlgebraViewPanel(ec, this, kernel);

		this.contentPanel.addEast(this.algebraViewPanel, computeAlgebraWidth());
		this.contentPanel.add(this.euclidianViewPanel);
		this.contentPanel.setHeight("100%");

		this.euclidianViewPanel.add(this.styleBar);
		this.euclidianViewPanel.setWidgetPosition(this.styleBar, 0, 0);

		this.setContentWidget(this.contentPanel);

		this.toolBar = new ToolBar(this.touchModel, this.app, this);
		this.setFooterWidget(this.toolBar);

		// show/hide AlgebraView Button
		this.algebraViewButtonPanel = new FlowPanel();

		// Prevent events from getting through to the canvas
		this.algebraViewButtonPanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, ClickEvent.getType());

		this.algebraViewButtonPanel.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}

		}, MouseDownEvent.getType());

		this.algebraViewButtonPanel.addDomHandler(new TouchStartHandler() {

			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}

		}, TouchStartEvent.getType());

		this.algebraViewArrowPanel = new FlowPanel();
		this.algebraViewArrowPanel.setStyleName("algebraViewArrowPanel");

		this.algebraButton = new StandardImageButton(TouchEntryPoint
				.getLookAndFeel().getIcons().triangle_left());
		this.algebraButton.setStyleName("arrowRight");
		this.algebraButton = TouchEntryPoint.getLookAndFeel()
				.setAlgebraButtonHandler(this.algebraButton, this);

		this.algebraViewArrowPanel.add(this.algebraButton);

		this.euclidianViewPanel.add(this.algebraViewButtonPanel);
		this.euclidianViewPanel.setWidgetPosition(this.algebraViewButtonPanel,
				width - TabletGUI.ALGEBRA_BUTTON_WIDTH, 0);

		this.algebraViewButtonPanel.setStyleName("algebraViewButtonPanel");

		this.algebraViewButtonPanel.add(this.algebraViewArrowPanel);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				TabletGUI.this.onResize(event);

				if (TabletGUI.this.touchModel != null) {
					TabletGUI.this.touchModel.getGuiModel().closeOptions();
				}
			}
		});
	}

	@Override
	public boolean isAlgebraShowing() {
		return this.algebraViewPanel.isVisible();
	}

	protected void onResize(ResizeEvent event) {
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize(event);
		}

		this.contentPanel.setPixelSize(event.getWidth(), event.getHeight()
				- getLaf().getPanelsHeight());
		this.contentPanel.onResize();
		this.updateViewSizes(this.algebraViewPanel.isVisible());

		// this.toolBar.setWidth(event.getWidth() + "px");

		// ToolBar
		this.toolBar.onResize();

		this.touchModel.getGuiModel().closeOptions();
	}

	@Override
	public void resetMode() {
		this.touchModel.setCommand(ToolBarCommand.Move_Mobile);
		this.touchModel.getGuiModel().updateStyleBar();
	}

	public void restoreEuclidian(DockLayoutPanel panel) {
		this.contentPanel = panel;
		this.setContentWidget(this.contentPanel);
		this.contentPanel.setPixelSize(Window.getClientWidth(),
				Window.getClientHeight() - getLaf().getPanelsHeight());
	}

	@Override
	public void setAlgebraVisible(boolean visible) {
		this.updateViewSizes(visible);
		this.algebraViewPanel.setVisible(visible);
	}

	@Override
	public void setLabels() {
		if (this.algebraViewPanel != null) {
			this.algebraViewPanel.setLabels();
		}
		if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null) {
			TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel().setLabels();
		}
		this.toolBar.setLabels();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (this.algebraViewButtonPanel != null) {
			this.algebraViewButtonPanel.setVisible(this.editing && visible);
		}
	}

	public void toggleAlgebraView() {
		this.setAlgebraVisible(!this.algebraViewPanel.isVisible());
		this.app.setUnsaved();
	}

	public void updateViewSizes(boolean algebraVisible) {

		final int panelHeight = this.editing ? TouchEntryPoint.getLookAndFeel()
				.getPanelsHeight() : TouchEntryPoint.getLookAndFeel()
				.getAppBarHeight();

		this.algebraButton.setStyleName(algebraVisible ? "arrowRight"
				: "arrowLeft");

		if (!algebraVisible) {
			this.contentPanel.setWidgetSize(this.algebraViewPanel, 0);
			this.euclidianViewPanel.setPixelSize(Window.getClientWidth(),
					Window.getClientHeight() - panelHeight);

			// for Win8 position it on top, for others under appbar

			// this.algebraViewButtonPanel.setPopupPosition(Window.getClientWidth()
			// -
			// ALGEBRA_BUTTON_WIDTH,
			// TouchEntryPoint.getLookAndFeel().getAppBarHeight());
			this.euclidianViewPanel.setWidgetPosition(
					this.algebraViewButtonPanel, Window.getClientWidth()
							- ALGEBRA_BUTTON_WIDTH, 0);

			// Set algebraviewbutton transparent, when algebra view is closed
			this.algebraViewButtonPanel.addStyleName("transparent");
		} else {
			this.contentPanel.setWidgetSize(this.algebraViewPanel,
					computeAlgebraWidth());

			final int euclidianWidth = Window.getClientWidth()
					- computeAlgebraWidth();
			if (euclidianWidth <= 0) {
				this.algebraViewPanel.addInsideArrow();
			} else {
				this.algebraViewPanel.removeInsideArrow();
			}
			this.euclidianViewPanel.setPixelSize(euclidianWidth,
					Window.getClientHeight() - panelHeight);

			// for Win8 position it on top, for others under appbar
			this.euclidianViewPanel.setWidgetPosition(
					this.algebraViewButtonPanel, Window.getClientWidth()
							- TabletGUI.computeAlgebraWidth()
							- ALGEBRA_BUTTON_WIDTH, 0);

			// this.algebraViewButtonPanel.setPopupPosition(euclidianWidth -
			// ALGEBRA_BUTTON_WIDTH,
			// TouchEntryPoint.getLookAndFeel().getAppBarHeight());
			this.euclidianViewPanel.setWidgetPosition(
					this.algebraViewButtonPanel, Window.getClientWidth()
							- TabletGUI.computeAlgebraWidth()
							- ALGEBRA_BUTTON_WIDTH, 0);

			// Set algebraviewbutton nontransparent, when algebra view is open
			this.algebraViewButtonPanel.removeStyleName("transparent");
			this.algebraViewPanel.onResize();
		}
	}
}
