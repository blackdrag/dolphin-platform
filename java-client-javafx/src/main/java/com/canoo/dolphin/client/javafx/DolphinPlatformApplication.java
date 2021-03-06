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
package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ClientContextFactory;
import com.canoo.dolphin.client.ClientInitializationException;
import com.canoo.dolphin.client.ClientShutdownException;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Defines a basic application class for Dolphin Platform based applications that can be used like the {@link Application}
 * class. Next to the general {@link Application} class of JavaFX this class supports the DOlphin Platform connecttion lifecycle.
 */
public abstract class DolphinPlatformApplication extends Application {

    private ClientContext clientContext;

    private ClientInitializationException initializationException;

    /**
     * Creates the connection to the DOlphin Platform server. If this method will be overridden always call the super method.
     * @throws Exception a exception if the connection can't be created
     */
    @Override
    public void init() throws Exception {
        try {
            clientContext = ClientContextFactory.connect(getClientConfiguration()).get();
        } catch (Exception e) {
            initializationException = new ClientInitializationException("Can not initialize Dolphin Platform Context", e);
        }
    }

    /**
     * Returns the Dolphin Platform configuration for the client. As long as all the default configurations can be used
     * this method don't need to be overridden. The URL of the server will be configured by the {@link DolphinPlatformApplication#getServerEndpoint()}
     * method.
     * @return The Dolphin Platform configuration for this client
     */
    protected JavaFXConfiguration getClientConfiguration() {
        return new JavaFXConfiguration(getServerEndpoint());
    }

    /**
     * Returns the server url of the Dolphin Platform server endpoint.
     * @return the server url
     */
    protected abstract String getServerEndpoint();

    /**
     * This methods defines parts of the Dolphin Platform lifecyycle and is therefore defined as final.
     * Use the {@link DolphinPlatformApplication#start(Stage, ClientContext)} method instead.
     * @param primaryStage the primary stage
     * @throws Exception in case of an error
     */
    @Override
    public final void start(Stage primaryStage) throws Exception {
        if (initializationException == null) {
            if(clientContext != null) {
                start(primaryStage, clientContext);
            } else {
                onInitializationError(primaryStage, new ClientInitializationException("No clientContext was created!"));
            }
        } else {
            onInitializationError(primaryStage, initializationException);
        }
    }

    /**
     * This method must be defined by each application to create the initial view. The method will be called on
     * the JavaFX Platform thread after the connection to the DOlphin Platform server has been created.
     * @param primaryStage the primary stage
     * @param clientContext the Dolphin Platform context
     * @throws Exception
     */
    protected abstract void start(Stage primaryStage, ClientContext clientContext) throws Exception;

    /**
     * This method is called if the connection to the Dolphin Platform server can't be created. Application developers
     * can define some kind of error handling here.
     * By default the methods prints the exception in the log an call {@link System#exit(int)}
     * @param primaryStage the primary stage
     * @param initializationException the exception
     * @throws Exception an error
     */
    protected void onInitializationError(Stage primaryStage, ClientInitializationException initializationException) throws Exception {
        initializationException.printStackTrace();
        System.exit(-1);
    }

    /**
     * Whenever JavaFX calls the stop method the connectiopn to the DOlphin Platform server will be closed.
     * @throws Exception an error
     */
    @Override
    public final void stop() throws Exception {
        if(clientContext != null) {
            try {
                clientContext.disconnect().get();
                onShutdown();
            } catch (Exception e) {
                onShutdownException(new ClientShutdownException(e));
            }
        }
    }

    /**
     * This method is called if the connection to the Dolphin Platform server can't be closed on {@link Application#stop()}.
     * Application developers can define some kind of error handling here.
     * By default the methods prints the exception in the log an call {@link System#exit(int)}
     * @param shutdownException
     */
    protected void onShutdownException(ClientShutdownException shutdownException) {
        shutdownException.printStackTrace();
        System.exit(-1);
    }

    /**
     * This method will be called in the {@link Application#stop()} method after the connection to the Dolphin
     * Platform server is closed. Application developers can define some kind of close handling here.
     *
     *  By default the methods calls {@link System#exit(int)}
     */
    protected void onShutdown() {
        System.exit(0);
    }

}
