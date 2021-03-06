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
package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.InternalAttributesBean;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

public class ServerPlatformBeanRepository {

    private ServerControllerActionCallBean controllerActionCallBean;

    private final InternalAttributesBean internalAttributesBean;

    public ServerPlatformBeanRepository(ServerDolphin dolphin, final BeanRepository beanRepository, EventDispatcher dispatcher) {
        dispatcher.addControllerActionCallBeanAddedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                final String type = model.getPresentationModelType();
                switch (type) {
                    case PlatformConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                        controllerActionCallBean = new ServerControllerActionCallBean(beanRepository, model);
                        break;
                }
            }
        });

        dispatcher.addControllerActionCallBeanRemovedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                final String type = model.getPresentationModelType();
                switch (type) {
                    case PlatformConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                        controllerActionCallBean = null;
                        break;
                }
            }
        });

        internalAttributesBean = new InternalAttributesBean(beanRepository, new ServerPresentationModelBuilder(dolphin));
    }

    public ServerControllerActionCallBean getControllerActionCallBean() {
        return controllerActionCallBean;
    }

    public InternalAttributesBean getInternalAttributesBean() {
        return internalAttributesBean;
    }
}
