package com.dari.mytest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private long countdownTimeMillis;
    private CountDownTimer countDownTimer;
    TextView textView, time, textView4;
    CalendarView calendarView;
    Calendar calendar;
    String selectedDate;

    String nowTime;
    Date now;
    DateFormat df;
    Period period;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        time = findViewById(R.id.textView2);
        textView = findViewById(R.id.textView1);

        textView4 = findViewById(R.id.textView4);

        calendarView = findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();

        now = new Date();
        df = new SimpleDateFormat("dd/MM/yyyy");
        nowTime = df.format(now);
        long kalanSure = getRemainingTime();

        displayTime(kalanSure);



        getDate();



    }

    @Override
    protected void onPause(){
        super.onPause();
        saveRemainingTime(countdownTimeMillis);
    }
    @Override
    protected void onResume(){
        super.onResume();

        long kalanSureMillis = getRemainingTime();

        if (kalanSureMillis > 0) {
            startCountdown(kalanSureMillis);
        }
    }


    private long getRemainingTime(){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getLong("kalan_sure", 0);
    }

    private void displayTime(long kalanSure){
        if (kalanSure>0){

            long kalanGun = kalanSure / (1000 * 60 * 60 * 24);
            long kalanSaat = (kalanSure % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long kalanDakika = (kalanSure % (1000 * 60 * 60)) / (1000 * 60);
            long kalanSaniye = (kalanSure % (1000 * 60)) / 1000;

            String kalanSureMetni = String.format(Locale.getDefault(), "Kalan Süre: %d gün, %d saat, %d dakika, %d saniye",
                    kalanGun, kalanSaat, kalanDakika, kalanSaniye);


            textView4.setText(kalanSureMetni);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save(View view){

        long kalanSureMillis = getRemainingTime();
        getDate();
        updateCountdownTime();
        startCountdown(kalanSureMillis);

    }

    public void getDate(){

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                boolean singleDigitDay = day<10;
                boolean singleDigitMonth = (month+1)<10;

                String dayString = singleDigitDay ? "0" +day : String.valueOf(day);
                String monthString = singleDigitMonth ? "0" + (month + 1) : String.valueOf(month+1);



                selectedDate = dayString + "/" + monthString + "/" + year;

                LocalDate t1 = parseToLocalDate(nowTime,"dd/MM/yyyy");
                LocalDate t2 = parseToLocalDate(selectedDate,"d/M/yyyy");

                period = t1.until(t2);

                long gunFarki = period.getDays();
                long ayFarki = period.toTotalMonths();
                long yilFarki = period.getYears();


                textView.setText("gunFarki : " + gunFarki+ " Ay Farki : " + ayFarki + " Yıl Farki" + yilFarki);



            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateCountdownTime() {
        LocalDate t1 = parseToLocalDate(nowTime, "dd/MM/yyyy");
        LocalDate t2 = parseToLocalDate(selectedDate, "dd/MM/yyyy");


       // LocalDateTime dateTime1 = LocalDateTime.of(t1, LocalTime.MIDNIGHT);
       // LocalDateTime dateTime2 = LocalDateTime.of(t2, LocalTime.MIDNIGHT);

       // Period period = Period.between(dateTime1.toLocalDate(), dateTime2.toLocalDate());

        // Geri sayım süresini milisaniye cinsinden hesapla
        long daysDifference = ChronoUnit.DAYS.between(t1,t2);


        countdownTimeMillis = daysDifference * 24 * 60 * 60 * 1000;


    }

    private void startCountdown(long remainingTime) {
        if (countdownTimeMillis>0) {
            if (countDownTimer != null){
                countDownTimer.cancel();
            }

            countDownTimer = new CountDownTimer(countdownTimeMillis,1000) {
                @Override
                public void onTick(long l) {

                    long remainingDays = l /(1000* 60 * 60 * 24);
                    long remainingHours = (l % (1000 * 60 *60 *24)) /(1000 * 60* 60);
                    long remainingMinutes = (l % (1000 * 60 * 60)) / (1000 * 60);
                    long remainingSeconds = (l % (1000 * 60)) / 1000;

                    String countDownText = String.format(Locale.getDefault() , "Kalan Süre : %d gün, %d saat , %d dakika , %d saniye",
                            remainingDays , remainingHours , remainingMinutes , remainingSeconds);

                    time.setText("sss :  "  + countDownText);

                    saveRemainingTime(l);

                    displayTime(l);
                }

                @Override
                public void onFinish() {

                    saveRemainingTime(0);
                    textView.setText("Geri Sayım Tamamlandı");
                }
            }.start();
        }
    }


    private void saveRemainingTime(long remainingTime){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("kalan_sure" , remainingTime);
        editor.apply();

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private static LocalDate parseToLocalDate(String dateString, String pattern){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateString,formatter);
    }




}