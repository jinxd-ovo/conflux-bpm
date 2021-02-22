package com.jeestudio.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.MimeUtility;
import java.io.*;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * @Description: File util
 * @author whl
 * @author houxl
 * @Date: 2019-12-03
 */
public class FileUtil extends FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
    /**
     * XOR number
     */
    public static final int numOfEncAndDec = 0x99;
    public static final String TYPE_FILE = "FILE";
    public static final String TYPE_PIC = "PIC";
    public static final String TYPE_VIDEO = "VIDEO";
    public static final String TYPE_AUDIO = "AUDIO";
    public static final String TYPE_OTHER = "OTHER";

    /**
     * Copy a single file, do not overwrite if the target file exists.
     */
    public static boolean copyFile(String srcFileName, String descFileName) {
        return FileUtil.copyFileCover(srcFileName, descFileName, false);
    }

    /**
     * Copy a single file
     */
    public static boolean copyFileCover(String srcFileName, String descFileName, boolean coverable) {
        File srcFile = new File(srcFileName);
        if (false == srcFile.exists()) {
            logger.debug("Failed to copy file, source file " + srcFileName + " does not exist.");
            return false;
        } else if (false == srcFile.isFile()) {
            logger.debug("Failed to copy file, " + srcFileName + " is not a file.");
            return false;
        } else {
            File descFile = new File(descFileName);
            if (descFile.exists()) {
                if (coverable) {
                    if (false == FileUtil.deleteFileOrDirectory(descFileName)) {
                        logger.debug("Failed to delete the target file " + descFileName + ".");
                        return false;
                    }
                } else {
                    logger.debug("Failed to copy file, target file " + descFileName + " already exists.");
                    return false;
                }
            } else {
                if (false == descFile.getParentFile().exists()) {
                    if (false == descFile.getParentFile().mkdirs()) {
                        logger.debug("Failed to create the directory where the target file is located.");
                        return false;
                    }
                }
            }

            int readByte = 0;
            InputStream ins = null;
            OutputStream outs = null;
            try {
                ins = new FileInputStream(srcFile);
                outs = new FileOutputStream(descFile);
                byte[] buf = new byte[1024];
                while ((readByte = ins.read(buf)) != -1) {
                    outs.write(buf, 0, readByte);
                }
                logger.debug("Copying single file " + srcFileName + " to " + descFileName + " successfully.");
                return true;
            } catch (Exception e) {
                logger.error("Failed to copy file, " + ExceptionUtils.getStackTrace(e));
                return false;
            } finally {
                if (outs != null) {
                    try {
                        outs.close();
                    } catch (IOException oute) {
                        logger.error("Failed to copy file, " + ExceptionUtils.getStackTrace(oute));
                    }
                }
                if (ins != null) {
                    try {
                        ins.close();
                    } catch (IOException ine) {
                        logger.error("Failed to copy file, " + ExceptionUtils.getStackTrace(ine));
                    }
                }
            }
        }
    }

    /**
     * Copy the contents of the entire directory without overwriting if the target directory exists
     */
    public static boolean copyDirectory(String srcDirName, String descDirName) {
        return FileUtil.copyDirectoryCover(srcDirName, descDirName, false);
    }

    /**
     * Copy the contents of the entire catalog
     */
    public static boolean copyDirectoryCover(String srcDirName, String descDirName, boolean coverable) {
        File srcDir = new File(srcDirName);
        if (false == srcDir.exists()) {
            logger.debug("Failed to copy directory, source directory " + srcDirName + " does not exist.");
            return false;
        } else if (false == srcDir.isDirectory()) {
            logger.debug("Failed to copy directory, " + srcDirName + " is not a directory.");
            return false;
        } else {
            //Automatically add a file separator if the destination folder name does not end with a file separator
            String descDirNames = descDirName;
            if (false == descDirNames.endsWith(File.separator)) {
                descDirNames = descDirNames + File.separator;
            }
            File descDir = new File(descDirNames);
            if (descDir.exists()) {
                if (coverable) {
                    if (false == FileUtil.deleteFileOrDirectory(descDirNames)) {
                        logger.debug("Failed to delete directory " + descDirNames + ".");
                        return false;
                    }
                } else {
                    logger.debug("Directory copy failed, target directory " + descDirNames + " already exists.");
                    return false;
                }
            } else {
                if (false == descDir.mkdirs()) {
                    logger.debug("Failed to create target directory.");
                    return false;
                }
            }

            boolean flag = true;
            File[] files = srcDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                //It's a file
                if (files[i].isFile()) {
                    flag = FileUtil.copyFile(files[i].getAbsolutePath(), descDirName + files[i].getName());
                    if (false == flag) {
                        break;
                    }
                }
                //It's a directory
                if (files[i].isDirectory()) {
                    flag = FileUtil.copyDirectory(files[i].getAbsolutePath(), descDirName + files[i].getName());
                    if (false == flag) {
                        break;
                    }
                }
            }

            if (false == flag) {
                logger.debug("Failed to copy directory " + srcDirName + " to " + descDirName + ".");
                return false;
            }
            logger.debug("Copying directory " + srcDirName + " to " + descDirName + " successfully.");
            return true;
        }
    }

    /**
     * Delete file or directory
     */
    public static boolean deleteFileOrDirectory(String name) {
        File file = new File(name);
        if (false == file.exists()) {
            logger.debug(name + " does not exist.");
            return true;
        } else {
            if (file.isFile()) {
                return FileUtil.deleteFile(name);
            } else {
                return FileUtil.deleteDirectory(name);
            }
        }
    }

    /**
     * Delete single file
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                logger.debug("Delete file " + fileName + " successfully.");
                return true;
            } else {
                logger.debug("Delete file " + fileName + " failed.");
                return false;
            }
        } else {
            logger.debug(fileName + " does not exist.");
            return true;
        }
    }

    /**
     * Delete directory and files under directory
     */
    public static boolean deleteDirectory(String dirName) {
        String dirNames = dirName;
        if (false == dirNames.endsWith(File.separator)) {
            dirNames = dirNames + File.separator;
        }
        File dirFile = new File(dirNames);
        if (false == dirFile.exists() || false == dirFile.isDirectory()) {
            logger.warn(dirNames + " does not exist.");
            return true;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = FileUtil.deleteFile(files[i].getAbsolutePath());
                if (false == flag) {
                    break;
                }
            } else if (files[i].isDirectory()) {
                flag = FileUtil.deleteDirectory(files[i].getAbsolutePath());
                if (false == flag) {
                    break;
                }
            }
        }

        if (false == flag) {
            logger.warn("Failed to delete directory.");
            return false;
        }
        if (dirFile.delete()) {
            logger.debug("Delete directory " + dirName + " successfully.");
            return true;
        } else {
            logger.warn("Failed to delete directory " + dirName + ".");
            return false;
        }
    }

    /**
     * Create a single file
     */
    public static boolean createFile(String descFileName) {
        File file = new File(descFileName);
        if (file.exists()) {
            logger.warn("File " + descFileName + " already exists.");
            return false;
        }
        if (descFileName.endsWith(File.separator)) {
            logger.warn(descFileName + " is directory, not a file.");
            return false;
        }
        if (false == file.getParentFile().exists()) {
            if (false == file.getParentFile().mkdirs()) {
                logger.warn("Failed to create the directory where the file is located.");
                return false;
            }
        }

        try {
            if (file.createNewFile()) {
                logger.debug(descFileName + " created successfully.");
                return true;
            } else {
                logger.warn(descFileName + " created failed.");
                return false;
            }
        } catch (Exception e) {
            logger.error(descFileName + " created failed, " + ExceptionUtils.getStackTrace(e));
            return false;
        }

    }

    /**
     * Create a directory
     */
    public static boolean createDirectory(String descDirName) {
        String descDirNames = descDirName;
        if (!descDirNames.endsWith(File.separator)) {
            descDirNames = descDirNames + File.separator;
        }
        File descDir = new File(descDirNames);
        if (descDir.exists()) {
            logger.warn("Directory " + descDirNames + " already exists.");
            return false;
        }
        if (descDir.mkdirs()) {
            logger.debug("Directory " + descDirNames + " created successfully.");
            return true;
        } else {
            logger.warn("Directory " + descDirNames + " create failed.");
            return false;
        }
    }

    /**
     * Write content to file in utf-8
     */
    public static void writeToFile(String fileName, String content, boolean append) throws IOException {
        try {
            FileUtils.write(new File(fileName), content, "utf-8", append);
            logger.debug("File " + fileName + " written successfully.");
        } catch (IOException e) {
            logger.error("File " + fileName + " written failed, " + ExceptionUtils.getStackTrace(e));
            throw(e);
        }
    }

    /**
     * Write content to file in encoding
     */
    public static void writeToFile(String fileName, String content, String encoding, boolean append) throws IOException {
        try {
            FileUtils.write(new File(fileName), content, encoding, append);
            logger.debug("File " + fileName + " written successfully.");
        } catch (IOException e) {
            logger.error("File " + fileName + " written failed, " + ExceptionUtils.getStackTrace(e));
            throw(e);
        }
    }

    /**
     * Compressed files or directories
     * @param srcDirName compressed root
     * @param fileName the name of the file or folder to be compressed in the root directory, where * or "" means all the files in the directory.
     * @param descFileName target zip file
     */
    public static void zipFiles(String srcDirName, String fileName, String descFileName) throws IOException {
        if (srcDirName == null) {
            logger.warn("Compression failed, directory " + srcDirName + " does not exist.");
        } else {
            File fileDir = new File(srcDirName);
            if (false == fileDir.exists() || false == fileDir.isDirectory()) {
                logger.warn("Compression failed, directory " + srcDirName + " does not exist.");
            } else {
                String dirPath = fileDir.getAbsolutePath();
                File descFile = new File(descFileName);
                ZipOutputStream zouts = null;
                try {
                    zouts = new ZipOutputStream(new FileOutputStream(descFile));
                    if ("*".equals(fileName) || "".equals(fileName)) {
                        FileUtil.zipDirectoryToZipFile(dirPath, fileDir, zouts);
                    } else {
                        File file = new File(fileDir, fileName);
                        if (file.isFile()) {
                            FileUtil.zipFilesToZipFile(dirPath, file, zouts);
                        } else {
                            FileUtil.zipDirectoryToZipFile(dirPath, file, zouts);
                        }
                    }
                    logger.debug(descFileName + " compressed successfully.");
                } catch (Exception e) {
                    logger.error("File compression failed, " + ExceptionUtils.getStackTrace(e));
                    throw(e);
                } finally {
                    if (zouts != null) {
                        try {
                            zouts.close();
                        } catch (Exception err) {
                            logger.error(ExceptionUtils.getStackTrace(err));
                        }
                    }
                }
            }
        }
    }

    /**
     * Decompress file
     */
    public static boolean unZipFiles(String zipFileName, String descFileName) {
        String descFileNames = descFileName;
        if (false == descFileNames.endsWith(File.separator)) {
            descFileNames = descFileNames + File.separator;
        }
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFileName);
            ZipEntry entry = null;
            String entryName = null;
            String descFileDir = null;
            byte[] buf = new byte[4096];
            int readByte = 0;
            Enumeration enums = zipFile.getEntries();
            while (enums.hasMoreElements()) {
                entry = (ZipEntry) enums.nextElement();
                entryName = entry.getName();
                descFileDir = descFileNames + entryName;
                if (entry.isDirectory()) {
                    new File(descFileDir).mkdirs();
                    continue;
                } else {
                    new File(descFileDir).getParentFile().mkdirs();
                }
                File file = new File(descFileDir);
                OutputStream os = null;
                InputStream is = null;
                try {
                    os = new FileOutputStream(file);
                    is = zipFile.getInputStream(entry);
                    while ((readByte = is.read(buf)) != -1) {
                        os.write(buf, 0, readByte);
                    }

                } catch (Exception err) {
                    throw(err);
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception osErr) {
                            logger.error(ExceptionUtils.getStackTrace(osErr));
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception isErr) {
                            logger.error(ExceptionUtils.getStackTrace(isErr));
                        }
                    }
                }

            }
            logger.debug("File decompressed successfully.");
            return true;
        } catch (Exception e) {
            logger.debug("File decompression failed, " + ExceptionUtils.getStackTrace(e));
            return false;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception err) {
                    logger.error(ExceptionUtils.getStackTrace(err));
                }
            }
        }
    }

    /**
     * Compress directory to zip output stream
     */
    public static void zipDirectoryToZipFile(String dirPath, File fileDir, ZipOutputStream zouts) throws IOException {
        if (fileDir.isDirectory()) {
            File[] files = fileDir.listFiles();
            if (files.length == 0) {
                ZipEntry entry = new ZipEntry(getEntryName(dirPath, fileDir));
                try {
                    zouts.putNextEntry(entry);
                    zouts.closeEntry();
                } catch (Exception e) {
                    logger.error("Directory compressed failed, " + ExceptionUtils.getStackTrace(e));
                    throw(e);
                }
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    FileUtil.zipFilesToZipFile(dirPath, files[i], zouts);
                } else {
                    FileUtil.zipDirectoryToZipFile(dirPath, files[i], zouts);
                }
            }
        }
    }

    /**
     * Compress files to zip output stream
     */
    public static void zipFilesToZipFile(String dirPath, File file, ZipOutputStream zouts) throws IOException {
        FileInputStream fin = null;
        ZipEntry entry = null;
        byte[] buf = new byte[4096];
        int readByte = 0;
        if (file.isFile()) {
            try {
                fin = new FileInputStream(file);
                entry = new ZipEntry(getEntryName(dirPath, file));
                zouts.putNextEntry(entry);
                while ((readByte = fin.read(buf)) != -1) {
                    zouts.write(buf, 0, readByte);
                }
            } catch (Exception e) {
                logger.error("Directory compressed failed, " + ExceptionUtils.getStackTrace(e));
                throw(e);
            } finally {
                if (zouts != null) {
                    try {
                        zouts.closeEntry();
                    } catch (Exception zoutsErr) {
                        logger.error(ExceptionUtils.getStackTrace(zoutsErr));
                    }
                }
                if (fin != null) {
                    try {
                        fin.close();
                    } catch (Exception finErr) {
                        logger.error(ExceptionUtils.getStackTrace(finErr));
                    }
                }
            }
        }

    }

    /**
     * Get the name of the entry of the file to be compressed in the zip file
     */
    private static String getEntryName(String dirPath, File file) {
        String dirPaths = dirPath;
        if (false == dirPaths.endsWith(File.separator)) {
            dirPaths = dirPaths + File.separator;
        }
        String filePath = file.getAbsolutePath();
        if (file.isDirectory()) {
            filePath += "/";
        }
        int index = filePath.indexOf(dirPaths);
        return filePath.substring(index + dirPaths.length());
    }

    /**
     * Get file prefix
     */
    public static String getFilePrefix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, splitIndex);
    }

    /**
     * Copy a single file in XOR, do not overwrite if the target file exists.
     */
    public static boolean copyFileXOR(String srcFileName, String descFileName) {
        FileInputStream filein = null;
        FileOutputStream fileou = null;
        try {
            filein= new FileInputStream(srcFileName);
            fileou= new FileOutputStream(descFileName);
            int n = 0;
            while((n = filein.read()) !=-1){
                fileou.write(n^numOfEncAndDec);
            }
            logger.debug("File copied successfully.");
            return true;
        } catch (IOException e) {
            logger.error("File copied failed, " + ExceptionUtils.getStackTrace(e));
            return false;
        } finally {
            if(fileou != null) {
                try {
                    fileou.close();
                } catch (Exception fileouErr) {
                    logger.error(ExceptionUtils.getStackTrace(fileouErr));
                }
            }
            if(filein != null) {
                try {
                    filein.close();
                } catch (Exception fileinErr) {
                    logger.error(ExceptionUtils.getStackTrace(fileinErr));
                }
            }
        }
    }

    /**
     * Get file name that is not garbled when downloading
     */
    public static String getFileName(String userAgent, String fileName) {
        userAgent = (userAgent == null ? "" : userAgent.toLowerCase());
        String rtn = new String();
        try {
            /*
             * Because the browser will make special treatment for the space in the file name. as:
             * (1) Firefox will directly discard the part after the space, adding "" to the filename can solve the problem.
             * (2) in ie, when the original file name contains spaces, it will be changed to underline by default, i.e., "UU"; if we use urlencode() to code the file name when we output the file, the spaces will become plus sign, i.e., "+", just change "+" to spaces after coding.
             * (3) in opera, the file name can be parsed correctly without urlencode(), but notice that after urlencode(), the space becomes a plus sign just like ie.
             */
            String new_filename = URLEncoder.encode(fileName, "UTF8");
            new_filename = new_filename.replaceAll("\\+", " ");
            if (userAgent != null) {
                userAgent = userAgent.toLowerCase();
                if (userAgent.indexOf("msie") != -1) {
                    rtn = new_filename;
                }
                else if (userAgent.indexOf("opera") != -1) {
                    rtn = "filename*=UTF-8''" + new_filename;
                }
                else if (userAgent.indexOf("safari") != -1) {
                    rtn = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
                }
                else if (userAgent.indexOf("applewebkit") != -1) {
                    new_filename = MimeUtility.encodeText(fileName, "UTF8", "B");
                    rtn = new_filename;
                }
                else if (userAgent.indexOf("firefox") != -1) {
                    rtn = "\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
                } else {
                    rtn = new_filename;
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("Error while getting file name, " + ExceptionUtils.getStackTrace(e));
        }
        return rtn;
    }

    /**
     * Get file type
     */
    public static String getFileType(String fileName) {
        final String extension = FilenameUtils.getExtension(fileName);
        String fileType = TYPE_OTHER;
        switch (extension.toLowerCase()) {
            case "bmp":
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "tif":
            case "tiff":
                fileType = TYPE_PIC;
                break;
            case "mp3":
            case "amr":
                fileType = TYPE_AUDIO;
                break;
            case "mp4":
            case "wav":
            case "wma":
            case "3gp":
                fileType = TYPE_VIDEO;
                break;
            case "doc":
            case "docx":
            case "xls":
            case "xlsx":
            case "txt":
            case "ppt":
            case "pptx":
            case "pdf":
                fileType = TYPE_FILE;
                break;
            default:
                fileType = TYPE_OTHER;
                break;
        }
        return fileType;
    }
}
