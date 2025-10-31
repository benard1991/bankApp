package com.bankapplication.util;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class RequestUtils {

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Handle multiple IPs from proxy
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(ip);

            // If it's IPv6 (::1, etc.), always return IPv4 loopback
            if (inetAddress.getHostAddress().contains(":")) {
                return "127.0.0.1";
            }

            // Otherwise, return the IPv4 address
            return inetAddress.getHostAddress();

        } catch (UnknownHostException e) {
            // Fallback if IP cannot be resolved
            return "127.0.0.1";
        }
    }
}
