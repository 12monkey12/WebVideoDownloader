package com.jee.jfx.views;

import com.jee.download.Downloader;
import com.jee.download.DownloaderFactory;
import com.jee.jfx.ChoiceBoxConverter;
import com.jee.po.BiliBiliVideoInfo;
import com.jee.po.VideoInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @program: WebVideoDownloader
 * @description: fxml控制器类
 * @author: animal
 * @create: 2023-06-16 19:01
 **/
@Component
@Slf4j
public class WindowController implements Initializable {

    @FXML
    private ChoiceBox<String> sourceCBox;
    @FXML
    private Label logNameLabel;
    @FXML
    private TextArea urlInput;
    @FXML
    private Button loadBut;
    @FXML
    private Label viewNameLabel;
    @FXML
    private ChoiceBox<Pair<Integer, String>> articulationCBox;
    @FXML
    private Button downloadBut;

    @Autowired
    private DownloaderFactory downloaderFactory;
    private Downloader downloader;
    private VideoInfo videoInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logNameLabel.setText("未登录");
        viewNameLabel.setText("");

        sourceCBox.getItems().addAll(new String[]{"bilibili"});
        sourceCBox.setValue("bilibili");
        urlInput.setText("https://www.bilibili.com/video/BV1zu4y1o7k7/?spm_id_from=333.1007.tianma.2-3-6.click&vd_source=3c1de7750ea47eddcc8ad95c1f2a8ca9");
    }

    @FXML
    public void handleLoadButtonAction(ActionEvent event){
        String type = sourceCBox.getValue();
        downloader = downloaderFactory.createDownloader(type);

        String url = urlInput.getText();
        try {
            videoInfo = downloader.load(url);

            BiliBiliVideoInfo biliBiliVideoInfo = (BiliBiliVideoInfo) videoInfo;
            viewNameLabel.setText(biliBiliVideoInfo.getTitle());
            List<Pair<Integer, String>> resolutions = biliBiliVideoInfo.getResolutions();
            articulationCBox.getItems().addAll(resolutions);
            articulationCBox.setConverter(new ChoiceBoxConverter());
            articulationCBox.setValue(resolutions.get(0));

            downloadBut.setDisable(false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("通知");
            alert.setHeaderText("false!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleDownloadButtonAction(){
        try {
            downloader.download(videoInfo);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("通知");
            alert.setHeaderText("header");
            alert.setContentText("sucess!");
            alert.showAndWait();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("通知");
            alert.setHeaderText("false!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

}
