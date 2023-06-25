package com.jee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-11-26 22:07
 **/
@SpringBootApplication
public class MyApplication extends Application {

    private static final String fxmlClassPath = "com/jee/jfx/views/window.fxml";
    // 任何地方都可以通过这个applicationContext获取springboot的上下文
    public static ConfigurableApplicationContext context;
    private static String[] args;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // 加载FXML文件
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlClassPath));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();

        // 创建一个场景
        Scene scene = new Scene(root, 700, 300);

        // 设置场景到舞台
        primaryStage.setScene(scene);

        // 设置舞台标题并显示
        primaryStage.setTitle("WebVideoDownloader");
        primaryStage.show();
    }


    public static void main(String[] args) {
        MyApplication.args = args;
        launch(args);
    }

    @Override
    public void init() throws Exception {
        // 启动springboot
        context = SpringApplication.run(MyApplication.class, args);
    }

    @Override
    public void stop() throws Exception {
        // 关闭springboot
        context.stop();
    }
}
