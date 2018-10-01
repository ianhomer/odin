/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.api.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Place holder for core configuration.   Note that order is 50 to give space before to inject
 * specialised configuration for different profiles.
 */
/*
 * TODO : Make this security base line, i.e. always, not needing explicit prod profile.
 */
@Profile("prod")
@Configuration
@Order(4)
public class MainSecurityAdapter extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // TODO : Enable CSRF
    http.csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.DELETE).permitAll()
        .antMatchers(HttpMethod.PATCH).permitAll();
  }
}
