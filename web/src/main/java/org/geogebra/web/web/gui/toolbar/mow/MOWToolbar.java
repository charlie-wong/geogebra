package org.geogebra.web.web.gui.toolbar.mow;


import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * The toolbar for MOW.
 * 
 * @author Laszlo Gal, Alicia Hofstaetter
 *
 */
public class MOWToolbar extends FlowPanel implements FastClickHandler {

	private static final int DEFAULT_SUBMENU_HEIGHT = 120;
	private AppW app;
	private StandardButton redoButton;
	private StandardButton undoButton;
	private StandardButton closeButton;
	private StandardButton penButton;
	private StandardButton toolsButton;
	private StandardButton mediaButton;
	private FlowPanel leftPanel;
	private FlowPanel middlePanel;
	private FlowPanel rightPanel;
	private SubMenuPanel currentMenu = null;
	private SubMenuPanel penMenu;
	private SubMenuPanel toolsMenu;
	private SubMenuPanel mediaMenu;
	private FlowPanel subMenuPanel;
	private boolean isSubmenuOpen;
	private ToolbarResources pr;

	/**
	 *
	 * @param app
	 *            application
	 */
	public MOWToolbar(AppW app) {
		this.app = app;
		buildGUI();
	}

	/**
	 * Builds main GUI parts: left (undo/redo), middle (pen/tools/media) and
	 * right (move) panels
	 */
	protected void buildGUI() {
		pr = ((ImageFactory) GWT.create(ImageFactory.class)).getToolbarResources();
		leftPanel = new FlowPanel();
		leftPanel.addStyleName("left");

		middlePanel = new FlowPanel();
		middlePanel.addStyleName("middle");

		rightPanel = new FlowPanel();
		rightPanel.addStyleName("right");

		createUndoRedo();
		// midddle buttons open submenus
		createMiddleButtons();
		createCloseButton();
		add(LayoutUtilW.panelRow(leftPanel, middlePanel, rightPanel));

		subMenuPanel = new FlowPanel();
		add(subMenuPanel);
		addStyleName("mowToolbar");
		// sets the horizontal position of the toolbar
		setResponsivePosition();

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				setResponsivePosition();
			}
		});


	}

	private void createMiddleButtons() {
		createPenButton();
		createToolsButton();
		createMediaButton();

		middlePanel.add(penButton);
		middlePanel.add(toolsButton);
		middlePanel.add(mediaButton);
	}

	/**
	 * Creates a button for MOW main toolbar.
	 * 
	 * @param url
	 *            The image URL for the button.
	 * @param handler
	 *            The handler of the button.
	 * @return the newly created button for the toolbar.
	 */
	public StandardButton createButton(String url,
			FastClickHandler handler) {
		NoDragImage im = new NoDragImage(url);
		StandardButton btn = new StandardButton(null, "", 24, app);
		btn.getUpFace().setImage(im);
		btn.addFastClickHandler(handler);
		return btn;
	}

	private void createPenButton() {
		penButton = createButton(GGWToolBar.getImageURL(EuclidianConstants.MODE_PEN_PANEL, app), this);
		penMenu = new PenSubMenu(app);
	}

	private void createToolsButton() {
		toolsButton = createButton(GGWToolBar.getImageURL(EuclidianConstants.MODE_TOOLS_PANEL, app), this);
		toolsMenu = new ToolsSubMenu(app);
	}

	private void createMediaButton() {
		mediaButton = createButton(GGWToolBar.getImageURL(EuclidianConstants.MODE_MEDIA_PANEL, app), this);
		mediaMenu = new MediaSubMenu(app);
	}

	private void createCloseButton() {
		closeButton = new StandardButton("", app);
		closeButton.getUpFace()
				.setImage(new Image(MaterialDesignResources.INSTANCE
						.toolbar_close_portrait_black()));
		rightPanel.add(closeButton);
		closeButton.addFastClickHandler(this);
	}

	/**
	 * Toggles the pen panel icon
	 * 
	 * @param toggle
	 *            true = highlighted icon, false = gray icon
	 */
	private void togglePenButton(boolean toggle) {
		NoDragImage upFace = getImage(pr.pen_panel_24(), 24);
		NoDragImage downFace = getImage(pr.pen_panel_active_24(), 24);
		if (toggle) {
			penButton.getUpFace().setImage(downFace);
		} else {
			penButton.getUpFace().setImage(upFace);
		}
	}

	/**
	 * Toggles the tools panel icon
	 * 
	 * @param toggle
	 *            true = highlighted icon, false = gray icon
	 */
	private void toggleToolsButton(boolean toggle) {
		NoDragImage upFace = getImage(pr.tools_panel_24(), 24);
		NoDragImage downFace = getImage(pr.tools_panel_active_24(), 24);
		if (toggle) {
			toolsButton.getUpFace().setImage(downFace);
		} else {
			toolsButton.getUpFace().setImage(upFace);
		}
	}

	/**
	 * Toggles the media panel icon
	 * 
	 * @param toggle
	 *            true = highlighted icon, false = gray icon
	 */
	private void toggleMediaButton(boolean toggle) {
		NoDragImage upFace = getImage(pr.media_panel_24(), 24);
		NoDragImage downFace = getImage(pr.media_panel_active_24(), 24);
		if (toggle) {
			mediaButton.getUpFace().setImage(downFace);
		} else {
			mediaButton.getUpFace().setImage(upFace);
		}
	}

	/**
	 * Toggles the open/close toolbar icon
	 * 
	 * @param toggle
	 * 
	 */
	public void toggleCloseButton(boolean toggle) {
		Image upFace = new Image(
				MaterialDesignResources.INSTANCE.toolbar_open_portrait_black());
		Image downFace = new Image(MaterialDesignResources.INSTANCE
				.toolbar_close_portrait_black());
		if (toggle) {
			closeButton.getUpFace().setImage(downFace);
		} else {
			closeButton.getUpFace().setImage(upFace);
		}
	}

	/**
	 * Toggles the toolbar icons - only 1 icon highlighted at a time
	 * 
	 * @param button
	 *            the button to be highlighted
	 */
	private void setButtonActive(Widget button) {
		if (button == penButton) {
			togglePenButton(true);
			toggleToolsButton(false);
			toggleMediaButton(false);
		}
		if (button == toolsButton) {
			togglePenButton(false);
			toggleToolsButton(true);
			toggleMediaButton(false);
		}
		if (button == mediaButton) {
			togglePenButton(false);
			toggleToolsButton(false);
			toggleMediaButton(true);
		}

	}

	/**
	 * Updates the toolbar ie. undo/redo button states
	 */
	public void update() {
		updateUndoActions();
	}

	private void createUndoRedo() {
		redoButton = new StandardButton(
				MaterialDesignResources.INSTANCE.redo_black(), app);
		redoButton.addFastClickHandler(this);

		undoButton = new StandardButton(
				MaterialDesignResources.INSTANCE.undo_black(), app);
		undoButton.addFastClickHandler(this);

		leftPanel.add(undoButton);
		leftPanel.add(redoButton);
		updateUndoActions();
	}

	/**
	 * Update enabled/disabled state of undo and redo buttons.
	 */
	public void updateUndoActions() {
		if (undoButton != null) {
			this.undoButton.setEnabled(app.getKernel().undoPossible());
		}
		if (this.redoButton != null) {
			this.redoButton.setVisible(app.getKernel().redoPossible());
		}
	}

	/**
	 * @param uri
	 *            image URI
	 * @param width
	 *            size
	 * @return image wrapped in no-dragging widget
	 */
	public static NoDragImage getImage(ResourcePrototype uri, int width) {
		return new NoDragImage(ImgResourceHelper.safeURI(uri), width);
	}


	@Override
	public void onClick(Widget source) {
		if (source == redoButton) {
			app.getGuiManager().redo();
			app.hideKeyboard();
		} else if (source == undoButton) {
			app.getGuiManager().undo();
			app.hideKeyboard();

		} else if (source == closeButton) {
			if (subMenuPanel.isVisible()) {
				setSubmenuVisible(false);
				toggleCloseButton(false);
			} else {
				setSubmenuVisible(true);
				toggleCloseButton(true);
			}

		} else if (source == penButton) {
			setCurrentMenu(penMenu);
			toggleCloseButton(true);
		} else if (source == toolsButton) {
			setCurrentMenu(toolsMenu);
			toggleCloseButton(true);
		} else if (source == mediaButton) {
			setCurrentMenu(mediaMenu);
			toggleCloseButton(true);
		}
		setButtonActive(source);
	}

	private SubMenuPanel getSubMenuForMode(int mode) {
		if (mode == EuclidianConstants.MODE_TEXT
				|| mode == EuclidianConstants.MODE_IMAGE
				|| mode == EuclidianConstants.MODE_VIDEO
				|| mode == EuclidianConstants.MODE_AUDIO
				|| mode == EuclidianConstants.MODE_GEOGEBRA) {
			return mediaMenu;
		} else if (mode == EuclidianConstants.MODE_PEN
				|| mode == EuclidianConstants.MODE_ERASER) {
			return penMenu;
		} else if (mode == EuclidianConstants.MODE_MOVE
				|| mode == EuclidianConstants.MODE_SELECT) {
			if (currentMenu != null) {
				return currentMenu;
			}
			return penMenu;
		} else {
			return toolsMenu;
		}
	}

	/**
	 * Set the toolbar state for the selected mode
	 * 
	 * @param mode
	 *            the mode to set.
	 */
	public void setMode(int mode) {

		if (mode == EuclidianConstants.MODE_PEN) {
			setButtonActive(penButton);
		}

		if (!(mode == EuclidianConstants.MODE_PEN
				&& currentMenu == penMenu)) {
			doSetCurrentMenu(getSubMenuForMode(mode));
		}

		if (currentMenu != null) {
			currentMenu.setMode(mode);
		}
	}


	/**
	 * 
	 * @return The current submenu panel that is visible or last used.
	 */
	public SubMenuPanel getCurrentMenu() {
		return currentMenu;
	}

	/**
	 * Sets the actual submenu, and opens it if it is different than the last
	 * one, toggles its visibility otherwise.
	 * 
	 * @param submenu
	 *            The submenu panel to set.
	 */
	public void setCurrentMenu(SubMenuPanel submenu) {
		if (submenu == null) {
			setSubmenuVisible(false);
			currentMenu = null;
			return;
		}

		if (currentMenu == submenu) {
			if (!subMenuPanel.isVisible()) {
				currentMenu.onOpen();
				setSubmenuVisible(true);
			}
			return;
		}
		// this will call setMode => submenu is open
		app.setMode(submenu.getFirstMode());
	}

	private void doSetCurrentMenu(SubMenuPanel submenu) {
		subMenuPanel.clear();
		this.currentMenu = submenu;
		subMenuPanel.add(currentMenu);
		setSubmenuVisible(true);

	}

	/**
	 * Set the submenu visible / invisible and adds the animation
	 * 
	 * @param visible
	 *            true if submenu should be visible
	 */
	private void setSubmenuVisible(final boolean visible) {

		if (visible) {
			subMenuPanel.setVisible(visible);
			if (!isSubmenuOpen) {
				setStyleName("showSubmenu");
			}
			isSubmenuOpen = true;
		} else {
			if (isSubmenuOpen) {
				setStyleName("hideSubmenu");
			}
			Timer timer = new Timer() {
				@Override
				public void run() {
					doShowSubmenu(visible);
				}
			};
			timer.schedule(250);
			isSubmenuOpen = false;
		}
		addStyleName("mowToolbar");
		setResponsivePosition();
	}

	/**
	 * @param visible
	 *            whether to show the subpanel
	 */
	protected void doShowSubmenu(boolean visible) {
		subMenuPanel.setVisible(visible);

	}

	/**
	 * Sets the horizontal position of the toolbar depending on screen size
	 */
	public void setResponsivePosition() {
		// small screen
		if (app.getWidth() < 700) {
			removeStyleName("BigScreen");
			addStyleName("SmallScreen");
			getElement().getStyle().setLeft(0, Unit.PX);
		} // big screen
		else {
			removeStyleName("SmallScreen");
			addStyleName("BigScreen");
			getElement().getStyle().setLeft((app.getWidth() - 700) / 2, Unit.PX);
		}
	}

}