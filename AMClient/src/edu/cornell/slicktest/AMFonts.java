package edu.cornell.slicktest;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.SlickException;

public class AMFonts {
	private static AngelCodeFont arialRegular12 = null;
	private static AngelCodeFont arialRegular16 = null;
	private static AngelCodeFont arialBold16 = null;
	private static AngelCodeFont arialBlackRegular16 = null;
	private static AngelCodeFont arialBlackRegular32 = null;
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
	public static AngelCodeFont  getArialBlackRegular16() throws SlickException {
		if (arialBlackRegular16 == null) {
			arialBlackRegular16 = new AngelCodeFont("fonts/arialblack-16-regular.fnt", "fonts/arialblack-16-regular_0.tga");
		}
		return arialBlackRegular16;
	}
	public static AngelCodeFont  getArialBlackRegular32() throws SlickException {
		if (arialBlackRegular32 == null) {
			arialBlackRegular32 = new AngelCodeFont("fonts/arialblack-32-regular.fnt", "fonts/arialblack-32-regular_0.tga");
		}
		return arialBlackRegular32;
	}
	public static AngelCodeFont  getArialFrak() throws SlickException {
		if (arialFrak == null) {
			arialFrak = new AngelCodeFont("fonts/arial-frak.fnt", "fonts/arial-frak_0.tga");
		}
		return arialFrak;
	}
	
}
