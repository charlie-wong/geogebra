package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolcategorization.ToolCategorization;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.Category;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author judit Content of tools tab of Toolbar panel.
 */
public class Tools extends FlowPanel {

	/**
	 * Tool categories
	 */
	ToolCategorization mToolCategorization;
	/**
	 * application
	 */
	AppW app;
	/**
	 * move button
	 */
	StandardButton moveButton;

	/**
	 * @param appl
	 *            application
	 */
	public Tools(AppW appl) {
		app = appl;
		this.addStyleName("toolsPanel");
		buildGui();
	}

	/**
	 * Selects MODE_MOVE as mode and changes visual settings accordingly of
	 * this.
	 */
	public void setMoveMode() {
		app.setMode(EuclidianConstants.MODE_MOVE);
		clearSelectionStyle();
		if (moveButton != null) {
			moveButton.getElement().setAttribute("selected", "true");
		}
	}

	/**
	 * Changes visual settings of selected mode.
	 * 
	 * @param mode
	 *            the mode will be selected
	 */
	public void setMode(int mode) {
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof CategoryPanel) {
				FlowPanel panelTools = ((CategoryPanel) w).getToolsPanel();
				for (int j = 0; j < panelTools.getWidgetCount(); j++) {
					if ((mode + "").equals(panelTools.getWidget(j).getElement()
							.getAttribute("mode"))) {
						panelTools.getWidget(j).getElement()
								.setAttribute("selected", "true");

					} else {
						panelTools.getWidget(j).getElement()
								.setAttribute("selected", "false");
					}
				}
			}
		}

	}

	/**
	 * Clears visual selection of all tools.
	 */
	public void clearSelectionStyle() {
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof CategoryPanel) {
				FlowPanel panelTools = ((CategoryPanel) w).getToolsPanel();
				for (int j = 0; j < panelTools.getWidgetCount(); j++) {
					panelTools.getWidget(j).getElement()
							.setAttribute("selected", "false");
				}
			}
		}
	}

	/**
	 * Builds the panel of tools.
	 */
	public void buildGui() {
		this.clear();

		mToolCategorization = new ToolCategorization(app,
				app.getSettings().getToolbarSettings().getType(), app.getSettings().getToolbarSettings().getToolsetLevel(), false);
		mToolCategorization.resetTools();
		ArrayList<ToolCategorization.Category> categories = mToolCategorization
				.getCategories();

		for (int i = 0; i < categories.size(); i++) {
			add(new CategoryPanel(categories.get(i)));
		}
		
		setMoveMode();
	}

	private class CategoryPanel extends FlowPanel {
		private Category category;
		private FlowPanel toolsPanel;

		public CategoryPanel(ToolCategorization.Category cat) {
			super();
			category = cat;
			initGui();
		}

		private void initGui() {
			add(new Label(mToolCategorization.getLocalizedHeader(category)));

			toolsPanel = new FlowPanel();
			toolsPanel.addStyleName("categoryPanel");
			ArrayList<Integer> tools = mToolCategorization.getTools(
					mToolCategorization.getCategories().indexOf(category));

			for (int i = 0; i < tools.size(); i++) {
				StandardButton btn = getButton(tools.get(i));
				toolsPanel.add(btn);
				if (tools.get(i) == EuclidianConstants.MODE_MOVE) {
					moveButton = btn;
				}
			}
			add(toolsPanel);
		}

		FlowPanel getToolsPanel() {
			return toolsPanel;
		}

		private StandardButton getButton(final int mode) {
			NoDragImage im = new NoDragImage(GGWToolBar
					.getImageURL(mode, app));
			if (mode == EuclidianConstants.MODE_DELETE
					|| mode == EuclidianConstants.MODE_IMAGE) {
				im.addStyleName("plusPadding");
			}
			final StandardButton btn = new StandardButton(null, "", 32, app);
			btn.setTitle(app.getLocalization()
					.getMenu(EuclidianConstants.getModeText(mode)));

			btn.getUpFace().setImage(im);
			btn.getElement().setAttribute("mode", mode + "");

			btn.addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick(Widget source) {
					app.setMode(mode);
					if (!Browser.isMobile()) {
						ToolTipManagerW.sharedInstance().setBlockToolTip(false);
						ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
							app.getToolTooltipHTML(mode),
							app.getGuiManager().getTooltipURL(mode),
							ToolTipLinkType.Help, app,
							app.getAppletFrame().isKeyboardShowing());
						ToolTipManagerW.sharedInstance().setBlockToolTip(true);
					}
					app.updateDynamicStyleBars();
				}

			});

			return btn;
		}
	}

}