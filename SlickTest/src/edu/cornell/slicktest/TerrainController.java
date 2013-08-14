package edu.cornell.slicktest;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;

public class TerrainController 
{
	protected Ground ground;
	protected ImageBuffer groundBuffer;
	protected int width;
	protected int height;
	
	public TerrainController(Ground g)
	{
		ground = g;
		groundBuffer = new ImageBuffer(width, height);
		initializeBuffer();
	}
	
	private void initializeBuffer()
	{
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				Color pixel = ground.foreground.getColor(i, j);
				groundBuffer.setRGBA(i, j, pixel.getRed(), pixel.getGreen(), pixel.getBlue(), pixel.getAlpha()); //NOTE: Transparent = 0
			}
		}
	}
	

	
	public Image explode(int radius, int x, int y, Image foreground)
	{
		for(int i = (x - radius); i < (x + radius); i++)
		{
			for(int j = (y - radius); j < (y + radius); j++)
			{
				if(pythag(i, j, x, y) <= radius)
				{
					if (i < ground.width && 0 < i && j < ground.height && 0 < j){
						ground.isGround[i][j]= false;
						Color pixel = ground.foreground.getColor(i, j);
						groundBuffer.setRGBA(i, j, pixel.getRed(), pixel.getGreen(), pixel.getBlue(), 0);
					}
				}
			}
		}
		return groundBuffer.getImage();
	}
	
	private double pythag(int x0, int y0, int x1, int y1)
	{
		int x = x1 - x0;
		int y = y1 - y0;
		return (Math.sqrt(Math.pow(x, 2) + Math.pow(y,  2)));
	}
	

	/*public Image updateGround(Image ground)
	{
		ImageBuffer buffer = new ImageBuffer(width, height);
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				Color pixel = ground.getColor(i, j);
				if(groundCurrent[i][j] == false)
				{
					buffer.setRGBA(i, j, pixel.getRed(), pixel.getGreen(), pixel.getBlue(), 255);
				}
				else
				{
					
					buffer.setRGBA(i, j, pixel.getRed(), pixel.getGreen(), pixel.getBlue(), pixel.getAlpha());
				}
			}
		}
		return buffer.getImage();
	}*/
}
