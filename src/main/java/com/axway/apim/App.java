package com.axway.apim;

import java.io.*;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Create fed file
 */
public class App {
    public static void main(String[] args) {

        String fedName = System.getenv("fedName");
        String fedDir = System.getenv("fedDir");

        if(fedName == null){
            throw new RuntimeException("Please setup a system property fedName");
        }

        if(fedDir == null){
            throw new RuntimeException("Please setup a system property fedDir");
        }

        File fed = new File(fedDir);
        if(!fed.isDirectory()){
            throw new RuntimeException(fed.getAbsolutePath() + " is not a valid directory");
        }

        App app = new App();
        app.run(fedName, fed);

    }

    public void run(String fedName, File fedDir){
        App app = new App();
        String UUIDStr = UUID.randomUUID().toString();
        Manifest manifest = app.createManifest(UUIDStr);

        try {
            app.createFed(UUIDStr, manifest, fedName, fedDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Manifest createManifest(String UUIDStr){
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(new Attributes.Name("Id"),UUIDStr);
        attributes.put(new Attributes.Name("Timestamp"), System.currentTimeMillis()+"");
        return manifest;
    }

    public  void createFed(String UUIDStr, Manifest manifest, String filename, File dir) throws IOException {
        FileOutputStream fileOutputStream = null;
        JarOutputStream jarOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filename);
            jarOutputStream = new JarOutputStream(fileOutputStream, manifest);
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    addContent(jarOutputStream, file, UUIDStr);
                } else {
                    if (file.getName().equals("meta-inf")) {
                        File[] metaInfFiles = file.listFiles();
                        for (File metaInfFile : metaInfFiles) {
                            System.out.println(metaInfFile.getName());
                            if (metaInfFile.getName().equals("manifest.mf")) {
                                continue;
                            }

                            addContent(jarOutputStream, metaInfFile, "meta-inf");
                        }
                    }
                }
            }

        } finally {
            if (jarOutputStream != null) {
                jarOutputStream.close();
            }

            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }

    }


    private   void addContent(JarOutputStream jarOutputStream, File file, String dirName) throws IOException {
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            jarOutputStream.putNextEntry(new JarEntry(dirName + "/" + file.getName()));
            byte[] buffer = new byte[1024];
            while (true) {
                int count = bufferedInputStream.read(buffer);
                if (count == -1)
                    break;
                jarOutputStream.write(buffer, 0, count);
            }
            jarOutputStream.closeEntry();

        }finally {
            if( bufferedInputStream != null){
                bufferedInputStream.close();
            }
        }
    }
}
