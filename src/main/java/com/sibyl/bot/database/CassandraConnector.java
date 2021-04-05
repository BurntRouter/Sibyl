package com.sibyl.bot.database;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

import java.net.InetSocketAddress;

public class CassandraConnector {
    private CqlSession session;

    public void connect(String node, Integer port, String dataCenter) {
        CqlSessionBuilder builder = CqlSession.builder();
        builder.addContactPoint(new InetSocketAddress(node, port));
        builder.withLocalDatacenter(dataCenter);

        session = builder.build();
    }

    public CqlSession getSession() {
        return this.session;
    }

    public void close() {
        session.close();
    }

}
