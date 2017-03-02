package org.pzone.md.upload;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import javax.imageio.ImageIO;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

public class MarkdownHelper {

	Properties prop=null;
	InputStream is = null;
	public static void main(String[] args) {
		if (args.length !=1)
			return;
		MarkdownHelper mdhelper=new MarkdownHelper();
		String mode = args[0];
		if (mode.equals("-c")) {
			mdhelper.fromClipboard();
		} else if (mode.equals("-f")) {
			mdhelper.fromFile();
		} else {
			return;
		}
	}
	/**
	 *  读取配置文件
	 */
    private void config(){
    	ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    	InputStream in=null;
    	if(classloader.getResource("")==null){
    		try {
    			in = new FileInputStream("conf/config.properties");
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}else
    		in = classloader.getResourceAsStream("config.properties");
    	prop = new Properties();
		try {
			prop.load(in);
			in.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
    /**
     *  从剪贴板获取图片上传
     */
    public void fromClipboard(){
    	if(prop==null)
    		this.config();
    	Image image = getImageClipboard();
		if (image == null) {
			System.out.println("剪贴板中没有图片");
			return;
		}
		is = getStreamFromImage(image);
		upload();
    }
    /**
     *  从本地选择文件上传
     */
    public void fromFile(){
    	if(prop==null)
    		this.config();
		Frame frame = new Frame();
		FileDialog fd = new FileDialog(frame, "打开图片", FileDialog.LOAD);
		fd.setVisible(true);
		if (fd.getFile() != null) {
			File f = new File(fd.getDirectory(), fd.getFile());
			try {
				is = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			frame.dispose();
		} else {
			frame.dispose();
			return;
		}
		upload();
    }
    /**
     *  上传图片到七牛云
     */
	private void upload() {
		// 构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());
		// ...其他参数参考类注释
		UploadManager uploadManager = new UploadManager(cfg);
		// ...生成上传凭证，然后准备上传
		String accessKey = prop.getProperty("ak");
		String secretKey = prop.getProperty("sk");
		String bucket = prop.getProperty("bucket");
		// 默认不指定key的情况下，以文件内容的hash值作为文件名
		String key = getUUID() + ".png";
		//System.out.println(key);
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket);
		try {
			Response response = uploadManager.put(is, key, upToken, null, null);
			// 解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			System.out.println(putRet.key);
			//System.out.println(putRet.hash);
			setSystemClipboard(prop.getProperty("url_prefix")+putRet.key);
		} catch (QiniuException ex) {
			Response r = ex.response;
			System.err.println(r.toString());
			try {
				System.err.println(r.bodyString());
			} catch (QiniuException ex2) {
				// ignore
			}
		}

	}
    /**
     * Image转化为输入流
     * @param image
     * @return 流
     */
	private InputStream getStreamFromImage(Image image) {
		try {
			BufferedImage buff_image = null;
			// 根据图片创建BufferedImage对象
			buff_image = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			// 将图片载入到BufferedImage
			Graphics2D g2 = (Graphics2D) buff_image.getGraphics();
			g2.drawImage(image, 0, 0, null);// 这个必须有
			// 将BufferedImage对象写入本地文件
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ImageIO.write(buff_image, "png",bos);
			byte[] buff = bos.toByteArray();
			InputStream is = new ByteArrayInputStream(buff);
			return is;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 从剪贴板获取Image对象
	 * @return Image
	 */
	private Image getImageClipboard() {   
	    Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);   
	    try {   
	        if (null  != t && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {   
	        Image image = (Image)t.getTransferData(DataFlavor.imageFlavor);   
	        return image;   
	        }   
	    } catch (UnsupportedFlavorException e) {   
	          System.out.println("Error tip: "+e.getMessage());  
	    } catch (IOException e) {   
	          //System.out.println("Error tip: "+e.getMessage());  
	    }   
	    return null;   
	}
	/**
	 * 设置剪贴板文本内容
	 * @param refContent 文本内容
	 */
	private void setSystemClipboard(String refContent){   
		    String vc = refContent.trim();  
		    StringSelection ss = new StringSelection(vc);  
		      
		    Clipboard sysClb=null;  
		    sysClb = Toolkit.getDefaultToolkit().getSystemClipboard();  
		    sysClb.setContents(ss,null);  
		}  
	private String getUUID(){ 
        String s = UUID.randomUUID().toString(); 
        //去掉“-”符号 
        return s.replaceAll("-", ""); 
    }
}
