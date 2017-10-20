/*
 * Copyright 2017 ECM Solutions.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acolina.animeview.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 *
 * @author angel
 */
@Component
public class SimpleCORSFilter implements Filter {

    /*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
//		response.setHeader("Access-Control-Allow-Headers",
//				"cache-control,Origin,X-Requested-With,Content-Type,Accept," //
//						+ HeaderValueNames.HEADER_AREA_ID.getName() //
//						+ "," + HeaderValueNames.HEADER_AUTH_TOKEN.getName() //
//						+ "," + HeaderValueNames.HEADER_USER_KEY.getName()
//						+ "," + HeaderValueNames.HEADER_USER_ID.getName()
//						+ "," + HeaderValueNames.HEADER_CONTENT_USER.getName());

        chain.doFilter(req, res);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) {

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {

    }

}
