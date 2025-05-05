package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.dto.AusenciaDTO;
import com.example.riberrepublicfichajeapi.dto.CrearAusenciaDTO;
import com.example.riberrepublicfichajeapi.mapper.AusenciaMapper;
import com.example.riberrepublicfichajeapi.model.Ausencia;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.repository.AusenciaRepository;
import com.example.riberrepublicfichajeapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AusenciaService {

    private final AusenciaRepository ausenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public AusenciaService(AusenciaRepository ausenciaRepository, UsuarioRepository usuarioRepository) {
        this.ausenciaRepository = ausenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Ausencia> getAusencias() {
        return ausenciaRepository.findAll();
    }

    public Ausencia crearAusencia(int idUsuario, CrearAusenciaDTO crearAusenciaDTO) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Ausencia ausencia = new Ausencia();
        ausencia.setUsuario(usuario);
        ausencia.setFecha(crearAusenciaDTO.getFecha());
        ausencia.setMotivo(Ausencia.Motivo.valueOf(crearAusenciaDTO.getMotivo()));
        ausencia.setDetalles(crearAusenciaDTO.getDetalles());
        ausencia.setJustificada(false);
        ausencia.setTiempoRegistrado(LocalDateTime.now());

        return ausenciaRepository.save(ausencia);
    }
}
