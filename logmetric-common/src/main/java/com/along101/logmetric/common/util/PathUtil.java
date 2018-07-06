package com.along101.logmetric.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class PathUtil {

    private static String defDirectory;


    static {
        try {
            URL url = ReflectUtil.getDefaultClassLoader().getResource("");
            if (url != null){
                File file = new File(url.getPath());
                defDirectory = file.getCanonicalFile().getPath();
            }else {
                url = PathUtil.class.getProtectionDomain().getCodeSource().getLocation();
                File file = new File(url.getPath());
                defDirectory =  file.getParent();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
	
    public static boolean probePath(String probeFilePath){
        if (probePathInParent(probeFilePath))
            return true;
        File jarFile = new File(PathUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        if (jarFile.exists() && jarFile.getParentFile().exists()) {
        	defDirectory = jarFile.getParent();
            return probePathInParent(probeFilePath);
        }
        return false;
    }
    
    private static boolean probePathInParent(String probeFilePath){
        File probeFile = new File(defDirectory,probeFilePath);
        if (probeFile.exists())
            return true;
        File currBaseFile = new File(defDirectory);
        if (new File(currBaseFile.getParent(),probeFilePath).exists()) {
        	defDirectory = currBaseFile.getParent();
            return true;
        }
        return false;
    }
    
    public static void main(String args[]){
    	System.out.println("Fdsafas");
    }
}
