package com.mballem.demoparkpi.web.controller;

import com.mballem.demoparkpi.entyti.Cliente;
import com.mballem.demoparkpi.jwt.JwtUserDetails;
import com.mballem.demoparkpi.repository.projection.ClienteProjection;
import com.mballem.demoparkpi.service.ClienteService;
import com.mballem.demoparkpi.service.UsuarioService;
import com.mballem.demoparkpi.web.dto.ClienteCreateDto;
import com.mballem.demoparkpi.web.dto.ClienteResponseDto;
import com.mballem.demoparkpi.web.dto.PageableDto;
import com.mballem.demoparkpi.web.dto.UsuarioResponseDto;
import com.mballem.demoparkpi.web.dto.mapper.ClienteMapper;
import com.mballem.demoparkpi.web.dto.mapper.PageableMapper;
import com.mballem.demoparkpi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "api/v1/clientes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClienteController {
    private final ClienteService clienteService;

    private final UsuarioService usuarioService;

    @Operation(summary = "Criar um novo Cliente", description = "Recurso para criar um novo Cliente vinculado a um usuário cadastrado",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Usuario CPF já cadastrado no sistema",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada invalidos",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não permitido ao perfil Admin",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> createCliente(@RequestBody @Valid ClienteCreateDto dto, @AuthenticationPrincipal JwtUserDetails userDetails){
        Cliente cliente = ClienteMapper.toCliente(dto);
        cliente.setUsuario(usuarioService.buscarPorId(userDetails.getId()));
        clienteService.salvarCliente(cliente);
        return ResponseEntity.status(201).body(ClienteMapper.toDto(cliente));
    }

    @Operation(summary = "Localizar um Cliente", description = "Localizar cliente por Id",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Cliente não encontrado",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não permitido ao perfil Cliente",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDto> getByIdCliente(@PathVariable Long id){
        Cliente cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }

    @Operation(summary = "Recuperar lista de clientes", description = "Requisição exige uso  de um Bearer token. Acesso restrito a Role=ADMIN",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page",
                        content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                        description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                            content = @Content(schema = @Schema(type = "String", defaultValue = "id,asc")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                            description = "Representa a ordenação dos resultados. Aceita multiplos critérioscritérios de ordenação são suportados"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Cliente não encontrado",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não permitido ao perfil Cliente",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getByAllCliente(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"nome"}, direction = Sort.Direction.ASC) Pageable pageable){
        Page<ClienteProjection> cliente = clienteService.buscarTodosClientes(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(cliente));
    }
    @GetMapping(path = "/detalhes", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> getDetalhesCliente(@AuthenticationPrincipal JwtUserDetails userDetails){
        Cliente cliente = clienteService.buscarPorUsuarioId(userDetails.getId());
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }
}
