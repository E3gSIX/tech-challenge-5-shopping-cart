package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.impl;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.ShoppingCartController;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.PaymentIntegrityResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.ShoppingCartResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.impl.ShoppingCartControllerImpl.URL_SHOPPING_CART;

@RestController
@RequestMapping(URL_SHOPPING_CART)
@Tag(name = "Carrinho de Compras", description = "Gerenciamento de items em carrinhos de compra.")
public class ShoppingCartControllerImpl implements ShoppingCartController {

    public static final String URL_SHOPPING_CART = "/shopping-carts";
    public static final String URL_SHOPPING_CART_ITEM = "/items";
    public static final String URL_SHOPPING_CART_BY_ID = "/{id}";
    public static final String URL_SHOPPING_CART_BY_ID_PAYMENT_INTEGRITY = "/{id}/payment-integrity";

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartControllerImpl(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @Operation(
            summary = "Inclusão de item no Carrinho",
            description = "Endpoint que possibilita a inclusão de itens em um Carrinho de Compras. O usuário terá " +
                    "apenas um Carrinho de Compras ativo persistido, logo pode ser que o item apenas incremente a " +
                    "quantidade de um determinado item no carrinho, assim como também pode ser que um novo carrinho " +
                    "tenha de ser criado.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Item incluso no carrinho com sucesso.",
                            headers = {
                                    @Header(
                                            name = "Location",
                                            description = "Localização do carrinho ativo (onde o item foi adicionado)."
                                    )
                            }),
                    @ApiResponse(responseCode = "400", description = "Inconsistência nos dados recebidos."),
                    @ApiResponse(responseCode = "401", description = "Token não foi informado ou está inválido."),
                    @ApiResponse(responseCode = "403", description = "Requisitante não tem permissões."),
            }
    )
    @Override
    @PostMapping(URL_SHOPPING_CART_ITEM)
    public ResponseEntity add(
            @RequestHeader("Authorization") String authorization,
            @RequestParam UUID userId,
            @RequestBody ShoppingCartItemAddRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        Long activeShoppingCartId = this.shoppingCartService.add(authorization, userId, request);

        URI uri = uriComponentsBuilder.path(URL_SHOPPING_CART.concat(URL_SHOPPING_CART_BY_ID))
                .buildAndExpand(activeShoppingCartId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @Operation(
            summary = "Remoção de item no Carrinho",
            description = "Recurso que retira itens do carrinho ativo identificando-o pelo ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Item removido do carrinho com sucesso.",
                            headers = {
                                    @Header(
                                            name = "Location",
                                            description = "Localização do carrinho ativo (onde o item foi removido)."
                                    )
                            }),
                    @ApiResponse(responseCode = "400", description = "Inconsistência nos dados recebidos."),
                    @ApiResponse(responseCode = "401", description = "Token não foi informado ou está inválido."),
                    @ApiResponse(responseCode = "403", description = "Requisitante não tem permissões."),
            }
    )
    @Override
    @DeleteMapping(URL_SHOPPING_CART_ITEM)
    public ResponseEntity remove(
            @RequestHeader("Authorization") String authorization,
            @RequestParam UUID userId,
            @RequestParam Long itemId,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        Long activeShoppingCartId = this.shoppingCartService.remove(authorization, userId, itemId);

        URI uri = uriComponentsBuilder.path(URL_SHOPPING_CART.concat(URL_SHOPPING_CART_BY_ID))
                .buildAndExpand(activeShoppingCartId)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);

        return new ResponseEntity(headers, HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Buscar Carrinho pelo ID",
            description = "Funcionalidade de consultar um Carrinho de Compras",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Carrinho de Compras encontrado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Inconsistência nos dados recebidos."),
                    @ApiResponse(responseCode = "401", description = "Token não foi informado ou está inválido."),
                    @ApiResponse(responseCode = "403", description = "Requisitante não tem permissões."),
                    @ApiResponse(responseCode = "404", description = "Carrinho de Compras com ID informado não foi encontrado.")
            }
    )
    @Override
    @GetMapping(URL_SHOPPING_CART_BY_ID)
    public ResponseEntity<ShoppingCartResponse> findById(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestParam UUID userId
    ) {
        ShoppingCartResponse response = this.shoppingCartService.findById(authorization, userId, id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Consultar integridade do Carrinho pelo ID",
            description = "Endpoint que retorna a integridade do Carrinho de Compras, verificação importante para " +
                    "conclusão, pois garante que se encontra em um estado válido para o pagamento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Carrinho de Compras encontrado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Inconsistência nos dados recebidos."),
                    @ApiResponse(responseCode = "401", description = "Token não foi informado ou está inválido."),
                    @ApiResponse(responseCode = "403", description = "Requisitante não tem permissões."),
                    @ApiResponse(responseCode = "404", description = "Carrinho de Compras com ID informado não foi encontrado.")
            }
    )
    @Override
    @GetMapping(URL_SHOPPING_CART_BY_ID_PAYMENT_INTEGRITY)
    public ResponseEntity<PaymentIntegrityResponse> checkIntegrityForPayment(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestParam UUID userId
    ) {
        PaymentIntegrityResponse response = this.shoppingCartService.checkPaymentIntegrity(authorization, userId, id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Finalizar Carrinho de Compras",
            description = "Funcionalidade que atualza o estado do carrinho para o estado final de concluído, " +
                    "impossibilitando inclusão ou retirada de itens.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Carrinho de Compras encontrado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Inconsistência nos dados recebidos."),
                    @ApiResponse(responseCode = "401", description = "Token não foi informado ou está inválido."),
                    @ApiResponse(responseCode = "403", description = "Requisitante não tem permissões."),
                    @ApiResponse(responseCode = "404", description = "Carrinho de Compras com ID informado não foi encontrado.")
            }
    )
    @Override
    @PostMapping(URL_SHOPPING_CART_BY_ID)
    public ResponseEntity conclude(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestParam UUID userId
    ) {
        this.shoppingCartService.conclude(authorization, userId, id);

        return ResponseEntity.ok().build();
    }
}
