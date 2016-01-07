/*
 * Copyright 2015 Canoo Engineering AG.
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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.event.BeanAddedListener;
import com.canoo.dolphin.event.BeanRemovedListener;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.qualifier.Qualifier;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.BaseAttribute;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class BeanManagerImpl implements Serializable, BeanManager {

    protected final BeanRepository beanRepository;

    private final BeanBuilder beanBuilder;

    public BeanManagerImpl(BeanRepository beanRepository, BeanBuilder beanBuilder) {
        this.beanRepository = beanRepository;
        this.beanBuilder = beanBuilder;
    }

    @Override
    public boolean isManaged(Object bean) {
        return beanRepository.isManaged(bean);
    }

    @Override
    public <T> T create(Class<T> beanClass) {
        return beanBuilder.create(beanClass);
    }

    @Override
    public void remove(Object bean) {
        beanRepository.delete(bean);
    }

    @Override
    public void removeAll(Class<?> beanClass) {
        for (Object bean : findAll(beanClass)) {
            beanRepository.delete(bean);
        }
    }

    @Override
    public void removeAll(Object... beans) {
        for (final Object bean : beans) {
            remove(bean);
        }
    }

    @Override
    public void removeAll(Collection<?> beans) {
        for (final Object bean : beans) {
            remove(bean);
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> beanClass) {
        return beanRepository.findAll(beanClass);
    }

    @Override
    public <T> Subscription onAdded(Class<T> beanClass, BeanAddedListener<? super T> listener) {
        return beanRepository.addOnAddedListener(beanClass, listener);
    }

    @Override
    public Subscription onAdded(BeanAddedListener<Object> listener) {
        return beanRepository.addOnAddedListener(listener);
    }

    @Override
    public <T> Subscription onRemoved(Class<T> beanClass, BeanRemovedListener<? super T> listener) {
        return beanRepository.addOnRemovedListener(beanClass, listener);
    }

    @Override
    public Subscription onRemoved(BeanRemovedListener<Object> listener) {
        return beanRepository.addOnRemovedListener(listener);
    }

    @Override
    public <T> void setQualifier(Property<T> property, Qualifier<T> qualifier)  {
        if(property == null) {
            throw new IllegalArgumentException("property must not be null!");
        }
        if(qualifier == null) {
            throw new IllegalArgumentException("qualifier must not be null!");
        }
        if(!(property instanceof PropertyImpl)) {
            throw new IllegalArgumentException("Dolphin Platform Internal Error: Property implementation not supported for qualifier!");
        }
        Attribute attribute = ((PropertyImpl)property).getAttribute();
        if(!(attribute instanceof BaseAttribute)) {
            throw new IllegalArgumentException("Dolphin Platform Internal Error: Attribute implementation not supported for qualifier!");
        }
        ((BaseAttribute)attribute).setQualifier(qualifier.getName());
    }

    @Override
    public <T> Qualifier<T> getQualifier(Property<T> property) {
        if(property == null) {
            throw new IllegalArgumentException("property must not be null!");
        }
        if(!(property instanceof PropertyImpl)) {
            throw new IllegalArgumentException("Dolphin Platform Internal Error: Property implementation not supported for qualifier!");
        }
        return new Qualifier<T>(((PropertyImpl)property).getAttribute().getQualifier());
    }

}
