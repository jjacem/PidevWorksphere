package esprit.tn.services;

public class JitsiMeetService {
    private static final String JITSI_MEET_URL = "https://meet.jit.si/";

    /**
     * Génère une URL de réunion avec paramètres personnalisés.
     * @param roomName Nom de la salle
     * @param displayName Nom affiché dans la réunion
     * @param startMuted Si le micro doit être coupé au départ
     * @return URL complète de la réunion Jitsi
     */
    public String generateMeetingUrl(String roomName, String displayName, boolean startMuted) {
        return JITSI_MEET_URL + roomName +
                "#userInfo.displayName=" + displayName.replace(" ", "%20") +
                "&config.startWithAudioMuted=" + startMuted;
    }
}
