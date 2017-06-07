package com.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

public class AntZip {

	/**
	 * 压缩文件到指定文件
	 * 
	 * @param srcFiles
	 *            待压缩文件列表
	 * @param zipFile
	 *            压缩后的文件
	 */
	public static void zipFiles(File[] srcFiles, File zipFile) {
		byte[] buf = new byte[1024];
		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					zipFile));
			for (int i = 0; i < srcFiles.length; i++) {
				FileInputStream in = new FileInputStream(srcFiles[i]);
				out.putNextEntry(new ZipEntry(srcFiles[i].getName()));
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			out.close();
			System.out.println("压缩完成");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压到指定目录
	 * 
	 * @param zipFile
	 * @param destDir
	 */
	@SuppressWarnings("rawtypes")
	public static void unZipFiles(File zipFile, String destDir) {
		try {
			ZipFile zf = new ZipFile(zipFile);
			File fileOut = null;
			for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.isDirectory()) {
					fileOut = new File(destDir, entry.getName());
					if (!fileOut.exists()) {
						fileOut.mkdirs();
						System.out.println(fileOut + "目录创建成功");
					} else {
						System.out.println(fileOut + "目录已存在");
					}
				} else {
					fileOut = new File(destDir, entry.getName());
					if (!fileOut.exists()) {
						(new File(fileOut.getParent())).mkdirs();
					}
					BufferedInputStream bis = new BufferedInputStream(
							zf.getInputStream(entry));
					FileOutputStream out = new FileOutputStream(fileOut);
					BufferedOutputStream bos = new BufferedOutputStream(out);
					int b;
					while ((b = bis.read()) != -1) {
						bos.write(b);
					}
					bis.close();
					bos.close();
					out.close();
					System.out.println(fileOut + "文件解压成功");
				}
			}
			zf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据原始rar路径，解压到指定文件夹下.
	 * @param srcRarPath 		原始rar路径
	 * @param dstDirectoryPath  解压到的文件夹
	 */
	public static void unRarFile(String srcRarPath, String dstDirectoryPath) {
		if (!srcRarPath.toLowerCase().endsWith(".rar")) {
			System.out.println("非rar文件！");
			return;
		}
		File dstDiretory = new File(dstDirectoryPath);
		if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
			dstDiretory.mkdirs();
		}
		Archive a = null;
		try {
			a = new Archive(new File(srcRarPath));
			if (a != null) {
				a.getMainHeader().print(); // 打印文件信息.
				FileHeader fh = a.nextFileHeader();
				while (fh != null) {
					if (fh.isDirectory()) { // 文件夹
						File fol = new File(dstDirectoryPath + File.separator
								+ fh.getFileNameString());
						fol.mkdirs();
					} else { // 文件
						File out = new File(dstDirectoryPath + File.separator
								+ fh.getFileNameString().trim());
						// System.out.println(out.getAbsolutePath());
						try {// 之所以这么写try，是因为万一这里面有了异常，不影响继续解压.
							if (!out.exists()) {
								if (!out.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录.
									out.getParentFile().mkdirs();
								}
								out.createNewFile();
							}
							FileOutputStream os = new FileOutputStream(out);
							a.extractFile(fh, os);
							os.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					fh = a.nextFileHeader();
				}
				a.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		unRarFile("F://test//testRar.rar", "F://test//beifen");
	}
}
