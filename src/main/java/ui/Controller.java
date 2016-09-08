package ui;

import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import processor.CheckCertNoProcessor;
import processor.CheckRegionCodeProcessor;
import processor.MergeProcessor;
import service.*;
import utils.TransLogger;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Controller {
  private static Logger logger = TransLogger.getLogger(Controller.class);

  private static int CHECK_CERTNO_PARTITION_COUNT = 64;
  private static int CHECK_CERTNO_THREAD_POOL_SIZE = 10;

  private static int MERGE_THREAD_PARTITION_COUNT = 10;
  private static int MERGE_THREAD_POOL_SIZE = 10;

  private static int CHECK_DIC_CODE_THREAD_PROCESS_COUNT = 1000;
  private static int CHECK_DIC_CODE_THREAD_POOL_SIZE = 20;

  @FXML //  fx:id="btnCheckCertNo"
  private Button btnCheckCertNo; // Value injected by FXMLLoader
  @FXML //  fx:id="btnNormalizeName"
  private Button btnMerge; // Value injected by FXMLLoader
  @FXML //  fx:id="btnExtractResultCode"
  private Button btnExtractResultCode; // Value injected by FXMLLoader
  @FXML //  fx:id="btnCheckRegionCode"
  private Button btnCheckRegionCode; // Value injected by FXMLLoader
  @FXML //  fx:id="vboxBase"
  private VBox vboxBase; // Value injected by FXMLLoader
  @FXML //  fx:id="scrollPane"
  private ScrollPane scrollPane; // Value injected by FXMLLoader
  @FXML //  fx:id="vBoxItems"
  private VBox vBoxItems; // Value injected by FXMLLoader
  @FXML //  fx:id="labInfo"
  private Label labInfo; // Value injected by FXMLLoader
  @FXML //  fx:id="labRunning"
  private Label labRunning; // Value injected by FXMLLoader

  /**
   * Initializes the controller class.
   */
  @FXML
  // This method is called by the FXMLLoader when initialization is complete
  void initialize() {
    assert btnCheckCertNo != null : "fx:id=\"btnCheckCertNo\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
    assert btnMerge != null : "fx:id=\"btnMerge\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
    assert btnExtractResultCode != null : "fx:id=\"btnExtractResultCode\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
    assert btnCheckRegionCode != null : "fx:id=\"btnCheckRegionCode\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";

    assert labInfo != null : "fx:id=\"labInfo\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
    assert labRunning != null : "fx:id=\"labRunning\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
    assert vboxBase != null : "fx:id=\"vboxBase\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
    assert scrollPane != null : "fx:id=\"scrollPane\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
    assert vBoxItems != null : "fx:id=\"vBoxItems\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";

    try {
      InputStream in = Controller.class.getClassLoader().getResourceAsStream("config.properties");
      Properties prop = new Properties();
      prop.load(in);
      CHECK_CERTNO_PARTITION_COUNT = Integer.parseInt(prop.getProperty("check_certno_partition_count"));
      CHECK_CERTNO_THREAD_POOL_SIZE = Integer.parseInt(prop.getProperty("check_certno_threadpool_max_size"));
      MERGE_THREAD_PARTITION_COUNT = 128;
      MERGE_THREAD_POOL_SIZE = Integer.parseInt(prop.getProperty("merge_threadpool_max_size"));
      CHECK_DIC_CODE_THREAD_PROCESS_COUNT = Integer.parseInt(prop.getProperty("check_dic_code_thread_process_count"));
      CHECK_DIC_CODE_THREAD_POOL_SIZE = Integer.parseInt(prop.getProperty("check_dic_code_threadpool_max_size"));
    } catch (Exception e) {
      logger.severe(e.getMessage());
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("错误");
      alert.setHeaderText("读取配置文件config.properties 错误");
      alert.setContentText(e.getMessage());
      alert.showAndWait();
      System.exit(0);
    }

  }


  @FXML
  void CheckCertNoFired(ActionEvent event) throws InterruptedException {
    vBoxItems.getChildren().clear();

    labInfo.setText("开始建立目标表");

    CheckCertNoProcessor.createDescTable();
    labInfo.setText("分区数:" + CHECK_CERTNO_PARTITION_COUNT + " 线程池窗口: " + CHECK_CERTNO_THREAD_POOL_SIZE);


    final Label[] labels = new Label[CHECK_CERTNO_PARTITION_COUNT];
    final ProgressBar[] pbs = new ProgressBar[CHECK_CERTNO_PARTITION_COUNT];
    final HBox hbs[] = new HBox[CHECK_CERTNO_PARTITION_COUNT];

    WapperService wapper = new WapperService();

    ExecutorService th = Executors.newFixedThreadPool(CHECK_CERTNO_THREAD_POOL_SIZE);
    wapper.setEs(th);

    for (int i = 0; i < CHECK_CERTNO_PARTITION_COUNT; i++) {
      String partName = "part_" + String.format("%03d", i + 1);

      CheckCertNoService service = new CheckCertNoService();
      service.setPartName(partName);

      final Label label = labels[i] = new Label();
      label.setPrefWidth(200);
      label.textProperty().bind(service.titleProperty());

      final ProgressBar pb = pbs[i] = new ProgressBar();
      pb.setPrefWidth(300);
      pb.setProgress(0);

      pb.progressProperty().bind(service.progressProperty());

      final HBox hb = hbs[i] = new HBox();
      hb.setSpacing(15);
      hb.setAlignment(Pos.CENTER_LEFT);
      hb.getChildren().addAll(label, pb);

      service.setExecutor(th);
      service.start();
    }

    wapper.start();

    bindButtonState(wapper);

    vBoxItems.getChildren().addAll(hbs);
    th.shutdown();
  }


  @FXML
  void btnMergeFired(ActionEvent event) {
    MergeProcessor.createSourceTable();
    MergeProcessor.createDescTable();

    labInfo.setText("分区数:" + MERGE_THREAD_PARTITION_COUNT + " 线程池窗口: " + MERGE_THREAD_POOL_SIZE);

    vBoxItems.getChildren().clear();

    final Label[] labels = new Label[MERGE_THREAD_PARTITION_COUNT];
    final ProgressBar[] pbs = new ProgressBar[MERGE_THREAD_PARTITION_COUNT];
    final HBox hbs[] = new HBox[MERGE_THREAD_PARTITION_COUNT];

    WapperService wapper = new WapperService();
    ExecutorService th = Executors.newFixedThreadPool(MERGE_THREAD_POOL_SIZE);
    wapper.setEs(th);
    for (int i = 0; i < MERGE_THREAD_PARTITION_COUNT; i++) {
      String partName = "part_" + String.format("%03d", i + 1);

      MergeWorkService service = new MergeWorkService();
      service.setPartName(partName);

      final Label label = labels[i] = new Label();
      label.setPrefWidth(200);
      label.textProperty().bind(service.titleProperty());

      final ProgressBar pb = pbs[i] = new ProgressBar();
      pb.setPrefWidth(300);
      pb.setProgress(0);

      pb.progressProperty().bind(service.progressProperty());

      final HBox hb = hbs[i] = new HBox();
      hb.setSpacing(15);
      hb.setAlignment(Pos.CENTER_LEFT);
      hb.getChildren().addAll(label, pb);

      service.setExecutor(th);
      service.start();
    }

    wapper.start();
    bindButtonState(wapper);

    vBoxItems.getChildren().addAll(hbs);
    th.shutdown();
  }

  @FXML
  void btnExtractResultFired(ActionEvent event) throws InterruptedException {
    try {
      vBoxItems.getChildren().clear();
      ExtractResultService service = new ExtractResultService();

      final Label label = new Label();
      label.setPrefWidth(600);
      label.setWrapText(true);
      label.textProperty().bind(service.titleProperty());

      final ProgressBar pb = new ProgressBar();
      pb.setPrefWidth(300);
      pb.setProgress(0);

      pb.progressProperty().bind(service.progressProperty());

      final VBox vb = new VBox();
      vb.setSpacing(15);
      vb.setAlignment(Pos.CENTER_LEFT);
      vb.getChildren().addAll(label, pb);

      bindButtonState(service);
      service.start();

      vBoxItems.getChildren().addAll(vb);

    } catch (Exception e) {
      logger.severe(e.getMessage());
    }

  }

  /**
   * Called when the SaveIssue button is fired.
   *
   * @param event the action event.
   */
  @FXML
  void btnCheckRegionCodeFired(ActionEvent event) {
    final int totalCount = CheckRegionCodeProcessor.ALL_CODE_COUNT;
    if (totalCount <= 0) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("错误");
      alert.setHeaderText("目标表为空");
      alert.showAndWait();
      return;
    }
    logger.info("totalCount:" + totalCount);

    int threadCount = (int) (totalCount / CHECK_DIC_CODE_THREAD_PROCESS_COUNT);
    if ((totalCount % CHECK_DIC_CODE_THREAD_PROCESS_COUNT) != 0)
      threadCount++;

    final int THREAD_COUNT = threadCount;
    labInfo.setText("待验证区划代码 总量:" + totalCount + " 线程数:" + THREAD_COUNT + " 每页:" + CHECK_DIC_CODE_THREAD_PROCESS_COUNT);

    vBoxItems.getChildren().clear();

    final Label[] labels = new Label[THREAD_COUNT];
    final ProgressBar[] pbs = new ProgressBar[THREAD_COUNT];
    final HBox hbs[] = new HBox[THREAD_COUNT];

    WapperService wapper = new WapperService();
    ExecutorService th = Executors.newFixedThreadPool(CHECK_DIC_CODE_THREAD_POOL_SIZE);
    wapper.setEs(th);

    for (int i = 0; i < THREAD_COUNT; i++) {
      Integer indexEnd = java.lang.Integer.min((i + 1) * CHECK_DIC_CODE_THREAD_PROCESS_COUNT, totalCount);
      Integer indexStart = i * CHECK_DIC_CODE_THREAD_PROCESS_COUNT;

      CheckRegionCodeService service = new CheckRegionCodeService();
      service.setIndexStart(indexStart);
      service.setIndexEnd(indexEnd);

      final Label label = labels[i] = new Label();
      label.setPrefWidth(200);
      label.setText("第" + i + "页:" + indexStart + "~" + indexEnd);

      final ProgressBar pb = pbs[i] = new ProgressBar();
      pb.setPrefWidth(300);
      pb.setProgress(0);

      pb.progressProperty().bind(service.progressProperty());

      final HBox hb = hbs[i] = new HBox();
      hb.setSpacing(15);
      hb.setAlignment(Pos.CENTER_LEFT);
      hb.getChildren().addAll(label, pb);

      service.setExecutor(th);
      service.start();
    }


    wapper.start();
    bindButtonState(wapper);

    vBoxItems.getChildren().addAll(hbs);
    th.shutdown();
  }

  private void bindButtonState(Service<Integer> service) {
    btnCheckCertNo.disableProperty().bind(service.runningProperty());
    btnMerge.disableProperty().bind(service.runningProperty());
    btnExtractResultCode.disableProperty().bind(service.runningProperty());
    btnCheckRegionCode.disableProperty().bind(service.runningProperty());

    labRunning.textProperty().bind(service.runningProperty().asString());
  }

}
