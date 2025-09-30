package com.edson.eventHub.enuns;

public enum Role {
    /**
     * Permissão mais alta.
     * Pode gerenciar todos os aspectos da plataforma, incluindo usuários,
     * todos os eventos, configurações do sistema e relatórios financeiros.
     * O superusuário do sistema.
     */
    ADMIN,

    /**
     * Usuário com permissão para criar, editar e gerenciar seus próprios eventos.
     * Pode ver painéis de vendas e participantes dos seus eventos,
     * mas não pode modificar eventos de outros organizadores nem gerenciar usuários.
     */
    ORGANIZER,

    /**
     * O papel padrão para qualquer usuário que se cadastra na plataforma.
     * Pode comprar ingressos, visualizar seus próprios pedidos, editar seu perfil
     * e participar de eventos.
     */
    USER,

    /**
     * Papel com permissões limitadas, focado na operação do evento no dia.
     * Geralmente, tem acesso apenas à funcionalidade de check-in, como escanear
     * os ingressos dos participantes na entrada do evento. Não tem acesso a dados
     * financeiros ou de edição do evento.
     */
    STAFF
}
