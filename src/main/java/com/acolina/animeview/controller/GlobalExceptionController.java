/*
 * Copyright (C) 2017 Pivotal Software, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.acolina.animeview.controller;

import com.acolina.animeview.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Angel Colina
 * @version 1.0
 */
@ControllerAdvice
public class GlobalExceptionController {

    @Autowired
    private EmailService emailService;

    /**
     * Handler para errores tipo {@link Exception}
     *
     * @param ex Exception ocurrida durante la llamada de los servicios Rest
     * @return Json que representa el error que se presento y el Codigo de Error
     * HTTP que representa dicho error
     */
    @ExceptionHandler(Exception.class)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> handleAllException(
            Exception ex) {

        Map<String, Object> result = new HashMap<>();

        // Se valida que la excepcion traiga una causa, de lo contrario se
        // coloca una generica
        result.put("error", true);
        result.put("error_msg", String.format("%s. %s", "Error interno en el servidor", ex.getMessage()));

        emailService.sendErrorMail(ex);
//
//        result.put("stackTrace", sw.toString());
        // Exception del tipo 500 (Internal Server Error)
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof AccessDeniedException) {
            // Exception del tipo 401
            status = HttpStatus.UNAUTHORIZED;
        }

        result.put("status", status.value());
        result.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/aaaa")));
        return new ResponseEntity<>(result, status);

    }
}
