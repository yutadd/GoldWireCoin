package jp.sugoi;

import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

public class ImageEditor {
	public ImageEditor() {
	}
	ImageIcon make(String path,double x,double y) throws Exception{
		try {
			System.out.println(new File(path).exists());
			ImageIcon desktop=new ImageIcon(path);
			Image bigImgr =desktop.getImage().getScaledInstance(-1, (int)((desktop.getIconHeight()*y )), Image.SCALE_SMOOTH);
			Image bigImg_r = bigImgr.getScaledInstance((int)((desktop.getIconWidth()*x )), -1, Image.SCALE_SMOOTH);
			return new ImageIcon(bigImg_r);
		}catch(Exception e) {
			System.out.println("Image : "+path+" not found");
			throw new Exception();
		}
	}

}
