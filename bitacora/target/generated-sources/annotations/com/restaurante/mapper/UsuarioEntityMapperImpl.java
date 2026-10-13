package com.restaurante.mapper;

import com.restaurante.model.domain.Usuario;
import com.restaurante.persistence.entity.UsuarioEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-13T00:10:04-0500",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class UsuarioEntityMapperImpl implements UsuarioEntityMapper {

    @Override
    public UsuarioEntity toEntity(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        UsuarioEntity.UsuarioEntityBuilder usuarioEntity = UsuarioEntity.builder();

        usuarioEntity.id( usuario.getId() );
        usuarioEntity.email( usuario.getEmail() );
        usuarioEntity.password( usuario.getPassword() );
        usuarioEntity.nombre( usuario.getNombre() );
        usuarioEntity.rol( usuario.getRol() );

        return usuarioEntity.build();
    }

    @Override
    public Usuario toDomain(UsuarioEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Usuario.UsuarioBuilder usuario = Usuario.builder();

        usuario.id( entity.getId() );
        usuario.email( entity.getEmail() );
        usuario.password( entity.getPassword() );
        usuario.nombre( entity.getNombre() );
        usuario.rol( entity.getRol() );

        return usuario.build();
    }
}
