package it.unito.edu.scavolini.reservation;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootApplication
public class ReservationApplication {



    public static void main(String[] args) {
        startFirebase();
        SpringApplication.run(ReservationApplication.class, args);
    }

    private static void startFirebase(){
        try {
            FileInputStream serviceAccount =
                new FileInputStream("C:\\Users\\Gianl\\Desktop\\UniTo\\taas\\firebase\\serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            System.out.println("Exception while logging with firebase");
            e.printStackTrace();
        }
    }






}
