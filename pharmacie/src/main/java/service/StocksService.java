package service;
import model.Medicament;
import java.util.stream.Collectors;
import java.util.List;

public class StocksService {

    public void ajouterMedicament(Medicament m) {
        // validation + DAO
    }

    public List<Medicament> medicamentsExpirés(List<Medicament> list) {
        return list.stream()
                .filter(m -> m.getDateExpiration().isBefore(java.time.LocalDate.now()))
                .collect(java.util.stream.Collectors.toList());
    }
}
