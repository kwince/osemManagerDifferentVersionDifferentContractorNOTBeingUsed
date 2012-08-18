package org.kwince.osem.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.FactoryBean;

/**
 * An {@link FactoryBean} used to create OsemManagerFactory that uses Node
 * Client instance {@link Node} which is an embedded instance of the cluster
 * within a running application. <br>
 * The lifecycle of the underlying {@link Node} instance is tied to the
 * lifecycle of the bean via the {@link #destroy()} method which calls
 * {@link Node#close()} <br>
 * <br>
 * Example :
 * 
 * <pre>
 * {@code
 *  <bean id="esSessionFactory"
 *    class="org.kwince.osem.es.NodeClientOsemSessionFactoryBean">
 *    <property name="indexName" value="mydb" />
 *  </bean>
 * }
 * </pre>
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * @see TransportClient
 */
public class NodeClientOsemSessionFactoryBean extends AbstractOsemSessionFactoryBean {
    // See
    // http://elasticsearch-users.115913.n3.nabble.com/node-client-true-vs-node-data-false-td968859.html
    private boolean clientNode;
    private boolean local;
    private Node node;

    public void setClientNode(boolean clientNode) {
        this.clientNode = clientNode;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    @Override
    public void initEsConfig() throws Exception {
        Builder settings = buildConfiguredPropertySettings();
        settings.put("node.client", clientNode);
        node = NodeBuilder.nodeBuilder().client(clientNode).local(local).settings(settings.build()).node().start();
        client = node.client();
    }

    @Override
    public void destroy() throws Exception {
        if (node != null) {
            try {
                node.close();
            } catch (Exception e) {
                logger.error("Error closing node client.", e);
            }
        }
    }

}
