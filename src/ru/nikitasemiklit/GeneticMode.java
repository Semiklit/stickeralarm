package ru.nikitasemiklit;

import com.google.gson.Gson;
import ru.nikitasemiklit.model.ModelParameters;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GeneticMode {

    private final List <ModelParameters> parametersForBegin;
    private final PackageMode packageMode;
    private final File dirToResults;
    private final TestClass testClass = new TestClass();
    private int topCount = 10;

    public GeneticMode(File filesDirectory, File trainingFile, File dirToResults) throws IOException, ParseException, FileNotFoundException {
        packageMode = new PackageMode(filesDirectory);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(trainingFile));
        String input;
        while ((input = bufferedReader.readLine()) != null) {
            String[] fields = input.split(";");
            String fileName = fields[0];
            Integer data = Integer.parseInt(fields[1]);
            testClass.addTest(fileName, data);
        }
        parametersForBegin = generateFirstSample();
        this.dirToResults = dirToResults;
    }

    public void run (){
        List<ModelParameters> parameters = parametersForBegin;
        List<ModelParameters> topParameters = new ArrayList<>();
        int bestResult = 10;
        try {
            while (bestResult > 2) {
                Map<ModelParameters, Map<String, Integer>> algorithmResults = count(parameters);
                topParameters.clear();
                bestResult = getTop10Results(algorithmResults, topParameters);
                System.out.println("BEST RESULT: " + bestResult);
                saveBestResult(topParameters, bestResult);
                generateMoreParameters(topParameters);
                parameters = getNewSample(topParameters);
            }
        } catch (NotEnoughFilesException ex){
            System.out.println(ex.getMessage());
        }

        saveTopResults(topParameters);
    }

    private List<ModelParameters> generateFirstSample(){
        List<ModelParameters> modelParametersList = new ArrayList<>();
        for (int i=0; i<20; i++){
            double [] parameters = new double[11];
            for (int j = 0; j <11; j++){
                parameters[j] = getRandomParameter(j);
            }
            ModelParameters modelParameters = new ModelParameters(parameters);
            modelParametersList.add(modelParameters);
        }
        return modelParametersList;
    }

    private void generateMoreParameters(List<ModelParameters> modelParameters){
        while (modelParameters.size() < 5){
            double [] parameters = new double[11];
            for (int j = 0; j <11; j++){
                parameters[j] = getRandomParameter(j);
            }
            modelParameters.add(new ModelParameters(parameters));
        }
        if (modelParameters.size() < 10){
            topCount = modelParameters.size();
        }
    }

    private Map<ModelParameters, Map<String, Integer>> count (List<ModelParameters> parameters){
        Map<ModelParameters, Map<String, Integer>> results = new ConcurrentHashMap<>();
        parameters.forEach((parameter) -> {
            Map<String, Integer> result = packageMode.run(parameter, 0.5, 0);
            results.put(parameter ,result);
        });
        return results;
    }

    private int getTop10Results(Map<ModelParameters, Map<String, Integer>> algorithmResults, List<ModelParameters> topParameters) throws NotEnoughFilesException{
        Vector<PairOfResults> resultList = new Vector<>();
        algorithmResults.forEach((parameters, resultsMap) ->{
            int falseDetections = 0;
            boolean loosedStickers = false;
            for (Map.Entry<String, Integer> entry : resultsMap.entrySet()){
                //System.out.println(entry.getKey());
                int test = testClass.getResultForFile(entry.getKey());
                if (entry.getValue() > test){
                    //увеличиваем ложные срабатывания
                    falseDetections += 1;
                }
                if (entry.getValue() < test){
                    //отмечаем пропущенный стикер
                    loosedStickers = true;
                    falseDetections += 10;
                    System.out.println("Пропущен стикер: " + entry.getKey() + " результат выполнения " + entry.getValue() );
                }
            }
            System.out.println("Ошибок: " + falseDetections + " " + "пропущенные стикеры: " + loosedStickers);
            //if (!loosedStickers){
                System.out.println("Добавлены результаты");
                resultList.add(new PairOfResults( parameters, falseDetections));
            //}
        });

        if (resultList.size() != 0) {
            resultList.sort(new Comparator<PairOfResults>() {
                @Override
                public int compare(PairOfResults o1, PairOfResults o2) {
                    return o1.getResult().compareTo(o2.getResult());
                }
            });
        }

        topCount = 10;

        if (resultList.size() < topCount) {
            topCount = resultList.size();
        }
        if (topCount != 0) {
            System.out.println("Выбираем " + topCount + " лучших");
            for (int i = 0; i < topCount; i++) {
                topParameters.add(resultList.elementAt(i).getModelParameters());
            }
            return resultList.elementAt(0).getResult();
        }

        return 1000;
    }

    class PairOfResults {
        private final ModelParameters modelParameters;
        private final Integer result;

        ModelParameters getModelParameters() {
            return modelParameters;
        }

        Integer getResult() {
            return result;
        }

        PairOfResults(ModelParameters modelParameters, Integer result) {

            this.modelParameters = modelParameters;
            this.result = result;
        }
    }

    class NotEnoughFilesException extends Exception{
        public NotEnoughFilesException() {
            super("Not enough files!!!");
        }
    }

    class TestClass{
        final Vector<String> fileNames = new Vector<>();
        final Vector<Integer> datas = new Vector<>();

        void addTest (String fileName, Integer data){
            fileNames.add(fileName);
            datas.add(data);
            System.out.println("Added data: " + fileName + " " + data);
        }

        synchronized int getResultForFile (String fileName){
            for (int i = 0; i< fileNames.size(); i++){
                if (fileName.equals(fileNames.elementAt(i))){
                    return datas.elementAt(i);
                }
            }
            throw new NoSuchElementException();
        }

    }

    private List<ModelParameters> getNewSample(List<ModelParameters> modelParameters){
        List<ModelParameters> newSample = new ArrayList<>();
        for (int i = 0; i<10; i++) {
            //случзнач от 1 до 20
            int separator = (int) (Math.random() * 11);

            int firstIndex = (int) (Math.random()*topCount);
            int secondIndex = (int) (Math.random()*topCount);

            double[] firstInput =  modelParameters.get(firstIndex).getParametersArray();
            double[] secondInput = modelParameters.get(secondIndex).getParametersArray();
            double[] firstSample = new double[11];
            double[] secondSample = new double[11];

            for (int j = 0; j<separator; j++){
                firstSample[j] = firstInput[j];
                secondSample[j] = secondInput[j];
            }
            for (int j = separator; j<11; j++){
                firstSample[j] = secondInput[j];
                secondSample[j] = secondInput[j];
            }

            for (int j=0; j<11; j++){
                if (Math.random() < 0.1){
                    firstSample[j] = getRandomParameter(j);
                }
                if (Math.random() < 0.1){
                    secondSample[j] = getRandomParameter(j);
                }
            }

            ModelParameters newParameters = new ModelParameters(firstSample);
            newSample.add(newParameters);
            newParameters = new ModelParameters(secondSample);
            newSample.add(newParameters);
        }
        return newSample;
    }

    private double getRandomParameter (int index){
        switch (index){
            case 0: return 0.15 + Math.random() * 0.85;
            case 1: return 2 + Math.random() * 3;
            case 2: return -0.15 - Math.random() * 0.85;
            case 3: return -2 - Math.random() * 3;
            case 4: return 3 + Math.random() * 7;
            case 5: return 18 + Math.random() * 25;
            case 6: return 3 + Math.random() * 7;
            case 7: return 0.2 + Math.random() * 0.4;
            case 8: return 1.3 + Math.random() * 1.9;
            case 9: return 25 + Math.random() * 45;
            case 10: return 7;// 6 + Math.random() * 4;
        }
        return 0;
    }

    private void saveTopResults(List<ModelParameters> modelParameters){
        Gson gson = new Gson();
        modelParameters.forEach(parameters ->{
            String fileName = dirToResults + File.separator + modelParameters.indexOf(parameters) + ".json";
            try(Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), "utf-8"))){
                writer.write(gson.toJson(parameters));
            }
            catch (IOException ex){
                System.out.println("Unable to write configuration file");
            }
        });
    }

    private void saveBestResult(List<ModelParameters> modelParameters, int bestResult){
        Gson gson = new Gson();
        if (modelParameters.size() > 0) {
            ModelParameters parameters = modelParameters.get(0);
            String fileName = dirToResults + File.separator + "Result_" + bestResult + "_at_" + System.currentTimeMillis() + ".json";
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), "utf-8"))) {
                writer.write(gson.toJson(parameters));
                System.out.println("Файл сохранен " + fileName);
            } catch (IOException ex) {
                System.out.println("Unable to write configuration file");
            }
        }

    }

}
