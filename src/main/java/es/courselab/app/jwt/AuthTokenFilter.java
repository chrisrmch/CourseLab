package es.courselab.app.jwt;

import es.courselab.app.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter implements WebFilter {

    private final JwtUtils jwtUtils;
    private final UserServiceImpl userService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String jwt = parseJwt(exchange.getRequest());
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            return userService.findByUsername(username)
                    .flatMap(userDetails -> {
                        var auth = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                    }).doOnError(Throwable::printStackTrace)
                    .onErrorResume(err -> chain.filter(exchange));
        }
        return chain.filter(exchange);
    }

    private String parseJwt(ServerHttpRequest request) {
        String headerAuth = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    //    @Autowired
//    private JwtUtils jwtUtils;
//
//    @Autowired
//    private UserServiceImpl userService;
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain)
//            throws ServletException, IOException {
//        String jwt = parseJwt(request);
//        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
//            String username = jwtUtils.getUserNameFromJwtToken(jwt);
//            Mono<UserDetails> userDetails = userService.loadUserByUsername(username);
//            userDetails.subscribe();
//            userDetails.doOnSuccess(fetchData -> {
//                UsernamePasswordAuthenticationToken authenticationToken = null;
//                authenticationToken = new UsernamePasswordAuthenticationToken(fetchData, null, fetchData.getAuthorities());
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            });
////            UsernamePasswordAuthenticationToken authentication =
////                    new UsernamePasswordAuthenticationToken(
////                            userDetails,
////                            null,
////                            userDetails.getAuthorities());
////            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String parseJwt(HttpServletRequest request) {
//        String headerAuth = request.getHeader("Authorization");
//
//        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
//            return headerAuth.substring(7);
//        }
//
//        return null;
//    }
}
