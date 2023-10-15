package com.example.gui_chat12566;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HelloController {
    @FXML
    private TextField textField;
    @FXML
    private TextArea textArea;
    DataOutputStream out;

    @FXML
    private void onSendHandler(){
        String msg = textField.getText();
        textArea.appendText(msg+"\n");
        textField.clear();
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            System.out.println("Потеряно соединение с сервером");
        }
    }

    @FXML
    private void connect(){
        try {
            Socket socket = new Socket("127.0.0.1", 9123);
            out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true){
                            String response = in.readUTF();
                            textArea.appendText(response+"\n");
                        }
                    }catch (IOException e){
                        textArea.appendText("Потеряно соединение с сервером\n");
                    }
                }
            });
            thread.start();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}