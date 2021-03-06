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
package com.canoo.dolphin.server.controller;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.container.ModelInjector;
import com.canoo.dolphin.server.impl.ServerControllerActionCallBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class wrapps the complete Dolphin Platform controller handling.
 * It defines the methods to create or destroy controllers and to interact with them.
 */
public class ControllerHandler {

    private final Map<String, Object> controllers = new HashMap<>();

    private final Map<String, Class> controllerClassMapping = new HashMap<>();

    private final Map<String, Object> models = new HashMap<>();

    private final ContainerManager containerManager;

    private final BeanManager beanManager;

    private final ControllerRepository controllerRepository;

    public ControllerHandler(ContainerManager containerManager, BeanManager beanManager, ControllerRepository controllerRepository) {
        this.containerManager = containerManager;
        this.beanManager = beanManager;
        this.controllerRepository = controllerRepository;
    }

    public Object getControllerModel(String id) {
        return models.get(id);
    }

    public String createController(String name) {
        Class<?> controllerClass = controllerRepository.getControllerClassForName(name);

        final String id = UUID.randomUUID().toString();
        Object instance = containerManager.createManagedController(controllerClass, new ModelInjector() {
            @Override
            public void inject(Object controller) {
                attachModel(id, controller);
            }
        });
        controllers.put(id, instance);
        controllerClassMapping.put(id, controllerClass);

        return id;
    }

    private void attachModel(String controllerId, Object controller) {
        List<Field> allFields = ReflectionHelper.getInheritedDeclaredFields(controller.getClass());

        Field modelField = null;

        for (Field field : allFields) {
            if (field.isAnnotationPresent(DolphinModel.class)) {
                if (modelField != null) {
                    throw new RuntimeException("More than one Model was found for controller " + controller.getClass().getName());
                }
                modelField = field;
            }
        }

        if (modelField != null) {
            Object model = beanManager.create(modelField.getType());
            ReflectionHelper.setPrivileged(modelField, controller, model);
            models.put(controllerId, model);
        }
    }

    public void invokeAction(ServerControllerActionCallBean bean) throws InvokeActionException {
        try {
            final Object controller = controllers.get(bean.getControllerId());
            final Class controllerClass = controllerClassMapping.get(bean.getControllerId());
            final Method actionMethod = getActionMethod(controller, controllerClass, bean.getActionName());
            final List<Object> args = getArgs(actionMethod, bean);
            ReflectionHelper.invokePrivileged(actionMethod, controller, args.toArray());
        } catch (Exception e) {
            throw new InvokeActionException(e);
        }
    }

    private List<Object> getArgs(Method method, ServerControllerActionCallBean bean) {
        final int n = method.getParameterTypes().length;
        final List<Object> args = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            String paramName = Integer.toString(i);
            for (Annotation annotation : method.getParameterAnnotations()[i]) {
                if (annotation.annotationType().equals(Param.class)) {
                    final Param param = (Param) annotation;
                    if (param.value() != null && !param.value().isEmpty()) {
                        paramName = param.value();
                    }
                }
            }
            args.add(bean.getParam(paramName));
        }
        return args;
    }

    public void destroyController(String id) {
        Object controller = controllers.remove(id);
        Class controllerClass = controllerClassMapping.remove(id);
        containerManager.destroyController(controller, controllerClass);

        Object model = models.get(id);
        if (model != null) {
            beanManager.remove(model);
        }
    }

    private <T> Method getActionMethod(T controller, Class<T> controllerClass, String actionName) {
        List<Method> allMethods = ReflectionHelper.getInheritedDeclaredMethods(controllerClass);
        Method foundMethod = null;
        for (Method method : allMethods) {
            if (method.isAnnotationPresent(DolphinAction.class)) {
                DolphinAction actionAnnotation = method.getAnnotation(DolphinAction.class);
                String currentActionName = method.getName();
                if (actionAnnotation.value() != null && !actionAnnotation.value().trim().isEmpty()) {
                    currentActionName = actionAnnotation.value();
                }
                if (currentActionName.equals(actionName)) {
                    if (foundMethod != null) {
                        throw new RuntimeException("More than one method for action " + actionName + " found in " + controller.getClass());
                    }
                    foundMethod = method;
                }
            }
        }
        return foundMethod;
    }

    @SuppressWarnings("unchecked")
    public <T> List<? extends T> getAllControllersThatImplement(Class<T> cls) {
        final List<T> ret = new ArrayList<>();
        for (Object controller : controllers.values()) {
            if (cls.isAssignableFrom(controller.getClass())) {
                ret.add((T) controller);
            }
        }
        return ret;
    }

    public <T> T getControllerById(String id) {
        return (T) controllers.get(id);
    }
}
