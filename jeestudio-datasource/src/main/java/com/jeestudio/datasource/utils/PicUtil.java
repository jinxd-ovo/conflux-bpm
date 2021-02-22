package com.jeestudio.datasource.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
 
import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: Pic Utils
 * @author: David
 * @Date: 2020-03-17
 */
public class PicUtil {

	protected static final Logger logger = LoggerFactory.getLogger(PicUtil.class);

	public static String commpressPicForScale(String srcPath, String desPath, long desFileSize, double accuracy) {
		if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(srcPath)) {
			return null;
		}
		if (false == new File(srcPath).exists()) {
			return null;
		}
		try {
			File srcFile = new File(srcPath);
			long srcFileSize = srcFile.length();
			Thumbnails.of(srcPath).scale(1f).toFile(desPath);
			commpressPicCycle(desPath, desFileSize, accuracy);
			File desFile = new File(desPath);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("Warn while commpressing pic for scale:" + ExceptionUtils.getStackTrace(e));
			return null;
		}
		return desPath;
	}

	private static void commpressPicCycle(String desPath, long desFileSize, double accuracy) throws IOException {
		File srcFileJPG = new File(desPath);
		long srcFileSizeJPG = srcFileJPG.length();
		if (srcFileSizeJPG <= desFileSize * 1024) {
			return;
		}
		BufferedImage bim = ImageIO.read(srcFileJPG);
		int srcWdith = bim.getWidth();
		int srcHeigth = bim.getHeight();
		int desWidth = new BigDecimal(srcWdith).multiply(new BigDecimal(accuracy)).intValue();
		int desHeight = new BigDecimal(srcHeigth).multiply(new BigDecimal(accuracy)).intValue();

		Thumbnails.of(desPath).size(desWidth, desHeight).outputQuality(accuracy).toFile(desPath);
		commpressPicCycle(desPath, desFileSize, accuracy);
	}

	public static void main(String[] args) {
		PicUtil.commpressPicForScale("C:\\Users\\123\\Desktop\\1.png",
				"C:\\Users\\123\\Desktop\\12.jpg", 500, 0.8);
	}
}
