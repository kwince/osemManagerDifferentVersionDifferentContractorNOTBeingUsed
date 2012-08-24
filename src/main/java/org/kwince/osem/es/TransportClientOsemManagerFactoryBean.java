package org.kwince.osem.es;

import java.util.List;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.FactoryBean;

/**
 * An {@link FactoryBean} used to create OsemManager that uses ElasticSearch
 * Transport {@link Client}. <br>
 * You need to define the host that holds the nodes you want to communicate
 * with.<br>
 * Example :
 * 
 * <pre>
 * {@code
 *  <bean id="esFactory"
 *    class="org.kwince.osem.es.TransportClientOsemManagerFactoryBean">
 *    <property name="indexName" value="mydb" />
 *    <property name="packagesToScan" value="org.kwince.osem.es.model" />
 *    <property name="hosts">
 *      <list>
 *        <value>localhost:9300</value>
 *        <value>localhost:9301</value>
 *      </list>
 *    </property>
 *  </bean>
 * }
 * </pre>
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * @see TransportClient
 */
public class TransportClientOsemManagerFactoryBean extends AbstractOsemSessionFactoryBean {
    private List<String> hosts;

    private boolean sniff = true;

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public void sniff(boolean sniff) {
        this.sniff = sniff;
    }

    @Override
    public void initEsConfig() throws Exception {
        Builder settings = getConfiguration().getSettingsBuilder();
        settings.put("client.transport.sniff", sniff);
        settings.build();
        client = new TransportClient(settings);
        for (String host : hosts) {
            ((TransportClient) client).addTransportAddress(toAddress(host));
        }
        IndicesExistsResponse existResponse = client.admin().indices().exists(new IndicesExistsRequest(indexName)).actionGet();
        if (!existResponse.exists()) {
            client.admin().indices().create(new CreateIndexRequest(indexName)).actionGet();
        }
    }

    private InetSocketTransportAddress toAddress(String address) {
        String[] arr = address.split(":");
        return new InetSocketTransportAddress(arr[0], Integer.parseInt(arr[1]));
    }
}
