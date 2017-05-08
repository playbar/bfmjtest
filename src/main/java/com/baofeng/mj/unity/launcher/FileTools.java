package com.baofeng.mj.unity.launcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.StatFs;

import com.baofeng.mj.util.publicutil.ImageUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;

@SuppressLint("NewApi")
public class FileTools {
    public static final String[] fileNameExtensions = {".asf", ".wm", ".wmp", ".wmv",
            ".ram", ".rm", ".rmvb", ".rpm", ".scm", ".rp", ".evo", ".vob",
            ".mov", ".qt", ".3g2", ".3gp", ".3gp2", ".3gpp", ".BHD", ".GHD",
            ".amv", ".avi", ".bik", ".csf", ".d2v", ".dsm", ".ivf", ".m1v",
            ".m2p", ".m2ts", ".m2v", ".m4b", ".m4p", ".m4v", ".mkv", ".mp4",
            ".mpe", ".mpeg", ".mpg", ".mts", ".ogm", ".pmp", ".pmp2", ".pss",
            ".pva", ".ratDVD", ".smk", ".tp", ".tpr", ".ts", ".vg2", ".vid",
            ".vp6", ".vp7", ".wv", ".asm", ".avsts", ".divx", ".webm", ".swf",
            ".flv", ".flic", ".fli", ".flc", ".mod", ".vp5", ".asx", ".sub",
            ".bhd"};

    /**
     * 判断后缀
     *
     * @param file
     * @param fileNameExtensions
     * @return
     */
    public static boolean isFileEnd(File file, String... fileNameExtensions) {

        final String name = file.getName();
        for (final String nameString : fileNameExtensions) {
            if (name.endsWith(nameString))
                return true;

        }
        return false;
    }


    public static boolean hasSd() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    /**
     * @param filePath 路径
     * @return
     * @author qiguolong @Date 2015-2-11 下午3:33:35
     * @description:{创建视频缩略图
     */
    public static Bitmap createVideoThumbnail(String filePath, int width,
                                              int height) {
        Bitmap bitmap = createVideoThumbnail(filePath);
        if (bitmap != null) {
            bitmap = ImageUtil.zoomBitmap(bitmap, width, height);
        }
        return bitmap;
    }

    /**
     * @param filePath 路径
     * @return
     * @author qiguolong @Date 2015-8-11 下午3:33:35
     * @description:{创建视频缩略图 原始分辨率
     */
    public static Bitmap createVideoThumbnail(final String filePath) {
        Bitmap bitmap = null;
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {// MODE_CAPTURE_FRAME_ONLY
            retriever.setDataSource(filePath);
            final String timeString = retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final long time = Long.parseLong(timeString) * 1000;
            //
            //            String mime = retriever.extractMetadata(MediaMetadataRetriever
            // .METADATA_KEY_MIMETYPE);
            final String bitrate = retriever.extractMetadata(MediaMetadataRetriever
                    .METADATA_KEY_BITRATE);

            bitmap = retriever.getFrameAtTime(time * 31 / 160); // 按视频长度比例选择帧

            //type 1全景 0非全景
            final int width = bitmap.getWidth();
            final int height = bitmap.getHeight();


        }
        catch (final IllegalArgumentException ex) {
            // Assume this is a corrupt video file

        }
        catch (final RuntimeException ex) {
            // Assume this is a corrupt video file.
        }
        finally {
            try {
                retriever.release();
            }
            catch (final Exception ex) {
                // Ignore failures while cleaning up.
                ex.printStackTrace();
            }
        }

        return bitmap;
    }



