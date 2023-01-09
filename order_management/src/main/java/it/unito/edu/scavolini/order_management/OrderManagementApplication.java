package it.unito.edu.scavolini.order_management;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;

@SpringBootApplication
public class OrderManagementApplication {

    public static void main(String[] args) {
        startFirebase();
        SpringApplication.run(OrderManagementApplication.class, args);
    }

    private static void startFirebase(){
        try {
            // if running with docker the path must be "serviceAccountKey.json"
           FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");

            // if running with IntelliJ the path must be "order_management/src/main/resources/serviceAccountKey.json"
            //FileInputStream serviceAccount =
            //    new FileInputStream("order_management/src/main/resources/serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            System.out.println("|||||||||||||||||||| Exception while logging with firebase ||||||||||||||||||||");
            e.printStackTrace();
        }
        System.out.println("OOOOOOOOOOOOOOOOOOOO Firebase start end OOOOOOOOOOOOOOOOOOOO");
    }

}
