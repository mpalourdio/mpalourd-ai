package com.mpalourdio.projects.mpalourd_ai.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.filter.GenericFilterBean

class CacheControlHeaderFilter : GenericFilterBean() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpResponse = response as HttpServletResponse
        log.debug("Cache-Control header added for '{}'",(request as HttpServletRequest).requestURI)
        httpResponse.setHeader("Cache-Control", "public, max-age=31536000, immutable")
        chain?.doFilter(request, response)
    }
}
