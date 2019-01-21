package com.robomwm.mehcheat;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

/**
 * Created on 1/21/2019.
 *
 * Snippet taken and modified from https://github.com/RoboMWM/usefulutil
 *
 * @author RoboMWM
 */
public class UsefulUtil
{
    /**
     * Asynchronously save the specified string into a file
     *
     * TODO: return status via a future or something
     *
     * @param storageFile File to store contents in
     * @param contents
     */
    public static void saveStringToFile(File storageFile, String contents)
    {
        new Thread(
                () -> {
                    try
                    {
                        storageFile.getParentFile().mkdirs();
                        storageFile.createNewFile();
                        Files.write(storageFile.toPath(), Collections.singletonList(contents), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
        ).start();

    }
}
