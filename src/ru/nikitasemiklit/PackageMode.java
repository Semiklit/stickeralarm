package ru.nikitasemiklit;

import ru.nikitasemiklit.gui.Window;
import ru.nikitasemiklit.model.Model;
import ru.nikitasemiklit.model.ModelParameters;
import ru.nikitasemiklit.model.RawData;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.*;

public class PackageMode {

    File file;

    public PackageMode(File file) {
        this.file = file;
    }

    public Map<String, Integer> run (final ModelParameters modelParameters, double alpha, int smoothCount){

        ConcurrentMap results = new ConcurrentHashMap<String, Integer>();
        String[] paths = file.list();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch SYNC = new CountDownLatch(paths.length);

        try {
            for (String path : paths) {
                executor.submit(() -> {
                    System.out.println("parsing file " + path);
                    try {
                        File currentFile = new File(file.getAbsolutePath() + "/" + path);
                        RawData rawData = RawData.parseFile(currentFile);
                        Model model = Model.countTempRate(rawData, modelParameters.getIntervalForCalculatingSec());
                        for (int i = 0; i< smoothCount; i++){
                            model.smooth(alpha);
                        }
                        int answer = model.countCriticalSensors(modelParameters, false);
                        results.put(currentFile.getName(), answer);
                    } catch (ParseException ex) {
                        System.out.println("unable to parse: " + file.getAbsolutePath() + "/" + path);
                    } catch (IOException ex) {
                        System.out.println("unable to open: " + file.getAbsolutePath() + "/" + path);
                    } finally {
                        SYNC.countDown();
                    }
                });
            }

            SYNC.await();

        } catch (InterruptedException e){

        }
        return results;
    }

}
