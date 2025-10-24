package consultation.server.protocol;

import hepl.fead.model.dao.DAOFactory;
import java.io.Serializable;

public interface Requete extends Serializable {
    ReponseTraitee traite(DAOFactory daoFactory) throws Exception;
    default boolean isLogout() {
        return false;
    }
}
