package consultation.server.protocol;

import hepl.fead.model.dao.DAOFactory;

public class RequeteLogout implements Requete {
    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) {
        return new ReponseTraitee(true, "Déconnexion", null);
    }
    @Override
    public boolean isLogout() {
        return true;
    }
}
