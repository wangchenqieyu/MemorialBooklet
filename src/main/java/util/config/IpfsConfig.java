package util.config;

import ipfs.IpfsClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IpfsConfig {

    @Value("${ipfs.multi.address:/ip4/127.0.0.1/tcp/5001}")
    private String ipfsAddress;

    @Bean
    public IpfsClientFactory ipfsClientFactory() {
        return new IpfsClientFactory(ipfsAddress);
    }
}
