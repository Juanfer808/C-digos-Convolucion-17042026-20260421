package util;

public class File {
    public static void createDirStructure(String path){
        String[] dirs = path.split("/");
        String currentDir = "";
        for (int i = 0; i < dirs.length; i++) {
            currentDir += dirs[i] + "/";
            java.io.File dir = new java.io.File(currentDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
    }

    public static String getFileName(String path){
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    public static String getFileNameWithoutExtension(String path){
        String[] parts = path.split("/");
        String fileName = parts[parts.length - 1];
        parts = fileName.split("\\.");
        return parts[0];
    }

    public static String getDir(String path){
        String[] parts = path.split("/");
        String dir = "";
        for (int i = 0; i < parts.length - 1; i++) {
            dir += parts[i] + "/";
        }
        return dir;
    }

    public static String getExtension(String path){
        String[] parts = path.split("\\.");
        return parts[parts.length - 1];
    }

    public static boolean isFile(String path){
        java.io.File file = new java.io.File(path);
        return file.isFile();
    }

    public static boolean isDirectory(String path){
        java.io.File file = new java.io.File(path);
        return file.isDirectory();
    }
}
