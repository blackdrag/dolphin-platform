/**
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.event.DolphinEventBus;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Factory that provides all needed Dolphin Platform extensions as CDI beans.
 *
 * @author Hendrik Ebbers
 */
public class BeanFactory {

    @Produces
    @ClientScoped
    public BeanManager createManager() {
        return DolphinPlatformJavaeeBootstrap.getCurrentContext().getBeanManager();
    }

    @Produces
    @ClientScoped
    public DolphinSession createDolphinSession() {
        return DolphinPlatformJavaeeBootstrap.getCurrentContext().getCurrentDolphinSession();
    }

    @Produces
    @ApplicationScoped
    public DolphinEventBus createEventBus() {
        return DolphinPlatformJavaeeBootstrap.getContextHandler().getDolphinEventBus();
    }

}
