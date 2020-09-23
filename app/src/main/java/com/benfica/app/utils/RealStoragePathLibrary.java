package com.benfica.app.utils;

/**
 * Created by AblySoft Pvt Ltd. on 23/9/20.
 */

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class RealStoragePathLibrary {
    public static final int MAX_INTERAL_COUNT = 3;
    private static final String TAG = RealStoragePathLibrary.class.getSimpleName();
    @Nullable
    public static String APP_SPECIFIC_DIRECTORY_SDCARD = null;
    private static RealStoragePathLibrary mInstance;
    @Nullable
    String realInternalStoragePath = null;
    @Nullable
    String realRemovableStoragePath = null;
    @Nullable
    String realInternalStorageAppSpecificDirectoryPath = null;
    @Nullable
    String realRemovableStorageAppSpecificDirectoryPath = null;
    @Nullable
    private Context mContext = null;
    @NonNull
    private ArrayList<VolumeInfo> mFixedVolumeInfoArrayList = new ArrayList<VolumeInfo>();
    @NonNull
    private ArrayList<VolumeInfo> mRemovableVolumeInfoArrayList = new ArrayList<VolumeInfo>();
    @Nullable
    private String mUserStorage = null;
    private boolean DEBUG = false;

    public RealStoragePathLibrary(Context context) {
        mContext = context;
        if (Build.VERSION.SDK_INT >= 19) {
            initializePathsForKitKatAndAbove();
        } else {
            initialize();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializePathsForKitKatAndAbove() {
        String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (Environment.isExternalStorageRemovable()) {
            realRemovableStoragePath = externalPath;
        } else {
            realInternalStoragePath = externalPath;
        }

        File[] files = mContext.getExternalFilesDirs(null);
        String path1 = null;

        for (File f : files) {
            if (f != null) {
                String path = f.getAbsolutePath();
                if (path.contains("" + realInternalStoragePath)) {
                    realInternalStorageAppSpecificDirectoryPath = path;
                } else {
                    realRemovableStorageAppSpecificDirectoryPath = path;
                    int lastIndex = path.indexOf("/Android");
                    if (lastIndex > 0) {
                        realRemovableStoragePath = path.substring(0, lastIndex);
                    }
                }
            }
        }
    }

    private void initialize() {
        mFixedVolumeInfoArrayList.clear();
        mRemovableVolumeInfoArrayList.clear();

        File filePath = Environment.getDataDirectory();
        VolumeInfo info = new VolumeInfo(filePath.getName(),
                filePath.getAbsolutePath(),
//					Integer.toHexString(FileUtils.getFatVolumeId(filePath.getAbsolutePath())));
                Integer.toHexString(0));

        mFixedVolumeInfoArrayList.add(info);

//		if (android.os.Build.MODEL.equalsIgnoreCase("SHW-M380W")  // Galaxy Tab 2
//			|| android.os.Build.MODEL.equalsIgnoreCase("Xoom"))  // XOOM

        if (Build.VERSION.SDK_INT >= 11 && !Environment.isExternalStorageRemovable())  // over HoneyComb
        {
            filePath = Environment.getExternalStorageDirectory();
            info = new VolumeInfo(filePath.getName(),
                    filePath.getAbsolutePath(),
                    //	Integer.toHexString(FileUtils.getFatVolumeId(filePath.getAbsolutePath())));
                    Integer.toHexString(0));

            mFixedVolumeInfoArrayList.add(info);
        }

        MountInfoManager mountInfoManager = new MountInfoManager();
        for (int i = 0; i < mountInfoManager.size(); i++) {
            addVolumeInfo(
                    mountInfoManager.getPartAt(i),
                    mountInfoManager.getLabelAt(i),
                    mountInfoManager.getMountPointAt(i));
        }

        if (mRemovableVolumeInfoArrayList.size() > 1) {
            if (mFixedVolumeInfoArrayList.size() < 2) {
                // treat LG phone bug
                for (int i = 0; i < mRemovableVolumeInfoArrayList.size(); i++) {
                    VolumeInfo item = mRemovableVolumeInfoArrayList.get(i);
                    if (item.getPath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                        mFixedVolumeInfoArrayList.add(item);
                        mRemovableVolumeInfoArrayList.remove(i);
                        break;
                    }
                }
            } else {  // for ICS

                for (int j = 0; j < mFixedVolumeInfoArrayList.size(); j++) {
                    VolumeInfo itemFixed = mFixedVolumeInfoArrayList.get(j);
                    for (int i = 0; i < mRemovableVolumeInfoArrayList.size(); i++) {
                        VolumeInfo item = mRemovableVolumeInfoArrayList.get(i);
                        if (item.getPath().equalsIgnoreCase(itemFixed.getPath())) {
                            mRemovableVolumeInfoArrayList.remove(i);
                            break;
                        }
                    }
                }
            }
        }

        if (mRemovableVolumeInfoArrayList.size() > 0) {
            VolumeInfo firstRemovableStorage = mRemovableVolumeInfoArrayList.get(0);

            mUserStorage = firstRemovableStorage.getPath();
        }

        if (mUserStorage == null)  // no external storage
        {
            for (int i = 0; i < mFixedVolumeInfoArrayList.size(); i++) {
                VolumeInfo item = mFixedVolumeInfoArrayList.get(i);
                if (item.getPath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                    mUserStorage = item.getPath();
                    break;
                }
            }
        }

        String manuf = Build.MANUFACTURER;
        if (manuf.equalsIgnoreCase("sony")) {
            if (mUserStorage.contains("/storage/sdcard1")) {
                File f = new File("/storage/removable/sdcard1");
                if (f.exists()) {
                    mUserStorage = "/storage/removable/sdcard1";
                }
            }
        }

        if (true) {
            for (int i = 0; i < mFixedVolumeInfoArrayList.size(); i++) {
                VolumeInfo item = mFixedVolumeInfoArrayList.get(i);
            }

            for (int i = 0; i < mRemovableVolumeInfoArrayList.size(); i++) {
                VolumeInfo item = mRemovableVolumeInfoArrayList.get(i);
                System.out.println((i + MAX_INTERAL_COUNT) + " = " + item.toString());

            }
        }

        realRemovableStoragePath = this.getRealRemovableStoragePath();
        realInternalStoragePath = this.getRealInternalStoragePath();
    }

    private void addVolumeInfo(@NonNull String part, @NonNull String label, @NonNull String mountPoint) {

        VolumeInfo info = new VolumeInfo(label,
                mountPoint,
                //	Integer.toHexString(FileUtils.getFatVolumeId(mountPoint)));
                Integer.toHexString(0));

//		if ((info != null) && (MountInfoManager.isMounted(mountPoint))) {
        if (info != null) {
            if (part.contentEquals("auto") == true) {
                if (!label.toLowerCase().contains("usb") && !mountPoint.toLowerCase().contains("usb"))
                    mRemovableVolumeInfoArrayList.add(info);
            } else {
                if (MountInfoManager.isMounted(mountPoint))
                    mFixedVolumeInfoArrayList.add(info);
                else
                    System.out.println("not mounted and not add : " + mountPoint);
            }
        }
    }

    @Nullable
    private String getRealRemovableStoragePath() {

        String path = null;
        String manuf = Build.MANUFACTURER;
        if (Build.VERSION.SDK_INT >= 9) {
            File sdcardFile = Environment.getExternalStorageDirectory();
            boolean isExternal = Environment.isExternalStorageRemovable();

            if (isExternal) {
                System.out.println("getRealExternalStorageDirectoryPath isExternal = True");

                path = sdcardFile.getAbsolutePath();
                return path;
            }
        }

        if (manuf.equalsIgnoreCase("Sony")) {
            if (mRemovableVolumeInfoArrayList.size() > 0) {
                VolumeInfo firstRemovableStorage = mRemovableVolumeInfoArrayList.get(0);

                path = firstRemovableStorage.getPath();
                if (path.equals("/storage/sdcard1")) {
                    File extPath = new File("/storage/removable/sdcard1");
                    if (extPath.exists()) {
                        path = "/storage/removable/sdcard1";
                    }
                }
            }
        } else {
            if (!Build.MODEL.equalsIgnoreCase("Nexus S")) {
                if (mRemovableVolumeInfoArrayList.size() > 0) {
                    VolumeInfo firstRemovableStorage = mRemovableVolumeInfoArrayList.get(0);

                    path = firstRemovableStorage.getPath();
                }
            }
        }

        if (Build.VERSION.SDK_INT >= 19) {
            path = APP_SPECIFIC_DIRECTORY_SDCARD;

            if (path != null) {
                path = path.substring(0, path.indexOf("/Android"));
            }

        }

        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            if (path == null) {
                path = getExternalCardFromMountedDevices();
            }
        }

        return path;
    }

    // to get internal storage
    @Nullable
    private String getRealInternalStoragePath() {
        String internalStorage = null;
        VolumeInfo item = null;
        if (mFixedVolumeInfoArrayList.size() > 1) {
            item = mFixedVolumeInfoArrayList.get(mFixedVolumeInfoArrayList.size() - 1);
            internalStorage = item.getPath();

            if (internalStorage.equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())
                    && MountInfoManager.isMounted(internalStorage)) {
                return internalStorage;
            }
        }
        return internalStorage;
    }

    @Nullable
    private String getExternalCardFromMountedDevices() {
        String path = null;

        ArrayList<String> mountedDevices = new ArrayList();

        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/mounts"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("/mnt") || line.startsWith("/dev")) {
                    String str[] = line.split(" ");
                    if (str.length > 1 && str[1].contains("/storage/")) {
                        mountedDevices.add(str[1]);
                        System.out.println("MountPoint = " + str[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage() + " : " + e);
        }

        if (mountedDevices.size() >= 1) {
            path = mountedDevices.get(0);
        } else {
            path = "/storage/extSdCard";
        }

        return path;

    }

    @Nullable
    private static String getAppSpecificDirectorySDPath() {
        return APP_SPECIFIC_DIRECTORY_SDCARD;
    }

    private static void setAppSpecificDirectorySDPath(String path) {
        APP_SPECIFIC_DIRECTORY_SDCARD = path;
    }

    /**
     * Get the Real In-Built Storage path of the device.
     *
     * @return Path of In-Built Storage.
     */
    @Nullable
    public String getInbuiltStoragePath() {
        return realInternalStoragePath;
    }

    /**
     * Get the Real Removable Micro SD card Storage path of the device.
     *
     * @return Path of Micro SD card Storage.
     */
    @Nullable
    public String getMicroSDStoragePath() {
        return realRemovableStoragePath;
    }

    /**
     * Get the Real In-Built Storage App Specific Directory path of the device.
     *
     * @return Path of In-Built Storage App Specific Directory.
     */
    @Nullable
    public String getInbuiltStorageAppSpecificDirectoryPath() {
        return realInternalStorageAppSpecificDirectoryPath;
    }

    /**
     * Get the Real Micro SD Storage App Specific Directory path of the device.
     *
     * @return Path of Micro SD card App Specific Directory.
     */
    @Nullable
    public String getMicroSDStorageAppSpecificDirectoryPath() {
        return realRemovableStorageAppSpecificDirectoryPath;
    }

    /**
     * Get the In-Built Storage Total Size in Bytes.
     *
     * @return Total Size in Bytes of In-Built Storage.
     */
    public long getInbuiltStorageTotalSpace() {
        return totalSpace(realInternalStoragePath);
    }

    private long totalSpace(@Nullable String path) {
        if (path != null) {
            StatFs stat = new StatFs(path);
            long blockSize = 0;
            long totalBlocks = 0;

            if (Build.VERSION.SDK_INT < 18) {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
            } else {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            }

            return totalBlocks * blockSize;
        } else {
            return 0L;
        }
    }

    /**
     * Get the In-Built Storage Used Size in Bytes.
     *
     * @return Used Size in Bytes of In-Built Storage.
     */
    public long getInbuiltStorageUsedSpace() {
        return usedSpace(realInternalStoragePath);
    }

    private long usedSpace(@Nullable String path) {
        if (path != null) {
            StatFs stat = new StatFs(path);
            long blockSize = 0;
            long totalBlocks = 0;
            long availableBlocks = 0;

            if (Build.VERSION.SDK_INT < 18) {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
                availableBlocks = stat.getAvailableBlocks();
            } else {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
                availableBlocks = stat.getAvailableBlocksLong();
            }

            return (totalBlocks - availableBlocks) * blockSize;
        } else {
            return 0L;
        }
    }

    /**
     * Get the In-Built Storage Available Size in Bytes.
     *
     * @return Available Size in Bytes of In-Built Storage.
     */
    public long getInbuiltStorageAvailableSpace() {
        return availableSpace(realInternalStoragePath);
    }

    private long availableSpace(@Nullable String path) {
        if (path != null) {
            StatFs stat = new StatFs(path);
            long blockSize = 0;
            long availableBlocks = 0;

            if (Build.VERSION.SDK_INT < 18) {
                blockSize = stat.getBlockSize();
                availableBlocks = stat.getAvailableBlocks();
            } else {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            }

            return availableBlocks * blockSize;
        } else {
            return 0L;
        }
    }

    /**
     * Get the Micro SD Storage Total Size in Bytes.
     *
     * @return Total Size in Bytes of Micro SD Storage.
     */
    public long getMicroSDStorageTotalSpace() {
        return totalSpace(realRemovableStoragePath);
    }

    /**
     * Get the Micro SD Storage Used Size in Bytes.
     *
     * @return Used Size in Bytes of Micro SD Storage.
     */
    public long getMicroSDStorageUsedSpace() {
        return usedSpace(realRemovableStoragePath);
    }

    /**
     * Get the Micro SD Storage Available Size in Bytes.
     *
     * @return Available Size in Bytes of Micro SD Storage.
     */
    public long getMicroSDStorageAvailableSpace() {
        return availableSpace(realRemovableStoragePath);
    }

    private boolean isFixedPath(@Nullable String apcpPath) {
        if (apcpPath == null || apcpPath.length() < 2)
            return false;

        boolean bRet = false;
        try {
            int storageIndex = Integer.parseInt(apcpPath.substring(0, 1));
            if (storageIndex < MAX_INTERAL_COUNT) {
                bRet = true;
            }
        } catch (NumberFormatException e) {
            System.out.println("fail to isFixedPath: " + apcpPath);
        }

        return bRet;
    }

    private boolean isExistExternalStorage() {

        boolean bRet = false;
        String externalPath = getRealRemovableStoragePath();

        if (externalPath != null && MountInfoManager.isMounted(externalPath)) {

            bRet = true;
        }

        return bRet;
    }

    @Nullable
    private String getUserStorageDirectoryPath() {

        return mUserStorage;
    }

    private boolean isExistUserStorage() {
        boolean bRet = false;

        if (mUserStorage != null && MountInfoManager.isMounted(mUserStorage)) {
            bRet = true;
        }
        return bRet;
    }

    private int getFixedStorageCount() {
        return mFixedVolumeInfoArrayList.size();
    }

    @Nullable
    private VolumeInfo getFixedStorage(int index) {
        VolumeInfo ret = null;

        if (index >= 0 && index < mFixedVolumeInfoArrayList.size())
            ret = mFixedVolumeInfoArrayList.get(index);

        return ret;
    }

    // for improve to use internal storage
    private boolean isInternalIsUserStorage() {
        boolean bRet = false;
        VolumeInfo item = null;
        if (mFixedVolumeInfoArrayList.size() > 1) {
            item = mFixedVolumeInfoArrayList.get(mFixedVolumeInfoArrayList.size() - 1);
            String internalStorage = item.getPath();
            System.out.println("internalStorage = " + internalStorage);

            if (internalStorage.equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())
                    && MountInfoManager.isMounted(internalStorage)) {
                bRet = true;
            }
        }
        return bRet;
    }

    private static class MountInfoManager {
        private static final String TAG = MountInfoManager.class.getSimpleName();

        /**
         * The file path from where we are accessing external storage directory in
         * 2.2 version.
         */
        private static final String FROYO_FILE_PATH = "/etc/vold.fstab";
        //	private static final String FROYO_FILE_PATH = "/mnt/sdcard/vold/vold.fstab";  // for FaqFragmnt

        /**
         * The file path from where we are accessing external storage directory in
         * 2.1 or lower version.
         */
        private static final String ECLAIR_FILE_PATH = "/etc/vold.conf";

        /**
         * Label to map external storage directory path in 2.2
         */
        private static final String FROYO_MAPPING_STRING = "sdcard";
        private static final String FROYO_MAPPING_STRING_LG = "extsdcard";
        private static final String MOUNTINFO_NAME_EMMC = "emmc";

        /**
         * Starting string to map external storage path in 2.2
         */
        private static final String FROYO_START_STRING = "dev_mount";

        /**
         * Label to map external storage directory path in 2.1 or lower
         */
        private static final String ECLAIR_MAPPING_STRING = "mount_point";

        /**
         * Starting string to map external storage path in 2.1 or lower
         */
        private static final String ECLAIR_START_STRING = "volume_sdcard";

        private static final String ANDROID_MOUNTS = "/proc/mounts";
        //    private static final String ANDROID_MOUNTS = "/mnt/sdcard/vold/mounts"; // for FaqFragmnt

        private static final String ANDROID_MOUNTPOINT = "/dev";
        @NonNull
        private ArrayList<MountInfo> mMountArrayList = new ArrayList<MountInfo>();

        public MountInfoManager() {
            mMountArrayList.clear();

            mMountArrayList = getMountInfoList();

        }

        @NonNull
        public static ArrayList<MountInfo> getMountInfoList() {
            String strLine;
            File file = null;
            FileInputStream fstream = null;
            DataInputStream in = null;
            BufferedReader br = null;
            ArrayList<MountInfo> mountInfo = new ArrayList<MountInfo>();
            try {
                boolean isPathFound = false;
                file = new File(FROYO_FILE_PATH);
                if (file.exists()) {
                    System.out.println("FROYO_FILE_PATH exists");
                    fstream = new FileInputStream(file);
                    in = new DataInputStream(fstream);
                    br = new BufferedReader(new InputStreamReader(in));
                    while ((strLine = br.readLine()) != null) {
                        if (strLine.startsWith(FROYO_START_STRING)) {
                            String str[] = strLine.split("[ \t]");

                            if (str.length > 4) {
                                for (int i = 0; i < str.length; i++) {
                                    str[i] = str[i].trim();
                                    if (str[i].length() < 1) {
                                        for (int j = i; j < str.length - 1; j++) {
                                            str[j] = str[j + 1];
                                        }
                                    }
                                }

                                if (str[2].length() > 0) {
                                    MountInfo mInfo = new MountInfo(str[1], str[2], str[3]);
                                    mountInfo.add(mInfo);
                                }

                                isPathFound = true;
                            }
                        }
                    }
                }

                if (!isPathFound) {
                    file = new File(ECLAIR_FILE_PATH);
                    if (file.exists()) {
                        System.out.println("ECLAIR_FILE_PATH exists");
                        fstream = new FileInputStream(file);
                        in = new DataInputStream(fstream);
                        br = new BufferedReader(new InputStreamReader(in));
                        while ((strLine = br.readLine()) != null) {
                            if (strLine.startsWith(ECLAIR_START_STRING)) {
                                while ((strLine = br.readLine()) != null) {
                                    if (strLine.trim().startsWith(ECLAIR_MAPPING_STRING)) {
                                        //TODO: but for Gals2.3, vold.conf has only /sdcard
                                        String mountpoint = strLine.substring(strLine.indexOf("/"));
                                        if (mountpoint.length() > 0) {
                                            MountInfo mInfo = new MountInfo("", mountpoint, "");
                                            mountInfo.add(mInfo);
                                            break;
                                        }
                                    }
                                }
                                //                            break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception :" + e.getMessage());
            } finally {
                try {
                    if (br != null)
                        br.close();

                    if (in != null)
                        in.close();

                    if (fstream != null)
                        fstream.close();

                } catch (IOException e) {
                    System.out.println("Exception :" + e.getMessage());
                }
            }

            return mountInfo;
        }

        public static boolean isMounted(@NonNull String mountPoint) {
            boolean result = false;
            try {
                if (mountPoint.endsWith("/")) {
                    mountPoint = mountPoint.substring(0, mountPoint.length() - 1);
                }

                if (mountPoint.length() > 0) {
                    BufferedReader br = new BufferedReader(new FileReader(ANDROID_MOUNTS));
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith(ANDROID_MOUNTPOINT) || line.startsWith("/storage") || line.startsWith("/mnt") || line.startsWith("/")) {
                            String str[] = line.split(" ");
                            if ((str.length > 1) && (str[1].equals(mountPoint))) {
                                System.out.println(mountPoint + " is confirmed to be mounted.");
                                result = true;
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage() + " : " + e);
            }
            return result;
        }

        @Nullable
        public static String getEmmcDirectoryPath() {
            String path = null;
            ArrayList<MountInfo> mountInfo = getMountInfoList();
            if (mountInfo != null) {
                for (int i = 0; i < mountInfo.size(); i++) {
                    if (mountInfo.get(i).mLabel.equals(MOUNTINFO_NAME_EMMC)) {
                        path = mountInfo.get(i).mMountPoint;
                        //    	        System.out.println( "emmc path found: " + path);
                        break;
                    }
                }
            }
            return path;
        }

        public int size() {
            return mMountArrayList.size();
        }

        public String getLabelAt(int index) {
            return mMountArrayList.get(index).mLabel;
        }

        public String getMountPointAt(int index) {
            return mMountArrayList.get(index).mMountPoint;
        }

        public String getPartAt(int index) {
            return mMountArrayList.get(index).mPart;
        }

        /**
         * Method to extract external media storage path from files.
         *
         * @return the external media path.
         */
        private static class MountInfo {
            private final String mLabel;
            private final String mMountPoint;
            private final String mPart;

            private MountInfo(String label, String mountPoint, String part) {
                mLabel = label;
                mMountPoint = mountPoint;
                mPart = part;
            }
        }
    }

    private class VolumeInfo {
        private final String mLabel;
        @NonNull
        private final String mPath;
        private final String mVolumeId;
        private long mVolumeSize;
        private long mAvailableSize;

        private VolumeInfo(final String label, @NonNull final String path, final String volumeId) {
            mLabel = label;
            mVolumeId = volumeId;

            int index = path.indexOf(":");
            if (index >= 0)
                mPath = path.substring(0, index);
            else
                mPath = path;

//			System.out.println( "mPath = " + mPath);
            mVolumeSize = 0;
            mAvailableSize = 0;

            File volume = new File(mPath);
            if (volume.exists()) {
                try {
                    StatFs stat = new StatFs(volume.getPath());
                    long blockSize;
                    long blockCount;
                    long availableBlocks;

                    if (Build.VERSION.SDK_INT < 18) {
                        blockSize = stat.getBlockSize();
                        blockCount = stat.getBlockCount();
                        availableBlocks = stat.getAvailableBlocks();
                    } else {
                        blockSize = stat.getBlockSizeLong();
                        blockCount = stat.getBlockCountLong();
                        availableBlocks = stat.getAvailableBlocksLong();
                    }

                    mVolumeSize = blockSize * blockCount;
                    mAvailableSize = blockSize * availableBlocks;
                } catch (IllegalArgumentException e) {
                    System.out.println("IllegalArgumentException : " + path);
                }
            }
        }

        @NonNull
        @Override
        public String toString() {
            String ret = "mLabel : " + mLabel + ", mPath : " + mPath
                    + ", mVolumeId : " + mVolumeId
                    + ", mVolumeSize : " + mVolumeSize
                    + ", mAvailableSize : " + mAvailableSize;
            return ret;
        }

        @NonNull
        public String getPath() {
            return mPath;
        }

    }
}