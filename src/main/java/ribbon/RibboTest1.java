package ribbon;


import com.netflix.client.ClientFactory;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.netflix.niws.client.http.RestClient;
import org.junit.Test;

import java.net.URI;

public class RibboTest1 {

    @Test
    public void test1() throws Exception {

        String keyServers = "sample-client.ribbon.listOfServers";
        ConfigurationManager.loadPropertiesFromResources("p1.properties");  // 1
        System.out.println(ConfigurationManager.getConfigInstance().getProperty(keyServers));
        RestClient client = (RestClient) ClientFactory.getNamedClient("sample-client"); // 2
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("/")).build(); // 3
        for (int i = 0; i < 6; i++) {
            HttpResponse response = client.executeWithLoadBalancer(request); // 4
            System.out.println(i + ": Status code for " + response.getRequestedURI() + "  :" + response.getStatus());
        }

        ZoneAwareLoadBalancer lb = (ZoneAwareLoadBalancer) client.getLoadBalancer();
        System.out.println(lb.getLoadBalancerStats());


        ConfigurationManager.getConfigInstance().setProperty(
                keyServers, "www.linkedin.com:80,www.microsoft.com:80,www.abc.com:80"); // 5
        System.out.println(ConfigurationManager.getConfigInstance().getProperty(keyServers));

        System.out.println("changing servers ...");

        Thread.sleep(3000); // 6

        for (int i = 0; i < 6; i++) {
            HttpResponse response = null;
            try {
                response = client.executeWithLoadBalancer(request);
                System.out.println(i + ": Status code for " + response.getRequestedURI() + "  :" + response.getStatus());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }
        System.out.println(lb.getLoadBalancerStats()); // 7
    }

}
