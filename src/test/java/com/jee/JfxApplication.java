package com.jee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-12-11 16:43
 **/
public class JfxApplication extends Application {

    private String fxmlClassPath = "com/jee/jfx/views/window.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {

        // 加载FXML文件
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(fxmlClassPath));

        // 创建一个场景
        Scene scene = new Scene(root, 700, 300);

        // 设置场景到舞台
        primaryStage.setScene(scene);

        // 设置舞台标题并显示
        primaryStage.setTitle("WebVideoDownloader");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
