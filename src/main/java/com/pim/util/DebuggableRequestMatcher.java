package com.pim.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class DebuggableRequestMatcher implements RequestMatcher {
    private final RequestMatcher delegate;
    private final String description;

    public DebuggableRequestMatcher(RequestMatcher delegate, String description) {
        this.delegate = delegate;
        this.description = description;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        boolean matches = delegate.matches(request);
        System.out.println("Request path: " + request.getRequestURI() +
                " | Matcher: " + description +
                " | Matches: " + matches);
        return matches;
    }
}