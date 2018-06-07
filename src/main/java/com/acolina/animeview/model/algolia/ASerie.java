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
package com.acolina.animeview.model.algolia;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Angel Colina
 */
@Data
@NoArgsConstructor
public class ASerie {

    private Integer _id;
    private String url;
    private String title;
    private String urlFront;
    private String synopsis;
    private List<String> genders;
    private String state;
    private String type;
    private String rating;
    private Integer year;


}
