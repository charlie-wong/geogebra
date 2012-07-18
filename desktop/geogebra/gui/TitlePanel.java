/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.gui;

import geogebra.common.kernel.Construction;
import geogebra.gui.inputfield.MyFormattedTextField;
import geogebra.gui.inputfield.MyTextField;
import geogebra.main.AppD;
import geogebra.main.GeoGebraPreferencesD;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel with title, author and date of construction. Forwards all updates to
 * kernel and notifies attached ActionListeners about kernel changes. Thus, it
 * can be used to edit the aforementioned values in the kernel.
 * 
 * @author Markus Hohenwarter
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class TitlePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField titleField, authorField;

	private JFormattedTextField dateField;

	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();

	private Construction cons;

	public TitlePanel(AppD app) {
		cons = app.getKernel().getConstruction();

		setLayout(new BorderLayout(5, 5));
		titleField = new MyTextField(app);
		authorField = new MyTextField(app);
		dateField = new MyFormattedTextField(app.getGuiManager(),DateFormat
				.getDateInstance(DateFormat.LONG));
		dateField.setColumns(12);
		dateField.setFocusLostBehavior(JFormattedTextField.PERSIST);
		dateField.setFont(app.getPlainFont());

		updateData();

		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.add(new JLabel(app.getPlain("Title") + ": "), BorderLayout.WEST);
		p.add(titleField, BorderLayout.CENTER);
		add(p, BorderLayout.NORTH);

		p = new JPanel(new BorderLayout(5, 5));
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(new JLabel(app.getPlain("Author") + ": "), BorderLayout.WEST);
		p1.add(authorField, BorderLayout.CENTER);
		p.add(p1, BorderLayout.CENTER);

		p1 = new JPanel(new BorderLayout());
		p1.add(new JLabel(app.getPlain("Date") + ": "), BorderLayout.WEST);
		p1.add(dateField, BorderLayout.CENTER);

		p.add(p1, BorderLayout.EAST);
		add(p, BorderLayout.CENTER);

		setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5,
				5)));

		// setBorder(BorderFactory.createTitledBorder(app
		// .getPlain("Document info")));

		ActionListener lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireTextFieldUpdate((JTextField) e.getSource());
			}
		};
		titleField.addActionListener(lst);
		authorField.addActionListener(lst);
		dateField.addActionListener(lst);

		FocusAdapter focusListener = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				fireTextFieldUpdate((JTextField) e.getSource());
			}
		};
		titleField.addFocusListener(focusListener);
		authorField.addFocusListener(focusListener);
		dateField.addFocusListener(focusListener);
	}
	
	public void updateData() {
		titleField.setText(cons.getTitle());
		authorField.setText(loadAuthor());

		dateField.setText(configureDate(cons.getDate()));
	}

	public String configureDate(String src) {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();

		// If no date specified use current date
		if (src.equals("")) {
			return DateFormat.getDateInstance(DateFormat.LONG)
					.format(date);
		} else
			return src;

		/*
		 * Try to parse date with the local date format. If this fails just
		 * display it and let the user re-edit it. To draw user attention to
		 * this shortcoming we highlight the text field with red and let it have
		 * the focus. TODO: Is this sufficient enough without any textual advice
		 * such as a dialog?
		 * 
		 * COMMENTED by Markus Hohenwarter: the change of background color does
		 * not really help the user, moreover it does not work at the moment ...
		 * else { try { date = DateFormat.getDateInstance().parse(src); } catch
		 * (ParseException e) { dateField.setBackground(new Color(255, 48, 48));
		 * dateField.requestFocusInWindow(); return src; } }
		 */

	}

	public String loadAuthor() {
		String author = cons.getAuthor();
		if ("".equals(author)) {
			author = 
				GeoGebraPreferencesD.getPref()
				.loadPreference(
					GeoGebraPreferencesD.AUTHOR, "");
			cons.setAuthor(author);
		}
		return author;
	}

	private boolean saveAuthor(String author) {
		boolean kernelChanged = !author.equals(cons.getAuthor());
		if (kernelChanged) {
			cons.setAuthor(author);
			GeoGebraPreferencesD.getPref()
				.savePreference(GeoGebraPreferencesD.AUTHOR,
					author);
		}
		return kernelChanged;
	}

	/**
	 * Updates the kernel if the user makes changes to the text fields.
	 */
	private void fireTextFieldUpdate(JTextField tf) {
		String text = tf.getText();
		boolean kernelChanged = false;

		if (tf == titleField) {
			if (text.equals(cons.getTitle()))
				return;
			cons.setTitle(text);
			kernelChanged = true;
		} else if (tf == authorField) {
			kernelChanged = saveAuthor(tf.getText());
		} else if (tf == dateField) {
			if (text.equals(cons.getDate()))
				return;
			cons.setDate(text);
			kernelChanged = true;
		}

		if (kernelChanged) {
			notifyListeners();
		}
	}

	public void addActionListener(ActionListener lst) {
		listeners.add(lst);
	}

	private void notifyListeners() {
		int size = listeners.size();
		for (int i = 0; i < size; i++) {
			listeners.get(i)
					.actionPerformed(new ActionEvent(this,
							ActionEvent.ACTION_PERFORMED, "TitleChanged"));
		}
	}

}
