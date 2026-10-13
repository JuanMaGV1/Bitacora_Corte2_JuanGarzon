package com.restaurante.service.impl;

import com.restaurante.exception.NoEsRollException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.exception.TandaExcedidaException;
import com.restaurante.mapper.TandaDocumentMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.TandaResponseDTO;
import com.restaurante.persistence.document.TandaDocument;
import com.restaurante.repository.TandaRepository;
import com.restaurante.service.PlatoService;
import com.restaurante.service.TandaService;
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

    private static final int    MAX_ROLLS      = 6;
    private static final String CATEGORIA_ROLL = "ROLL";

    private final TandaRepository     tandaRepository;
    private final TandaDocumentMapper documentMapper;
    private final PlatoService        platoService;

    @Override
    public TandaResponseDTO crear(List<Long> idsRolls) {
        // 1. Al menos 1 roll
        if (idsRolls == null || idsRolls.isEmpty()) {
            throw new ReglaDeNegocioException("La tanda debe tener al menos 1 roll");
        }

        // 2. Máximo 6 rolls
        if (idsRolls.size() > MAX_ROLLS) {
            log.warn("Tanda excedida: {} rolls (máx {})", idsRolls.size(), MAX_ROLLS);
            throw new TandaExcedidaException(
                    "Una tanda de Sakura Sushi no puede tener más de "
                    + MAX_ROLLS + " rolls. Recibidos: " + idsRolls.size());
        }

        // 3. Cada id existe
        List<Plato> rolls = new ArrayList<>();
        for (Long idPlato : idsRolls) {
            Plato plato = platoService.obtenerPorId(idPlato);
            rolls.add(plato);
        }

        // 4. Todos son ROLL
        boolean hayNoRoll = rolls.stream()
                .anyMatch(p -> !CATEGORIA_ROLL.equalsIgnoreCase(p.getCategoria()));
        if (hayNoRoll) {
            List<String> noRolls = rolls.stream()
                    .filter(p -> !CATEGORIA_ROLL.equalsIgnoreCase(p.getCategoria()))
                    .map(p -> p.getNombre() + " (" + p.getCategoria() + ")")
                    .toList();
            throw new NoEsRollException(
                    "Solo se pueden agrupar rolls. Los siguientes no son rolls: " + noRolls);
        }

        // 5. Todos disponibles
        boolean hayAgotado = rolls.stream().anyMatch(p -> !p.estaDisponible());
        if (hayAgotado) {
            List<String> agotados = rolls.stream()
                    .filter(p -> !p.estaDisponible())
                    .map(Plato::getNombre)
                    .toList();
            throw new ReglaDeNegocioException(
                    "No se puede crear la tanda: rolls agotados → " + agotados);
        }

        // 6. Guardar en MongoDB
        TandaDocument documento = TandaDocument.builder()
                .idsRolls(idsRolls)
                .cantidad(idsRolls.size())
                .fechaCreacion(LocalDateTime.now())
                .estado("EN_PREPARACION")
                .build();

        TandaDocument guardado = tandaRepository.save(documento);
        log.info("Tanda #{} creada en MongoDB con {} rolls",
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