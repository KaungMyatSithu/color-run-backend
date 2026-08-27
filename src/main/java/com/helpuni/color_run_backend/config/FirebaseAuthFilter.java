package com.helpuni.color_run_backend.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.cloud.firestore.Firestore;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private final Firestore firestore;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if(!request.getRequestURI().startsWith("/api/admin")){
            filterChain.doFilter(request,response);
            return;
        }

        // Auth
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing or invalid Authorization header");
            return;
        }

        // Token Gen
        String idToken = authHeader.substring(7);
        try{
            FirebaseToken decode = FirebaseAuth.getInstance().verifyIdToken(idToken);
            boolean isAdmin = firestore.collection("admins").document(decode.getUid())
                    .get().get().exists();
            if (!isAdmin) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access is required");
                return;
            }
            request.setAttribute("firebaseUid", decode.getUid());
            filterChain.doFilter(request,response);
        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token");
        }
    }
}
