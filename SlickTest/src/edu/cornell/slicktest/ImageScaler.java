package edu.cornell.slicktest;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;

public class ImageScaler 
{
	protected Image currentImage;
	protected ImageBuffer buffer;
	protected int oldWidth;
	protected int newWidth;
	protected int oldHeight;
	protected int newHeight;
	
	public ImageScaler(){}
	
	public Image scaleImage(Image image, int scale)
	{
		currentImage = image;
		oldWidth = currentImage.getWidth();
		oldHeight = currentImage.getHeight();
		newWidth = (int) currentImage.getWidth() * scale;
		newHeight = (int) currentImage.getHeight() * scale;
		buffer = new ImageBuffer(newWidth, newHeight);
		loadToBuffer();
		return buffer.getImage();
	}
	
	private void loadToBuffer()
	{
		for(int x = 0; x < newWidth; x++)
		{
			for(int y = 0; y < newHeight; y++)
			{
				
			}
		}
	}
	
	/*private Color bilerp(int x, int y)
	{
		
	}*/
}
