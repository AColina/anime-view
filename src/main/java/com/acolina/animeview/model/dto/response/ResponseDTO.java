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
package com.acolina.animeview.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Angel Colina
 * @version 1.0
 */
@Getter
@Setter
public class ResponseDTO<T> implements Serializable {

    private boolean error;
    @JsonProperty("error_msg")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, List<String>> errorField;
    private T object;

    public static <T> ResponseEntity<ResponseDTO<T>> ok(T object) {
        if (Objects.isNull(object)) {
            return error("Error desconocido. Vuelva a intentar");
        }
        ResponseDTO<T> dto = new ResponseDTO<>();
        dto.error = false;
        dto.errorMessage = null;
        dto.object = object;
        return ResponseEntity.ok(dto);
    }

    public static <T> ResponseEntity<ResponseDTO<T>> error(String errorMessage) {
        ResponseDTO<T> dto = new ResponseDTO<>();
        dto.error = true;
        dto.errorMessage = errorMessage;
        dto.object = null;
        return ResponseEntity.ok(dto);
    }

//    public static <T> ResponseEntity<ResponseDTO<T>> error( EntityValidatedException ex) {
//        ResponseDTO<T> dto = new ResponseDTO<>();
//        dto.error = true;
//        dto.errorMessage = ex.getMessage();
//        dto.object = null;
//        dto.errorField = ex.getErrors();
//        return ResponseEntity.ok(dto);
//    }
}
