package edu.cornell.slicktest;

import org.newdawn.slick.Image;

public class Ground
{
	protected Image foreground;
	protected boolean[][] isGround;
	protected int width;
	protected int height;
	
	public Ground(Image f)
	{
		foreground = f;
		height = foreground.getHeight();
		width = foreground.getWidth();
		isGround = new boolean[width][height];
	}
	
	
	public Image getForeground()
	{
		return foreground;
	}
	public void setForeground(Image f)
	{
		foreground = f;
	}
	public int getWidth()
	{
		return width;
	}
	public int getHeight()
	{
		return height;
	}
}
