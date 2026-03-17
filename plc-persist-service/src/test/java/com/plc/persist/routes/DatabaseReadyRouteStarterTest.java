package com.plc.persist.routes;

import static org.mockito.Mockito.*;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

class DatabaseReadyRouteStarterTest {

    @Test
    void shouldStartRouteImmediatelyWhenDatabaseIsReady() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        CamelContext camelContext = mock(CamelContext.class);
        RouteController routeController = mock(RouteController.class);
        Sleeper sleeper = mock(Sleeper.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);
        when(camelContext.getRouteController()).thenReturn(routeController);

        DatabaseReadyRouteStarter starter =
                new DatabaseReadyRouteStarter(dataSource, camelContext, sleeper);

        starter.run(new DefaultApplicationArguments(new String[]{}));

        verify(routeController).startRoute("plc-persist-route");
        verify(sleeper, never()).sleep(any());
    }
    
    @Test
    void shouldRetryUntilDatabaseBecomesReady() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        CamelContext camelContext = mock(CamelContext.class);
        RouteController routeController = mock(RouteController.class);
        Sleeper sleeper = mock(Sleeper.class);

        when(dataSource.getConnection())
                .thenThrow(new RuntimeException("DB down"))
                .thenThrow(new RuntimeException("DB still down"))
                .thenReturn(connection);

        when(connection.isValid(2)).thenReturn(true);
        when(camelContext.getRouteController()).thenReturn(routeController);

        DatabaseReadyRouteStarter starter =
                new DatabaseReadyRouteStarter(dataSource, camelContext, sleeper);

        starter.run(new DefaultApplicationArguments(new String[]{}));

        verify(sleeper, times(2)).sleep(any());
        verify(routeController).startRoute("plc-persist-route");
    }
    
    @Test
    void shouldRetryWhenConnectionIsInvalid() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection invalidConnection = mock(Connection.class);
        Connection validConnection = mock(Connection.class);
        CamelContext camelContext = mock(CamelContext.class);
        RouteController routeController = mock(RouteController.class);
        Sleeper sleeper = mock(Sleeper.class);

        when(dataSource.getConnection())
                .thenReturn(invalidConnection)
                .thenReturn(validConnection);

        when(invalidConnection.isValid(2)).thenReturn(false);
        when(validConnection.isValid(2)).thenReturn(true);
        when(camelContext.getRouteController()).thenReturn(routeController);

        DatabaseReadyRouteStarter starter =
                new DatabaseReadyRouteStarter(dataSource, camelContext, sleeper);

        starter.run(new DefaultApplicationArguments(new String[]{}));

        verify(sleeper, times(1)).sleep(any());
        verify(routeController).startRoute("plc-persist-route");
    }
    
    @Test
    void shouldPropagateInterruptedException() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        CamelContext camelContext = mock(CamelContext.class);
        Sleeper sleeper = mock(Sleeper.class);

        when(dataSource.getConnection()).thenThrow(new RuntimeException("DB down"));
        doThrow(new InterruptedException("interrupted")).when(sleeper).sleep(any());

        DatabaseReadyRouteStarter starter =
                new DatabaseReadyRouteStarter(dataSource, camelContext, sleeper);

        org.junit.jupiter.api.Assertions.assertThrows(
                InterruptedException.class,
                () -> starter.run(new DefaultApplicationArguments(new String[]{}))
        );
    }
}