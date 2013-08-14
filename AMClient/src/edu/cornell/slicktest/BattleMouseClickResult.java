package edu.cornell.slicktest;

public class BattleMouseClickResult {
	public boolean unitIsSelected;
	public int unitSelected;
	public boolean mouseClicked;
	public double mouseX;
	public double mouseY;
	public int battleButtonClicked;
	
	public BattleMouseClickResult(int battleButtonClicked, boolean unitIsSelected, int unitSelected,
			boolean mouseClicked, double mouseX, double mouseY) {
		this.battleButtonClicked = battleButtonClicked;
		this.unitIsSelected = unitIsSelected;
		this.unitSelected = unitSelected;
		this.mouseClicked = mouseClicked;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

}
