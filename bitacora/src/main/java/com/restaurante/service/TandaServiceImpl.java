package com.restaurante.service;

import com.restaurante.exception.NoEsRollException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.exception.TandaExcedidaException;
import com.restaurante.mapper.TandaDocumentMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.TandaResponseDTO;
import com.restaurante.persistence.document.TandaDocument;
import com.restaurante.repository.TandaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TandaServiceImpl implements TandaService {

    private static final int MAX_ROLLS = 6;
    private static final String CATEGORIA_ROLL = "ROLL";

    private final TandaRepository     tandaRepository;    // ← Mongo en vez de ArrayList
    private final TandaDocumentMapper documentMapper;
    private final PlatoService        platoService;

    @Override
    public TandaResponseDTO crear(List<Long> idsRolls) {
        // ─── Validación 1: al menos 1 roll ──────────────────────────
        if (idsRolls == null || idsRolls.isEmpty()) {
            throw new IllegalArgumentException(
                    "La tanda debe tener al menos 1 roll");
        }

        // ─── Validación 2: máximo 6 rolls (regla de Sakura) ─────────
        if (idsRolls.size() > MAX_ROLLS) {
            log.warn("Tanda excedida: {} rolls (máx {})", idsRolls.size(), MAX_ROLLS);
            throw new TandaExcedidaException(
                    "Una tanda de Sakura Sushi no puede tener más de "
                    + MAX_ROLLS + " rolls. Recibidos: " + idsRolls.size());
        }

        // ─── Validación 3: cada id debe existir ─────────────────────
        List<Plato> rolls = new ArrayList<>();
        for (Long idPlato : idsRolls) {
            Plato plato = platoService.obtenerPorId(idPlato);   // lanza 404 si no existe
            rolls.add(plato);
        }

        // ─── Validación 4: todos deben ser categoría ROLL ───────────
        boolean hayNoRoll = rolls.stream()
                .anyMatch(p -> !CATEGORIA_ROLL.equalsIgnoreCase(p.getCategoria()));

        if (hayNoRoll) {
            List<String> noRolls = rolls.stream()
                    .filter(p -> !CATEGORIA_ROLL.equalsIgnoreCase(p.getCategoria()))
                    .map(p -> p.getNombre() + " (" + p.getCategoria() + ")")
                    .toList();
            log.warn("Tanda rechazada: platos que no son rolls → {}", noRolls);
            throw new NoEsRollException(
                    "Solo se pueden agrupar rolls en una tanda. " +
                    "Los siguientes no son rolls: " + noRolls);
        }

        // ─── Validación 5: todos disponibles ────────────────────────
        boolean hayAgotado = rolls.stream().anyMatch(p -> !p.estaDisponible());

        if (hayAgotado) {
            List<String> agotados = rolls.stream()
                    .filter(p -> !p.estaDisponible())
                    .map(Plato::getNombre)
                    .toList();
            log.warn("Tanda rechazada: rolls agotados → {}", agotados);
            throw new IllegalArgumentException(
                    "No se puede crear la tanda: rolls agotados → " + agotados);
        }

        // ─── Crear y guardar en MongoDB ────────────────────────────
        TandaDocument documento = TandaDocument.builder()
                .idsRolls(idsRolls)
                .cantidad(idsRolls.size())
                .fechaCreacion(LocalDateTime.now())
                .estado("EN_PREPARACION")
                .build();

        TandaDocument guardado = tandaRepository.save(documento);

        log.info("Tanda #{} creada en MongoDB con {} rolls — Sakura Sushi",
                guardado.getId(), guardado.getCantidad());
        return documentMapper.toResponse(guardado);
    }

    @Override
    public List<TandaResponseDTO> obtenerTodas() {
        return tandaRepository.findAll().stream()
                .map(documentMapper::toResponse)
                .toList();
    }
}