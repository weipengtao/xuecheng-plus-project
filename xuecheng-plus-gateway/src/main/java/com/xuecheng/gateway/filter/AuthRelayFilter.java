package com.xuecheng.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthRelayFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    ServerWebExchange mutatedExchange = exchange;

                    if (securityContext != null) {
                        Authentication auth = securityContext.getAuthentication();
                        if (auth != null && auth.isAuthenticated()) {
                            // mutate request 并绑定回 exchange
                            mutatedExchange = exchange.mutate()
                                    .request(exchange.getRequest().mutate()
                                            .header("X-User-Name", auth.getName())
                                            .build())
                                    .build();
                        }
                    }

                    // 把修改后的 exchange 传给下一个 filter
                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
