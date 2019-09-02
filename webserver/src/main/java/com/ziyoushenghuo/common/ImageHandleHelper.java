
package com.ziyoushenghuo.common;




import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;

import static javassist.bytecode.CodeAttribute.tag;

/* 图片处理函数，不再后端生成分享图，保存代码
  @author 王江波
  @version V1.0
*/

@Deprecated
public class ImageHandleHelper {

    /**
     * @param srcFile源图片、targetFile截好后图片全名、startAcross 开始截取位置横坐标、StartEndlong开始截图位置纵坐标、width截取的长，hight截取的高
     * @Description:截图
     * @author:liuyc
     * @time:2016年5月27日 上午10:18:23
     */
    public static void cutImage(String srcFile, String targetFile, int startAcross, int StartEndlong, int width,
                                int hight) throws Exception {
        // 取得图片读入器
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader reader = readers.next();
        // 取得图片读入流
        InputStream source = new FileInputStream(srcFile);
        ImageInputStream iis = ImageIO.createImageInputStream(source);
        reader.setInput(iis, true);
        // 图片参数对象
        ImageReadParam param = reader.getDefaultReadParam();
        Rectangle rect = new Rectangle(startAcross, StartEndlong, width, hight);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        ImageIO.write(bi, targetFile.split("\\.")[1], new File(targetFile));
    }


    private static BufferedImage roundImage(BufferedImage image, int targetSize, int cornerRadius) {
        BufferedImage outputImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, targetSize, targetSize, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return outputImage;
    }


    /**
     * @param files 要拼接的文件列表
     * @param type1 横向拼接， 2 纵向拼接
     * @Description:图片拼接 （注意：必须两张图片长宽一致哦）
     * @author:liuyc
     * @time:2016年5月27日 下午5:52:24
     */
    public static void mergeImage(String[] files, int type, String targetFile) {
        int len = files.length;
        if (len < 1) {
            throw new RuntimeException("图片数量小于1");
        }
        File[] src = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
                src[i] = new File(files[i]);
                images[i] = ImageIO.read(src[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            ImageArrays[i] = new int[width * height];
            ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);
        }
        int newHeight = 0;
        int newWidth = 0;
        for (int i = 0; i < images.length; i++) {
            // 横向
            if (type == 1) {
                newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();
                newWidth += images[i].getWidth();
            } else if (type == 2) {// 纵向
                newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();
                newHeight += images[i].getHeight();
            }
        }
        if (type == 1 && newWidth < 1) {
            return;
        }
        if (type == 2 && newHeight < 1) {
            return;
        }

        // 生成新图片
        try {
            BufferedImage ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            int width_i = 0;
            for (int i = 0; i < images.length; i++) {
                if (type == 1) {
                    ImageNew.setRGB(width_i, 0, images[i].getWidth(), newHeight, ImageArrays[i], 0,
                            images[i].getWidth());
                    width_i += images[i].getWidth();
                } else if (type == 2) {
                    ImageNew.setRGB(0, height_i, newWidth, images[i].getHeight(), ImageArrays[i], 0, newWidth);
                    height_i += images[i].getHeight();
                }
            }
            //输出想要的图片
            ImageIO.write(ImageNew, targetFile.split("\\.")[1], new File(targetFile));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:小图片贴到大图片形成一张图(合成)
     * @author:liuyc
     * @time:2016年5月27日 下午5:51:20
     */
    public static final void overlapImage(String bigPath, String smallPath, String outFile) {
        try {
            BufferedImage big = ImageIO.read(new File(bigPath));
            BufferedImage small = ImageIO.read(new File(smallPath));
            Graphics2D g = big.createGraphics();
            int x = (big.getWidth() - small.getWidth()) / 2;
            int y = (big.getHeight() - small.getHeight()) / 2;
            g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
            g.dispose();
            ImageIO.write(big, outFile.split("\\.")[1], new File(outFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage resize(BufferedImage big,
                                       int newWidth, boolean circular) throws IOException {


        BufferedImage bfImage = new BufferedImage(newWidth, newWidth,
                BufferedImage.TYPE_INT_RGB);


        int iWidth = big.getWidth(null);
        int iHeight = big.getHeight(null);

        Image resizedImage = null;

        if (iWidth > iHeight) {
            resizedImage = big.getScaledInstance(newWidth, (newWidth * iHeight)
                    / iWidth, Image.SCALE_SMOOTH);
        } else {
            resizedImage = big.getScaledInstance((newWidth * iWidth) / iHeight,
                    newWidth, Image.SCALE_SMOOTH);
        }


            Graphics g = bfImage.createGraphics();


            g.setColor(Color.white);
            g.fillRect(0, 0, resizedImage .getWidth(null), resizedImage .getHeight(null));
            g.drawImage(resizedImage , 0, 0, null);
            g.dispose();

            // Soften.
            float softenFactor = 0.05f;
            float[] softenArray = {0, softenFactor, 0, softenFactor,
                    1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0};
            Kernel kernel = new Kernel(3, 3, softenArray);
            ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            bfImage = cOp.filter(bfImage, null);

            if (circular) {
                bfImage = roundImage(bfImage, newWidth, newWidth);
            }

            return bfImage;
        }


    public static BufferedImage resize(InputStream stream,
                                       int newWidth, boolean circular) throws IOException {


        BufferedImage big = ImageIO.read(stream);

        return resize(big, newWidth, circular);
    }


    public static BufferedImage resize(File originalFile,
                                       int newWidth, boolean circular) throws IOException {


        BufferedImage img = ImageIO.read(originalFile);

        return resize(img, newWidth, circular);

    }


    public static final void overlapImage(String bigPath, String smallPath, String outFile, int startx, int starty) {
        try {
            BufferedImage big = ImageIO.read(new File(bigPath));
            BufferedImage small = ImageIO.read(new File(smallPath));
            Graphics2D g = big.createGraphics();
            int x = startx;
            int y = starty;
            g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
            g.dispose();
            ImageIO.write(big, outFile.split("\\.")[1], new File(outFile));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage overlapImage(String bigPath, BufferedImage small, int startx, int starty) {
        try {
            BufferedImage big = ImageIO.read(new File(bigPath));

            Graphics2D g = big.createGraphics();
            int x = startx;
            int y = starty;
            g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
            g.dispose();
            return big;

        } catch (Exception e) {

        }

        return null;
    }

    public static BufferedImage overlapImage(BufferedImage big, BufferedImage small, int startx, int starty) {
        try {


            Graphics2D g = big.createGraphics();
            int x = startx;
            int y = starty;
            g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
            g.dispose();
            return big;

        } catch (Exception e) {

        }

        return null;
    }

    public static void drawStringForImage(String filePath, String content, Color contentColor, float qualNum, String targetFile, int x, int y) {
        ImageIcon imgIcon = new ImageIcon(filePath);
        Image theImg = imgIcon.getImage();
        int width = theImg.getWidth(null) == -1 ? 200 : theImg.getWidth(null);
        int height = theImg.getHeight(null) == -1 ? 200 : theImg.getHeight(null);
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bimage.createGraphics();
        g.setColor(contentColor);
        g.setBackground(Color.red);
        g.drawImage(theImg, 0, 0, null);

        g.setFont(new Font("仿宋", Font.PLAIN, 16));

        g.drawString(content, x, y);
        g.dispose();

        try {

            ImageIO.write(bimage, "png", new File(targetFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

        }
    }
}


