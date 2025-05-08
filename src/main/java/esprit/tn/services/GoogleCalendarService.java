package esprit.tn.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import esprit.tn.entities.Entretien;
import esprit.tn.entities.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH ="tokens";
//            "tokens";

    private final ServiceUser userService = new ServiceUser();


    private static final List<String> SCOPES = Arrays.asList(
            CalendarScopes.CALENDAR_EVENTS,
            CalendarScopes.CALENDAR
    );

    private static final String CREDENTIALS_FILE_PATH =
            "C:\\Users\\yassi\\Downloads\\credentials.json";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        File in = new File(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(in)));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }




//    public void ajouterEntretienDansGoogleCalendar(Entretien entretien) throws Exception {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//
//        String dateStr = dateFormat.format(entretien.getDate_entretien());
//        String timeStr = timeFormat.format(entretien.getHeure_entretien());
//
//        String startDateTimeStr = dateStr + "T" + timeStr;
//        String endDateTimeStr = dateStr + "T" + timeStr;
//
//        Event event = new Event()
//                .setSummary(entretien.getTitre())
//                .setDescription(entretien.getDescription());
//
//        DateTime startDateTime = new DateTime(startDateTimeStr);
//        EventDateTime start = new EventDateTime()
//                .setDateTime(startDateTime)
//                .setTimeZone("Africa/Tunis");
//        event.setStart(start);
//
//        DateTime endDateTime = new DateTime(endDateTimeStr);
//        EventDateTime end = new EventDateTime()
//                .setDateTime(endDateTime)
//                .setTimeZone("Africa/Tunis");
//        event.setEnd(end);
//
//        String calendarId = "hbaieb.houssem999@gmail.com";
//        service.events().insert(calendarId, event).execute();
//    }





    public void ajouterEntretienDansGoogleCalendar(Entretien entretien) throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(entretien.getDate_entretien());
        String timeStr = timeFormat.format(entretien.getHeure_entretien());

        String startDateTimeStr = dateStr + "T" + timeStr;
        String endDateTimeStr = dateStr + "T" + timeStr;

        String type = String.valueOf(entretien.getType_entretien());

        Event event = new Event()
                .setSummary(entretien.getTitre())
                .setDescription(entretien.getDescription())
                .setLocation(type)
                .setColorId("5");

        DateTime startDateTime = new DateTime(startDateTimeStr);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Africa/Tunis");
        event.setStart(start);

        DateTime endDateTime = new DateTime(endDateTimeStr);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Africa/Tunis");
        event.setEnd(end);

//        User candidat = userService.findbyid(entretien.getCandidatId());
//        EventAttendee[] attendees = new EventAttendee[]{
//                new EventAttendee().setEmail(candidat.getEmail()),
//        };
//        event.setAttendees(Arrays.asList(attendees));
//
//        EventReminder[] reminderOverrides = new EventReminder[]{
//                new EventReminder().setMethod("email").setMinutes(24 * 60),
//                new EventReminder().setMethod("popup").setMinutes(30)
//        };
//        Event.Reminders reminders = new Event.Reminders()
//                .setUseDefault(false)
//                .setOverrides(Arrays.asList(reminderOverrides));
//        event.setReminders(reminders);

        String calendarId = "hbaieb.houssem999@gmail.com";
        service.events().insert(calendarId, event).execute();
    }






















}