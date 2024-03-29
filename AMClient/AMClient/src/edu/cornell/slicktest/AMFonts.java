package edu.cornell.slicktest;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.SlickException;

public class AMFonts {
	private static AngelCodeFont arialRegular12 = null;
	private static AngelCodeFont arialRegular16 = null;
	private static AngelCodeFont arialBold16 = null;
	private static AngelCodeFont arialBlackBold16 = null;
	private static AngelCodeFont arialFrak = null;
	
	public static AngelCodeFont  getArialRegular12() throws SlickException {
		if (arialRegular12 == null) {
			arialRegular12 = new AngelCodeFont("fonts/arial-12-regular.fnt", "fonts/arial-12-regular_0.tga");
		}
		return arialRegular12;
	}
	public static AngelCodeFont  getArialRegular16() throws SlickException {
		if (arialRegular16 == null) {
			arialRegular16 = new AngelCodeFont("fonts/arial-16-regular.fnt", "fonts/arial-16-regular_0.tga");
		}
		return arialRegular16;
	}
	public static AngelCodeFont  getArialBold16() throws SlickException {
		if (arialBold16 == null) {
			arialBold16 = new AngelCodeFont("fonts/arial-16-bold.fnt", "fonts/arial-16-bold_0.tga");
		}
		return arialBold16;
	}
	public static AngelCodeFont  getArialBlackBold16() throws SlickException {
		if (arialBlackBold16 == null) {
			arialBlackBold16 = new AngelCodeFont("fonts/arialblack-16-bold.fnt", "fonts/arialblack-16-bold_0.tga");
		}
		return arialBlackBold16;
	}
	public static AngelCodeFont  getArialFrak() throws SlickException {
		if (arialFrak == null) {
			arialFrak = new AngelCodeFont("fonts/arial-frak.fnt", "fonts/arial-frak_0.tga");
		}
		return arialFrak;
	}
}
