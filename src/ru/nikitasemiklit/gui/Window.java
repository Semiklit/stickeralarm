package ru.nikitasemiklit.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.knowm.xchart.*;
import ru.nikitasemiklit.enums.TYPE_DATA_TO_DRAW;
import ru.nikitasemiklit.model.Model;
import ru.nikitasemiklit.model.ModelParameters;
import ru.nikitasemiklit.model.RawData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.ParseException;

public class Window extends JFrame {

    private final String CONFIG_FILE = "Config.json";

    private long timeFrom;
    private long timeTo;

    private static ModelParameters modelParameters = new ModelParameters();
    private Model model;
    private RawData rawData;

    private File dir = null;

    private static final JButton countButton = new JButton("Пересчитать");
    private static final JButton openFileButton = new JButton("Открыть файл");
    private static final JButton saveChartButton = new JButton("Сохранить");
    private static final JButton countCriticalSensorsButton = new JButton("Применить");
    private static final JButton saveConfigurationButton = new JButton("Сохранить конфигурацию");

    private static final JLabel statusLabel = new JLabel("Статус");
    private static final JLabel filePathLabel = new JLabel("Путь к файлу");
    private static final JLabel timeStartLabel = new JLabel("Время начала графика в секундах (необязательно)");
    private static final JLabel timeFinishLabel = new JLabel("Время окончания графика в секундах (необязательно)");
    private static final JLabel dataLabel = new JLabel("Данные для вывода (номера датчиков или столбцов через пробел)");
    private static final JLabel intervalForCalculatingSecLabel = new JLabel ("Interval for calculating temperature change rate");
    private static final JLabel minimumRisingRateLabel = new JLabel("Minimum temperature rising rate");
    private static final JLabel maximumRisingRateLabel = new JLabel("Maximum temperature rising rate");
    private static final JLabel minimumFallingRateLabel = new JLabel("Maximum temperature falling rate");
    private static final JLabel maximumFallingRateLabel = new JLabel("Minimum temperature falling rate");
    private static final JLabel minimumTempretureLabel = new JLabel("Minimum temperature");
    private static final JLabel maximumTeptretureLabel = new JLabel("Maximum temperature");
    private static final JLabel minimumDurationTempRiseLabel = new JLabel("Minimum duration for abnormal temperature rise");
    private static final JLabel maximumDurationTempRiseLabel = new JLabel("Maximum duration for abnormal temperature rise");
    private static final JLabel minimumDurationTempFallLabel = new JLabel("Minimum duration for abnormal temperature fall");
    private static final JLabel minimumRatioStickerSpeedToCastingSpeedLabel = new JLabel("Minimum range of the ratio of Vy/Vc");
    private static final JLabel maximumRatioStickerSpeedToCastingSpeedLabel = new JLabel("Maximum range of the ratio of Vy/Vc");
    private static final JLabel abnormalIntervalLabel = new JLabel("Check interval of abnormality");
    private static final JLabel abnormalStickerAlarmLabel = new JLabel("Abnormal thermocouples count threshold of sticking alarm");
    private static final JLabel abnormalStickerWarningLabel = new JLabel("Abnormal thermocouples count threshold of sticking warning");

    private static final JTextField filePathField = new JTextField();

    private static final JRadioButton drawBySensors = new JRadioButton("Изобразить отдельные датчики");
    private static final JRadioButton drawByColumn = new JRadioButton("Изобразить датчики в отдельном столбце");
    private static final JRadioButton drawByLine = new JRadioButton("Изобразить датчики в отдельных строчках");

    private static final JTextField dataToDrawField = new JTextField();
    private static final JTextField timeFromField = new JTextField();
    private static final JTextField timeToField = new JTextField();
    private static final JTextField intervalForCalculatingSecField = new JTextField ("5");
    private static final JTextField minimumRisingRateField = new JTextField("0.18");
    private static final JTextField maximumRisingRateField = new JTextField("2.2");
    private static final JTextField minimumFallingRateField = new JTextField("-0.17");
    private static final JTextField maximumFallingRateField = new JTextField("-2.0");
    private static final JTextField minimumTempretureField = new JTextField("50");
    private static final JTextField maximumTeptretureField = new JTextField("200");
    private static final JTextField minimumDurationTempRiseField = new JTextField("3");
    private static final JTextField maximumDurationTempRiseField = new JTextField("25");
    private static final JTextField minimumDurationTempFallField = new JTextField("5");
    private static final JTextField minimumRatioStickerSpeedToCastingSpeedField = new JTextField("0.38");
    private static final JTextField maximumRatioStickerSpeedToCastingSpeedField = new JTextField("1.5");
    private static final JTextField abnormalIntervalField = new JTextField("30");
    private static final JTextField abnormalStickerAlarmField = new JTextField("6");
    private static final JTextField abnormalStickerWarningField = new JTextField("3");

