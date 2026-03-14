package com.example.goutamjarvis;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    TextToSpeech tts;
    Button btnSpeak;
    ImageView jarvisCircle;

    EditText etCommand;
    Button btnSend;

    CameraManager cameraManager;
    String cameraId;

    Map<String, String> predefinedApps = new HashMap<>();
    Map<String, String> predefinedWebsites = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSpeak = findViewById(R.id.btnSpeak);
        jarvisCircle = findViewById(R.id.jarvisCircle);

        etCommand = findViewById(R.id.etCommand);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> {
            String typedCommand = etCommand.getText().toString().toLowerCase().trim();
            if (!typedCommand.isEmpty()) {
                processCommand(typedCommand);
                etCommand.setText("");
            } else speak("Please type a command first.");
        });

        checkPermissions();
        initPredefinedApps();
        initPredefinedWebsites();

        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                int result = tts.setLanguage(new Locale("en", "IN"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.US);
                }
                tts.setSpeechRate(0.9f);
                tts.setPitch(1.0f);
                speak(getTimeBasedGreeting());
            }
        });

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try { cameraId = cameraManager.getCameraIdList()[0]; } catch (Exception e) { e.printStackTrace(); }

        btnSpeak.setOnClickListener(v -> startListening());
    }

    private void initPredefinedApps() {
        // ---------- Messaging & Social Media ----------
        predefinedApps.put("whatsapp", "com.whatsapp");
        predefinedApps.put("whatsapp business", "com.whatsapp.w4b");
        predefinedApps.put("telegram", "org.telegram.messenger");
        predefinedApps.put("messenger", "com.facebook.orca");
        predefinedApps.put("facebook", "com.facebook.katana");
        predefinedApps.put("facebook lite", "com.facebook.lite");
        predefinedApps.put("instagram", "com.instagram.android");
        predefinedApps.put("linkedin", "com.linkedin.android");
        predefinedApps.put("twitter", "com.twitter.android");
        predefinedApps.put("snapchat", "com.snapchat.android");
        predefinedApps.put("tiktok", "com.zhiliaoapp.musically");
        predefinedApps.put("discord", "com.discord");
        predefinedApps.put("reddit", "com.reddit.frontpage");

        // ---------- Browsers ----------
        predefinedApps.put("chrome", "com.android.chrome");
        predefinedApps.put("firefox", "org.mozilla.firefox");
        predefinedApps.put("opera", "com.opera.browser");
        predefinedApps.put("brave", "com.brave.browser");

        // ---------- Video & Streaming ----------
        predefinedApps.put("youtube", "com.google.android.youtube");
        predefinedApps.put("netflix", "com.netflix.mediaclient");
        predefinedApps.put("prime video", "com.amazon.avod.thirdpartyclient");
        predefinedApps.put("hotstar", "in.startv.hotstar");
        predefinedApps.put("sony liv", "com.sonyliv");
        predefinedApps.put("zee5", "com.graymatrix.did");

        // ---------- Payment & Finance ----------
        predefinedApps.put("paytm", "net.one97.paytm");
        predefinedApps.put("phonepe", "com.phonepe.app");
        predefinedApps.put("google pay", "com.google.android.apps.nbu.paisa.user");
        predefinedApps.put("sbi", "in.org.npci.upiapp");
        predefinedApps.put("icici bank", "com.icici.bank.imobile");
        predefinedApps.put("hdfc bank", "com.hdfc.android.mbanking");
        predefinedApps.put("axis bank", "com.axis.mobank");
        predefinedApps.put("kotak bank", "com.kotak.mb");
        predefinedApps.put("rbi", "in.org.rbi.app");

        // ---------- Shopping & E-Commerce ----------
        predefinedApps.put("amazon", "in.amazon.mShop.android.shopping");
        predefinedApps.put("flipkart", "com.flipkart.android");
        predefinedApps.put("myntra", "com.myntra.android");
        predefinedApps.put("snapdeal", "com.snapdeal.main");
        predefinedApps.put("ajio", "com.ril.ajio");
        predefinedApps.put("aliexpress", "com.alibaba.aliexpresshd");
        predefinedApps.put("shopclues", "com.shopclues");
        predefinedApps.put("bigbasket", "com.bigbasket.mobileapp");
        predefinedApps.put("pepperfry", "com.pepperfry.consumer");

        // ---------- Food & Delivery ----------
        predefinedApps.put("swiggy", "in.swiggy.android");
        predefinedApps.put("zomato", "com.zomato.android");
        predefinedApps.put("dominos", "com.dominos");
        predefinedApps.put("ubereats", "com.ubercab.eats");

        // ---------- Productivity ----------
        predefinedApps.put("gmail", "com.google.android.gm");
        predefinedApps.put("google drive", "com.google.android.apps.docs");
        predefinedApps.put("google docs", "com.google.android.apps.docs.editors.docs");
        predefinedApps.put("google sheets", "com.google.android.apps.docs.editors.sheets");
        predefinedApps.put("google slides", "com.google.android.apps.docs.editors.slides");
        predefinedApps.put("keep notes", "com.google.android.keep");
        predefinedApps.put("evernote", "com.evernote");

        // ---------- Utilities ----------
        predefinedApps.put("photos", "com.google.android.apps.photos");
        predefinedApps.put("gallery", "com.sec.android.gallery3d");
        predefinedApps.put("maps", "com.google.android.apps.maps");
        predefinedApps.put("calculator", "com.android.calculator2");
        predefinedApps.put("clock", "com.android.deskclock");
        predefinedApps.put("weather", "com.accuweather.android");
        predefinedApps.put("file manager", "com.google.android.apps.nbu.files");

        // ---------- Education & Learning ----------
        predefinedApps.put("geeksforgeeks", "org.geeksforgeeks");
        predefinedApps.put("javatpoint", "com.javatpoint");
        predefinedApps.put("tutorialspoint", "com.tutorialspoint.android");
        predefinedApps.put("w3schools", "com.w3schools.android");
        predefinedApps.put("khan academy", "org.khanacademy.android");
        predefinedApps.put("coursera", "org.coursera.android");
        predefinedApps.put("udemy", "com.udemy.android");
        predefinedApps.put("edx", "org.edx.mobile");
        predefinedApps.put("coding ninjas", "com.codingninjas");

        // ---------- Travel & Transport ----------
        predefinedApps.put("irctc rail", "in.gov.irctc");
        predefinedApps.put("redbus", "com.redbus.android");
        predefinedApps.put("ola cabs", "com.olacabs.customer");
        predefinedApps.put("uber", "com.ubercab");

        // ---------- Music & Audio ----------
        predefinedApps.put("spotify", "com.spotify.music");
        predefinedApps.put("soundcloud", "com.soundcloud.android");
        predefinedApps.put("gaana", "com.gaana");
        predefinedApps.put("jio saavn", "com.jio.media.jiobeats");

        // ---------- News ----------
        predefinedApps.put("times of india", "com.toi.reader.activities");
        predefinedApps.put("hindustan times", "com.ht.digital");
        predefinedApps.put("ndtv", "com.ndtv.android");
        predefinedApps.put("bbc news", "bbc.mobile.news");
        predefinedApps.put("cnn", "com.cnn.mobile.android.phone");

        // ---------- Gaming ----------
        predefinedApps.put("pubg mobile", "com.tencent.ig");
        predefinedApps.put("call of duty mobile", "com.activision.callofduty.shooter");
        predefinedApps.put("free fire", "com.dts.freefireth");
        predefinedApps.put("free fire max", "com.dts.freefiremax");
        predefinedApps.put("clash of clans", "com.supercell.clashofclans");
        predefinedApps.put("clash royale", "com.supercell.clashroyale");
        predefinedApps.put("minecraft", "com.mojang.minecraftpe");
        predefinedApps.put("among us", "com.innersloth.spacemafia");
        predefinedApps.put("subway surfers", "com.kiloo.subwaysurf");
        predefinedApps.put("temple run", "com.imangi.templerun");
        predefinedApps.put("pokemon go", "com.nianticlabs.pokemongo");
        predefinedApps.put("roblox", "com.roblox.client");
        predefinedApps.put("valorant mobile", "com.riotgames.valorant");
        predefinedApps.put("gta san andreas", "com.rockstargames.gtasa");

        // ---------- Google App ----------
        predefinedApps.put("google", "com.google.android.googlequicksearchbox");
    }

    private void initPredefinedWebsites() {
        predefinedWebsites.put("instagram", "https://www.instagram.com");
        predefinedWebsites.put("facebook", "https://www.facebook.com");
        predefinedWebsites.put("twitter", "https://www.twitter.com");
        predefinedWebsites.put("linkedin", "https://www.linkedin.com");
        predefinedWebsites.put("youtube", "https://www.youtube.com");
        predefinedWebsites.put("snapchat", "https://www.snapchat.com");
        predefinedWebsites.put("telegram", "https://web.telegram.org");
        predefinedWebsites.put("whatsapp web", "https://web.whatsapp.com");
        predefinedWebsites.put("reddit", "https://www.reddit.com");
        predefinedWebsites.put("pinterest", "https://www.pinterest.com");
        predefinedWebsites.put("tiktok", "https://www.tiktok.com");
        predefinedWebsites.put("discord", "https://discord.com");
        predefinedWebsites.put("tumblr", "https://www.tumblr.com");
        predefinedWebsites.put("google", "https://www.google.com");
        predefinedWebsites.put("bing", "https://www.bing.com");
        predefinedWebsites.put("yahoo", "https://www.yahoo.com");
        predefinedWebsites.put("duckduckgo", "https://duckduckgo.com");
        predefinedWebsites.put("chatgpt", "https://chatgpt.com");
        predefinedWebsites.put("amazon", "https://www.amazon.in");
        predefinedWebsites.put("flipkart", "https://www.flipkart.com");
        predefinedWebsites.put("ebay", "https://www.ebay.com");
        predefinedWebsites.put("snapdeal", "https://www.snapdeal.com");
        predefinedWebsites.put("myntra", "https://www.myntra.com");
        predefinedWebsites.put("ajio", "https://www.ajio.com");
        predefinedWebsites.put("aliexpress", "https://www.aliexpress.com");
        predefinedWebsites.put("shopclues", "https://www.shopclues.com");
        predefinedWebsites.put("bigbasket", "https://www.bigbasket.com");
        predefinedWebsites.put("pepperfry", "https://www.pepperfry.com");
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try { startActivityForResult(intent, 100); }
        catch (Exception e) { Toast.makeText(this, "Speech not supported", Toast.LENGTH_SHORT).show(); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String command = result.get(0).toLowerCase();
            if (command.contains("hey jarvis")) {
                speak("Yes Sir.");
                startListening();
                return;
            }
            processCommand(command);
        }
    }

    private void processCommand(String command) {
        if (command.startsWith("call ")) {
            directCall(command.replace("call ", "").trim());
        } else if (command.startsWith("open ")) {
            String target = command.replace("open ", "").trim();
            if (target.endsWith(" app")) target = target.replace(" app", "").trim();
            if (target.endsWith(" website")) target = target.replace(" website", "").trim();
            openAppOrWebsite(target.toLowerCase());
        } else if (command.contains("turn on flashlight")) {
            try { cameraManager.setTorchMode(cameraId, true); speak("Flashlight turned on"); } catch (Exception e) {}
        } else if (command.contains("turn off flashlight")) {
            try { cameraManager.setTorchMode(cameraId, false); speak("Flashlight turned off"); } catch (Exception e) {}
        } else if (command.contains("open camera")) {
            startActivity(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            speak("Opening camera");
        } else if (command.contains("set alarm")) {
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR, 7);
            intent.putExtra(AlarmClock.EXTRA_MINUTES, 0);
            startActivity(intent);
            speak("Alarm set for 7 AM");
        } else speak("Sorry Sir, I did not understand.");
    }

    private void directCall(String spokenName) {
        String name = spokenName.toLowerCase().replaceAll("\\s+", "");
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String bestMatchNumber = null;
            int minDistance = Integer.MAX_VALUE;

            do {
                @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contactNormalized = contactName.toLowerCase().replaceAll("\\s+", "");
                int distance = levenshteinDistance(name, contactNormalized);

                if (distance < minDistance) {
                    minDistance = distance;
                    bestMatchNumber = contactNumber;
                }
            } while (cursor.moveToNext());
            cursor.close();

            if (bestMatchNumber != null && minDistance <= 2) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + bestMatchNumber));
                startActivity(intent);
                speak("Calling " + spokenName);
            } else speak("Contact not found.");
        } else speak("No contacts found.");
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) dp[i][j] = dp[i-1][j-1];
                else dp[i][j] = 1 + Math.min(dp[i-1][j-1], Math.min(dp[i-1][j], dp[i][j-1]));
            }
        }
        return dp[a.length()][b.length()];
    }

    private void openAppOrWebsite(String target) {
        target = target.toLowerCase();

        if (predefinedApps.containsKey(target)) {
            String packageName = predefinedApps.get(target);
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
                speak("Opening " + target + " app");
            } else openWebsite(target);
            return;
        }

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo app : apps) {
            String label = pm.getApplicationLabel(app).toString().toLowerCase();
            if (label.contains(target)) {
                Intent launchIntent = pm.getLaunchIntentForPackage(app.packageName);
                if (launchIntent != null) {
                    startActivity(launchIntent);
                    speak("Opening " + target + " app");
                    return;
                }
            }
        }
        openWebsite(target);
    }

    private void openWebsite(String name) {
        String url = predefinedWebsites.getOrDefault(name, "https://www." + name + ".com");
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            speak("Opening " + name + " website");
        } catch (Exception e) { speak("Unable to open " + name); }
    }

    private void speak(String text) { tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null); }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE
        };
        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    private String getTimeBasedGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) return "Good Morning Sir. Jarvis is ready to assist you.";
        else if (hour >= 12 && hour < 17) return "Good Afternoon Sir. How may I help you today?";
        else if (hour >= 17 && hour < 21) return "Good Evening Sir. What would you like to do?";
        else return "Hello Sir. Working late tonight? Jarvis is online.";
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}