package consultation.server.protocol;

import hepl.fead.model.dao.DAOFactory;
import hepl.fead.model.dao.DoctorDAO;
import hepl.fead.model.entity.Doctor;


public class RequeteLogin implements Requete {
    private final String login;
    private final String password;
    public RequeteLogin(String login, String password) {
        this.login = login;
        this.password = password;
    }
    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
        DoctorDAO doctorDAO = daoFactory.getDoctorDAO();
        Doctor doctor = doctorDAO.login(login, password);
        if (doctor != null) {
            return new ReponseTraitee(true, "Connexion réussie", doctor);
        }
        return new ReponseTraitee(false, "Identifiants invalides", null);
    }
}
