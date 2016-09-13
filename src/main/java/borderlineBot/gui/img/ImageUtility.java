package borderlineBot.gui.img;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImageUtility {
	
	/** Empty Panel Object That Is Used To Easily Import Images */
	private static final PictureHelper PANEL = new PictureHelper();
	
	/** Returns Scaled Input Image According To Width And Height */
	public static Image scaleImage(Image image, int width, int height){
		try{
			Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);   
			ImageIcon icon = new ImageIcon(scaledImage);
			return icon.getImage();
		}catch(Exception ex){
			//In Case The Scaling Was Not Successful It Will Just Be Retried
			return scaleImage(image, width, height);
		}
	}
	
	/** Returns Picture Of Image With Name */
	public static Image getImage(String name){
		return PANEL.importImage(name);
	}
	
	/** Returns Picture of Image from outside the project */
	public static Image getOutsideImage(String name){
		return PANEL.importImageOutside(name);
	}
	
	/** Returns 2D Array Of Cropped Image With Name And With X Parts On The X Axis And Y Parts On The Y Axis */
	public static Image[][] getCroppedImage(String name, int x, int y){
		Image[][] images = PANEL.cropImage(PANEL.importImage(name), x, y);
		return images;
	}
	
	/** Returns 2D Array Of Cropped Image Of Image And With X Parts On The X Axis And Y Parts On The Y Axis */
	public static Image[][] getCroppedImage(Image img, int x, int y){
		Image[][] images = PANEL.cropImage(img, x, y);
		return images;
	}
	
	/** Class Used To Help With Basic Image Loading And Cropping, Since JPanel Needed */
	static class PictureHelper extends JPanel{
		/** Returns Imported Image of Name (Used By Picture To Import Images) */
		protected Image importImage(String name){
			name = name+".png";
			return new ImageIcon(this.getClass().getResource(name)).getImage();
		}
		/** Imports an Image from the outside */
		protected Image importImageOutside(String name){
			if(!name.contains(".png")){
				name += ".png";
			}
			BufferedImage img = null;
			try {
			    img = ImageIO.read(new File(name));
			} catch(Exception e){}
			return img;
		}
		/** Returns 2D Image Array Of Image With Y And X Defining The Subsections (Used By Picture To Subdivide Images) */
		protected Image[][] cropImage(Image image, int x, int y){
			int width = -1;
			while(width==-1)width = image.getWidth(null);
			int height = -1;
			while(height==-1)height = image.getHeight(null);
			Image[][] images = new Image[y][x];
			for(int cy=0; cy<y; cy++){
				for(int cx=0; cx<x; cx++){
					images[cy][cx] = createImage(new FilteredImageSource(image.getSource(), new CropImageFilter(cx*(width/x), cy*(height/y), width/x, height/y)));
				}
			}
			return images;
		}
	}
	
	//--classes--
	/** Enum listing all internal images */
	public static enum Picture{
		
		TILE_NEUTRAL("tileNeutral"),
		TILE_RED("tileRed"),
		TILE_BLUE("tileBlue"),
		
		RED_ONE("red1"),
		RED_TWO("red2"),
		BLUE_ONE("blue1"),
		BLUE_TWO("blue2");
		
		/** Path to the image */
		private String path;
		/** Construct New Picture */
		private Picture(String path){
			this.path = path;
		}
		/** Returns Path */
		public String path(){
			return path;
		}
		
	}
	
}
