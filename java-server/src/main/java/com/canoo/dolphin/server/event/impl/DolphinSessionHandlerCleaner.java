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
package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextHandler;
import com.canoo.dolphin.util.Assert;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class DolphinSessionHandlerCleaner implements HttpSessionListener {

    private DolphinContextHandler dolphinContextHandler;

    public DolphinSessionHandlerCleaner(DolphinContextHandler dolphinContextHandler) {
        Assert.requireNonNull(dolphinContextHandler, "dolphinContextHandler");
        this.dolphinContextHandler = dolphinContextHandler;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        Assert.requireNonNull(sessionEvent, "sessionEvent");
        final Iterable<DolphinContext> contexts = dolphinContextHandler.getContextsInSession(sessionEvent.getSession());
        if (contexts != null) {
            for (DolphinContext dolphinContext : contexts) {
                dolphinContextHandler.getDolphinEventBus().unsubscribeSession(dolphinContext.getId());
            }
        }
    }
}
