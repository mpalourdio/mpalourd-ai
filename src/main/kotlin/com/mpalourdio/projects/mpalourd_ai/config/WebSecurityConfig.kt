/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mpalourdio.projects.mpalourd_ai.config

import com.mpalourdio.projects.mpalourd_ai.csrf.SpaCsrfTokenRequestHandler
import com.mpalourdio.projects.mpalourd_ai.filter.CacheControlHeaderFilter
import org.springframework.boot.web.server.Cookie
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.header.HeaderWriterFilter
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
import org.springframework.security.web.util.matcher.AndRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher

@Configuration(proxyBeanMethods = false)
class WebSecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        val tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
        tokenRepository.setCookieCustomizer { c -> c.secure(true).sameSite(Cookie.SameSite.STRICT.attributeValue()) }

        http {
            csrf {
                csrfTokenRepository = tokenRepository
                csrfTokenRequestHandler = SpaCsrfTokenRequestHandler()
            }
        }

        return http.build()
    }

    @Bean
    @Order(-1)
    @Throws(Exception::class)
    fun staticResources(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(
                AndRequestMatcher(
                    NegatedRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/api/**")),
                    NegatedRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/")),
                    NegatedRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/index.html")),
                )
            )
            addFilterAfter<HeaderWriterFilter>(CacheControlHeaderFilter())
            authorizeHttpRequests {
                authorize(anyRequest, permitAll)
            }
        }

        return http.build()
    }
}

