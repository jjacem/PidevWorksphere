package esprit.tn.entities;

public class Meetings {
    private int id_meeting;
    private String room_name;
    private String meeting_url;
    private int reservation_id; // Ajout de l'ID de la réservation

    public Meetings() {}

    public Meetings(int id_meeting, String room_name, String meeting_url, int reservation_id) {
        this.id_meeting = id_meeting;
        this.room_name = room_name;
        this.meeting_url = meeting_url;
        this.reservation_id = reservation_id;
    }

    public int getId_meeting() {
        return id_meeting;
    }

    public void setId_meeting(int id_meeting) {
        this.id_meeting = id_meeting;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getMeeting_url() {
        return meeting_url;
    }

    public void setMeeting_url(String meeting_url) {
        this.meeting_url = meeting_url;
    }

    public int getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(int reservation_id) {
        this.reservation_id = reservation_id;
    }
}
