package consultation.server.protocol;

import hepl.fead.model.dao.DAOFactory;

public class CAPProtocol {
    private final DAOFactory daoFactory;
    public CAPProtocol(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    public ReponseTraitee traiter(Requete requete) {
        try {
            return requete.traite(daoFactory);
        } catch (Exception e) {
            return new ReponseTraitee(false, e.getMessage(), null);
        }
    }
}
