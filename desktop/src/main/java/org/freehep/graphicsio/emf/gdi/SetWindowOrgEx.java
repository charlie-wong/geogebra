// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetWindowOrgEx TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetWindowOrgEx.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SetWindowOrgEx extends EMFTag {

	private Point point;

	public SetWindowOrgEx() {
		super(10, 1);
	}

	public SetWindowOrgEx(Point point) {
		this();
		this.point = point;
	}

	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetWindowOrgEx tag = new SetWindowOrgEx(emf.readPOINTL());
		return tag;
	}

	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writePOINTL(point);
	}

	public String toString() {
		return super.toString() + "\n" + "  point: " + point;
	}
}