    /**
     * @param o
     * @param path
     * @author qiguolong @Date 2015-7-6 下午3:36:26
     * @description:{序列化写文件
     */
    public static void writeSerFile(Serializable o, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(path));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.close();
            fos.close();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 序列化写文件
     *
     * @param jsonStr
     * @param path
     */
    public static void writeStrFile(String jsonStr, String path) {
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeBytes(jsonStr);
            oos.flush();
            oos.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 序列化写文件
     *
     * @param jsonStr
     * @param file
     */
    public static void writeStrFile(String jsonStr, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeBytes(jsonStr);
            oos.flush();
            oos.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件
     *
     * @param path
     * @author qiguolong @Date 2015-7-6 下午3:36:26
     * @description:{序列化读文件
     */
    public static String readStrFile(String path) {
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            ObjectInputStream ois = new ObjectInputStream(fis);
            String jsonStr = (String) ois.readObject();
            ois.close();
            fis.close();
            return jsonStr;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readStrFileUTF(String path) {
        try {
            StringBuilder sb = new StringBuilder();
            FileInputStream inputStream = new FileInputStream(new File(path));
            InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeStrFile1(String jsonStr, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(path));
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(jsonStr);
            osw.flush();
            osw.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件
     *
     * @param path
     * @author qiguolong @Date 2015-7-6 下午3:36:26
     * @description:{序列化读文件
     */
    public static Serializable readSerFile(String path) {
        try {
            final FileInputStream fis = new FileInputStream(new File(path));
            final ObjectInputStream ois = new ObjectInputStream(fis);
            final Serializable o = (Serializable) ois.readObject();
            ois.close();
            fis.close();
            return o;
        }
        catch (final Exception e) {
        }
        return null;
    }

    public static String convertFileSize(long size) {
        final long kb = 1024;
        final long mb = kb * 1024;
        final long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            final float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            final float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static String getFileNameFromPath(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1,
                path.length());
    }


    /**
     * @param path
     * @return
     * @author qiguolong @Date 2015-4-30 上午10:22:44
     * @description:{设备总容量
     */
    public static long getDiskSize(String path) {
        // 总的容量
        long totalSize = 0;
        try {
            final StatFs stat = new StatFs(path);

            try {
                final long blockSize = stat.getBlockSizeLong();
                // 文件系统的总的块数
                final long totalBlocks = stat.getBlockCountLong();
                totalSize = blockSize * totalBlocks;
            }
            catch (final NoSuchMethodError e) {
                // TODO: handle exception
                final long blockSize = stat.getBlockSize();
                // 文件系统的总的块数
                final long totalBlocks = stat.getBlockCount();
                totalSize = (blockSize * totalBlocks);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return totalSize;

    }

    /**
     * @param path
     * @return
     * @author qiguolong @Date 2015-4-30 上午10:22:29
     * @description:{获取剩余容量
     */
    public static long getDiskAvaliableSize(String path) {
        final StatFs stat = new StatFs(path);
        long availableSize = 0;
        try {
            // 文件系统的块的大小（byte）
            final long blockSize = stat.getBlockSizeLong();
            // // 文件系统的总的块数
            // long totalBlocks = stat.getBlockCountLong();
            // 文件系统上空闲的可用于程序的存储块数
            final long availableBlocks = stat.getAvailableBlocksLong();
            // 总的容量
            availableSize = blockSize * availableBlocks;
        }
        catch (final NoSuchMethodError e) {
            // TODO: handle exception
            final long blockSize = stat.getBlockSize();
            // // 文件系统的总的块数
            // long totalBlocks = stat.getBlockCountLong();
            // 文件系统上空闲的可用于程序的存储块数
            final long availableBlocks = stat.getAvailableBlocks();
            // 总的容量
            availableSize = blockSize * availableBlocks;
        }

        return availableSize;

    }


    public static long getFileSize(File f) {
        long size = 0;
        final File flist[] = f.listFiles();
        if (flist == null)
            return 0;
        for (final File file : flist) {
            if (file.isDirectory()) {
                size += getFileSize(file);
            } else {
                size = size + file.length();
            }
        }
        return size;
    }


    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        final FileInputStream input = new FileInputStream(sourceFile);
        final BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        final FileOutputStream output = new FileOutputStream(targetFile);
        final BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        final byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        // 关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }


    /**
     * @param oldPath
     * @param newPath
     * @return true 为成功
     * @author qiguolong @Date 2015-5-4 上午10:56:02
     * @description:{使用shell命令 复制}
     */
    public static boolean todoCopyShell(String oldPath, String newPath) {

        try {
            final String cmd = "cp -r " + oldPath + "/. " + newPath;
            final Runtime runtime = Runtime.getRuntime();
            Process proc = null;
            proc = runtime.exec(cmd);
            final int t = proc.waitFor();
            if (t != 0) {
                printShellError(proc);
            }

            proc.destroy();
            return t == 0;
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @return
     * @author qiguolong @Date 2015-5-7 下午6:16:17
     * @description:{去sd卡的另外一种写法
     */
    public static ArrayList<String> getMountStorage() {
        final ArrayList<String> exStorageMountPath = new ArrayList<String>();
        Process proc = null;
        try {
            final Runtime runtime = Runtime.getRuntime();
            proc = runtime.exec("mount");
            final InputStream is = proc.getInputStream();
            final InputStreamReader isr = new InputStreamReader(is);
            final BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.startsWith("/dev/block/vold/")) {
                    final String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        exStorageMountPath.add(columns[1]);
                    }
                }
            }
            isr.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (proc != null) {
                proc.destroy();
            }
        }

        final String sd = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        for (String str : exStorageMountPath) {
            if (sd.equals(str)) {
                exStorageMountPath.remove(str);
                break;
            }
        }
        return exStorageMountPath;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            final String[] children = dir.list();
            // 递归删除目录中的子目录下
            if (children == null) {
                return dir.delete();
            }
            for (int i = 0; i < children.length; i++) {
                final boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除

        return dir.delete();
    }

    /**
     * @param proc
     * @author qiguolong @Date 2015-5-8 下午12:06:19
     * @description:{打印失败原因
     */
    public static void printShellError(Process proc) {
        final InputStream in = proc.getErrorStream();
        StringBuilder result = new StringBuilder();
        final byte[] re = new byte[1024];
        try {
            while (in.read(re) != -1) {
                result = result.append(new String(re));
            }
            System.out.println("失败原因:" + result);
        }
        catch (final IOException e) {
        }
    }

    private static String getExternalCacheDir(Context context) {
        StringBuilder sb = new StringBuilder();

        sb.append(Environment.getExternalStorageDirectory().getPath())
                .append("/Android/data/").append(context.getPackageName())
                .append("/cache").toString();
        return sb.toString();
    }


    private static String getSDPathFromCache(String cashpath) {
        return cashpath.substring(0, cashpath.indexOf("Android"));
    }

    /**
     * 创建文件夹
     * @param path
     * @return
     */
    public static boolean creatDir(String path) {
        File file = new File(path);
        if (!file.isDirectory()) {
            return file.mkdirs();
        }
        return  true;

    }

}
