package com.example.gui_chat12566;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HelloController {
    @FXML
    private TextField textField;
    @FXML
    private TextArea textArea;
    @FXML
    private VBox userList;
    DataOutputStream out;
    @FXML
    private Label chatName;
    private int toId = 0;

    @FXML
    private void onSendHandler(){
        JSONObject jsonObject = new JSONObject();
        String msg = textField.getText();
        textArea.appendText(msg+"\n");
        textField.clear();
        jsonObject.put("message", msg);
        jsonObject.put("toId", toId);
        try {
            out.writeUTF(jsonObject.toJSONString());
        } catch (IOException e) {
            System.out.println("Потеряно соединение с сервером");
        }
    }

    @FXML
    public void connect(){
        try {
            Socket socket = new Socket("127.0.0.1", 9123);
            out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true){
                            String response = in.readUTF(); // ожидаем сообщение от сервера (оно будет в формате JSOB)
                            JSONParser jsonParser = new JSONParser(); // Парсер (анализатор json)
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(response); // Превращаем полученный от сервера JSON в JSONObject
                            // Проверяем, есть ли тут ключ "onlineUsers"
                                Platform.runLater(new Runnable() { // Отложенный поток, для работы с UI (User interface)
                                    @Override
                                    public void run() {
                                        if(jsonObject.containsKey("onlineUsers")){
                                            // jsonObject = {"onlineUsers": [{"id": 1, "name": "Ivan"},{"id": 2, "name": "Oleg"},{"id": 3, "name": "Igor"}]}
                                            userList.getChildren().removeAll(); // Удаляем элементы из VBOX (слева)
                                            JSONArray jsonArray = (JSONArray) jsonObject.get("onlineUsers"); // Получаем массив со списком пользователей
                                            // jsonArray = [{"id": 1, "name": "Ivan"},{"id": 2, "name": "Oleg"},{"id": 3, "name": "Igor"}]
                                            jsonArray.forEach(user->{ // Перебираем массив пользователей (тут с помощью лямбда)
                                                JSONObject jsonUser = (JSONObject) user; // jsonUser = {"id": 1, "name": "Ivan"}
                                                String name = jsonUser.get("name").toString();
                                                Button userBtn = new Button(); // Создаём кнопку
                                                userBtn.setText(name); // Добавляем текст на кнопку
                                                userBtn.setPrefWidth(200); // Устанавливаем ширину кнопки 200px
                                                userBtn.setOnAction(event -> {
                                                    textArea.clear();
                                                    chatName.setText("Приватная переписка с "+name);
                                                    toId = Integer.parseInt(jsonUser.get("id").toString());
                                                });
                                                userList.getChildren().add(userBtn); // Добавляем кнопку на VBOX
                                        });
                                        }else{
                                            String msg = jsonObject.get("message").toString(); // Получаем текст из JSOBObject по ключу "message"
                                            textArea.appendText(msg+"\n"); // Печатаем текст на экране приложения в textArea
                                        }
                                    }
                                });
                        }
                    }catch (IOException e){
                        textArea.appendText("Потеряно соединение с сервером\n");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}