    private static final JCheckBox speedLineCheckBox = new JCheckBox("Отображать скорость", true);


    private static XYChart chart = new XYChartBuilder().title("Let's search some stickers").xAxisTitle("Time").yAxisTitle("Tempreture Rate").build();


    public Window (String title){

        super(title);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container mainContainer = this.getContentPane();
        mainContainer.setLayout(new BorderLayout());
        Container workArea = new Container();
        workArea.setLayout(new BoxLayout(workArea, BoxLayout.Y_AXIS));
        workArea.add(new XChartPanel<>(chart));

        Container bottomArea = new Container();
        bottomArea.setLayout(new GridLayout());

        Container dataControllArea = new Container();
        dataControllArea.setLayout(new BoxLayout(dataControllArea, BoxLayout.Y_AXIS));
        dataControllArea.add(filePathLabel);
        dataControllArea.add(filePathField);
        dataControllArea.add(openFileButton);
        dataControllArea.add(timeStartLabel);
        dataControllArea.add(timeFromField);
        dataControllArea.add(timeFinishLabel);
        dataControllArea.add(timeToField);

        openFileButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (dir != null) {
                    fileChooser.setCurrentDirectory(dir);
                }
                int ret = fileChooser.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        dir = file.getParentFile();
                        filePathField.setText(file.getName());
                        rawData = RawData.parseFile(file);
                        model = Model.countTempRate(rawData, modelParameters.getIntervalForCalculatingSec());
                        timeFromField.setText((model.getFirstTime()).toString());
                        timeToField.setText((model.getLastTime()).toString());
                    }
                    catch (ParseException ex){
                        statusLabel.setText("Неверный файл");
                    }
                    catch (IOException ex){
                        statusLabel.setText("Ошибка чтения файла");
                    }
                }
            }
        });

        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(drawBySensors);
        radioGroup.add(drawByColumn);
        radioGroup.add(drawByLine);
        dataControllArea.add(drawBySensors);
        dataControllArea.add(drawByColumn);
        dataControllArea.add(drawByLine);
        drawByColumn.setSelected(true);
        dataControllArea.add(speedLineCheckBox);
        dataControllArea.add(dataLabel);
        dataControllArea.add(dataToDrawField);
        dataControllArea.add(countButton);
        dataControllArea.add(saveChartButton);


        Container calculationControllArea = new Container();
        calculationControllArea.setLayout(new BoxLayout(calculationControllArea, BoxLayout.Y_AXIS));
        calculationControllArea.add(intervalForCalculatingSecLabel);
        calculationControllArea.add(intervalForCalculatingSecField);
        calculationControllArea.add(minimumRisingRateLabel);
        calculationControllArea.add(minimumRisingRateField);
        calculationControllArea.add(maximumRisingRateLabel);
        calculationControllArea.add(maximumRisingRateField);
        calculationControllArea.add(minimumFallingRateLabel);
        calculationControllArea.add(minimumFallingRateField);
        calculationControllArea.add(maximumFallingRateLabel);
        calculationControllArea.add(maximumFallingRateField);
        calculationControllArea.add(minimumTempretureLabel);
        calculationControllArea.add(minimumTempretureField);
        calculationControllArea.add(maximumTeptretureLabel);
        calculationControllArea.add(maximumTeptretureField);
        calculationControllArea.add(minimumDurationTempRiseLabel);
        calculationControllArea.add(minimumDurationTempRiseField);

        Container calculationSecondControllArea = new Container();
        calculationSecondControllArea.setLayout(new BoxLayout(calculationSecondControllArea, BoxLayout.Y_AXIS));
        calculationSecondControllArea.add(maximumDurationTempRiseLabel);
        calculationSecondControllArea.add(maximumDurationTempRiseField);
        calculationSecondControllArea.add(minimumDurationTempFallLabel);
        calculationSecondControllArea.add(minimumDurationTempFallField);
        calculationSecondControllArea.add(minimumRatioStickerSpeedToCastingSpeedLabel);
        calculationSecondControllArea.add(minimumRatioStickerSpeedToCastingSpeedField);
        calculationSecondControllArea.add(maximumRatioStickerSpeedToCastingSpeedLabel);
        calculationSecondControllArea.add(maximumRatioStickerSpeedToCastingSpeedField);
        calculationSecondControllArea.add(abnormalIntervalLabel);
        calculationSecondControllArea.add(abnormalIntervalField);
        calculationSecondControllArea.add(abnormalStickerAlarmLabel);
        calculationSecondControllArea.add(abnormalStickerAlarmField);
        calculationSecondControllArea.add(abnormalStickerWarningLabel);
        calculationSecondControllArea.add(abnormalStickerWarningField);
        calculationSecondControllArea.add(countCriticalSensorsButton);
        calculationSecondControllArea.add(saveConfigurationButton);

        bottomArea.add(dataControllArea);
        bottomArea.add(calculationControllArea);
        bottomArea.add(calculationSecondControllArea);
        workArea.add(bottomArea);
        mainContainer.add(workArea, BorderLayout.CENTER);
        mainContainer.add(statusLabel, BorderLayout.SOUTH);

        countCriticalSensorsButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recountModelParameters();
                model.countCriticalSensors(modelParameters);
            }
        });

        countButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    timeFrom = Long.parseLong(timeFromField.getText());
                    timeTo = Long.parseLong(timeToField.getText());
                }
                catch (NumberFormatException ex){
                    timeFrom = 0;
                    timeTo = 0;
                }
                TYPE_DATA_TO_DRAW draw = TYPE_DATA_TO_DRAW.NOTHING;
                if (drawByColumn.isSelected()){
                    draw = TYPE_DATA_TO_DRAW.COLUMNS;
                }
                if (drawByLine.isSelected()){
                    draw = TYPE_DATA_TO_DRAW.LINES;
                }
                if (drawBySensors.isSelected()){
                    draw = TYPE_DATA_TO_DRAW.SENSORS;
                }
                chart = model.getChart(timeFrom, timeTo, dataToDrawField.getText(), speedLineCheckBox.isSelected(), draw);
                workArea.remove(0);
                workArea.add(new XChartPanel<>(chart), 0);
                statusLabel.setText("Обновлено " + System.currentTimeMillis());
            }
        });

        saveChartButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String fileName = filePathField.getText();
                    if (drawByColumn.isSelected()){
                        fileName += " columns";
                    }
                    if (drawByLine.isSelected()){
                        fileName += " lines";
                    }
                    if (drawBySensors.isSelected()){
                        fileName += " rawTempreture";
                    }
                    fileName += " " + dataToDrawField.getText();
                    fileName += " " + timeFrom;
                    fileName += " " + timeTo;
                    BitmapEncoder.saveBitmapWithDPI(chart, fileName, BitmapEncoder.BitmapFormat.PNG, 100);
                } catch (IOException ex){
                    statusLabel.setText("Ошибка создания файла");
                }
            }
        });

        saveConfigurationButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Gson gson = new GsonBuilder().create();
                recountModelParameters();
                try(Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(CONFIG_FILE), "utf-8"))){
                    writer.write(gson.toJson(modelParameters));
                }
                catch (IOException ex){
                    System.out.println("Ошибка записи файла конфигурации");
                }
            }
        });
    }

    private void recountModelParameters(){
        int intervalForCalculatingSec = Integer.parseInt(intervalForCalculatingSecField.getText());
        double minimumRisingRate = Double.parseDouble(minimumRisingRateField.getText());
        double maximumRisingRate = Double.parseDouble(maximumRisingRateField.getText());
        double minimumFallingRate = Double.parseDouble(minimumFallingRateField.getText());
        double maximumFallingRate = Double.parseDouble(maximumFallingRateField.getText());
        int minimumTempreture = Integer.parseInt(minimumTempretureField.getText());
        int maximumTeptreture = Integer.parseInt(maximumTeptretureField.getText());
        int minimumDurationTempRise = Integer.parseInt(minimumDurationTempRiseField.getText());
        int maximumDurationTempRise = Integer.parseInt(maximumDurationTempRiseField.getText());
        int minimumDurationTempFall = Integer.parseInt(minimumDurationTempFallField.getText());
        double minimumRatioStickerSpeedToCastingSpeed = Double.parseDouble(minimumRatioStickerSpeedToCastingSpeedField.getText());
        double maximumRatioStickerSpeedToCastingSpeed = Double.parseDouble(maximumRatioStickerSpeedToCastingSpeedField.getText());
        int abnormalInterval = Integer.parseInt(abnormalIntervalField.getText());
        int abnormalStickerAlarm = Integer.parseInt(abnormalStickerAlarmField.getText());
        int abnormalStickerWarning = Integer.parseInt(abnormalStickerWarningField.getText());
        modelParameters = new ModelParameters(intervalForCalculatingSec, minimumRisingRate, maximumRisingRate, minimumFallingRate, maximumFallingRate, minimumTempreture, maximumTeptreture,
                minimumDurationTempRise, maximumDurationTempRise, minimumDurationTempFall, minimumRatioStickerSpeedToCastingSpeed, maximumRatioStickerSpeedToCastingSpeed, abnormalInterval,
                abnormalStickerAlarm, abnormalStickerWarning);
    }
    }
